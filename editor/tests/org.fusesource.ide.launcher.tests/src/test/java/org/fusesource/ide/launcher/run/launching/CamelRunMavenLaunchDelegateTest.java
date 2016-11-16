/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.run.launching;

import org.assertj.core.api.Assertions;
import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.fusesource.ide.launcher.run.util.CamelContextLaunchConfigConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CamelRunMavenLaunchDelegateTest {

	@Mock
	private ILaunchConfiguration launchConfig;

	private CamelRunMavenLaunchDelegate camelRunMavenLaunchDelegate = Mockito.spy(new CamelRunMavenLaunchDelegate());

	@Test
	public void testGetGoals() throws Exception {
		Mockito.doReturn("file:C:\\my%20path%20with%20space").when(launchConfig).getAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, (String) null);
		Mockito.doReturn(null).when(camelRunMavenLaunchDelegate).getFileInWorkspace(Mockito.anyString());
		Mockito.doReturn(false).when(camelRunMavenLaunchDelegate).isWarPackaging(Mockito.any(IFile.class));

		Assertions.assertThat(camelRunMavenLaunchDelegate.getGoals(launchConfig))
				.isEqualTo("clean package org.apache.camel:camel-maven-plugin:run -Dcamel.fileApplicationContextUri=\"file:C:\\my path with space\"");
	}

}
