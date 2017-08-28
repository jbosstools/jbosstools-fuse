/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.preferences.tests;

import org.jboss.tools.foundation.core.plugin.log.IPluginLog;
import org.jboss.tools.foundation.ui.plugin.BaseUIPlugin;

public class PreferencesIntegrationTestsActivator extends BaseUIPlugin {
	
	private static PreferencesIntegrationTestsActivator instance = null;
	
	public PreferencesIntegrationTestsActivator() {
		instance = this;
	}
	
	public static PreferencesIntegrationTestsActivator getDefault() {
		if(instance == null){
			instance = new PreferencesIntegrationTestsActivator();
		}
		return instance;
	}

	public static IPluginLog pluginLog() {
		return getDefault().pluginLogInternal();
	}

}
