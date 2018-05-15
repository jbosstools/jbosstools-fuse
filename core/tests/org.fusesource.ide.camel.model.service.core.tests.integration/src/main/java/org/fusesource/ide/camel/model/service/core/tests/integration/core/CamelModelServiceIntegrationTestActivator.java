/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.tests.integration.core;

import org.jboss.tools.common.log.BaseUIPlugin;
import org.osgi.framework.BundleContext;

public class CamelModelServiceIntegrationTestActivator extends BaseUIPlugin {
	
	private static CamelModelServiceIntegrationTestActivator plugin;
	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		setInstance(this);
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		setInstance(null);
		super.stop(context);
	}
	
	private static synchronized void setInstance(CamelModelServiceIntegrationTestActivator camelModelServiceIntegrationTestActivator) {
		plugin = camelModelServiceIntegrationTestActivator;
	}
	
	public static CamelModelServiceIntegrationTestActivator getDefault() {
		return plugin;
	}
	
}
