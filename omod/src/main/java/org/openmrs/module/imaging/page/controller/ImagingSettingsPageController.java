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
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.ImagingConstants;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.api.OrthancConfigurationService;
import org.openmrs.ui.framework.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

@Controller
public class ImagingSettingsPageController {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public void get(Model model) {
		OrthancConfigurationService orthancConfigureService = Context.getService(OrthancConfigurationService.class);
		model.addAttribute("orthancConfigurations", orthancConfigureService.getAllOrthancConfigurations());
		model.addAttribute("privilegeManagerOrthancConfiguration",
		    Context.getAuthenticatedUser().hasPrivilege(ImagingConstants.PRIVILEGE_Manager_ORTHANC_CONFIGURATION));
	}
	
	/**
	 * @param redirectAttributes the redirect attributes for direct to another page
	 * @param url the orthanc url
	 * @param username the orthanc user name
	 * @param password the orthanc user password
	 * @return the response status code
	 */
	@RequestMapping(value = "/module/imaging/storeConfiguration.form", method = RequestMethod.POST)
	public String storeConfiguration(RedirectAttributes redirectAttributes, @RequestParam(value = "url") String url,
	        @RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
		OrthancConfigurationService orthancConfigureService = Context.getService(OrthancConfigurationService.class);
		url = url.trim();
		username = username.trim();
		password = password.trim();
		if (!url.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
			OrthancConfiguration oc = new OrthancConfiguration();
			oc.setOrthancBaseUrl(url);
			oc.setOrthancUsername(username);
			oc.setOrthancPassword(password);
			orthancConfigureService.saveOrthancConfiguration(oc);
			return "redirect:/imaging/imagingSettings.page";
		} else {
			redirectAttributes.addAttribute("message", "Saving orthanc configuration failed");
			return "redirect:/imaging/imagingSettings.page";
		}
	}
	
	/**
	 * @param redirectAttributes the redirect attributes for direct to another page
	 * @param id the configuration id
	 * @return the status of the delete orthanc configuration
	 */
	@RequestMapping(value = "/module/imaging/deleteConfiguration.form", method = RequestMethod.POST)
	public String deleteConfiguration(RedirectAttributes redirectAttributes, @RequestParam(value = "id") int id) {
		OrthancConfigurationService orthancConfigureService = Context.getService(OrthancConfigurationService.class);
		DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
		OrthancConfiguration config = orthancConfigureService.getOrthancConfiguration(id);
		boolean hasStudy = dicomStudyService.hasStudy(config);
		if (hasStudy) {
			redirectAttributes.addAttribute("message",
			    "The configuration can not be deleted because there is at least one study referring to it");
		} else {
			orthancConfigureService.removeOrthancConfiguration(orthancConfigureService.getOrthancConfiguration(id));
		}
		return "redirect:/imaging/imagingSettings.page";
	}
	
	/**
	 * @param response the response
	 * @param url the orthanc url
	 * @param username the orthanc user name
	 * @param password the orthanc user password
	 */
	@RequestMapping(value = "/module/imaging/checkConfiguration.form", method = RequestMethod.GET)
	@ResponseBody
	public void checkConfiguration(HttpServletResponse response, @RequestParam(value = "url") String url,
	        @RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
		DicomStudyService dicomStudyService = Context.getService(DicomStudyService.class);
		try {
			try {
				int status = dicomStudyService.testOrthancConnection(url, username, password);
				if (status == 200) {
					response.getOutputStream().print("Check successful. The Orthanc server responded correctly.");
				} else {
					response.getOutputStream().print("Check failed. The server responded with error " + status);
				}
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
