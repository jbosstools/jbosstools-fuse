/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.camel.editor.properties.creators.ComboParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.modifylisteners.text.ComboParameterPropertyModifyListener;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteContainerElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;

/**
 * @author brianf
 *
 */
public class RouteRefAttributeComboFieldPropertyUICreator extends ComboParameterPropertyUICreator {

	public RouteRefAttributeComboFieldPropertyUICreator(DataBindingContext dbc, IObservableMap<String, String> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, 
				new ComboParameterPropertyModifyListener(camelModelElement, parameter.getName()));
		setValues();
	}

	public RouteRefAttributeComboFieldPropertyUICreator(DataBindingContext dbc, IObservableMap<String, String> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory, ComboParameterPropertyModifyListener listener) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, listener);
		setValues();
	}

	public RouteRefAttributeComboFieldPropertyUICreator(DataBindingContext dbc, IObservableMap<String, String> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory, ComboParameterPropertyModifyListener listener, IValidator extraValidator) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, listener, extraValidator);
		setValues();
	}

	private String getURI(AbstractCamelModelElement element) {
		if (element != null && element.getParameter(AbstractCamelModelElement.URI_PARAMETER_KEY) != null) {
			return (String) element.getParameter(AbstractCamelModelElement.URI_PARAMETER_KEY);
		}
		return null;
	}
	
	private String[] getRoutes(CamelFile cf) {
		List<String> routeURIs = new ArrayList<>();
		routeURIs.add("");
		if (cf.getRouteContainer() != null) {
			final CamelRouteContainerElement crce = cf.getRouteContainer();
			Iterator<AbstractCamelModelElement> childIter = crce.getChildElements().iterator();
			while (childIter.hasNext()) {
				AbstractCamelModelElement child = childIter.next();
				if (child instanceof CamelRouteElement) {
					CamelRouteElement cre = (CamelRouteElement) child;
					if (!cre.getInputs().isEmpty()) {
						AbstractCamelModelElement firstInFlow = cre.getInputs().get(0);
						String routeURI = getURI(firstInFlow);
						if (routeURI != null) {
							routeURIs.add(routeURI);
						}
					}
				}
			}
		}
		return routeURIs.toArray(new String[routeURIs.size()]);
	}
	
	private void setValues() {
		String[] routeRefs = getRoutes(this.camelModelElement.getCamelFile());
		super.setValues(routeRefs);
	}
}
