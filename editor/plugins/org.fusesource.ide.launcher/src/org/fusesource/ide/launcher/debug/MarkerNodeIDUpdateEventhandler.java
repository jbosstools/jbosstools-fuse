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
import org.eclipse.debug.core.model.IBreakpoint;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.launcher.debug.model.CamelEndpointBreakpoint;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * @author Aurelien Pupier
 *
 */
public final class MarkerNodeIDUpdateEventhandler implements EventHandler {

	@Override
	public void handleEvent(Event event) {
		String oldId = (String) event.getProperty(AbstractCamelModelElement.PROPERTY_KEY_OLD_ID);
		String newId = (String) event.getProperty(AbstractCamelModelElement.PROPERTY_KEY_NEW_ID);
		CamelFile camelFile = (CamelFile) event.getProperty(AbstractCamelModelElement.PROPERTY_KEY_CAMEL_FILE);
		updateBreakpoint(newId, oldId, camelFile);
	}

	private void updateBreakpoint(String newId, String oldId, CamelFile camelFile) {
		IBreakpoint breakpointForSelection = getBreakpoint(oldId, camelFile);
		if (breakpointForSelection != null && breakpointForSelection instanceof CamelEndpointBreakpoint) {
			((CamelEndpointBreakpoint) breakpointForSelection).updateEndpointNodeId(newId);
		}
	}

	private IBreakpoint getBreakpoint(String oldId, CamelFile camelFile) {
		final IResource resource = camelFile.getResource();
		return CamelDebugUtils.getBreakpointForSelection(oldId, resource.getName(), resource.getProject().getName());
	}
}