import argparse
import time
from utils import env_or, logger
from openmrs_client import OpenMRSClient
from orthanc_client import OrthancClient
import pydicom
from pydicom.dataset import Dataset, FileDataset
from pydicom.sequence import Sequence
from pynetdicom import AE, QueryRetrievePresentationContexts
from pynetdicom.sop_class import StudyRootQueryRetrieveInformationModelFind, ModalityWorklistInformationFind

from utils import (
    Accession_Number,
    Study_Instance_UID,
    Request_Description,
    Performed_Procedure_Step_ID,
    Configuration_ID,
    Given_Name,
    Family_Name,
    Patient_ID,
    Modality,
    Priority,
    config_baseURL,
    config_proxyurl,
    orthanc_username,
    orthanc_password
)

# --------------------------------- Main Test Logic -----------------------------------------
def run_test(args):
    logger.info("Starting integration test with OpenMRS (%s) and orthanc (%s)",
                args.openmrs, args.orthanc_http)
    patient_uuid = None
    procedure_id = None

    openmrs = OpenMRSClient(args.openmrs, args.user, args.password)
    orthanc = OrthancClient(args.orthanc_http,
                        dicom_ae=args.orthanc_dicom_ae,
                        dicom_host=args.orthanc_dicom_host,
                        dicom_port=args.orthanc_dicom_port)
    try:
        # Create only one patient
        patients = openmrs.search_patient(args.given_name, args.family_name, args.gender)
        for p in patients:
            openmrs.delete_patient(p)

        if not patients:
            patient = openmrs.create_patient(args.given_name, args.family_name, args.gender)
        else:
            # Use the first patient found
            patient = patients[0]

        if not patient:
            logger.error("Failed to find or create the patient")
            return

        patient_uuid = patient["uuid"]
        person = patient.get("person")
        if person:
            patient_name = person.get("display")
        else:
            patient_name = f"{args.given_name} {args.family_name}"

        logger.info("Using patient: %s (%s)", patient_name, patient_uuid)

        # Verify Orthanc configuration
        configs = openmrs.get_orthanc_configurations()
        if configs==[] or len(configs) == 0 or configs is None:
            logger.warning("No Orthanc configuration found.")
            openmrs.create_orthanc_configuration(config_baseURL, config_proxyurl, orthanc_username, orthanc_password)
            logger.warning("Added a new Orthanc configuration")
            configs = openmrs.get_orthanc_configurations()
        else:
            logger.info("OpenMRS-Orthanc integration configuration confirmed")

        # Create request procedure for worklist
        request_procedure = openmrs.create_requestProcedure(
            patient_uuid,
            Accession_Number,
            Study_Instance_UID,
            Request_Description,
            priority=Priority,
            configuration_id=configs[0]["id"]
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

        except Exception as e_procedure:
            logger.exception(f"Failed to fetch created request procedure: {e_procedure}")
            raise

        # --- Create procedure step ---
        procedure_step = openmrs.create_requestProcedureStep(procedure_id)
        if procedure_step is None:
            logger.error("Failed to create procedure step")
        else:
            logger.info("Procedure step created successfully: %s", procedure_step)

        steps = openmrs.get_steps_by_request(procedure_id) or []
        logger.info(f"Number of steps for procedure {procedure_id}: {len(steps)}")

        # --------------------Orthanc worklist query ----------------------------
        query = Dataset()
        query.PatientName = patient_name
        # query.PatientID = Patient_ID
        query.AccessionNumber = Accession_Number

        results = orthanc.find_worklist(query)
        logger.info(f"Found worklist items: {len(results)}")
        for r in results:
            logger.info(f"Found worklist item: {r}")

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
                except Exception as e_series:
                    logger.error(
                        "Failed to upload DICOM instance %d in %s: %s",
                        idx+1, series_name, e_series
                    )

    except Exception as e:
        logger.exception("Integration test failed with exception: %s", e)
        raise
    finally:
        logger.info("Starting cleanup...")
        try:
            if patient_uuid:
                try:
                    # Delete all studies created for this patient
                    openmrs.delete_all_studies_for_patient(patient_uuid, delete_option="full")
                except Exception as e:
                    logger.warning("Failed to delete OpenMRS studies: %s", e)

                try:
                    orthanc.delete_all_studies_in_orthanc()
                except Exception as e_deleteOrthanc:
                    logger.warning(f"Failed to delete Orthanc studies: %s {e_deleteOrthanc}")

                if procedure_id:
                    try:
                        procedure_steps = openmrs.get_steps_by_request(procedure_id)
                        for step in procedure_steps:
                            step_id = step.get('id') or step.get('uuid')
                            if step_id:
                                openmrs.delete_procedure_Step(step_id)
                    except Exception as e_procedureStep:
                        logger.warning(f"Failed to delete procedure steps: {e_procedureStep}")

                # Delete procedures
                try:
                    procedures = openmrs.get_procedures_by_patient(patient_uuid)
                    for procedure in procedures:
                        proc_id = procedure.get('id')
                        if proc_id:
                            openmrs.delete_requestProcedure(proc_id)
                except Exception as e_delete_procedure:
                    logger.warning(f"Failed to delete procedures: {e_delete_procedure}")

            # Delete patient
            try:
                openmrs.delete_patient(patient_uuid)
                time.sleep(2)
                still_exists = openmrs.search_patient(args.given_name, args.family_name, args.gender)
                if not still_exists:
                    logger.info("Patient successfully deleted and not found in OpenMRS")
                else:
                    logger.warning("Patient still exists after deletion: %s", still_exists)

                logger.info("Cleanup completed successfully.")
            except Exception as e_deletePatient:
                logger.warning("Failed to delete patient: %s", e_deletePatient)

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
    ap.add_argument('--given-name', default=Given_Name)
    ap.add_argument('--family-name', default=Family_Name)
    ap.add_argument('--gender', default='M')
    ap.add_argument('--poll-timeout', type=int, default=60)
    ap.add_argument('--poll-interval', type=int, default=5)
    args = ap.parse_args()

    logger.info("Parsed arguments: %s", vars(args))

    try:
        run_test(args)

    except Exception as e:
        logger.exception("Integration test failed: %s", e)