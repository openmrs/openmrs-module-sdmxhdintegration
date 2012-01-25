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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.dsd.Dimension;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.jembi.sdmxhd.primitives.Code;
import org.jembi.sdmxhd.primitives.CodeList;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.dimension.CohortDimension;
import org.openmrs.module.reporting.indicator.dimension.service.DimensionService;
import org.openmrs.module.reporting.indicator.util.IndicatorUtil;
import org.openmrs.module.sdmxhdintegration.KeyFamilyMapping;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDService;
import org.openmrs.module.sdmxhdintegration.Utils;
import org.openmrs.module.sdmxhdintegration.reporting.extension.SDMXHDCohortIndicatorDataSetDefinition;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * Controller for dimension mapping dialog box
 */
@Controller
@RequestMapping("/module/sdmxhdintegration/mapDimensionDialog")
public class MapDimensionDialogController {
	
	private static final String DIM_OPT = "dimOptMapping.";
	
	/**
	 * Shows the dimensions mapping dialog
	 * @param model the model
	 * @param sdmxhdIndicator the SDMX indicator
	 * @param messageId the SDMX message id
	 * @param keyFamilyId the key family id
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public void showDialog(ModelMap model,
	                          @RequestParam("sdmxhdDimension") String sdmxhdDimension,
	                          @RequestParam("messageId") Integer messageId,
	                          @RequestParam(value="omrsDimension", required=false) Integer omrsDimension,
	                          @RequestParam("keyFamilyId") String keyFamilyId) throws IOException, XMLStreamException, ExternalRefrenceNotFoundException, ValidationException, SchemaValidationException {
		sdmxhdDimension = URLDecoder.decode(sdmxhdDimension);
		keyFamilyId = URLDecoder.decode(keyFamilyId);
		
    	model.addAttribute("sdmxhdDimension", sdmxhdDimension);
    	model.addAttribute("messageId", messageId);
    	model.addAttribute("keyFamilyId", keyFamilyId);
		
		// get all omrs Dimensions
    	DimensionService ds = Context.getService(DimensionService.class);
		List<org.openmrs.module.reporting.indicator.dimension.Dimension> omrsDimensions = ds.getAllDefinitions(false);
    	model.addAttribute("omrsDimensions", omrsDimensions);
    	
    	SDMXHDService sdmxhdService = Context.getService(SDMXHDService.class);
    	SDMXHDMessage sdmxhdMessage = sdmxhdService.getMessage(messageId);
    	
    	DSD sdmxhdDSD = Utils.getDataSetDefinition(sdmxhdMessage);
    	List<String> sdmxhdDimensionOptions = new ArrayList<String>();
    	Dimension sdmxhdDimensionObj = sdmxhdDSD.getDimension(sdmxhdDimension, keyFamilyId);
    	CodeList codeList = sdmxhdDSD.getCodeList(sdmxhdDimensionObj.getCodelistRef());
    	for (Code c : codeList.getCodes()) {
    		sdmxhdDimensionOptions.add(c.getDescription().getDefaultStr());
    	}
    	model.addAttribute("sdmxhdDimensionOptions", sdmxhdDimensionOptions);
    	
    	KeyFamilyMapping keyFamilyMapping = sdmxhdService.getKeyFamilyMapping(sdmxhdMessage, keyFamilyId);
    	SDMXHDCohortIndicatorDataSetDefinition omrsDSD = null;
    	
    	// if a OMRS DSD is attached then get the mapped dimension and the dimension options mappings
    	if (keyFamilyMapping.getReportDefinitionId() != null) {
    		omrsDSD = Utils.getDataSetDefinition(sdmxhdMessage, keyFamilyId);
    		// get mapped dimension if none is specified in the request
    		if (omrsDimension == null) {
	    		DataSetDefinitionService dss = Context.getService(DataSetDefinitionService.class);
	    		Integer omrsMappedDimensionId = omrsDSD.getOMRSMappedDimension(sdmxhdDimension);
	    		model.addAttribute("mappedOMRSDimensionId", omrsMappedDimensionId);
	    		omrsDimension = omrsMappedDimensionId;
	    		
	    		// get sdmx-hd -> omrs Dimension mappings for mapped Dimension
	    		if (omrsMappedDimensionId != null) {
	    			Map<String, String> mappedDimensionOptions = omrsDSD.getOMRSMappedDimensionOptions(sdmxhdDimension);
	    			model.addAttribute("mappedDimOpts", mappedDimensionOptions);
	    		}
    		}
    		// else set the dimension specified in the request
    		else {
    			model.addAttribute("mappedOMRSDimensionId", omrsDimension);
    		}
    	} else if (omrsDimension != null) {
    		model.addAttribute("mappedOMRSDimensionId", omrsDimension);
    	}
    	
    	// get omrs Dimension Options if there is a valid dimension to work with
    	if (omrsDimension != null) {
	    	org.openmrs.module.reporting.indicator.dimension.Dimension omrsDimensionObj = ds.getDefinition(CohortDimension.class, omrsDimension);
	    	List<String> omrsDimensionOptions = omrsDimensionObj.getOptionKeys();
	    	model.addAttribute("omrsDimensionOptions", omrsDimensionOptions);
    	}
    	
    	// get fixed value data
    	if (omrsDSD != null) {
	    	String fixedDimensionValue = omrsDSD.getFixedDimensionValues(sdmxhdDimension);
	    	if (fixedDimensionValue != null) {
	    		model.addAttribute("fixedValue", fixedDimensionValue);
	    		model.addAttribute("fixedValueCheckbox", true);
	    	} else {
	    		model.addAttribute("fixedValueCheckbox", false);
	    	}
    	} else {
    		model.addAttribute("fixedValueCheckbox", false);
    	}
	}
	
	/**
	 * Handles submission of dimension mapping dialog
	 */
	@RequestMapping(method=RequestMethod.POST)
	public String handleDialogSubmit(WebRequest request,
	                                        @RequestParam("mappedOMRSDimensionId") Integer mappedOMRSDimensionId,
	                                        @RequestParam("messageId") Integer messageId,
	                                        @RequestParam("sdmxhdDimension") String sdmxhdDimension,
	                                        @RequestParam("keyFamilyId") String keyFamilyId,
	                                        @RequestParam(value="fixedValueCheckbox", required=false) String fixedValueCheckbox,
	                                        @RequestParam(value="fixedValue", required=false) String fixedValue) throws Exception {
		sdmxhdDimension = URLDecoder.decode(sdmxhdDimension);
		
		SDMXHDMessage message = Context.getService(SDMXHDService.class).getMessage(messageId);
		SDMXHDCohortIndicatorDataSetDefinition omrsDSD = Utils.getDataSetDefinition(message, keyFamilyId);
		
		// delete previous mappings if there are any
		Integer omrsDimensionId = omrsDSD.getOMRSMappedDimension(sdmxhdDimension);
		if (omrsDimensionId != null) {
			// remove previous dimensions
			omrsDSD.removeDimension(omrsDimensionId + "");
			// TODO remove all Columns that use that dimension (reporting ticket?)
		}
		omrsDSD.getOMRSMappedDimensions().remove(sdmxhdDimension);
		omrsDSD.getOMRSMappedDimensionOptions().remove(sdmxhdDimension);
		omrsDSD.getFixedDimensionValues().remove(sdmxhdDimension);
		
		if (fixedValueCheckbox != null) {
			omrsDSD.addFixedDimensionValues(sdmxhdDimension, fixedValue);
		} else {
			// Build up Dimension Options Map
			Map<String,String> mappedDimOpts = new HashMap<String, String>();
			Map<String,String[]> paramMap = request.getParameterMap();
			for (String key : paramMap.keySet()) {
				if (key.startsWith(DIM_OPT)) {
					String mappedSDMXHSDimension = key.replaceFirst(DIM_OPT, "");
					String mappedOMRSDimension = paramMap.get(key)[0];
					mappedDimOpts.put(mappedSDMXHSDimension, mappedOMRSDimension);
				}
			}
			
			// Map Dimension and Dimension options
			omrsDSD.mapDimension(sdmxhdDimension, mappedOMRSDimensionId, mappedDimOpts);
			
			// add dimension to DataSet
			DimensionService ds = Context.getService(DimensionService.class);
			CohortDefinitionDimension omrsDimension = ds.getDefinition(CohortDefinitionDimension.class, mappedOMRSDimensionId);
			omrsDimension.addParameters(IndicatorUtil.getDefaultParameters());
			omrsDimension = (CohortDefinitionDimension) ds.saveDefinition(omrsDimension);
			omrsDSD.addDimension(mappedOMRSDimensionId + "", omrsDimension, IndicatorUtil.getDefaultParameterMappings());
		}
		
		// save dataset
		DataSetDefinitionService dss = Context.getService(DataSetDefinitionService.class);
		dss.saveDefinition(omrsDSD);
		
		return "redirect:redirectParent.form?url=keyFamilyMapping.form?messageId=" + messageId + "%26keyFamilyId=" + keyFamilyId;
	}
}
