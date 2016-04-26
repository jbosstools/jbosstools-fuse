/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.properties.creators;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.camel.editor.properties.creators.modifylisteners.text.TextParameterPropertyModifyListenerForDetails;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.util.CamelComponentUtils;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;

/**
 * @author Aurelien Pupier
 *
 */
public class TextParameterPropertyUICreator extends AbstractTextFieldParameterPropertyUICreator {

	public TextParameterPropertyUICreator(DataBindingContext dbc, IObservableMap modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent,
			TabbedPropertySheetWidgetFactory widgetFactory) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, new TextParameterPropertyModifyListenerForDetails(camelModelElement, parameter.getName()));
	}

	@Override
	protected IValidator createValidator() {
		final IValidator superValidator = super.createValidator();
		return new IValidator() {

			@Override
			public IStatus validate(Object value) {
				IStatus superValidation = superValidator.validate(value);
				if (!superValidation.isOK()) {
					return superValidation;
				}
				if (PropertiesUtils.isRequired(parameter) || parameter.getName().equalsIgnoreCase("id")) {
					if (parameter.getName().equalsIgnoreCase("uri")) {
						// only enforce URI if there is no REF set
						if (camelModelElement.getParameter("uri") == null || ((String) camelModelElement.getParameter("uri")).trim().length() < 1) {
							// no URI set -> check for REF
							if (camelModelElement.getParameter("ref") == null || ((String) camelModelElement.getParameter("ref")).trim().length() < 1) {
								// there is no ref
								return ValidationStatus.warning("One of Ref and Uri values have to be filled!");
							} else {
								// ref found - now check if REF has URI defined
								AbstractCamelModelElement cme = camelModelElement.getCamelContext().findNode((String) camelModelElement.getParameter("ref"));
								if (cme == null || cme.getParameter("uri") == null || ((String) cme.getParameter("uri")).trim().length() < 1) {
									// no uri defined on ref
									return ValidationStatus.warning("The referenced endpoint has no URI defined or does not exist.");
								}
							}
						}

						// check for broken refs
						if (camelModelElement.getParameter("uri") != null && ((String) camelModelElement.getParameter("uri")).startsWith("ref:")) {
							String refId = ((String) camelModelElement.getParameter("uri")).trim().length() > "ref:".length()
									? ((String) camelModelElement.getParameter("uri")).substring("ref:".length()) : null;
							List<String> refs = Arrays.asList(CamelComponentUtils.getRefs(camelModelElement.getCamelFile()));
							if (refId == null || refId.trim().length() < 1 || refs.contains(refId) == false) {
								return ValidationStatus.warning("The entered reference does not exist in your context!");
							}
						}

						// warn user if he set both ref and uri
						if (camelModelElement.getParameter("uri") != null && ((String) camelModelElement.getParameter("uri")).trim().length() > 0
								&& camelModelElement.getParameter("ref") != null && ((String) camelModelElement.getParameter("ref")).trim().length() > 0) {
							return ValidationStatus.error("Please choose either URI or Ref but do not enter both values.");
						}

					} else if (parameter.getName().equalsIgnoreCase("ref")) {

						if (value != null && value instanceof String && value.toString().trim().length() > 0) {
							String refId = (String) value;
							AbstractCamelModelElement cme = camelModelElement.getCamelContext().findNode(refId);
							if (cme == null) {
								// check for global beans
								if (camelModelElement.getCamelFile().getGlobalDefinitions().containsKey(refId) == false) {
									// the ref doesn't exist
									return ValidationStatus.warning("The entered reference does not exist in your context!");
								}
							} else {
								// the ref exists
								if (cme.getParameter("uri") == null || ((String) cme.getParameter("uri")).trim().length() < 1) {
									// but has no URI defined
									return ValidationStatus.error("The referenced endpoint does not define a valid URI!");
								}
							}
						}

						// warn user if he set both ref and uri
						if (camelModelElement.getParameter("uri") != null && ((String) camelModelElement.getParameter("uri")).trim().length() > 0
								&& camelModelElement.getParameter("ref") != null && ((String) camelModelElement.getParameter("ref")).trim().length() > 0) {
							return ValidationStatus.warning("Please choose only ONE of Uri and Ref.");
						}

					} else if (parameter.getName().equalsIgnoreCase("id")) {
						// check if ID is unique
						if (value == null || value instanceof String == false || value.toString().trim().length() < 1) {
							return ValidationStatus.warning("Parameter " + parameter.getName() + " is a mandatory field and cannot be empty.");
						} else {
							if (camelModelElement.getCamelContext().isIDUnique((String) value) == false) {
								return ValidationStatus.warning("Parameter " + parameter.getName() + " does not contain a unique value.");
							}
						}
					} else {
						// by default we only check for a value != null and
						// length > 0
						if (value == null || value instanceof String == false || value.toString().trim().length() < 1) {
							return ValidationStatus.warning("Parameter " + parameter.getName() + " is a mandatory field and cannot be empty.");
						}
					}
				}
				// all checks passed
				return ValidationStatus.ok();
			}
		};
	}

}
