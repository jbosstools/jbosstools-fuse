/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.integration.properties.creators.advanced;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.editor.integration.properties.creators.AbstractParameterPropertySectionUICreatorITHelper;
import org.fusesource.ide.camel.editor.properties.creators.advanced.TextParameterPropertyUICreatorForAdvanced;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.junit.Test;

public class TextParameterPropertyUICreatorForAdvancedIT extends AbstractParameterPropertySectionUICreatorITHelper {
	
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
		camelModelElement.setParameter("uri", "");

		final TextParameterPropertyUICreatorForAdvanced textParameterPropertyUICreator = new TextParameterPropertyUICreatorForAdvanced(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory);
		textParameterPropertyUICreator.create();

		final Text control = textParameterPropertyUICreator.getControl();
		assertThat(control.getText()).as("The default value for char types are ignored.").isEqualTo("<");
		
		control.setText("!");

		assertThat(modelMap.get("testParameterName")).isEqualTo("!");
		assertThat(camelModelElement.getParameter("uri")).isEqualTo("?testParameterName=!");
	}
	
}
