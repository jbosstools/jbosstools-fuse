/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.launcher.debug;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * @author Aurelien Pupier
 *
 */
public final class MarkerNodeRemovalEventHandler implements EventHandler {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.
	 * event.Event)
	 */
	@Override
	public void handleEvent(Event event) {
		final Object cme = event.getProperty(IEventBroker.DATA);

		if (cme instanceof AbstractCamelModelElement) {
			for (AbstractCamelModelElement child : ((AbstractCamelModelElement) cme).getChildElements()) {
				clearBreakpoints(child);
			}
			clearBreakpoints((AbstractCamelModelElement) cme);
		}
	}

	/**
	 * @param child
	 */
	private void clearBreakpoints(AbstractCamelModelElement cme) {
		final IResource resource = cme.getCamelFile().getResource();
		IBreakpoint breakpoint = CamelDebugUtils.getBreakpointForSelection(cme.getId(), resource.getName(), resource.getProject().getName());
		if (breakpoint != null) {
			try {
				breakpoint.delete();
			} catch (CoreException e) {
				Activator.getLogger().error(e);
			}
		}
	}
}