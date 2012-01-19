
package org.openmrs.module.sdmxhdintegration.web.controller;

import javax.servlet.http.HttpSession;

import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/module/sdmxhdintegration/config")
public class ConfigurationPageController {

	@RequestMapping(method = RequestMethod.GET)
	public void showPage(ModelMap model) {
		AdministrationService as = Context.getAdministrationService();
		
		String value = as.getGlobalProperty("sdmxhdintegration.messageUploadDir");
		model.addAttribute("messageUploadDir", value);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String doPost(HttpSession session, @RequestParam String messageUploadDir) {
		
		AdministrationService as = Context.getAdministrationService();
		
		GlobalProperty gp = as.getGlobalPropertyObject("sdmxhdintegration.messageUploadDir");
		gp.setPropertyValue(messageUploadDir);
		as.saveGlobalProperty(gp);
		
		session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Configuration saved");
		
		return "redirect:config.form";
	}
	
}
