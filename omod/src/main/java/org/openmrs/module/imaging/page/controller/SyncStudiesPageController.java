package org.openmrs.module.imaging.page.controller;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.ImagingConstants;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.ui.framework.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;

@Controller
public class SyncStudiesPageController {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public void get(Model model, @RequestParam(value = "patientId") Patient patient,
	        @RequestParam(value = "message") String message) {
		DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
		List<DicomStudy> allStudies = dicomStudyService.getAllStudies();
		model.addAttribute("studies", allStudies);
		
		HashMap<String, Integer> match = new HashMap<String, Integer>();
		for (DicomStudy study : allStudies) {
			// https://github.com/xdrop/fuzzywuzzy?tab=readme-ov-file
			int score = FuzzySearch.tokenSetRatio(patient.getGivenName() + " " + patient.getFamilyName(),
			    study.getPatientName());
			match.put(study.getStudyInstanceUID(), score);
		}
		model.addAttribute("match", match);
		model.addAttribute("privilegeModifyImageData",
		    Context.getAuthenticatedUser().hasPrivilege(ImagingConstants.PRIVILEGE_Modify_IMAGE_DATA));
	}
	
	@RequestMapping(value = "/module/imaging/assignStudy.form", method = RequestMethod.POST)
	public String assignStudy(RedirectAttributes redirectAttributes, @RequestParam(value = "patientId") Patient patient,
	        @RequestParam(value = "studyInstanceUID") String studyInstanceUID, boolean isChecked) {
		DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
		String message;
		if (isChecked) {
			dicomStudyService.setPatient(dicomStudyService.getDicomStudy(studyInstanceUID), patient);
			message = "Study assigned to patient";
		} else {
			dicomStudyService.setPatient(dicomStudyService.getDicomStudy(studyInstanceUID), null);
			message = "Study assignment removed";
		}
		
		redirectAttributes.addAttribute("patientId", patient.getId());
		redirectAttributes.addAttribute("message", message);
		return "redirect:/imaging/syncStudies.page";
	}
}
