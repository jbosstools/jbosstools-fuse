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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.WritableMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;

/**
 * @author Aurelien Pupier
 *
 */
public class AbstractParameterPropertySectionUICreatorITHelper {

	protected Composite parent = new Composite(new Shell(), SWT.NONE);
	protected AbstractCamelModelElement camelModelElement = new CamelBasicModelElement(null, null);
	protected IObservableMap modelMap = new WritableMap();
	protected DataBindingContext dbc = new DataBindingContext();
	protected TabbedPropertySheetWidgetFactory widgetFactory = new TabbedPropertySheetWidgetFactory();
}
