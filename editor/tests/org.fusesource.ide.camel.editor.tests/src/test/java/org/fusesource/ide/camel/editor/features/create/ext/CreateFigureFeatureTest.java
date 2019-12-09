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
package org.fusesource.ide.camel.editor.features.create.ext;

import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelEndpoint;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class CreateFigureFeatureTest {
	@Mock
	public Eip eipTo;
	@Mock
	public Eip eipFrom;
	@Mock
	public Eip eipOther;
	
	@Before
	public void setup(){
		doReturn(AbstractCamelModelElement.ENDPOINT_TYPE_TO).when(eipTo).getName();
		doReturn("other").when(eipOther).getName();
	}

	@Test
	public void testDetermineEIP_returnConvert_To_To_From_WhenRouteHasNoChild() throws Exception {
		CreateFigureFeature createFigureFeature = spy(new CreateFigureFeature(null, null, null, eipTo));
		doReturn(eipTo).when(createFigureFeature).getEipByName(AbstractCamelModelElement.ENDPOINT_TYPE_TO);
		doReturn(eipFrom).when(createFigureFeature).getEipByName(AbstractCamelModelElement.ENDPOINT_TYPE_FROM);
		AbstractCamelModelElement cmeParent = new CamelRouteElement(null, null);
		
		assertThat(createFigureFeature.determineEIP(cmeParent)).isEqualTo(eipFrom);
	}
	
	@Test
	public void testDetermineEIP_returnKeep_OtherEip_WhenRouteHasNoChild() throws Exception {
		CreateFigureFeature createFigureFeature = spy(new CreateFigureFeature(null, null, null, eipOther));
		doReturn(eipTo).when(createFigureFeature).getEipByName(AbstractCamelModelElement.ENDPOINT_TYPE_TO);
		doReturn(eipFrom).when(createFigureFeature).getEipByName(AbstractCamelModelElement.ENDPOINT_TYPE_FROM);
		AbstractCamelModelElement cmeParent = new CamelRouteElement(null, null);
		
		assertThat(createFigureFeature.determineEIP(cmeParent)).isEqualTo(eipOther);
	}
	
	@Test
	public void testDetermineEIP_return_To_WhenRouteHasChildren() throws Exception {
		CreateFigureFeature createFigureFeature = spy(new CreateFigureFeature(null, null, null, eipTo));
		doReturn(eipTo).when(createFigureFeature).getEipByName(AbstractCamelModelElement.ENDPOINT_TYPE_TO);
		doReturn(eipFrom).when(createFigureFeature).getEipByName(AbstractCamelModelElement.ENDPOINT_TYPE_FROM);
		AbstractCamelModelElement cmeParent = new CamelRouteElement(null, null);
		cmeParent.addChildElement(new CamelEndpoint(""));
		
		assertThat(createFigureFeature.determineEIP(cmeParent)).isEqualTo(eipTo);
	}

}
