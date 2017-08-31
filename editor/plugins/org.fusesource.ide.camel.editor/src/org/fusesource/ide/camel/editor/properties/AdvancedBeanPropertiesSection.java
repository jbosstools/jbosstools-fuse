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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
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
import org.fusesource.ide.camel.editor.properties.bean.BeanRefAttributeComboFieldPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.bean.NewBeanIdPropertyValidator;
import org.fusesource.ide.camel.editor.properties.bean.PropertyMethodValidator;
import org.fusesource.ide.camel.editor.properties.bean.PropertyRefValidator;
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

	private static final int PUBLIC_AND_STATIC_METHOD_BROWSE = 1;
	private static final int PUBLIC_NO_ARG_METHOD_BROWSE = 2;
	private static final int PUBLIC_OR_STATIC_METHOD_BROWSE = 3;
	
	private BeanConfigUtil beanConfigUtil = new BeanConfigUtil();
	private List<Parameter> parameterList;
	private Map<String, IObservableValue<?>> modelValueMap;
	private Map<String, IObservableValue<?>> targetValueMap;
	private String factoryBeanTag;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		this.toolkit = new FormToolkit(parent.getDisplay());
		super.createControls(parent, aTabbedPropertySheetPage);
	
		createStandardTabLayout(UIMessages.advancedBeanPropertiesSectionTitle);
	}

	private void createParameterList() {
		parameterList = new ArrayList<>();

		// define the properties we're handling here
		Parameter idParam = beanConfigUtil.createParameter(GlobalBeanEIP.PROP_ID, String.class.getName());
		idParam.setRequired("true"); //$NON-NLS-1$
		parameterList.add(idParam);
		Parameter classParam = beanConfigUtil.createParameter(GlobalBeanEIP.PROP_CLASS, String.class.getName());
		classParam.setRequired("true"); //$NON-NLS-1$
		parameterList.add(classParam);
		parameterList.add(beanConfigUtil.createParameter(GlobalBeanEIP.PROP_SCOPE, String.class.getName()));
		parameterList.add(beanConfigUtil.createParameter(GlobalBeanEIP.PROP_DEPENDS_ON, String.class.getName()));
		parameterList.add(beanConfigUtil.createParameter(GlobalBeanEIP.PROP_INIT_METHOD, String.class.getName()));
		parameterList.add(beanConfigUtil.createParameter(GlobalBeanEIP.PROP_DESTROY_METHOD, String.class.getName()));

		factoryBeanTag = beanConfigUtil.getFactoryBeanTag(selectedEP.getXmlNode());
		Parameter factoryBeanParameter = beanConfigUtil.createParameter(factoryBeanTag, String.class.getName());
		parameterList.add(factoryBeanParameter);
		
		String factoryAttribute = beanConfigUtil.getFactoryMethodAttribute(selectedEP.getXmlNode());
		parameterList.add(beanConfigUtil.createParameter(factoryAttribute, String.class.getName()));

		parameterList.sort(new ParameterPriorityComparator());
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

	/**
	 * 
	 * @param props
	 * @param page
	 * @param ignorePathProperties
	 * @param group
	 */
	protected void generateTabContents(List<Parameter> props, final Composite page) {
		modelValueMap = new HashMap<>();
		targetValueMap = new HashMap<>();
		for (Parameter p : props) {
			createPropertyLabel(toolkit, page, p);
			AbstractParameterPropertyUICreator creator = createPropertyFieldEditor(page, p);
			if (creator != null) {
				IObservableValue<?> modelValue = (IObservableValue<?>) creator.getBinding().getModel();
				IObservableValue<?> targetValue = (IObservableValue<?>) creator.getUiObservable();
				modelValueMap.put(p.getName(), modelValue);
				targetValueMap.put(p.getName(), targetValue);
			}
		}

		IObservableValue<?> classNameObservable = modelValueMap.get(GlobalBeanEIP.PROP_CLASS);
		IObservableValue<?> beanRefObservable = modelValueMap.get(factoryBeanTag);
		MultiValidator beanClassAndReferenceValidator = new BeanClassOrReferenceValidator(
				selectedEP.getCamelFile().getResource().getProject(), classNameObservable, beanRefObservable);
		dbc.addValidationStatusProvider(beanClassAndReferenceValidator);

//		final IObservableValue<?> classNameTargetObservable = targetValueMap.get(GlobalBeanEIP.PROP_CLASS);
//		final IObservableValue<?> beanRefTargetObservable = targetValueMap.get(factoryBeanTag);
		ControlDecorationSupport.create(beanClassAndReferenceValidator, SWT.TOP | SWT.LEFT, parent);
	}
	
	class BeanClassOrReferenceValidator extends MultiValidator {
		
		private final IObservableValue<?> classNameObservable;
		private final IObservableValue<?> beanRefObservable;
		private final IProject project;
		
		BeanClassOrReferenceValidator(final IProject project,
				final IObservableValue<?> classNameObservable,
				final IObservableValue<?> beanRefObservable) {
			this.project = project;
			this.classNameObservable = classNameObservable;
			this.beanRefObservable = beanRefObservable;
		}

		@Override
		protected IStatus validate() {
			final String className = (String) classNameObservable.getValue();
			final String beanRefId = (String) beanRefObservable.getValue();
			if (Strings.isEmpty(className) && Strings.isEmpty(beanRefId)) {
				return ValidationStatus.error("Must specify either an explicit class name in the project or a reference to a global bean that exposes one.");
			}
			if (!Strings.isEmpty(className) && !Strings.isEmpty(beanRefId)) {
				return ValidationStatus.error("Must specify either an explicit class name in the project or a reference to a global bean that exposes one, not both.");
			}
			
			String referencedClassName = beanConfigUtil.getClassNameFromReferencedCamelBean(selectedEP, beanRefId);
			IStatus firstStatus = classExistsInProject(referencedClassName);
			if (firstStatus != ValidationStatus.ok() && !Strings.isEmpty(className)) {
				return classExistsInProject(className);
			}
			return ValidationStatus.ok();
		}
		
		private IStatus classExistsInProject(String className) {
			if (className == null || className.isEmpty()) {
				return ValidationStatus.error(UIMessages.beanClassExistsValidatorErrorBeanClassMandatory);
			}
			IJavaProject javaProject = JavaCore.create(this.project);
	        IType javaClass;
			try {
				javaClass = javaProject == null ? null : javaProject.findType(className);
				if (javaClass == null) {
					return ValidationStatus.error(UIMessages.beanClassExistsValidatorErrorBeanClassMustExist);
				}
			} catch (JavaModelException e) {
				return ValidationStatus.error(UIMessages.beanClassExistsValidatorErrorBeanClassMustExist, e);
			}
			return ValidationStatus.ok();
		}
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
			createTextField(p, page);
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
			validator = new BeanClassExistsValidator(project);
		} else if (GlobalBeanEIP.PROP_INIT_METHOD.equals(propName)
				|| GlobalBeanEIP.PROP_DESTROY_METHOD.equals(propName)
				|| GlobalBeanEIP.PROP_FACTORY_METHOD.equals(propName)) {
			validator = new PropertyMethodValidator(modelMap, project);
		} else if (GlobalBeanEIP.PROP_FACTORY_BEAN.equals(propName) || GlobalBeanEIP.PROP_FACTORY_REF.equals(propName)) {
			validator = new PropertyRefValidator(p, selectedEP);
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
	
	private AbstractTextFieldParameterPropertyUICreator createTextFieldWithPublicStaticMethodBrowse(final Parameter p, final Composite page) {
		AbstractTextFieldParameterPropertyUICreator txtFieldCreator = new AttributeTextFieldPropertyUICreatorWithBrowse(dbc, modelMap, eip, selectedEP, p,  getValidatorForField(p), page, getWidgetFactory());
		txtFieldCreator.setColumnSpan(2);
		txtFieldCreator.create();
		createPublicAndStaticMethodBrowseButton(page, txtFieldCreator.getControl());
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
		BeanRefAttributeComboFieldPropertyUICreator refFieldCreator = new BeanRefAttributeComboFieldPropertyUICreator(dbc, modelMap, eip, selectedEP, p, page, getWidgetFactory());
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
	private void createPublicAndStaticMethodBrowseButton(Composite composite, Text field) {
		createMethodBrowseBtn(composite, field, PUBLIC_AND_STATIC_METHOD_BROWSE);
	}
	
	
}
