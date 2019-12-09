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
package org.fusesource.ide.camel.editor.properties.creators.calculator;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;

/**
 * @author Aurelien Pupier
 *
 */
public class InitialValueAdvancedCalculator extends InitialValueCalculator<String> {

	private Component component;

	public InitialValueAdvancedCalculator(AbstractCamelModelElement camelModelElement, Parameter parameter, Component component) {
		super(camelModelElement, parameter);
		this.component = component;
	}

	@Override
	public String getInitialValue() {
		return PropertiesUtils.getPropertyFromUri(camelModelElement, parameter, component);
	}

}
