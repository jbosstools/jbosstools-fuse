/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
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

import org.junit.Test;

/**
 * @author lheinema
 *
 */
public class CamelCatalogUtilsTest {

	@Test
	public void testRemoteVersionMappingAvailability() {
		try {
			Properties vMapping = new Properties();
			URL url = new URL(CamelCatalogUtils.CAMEL_TO_BOM_MAPPING_URL);
			vMapping.load(url.openStream());
			assertTrue("Seems there is no data available in the online mapping file for Camel2BOM versions.", vMapping.size() > 0);
		} catch (IOException ex) {
			fail("Unable to load the Camel2BOM mapping file from remote repo.");
		}
	}
}
