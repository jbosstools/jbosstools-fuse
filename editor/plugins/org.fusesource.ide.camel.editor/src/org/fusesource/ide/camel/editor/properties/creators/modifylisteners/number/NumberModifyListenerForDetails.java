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
package org.fusesource.ide.camel.editor.properties.creators.modifylisteners.number;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author Aurelien Pupier
 *
 */
public class NumberModifyListenerForDetails extends AbstractNumberModifyListener {

	public NumberModifyListenerForDetails(AbstractCamelModelElement camelModelElement, Parameter parameter) {
		super(camelModelElement, parameter);
	}

	@Override
	protected void updateModel(String newValue) {
		camelModelElement.setParameter(parameter.getName(), newValue);
	}

}
