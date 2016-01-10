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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.WritableMap;
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
import org.eclipse.jface.viewers.ISelection;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.internal.forms.widgets.FormsResources;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.utils.CamelComponentUtils;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.editor.utils.DiagramUtils;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.editor.utils.PropertiesUtils;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.catalog.languages.Language;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.foundation.core.util.Strings;
import org.w3c.dom.Node;

/**
 * Shows the property details for the currently selected node
 */
public class DetailsSection extends AbstractPropertySection {

	public static final String DEFAULT_GROUP = "General";
	
	private FormToolkit toolkit;
    private Form form;
    private CTabFolder tabFolder;
    private List<CTabItem> tabs = new ArrayList<CTabItem>();
    private CamelModelElement selectedEP;
    private DataBindingContext dbc;
    private IObservableMap modelMap = new WritableMap();
    private Eip eip;
    private Composite parent;
    private TabbedPropertySheetPage aTabbedPropertySheetPage;
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#dispose()
     */
    @Override
    public void dispose() {
        if (toolkit != null) {
            toolkit.dispose();
            toolkit = null;
        }
        this.eip = null;
        super.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput
     * (org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void setInput(IWorkbenchPart part, ISelection selection) {
    	super.setInput(part, selection);

        this.dbc = new DataBindingContext();

        Object o = Selections.getFirstSelection(selection);
        CamelModelElement n = NodeUtils.toCamelElement(o);
        
        if (n.getUnderlyingMetaModelObject() != null) {
            this.selectedEP = n;
            this.eip = PropertiesUtils.getEipFor(selectedEP);
            form.setText("Details - " + DiagramUtils.filterFigureLabel(selectedEP.getDisplayText()));
        } else {
            this.selectedEP = null;
            form.setText("Details");
        }

        int idx = Math.max(tabFolder.getSelectionIndex(), 0);

        if (this.tabs.isEmpty() == false) {
        	for (CTabItem tab : this.tabs) {
        		if (!tab.isDisposed()) tab.dispose();
        	}
        	tabs.clear();
        }

        // now generate the tab contents
        createContentTabs(tabFolder);
        
        tabFolder.setSingle(tabFolder.getItemCount()==1);
        tabFolder.setSelection(idx >= tabFolder.getItemCount() ? 0 : idx);
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

        this.parent = parent;
        this.aTabbedPropertySheetPage = aTabbedPropertySheetPage;
        
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

        toolkit.decorateFormHeading(form);
        
        form.layout();
        tabFolder.setSelection(0);
    }
    
    /**
     * 
     * @param folder
     */
    private void createContentTabs(CTabFolder folder) {
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
        
        // display all the properties in alphabetic order - sorting needed
        Collections.sort(props, new Comparator<Parameter>() {
            /* (non-Javadoc)
             * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
             */
            @Override
            public int compare(Parameter o1, Parameter o2) {
                return o1.getName().compareTo(o2.getName());
            }
        }); 
        
        if (tabsToCreate.size()>1) {
        	// display all the properties in alphabetic order - sorting needed
            Collections.sort(tabsToCreate, new Comparator<String>() {
                /* (non-Javadoc)
                 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
                 */
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            }); 
        }
        
        for (String group : tabsToCreate) {
        	CTabItem contentTab = new CTabItem(this.tabFolder, SWT.NONE);
            contentTab.setText(Strings.humanize(group));

            Composite page = this.toolkit.createComposite(folder);
            page.setLayout(new GridLayout(4, false));
                    
            generateTabContents(props, page, group);

            contentTab.setControl(page);        	
            
            this.tabs.add(contentTab);
        }
    }
    
    /**
     * 
     * @param props
     * @param page
     * @param group
     */
    protected void generateTabContents(List<Parameter> props, final Composite page, final String group) {
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
            
            String s = Strings.humanize(p.getName());
            if (p.getDeprecated() != null && p.getDeprecated().equalsIgnoreCase("true")) s += " (deprecated)"; 
            
            Label l = getWidgetFactory().createLabel(page, s);            
            l.setLayoutData(new GridData());
            if (p.getDescription() != null) {
            	l.setToolTipText(p.getDescription());
            }
            if (p.getRequired() != null && p.getRequired().equalsIgnoreCase("true")) {
            	l.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
            }
            
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
                txtField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
                c = txtField;
                //initialize the map entry
                modelMap.put(p.getName(), txtField.getText());
                // create observables for the control
                uiObservable = WidgetProperties.text(SWT.Modify).observe(txtField);
                if (p.getRequired() != null && p.getRequired().equalsIgnoreCase("true")) {
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
                Button checkBox = getWidgetFactory().createButton(page, "", SWT.CHECK | SWT.BORDER);
                Boolean b = Boolean.parseBoolean( (this.selectedEP.getParameter(p.getName()) != null ? this.selectedEP.getParameter(p.getName()).toString() : this.eip.getParameter(p.getName()).getDefaultValue()));
                checkBox.setSelection(b);
                checkBox.addSelectionListener(new SelectionAdapter() {
                    /* (non-Javadoc)
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        selectedEP.setParameter(prop.getName(), checkBox.getSelection());
                    }
                });
                checkBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
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
                txtField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
                c = txtField;
                //initialize the map entry
                modelMap.put(p.getName(), txtField.getText());
                // create observables for the control
                uiObservable = WidgetProperties.text(SWT.Modify).observe(txtField);
                if (p.getRequired() != null && p.getRequired().equalsIgnoreCase("true") || p.getName().equalsIgnoreCase("id")) {
					validator = new IValidator() {
						/*
						 * (non-Javadoc)
						 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
						 */
						@Override
						public IStatus validate(Object value) {
							if (selectedEP.getParameter("uri") != null && ((String)selectedEP.getParameter("uri")).startsWith("ref:")) {
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
                txtField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
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
						// only check non-empty fields
						if (value != null && value.toString().trim().length()>0) {
							try {
								Double.parseDouble(value.toString());
							} catch (NumberFormatException ex) {
								return ValidationStatus.error("The parameter " + prop.getName() + " requires a numeric value.");
							}
						}
						return ValidationStatus.ok();
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
                choiceCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
                c = choiceCombo;
                //initialize the map entry
                modelMap.put(p.getName(), choiceCombo.getText());
                // create observables for the control
                uiObservable = WidgetProperties.selection().observe(choiceCombo);                
                if (p.getRequired() != null && p.getRequired().equalsIgnoreCase("true")) {
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
                choiceCombo.setEditable(false);
                choiceCombo.setItems(CamelComponentUtils.getRefs(this.selectedEP.getCamelFile()));
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
                choiceCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
                c = choiceCombo;
                //initialize the map entry
                modelMap.put(p.getName(), choiceCombo.getText());
                // create observables for the control
                uiObservable = WidgetProperties.selection().observe(choiceCombo);                
                if (p.getRequired() != null && p.getRequired().equalsIgnoreCase("true")) {
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
                final Text txtField = getWidgetFactory().createText(page, (String)(this.selectedEP.getParameter(p.getName()) != null ? this.selectedEP.getParameter(p.getName()) : this.eip.getParameter(p.getName()).getDefaultValue()), SWT.SINGLE | SWT.BORDER | SWT.LEFT);
                txtField.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent e) {
                        Text txt = (Text)e.getSource();
                        selectedEP.setParameter(prop.getName(), txt.getText());
                    }
                });
                txtField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
                
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
                if (p.getRequired() != null && p.getRequired().equalsIgnoreCase("true")) {
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
                list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
                
                ArrayList<String> listElements = this.selectedEP.getParameter(prop.getName()) != null ? (ArrayList<String>)this.selectedEP.getParameter(prop.getName()) : new ArrayList<String>();
                list.setItems(listElements.toArray(new String[listElements.size()]));
                
                c = list;
                //initialize the map entry
                modelMap.put(p.getName(), Arrays.asList(list.getItems()));
                // create observables for the control
                uiListObservable = WidgetProperties.items().observe(list);                
                if (p.getRequired() != null && p.getRequired().equalsIgnoreCase("true")) {
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
                choiceCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
                
                CamelModelElement expressionElement = this.selectedEP.getParameter(prop.getName()) != null ? (CamelModelElement)this.selectedEP.getParameter(prop.getName()) : null;
                choiceCombo.setItems(CamelComponentUtils.getOneOfList(prop));

                ExpandableComposite eform = getWidgetFactory().createExpandableComposite(page, ExpandableComposite.TREE_NODE | ExpandableComposite.CLIENT_INDENT);
                eform.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
                eform.setText("Expression Settings...");
                eform.setLayout(new GridLayout(1, true));
                eform.addExpansionListener(new IExpansionListener() {
                	/*
                	 * (non-Javadoc)
                	 * @see org.eclipse.ui.forms.events.IExpansionListener#expansionStateChanging(org.eclipse.ui.forms.events.ExpansionEvent)
                	 */
					@Override
					public void expansionStateChanging(ExpansionEvent e) {
					}
					
					/*
					 * (non-Javadoc)
					 * @see org.eclipse.ui.forms.events.IExpansionListener#expansionStateChanged(org.eclipse.ui.forms.events.ExpansionEvent)
					 */
					@Override
					public void expansionStateChanged(ExpansionEvent e) {
						page.layout(true);
						refresh();
						aTabbedPropertySheetPage.resizeScrolledComposite();
					}
				});

                choiceCombo.addSelectionListener(new SelectionAdapter() {
                	/* (non-Javadoc)
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        CCombo choice = (CCombo)e.getSource();
                        String language = choice.getText();
                        languageChanged(language, eform, expressionElement, page, prop);
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
                if (p.getRequired() != null && p.getRequired().equalsIgnoreCase("true")) {
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
                choiceCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
                
                CamelModelElement dataformatElement = this.selectedEP.getParameter(prop.getName()) != null ? (CamelModelElement)this.selectedEP.getParameter(prop.getName()) : null;
                choiceCombo.setItems(CamelComponentUtils.getOneOfList(prop));

                ExpandableComposite eform = getWidgetFactory().createExpandableComposite(page, ExpandableComposite.TREE_NODE | ExpandableComposite.CLIENT_INDENT);
                eform.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
                eform.setText("Data Format Settings...");
                eform.setLayout(new GridLayout(1, true));
                eform.addExpansionListener(new IExpansionListener() {
                	/*
                	 * (non-Javadoc)
                	 * @see org.eclipse.ui.forms.events.IExpansionListener#expansionStateChanging(org.eclipse.ui.forms.events.ExpansionEvent)
                	 */
					@Override
					public void expansionStateChanging(ExpansionEvent e) {
					}
					
					/*
					 * (non-Javadoc)
					 * @see org.eclipse.ui.forms.events.IExpansionListener#expansionStateChanged(org.eclipse.ui.forms.events.ExpansionEvent)
					 */
					@Override
					public void expansionStateChanged(ExpansionEvent e) {
						page.layout(true);
						refresh();
						aTabbedPropertySheetPage.resizeScrolledComposite();
					}
				});

                choiceCombo.addSelectionListener(new SelectionAdapter() {
                	/* (non-Javadoc)
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        CCombo choice = (CCombo)e.getSource();
                        String dataformat = choice.getText();
                        CamelModelElement dataFormatElement = selectedEP.getParameter(prop.getName()) != null ? (CamelModelElement)selectedEP.getParameter(prop.getName()) : null;
                        dataFormatChanged(dataformat, eform, dataFormatElement, page, prop);
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
                if (p.getRequired() != null && p.getRequired().equalsIgnoreCase("true")) {
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
                txtField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
                c = txtField;
                if (p.getRequired() != null && p.getRequired().equalsIgnoreCase("true")) {
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
                txtField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
                
                URLClassLoader child = CamelComponentUtils.getProjectClassLoader();
                Class classToLoad;
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
                final Class fClass = classToLoad;
                
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
                if (p.getRequired() != null && p.getRequired().equalsIgnoreCase("true")) {
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
            
            if (p.getDescription() != null) c.setToolTipText(p.getDescription());
        }
        page.layout();
    }
    
    /**
     * called when user switches the expression language
     * 
     * @param language				the new language for the expression
     * @param eform					the expandable form to use
     * @param expressionElement		the expression element if simple expression, otherwise it will be the container element which contains the expression element as parameter "expression"
     * @param page					the page
     * @param prop					the property which is currently used
     */
    private void languageChanged(String language, ExpandableComposite eform, CamelModelElement expressionElement, Composite page, Parameter prop) {
    	eform.setText(language.trim());
        for (Control co : eform.getChildren()) if (co.getData("fuseExpressionClient") != null) co.dispose();
        Composite client = getWidgetFactory().createComposite(eform);
        client.setData("fuseExpressionClient", true);
        client.setLayoutData(new GridData(GridData.FILL_BOTH));
        client.setLayout(new GridLayout(4, false));
        eform.setClient(client);
        
        CamelModelElement uiExpressionElement = null;
        
        if (prop.getName().equalsIgnoreCase("expression")) {
        	// normal expression subnode - no cascading -> when.<expression>
        	// the content of expressionElement is the language node itself
            if (expressionElement != null && expressionElement.getTranslatedNodeName().equals(language) == false) {
            	Node oldExpNode = null;
            	for (int i=0; i<selectedEP.getXmlNode().getChildNodes().getLength(); i++) {
            		if (org.fusesource.ide.foundation.core.util.CamelUtils.getTranslatedNodeName(selectedEP.getXmlNode().getChildNodes().item(i)).equals(expressionElement.getTranslatedNodeName())) {
            			oldExpNode = selectedEP.getXmlNode().getChildNodes().item(i);
            			break;
            		}
            	}
            	if (language.trim().length()>0) {
	            	Node expNode = selectedEP.createElement(language, selectedEP != null && selectedEP.getXmlNode() != null ? selectedEP.getXmlNode().getPrefix() : null);
	            	expressionElement = new CamelModelElement(this.selectedEP, expNode);
	            	selectedEP.setParameter(prop.getName(), expressionElement);
	            	selectedEP.getXmlNode().replaceChild(expNode, oldExpNode);
            	} else {
            		// user wants to delete the expression
            		selectedEP.getXmlNode().removeChild(oldExpNode);
            		selectedEP.removeParameter(prop.getName());
            	}
        	} else if (expressionElement == null && language.trim().length()>0) {
        		// no expression set, but now we set one
        		Node expNode = selectedEP.createElement(language, selectedEP != null && selectedEP.getXmlNode() != null ? selectedEP.getXmlNode().getPrefix() : null);
        		expressionElement = new CamelModelElement(this.selectedEP, expNode);
            	selectedEP.getXmlNode().insertBefore(expNode, selectedEP.getXmlNode().getFirstChild());
            	this.selectedEP.setParameter(prop.getName(), expressionElement);
            } 
            uiExpressionElement = expressionElement;

        } else {
        	
        	// cascaded expression subnode -> onException.handled.<expression>
        	// the content of expressionElement is the container element which holds the expression as parameter "expression"
        	if (expressionElement != null && expressionElement.getParameter("expression") != null) {
            	// 1. container element exists and expression element exists
        		Node oldExpNode = null;
            	List<String> langs = Arrays.asList(CamelComponentUtils.getOneOfList(prop));
            	for (int i=0; i<expressionElement.getXmlNode().getChildNodes().getLength(); i++) {
            		Node n = expressionElement.getXmlNode().getChildNodes().item(i);
            		if (langs.contains(org.fusesource.ide.foundation.core.util.CamelUtils.getTranslatedNodeName(n))) {
            			oldExpNode = n;
            			break;
            		}
            	}
            	CamelModelElement expElement = (CamelModelElement)expressionElement.getParameter("expression");
            	if (expElement.getTranslatedNodeName().equals(language) == false) {
            		if (language.trim().length()>0) {
	            		Node expNode = selectedEP.createElement(language, selectedEP != null && selectedEP.getXmlNode() != null ? selectedEP.getXmlNode().getPrefix() : null);
	                	uiExpressionElement = new CamelModelElement(expressionElement, expNode);
	                	expressionElement.getXmlNode().replaceChild(expNode, oldExpNode);
	                	expressionElement.setParameter("expression", uiExpressionElement);
            		} else {
            			// user deletes the expression
            			selectedEP.getXmlNode().removeChild(expressionElement.getXmlNode());
            			selectedEP.removeParameter(prop.getName());
            		}
            	} else {
            		uiExpressionElement = expElement;
            	}
        		
        	} else if (expressionElement != null && expressionElement.getParameter("expression") == null) {
        		// 2. container element exists but no expression element exists
        		Node expNode = selectedEP.createElement(language, selectedEP != null && selectedEP.getXmlNode() != null ? selectedEP.getXmlNode().getPrefix() : null);
        		uiExpressionElement = new CamelModelElement(expressionElement, expNode);
        		expressionElement.getXmlNode().appendChild(expNode);
            	expressionElement.setParameter("expression", uiExpressionElement);
        		
        	} else if (expressionElement == null && language.trim().length()>0) {
        		// 3. No container but language set
        		Node expContainerNode = selectedEP.createElement(prop.getName(), selectedEP != null && selectedEP.getXmlNode() != null ? selectedEP.getXmlNode().getPrefix() : null);
        		Node expNode = selectedEP.createElement(language, selectedEP != null && selectedEP.getXmlNode() != null ? selectedEP.getXmlNode().getPrefix() : null);
        		CamelModelElement expContainerElement = new CamelModelElement(selectedEP, expContainerNode);
        		expressionElement = new CamelModelElement(expContainerElement, expNode);
        		expContainerElement.getXmlNode().appendChild(expNode);
            	selectedEP.getXmlNode().insertBefore(expContainerNode, selectedEP.getXmlNode().getFirstChild());
            	expContainerElement.setParameter("expression", expressionElement);
            	this.selectedEP.setParameter(prop.getName(), expContainerElement);
            	uiExpressionElement = expressionElement;        		
        	}
        }
        
        prepareExpressionUIForLanguage(language, uiExpressionElement, client);
		page.layout(true);
		refresh();
		eform.layout(true);
		aTabbedPropertySheetPage.resizeScrolledComposite();
    }
    
    private void prepareExpressionUIForLanguage(String language, CamelModelElement expressionElement, Composite parent) {
    	CamelModel model = getCamelModel(expressionElement);
    	
    	// now create the new fields
    	Language lang = model.getLanguageModel().getLanguageByName(language);
    	if (lang != null) {
    		List<Parameter> props = lang.getParameters();
    		// display all the properties in alphabetic order - sorting needed
            Collections.sort(props, new Comparator<Parameter>() {
                /* (non-Javadoc)
                 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
                 */
                @Override
                public int compare(Parameter o1, Parameter o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            }); 
    		
    		for (Parameter p : props) {
                // Label
    			String s = Strings.humanize(p.getName());
                if (p.getDeprecated() != null && p.getDeprecated().equalsIgnoreCase("true")) s += " (deprecated)"; 
                
                Label l = getWidgetFactory().createLabel(parent, s);            
                l.setLayoutData(new GridData());
                if (p.getDescription() != null) {
                	l.setToolTipText(p.getDescription());
                }
                if (p.getRequired() != null && p.getRequired().equalsIgnoreCase("true")) {
                	l.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
                }
                
                // Field
                Control field = getControlForParameter(p, parent, expressionElement, lang);
                field.setToolTipText(p.getDescription());
    		}
    	}
    }
    
    private Control getControlForParameter(Parameter p, Composite parent, CamelModelElement expressionElement, Language lang) {
    	Control c = null;
    	
    	// BOOLEAN PROPERTIES
    	if (CamelComponentUtils.isBooleanProperty(p)) {
    		Button checkBox = getWidgetFactory().createButton(parent, "", SWT.CHECK | SWT.BORDER);
    		Boolean b = Boolean.parseBoolean( (expressionElement != null && expressionElement.getParameter(p.getName()) != null ? (String)expressionElement.getParameter(p.getName()) : lang.getParameter(p.getName()).getDefaultValue()));
    		checkBox.setSelection(b);
    		checkBox.addSelectionListener(new SelectionAdapter() {
	            /* (non-Javadoc)
	             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	             */
	            @Override
	            public void widgetSelected(SelectionEvent e) {
	                expressionElement.setParameter(p.getName(), checkBox.getSelection());
	            }
	        });
    		checkBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
    		c = checkBox;
        
    		// TEXT PROPERTIES
    	} else if (CamelComponentUtils.isTextProperty(p)) {
	        Text txtField = getWidgetFactory().createText(parent, (String)(expressionElement != null && expressionElement.getParameter(p.getName()) != null ? expressionElement.getParameter(p.getName()) : lang.getParameter(p.getName()).getDefaultValue()), SWT.SINGLE | SWT.BORDER | SWT.LEFT);
	        txtField.addModifyListener(new ModifyListener() {
	            @Override
	            public void modifyText(ModifyEvent e) {
	                Text txt = (Text)e.getSource();
	                expressionElement.setParameter(p.getName(), txt.getText());
	            }
	        });
	        txtField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
	        c = txtField;
        
	    // NUMBER PROPERTIES
	    } else if (CamelComponentUtils.isNumberProperty(p)) {
	        Text txtField = getWidgetFactory().createText(parent, (String)(expressionElement != null && expressionElement.getParameter(p.getName()) != null ? expressionElement.getParameter(p.getName()) : lang.getParameter(p.getName()).getDefaultValue()), SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
	        txtField.addModifyListener(new ModifyListener() {
	            @Override
	            public void modifyText(ModifyEvent e) {
	                Text txt = (Text)e.getSource();
	                String val = txt.getText();
	                try {
	                	Double.parseDouble(val);
	                	txt.setBackground(ColorConstants.white);
	                    expressionElement.setParameter(p.getName(), txt.getText());
	                } catch (NumberFormatException ex) {
	                	// invalid character found
	                    txt.setBackground(ColorConstants.red);
	                    return;
	                }
	            }
	        });
	        txtField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
	        c = txtField;

        // OTHER
	    } else {
	    	Text txtField = getWidgetFactory().createText(parent, (String)(expressionElement != null && expressionElement.getParameter(p.getName()) != null ? expressionElement.getParameter(p.getName()) : lang.getParameter(p.getName()).getDefaultValue()), SWT.SINGLE | SWT.BORDER | SWT.LEFT);
	        txtField.addModifyListener(new ModifyListener() {
	            @Override
	            public void modifyText(ModifyEvent e) {
	                Text txt = (Text)e.getSource();
	                expressionElement.setParameter(p.getName(), txt.getText());
	            }
	        });
	        txtField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
	        c = txtField;
	    }
    	
    	return c;
    }
    
    /**
     * called when user switches the expression language
     * 
     * @param language				the new language for the expression
     * @param eform					the expandable form to use
     * @param dataFormatElement		the expression element if simple expression, otherwise it will be the container element which contains the expression element as parameter "expression"
     * @param page					the page
     * @param prop					the property which is currently used
     */
    private void dataFormatChanged(String dataformat, ExpandableComposite eform, CamelModelElement dataFormatElement, Composite page, Parameter prop) {
    	eform.setText(dataformat.trim());
        for (Control co : eform.getChildren()) if (co.getData("fuseDataFormatClient") != null) co.dispose();
        Composite client = getWidgetFactory().createComposite(eform);
        client.setData("fuseDataFormatClient", true);
        client.setLayoutData(new GridData(GridData.FILL_BOTH));
        client.setLayout(new GridLayout(4, false));
        eform.setClient(client);
        
        CamelModelElement uiDataFormatElement = null;
        
        if (dataFormatElement != null && dataFormatElement.getTranslatedNodeName().equals(dataformat) == false) {
        	Node oldExpNode = null;
        	for (int i=0; i<selectedEP.getXmlNode().getChildNodes().getLength(); i++) {
        		if (org.fusesource.ide.foundation.core.util.CamelUtils.getTranslatedNodeName(selectedEP.getXmlNode().getChildNodes().item(i)).equalsIgnoreCase(dataFormatElement.getTranslatedNodeName())) {
        			oldExpNode = selectedEP.getXmlNode().getChildNodes().item(i);
        			break;
        		}
        	}
        	if (dataformat.trim().length()>0) {
            	Node expNode = selectedEP.createElement(dataformat, selectedEP != null && selectedEP.getXmlNode() != null ? selectedEP.getXmlNode().getPrefix() : null);
            	dataFormatElement = new CamelModelElement(this.selectedEP, expNode);
            	selectedEP.setParameter(prop.getName(), dataFormatElement);
            	if (oldExpNode == null) {
            		System.err.println("pdd");
            	}
            	selectedEP.getXmlNode().replaceChild(expNode, oldExpNode);
        	} else {
        		// user wants to delete the expression
        		selectedEP.getXmlNode().removeChild(oldExpNode);
        		selectedEP.removeParameter(prop.getName());
        	}
    	} else if (dataFormatElement == null && dataformat.trim().length()>0) {
    		// no expression set, but now we set one
    		Node expNode = selectedEP.createElement(dataformat, selectedEP != null && selectedEP.getXmlNode() != null ? selectedEP.getXmlNode().getPrefix() : null);
    		dataFormatElement = new CamelModelElement(this.selectedEP, expNode);
        	selectedEP.getXmlNode().insertBefore(expNode, selectedEP.getXmlNode().getFirstChild());
        	this.selectedEP.setParameter(prop.getName(), dataFormatElement);
        } 
        uiDataFormatElement = dataFormatElement;
        
        prepareDataFormatUIForDataFormat(dataformat, uiDataFormatElement, client);
		page.layout(true);
		refresh();
		eform.layout(true);
		aTabbedPropertySheetPage.resizeScrolledComposite();
    }
    
    private void prepareDataFormatUIForDataFormat(String dataformat, CamelModelElement dataFormatElement, Composite parent) {
    	CamelModel model = getCamelModel(dataFormatElement);
    	
    	// now create the new fields
    	DataFormat df = model.getDataformatModel().getDataFormatByName(dataformat);
    	if (df != null) {
    		List<Parameter> props = df.getParameters();
    		// display all the properties in alphabetic order - sorting needed
            Collections.sort(props, new Comparator<Parameter>() {
                /* (non-Javadoc)
                 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
                 */
                @Override
                public int compare(Parameter o1, Parameter o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            }); 
    		
    		for (Parameter p : props) {
                // Label
    			String s = Strings.humanize(p.getName());
                if (p.getDeprecated() != null && p.getDeprecated().equalsIgnoreCase("true")) s += " (deprecated)"; 
                
                Label l = getWidgetFactory().createLabel(parent, s);            
                l.setLayoutData(new GridData());
                if (p.getDescription() != null) {
                	l.setToolTipText(p.getDescription());
                }
                if (p.getRequired() != null && p.getRequired().equalsIgnoreCase("true")) {
                	l.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
                }
                
                // Field
                Control field = getControlForParameter(p, parent, dataFormatElement, df);
                field.setToolTipText(p.getDescription());
    		}
    	}
    }
    
    private Control getControlForParameter(Parameter p, Composite parent, CamelModelElement dataFormatElement, DataFormat df) {
    	Control c = null;
    	
    	// BOOLEAN PROPERTIES
    	if (CamelComponentUtils.isBooleanProperty(p)) {
    		Button checkBox = getWidgetFactory().createButton(parent, "", SWT.CHECK | SWT.BORDER);
    		String boolVal = dataFormatElement.getParameter(p.getName()) instanceof Boolean ? Boolean.toString((boolean)dataFormatElement.getParameter(p.getName())) : (String)dataFormatElement.getParameter(p.getName());
    		Boolean b = Boolean.parseBoolean( (dataFormatElement != null && dataFormatElement.getParameter(p.getName()) != null ? boolVal : df.getParameter(p.getName()).getDefaultValue()));
    		checkBox.setSelection(b);
    		checkBox.addSelectionListener(new SelectionAdapter() {
	            /* (non-Javadoc)
	             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	             */
	            @Override
	            public void widgetSelected(SelectionEvent e) {
	            	dataFormatElement.setParameter(p.getName(), checkBox.getSelection());
	            }
	        });
    		checkBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
    		c = checkBox;
        
    		// TEXT PROPERTIES
    	} else if (CamelComponentUtils.isTextProperty(p)) {
	        Text txtField = getWidgetFactory().createText(parent, (String)(dataFormatElement != null && dataFormatElement.getParameter(p.getName()) != null ? dataFormatElement.getParameter(p.getName()) : df.getParameter(p.getName()).getDefaultValue()), SWT.SINGLE | SWT.BORDER | SWT.LEFT);
	        txtField.addModifyListener(new ModifyListener() {
	            @Override
	            public void modifyText(ModifyEvent e) {
	                Text txt = (Text)e.getSource();
	                dataFormatElement.setParameter(p.getName(), txt.getText());
	            }
	        });
	        txtField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
	        c = txtField;
        
	    // NUMBER PROPERTIES
	    } else if (CamelComponentUtils.isNumberProperty(p)) {
	        Text txtField = getWidgetFactory().createText(parent, (String)(dataFormatElement != null && dataFormatElement.getParameter(p.getName()) != null ? dataFormatElement.getParameter(p.getName()) : df.getParameter(p.getName()).getDefaultValue()), SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
	        txtField.addModifyListener(new ModifyListener() {
	            @Override
	            public void modifyText(ModifyEvent e) {
	                Text txt = (Text)e.getSource();
	                String val = txt.getText();
	                try {
	                	Double.parseDouble(val);
	                	txt.setBackground(ColorConstants.white);
	                	dataFormatElement.setParameter(p.getName(), txt.getText());
	                } catch (NumberFormatException ex) {
	                	// invalid character found
	                    txt.setBackground(ColorConstants.red);
	                    return;
	                }
	            }
	        });
	        txtField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
	        c = txtField;

        // OTHER
	    } else {
	    	Text txtField = getWidgetFactory().createText(parent, (String)(dataFormatElement != null && dataFormatElement.getParameter(p.getName()) != null ? dataFormatElement.getParameter(p.getName()) : df.getParameter(p.getName()).getDefaultValue()), SWT.SINGLE | SWT.BORDER | SWT.LEFT);
	        txtField.addModifyListener(new ModifyListener() {
	            @Override
	            public void modifyText(ModifyEvent e) {
	                Text txt = (Text)e.getSource();
	                dataFormatElement.setParameter(p.getName(), txt.getText());
	            }
	        });
	        txtField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
	        c = txtField;
	    }
    	
    	return c;
    }
    
    private CamelModel getCamelModel(CamelModelElement modelElement) {
    	String prjCamelVersion = CamelUtils.getCurrentProjectCamelVersion();
		// then get the meta model for the given camel version
		CamelModel model = CamelModelFactory.getModelForVersion(prjCamelVersion);
		if (model == null) {
			return null;
		}
		return model;
    }
}
