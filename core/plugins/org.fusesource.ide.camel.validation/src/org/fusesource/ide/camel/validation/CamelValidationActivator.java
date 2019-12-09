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
package org.fusesource.ide.camel.validation;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.validation.diagram.BasicNodeValidator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CamelValidationActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.fusesource.ide.camel.validation"; //$NON-NLS-1$

	// The shared instance
	private static CamelValidationActivator plugin;

	private ClearValidationMarkerOnRemoveEventHandler eventHandler = null;
	
	/**
	 * The constructor
	 */
	public CamelValidationActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		IEventBroker eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
		if(eventBroker != null){
			eventHandler = new ClearValidationMarkerOnRemoveEventHandler(new BasicNodeValidator());
			eventBroker.subscribe(AbstractCamelModelElement.TOPIC_REMOVE_CAMEL_ELEMENT, eventHandler);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		IEventBroker eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
		if (eventHandler != null && eventBroker != null) {
			eventBroker.unsubscribe(eventHandler);
		}
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static CamelValidationActivator getDefault() {
		return plugin;
	}

}
