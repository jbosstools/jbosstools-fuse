/*******************************************************************************
* Copyright (c) 2017 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at https://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
******************************************************************************/ 
package org.fusesource.ide.imports.sap;

import org.eclipse.equinox.p2.ui.Policy;

public class SapToolsPolicy extends Policy {

	public SapToolsPolicy() {
		setContactAllSites(false);
		setFilterOnEnv(false);
		setGroupByCategory(false);
		setHideAlreadyInstalled(true);
		setRepositoriesVisible(false);
		setRepositoryPreferencePageId(null);
		setRepositoryPreferencePageName(null);
		setRestartPolicy(RESTART_POLICY_PROMPT);
		setShowDrilldownRequirements(false);
		setShowLatestVersionsOnly(true);
		setUpdateDetailsPreferredSize(null);
		setUpdateWizardStyle(UPDATE_STYLE_MULTIPLE_IUS);
		
	}
}
