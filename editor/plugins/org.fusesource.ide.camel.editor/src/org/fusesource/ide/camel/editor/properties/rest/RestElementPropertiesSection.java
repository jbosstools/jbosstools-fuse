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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.properties.FusePropertySection;
import org.fusesource.ide.camel.editor.properties.bean.NewBeanIdPropertyValidator;
import org.fusesource.ide.camel.editor.properties.bean.PropertyRequiredValidator;
import org.fusesource.ide.camel.editor.properties.creators.AbstractParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.AbstractTextFieldParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.TextParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.advanced.BooleanParameterPropertyUICreatorForAdvanced;
import org.fusesource.ide.camel.editor.properties.creators.advanced.UnsupportedParameterPropertyUICreatorForAdvanced;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;
import org.fusesource.ide.camel.model.service.core.model.eips.RestElementEIP;
import org.fusesource.ide.camel.model.service.core.util.CamelComponentUtils;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author brianf
 * 
 */
public class RestElementPropertiesSection extends FusePropertySection {
	
	private Map<String, Parameter> parameterList;
	private Map<String, IObservableValue<?>> modelValueMap = new HashMap<>();
	private Map<String, IObservableValue<?>> targetValueMap = new HashMap<>();


	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		
		this.toolkit = new FormToolkit(parent.getDisplay());
		super.createControls(parent, aTabbedPropertySheetPage);

		createStandardTabLayout(UIMessages.advancedBeanPropertiesSectionTitle);
	}

	/**
	 * 
	 * @param folder
	 */
	@Override
	protected void createContentTabs(CTabFolder folder) {
		createParameterList();

		CTabItem contentTab = new CTabItem(this.tabFolder, SWT.NONE);
		contentTab.setText(Strings.humanize(GROUP_COMMON));
		Composite page = this.toolkit.createComposite(folder);
		page.setLayout(new GridLayout(4, false));

		generateTabContents(parameterList, page);
		
		contentTab.setControl(page);
		this.tabs.add(contentTab);
	}
	
	private AbstractParameterPropertyUICreator handleField(final Parameter p, final Composite page) {
		createPropertyLabel(toolkit, page, p);
		AbstractParameterPropertyUICreator creator = createPropertyFieldEditor(page, p);
		if (creator != null) {
			IObservableValue<?> modelValue = (IObservableValue<?>) creator.getBinding().getModel();
			IObservableValue<?> targetValue = (IObservableValue<?>) creator.getUiObservable();
			modelValueMap.put(p.getName(), modelValue);
			targetValueMap.put(p.getName(), targetValue);
			return creator;
		}
		throw new NullPointerException();
	}
	
	private void createParameterList() {
		parameterList = new HashMap<>();

		// define the properties we're handling here
		Parameter idParam = createParameter(RestElementEIP.PROP_ID, String.class.getName());
		idParam.setRequired("true"); //$NON-NLS-1$
		parameterList.put(RestElementEIP.PROP_ID, idParam);
		
		parameterList.put(RestElementEIP.PROP_PATH,
				createParameter(RestElementEIP.PROP_PATH, String.class.getName()));
		parameterList.put(RestElementEIP.PROP_TAG,
				createParameter(RestElementEIP.PROP_TAG, String.class.getName()));
		parameterList.put(RestElementEIP.PROP_CONSUMES,
				createParameter(RestElementEIP.PROP_CONSUMES, String.class.getName()));
		parameterList.put(RestElementEIP.PROP_PRODUCES,
				createParameter(RestElementEIP.PROP_PRODUCES, String.class.getName()));
		parameterList.put(RestElementEIP.PROP_SKIPBINDINGONERRORCODE,
				createParameter(RestElementEIP.PROP_SKIPBINDINGONERRORCODE, Boolean.class.getName()));
		parameterList.put(RestElementEIP.PROP_ENABLECORS,
				createParameter(RestElementEIP.PROP_ENABLECORS, Boolean.class.getName()));
		parameterList.put(RestElementEIP.PROP_APIDOCS,
				createParameter(RestElementEIP.PROP_APIDOCS, Boolean.class.getName()));
	}

	public Parameter createParameter(String name, String jType) {
		Parameter outParm = new Parameter();
		outParm.setName(name);
		outParm.setJavaType(jType);
		return outParm;
	}
	
	private AbstractParameterPropertyUICreator createPropertyFieldEditor(final Composite page, Parameter p) {
		if ("java.lang.String".equals(p.getJavaType())) { //$NON-NLS-1$
			AbstractTextFieldParameterPropertyUICreator txtFieldCreator = new TextParameterPropertyUICreator(dbc, modelMap, eip, selectedEP, p, getValidatorForField(p), page, getWidgetFactory());
			txtFieldCreator.create();
			return txtFieldCreator;
		} else if ("java.lang.Boolean".equals(p.getJavaType())) { //$NON-NLS-1$
			AbstractParameterPropertyUICreator creator = new BooleanParameterPropertyUICreatorForAdvanced(
					dbc, modelMap, eip, selectedEP, p, page, getWidgetFactory());
			creator.create();
			return creator;
		} else if (CamelComponentUtils.isUnsupportedProperty(p)) { 
			// handle unsupported props
			AbstractParameterPropertyUICreator creator = new UnsupportedParameterPropertyUICreatorForAdvanced(
					dbc, modelMap, eip, selectedEP, p, page,getWidgetFactory());
			creator.create();
			return creator;
		}
		return null;
	}

	private IValidator getValidatorForField(Parameter p) {
		IValidator validator = null;
		String propName = p.getName();

		if (GlobalBeanEIP.PROP_ID.equals(propName)) {
			validator = new NewBeanIdPropertyValidator(p, selectedEP);
		}
		if (validator == null && p.getRequired() != null && "true".contentEquals(p.getRequired())) { //$NON-NLS-1$
			validator = new PropertyRequiredValidator(p);
		}
		return validator;
	}

	/**
	 * 
	 * @param props
	 * @param page
	 * @param ignorePathProperties
	 * @param group
	 */
	protected void generateTabContents(Map<String, Parameter> props, final Composite page) {
		handleField(props.get(GlobalBeanEIP.PROP_ID), page);
		props.remove(GlobalBeanEIP.PROP_ID);

		for (Parameter p : props.values()) {
			handleField(p, page);
		}
		
	}
	
}
