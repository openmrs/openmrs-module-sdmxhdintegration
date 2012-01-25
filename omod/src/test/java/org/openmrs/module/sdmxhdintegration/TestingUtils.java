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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;

import com.mchange.v2.c3p0.util.TestUtils;

/**
 * Utility methods for testing
 */
public class TestingUtils {
	/**
	 * Uploads a message into the system
	 * @param zip the message zip file path
	 * @throws Exception 
	 */
	public static void uploadMessage(String zip) throws Exception {
		AdministrationService as = Context.getAdministrationService();
		String dir = as.getGlobalProperty("sdmxhdintegration.messageUploadDir");
		copyResource(zip, dir + File.separator + zip);
	}
	
	/**
	 * Copies a resource in a JAR to an actual file
	 * @param resource the resource path
	 * @param destination the destination path
	 * @throws Exception
	 */
	public static void copyResource(String resource, String destination) throws Exception {
		InputStream in;
		File resFile = new File(resource);
		if (resFile.exists())
			in = new FileInputStream(resFile);
		else
			in = TestUtils.class.getClassLoader().getResourceAsStream(resource);
		OutputStream out = new FileOutputStream(destination);
		
		// Copy byte by byte
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0)
			out.write(buf, 0, len);
		
		in.close();
		out.close();
	}
}
