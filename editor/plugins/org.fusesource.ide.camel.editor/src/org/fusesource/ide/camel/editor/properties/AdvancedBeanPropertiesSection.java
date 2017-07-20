/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.BeanConfigUtil;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.wizards.pages.BeanClassExistsValidator;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.properties.bean.AttributeTextFieldPropertyUICreatorWithBrowse;
import org.fusesource.ide.camel.editor.properties.bean.NewBeanIdPropertyValidator;
import org.fusesource.ide.camel.editor.properties.bean.PropertyMethodValidator;
import org.fusesource.ide.camel.editor.properties.bean.PropertyRequiredValidator;
import org.fusesource.ide.camel.editor.properties.bean.ScopeAttributeComboFieldPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.AbstractParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.AbstractTextFieldParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.TextParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.advanced.UnsupportedParameterPropertyUICreatorForAdvanced;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;
import org.fusesource.ide.camel.model.service.core.util.CamelComponentUtils;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author brianf
 * 
 */
public class AdvancedBeanPropertiesSection extends FusePropertySection {

	private static final int PUBLIC_STATIC_METHOD_BROWSE = 1;
	private static final int PUBLIC_NO_ARG_METHOD_BROWSE = 2;
	
	private BeanConfigUtil beanConfigUtil = new BeanConfigUtil();

	/**
	 * 
	 * @param folder
	 */
	@Override
	protected void createContentTabs(CTabFolder folder) {
		List<Parameter> props = new ArrayList<>();
		List<String> tabsToCreate = new ArrayList<>();
		tabsToCreate.add(GROUP_COMMON);
		
		// define the properties we're handling here
		Parameter idParam = beanConfigUtil.createParameter(GlobalBeanEIP.PROP_ID, String.class.getName());
		idParam.setRequired("true"); //$NON-NLS-1$
		props.add(idParam);
		Parameter classParam = beanConfigUtil.createParameter(GlobalBeanEIP.PROP_CLASS, String.class.getName());
		classParam.setRequired("true"); //$NON-NLS-1$
		props.add(classParam);
		props.add(beanConfigUtil.createParameter(GlobalBeanEIP.PROP_SCOPE, String.class.getName()));
		props.add(beanConfigUtil.createParameter(GlobalBeanEIP.PROP_DEPENDS_ON, String.class.getName()));
		props.add(beanConfigUtil.createParameter(GlobalBeanEIP.PROP_INIT_METHOD, String.class.getName()));
		props.add(beanConfigUtil.createParameter(GlobalBeanEIP.PROP_DESTROY_METHOD, String.class.getName()));
		
		String factoryAttribute = beanConfigUtil.getFactoryMethodAttribute(selectedEP.getXmlNode());
		props.add(beanConfigUtil.createParameter(factoryAttribute, String.class.getName()));

		props.sort(new ParameterPriorityComparator());

		for (String group : tabsToCreate) {
			CTabItem contentTab = new CTabItem(this.tabFolder, SWT.NONE);
			contentTab.setText(Strings.humanize(group));

			Composite page = this.toolkit.createComposite(folder);
			page.setLayout(new GridLayout(4, false));

			generateTabContents(props, page);

			contentTab.setControl(page);

			this.tabs.add(contentTab);
		}
	}

	private AbstractTextFieldParameterPropertyUICreator createTextFieldWithClassBrowseAndNew(final Parameter p, final Composite page) {
		AbstractTextFieldParameterPropertyUICreator txtFieldCreator = 
				new AttributeTextFieldPropertyUICreatorWithBrowse(dbc, modelMap, eip, selectedEP, p, page, getWidgetFactory());
		txtFieldCreator.setColumnSpan(1);
		txtFieldCreator.create();
		createClassBrowseButton(page, txtFieldCreator.getControl());
		createClassNewButton(page, txtFieldCreator.getControl());
		return txtFieldCreator;
	}
	
	private AbstractTextFieldParameterPropertyUICreator createTextFieldWithPublicStaticMethodBrowse(final Parameter p, final Composite page) {
		AbstractTextFieldParameterPropertyUICreator txtFieldCreator = 
				new AttributeTextFieldPropertyUICreatorWithBrowse(dbc, modelMap, eip, selectedEP, p, page, getWidgetFactory());
		txtFieldCreator.setColumnSpan(2);
		txtFieldCreator.create();
		createPublicStaticMethodBrowseButton(page, txtFieldCreator.getControl());
		return txtFieldCreator;
	}

	private AbstractTextFieldParameterPropertyUICreator createTextFieldWithNoArgMethodBrowse(final Parameter p, final Composite page) {
		AbstractTextFieldParameterPropertyUICreator txtFieldCreator = 
				new AttributeTextFieldPropertyUICreatorWithBrowse(dbc, modelMap, eip, selectedEP, p, page, getWidgetFactory());
		txtFieldCreator.setColumnSpan(2);
		txtFieldCreator.create();
		createNoArgMethodBrowseButton(page, txtFieldCreator.getControl());
		return txtFieldCreator;
	}

	private AbstractTextFieldParameterPropertyUICreator createTextField(final Parameter p, final Composite page) {
		AbstractTextFieldParameterPropertyUICreator txtFieldCreator = new TextParameterPropertyUICreator(dbc, modelMap, eip, selectedEP, p, page,
				getWidgetFactory());
		txtFieldCreator.create();
		return txtFieldCreator;
	}

	private ScopeAttributeComboFieldPropertyUICreator createScopeCombo(final Parameter p, final Composite page) {
		ScopeAttributeComboFieldPropertyUICreator scopeFieldCreator = new ScopeAttributeComboFieldPropertyUICreator(dbc, modelMap, eip, selectedEP, p, page,
				getWidgetFactory());
		scopeFieldCreator.create();
		return scopeFieldCreator;
	}

	/**
	 * 
	 * @param props
	 * @param page
	 * @param ignorePathProperties
	 * @param group
	 */
	protected void generateTabContents(List<Parameter> props, final Composite page) {
		props.sort(new ParameterPriorityComparator());
		for (Parameter p : props) {
			createPropertyLabel(toolkit, page, p);
			createPropertyFieldEditor(page, p);
		}
	}

	private IValidator getValidatorForField(Parameter p) {
		IValidator validator = null;
		IProject project = selectedEP.getCamelFile().getResource().getProject();
		String propName = p.getName();

		if (GlobalBeanEIP.PROP_CLASS.equals(propName)) {
			validator = new BeanClassExistsValidator(project);
		} else if (GlobalBeanEIP.PROP_INIT_METHOD.equals(propName)
				|| GlobalBeanEIP.PROP_DESTROY_METHOD.equals(propName)
				|| GlobalBeanEIP.PROP_FACTORY_METHOD.equals(propName)
				|| GlobalBeanEIP.PROP_FACTORY_BEAN.equals(propName)) {
			validator = new PropertyMethodValidator(modelMap, project);
		} else if (GlobalBeanEIP.PROP_ID.equals(propName)) {
			validator = new NewBeanIdPropertyValidator(p, selectedEP);
		}
		if (validator == null && p.getRequired() != null && "true".contentEquals(p.getRequired())) { //$NON-NLS-1$
			validator = new PropertyRequiredValidator(p);
		}
		return validator;
	}
	
	private void createPropertyFieldEditor(final Composite page, Parameter p) {
		IValidator validator = getValidatorForField(p);
		AbstractParameterPropertyUICreator fieldCreator = null;
		String propName = p.getName();
		if (GlobalBeanEIP.PROP_CLASS.equals(propName)) {
			fieldCreator = createTextFieldWithClassBrowseAndNew(p, page);
		} else if (GlobalBeanEIP.PROP_INIT_METHOD.equals(propName) || GlobalBeanEIP.PROP_DESTROY_METHOD.equals(propName)) {
			fieldCreator = createTextFieldWithNoArgMethodBrowse(p, page);
		} else if (GlobalBeanEIP.PROP_FACTORY_METHOD.equals(propName) || GlobalBeanEIP.PROP_FACTORY_BEAN.equals(propName)) {
			fieldCreator = createTextFieldWithPublicStaticMethodBrowse(p, page);
		} else if (GlobalBeanEIP.PROP_ID.equals(propName)) {
			fieldCreator = createTextField(p, page);
		} else if (GlobalBeanEIP.PROP_SCOPE.equals(propName)) {
			fieldCreator = createScopeCombo(p, page);
		} else if (CamelComponentUtils.isTextProperty(p) || CamelComponentUtils.isCharProperty(p)) {
			fieldCreator = createTextField(p, page);
		} else if (CamelComponentUtils.isUnsupportedProperty(p)) { // handle unsupported props
			new UnsupportedParameterPropertyUICreatorForAdvanced(dbc, modelMap, eip, selectedEP, p, page,
					getWidgetFactory()).create();
		}
		if (fieldCreator != null && fieldCreator instanceof AbstractTextFieldParameterPropertyUICreator) {
			ISWTObservableValue uiObservable = handleObservable((AbstractTextFieldParameterPropertyUICreator) fieldCreator, p);
			if (uiObservable != null) { 
				bindField(validator, uiObservable, p);
			}
		}
	}	
	
	private ISWTObservableValue handleObservable(AbstractTextFieldParameterPropertyUICreator txtFieldCreator, Parameter p) {
		// initialize the map entry
		modelMap.put(p.getName(), txtFieldCreator.getControl().getText());
		// create observables for the control
		return txtFieldCreator.getUiObservable();
	}
	
	private void bindField(IValidator validator, ISWTObservableValue uiObservable, Parameter p) {
		// create UpdateValueStrategy and assign to the binding
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(validator);

		// create observables for the Map entries
		IObservableValue<Object> modelObservable = Observables.observeMapEntry(modelMap, p.getName());
		// bind the observables
		Binding bindValue = dbc.bindValue(uiObservable, modelObservable, strategy, null);
		ControlDecorationSupport.create(bindValue, SWT.TOP | SWT.LEFT);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#
	 * createControls (org.eclipse.swt.widgets.Composite,
	 * org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		this.toolkit = new FormToolkit(parent.getDisplay());
		super.createControls(parent, aTabbedPropertySheetPage);

		createStandardTabLayout(UIMessages.advancedBeanPropertiesSectionTitle);
	}

	private void createClassBrowseButton(Composite composite, Text field) {
		Button browseBeanButton = new Button(composite, SWT.PUSH);
		browseBeanButton.setText("..."); //$NON-NLS-1$
		browseBeanButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final IProject project = selectedEP.getCamelFile().getResource().getProject();
				String className = beanConfigUtil.handleClassBrowse(project, getDisplay().getActiveShell());
				if (className != null) {
					field.setText(className);
				}
			}

		});
	}

	private void createClassNewButton(Composite composite, Text field) {
		Button newBeanButton = new Button(composite, SWT.PUSH);
		newBeanButton.setText("+"); //$NON-NLS-1$
		newBeanButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final IProject project = selectedEP.getCamelFile().getResource().getProject();
				String initialClassName = field.getText();
				String value = beanConfigUtil.handleNewClassWizard(project, getDisplay().getActiveShell(), initialClassName);
				if (value != null) {
					field.setText(value);
				}
			}

		});
	}

	private void createNoArgMethodBrowseButton(Composite composite, Text field) {
		createMethodBrowseBtn(composite, field, PUBLIC_NO_ARG_METHOD_BROWSE);
	}

	private void createMethodBrowseBtn(Composite composite, Text field, int methodBrowseType) {
		Button browseBeanButton = new Button(composite, SWT.PUSH);
		browseBeanButton.setText("..."); //$NON-NLS-1$
		browseBeanButton.addSelectionListener(new MethodSelectionListener(methodBrowseType, field));
	}
	
	/*
	 * Handle which type of method selection should be presented to the user.
	 * @author brianf
	 *
	 */
	class MethodSelectionListener extends SelectionAdapter {
		
		private int methodBrowseType;
		private Text field;
		
		public MethodSelectionListener(int browseType, Text field) {
			super();
			this.methodBrowseType = browseType;
			this.field = field;
		}
		
		@Override
		public void widgetSelected(SelectionEvent event) {
			Object control = modelMap.get(GlobalBeanEIP.PROP_CLASS);
			if (control != null) {
				final IProject project = selectedEP.getCamelFile().getResource().getProject();
				String className = (String) control;
				String methodName;
				switch (methodBrowseType) {
				case PUBLIC_STATIC_METHOD_BROWSE:
					methodName = beanConfigUtil.handlePublicStaticMethodBrowse(project, className,
							getDisplay().getActiveShell());
					break;
				case PUBLIC_NO_ARG_METHOD_BROWSE:
					methodName = beanConfigUtil.handleNoArgMethodBrowse(project, className,
							getDisplay().getActiveShell());
					break;
				default:
					// default to standard method browse
					methodName = beanConfigUtil.handleMethodBrowse(project, className,
							getDisplay().getActiveShell());
				}
				if (methodName != null) {
					field.setText(methodName);
				}
			}
		}
	}

	private void createPublicStaticMethodBrowseButton(Composite composite, Text field) {
		createMethodBrowseBtn(composite, field, PUBLIC_STATIC_METHOD_BROWSE);
	}
}
