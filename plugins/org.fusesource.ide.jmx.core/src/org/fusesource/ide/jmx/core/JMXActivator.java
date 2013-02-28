/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    "Rob Stryker" <rob.stryker@redhat.com> - Initial implementation
 *******************************************************************************/
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

package org.fusesource.ide.jmx.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.commons.logging.RiderLogFacade;
import org.fusesource.ide.commons.ui.ImagesActivatorSupport;
import org.fusesource.ide.jmx.core.tree.NodeProvider;
import org.osgi.framework.BundleContext;


//import org.fusesource.ide.commons.logging.RiderLogFacade;

/**
 * Adding an activator where there wasn't one before
 */
public class JMXActivator extends ImagesActivatorSupport {
	public static final String PLUGIN_ID = "org.fusesource.ide.jmx.core"; //$NON-NLS-1$
	private static JMXActivator plugin;
	private static List<NodeProvider> nodeProviders = new CopyOnWriteArrayList<NodeProvider>();

	public JMXActivator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static JMXActivator getDefault() {
		return plugin;
	}

	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void log(int severity, String message, Throwable e) {
		log(new Status(severity, PLUGIN_ID, 0, message, e));
	}


	// TODO use the OSGi registry instead :)
	public static void addNodeProvider(NodeProvider nodeProvider) {
		if (!nodeProviders.contains(nodeProvider)) {
			nodeProviders.add(nodeProvider);
		}
	}

	public static void removeNodeProvider(NodeProvider nodeProvider) {
		nodeProviders.remove(nodeProvider);
	}

	public static List<NodeProvider> getNodeProviders() {
		return nodeProviders;
	}
}
