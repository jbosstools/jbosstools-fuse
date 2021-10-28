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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelEndpoint;
import org.fusesource.ide.camel.validation.diagram.BasicNodeValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.service.event.Event;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ClearValidationMarkerOnRemoveEventHandlerTest {

	@Mock
	private BasicNodeValidator basicNodeValidator;

	@Test
	public void testClearMarkersRecursivity() throws Exception {
		Map<String, Object> eventMap = new HashMap<>();
		AbstractCamelModelElement cme = new CamelEndpoint("plop");
		final CamelEndpoint child = new CamelEndpoint("child");
		cme.addChildElement(child);
		eventMap.put(IEventBroker.DATA, cme);

		new ClearValidationMarkerOnRemoveEventHandler(basicNodeValidator).handleEvent(new Event(AbstractCamelModelElement.TOPIC_REMOVE_CAMEL_ELEMENT, eventMap));

		verify(basicNodeValidator).clearMarkers(cme);
		verify(basicNodeValidator).clearMarkers(child);
	}

}
