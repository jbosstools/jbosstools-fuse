package org.fusesource.ide.launcher.ui.tabs;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.RefreshTab;

/**
 * @author lhein
 */
public class CamelContextLaunchConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {

	/**
	 * constructor
	 */
	public CamelContextLaunchConfigurationTabGroup() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog, java.lang.String)
	 */
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new MavenLaunchMainTab(false),
                new MavenJRETab(), 
                new RefreshTab(), 
                new EnvironmentTab(), 
                new CommonTab()
			};
			setTabs(tabs);
	}
}
