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

package org.openmrs.module.sdmxhdintegration.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipFile;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.parser.SDMXHDParser;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sdmxhdintegration.KeyFamilyMapping;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDService;
import org.openmrs.module.sdmxhdintegration.db.SDMXHDMessageDAO;

/**
 * Implementation of the module service
 */
public class SDMXHDMessageServiceImpl extends BaseOpenmrsService implements SDMXHDService {
    
	private SDMXHDMessageDAO dao;
	
	/**
	 * Sets the DAO used by this service
	 * @param dao the DAO
	 */
	public void setDao(SDMXHDMessageDAO dao) {
	    this.dao = dao;
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getMessage(Integer)
	 */
	@Override
	public SDMXHDMessage getMessage(Integer id) {
		return dao.getMessage(id);
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getAllMessages(Boolean)
	 */
	@Override
	public List<SDMXHDMessage> getAllMessages(Boolean includeRetired) {
		return dao.getAllMessages(includeRetired);
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#deleteMessage(SDMXHDMessage)
	 */
	@Override
	public void deleteMessage(SDMXHDMessage message) {
		dao.deleteMessage(message);
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#saveMessage(SDMXHDMessage)
	 */
	@Override
	public void saveMessage(SDMXHDMessage message) {
		if (message.getCreator() == null) {
			message.setCreator(Context.getAuthenticatedUser());
		}
		if (message.getDateCreated() == null) {
			message.setDateCreated(new Date());
		}
		if (message.getId() != null) {
		    if (message.getChangedBy() == null) {
		    	message.setChangedBy(Context.getAuthenticatedUser());
		    }
		    if (message.getDateChanged() == null) {
		    	message.setDateChanged(new Date());
		    }
		}
		if (message.getRetired() == null) {
			message.setRetired(false);
		}
		
		dao.saveMessage(message);
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getKeyFamilyMapping(java.lang.Integer)
	 */
	@Override
	public KeyFamilyMapping getKeyFamilyMapping(Integer id) {
	    return dao.getKeyFamilyMapping(id);
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getKeyFamilyMapping(org.openmrs.module.sdmxhdintegration.SDMXHDMessage, java.lang.String)
	 */
	@Override
	public KeyFamilyMapping getKeyFamilyMapping(SDMXHDMessage message, String keyFamilyId) {
	    return dao.getKeyFamilyMapping(message, keyFamilyId);
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getKeyFamilyMappingByReportDefinitionId(java.lang.Integer)
	 */
	@Override
	public KeyFamilyMapping getKeyFamilyMappingByReportDefinitionId(Integer reportDefinitionId) {
		return dao.getKeyFamilyMappingByReportDefinitionId(reportDefinitionId);
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getKeyFamilyMappingsFromMessage(org.openmrs.module.sdmxhdintegration.SDMXHDMessage)
	 */
	@Override
	public List<KeyFamilyMapping> getKeyFamilyMappingsFromMessage(SDMXHDMessage message) {
	    return dao.getKeyFamilyMappingsFromMessage(message);
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getAllKeyFamilyMappings()
	 */
	@Override
	public List<KeyFamilyMapping> getAllKeyFamilyMappings() {
	    return dao.getAllKeyFamilyMappings();
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#purgeKeyFamilyMapping(org.openmrs.module.sdmxhdintegration.KeyFamilyMapping)
	 */
	@Override
	public void purgeKeyFamilyMapping(KeyFamilyMapping keyFamilyMapping) {
	    dao.deleteKeyFamilyMapping(keyFamilyMapping);
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#saveKeyFamilyMapping(org.openmrs.module.sdmxhdintegration.KeyFamilyMapping)
	 */
	@Override
	public KeyFamilyMapping saveKeyFamilyMapping(KeyFamilyMapping keyFamilyMapping) {
		return dao.saveKeyFamilyMapping(keyFamilyMapping);
	}
	
	/**
	 * @throws IOException 
	 * @throws SchemaValidationException 
	 * @throws ExternalRefrenceNotFoundException 
	 * @throws XMLStreamException 
	 * @throws ValidationException
	 * @should should get parsed DSD for given message
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getDataSetDefinition(org.openmrs.module.sdmxhdintegration.SDMXHDMessage)
	 */
	@Override
	public DSD getDataSetDefinition(SDMXHDMessage message) throws IOException, ValidationException, XMLStreamException, ExternalRefrenceNotFoundException, SchemaValidationException {
		String path = Context.getAdministrationService().getGlobalProperty("sdmxhdintegration.messageUploadDir");
		ZipFile zf = new ZipFile(path + File.separator + message.getZipFilename());
		SDMXHDParser parser = new SDMXHDParser();
		org.jembi.sdmxhd.SDMXHDMessage sdmxhdData = parser.parse(zf);
		return sdmxhdData.getDsd();
	}
}
