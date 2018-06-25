/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.fusesource.ide.syndesis.extensions.core.util.SyndesisExtensionsUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SyndesisExtensionUtilInvalidVersionTest {

	@Parameter
	public String invalidVersion;
	
	@Parameters(name = "{0}")
	public static String[] invalidVersions() {
		return new String[] {
				"1",
				"1-SNAPSHOT",
				"1.b",
				"1.1.",
				".1.1",
				".1.",
				".1.1.",
				".1.1-",
				"a",
				"a.b",
				"a.b.c",
				"a-SNAPSHOT",
				"a.1-SNAPSHOT",
				"1.a-SNAPSHOT",
				".-."
				};
	}
	
	@Test
	public void testIsValidSyndesisExtensionVersion() {
		assertThat(SyndesisExtensionsUtil.isValidSyndesisExtensionVersion(invalidVersion)).isFalse();
	}
	
}
