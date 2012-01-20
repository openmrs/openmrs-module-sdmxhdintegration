
package org.openmrs.module.sdmxhdintegration.web.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jembi.sdmxhd.dsd.Attribute;
import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.parser.SDMXHDParser;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.jembi.sdmxhd.primitives.Code;
import org.jembi.sdmxhd.primitives.CodeList;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDService;
import org.openmrs.module.sdmxhdintegration.SimpleCode;
import org.openmrs.module.sdmxhdintegration.Util;
import org.openmrs.module.sdmxhdintegration.reporting.extension.SDMXHDCohortIndicatorDataSetDefinition;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;


@Controller
public class SetAttributesDialogController {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	@RequestMapping("/module/sdmxhdintegration/setAttributesDialog")
	public void showForm(ModelMap model,
	                     @RequestParam("sdmxhdMessageId") Integer sdmxhdMessageId,
	                     @RequestParam("attachmentLevel") String attachmentLevel,
	                     @RequestParam("keyfamilyid") String keyFamilyId,
	                     @RequestParam(value="columnName", required=false) String columnName) throws IOException, XMLStreamException, ExternalRefrenceNotFoundException, ValidationException, SchemaValidationException {
		attachmentLevel = URLDecoder.decode(attachmentLevel);
		if (columnName != null) {
			columnName = URLDecoder.decode(columnName);
		}
		
		SDMXHDService sdmxhdService = Context.getService(SDMXHDService.class);
    	SDMXHDMessage sdmxhdMessage = sdmxhdService.getMessage(sdmxhdMessageId);
    	
    	// get OMRS DSD
		DataSetDefinitionService dss = Context.getService(DataSetDefinitionService.class);
		SDMXHDCohortIndicatorDataSetDefinition omrsDSD = Util.getOMRSDataSetDefinition(sdmxhdMessage, keyFamilyId);
    	
		String path = Context.getAdministrationService().getGlobalProperty("sdmxhdintegration.messageUploadDir");
    	ZipFile zf = new ZipFile(path + File.separator + sdmxhdMessage.getSdmxhdZipFileName());
    	SDMXHDParser parser = new SDMXHDParser();
    	org.jembi.sdmxhd.SDMXHDMessage sdmxhdData = parser.parse(zf);
    	DSD sdmxhdDSD = sdmxhdData.getDsd();
    	
    	Map<String, List<SimpleCode>> codelistValues = new HashMap<String, List<SimpleCode>>();
    	
    	// get mandatory attributes
    	List<Attribute> mandAttributes = sdmxhdDSD.getAttributes(attachmentLevel, Attribute.MANDATORY);
    	
    	// set the mandatory attributes datatypes
    	Map<String, String> mandAttrDataTypes = new HashMap<String, String>();
    	
    	for (Attribute a : mandAttributes) {
    		if (a.getCodelist() != null) {
    			// Get codelist from DSD
    			CodeList codeList = sdmxhdDSD.getCodeList(a.getCodelist());
    			
    			// Extract code values
    			List<SimpleCode> codeValues = new ArrayList<SimpleCode>();
    			for (Code c : codeList.getCodes()) {
    				codeValues.add(new SimpleCode(c.getValue(), c.getDescription().getDefaultStr()));
    			}
    			
    			// Add to master map
    			codelistValues.put(a.getConceptRef(), codeValues);
    			
    			mandAttrDataTypes.put(a.getConceptRef(), "Coded");
    		} else if (a.getTextFormat().getTextType().equalsIgnoreCase("String")) {
    			mandAttrDataTypes.put(a.getConceptRef(), "String");
    		} else if (a.getTextFormat().getTextType().equalsIgnoreCase("Date")) {
    			mandAttrDataTypes.put(a.getConceptRef(), "Date");
    		}
    	}
    	
    	// get conditional attributes
    	List<Attribute> condAttributes = sdmxhdDSD.getAttributes(attachmentLevel, Attribute.CONDITIONAL);
    	
    	// set the conditional attributes datatypes
    	Map<String, String> condAttrDataTypes = new HashMap<String, String>();
    	for (Attribute a : condAttributes) {
    		if (a.getCodelist() != null) {
    			// Get codelist from DSD
    			CodeList codeList = sdmxhdDSD.getCodeList(a.getCodelist());
    			
    			// Extract code values
    			List<SimpleCode> codeValues = new ArrayList<SimpleCode>();
    			for (Code c : codeList.getCodes()) {
    				codeValues.add(new SimpleCode(c.getValue(), c.getDescription().getDefaultStr()));
    			}
    			
    			// Add to master map
    			codelistValues.put(a.getConceptRef(), codeValues);
    			
    			condAttrDataTypes.put(a.getConceptRef(), "Coded");
    		} else if (a.getTextFormat().getTextType().equalsIgnoreCase("String")) {
    			condAttrDataTypes.put(a.getConceptRef(), "String");
    		} else if (a.getTextFormat().getTextType().equalsIgnoreCase("Date")) {
    			condAttrDataTypes.put(a.getConceptRef(), "Date");
    		}
    	}
    	
    	// get attribute values for the attachment level
    	Map<String, String> attributeValues = null;
    	if (attachmentLevel.equals("DataSet")) {
    		attributeValues = omrsDSD.getDataSetAttachedAttributes();
    	} else if (attachmentLevel.equals("Series")) {
    		attributeValues = omrsDSD.getSeriesAttachedAttributes().get(columnName);
    	} else if (attachmentLevel.equals("Observation")) {
    		attributeValues = omrsDSD.getObsAttachedAttributes().get(columnName);
     	}
    	
    	model.addAttribute("mandAttributes", mandAttributes);
    	model.addAttribute("condAttributes", condAttributes);
    	
    	model.addAttribute("mandAttrDataTypes", mandAttrDataTypes);
    	model.addAttribute("condAttrDataTypes", condAttrDataTypes);
    	
    	model.addAttribute("attributeValues", attributeValues);
    	model.addAttribute("codelistValues", codelistValues);
    	
    	model.addAttribute("sdmxhdmessageid", sdmxhdMessageId);
    	model.addAttribute("attachmentLevel", attachmentLevel);
	}
	
	public static final String ATTRIBUTE = "attribute.";
	
	@RequestMapping(value="/module/sdmxhdintegration/setAttributesDialog", method=RequestMethod.POST)
	public String handleSubmission(WebRequest request,
	                               @RequestParam("sdmxhdMessageId") Integer sdmxhdMessageId,
	                               @RequestParam("attachmentLevel") String attachmentLevel,
	                               @RequestParam("keyfamilyid") String keyFamilyId,
	                               @RequestParam(value="columnName", required=false) String columnName) {
		attachmentLevel = URLDecoder.decode(attachmentLevel);
		if (columnName != null) {
			columnName = URLDecoder.decode(columnName);
		}
		
		// get SDMXHDMessage Object in OMRS
		SDMXHDMessage sdmxhdMessage = Context.getService(SDMXHDService.class).getMessage(sdmxhdMessageId);
		
		// get OMRS DSD
		DataSetDefinitionService dss = Context.getService(DataSetDefinitionService.class);
		SDMXHDCohortIndicatorDataSetDefinition omrsDSD = Util.getOMRSDataSetDefinition(sdmxhdMessage, keyFamilyId);
		
		Map<String, String[]> paramMap = request.getParameterMap();
		for (String key : paramMap.keySet()) {
			if (key.startsWith(ATTRIBUTE)) {
				String attributeValue = paramMap.get(key)[0];
				if (attributeValue != null && attributeValue != "") {
					String attribute = key.replaceFirst(ATTRIBUTE, "");
					
					if (attachmentLevel.equals("DataSet")) {
						omrsDSD.addDataSetAttribute(attribute, attributeValue);
					} else if (attachmentLevel.equals("Series")) {
						omrsDSD.addSeriesAttributesToColumn(columnName, attribute, attributeValue);
					} else if (attachmentLevel.equals("Observation")) {
						omrsDSD.addObsAttributesToCoulmn(columnName, attribute, attributeValue);
					} else {
						// error
						log.error("Attachment level is not one of 'DataSet', 'Series' or 'Observation'");
					}
				}
			}
		}
		
		dss.saveDefinition(omrsDSD);
		
		return "redirect:redirectParent.form?url=setAttributes.form?sdmxhdMessageId=" + sdmxhdMessageId + "%26keyfamilyid=" + keyFamilyId;
	}

}
