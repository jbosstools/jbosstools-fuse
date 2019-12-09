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
package org.fusesource.ide.camel.editor.globalconfiguration;

import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Node;

import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class GlobalConfigLabelProviderTest {
	@Mock
	private CamelGlobalConfigEditor camelGlobalConfigEditor;
	@InjectMocks
	private GlobalConfigLabelProvider globalConfigLabelProvider;
	@Mock
	private Node xmlNode;
	@Mock
	private AbstractCamelModelElement cme;

	@Test
	public void testGetImage_RobustToNullParentAndNullXMLNode() throws Exception {
		globalConfigLabelProvider.getImage(new CamelBasicModelElement(null, null));
	}

	@Test
	public void testGetImage_RobustToNullParent() throws Exception {
		doReturn(xmlNode).when(cme).getXmlNode();
		doReturn("nodeName").when(xmlNode).getNodeName();
		globalConfigLabelProvider.getImage(cme);
	}

}
