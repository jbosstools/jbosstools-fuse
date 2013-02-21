package org.fusesource.ide.deployment.handler;




public class ProvisionMavenLaunchDelegate extends MavenLaunchDelegateSupport {

	public static final String MAVEN_GOALS = "clean org.fusesource.mvnplugins:maven-provision-plugin:provision";

	public ProvisionMavenLaunchDelegate() {
		super(MAVEN_GOALS);
	}
	
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.MavenLaunchDelegate#getEclipseProcessName()
	 */
	@Override
	public String getEclipseProcessName() {
		return "Fuse Deployment Runner";
	}
}
