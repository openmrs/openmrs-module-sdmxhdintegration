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
package org.openmrs.module.sdmxhdintegration;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.BaseOpenmrsMetadata;


/**
 * This class represents a SDMX-HD Message. It stores the SDMX-HD as a ZipFile (as per specification)
 * and implements the various getter and setter required by an openmrs metadata object.
 */
public class SDMXHDMessage extends BaseOpenmrsMetadata {
	
	private Integer id;
	private String sdmxhdZipFileName;
	
	private Map<String, String> headerElementAttributes = new HashMap<String, String>();
	private Map<String, String> datasetElementAttributes = new HashMap<String, String>();
	private Map<String, String> groupElementAttributes = new HashMap<String, String>();

	/**
     * @see org.openmrs.OpenmrsObject#getId()
     */
    @Override
    public Integer getId() {
	    return id;
    }

	/**
     * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
     */
    @Override
    public void setId(Integer id) {
	    this.id = id;
    }

	
    public String getSdmxhdZipFileName() {
    	return sdmxhdZipFileName;
    }

	
    public void setSdmxhdZipFileName(String sdmxhdZipFileName) {
    	this.sdmxhdZipFileName = sdmxhdZipFileName;
    }

    @Override
    public boolean equals(Object obj) {
    	if (this == obj) {
    		return true;
    	}
    	
    	if (!(obj instanceof SDMXHDMessage)) {
    		return false;
    	}
    	
    	SDMXHDMessage sdmxhdMessage = (SDMXHDMessage) obj;
    	
    	return (this.getId().equals(sdmxhdMessage.getId()));
    }

	
    /**
     * @return the datasetElementAttributes
     */
    public Map<String, String> getDatasetElementAttributes() {
    	return datasetElementAttributes;
    }

	
    /**
     * @param datasetElementAttributes the datasetElementAttributes to set
     */
    public void setDatasetElementAttributes(Map<String, String> datasetElementAttributes) {
    	this.datasetElementAttributes = datasetElementAttributes;
    }

	
    /**
     * @return the headerElementAttributes
     */
    public Map<String, String> getHeaderElementAttributes() {
    	return headerElementAttributes;
    }

	
    /**
     * @param headerElementAttributes the headerElementAttributes to set
     */
    public void setHeaderElementAttributes(Map<String, String> headerElementAttributes) {
    	this.headerElementAttributes = headerElementAttributes;
    }

	
    /**
     * @return the groupElementAttributes
     */
    public Map<String, String> getGroupElementAttributes() {
    	return groupElementAttributes;
    }

	
    /**
     * @param groupElementAttributes the groupElementAttributes to set
     */
    public void setGroupElementAttributes(Map<String, String> groupElementAttributes) {
    	this.groupElementAttributes = groupElementAttributes;
    }
    
}
