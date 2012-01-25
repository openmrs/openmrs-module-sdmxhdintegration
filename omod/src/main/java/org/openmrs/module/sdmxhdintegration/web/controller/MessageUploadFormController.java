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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.dsd.KeyFamily;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.sdmxhdintegration.KeyFamilyMapping;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessageValidator;
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
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

/**
 * Controller for message upload form
 */
@Controller
@SessionAttributes("message")
@RequestMapping("/module/sdmxhdintegration/messageUpload")
public class MessageUploadFormController {
	
	private static Log log = LogFactory.getLog(MessageUploadFormController.class);
	
	/**
	 * Displays the form
	 * @param messageId the message id (null for new messages)
	 * @param model the model
	 */
	@RequestMapping(method=RequestMethod.GET)
    public void showForm(@RequestParam(value = "messageId", required = false) Integer messageId, ModelMap model) {
		if (messageId != null) {
	    	SDMXHDService service = (SDMXHDService) Context.getService(SDMXHDService.class);
	    	model.addAttribute("message", service.getMessage(messageId));
    	} else {
    		model.addAttribute("message", new SDMXHDMessage());
    	}
    }
	
	/**
	 * Handles form submission
	 * @param request the request
	 * @param message the message
	 * @param result the binding result
	 * @param status the session status
	 * @return the view
	 * @throws IllegalStateException
	 */
	@RequestMapping(method=RequestMethod.POST)
    public String handleSubmission(HttpServletRequest request,
                                   @ModelAttribute("message") SDMXHDMessage message,
                                   BindingResult result,
                                   SessionStatus status) throws IllegalStateException {
		
		DefaultMultipartHttpServletRequest req = (DefaultMultipartHttpServletRequest) request;
		MultipartFile file = req.getFile("sdmxhdMessage");
		File destFile = null;
		
		if (!(file.getSize() <= 0)) {
			AdministrationService as = Context.getAdministrationService();
			String dir = as.getGlobalProperty("sdmxhdintegration.messageUploadDir");
			String filename = file.getOriginalFilename();
			filename = "_" + (new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss")).format(new Date()) + "_" + filename;
			destFile = new File(dir + File.separator + filename);
			destFile.mkdirs();
			
			try {
	            file.transferTo(destFile);
            }
            catch (IOException e) {
            	HttpSession session = request.getSession();
            	session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Could not save file. Make sure you have setup the upload directory using the configuration page and that the directory is readable by the tomcat user.");
            	return "/module/sdmxhdintegration/messageUpload";
            }
			
			message.setZipFilename(filename);
		}
		
		new SDMXHDMessageValidator().validate(message, result);
		
		if (result.hasErrors()) {
			log.error("SDMXHDMessage object failed validation");
			if (destFile != null) {
				destFile.delete();
			}
			return "/module/sdmxhdintegration/messageUpload";
		}
		
		SDMXHDService service = Context.getService(SDMXHDService.class);
		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
		service.saveMessage(message);
		
		// delete all existing mappings and reports
		List<KeyFamilyMapping> allKeyFamilyMappingsForMsg = service.getKeyFamilyMappingsFromMessage(message);
		for (Iterator<KeyFamilyMapping> iterator = allKeyFamilyMappingsForMsg.iterator(); iterator.hasNext();) {
	        KeyFamilyMapping kfm = iterator.next();
	        Integer reportDefinitionId = kfm.getReportDefinitionId();
	        service.deleteKeyFamilyMapping(kfm);
	        if (reportDefinitionId != null) {
	        	rds.purgeDefinition(rds.getDefinition(reportDefinitionId));
	        }
        }
		
		// create initial keyFamilyMappings
		try {
	        DSD dsd = service.getDataSetDefinition(message);
	        List<KeyFamily> keyFamilies = dsd.getKeyFamilies();
	        for (Iterator<KeyFamily> iterator = keyFamilies.iterator(); iterator.hasNext();) {
	            KeyFamily keyFamily = iterator.next();
	            
	            KeyFamilyMapping kfm = new KeyFamilyMapping();
            	kfm.setKeyFamilyId(keyFamily.getId());
	            kfm.setMessage(message);
	            service.saveKeyFamilyMapping(kfm);
            }
        }
        catch (Exception e) {
        	log.error("Error parsing SDMX-HD Message: " + e, e);
        	if (destFile != null) {
        		destFile.delete();
        	}
        	
        	service.deleteMessage(message);
        	result.rejectValue("sdmxhdZipFileName", "upload.file.rejected", "This file is not a valid zip file or it does not contain a valid SDMX-HD DataSetDefinition");
        	return "/module/sdmxhdintegration/messageUpload";
        }
        
		return "redirect:messages.list";
	}
}
