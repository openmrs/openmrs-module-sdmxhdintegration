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

import junit.framework.Assert;

import org.jembi.sdmxhd.dsd.DSD;
import org.jembi.sdmxhd.primitives.Code;
import org.jembi.sdmxhd.primitives.CodeList;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests for Utils class
 */
public class UtilsTest extends BaseModuleContextSensitiveTest {
	
	@SuppressWarnings("deprecation")
	@Before
	public void runBeforeAllTests() throws Exception {
		executeDataSet("TestingDataset.xml");
		
		// Set value of message upload dir to tmp directory
		AdministrationService as = Context.getAdministrationService();
		as.setGlobalProperty("sdmxhdintegration.messageUploadDir", System.getProperty("java.io.tmpdir"));
		
		// Copy zips to upload dir (i.e. the temp dir)
		TestingUtils.uploadMessage("test_message1.zip");
		TestingUtils.uploadMessage("test_message2.zip");
	}
	
	/**
	 * @see org.openmrs.module.sdmxhdintegration.Utils#getDataSetDefinition(SDMXHDMessage)
	 */
	@Test
	@Verifies(value = "should get parsed DSD for given message", method = "getDataSetDefinition(SDMXHDMessage)")
	public void getDataSetDefinition_shouldGetParsedDSDForMessage() throws Exception {
		SDMXHDService service = Context.getService(SDMXHDService.class);
		SDMXHDMessage message = service.getMessage(1);
		
		DSD dsd = Utils.getDataSetDefinition(message);
		
		// Check DSD parsed correctly
		Assert.assertNotNull(dsd);
		Assert.assertEquals(1, dsd.getKeyFamilies().size());
		Assert.assertEquals("SDMX-HD", dsd.getKeyFamilies().get(0).getId());
		Assert.assertEquals("WHO", dsd.getKeyFamilies().get(0).getAgencyID());
	}
	
	@Test
    public void getDisplayNameForIndicatorAndDimensions_shouldGetDisplayName() throws Exception {
		SDMXHDService service = Context.getService(SDMXHDService.class);
		SDMXHDMessage message = service.getMessage(1);
		DSD dsd = Utils.getDataSetDefinition(message);
		String name = Utils.getDisplayNameForIndicatorAndDimensions(dsd, "INDICATOR=4,GENDER=_ALL,AGROUP=5");
		Assert.assertEquals("Adults aged = 15 years who are obese (All 15+)", name);
    }
}
