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

import java.util.List;

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
	 * @should get the correct message for the given id
	 */
	@Transactional(readOnly=true)
	public SDMXHDMessage getMessage(Integer id);
	
	/**
	 * Gets all SDMX-HD messages
	 * @param includeRetired true if retired messages should be included
	 * @return the messages
	 * @should return all messages if includeRetired is true
	 * @should return all non-retired messages if includeRetired is false
	 * @should return an empty list if no messages exist
	 */
	@Transactional(readOnly=true)
	public List<SDMXHDMessage> getAllMessages(Boolean includeRetired);
	
	/**
	 * Saves an SDMX-HD message
	 * @param sdmxhdMessage the message
	 * @should save the given message
	 */
	public void saveMessage(SDMXHDMessage message);
	
	/**
	 * Deletes an SDMX-HD message
	 * @param sdmxhdMessage the message
	 * @should delete the given message
	 */
	public void deleteMessage(SDMXHDMessage message);

	/**
	 * Gets a key family mapping by id
	 * @param the mapping id
	 * @return the mapping
	 * @should return the correct mapping for the given id
	 */
	@Transactional(readOnly=true)
	public KeyFamilyMapping getKeyFamilyMapping(Integer id);
	
	/**
	 * Gets key family mapping from the given message by key family id
	 * @param message the message
	 * @param keyFamilyId the key family id
	 * @return the mapping
	 * @should return the correct mapping for the given message and key family
	 */
	@Transactional(readOnly=true)
	public KeyFamilyMapping getKeyFamilyMapping(SDMXHDMessage message, String keyFamilyId);
	
	/**
	 * Gets the key family mapping for the given report definition
	 * @param reportDefinitionId the report definition id
	 * @return the mapping
	 */
	@Transactional(readOnly=true)
	public KeyFamilyMapping getKeyFamilyMappingByReportDefinitionId(Integer reportDefinitionId);
	
	/**
	 * Gets all the key family mapping for the given message
	 * @param message the message
	 * @return the mappings
	 */
	@Transactional(readOnly=true)
	public List<KeyFamilyMapping> getKeyFamilyMappingsFromMessage(SDMXHDMessage message);
	
	/**
	 * Gets all key family mappings
	 * @return the mappings
	 * @should return all mappings
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
	public void deleteKeyFamilyMapping(KeyFamilyMapping keyFamilyMapping);    
}
