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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.ValidationEvent;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelEndpoint;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Element;
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
	private Element xmlNode;

	@Spy
	private XMLCamelRoutesValidator xmlCamelRoutesValidator;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private Node camelContextXmlNode;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private Element unmarshalNode;
	@Mock
	private Element jaxbXmlNode;

	@Test
	public void testValidate() throws Exception {
		CamelFile camelFile = createRouteWithAnEndpointContainingError();
		doReturn("myNodeName").when(xmlNode).getNodeName();
		doReturn(camelFile).when(xmlCamelRoutesValidator).loadCamelFile(monitor, resource);
		doReturn(marker).when(resource).createMarker(Mockito.anyString());
		doReturn(new IMarker[] { marker }).when(resource).findMarkers(Mockito.anyString(), Mockito.eq(true), Mockito.anyInt());
		ValidationEvent event = new ValidationEvent(resource, IResourceDelta.CHANGED, null);
		ValidationState state = new ValidationState();
		
		Assertions.assertThat(xmlCamelRoutesValidator.validate(event, state, monitor).getSeverityError()).isEqualTo(1);
		// Check marker created
		Mockito.verify(resource).createMarker(Mockito.anyString());
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
		doReturn("myNodeName").when(xmlNode).getNodeName();
		doReturn(camelFile).when(xmlCamelRoutesValidator).loadCamelFile(monitor, resource);
		doReturn(marker).when(resource).createMarker(Mockito.anyString());
		doReturn(new IMarker[] { marker }).when(resource).findMarkers(Mockito.anyString(), Mockito.eq(true), Mockito.anyInt());
		ValidationEvent event = new ValidationEvent(resource, IResourceDelta.CHANGED, null);
		ValidationState state = new ValidationState();

		Assertions.assertThat(xmlCamelRoutesValidator.validate(event, state, monitor).getSeverityError()).isEqualTo(1);
		// Check marker created
		Mockito.verify(resource).createMarker(Mockito.anyString());
	}

	@Test
	public void testValidateUnMarshallNodesMissingChild() throws Exception {
		CamelFile camelFile = createRouteWithUnMarshalNodeWithNoChild();
		doReturn("myNodeName").when(xmlNode).getNodeName();
		doReturn(camelFile).when(xmlCamelRoutesValidator).loadCamelFile(monitor, resource);
		doReturn(marker).when(resource).createMarker(Mockito.anyString());
		doReturn(new IMarker[] { marker }).when(resource).findMarkers(Mockito.anyString(), Mockito.eq(true), Mockito.anyInt());
		ValidationEvent event = new ValidationEvent(resource, IResourceDelta.CHANGED, null);
		ValidationState state = new ValidationState();

		assertThat(xmlCamelRoutesValidator.validate(event, state, monitor).getSeverityWarning()).isEqualTo(1);
		// Check marker created
		verify(resource).createMarker(Mockito.anyString());

	}

	private CamelFile createRouteWithUnMarshalNodeWithNoChild() {
		CamelFile camelFile = new CamelFile(resource);
		CamelRouteElement route = new CamelRouteElement(new CamelContextElement(camelFile, null), null);
		camelFile.addChildElement(route);
		CamelBasicModelElement unmarshall = new CamelBasicModelElement(route, null);
		doReturn("unmarshal").when(unmarshalNode).getNodeName();
		unmarshall.setXmlNode(unmarshalNode);
		CamelModel camelModel = CamelCatalogCacheManager.getInstance().getCamelModelForVersion(CamelCatalogUtils.getLatestCamelVersion());

		unmarshall.setUnderlyingMetaModelObject(camelModel.getEip("unmarshal"));
		unmarshall.setParent(route);

		route.addChildElement(unmarshall);
		return camelFile;
	}

	@Test
	public void testValidateUnMarshallNodesWithoutError() throws Exception {
		CamelFile camelFile = createRouteWithUnMarshalNodeWithoutError();
		doReturn("myNodeName").when(xmlNode).getNodeName();
		doReturn(camelFile).when(xmlCamelRoutesValidator).loadCamelFile(monitor, resource);
		doReturn(marker).when(resource).createMarker(Mockito.anyString());
		doReturn(new IMarker[] { marker }).when(resource).findMarkers(Mockito.anyString(), Mockito.eq(true), Mockito.anyInt());
		ValidationEvent event = new ValidationEvent(resource, IResourceDelta.CHANGED, null);
		ValidationState state = new ValidationState();

		final ValidationResult validationResult = xmlCamelRoutesValidator.validate(event, state, monitor);

		assertThat(validationResult.getSeverityError()).isEqualTo(0);
		Mockito.verify(resource, never()).createMarker(Mockito.anyString());
	}

	private CamelFile createRouteWithUnMarshalNodeWithoutError() {
		CamelFile camelFile = new CamelFile(resource);
		CamelRouteElement route = new CamelRouteElement(new CamelContextElement(camelFile, null), null);
		camelFile.addChildElement(route);
		CamelBasicModelElement unmarshall = new CamelBasicModelElement(route, null);
		doReturn("unmarshal").when(unmarshalNode).getNodeName();
		unmarshall.setXmlNode(unmarshalNode);
		doReturn("jaxb").when(jaxbXmlNode).getNodeName();
		CamelBasicModelElement jaxBElement = new CamelBasicModelElement(unmarshall, jaxbXmlNode);
		jaxBElement.setXmlNode(jaxbXmlNode);
		unmarshall.setParameter("dataFormatType", jaxBElement);
		CamelModel camelModel = CamelCatalogCacheManager.getInstance().getCamelModelForVersion(CamelCatalogUtils.getLatestCamelVersion());

		unmarshall.setUnderlyingMetaModelObject(camelModel.getEip("unmarshal"));
		unmarshall.setParent(route);

		route.addChildElement(unmarshall);
		return camelFile;
	}

	@Test
	public void testValidateUnMarshallNodesWithRefSettedReturnsError() throws Exception {
		CamelFile camelFile = createRouteWithUnMarshalNodeWithrefAlsoSet();
		doReturn("myNodeName").when(xmlNode).getNodeName();
		doReturn(camelFile).when(xmlCamelRoutesValidator).loadCamelFile(monitor, resource);
		doReturn(marker).when(resource).createMarker(Mockito.anyString());
		doReturn(new IMarker[] { marker }).when(resource).findMarkers(Mockito.anyString(), Mockito.eq(true), Mockito.anyInt());
		ValidationEvent event = new ValidationEvent(resource, IResourceDelta.CHANGED, null);
		ValidationState state = new ValidationState();

		final ValidationResult validationResult = xmlCamelRoutesValidator.validate(event, state, monitor);

		Assertions.assertThat(validationResult.getSeverityError()).isEqualTo(2);
		Mockito.verify(resource, times(2)).createMarker(Mockito.anyString());
	}

	private CamelFile createRouteWithUnMarshalNodeWithrefAlsoSet() {
		CamelFile camelFile = new CamelFile(resource);
		CamelRouteElement route = new CamelRouteElement(new CamelContextElement(camelFile, null), null);
		camelFile.addChildElement(route);
		CamelBasicModelElement unmarshall = new CamelBasicModelElement(route, null);
		doReturn("unmarshal").when(unmarshalNode).getNodeName();
		unmarshall.setXmlNode(unmarshalNode);
		doReturn("jaxb").when(jaxbXmlNode).getNodeName();
		CamelBasicModelElement jaxBElement = new CamelBasicModelElement(unmarshall, jaxbXmlNode);
		jaxBElement.setXmlNode(jaxbXmlNode);
		unmarshall.setParameter("dataFormatType", jaxBElement);
		unmarshall.setParameter("ref", "aRefValue");
		CamelModel camelModel = CamelCatalogCacheManager.getInstance().getCamelModelForVersion(CamelCatalogUtils.getLatestCamelVersion());

		unmarshall.setUnderlyingMetaModelObject(camelModel.getEip("unmarshal"));
		unmarshall.setParent(route);

		route.addChildElement(unmarshall);
		return camelFile;
	}
	
	@Test
	public void testValidateRefMissingReportsWarning() throws Exception {
		doReturn(AbstractCamelModelElement.ENDPOINT_TYPE_TO).when(xmlNode).getNodeName();
		CamelFile camelFile = createRouteWithEndpointMissingRef();
		doReturn(camelFile).when(xmlCamelRoutesValidator).loadCamelFile(monitor, resource);
		doReturn(marker).when(resource).createMarker(Mockito.anyString());
		doReturn(new IMarker[] { marker }).when(resource).findMarkers(Mockito.anyString(), Mockito.eq(true), Mockito.anyInt());
		
		ValidationEvent event = new ValidationEvent(resource, IResourceDelta.CHANGED, null);
		ValidationState state = new ValidationState();
		
		final ValidationResult validationResult = xmlCamelRoutesValidator.validate(event, state, monitor);
		assertThat(validationResult.getSeverityError()).isEqualTo(0);
		assertThat(validationResult.getSeverityWarning()).isEqualTo(1);
		// Check marker created
		verify(resource).createMarker(Mockito.anyString());
	}

	private CamelFile createRouteWithEndpointMissingRef() {
		CamelFile camelFile = new CamelFile(resource);
		CamelRouteElement route = new CamelRouteElement(new CamelContextElement(camelFile, null), null);
		camelFile.addChildElement(route);
		final CamelBasicModelElement cme = new CamelBasicModelElement(route, xmlNode);
		route.addChildElement(cme);
		cme.setParameter("uri", "ref:notExist");
		cme.setParent(route);
		cme.setXmlNode(xmlNode);
		return camelFile;
	}

}
