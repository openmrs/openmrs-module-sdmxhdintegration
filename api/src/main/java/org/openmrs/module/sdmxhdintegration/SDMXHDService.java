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

import java.io.IOException;
import java.util.List;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLStreamException;

import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.parser.exceptions.ExternalRefrenceNotFoundException;
import org.jembi.sdmxhd.parser.exceptions.SchemaValidationException;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * SDMX integration service provided by this module
 */
@Transactional
public interface SDMXHDService extends OpenmrsService {
	
	/**
	 * Gets an SDMX-HD message
	 * @param id the message id
	 * @return the message
	 */
	@Transactional(readOnly=true)
	public SDMXHDMessage getMessage(Integer id);
	
	/**
	 * Gets all SDMX-HD messages
	 * @param includeRetired true if retired messages should be included
	 * @return the messages
	 */
	@Transactional(readOnly=true)
	public List<SDMXHDMessage> getAllMessages(Boolean includeRetired);
	
	/**
	 * Saves an SDMX-HD message
	 * @param sdmxhdMessage the message
	 */
	public void saveMessage(SDMXHDMessage message);
	
	/**
	 * Deletes an SDMX-HD message
	 * @param sdmxhdMessage the message
	 */
	public void deleteMessage(SDMXHDMessage message);
	
	/**
	 * Gets a key family mapping by id
	 * @param the mapping id
	 * @return the mapping
	 */
	@Transactional(readOnly=true)
	public KeyFamilyMapping getKeyFamilyMapping(Integer id);
	
	/**
	 * Gets key family mapping from the given message by key family id
	 * @param sdmxhdMessage the message
	 * @param keyFamilyId the key family id
	 * @return the mapping
	 */
	@Transactional(readOnly=true)
	public KeyFamilyMapping getKeyFamilyMapping(SDMXHDMessage sdmxhdMessage, String keyFamilyId);
	
	/**
	 * Gets the key family mapping for the given report definition
	 * @param reportDefinitionId the report definition id
	 * @return the mapping
	 */
	@Transactional(readOnly=true)
	public KeyFamilyMapping getKeyFamilyMappingByReportDefinitionId(Integer reportDefinitionId);
	
	/**
	 * Gets all the key family mapping for the given message
	 * @param sdmxhdMessage the message
	 * @return the mappings
	 */
	@Transactional(readOnly=true)
	public List<KeyFamilyMapping> getKeyFamilyMappingsFromMessage(SDMXHDMessage sdmxhdMessage);
	
	/**
	 * Gets all key family mappings
	 * @return the mappings
	 */
	@Transactional(readOnly=true)
	public List<KeyFamilyMapping> getAllKeyFamilyMappings();
	
	/**
	 * Saves a key family mapping
	 * @param keyFamilyMapping the mapping
	 * @return the same mapping
	 */
	public KeyFamilyMapping saveKeyFamilyMapping(KeyFamilyMapping keyFamilyMapping);
	
	/**
	 * Purges a key family mapping
	 * @param keyFamilyMapping the mapping
	 */
	public void purgeKeyFamilyMapping(KeyFamilyMapping keyFamilyMapping);
	
	
	@Transactional(readOnly=true)
	public DSD getSDMXHDDataSetDefinition(SDMXHDMessage sdmxhdMessage) throws IOException, ValidationException, XMLStreamException, ExternalRefrenceNotFoundException, SchemaValidationException;    
}
