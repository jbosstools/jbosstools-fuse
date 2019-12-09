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

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;

/**
 * @author Aurelien Pupier
 *
 */
public class NumberModifyListenerForAdvanced extends AbstractNumberModifyListener {

	private Component component;
	private IObservableMap<?,?> modelMap;

	public NumberModifyListenerForAdvanced(AbstractCamelModelElement camelModelElement, Parameter parameter, IObservableMap<?,?> modelMap) {
		super(camelModelElement, parameter);
		this.component = PropertiesUtils.getComponentFor(camelModelElement);
		this.modelMap = modelMap;
	}
	
	@Override
	protected void updateModel(String newValue) {
		PropertiesUtils.updateURIParams(camelModelElement, parameter, newValue, component, modelMap);
	}

}
