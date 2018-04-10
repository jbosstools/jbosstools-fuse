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
package org.fusesource.ide.camel.validation.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.junit.Test;

public class NumberValidatorIT {

	@Test
	public void testValidateOK() throws Exception {
		final Parameter parameter = new Parameter();
		parameter.setJavaType(Integer.class.getName());
		assertThat(new NumberValidator(parameter).validate("12").isOK()).isTrue();
	}
	
	@Test
	public void testValidateOKWithEmptyConstructor() throws Exception {
		assertThat(new NumberValidator().validate("12").isOK()).isTrue();
	}

	@Test
	public void testValidateKO() throws Exception {
		final Parameter parameter = new Parameter();
		parameter.setJavaType(Integer.class.getName());
		assertThat(new NumberValidator(parameter).validate("test").isOK()).isFalse();
	}
	
	@Test
	public void testValidateKOWithEmptyConstructor() throws Exception {
		final Parameter parameter = new Parameter();
		parameter.setJavaType(Integer.class.getName());
		assertThat(new NumberValidator().validate("test").isOK()).isFalse();
	}

	@Test
	public void testIgnoreNotInteger() throws Exception {
		final Parameter parameter = new Parameter();
		parameter.setJavaType(String.class.getName());
		assertThat(new NumberValidator(parameter).validate("test").isOK()).isTrue();
	}

	@Test
	public void testIgnoreEmptyValues() throws Exception {
		final Parameter parameter = new Parameter();
		parameter.setJavaType(Integer.class.getName());
		assertThat(new NumberValidator(parameter).validate("").isOK()).isTrue();
	}
	
	@Test
	public void testIgnoreNullValues() throws Exception {
		final Parameter parameter = new Parameter();
		parameter.setJavaType(Integer.class.getName());
		assertThat(new NumberValidator(parameter).validate(null).isOK()).isTrue();
	}
	
	@Test
	public void testIgnoreNullValuesWithEmptyConstructor() throws Exception {
		assertThat(new NumberValidator().validate(null).isOK()).isTrue();
	}
	
	@Test
	public void testIgnorePropertyPlaceholder() throws Exception {
		final Parameter parameter = new Parameter();
		parameter.setJavaType(Integer.class.getName());
		assertThat(new NumberValidator(parameter).validate("{{placeholder}}").isOK()).isTrue();
	}

}
