
package org.openmrs.module.sdmxhdintegration.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.jembi.sdmxhd.primitives.CodeList;
import org.openmrs.api.context.Context;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDService;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("sdmxhdMessage")
@RequestMapping("/module/sdmxhdintegration/globalMessageConfig")
public class GlobalMessageConfigController {
	
	@RequestMapping(method=RequestMethod.GET)
	public void showPage(ModelMap model, @RequestParam("sdmxhdMessageId") Integer sdmxhdMessageId) throws ValidationException, IOException, XMLStreamException, ExternalRefrenceNotFoundException, SchemaValidationException {
		SDMXHDService sdmxhdService = Context.getService(SDMXHDService.class);
		SDMXHDMessage sdmxhdMessage = sdmxhdService.getMessage(sdmxhdMessageId);
		
		// skip lazy initilization
		sdmxhdMessage.getGroupElementAttributes();
		
		DSD sdmxhdDSD = sdmxhdService.getSDMXHDDataSetDefinition(sdmxhdMessage);
		CodeList freqCL = sdmxhdDSD.getCodeList("CL_FREQ");
		if (freqCL != null) {
			model.addAttribute("CL_FREQ", freqCL.getCodes());
		}
		
		model.addAttribute("sdmxhdMessage", sdmxhdMessage);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String handleSubmission(HttpServletRequest request,
	                               @ModelAttribute("sdmxhdMessage") SDMXHDMessage sdmxhdMessage,
	                               BindingResult result) {
		
		SDMXHDService sdmxhdService = Context.getService(SDMXHDService.class);
		sdmxhdService.saveMessage(sdmxhdMessage);
		
		HttpSession session = request.getSession();
		session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Global message configuration saved");
		return "redirect:messages.list";
	}
	
}
