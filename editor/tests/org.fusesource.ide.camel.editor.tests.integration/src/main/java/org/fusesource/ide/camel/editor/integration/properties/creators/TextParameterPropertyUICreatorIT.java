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
package org.fusesource.ide.camel.editor.integration.properties.creators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.editor.properties.creators.TextParameterPropertyUICreator;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.junit.Test;

/**
 * @author Aurelien Pupier
 *
 */
public class TextParameterPropertyUICreatorIT extends AbstractParameterPropertySectionUICreatorITHelper {
	
	private boolean extraValidationCalled = false;

	@Test
	public void testUIDisplayed() throws Exception {
		Parameter parameter = new Parameter();
		parameter.setName("testParameterName");
		parameter.setKind("parameter");
		Eip eip = new Eip();
		final Map<String, Parameter> parameters = new HashMap<>();
		parameters.put(parameter.getName(), parameter);
		eip.setProperties(parameters);

		final TextParameterPropertyUICreator textParameterPropertyUICreator = new TextParameterPropertyUICreator(dbc, modelMap, eip, camelModelElement, parameter, null, parent,
				widgetFactory);
		textParameterPropertyUICreator.create();

		final Text control = textParameterPropertyUICreator.getControl();
		control.setText("newValue");

		assertThat(modelMap.get("testParameterName")).isEqualTo("newValue");
		assertThat(camelModelElement.getParameter("testParameterName")).isEqualTo("newValue");
	}
	
	@Test
	public void testUIDisplayedForChar() throws Exception {
		Parameter parameter = new Parameter();
		parameter.setName("testParameterName");
		parameter.setKind("parameter");
		parameter.setDefaultValue("<");
		parameter.setJavaType("char");
		Eip eip = new Eip();
		final Map<String, Parameter> parameters = new HashMap<>();
		parameters.put(parameter.getName(), parameter);
		eip.setProperties(parameters);

		final TextParameterPropertyUICreator textParameterPropertyUICreator = new TextParameterPropertyUICreator(dbc, modelMap, eip, camelModelElement, parameter, null, parent, widgetFactory);
		textParameterPropertyUICreator.create();

		final Text control = textParameterPropertyUICreator.getControl();
		assertThat(control.getText()).isEqualTo("<");
		
		control.setText(">");

		assertThat(modelMap.get("testParameterName")).isEqualTo(">");
		assertThat(camelModelElement.getParameter("testParameterName")).isEqualTo(">");
	}
	
	@Test
	public void testExtraValidator() throws Exception {
		extraValidationCalled = false;
		
		Parameter parameter = new Parameter();
		parameter.setName("testParameterName");
		parameter.setKind("parameter");
		Eip eip = new Eip();
		final Map<String, Parameter> parameters = new HashMap<>();
		parameters.put(parameter.getName(), parameter);
		eip.setProperties(parameters);

		final TextParameterPropertyUICreator textParameterPropertyUICreator = new TextParameterPropertyUICreator(dbc, modelMap, eip, camelModelElement, parameter, new IValidator() {
			
			@Override
			public IStatus validate(Object value) {
				extraValidationCalled = true;
				return ValidationStatus.ok();
			}
		}, parent,
				widgetFactory);
		textParameterPropertyUICreator.create();

		final Text control = textParameterPropertyUICreator.getControl();
		control.setText("newValue");
		
		assertThat(modelMap.get("testParameterName")).isEqualTo("newValue");
		assertThat(camelModelElement.getParameter("testParameterName")).isEqualTo("newValue");
		assertThat(extraValidationCalled).isTrue();
	}

}
