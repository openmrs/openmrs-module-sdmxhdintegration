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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.xml.bind.ValidationException;

import org.jembi.sdmxhd.convenience.DimensionWrapper;
import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.dsd.Dimension;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.module.reporting.indicator.util.IndicatorUtil;
import org.openmrs.module.sdmxhdintegration.KeyFamilyMapping;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDService;
import org.openmrs.module.sdmxhdintegration.Utils;
import org.openmrs.module.sdmxhdintegration.exceptions.DimensionNotMappedException;
import org.openmrs.module.sdmxhdintegration.reporting.extension.SDMXHDCohortIndicatorDataSetDefinition;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for indicator and dimension mapping dialog boxes
 */
@Controller
@RequestMapping("/module/sdmxhdintegration/mapIndicatorDialog")
public class MapIndicatorDialogController {
	
	/**
	 * Shows the indicator mapping dialog
	 * @param model the model
	 * @param sdmxhdIndicator the SDMX indicator
	 * @param messageId the SDMX message id
	 * @param keyFamilyId the key family id
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public void showDialog(ModelMap model, @RequestParam("sdmxhdIndicator") String sdmxhdIndicator,
	                          @RequestParam("messageId") Integer messageId,
	                          @RequestParam("keyFamilyId") String keyFamilyId) throws UnsupportedEncodingException {
		
		sdmxhdIndicator = URLDecoder.decode(sdmxhdIndicator);
		
		// get indicators
		IndicatorService is = Context.getService(IndicatorService.class);
		List<Indicator> omrsIndicators = is.getAllDefinitions(false);
    	model.addAttribute("omrsIndicators", omrsIndicators);
    	model.addAttribute("sdmxIndicator", sdmxhdIndicator);
    	model.addAttribute("messageId", messageId);
    	
    	SDMXHDService sdmxhdService = Context.getService(SDMXHDService.class);
    	SDMXHDMessage sdmxhdMessage = sdmxhdService.getMessage(messageId);
    	KeyFamilyMapping keyFamilyMapping = sdmxhdService.getKeyFamilyMapping(sdmxhdMessage, keyFamilyId);
    	
    	// if a OMRS DSD is attached then get the mapped indicator as well
    	if (keyFamilyMapping.getReportDefinitionId() != null) {
    		DataSetDefinitionService dss = Context.getService(DataSetDefinitionService.class);
    		SDMXHDCohortIndicatorDataSetDefinition omrsDSD = Utils.getDataSetDefinition(sdmxhdMessage, keyFamilyId);
    		Integer omrsMappedIndicatorId = omrsDSD.getOMRSMappedIndicator(sdmxhdIndicator);
    		model.addAttribute("mappedOMRSIndicatorId", omrsMappedIndicatorId);
    	}
	}
	
	/**
	 * Handles submission of indicator mapping dialog
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String handleDialogSubmit(HttpSession httpSession,
			@RequestParam("mappedOMRSIndicatorId") Integer mappedOMRSIndicatorId,
			@RequestParam("messageId") Integer messageId,
			@RequestParam("sdmxhdIndicator") String sdmxhdIndicator,
			@RequestParam("keyFamilyId") String keyFamilyId) throws Exception {
		sdmxhdIndicator = URLDecoder.decode(sdmxhdIndicator);
		
		SDMXHDService service = Context.getService(SDMXHDService.class);
		SDMXHDMessage message = service.getMessage(messageId);
		DSD sdmxhdDSD = Utils.getDataSetDefinition(message);
    	
    	// get Indicator obj
    	// TODO do this properly with either uuid or create method in service to fetch by id
    	IndicatorService is = Context.getService(IndicatorService.class);
    	CohortIndicator omrsIndicator = null;
    	List<Indicator> allIndicators = is.getAllDefinitions(false);
    	for (Indicator i : allIndicators) {
    		if (i.getId().equals(mappedOMRSIndicatorId)) {
    			omrsIndicator = (CohortIndicator) i;
    			break;
    		}
    	}
    	
    	SDMXHDCohortIndicatorDataSetDefinition omrsDSD = Utils.getDataSetDefinition(message, keyFamilyId);
    	
    	// remove previous column specification if there is one
    	List<String> columnNames = omrsDSD.getIndicatorColumnMapping().get(sdmxhdIndicator);
    	if (columnNames != null) {
	    	for (Iterator<String> iterator = columnNames.iterator(); iterator.hasNext();) {
		        String columnName = iterator.next();
		        omrsDSD.removeColumn(columnName);
	        }
    	}
    	omrsDSD.removeIndicatorColumnMappings(sdmxhdIndicator);
		
		// Map Indicator
		omrsDSD.mapIndicator(sdmxhdIndicator, mappedOMRSIndicatorId);
		
		try {
	        // add column specifications for this indicator
	        // find all combinations of dimensions for this indicator
	        List<List<DimensionWrapper>> allCombinationofDimensionsForIndicator = sdmxhdDSD.getAllCombinationofDimensionsForIndicator(sdmxhdIndicator, keyFamilyId);
	        List<String> baseFixedDimensionToBeMapped = new ArrayList<String>();
	        
	        // if there is no disaggregation hierarchy for this indicator... use all (non-fixed) dimensions for disagregation
	        if (allCombinationofDimensionsForIndicator == null || allCombinationofDimensionsForIndicator.size() < 0) {
	        	Set<String> smxhdFixedDimensions = omrsDSD.getFixedDimensionValues().keySet();
	        	List<Dimension> allNonStanadrdDimensions = sdmxhdDSD.getAllNonStanadrdDimensions(keyFamilyId);
	        	List<Dimension> listToBeRemoved = new ArrayList<Dimension>();
	        	for (Iterator<Dimension> iterator = allNonStanadrdDimensions.iterator(); iterator.hasNext();) {
	                Dimension dimension = iterator.next();
	                if (smxhdFixedDimensions.contains(dimension.getConceptRef())) {
	                	listToBeRemoved.add(dimension);
	                }
                }
	        	// remove all fixed dimension from being calculated in the combination permutations ...
	        	allNonStanadrdDimensions.removeAll(listToBeRemoved);
	        	allCombinationofDimensionsForIndicator = sdmxhdDSD.getAllCombinationOfDimensions(keyFamilyId, allNonStanadrdDimensions);
	        	
	        	// ... but save them for mapping later
	        	for (Dimension d : listToBeRemoved) {
	        		baseFixedDimensionToBeMapped.add(d.getConceptRef());
	        	}
	        	
	        //added by Dave Thomas -- if the indicator is listed in the hierarchy as having no dimensions explicitly, just add indicator with no dimensions,
	        //rather than applying all possible dimension combinations.
	        } else if (allCombinationofDimensionsForIndicator.size() == 0){
	        	
	        	String columnName = omrsIndicator.getName();
	        	Mapped<CohortIndicator> mappedOMRSIndicator = new Mapped<CohortIndicator>(omrsIndicator, IndicatorUtil.getDefaultParameterMappings());
	        	omrsDSD.addColumn(columnName, columnName, mappedOMRSIndicator, new HashMap<String, String>());  //empty map = no dimensions
	        	omrsDSD.addIndicatorColumnMapping(sdmxhdIndicator, columnName);
	        	
	        }
	        
	        for (List<DimensionWrapper> combinationOfDimensions : allCombinationofDimensionsForIndicator) {
	        	// construct a dim option mapping for each combination
	        	//List<DimensionWrapper> combinationOfDimensions = sdmxhdDSD.getDimensionHierarchy(sdmxhdIndicator);
	        	StringBuilder dimOptsString = new StringBuilder();
	        	StringBuilder fixedDimsString = new StringBuilder();
	        	Map<String, String> dimOpts = new HashMap<String, String>();
	        	List<String> fixedDimensionToBeMapped = new ArrayList<String>();
	        	
	        	for (DimensionWrapper dw : combinationOfDimensions) {
	        		String omrsMappedDimensionOption = omrsDSD.getORMSMappedDimensionOption(dw.getDimension().getConceptRef(), dw.getCode().getDescription().getDefaultStr());
	        		Integer omrsMappedDimensionId = omrsDSD.getOMRSMappedDimension(dw.getDimension().getConceptRef());
	        		
	        		if (omrsMappedDimensionOption == null || omrsMappedDimensionId == null) {
	        			if (omrsDSD.getFixedDimensionValues(dw.getDimension().getConceptRef()) == null) {
	        				throw new DimensionNotMappedException(dw.getDimension());
	        			} 
	        			else {
	        				// Fixed value is set, no need to add this dimension
	        				// Just save it for mapping once we know the column name
	        				String sdmxhdDimension = dw.getDimension().getConceptRef();
	        				fixedDimensionToBeMapped.add(sdmxhdDimension);
	        				if (fixedDimsString.length() > 0) {
	        					fixedDimsString.append(", ");
	        				}
	        				fixedDimsString.append(sdmxhdDimension + "=" + omrsDSD.getFixedDimensionValues(sdmxhdDimension));
	        				continue;
	        			}
	        		}
	        		
	        		dimOpts.put(omrsMappedDimensionId + "", omrsMappedDimensionOption);
	        		if (dimOptsString.length() > 0) {
	        			dimOptsString.append(", ");
	        		}
	        		dimOptsString.append(omrsMappedDimensionId + "=" + omrsMappedDimensionOption);
	        	}
	        	
	        	dimOptsString.insert(0, " Dims[");
	        	dimOptsString.append("]");
	        	
	        	// make sure base fixed dimensions are included
	        	for (String sdmxhdDimension : baseFixedDimensionToBeMapped) {
	        		fixedDimsString.append(sdmxhdDimension + "=" + omrsDSD.getFixedDimensionValues(sdmxhdDimension));
	        	}
	        	
	        	fixedDimsString.insert(0, " FixedValDims:[");
	        	fixedDimsString.append("]");
	        	
	        	String columnName = omrsIndicator.getName() + dimOptsString.toString() + fixedDimsString.toString();
	        	
	        	Mapped<CohortIndicator> mappedOMRSIndicator = new Mapped<CohortIndicator>(omrsIndicator, IndicatorUtil.getDefaultParameterMappings());
	        	
	        	// add column specification for each dimension combination
	        	omrsDSD.addColumn(columnName, columnName, mappedOMRSIndicator, dimOpts);
	        	omrsDSD.addIndicatorColumnMapping(sdmxhdIndicator, columnName);
	        	
	        	// add base fixed value dimension (if any)
	        	fixedDimensionToBeMapped.addAll(baseFixedDimensionToBeMapped);
	        	// map fixed value dimensions
	        	Iterator<String> iter = fixedDimensionToBeMapped.iterator();
	        	while (iter.hasNext()) {
	                String sdmxhdDimension = iter.next();
	                omrsDSD.mapFixedDimension(columnName, sdmxhdDimension);
                }
	        }
	        
	        // save dataset
	        DataSetDefinitionService dss = Context.getService(DataSetDefinitionService.class);
	        dss.saveDefinition(omrsDSD);
	        
		} catch (DimensionNotMappedException e) {
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Indicator could not be mapped as one or more Dimensions that are used by this indicator have not been mapped");
        }
	        
        return "redirect:redirectParent.form?url=keyFamilyMapping.form?messageId=" + messageId + "%26keyFamilyId=" + keyFamilyId;
	}
}
