
package org.openmrs.module.sdmxhdintegration.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.dsd.KeyFamily;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.sdmxhdintegration.KeyFamilyMapping;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewSDMXMessageFormController {
	
	@RequestMapping("/module/sdmxhdintegration/viewSDMXHDMessages")
    public void showList(@RequestParam(value="deletemsgid", required=false) Integer deleteMsgId, ModelMap model) throws ValidationException, IOException, XMLStreamException, ExternalRefrenceNotFoundException, SchemaValidationException {
		SDMXHDService sdmxhdService = (SDMXHDService) Context.getService(SDMXHDService.class);
		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
		
		if (deleteMsgId != null) {
			SDMXHDMessage sdmxhdMessage = sdmxhdService.getSDMXHDMessage(deleteMsgId);
			sdmxhdMessage.setRetired(true);
			sdmxhdMessage.setDateRetired(new Date());
			sdmxhdMessage.setRetiredBy(Context.getAuthenticatedUser());
			sdmxhdMessage.setRetireReason("User Deleted");
			sdmxhdService.saveSDMXHDMessage(sdmxhdMessage);
			
		}
		
		List<SDMXHDMessage> allSDMXHDMessages = sdmxhdService.getAllSDMXHDMessages(false);
		List<KeyFamilyMapping> allKeyFamilyMappings = sdmxhdService.getAllKeyFamilyMappings();

		// get report uuid's
		Map<String, String> reportUuidMapping = new HashMap<String, String>();
		for (Iterator iterator = allKeyFamilyMappings.iterator(); iterator.hasNext();) {
	        KeyFamilyMapping keyFamilyMapping = (KeyFamilyMapping) iterator.next();
	        Integer reportDefinitionId = keyFamilyMapping.getReportDefinitionId();
	        if (reportDefinitionId != null) {
		        ReportDefinition reportDefinition = rds.getDefinition(reportDefinitionId);
		        reportUuidMapping.put(keyFamilyMapping.getKeyFamilyId(), reportDefinition.getUuid());
	        }
        }
		
		// get keyFamilyNames
		Map<String, String> keyFamilyNamesMap = new HashMap<String, String>();
		for (Iterator<SDMXHDMessage> iterator = allSDMXHDMessages.iterator(); iterator.hasNext();) {
	        SDMXHDMessage sdmxhdMessage = iterator.next();
	        DSD dsd = sdmxhdService.getSDMXHDDataSetDefinition(sdmxhdMessage);
			for (Iterator<KeyFamilyMapping> iterator2 = allKeyFamilyMappings.iterator(); iterator2.hasNext();) {
		        KeyFamilyMapping keyFamilyMapping = iterator2.next();
		        if (keyFamilyMapping.getSdmxhdMessage().getId().equals(sdmxhdMessage.getId())) {
			        KeyFamily keyFamily = dsd.getKeyFamily(keyFamilyMapping.getKeyFamilyId());
			        keyFamilyNamesMap.put(keyFamily.getId(), keyFamily.getName().getDefaultStr());
		        }
	        }
        }
		
		
		model.addAttribute("sdmxhdMessages", allSDMXHDMessages);
		model.addAttribute("keyFamilyMappings", allKeyFamilyMappings);
		model.addAttribute("reportUuidMapping", reportUuidMapping);
		model.addAttribute("keyFamilyNamesMap", keyFamilyNamesMap);
    }
	
}
