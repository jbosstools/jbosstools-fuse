/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.fuse.core.server.subsystems;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.launching.AbstractVMInstall;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.fuse.core.runtime.FuseESBRuntimeDelegate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class Fuse7xStartupLaunchConfiguratorTest {
	@Mock
	private IServer server;
	@InjectMocks
	private Fuse7xStartupLaunchConfigurator fuse7xStartupLaunchConfigurator;
	@Mock
	private FuseESBRuntimeDelegate runtime;
	@Mock
	AbstractVMInstall vmInstall;

	@Test
	public void testGetVMArguments() throws Exception {
		String vmArguments = fuse7xStartupLaunchConfigurator.getVMArguments("karafInstallDir", runtime, "endorsedDirs", "extDirs");
		
		assertThat(vmArguments)
		.doesNotContain("PermSize")
		.contains("karaf.etc")
		.doesNotContain("derby");
	}
	
	@Test
	public void testGetVMArgumentsForJava9AndMore() throws Exception {
		when(runtime.getVM()).thenReturn(vmInstall);
		when(vmInstall.getJavaVersion()).thenReturn("11.0.2");
		
		String vmArguments = fuse7xStartupLaunchConfigurator.getVMArguments("karafInstallDir", runtime, "endorsedDirs", "extDirs");
		
		assertThat(vmArguments)
		.doesNotContain("PermSize")
		.contains("karaf.etc")
		.doesNotContain("derby")
		.doesNotContain("endorsed")
		.doesNotContain("ext.dir")
		.doesNotContain("UnsyncloadClass");
	}

}
