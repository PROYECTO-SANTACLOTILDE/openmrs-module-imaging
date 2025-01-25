package org.openmrs.module.imaging.page.controller;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.api.study.DicomSeries;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.ui.framework.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class SeriesPageController {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public void get(Model model, @RequestParam(value = "studyInstanceUID") String studyInstanceUID) throws IOException {
		try {
			DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
			List<DicomSeries> seriesList = dicomStudyService.fetchSeries(studyInstanceUID);
			if (seriesList.isEmpty()) {
				seriesList = dicomStudyService.fetchSeries(studyInstanceUID);
			}
			model.addAttribute("serieses", seriesList);
			model.addAttribute("studyInstanceUID", studyInstanceUID);
		}
		catch (IOException e) {
			throw new RuntimeException();
		}
	}
	
	@RequestMapping(value = "/module/imaging/deleteSeries.form", method = RequestMethod.POST)
	public String deleteSeries(RedirectAttributes redirectAttributes,
	        @RequestParam(value = "orthancSeriesUID") String orthancSeriesUID,
	        @RequestParam(value = "studyInstanceUID") String studyInstanceUID,
	        @RequestParam(value = "patientId") Patient patient) {
		
		DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
		DicomStudy seriesStudy = dicomStudyService.getDicomStudy(studyInstanceUID);
		int responseCode = dicomStudyService.deleteSeries(orthancSeriesUID, seriesStudy);
		String message;
		if (responseCode == 200) {
			message = "Series successfully deleted";
		} else {
			message = "Series failed deleted";
		}
		redirectAttributes.addAttribute("patientId", patient.getId());
		redirectAttributes.addAttribute("studyInstanceUID", studyInstanceUID);
		redirectAttributes.addAttribute("message", message);
		return "redirect:/imaging/series.page";
	}
	
}
