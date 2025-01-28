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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class StudiesPageController {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public void get(Model model, @RequestParam(value = "patientId") Patient patient) {
		DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
		List<DicomStudy> studies = dicomStudyService.getStudies(patient);
		model.addAttribute("studies", studies);
		
		OrthancConfigurationService orthancConfigureService = Context.getService(OrthancConfigurationService.class);
		model.addAttribute("orthancConfigurations", orthancConfigureService.getAllOrthancConfigurations());
		model.addAttribute("privilegeModifyImageData",
		    Context.getAuthenticatedUser().hasPrivilege(ImagingConstants.PRIVILEGE_Modify_IMAGE_DATA));
	}
	
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
	        @RequestParam(value = "patientId") Patient patient) {
		String message;
		try {
			DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
			OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
			
			if (orthancConfigurationId == -1) {
				dicomStudyService.fetchStudies();
			} else {
				dicomStudyService.fetchStudies(orthancConfigurationService.getOrthancConfiguration(orthancConfigurationId));
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
