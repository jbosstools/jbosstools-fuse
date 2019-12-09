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
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author Aurelien Pupier
 *
 */
public abstract class InitialValueCalculator<T> {

	protected AbstractCamelModelElement camelModelElement;
	protected Parameter parameter;

	public InitialValueCalculator(AbstractCamelModelElement camelModelElement, Parameter parameter) {
		this.camelModelElement = camelModelElement;
		this.parameter = parameter;
	}

	public abstract T getInitialValue();

}
