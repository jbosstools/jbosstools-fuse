package org.fusesource.ide.fabric.navigator.maven;

import org.fusesource.ide.deployment.handler.MavenLaunchDelegateSupport;

public class ProvisionMavenLaunchDelegate extends MavenLaunchDelegateSupport {

	public static final String MAVEN_GOALS = "install";

	public ProvisionMavenLaunchDelegate() {
		super(MAVEN_GOALS);
	}


	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.MavenLaunchDelegate#getEclipseProcessName()
	 */
	@Override
	public String getEclipseProcessName() {
		return "Fuse IDE Build Runner";
	}
}
