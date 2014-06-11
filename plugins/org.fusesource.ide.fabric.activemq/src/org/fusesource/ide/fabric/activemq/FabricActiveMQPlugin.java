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

package org.fusesource.ide.fabric.activemq;


import java.util.concurrent.atomic.AtomicBoolean;

import org.fusesource.ide.commons.logging.RiderLogFacade;
import org.fusesource.ide.commons.ui.ImagesActivatorSupport;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.activemq.navigator.ActiveMQNodeProvider;
import org.fusesource.ide.fabric.activemq.navigator.ActiveMQPreferenceInitializer;
import org.osgi.framework.BundleContext;


/**
 * ActiveMQ plugin for Fabric
 */
public class FabricActiveMQPlugin extends ImagesActivatorSupport {

	private static FabricActiveMQPlugin plugin;
	private static ActiveMQNodeProvider nodeProvider;
	private static ActiveMQConverter converter;
	private static AtomicBoolean started = new AtomicBoolean(false);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public static void registerNodeProviders() {
		if (started.compareAndSet(false, true)) {
			new ActiveMQPreferenceInitializer().initializeDefaultPreferences();
			nodeProvider = new ActiveMQNodeProvider();
			FabricPlugin.addNodeProvider(nodeProvider);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		FabricPlugin.removeNodeProvider(nodeProvider);

		plugin = null;
		super.stop(context);
	}

	public static FabricActiveMQPlugin getDefault() {
		return plugin;
	}

	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}

	public static ActiveMQConverter getConverter() {
		if (converter == null) {
			converter = new ActiveMQConverter();
		}
		return converter;
	}

	public static void setConverter(ActiveMQConverter converter) {
		FabricActiveMQPlugin.converter = converter;
	}


}
