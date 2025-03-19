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
		model.addAttribute("privilegeEditRequestProcedure",
		    Context.getAuthenticatedUser().hasPrivilege(ImagingConstants.PRIVILEGE_EDIT_REQUESTPROCEDURE));
	}
	
	@RequestMapping(value = "/module/imaging/newRequest.form", method = RequestMethod.POST)
	public String newRequest(RedirectAttributes redirectAttributes, @RequestParam(value = "patientId") Patient patient,
	        @RequestParam(value = "orthancConfigurationId") int orthancConfigurationId,
	        @RequestParam(value = "createdDate") String createdDate,
	        @RequestParam(value = "accessionNumber") String accessionNumber,
	        @RequestParam(value = "studyInstanceUID") String studyInstanceUID) {
		String message;
		boolean hasPrivilege = Context.getAuthenticatedUser().hasPrivilege(ImagingConstants.PRIVILEGE_EDIT_REQUESTPROCEDURE);
		if (hasPrivilege) {
			RequestProcedureService requestProcedureService = Context.getService(RequestProcedureService.class);
			OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
			OrthancConfiguration orthancConfiguration = orthancConfigurationService
			        .getOrthancConfiguration(orthancConfigurationId);
			try {
				RequestProcedure requestProcedure = new RequestProcedure();
				requestProcedure.setCreatedDate(createdDate);
				requestProcedure.setStatus("Incomplete");
				requestProcedure.setMrsPatient(patient);
				requestProcedure.setOrthancConfiguration(orthancConfiguration);
				requestProcedure.setAccessionNumber(accessionNumber);
				requestProcedure.setStudyInstanceUID(studyInstanceUID);
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
	
	@RequestMapping(value = "/module/imaging/deleteRequest.form", method = RequestMethod.POST)
	public String deleteRequest(RedirectAttributes redirectAttributes,
	        @RequestParam(value = "requestProcedureId") int requestProcedureId,
	        @RequestParam(value = "patientId") Patient patient) {
		
		String message;
		boolean hasPrivilege = Context.getAuthenticatedUser().hasPrivilege(ImagingConstants.PRIVILEGE_EDIT_REQUESTPROCEDURE);
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
	
	@RequestMapping(value = "/module/imaging/newProcedureSteps.form", method = RequestMethod.POST)
	public String newProcedureSteps(RedirectAttributes redirectAttributes,
	        @RequestParam(value = "requestProcedureId") int requestProcedureId,
	        @RequestParam(value = "modality") String modality, @RequestParam(value = "aetTitle") String aetTitle,
	        @RequestParam(value = "referringPhysician") String referringPhysician,
	        @RequestParam(value = "requestedProcedureDescription") String requestedProcedureDescription,
	        @RequestParam(value = "stepStartDate") String stepStartDate,
	        @RequestParam(value = "stepStartTime") String stepStartTime,
	        @RequestParam(value = "stationName") String stationName,
	        @RequestParam(value = "procedureStepLocation") String procedureStepLocation,
	        @RequestParam(value = "patientId") Patient patient) {
		String message;
		boolean hasPrivilege = Context.getAuthenticatedUser().hasPrivilege(ImagingConstants.PRIVILEGE_EDIT_REQUESTPROCEDURE);
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
				steps.setReferringPhysician(referringPhysician);
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
	
	@RequestMapping(value = "/module/imaging/deleteProcedureSteps.form", method = RequestMethod.POST)
	public String deleteProcedureSteps(RedirectAttributes redirectAttributes, @RequestParam(value = "id") int stepsId,
	        @RequestParam(value = "patientId") Patient patient) {
		String message;
		boolean hasPrivilege = Context.getAuthenticatedUser().hasPrivilege(ImagingConstants.PRIVILEGE_EDIT_REQUESTPROCEDURE);
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
