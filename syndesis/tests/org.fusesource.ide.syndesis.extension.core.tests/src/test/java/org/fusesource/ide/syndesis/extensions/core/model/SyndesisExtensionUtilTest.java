/******************************************************************************* 
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.fusesource.ide.syndesis.extensions.core.util.SyndesisExtensionsUtil;
import org.junit.Test;

/**
 * @author lheinema
 */
public class SyndesisExtensionUtilTest {

	private String[] validVersions = new String[] {
			"1.0",
			"1.0.0",
			"1.1.20180108"
	};
	
	private String[] invalidVersions = new String[] {
			"1",
			"1-SNAPSHOT",
			"1.1.SNAPSHOT",
			"1.1-SNAPSHOT",
			"1.1.0-SNAPSHOT",
			"1.1.v20180108",
			"1.b",
			"1.1.",
			".1.1",
			".1."
	};
	
	@Test
	public void testIsValidSyndesisExtensionVersion() {
		for (String version : validVersions) {
			assertThat(SyndesisExtensionsUtil.isValidSyndesisExtensionVersion(version)).as("Check value: " + version).isTrue();
		}
	}
	
	@Test
	public void testIsInvalidSyndesisExtensionVersion() {
		for (String version : invalidVersions) {
			assertThat(SyndesisExtensionsUtil.isValidSyndesisExtensionVersion(version)).as("Check value: " + version).isFalse();
		}
	}
}
