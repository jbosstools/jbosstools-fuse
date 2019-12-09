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
package org.fusesource.ide.camel.editor.properties;

import org.assertj.core.api.Assertions;
import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CamelTypeMapperTest {

	@Spy
	CamelTypeMapper camelTypeMapper;

	@Mock
	ContainerShapeEditPart editPart;

	@Test
	public void testMapType_CamelNode() throws Exception {
		Mockito.doReturn(new CamelBasicModelElement(null, null)).when(camelTypeMapper).resolveCamelModelElement(Mockito.any(ContainerShapeEditPart.class));
		Assertions.assertThat(camelTypeMapper.mapType(editPart)).isEqualTo(CamelBasicModelElement.class);
	}

	@Test
	public void testMapType_OtherStuff() throws Exception {
		Mockito.doReturn(null).when(camelTypeMapper).resolveCamelModelElement(Mockito.any());
		Assertions.assertThat(camelTypeMapper.mapType(new Object())).isEqualTo(Object.class);
	}

}
