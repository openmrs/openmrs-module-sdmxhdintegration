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

/**
 * Used by UI to represent an SDMX code 
 */
public class SimpleCode {
	private String value;
	private String description;
	
	/**
	 * Constructs a code
	 * @param value the code value
	 * @param description the code description
	 */
	public SimpleCode(String value, String description) {
		this.value = value;
		this.description = description;
	}
	
	/**
     * Gets the value
     * @return the value
     */
    public String getValue() {
	    return value;
    }
    
    /**
     * Gets the description
     * @return the description
     */
    public String getDescription() {
	    return description;
    }
}
