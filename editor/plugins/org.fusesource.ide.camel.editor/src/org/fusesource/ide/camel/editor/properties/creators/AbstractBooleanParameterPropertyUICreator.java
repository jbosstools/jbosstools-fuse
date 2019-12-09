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
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author Aurelien Pupier
 *
 */
public abstract class AbstractBooleanParameterPropertyUICreator extends AbstractParameterPropertyUICreator {

	public AbstractBooleanParameterPropertyUICreator(DataBindingContext dbc, IObservableMap modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent,
			TabbedPropertySheetWidgetFactory widgetFactory) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory);
	}

	@Override
	protected void init(Composite parent) {
		Button checkBox = getWidgetFactory().createButton(parent, "", SWT.CHECK);
		checkBox.setSelection(getInitialValue());
		checkBox.addSelectionListener(createSelectionListener());
		checkBox.setLayoutData(createPropertyFieldLayoutData());
		setControl(checkBox);

		setUiObservable(WidgetProperties.selection().observe(checkBox));
	}

	protected abstract SelectionAdapter createSelectionListener();

	@Override
	public abstract Boolean getInitialValue();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fusesource.ide.camel.editor.properties.creators.
	 * AbstractParameterPropertyUICreator#getControl()
	 */
	@Override
	public Button getControl() {
		return (Button) super.getControl();
	}

}
