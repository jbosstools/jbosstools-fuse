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
package org.fusesource.ide.camel.model.service.core.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Node;

@RunWith(MockitoJUnitRunner.class)
public class AbstractCamelModelElementTest {
	
	@Mock
	private Node underlyingNode;
	@Mock
	private AbstractCamelModelElement cme;
	@Mock
	private CamelContextElement context;
	
	@Before
	public void setup() {
		doReturn("notEmptyUri").when(cme).getParameter(AbstractCamelModelElement.URI_PARAMETER_KEY);
		doCallRealMethod().when(cme).isEndpointElement();
		doCallRealMethod().when(cme).getIconName();
		doReturn(context).when(cme).getRouteContainer();
		
	}

	@Test
	public void testIsEndpointElement_returnTrueForEndpoint() throws Exception {
		doReturn("endpoint").when(cme).getNodeTypeId();
		assertThat(cme.isEndpointElement()).isTrue();
	}
	
	@Test
	public void testIsEndpointElement_returnTrueForEndpointWithNoURI() throws Exception {
		doReturn(null).when(cme).getParameter(AbstractCamelModelElement.URI_PARAMETER_KEY);
		doReturn("endpoint").when(cme).getNodeTypeId();
		
		assertThat(cme.isEndpointElement()).isTrue();
	}
	
	@Test
	public void testIsEndpointElement_returnTrueForTo() throws Exception {
		doReturn("to").when(cme).getNodeTypeId();
		assertThat(cme.isEndpointElement()).isTrue();
	}
	
	@Test
	public void testIsEndpointElement_returnTrueForFrom() throws Exception {
		doReturn("from").when(cme).getNodeTypeId();
		assertThat(cme.isEndpointElement()).isTrue();
	}
	
	@Test
	public void testIsEndpointElement_returnFalseForWireTapEvenHavingURiAttribute() throws Exception {
		doReturn("wireTap").when(cme).getNodeTypeId();
		assertThat((String)cme.getParameter(AbstractCamelModelElement.URI_PARAMETER_KEY)).isNotEmpty();
		
		assertThat(cme.isEndpointElement()).isFalse();
	}
	
	@Test
	public void testgetIconNameReturnsDefaultValueForBadRef() throws Exception {
		doReturn("to").when(cme).getNodeTypeId();
		doReturn("ref:unknow").when(cme).getParameter(AbstractCamelModelElement.URI_PARAMETER_KEY);
		
		assertThat(cme.getIconName()).isEqualTo("endpoint");
	}
	
	@Test
	public void testgetIconNameReturnsReference() throws Exception {
		doReturn("to").when(cme).getNodeTypeId();
		doReturn("ref:aRef").when(cme).getParameter(AbstractCamelModelElement.URI_PARAMETER_KEY);
		AbstractCamelModelElement referencedEndpoint = Mockito.mock(AbstractCamelModelElement.class);
		doReturn("bean:example").when(cme).getParameter(AbstractCamelModelElement.URI_PARAMETER_KEY);
		Map<String, AbstractCamelModelElement> map = new HashMap<>();
		map.put("aRef", referencedEndpoint);
		doReturn(map).when(context).getEndpointDefinitions();
		
		assertThat(cme.getIconName()).isEqualTo("bean");
	}
	
	@Test
	public void testgetIconNamehandleReferenceWithBadScheme() throws Exception {
		doReturn("to").when(cme).getNodeTypeId();
		doReturn("ref:aRef").when(cme).getParameter(AbstractCamelModelElement.URI_PARAMETER_KEY);
		AbstractCamelModelElement referencedEndpoint = Mockito.mock(AbstractCamelModelElement.class);
		doReturn("bean").when(cme).getParameter(AbstractCamelModelElement.URI_PARAMETER_KEY);
		Map<String, AbstractCamelModelElement> map = new HashMap<>();
		map.put("aRef", referencedEndpoint);
		doReturn(map).when(context).getEndpointDefinitions();
		
		assertThat(cme.getIconName()).isEqualTo("endpoint");
	}

}
