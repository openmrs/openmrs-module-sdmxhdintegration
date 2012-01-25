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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.dsd.Dimension;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.jembi.sdmxhd.primitives.Code;
import org.jembi.sdmxhd.primitives.LocalizedString;
import org.openmrs.api.context.Context;
import org.openmrs.module.sdmxhdintegration.KeyFamilyMapping;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDService;
import org.openmrs.module.sdmxhdintegration.SimpleDimension;
import org.openmrs.module.sdmxhdintegration.Util;
import org.openmrs.module.sdmxhdintegration.reporting.extension.SDMXHDCohortIndicatorDataSetDefinition;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for mapping page
 */
@Controller
@RequestMapping("/module/sdmxhdintegration/mapping")
public class MappingFormController {
	
	/**
	 * Displays the form
	 * @param messageId the message id
	 * @param keyFamilyId the key family id
	 * @param model the page model
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws ExternalRefrenceNotFoundException
	 * @throws ValidationException
	 * @throws SchemaValidationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public void showForm(@RequestParam("messageId") Integer messageId,
	                     @RequestParam("keyFamilyId") String keyFamilyId,
	                     ModelMap model) throws IOException, XMLStreamException, ExternalRefrenceNotFoundException, ValidationException, SchemaValidationException {
		
		if (messageId != null) {
	    	SDMXHDService service = (SDMXHDService) Context.getService(SDMXHDService.class);
	    	SDMXHDMessage message = service.getMessage(messageId);
	    	
	    	// Add parameters to model
	    	model.addAttribute("messageId", messageId);
	    	model.addAttribute("keyFamilyId", keyFamilyId);
	    	
	    	// Get DSD indicators
	    	DSD dsd = service.getDataSetDefinition(message); 	
	    	Set<LocalizedString> indicatorNames = dsd.getIndicatorNames(keyFamilyId);
	    	
	    	// Make list of default indicator names
	    	List<String> simpleIndicatorNames = new ArrayList<String>();
	    	for (LocalizedString ls : indicatorNames) {
	    		simpleIndicatorNames.add(ls.getDefaultStr());
	    	}
	    	Collections.sort(simpleIndicatorNames);
	    	
	    	// Get SDMX-HD dimensions
	    	List<Dimension> sdmxhdDimensions = dsd.getAllIndicatorDimensions(keyFamilyId);
	    	if (sdmxhdDimensions == null || sdmxhdDimensions.size() < 1) {
	    		sdmxhdDimensions = dsd.getAllNonStanadrdDimensions(keyFamilyId);
	    	}
	    	
	    	// Convert to list of simple dimensions
	    	List<SimpleDimension> sDims = new ArrayList<SimpleDimension>();
	    	for (Dimension d : sdmxhdDimensions) {
	    		SimpleDimension sd = new SimpleDimension(d.getConceptRef());
	
	    		for (Code c : dsd.getCodeList(d.getCodelistRef()).getCodes()) {
	    			sd.addValue(c.getDescription().getDefaultStr());
	    		}
	    		
	    		sDims.add(sd);
	    	}

	    	// Add SDMX-HD dimensions and indicators to the model
	    	model.addAttribute("sdmxhdDimensions", sDims);
	    	model.addAttribute("sdmxhdIndicators", simpleIndicatorNames);
	    	
	    	KeyFamilyMapping keyFamilyMapping = service.getKeyFamilyMapping(message, keyFamilyId);
	    	
	    	if (keyFamilyMapping.getReportDefinitionId() != null) {
	    		SDMXHDCohortIndicatorDataSetDefinition omrsDSD = Util.getOMRSDataSetDefinition(message, keyFamilyId);
	    		
	    		// Add OpenMRS indicators and dimensions to model
		    	model.addAttribute("mappedIndicators", omrsDSD.getOMRSMappedIndicators());
		    	model.addAttribute("mappedDimensions", omrsDSD.getOMRSMappedDimensions());
		    	model.addAttribute("fixedDimensionValues", omrsDSD.getFixedDimensionValues());
	    	}
    	}	
	}	
}
