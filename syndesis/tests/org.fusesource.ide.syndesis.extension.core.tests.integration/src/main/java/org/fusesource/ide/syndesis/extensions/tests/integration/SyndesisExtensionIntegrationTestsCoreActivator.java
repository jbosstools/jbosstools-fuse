/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.tests.integration;

import org.jboss.tools.foundation.core.plugin.log.IPluginLog;
import org.jboss.tools.foundation.ui.plugin.BaseUIPlugin;

public class SyndesisExtensionIntegrationTestsCoreActivator extends BaseUIPlugin {
	
	private static SyndesisExtensionIntegrationTestsCoreActivator instance = null;
	
	public SyndesisExtensionIntegrationTestsCoreActivator() {
		instance = this;
	}
	
	public static SyndesisExtensionIntegrationTestsCoreActivator getDefault() {
		if(instance == null){
			instance = new SyndesisExtensionIntegrationTestsCoreActivator();
		}
		return instance;
	}

	public static IPluginLog pluginLog() {
		return getDefault().pluginLogInternal();
	}

}
