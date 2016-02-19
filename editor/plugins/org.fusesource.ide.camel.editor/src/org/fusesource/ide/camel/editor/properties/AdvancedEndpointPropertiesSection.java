/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
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
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.Observables;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.internal.forms.widgets.FormsResources;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.UriParameterKind;
import org.fusesource.ide.camel.model.service.core.util.CamelComponentUtils;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.fusesource.ide.camel.validation.model.NumberValidator;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author lhein
 */
public class AdvancedEndpointPropertiesSection extends FusePropertySection {

	
    /**
     * 
     * @param folder
     */
    protected void createContentTabs(CTabFolder folder) {
        List<Parameter> props = PropertiesUtils.getComponentPropertiesFor(selectedEP);

        if (props.isEmpty()) return;
       
        boolean createCommonsTab = false;
        List<Parameter> commonProps = PropertiesUtils.getPropertiesFor(selectedEP, UriParameterKind.BOTH);
        for (Parameter p : commonProps) {
        	if (p.getGroup() == null || p.getGroup().trim().length() < 1) {
        		createCommonsTab = true;
        		break;
        	}
        }
        
        boolean createConsumerTab = false;
        List<Parameter> consumerProps = PropertiesUtils.getPropertiesFor(selectedEP, UriParameterKind.CONSUMER);
        for (Parameter p : consumerProps) {
        	if (p.getGroup() == null || p.getGroup().trim().length() < 1) {
        		createConsumerTab = true;
        		break;
        	}
        }
        
        boolean createProducerTab = false;
        List<Parameter> producerProps = PropertiesUtils.getPropertiesFor(selectedEP, UriParameterKind.PRODUCER);
        for (Parameter p : producerProps) {
        	if (p.getGroup() == null || p.getGroup().trim().length() < 1) {
        		createProducerTab = true;
        		break;
        	}
        }        
        
        List<String> tabsToCreate = new ArrayList<String>();
        // path tab is always there
        tabsToCreate.add(GROUP_PATH);
        if (createCommonsTab) tabsToCreate.add(GROUP_COMMON);
        if (createConsumerTab) tabsToCreate.add(GROUP_CONSUMER);
        if (createProducerTab) tabsToCreate.add(GROUP_PRODUCER);
        
        for (Parameter p : props) {
        	if (p.getGroup() != null && p.getGroup().trim().length() > 0 && tabsToCreate.contains(p.getGroup()) == false) {
        		tabsToCreate.add(p.getGroup());
        	}
        }
        
        props.sort(new ParameterPriorityComparator());
        
        for (String group : tabsToCreate) {
        	CTabItem contentTab = new CTabItem(this.tabFolder, SWT.NONE);
            contentTab.setText(Strings.humanize(group));

            Composite page = this.toolkit.createComposite(folder);
            page.setLayout(new GridLayout(4, false));

            if (group.equalsIgnoreCase(GROUP_PATH)) {
            	generateTabContents(PropertiesUtils.getPathProperties(selectedEP), page, false, group);
            } else {
                generateTabContents(props, page, true, group);            	
            }	

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
    protected void generateTabContents(List<Parameter> props, final Composite page, boolean ignorePathProperties, String group) {
    	props.sort(new ParameterPriorityComparator());
        for (Parameter p : props) {
        	final Parameter prop = p;
        	
        	// atm we don't want to care about path parameters if thats not the path tab
        	if (ignorePathProperties && p.getKind() != null && p.getKind().equalsIgnoreCase("path")) continue;

        	// we don't display items which don't fit the group
        	if (p.getGroup() != null && p.getGroup().trim().length()>0) {
        		// a group has been explicitely defined, so use it
        		if (group.equalsIgnoreCase(p.getGroup()) == false && p.getKind().equalsIgnoreCase(GROUP_PATH) == false) continue;
        	} else if (prop.getKind().equalsIgnoreCase(GROUP_PATH) && group.equalsIgnoreCase(GROUP_PATH)) {
        		// special handling for path properties - otherwise the else would kick all props of type path
        	} else {
        		// no group defined, fall back to use label
        		if (prop.getLabel() != null && PropertiesUtils.containsLabel(group, prop) == false) continue;
        		if (prop.getLabel() == null && group.equalsIgnoreCase(GROUP_COMMON) == false) continue;
        	}
            
            ISWTObservableValue uiObservable = null;
            IObservableValue modelObservable = null;
            IValidator validator = null;
            
            createPropertyLabel(toolkit, page, p);
            
            Control c = null;
            
            // BOOLEAN PROPERTIES
            if (CamelComponentUtils.isBooleanProperty(prop)) {
				Button checkBox = toolkit.createButton(page, "", SWT.CHECK);
                Boolean b = (Boolean)PropertiesUtils.getTypedPropertyFromUri(selectedEP, prop, component);
                checkBox.setSelection(b);
                checkBox.addSelectionListener(new SelectionAdapter() {
                    /* (non-Javadoc)
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        PropertiesUtils.updateURIParams(selectedEP, prop, ((Button)e.getSource()).getSelection(), component, modelMap);
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
							if (((String)selectedEP.getParameter("uri")).startsWith("ref:")) {
								// check for broken refs
								String refId = ((String)selectedEP.getParameter("uri")).trim().length()>"ref:".length() ? ((String)selectedEP.getParameter("uri")).substring("ref:".length()) : null;
								if (refId == null || refId.trim().length()<1 || selectedEP.getCamelContext().getEndpointDefinitions().get(refId) == null) {
									return ValidationStatus.error("The entered reference does not exist in your context!");
								}
							}
							
							if (value != null && value instanceof String && value.toString().trim().length()>0) {
								return ValidationStatus.ok();
							}
							return ValidationStatus.error("Parameter " + prop.getName() + " is a mandatory field and cannot be empty.");
						}
					};
                }
                
            // NUMBER PROPERTIES
            } else if (CamelComponentUtils.isNumberProperty(prop)) {
                Text txtField = toolkit.createText(page, PropertiesUtils.getPropertyFromUri(selectedEP, prop, component), SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
                txtField.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent e) {
                        Text txt = (Text)e.getSource();
                        String val = txt.getText();
						if (val != null && !val.isEmpty()) {
                        try {
                        	Double.parseDouble(val);
                        	txt.setBackground(ColorConstants.white);
                            PropertiesUtils.updateURIParams(selectedEP, prop, txt.getText(), component, modelMap);
                        } catch (NumberFormatException ex) {
                        	// invalid character found
                            txt.setBackground(ColorConstants.red);
                            return;
							}
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
                toolkit.adapt(choiceCombo, true, true);
                choiceCombo.setEditable(false);
                choiceCombo.setItems(CamelComponentUtils.getChoices(prop));
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
                    /* (non-Javadoc)
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        CCombo choice = (CCombo)e.getSource();
                        PropertiesUtils.updateURIParams(selectedEP, prop, choice.getText(), component, modelMap);
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
            // FILE PROPERTIES
            } else if (CamelComponentUtils.isFileProperty(prop)) {
				final Text txtField = toolkit.createText(page, PropertiesUtils.getPropertyFromUri(selectedEP, prop, component), SWT.SINGLE | SWT.BORDER | SWT.LEFT);
                txtField.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent e) {
                        Text txt = (Text)e.getSource();
                        PropertiesUtils.updateURIParams(selectedEP, prop, txt.getText(), component, modelMap);
						// txt.getParent().layout(true);
						// txt.redraw();
                    }
                });
				txtField.setLayoutData(GridDataFactory.fillDefaults().indent(5, 0).span(2, 1).grab(true, false).create());
                
				Button btn_browse = toolkit.createButton(page, "...", SWT.FLAT | SWT.PUSH);
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
                
            // DATAFORMAT PROPERTIES
            } else if (CamelComponentUtils.isDataFormatProperty(prop)) {
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
                
            // UNSUPPORTED PROPERTIES / REFS
            } else if (CamelComponentUtils.isUnsupportedProperty(prop)) {
            	
            	// TODO: check how to handle lists and maps - for now we treat them as string field only
            	
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
                final Text txtField = toolkit.createText(page, PropertiesUtils.getPropertyFromUri(selectedEP, prop, component), SWT.SINGLE | SWT.BORDER | SWT.LEFT);
                txtField.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent e) {
                        Text txt = (Text)e.getSource();
                        PropertiesUtils.updateURIParams(selectedEP, prop, txt.getText(), component, modelMap);
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
                
                Button btn_create = toolkit.createButton(page, " + ", SWT.FLAT | SWT.PUSH);
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
                
                Button btn_browse = toolkit.createButton(page, "...", SWT.FLAT | SWT.PUSH);
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
            
            // create UpdateValueStrategy and assign to the binding
            UpdateValueStrategy strategy = new UpdateValueStrategy();
            strategy.setBeforeSetValidator(validator);
            
            // create observables for the Map entries
            modelObservable = Observables.observeMapEntry(modelMap, p.getName());
            // bind the observables
            Binding bindValue = dbc.bindValue(uiObservable, modelObservable, strategy, null);
            ControlDecorationSupport.create(bindValue, SWT.TOP | SWT.LEFT); 
            
            if (p.getDescription() != null) c.setToolTipText(p.getDescription());
        }
    }

	/*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls
     * (org.eclipse.swt.widgets.Composite,
     * org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
     */
    @Override
    public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
        this.toolkit = new FormToolkit(parent.getDisplay());
        super.createControls(parent, aTabbedPropertySheetPage);

        // now setup the file binding properties page
        parent.setLayout(new GridLayout());
        parent.setLayoutData(new GridData(GridData.FILL_BOTH));

        form = toolkit.createForm(parent);
        form.setLayoutData(new GridData(GridData.FILL_BOTH));
        form.getBody().setLayout(new GridLayout(1, false));

        Composite sbody = form.getBody();

        tabFolder = new CTabFolder(sbody, SWT.TOP | SWT.FLAT);
        toolkit.adapt(tabFolder, true, true);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

        Color selectedColor = toolkit.getColors().getColor(IFormColors.SEPARATOR);
        tabFolder.setSelectionBackground(new Color[] { selectedColor, toolkit.getColors().getBackground() }, new int[] { 20 }, true);
        tabFolder.setCursor(FormsResources.getHandCursor());
        toolkit.paintBordersFor(tabFolder);

        form.setText("Advanced Properties");
        toolkit.decorateFormHeading(form);
        
        form.layout();
        tabFolder.setSelection(0);
    }
}
