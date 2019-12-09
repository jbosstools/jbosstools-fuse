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
package org.fusesource.ide.camel.validation.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.junit.Test;

public class NumberValidatorIT {

	@Test
	public void testValidateIntegerOK() throws Exception {
		testOk(Integer.class.getName(), "12");
	}
	
	@Test
	public void testValidateIntegerDurationOK() throws Exception {
		testOk(Integer.class.getName(), "12s");
	}
	
	@Test
	public void testValidateIntegerKO() throws Exception {
		testKo(Integer.class.getName(), "test");
	}

	@Test
	public void testValidateOKLong() throws Exception {
		testOk(Long.class.getName(), "0");
	}
	
	@Test
	public void testValidateOKDurationLong() throws Exception {
		testOk(Long.class.getName(), "0s");
	}
	
	@Test
	public void testValidateKOLong() throws Exception {
		testKo(Long.class.getName(), "0.1");
	}

	private void testOk(String javaType, String value) {
		final Parameter parameter = new Parameter();
		parameter.setJavaType(javaType);
		assertThat(new NumberValidator(parameter).validate(value).isOK()).isTrue();
	}
	
	private void testKo(String typeName, String value) {
		final Parameter parameter = new Parameter();
		parameter.setJavaType(typeName);
		assertThat(new NumberValidator(parameter).validate(value).isOK()).isFalse();
	}

}
