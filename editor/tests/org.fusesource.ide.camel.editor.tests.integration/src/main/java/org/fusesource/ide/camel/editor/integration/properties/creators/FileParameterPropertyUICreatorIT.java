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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.editor.properties.creators.AbstractFileParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.advanced.FileParameterPropertyUICreatorForAdvanced;
import org.fusesource.ide.camel.editor.properties.creators.details.FileParameterPropertyUICreatorForDetails;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Aurelien Pupier
 *
 */
public class FileParameterPropertyUICreatorIT extends AbstractParameterPropertySectionUICreatorITHelper {

	private Parameter parameter;
	private Eip eip;

	@Before
	public void setup() {
		parameter = new Parameter();
		parameter.setName("testParameterName");
		parameter.setJavaType(File.class.getName());
		parameter.setKind("parameter");
		eip = new Eip();
		final Map<String, Parameter> parameters = new HashMap<>();
		parameters.put(parameter.getName(), parameter);
		eip.setProperties(parameters);
	}

	@Test
	public void testUIDisplayedForDetails() throws Exception {
		final AbstractFileParameterPropertyUICreator textParameterPropertyUICreator = new FileParameterPropertyUICreatorForDetails(dbc, modelMap, eip, camelModelElement, parameter,
				parent,
				widgetFactory);
		textParameterPropertyUICreator.create();

		final Text control = textParameterPropertyUICreator.getControl();
		control.setText("newValue");

		assertThat(modelMap.get("testParameterName")).isEqualTo("newValue");
		assertThat(camelModelElement.getParameter("testParameterName")).isEqualTo("newValue");
	}

	@Test
	public void testUIDisplayedForAdvanced() throws Exception {
		camelModelElement.setParameter("uri", "testUri?testParameterName=oldValue");
		final AbstractFileParameterPropertyUICreator textParameterPropertyUICreator = new FileParameterPropertyUICreatorForAdvanced(dbc, modelMap, eip, camelModelElement,
				parameter, parent, widgetFactory);
		textParameterPropertyUICreator.create();

		assertThat(textParameterPropertyUICreator.getInitialValue()).isEqualTo("oldValue");

		final Text control = textParameterPropertyUICreator.getControl();
		control.setText("newValue");

		assertThat(modelMap.get("testParameterName")).isEqualTo("newValue");
		assertThat(PropertiesUtils.getPropertyFromUri(camelModelElement, parameter, PropertiesUtils.getComponentFor(camelModelElement))).isEqualTo("newValue");
	}

}
