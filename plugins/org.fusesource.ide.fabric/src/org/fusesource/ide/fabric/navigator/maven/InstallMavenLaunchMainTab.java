package org.fusesource.ide.fabric.navigator.maven;

import org.fusesource.ide.launcher.ui.tabs.MavenLaunchMainTab;

public class InstallMavenLaunchMainTab extends MavenLaunchMainTab {

	public InstallMavenLaunchMainTab(boolean isBuilder) {
		super(isBuilder);
	}

	@Override
	protected String getDefaultGoals() {
		return "install";
	}

}