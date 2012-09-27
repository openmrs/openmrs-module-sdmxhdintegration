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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jembi.sdmxhd.SDMXHDMessage;
import org.jembi.sdmxhd.convenience.DimensionWrapper;
import org.jembi.sdmxhd.csds.CSDS;
import org.jembi.sdmxhd.csds.DataSet;
import org.jembi.sdmxhd.csds.Group;
import org.jembi.sdmxhd.csds.Obs;
import org.jembi.sdmxhd.csds.Section;
import org.jembi.sdmxhd.dsd.CodeRef;
import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.dsd.Dimension;
import org.jembi.sdmxhd.dsd.HierarchicalCodelist;
import org.jembi.sdmxhd.dsd.Hierarchy;
import org.jembi.sdmxhd.dsd.KeyFamily;
import org.jembi.sdmxhd.header.Header;
import org.jembi.sdmxhd.header.Sender;
import org.jembi.sdmxhd.parser.SDMXHDParser;
import org.jembi.sdmxhd.primitives.Code;
import org.jembi.sdmxhd.primitives.CodeList;
import org.jembi.sdmxhd.primitives.LocalizedString;
import org.jembi.sdmxhd.util.Constants;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.ReportTemplateRenderer;
import org.openmrs.module.sdmxhdintegration.Utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Renders a ReportDefinition into an SDMX-HD message, given the associated template, and mapping properties
 */
@Handler
@Localized("reporting.SdmxReportRenderer")
public class SdmxReportRenderer extends ReportTemplateRenderer {
	
	protected transient Log log = LogFactory.getLog(this.getClass());

	public static final String DATE_FORMAT = "yyyy-MM-dd";
	
	/**
     * @see ReportRenderer#render(ReportData, String, OutputStream)
     */
    @Override
    public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {

		ReportDesign design = getDesign(argument);
		EvaluationContext context = reportData.getContext();
		SdmxDesignConfiguration config = getDesignConfiguration(design);
		
		if (config == null) {
			throw new RenderingException("No sdmx design configuration file found. Unable to render.  Please add this as a report design resource file.");
		}
		log.info("Loaded config file with " + config.getColumnMappings().size() + " configured mappings");

		// Load the SDMX-HD Message
		SDMXHDMessage sdmxMessage = getSdmxMessage(getTemplate(design));
		DSD sdmxDsd = sdmxMessage.getDsd();
		
		// Resolve and validate report dates and time periods
		Date startDate = (Date)context.getParameterValue(config.getStartDateParameterName());
		Date endDate = (Date)context.getParameterValue(config.getEndDateParameterName());
		
		String frequency = config.getReportfrequency();
		String timePeriod = null;
    	if (frequency != null) {
    		if (frequency.equals("M")) {
    			if (DateUtil.getStartOfMonth(startDate).equals(startDate) &&
    				DateUtil.getEndOfMonth(endDate).equals(endDate) &&
    				Utils.datesMatchWithFormat(startDate, endDate, "yyyy-MM")) {
    				timePeriod = DateUtil.formatDate(startDate, "yyyy-MM");
    			}
    			else {
	    			throw new RenderingException("Frequency is set to monthly. Start and end date of " + startDate + "-" + endDate + " is not valid");
    			}
    		}
    		else if (frequency.equals("A")) {
    			if (DateUtil.getStartOfYear(startDate).equals(startDate) &&
        			DateUtil.getEndOfYear(endDate).equals(endDate) &&
        			Utils.datesMatchWithFormat(startDate, endDate, "yyyy")) {
        			timePeriod = DateUtil.formatDate(startDate, "yyyy");
        		}
        		else {
    	    		throw new RenderingException("Frequency is set to annual. Start and end date of " + startDate + "-" + endDate + " is not valid");
        		}
    		}
    		else {
    			throw new RenderingException("Only frequency of monthly and annually is currently supported.  Add more elses in here to support others");
    		}
    	}
    	
    	// Get all of the values out of the report data that are mapped in the configuration file
    	// And put them into a Map where the key is a sorted Map of indicator + dimension options
    	Map<String, Object> data = getBaseReplacementData(reportData, design);
    	Map<String, String> valuesByKey = new HashMap<String, String>();
    	for (Object key : config.getColumnMappings().keySet()) {
    		Object value = data.get(key);
    		if (value != null) {
    			String indicatorAndDimensionString = (String)config.getColumnMappings().get(key);
    			Map<String, String> indicatorAndDimensionMap = new TreeMap<String, String>();
    			for (String s : indicatorAndDimensionString.split("\\,")) {
    				String[] split = s.split("\\=");
    				indicatorAndDimensionMap.put(split[0], split[1]);
    			}
    			String sortedIndicatorAndDimensionString = ObjectUtil.toString(indicatorAndDimensionMap, "=", ",").toLowerCase().trim();
    			valuesByKey.put(sortedIndicatorAndDimensionString, ObjectUtil.format(value));
    		}
    	}
    	
    	log.info("ReportData contains " + valuesByKey.size() + " non-null values for the " + config.getColumnMappings().size() + " defined mappings.");
		
		// Start constructing the cross-sectional data set results
        CSDS csds = new CSDS();
        
        // Add in header data
        Header header = new Header();
        header.setId(config.getHeaderId());
        header.getName().addValue("en", config.getHeaderName());
        header.setTest(false);
        header.setTruncated(false);
        header.setPrepared(DateUtil.formatDate(new Date(), DATE_FORMAT));
        header.setReportingBegin(DateUtil.formatDate(startDate, DATE_FORMAT));
        header.setReportingEnd(DateUtil.formatDate(endDate, DATE_FORMAT));
        
        Sender sender = new Sender();
        sender.setId(design.getPropertyValue(config.getSenderId(), "OMRS"));
        sender.setName(design.getPropertyValue(config.getSenderName(), "OpenMRS"));
        header.getSenders().add(sender);
        
        csds.setHeader(header);
        
        // Add in DataSet data
        DataSet sdmxDataSet = new DataSet();
        sdmxDataSet.setReportingBeginDate(DateUtil.formatDate(startDate, DATE_FORMAT));
        sdmxDataSet.setReportingEndDate(DateUtil.formatDate(endDate, DATE_FORMAT));  
        if (config.getDatasetAttributes() != null) {
        	for (Map.Entry<Object, Object> e : config.getDatasetAttributes().entrySet()) {
        		String value = (String)e.getValue();
        		if (value.startsWith("gp:")) {
        			value = Context.getAdministrationService().getGlobalProperty(value.substring(3));
        		}
        		sdmxDataSet.addAttribute((String)e.getKey(), value);
        	}
        }
        csds.getDatasets().add(sdmxDataSet);
        
        // Add in Group data
        Group group = new Group();
        if (frequency != null) {
        	group.addAttribute("FREQ", frequency);
        }
        if (timePeriod != null) {
        	group.addAttribute("TIME_PERIOD", timePeriod);
        }
        sdmxDataSet.getGroups().add(group); 
        
        // Keep a reference of all of the Sections that are needed
        Map<String, Section> sectionMap = new LinkedHashMap<String, Section>();
        
        int numIndicatorsFound = 0;
        int numIndicatorsPopulated = 0;
        
        // Iterate over each keyfamily (most likely there is only one)
        for (KeyFamily kf : sdmxDsd.getKeyFamilies()) {
        	
        	// Get the CodeList containing all indicators
    		Dimension indicatorDimension = sdmxDsd.getIndicatorOrDataElementDimension(kf.getId());
    		CodeList indicatorCodeList = sdmxDsd.getCodeList(indicatorDimension.getCodelistRef());
        	
    		// Iterate across all of the indicators.
        	Set<LocalizedString> indicators = sdmxDsd.getIndicatorNames(kf.getId());
            for (LocalizedString ls : indicators) {
            	
            	Code indicatorCode = indicatorCodeList.getCodeByDescription(ls.getDefaultStr());
            	String primaryMeasure = sdmxDsd.getKeyFamily(kf.getId()).getComponents().getPrimaryMeasure().getConceptRef();
            	Code sectionCode = getSectionCodeForIndicator(sdmxDsd, indicatorCode);
            	Section section = sectionMap.get(sectionCode.getValue());
            	
            	// Find and construct the section that the indicator is contained within, if any
            	if (section == null) {
            		section = new Section();;
                	section.addAttribute("description", sectionCode.getDescription().getDefaultStr());
                	sectionMap.put(sectionCode.getValue(), section);
            	}
            	
            	// Retrieve all possible Obs for this indicator (all combinations of indicator + dimensions as Obs)
            	List<Obs> eligibleObs = new ArrayList<Obs>();
            	List<List<DimensionWrapper>> dwl = sdmxDsd.getAllCombinationofDimensionsForIndicator(ls.getDefaultStr(), kf.getId());
            	if (dwl.isEmpty()) {
            		eligibleObs.add(constructObs(primaryMeasure, indicatorDimension, indicatorCode, null));
            	}
            	for (List<DimensionWrapper> l : dwl) {
            		eligibleObs.add(constructObs(primaryMeasure, indicatorDimension, indicatorCode, l));
            	}
            	
            	// Find the value in the ReportData for each eligible Obs.  For any that are non-null, set the value and add to the appropriate section
            	for (Obs o : eligibleObs) {
            		Map<String, String> m = new TreeMap<String, String>(o.getAttributes());
            		String valueKey = ObjectUtil.toString(m, "=", ",").toLowerCase().trim();
            		String value = valuesByKey.get(valueKey);
            		numIndicatorsFound++;
            		if (value != null) {
            			o.addAttribute("value", value);
            			section.getObs().add(o);
            			numIndicatorsPopulated++;
            		}
            	}
            }
            
        	log.info("In the SDMX definition, found " + numIndicatorsFound + " defined indicators");
        	log.info("Of these " + numIndicatorsPopulated + " non-null values were found that match these");
        	
            // Add those sections into the output which have at least one non-null Obs
	        for (Section section : sectionMap.values()) {
	        	if (!section.getObs().isEmpty()) {
	        		group.getSections().add(section);
	        	}
	        }

            String derivedNamespace = Constants.DERIVED_NAMESPACE_PREFIX + kf.getAgencyID() + ":" + kf.getId() + ":" + kf.getVersion() + ":cross";
            try {
            	String xml = csds.toXML(derivedNamespace);
            	IOUtils.write(xml, out, "UTF-8");
            }
            catch (Exception e) {
            	throw new RenderingException("Unable to render data to xml", e);
            }
        }
    }
    
    /**
     * @return the Code from the appropriate CodeList that represents the section that the passed indicator goes in
     */
    public Code getSectionCodeForIndicator(DSD dsd, Code indicatorCode) {
    	HierarchicalCodelist codeList = dsd.getHierarchicalCodeList("HCL_CONFIGURATION_HIERARCHIES");
    	if (codeList != null) { //if the codelist hierarchy exists
    		Hierarchy h = codeList.getHierarchy("INDICATOR_SET_INDICATOR_HIERARCHY"); //this is the spot in the DSD where you put indicators into sets described in CL_ISET
    		if (h != null && h.getCodeRefs() != null) {
    			for (CodeRef cr : h.getCodeRefs()){ // these are one of these for each AL_ISET entry
    				if (cr.getChildren() != null) { 
    					for (CodeRef crInner : cr.getChildren()) { // these point to AL_INDICATOR
    						if (crInner.getCodeID().equals(indicatorCode.getValue())) { //we've found the indicator by its codeId
    							CodeList cl_iset = dsd.getCodeListByAlias(cr.getCodelistAliasRef()); //get the CL_ISET codelist
    							if (cl_iset != null) {
    								return cl_iset.getCodeByID(cr.getCodeID());
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    	return null;
    }
    
    /**
     * @return a new Obs that does not yet have a value or a section attached
     */
    public static Obs constructObs(String primaryMeasure, Dimension indicatorDimension, Code indicatorCode, List<DimensionWrapper> dimensions) {
    	Obs obs = new Obs();
    	obs.addAttribute(indicatorDimension.getConceptRef(), indicatorCode.getValue());
    	obs.elementName = primaryMeasure;
    	if (dimensions != null) {
    		for (DimensionWrapper dw : dimensions) {
    			obs.addAttribute(dw.getDimension().getConceptRef(), dw.getCode().getValue());
    		}
    	}
    	return obs;
    }
    
    /**
     * @return the SDMX-HD message stored in a ReportDesignResource
     */
	public static SDMXHDMessage getSdmxMessage(ReportDesignResource resource) {
		File tmpFile = null;
		try {        
			SDMXHDParser parser = new SDMXHDParser();
			tmpFile = File.createTempFile("sdmx", resource.getReportDesign().getUuid());
			FileUtils.writeByteArrayToFile(tmpFile, resource.getContents());
			ZipFile zipFile = new ZipFile(tmpFile);
			return parser.parse(zipFile);
		}
		catch (Exception e) {
			throw new RenderingException("Unable to load an sdmxhd message from report design resource", e);
		}
		finally {
			FileUtils.deleteQuietly(tmpFile);
		}
	}
	
	/**
	 * @see ReportTemplateRenderer#getBaseReplacementDataReportData, ReportDesign)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getBaseReplacementData(ReportData reportData, ReportDesign design) {
		// Populate the replacement data with all core values, and any data sets with only one row
		Map<String, Object> data = super.getBaseReplacementData(reportData, design);
		
		// Now go through and add data sets and add rows by index to replacement data as well
		for (String dataSetName : reportData.getDataSets().keySet()) {
			int rowNum = 0;
			for (DataSetRow row : reportData.getDataSets().get(dataSetName)) {
				for (Object entry : row.getColumnValues().entrySet()) {
					rowNum++;
					Map.Entry<DataSetColumn, Object> e = (Map.Entry<DataSetColumn, Object>) entry;
					String baseKey = dataSetName + SEPARATOR + e.getKey().getName() + SEPARATOR + rowNum;
					Object replacementValue = getReplacementValue(e.getValue());
					data.put(baseKey, replacementValue);
					String columnLabel = Context.getMessageSourceService().getMessage(e.getKey().getLabel());
					data.put(baseKey + SEPARATOR + LABEL, columnLabel);
					if (reportData.getDataSets().size() == 1) {
						data.put(e.getKey().getName() + SEPARATOR + rowNum, replacementValue);
						data.put(e.getKey().getName() + SEPARATOR + rowNum + SEPARATOR + LABEL, columnLabel);
					}
				}				
			}
		}
		return data;
	}
	
	/**
	 * @see ReportTemplateRenderer#getTemplate(ReportDesign)
	 */
	@Override
	public ReportDesignResource getTemplate(ReportDesign design) {
		for (ReportDesignResource resource : design.getResources()) {
			if ("zip".equals(resource.getExtension())) {
				return resource;
			}
		}
		return null;
	}

	/**
	 * @return the design configuration for the passed in design
	 */
	public static SdmxDesignConfiguration getDesignConfiguration(ReportDesign design) {
		for (ReportDesignResource resource : design.getResources()) {
			if ("xml".equals(resource.getExtension())) {
				try {
					return (SdmxDesignConfiguration)getConfigurationSerializer().fromXML(new String(resource.getContents(), "UTF-8"));
				}
				catch (Exception e) {
					throw new RenderingException("Unable to load design configuration from xml", e);
				}
			}
		}
		return null;
	}
	
	/**
	 * @return a serializer that can convert the design configuration to/from string
	 */
	public static XStream getConfigurationSerializer() {
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		xstream.alias("sdmxDesignConfiguration", SdmxDesignConfiguration.class);
		return xstream;
	}
	
	/**
	 * Encapsulates the configuration needed to map a report definition to the sdmx output
	 */
	public class SdmxDesignConfiguration {
		
		private String headerId = "SDMX-HD-CSDS";
		private String headerName = "OpenMRS SDMX-HD Export";
		private String senderId = "OMRS";
		private String senderName = "OpenMRS";
		private String startDateParameterName = "startDate";
		private String endDateParameterName = "endDate";
		private String reportfrequency;
		private Properties datasetAttributes; // An attribute value can refer to a global property by starting with gp:
		private Properties columnMappings; // The key references the value in the report.  The value is in the format ind=xxx,dim=yyy,dim=zzz
		
		public SdmxDesignConfiguration() {};
		
		/**
		 * @return the headerId
		 */
		public String getHeaderId() {
			return headerId;
		}

		/**
		 * @param headerId the headerId to set
		 */
		public void setHeaderId(String headerId) {
			this.headerId = headerId;
		}

		/**
		 * @return the headerName
		 */
		public String getHeaderName() {
			return headerName;
		}

		/**
		 * @param headerName the headerName to set
		 */
		public void setHeaderName(String headerName) {
			this.headerName = headerName;
		}

		/**
		 * @return the senderId
		 */
		public String getSenderId() {
			return senderId;
		}

		/**
		 * @param senderId the senderId to set
		 */
		public void setSenderId(String senderId) {
			this.senderId = senderId;
		}

		/**
		 * @return the senderName
		 */
		public String getSenderName() {
			return senderName;
		}

		/**
		 * @param senderName the senderName to set
		 */
		public void setSenderName(String senderName) {
			this.senderName = senderName;
		}

		/**
		 * @return the startDateParameterName
		 */
		public String getStartDateParameterName() {
			return startDateParameterName;
		}

		/**
		 * @param startDateParameterName the startDateParameterName to set
		 */
		public void setStartDateParameterName(String startDateParameterName) {
			this.startDateParameterName = startDateParameterName;
		}

		/**
		 * @return the endDateParameterName
		 */
		public String getEndDateParameterName() {
			return endDateParameterName;
		}

		/**
		 * @param endDateParameterName the endDateParameterName to setMap<String, String>
		 */
		public void setEndDateParameterName(String endDateParameterName) {
			this.endDateParameterName = endDateParameterName;
		}

		/**
		 * @return the reportfrequency
		 */
		public String getReportfrequency() {
			return reportfrequency;
		}

		/**
		 * @param reportfrequency the reportfrequency to set
		 */
		public void setReportfrequency(String reportfrequency) {
			this.reportfrequency = reportfrequency;
		}

		/**
		 * @return the datasetAttributes
		 */
		public Properties getDatasetAttributes() {
			return datasetAttributes;
		}

		/**
		 * @param datasetAttributes the datasetAttributes to set
		 */
		public void setDatasetAttributes(Properties datasetAttributes) {
			this.datasetAttributes = datasetAttributes;
		}
		
		/**
		 * Adds a datasetAttribute
		 */
		public void addDataSetAttribute(String key, String value) {
			if (datasetAttributes == null) {
				datasetAttributes = new Properties();
			}
			datasetAttributes.put(key, value);
		}

		/**
		 * @return the columnMappings
		 */
		public Properties getColumnMappings() {
			return columnMappings;
		}

		/**
		 * @param columnMappings the columnMappings to set
		 */
		public void setColumnMappings(Properties columnMappings) {
			this.columnMappings = columnMappings;
		}
		
		/**
		 * Adds a columnMapping
		 */
		public void addColumnMapping(String key, String value) {
			if (columnMappings == null) {
				columnMappings = new Properties();
			}
			columnMappings.put(key, value);
		}
	}
}
