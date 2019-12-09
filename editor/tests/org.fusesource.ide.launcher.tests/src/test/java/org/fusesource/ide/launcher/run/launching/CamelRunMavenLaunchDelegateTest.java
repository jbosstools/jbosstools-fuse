/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.run.launching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.fusesource.ide.launcher.run.util.CamelContextLaunchConfigConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CamelRunMavenLaunchDelegateTest {

	@Mock
	private ILaunchConfiguration launchConfig;

	private CamelRunMavenLaunchDelegate camelRunMavenLaunchDelegate = spy(new CamelRunMavenLaunchDelegate());
	
	@Before
	public void setup() throws Exception {
		doReturn("file:C:\\my%20path%20with%20space").when(launchConfig).getAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, (String) null);
		doReturn(null).when(camelRunMavenLaunchDelegate).getFileInWorkspace(anyString());
		doReturn(false).when(camelRunMavenLaunchDelegate).isWarPackaging(any(IFile.class));
	}

	@Test
	public void testGetGoalsForNonSpringBootNonProductizedGroupId() throws Exception {
		doReturn(false).when(camelRunMavenLaunchDelegate).isSpringBoot(any(IFile.class));
		doReturn(false).when(camelRunMavenLaunchDelegate).isProductizedMavenPluginUsed(any(IFile.class));
		
		assertThat(camelRunMavenLaunchDelegate.getGoals(launchConfig))
				.isEqualTo("-U clean package org.apache.camel:camel-maven-plugin:run -Dcamel.fileApplicationContextUri=\"file:C:\\my path with space\"");
	}
	
	@Test
	public void testGetGoalsForNonSpringBootAndProductizedGroupId() throws Exception {
		doReturn(false).when(camelRunMavenLaunchDelegate).isSpringBoot(any(IFile.class));
		doReturn(true).when(camelRunMavenLaunchDelegate).isProductizedMavenPluginUsed(any(IFile.class));
		
		assertThat(camelRunMavenLaunchDelegate.getGoals(launchConfig))
				.isEqualTo("-U clean package org.jboss.redhat-fuse:camel-maven-plugin:run -Dcamel.fileApplicationContextUri=\"file:C:\\my path with space\"");
	}
	
	@Test
	public void testGetGoalsForSpringBootNonProductizedGroupId() throws Exception {
		doReturn(true).when(camelRunMavenLaunchDelegate).isSpringBoot(any(IFile.class));
		doReturn(false).when(camelRunMavenLaunchDelegate).isProductizedMavenPluginUsed(any(IFile.class));
		
		assertThat(camelRunMavenLaunchDelegate.getGoals(launchConfig))
				.isEqualTo("-U clean package org.springframework.boot:spring-boot-maven-plugin:run -Dcamel.fileApplicationContextUri=\"file:C:\\my path with space\"");
	}
	
	@Test
	public void testGetGoalsForSpringBootAndProductizedGroupId() throws Exception {
		doReturn(true).when(camelRunMavenLaunchDelegate).isSpringBoot(any(IFile.class));
		doReturn(true).when(camelRunMavenLaunchDelegate).isProductizedMavenPluginUsed(any(IFile.class));
		
		assertThat(camelRunMavenLaunchDelegate.getGoals(launchConfig))
				.isEqualTo("-U clean package org.jboss.redhat-fuse:spring-boot-maven-plugin:run -Dcamel.fileApplicationContextUri=\"file:C:\\my path with space\"");
	}

}
