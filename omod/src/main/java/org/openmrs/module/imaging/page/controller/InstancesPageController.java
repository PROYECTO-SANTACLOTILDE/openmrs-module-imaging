package org.openmrs.module.imaging.page.controller;

import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.api.study.DicomInstance;
import org.springframework.stereotype.Controller;
import org.openmrs.ui.framework.Model;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.List;

@Controller
public class InstancesPageController {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public void get(Model model, @RequestParam(value = "seriesInstanceUID") String seriesInstanceUID) {
		DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
		List<DicomInstance> instances = dicomStudyService.getInstances(seriesInstanceUID);
		model.addAttribute("instances", instances);
		model.addAttribute("seriesInstanceUID", seriesInstanceUID);
	}
}
