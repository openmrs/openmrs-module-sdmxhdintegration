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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.jembi.sdmxhd.dsd.Attribute;
import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition.CohortIndicatorAndDimensionColumn;
import org.openmrs.module.sdmxhdintegration.KeyFamilyMapping;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDService;
import org.openmrs.module.sdmxhdintegration.Utils;
import org.openmrs.module.sdmxhdintegration.reporting.extension.SDMXHDCohortIndicatorDataSetDefinition;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for key family attributes page
 */
@Controller
public class KeyFamilyAttributesController {
	
	/**
	 * Shows the page
	 * @param model the model
	 * @param messageId the message id
	 * @param keyFamilyId the key family id
	 * @throws IOException
	 * @throws ValidationException
	 * @throws XMLStreamException
	 * @throws ExternalRefrenceNotFoundException
	 * @throws SchemaValidationException
	 */
	@RequestMapping("/module/sdmxhdintegration/keyFamilyAttributes")
	public void showPage(ModelMap model, @RequestParam("messageId") Integer messageId, @RequestParam("keyFamilyId") String keyFamilyId)
			throws IOException, ValidationException, XMLStreamException, ExternalRefrenceNotFoundException, SchemaValidationException {
		
		SDMXHDService service = Context.getService(SDMXHDService.class);
		SDMXHDMessage message = service.getMessage(messageId);
		KeyFamilyMapping keyFamilyMapping = service.getKeyFamilyMapping(message, keyFamilyId);
		
		if (keyFamilyMapping.getReportDefinitionId() != null) {
			SDMXHDCohortIndicatorDataSetDefinition omrsDSD = Utils.getOMRSDataSetDefinition(message, keyFamilyId);
			
			model.addAttribute("columns", omrsDSD.getColumns());
			model.addAttribute("message", message);
			model.addAttribute("keyFamilyId", keyFamilyId);
			
			DSD dsd = Utils.getDataSetDefinition(message);
			
			// Get mandatory dataset level attributes and those attached
			List<Attribute> mandatoryDataSetAttrs = dsd.getAttributes(Attribute.DATASET_ATTACHMENT_LEVEL, Attribute.MANDATORY);
			Map<String, String> attachedDatasetAttrs = omrsDSD.getDataSetAttachedAttributes();
			boolean hasAllMandatoryDatasetAttrs = containsAllAttributes(mandatoryDataSetAttrs, attachedDatasetAttrs);		
			
			// Column level (series and obs) attributes
			Map<String, Boolean> hasAllMandatorySeriesAttrs = new HashMap<String, Boolean>();
			Map<String, Boolean> hasAllMandatoryObsAttrs = new HashMap<String, Boolean>();
			
			for (CohortIndicatorAndDimensionColumn c : omrsDSD.getColumns()) {
				
				// Get mandatory series level attributes and those attached
				List<Attribute> mandatorySeriesAttrs = dsd.getAttributes(Attribute.SERIES_ATTACHMENT_LEVEL, Attribute.MANDATORY);
				Map<String, String> attachedSeriesAttrs = omrsDSD.getSeriesAttachedAttributes().get(c.getName());
				hasAllMandatorySeriesAttrs.put(c.getName(), containsAllAttributes(mandatorySeriesAttrs, attachedSeriesAttrs));
				
				// Get mandatory obs level attributes and those attached
				List<Attribute> mandatoryObsAttrs = dsd.getAttributes(Attribute.OBSERVATION_ATTACHMENT_LEVEL, Attribute.MANDATORY);
				Map<String, String> attachedObsAttrs = omrsDSD.getObsAttachedAttributes().get(c.getName());
				hasAllMandatoryObsAttrs.put(c.getName(), containsAllAttributes(mandatoryObsAttrs, attachedObsAttrs));
			}
			
			model.addAttribute("attachedDatasetAttrs", attachedDatasetAttrs);
			model.addAttribute("hasAllMandatoryDatasetAttrs", hasAllMandatoryDatasetAttrs);
			model.addAttribute("hasAllMandatorySeriesAttrs", hasAllMandatorySeriesAttrs);
			model.addAttribute("hasAllMandatoryObsAttrs", hasAllMandatoryObsAttrs);
		}
	}
	
	/**
	 * Check a map of attributes to see if they include all the mandatory attributes from the given list
	 * @param mandatoryAttributes the mandatory attributes
	 * @param attributes the attributes
	 * @return true if list contains all mandatory attributes, else false
	 */
	private static boolean containsAllAttributes(List<Attribute> mandatoryAttributes, Map<String, String> attributes) {
		if (mandatoryAttributes == null || mandatoryAttributes.size() < 1)
			return true;
		else if (attributes == null)
			return false;
		
		for (Attribute a : mandatoryAttributes) {
			if (attributes.get(a.getConceptRef()) == null || attributes.get(a.getConceptRef()).equals(""))
				return false;
		}
		return true;
	}
	
}
