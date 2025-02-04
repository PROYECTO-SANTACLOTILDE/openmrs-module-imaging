package org.openmrs.module.imaging.page.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.ImagingConstants;
import org.openmrs.module.imaging.ImagingProperties;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.api.OrthancConfigurationService;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.ui.framework.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

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
		
		ImagingProperties imageProps = Context.getRegisteredComponent("imagingProperties", ImagingProperties.class);
		//	long maxUploadImageDataSize = imageProps.getMaxUploadImageDataSize();
		//	Context.getRegisteredComponent("multipartResolver", CommonsMultipartResolver.class).setMaxUploadSize(
		//				maxUploadImageDataSize);
		
		long testSize = 200000000; // Wei: later delete.
		Context.getRegisteredComponent("multipartResolver", CommonsMultipartResolver.class).setMaxUploadSize(testSize);
	}
	
	//	@ExceptionHandler(MaxUploadSizeExceededException.class)
	//	@ResponseStatus(HttpStatus.REQUEST_ENTITY_TOO_LARGE)
	//	public String handleMaxSizeException(RedirectAttributes redirectAttributes, MaxUploadSizeExceededException e) {
	//		log.error("************** too large");
	//		//			return new ResponseEntity<>("Total size exceeds maximum upload limit. Please upload smaller or less files.",
	//		//					HttpStatus.REQUEST_ENTITY_TOO_LARGE);
	//		String status = "File size exceeds maximum upload limit. Please upload a smaller file.";
	//		redirectAttributes.addAttribute("patientId", 8);
	//		redirectAttributes.addAttribute("message", status);
	//		return "redirect:/imaging/studies.page";
	//	}
	
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
	
	@RequestMapping(value = "/module/imaging/syncStudies.form", method = RequestMethod.POST)
	public String syncStudy(RedirectAttributes redirectAttributes,
	        @RequestParam(value = "orthancConfigurationId") int orthancConfigurationId,
	        @RequestParam(value = "fetchOption") String fetchOption, @RequestParam(value = "patientId") Patient patient) {
		String message;
		try {
			DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
			OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
			if (orthancConfigurationId == -1) {
				if (fetchOption.equals("all")) {
					dicomStudyService.fetchStudies();
				} else {
					//					dicomStudyService.fetchStudies(0);
					log.error("cccc");
				}
			} else {
				if (fetchOption.equals("all")) {
					dicomStudyService.fetchStudies(orthancConfigurationService
					        .getOrthancConfiguration(orthancConfigurationId));
				} else {
					dicomStudyService.fetchStudies(orthancConfigurationService.getOrthancConfiguration(0));
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
