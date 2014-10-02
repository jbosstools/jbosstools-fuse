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

package org.fusesource.ide.camel.model;

import java.net.URL;

import org.fusesource.ide.camel.model.connectors.ConnectorModelFactory;
import org.fusesource.ide.commons.camel.tools.CamelNamespaces;
import org.fusesource.ide.commons.camel.tools.SchemaFinder;
import org.fusesource.ide.commons.camel.tools.XsdDetails;
import org.fusesource.ide.commons.logging.RiderLogFacade;
import org.fusesource.ide.commons.ui.ImagesActivatorSupport;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author lhein
 */
public class Activator extends ImagesActivatorSupport {

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		// initialize the connector models
		ConnectorModelFactory.initializeModels();
	
		// 
		CamelNamespaces.loadSchemasWith(new SchemaFinder() {

			@Override
			public URL findSchema(XsdDetails xsd) {
				String path = xsd.getPath();
				URL answer = null;
				Bundle[] bundles = Activator.getDefault().getBundle().getBundleContext().getBundles();
				for (Bundle bundle : bundles) {
					answer = bundle.getResource(path);
					if (answer != null) {
						break;
					}
				}
				//Activator.getLogger().debug("for path: " + path + " xsd " + xsd + " found: " + answer);
				return answer;
			}
		});
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

	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
}
