/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc.
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

import org.fusesource.ide.camel.model.service.core.util.versionmapper.CamelForFuse6ToBomMapper;
import org.fusesource.ide.projecttemplates.wizards.pages.model.EnvironmentData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseDeploymentPlatform;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseRuntimeKind;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CBRTemplateForFuse6Test {

	@Parameter
	public String version;
	
	@Parameter(1)
	public boolean isCompatible;
	
	@Parameters(name = "{0} should be compatible? {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{ "2.17.0", false },
			{ CamelForFuse6ToBomMapper.FUSE_63_R8_CAMEL_VERSION, false },
			{ CamelForFuse6ToBomMapper.FUSE_63_R9_CAMEL_VERSION, true },
			{ "2.17.0.redhat-630357", true },
			{ CamelForFuse6ToBomMapper.FUSE_621_R9_CAMEL_VERSION, true }
		});
	}
	
	@Test
	public void testIsCompatible() throws Exception {
		EnvironmentData environment = new EnvironmentData(version, FuseDeploymentPlatform.STANDALONE, FuseRuntimeKind.KARAF);
		assertThat(new CBRTemplateForFuse6().isCompatible(environment)).isEqualTo(isCompatible);
	}

}
