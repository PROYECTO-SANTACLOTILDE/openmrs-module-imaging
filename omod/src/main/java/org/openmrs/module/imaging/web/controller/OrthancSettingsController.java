package org.openmrs.module.imaging.web.controller;

import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrthancSettingsController {
	
	@Autowired
	private AdministrationService administrationService;
	
	@RequestMapping(value = "/module/imaging/settings", method = RequestMethod.GET)
	public String getSettings(Model model) {
		model.addAttribute("orthancBaseUrl", administrationService.getGlobalProperty("orthanc.base.url"));
		model.addAttribute("username", administrationService.getGlobalProperty("orthanc.username"));
		return "imaging/settings";
	}
	
	@RequestMapping(value = "/module/imaging/settings", method = RequestMethod.POST)
	public String saveSettings(@RequestParam String orthancBaseUrl, @RequestParam String username,
	        @RequestParam String password) {
		administrationService.saveGlobalProperty(new GlobalProperty("orthanc.base.url", orthancBaseUrl));
		administrationService.saveGlobalProperty(new GlobalProperty("orthanc.username", username));
		administrationService.saveGlobalProperty(new GlobalProperty("orthanc.password", password));
		return "redirect:/module/imaging/settings";
	}
}
