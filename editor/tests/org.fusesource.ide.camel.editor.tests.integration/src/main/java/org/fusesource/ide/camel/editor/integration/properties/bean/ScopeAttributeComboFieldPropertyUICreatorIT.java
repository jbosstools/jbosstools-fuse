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
package org.fusesource.ide.camel.editor.integration.properties.bean;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Combo;
import org.fusesource.ide.camel.editor.integration.properties.creators.AbstractParameterPropertySectionUICreatorITHelper;
import org.fusesource.ide.camel.editor.properties.bean.ScopeAttributeComboFieldPropertyUICreator;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;
import org.junit.Test;

/**
 * @author brianf
 *
 */
public class ScopeAttributeComboFieldPropertyUICreatorIT extends AbstractParameterPropertySectionUICreatorITHelper {

	private static String singletonScope = "singleton"; //$NON-NLS-1$
	private static String defaultScope = singletonScope;
	private static String prototypeScope = "prototype";  //$NON-NLS-1$

	@Test
	public void testUIDisplayed() throws Exception {

		Parameter parameter = new Parameter();
		parameter.setName(GlobalBeanEIP.PROP_SCOPE);
		parameter.setKind("parameter");
		Eip eip = new Eip();
		final Map<String, Parameter> parameters = new HashMap<>();
		parameters.put(parameter.getName(), parameter);
		eip.setProperties(parameters);

		final ScopeAttributeComboFieldPropertyUICreator scopeParameterPropertyUICreator = 
				new ScopeAttributeComboFieldPropertyUICreator(dbc, modelMap, eip, camelModelElement, parameter, parent,
				widgetFactory);
		scopeParameterPropertyUICreator.create();

		final Combo control = scopeParameterPropertyUICreator.getControl();
		assertThat(control.getText()).isEqualTo(defaultScope);
		
		control.select(1);
		assertThat(modelMap.get(GlobalBeanEIP.PROP_SCOPE)).isEqualTo(prototypeScope);
		assertThat(camelModelElement.getParameter(GlobalBeanEIP.PROP_SCOPE)).isEqualTo(prototypeScope);
	}
}
