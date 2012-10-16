package org.fusesource.ide.deployment.tabs;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.RefreshTab;
import org.fusesource.ide.launcher.ui.tabs.MavenJRETab;


/**
 * @author lhein
 */
public class DeployLaunchConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {

	/**
	 * constructor
	 */
	public DeployLaunchConfigurationTabGroup() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog, java.lang.String)
	 */
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new DeployToMavenLaunchMainTab(false),
                new MavenJRETab(), 
                new RefreshTab(), 
                new EnvironmentTab(), 
                new CommonTab()
			};
			setTabs(tabs);
	}
}
