/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties;

import java.net.URLClassLoader;
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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jdt.internal.ui.wizards.NewClassCreationWizard;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
import org.fusesource.ide.camel.model.service.core.util.CamelComponentUtils;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.fusesource.ide.camel.validation.model.NumberValidator;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * Shows the property details for the currently selected node
 */
public class DetailsSection extends FusePropertySection {

    /**
     * 
     * @param folder
     */
    protected void createContentTabs(CTabFolder folder) {
        List<Parameter> props = PropertiesUtils.getPropertiesFor(selectedEP);

        if (props.isEmpty()) return;
       
        boolean createGeneralTab = false;
        List<String> tabsToCreate = new ArrayList<String>();
        for (Parameter p : props) {
        	if (p.getGroup() != null && p.getGroup().trim().length() > 0 && tabsToCreate.contains(p.getGroup()) == false) {
        		tabsToCreate.add(p.getGroup());
        	} else if (p.getGroup() == null || p.getGroup().trim().length() < 1) {
        		createGeneralTab = true;
        	}
        }
        // groups were introduced in Camel 2.16.x -> earlier versions might not have it
        if (tabsToCreate.isEmpty() || createGeneralTab) tabsToCreate.add(DEFAULT_GROUP);
        
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

        	// we don't display items which don't fit the group
        	if (group.equals(DEFAULT_GROUP) == false && group.equals(prop.getGroup()) == false) continue;
        	if (group.equals(DEFAULT_GROUP) && prop.getGroup() != null && prop.getGroup().trim().length()>0) continue;
        	
        	// we don't want to display properties for internal element attributes like inputs or outputs
        	if ((p.getKind().equalsIgnoreCase("element") && p.getType().equalsIgnoreCase("array") && p.getName().equalsIgnoreCase("exception") == false) || p.getJavaType().equals("org.apache.camel.model.OtherwiseDefinition")) continue;
        	
            ISWTObservableValue uiObservable = null;
            IObservableList uiListObservable = null;
            IObservableValue modelObservable = null;
            IObservableList modelListObservable = null;
            IValidator validator = null;
            
            createPropertyLabel(toolkit, page, p);
            
            Control c = null;
            
            // DESCRIPTION PROPERTIES
            if (CamelComponentUtils.isDescriptionProperty(prop)) {
            	String description = null;
            	if (this.selectedEP.getDescription() != null) {
            		description = this.selectedEP.getDescription();
            	} else {
            		description = this.eip.getParameter(p.getName()).getDefaultValue();
            	}
            	
            	Text txtField = getWidgetFactory().createText(page, description, SWT.SINGLE | SWT.BORDER | SWT.LEFT);
                txtField.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent e) {
                        Text txt = (Text)e.getSource();
                        selectedEP.setDescription(txt.getText());
                    }
                });
				txtField.setLayoutData(createPropertyFieldLayoutData());
                c = txtField;
                //initialize the map entry
                modelMap.put(p.getName(), txtField.getText());
                // create observables for the control
                uiObservable = WidgetProperties.text(SWT.Modify).observe(txtField);
                if (isRequired(p)) {
					validator = new IValidator() {
						/*
						 * (non-Javadoc)
						 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
						 */
						@Override
						public IStatus validate(Object value) {
							// TODO: add validation for descriptions (xml escape chars etc)
							return ValidationStatus.ok();
						}
					};
                }
            	
            // BOOLEAN PROPERTIES
            } else if (CamelComponentUtils.isBooleanProperty(prop)) {
				Button checkBox = getWidgetFactory().createButton(page, "", SWT.CHECK);
                Boolean b = Boolean.parseBoolean( (this.selectedEP.getParameter(p.getName()) != null ? this.selectedEP.getParameter(p.getName()).toString() : this.eip.getParameter(p.getName()).getDefaultValue()));
                checkBox.setSelection(b);
                checkBox.addSelectionListener(new SelectionAdapter() {
                    /* (non-Javadoc)
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                    	Button chkBox = (Button)e.getSource();
                        selectedEP.setParameter(prop.getName(), chkBox.getSelection());
                    }
                });
				checkBox.setLayoutData(createPropertyFieldLayoutData());
                c = checkBox;
                
                //initialize the map entry
                modelMap.put(p.getName(), checkBox.getSelection());
                // create observables for the control
                uiObservable = WidgetProperties.selection().observe(checkBox);
                
            // TEXT PROPERTIES
            } else if (CamelComponentUtils.isTextProperty(prop)) {
                Text txtField = getWidgetFactory().createText(page, (String)(this.selectedEP.getParameter(p.getName()) != null ? this.selectedEP.getParameter(p.getName()) : this.eip.getParameter(p.getName()).getDefaultValue()), SWT.SINGLE | SWT.BORDER | SWT.LEFT);
                txtField.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent e) {
                        Text txt = (Text)e.getSource();
                        selectedEP.setParameter(prop.getName(), txt.getText());
                    }
                });
				txtField.setLayoutData(createPropertyFieldLayoutData());
                c = txtField;
                //initialize the map entry
                modelMap.put(p.getName(), txtField.getText());
                // create observables for the control
                uiObservable = WidgetProperties.text(SWT.Modify).observe(txtField);
                if (isRequired(p) || p.getName().equalsIgnoreCase("id")) {
					validator = new IValidator() {
						/*
						 * (non-Javadoc)
						 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
						 */
						@Override
						public IStatus validate(Object value) {
							
							if (prop.getName().equalsIgnoreCase("uri")) {
								// only enforce URI if there is no REF set
								if (selectedEP.getParameter("uri") == null || ((String)selectedEP.getParameter("uri")).trim().length()<1) {
									// no URI set -> check for REF
									if (selectedEP.getParameter("ref") == null || ((String)selectedEP.getParameter("ref")).trim().length()<1) {
										// there is no ref 
										return ValidationStatus.error("One of Ref and Uri values have to be filled!");
									} else {
										// ref found - now check if REF has URI defined
										CamelModelElement cme = selectedEP.getCamelContext().findNode((String)selectedEP.getParameter("ref"));
										if (cme == null || cme.getParameter("uri") == null || ((String)cme.getParameter("uri")).trim().length()<1) {
											// no uri defined on ref
											return ValidationStatus.error("The referenced endpoint has no URI defined or does not exist.");
										}
									}
								}
								
								// check for broken refs							
								if (selectedEP.getParameter("uri") != null && ((String)selectedEP.getParameter("uri")).startsWith("ref:")) {
									String refId = ((String)selectedEP.getParameter("uri")).trim().length()>"ref:".length() ? ((String)selectedEP.getParameter("uri")).substring("ref:".length()) : null;
									List<String> refs = Arrays.asList(CamelComponentUtils.getRefs(selectedEP.getCamelFile()));
									if (refId == null || refId.trim().length()<1 || refs.contains(refId) == false) {
										return ValidationStatus.error("The entered reference does not exist in your context!");
									}
								}
								
								// warn user if he set both ref and uri
								if (selectedEP.getParameter("uri") != null && ((String)selectedEP.getParameter("uri")).trim().length()>0 &&
									selectedEP.getParameter("ref") != null && ((String)selectedEP.getParameter("ref")).trim().length()>0 ) {
									return ValidationStatus.warning("Please choose either URI or Ref but do not enter both values.");
								}
								
							} else if (prop.getName().equalsIgnoreCase("ref")) {

								if (value != null && value instanceof String && value.toString().trim().length()>0) {
									String refId = (String)value;
									CamelModelElement cme = selectedEP.getCamelContext().findNode(refId);
									if (cme == null) {
										// the ref doesn't exist
										return ValidationStatus.error("The entered reference does not exist in your context!");
									} else {
										// the ref exists
										if (cme.getParameter("uri") == null || ((String)cme.getParameter("uri")).trim().length()<1) {
											// but has no URI defined
											return ValidationStatus.error("The referenced endpoint does not define a valid URI!");
										}
									}
								}
								
								// warn user if he set both ref and uri
								if (selectedEP.getParameter("uri") != null && ((String)selectedEP.getParameter("uri")).trim().length()>0 &&
									selectedEP.getParameter("ref") != null && ((String)selectedEP.getParameter("ref")).trim().length()>0 ) {
									return ValidationStatus.warning("Please choose only ONE of Uri and Ref.");
								}

								
							} else if (prop.getName().equalsIgnoreCase("id")) {
								// check if ID is unique
								if (value == null || value instanceof String == false || value.toString().trim().length()<1) {
									return ValidationStatus.error("Parameter " + prop.getName() + " is a mandatory field and cannot be empty.");
								} else {
									if (selectedEP.getCamelContext().isIDUnique((String)value) == false) {
										return ValidationStatus.error("Parameter " + prop.getName() + " does not contain a unique value.");
									}
								}
							} else {
								// by default we only check for a value != null and length > 0
								if (value == null || value instanceof String == false || value.toString().trim().length()<1) {
									return ValidationStatus.error("Parameter " + prop.getName() + " is a mandatory field and cannot be empty.");
								}
							}
							// all checks passed
							return ValidationStatus.ok();
						}
					};
                }
                
            // NUMBER PROPERTIES
            } else if (CamelComponentUtils.isNumberProperty(prop)) {
                Text txtField = getWidgetFactory().createText(page, (String)(this.selectedEP.getParameter(p.getName()) != null ? this.selectedEP.getParameter(p.getName()) : this.eip.getParameter(p.getName()).getDefaultValue()), SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
                txtField.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent e) {
                        Text txt = (Text)e.getSource();
                        String val = txt.getText();
                        try {
                        	Double.parseDouble(val);
                        	txt.setBackground(ColorConstants.white);
                            selectedEP.setParameter(prop.getName(), txt.getText());
                        } catch (NumberFormatException ex) {
                        	// invalid character found
                            txt.setBackground(ColorConstants.red);
                            return;
                        }
                    }
                });
				txtField.setLayoutData(createPropertyFieldLayoutData());
                c = txtField;
                //initialize the map entry
                modelMap.put(p.getName(), txtField.getText());
                // create observables for the control
                uiObservable = WidgetProperties.text(SWT.Modify).observe(txtField);                
                validator = new IValidator() {
					/*
					 * (non-Javadoc)
					 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
					 */
					@Override
					public IStatus validate(Object value) {
						if (prop.getRequired() != null && prop.getRequired().equalsIgnoreCase("true") && (value == null || value.toString().trim().length()<1)) {
							return ValidationStatus.error("Parameter " + prop.getName() + " is a mandatory field and cannot be empty.");
						}
						return new NumberValidator(prop).validate(value);
					}
				};

			// CHOICE PROPERTIES
            } else if (CamelComponentUtils.isChoiceProperty(prop)) {
                CCombo choiceCombo = new CCombo(page, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY | SWT.SINGLE);
                getWidgetFactory().adapt(choiceCombo, true, true);
                choiceCombo.setEditable(false);
                choiceCombo.setItems(CamelComponentUtils.getChoices(prop));
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
                if (isRequired(p)) {
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
				choiceCombo.setLayoutData(createPropertyFieldLayoutData());
                c = choiceCombo;
                //initialize the map entry
                modelMap.put(p.getName(), choiceCombo.getText());
                // create observables for the control
                uiObservable = WidgetProperties.selection().observe(choiceCombo);                
				validator = new IValidator() {
					/*
					 * (non-Javadoc)
					 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
					 */
					@Override
					public IStatus validate(Object value) {
						// check if value has content
						if (isRequired(prop)) {
							if (value == null || value instanceof String == false || value.toString().trim().length()<1) {
								return ValidationStatus.error("Parameter " + prop.getName() + " is a mandatory field and cannot be empty.");	
							}	
						} else {
							if (value != null && value instanceof String && value.toString().trim().length()>0) {
								if (selectedEP.getCamelContext().findNode((String)value) == null) {
									// no ref found
									return ValidationStatus.error("Parameter " + prop.getName() + " does not point to an existing reference.");
								}
							}
						}							
						// all tests passed
						return ValidationStatus.ok();
						
					}
				};
                
            // FILE PROPERTIES
            } else if (CamelComponentUtils.isFileProperty(prop)) {
                final Text txtField = getWidgetFactory().createText(page, (String)(this.selectedEP.getParameter(p.getName()) != null ? this.selectedEP.getParameter(p.getName()) : this.eip.getParameter(p.getName()).getDefaultValue()), SWT.SINGLE | SWT.BORDER | SWT.LEFT);
                txtField.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent e) {
                        Text txt = (Text)e.getSource();
                        selectedEP.setParameter(prop.getName(), txt.getText());
                    }
                });
				txtField.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).indent(5, 0).create());
                
                Button btn_browse = getWidgetFactory().createButton(page, "...", SWT.BORDER | SWT.PUSH);
                btn_browse.addSelectionListener(new SelectionAdapter() {
                    /* (non-Javadoc)
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                    	DirectoryDialog dd = new DirectoryDialog(page.getShell());
                        String pathName = dd.open();
                        if (pathName != null) {
                            txtField.setText(pathName);
                        }
                    }
                });
                btn_browse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
                c = txtField;
                if (isRequired(p)) {
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

            // LIST PROPERTIES
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
                if (isRequired(p)) {
					validator = new IValidator() {
						/*
						 * (non-Javadoc)
						 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
						 */
						@Override
						public IStatus validate(Object value) {
							if (value != null && value instanceof List && ((List)value).isEmpty() == false) {
								return ValidationStatus.ok();
							}
							return ValidationStatus.error("Parameter " + prop.getName() + " is a mandatory field and cannot be empty.");
						}
					};
                }
                
            // EXPRESSION PROPERTIES
            } else if (CamelComponentUtils.isExpressionProperty(prop)) {
            	CCombo choiceCombo = new CCombo(page, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY | SWT.SINGLE);
                getWidgetFactory().adapt(choiceCombo, true, true);
                choiceCombo.setEditable(false);
				choiceCombo.setLayoutData(createPropertyFieldLayoutData());
                
                final CamelModelElement expressionElement = this.selectedEP.getParameter(prop.getName()) != null ? (CamelModelElement)this.selectedEP.getParameter(prop.getName()) : null;
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
                        String language = choice.getText();
                        languageChanged(language, eform, selectedEP.getParameter(prop.getName()) != null ? (CamelModelElement)selectedEP.getParameter(prop.getName()) : null, page, prop);
                    }
                });
                
				if (expressionElement != null) {
					String value = expressionElement.getNodeTypeId();
					if (expressionElement.getParameter("expression") != null && expressionElement.getParameter("expression") instanceof CamelModelElement ) {
						CamelModelElement ex = (CamelModelElement)expressionElement.getParameter("expression");
	                    value = ex.getTranslatedNodeName();
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
                if (isRequired(p)) {
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

             // DATAFORMAT PROPERTIES
            } else if (CamelComponentUtils.isDataFormatProperty(prop)) {
            	CCombo choiceCombo = new CCombo(page, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY | SWT.SINGLE);
                getWidgetFactory().adapt(choiceCombo, true, true);
                choiceCombo.setEditable(false);
				choiceCombo.setLayoutData(createPropertyFieldLayoutData());
                
                final CamelModelElement dataformatElement = this.selectedEP.getParameter(prop.getName()) != null ? (CamelModelElement)this.selectedEP.getParameter(prop.getName()) : null;
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
                        dataFormatChanged(dataformat, eform, selectedEP.getParameter(prop.getName()) != null ? (CamelModelElement)selectedEP.getParameter(prop.getName()) : null, page, prop);
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
                if (isRequired(p)) {
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
                
            // UNSUPPORTED PROPERTIES / REFS
            } else if (CamelComponentUtils.isUnsupportedProperty(prop)) {
            	
            	// TODO: check how to handle lists and maps - for now we treat them as string field only
            	
            	Text txtField = getWidgetFactory().createText(page, (String)(this.selectedEP.getParameter(p.getName()) != null ? this.selectedEP.getParameter(p.getName()) : this.eip.getParameter(p.getName()).getDefaultValue()), SWT.SINGLE | SWT.BORDER | SWT.LEFT);
                txtField.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent e) {
                        Text txt = (Text)e.getSource();
                        selectedEP.setParameter(prop.getName(), txt.getText());
                    }
                });
				txtField.setLayoutData(createPropertyFieldLayoutData());
                c = txtField;
                if (isRequired(p)) {
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
                
            // CLASS BASED PROPERTIES - REF OR CLASSNAMES AS STRINGS
            } else {
                // must be some class as all other options were missed
                final Text txtField = getWidgetFactory().createText(page, (String)(this.selectedEP.getParameter(p.getName()) != null ? this.selectedEP.getParameter(p.getName()) : this.eip.getParameter(p.getName()).getDefaultValue()), SWT.SINGLE | SWT.BORDER | SWT.LEFT);
                txtField.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent e) {
                        Text txt = (Text)e.getSource();
                        selectedEP.setParameter(prop.getName(), txt.getText());
                    }
                });
				txtField.setLayoutData(GridDataFactory.fillDefaults().indent(5, 0).grab(true, false).create());
                
				URLClassLoader child = CamelComponentUtils.getProjectClassLoader(selectedEP.getCamelFile().getResource().getProject());
				Class<?> classToLoad;
                try {
                    if (prop.getJavaType().indexOf("<")!=-1) {
                        classToLoad = child.loadClass(prop.getJavaType().substring(0,  prop.getJavaType().indexOf("<")));
                    } else {
                        classToLoad = child.loadClass(prop.getJavaType());   
                    }
                } catch (ClassNotFoundException ex) {
                    CamelEditorUIActivator.pluginLog().logWarning("Cannot find class " + prop.getJavaType() + " on classpath.", ex);
                    classToLoad = null;
                }
                
                final IProject project = CamelUtils.getDiagramEditor().getModel().getResource().getProject();
				final Class<?> fClass = classToLoad;
                
                Button btn_create = getWidgetFactory().createButton(page, " + ", SWT.BORDER | SWT.PUSH);
                btn_create.addSelectionListener(new SelectionAdapter() {
                    /* (non-Javadoc)
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        NewClassCreationWizard wiz = new NewClassCreationWizard();
                        wiz.addPages();
                        wiz.init(PlatformUI.getWorkbench(), null);
                        NewClassWizardPage wp = (NewClassWizardPage)wiz.getStartingPage();
                        WizardDialog wd = new WizardDialog(e.display.getActiveShell(), wiz);
                        if (fClass.isInterface()) {
                            wp.setSuperInterfaces(Arrays.asList(fClass.getName()), true);
                        } else {
                            wp.setSuperClass(fClass.getName(), true);
                        }
                        wp.setAddComments(true, true);
                        IPackageFragmentRoot fragroot = null;
                        try {
                            IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
                            IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create(project, new NullProgressMonitor());
                            IPath[] paths = facade.getCompileSourceLocations();
                            if (paths != null && paths.length>0) {
                                for (IPath p :paths) {
                                    if (p == null) continue; 
                                    IResource res = project.findMember(p);
                                    fragroot = javaProject.getPackageFragmentRoot(res);
                                    break;
                                }
                                if (fragroot != null) wp.setPackageFragmentRoot(fragroot, true);   
                                wp.setPackageFragment(PropertiesUtils.getPackage(javaProject, fragroot), true);
                            }
                        } catch (Exception ex) {
                            CamelEditorUIActivator.pluginLog().logError(ex);
                        }
                        if (Window.OK == wd.open()) {
                            String value = wp.getCreatedType().getFullyQualifiedName();
                            if (value != null) txtField.setText(value);
                        }
                    }
                });
                btn_create.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
                btn_create.setEnabled(fClass != null);
                
                Button btn_browse = getWidgetFactory().createButton(page, "...", SWT.BORDER | SWT.PUSH);
                btn_browse.addSelectionListener(new SelectionAdapter() {
                    /* (non-Javadoc)
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        try {
                            IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
                            IJavaElement[] elements=new IJavaElement[]{javaProject};
                            IJavaSearchScope scope=SearchEngine.createJavaSearchScope(elements);
                            
                            FilteredTypesSelectionDialog dlg = new FilteredTypesSelectionDialog(Display.getDefault().getActiveShell(), 
                                    false, 
                                    PlatformUI.getWorkbench().getProgressService(), 
                                    scope, 
                                    IJavaSearchConstants.CLASS);
                            
                            if (Window.OK == dlg.open()) {
                                Object o = dlg.getFirstResult();
                                if (o instanceof SourceType) {
                                    txtField.setText(((SourceType)o).getFullyQualifiedName());
                                    selectedEP.setParameter(prop.getName(), txtField.getText());
                                }
                            }
                        } catch (Exception ex) {
                            CamelEditorUIActivator.pluginLog().logError(ex);
                        }
                    }
                });
                btn_browse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
                btn_browse.setEnabled(fClass != null);
                c = txtField;
                if (isRequired(p)) {
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
        		modelListObservable = Observables.staticObservableList((List)modelMap.get(p.getName()), String.class);
        		UpdateListStrategy listStrategy = new UpdateListStrategy() {
        			/* (non-Javadoc)
        			 * @see org.eclipse.core.databinding.UpdateListStrategy#doAdd(org.eclipse.core.databinding.observable.list.IObservableList, java.lang.Object, int)
        			 */
        			@Override
        			protected IStatus doAdd(IObservableList observableList, Object element, int index) {
        				super.doAdd(observableList, element, index);
        				if (prop.getRequired() != null && prop.getRequired().equalsIgnoreCase("true")) {
        					if (observableList.size()<1) return ValidationStatus.error("Parameter " + prop.getName() + " is a mandatory field and cannot be empty.");	
        				}
        				return ValidationStatus.ok();
        			}
        			
        			/* (non-Javadoc)
        			 * @see org.eclipse.core.databinding.UpdateListStrategy#doRemove(org.eclipse.core.databinding.observable.list.IObservableList, int)
        			 */
        			@Override
        			protected IStatus doRemove(IObservableList observableList, int index) {
        				super.doRemove(observableList, index);
        				if (prop.getRequired() != null && prop.getRequired().equalsIgnoreCase("true")) {
        					if (observableList.size()<1) return ValidationStatus.error("Parameter " + prop.getName() + " is a mandatory field and cannot be empty.");	
        				}
        				return ValidationStatus.ok();
        			}
        		};
        		bindValue = dbc.bindList(uiListObservable, modelListObservable, listStrategy, null);
        	}
            ControlDecorationSupport.create(bindValue, SWT.TOP | SWT.LEFT); 
            
			createHelpDecoration(p, c);
        }
        page.layout();
    }
    

}
