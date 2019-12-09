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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.editor.properties.creators.AbstractNumberParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.advanced.NumberParameterPropertyUICreatorForAdvanced;
import org.fusesource.ide.camel.editor.properties.creators.details.NumberParameterPropertyUICreatorForDetails;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Aurelien Pupier
 *
 */
public class NumberParameterPropertyUICreatorIT extends AbstractParameterPropertySectionUICreatorITHelper {

	private Parameter parameter;
	private Eip eip;

	@Before
	public void setup() {
		parameter = new Parameter();
		parameter.setName("testParameterName");
		parameter.setJavaType(Integer.class.getName());
		parameter.setType("integer");
		parameter.setKind("parameter");
		eip = new Eip();
		final Map<String, Parameter> parameters = new HashMap<>();
		parameters.put(parameter.getName(), parameter);
		eip.setProperties(parameters);
	}

	@Test
	public void testUIDisplayedForDetails() throws Exception {
		final AbstractNumberParameterPropertyUICreator numberParameterPropertyUICreator = new NumberParameterPropertyUICreatorForDetails(dbc, modelMap, eip, camelModelElement,
				parameter, parent,
				widgetFactory);
		numberParameterPropertyUICreator.create();

		final Text control = numberParameterPropertyUICreator.getControl();
		control.setText("2");

		assertThat(modelMap.get("testParameterName")).isEqualTo("2");
		assertThat(camelModelElement.getParameter("testParameterName")).isEqualTo("2");
	}

	@Test
	public void testUIDisplayedForAdvanced() throws Exception {
		camelModelElement.setParameter("uri", "test");
		final AbstractNumberParameterPropertyUICreator numberParameterPropertyUICreator = new NumberParameterPropertyUICreatorForAdvanced(dbc, modelMap, eip, camelModelElement,
				parameter, parent,
				widgetFactory);
		numberParameterPropertyUICreator.create();

		final Text control = numberParameterPropertyUICreator.getControl();
		control.setText("2");

		assertThat(modelMap.get("testParameterName")).isEqualTo("2");
		assertThat(PropertiesUtils.getTypedPropertyFromUri(camelModelElement, parameter, PropertiesUtils.getComponentFor(camelModelElement))).isEqualTo("2");
	}
	
	@Test
	public void testBackgroundDisplayedForIntegerParameter() throws Exception {
		final AbstractNumberParameterPropertyUICreator numberParameterPropertyUICreator = new NumberParameterPropertyUICreatorForDetails(
				dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory);
		numberParameterPropertyUICreator.create();
		final Text control = numberParameterPropertyUICreator.getControl();
		assertThat(control.getBackground()).isNotEqualTo(ColorConstants.red);
		control.setText("Invalid");
		assertThat(control.getBackground()).isEqualTo(ColorConstants.red);
	}
}
