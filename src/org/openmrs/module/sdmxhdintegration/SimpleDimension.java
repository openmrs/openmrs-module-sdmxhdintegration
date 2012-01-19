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

import java.util.ArrayList;
import java.util.List;

/**
 * Used by UI to represent an SDMX dimension 
 */
public class SimpleDimension {
	
	private String name;
	private List<String> values = new ArrayList<String>();
	
    /**
     * Constructs a dimension
     * @param name the name
     */
    public SimpleDimension(String name) {
	    this.name = name;
    }

    /**
     * Gets the name
     * @return the name
     */
	public String getName() {
    	return name;
    }
	
	/**
     * Gets the values
     * @return the values
     */
    public List<String> getValues() {
    	return values;
    }
    
    /**
     * Adds a value
     * @param value the value
     */
    public void addValue(String value) {
    	values.add(value);
    }
}
