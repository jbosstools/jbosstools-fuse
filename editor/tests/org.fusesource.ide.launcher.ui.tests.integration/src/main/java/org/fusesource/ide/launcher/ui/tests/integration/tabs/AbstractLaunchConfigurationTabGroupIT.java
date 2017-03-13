/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.ui.tests.integration.tabs;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationPresentationManager;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationTabGroupWrapper;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.equinox.internal.security.storage.friends.IStorageConstants;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractLaunchConfigurationTabGroupIT {
	
	private String initialValue;
	
	@Before
	public void setup(){
		//it avoids to have Master Security Password popup
		IEclipsePreferences node = ConfigurationScope.INSTANCE.getNode("org.eclipse.equinox.security");
		initialValue = node.get(IStorageConstants.DISABLED_PROVIDERS_KEY, "");
		node.put(IStorageConstants.DISABLED_PROVIDERS_KEY, "org.eclipse.equinox.security.ui.defaultpasswordprovider");
	}
	
	@After
	public void tearDown(){
		IEclipsePreferences node = ConfigurationScope.INSTANCE.getNode("org.eclipse.equinox.security");
		if(initialValue.isEmpty()){
			node.remove(IStorageConstants.DISABLED_PROVIDERS_KEY);
		} else {
			node.put(IStorageConstants.DISABLED_PROVIDERS_KEY, initialValue);
		}
	}

	@Test
	public void testDefaultJMXValues() throws CoreException {
		ILaunchConfigurationType launchConfigurationType = getLaunchConfigurationType();	
		LaunchConfigurationTabGroupWrapper wrapper = createTabGroup(launchConfigurationType);
		//require to call get to initialize with extension points
		wrapper.getTabs();
		ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType.newInstance(null, getClass().getSimpleName()+".testDefaultValues");
		
		wrapper.setDefaults(workingCopy);
		
		assertThat(workingCopy.getAttribute(ICamelDebugConstants.ATTR_JMX_URI_ID, "if-this-one-the-default-value-has-not-initialized")).isEqualTo(getExpectedDefaultJMXURI());
	}

	protected ILaunchConfigurationType getLaunchConfigurationType() {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		return launchManager.getLaunchConfigurationType(getLaunchConfigId());
	}

	protected LaunchConfigurationTabGroupWrapper createTabGroup(ILaunchConfigurationType launchConfigurationType) throws CoreException {
		LaunchConfigurationTabGroupWrapper wrapper = (LaunchConfigurationTabGroupWrapper)LaunchConfigurationPresentationManager.getDefault().getTabGroup(launchConfigurationType, getLaunchMode());
		wrapper.createTabs(null, getLaunchMode());
		return wrapper;
	}
	
	protected ILaunchConfigurationTab[] getTabs() throws CoreException {
		LaunchConfigurationTabGroupWrapper wrapper = createTabGroup(getLaunchConfigurationType());
		return wrapper.getTabs();
	}

	protected abstract String getExpectedDefaultJMXURI();
	protected abstract String getLaunchConfigId();
	protected abstract String getLaunchMode();

}