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

import org.openmrs.BaseOpenmrsObject;

/**
 * Entity which stores mappings for a key family
 */
public class KeyFamilyMapping extends BaseOpenmrsObject {
	
	private Integer id;
	private SDMXHDMessage message;
	private String keyFamilyId;
	private Integer reportDefinitionId;
	
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

    /**
     * Gets the SDMX message
     * @return the message
     */
    public SDMXHDMessage getMessage() {
    	return message;
    }
	
    /**
     * Sets the SDMX message
     * @param message the message
     */
    public void setMessage(SDMXHDMessage message) {
    	this.message = message;
    }

    /**
     * Gets the key family id
     * @return the id
     */
    public String getKeyFamilyId() {
    	return keyFamilyId;
    }
	
    /**
     * Sets the key family id
     * @param keyFamilyId the id
     */
    public void setKeyFamilyId(String keyFamilyId) {
    	this.keyFamilyId = keyFamilyId;
    }
	
    /**
     * Gets the report definition id
     * @return the id
     */
    public Integer getReportDefinitionId() {
    	return reportDefinitionId;
    }
	
    /**
     * Sets the report definition id
     * @param reportDefinitionId the id
     */
    public void setReportDefinitionId(Integer reportDefinitionId) {
    	this.reportDefinitionId = reportDefinitionId;
    }
}
