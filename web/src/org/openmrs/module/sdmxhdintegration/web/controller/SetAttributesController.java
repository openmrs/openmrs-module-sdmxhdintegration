
package org.openmrs.module.sdmxhdintegration.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.jembi.sdmxhd.dsd.Attribute;
import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.parser.SDMXHDParser;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition.CohortIndicatorAndDimensionColumn;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.sdmxhdintegration.KeyFamilyMapping;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDService;
import org.openmrs.module.sdmxhdintegration.Util;
import org.openmrs.module.sdmxhdintegration.reporting.extension.SDMXHDCohortIndicatorDataSetDefinition;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SetAttributesController {
	
	@RequestMapping("/module/sdmxhdintegration/setAttributes")
	public void showPage(ModelMap model, @RequestParam("sdmxhdMessageId") Integer sdmxhdMessageId,
	                     				 @RequestParam("keyfamilyid") String keyFamilyId)
	                                                                                              throws IOException,
	                                                                                              ValidationException,
	                                                                                              XMLStreamException,
	                                                                                              ExternalRefrenceNotFoundException,
	                                                                                              SchemaValidationException {
		SDMXHDService sdmxhdService = Context.getService(SDMXHDService.class);
		SDMXHDMessage sdmxhdMessage = sdmxhdService.getSDMXHDMessage(sdmxhdMessageId);
		KeyFamilyMapping keyFamilyMapping = sdmxhdService.getKeyFamilyMapping(sdmxhdMessage, keyFamilyId);
		if (keyFamilyMapping.getReportDefinitionId() != null) {
			DataSetDefinitionService dss = Context.getService(DataSetDefinitionService.class);
			SDMXHDCohortIndicatorDataSetDefinition omrsDSD = Util.getOMRSDataSetDefinition(sdmxhdMessage, keyFamilyId);
			
			model.addAttribute("columns", omrsDSD.getColumns());
			model.addAttribute("sdmxhdMessageId", sdmxhdMessageId);
			model.addAttribute("keyfamilyid", keyFamilyId);
			
			String path = Context.getAdministrationService().getGlobalProperty("sdmxhdintegration.messageUploadDir");
			ZipFile zf = new ZipFile(path + File.separator + sdmxhdMessage.getSdmxhdZipFileName());
			SDMXHDParser parser = new SDMXHDParser();
			org.jembi.sdmxhd.SDMXHDMessage sdmxhdData = parser.parse(zf);
			DSD sdmxhdDSD = sdmxhdData.getDsd();
			
			// test if mandatory attributes are set
			// DataSet Attachment
			List<Attribute> mandDataSetAttr = sdmxhdDSD.getAttributes(Attribute.DATASET_ATTACHMENT_LEVEL,
			    Attribute.MANDATORY);
			Map<String, String> dataSetAttachedAttributes = omrsDSD.getDataSetAttachedAttributes();
			
			model.addAttribute("datasetMandAttrSet", isMandatoryAttributesSet(mandDataSetAttr, dataSetAttachedAttributes));
			
			// Series Attachment
			Map<String, Boolean> seriesMandAttrSet = new HashMap<String, Boolean>();
			for (CohortIndicatorAndDimensionColumn c : omrsDSD.getColumns()) {
				List<Attribute> mandSeriesAttr = sdmxhdDSD.getAttributes(Attribute.SERIES_ATTACHMENT_LEVEL,
				    Attribute.MANDATORY);
				Map<String, String> seriesAttachedAttributes = omrsDSD.getSeriesAttachedAttributes().get(c.getName());
				
				seriesMandAttrSet.put(c.getName(), isMandatoryAttributesSet(mandSeriesAttr, seriesAttachedAttributes));
				
			}
			model.addAttribute("seriesMandAttrSet", seriesMandAttrSet);
			
			// Obs Attachment
			Map<String, Boolean> obsMandAttrSet = new HashMap<String, Boolean>();
			for (CohortIndicatorAndDimensionColumn c : omrsDSD.getColumns()) {
				List<Attribute> mandObsAttr = sdmxhdDSD.getAttributes(Attribute.OBSERVATION_ATTACHMENT_LEVEL,
				    Attribute.MANDATORY);
				Map<String, String> obsAttachedAttributes = omrsDSD.getObsAttachedAttributes().get(c.getName());
				
				obsMandAttrSet.put(c.getName(), isMandatoryAttributesSet(mandObsAttr, obsAttachedAttributes));
				
			}
			model.addAttribute("obsMandAttrSet", obsMandAttrSet);
		}
	}
	
	private boolean isMandatoryAttributesSet(List<Attribute> mandAttributes, Map<String, String> attributes) {
		if (mandAttributes == null || mandAttributes.size() < 1) {
			return true;
		}
		if (attributes == null) {
			return false;
		}
		
		boolean mandandatoryAttributesSet = true;
		for (Attribute a : mandAttributes) {
			if (attributes.get(a.getConceptRef()) == null || attributes.get(a.getConceptRef()).equals("")) {
				mandandatoryAttributesSet = false;
			}
		}
		
		return mandandatoryAttributesSet;
	}
	
}
