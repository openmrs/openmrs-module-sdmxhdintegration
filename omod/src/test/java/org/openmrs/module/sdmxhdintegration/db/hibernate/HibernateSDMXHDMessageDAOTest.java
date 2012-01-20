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

package org.openmrs.module.sdmxhdintegration.db.hibernate;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.sdmxhdintegration.SDMXHDMessage;
import org.openmrs.module.sdmxhdintegration.SDMXHDService;
import org.openmrs.module.sdmxhdintegration.db.hibernate.HibernateSDMXHDMessageDAO;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests for module DAO implementation
 */
public class HibernateSDMXHDMessageDAOTest extends BaseModuleContextSensitiveTest {
	
	public Integer testMsg1Id;
	public Integer testMsg2Id;
	
	@Before
	public void runBeforeAllTests() throws Exception {
		executeDataSet("IndicatorTest.xml");
		SDMXHDService sdmxhdService = Context.getService(SDMXHDService.class);
		
		// Create 2 test messages
		SDMXHDMessage message1 = new SDMXHDMessage();	
		message1.setName("test1");
		message1.setDescription("test1");
		message1.setSdmxhdZipFileName("SDMX-HD.v1.0 sample1.zip");
		SDMXHDMessage message2 = new SDMXHDMessage();
		message2.setName("test2");
		message2.setDescription("test2");
		message2.setSdmxhdZipFileName("SDMX-HD.v1.0 sample2.zip");
		
		// Save them
		sdmxhdService.saveMessage(message1);
		sdmxhdService.saveMessage(message2);
		
		// Get their ids for use in tests
		testMsg1Id = message1.getId();
		testMsg2Id = message2.getId();
	}
	
	/**
	 * @see {@link HibernateSDMXHDMessageDAO#deleteSDMXHDMessage(SDMXHDMessage)}
	 * 
	 */
	@Test
	@Verifies(value = "should delete the message with the given id", method = "deleteMessage(SDMXHDMessage)")
	public void deleteMessage_shouldDeleteMessageWithTheGivenId() throws Exception {
		// Create test message
		SDMXHDMessage message = new SDMXHDMessage();
		message.setName("deltest");
		message.setDescription("deltest");
		message.setSdmxhdZipFileName("SDMX-HD.v1.0 sample1.zip");
		
		// Save message and then delete it
		SDMXHDService service = Context.getService(SDMXHDService.class);
		service.saveMessage(message);
		Integer messageId = message.getId();
		service.deleteMessage(message);
		
		// Request for that object should now return null
		Assert.assertNull(service.getMessage(messageId));
	}
	
	/**
	 * @see {@link HibernateSDMXHDMessageDAO#getAllSDMXHDMessages()}
	 * 
	 */
	@Test
	@Verifies(value = "should return all sdmx messages", method = "getAllMessages()")
	public void getAllMessages_shouldReturnAllMessages() throws Exception {
		SDMXHDService sdmxhdService = (SDMXHDService) Context.getService(SDMXHDService.class);
		List<SDMXHDMessage> messages = sdmxhdService.getAllMessages(false);
		
		Assert.assertEquals(2, messages.size());
	}
	
	/**
	 * @see {@link HibernateSDMXHDMessageDAO#getMessage(Integer)}
	 * 
	 */
	@Test
	@Verifies(value = "should get the correct message for the given id", method = "getMessage(Integer)")
	public void getMessage_shouldGetTheCorrectMessageForTheGivenId() throws Exception {
		SDMXHDService sdmxhdService = (SDMXHDService) Context.getService(SDMXHDService.class);
		SDMXHDMessage message = sdmxhdService.getMessage(testMsg1Id);
		
		Assert.assertNotNull(message);
		Assert.assertEquals("test1", message.getName());
	}
	
	/**
	 * @see {@link HibernateSDMXHDMessageDAO#saveMessage(SDMXHDMessage)}
	 * 
	 */
	@Test
	@Verifies(value = "should save the given message", method = "saveMessage(SDMXHDMessage)")
	public void saveSDMXHDMessage_shouldSaveTheGivenMessage() throws Exception {
		// Create test message
		SDMXHDMessage message = new SDMXHDMessage();
		message.setName("test3");
		message.setDescription("test3");
		message.setSdmxhdZipFileName("SDMX-HD.v1.0 sample1.zip");
		
		// Save message
		SDMXHDService service = (SDMXHDService) Context.getService(SDMXHDService.class);
		service.saveMessage(message);
		
		// Check it now has an id
		Assert.assertNotNull(message.getId());
	}
}