#!/usr/bin/env python3

"""
OpenMRS + Orthanc independent test tool with cleanup and pytest support

How:
- Uses realistic RequestProcedure and RequestProcedureStep fields
- Generates DICOM with matching StudyInstanceUID and Modality
- Polls for procedure steps and status updates
- Structured logging with timestamps
- Cleans up patients and procedure requests after test
- Designed for pytest integration

==========
workflow:
1) Create patient in OpenMRS via REST API => ok
2) Create a requestRrocedure via OpenMRS API => ok
3) Perform c-FIND to check orthanc has created a request procedure
4) Create fake DICOM study, send to orthanc => ok
5) check that OpenMRS request status changed (complete)
6) Check: Orthanc server is stopped
7) Check: OpenMRS server is not started
8) Check: No configuration server

TODO: Update to fix mismatching in OpenMRS frontend, automatic matching
"""

import os
import time
import uuid
import subprocess
import json
import argparse
from typing import Optional
import logging
import shutil
import tempfile
import requests
import pydicom
import requests
import sys
from datetime import datetime, timezone
from io import BytesIO
from typing import Optional, List, Dict

from pydicom.dataset import Dataset, FileDataset
from pynetdicom import AE, QueryRetrievePresentationContexts
from pynetdicom.sop_class import StudyRootQueryRetrieveInformationModelFind

# -----------------------------Logging setup -------------------
log_file_path = "integration_test_tool.log"

# Remove old log file
if os.path.exists(log_file_path):
    os.remove(log_file_path)

# Logger
logger = logging.getLogger("PatientTest")
logger.setLevel(logging.INFO)

# Console handler
console_handler = logging.StreamHandler(sys.stdout)
console_handler.setLevel(logging.INFO)
console_formatter = logging.Formatter('%(asctime)s [%(levelname)s] %(message)s')
console_handler.setFormatter(console_formatter)
logger.addHandler(console_handler)

# File handler (overwrite)
file_handler = logging.FileHandler(log_file_path, mode='w')
file_handler.setLevel(logging.DEBUG)
file_formatter = logging.Formatter('%(asctime)s [%(levelname)s] %(message)s')
file_handler.setFormatter(file_formatter)
logger.addHandler(file_handler)

logger.info("Logger initialized. Logs go to console and '%s'", log_file_path)

# -----------------------------Test parameter ----------------------
Accession_Number = str(uuid.uuid4())
Study_Instance_UID = pydicom.uid.generate_uid()
Request_Description = f"Test request {uuid.uuid4()}"
Performed_Procedure_Step_ID = "1"
Configuration_ID=1
Requesting_Physician = "Dr. Tester"
Study_Description = "Test study"
Series_Description = "Test series description"
Patient_ID="1234"
Modality="CT"
Priority="High"
Aet_Title="ORTHANC"
Referring_Physician="Dr. Referring Doctor"
Requested_Step_Description= "Procedure step CT study"
Station_Name="TEST-STATIOM"
Location="Test Room 1"
Step_Status="SCHEDULED"

# ------------------------------- Utilities ------------------------------
def env_or(name: str, default: Optional[str] = None) -> Optional[str]:
    return os.environ.get(name, default)

def basic_auth_tuple(base_user: str, base_pass: str):
    return (base_user, base_pass)

def generate_openmrs_id(prefix=""):
    import random
    allowed_chars = "0123456789ACDEFGHJKLMNPRTUVWXY"
    base = ''.join(random.choice(allowed_chars) for _ in range(7))  # 7 base chars

    # Compute check digit
    total = 0
    factor = 2
    for char in reversed(base):
        codepoint = allowed_chars.index(char)
        addend = factor * codepoint
        addend = (addend // 30) + (addend % 30)
        total += addend
        factor = 1 if factor == 2 else 2
    remainder = total % 30
    check_codepoint = (30 - remainder) % 30
    check_digit = allowed_chars[check_codepoint]
    return f"{prefix}{base}{check_digit}"

# -------------------------------OpenMRS Client -------------------------
class OpenMRSClient:
    def __init__(self, base_url: str, username: str, password: str):
        self.base_url = base_url.rstrip('/') + '/ws/rest/v1'
        self.auth = (username, password)
        self.headers = {'Content-Type': 'application/json'}


    def create_patient(self, given_name: str, family_name: str, gender: str = 'M', birthdate: str = None):
        if birthdate is None:
            birthdate = datetime.now(timezone.utc).strftime('%Y-%m-%dT00:00:00.000%z')

        identifier_type_uuid = "05a29f94-c0ed-11e2-94be-8c13b969e334"  # OpenMRS ID
        location_uuid = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f"  

        # Generate a random identifier
        identifier_value = generate_openmrs_id()
        logger.info(f"Generated OpenMRS ID: {identifier_value}")

        payload = {
            "person": {
                "names": [{"givenName": given_name, "familyName": family_name}],
                "gender": gender,
                "birthdate": birthdate
            },
            "identifiers": [
                {
                    "identifier": identifier_value,
                    "identifierType": identifier_type_uuid,
                    "location": location_uuid,
                    "preferred": True
                }
            ]
        }
        url = f"{self.base_url}/patient"
        logger.info(f"Create patient URL: {url}")

        resp = requests.post(url, auth=self.auth, headers=self.headers, data=json.dumps(payload))
        if resp.status_code >= 400:
            logger.error("Failed to create patient: %s", resp.text)
        
        resp.raise_for_status()
        patient = resp.json()
        logger.info("Created patient %s %s (%s)", given_name, family_name, patient.get("uuid"))
        return patient

    def delete_patient(self, patient_uuid: str):
        url = f"{self.base_url}/patient/{patient_uuid}?purge=true"
        resp = requests.delete(url, auth=self.auth, headers=self.headers)
        if resp.status_code in [200, 204]:
            logger.info("Deleted patient %s", patient_uuid)
        else:
            logger.warning("Failed to delete patient %s: %s", patient_uuid, resp.status_code)


    def search_patient(self, given_name="Test", family_name="Patient", gender="M"):
        url = f"{self.base_url}/patient?q={given_name}+{family_name}"
        resp = requests.get(url, auth=self.auth, headers=self.headers)
        if resp.status_code != 200:
            logger.error("Failed to search patients: %s", resp.text)
            return []
        
        results = resp.json().get("results", [])
        filtered = []
        for patient in results:
            uuid = patient["uuid"]
            details = requests.get(f"{self.base_url}/patient/{uuid}", auth=self.auth, headers=self.headers).json()
            person = details.get("person", [])
            name = given_name + " " + family_name

            if  person.get('display') == name and person.get("gender") == gender:
                
                filtered.append({'name': person['display'], 'uuid': uuid})
        
        logger.info("search_patient: found %d matching patients", len(filtered))
        return filtered
    
    def create_requestProcedure(self, 
                        patient_uuid: str,
                        accession_number: str,
                        study_instance_uid: str,
                        request_description: str,
                        requesting_physician: str = Requesting_Physician,
                        priority: str = Priority,
                        configuration_id: int = Configuration_ID
                    ):
        
        url_pr = f"{self.base_url}/worklist/saverequest"
        payload_pr = {
            "patientUuid": patient_uuid,
            "accessionNumber": accession_number,
            "studyInstanceUID": study_instance_uid,
            "requestDescription": request_description,
            "requestingPhysician": requesting_physician,
            "priority": priority,
            "configurationId": configuration_id
        }

        # logger.info(f"Creating RequestProcedure with payload: {payload_pr}")
        resp = requests.post(url_pr, auth=self.auth, headers=self.headers, data=json.dumps(payload_pr))
        logger.info(f"Create request procedure response: {resp.status_code} {resp.text}")

        if resp.status_code >= 400:
            logger.error(f"Failed to create RequestProcedure: {resp.text}")
            resp.raise_for_status()
        try:
            data = resp.json() if resp.text else {}
        except ValueError:
            data = []
        return data
    
    def delete_requestProcedure(self, procedure_id: str):
        url = f"{self.base_url}/worklist/request"
        params = {"requestId": procedure_id} 
        resp = requests.delete(url, auth=self.auth, headers=self.headers,  params=params)
        if resp.status_code not in [200, 204]:
            logger.warning(f"Failed to delete request procedure {procedure_id}: {resp.status_code}")
        else:
            logger.info(f"Deleted request procedure: {procedure_id}")
    
    def get_procedures_by_patient(self, patient_uuid: str):
        url = f"{self.base_url}/worklist/requests"
        params = {'patient': patient_uuid}
        resp = requests.get(url, auth=self.auth, headers=self.headers, params=params)
        logger.info(f"Get procedures by patient response: {resp}")
        resp.raise_for_status()
        return resp.json()
    

    def create_requestProcedureStep(self, 
                                    request_id: int, 
                                    modality: str = Modality,
                                    aet_title: str = Aet_Title,
                                    referring_physician: str = Referring_Physician,
                                    requested_step_description: str = Requested_Step_Description,
                                    station_name: str = Station_Name,
                                    location: str = Location,
                                    step_status: str = Step_Status
                                ):
        """
        Create a procedure step for an existing request procedure.
        org.openmrs.module.imaging.api.worklist.RequestProcedureStep fields.
        """
        url = f"{self.base_url}/worklist/savestep"

        now = datetime.now(timezone.utc)
        payload = {
            "requestId": request_id, 
            "modality": modality,
            "aetTitle": aet_title,
            "scheduledReferringPhysician": referring_physician,
            "requestedProcedureDescription": requested_step_description,
            "stepStartDate": now.strftime("%Y-%m-%d"),
            "stepStartTime": now.strftime("%H:%M:%S"),
            "performedProcedureStepStatus": step_status,
            "stationName": station_name,
            "procedureStepLocation": location
        }

        # print("Payload being sent:")
        # print(json.dumps(payload, indent=2))

        response = requests.post(url, auth=self.auth, headers=self.headers, data=json.dumps(payload))
        logger.info(f"Create procedure step response: {response.status_code}")

        if response.status_code != 200:
            logger.error(f"Procedure step creation failed: {response.text}")
            return None
        try:
            res_json = response.json()
        except ValueError:
            res_json = {"status": "false", "message": "Step is not crearted"}

        logger.info(f"Procedure step created successfully: {res_json}")
        return res_json
        
    def get_steps_by_request(self, request_id):
        url = f"{self.base_url}/worklist/requeststep?requestId={request_id}"
        response = requests.get(url, auth=self.auth)
        response.raise_for_status()
        steps = response.json()
        logger.info(f"Found {len(steps)} procedure steps for request {request_id}")
        return steps


    def update_procedure_step_status(self, step_uuid: str, new_status: str = "COMPLETE"):
        url = f"{self.base_url}/worklist/updaterequeststatus"
        payload = {"uuid": step_uuid, "performedProcedureStepStatus": new_status}
        logger.info(f"Updating procedure step {step_uuid} status to {new_status}")
        resp = requests.post(url, auth=self.auth, headers=self.headers, data=json.dumps(payload))
        logger.info(f"Update procedures step status response: {resp}")
        resp.raise_for_status()
        return resp.json()
    
    def delete_procedure_Step(self, step_id: str):
        url = f"{self.base_url}/worklist/requeststep"
        params = {"stepId": step_id} 
        resp = requests.delete(url, auth=self.auth, headers=self.headers, params=params)
        if resp.status_code not in [200, 204]:
            logger.warning(f"Failed to delete request procedure step {step_id}: {resp.status_code}")
        else:
            logger.info(f"Deleted procedure step: {step_id}")

    def get_studies(self, patient_uuid: str):
        url = f"{self.base_url}/imaging/studies?patient={patient_uuid}"
        resp = requests.get(url, auth=self.auth, headers=self.headers)
        resp.raise_for_status()
        return resp.json()

    def get_study_id_by_uid(self, patient_id: str, study_instance_uid: str):
        studies = self.get_studies(patient_id)
        for study in studies:
            if study.get("studyInstanceUID") == study_instance_uid:
                return study.get("id")
        return None
    
    def delete_study(self, study_id: int, delete_option: str = "full"):
        url = f"{self.base_url}/imaging/study"
        params = {"studyId": study_id, "deleteOption": delete_option}
        resp = requests.delete(url, params=params)
        resp.raise_for_status()
        logger.info("Deleted study %s with option '%s'", study_id, delete_option)

    
    def delete_all_studies_for_patient(self, patient_uuid: str, delete_option="full"):
        try:
            studies = self.get_studies(patient_uuid)

            if not studies:
                logger.info(f"No studies found for patient {patient_uuid}")
                return
            for study in studies:
                study_id = study.get("id")
                study_uid = study.get("studyInstanceUID")
                logger.info(f"Deleting study {study_id} (UID={study_uid}) with option '{delete_option}'")
                # Call your API to delete the study
                try:
                    self.delete_study(study_id, delete_option=delete_option)
                except Exception as e:
                    logger.warning(f"Failed to delete study {study_id}: {e}")
                    
            logger.info(f"All studies for patient {patient_uuid} deleted")
        except Exception as e:
            logger.exception(f"Error deleting studies for patient {patient_uuid}: {e}")

# -------------------------------Orthanc Client ------------------------------------------------
class OrthancClient: 
    """
    Handles both HTTP and DICOM communication with Orthanc
    Supports C-FIND via pynetdicom (default) or DCMTK findscu (external CLI).
    """

    def __init__(self, http_url: str, 
                 dicom_ae: str = 'ORTHANC', 
                 dicom_host: str = 'localhost', 
                 dicom_port: int = 4242):
        self.http_url = http_url.rstrip('/')
        self.dicom_ae = dicom_ae
        self.dicom_host = dicom_host
        self.dicom_port = dicom_port

        logger.info(f"Initialized Orthanc client: HTTP={self.http_url}, " 
                    f"DICOM AE={self.dicom_ae}, HOST={self.dicom_host}, PORT={self.dicom_port}")
        
    # ----------- DICOM C-FIND Query ------------------------------------------------------------
    def cfind_study(self, query: dict,
                    ae_title: str = 'PY_TEST_AE',
                    use_findscu: bool = False):
        
        logger.info("Performing C-FIND query (method=%s)...",
                    "findscu" if use_findscu else "pynetdicom")

        logger.debug("C-FIND query dataset: %s", query)

        if use_findscu:
            return self._cfind_with_findscu(query, ae_title)
        return self._cfind_with_pynetdicom(query, ae_title)

    def _cfind_with_pynetdicom(self, query: dict, ae_title: str):

        logger.debug("Using pynetdicom C-FIND to %s:%s (AE=%s)",
                     self.dicom_host, self.dicom_port, self.dicom_ae)

        ae = AE(ae_title=ae_title)
        for cx in QueryRetrievePresentationContexts:
            ae.add_requested_context(cx.abstract_syntax)
        assoc = ae.associate(self.dicom_host, self.dicom_port, ae_title=self.dicom_ae)

        results = []
        if assoc.is_established:
            logger.info("DICOM association established with Orthanc AE: %s", self.dicom_ae)
            response = assoc.send_c_find(query,
                                        StudyRootQueryRetrieveInformationModelFind)
            for (status, identifier) in response:
                if status: 
                    logger.debug("C-FIND status: 0x%04x", status.Status)
                if identifier:
                    logger.debug("C-FIND identifier: %s", identifier)
                results.append((status, identifier))
            assoc.release()
            logger.info("C-FIND association released normally.")
        else:
            logger.error("Failed to associate with Orthanc at %s:%s (AE=%s)",
                         self.dicom_host, self.dicom_port, self.dicom_ae)
            raise ConnectionError(f"Could not associate to {self.dicom_host}:{self.dicom_port} "
                                f"as AE {self.dicom_ae}")
        
        return results

    def _cfind_with_findscu(self, query: str, ae_title):
        """
        Perform C-FIND using the external DCMTK findscu binary
        """
        findscu_path = shutil.which("findscu")
        if not findscu_path:
            logger.error("findscu executable not found in PATH.")
            raise FileNotFoundError("findscu executable not found in PATH")
        
        logger.debug("findscu found at: %s", findscu_path)

        # Build temporary DICOM dataset for query
        with tempfile.NamedTemporaryFile(suffix='.dcm', delete=False) as tmp:
            tmp_path = tmp.name
            ds = self._make_dicom_query_dataset(query)
            ds.save_as(tmp_path, write_as_original=False)
            logger.debug("Temporary query dataset saved ot %s", tmp_path)

            cmd = [
                findscu_path,
                '-p',
                '-aec', self.dicom_ae,
                '-aet', ae_title,
                self.dicom_host,
                str(self.dicom_port),
                tmp_path
            ]
            logger.info("Running findscu command: %s", " ".join(cmd))
            process = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            stdout, stderr = process.communicate()

            os.remove(tmp_path)
            logger.debug("Removed temporary dataset: %s", tmp_path)

            output = stdout.decode() or stderr.decode()
            if process.returncode != 0:
                logger.error("findscu failed (code=%d): %s", process.returncode, output)
                raise RuntimeError(f"findscu failed ({process.returncode}): {output}")
            
            logger.info("findscu executed successfully.")
            logger.debug("findscu output: %s", output)
            return output

    def _make_dicom_query_dataset(self, query: dict):
        ds = pydicom.Dataset()
        for key, value in query.items():
            setattr(ds, key, value)
        logger.debug("Constructed DICOM query dataset with %d elements.", len(query))
        return ds

    # HTTP UPLOAD
    def upload_instance(self, dcm_bytes: bytes):
        url = f"{self.http_url}/instances"
        headers = {'Content-Type': 'application/dicom'}
        logger.info("Uploading DICOM instace to %s", url)

        response = requests.post(url, data=dcm_bytes, headers=headers)
        if response.status_code >= 400:
            logger.error("Failed to upload DICOM instance: %s %s", response.status_code, response.text)
        else:
            logger.info("DICOM instance uploaded successfully (status=%s)", response.status_code)

        response.raise_for_status()
        return response.json()

    def delete_all_studies_in_orthanc(self):

        # 1. Get all study IDs from Orthanc
        url = f"{self.http_url}/studies"
        logger.info("Fetching all studies from Orthanc: %s", url)
        resp = requests.get(url)
        resp.raise_for_status()
        study_ids = resp.json()  # list of study IDs (strings)
        logger.info("Found %d studies in Orthanc", len(study_ids))

        # 2. Delete each study
        for study_id in study_ids:
            del_url = f"{self.http_url}/studies/{study_id}"
            logger.info("Deleting study %s from Orthanc", study_id)
            del_resp = requests.delete(del_url)
            if del_resp.status_code in [200, 204]:
                logger.info("Study %s deleted successfully", study_id)
            else:
                logger.warning("Failed to delete study %s: %s %s", study_id, del_resp.status_code, del_resp.text)

# ------------------------------DICOM creation --------------------------
    def create_fake_dicom(
            self,
            patient_name: str, 
            patient_id: str, 
            modality:str = Modality,
            study_instance_uid: Optional[str] = None,
            series_instance_uid: Optional[str] = None,
            sop_instance_uid: Optional[str] = None,
            performed_procedure_step_id: Optional[str] = None,
        ) -> bytes:
        
        logger.info("Creating fake DICOM for patient '%s' (ID=%s, Modality=%s)", 
                patient_name, patient_id, modality)
        
        
        if study_instance_uid is None:
            study_instance_uid = pydicom.uid.generate_uid()
            logger.debug("Generated new StudyInstanceUID: %s", study_instance_uid)
        
        if series_instance_uid is None:
            series_instance_uid = pydicom.uid.generate_uid()
        
        if sop_instance_uid is None:
            sop_instance_uid = pydicom.uid.generate_uid()

        file_meta = pydicom.dataset.FileMetaDataset()
        file_meta.MediaStorageSOPClassUID = pydicom.uid.SecondaryCaptureImageStorage
        file_meta.MediaStorageSOPInstanceUID = sop_instance_uid
        file_meta.ImplementationClassUID = pydicom.uid.generate_uid()

        logger.debug(
            "File Meta prepared: MediaStorageSOPClassUID=%s, "
            "MediaStorageSOPInstanceUID=%s, ImplementationClassUID=%s",
            file_meta.MediaStorageSOPClassUID,
            file_meta.MediaStorageSOPInstanceUID,
            file_meta.ImplementationClassUID
        )

        ds = FileDataset(None, {}, file_meta=file_meta, preamble=b"\0" * 128)
        ds.PatientName = patient_name
        ds.PatientID = patient_id
        ds.StudyInstanceUID = study_instance_uid
        ds.SeriesInstanceUID = pydicom.uid.generate_uid()
        ds.SOPInstanceUID = pydicom.uid.generate_uid()
        ds.Modality = modality
        ds.StudyDate = datetime.now(timezone.utc).strftime('%Y%m%d')
        ds.StudyTime = datetime.now(timezone.utc).strftime('%H%M%S')
        ds.AccessionNumber = Accession_Number
        ds.Study_Description = Study_Description
        ds.RequestingPhysician = Requesting_Physician
        ds.SeriesDate = datetime.now(timezone.utc).strftime('%Y%m%d')
        ds.SeriesTime = datetime.now(timezone.utc).strftime('%H%M%S')

        logger.debug(
            "DICOM dataset initialized: SeriesInstanceUID=%s, SOPInstanceUID=%s, "
            "StudyDate=%s, StudyTime=%s",
            ds.SeriesInstanceUID,
            ds.SOPInstanceUID,
            ds.StudyDate,
            ds.StudyTime
        )


        ds.PerformedProcedureStepID = performed_procedure_step_id    
        ds.Rows = 32
        ds.Columns = 32
        ds.BitsAllocated = 8
        ds.SamplesPerPixel = 1
        ds.is_little_endian = True
        ds.is_implicit_VR = True
        ds.PixelData = b"\x00" * (ds.Rows * ds.Columns)

        logger.debug(
            "Pixel data added: %dx%d (%d bytes)", 
            ds.Rows, ds.Columns, len(ds.PixelData)
        )

        bio = BytesIO()
        ds.save_as(bio, write_like_original=False)
    
        logger.debug(
            "DICOM instance created: StudyUID=%s, SeriesUID=%s, SOPUID=%s",
            ds.StudyInstanceUID, ds.SeriesInstanceUID, ds.SOPInstanceUID
        )

        return bio.getvalue()

    def create_dicom_study(
        self,
        patient_name: str,
        patient_id: str,
        modality: str = Modality,
        series_count: int = 2,
        instances_per_series: int = 3,
        performed_procedure_step_id: Optional[str] = Performed_Procedure_Step_ID
    ) -> Dict[str, List[bytes]]:
        
        logger.info(
            "Creating complex DICOM study: patient=%s, series=%d, instances/series=%d",
            patient_name, series_count, instances_per_series
        )

        study_uid = pydicom.uid.generate_uid()
        study = {}

        for series_index in range(series_count):
            series_uid = pydicom.uid.generate_uid()
            series_instances = []

            for instance_index in range(instances_per_series):
                sop_uid = pydicom.uid.generate_uid()
                dicom_bytes = self.create_fake_dicom(
                    patient_name=patient_name,
                    patient_id=patient_id,
                    modality=modality,
                    study_instance_uid=study_uid,
                    series_instance_uid=series_uid,
                    sop_instance_uid=sop_uid,
                    performed_procedure_step_id=performed_procedure_step_id,
                )
 
                # append inside the inner loop
                series_instances.append(dicom_bytes)
                logger.debug(
                    "Created instance %d for series %d (StudyUID=%s, SeriesUID=%s, SOPUID=%s)",
                    instance_index + 1, series_index + 1, study_uid, series_uid, sop_uid
                )

            study[f"Series_{series_index + 1}"] = series_instances

        logger.info(
            "Study created with %d series and %d total instances",
            series_count, series_count * instances_per_series
        )

        return study


# ------------------------------Main Test Logic ------------------------------------------------------
def run_test(args):
    logger.info("Starting integration test with OpenMRS (%s) and Orthanc (%s)",
                    args.openmrs, args.orthanc_http)
    patient = None
    procedure_id = None

    try: 
        openmrs = OpenMRSClient(args.openmrs, args.user, args.password)
        orthanc = OrthancClient(args.orthanc_http, dicom_ae=args.orthanc_dicom_ae, dicom_host=args.orthanc_dicom_host, dicom_port=args.orthanc_dicom_port)

        # TODO: modality client

        
        # Create only one patient
        patients = openmrs.search_patient(args.given_name, args.family_name, args.gender)
        for patient_uuid in patients:
            openmrs.delete_patient(patient_uuid)

        if not patients:
            patient = openmrs.create_patient(args.given_name, args.family_name, args.gender)
        else:
            # Use the first patient found
            patient = patients[0]

        if not patient['uuid']:
            logger.error("Failed to find or create the patient")
            return
        
        patient_uuid = patient["uuid"]
        # patient_name = patient["name"]
        patient_name = patient.get("name") or patient.get("display") or f"{args.given_name} {args.family_name}"

        if not patient_uuid:
            raise RuntimeError("Failed to find or create patient (no UUID)")

        logger.info(f"Using patient: {patient_name} ({patient_uuid})")

        request_procedure = openmrs.create_requestProcedure(
            patient_uuid, 
            Accession_Number, 
            Study_Instance_UID, 
            Request_Description,
            priority=Priority,
            configuration_id=Configuration_ID
        )

        # --- Fetch the newly created request to get its numeric ID ---
        try:
            procedure_list = openmrs.get_procedures_by_patient(patient_uuid)
            if not procedure_list:
                raise RuntimeError("No request procedures found for this patient after creation.")
            
            # Assuming the last one is the newest
            latest_proc = procedure_list[-1]
            procedure_id = latest_proc.get("id") or latest_proc.get("requestId")
            logger.info(f"Fetched procedure ID: {procedure_id}")

            if not procedure_id:
                raise RuntimeError(f"Could not find request ID in response: {latest_proc}")

        except Exception as e:
            logger.exception(f"Failed to fetch created request procedure: {e}")
            raise

        # --- Create procedure step ---
        procedure_step = openmrs.create_requestProcedureStep(procedure_id)
        if procedure_step is None:
            logger.error("Failed to create procedure step")
        else:
            logger.info("Procedure step created successfully: %s", procedure_step)

          
        steps= openmrs.get_steps_by_request(procedure_id) or []
        logger.info("step number: %d", len(steps))

        # --- Generate fake DICOM study ---
        logger.info("Generating fake DICOM for patient %s", patient_name)
        study_data = orthanc.create_dicom_study(
            patient_name=patient_name, 
            patient_id=Patient_ID, 
            modality=Modality,
            series_count=2,
            instances_per_series=3,
            performed_procedure_step_id=Performed_Procedure_Step_ID
        )

        # Save one instance as example
        with open("example_instance.dcm", "wb") as f:
            f.write(list(study_data.values())[0][0])

        
        # Upload each DICOM instance to Orthanc
        for series_name, instances in study_data.items():
            for idx, dicom_bytes in enumerate(instances):
                try:
                    response = orthanc.upload_instance(dicom_bytes)
                    logger.info(
                        "Upload %s instance %d/%d orthanc ID=%s",
                        series_name, idx+1, len(instances), response.get("ID")
                    )
                except Exception as e:
                    logger.error(
                        "Failed to upload DICOM instance %d in %s: %s",
                        idx+1, series_name, e
                    )

    except Exception as e:
        logger.exception("Integration test failed with exception: %s", e)
        raise
    finally:
        logger.info("Starting cleanup...")
        try: 
            if patient and patient_uuid:
                # Delete all studies created for this patient
                openmrs.delete_all_studies_for_patient(patient_uuid, delete_option="full")
                orthanc.delete_all_studies_in_orthanc()

                if procedure_id: 
                    try:
                        procedure_steps = openmrs.get_steps_by_request(procedure_id)
                        for step in procedure_steps:
                            step_id = step.get('id') or step.get('uuid')
                            if step_id:
                                openmrs.delete_procedure_Step(step_id)
                    except:
                        logger.warning(f"Failed to delete procedure steps: {e}")
                
                try: 
                    procedures = openmrs.get_procedures_by_patient(patient_uuid)
                    for procedure in procedures:
                        proc_id = procedure.get('id')
                        if proc_id:
                            openmrs.delete_requestProcedure(proc_id)
                except Exception as e:
                    logger.warning(f"Failed to delete procedures: {e}")

                # Delete patient
                openmrs.delete_patient(patient_uuid)
                time.sleep(2)  # wait to ensure deletion

                # Verify patient no longer exists
                still_exists = openmrs.search_patient(args.given_name, args.family_name, args.gender)
                if not still_exists:
                    logger.info("Patient successfully deleted and not found in OpenMRS")
                else:
                    logger.warning("Patient still exists after deletion: %s", still_exists)

                
            logger.info("Cleanup completed successfully.")

        except Exception as cleanup_err:
            logger.exception("Error during cleanup: %s", cleanup_err)

# ------------------------------Main / pytest support -------------------
if __name__ == '__main__':

    ap = argparse.ArgumentParser(description='OpenMRS-Orthanc test tool with cleanup')
    ap.add_argument('--openmrs', default=env_or('OPENMRS_BASE_URL', 'http://localhost:8080/openmrs'))
    ap.add_argument('--user', default=env_or('OPENMRS_USER', 'admin'))
    ap.add_argument('--password', default=env_or('OPENMRS_PASS', 'Admin123'))
    ap.add_argument('--orthanc-http', default=env_or('ORTHANC_HTTP_URL', 'http://localhost:8052'))
    ap.add_argument('--orthanc-dicom-ae', default=env_or('ORTHANC_DICOM_AE', 'ORTHANC'))
    ap.add_argument('--orthanc-dicom-host', default=env_or('ORTHANC_DICOM_HOST', 'localhost'))
    ap.add_argument('--orthanc-dicom-port', type=int, default=int(env_or('ORTHANC_DICOM_PORT', '4242')))
    ap.add_argument('--given-name', default='Test')
    ap.add_argument('--family-name', default='Patient')
    ap.add_argument('--gender', default='M')
    ap.add_argument('--poll-timeout', type=int, default=60)
    ap.add_argument('--poll-interval', type=int, default=5)
    args = ap.parse_args()

    logger.info("Parsed arguments: %s", vars(args))

    try:
        run_test(args)

    except Exception as e:
        logger.exception("Integration test failed: %s", e)