package org.openmrs.module.imaging.page.controller;

import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.api.study.DicomInstance;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.openmrs.ui.framework.Model;
import org.apache.commons.logging.Log;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
public class InstancesPageController {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public void get(Model model, @RequestParam(value = "seriesInstanceUID") String seriesInstanceUID) throws IOException {
		DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
		List<DicomInstance> instances = dicomStudyService.fetchInstances(seriesInstanceUID);
		model.addAttribute("instances", instances);
	}
	
	@RequestMapping(value = "/module/imaging/previewInstance.form", method = RequestMethod.GET)
	public ResponseEntity previewInstance(@RequestParam(value = "orthancInstanceUID") String orthancInstanceUID,
	        @RequestParam(value = "studyInstanceUID") String studyInstanceUID) {
		
		DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
		DicomStudy study = dicomStudyService.getDicomStudy(studyInstanceUID);
		try {
			DicomStudyService.PreviewResult previewResult = dicomStudyService
			        .fetchInstancePreview(orthancInstanceUID, study);
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-type", previewResult.contentType);
			return new ResponseEntity<byte[]>(previewResult.data, headers, HttpStatus.OK);
		}
		catch (IOException e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
