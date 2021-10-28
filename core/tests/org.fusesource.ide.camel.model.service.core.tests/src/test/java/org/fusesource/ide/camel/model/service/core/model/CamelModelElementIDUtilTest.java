/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.model;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.w3c.dom.Node;

@RunWith(MockitoJUnitRunner.class)
public class CamelModelElementIDUtilTest {
	
	private static final String CME_NAME = "cmeName";
	private static final String ID_TO_BE_RETURNED = "aNewId";
	@Mock
	private Node underlyingNode;
	@Mock
	private AbstractCamelModelElement cme;
	@Mock
	private AbstractCamelModelElement parentCme;
	@Mock
	private Eip eip;
	
	@Before
	public void setUp() throws Exception {
		doReturn(parentCme).when(cme).getParent();
		doReturn(ID_TO_BE_RETURNED).when(cme).getNewID();
		doReturn(CME_NAME).when(cme).getName();
	}

	@Test
	public void testEnsureUniqueID() throws Exception {
		doReturn(eip).when(cme).getUnderlyingMetaModelObject();
		
		new CamelModelElementIDUtil().ensureUniqueID(cme);
		
		verify(cme).setId(ID_TO_BE_RETURNED);
	}
	
	@Test
	public void testEnsureUniqueID_setNoIdWhenNoEip() throws Exception {
		doReturn(null).when(cme).getUnderlyingMetaModelObject();
		
		new CamelModelElementIDUtil().ensureUniqueID(cme);
		
		verify(cme, never()).setId(Mockito.anyString());
	}
	
	@Test
	public void testEnsureUniqueID_setNoIdWhenParentElementParameter() throws Exception {
		doReturn(eip).when(cme).getUnderlyingMetaModelObject();
		doReturn(eip).when(parentCme).getUnderlyingMetaModelObject();
		doReturn(CME_NAME).when(cme).getTagNameWithoutPrefix();
		Parameter parameter = new Parameter();
		parameter.setKind(AbstractCamelModelElement.NODE_KIND_ELEMENT);
		doReturn(parameter).when(eip).getParameter(CME_NAME);
		doReturn(parameter).when(parentCme).getParameter(CME_NAME);
		
		new CamelModelElementIDUtil().ensureUniqueID(cme);
		
		verify(cme, never()).setId(Mockito.anyString());
	}

}
