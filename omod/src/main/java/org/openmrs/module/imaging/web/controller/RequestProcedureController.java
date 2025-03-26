/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
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
        RequestProcedureService requestProcedureService = Context.getService(RequestProcedureService.class);
        RequestProcedureStepsService requestProcedureStepsService = Context.getService(RequestProcedureStepsService.class);

        List<RequestProcedure> rps = requestProcedureService.getAllRequestProcedures();
        List<Map<String,Object>> result = new LinkedList<Map<String,Object>>();
        for (RequestProcedure rp : rps) {
            if(rp.getStatus().equalsIgnoreCase("pending")) {
                Map<String,Object> map = new HashMap<String,Object>();
                writeProcedure(rp, map, requestProcedureStepsService);
                result.add(map);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

	/**
	 * @param rp The request procedure object
	 * @param map The worklist data map
	 * @param requestProcedureStepsService The request procedure steps service
	 * */
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

	/**
	 * @param steps The request procedure steps
	 * @param stepList The list of the procedure steps
	 * */
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

	/**
	 * @param studyInstanceUID The dicom study instance UID
	 * */
	@RequestMapping(value = "/updatestatus", method = RequestMethod.POST,
	// consumes = MediaType.APPLICATION_JSON_VALUE,
	produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public void updateRequestStatus(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam(value = "studyInstanceUID") String studyInstanceUID) {
		RequestProcedureService requestProcedureService = Context.getService(RequestProcedureService.class);
		List<RequestProcedure> requestProcedures = requestProcedureService.getAllByStudyInstanceUID(studyInstanceUID);
		for (RequestProcedure requestProcedure : requestProcedures) {
			requestProcedure.setStatus("Complete");
			requestProcedureService.updateRequstStatus(requestProcedure);
		}
	}
}
