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
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Node;

import static org.mockito.Mockito.when;

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
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private Node camelContextXmlNode;

	@Test
	public void testValidate() throws Exception {
		CamelFile camelFile = createRouteWithAnEndpointContainingError();
		Mockito.doReturn("myNodeName").when(xmlNode).getNodeName();
		Mockito.doReturn(camelFile).when(xmlCamelRoutesValidator).loadCamelFile(monitor, resource);
		Mockito.doReturn(marker).when(resource).createMarker(Mockito.anyString());
		Mockito.doReturn(new IMarker[] { marker }).when(resource).findMarkers(Mockito.anyString(), Mockito.eq(true), Mockito.anyInt());
		ValidationEvent event = new ValidationEvent(resource, IResourceDelta.CHANGED, null);
		ValidationState state = new ValidationState();
		
		Assertions.assertThat(xmlCamelRoutesValidator.validate(event, state, monitor).getSeverityError()).isEqualTo(1);
		// Check marker created
		Mockito.verify(resource).createMarker(Mockito.anyString());
		
		Assertions.assertThat(xmlCamelRoutesValidator.validate(event, state, monitor).getSeverityError()).isEqualTo(1);
		// Check marker deleted and recreated
		Mockito.verify(marker).delete();
		Mockito.verify(resource, Mockito.times(2)).createMarker(Mockito.anyString());
	}

	/**
	 * @return
	 */
	private CamelFile createRouteWithAnEndpointContainingError() {
		CamelFile camelFile = new CamelFile(resource);
		CamelRouteElement route = new CamelRouteElement(new CamelContextElement(camelFile, null), null);
		camelFile.addChildElement(route);
		final CamelEndpoint endPoint = new CamelEndpoint("imap:host:port");
		route.addChildElement(endPoint);
		endPoint.setParent(route);
		endPoint.setXmlNode(xmlNode);
		return camelFile;
	}

	@Test
	public void testValidateOnGlobalEndpoints() throws Exception {
		CamelFile camelFile = new CamelFile(resource);
		final CamelContextElement camelContext = new CamelContextElement(camelFile, null);
		when(camelContextXmlNode.getChildNodes().getLength()).thenReturn(0);
		camelContext.setXmlNode(camelContextXmlNode);
		final CamelEndpoint endPoint = new CamelEndpoint("imap:host:port");
		endPoint.setParent(camelContext);
		endPoint.setXmlNode(xmlNode);
		camelContext.addEndpointDefinition(endPoint);
		camelFile.addChildElement(camelContext);
		Mockito.doReturn("myNodeName").when(xmlNode).getNodeName();
		Mockito.doReturn(camelFile).when(xmlCamelRoutesValidator).loadCamelFile(monitor, resource);
		Mockito.doReturn(marker).when(resource).createMarker(Mockito.anyString());
		Mockito.doReturn(new IMarker[] { marker }).when(resource).findMarkers(Mockito.anyString(), Mockito.eq(true), Mockito.anyInt());
		ValidationEvent event = new ValidationEvent(resource, IResourceDelta.CHANGED, null);
		ValidationState state = new ValidationState();

		Assertions.assertThat(xmlCamelRoutesValidator.validate(event, state, monitor).getSeverityError()).isEqualTo(1);
		// Check marker created
		Mockito.verify(resource).createMarker(Mockito.anyString());

		Assertions.assertThat(xmlCamelRoutesValidator.validate(event, state, monitor).getSeverityError()).isEqualTo(1);
		// Check marker deleted and recreated
		Mockito.verify(marker).delete();
		Mockito.verify(resource, Mockito.times(2)).createMarker(Mockito.anyString());
	}

}
