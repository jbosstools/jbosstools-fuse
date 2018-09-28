/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.validation.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.junit.Test;

public class NumberValidatorTest {

	@Test
	public void testIgnoreNotNumber() throws Exception {
		testOk(String.class.getName(), "test");
	}

	@Test
	public void testIgnoreEmptyValues() throws Exception {
		testOk(Integer.class.getName(), "");
	}
	
	@Test
	public void testIgnoreNullValues() throws Exception {
		testOk(Integer.class.getName(), null);
	}
	
	@Test
	public void testIgnorePropertyPlaceholder() throws Exception {
		testOk(Integer.class.getName(), "{{placeholder}}");
	}
	
	@Test
	public void testValidateOKDouble() throws Exception {
		testOk(Double.class.getName(), "0.1");
	}
	
	@Test
	public void testValidateKODouble() throws Exception {
		testKo(Double.class.getName(), "test");
	}
	
	@Test
	public void testValidateOKFloat() throws Exception {
		testOk(Float.class.getName(), "0.1");
	}
	
	@Test
	public void testValidateKOFloat() throws Exception {
		testKo(Float.class.getName(), "test");
	}
	
	private void testKo(String typeName, String value) {
		final Parameter parameter = new Parameter();
		parameter.setJavaType(typeName);
		assertThat(new NumberValidator(parameter).validate(value).isOK()).isFalse();
	}
	
	private void testOk(String javaType, String value) {
		final Parameter parameter = new Parameter();
		parameter.setJavaType(javaType);
		assertThat(new NumberValidator(parameter).validate(value).isOK()).isTrue();
	}

}
