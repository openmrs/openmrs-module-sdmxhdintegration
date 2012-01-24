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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.dsd.KeyFamily;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.sdmxhdintegration.KeyFamilyMapping;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for the messages list page
 */
@Controller
public class MessagesController {
	
	/**
	 * Displays the page
	 * @param deleteMsgId the message to delete (optional)
	 * @param model the model
	 * @throws ValidationException
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws ExternalRefrenceNotFoundException
	 * @throws SchemaValidationException
	 */
	@RequestMapping("/module/sdmxhdintegration/messages")
    public void showList(@RequestParam(value="deleteMsgId", required=false) Integer deleteMsgId, ModelMap model) throws ValidationException, IOException, XMLStreamException, ExternalRefrenceNotFoundException, SchemaValidationException {
		SDMXHDService sdmxhdService = (SDMXHDService) Context.getService(SDMXHDService.class);
		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
		
		// hAndle request to delete a message
		if (deleteMsgId != null) {
			SDMXHDMessage sdmxhdMessage = sdmxhdService.getMessage(deleteMsgId);
			sdmxhdMessage.setRetired(true);
			sdmxhdMessage.setDateRetired(new Date());
			sdmxhdMessage.setRetiredBy(Context.getAuthenticatedUser());
			sdmxhdMessage.setRetireReason("User Deleted");
			sdmxhdService.saveMessage(sdmxhdMessage);
		}
		
		List<SDMXHDMessage> messages = sdmxhdService.getAllMessages(false);
		List<KeyFamilyMapping> allKeyFamilyMappings = sdmxhdService.getAllKeyFamilyMappings();

		// get report uuid's
		Map<String, String> reportUuidMapping = new HashMap<String, String>();
		for (Iterator iterator = allKeyFamilyMappings.iterator(); iterator.hasNext();) {
	        KeyFamilyMapping keyFamilyMapping = (KeyFamilyMapping) iterator.next();
	        Integer reportDefinitionId = keyFamilyMapping.getReportDefinitionId();
	        if (reportDefinitionId != null) {
		        ReportDefinition reportDefinition = rds.getDefinition(reportDefinitionId);
		        reportUuidMapping.put(keyFamilyMapping.getKeyFamilyId(), reportDefinition.getUuid());
	        }
        }
		
		// get keyFamilyNames
		Map<String, String> keyFamilyNamesMap = new HashMap<String, String>();
		for (SDMXHDMessage message : messages) {
	        DSD dsd = sdmxhdService.getSDMXHDDataSetDefinition(message);
			for (Iterator<KeyFamilyMapping> iterator2 = allKeyFamilyMappings.iterator(); iterator2.hasNext();) {
		        KeyFamilyMapping keyFamilyMapping = iterator2.next();
		        if (keyFamilyMapping.getMessage().getId().equals(message.getId())) {
			        KeyFamily keyFamily = dsd.getKeyFamily(keyFamilyMapping.getKeyFamilyId());
			        keyFamilyNamesMap.put(keyFamily.getId(), keyFamily.getName().getDefaultStr());
		        }
	        }
        }
				
		model.addAttribute("messages", messages);
		model.addAttribute("keyFamilyMappings", allKeyFamilyMappings);
		model.addAttribute("reportUuidMapping", reportUuidMapping);
		model.addAttribute("keyFamilyNamesMap", keyFamilyNamesMap);
    }	
}
