package org.openmrs.module.imaging.web.controller;

import org.openmrs.module.imaging.OrthancService;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class StudiesController {
	
	@Autowired
	private OrthancService orthancService;
	
	@RequestMapping(value = "/module/radiology/patient/{patientId}/studies", method = RequestMethod.GET)
	public String getPatientStudies(@PathVariable("patientId") String patientId, Model model) {
		List<DicomStudy> studies = orthancService.getPatientStudies(patientId);
		model.addAttribute("studies", studies);
		return "studies";
	}
}
