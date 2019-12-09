/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.launcher.ui.tabs;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.RefreshTab;
import org.eclipse.m2e.ui.internal.launch.MavenJRETab;
import org.eclipse.m2e.ui.internal.launch.MavenLaunchMainTab;

/**
 * @author lhein
 */
public class CamelContextLaunchConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {

	public CamelContextLaunchConfigurationTabGroup() {
		/* Left empty for reflection */
	}

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new MavenLaunchMainTab(),
				new MavenJRETab(),
				new RefreshTab(), 
                new EnvironmentTab(), 
                new CommonTab()
			};
			setTabs(tabs);
	}
}
