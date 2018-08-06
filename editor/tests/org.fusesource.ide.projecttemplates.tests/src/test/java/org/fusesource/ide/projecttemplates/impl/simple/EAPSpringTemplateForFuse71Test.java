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
package org.fusesource.ide.projecttemplates.impl.simple;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.fusesource.ide.projecttemplates.wizards.pages.model.EnvironmentData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseDeploymentPlatform;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseRuntimeKind;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class EAPSpringTemplateForFuse71Test {
	
	@Parameter
	public String version;
	
	@Parameter(1)
	public boolean isCompatible;
	
	@Parameters(name = "{0} should be compatible? {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{ "2.21.0.fuse-710XXX-redhat-X", true },
			{ "2.21.1", true },
			{ "2.21.0.fuse-000XXX-redhat-X", false },
			{ "2.22.0", true },
			{ "2.21.0", false },
			{ "2.20.0", false },
			{ "2.19.9", false }
		});
	}
	
	@Test
	public void testIsCompatible() throws Exception {
		assertThat(new EAPSpringTemplateForFuse71().isCompatible(new EnvironmentData(version, FuseDeploymentPlatform.STANDALONE, FuseRuntimeKind.WILDFLY))).isEqualTo(isCompatible);
	}
}
