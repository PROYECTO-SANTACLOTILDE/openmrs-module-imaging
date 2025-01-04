package org.openmrs.module.imaging.page.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.OrthancConfigurationService;
import org.openmrs.ui.framework.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ImagingSettingsPageController {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public void get(Model model) {
		OrthancConfigurationService orthancConfigureService = Context.getService(OrthancConfigurationService.class);
		model.addAttribute("orthancConfigurations", orthancConfigureService.getAllOrthancConfigurations());
	}
	
	@RequestMapping(value = "/module/imaging/storeConfiguration.form", method = RequestMethod.POST)
	public String storeConfiguration(@RequestParam(value = "url") String url,
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
		} else {
			log.error("saving orthanc configuration failed");
		}
		
		return "redirect:/imaging/imagingSettings.page";
	}
	
	//	public void post(Model model, @RequestParam(value = "orthancBaseUrl") String orthancBaseUrl,
	//	        @RequestParam(value = "orthancUsername") String orthancUsername,
	//	        @RequestParam(value = "orthancPassword") String orthancPassword) {
	//		administrationService.saveGlobalProperty(new GlobalProperty("orthanc.base.url", orthancBaseUrl));
	//		administrationService.saveGlobalProperty(new GlobalProperty("orthanc.username", orthancUsername));
	//		administrationService.saveGlobalProperty(new GlobalProperty("orthanc.password", orthancPassword));
	//
	//		model.addAttribute("orthancBaseUrl", administrationService.getGlobalProperty("orthanc.base.url"));
	//		model.addAttribute("orthancUsername", administrationService.getGlobalProperty("orthanc.username"));
	//		model.addAttribute("orthancPassword", administrationService.getGlobalProperty("orthanc.password"));
	//		model.addAttribute("status", "Settings saved");
	//	}
	//
	//	public String post(WebRequest request, HttpSession httpSession, ModelMap model,
	//	        @RequestParam(required = false, value = "action") String action,
	//	        @ModelAttribute("OrthancConfiguration") OrthancConfiguration orthancConfiguration, BindingResult errors) {
	//
	//		MessageSourceService mss = Context.getMessageSourceService();
	//		OrthancConfigurationService orthancConfigureService = Context.getService(OrthancConfigurationService.class);
	//		if (!Context.isAuthenticated()) {
	//			errors.reject("orthancConfigure.auth.required");
	//		} else if (mss.getMessage("orthancConfigure.purgeOrthancConfigure").equals(action)) {
	//			try {
	//				orthancConfigureService.removeOrthancConfiguration(orthancConfiguration);
	//				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "orthancConfigure.delete.success");
	//				return "redirect:orthancConfigureList.list";
	//			}
	//			catch (Exception ex) {
	//				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "orthancConfigure.delete.failure");
	//				log.error("Failed to delete orthanc configure", ex);
	//				return "redirect:orthancConfigureForm.form?orthancConfigureId=" + request.getParameter("orthancConfigureId");
	//			}
	//		} else {
	//			orthancConfigureService.saveOrthancConfiguration(orthancConfiguration);
	//			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "orthancConfigure.saved");
	//		}
	//		return "redirect:orthancConfigureList.list";
	//	}
}
