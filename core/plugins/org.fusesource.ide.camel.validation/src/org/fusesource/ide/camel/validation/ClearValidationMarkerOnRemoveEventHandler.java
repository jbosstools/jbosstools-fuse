/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.validation;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.validation.diagram.BasicNodeValidator;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * @author Aurelien Pupier
 *
 */
public class ClearValidationMarkerOnRemoveEventHandler implements EventHandler {

	private BasicNodeValidator basicNodeValidator;

	public ClearValidationMarkerOnRemoveEventHandler(BasicNodeValidator basicNodeValidator) {
		this.basicNodeValidator = basicNodeValidator;
	}

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
			clearMarkers((AbstractCamelModelElement) cme);
		}
	}

	/**
	 * @param cme
	 *            The Camel Model Element for which we need to clear its markers
	 *            and its children markers
	 */
	private void clearMarkers(final AbstractCamelModelElement cme) {
		for (AbstractCamelModelElement child : cme.getChildElements()) {
			clearMarkers(child);
		}
		basicNodeValidator.clearMarkers(cme);
	}
}
