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
package org.jboss.tools.fuse.transformation.extensions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DataTransformationPaletteEntryTest {
	
	@Spy
	DataTransformationPaletteEntry dataTransformationPaletteEntry;
	
	@Test
	public void testIsNotValidFor220() throws Exception {
		checkNotSupportedVersion("2.20.0");
	}
	
	@Test
	public void testIsNotValidForLatestCamelCommunity() throws Exception {
		checkNotSupportedVersion(CamelCatalogUtils.CAMEL_VERSION_LATEST_COMMUNITY);
	}

	protected void checkNotSupportedVersion(String notSupportedVersion) {
		doReturn(notSupportedVersion).when(dataTransformationPaletteEntry).getCurrentProjectCamelVersion();
		assertThat(dataTransformationPaletteEntry.isValid(null)).isFalse();
	}
	
	@Test
	public void testValidForLatestFuse63() throws Exception {
		checkValid(CamelCatalogUtils.CAMEL_VERSION_LATEST_PRODUCTIZED_63);
	}
	
	@Test
	public void testValidForLatestFuse62() throws Exception {
		checkValid(CamelCatalogUtils.CAMEL_VERSION_LATEST_PRODUCTIZED_62);
	}

	protected void checkValid(String validVersion) {
		doReturn(validVersion).when(dataTransformationPaletteEntry).getCurrentProjectCamelVersion();
		assertThat(dataTransformationPaletteEntry.isValid(null)).isTrue();
	}

}
