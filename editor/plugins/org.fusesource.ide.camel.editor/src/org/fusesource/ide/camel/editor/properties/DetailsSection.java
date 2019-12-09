/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
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
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
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
import org.eclipse.swt.widgets.Group;
import org.fusesource.ide.camel.editor.properties.creators.TextParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.details.BooleanParameterPropertyUICreatorForDetails;
import org.fusesource.ide.camel.editor.properties.creators.details.DescriptionParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.details.FileParameterPropertyUICreatorForDetails;
import org.fusesource.ide.camel.editor.properties.creators.details.NumberParameterPropertyUICreatorForDetails;
import org.fusesource.ide.camel.editor.properties.creators.details.UnsupportedParameterPropertyUICreatorForDetails;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.util.CamelComponentUtils;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.fusesource.ide.camel.validation.model.RefOrDataFormatUnicityChoiceValidator;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * Shows the property details for the currently selected node
 */
public class DetailsSection extends FusePropertySection {

    /**
     * 
     * @param folder the CTabFolder in which content will be created
     */
	@Override
    protected void createContentTabs(CTabFolder folder) {
        List<Parameter> props = PropertiesUtils.getPropertiesFor(selectedEP);

        if (props.isEmpty()) {
        	return;
        }
       
        boolean createGeneralTab = false;
        List<String> tabsToCreate = new ArrayList<>();
        for (Parameter p : props) {
        	String parameterGroup = p.getGroup();
			if (parameterGroup != null && parameterGroup.trim().length() > 0 && !tabsToCreate.contains(parameterGroup)) {
        		tabsToCreate.add(parameterGroup);
        	} else if (parameterGroup == null || parameterGroup.trim().length() < 1) {
        		createGeneralTab = true;
        	}
        }
        // groups were introduced in Camel 2.16.x -> earlier versions might not have it
        if (tabsToCreate.isEmpty() || createGeneralTab){
        	tabsToCreate.add(DEFAULT_GROUP);
        }
        
        for (String group : tabsToCreate) {
        	CTabItem contentTab = new CTabItem(this.tabFolder, SWT.NONE);
            contentTab.setText(Strings.humanize(group));

            Composite page = this.toolkit.createComposite(folder);
            page.setLayout(new GridLayout(4, false));
                    
            generateTabContents(props, page, false, group);

            contentTab.setControl(page);        	
            
            this.tabs.add(contentTab);
        }
    }
    
    /**
     * 
     * @param props
     * @param page
     * @param ignorePathProperties
     * @param group
     */
    protected void generateTabContents(List<Parameter> props, final Composite page, boolean ignorePathProperties, final String group) {
    	props.sort(new ParameterPriorityComparator());
        for (Parameter p : props) {
        	final Parameter prop = p;

        	String currentPropertyGroup = prop.getGroup();
			if (shouldHidePropertyFromGroup(group, p, currentPropertyGroup)){
        		continue;
        	}
        	
            ISWTObservableValue uiObservable = null;
            IObservableList<Object> uiListObservable = null;
            IObservableValue<Object> modelObservable;
            IObservableList<Object> modelListObservable;
            IValidator validator = null;
            
			createPropertyLabel(toolkit, page, p);
            
            Control c = null;
            
            // DESCRIPTION PROPERTIES
            if (CamelComponentUtils.isDescriptionProperty(prop)) {
				new DescriptionParameterPropertyUICreator(dbc, modelMap, eip, selectedEP, p, page, getWidgetFactory()).create();
            } else if (CamelComponentUtils.isBooleanProperty(prop)) {
				new BooleanParameterPropertyUICreatorForDetails(dbc, modelMap, eip, selectedEP, p, page, getWidgetFactory()).create();
            } else if (CamelComponentUtils.isTextProperty(prop) || CamelComponentUtils.isCharProperty(prop)) {
				new TextParameterPropertyUICreator(dbc, modelMap, eip, selectedEP, p, null, page, getWidgetFactory()).create();
            } else if (CamelComponentUtils.isNumberProperty(prop)) {
				new NumberParameterPropertyUICreatorForDetails(dbc, modelMap, eip, selectedEP, p, page, getWidgetFactory()).create();
            } else if (CamelComponentUtils.isChoiceProperty(prop)) {
                CCombo choiceCombo = new CCombo(page, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY | SWT.SINGLE);
                getWidgetFactory().adapt(choiceCombo, true, true);
                choiceCombo.setEditable(false);
                choiceCombo.setItems(CamelComponentUtils.getChoicesWithExtraEmptyEntry(prop));
                String value = (String)(this.selectedEP.getParameter(p.getName()) != null ? this.selectedEP.getParameter(p.getName()) : this.eip.getParameter(p.getName()).getDefaultValue());
                for (int i=0; i < choiceCombo.getItems().length; i++) {
                    if (choiceCombo.getItem(i).equalsIgnoreCase(value)) {
                        choiceCombo.select(i);
                        break;
                    }
                }
                choiceCombo.addSelectionListener(new SelectionAdapter() {
                    /* (non-Javadoc)
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        CCombo choice = (CCombo)e.getSource();
                        selectedEP.setParameter(prop.getName(), choice.getText());
                    }
                });
				choiceCombo.setLayoutData(createPropertyFieldLayoutData());
                c = choiceCombo;
                //initialize the map entry
                modelMap.put(p.getName(), choiceCombo.getText());
                // create observables for the control
                uiObservable = WidgetProperties.selection().observe(choiceCombo);                
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
                
            // REF PROPERTIES
            } else if (CamelComponentUtils.isRefProperty(prop)) {
                CCombo choiceCombo = new CCombo(page, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY | SWT.SINGLE);
                getWidgetFactory().adapt(choiceCombo, true, true);
                choiceCombo.setEditable(true);
                choiceCombo.setItems(CamelComponentUtils.getRefs(this.selectedEP.getCamelFile()));
                String value = (String)(this.selectedEP.getParameter(p.getName()) != null ? this.selectedEP.getParameter(p.getName()) : this.eip.getParameter(p.getName()).getDefaultValue());
                boolean selected = false;
                for (int i=0; i < choiceCombo.getItems().length; i++) {
                    if (choiceCombo.getItem(i).equalsIgnoreCase(value)) {
                        choiceCombo.select(i);
                        selected = true;
                        break;
                    }
                }
                if (!selected && value != null) {
                	choiceCombo.setText(value);
                }
                choiceCombo.addSelectionListener(new SelectionAdapter() {
                    /* (non-Javadoc)
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        CCombo choice = (CCombo)e.getSource();
                        selectedEP.setParameter(prop.getName(), choice.getText());
                    }
                });
                choiceCombo.addModifyListener(new ModifyListener() {
					
					@Override
					public void modifyText(ModifyEvent e) {
						CCombo choice = (CCombo)e.getSource();
                        selectedEP.setParameter(prop.getName(), choice.getText());						
					}
				});
				choiceCombo.setLayoutData(createPropertyFieldLayoutData());
                c = choiceCombo;
                //initialize the map entry
                modelMap.put(p.getName(), choiceCombo.getText());
                // create observables for the control
                uiObservable = WidgetProperties.selection().observe(choiceCombo);                
				validator = new IValidator() {
					@Override
					public IStatus validate(Object value) {
						// check if value has content
						if (PropertiesUtils.isRequired(prop)) {
							if (value == null || !(value instanceof String) || value.toString().trim().length()<1) {
								return ValidationStatus.error("Parameter " + prop.getName() + " is a mandatory field and cannot be empty.");	
							}	
						} else {
							if (isNotEmptyString(value)
									&& selectedEP.getRouteContainer().findNode((String)value) == null &&
									!selectedEP.getCamelFile().getGlobalDefinitions().containsKey((String)value)) {
								// no ref found - could be something the server provides
								return ValidationStatus.warning("Parameter " + prop.getName() + " does not point to an existing reference inside the context.");
							}
						}
						return new RefOrDataFormatUnicityChoiceValidator(selectedEP, prop).validate(value);
					}

					private boolean isNotEmptyString(Object value) {
						return value != null && value instanceof String && value.toString().trim().length()>0;
					}
				};
                
            // FILE PROPERTIES
            } else if (CamelComponentUtils.isFileProperty(prop)) {
				new FileParameterPropertyUICreatorForDetails(dbc, modelMap, eip, selectedEP, p, page, getWidgetFactory()).create();
            } else if (CamelComponentUtils.isListProperty(prop)) {
            	org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(page, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY | SWT.SINGLE);
                getWidgetFactory().adapt(list, true, true);
				list.setLayoutData(createPropertyFieldLayoutData());
                
                ArrayList<String> listElements = this.selectedEP.getParameter(prop.getName()) != null ? (ArrayList<String>)this.selectedEP.getParameter(prop.getName()) : new ArrayList<String>();
                list.setItems(listElements.toArray(new String[listElements.size()]));
                
                c = list;
                //initialize the map entry
                modelMap.put(p.getName(), Arrays.asList(list.getItems()));
                // create observables for the control
                uiListObservable = WidgetProperties.items().observe(list);                
                if (PropertiesUtils.isRequired(p)) {
					validator = new IValidator() {
						@Override
						public IStatus validate(Object value) {
							if (value != null && value instanceof List && !((List<?>)value).isEmpty()) {
								return ValidationStatus.ok();
							}
							return ValidationStatus.error("Parameter " + prop.getName() + " is a mandatory field and cannot be empty.");
						}
					};
                }
                
            // EXPRESSION PROPERTIES
            } else if (CamelComponentUtils.isExpressionProperty(prop)) {
            	CCombo choiceCombo = new CCombo(page, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY | SWT.SINGLE);
            	deactivateMouseWheel(choiceCombo);
                getWidgetFactory().adapt(choiceCombo, true, true);
                choiceCombo.setEditable(false);
				choiceCombo.setLayoutData(createPropertyFieldLayoutData());
                
                final AbstractCamelModelElement expressionElement = this.selectedEP.getParameter(prop.getName()) != null ? (AbstractCamelModelElement)this.selectedEP.getParameter(prop.getName()) : null;
                choiceCombo.setItems(CamelComponentUtils.getOneOfList(prop));

                final Composite eform = getWidgetFactory().createFlatFormComposite(page);
				eform.setLayoutData(GridDataFactory.fillDefaults().indent(5, 0).span(4, 1).grab(true, false).create());
                eform.setLayout(new GridLayout(1, true));

                choiceCombo.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        CCombo choice = (CCombo)e.getSource();
                        String language = choice.getText();
                        languageChanged(language, eform, selectedEP.getParameter(prop.getName()) != null ? (AbstractCamelModelElement)selectedEP.getParameter(prop.getName()) : null, page, prop);
                    }
                });
                
				if (expressionElement != null) {
					String value = expressionElement.getNodeTypeId();
					Object expressionParameterValue = expressionElement.getParameter("expression");
					if (expressionParameterValue != null && expressionParameterValue instanceof AbstractCamelModelElement ) {
						AbstractCamelModelElement ex = (AbstractCamelModelElement)expressionParameterValue;
	                    value = ex.getTagNameWithoutPrefix();
					}
                    choiceCombo.deselectAll();
                    for (int i=0; i < choiceCombo.getItems().length; i++) {
                        if (choiceCombo.getItem(i).equalsIgnoreCase(value)) {
                        	choiceCombo.select(i);
                        	languageChanged(value, eform, expressionElement, page, prop);
                            break;
                        }
                    }
                }		
                
                c = choiceCombo;
                //initialize the map entry
                modelMap.put(p.getName(), choiceCombo.getText());
                // create observables for the control
                uiObservable = WidgetProperties.selection().observe(choiceCombo);                
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

             // DATAFORMAT PROPERTIES
            } else if (CamelComponentUtils.isDataFormatProperty(prop)) {
            	CCombo choiceCombo = new CCombo(page, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY | SWT.SINGLE);
            	deactivateMouseWheel(choiceCombo);
                getWidgetFactory().adapt(choiceCombo, true, true);
                choiceCombo.setEditable(false);
				choiceCombo.setLayoutData(createPropertyFieldLayoutData());
                
				final AbstractCamelModelElement dataformatElement = selectedEP.getParameter(prop.getName()) != null
						? (AbstractCamelModelElement) selectedEP.getParameter(prop.getName()) : null;
                choiceCombo.setItems(CamelComponentUtils.getOneOfList(prop));

                final Composite eform = getWidgetFactory().createFlatFormComposite(page);
				eform.setLayoutData(GridDataFactory.fillDefaults().indent(5, 0).span(4, 1).grab(true, false).create());
                eform.setLayout(new GridLayout(1, true));

                choiceCombo.addSelectionListener(new SelectionAdapter() {
                	/* (non-Javadoc)
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        CCombo choice = (CCombo)e.getSource();
                        String dataformat = choice.getText();
                        dataFormatChanged(dataformat, eform, selectedEP.getParameter(prop.getName()) != null ? (AbstractCamelModelElement)selectedEP.getParameter(prop.getName()) : null, page, prop);
                    }
                });
                
				if (dataformatElement != null) {
					String value = dataformatElement.getNodeTypeId();
                    choiceCombo.deselectAll();
                    for (int i=0; i < choiceCombo.getItems().length; i++) {
                        if (choiceCombo.getItem(i).equalsIgnoreCase(value)) {
                        	choiceCombo.select(i);
                        	dataFormatChanged(value, eform, dataformatElement, page, prop);
                            break;
                        }
                    }
                }		
                
                c = choiceCombo;
                //initialize the map entry
                modelMap.put(p.getName(), choiceCombo.getText());
                // create observables for the control
                uiObservable = WidgetProperties.selection().observe(choiceCombo);                
                if (PropertiesUtils.isRequired(p)) {
					validator = new IValidator() {
						@Override
						public IStatus validate(Object value) {
							if (value != null && value instanceof String && value.toString().trim().length()>0) {
								return new RefOrDataFormatUnicityChoiceValidator(selectedEP, prop).validate(value);
							}
							return ValidationStatus.error("Parameter " + prop.getName() + " is a mandatory field and cannot be empty.");

						}
					};
                }
                
            // UNSUPPORTED PROPERTIES / REFS
            } else if (CamelComponentUtils.isUnsupportedProperty(prop)) {
				new UnsupportedParameterPropertyUICreatorForDetails(dbc, modelMap, eip, selectedEP, p, page, getWidgetFactory()).create();
            } else if ("redeliveryPolicy".equals(prop.getName())) {
				Object valueToDisplay = this.selectedEP.getParameter(p.getName()) != null ? this.selectedEP.getParameter(p.getName()) : this.eip.getParameter(p.getName()).getDefaultValue();
				if (valueToDisplay instanceof AbstractCamelModelElement) {
					Group objectGroup = getWidgetFactory().createGroup(page, "");
					objectGroup.setLayout(GridLayoutFactory.fillDefaults().numColumns(4).create());
					objectGroup.setLayoutData(GridDataFactory.fillDefaults().indent(5, 0).span(4, 1).grab(true, false).create());
					CamelModel camelModel = getCamelModel((AbstractCamelModelElement) valueToDisplay);
					final Eip eip = camelModel.getEip(prop.getName());
					for (Parameter childParameter : eip.getParameters()) {
						createPropertyLabel(toolkit, objectGroup, childParameter);

						// Field
						Control field = getControlForParameter(childParameter, objectGroup, (AbstractCamelModelElement) valueToDisplay, eip);
						field.setToolTipText(childParameter.getDescription());
					}
					c = objectGroup;
				}
			// CLASS BASED PROPERTIES - REF OR CLASSNAMES AS STRINGS
			} else {
				new TextParameterPropertyUICreator(dbc, modelMap, eip, selectedEP, p, null, page, getWidgetFactory()).create();
            }
            
			// bind the observables
			Binding bindValue = null;
			if (uiObservable != null) {
				// create observables for the Map entries
				modelObservable = Observables.observeMapEntry(modelMap, p.getName());

				// create UpdateValueStrategy and assign to the binding
				UpdateValueStrategy strategy = new UpdateValueStrategy();
				strategy.setBeforeSetValidator(validator);

				bindValue = dbc.bindValue(uiObservable, modelObservable, strategy, null);
			} else if (uiListObservable != null) {
				modelListObservable = Observables.staticObservableList((List) modelMap.get(p.getName()), String.class);
				UpdateListStrategy listStrategy = new UpdateListStrategy() {
					@Override
					protected IStatus doAdd(IObservableList observableList, Object element, int index) {
						super.doAdd(observableList, element, index);
						return validateMandatoryBehavior(prop, observableList);
					}

					@Override
					protected IStatus doRemove(IObservableList observableList, int index) {
						super.doRemove(observableList, index);
						return validateMandatoryBehavior(prop, observableList);
					}
					
					private IStatus validateMandatoryBehavior(final Parameter prop, IObservableList observableList) {
						if (prop.getRequired() != null && "true".equalsIgnoreCase(prop.getRequired()) && observableList.isEmpty()) {
							return ValidationStatus.error("Parameter " + prop.getName() + " is a mandatory field and cannot be empty.");
						}
						return ValidationStatus.ok();
					}
				};
				bindValue = dbc.bindList(uiListObservable, modelListObservable, listStrategy, null);
			}
			if (bindValue != null) {
				ControlDecorationSupport.create(bindValue, SWT.TOP | SWT.LEFT);
			}

			if (c != null) {
				createHelpDecoration(p, c);
			}
        }
        page.layout();
    }

	protected void deactivateMouseWheel(CCombo choiceCombo) {
		choiceCombo.addListener(SWT.MouseVerticalWheel, event -> event.doit = false);
		choiceCombo.addListener(SWT.MouseWheel, event -> event.doit = false);
	}

	boolean shouldHidePropertyFromGroup(final String group, Parameter p, String currentPropertyGroup) {
		return isNotMatchingGroup(group, currentPropertyGroup)
				|| isInternalElementToHide(p)
				|| isClassParamToHide(p);
	}

	private boolean isNotMatchingGroup(final String group, String currentPropertyGroup) {
		if(DEFAULT_GROUP.equals(group)){
			return currentPropertyGroup != null && !currentPropertyGroup.trim().isEmpty();
		} else {
			return !group.equals(currentPropertyGroup);
		}
	}

	private boolean isClassParamToHide(Parameter p) {
		return CamelComponentUtils.isClassProperty(p)
				&& AbstractCamelModelElement.NODE_KIND_ELEMENT.equalsIgnoreCase(p.getKind())
				&& !CamelComponentUtils.isDataFormatProperty(p)
				&& !CamelComponentUtils.isDescriptionProperty(p);
	}

	private boolean isInternalElementToHide(Parameter p) {
		return (AbstractCamelModelElement.NODE_KIND_ELEMENT.equalsIgnoreCase(p.getKind()) && "array".equalsIgnoreCase(p.getType()) && !"exception".equalsIgnoreCase(p.getName()))
				|| "org.apache.camel.model.OtherwiseDefinition".equals(p.getJavaType());
	}
    

}
