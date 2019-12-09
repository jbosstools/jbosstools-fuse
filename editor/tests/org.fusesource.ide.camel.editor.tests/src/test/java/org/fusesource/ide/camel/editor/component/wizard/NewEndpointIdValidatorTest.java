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
package org.fusesource.ide.camel.editor.component.wizard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.util.Arrays;

import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NewEndpointIdValidatorTest {
	
	@Mock
	private AbstractCamelModelElement parent;
	@Mock
	private AbstractCamelModelElement anotherCamelModelElement;

	@Test
	public void testEmptyId() throws Exception {
		assertThat(new NewEndpointIdValidator(null).validate("").isOK()).isFalse();
	}
	
	@Test
	public void testNullId() throws Exception {
		assertThat(new NewEndpointIdValidator(null).validate(null).isOK()).isFalse();
	}
	
	@Test
	public void testValidId() throws Exception {
		assertThat(new NewEndpointIdValidator(parent).validate("validId").isOK()).isTrue();
	}
	
	@Test
	public void testExistingId() throws Exception {
		doReturn(Arrays.asList(new AbstractCamelModelElement[]{anotherCamelModelElement})).when(parent).findAllNodesWithId("duplicatedId");
		assertThat(new NewEndpointIdValidator(parent).validate("duplicatedId").isOK()).isFalse();
	}
	
}
