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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.jembi.sdmxhd.convenience.DimensionWrapper;
import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.dsd.Dimension;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.jembi.sdmxhd.primitives.Code;
import org.jembi.sdmxhd.primitives.CodeList;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.dimension.CohortDimension;
import org.openmrs.module.reporting.indicator.dimension.service.DimensionService;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.module.reporting.indicator.util.IndicatorUtil;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.sdmxhdintegration.KeyFamilyMapping;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDService;
import org.openmrs.module.sdmxhdintegration.Util;
import org.openmrs.module.sdmxhdintegration.exceptions.DimensionNotMappedException;
import org.openmrs.module.sdmxhdintegration.reporting.extension.SDMXHDCohortIndicatorDataSetDefinition;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * Controller for indicator and dimension mapping dialog boxes
 */
@Controller
public class MappingDialogController {
	
	/**
	 * Shows the dimensions mapping dialog
	 * @param model the model
	 * @param sdmxhdIndicator the SDMX indicator
	 * @param sdmxhdMessageId the SDMX message id
	 * @param keyFamilyId the key family id
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/module/sdmxhdintegration/mappingDimDialog")
	public void showDimDialog(ModelMap model,
	                          @RequestParam("sdmxhdDimension") String sdmxhdDimension,
	                          @RequestParam("sdmxhdMessageId") Integer sdmxhdMessageId,
	                          @RequestParam(value="omrsDimension", required=false) Integer omrsDimension,
	                          @RequestParam("keyfamilyid") String keyFamilyId) throws IOException, XMLStreamException, ExternalRefrenceNotFoundException, ValidationException, SchemaValidationException {
		sdmxhdDimension = URLDecoder.decode(sdmxhdDimension);
		keyFamilyId = URLDecoder.decode(keyFamilyId);
		
    	model.addAttribute("sdmxhdDimension", sdmxhdDimension);
    	model.addAttribute("sdmxhdMessageId", sdmxhdMessageId);
    	model.addAttribute("keyfamilyid", keyFamilyId);
		
		// get all omrs Dimensions
    	DimensionService ds = Context.getService(DimensionService.class);
		List<org.openmrs.module.reporting.indicator.dimension.Dimension> omrsDimensions = ds.getAllDefinitions(false);
    	model.addAttribute("omrsDimensions", omrsDimensions);
    	
    	SDMXHDService sdmxhdService = Context.getService(SDMXHDService.class);
    	SDMXHDMessage sdmxhdMessage = sdmxhdService.getSDMXHDMessage(sdmxhdMessageId);
    	
    	// get sdmxhd Dimension options
    	DSD sdmxhdDSD = sdmxhdService.getSDMXHDDataSetDefinition(sdmxhdMessage);
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
    		omrsDSD = getDataSetDefinition(sdmxhdMessage, keyFamilyId);
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
	
	public static final String DIM_OPT = "dimOptMapping.";
	
	/**
	 * Handles submission of dimension mapping dialog
	 */
	@RequestMapping(value="/module/sdmxhdintegration/mappingDimDialog", method=RequestMethod.POST)
	public String handleDimDialogSubmission(WebRequest request,
	                                        @RequestParam("mappedOMRSDimensionId") Integer mappedOMRSDimensionId,
	                                        @RequestParam("sdmxhdMessageId") Integer sdmxhdMessageId,
	                                        @RequestParam("sdmxhdDimension") String sdmxhdDimension,
	                                        @RequestParam("keyfamilyid") String keyFamilyId,
	                                        @RequestParam(value="fixedValueCheckbox", required=false) String fixedValueCheckbox,
	                                        @RequestParam(value="fixedValue", required=false) String fixedValue) throws Exception {
		sdmxhdDimension = URLDecoder.decode(sdmxhdDimension);
		
		// get SDMXHDMessage Object in OMRS
		SDMXHDMessage sdmxhdMessage = Context.getService(SDMXHDService.class).getSDMXHDMessage(sdmxhdMessageId);
		
		SDMXHDCohortIndicatorDataSetDefinition omrsDSD = getDataSetDefinition(sdmxhdMessage, keyFamilyId);
		
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
		
		// %26 == HTML encoding of &
		return "redirect:redirectParent.form?url=mapping.form?sdmxhdmessageid=" + sdmxhdMessageId + "%26keyfamilyid=" + keyFamilyId;
	}
	
	/**
	 * Shows the indicator mapping dialog
	 * @param model the model
	 * @param sdmxhdIndicator the SDMX indicator
	 * @param sdmxhdMessageId the SDMX message id
	 * @param keyFamilyId the key family id
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/module/sdmxhdintegration/mappingIndDialog")
	public void showIndDialog(ModelMap model, @RequestParam("sdmxhdIndicator") String sdmxhdIndicator,
	                          @RequestParam("sdmxhdMessageId") Integer sdmxhdMessageId,
	                          @RequestParam("keyfamilyid") String keyFamilyId) throws UnsupportedEncodingException {
		sdmxhdIndicator = URLDecoder.decode(sdmxhdIndicator);
		
		// get indicators
		IndicatorService is = Context.getService(IndicatorService.class);
		List<Indicator> omrsIndicators = is.getAllDefinitions(false);
    	model.addAttribute("omrsIndicators", omrsIndicators);
    	model.addAttribute("sdmxIndicator", sdmxhdIndicator);
    	model.addAttribute("sdmxhdMessageId");
    	
    	SDMXHDService sdmxhdService = Context.getService(SDMXHDService.class);
    	SDMXHDMessage sdmxhdMessage = sdmxhdService.getSDMXHDMessage(sdmxhdMessageId);
    	KeyFamilyMapping keyFamilyMapping = sdmxhdService.getKeyFamilyMapping(sdmxhdMessage, keyFamilyId);
    	
    	// if a OMRS DSD is attached then get the mapped indicator as well
    	if (keyFamilyMapping.getReportDefinitionId() != null) {
    		DataSetDefinitionService dss = Context.getService(DataSetDefinitionService.class);
    		SDMXHDCohortIndicatorDataSetDefinition omrsDSD = getDataSetDefinition(sdmxhdMessage, keyFamilyId);
    		Integer omrsMappedIndicatorId = omrsDSD.getOMRSMappedIndicator(sdmxhdIndicator);
    		model.addAttribute("mappedOMRSIndicatorId", omrsMappedIndicatorId);
    	}
	}
	
	/**
	 * Handles submission of indicator mapping dialog
	 */
	@RequestMapping(value="/module/sdmxhdintegration/mappingIndDialog", method=RequestMethod.POST)
	public String handleIndDialogSubmission(HttpSession httpSession,
			@RequestParam("mappedOMRSIndicatorId") Integer mappedOMRSIndicatorId,
			@RequestParam("sdmxhdMessageId") Integer sdmxhdMessageId,
			@RequestParam("sdmxhdIndicator") String sdmxhdIndicator,
			@RequestParam("keyfamilyid") String keyFamilyId) throws Exception {
		sdmxhdIndicator = URLDecoder.decode(sdmxhdIndicator);
		
		SDMXHDService sdmxhdService = Context.getService(SDMXHDService.class);
		
		// get SDMXHDMessage Object in OMRS
		SDMXHDMessage sdmxhdMessage = sdmxhdService.getSDMXHDMessage(sdmxhdMessageId);
		
		// get SDMX-HD DSD
		DSD sdmxhdDSD = sdmxhdService.getSDMXHDDataSetDefinition(sdmxhdMessage);
    	
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
    	
    	SDMXHDCohortIndicatorDataSetDefinition omrsDSD = getDataSetDefinition(sdmxhdMessage, keyFamilyId);
    	
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
	        
		// %26 == HTML encoding of &
        return "redirect:redirectParent.form?url=mapping.form?sdmxhdmessageid=" + sdmxhdMessageId + "%26keyfamilyid=" + keyFamilyId;
	}
	
	/* HELPER METHODS */

	/**
     * Auto generated method comment
     * 
     * @param sdmxhdMessage
     * @return
     */
    private SDMXHDCohortIndicatorDataSetDefinition getDataSetDefinition(SDMXHDMessage sdmxhdMessage, String keyFamilyId) {
    	SDMXHDService sdmxhdService = Context.getService(SDMXHDService.class);
    	KeyFamilyMapping keyFamilyMapping = sdmxhdService.getKeyFamilyMapping(sdmxhdMessage, keyFamilyId);
    	
    	if (keyFamilyMapping.getReportDefinitionId() == null) {
    		// create Report
    		PeriodIndicatorReportDefinition report = new PeriodIndicatorReportDefinition();
    		report.setName(sdmxhdMessage.getName() + " (" + keyFamilyId + ")");
    		report.setDescription("SDMX-HD Message Description: " + sdmxhdMessage.getDescription());
    		
    		// create OMRS DSD
    		SDMXHDCohortIndicatorDataSetDefinition omrsDSD = new SDMXHDCohortIndicatorDataSetDefinition();
    		omrsDSD.setName(sdmxhdMessage.getName() + " (SDMX-HD Module generated DSD)");
    		omrsDSD.setDescription("SDMX-HD Message Description: " + sdmxhdMessage.getDescription());
    		omrsDSD.setSDMXHDMessageId(sdmxhdMessage.getId());
    		
    		omrsDSD.addParameter(ReportingConstants.START_DATE_PARAMETER);
    		omrsDSD.addParameter(ReportingConstants.END_DATE_PARAMETER);
    		omrsDSD.addParameter(ReportingConstants.LOCATION_PARAMETER);
    		
    		// save dataset
    		DataSetDefinitionService dss = Context.getService(DataSetDefinitionService.class);
    		omrsDSD = (SDMXHDCohortIndicatorDataSetDefinition) dss.saveDefinition(omrsDSD);
    		
    		report.addDataSetDefinition(PeriodIndicatorReportDefinition.DEFAULT_DATASET_KEY, omrsDSD, IndicatorUtil.getDefaultParameterMappings());
    		// save report
    		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
    		rds.saveDefinition(report);
    		
    		// set foreign key
    		keyFamilyMapping.setReportDefinitionId(report.getId());
    		
    		sdmxhdService.saveKeyFamilyMapping(keyFamilyMapping);

    		return omrsDSD;
    	} else {
    	    return Util.getOMRSDataSetDefinition(keyFamilyMapping);
    	}    	
    }
}
