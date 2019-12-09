/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.diagram.view;

import org.fusesource.ide.foundation.ui.logging.RiderLogFacade;
import org.fusesource.ide.foundation.ui.util.ImagesActivatorSupport;
import org.osgi.framework.BundleContext;

public class JMXDiagramViewActivator extends ImagesActivatorSupport {

	private static JMXDiagramViewActivator plugin;
	public static final String PLUGIN_ID = "org.fusesource.ide.jmx.diagram.view";

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static JMXDiagramViewActivator getDefault() {
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}

}
