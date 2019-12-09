/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;

public class CamelJMXLaunchConfiguration {
	
	private ILaunchConfiguration launchConfiguration;
	private String defaultJMXURI;

	public CamelJMXLaunchConfiguration(ILaunchConfiguration launchConfiguration, String defaultJMXURI) {
		this.launchConfiguration = launchConfiguration;
		this.defaultJMXURI = defaultJMXURI;
	}
	
	public String getJMXUrl() throws CoreException{
		return launchConfiguration.getAttribute(ICamelDebugConstants.ATTR_JMX_URI_ID, defaultJMXURI);
	}
	
	public String getJMXUser() throws CoreException{
		return launchConfiguration.getAttribute(ICamelDebugConstants.ATTR_JMX_USER_ID, "");
	}
	
	public String getJMXPassword(){
		String password = SecureStorageUtil.getFromSecureStorage(Activator.getBundleID(), launchConfiguration, ICamelDebugConstants.ATTR_JMX_PASSWORD_ID);
		return password != null ? password : "";
	}

}
