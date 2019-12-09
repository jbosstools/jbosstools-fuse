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

package org.fusesource.ide.server.karaf.ui.launch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;


public class KarafLaunchConfigTabGroup extends
		AbstractLaunchConfigurationTabGroup {

	public KarafLaunchConfigTabGroup() {
	}

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		List<ILaunchConfigurationTab> tabs = new ArrayList<>(5);
		
//		tabs.add(new ServerLaunchConfigurationTab(IKarafServerDelegate.SERVER_IDS_SUPPORTED));
		tabs.add(new JavaArgumentsTab());
		tabs.add(new JavaClasspathTab());
//		tabs.add(new SourceLookupTab());
		tabs.add(new EnvironmentTab());
		tabs.add(new CommonTab());
		setTabs(tabs.toArray( new ILaunchConfigurationTab[tabs.size()]));
	}

}
