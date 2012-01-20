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

package org.openmrs.module.sdmxhdintegration.exceptions;

import org.jembi.sdmxhd.dsd.Dimension;

/**
 * Exception used by MappingDialogController
 */
public class DimensionNotMappedException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private Dimension dimension;
    
    /**
     * Constructs an exception
     * @param dimension the unmapped dimension
     */
    public DimensionNotMappedException(Dimension dimension) {
    	this.dimension = dimension;
    }
    
    /**
     * Gets the unmapped dimension
     * @return the dimension
     */
    public Dimension getDimension() {
    	return dimension;
    }
}
