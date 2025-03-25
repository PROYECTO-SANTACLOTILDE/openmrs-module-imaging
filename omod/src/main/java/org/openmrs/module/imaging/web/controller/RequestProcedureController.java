package org.openmrs.module.imaging.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.api.RequestProcedureService;
import org.openmrs.module.imaging.api.RequestProcedureStepsService;
import org.openmrs.module.imaging.api.worklist.RequestProcedure;
import org.openmrs.module.imaging.api.worklist.RequestProcedureSteps;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/imaging")
public class RequestProcedureController extends MainResourceController {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	@Override
	public String getNamespace() {
		return RestConstants.VERSION_1 + "/imaging";
	}
	
	@RequestMapping(value = "/worklist", method = RequestMethod.GET,
            // consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Object> getRequestProcedures(HttpServletRequest request, HttpServletResponse response) {
        // Map<String, Object> query = new ObjectMapper().readValue(requestBody, Map.class);
        RequestProcedureService requestProcedureService = Context.getService(RequestProcedureService.class);
        RequestProcedureStepsService requestProcedureStepsService = Context.getService(RequestProcedureStepsService.class);

        List<RequestProcedure> rps = requestProcedureService.getAllRequestProcedures();
        List<Map<String,Object>> result = new LinkedList<Map<String,Object>>();
        for (RequestProcedure rp : rps) {
            if(rp.getStatus().equalsIgnoreCase("pending")) {
                Map<String,Object> map = new HashMap<String,Object>();
//                writeHeader(map);
                writeProcedure(rp, map, requestProcedureStepsService);
                result.add(map);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
	
	//	private static void writeHeader(Map<String, Object> map) {
	//		// DICOM-meta-information-Header
	//		// Used TransferSyntax: Little Endian Explicit
	//		map.put("FileMetaInformationGroupLength", "202"); // FileMetaInformationGroupLength
	//		map.put("FileMetaInformationVersion", "\\00\\01"); //  FileMetaInformationVersion
	//		map.put("MediaStorageSOPClassUID", "1.2.276.0.7230010.3.1.0.1"); // MediaStorageSOPClassUID
	//		map.put("MediaStorageSOPInstanceUID", "1.2.276.0.7230010.3.1.4.2831176407.11154.1448031138.805061"); // MediaStorageSOPInstanceUID
	//		map.put("TransferSyntaxUID", "LittleEndianExplicit"); // TransferSyntaxUID
	//		map.put("ImplementationClassUID", "1.2.276.0.7230010.3.0.3.6.0"); // ImplementationClassUID
	//		map.put("ImplementationVersionName", "OFFIS_DCMTK_360"); // ImplementationVersionName
	//	}
	
	private static void writeProcedure(RequestProcedure rp, Map<String, Object> map,
	        RequestProcedureStepsService requestProcedureStepsService) {

		map.put("SpecificCharacterSet", "ISO_IR 100");
		map.put("AccessionNumber", rp.getAccessionNumber());
		map.put("PatientName", rp.getMrsPatient().getPersonName().getFullName());
		map.put("PatientID", rp.getMrsPatient().getPatientIdentifier().getUuid());
		String birthDate = rp.getMrsPatient().getBirthdate().toString();
		String birthAge = rp.getMrsPatient().getAge().toString();
		if (birthDate == null || birthDate.trim().isEmpty()) {
			map.put("PatientBirthDate", birthAge);
		} else {
			map.put("PatientBirthDate", birthDate);
		}
		map.put("PatientSex", rp.getMrsPatient().getGender());
		map.put("MedicalAlerts", "unknown");
		map.put("Allergies", "unknown");
		map.put("StudyInstanceUID", rp.getStudyInstanceUID());
		map.put("RequestingPhysician", rp.getRequestingPhysician()); // RequestingPhysician
		map.put("RequestedProcedureDescription", rp.getRequestDescription());
		map.put("RequestedProcedureID", rp.getId().toString());
		map.put("RequestedProcedurePriority", rp.getPriority());

		// Read the procedure steps
		List<RequestProcedureSteps> procedureSteps = requestProcedureStepsService.getAllStepsByRequestProcedure(rp);
		List<Map<String, Object>> stepList = new ArrayList<>();
		for(RequestProcedureSteps steps : procedureSteps) {
			writeProcedureStep(steps, stepList);
		}
		map.put("ScheduledProcedureStepSequence", stepList);
	}
	
	private static void writeProcedureStep(RequestProcedureSteps steps, List<Map<String, Object>> stepList) {
		Map<String, Object> stepMap = new HashMap<String, Object>();
		stepMap.put("Modality", steps.getModality());
		stepMap.put("ScheduledStationAETitle", steps.getAetTitle());
		stepMap.put("ScheduledProcedureStepStartDate", steps.getStepStartDate());
		stepMap.put("ScheduledProcedureStepStartTime", steps.getStepStartTime());
		stepMap.put("ScheduledPerformingPhysicianName", steps.getScheduledReferringPhysician());
		stepMap.put("ScheduledProcedureStepDescription", steps.getRequestedProcedureDescription());
		stepMap.put("ScheduledProcedureStepID", steps.getId().toString());
		stepMap.put("ScheduledStationName", steps.getStationName());
		stepMap.put("ScheduledProcedureStepLocation", steps.getProcedureStepLocation());
		stepMap.put("CommentsOnTheScheduledProcedureStep", "no value available");
		stepList.add(stepMap);
	}
}
