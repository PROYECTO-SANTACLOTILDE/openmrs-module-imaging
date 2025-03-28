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
import org.openmrs.module.imaging.api.RequestProcedureStepService;
import org.openmrs.module.imaging.api.worklist.RequestProcedure;
import org.openmrs.module.imaging.api.worklist.RequestProcedureStep;
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
import java.io.IOException;
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
        RequestProcedureStepService requestProcedureStepService = Context.getService(RequestProcedureStepService.class);

        List<RequestProcedure> rps = requestProcedureService.getAllRequestProcedures();
        List<Map<String,Object>> result = new LinkedList<Map<String,Object>>();
        for (RequestProcedure rp : rps) {
            if(rp.getStatus().equalsIgnoreCase("Pending")) {
                Map<String,Object> map = new HashMap<String,Object>();
                writeProcedure(rp, map, requestProcedureStepService);
                result.add(map);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
	
	/**
	 * @param rp The request procedure object
	 * @param map The worklist data map
	 * @param requestProcedureStepService The request procedure step service
	 */
	private static void writeProcedure(RequestProcedure rp, Map<String, Object> map,
	        RequestProcedureStepService requestProcedureStepService) {

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

		// Read the procedure step
		List<RequestProcedureStep> procedureStep = requestProcedureStepService.getAllStepByRequestProcedure(rp);
		List<Map<String, Object>> stepList = new ArrayList<>();
		for(RequestProcedureStep step : procedureStep) {
			writeProcedureStep(step, stepList);
		}
		map.put("ScheduledProcedureStepSequence", stepList);
	}
	
	/**
	 * @param step The request procedure step
	 * @param stepList The list of the procedure step
	 */
	private static void writeProcedureStep(RequestProcedureStep step, List<Map<String, Object>> stepList) {
		Map<String, Object> stepMap = new HashMap<String, Object>();
		stepMap.put("Modality", step.getModality());
		stepMap.put("ScheduledStationAETitle", step.getAetTitle());
		stepMap.put("ScheduledProcedureStepStartDate", step.getStepStartDate());
		stepMap.put("ScheduledProcedureStepStartTime", step.getStepStartTime());
		stepMap.put("ScheduledPerformingPhysicianName", step.getScheduledReferringPhysician());
		stepMap.put("PerformedProcedureStepStatus", step.getPerformedProcedureStepStatus());
		stepMap.put("ScheduledProcedureStepDescription", step.getRequestedProcedureDescription());
		stepMap.put("ScheduledProcedureStepID", step.getId().toString());
		stepMap.put("ScheduledStationName", step.getStationName());
		stepMap.put("ScheduledProcedureStepLocation", step.getProcedureStepLocation());
		stepMap.put("CommentsOnTheScheduledProcedureStep", "no value available");
		stepList.add(stepMap);
	}
	
	/**
	 * @param studyInstanceUID The dicom study instance UID
	 */
	@RequestMapping(value = "/updatestatus", method = RequestMethod.POST,
	// consumes = MediaType.APPLICATION_JSON_VALUE,
	produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public void updateRequestStatus(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam(value = "studyInstanceUID") String studyInstanceUID,
	        @RequestParam(value = "performedProcedureStepID") String performedProcedureStepID) throws IOException {
		
		RequestProcedureService requestProcedureService = Context.getService(RequestProcedureService.class);
		RequestProcedureStepService requestProcedureStepService = Context.getService(RequestProcedureStepService.class);
		System.out.println("Study instances UID: " + studyInstanceUID);
		System.out.println("PerformedProcedureStepID: " + performedProcedureStepID);
		
		String testData = "3";
		
		if (!testData.isEmpty()) {
			try {
				RequestProcedureStep step = requestProcedureStepService.getProcedureStep(Integer.parseInt(testData));
				if (step != null && step.getRequestProcedure() != null) {
					// Update the perform procedure step status
					step.setPerformedProcedureStepStatus("COMPLETED");
					// Set the study instance UID created by modality device
					step.getRequestProcedure().setStudyInstanceUID(studyInstanceUID);
					try {
						requestProcedureStepService.updateProcedureStep(step);
					}
					catch (Exception e) {
						throw new RuntimeException("Error updating procedure step: " + e.getMessage());
					}
					
					// Check all procedure step perform status of the request
					RequestProcedure requestProcedure = step.getRequestProcedure();
					List<RequestProcedureStep> stepList = requestProcedureStepService
					        .getAllStepByRequestProcedure(requestProcedure);
					
					if (!stepList.isEmpty()) {
						boolean allComplete = stepList.stream().
								allMatch(item -> "COMPLETED".equalsIgnoreCase(item.getPerformedProcedureStepStatus().trim()));

						System.out.println("+++++++ all status of the step: " + allComplete);
						if (allComplete) {
							requestProcedure.setStatus("COMPLETED");
							requestProcedureService.updateRequstStatus(requestProcedure);
						}
					}
				}
			}
			catch (NumberFormatException e) {
				throw new NumberFormatException(e.getMessage());
			}
			catch (NullPointerException e) {
				throw new NullPointerException(e.getMessage());
			}
		}
	}
}
