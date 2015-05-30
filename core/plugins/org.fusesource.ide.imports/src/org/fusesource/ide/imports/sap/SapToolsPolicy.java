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
