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

import org.junit.Test;
import org.openmrs.test.Verifies;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import junit.framework.Assert;

/**
 * Test case for message validator
 */
public class SDMXHDMessageValidatorTest {

	private SDMXHDMessageValidator validator = new SDMXHDMessageValidator();
	
	@Test
	@Verifies(value = "should support only message class", method = "supports(Class)")
	public void supports_shouldSupportOnlyMessageClass() {
		Assert.assertTrue(validator.supports(SDMXHDMessage.class));
		Assert.assertFalse(validator.supports(Object.class));
	}
	
	@Test
	@Verifies(value = "should accept if valid", method = "validate(Object, Errors)")
	public void validate_shouldAcceptIfValid() {
		SDMXHDMessage message = new SDMXHDMessage();
		message.setName("Test");
		message.setDescription("Description");
		message.setZipFilename("dummy.zip");
		
		Errors errors = new BindException(message, "message");
		validator.validate(message, errors);
		Assert.assertFalse(errors.hasErrors());
	}
	
	@Test
	@Verifies(value = "should reject if name is empty", method = "validate(Object, Errors)")
	public void validate_shouldRejectIfNameEmpty() {
		SDMXHDMessage message = new SDMXHDMessage();
		message.setName(null);
		message.setDescription("Description");
		message.setZipFilename("dummy.zip");
		
		Errors errors = new BindException(message, "message");
		validator.validate(message, errors);
		Assert.assertTrue(errors.hasErrors());
	}
	
	@Test
	@Verifies(value = "should reject if zip file name is empty", method = "validate(Object, Errors)")
	public void validate_shouldRejectIfZipFilenameEmpty() {
		SDMXHDMessage message = new SDMXHDMessage();
		message.setName("Test");
		message.setDescription("Description");
		message.setZipFilename("");
		
		Errors errors = new BindException(message, "message");
		validator.validate(message, errors);
		Assert.assertTrue(errors.hasErrors());
	}
}
