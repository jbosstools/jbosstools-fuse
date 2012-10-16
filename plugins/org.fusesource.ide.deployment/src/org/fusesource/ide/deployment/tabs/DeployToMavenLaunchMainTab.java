package org.fusesource.ide.deployment.tabs;

import org.fusesource.ide.deployment.handler.ProvisionMavenLaunchDelegate;
import org.fusesource.ide.launcher.ui.tabs.MavenLaunchMainTab;


public class DeployToMavenLaunchMainTab extends MavenLaunchMainTab {

	public DeployToMavenLaunchMainTab(boolean isBuilder) {
		super(isBuilder);
	}

	@Override
	protected String getDefaultGoals() {
		return ProvisionMavenLaunchDelegate.MAVEN_GOALS;
	}

}