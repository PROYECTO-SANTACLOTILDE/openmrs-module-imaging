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
package org.openmrs.module.imaging.page.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.ImagingConstants;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.OrthancConfigurationService;
import org.openmrs.module.imaging.api.RequestProcedureService;
import org.openmrs.module.imaging.api.RequestProcedureStepsService;
import org.openmrs.module.imaging.api.worklist.RequestProcedure;
import org.openmrs.module.imaging.api.worklist.RequestProcedureSteps;
import org.openmrs.ui.framework.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

@Controller
public class RequestProcedurePageController {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public void get(Model model, @RequestParam(value = "patientId") Patient patient) {
		RequestProcedureService requestProcedureService = Context.getService(RequestProcedureService.class);
		RequestProcedureStepsService requestProcedureStepsService = Context.getService(RequestProcedureStepsService.class);

		List<RequestProcedure> requestProcedures = requestProcedureService.getRequestProcedureByPatient(patient);
		Map<RequestProcedure, List<RequestProcedureSteps>> groupedSteps = new HashMap<>();

		for (RequestProcedure requestProcedure : requestProcedures) {
			List<RequestProcedureSteps> stepsList = requestProcedureStepsService.getAllStepsByRequestProcedure(requestProcedure);
			groupedSteps.put(requestProcedure, stepsList);
		}

		// Add to model
		model.addAttribute("requestProcedureMap", groupedSteps);

		OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
		model.addAttribute("orthancConfigurations", orthancConfigurationService.getAllOrthancConfigurations());
		model.addAttribute("privilegeEditWorklist",
		    Context.getAuthenticatedUser().hasPrivilege(ImagingConstants.PRIVILEGE_EDIT_WORKLIST));
	}

	/**
	 * @param redirectAttributes the redirect attributes
	 * @param patient the openmrs patient
	 * @param orthancConfigurationId the orthanc configuration ID
	 * @param accessionNumber The accession number
	 * @param studyInstanceUID The DICOM study instance UID
	 * @param requestingPhysician The physician who creates the request
	 * @param requestDescription The description of the request
	 * @param priority The priority of the request
	 * */
	@RequestMapping(value = "/module/imaging/newRequest.form", method = RequestMethod.POST)
	public String newRequest(RedirectAttributes redirectAttributes, @RequestParam(value = "patientId") Patient patient,
	        @RequestParam(value = "orthancConfigurationId") int orthancConfigurationId,
	        @RequestParam(value = "accessionNumber") String accessionNumber,
	        @RequestParam(value = "studyInstanceUID") String studyInstanceUID,
	        @RequestParam(value = "requestingPhysician") String requestingPhysician,
	        @RequestParam(value = "requestDescription") String requestDescription,
	        @RequestParam(value = "priority") String priority) {
		String message;
		boolean hasPrivilege = Context.getAuthenticatedUser().hasPrivilege(ImagingConstants.PRIVILEGE_EDIT_WORKLIST);
		if (hasPrivilege) {
			RequestProcedureService requestProcedureService = Context.getService(RequestProcedureService.class);
			OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
			OrthancConfiguration orthancConfiguration = orthancConfigurationService
			        .getOrthancConfiguration(orthancConfigurationId);
			try {
				RequestProcedure requestProcedure = new RequestProcedure();
				requestProcedure.setStatus("Incomplete");
				requestProcedure.setMrsPatient(patient);
				requestProcedure.setOrthancConfiguration(orthancConfiguration);
				requestProcedure.setAccessionNumber(accessionNumber);
				requestProcedure.setStudyInstanceUID(studyInstanceUID);
				requestProcedure.setRequestingPhysician(requestingPhysician);
				requestProcedure.setRequestDescription(requestDescription);
				requestProcedure.setPriority(priority);
				
				requestProcedureService.newRequest(requestProcedure);
				message = "The new request procedure is successfully added";
			}
			catch (IOException e) {
				message = "Add then new request procedure failed. Reason: " + e.getMessage();
			}
			
		} else {
			message = "Permission denied (you don't have the necessary privileges)";
		}
		redirectAttributes.addAttribute("patientId", patient.getId());
		redirectAttributes.addAttribute("message", message);
		return "redirect:/imaging/requestProcedure.page";
	}

	/**
	 * @param redirectAttributes The redirect attributes
	 * @param requestProcedureId The request procedure ID
	 * @param patient The openmrs patient
	 * */
	@RequestMapping(value = "/module/imaging/deleteRequest.form", method = RequestMethod.POST)
	public String deleteRequest(RedirectAttributes redirectAttributes,
	        @RequestParam(value = "requestProcedureId") int requestProcedureId,
	        @RequestParam(value = "patientId") Patient patient) {
		
		String message;
		boolean hasPrivilege = Context.getAuthenticatedUser().hasPrivilege(ImagingConstants.PRIVILEGE_EDIT_WORKLIST);
		if (hasPrivilege) {
			RequestProcedureService requestProcedureService = Context.getService(RequestProcedureService.class);
			RequestProcedureStepsService requestProcedureStepsService = Context
			        .getService(RequestProcedureStepsService.class);
			RequestProcedure requestProcedure = requestProcedureService.getRequestProcedure(requestProcedureId);
			List<RequestProcedureSteps> stepsList = requestProcedureStepsService
			        .getAllStepsByRequestProcedure(requestProcedure);
			if (stepsList.isEmpty()) {
				try {
					requestProcedureService.deleteRequestProcedure(requestProcedure);
					message = "Request procedure successfully deleted";
				}
				catch (IOException e) {
					message = "Deletion of request procedure failed. Reason: " + e.getMessage();
				}
				
			} else {
				message = "Permission denied (you don't have the necessary privileges)";
			}
		} else {
			message = "The request cannot be deleted because there are pending procedural steps.";
		}
		
		redirectAttributes.addAttribute("patientId", patient.getId());
		redirectAttributes.addAttribute("message", message);
		return "redirect:/imaging/requestProcedure.page";
	}

	/**
	 * @param redirectAttributes The redirect attributes
	 * @param modality The modality of the study
	 * @param scheduledReferringPhysician The physician who performs the steps
	 * @param requestedProcedureDescription The description of the request procedure
	 * @param stepStartDate TThe creation date of the steps
	 * @param stepStartTime The creation time of the steps
	 * @param stationName The station name
	 * @param procedureStepLocation The location of the procedure steps
	 * @param patient The openmrs patient
	 * */
	@RequestMapping(value = "/module/imaging/newProcedureSteps.form", method = RequestMethod.POST)
	public String newProcedureSteps(RedirectAttributes redirectAttributes,
	        @RequestParam(value = "requestProcedureId") int requestProcedureId,
	        @RequestParam(value = "modality") String modality, @RequestParam(value = "aetTitle") String aetTitle,
	        @RequestParam(value = "scheduledReferringPhysician") String scheduledReferringPhysician,
	        @RequestParam(value = "requestedProcedureDescription") String requestedProcedureDescription,
	        @RequestParam(value = "stepStartDate") String stepStartDate,
	        @RequestParam(value = "stepStartTime") String stepStartTime,
	        @RequestParam(value = "stationName") String stationName,
	        @RequestParam(value = "procedureStepLocation") String procedureStepLocation,
	        @RequestParam(value = "patientId") Patient patient) {
		String message;
		boolean hasPrivilege = Context.getAuthenticatedUser().hasPrivilege(ImagingConstants.PRIVILEGE_EDIT_WORKLIST);
		if (hasPrivilege) {
			RequestProcedureService requestProcedureService = Context.getService(RequestProcedureService.class);
			RequestProcedure requestProcedure = requestProcedureService.getRequestProcedure(requestProcedureId);
			RequestProcedureStepsService requestProcedureStepsService = Context
			        .getService(RequestProcedureStepsService.class);
			
			try {
				RequestProcedureSteps steps = new RequestProcedureSteps();
				steps.setRequestProcedure(requestProcedure);
				steps.setModality(modality);
				steps.setAetTitle(aetTitle);
				steps.setScheduledReferringPhysician(scheduledReferringPhysician);
				steps.setRequestedProcedureDescription(requestedProcedureDescription);
				steps.setStepStartDate(stepStartDate);
				steps.setStepStartTime(stepStartTime);
				steps.setStationName(stationName);
				steps.setProcedureStepLocation(procedureStepLocation);
				requestProcedureStepsService.newProcedureSteps(steps);
				
				requestProcedure.setStatus("Pending");
				requestProcedureService.updateRequstStatus(requestProcedure);
				
				message = "The steps of the request procedure are successfully created";
				
			}
			catch (IOException e) {
				message = "Create the request procedure steps failed. Reason: " + e.getMessage();
			}
		} else {
			message = "Permission denied (you don't have the necessary privileges)";
		}
		
		redirectAttributes.addAttribute("patientId", patient.getId());
		redirectAttributes.addAttribute("message", message);
		return "redirect:/imaging/requestProcedure.page";
	}

	/**
	 * @param redirectAttributes The redirect attributes
	 * @param stepsId The procedure steps ID
	 * @param patient The openmrs patient
	 * */
	@RequestMapping(value = "/module/imaging/deleteProcedureSteps.form", method = RequestMethod.POST)
	public String deleteProcedureSteps(RedirectAttributes redirectAttributes, @RequestParam(value = "id") int stepsId,
	        @RequestParam(value = "patientId") Patient patient) {
		String message;
		boolean hasPrivilege = Context.getAuthenticatedUser().hasPrivilege(ImagingConstants.PRIVILEGE_EDIT_WORKLIST);
		if (hasPrivilege) {
			RequestProcedureStepsService requestProcedureStepsService = Context
			        .getService(RequestProcedureStepsService.class);
			RequestProcedureSteps requestProcedureSteps = requestProcedureStepsService.getProcedureSteps(stepsId);
			try {
				requestProcedureStepsService.deleteProcedureSteps(requestProcedureSteps);
				message = "Request procedure successfully deleted";
			}
			catch (IOException e) {
				message = "Deletion of procedure steps failed. Reason: " + e.getMessage();
			}
			
		} else {
			message = "Permission denied (you don't have the necessary privileges)";
		}
		
		redirectAttributes.addAttribute("patientId", patient.getId());
		redirectAttributes.addAttribute("message", message);
		return "redirect:/imaging/requestProcedure.page";
	}
}
