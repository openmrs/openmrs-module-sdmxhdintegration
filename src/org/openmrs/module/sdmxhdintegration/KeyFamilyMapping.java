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
import org.openmrs.module.reporting.report.definition.ReportDefinition;


/**
 *
 */
public class KeyFamilyMapping extends BaseOpenmrsObject {
	
	private Integer id;
	private SDMXHDMessage sdmxhdMessage;
	private String keyFamilyId;
	private Integer reportDefinitionId;

	
    /**
     * @return the sdmxhdMessage
     */
    public SDMXHDMessage getSdmxhdMessage() {
    	return sdmxhdMessage;
    }

	
    /**
     * @param sdmxhdMessage the sdmxhdMessage to set
     */
    public void setSdmxhdMessage(SDMXHDMessage sdmxhdMessage) {
    	this.sdmxhdMessage = sdmxhdMessage;
    }

	
    /**
     * @return the keyFamilyId
     */
    public String getKeyFamilyId() {
    	return keyFamilyId;
    }

	
    /**
     * @param keyFamilyId the keyFamilyId to set
     */
    public void setKeyFamilyId(String keyFamilyId) {
    	this.keyFamilyId = keyFamilyId;
    }

	
    /**
     * @return the reportDefinition
     */
    public Integer getReportDefinitionId() {
    	return reportDefinitionId;
    }

	
    /**
     * @param reportDefinition the reportDefinition to set
     */
    public void setReportDefinitionId(Integer reportDefinitionId) {
    	this.reportDefinitionId = reportDefinitionId;
    }

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

}
