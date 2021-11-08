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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.fuse.core.runtime.IFuseESBRuntime;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class Fuse7xStartupLaunchConfiguratorDoOverridesIT {
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private IRuntime runtime;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private IFuseESBRuntime fuseRuntime;
	private Fuse7xStartupLaunchConfigurator fuse7xStartupLaunchConfigurator;

	@Mock
	private IServer server;
	
	@Mock
	private ILaunchConfigurationWorkingCopy launchConfig;

	@Before
	public void setup() throws CoreException {
		fuse7xStartupLaunchConfigurator = spy(new Fuse7xStartupLaunchConfigurator(server));
		doReturn("-Djava.ext.dirs=\"whatever\" -Djava.endorsed.dirs=whateverAgain").when(launchConfig).getAttribute(eq(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS),
				anyString());
		when(server.getRuntime()).thenReturn(runtime);
		when(server.getRuntime().loadAdapter(eq(IKarafRuntime.class), any())).thenReturn(fuseRuntime);
		doNothing().when(fuse7xStartupLaunchConfigurator).configureJRE(launchConfig, fuseRuntime, "karafInstallDir");
		when(fuseRuntime.getVM().getInstallLocation()).thenReturn(new File("install"));
		when(runtime.getLocation().toOSString()).thenReturn("karafInstallDir");
	}

	@Test
	public void testDoOverrides() throws Exception {
		fuse7xStartupLaunchConfigurator.doOverrides(launchConfig);

		verify(launchConfig).setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
				"-Djava.ext.dirs=" + fuse7xStartupLaunchConfigurator.createExtDirValue("karafInstallDir", new File("install")) + " " + "-Djava.endorsed.dirs="
						+ fuse7xStartupLaunchConfigurator.createEndorsedDirValue("karafInstallDir", new File("install")) + " ");
	}
}
