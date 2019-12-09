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
package org.fusesource.ide.launcher.ui.tests.integration.remote.tabs;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.ui.remote.tabs.RemoteDebugJMXTab;
import org.fusesource.ide.launcher.ui.tabs.CamelContextFileTab;
import org.fusesource.ide.launcher.ui.tests.integration.tabs.AbstractLaunchConfigurationTabGroupIT;
import org.junit.Test;

public class RemoteCamelContextAndJavaLaunchConfigurationTabGroupIT extends AbstractLaunchConfigurationTabGroupIT{
	
	@Test
	public void testCreationTabs() throws CoreException {
		ILaunchConfigurationTab[] tabs = getTabs();
		assertThat(tabs).hasSize(5);
		assertThat(tabs[0]).isInstanceOf(CamelContextFileTab.class);
		assertThat(tabs[1]).isInstanceOf(RemoteDebugJMXTab.class);
	}
	
	@Override
	protected String getExpectedDefaultJMXURI() {
		return ICamelDebugConstants.DEFAULT_REMOTE_JMX_URI;
	}

	@Override
	protected String getLaunchConfigId() {
		return "org.fusesource.ide.launcher.camelandjavacontext.remote";
	}

	@Override
	protected String getLaunchMode() {
		return ILaunchManager.DEBUG_MODE;
	}
}
