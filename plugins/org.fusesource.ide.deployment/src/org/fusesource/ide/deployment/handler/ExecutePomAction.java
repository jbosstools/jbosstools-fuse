package org.fusesource.ide.deployment.handler;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.fusesource.ide.deployment.ConfigurationUtils;
import org.fusesource.ide.deployment.config.HotfolderDeploymentConfiguration;
import org.fusesource.ide.launcher.ui.ExecutePomActionSupport;
import org.fusesource.ide.launcher.ui.InvalidConfigurationException;



/**
 * Launches the provision goal
 */
public class ExecutePomAction extends ExecutePomActionSupport {

	public static final String CONFIG_TYPE_ID = "org.fusesource.ide.deployer";
	
	public ExecutePomAction() {
		super("org.fusesource.ide.deployer.tabGroup", CONFIG_TYPE_ID, ProvisionMavenLaunchDelegate.MAVEN_GOALS);
	}
	

	protected void appendAttributes(IContainer basedir,
			ILaunchConfigurationWorkingCopy workingCopy, String goal) {

		// lets deploy to the default hot deploy directory
		String dir = null;
		HotfolderDeploymentConfiguration config = getHotfolderConfiguration();
		if (config != null) {
			dir = config.getHotDeployPath();
		}
		if (dir == null || dir.length() == 0) {
			ConfigureDeploymentsHandler.openPreferencesDialog();
			// lets prevent continuing with the execution
			throw new InvalidConfigurationException();
		} else {
			List<String> properties = new ArrayList<String>();
			properties.add("outputDirectory=" + dir);
			workingCopy.setAttribute(MavenLaunchConstants.ATTR_PROPERTIES, properties);
		}
	}


	protected HotfolderDeploymentConfiguration getHotfolderConfiguration() {
		HotfolderDeploymentConfiguration config = ConfigurationUtils.loadDefaultConfiguration();
		return config;
	}
}