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
package org.fusesource.ide.camel.editor.properties.creators;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.camel.editor.properties.creators.modifylisteners.text.TextParameterPropertyModifyListenerForDetails;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.validation.model.RefOrDataFormatUnicityChoiceValidator;
import org.fusesource.ide.camel.validation.model.TextParameterValidator;
import org.fusesource.ide.foundation.core.util.CompoundValidator;

/**
 * @author Aurelien Pupier
 *
 */
public class TextParameterPropertyUICreator extends AbstractTextFieldParameterPropertyUICreator {
	
	private IValidator extraValidator;

	public TextParameterPropertyUICreator(DataBindingContext dbc, IObservableMap modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter, IValidator extraValidator,
			Composite parent,
			TabbedPropertySheetWidgetFactory widgetFactory) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, new TextParameterPropertyModifyListenerForDetails(camelModelElement, parameter.getName()));
		this.extraValidator = extraValidator;
	}

	@Override
	protected IValidator createValidator() {
		return new CompoundValidator(
				super.createValidator(),
				new TextParameterValidator(camelModelElement, parameter),
				new RefOrDataFormatUnicityChoiceValidator(camelModelElement, parameter),
				extraValidator);
	}

}
