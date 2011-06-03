
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
