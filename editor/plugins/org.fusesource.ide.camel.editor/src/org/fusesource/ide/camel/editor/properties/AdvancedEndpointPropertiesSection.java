/*******************************************************************************
 * Copyright (c) 2014-2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.camel.editor.properties.creators.advanced.BooleanParameterPropertyUICreatorForAdvanced;
import org.fusesource.ide.camel.editor.properties.creators.advanced.FileParameterPropertyUICreatorForAdvanced;
import org.fusesource.ide.camel.editor.properties.creators.advanced.NumberParameterPropertyUICreatorForAdvanced;
import org.fusesource.ide.camel.editor.properties.creators.advanced.TextParameterPropertyUICreatorForAdvanced;
import org.fusesource.ide.camel.editor.properties.creators.advanced.UnsupportedParameterPropertyUICreatorForAdvanced;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.UriParameterKind;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.util.CamelComponentUtils;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author lhein
 */
public class AdvancedEndpointPropertiesSection extends FusePropertySection {

	/**
	 * 
	 * @param folder
	 */
	@Override
	protected void createContentTabs(CTabFolder folder) {
		List<Parameter> props = PropertiesUtils.getComponentPropertiesFor(selectedEP);

		if (props.isEmpty()){
			return;
		}

		List<String> tabsToCreate = computeTabsToCreate(props);

		props.sort(new ParameterPriorityComparator());

		for (String group : tabsToCreate) {
			CTabItem contentTab = new CTabItem(this.tabFolder, SWT.NONE);
			contentTab.setText(Strings.humanize(group));

			Composite page = this.toolkit.createComposite(folder);
			page.setLayout(new GridLayout(4, false));

			if (GROUP_PATH.equalsIgnoreCase(group)) {
				generateTabContents(PropertiesUtils.getPathProperties(selectedEP), page, false, group);
			} else {
				generateTabContents(props, page, true, group);            	
			}	

			contentTab.setControl(page);        	

			this.tabs.add(contentTab);
		}
	}

	List<String> computeTabsToCreate(List<Parameter> props) {
		List<String> tabsToCreate = new ArrayList<>();
		// path tab is always there
		tabsToCreate.add(GROUP_PATH);
		if (shouldCreateTabsForKind(UriParameterKind.BOTH)) {
			tabsToCreate.add(GROUP_COMMON);
		}
		if (shouldCreateTabsForKind(UriParameterKind.CONSUMER)) {
			tabsToCreate.add(GROUP_CONSUMER);
		}
		if (shouldCreateTabsForKind(UriParameterKind.PRODUCER)) {
			tabsToCreate.add(GROUP_PRODUCER);
		}

		for (Parameter p : props) {
			String parameterGroup = p.getGroup();
			if (parameterGroup != null && parameterGroup.trim().length() > 0 && !tabsToCreate.contains(parameterGroup)) {
				tabsToCreate.add(parameterGroup);
			}
		}
		return tabsToCreate;
	}

	private boolean shouldCreateTabsForKind(UriParameterKind parameterKind) {
		List<Parameter> commonProps = PropertiesUtils.getPropertiesFor(selectedEP, parameterKind);
		return commonProps.parallelStream()
				.filter(p -> p.getGroup() == null || p.getGroup().trim().length() < 1)
				.findFirst()
				.isPresent();
	}

	/**
	 * 
	 * @param props
	 * @param page
	 * @param ignorePathProperties
	 * @param group
	 */
	protected void generateTabContents(List<Parameter> props, final Composite page, boolean ignorePathProperties, String group) {
		props.sort(new ParameterPriorityComparator());
		for (Parameter p : props) {
			final Parameter prop = p;

			if (!shouldBeDisplayed(prop, group, ignorePathProperties)){
				continue;
			}

			ISWTObservableValue uiObservable = null;
			IObservableValue<Object> modelObservable;
			IValidator validator = null;

			createPropertyLabel(toolkit, page, p);

			Control c = null;

			// BOOLEAN PROPERTIES
			if (CamelComponentUtils.isBooleanProperty(prop)) {
				new BooleanParameterPropertyUICreatorForAdvanced(dbc, modelMap, eip, selectedEP, p, page, getWidgetFactory()).create();
				// TEXT PROPERTIES
			} else if (CamelComponentUtils.isTextProperty(prop) || CamelComponentUtils.isCharProperty(prop)) {
				new TextParameterPropertyUICreatorForAdvanced(dbc, modelMap, eip, selectedEP, p, page, getWidgetFactory()).create();
				// NUMBER PROPERTIES
			} else if (CamelComponentUtils.isNumberProperty(prop)) {
				new NumberParameterPropertyUICreatorForAdvanced(dbc, modelMap, eip, selectedEP, prop, page, getWidgetFactory()).create();
				// CHOICE PROPERTIES
			} else if (CamelComponentUtils.isChoiceProperty(prop)) {
				CCombo choiceCombo = new CCombo(page, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY | SWT.SINGLE);
				toolkit.adapt(choiceCombo, true, true);
				choiceCombo.setEditable(false);
				choiceCombo.setItems(CamelComponentUtils.getChoicesWithExtraEmptyEntry(prop));
				String selectedValue = PropertiesUtils.getPropertyFromUri(selectedEP, prop, component);
				for (int i=0; i < choiceCombo.getItems().length; i++) {
					if (selectedValue != null && choiceCombo.getItem(i).equalsIgnoreCase(selectedValue)) {
						choiceCombo.select(i);
						break;
					} else if (selectedValue == null && p.getDefaultValue() != null && choiceCombo.getItem(i).equalsIgnoreCase(p.getDefaultValue())) {
						choiceCombo.select(i);
						break;
					}
				}
				choiceCombo.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						CCombo choice = (CCombo)e.getSource();
						String newValue = choice.getText();
						PropertiesUtils.updateURIParams(selectedEP, prop, newValue, component, modelMap);
						if (AbstractCamelModelElement.PARAMETER_LANGUAGENAME.equalsIgnoreCase(p.getName())) {
							updateDependenciesForLanguage(selectedEP, newValue);
						}
					}
				});
				choiceCombo.setLayoutData(createPropertyFieldLayoutData());
				c = choiceCombo;
				//initialize the map entry
				modelMap.put(p.getName(), choiceCombo.getText());
				// create observables for the control
				uiObservable = WidgetProperties.ccomboSelection().observe(choiceCombo);                
				if (PropertiesUtils.isRequired(p)) {
					validator = new IValidator() {
						@Override
						public IStatus validate(Object value) {
							if (value != null && value instanceof String && value.toString().trim().length()>0) {
								return ValidationStatus.ok();
							}
							return ValidationStatus.error("Parameter " + prop.getName() + " is a mandatory field and cannot be empty.");
						}
					};
				}
				// FILE PROPERTIES
			} else if (CamelComponentUtils.isFileProperty(prop)) {
				new FileParameterPropertyUICreatorForAdvanced(dbc, modelMap, eip, selectedEP, p, page, getWidgetFactory()).create();
				// EXPRESSION PROPERTIES
			} else if (CamelComponentUtils.isExpressionProperty(prop)) {
				Text txtField = toolkit.createText(page, PropertiesUtils.getPropertyFromUri(selectedEP, prop, component), SWT.SINGLE | SWT.BORDER | SWT.LEFT);
				txtField.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						Text txt = (Text)e.getSource();
						PropertiesUtils.updateURIParams(selectedEP, prop, txt.getText(), component, modelMap);
					}
				});
				txtField.setLayoutData(createPropertyFieldLayoutData());
				c = txtField;
				if (PropertiesUtils.isRequired(p)) {
					validator = new IValidator() {
						/*
						 * (non-Javadoc)
						 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
						 */
						@Override
						public IStatus validate(Object value) {
							if (value != null && value instanceof String && value.toString().trim().length()>0) {
								return ValidationStatus.ok();
							}
							return ValidationStatus.error("Parameter " + prop.getName() + " is a mandatory field and cannot be empty.");
						}
					};
				}
				//initialize the map entry
				modelMap.put(p.getName(), txtField.getText());
				// create observables for the control
				uiObservable = WidgetProperties.text(SWT.Modify).observe(txtField);                

				// DATAFORMAT PROPERTIES
			} else if (CamelComponentUtils.isDataFormatProperty(prop)) {
				Text txtField = toolkit.createText(page, PropertiesUtils.getPropertyFromUri(selectedEP, prop, component), SWT.SINGLE | SWT.BORDER | SWT.LEFT);
				txtField.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						Text txt = (Text)e.getSource();
						String newValue = txt.getText();
						PropertiesUtils.updateURIParams(selectedEP, prop, newValue, component, modelMap);
						updateDependenciesForDataFormat(selectedEP, newValue);
					}
				});
				txtField.setLayoutData(createPropertyFieldLayoutData());
				c = txtField;
				if (PropertiesUtils.isRequired(p)) {
					validator = new IValidator() {
						/*
						 * (non-Javadoc)
						 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
						 */
						@Override
						public IStatus validate(Object value) {
							if (value != null && value instanceof String && value.toString().trim().length()>0) {
								return ValidationStatus.ok();
							}
							return ValidationStatus.error("Parameter " + prop.getName() + " is a mandatory field and cannot be empty.");
						}
					};
				}
				//initialize the map entry
				modelMap.put(p.getName(), txtField.getText());
				// create observables for the control
				uiObservable = WidgetProperties.text(SWT.Modify).observe(txtField);                

				// UNSUPPORTED PROPERTIES / REFS
			} else if (CamelComponentUtils.isUnsupportedProperty(prop)) {
				// TODO: check how to handle lists and maps - for now we treat
				// them as string field only --> in DetailsSection seems that
				// there is something to handle that
				new UnsupportedParameterPropertyUICreatorForAdvanced(dbc, modelMap, eip, selectedEP, p, page, getWidgetFactory()).create();
				// CLASS BASED PROPERTIES - REF OR CLASSNAMES AS STRINGS
			} else {
				new TextParameterPropertyUICreatorForAdvanced(dbc, modelMap, eip, selectedEP, p, page, getWidgetFactory()).create();
			}

			if (uiObservable != null) {
				// create UpdateValueStrategy and assign to the binding
				UpdateValueStrategy strategy = new UpdateValueStrategy();
				strategy.setBeforeSetValidator(validator);

				// create observables for the Map entries
				modelObservable = Observables.observeMapEntry(modelMap, p.getName());
				// bind the observables
				Binding bindValue = dbc.bindValue(uiObservable, modelObservable, strategy, null);
				ControlDecorationSupport.create(bindValue, SWT.TOP | SWT.LEFT);

				if (p.getDescription() != null)
					c.setToolTipText(p.getDescription());
			}
		}
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		this.toolkit = new FormToolkit(parent.getDisplay());
		super.createControls(parent, aTabbedPropertySheetPage);

		createStandardTabLayout("Advanced Properties");
	}

	private boolean shouldBeDisplayed(Parameter prop, String group, boolean ignorePathProperties) {
		// atm we don't want to care about path parameters if thats not the path tab
		if (ignorePathProperties && "path".equalsIgnoreCase(prop.getKind())){
			return false;
		}

		// we currently don't want to display class parameters of type element
		if (CamelComponentUtils.isClassProperty(prop) && AbstractCamelModelElement.NODE_KIND_ELEMENT.equalsIgnoreCase(prop.getKind())){
			return false;
		}

		// we don't display items which don't fit the group
		if (prop.getGroup() != null && prop.getGroup().trim().length()>0) {
			// a group has been explicitly defined, so use it
			if (!group.equalsIgnoreCase(prop.getGroup()) && !GROUP_PATH.equalsIgnoreCase(prop.getKind())) {
				return false;
			}
		} else if (GROUP_PATH.equalsIgnoreCase(prop.getKind()) && GROUP_PATH.equalsIgnoreCase(group)) {
			// special handling for path properties - otherwise the else would kick all props of type path
		} else {
			// no group defined, fall back to use label
			if (prop.getLabel() != null && !PropertiesUtils.containsLabel(group, prop)){
				return false;
			}
			if (prop.getLabel() == null && !GROUP_COMMON.equalsIgnoreCase(group)){
				return false;
			}
		}
		return true;
	}
}
