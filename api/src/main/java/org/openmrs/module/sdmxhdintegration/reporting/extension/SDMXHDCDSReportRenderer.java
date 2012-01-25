
package org.openmrs.module.sdmxhdintegration.reporting.extension;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jembi.sdmxhd.cds.CDS;
import org.jembi.sdmxhd.cds.DataSet;
import org.jembi.sdmxhd.cds.Obs;
import org.jembi.sdmxhd.cds.Series;
import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.dsd.Dimension;
import org.jembi.sdmxhd.dsd.KeyFamily;
import org.jembi.sdmxhd.header.Header;
import org.jembi.sdmxhd.header.Sender;
import org.jembi.sdmxhd.parser.SDMXHDParser;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.jembi.sdmxhd.primitives.Code;
import org.jembi.sdmxhd.primitives.CodeList;
import org.jembi.sdmxhd.util.Constants;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition.CohortIndicatorAndDimensionColumn;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.AbstractReportRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.sdmxhdintegration.KeyFamilyMapping;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDService;


/**
 * Disabled for now
 */
//@Handler
//@Localized("SDMX-HD CDS")
public class SDMXHDCDSReportRenderer extends AbstractReportRenderer {
	
	private Log log = LogFactory.getLog(this.getClass());

	/**
     * @see org.openmrs.module.report.renderer.ReportRenderer#getFilename(org.openmrs.module.report.ReportDefinition, java.lang.String)
     */
    @Override
    public String getFilename(ReportDefinition definition, String argument) {
	    return definition.getName() + ".zip";
    }

	/**
     * @see org.openmrs.module.report.renderer.ReportRenderer#getRenderedContentType(org.openmrs.module.report.ReportDefinition, java.lang.String)
     */
    @Override
    public String getRenderedContentType(ReportDefinition definition, String argument) {
	    return "application/zip";
    }

	/**
     * @see org.openmrs.module.report.renderer.ReportRenderer#getRenderingModes(org.openmrs.module.report.ReportDefinition)
     */
    @Override
    public Collection<RenderingMode> getRenderingModes(ReportDefinition definition) {
		if (definition.getDataSetDefinitions() == null || definition.getDataSetDefinitions().size() != 1) {
			return null;
		}
		
		DataSetDefinition omrsDSD = definition.getDataSetDefinitions().get(definition.getDataSetDefinitions().keySet().iterator().next()).getParameterizable();
		
		// check that a corresponding SDMX-HD Message exists
		SDMXHDService sdmxhdService = Context.getService(SDMXHDService.class);
		List<KeyFamilyMapping> allKeyFamilyMappings = sdmxhdService.getAllKeyFamilyMappings();
		
		boolean mappingExists = false;
		for (KeyFamilyMapping kfm : allKeyFamilyMappings) {
			if (kfm.getReportDefinitionId() != null && kfm.getReportDefinitionId().equals(definition.getId())) {
				mappingExists = true;
				break;
			}
		}
		
		if (mappingExists) {
			return Collections.singleton(new RenderingMode(this, this.getLabel(), null, Integer.MIN_VALUE));
		} else {
			return null;
		}
    }

	/**
     * @see org.openmrs.module.report.renderer.ReportRenderer#render(org.openmrs.module.report.ReportData, java.lang.String, java.io.OutputStream)
     */
    @Override
    public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {
    	if (reportData.getDataSets().size() > 1) {
    		throw new RenderingException("This report contains multiple DataSets, this renderer does not support multiple DataSets");
    	} else if (reportData.getDataSets().size() < 1) {
    		throw new RenderingException("No DataSet defined in this report");
    	}
    	
    	// get results dataSet
    	org.openmrs.module.reporting.dataset.DataSet dataSet = reportData.getDataSets().get(reportData.getDataSets().keySet().iterator().next());
    	
    	// get OMRS DSD
    	Mapped<? extends DataSetDefinition> mappedOMRSDSD = reportData.getDefinition().getDataSetDefinitions().get(reportData.getDefinition().getDataSetDefinitions().keySet().iterator().next());
    	SDMXHDCohortIndicatorDataSetDefinition omrsDSD = (SDMXHDCohortIndicatorDataSetDefinition) mappedOMRSDSD.getParameterizable();
    	
    	// get SDMX-HD DSD
    	SDMXHDService sdmxhdService = (SDMXHDService) Context.getService(SDMXHDService.class);
    	SDMXHDMessage sdmxhdMessage = sdmxhdService.getMessage(omrsDSD.getSDMXHDMessageId());
    	
    	try {
    		String path = Context.getAdministrationService().getGlobalProperty("sdmxhdintegration.messageUploadDir");
	        ZipFile zf = new ZipFile(path + File.separator + sdmxhdMessage.getZipFilename());
	        SDMXHDParser parser = new SDMXHDParser();
	        org.jembi.sdmxhd.SDMXHDMessage sdmxhdData = parser.parse(zf);
	        DSD sdmxhdDSD = sdmxhdData.getDsd();
	        
	        // get dataset id
	        AdministrationService as = Context.getAdministrationService();
	        String datasetID = as.getGlobalProperty("sdmxhdintegration.datasetID");
	        
	        //Construct CDS object
	        Sender p = new Sender();
	        p.setId("OMRS");
	        p.setName("OpenMRS");
	        
	        Header h = new Header();
	        h.setId("SDMX-HD-CDS");
	        h.setTest(false);
	        h.setTruncated(false);
	        h.getName().addValue("en", "OpenMRS SDMX-HD Export");
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        h.setPrepared(sdf.format(new Date()));
	        h.setDataSetID(datasetID);
	        h.getSenders().add(p);
	        
	        // construct dataset
	        DataSet sdmxhdDataSet = new DataSet();
	        sdmxhdDataSet.setDatasetID(datasetID);
	        
	        // set data provider details from global properties
	        sdmxhdDataSet.setDataProviderID(as.getGlobalProperty("sdmxhdintegration.dataProviderID"));
	        sdmxhdDataSet.setDataProviderSchemeId(as.getGlobalProperty("sdmxhdintegration.dataProviderSchemeId"));
	        sdmxhdDataSet.setDataProviderSchemeAgencyId(as.getGlobalProperty("sdmxhdintegration.dataProviderSchemeAgencyId"));
	        
	        // set DataSet Attributes
	        Map<String, String> dataSetAttachedAttributes = omrsDSD.getDataSetAttachedAttributes();
	        for (String key : dataSetAttachedAttributes.keySet()) {
	        	sdmxhdDataSet.getAttributes().put(key, dataSetAttachedAttributes.get(key));
	        }
	        
	        //for each row of dataset
	        for (DataSetRow row : dataSet) {
	        	//for each column of dataset
	        	for (DataSetColumn column : row.getColumnValues().keySet()) {
	        		CohortIndicatorAndDimensionColumn cidColumn = (CohortIndicatorAndDimensionColumn) column;
	        		Object value = row.getColumnValues().get(column);
	        		String columnKey = column.getName();
	        		
	        		//get the indicator code for this column
	        		CohortIndicator indicator = cidColumn.getIndicator().getParameterizable();
	        		String sdmxhdIndicatorName = omrsDSD.getSDMXHDMappedIndicator(indicator.getId());
	        		CodeList indCodeList = sdmxhdDSD.getCodeList("CL_INDICATOR");
	        		Code indCode = indCodeList.getCodeByDescription(sdmxhdIndicatorName);
	        		
	        		//get the dimension for the list of indicators (CL_INDICATOR)
	        		Dimension indDimension = sdmxhdDSD.getDimension(indCodeList);
	        		
	        		Series s = new Series();
	        		
	        		//set the indicator attribute
	        		s.getAttributes().put(indDimension.getCodelistRef(), indCode.getValue());
	        		
	        		// set Series Attributes
	        		Map<String, String> seriesAttachedAttributes = omrsDSD.getSeriesAttachedAttributes().get(columnKey);
	        		if (seriesAttachedAttributes != null) {
		    	        for (String key : seriesAttachedAttributes.keySet()) {
		    	        	s.getAttributes().put(key, seriesAttachedAttributes.get(key));
		    	        }
	        		}
	        		
	    	        // write dimensions to series
	        		Map<String, String> dimensionOptions = cidColumn.getDimensionOptions();
	        		// for each dimension option for this column
	        		for (String omrsDimensionId : dimensionOptions.keySet()) {
	        			Integer omrsDimensionIdInt = Integer.parseInt(omrsDimensionId);
	        			// find sdmx-hd dimension name in mapping
	        			String sdmxhdDimensionName = null;
	        			Map<String, Integer> omrsMappedDimensions = omrsDSD.getOMRSMappedDimensions();
	        			for (String sdmxhdDimensionNameTemp : omrsMappedDimensions.keySet()) {
	        				if (omrsMappedDimensions.get(sdmxhdDimensionNameTemp).equals(omrsDimensionIdInt)) {
	        					sdmxhdDimensionName = sdmxhdDimensionNameTemp;
	        					break;
	        				}
	        			}
	        			// find sdmx-hd dimension option name in mapping
	        			String omrsDimensionOptionName = dimensionOptions.get(omrsDimensionId);
	        			String sdmxhdDimensionOptionName = null;
	        			Map<String, String> omrsMappedDimensionOptions = omrsDSD.getOMRSMappedDimensionOptions().get(sdmxhdDimensionName);
	        			for (String sdmxhdDimensionOptionNameTemp : omrsMappedDimensionOptions.keySet()) {
	        				if (omrsMappedDimensionOptions.get(sdmxhdDimensionOptionNameTemp).equals(omrsDimensionOptionName)) {
	        					sdmxhdDimensionOptionName = sdmxhdDimensionOptionNameTemp;
	        					break;
	        				}
	        			}
	        			//find code corresponding to this dimension option
	        			Dimension sdmxhdDimension = sdmxhdDSD.getDimension(sdmxhdDimensionName);
	        			CodeList codeList = sdmxhdDSD.getCodeList(sdmxhdDimension.getCodelistRef());
	        			Code code = codeList.getCodeByDescription(sdmxhdDimensionOptionName);
	        			s.addAttribute(sdmxhdDimensionName, code.getValue());
	        		}
	        		
	        		//construct new (SDMX-HD) obs to contain the indicator value
	        		Obs o = new Obs();
	        		
	        		// set Obs Attributes
	        		Map<String, String> obsAttachedAttributes = omrsDSD.getObsAttachedAttributes().get(columnKey);
	        		if (obsAttachedAttributes != null) {
		    	        for (String key : obsAttachedAttributes.keySet()) {
		    	        	o.getAttributes().put(key, obsAttachedAttributes.get(key));
		    	        }
	        		}
	        		
	        		String primaryMeasure = sdmxhdDSD.getKeyFamilies().get(0).getComponents().getPrimaryMeasure().getConceptRef();
	        		
	        		// write value
	        		if (value instanceof CohortIndicatorAndDimensionResult) {
	        			CohortIndicatorAndDimensionResult typedValue = (CohortIndicatorAndDimensionResult) value;
	        			o.getAttributes().put(primaryMeasure, typedValue.getValue().toString());
	        		} else {
	        			o.getAttributes().put(primaryMeasure, value.toString());
	        		}
	        		
	        		s.getObs().add(o);
	        		
	        		//add series to SDMX-HD dataset
	        		sdmxhdDataSet.getSeries().add(s);
	        	}
	        }
	        
	        CDS cds = new CDS();
	        cds.getDatasets().add(sdmxhdDataSet);
	        cds.setHeader(h);
	        
	        // build up namespace
	        KeyFamily keyFamily = sdmxhdDSD.getKeyFamilies().get(0);
	        String derivedNamespace = Constants.DERIVED_NAMESPACE_PREFIX + keyFamily.getAgencyID() + ":" + keyFamily.getId() + ":" + keyFamily.getVersion() + ":compact";
	        String xml = cds.toXML(derivedNamespace);
	        
	        // create temp zip file space
	        File tempFile = File.createTempFile("tmp", ".zip");
	        tempFile.delete();
	        
	        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(tempFile));
	        
	        // copy all zip entries to new zip file
	        zf = new ZipFile(path + File.separator + sdmxhdMessage.getZipFilename());
	        Enumeration<? extends ZipEntry> entries = zf.entries();
	        while(entries.hasMoreElements()) {
	        	ZipEntry readZipEntry = entries.nextElement();
	        	
	        	ZipEntry newZipEntry = new ZipEntry(readZipEntry.getName());
	        	zos.putNextEntry(newZipEntry);
	        	InputStream is = zf.getInputStream(readZipEntry);
	        	
	        	byte[] buffer = new byte[1024];
	        	int len;
	        	while ((len = is.read(buffer)) > 0){
	        		zos.write(buffer, 0, len);
	  	        }
	        }
	        
	        // insert CDS into temp file
	        ZipEntry e = new ZipEntry("CDS.xml");
	        zos.putNextEntry(e);
	        zos.write(xml.getBytes());
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
        catch (IllegalArgumentException e) {
	        log.error("Error generated", e);
	        throw new RenderingException("Error rendering the SDMX-HD message: " + e.getMessage(), e);
        }
        catch (XMLStreamException e) {
	        log.error("Error generated", e);
	        throw new RenderingException("Error rendering the SDMX-HD message: " + e.getMessage(), e);
        }
        catch (ExternalRefrenceNotFoundException e) {
	        log.error("Error generated", e);
	        throw new RenderingException("Error rendering the SDMX-HD message: " + e.getMessage(), e);
        }
        catch (IllegalAccessException e) {
	        log.error("Error generated", e);
	        throw new RenderingException("Error rendering the SDMX-HD message: " + e.getMessage(), e);
        }
        catch (ValidationException e) {
	        log.error("Error generated", e);
	        throw new RenderingException("Error rendering the SDMX-HD message: " + e.getMessage(), e);
        }
        catch (SchemaValidationException e) {
	        log.error("Error generated", e);
	        throw new RenderingException("Error rendering the SDMX-HD message: " + e.getMessage(), e);
        }
    }

}
