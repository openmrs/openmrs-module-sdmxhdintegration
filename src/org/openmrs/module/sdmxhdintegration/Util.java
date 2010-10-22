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

import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.sdmxhdintegration.reporting.extension.SDMXHDCohortIndicatorDataSetDefinition;


/**
 *
 */
public class Util {
	
	public static SDMXHDCohortIndicatorDataSetDefinition getOMRSDataSetDefinition(KeyFamilyMapping keyFamilyMapping) {
		// fetch OMRS DSD
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
	    ReportDefinition definition = rs.getDefinition(keyFamilyMapping.getReportDefinitionId());
	    if (definition == null) {
	    	return null;
	    }
	    Map<String, Mapped<? extends DataSetDefinition>> dataSetDefinitions = definition.getDataSetDefinitions();
	    Mapped<? extends DataSetDefinition> mapped = dataSetDefinitions.get(dataSetDefinitions.keySet().iterator().next());
	    DataSetDefinition dataSetDefinition = mapped.getParameterizable();
	    
	    return  (SDMXHDCohortIndicatorDataSetDefinition) dataSetDefinition;
	}
	
	public static SDMXHDCohortIndicatorDataSetDefinition getOMRSDataSetDefinition(SDMXHDMessage sdmxhdMessage, String keyFamilyId) {
		SDMXHDService s = Context.getService(SDMXHDService.class);
		KeyFamilyMapping keyFamilyMapping = s.getKeyFamilyMapping(sdmxhdMessage, keyFamilyId);
		return getOMRSDataSetDefinition(keyFamilyMapping);
	}

}
