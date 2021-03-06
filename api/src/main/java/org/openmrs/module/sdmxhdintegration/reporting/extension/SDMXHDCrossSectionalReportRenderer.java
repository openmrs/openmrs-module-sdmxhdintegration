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

package org.openmrs.module.sdmxhdintegration.reporting.extension;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jembi.sdmxhd.csds.CSDS;
import org.jembi.sdmxhd.csds.DataSet;
import org.jembi.sdmxhd.csds.Group;
import org.jembi.sdmxhd.csds.Obs;
import org.jembi.sdmxhd.csds.Section;
import org.jembi.sdmxhd.dsd.CodeRef;
import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.dsd.Dimension;
import org.jembi.sdmxhd.dsd.KeyFamily;
import org.jembi.sdmxhd.header.Header;
import org.jembi.sdmxhd.header.Sender;
import org.jembi.sdmxhd.parser.SDMXHDParser;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.jembi.sdmxhd.primitives.Code;
import org.jembi.sdmxhd.primitives.CodeList;
import org.jembi.sdmxhd.util.Constants;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition.CohortIndicatorAndDimensionColumn;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.AbstractReportRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.sdmxhdintegration.KeyFamilyMapping;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDService;
import org.openmrs.module.sdmxhdintegration.Utils;
import org.springframework.util.StringUtils;

/**
 * Renderer for SDMX-HD cross-sectional report
 */
@Handler
@Localized("SDMX-HD Cross Sectional DataSet")
public class SDMXHDCrossSectionalReportRenderer extends AbstractReportRenderer {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private static final SimpleDateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd");

	/**
     * @see org.openmrs.module.report.renderer.ReportRenderer#getFilename(org.openmrs.module.report.ReportDefinition, java.lang.String)
     */
    @Override
    public String getFilename(ReportDefinition definition, String argument) {
	    return definition.getName() + ".zip";
    }

	/**
     * @see org.openmrs.module.report.renderer.ReportRenderer#getRenderedContentType(org.openmrs.module.report.ReportDefinition, java.lang.String)
     */
    @Override
    public String getRenderedContentType(ReportDefinition definition, String argument) {
	    return "application/zip";
    }

	/**
     * @see org.openmrs.module.report.renderer.ReportRenderer#getRenderingModes(org.openmrs.module.report.ReportDefinition)
     */
    @Override
    public Collection<RenderingMode> getRenderingModes(ReportDefinition definition) {
		if (definition.getDataSetDefinitions() == null || definition.getDataSetDefinitions().size() != 1) {
			return null;
		}
		
		// check that a corresponding SDMX-HD Message exists
		SDMXHDService sdmxhdService = Context.getService(SDMXHDService.class);
		List<KeyFamilyMapping> allKeyFamilyMappings = sdmxhdService.getAllKeyFamilyMappings();
		
		boolean mappingExists = false;
		for (KeyFamilyMapping kfm : allKeyFamilyMappings) {
			if (kfm.getReportDefinitionId() != null && kfm.getReportDefinitionId().equals(definition.getId())) {
				mappingExists = true;
				break;
			}
		}
		
		if (mappingExists) {
			return Collections.singleton(new RenderingMode(this, this.getLabel(), null, Integer.MIN_VALUE));
		} else {
			return null;
		}
    }

	/**
     * @see org.openmrs.module.report.renderer.ReportRenderer#render(org.openmrs.module.report.ReportData, java.lang.String, java.io.OutputStream)
     */
    @Override
    public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {
    	if (reportData.getDataSets().size() > 1) {
    		throw new RenderingException("This report contains multiple DataSets, this renderer does not support multiple DataSets");
    	} else if (reportData.getDataSets().size() < 1) {
    		throw new RenderingException("No DataSet defined in this report");
    	}
    	
    	// get results dataSet
    	org.openmrs.module.reporting.dataset.DataSet dataSet = reportData.getDataSets().get(reportData.getDataSets().keySet().iterator().next());
    	
    	// get OMRS DSD
    	Mapped<? extends DataSetDefinition> mappedOMRSDSD = reportData.getDefinition().getDataSetDefinitions().get(reportData.getDefinition().getDataSetDefinitions().keySet().iterator().next());
    	SDMXHDCohortIndicatorDataSetDefinition omrsDSD = (SDMXHDCohortIndicatorDataSetDefinition) mappedOMRSDSD.getParameterizable();
    	
    	// get SDMX-HD DSD
    	SDMXHDService sdmxhdService = (SDMXHDService) Context.getService(SDMXHDService.class);
    	SDMXHDMessage sdmxhdMessage = sdmxhdService.getMessage(omrsDSD.getSDMXHDMessageId());
    	
    	// get keyFamilyId
    	KeyFamilyMapping keyFamilyMapping = sdmxhdService.getKeyFamilyMappingByReportDefinitionId(reportData.getDefinition().getId());
    	String keyFamilyId = keyFamilyMapping.getKeyFamilyId();
    	
    	// get reporting month
    	Date reportStartDate = (Date) reportData.getContext().getParameterValue("startDate");
    	Date reportEndDate = (Date) reportData.getContext().getParameterValue("endDate");
    	String timePeriod = null;
    	
    	// calculate time period and make sure reporting dates make sense
    	String freq = sdmxhdMessage.getGroupElementAttributes().get("FREQ");
    	if (freq != null) {
	    	if (freq.equals("M")) {
	    		// check that start and end date are the report are beginning and end day of the same month
	    		Calendar startCal = Calendar.getInstance();
	    		startCal.setTime(reportStartDate);
	    		
	    		int lastDay = startCal.getActualMaximum(Calendar.DAY_OF_MONTH);
	    		int month = startCal.get(Calendar.MONTH);
	    		int year = startCal.get(Calendar.YEAR);
	    		
	    		Calendar endCal = Calendar.getInstance();
	    		endCal.setTime(reportEndDate);
	    		
	    		if (endCal.get(Calendar.MONTH) != month || endCal.get(Calendar.DAY_OF_MONTH) != lastDay || endCal.get(Calendar.YEAR) != year) {
	    			throw new RenderingException("Frequency is set to monthly, but the reporting stat and end date don't correspond to a start and end date of a specific month");
	    		}
	    		
	    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
	    		timePeriod = sdf.format(reportStartDate); 
	    			
	    	} else if (freq.equals("A")) {
	    		// check start and end date are beginning and end day of same year
	    		Calendar startCal = Calendar.getInstance();
	    		startCal.setTime(reportStartDate);
	    		
	    		int startDay = startCal.get(Calendar.DAY_OF_MONTH);
	    		int startMonth = startCal.get(Calendar.MONTH);
	    		int startYear = startCal.get(Calendar.YEAR);
	    		
	    		Calendar endCal = Calendar.getInstance();
	    		endCal.setTime(reportEndDate);
	    		
	    		int endDay = startCal.get(Calendar.DAY_OF_MONTH);
	    		int endMonth = startCal.get(Calendar.MONTH);
	    		int endYear = startCal.get(Calendar.YEAR);
	    		
	    		if (startDay != 1 || startMonth != Calendar.JANUARY || startYear != endYear || endDay != 31 || endMonth != Calendar.DECEMBER) {
	    			throw new RenderingException("Frequency is set to annual, but the reporting start and end date are not the begining of the end day of the same year");
	    		}
	    		
	    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
	    		timePeriod = sdf.format(reportStartDate); 
	    	}
	    	// TODO other checks
    	}
    	
    	
    	try {
    		String path = Context.getAdministrationService().getGlobalProperty("sdmxhdintegration.messageUploadDir");
	        ZipFile zf = new ZipFile(path + File.separator + sdmxhdMessage.getZipFilename());
	        SDMXHDParser parser = new SDMXHDParser();
	        org.jembi.sdmxhd.SDMXHDMessage sdmxhdData = parser.parse(zf);
	        DSD sdmxhdDSD = sdmxhdData.getDsd();
	        
	        //Construct CDS object
	        Sender p = new Sender();
	        p.setId("OMRS");
	        p.setName("OpenMRS");
	        
	        Header h = new Header();
	        h.setId("SDMX-HD-CSDS");
	        h.setTest(false);
	        h.setTruncated(false);
	        h.getName().addValue("en", "OpenMRS SDMX-HD Export");
	        h.setPrepared(iso8601DateFormat.format(new Date()));
	        h.getSenders().add(p);
	        
	        h.setReportingBegin(iso8601DateFormat.format(reportStartDate));
	        h.setReportingEnd(iso8601DateFormat.format(reportEndDate));
	        
	        // Construct dataset
	        DataSet sdmxhdDataSet = new DataSet();
	        
	        sdmxhdDataSet.setReportingBeginDate(iso8601DateFormat.format(reportStartDate));
	        sdmxhdDataSet.setReportingEndDate(iso8601DateFormat.format(reportEndDate));
	        
	        // Add fixed dataset attributes
	        Map<String, String> datasetElementAttributes = sdmxhdMessage.getDatasetElementAttributes();
	        for (String attribute : datasetElementAttributes.keySet()) {
	        	String value = datasetElementAttributes.get(attribute);
	        	if (StringUtils.hasText(value)) {
	        		sdmxhdDataSet.addAttribute(attribute, value);
	        	}
	        }
	        
	        // Construct group
	        Group group = new Group();
	        
	        // Set time period and frequency
	        if (timePeriod != null)
	        	group.addAttribute("TIME_PERIOD", timePeriod);
	        if (timePeriod != null)
	        	group.addAttribute("FREQ", freq);
	        
	        // Set DataSet attributes
	        Map<String, String> dataSetAttachedAttributes = omrsDSD.getDataSetAttachedAttributes();
	        for (String key : dataSetAttachedAttributes.keySet()) {
	        	sdmxhdDataSet.getAttributes().put(key, dataSetAttachedAttributes.get(key));
	        }
	        
	        // Holder for all sections. Will hold a default section if no explicit hierarchy is found in SL_ISET
	        List<Section> sectionList = new ArrayList<Section>();
	        
	        // Iterate each row and colum of the dataset
	        for (DataSetRow row : dataSet) {
	        	for (DataSetColumn column : row.getColumnValues().keySet()) {
	        		
	        		CohortIndicatorAndDimensionColumn cidColumn = (CohortIndicatorAndDimensionColumn) column;
	        		Object value = row.getColumnValues().get(column);
	        		String columnName = column.getName();
	        		
	        		//get the indicator code for this column
	        		CohortIndicator indicator = cidColumn.getIndicator().getParameterizable();
	        		String sdmxhdIndicatorName = omrsDSD.getSDMXHDMappedIndicator(indicator.getId());
	        		
	        		// get indicator/dataelement codelist
	        		Dimension indDim = sdmxhdDSD.getIndicatorOrDataElementDimension(keyFamilyId);
	        		CodeList indCodeList = sdmxhdDSD.getCodeList(indDim.getCodelistRef());
	        		Code indCode = indCodeList.getCodeByDescription(sdmxhdIndicatorName);

	        		//setup or get the section for this indicator
	        		Section section = getSectionHelper(indCode, sectionList, sdmxhdDSD);  //indicator code, listOfSections, message
	        		
	        		//get the dimension for the list of indicators (CL_INDICATOR)
	        		Dimension indDimension = sdmxhdDSD.getDimension(indCodeList);
	        		
	        		//construct new (SDMX-HD) obs to contain the indicator value
	        		Obs obs = new Obs();
	        		
	        		// set the indicator attribute
	        		obs.getAttributes().put(indDimension.getConceptRef(), indCode.getValue());
	        		
	        		// set Section Attributes
	        		Map<String, String> seriesAttachedAttributes = omrsDSD.getSeriesAttachedAttributes().get(columnName);
	        		if (seriesAttachedAttributes != null) {
		    	        for (String key : seriesAttachedAttributes.keySet()) {
		    	        	section.getAttributes().put(key, seriesAttachedAttributes.get(key));
		    	        }
	        		}
	        		
	    	        // write dimensions to obs
	        		Map<String, String> dimensionOptions = cidColumn.getDimensionOptions();
	        		// for each dimension option for this column
	        		for (String omrsDimensionId : dimensionOptions.keySet()) {
	        			Integer omrsDimensionIdInt = Integer.parseInt(omrsDimensionId);
	        			// find sdmx-hd dimension name in mapping
	        			String sdmxhdDimensionName = null;
	        			Map<String, Integer> omrsMappedDimensions = omrsDSD.getOMRSMappedDimensions();
	        			for (String sdmxhdDimensionNameTemp : omrsMappedDimensions.keySet()) {
	        				if (omrsMappedDimensions.get(sdmxhdDimensionNameTemp).equals(omrsDimensionIdInt)) {
	        					sdmxhdDimensionName = sdmxhdDimensionNameTemp;
	        					break;
	        				}
	        			}
	        			// find sdmx-hd dimension option name in mapping
	        			String omrsDimensionOptionName = dimensionOptions.get(omrsDimensionId);
	        			String sdmxhdDimensionOptionName = null;
	        			Map<String, String> omrsMappedDimensionOptions = omrsDSD.getOMRSMappedDimensionOptions().get(sdmxhdDimensionName);
	        			for (String sdmxhdDimensionOptionNameTemp : omrsMappedDimensionOptions.keySet()) {
	        				if (omrsMappedDimensionOptions.get(sdmxhdDimensionOptionNameTemp).equals(omrsDimensionOptionName)) {
	        					sdmxhdDimensionOptionName = sdmxhdDimensionOptionNameTemp;
	        					break;
	        				}
	        			}
	        			//find code corresponding to this dimension option
	        			Dimension sdmxhdDimension = sdmxhdDSD.getDimension(sdmxhdDimensionName, keyFamilyId);
	        			CodeList codeList = sdmxhdDSD.getCodeList(sdmxhdDimension.getCodelistRef());
	        			Code code = codeList.getCodeByDescription(sdmxhdDimensionOptionName);
	        			obs.addAttribute(sdmxhdDimensionName, code.getValue());
	        		}
	        		
	        		// add dimensions with default values
	        		List<String> mappedFixedDimensions = omrsDSD.getMappedFixedDimension(columnName);
	        		Map<String, String> fixedDimensionValues = omrsDSD.getFixedDimensionValues();
	        		for (String sdmxhdDimension : mappedFixedDimensions) {
	        			if (fixedDimensionValues.get(sdmxhdDimension) != null) {
	        				String fixedValue = fixedDimensionValues.get(sdmxhdDimension);
	        				Dimension dimension = sdmxhdDSD.getDimension(sdmxhdDimension, keyFamilyId);
		        			CodeList codeList = sdmxhdDSD.getCodeList(dimension.getCodelistRef());
		        			Code code = codeList.getCodeByDescription(fixedValue);
	        				obs.addAttribute(sdmxhdDimension, code.getValue());
	        			}
	        		}
	        		
	        		// set Obs Attributes
	        		Map<String, String> obsAttachedAttributes = omrsDSD.getObsAttachedAttributes().get(columnName);
	        		if (obsAttachedAttributes != null) {
		    	        for (String key : obsAttachedAttributes.keySet()) {
		    	        	obs.getAttributes().put(key, obsAttachedAttributes.get(key));
		    	        }
	        		}
	        		
	        		String primaryMeasure = sdmxhdDSD.getKeyFamily(keyFamilyId).getComponents().getPrimaryMeasure().getConceptRef();
	        		obs.elementName = primaryMeasure; 
	        		
	        		// write value
	        		if (value instanceof CohortIndicatorAndDimensionResult) {
	        			CohortIndicatorAndDimensionResult typedValue = (CohortIndicatorAndDimensionResult) value;
	        			obs.getAttributes().put("value", typedValue.getValue().toString());
	        		} else {
	        			obs.getAttributes().put("value", value.toString());
	        		}
	        		
	        		section.getObs().add(obs);
	        	}
	        }
	        
	        // Add all sections to group
	        for (Section section : sectionList) {
	        	group.getSections().add(section);
	        }
	        		
    		// Add group to dataset
    		sdmxhdDataSet.getGroups().add(group);
	        
	        CSDS csds = new CSDS();
	        csds.getDatasets().add(sdmxhdDataSet);
	        csds.setHeader(h);
	        
	        // build up namespace
	        KeyFamily keyFamily = sdmxhdDSD.getKeyFamily(keyFamilyId);
	        String derivedNamespace = Constants.DERIVED_NAMESPACE_PREFIX + keyFamily.getAgencyID() + ":" + keyFamily.getId() + ":" + keyFamily.getVersion() + ":cross";
	        String xml = csds.toXML(derivedNamespace);
	        
	        // output csds in original zip
	        zf = new ZipFile(path + File.separator + sdmxhdMessage.getZipFilename());
	        Utils.outputCsdsInDsdZip(zf, xml, out);
        }
        catch (IllegalArgumentException e) {
	        log.error("Error generated", e);
	        throw new RenderingException("Error rendering the SDMX-HD message: " + e.getMessage(), e);
        }
        catch (XMLStreamException e) {
	        log.error("Error generated", e);
	        throw new RenderingException("Error rendering the SDMX-HD message: " + e.getMessage(), e);
        }
        catch (ExternalRefrenceNotFoundException e) {
	        log.error("Error generated", e);
	        throw new RenderingException("Error rendering the SDMX-HD message: " + e.getMessage(), e);
        }
        catch (IllegalAccessException e) {
	        log.error("Error generated", e);
	        throw new RenderingException("Error rendering the SDMX-HD message: " + e.getMessage(), e);
        }
        catch (ValidationException e) {
	        log.error("Error generated", e);
	        throw new RenderingException("Error rendering the SDMX-HD message: " + e.getMessage(), e);
        }
        catch (SchemaValidationException e) {
	        log.error("Error generated", e);
	        throw new RenderingException("Error rendering the SDMX-HD message: " + e.getMessage(), e);
        }
    }
    
    /**
     * This uses the HCL_CONFIGURATION_HIERARCHIES, INDICATOR_SET_INDICATOR_HIERARCHY in the DSD 
     * to put an indicator in the right Section based on the CL_ISET file.  
     * If not found, all results will end up in 1 section.
     * @param indCode
     * @param sectionList
     * @param dsd
     * @return
     */
    public Section getSectionHelper(Code indCode, List<Section> sectionList, DSD dsd){
    	org.jembi.sdmxhd.dsd.HierarchicalCodelist codeList = dsd.getHierarchicalCodeList("HCL_CONFIGURATION_HIERARCHIES");
    	String descriptionAttributeText = "";
    	Code code = null;
    	
    	if (codeList != null) { //if the codelist hierarchy exists
    		org.jembi.sdmxhd.dsd.Hierarchy h = codeList.getHierarchy("INDICATOR_SET_INDICATOR_HIERARCHY"); //this is the spot in the DSD where you put indicators into sets described in CL_ISET
    		if (h != null && h.getCodeRefs() != null){
    			for (CodeRef cr : h.getCodeRefs()){ // these are one of these for each AL_ISET entry
    				if (cr.getChildren() != null){ 
    					for (CodeRef crInner : cr.getChildren()){ // these point to AL_INDICATOR
    						if (crInner.getCodeID().equals(indCode.getValue())){ //we've found the indicator by its codeId
    							CodeList cl_iset = dsd.getCodeListByAlias(cr.getCodelistAliasRef()); //get the CL_ISET codelist
    							if (cl_iset != null){
    								code = cl_iset.getCodeByID(cr.getCodeID());  //get the description of the Code in CL_ISET
    								descriptionAttributeText = code.getDescription().getDefaultStr();  //now we have the description of the section that this indicator should go into
    								break;
    							}
    						}
    					}
    					if (code != null)
    						break;
    				}
    			}
    		}
    	}
    	
    	// Are sections described in hierarchy in DSD?
    	if (code == null) { 
    		if (sectionList.size() == 0){
    			Section section = new Section();
    			sectionList.add(section);
    			return section;
    		} else {
    			return sectionList.get(0);
    		}	
    	} 
    	else
    		return findSectionByCodeValue(sectionList, descriptionAttributeText, code);
    }
    
    /**
     * Looks in a list of sections to find an existing section by its code value and 
     * returns a new section if not found
     * @param sections the list of existing sections
     * @param attributeText
     * @return Section
     */
    private Section findSectionByCodeValue(List<Section> sections, String descriptionAttributeText, Code code){
    	for (Section s : sections){
    		if (s.getAttributeValue("value") != null && s.getAttributeValue("value").equals(code.getValue()))
    			return s;
    	}

    	// Create new section
    	Section section = new Section();
    	section.addAttribute("value", code.getValue());
    	section.addAttribute("description", descriptionAttributeText);
    	sections.add(section);
    	return section;
    }
}
