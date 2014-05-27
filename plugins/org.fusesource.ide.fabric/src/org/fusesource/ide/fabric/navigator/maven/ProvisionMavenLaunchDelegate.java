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


public class ProvisionMavenLaunchDelegate /**extends MavenLaunchDelegateSupport**/ {

	public static final String MAVEN_GOALS = "install";

	public ProvisionMavenLaunchDelegate() {
//		super(MAVEN_GOALS);
	}


	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.MavenLaunchDelegate#getEclipseProcessName()
	 */
//	@Override
	public String getEclipseProcessName() {
		return "Fuse Build Runner";
	}
}
