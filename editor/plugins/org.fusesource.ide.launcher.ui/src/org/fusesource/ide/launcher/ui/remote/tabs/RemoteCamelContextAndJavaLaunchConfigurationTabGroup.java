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
import java.util.Arrays;
import java.util.List;

import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.internal.debug.ui.launcher.RemoteJavaApplicationTabGroup;
import org.fusesource.ide.launcher.ui.tabs.CamelContextFileTab;

public class RemoteCamelContextAndJavaLaunchConfigurationTabGroup extends RemoteJavaApplicationTabGroup {
	
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		super.createTabs(dialog, mode);
		List<ILaunchConfigurationTab> tabs = new ArrayList<>(Arrays.asList(getTabs()));
		tabs.add(0, new CamelContextFileTab());
		tabs.add(1, new RemoteDebugJMXTab());
		setTabs(tabs.stream().toArray(ILaunchConfigurationTab[]::new));
	}

}
