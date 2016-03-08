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

package org.fusesource.ide.launcher;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.foundation.ui.logging.RiderLogFacade;
import org.fusesource.ide.launcher.debug.MarkerNodeIDUpdateEventhandler;
import org.osgi.framework.BundleContext;

/**
 * @author lhein
 */
public class Activator extends Plugin {

	private static Activator instance;
	private MarkerNodeIDUpdateEventhandler eventHandler = null;
	
	public Activator() {
		instance = this;
	}
	
	public static String getBundleID() {
		return instance.getBundle().getSymbolicName();
	}
	
	public static Activator getInstance() {
		return instance;
	}
	
	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(instance.getLog());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		IEventBroker eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
		eventHandler = new MarkerNodeIDUpdateEventhandler();
		eventBroker.subscribe(AbstractCamelModelElement.TOPIC_ID_RENAMING, eventHandler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		if (eventHandler != null) {
			IEventBroker eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
			eventBroker.unsubscribe(eventHandler);
			eventHandler = null;
		}
		super.stop(context);
	}
}
