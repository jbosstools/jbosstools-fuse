/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.fabric.navigator.maven;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.m2e.ui.internal.launch.MavenLaunchMainTab;

public class InstallMavenLaunchMainTab extends MavenLaunchMainTab {

	public InstallMavenLaunchMainTab(boolean isBuilder) {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.m2e.ui.internal.launch.MavenLaunchMainTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ATTR_GOALS, "install");
		super.setDefaults(configuration);
	}
}