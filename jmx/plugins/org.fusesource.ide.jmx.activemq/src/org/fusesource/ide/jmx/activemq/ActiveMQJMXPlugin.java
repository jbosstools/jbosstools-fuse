/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.activemq;


import java.util.concurrent.atomic.AtomicBoolean;

import org.fusesource.ide.foundation.ui.logging.RiderLogFacade;
import org.fusesource.ide.foundation.ui.util.ImagesActivatorSupport;
import org.fusesource.ide.jmx.activemq.navigator.ActiveMQNodeProvider;
import org.fusesource.ide.jmx.activemq.navigator.ActiveMQPreferenceInitializer;

import org.osgi.framework.BundleContext;

/**
 * ActiveMQ plugin for JMX
 */
public class ActiveMQJMXPlugin extends ImagesActivatorSupport {

	public static final String PLUGIN_ID = "org.fusesource.ide.jmx.activemq";
	private static ActiveMQJMXPlugin plugin;
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
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static ActiveMQJMXPlugin getDefault() {
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
		ActiveMQJMXPlugin.converter = converter;
	}

	/**
	 * Display a user error if an operation failed
	 */
	public static void showUserError(String title, String message, Exception e) {
		showUserError(PLUGIN_ID, getLogger(), title, message, e);
	}
}
