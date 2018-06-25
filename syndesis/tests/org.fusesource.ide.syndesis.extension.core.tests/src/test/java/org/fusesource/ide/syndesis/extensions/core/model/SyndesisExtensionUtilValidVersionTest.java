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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author lheinema
 */
@RunWith(Parameterized.class)
public class SyndesisExtensionUtilValidVersionTest {
	
	@Parameter
	public String validVersion;
	
	@Parameters(name = "{0}")
	public static String[] validVersions() {
		return new String[] {
				"1.0",
				"1.0.0",
				"1.1.20180108",
				"1.3-SNAPSHOT",
				"1.3.10-SNAPSHOT",
				"1.3.0.fuse-1",
				"1.3.10.fuse-000001-redhat-1"
		};
	}
	
	@Test
	public void testIsValidSyndesisExtensionVersion() {
		assertThat(SyndesisExtensionsUtil.isValidSyndesisExtensionVersion(validVersion)).isTrue();
	}
}
