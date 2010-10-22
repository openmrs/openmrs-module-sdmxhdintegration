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


public class SimpleCode {
	private String value;
	private String description;
	
	/**
     * @param description the description to set
     */
    public void setDescription(String description) {
	    this.description = description;
    }
	/**
     * @return the description
     */
    public String getDescription() {
	    return description;
    }
	/**
     * @param value the value to set
     */
    public void setValue(String value) {
	    this.value = value;
    }
	/**
     * @return the value
     */
    public String getValue() {
	    return value;
    }

}
