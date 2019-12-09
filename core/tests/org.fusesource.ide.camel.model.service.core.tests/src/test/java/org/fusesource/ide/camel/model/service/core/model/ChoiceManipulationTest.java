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
package org.fusesource.ide.camel.model.service.core.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author lhein
 */
@RunWith(MockitoJUnitRunner.class)
public class ChoiceManipulationTest {

	@Mock
	private Eip underlyingChoiceMetaModel;
	@Mock
	private Eip underlyingOtherwiseMetaModel;
	
	@Test
	public void testAddingOtherwiseChild() throws Exception {
		doReturn("choice").when(underlyingChoiceMetaModel).getName();
		doReturn("otherwise").when(underlyingOtherwiseMetaModel).getName();

		CamelBasicModelElement choiceElement = new CamelBasicModelElement(new CamelContextElement(null, null), null);
		choiceElement.setUnderlyingMetaModelObject(underlyingChoiceMetaModel);
		choiceElement.setId("choice1");

		CamelBasicModelElement otherwiseElement = new CamelBasicModelElement(choiceElement, null);
		otherwiseElement.setUnderlyingMetaModelObject(underlyingOtherwiseMetaModel);
		otherwiseElement.setId("otherwise1");
		
		choiceElement.addChildElement(otherwiseElement);
		
		assertThat(choiceElement.getParameter(underlyingOtherwiseMetaModel.getName())).isNotNull();
	}	
}
