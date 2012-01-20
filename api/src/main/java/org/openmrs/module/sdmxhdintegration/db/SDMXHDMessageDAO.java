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

package org.openmrs.module.sdmxhdintegration.db;

import java.util.List;

import org.openmrs.module.sdmxhdintegration.KeyFamilyMapping;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;

/**
 * Interface of the module DAO
 */
public interface SDMXHDMessageDAO {

	public SDMXHDMessage getSDMXHDMessage(Integer id);
	
	public List<SDMXHDMessage> getAllSDMXHDMessages(Boolean includeRetired);
	
	public SDMXHDMessage saveSDMXHDMessage(SDMXHDMessage sdmxhdMessage);
	
	public void deleteSDMXHDMessage(SDMXHDMessage sdmxhdMessage);
	
	public KeyFamilyMapping getKeyFamilyMapping(Integer id);
	
	public KeyFamilyMapping getKeyFamilyMapping(SDMXHDMessage sdmxhdMessage, String keyFamilyId);
	
	public KeyFamilyMapping getKeyFamilyMappingByReportDefinitionId(Integer reportDefinitionId);
	
	public List<KeyFamilyMapping> getKeyFamilyMappingsFromMessage(SDMXHDMessage sdmxhdMessage);
	
	public List<KeyFamilyMapping> getAllKeyFamilyMappings();
	
	public KeyFamilyMapping saveKeyFamilyMapping(KeyFamilyMapping keyFamilyMapping);
	
	public void deleteKeyFamilyMapping(KeyFamilyMapping keyFamilyMapping);
}
