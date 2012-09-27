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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.zip.ZipFile;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.parser.SDMXHDParser;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.util.IndicatorUtil;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.sdmxhdintegration.reporting.extension.SDMXHDCohortIndicatorDataSetDefinition;

/**
 * General utility methods
 */
public class Utils {
	/**
	 * Gets the cohort indicator dataset definition for a key family mapping
	 * @param keyFamilyMapping the key family mapping
	 * @return the dataset definition
	 */
	public static SDMXHDCohortIndicatorDataSetDefinition getOMRSDataSetDefinition(KeyFamilyMapping keyFamilyMapping) {
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
	
	/**
	 * Gets the cohort indicator dataset definition for a key family mapping
	 * @param message the message containing the key family mapping
	 * @param keyFamilyId the mapping id
	 * @return the dataset definition
	 */
	public static SDMXHDCohortIndicatorDataSetDefinition getOMRSDataSetDefinition(SDMXHDMessage message, String keyFamilyId) {
		SDMXHDService s = Context.getService(SDMXHDService.class);
		KeyFamilyMapping keyFamilyMapping = s.getKeyFamilyMapping(message, keyFamilyId);
		return getOMRSDataSetDefinition(keyFamilyMapping);
	}
	
	/**
     * Auto generated method comment
     * 
     * @param message
     * @return
     */
    public static SDMXHDCohortIndicatorDataSetDefinition getDataSetDefinition(SDMXHDMessage message, String keyFamilyId) {
    	SDMXHDService sdmxhdService = Context.getService(SDMXHDService.class);
    	KeyFamilyMapping keyFamilyMapping = sdmxhdService.getKeyFamilyMapping(message, keyFamilyId);
    	
    	if (keyFamilyMapping.getReportDefinitionId() == null) {
    		// create Report
    		PeriodIndicatorReportDefinition report = new PeriodIndicatorReportDefinition();
    		report.setName(message.getName() + " (" + keyFamilyId + ")");
    		report.setDescription("SDMX-HD Message Description: " + message.getDescription());
    		
    		// create OMRS DSD
    		SDMXHDCohortIndicatorDataSetDefinition omrsDSD = new SDMXHDCohortIndicatorDataSetDefinition();
    		omrsDSD.setName(message.getName() + " (SDMX-HD Module generated DSD)");
    		omrsDSD.setDescription("SDMX-HD Message Description: " + message.getDescription());
    		omrsDSD.setSDMXHDMessageId(message.getId());
    		
    		omrsDSD.addParameter(ReportingConstants.START_DATE_PARAMETER);
    		omrsDSD.addParameter(ReportingConstants.END_DATE_PARAMETER);
    		omrsDSD.addParameter(ReportingConstants.LOCATION_PARAMETER);
    		
    		// save dataset
    		DataSetDefinitionService dss = Context.getService(DataSetDefinitionService.class);
    		omrsDSD = (SDMXHDCohortIndicatorDataSetDefinition) dss.saveDefinition(omrsDSD);
    		
    		report.addDataSetDefinition(PeriodIndicatorReportDefinition.DEFAULT_DATASET_KEY, omrsDSD, IndicatorUtil.getDefaultParameterMappings());
    		// save report
    		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
    		rds.saveDefinition(report);
    		
    		// set foreign key
    		keyFamilyMapping.setReportDefinitionId(report.getId());
    		
    		sdmxhdService.saveKeyFamilyMapping(keyFamilyMapping);

    		return omrsDSD;
    	} else {
    	    return getOMRSDataSetDefinition(keyFamilyMapping);
    	}    	
    }
    
    /**
	 * Extracts the DSD from gvien message
	 * @throws IOException 
	 * @throws SchemaValidationException 
	 * @throws ExternalRefrenceNotFoundException 
	 * @throws XMLStreamException 
	 * @throws ValidationException
	 * @should should get parsed DSD for given message
	 */
	public static DSD getDataSetDefinition(SDMXHDMessage message) throws IOException, ValidationException, XMLStreamException, ExternalRefrenceNotFoundException, SchemaValidationException {
		String path = Context.getAdministrationService().getGlobalProperty("sdmxhdintegration.messageUploadDir");
		ZipFile zf = new ZipFile(path + File.separator + message.getZipFilename());
		SDMXHDParser parser = new SDMXHDParser();
		org.jembi.sdmxhd.SDMXHDMessage sdmxhdData = parser.parse(zf);
		return sdmxhdData.getDsd();
	}
	
	/**
	 * @return true if two dates match after being formatted
	 */
	public static boolean datesMatchWithFormat(Date d1, Date d2, String format) {
		if (d1 != null && d2 != null) {
			if (format != null) {
				DateFormat df = new SimpleDateFormat(format);
				return df.format(d1).equals(df.format(d2));
			}
			else {
				return d1.equals(d2);
			}
		}
		return false;
	}
}
