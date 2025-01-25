package org.openmrs.module.imaging.fragment.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class StudiesFragmentController {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public void get(FragmentModel model, @FragmentParam("patientId") Patient patient) {
		DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
		List<DicomStudy> patientStudies = dicomStudyService.getStudies(patient);
		model.addAttribute("patientStudiesNo", patientStudies.size());
	}
}
