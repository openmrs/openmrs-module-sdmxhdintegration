
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
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
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

@Controller
@RequestMapping("/module/sdmxhdintegration/mapping")
public class MappingFormController {
	
	@RequestMapping(method = RequestMethod.GET)
	public void showForm(@RequestParam("sdmxhdmessageid") Integer sdmxMessageId,
	                     @RequestParam("keyfamilyid") String keyFamilyId,
	                     ModelMap model) throws IOException, XMLStreamException, ExternalRefrenceNotFoundException, ValidationException, SchemaValidationException {
		
		if (sdmxMessageId != null) {
	    	SDMXHDService sdmxhdService = (SDMXHDService) Context.getService(SDMXHDService.class);
	    	SDMXHDMessage sdmxhdMessage = sdmxhdService.getSDMXHDMessage(sdmxMessageId);
	    	
	    	model.addAttribute("sdmxhdmessageid", sdmxMessageId);
	    	model.addAttribute("keyfamilyid", keyFamilyId);
	    	
	    	// get sdmxhd Indicators
	    	DSD dsd = sdmxhdService.getSDMXHDDataSetDefinition(sdmxhdMessage);
	    	
	    	Set<LocalizedString> indicatorNames = dsd.getIndicatorNames(keyFamilyId);
	    	List<String> simpleIndicatorNames = new ArrayList<String>();
	    	for (LocalizedString ls : indicatorNames) {
	    		simpleIndicatorNames.add(ls.getDefaultStr());
	    	}
	    	Collections.sort(simpleIndicatorNames);
	    	model.addAttribute("sdmxhdIndicators", simpleIndicatorNames);
	    	
	    	// get sdmxhdDimensions
	    	List<Dimension> sdmxhdDimensions = dsd.getAllIndicatorDimensions(keyFamilyId);
	    	if (sdmxhdDimensions == null || sdmxhdDimensions.size() < 1) {
	    		sdmxhdDimensions = dsd.getAllNonStanadrdDimensions(keyFamilyId);
	    	}
	    	List<SimpleDimension> sDims = new ArrayList<SimpleDimension>();
	    	for (Dimension d : sdmxhdDimensions) {
	    		SimpleDimension sd = new SimpleDimension();
	    		sd.setName(d.getConceptRef());
	    		for (Code c : dsd.getCodeList(d.getCodelistRef()).getCodes()) {
	    			sd.getValues().add(c.getDescription().getDefaultStr());
	    		}
	    		sDims.add(sd);
	    	}
	    	//Collections.sort(sDims);
	    	model.addAttribute("sdmxhdDimensions", sDims);
	    	
	    	KeyFamilyMapping keyFamilyMapping = sdmxhdService.getKeyFamilyMapping(sdmxhdMessage, keyFamilyId);
	    	
	    	if (keyFamilyMapping.getReportDefinitionId() != null) {
	    		DataSetDefinitionService dss = Context.getService(DataSetDefinitionService.class);
	    		SDMXHDCohortIndicatorDataSetDefinition omrsDSD = Util.getOMRSDataSetDefinition(sdmxhdMessage, keyFamilyId);
	    		
		    	// get Indicator Mappings
		    	model.addAttribute("mappedIndicators", omrsDSD.getOMRSMappedIndicators());
		    	
		    	// get Dimension Mappings
		    	model.addAttribute("mappedDimensions", omrsDSD.getOMRSMappedDimensions());
		    	
		    	// get fixed value Dimensions
		    	model.addAttribute("fixedDimensionValues", omrsDSD.getFixedDimensionValues());
	    	}
    	}
		
	}
	
}
