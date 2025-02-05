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
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.api.OrthancConfigurationService;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.ui.framework.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class StudiesPageController {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public void get(Model model, @RequestParam(value = "patientId") Patient patient) {
		
		// Context.getRegisteredComponent("multipartResolver", CommonsMultipartResolver.class).setMaxUploadSize(200_000_000);
		//Context.getRegisteredComponent("multipartResolver", CommonsMultipartResolver.class).setMaxUploadSize(-1);
		
		DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
		List<DicomStudy> studies = dicomStudyService.getStudies(patient);
		model.addAttribute("studies", studies);
		
		OrthancConfigurationService orthancConfigureService = Context.getService(OrthancConfigurationService.class);
		model.addAttribute("orthancConfigurations", orthancConfigureService.getAllOrthancConfigurations());
		model.addAttribute("privilegeModifyImageData",
		    Context.getAuthenticatedUser().hasPrivilege(ImagingConstants.PRIVILEGE_Modify_IMAGE_DATA));
		
		//		ImagingProperties imageProps = Context.getRegisteredComponent("imagingProperties", ImagingProperties.class);
		//		long maxUploadImageDataSize = imageProps.getMaxUploadImageDataSize();
		//		Context.getRegisteredComponent("multipartResolver", CommonsMultipartResolver.class).setMaxUploadSize(
		//					maxUploadImageDataSize);
		
		//		long testSize = 200000000; // Wei: later delete.
		//		Context.getRegisteredComponent("multipartResolver", CommonsMultipartResolver.class).setMaxUploadSize(testSize);
	}
	
	/**
	 * @param redirectAttributes the redirect attributes
	 * @return the model and view object
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	//	@ResponseStatus(HttpStatus.REQUEST_ENTITY_TOO_LARGE)
	public ModelAndView handleMaxSizeException(RedirectAttributes redirectAttributes) {
		//			return new ResponseEntity<>("Total size exceeds maximum upload limit. Please upload smaller or less files.",
		//					HttpStatus.REQUEST_ENTITY_TOO_LARGE);
		
		System.out.print("++ too large");
		String status = "File size exceeds maximum upload limit. Please upload a smaller file.";
		//		return ResponseEntity.status(HttpStatus.REQUEST_ENTITY_TOO_LARGE).body(
		//		    "Total size exceeds maximum upload limit. Please upload a smaller file.");
		
		//				String status = "File size exceeds maximum upload limit. Please upload a smaller file.";
		redirectAttributes.addAttribute("patientId", 8);
		redirectAttributes.addAttribute("message", status);
		//		return new RedirectController().redirectWithUsingRedirectView(redirectAttributes);
		return new ModelAndView("redirect:/imaging/studies.page");
	}
	
	/**
	 * @param redirectAttributes the redirect attributes
	 * @param response the http servlet response
	 * @param orthancConfigurationId the orthanc configuration ID
	 * @param files the upload files
	 * @param patient the openmrs patient
	 * @return the redirect url
	 */
	@RequestMapping(value = "/module/imaging/uploadStudy.form", method = RequestMethod.POST)
	public String uploadStudy(RedirectAttributes redirectAttributes, HttpServletResponse response,
	        @RequestParam(value = "orthancConfigurationId") int orthancConfigurationId,
	        @RequestParam("files") MultipartFile[] files, @RequestParam(value = "patientId") Patient patient) {
		log.info("Uploading " + files.length + " files");
		DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
		OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
		OrthancConfiguration config = orthancConfigurationService.getOrthancConfiguration(orthancConfigurationId);
		
		int numUploaded = 0;
		int numFiles = 0;
		for (MultipartFile file : files) {
			if (!file.isEmpty()) {
				numFiles++;
				try {
					int status = dicomStudyService.uploadFile(config, file.getInputStream());
					if (status == 200) {
						numUploaded++;
					}
				}
				catch (IOException e) {
					// do nothing
				}
			}
		}
		String message;
		if (numFiles == 0) {
			message = "No files to upload";
		} else if (numUploaded == numFiles) {
			message = "All files uploaded";
		} else {
			message = "Some files could not be uploaded. " + numUploaded + " of " + numFiles + " files uploaded.";
		}
		
		redirectAttributes.addAttribute("patientId", patient.getId());
		redirectAttributes.addAttribute("message", message);
		return "redirect:/imaging/studies.page";
	}
	
	/**
	 * @param redirectAttributes the redirect attributes
	 * @param orthancConfigurationId the orthanc configuration ID
	 * @param fetchOption the fetch option (all studies, new studies)
	 * @param patient the openmrs patient
	 * @return the redirect url
	 */
	@RequestMapping(value = "/module/imaging/syncStudies.form", method = RequestMethod.POST)
	public String syncStudy(RedirectAttributes redirectAttributes,
	        @RequestParam(value = "orthancConfigurationId") int orthancConfigurationId,
	        @RequestParam(value = "fetchOption") String fetchOption, @RequestParam(value = "patientId") Patient patient) {
		String message;
		try {
			DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
			if (orthancConfigurationId == -1) {
				if (fetchOption.equals("all")) {
					dicomStudyService.fetchAllStudies();
				} else {
					dicomStudyService.fetchNewChangedStudies();
				}
			} else {
				OrthancConfigurationService orthancConfigurationService = Context
				        .getService(OrthancConfigurationService.class);
				OrthancConfiguration config = orthancConfigurationService.getOrthancConfiguration(orthancConfigurationId);
				if (fetchOption.equals("all")) {
					dicomStudyService.fetchAllStudies(config);
				} else {
					dicomStudyService.fetchNewChangedStudies(config);
				}
			}
			message = "Studies successfully fetched";
		}
		catch (IOException e) {
			message = "Not all studies could be downloaded successfully. The server might be unavailable or stopped";
		}
		
		redirectAttributes.addAttribute("patientId", patient.getId());
		redirectAttributes.addAttribute("message", message);
		return "redirect:/imaging/syncStudies.page";
	}
	
	/**
	 * @param redirectAttributes the redirect attributes
	 * @param studyInstanceUID the study instance UID
	 * @param patient the openmrs patient
	 * @return the redirect url
	 */
	@RequestMapping(value = "/module/imaging/deleteStudy.form", method = RequestMethod.POST)
	public String deleteStudy(RedirectAttributes redirectAttributes,
	        @RequestParam(value = "studyInstanceUID") String studyInstanceUID,
	        @RequestParam(value = "patientId") Patient patient) {
		
		DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
		DicomStudy deleteStudy = dicomStudyService.getDicomStudy(studyInstanceUID);
		dicomStudyService.deleteStudy(deleteStudy);
		redirectAttributes.addAttribute("patientId", patient.getId());
		return "redirect:/imaging/studies.page";
	}
}
