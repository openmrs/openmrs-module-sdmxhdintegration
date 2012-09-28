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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.dsd.Dimension;
import org.jembi.sdmxhd.parser.SDMXHDParser;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.jembi.sdmxhd.primitives.Code;
import org.jembi.sdmxhd.primitives.CodeList;
import org.jembi.sdmxhd.util.Constants;
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
	
	/**
	 * @return a display name for an indicator and dimension string assuming the indicator is listed first
	 */
    public static String getDisplayNameForIndicatorAndDimensions(DSD dsd, String combinationString) {
    	StringBuilder sb = new StringBuilder();
    	int numFound = 0;
    	for (String s : combinationString.split("\\,")) {
    		String[] split = s.split("\\=");
    		Dimension d = dsd.getDimension(split[0]);
    		CodeList cl = dsd.getCodeList(d.getCodelistRef());
    		boolean found = false;
    		for (Code c : cl.getCodes()) {
    			if (c.getValue().equals(split[1])) {
    				if (numFound > 0) {
    					sb.append(" ");
    				}
    				if (numFound == 1) {
    					sb.append("(");
    				}
    	    		sb.append(c.getDescription().getDefaultStr());
    	    		found = true;
    	    		numFound++;
    			}
    		}
    		if (!found) {
    			throw new IllegalArgumentException("Cannot find " + s + " in dsd");
    		}
    	}
    	if (numFound > 1) {
    		sb.append(")");
    	}
    	return sb.toString();
    }
    
    /**
     * Outputs the CSDS xml as a new file in the root a copy of the original zipfile
     */
    public static void outputCsdsInDsdZip(ZipFile zf, String csdsXml, OutputStream out) throws IOException {
    	
    	File tempFile = File.createTempFile("tmp", ".zip");
        tempFile.delete();
        
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(tempFile));

        Enumeration<? extends ZipEntry> entries = zf.entries();
        while (entries.hasMoreElements()) {
        	
        	ZipEntry readZipEntry = entries.nextElement();
        	
        	// leave out data result files
        	if (!readZipEntry.getName().equals(Constants.CDS_PATH) && !readZipEntry.getName().equals(Constants.CSDS_PATH)) {
	        	ZipEntry newZipEntry = new ZipEntry(readZipEntry.getName());
	        	zos.putNextEntry(newZipEntry);
	        	InputStream is = zf.getInputStream(readZipEntry);
	        	byte[] buffer = new byte[1024];
	        	int len;
	        	while ((len = is.read(buffer)) > 0){
	        		zos.write(buffer, 0, len);
	  	        }
        	}
        }
        
        // insert CSDS into temp file
        ZipEntry e = new ZipEntry(Constants.CSDS_PATH);
        zos.putNextEntry(e);
        zos.write(csdsXml.getBytes());
        zos.closeEntry();
        zos.close();
        
        // write temp sdmxhdMessageFile to out
        FileInputStream inStream = new FileInputStream(tempFile);
        
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) > 0){
        	out.write(buffer, 0, len);
        }
        
        out.flush();
        out.close();
    }
}
