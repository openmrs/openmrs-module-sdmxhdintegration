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

import java.util.Properties;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Bean which contains configuration needed for the {@link SdmxReportRenderer}
 */
public class SdmxReportRendererConfig {
	
	private String headerId = "SDMX-HD-CSDS";
	private String headerName = "OpenMRS SDMX-HD Export";
	private String senderId = "OMRS";
	private String senderName = "OpenMRS";
	private String startDateParameterName = "startDate";
	private String endDateParameterName = "endDate";
	private String reportfrequency;
	private Properties datasetAttributes; // An attribute value can refer to a global property by starting with gp:
	private Properties columnMappings; // The key references the column in the report.  The value is in the format ind=xxx,dim=yyy,dim=zzz
	private Boolean outputWithinOriginalDsd = Boolean.FALSE;
	private Boolean compressOutput = Boolean.FALSE;
	
	public SdmxReportRendererConfig() {};
	
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
	
	/**
	 * @return the outputWithinOriginalDsd
	 */
	public Boolean getOutputWithinOriginalDsd() {
		return outputWithinOriginalDsd;
	}

	/**
	 * @param outputWithinOriginalDsd the outputWithinOriginalDsd to set
	 */
	public void setOutputWithinOriginalDsd(Boolean outputWithinOriginalDsd) {
		this.outputWithinOriginalDsd = outputWithinOriginalDsd;
	}

	/**
	 * @return the compressOutput
	 */
	public Boolean getCompressOutput() {
		return compressOutput;
	}

	/**
	 * @param compressOutput the compressOutput to set
	 */
	public void setCompressOutput(Boolean compressOutput) {
		this.compressOutput = compressOutput;
	}
	
	// Static methods

	/**
	 * @return a new instance given the passed xml
	 */
	public static SdmxReportRendererConfig loadFromXml(String xml) {
		return (SdmxReportRendererConfig)getConfigurationSerializer().fromXML(xml);
	}
	
	/**
	 * @return the serialized xml of the passed config
	 */
	public static String serializeToXml(SdmxReportRendererConfig config) {
		return getConfigurationSerializer().toXML(config);
	}
	
	/**
	 * @return a serializer that can convert the design configuration to/from string
	 */
	private static XStream getConfigurationSerializer() {
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		xstream.alias("sdmxDesignConfiguration", SdmxReportRendererConfig.class);
		return xstream;
	}
}
