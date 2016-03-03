/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.fusesource.ide.camel.model.service.core.debug.model.CamelEndpointBreakpoint;
import org.fusesource.ide.camel.model.service.core.debug.util.ICamelDebugConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Element;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AbstractCamelModelElementTest {
	@Mock
	private AbstractCamelModelElement parent;
	@Mock
	private Element xmlNode;
	@Mock
	private CamelEndpointBreakpoint breakpoint;
	@Mock
	private IMarker marker;

	private CamelEndpoint cme = spy(new CamelEndpoint("scheme:id=currentValue"));

	@Before
	public void setup() {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("id", "idToUpdate");
		parameters.put("toRemove", "valueToRemove");
		cme.setParameters(parameters);
		cme.setXmlNode(xmlNode);
		doReturn("attribute").when(cme).getKind(Mockito.anyString());
		doReturn(breakpoint).when(cme).getBreakpoint(Mockito.anyString());
		doCallRealMethod().when(breakpoint).updateEndpointNodeId(Mockito.anyString());
	}

	@Test
	public void testSetParameter_UpdateCallUpdateBreakpoint() throws Exception {
		doReturn(marker).when(breakpoint).getMarker();
		cme.setParameter("id", "newIdValue");
		verify(xmlNode).setAttribute("id", "newIdValue");
		verify(cme).updateBreakpoint("newIdValue", "idToUpdate");
		verify(marker).setAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_ENDPOINTID, "newIdValue");
	}

	@Test
	public void testSetParameter_AttributeRemoving() throws Exception {
		doReturn(true).when(xmlNode).hasAttribute("toRemove");
		cme.setParameter("toRemove", null);
		verify(xmlNode).removeAttribute("toRemove");
	}

	@Test
	public void testSetParameter_AttributeRemovingWhenDefaultValue() throws Exception {
		doReturn(true).when(xmlNode).hasAttribute("toRemove");
		doReturn("defaultValueToRemove").when(cme).getDefaultvalue("toRemove");
		cme.setParameter("toRemove", "defaultValueToRemove");
		verify(xmlNode).removeAttribute("toRemove");
	}

}
