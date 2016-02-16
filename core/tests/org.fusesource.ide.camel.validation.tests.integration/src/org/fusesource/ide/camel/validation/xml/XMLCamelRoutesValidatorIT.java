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
package org.fusesource.ide.camel.validation.xml;

import org.assertj.core.api.Assertions;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.ValidationEvent;
import org.eclipse.wst.validation.ValidationState;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelEndpoint;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Node;

@RunWith(MockitoJUnitRunner.class)
public class XMLCamelRoutesValidatorIT {

	@Mock
	private IResource resource;
	@Mock
	private IProgressMonitor monitor;
	@Mock
	private IMarker marker;
	@Mock
	private Node xmlNode;

	@Spy
	private XMLCamelRoutesValidator xmlCamelRoutesValidator;

	@Test
	public void testValidate() throws Exception {
		CamelFile camelFile = new CamelFile(resource);
		CamelRouteElement route = new CamelRouteElement(new CamelContextElement(camelFile, null), null);
		camelFile.addChildElement(route);
		final CamelEndpoint endPoint = new CamelEndpoint("imap:host:port");
		route.addChildElement(endPoint);
		endPoint.setParent(route);
		endPoint.setXmlNode(xmlNode);
		Mockito.doReturn(camelFile).when(xmlCamelRoutesValidator).loadCamelFile(monitor, resource);
		Mockito.doReturn(marker).when(resource).createMarker(Mockito.anyString());
		ValidationEvent event = new ValidationEvent(resource, IResourceDelta.CHANGED, null);
		ValidationState state = new ValidationState();
		Assertions.assertThat(xmlCamelRoutesValidator.validate(event, state, monitor).getSeverityError()).isEqualTo(1);
		Mockito.verify(resource).createMarker(Mockito.anyString());
	}

}
