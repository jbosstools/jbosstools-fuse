/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.ui.internal.localjmx;


import org.fusesource.ide.commons.logging.RiderLogFacade;
import org.fusesource.ide.commons.ui.ImagesActivatorSupport;
import org.fusesource.ide.jmx.ui.JMXUIActivator;
import org.fusesource.ide.jvmmonitor.core.JvmModel;
import org.osgi.framework.BundleContext;


public class Activator extends ImagesActivatorSupport {

	private static Activator plugin;
	private static LocalJmxNodeProvider nodeProvider;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		// prefill the process info storage to save time
		JvmConnectionWrapper.refreshProcessInformationStore();
	}

	public static void registerNodeProviders() {
		// lets start early loading of the model
		JvmModel model = JvmModel.getInstance();

		nodeProvider = new LocalJmxNodeProvider();
		JMXUIActivator.addRootJmxNodeProvider(nodeProvider);
	}	

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		JMXUIActivator.removeRootJmxNodeProvider(nodeProvider);

		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}
}
