package org.fusesource.ide.camel.validation.model;

import org.assertj.core.api.Assertions;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.junit.Test;

/**
 * @author Aurelien Pupier
 *
 */
public class RequiredPropertyValidatorTest {

	@Test
	public void testValidateKO_EmptyValue() throws Exception {
		Parameter parameter = new Parameter();
		parameter.setRequired("true");
		parameter.setType(String.class.getName());
		Assertions.assertThat(new RequiredPropertyValidator(parameter).validate("").isOK()).isFalse();
	}

	@Test
	public void testValidateKO_NullValue() throws Exception {
		Parameter parameter = new Parameter();
		parameter.setRequired("true");
		parameter.setType(String.class.getName());
		Assertions.assertThat(new RequiredPropertyValidator(parameter).validate(null).isOK()).isFalse();
	}

	@Test
	public void testValidateOK() throws Exception {
		Parameter parameter = new Parameter();
		parameter.setRequired("true");
		parameter.setType(Integer.class.getName());
		Assertions.assertThat(new RequiredPropertyValidator(parameter).validate("12").isOK()).isTrue();
	}

	@Test
	public void testValidateOK_whenNotRequired() throws Exception {
		Parameter parameter = new Parameter();
		parameter.setRequired("false");
		parameter.setType(String.class.getName());
		Assertions.assertThat(new RequiredPropertyValidator(parameter).validate("").isOK()).isTrue();
	}

	@Test
	public void testValidateOK_whenNotRequiredFierldSet() throws Exception {
		Parameter parameter = new Parameter();
		parameter.setType(String.class.getName());
		Assertions.assertThat(new RequiredPropertyValidator(parameter).validate(null).isOK()).isTrue();
	}

}
