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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.BeanConfigUtil;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.wizards.pages.BeanClassExistsValidator;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.wizards.pages.BeanRefClassExistsValidator;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.properties.bean.AttributeTextFieldPropertyUICreatorWithBrowse;
import org.fusesource.ide.camel.editor.properties.bean.BeanRefAttributeComboFieldPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.bean.NewBeanIdPropertyValidator;
import org.fusesource.ide.camel.editor.properties.bean.PropertyMethodValidator;
import org.fusesource.ide.camel.editor.properties.bean.PropertyRequiredValidator;
import org.fusesource.ide.camel.editor.properties.bean.ScopeAttributeComboFieldPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.AbstractParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.AbstractTextFieldParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.TextParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.advanced.UnsupportedParameterPropertyUICreatorForAdvanced;
import org.fusesource.ide.camel.editor.properties.creators.modifylisteners.text.ComboParameterPropertyModifyListener;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;
import org.fusesource.ide.camel.model.service.core.util.CamelComponentUtils;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author brianf
 * 
 */
public class AdvancedBeanPropertiesSection extends FusePropertySection {

	private static final int PUBLIC_AND_STATIC_METHOD_BROWSE = 1;
	private static final int PUBLIC_NO_ARG_METHOD_BROWSE = 2;
	private static final int PUBLIC_OR_STATIC_METHOD_BROWSE = 3;

	private BeanConfigUtil beanConfigUtil = new BeanConfigUtil();
	private Map<String, Parameter> parameterList;
	private Map<String, IObservableValue<?>> modelValueMap = new HashMap<>();
	private Map<String, IObservableValue<?>> targetValueMap = new HashMap<>();
	private String factoryBeanTag;
	private AbstractParameterPropertyUICreator classCreator;
	private AbstractParameterPropertyUICreator beanRefCreator;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		
		this.toolkit = new FormToolkit(parent.getDisplay());
		super.createControls(parent, aTabbedPropertySheetPage);

		createStandardTabLayout(UIMessages.advancedBeanPropertiesSectionTitle);
	}

	private void createParameterList() {
		parameterList = new HashMap<>();

		// define the properties we're handling here
		Parameter idParam = beanConfigUtil.createParameter(GlobalBeanEIP.PROP_ID, String.class.getName());
		idParam.setRequired("true"); //$NON-NLS-1$
		parameterList.put(GlobalBeanEIP.PROP_ID, idParam);
		Parameter classParam = beanConfigUtil.createParameter(GlobalBeanEIP.PROP_CLASS, String.class.getName());
		//		classParam.setRequired("true"); //$NON-NLS-1$
		parameterList.put(GlobalBeanEIP.PROP_CLASS, classParam);
		parameterList.put(GlobalBeanEIP.PROP_SCOPE,
				beanConfigUtil.createParameter(GlobalBeanEIP.PROP_SCOPE, String.class.getName()));
		parameterList.put(GlobalBeanEIP.PROP_DEPENDS_ON,
				beanConfigUtil.createParameter(GlobalBeanEIP.PROP_DEPENDS_ON, String.class.getName()));
		parameterList.put(GlobalBeanEIP.PROP_INIT_METHOD, 
				beanConfigUtil.createParameter(GlobalBeanEIP.PROP_INIT_METHOD, String.class.getName()));
		parameterList.put(GlobalBeanEIP.PROP_DESTROY_METHOD, 
				beanConfigUtil.createParameter(GlobalBeanEIP.PROP_DESTROY_METHOD, String.class.getName()));

		factoryBeanTag = beanConfigUtil.getFactoryBeanTag(selectedEP.getXmlNode());
		Parameter factoryBeanParameter = beanConfigUtil.createParameter(factoryBeanTag, String.class.getName());
		parameterList.put(factoryBeanTag, factoryBeanParameter);

		String factoryAttribute = beanConfigUtil.getFactoryMethodAttribute();
		parameterList.put(factoryAttribute, beanConfigUtil.createParameter(factoryAttribute, String.class.getName()));
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

		final Group classOrBeanGroup = new Group(page, SWT.NONE);
		classOrBeanGroup.setText("Bean Class or Global Bean Reference");
		classOrBeanGroup.setLayout(GridLayoutFactory.fillDefaults().numColumns(4).create());
		classOrBeanGroup.setLayoutData(GridDataFactory.fillDefaults().span(4, 1).grab(true, false).create());

		toolkit.adapt(classOrBeanGroup);
		final Button optClass = toolkit.createButton(classOrBeanGroup, "Bean Class", SWT.RADIO);
		optClass.setLayoutData(GridDataFactory.fillDefaults().indent(5, 0).span(4, 1).grab(true, false).create());

		final Parameter classParm = props.get(GlobalBeanEIP.PROP_CLASS);
		classCreator = handleField(classParm, classOrBeanGroup);
		props.remove(GlobalBeanEIP.PROP_CLASS);

		final Button optBeanRef = toolkit.createButton(classOrBeanGroup, "Bean Reference", SWT.RADIO);
		optBeanRef.setLayoutData(GridDataFactory.fillDefaults().indent(5, 0).span(4, 1).grab(true, false).create());

		final Parameter beanRefParm = props.get(factoryBeanTag);
		try {
			beanRefCreator = handleField(beanRefParm, classOrBeanGroup);
			props.remove(factoryBeanTag);
			final IObservableValue<?> classNameObservable = modelValueMap.get(GlobalBeanEIP.PROP_CLASS);
			classNameObservable.addChangeListener(event -> refreshClassAndBeanRefBindings());
			final IObservableValue<?> beanRefObservable = (IObservableValue<?>) beanRefCreator.getBinding().getModel();
			beanRefObservable.addChangeListener(event -> refreshClassAndBeanRefBindings());
			
			optBeanRef.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateOptionButtons(e, optClass, beanRefCreator, classCreator);
				}
			});

			optClass.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateOptionButtons(e, optClass, beanRefCreator, classCreator);
				}
			});

			if (!Strings.isEmpty((String) beanRefObservable.getValue())) {
				optBeanRef.setSelection(true);
				optBeanRef.notifyListeners(SWT.Selection, new Event());
			} else {
				// assume class by default
				optClass.setSelection(true);
				optClass.notifyListeners(SWT.Selection, new Event());
			}
		} catch (NullPointerException npe) {
			CamelEditorUIActivator.pluginLog().logError("Error encountered while generating bean text and reference controls.", npe);
		}
		
		for (Parameter p : props.values()) {
			handleField(p, page);
		}
		
		MultiValidator beanRefAndClassCrossValidator = new BeanRefAndClassCrossValidator(classCreator.getUiObservable(), beanRefCreator.getUiObservable());
		
		ControlDecorationSupport.create(beanRefAndClassCrossValidator, SWT.TOP | SWT.LEFT);
	}
	
	private void updateOptionButtons(final SelectionEvent e, 
			final Button optClass,
			final AbstractParameterPropertyUICreator beanRefCreator, 
			final AbstractParameterPropertyUICreator classCreator) {
		Button selectedOption = (Button) e.widget;
		boolean isSelected = selectedOption.getSelection();
		boolean isClassBtn = selectedOption.equals(optClass);
		boolean beanRefCreatorEnablement = !isClassBtn && isSelected;
		boolean classCreatorEnablement = isClassBtn && isSelected;
		
		if (beanRefCreatorEnablement) {
			// clear class field
	        Text textControl = (Text) classCreator.getControl();
	        textControl.setText("");
		} else if (classCreatorEnablement) {
			// clear ref field
	        Combo comboControl = (Combo) beanRefCreator.getControl();
			comboControl.deselectAll();
		}
		beanRefCreator.getControl().setEnabled(beanRefCreatorEnablement);
		classCreator.getControl().setEnabled(classCreatorEnablement);
	}

	private void refreshClassAndBeanRefBindings() {
		dbc.updateTargets();
	}
	
	private AbstractParameterPropertyUICreator createPropertyFieldEditor(final Composite page, Parameter p) {
		String propName = p.getName();
		if (GlobalBeanEIP.PROP_CLASS.equals(propName)) {
			return createTextFieldWithClassBrowseAndNew(p, page);
		} else if (GlobalBeanEIP.PROP_INIT_METHOD.equals(propName) || GlobalBeanEIP.PROP_DESTROY_METHOD.equals(propName)) {
			return createTextFieldWithNoArgMethodBrowse(p, page);
		} else if (GlobalBeanEIP.PROP_FACTORY_METHOD.equals(propName)) {
			return createTextFieldWithPublicOrStaticMethodBrowse(p, page);
		} else if (GlobalBeanEIP.PROP_ID.equals(propName)) {
			return createIDTextField(p, page);
		} else if (GlobalBeanEIP.PROP_SCOPE.equals(propName)) {
			return createScopeCombo(p, page);
		} else if (GlobalBeanEIP.PROP_FACTORY_BEAN.equals(propName) || GlobalBeanEIP.PROP_FACTORY_REF.equals(propName)) {
			return createRefCombo(p, page);
		} else if (CamelComponentUtils.isTextProperty(p) || CamelComponentUtils.isCharProperty(p)) {
			return createTextField(p, page);
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
		IProject project = selectedEP.getCamelFile().getResource().getProject();
		String propName = p.getName();

		if (GlobalBeanEIP.PROP_CLASS.equals(propName)) {
			validator = new BeanClassExistsValidator(project, selectedEP, modelMap);
		} else if (GlobalBeanEIP.PROP_INIT_METHOD.equals(propName)
				|| GlobalBeanEIP.PROP_DESTROY_METHOD.equals(propName)
				|| GlobalBeanEIP.PROP_FACTORY_METHOD.equals(propName)) {
			validator = new PropertyMethodValidator(modelMap, project, selectedEP);
		} else if (GlobalBeanEIP.PROP_FACTORY_BEAN.equals(propName) || GlobalBeanEIP.PROP_FACTORY_REF.equals(propName) || factoryBeanTag.equals(propName)) {
			validator = new BeanRefClassExistsValidator(project, selectedEP, modelMap);
		} else if (GlobalBeanEIP.PROP_ID.equals(propName)) {
			validator = new NewBeanIdPropertyValidator(p, selectedEP);
		}
		if (validator == null && p.getRequired() != null && "true".contentEquals(p.getRequired())) { //$NON-NLS-1$
			validator = new PropertyRequiredValidator(p);
		}
		return validator;
	}

	private AbstractTextFieldParameterPropertyUICreator createTextFieldWithClassBrowseAndNew(final Parameter p, final Composite page) {
		AbstractTextFieldParameterPropertyUICreator txtFieldCreator = 
				new AttributeTextFieldPropertyUICreatorWithBrowse(dbc, modelMap, eip, selectedEP, p, getValidatorForField(p), page, getWidgetFactory());
		txtFieldCreator.setColumnSpan(1);
		txtFieldCreator.create();
		createClassBrowseButton(page, txtFieldCreator.getControl());
		createClassNewButton(page, txtFieldCreator.getControl());
		return txtFieldCreator;
	}

	private AbstractTextFieldParameterPropertyUICreator createTextFieldWithPublicOrStaticMethodBrowse(final Parameter p, final Composite page) {
		AbstractTextFieldParameterPropertyUICreator txtFieldCreator = new AttributeTextFieldPropertyUICreatorWithBrowse(dbc, modelMap, eip, selectedEP, p,  getValidatorForField(p), page, getWidgetFactory());
		txtFieldCreator.setColumnSpan(2);
		txtFieldCreator.create();
		createPublicOrStaticMethodBrowseButton(page, txtFieldCreator.getControl());
		return txtFieldCreator;
	}

	private AbstractTextFieldParameterPropertyUICreator createTextFieldWithNoArgMethodBrowse(final Parameter p, final Composite page) {
		AbstractTextFieldParameterPropertyUICreator txtFieldCreator = new AttributeTextFieldPropertyUICreatorWithBrowse(dbc, modelMap, eip, selectedEP, p,  getValidatorForField(p), page, getWidgetFactory());
		txtFieldCreator.setColumnSpan(2);
		txtFieldCreator.create();
		createNoArgMethodBrowseButton(page, txtFieldCreator.getControl());
		return txtFieldCreator;
	}

	private AbstractTextFieldParameterPropertyUICreator createIDTextField(final Parameter p, final Composite page) {
		AbstractTextFieldParameterPropertyUICreator txtFieldCreator = new TextParameterPropertyUICreator(dbc, modelMap, eip, selectedEP, p, getValidatorForField(p), page, getWidgetFactory());
		txtFieldCreator.create();
		return txtFieldCreator;
	}

	private AbstractTextFieldParameterPropertyUICreator createTextField(final Parameter p, final Composite page) {
		AbstractTextFieldParameterPropertyUICreator txtFieldCreator = new TextParameterPropertyUICreator(dbc, modelMap, eip, selectedEP, p, getValidatorForField(p), page, getWidgetFactory());
		txtFieldCreator.create();
		return txtFieldCreator;
	}

	private ScopeAttributeComboFieldPropertyUICreator createScopeCombo(final Parameter p, final Composite page) {
		ScopeAttributeComboFieldPropertyUICreator scopeFieldCreator = new ScopeAttributeComboFieldPropertyUICreator(dbc, modelMap, eip, selectedEP, p, page, getWidgetFactory());
		scopeFieldCreator.create();
		return scopeFieldCreator;
	}

	private BeanRefAttributeComboFieldPropertyUICreator createRefCombo(final Parameter p, final Composite page) {
		BeanRefAttributeComboFieldPropertyUICreator refFieldCreator = new BeanRefAttributeComboFieldPropertyUICreator(
				dbc, modelMap, eip, selectedEP, p, page, getWidgetFactory(), new ComboParameterPropertyModifyListener(selectedEP, p.getName()), getValidatorForField(p));
		refFieldCreator.create();
		return refFieldCreator;
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
			// if the class isn't set explicitly, see if there's a bean reference
			if (Strings.isEmpty((String) control)) {
				Object ref = modelMap.get(beanConfigUtil.getFactoryBeanTag(selectedEP.getXmlNode()));
				control = beanConfigUtil.getClassNameFromReferencedCamelBean(selectedEP, (String) ref);
			}
			if (control != null) {
				final IProject project = selectedEP.getCamelFile().getResource().getProject();
				String className = (String) control;
				String methodName;
				switch (methodBrowseType) {
				case PUBLIC_AND_STATIC_METHOD_BROWSE:
					methodName = beanConfigUtil.handlePublicAndStaticMethodBrowse(project, className,
							getDisplay().getActiveShell());
					break;
				case PUBLIC_NO_ARG_METHOD_BROWSE:
					methodName = beanConfigUtil.handlePublicNoArgMethodBrowse(project, className,
							getDisplay().getActiveShell());
					break;
				case PUBLIC_OR_STATIC_METHOD_BROWSE:
					methodName = beanConfigUtil.handlePublicOrStaticMethodBrowse(project, className,
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

	private void createPublicOrStaticMethodBrowseButton(Composite composite, Text field) {
		createMethodBrowseBtn(composite, field, PUBLIC_OR_STATIC_METHOD_BROWSE);
	}
}
