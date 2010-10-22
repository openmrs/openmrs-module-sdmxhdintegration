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
@RequestMapping("/module/sdmxhdintegration/configPage")
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
		
		return "redirect:configPage.form";
	}
	
}
