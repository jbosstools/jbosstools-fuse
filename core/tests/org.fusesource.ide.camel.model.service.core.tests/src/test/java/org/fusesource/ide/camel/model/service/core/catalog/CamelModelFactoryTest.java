/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.model.service.core.catalog;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * @author lhein
 */
public class CamelModelFactoryTest {
	
	private static final List<String> AVAILABLE_VERSIONS = Arrays.asList("2.15.1.redhat-621084",
																		 "2.15.1.redhat-621199", 															 
																		 "2.15.1",
																		 "2.17.0.redhat-630077", 
																		 "2.17.0.redhat-630159");
	
	@Test
	public void testQualifierDecrease() {
		String version = CamelModelFactory.getCompatibleCamelVersion("2.15.1.redhat-621088", AVAILABLE_VERSIONS, "2.17.0.redhat-630159");
		assertThat(version).isEqualTo("2.15.1.redhat-621084");
	}

	@Test
	public void testQualifierRemovalIfEarlierVersionWithoutQualifierIsAvailable() {
		String version = CamelModelFactory.getCompatibleCamelVersion("2.15.1.redhat-621083", AVAILABLE_VERSIONS, "2.17.0.redhat-630159");
		assertThat(version).isEqualTo("2.15.1");
	}
	
	@Test
	public void testUnsupportedMinorVersionWillReturnEarlierSupportedMinorWithLatestMicro() {
		String version = CamelModelFactory.getCompatibleCamelVersion("2.16.3", AVAILABLE_VERSIONS, "2.17.0.redhat-630159");
		assertThat(version).isEqualTo("2.15.1.redhat-621199");
	}
	
	@Test
	public void testBleedingEdgeUnSupportedCamelVersionWillReturnLatestSupported() {
		String version = CamelModelFactory.getCompatibleCamelVersion("2.18.1", AVAILABLE_VERSIONS, "2.15.1.redhat-621199");
		assertThat(version).isEqualTo("2.17.0.redhat-630159");
	}
	
	@Test
	public void testTooEarlyCamelVersionWillReturnEarliestShipped() {
		String version = CamelModelFactory.getCompatibleCamelVersion("2.14.1", AVAILABLE_VERSIONS, "2.17.0.redhat-630159");
		assertThat(version).isEqualTo("2.15.1");
	}
}
