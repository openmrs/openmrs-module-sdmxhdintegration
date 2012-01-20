
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
public class MessagesController {
	
	@RequestMapping("/module/sdmxhdintegration/messages")
    public void showList(@RequestParam(value="deletemsgid", required=false) Integer deleteMsgId, ModelMap model) throws ValidationException, IOException, XMLStreamException, ExternalRefrenceNotFoundException, SchemaValidationException {
		SDMXHDService sdmxhdService = (SDMXHDService) Context.getService(SDMXHDService.class);
		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
		
		if (deleteMsgId != null) {
			SDMXHDMessage sdmxhdMessage = sdmxhdService.getMessage(deleteMsgId);
			sdmxhdMessage.setRetired(true);
			sdmxhdMessage.setDateRetired(new Date());
			sdmxhdMessage.setRetiredBy(Context.getAuthenticatedUser());
			sdmxhdMessage.setRetireReason("User Deleted");
			sdmxhdService.saveMessage(sdmxhdMessage);
		}
		
		List<SDMXHDMessage> messages = sdmxhdService.getAllMessages(false);
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
		for (SDMXHDMessage message : messages) {
	        DSD dsd = sdmxhdService.getSDMXHDDataSetDefinition(message);
			for (Iterator<KeyFamilyMapping> iterator2 = allKeyFamilyMappings.iterator(); iterator2.hasNext();) {
		        KeyFamilyMapping keyFamilyMapping = iterator2.next();
		        if (keyFamilyMapping.getSdmxhdMessage().getId().equals(message.getId())) {
			        KeyFamily keyFamily = dsd.getKeyFamily(keyFamilyMapping.getKeyFamilyId());
			        keyFamilyNamesMap.put(keyFamily.getId(), keyFamily.getName().getDefaultStr());
		        }
	        }
        }
				
		model.addAttribute("messages", messages);
		model.addAttribute("keyFamilyMappings", allKeyFamilyMappings);
		model.addAttribute("reportUuidMapping", reportUuidMapping);
		model.addAttribute("keyFamilyNamesMap", keyFamilyNamesMap);
    }	
}
