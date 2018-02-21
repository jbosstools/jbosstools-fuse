/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.launchconfigurations;

import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.LaunchConfiguration;

/**
 * Represents 'Maven Build' of launch configuration in Launch configuration dialog.
 * 
 * @author tsedmik
 */
public class MavenBuildLaunchConfiguration extends LaunchConfiguration {

	public MavenBuildLaunchConfiguration() {
		super("Maven Build");
	}
}
