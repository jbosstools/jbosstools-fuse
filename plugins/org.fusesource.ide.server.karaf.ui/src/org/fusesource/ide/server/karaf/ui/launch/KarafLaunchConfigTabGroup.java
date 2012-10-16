package org.fusesource.ide.server.karaf.ui.launch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.wst.server.ui.ServerLaunchConfigurationTab;
import org.fusesource.ide.server.karaf.core.internal.server.IServerConfiguration;


public class KarafLaunchConfigTabGroup extends
		AbstractLaunchConfigurationTabGroup {

	public KarafLaunchConfigTabGroup() {
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		List<ILaunchConfigurationTab> tabs = new ArrayList<ILaunchConfigurationTab>(5);
		
		tabs.add(new ServerLaunchConfigurationTab(IServerConfiguration.SERVER_IDS_SUPPORTED));
		tabs.add(new JavaArgumentsTab());
		tabs.add(new JavaClasspathTab());
//		tabs.add(new SourceLookupTab());
		tabs.add(new EnvironmentTab());
		tabs.add(new CommonTab());
		setTabs(tabs.toArray( new ILaunchConfigurationTab[tabs.size()]));
	}

}
