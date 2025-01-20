package org.openmrs.module.imaging.page.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.ui.framework.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
public class StudiesPageController {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public void get(Model model, @RequestParam(value = "patientId") Patient patient) {
		try {
			DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
			dicomStudyService.fetchStudies(); // remove this later
			List<DicomStudy> studies = dicomStudyService.getStudies(patient);
			if (studies.isEmpty()) {
				dicomStudyService.fetchStudies();
				studies = dicomStudyService.getStudies(patient);
			}
			model.addAttribute("studies", studies);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@RequestMapping(value = "/module/imaging/deleteStudy.form", method = RequestMethod.POST)
	public void deleteStudy(HttpServletResponse response, @RequestParam(value = "dicomStudy") DicomStudy dicomStudy,
	        @RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
		try {
			String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
			try {
				String url = dicomStudy.getOrthancConfiguration().getOrthancBaseUrl();
				URL serverURL = new URL(url + "/studies/" + dicomStudy.getStudyInstanceUID());
				HttpURLConnection con = (HttpURLConnection) serverURL.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("Authorization", "Basic " + encoding);
				con.setRequestProperty("Content-Type", "application/json");
				// Check response code
				int responseCode = con.getResponseCode();
				System.out.println("Response Code: " + responseCode);
				
			}
			catch (MalformedURLException e) {
				response.getOutputStream().print("The URL is not well formed.");
			}
			catch (UnknownHostException e) {
				response.getOutputStream().print("The server could not be reached.");
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
