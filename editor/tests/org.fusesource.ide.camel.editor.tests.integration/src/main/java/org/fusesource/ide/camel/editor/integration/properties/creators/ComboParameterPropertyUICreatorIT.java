/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.integration.properties.creators;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Combo;
import org.fusesource.ide.camel.editor.properties.creators.ComboParameterPropertyUICreator;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.junit.Test;

/**
 * @author brianf
 *
 */
public class ComboParameterPropertyUICreatorIT extends AbstractParameterPropertySectionUICreatorITHelper {

	@Test
	public void testUIDisplayed() throws Exception {
		Parameter parameter = new Parameter();
		parameter.setName("testParameterName");
		parameter.setKind("parameter");
		Eip eip = new Eip();
		final ArrayList<Parameter> parameters = new ArrayList<>();
		parameters.add(parameter);
		eip.setParameters(parameters);

		final ComboParameterPropertyUICreator comboParameterPropertyUICreator = 
				new ComboParameterPropertyUICreator(dbc, modelMap, eip, camelModelElement, parameter, parent,
				widgetFactory);
		comboParameterPropertyUICreator.setValues(new String[] {"one", "two", "three"});
		comboParameterPropertyUICreator.create();

		final Combo control = comboParameterPropertyUICreator.getControl();

		control.select(0);
		assertThat(modelMap.get("testParameterName")).isEqualTo("one");
		assertThat(camelModelElement.getParameter("testParameterName")).isEqualTo("one");

		control.select(2);
		assertThat(modelMap.get("testParameterName")).isEqualTo("three");
		assertThat(camelModelElement.getParameter("testParameterName")).isEqualTo("three");
	}
	
}
