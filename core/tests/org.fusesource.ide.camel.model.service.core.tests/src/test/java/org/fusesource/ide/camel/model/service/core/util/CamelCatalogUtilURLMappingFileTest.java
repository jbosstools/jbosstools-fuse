/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.fusesource.ide.camel.model.service.core.util.versionmapper.CamelForFIS20ToBomMapper;
import org.fusesource.ide.camel.model.service.core.util.versionmapper.CamelForFuse6ToBomMapper;
import org.junit.Test;

public class CamelCatalogUtilURLMappingFileTest {

	@Test
	public void testRemoteCamel2BOMVersionMappingAvailability() {
		try {
			Properties vMapping = new Properties();
			URL url = new URL(new CamelForFuse6ToBomMapper().getUrl());
			vMapping.load(url.openStream());
			assertTrue("Seems there is no data available in the online mapping file for Camel2BOM versions.", vMapping.size() > 0);
		} catch (IOException ex) {
			fail("Unable to load the Camel2BOM mapping file from remote repo.");
		}
	}
	
	@Test
	public void testRemoteFISVersionMappingAvailability() {
		try {
			Properties fisMapping = new Properties();
			URL url = new URL(new CamelForFIS20ToBomMapper().getUrl());
			fisMapping.load(url.openStream());
			assertTrue("Seems there is no data available in the online mapping file for FIS ONLY versions.", fisMapping.size() > 0);
		} catch (IOException ex) {
			fail("Unable to load the FIS-ONLY mapping file from remote repo.");
		}
	}
}
