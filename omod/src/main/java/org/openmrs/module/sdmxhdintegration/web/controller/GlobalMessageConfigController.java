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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.jembi.sdmxhd.primitives.CodeList;
import org.openmrs.api.context.Context;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDService;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("message")
@RequestMapping("/module/sdmxhdintegration/globalMessageConfig")
public class GlobalMessageConfigController {
	
	/**
	 * Displays the form
	 * @param model
	 * @param messageId
	 * @throws ValidationException
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws ExternalRefrenceNotFoundException
	 * @throws SchemaValidationException
	 */
	@RequestMapping(method=RequestMethod.GET)
	public void showPage(ModelMap model, @RequestParam("messageId") Integer messageId) throws ValidationException, IOException, XMLStreamException, ExternalRefrenceNotFoundException, SchemaValidationException {
		// Load message
		SDMXHDService service = Context.getService(SDMXHDService.class);
		SDMXHDMessage message = service.getMessage(messageId);
		
		// Prevent lazy initialization
		message.getGroupElementAttributes();
		
		DSD dsd = service.getSDMXHDDataSetDefinition(message);
		CodeList frequencyCodeList = dsd.getCodeList("CL_FREQ");
		if (frequencyCodeList != null) {
			model.addAttribute("frequencyCodes", frequencyCodeList.getCodes());
		}
		
		model.addAttribute("message", message);
	}
	
	/**
	 * Handles form submission
	 * @param request the request object
	 * @param message the message from the model
	 * @param result the binding result
	 * @return the view
	 */
	@RequestMapping(method=RequestMethod.POST)
	public String handleSubmission(HttpServletRequest request, @ModelAttribute("message") SDMXHDMessage message, BindingResult result) {
		
		SDMXHDService service = Context.getService(SDMXHDService.class);
		service.saveMessage(message);
		
		HttpSession session = request.getSession();
		session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Global message configuration saved");
		return "redirect:messages.list";
	}
	
}
