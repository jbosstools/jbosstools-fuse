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
package org.fusesource.ide.graph;

import org.fusesource.ide.foundation.ui.logging.RiderLogFacade;
import org.fusesource.ide.foundation.ui.util.ImagesActivatorSupport;
import org.osgi.framework.BundleContext;

/**
 * @author lhein
 */
public class Activator extends ImagesActivatorSupport {

	public static final String PLUGIN_ID = "org.fusesource.ide.graph";

	private static Activator instance;
	
	@Override
	public void start(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		super.start(context);
		instance = this;
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		instance = null;
		super.stop(context);
	}
	
	public static ImagesActivatorSupport getDefault() {
		return instance;
	}

	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}
}
