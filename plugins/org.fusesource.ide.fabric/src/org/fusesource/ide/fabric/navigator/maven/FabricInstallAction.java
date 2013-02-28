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


import org.fusesource.ide.launcher.ui.ExecutePomActionSupport;


/**
 * Does a local build and installs the build locally
 */
public class FabricInstallAction extends ExecutePomActionSupport {

	public static final String CONFIG_TAB_GROUP = "org.fusesource.fabric.build.tabGroup";
	public static final String CONFIG_TYPE_ID = "org.fusesource.fabric.build";

	public FabricInstallAction() {
		super(CONFIG_TAB_GROUP, CONFIG_TYPE_ID, "install");
	}
}