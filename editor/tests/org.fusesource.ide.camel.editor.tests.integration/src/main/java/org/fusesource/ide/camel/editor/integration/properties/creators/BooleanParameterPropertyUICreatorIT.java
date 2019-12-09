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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.fusesource.ide.camel.editor.properties.creators.AbstractBooleanParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.advanced.BooleanParameterPropertyUICreatorForAdvanced;
import org.fusesource.ide.camel.editor.properties.creators.details.BooleanParameterPropertyUICreatorForDetails;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Aurelien Pupier
 *
 */
public class BooleanParameterPropertyUICreatorIT extends AbstractParameterPropertySectionUICreatorITHelper {

	private Eip eip;
	private Parameter parameter;

	@Before
	public void setup() {
		parameter = new Parameter();
		parameter.setName("testParameterName");
		parameter.setKind("parameter");
		parameter.setJavaType(Boolean.class.getName());
		eip = new Eip();
		final Map<String, Parameter> parameters = new HashMap<>();
		parameters.put(parameter.getName(), parameter);
		eip.setProperties(parameters);
	}

	@Test
	public void testUIDisplayedForDetails() throws Exception {
		final AbstractBooleanParameterPropertyUICreator booleanParameterPropertyUICreatorForDetails = new BooleanParameterPropertyUICreatorForDetails(dbc, modelMap, eip,
				camelModelElement, parameter, parent, widgetFactory);
		booleanParameterPropertyUICreatorForDetails.create();

		modifySelection(booleanParameterPropertyUICreatorForDetails);

		assertThat(modelMap.get("testParameterName")).isEqualTo(true);
		assertThat(camelModelElement.getParameter("testParameterName")).isEqualTo(true);
	}

	@Test
	public void testUIDisplayedForAdvanced() throws Exception {
		camelModelElement.setParameter("uri", "test");
		final AbstractBooleanParameterPropertyUICreator booleanParameterPropertyUICreatorForDetails = new BooleanParameterPropertyUICreatorForAdvanced(dbc, modelMap, eip,
				camelModelElement, parameter, parent, widgetFactory);
		booleanParameterPropertyUICreatorForDetails.create();
		
		modifySelection(booleanParameterPropertyUICreatorForDetails);

		assertThat(modelMap.get("testParameterName")).isEqualTo(true);
		assertThat(PropertiesUtils.getTypedPropertyFromUri(camelModelElement, parameter, PropertiesUtils.getComponentFor(camelModelElement))).isEqualTo(true);
	}

	/**
	 * @param booleanParameterPropertyUICreator
	 */
	private void modifySelection(final AbstractBooleanParameterPropertyUICreator booleanParameterPropertyUICreator) {
		booleanParameterPropertyUICreator.getControl().setSelection(true);
		booleanParameterPropertyUICreator.getControl().notifyListeners(SWT.Selection, new Event());
	}

}
