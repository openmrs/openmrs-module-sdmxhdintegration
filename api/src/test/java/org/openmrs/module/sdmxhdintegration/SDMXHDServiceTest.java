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

import junit.framework.Assert;

import org.jembi.sdmxhd.dsd.DSD;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests for module service implementation
 */
public class SDMXHDServiceTest extends BaseModuleContextSensitiveTest {
	
	@SuppressWarnings("deprecation")
	@Before
	public void runBeforeAllTests() throws Exception {
		executeDataSet("TestingDataset.xml");
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getMessage(Integer)
	 */
	@Test
	@Verifies(value = "should get the correct message for the given id", method = "getMessage(Integer)")
	public void getMessage_shouldGetTheCorrectMessageForTheGivenId() throws Exception {
		SDMXHDService service = (SDMXHDService) Context.getService(SDMXHDService.class);
		
		SDMXHDMessage message1 = service.getMessage(1);
		Assert.assertEquals("test1", message1.getName());
		SDMXHDMessage message2 = service.getMessage(2);
		Assert.assertEquals("test2", message2.getName());
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getAllMessages()
	 */
	@Test
	@Verifies(value = "should return all messages if includeRetired is true", method = "getAllMessages(Boolean)")
	public void getAllMessages_shouldReturnAllMessages() throws Exception {
		SDMXHDService service = (SDMXHDService)Context.getService(SDMXHDService.class);
		List<SDMXHDMessage> messages = service.getAllMessages(true);

		Assert.assertEquals(2, messages.size());
		Assert.assertEquals(1, (int)messages.get(0).getId());
		Assert.assertEquals(2, (int)messages.get(1).getId());
	}
	
	/**
	 * @see {@link SDMXHDService#getAllMessages()}
	 */
	@Test
	@Verifies(value = "should return all non-retired messages if includeRetired is false", method = "getAllMessages(Boolean)")
	public void getAllMessages_shouldReturnAllNonRetiredMessages() throws Exception {
		SDMXHDService service = (SDMXHDService)Context.getService(SDMXHDService.class);
		List<SDMXHDMessage> messages = service.getAllMessages(false);

		Assert.assertEquals(1, messages.size());
		Assert.assertEquals(1, (int)messages.get(0).getId());
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#saveMessage(SDMXHDMessage)
	 */
	@Test
	@Verifies(value = "should save the given message", method = "saveMessage(SDMXHDMessage)")
	public void saveMessage_shouldSaveTheGivenMessage() throws Exception {
		// Create test message
		SDMXHDMessage message = new SDMXHDMessage();
		message.setName("test3");
		message.setDescription("description3");
		message.setZipFilename("dummy.zip");
		
		// Save message
		SDMXHDService service = (SDMXHDService) Context.getService(SDMXHDService.class);
		service.saveMessage(message);
		
		// Check it now has an id, creator and created date
		Assert.assertNotNull(message.getId());
		Assert.assertNotNull(message.getCreator());
		Assert.assertNotNull(message.getDateCreated());
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#deleteMessage(SDMXHDMessage)
	 */
	@Test
	@Verifies(value = "should delete the given message", method = "deleteMessage(SDMXHDMessage)")
	public void deleteMessage_shouldDeleteTheGivenMessage() throws Exception {
		SDMXHDService service = Context.getService(SDMXHDService.class);
		
		// Create test message
		SDMXHDMessage message = new SDMXHDMessage();
		message.setName("test4");
		message.setDescription("description4");
		message.setZipFilename("dummy.zip");
		
		// Save message and check it gets valid id
		service.saveMessage(message);
		int messageId = message.getId();
		Assert.assertTrue(messageId > 0);
		
		// Delete message and check it's gone
		service.deleteMessage(message);
		Assert.assertNull(service.getMessage(messageId));
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getKeyFamilyMapping(Integer)
	 */
	@Test
	@Verifies(value = "should return the correct mapping for the given id", method = "getKeyFamilyMapping(Integer)")
	public void getKeyFamilyMapping_shouldGetTheCorrectMappingForTheGivenId() throws Exception {
		SDMXHDService service = Context.getService(SDMXHDService.class);
		
		KeyFamilyMapping mapping1 = service.getKeyFamilyMapping(1);
		Assert.assertEquals(1, (int)mapping1.getId());
		
		KeyFamilyMapping mapping2 = service.getKeyFamilyMapping(2);
		Assert.assertEquals(2, (int)mapping2.getId());
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getKeyFamilyMapping(Integer)
	 */
	@Test
	@Verifies(value = "should return the correct mapping for the given message and key family", method = "getKeyFamilyMapping(SDMXHDMessage, Integer)")
	public void getKeyFamilyMapping_shouldGetTheCorrectMappingForTheGivenMessageAndKeyFamily() throws Exception {
		SDMXHDService service = Context.getService(SDMXHDService.class);
		
		SDMXHDMessage message1 = service.getMessage(1);	
		KeyFamilyMapping mapping1 = service.getKeyFamilyMapping(message1, "SDMX-HD");
		Assert.assertEquals(1, (int)mapping1.getId());
		
		SDMXHDMessage message2 = service.getMessage(2);	
		KeyFamilyMapping mapping2 = service.getKeyFamilyMapping(message2, "SDMX-HD");
		Assert.assertEquals(2, (int)mapping2.getId());
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.SDMXHDService#getAllKeyFamilyMappings()
	 */
	@Test
	@Verifies(value = "should return all mappings", method = "getAllKeyFamilyMappings()")
	public void getAllKeyFamilyMappings_shouldReturnAllMappings() throws Exception {
		SDMXHDService service = (SDMXHDService)Context.getService(SDMXHDService.class);
		List<KeyFamilyMapping> mappings = service.getAllKeyFamilyMappings();

		Assert.assertEquals(2, mappings.size());
		Assert.assertEquals(1, (int)mappings.get(0).getId());
		Assert.assertEquals(2, (int)mappings.get(1).getId());
	}
}
