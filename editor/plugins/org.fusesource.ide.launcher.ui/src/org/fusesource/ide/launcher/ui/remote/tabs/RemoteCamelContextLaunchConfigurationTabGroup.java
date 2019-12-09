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
package org.fusesource.ide.launcher.ui.remote.tabs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.fusesource.ide.launcher.ui.tabs.CamelContextFileTab;

public class RemoteCamelContextLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

	public RemoteCamelContextLaunchConfigurationTabGroup() {
		/* Left empty for reflection usage*/
	}

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		List<ILaunchConfigurationTab> tabs = new ArrayList<>();
		tabs.add(new CamelContextFileTab());
		tabs.add(new RemoteDebugJMXTab());
		setTabs(tabs.stream().toArray(ILaunchConfigurationTab[]::new));
	}

}
