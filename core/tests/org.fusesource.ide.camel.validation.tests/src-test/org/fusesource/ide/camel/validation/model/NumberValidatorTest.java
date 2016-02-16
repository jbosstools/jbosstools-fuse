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

import org.assertj.core.api.Assertions;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.junit.Test;

public class NumberValidatorTest {

	@Test
	public void testValidateOK() throws Exception {
		final Parameter parameter = new Parameter();
		parameter.setJavaType(Integer.class.getName());
		Assertions.assertThat(new NumberValidator(parameter).validate("12").isOK()).isTrue();
	}

	@Test
	public void testValidateKO() throws Exception {
		final Parameter parameter = new Parameter();
		parameter.setJavaType(Integer.class.getName());
		Assertions.assertThat(new NumberValidator(parameter).validate("test").isOK()).isFalse();
	}

	@Test
	public void testIgnoreNotInteger() throws Exception {
		final Parameter parameter = new Parameter();
		parameter.setJavaType(String.class.getName());
		Assertions.assertThat(new NumberValidator(parameter).validate("test").isOK()).isTrue();
	}

	@Test
	public void testIgnoreEmptyValues() throws Exception {
		final Parameter parameter = new Parameter();
		parameter.setJavaType(Integer.class.getName());
		Assertions.assertThat(new NumberValidator(parameter).validate("").isOK()).isTrue();
	}

}
