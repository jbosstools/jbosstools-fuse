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
package org.fusesource.ide.projecttemplates.wizards.pages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;
import org.fusesource.ide.foundation.ui.util.Selections;
import org.fusesource.ide.foundation.ui.util.Widgets;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.wizards.pages.filter.ExcludeEmptyCategoriesFilter;
import org.fusesource.ide.projecttemplates.wizards.pages.filter.TemplateNameAndKeywordPatternFilter;
import org.fusesource.ide.projecttemplates.wizards.pages.model.CategoryItem;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateItem;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateModel;
import org.fusesource.ide.projecttemplates.wizards.pages.provider.TemplateContentProvider;
import org.fusesource.ide.projecttemplates.wizards.pages.provider.TemplateLabelProvider;

/**
 * @author lhein
 */
public class FuseIntegrationProjectWizardTemplatePage extends WizardPage {

	private Button buttonEmptyProject;
	private Button buttonTemplateProject;
	private Button buttonBlueprintDSL;
	private Button buttonSpringDSL;
	private Button buttonJavaDSL;
	
	private FilteredTree listTemplates;
	private Text templateInfoText;
	
	public FuseIntegrationProjectWizardTemplatePage() {
		super(Messages.newProjectWizardTemplatePageName);
		setTitle(Messages.newProjectWizardTemplatePageTitle);
		setDescription(Messages.newProjectWizardTemplatePageDescription);
		setImageDescriptor(ProjectTemplatesActivator.imageDescriptorFromPlugin(ProjectTemplatesActivator.PLUGIN_ID, ProjectTemplatesActivator.IMAGE_CAMEL_PROJECT_ICON));
		setPageComplete(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));
		
		Label lblHeadline = new Label(container, SWT.None);
		lblHeadline.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		lblHeadline.setText(Messages.newProjectWizardTemplatePageHeadlineLabel);
		
		Composite grpEmptyVsTemplate = new Composite(container, SWT.None);
		GridLayout gridLayout = new GridLayout(1, false);
		grpEmptyVsTemplate.setLayout(gridLayout);
	    grpEmptyVsTemplate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
	       
	    buttonEmptyProject = new Button(grpEmptyVsTemplate, SWT.RADIO);
	    buttonEmptyProject.setText(Messages.newProjectWizardTemplatePageEmptyProjectLabel);
	    buttonEmptyProject.setToolTipText(Messages.newProjectWizardTemplatePageEmptyProjectDescription);
	    buttonEmptyProject.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		setTemplatesActive(false);
	    		validate();
	    	}
	    });
	    
	    buttonTemplateProject = new Button(grpEmptyVsTemplate, SWT.RADIO);
	    buttonTemplateProject.setText(Messages.newProjectWizardTemplatePageTemplateProjectLabel);
	    buttonTemplateProject.setToolTipText(Messages.newProjectWizardTemplatePageTemplateProjectDescription);
	    buttonTemplateProject.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		setTemplatesActive(true);
	    		validate();
	    	}
	    });

	    Composite templates = new Composite(grpEmptyVsTemplate, SWT.None);
	    GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		gd.horizontalIndent = 20;
	    templates.setLayoutData(gd);
	    templates.setLayout(new GridLayout(2, true));
	    
	    listTemplates = createFilteredTree(templates);
	    
	    templateInfoText = new Text(templates, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
	    templateInfoText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    
	    Label spacer = new Label(container, SWT.None);
	    spacer.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1));
	    
	    Label lblDsl = new Label(container, SWT.None);
		lblDsl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		lblDsl.setText(Messages.newProjectWizardTemplatePageDSLLabel);
			    
		Composite grpDslSelection = new Composite(container, SWT.None);
	    gridLayout = new GridLayout(1, false);
		grpDslSelection.setLayout(gridLayout);
		gd = new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1);
		gd.horizontalIndent = 20;
	    grpDslSelection.setLayoutData(gd);
	    
	    buttonBlueprintDSL = new Button(grpDslSelection, SWT.RADIO);
	    buttonBlueprintDSL.setText(Messages.newProjectWizardTemplatePageBlueprintDSLLabel);
	    buttonBlueprintDSL.setToolTipText(Messages.newProjectWizardTemplatePageBlueprintDSLDescription);
	    buttonBlueprintDSL.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		validate();
	    	}
	    });
	    
	    buttonSpringDSL = new Button(grpDslSelection, SWT.RADIO);
	    buttonSpringDSL.setText(Messages.newProjectWizardTemplatePageSpringDSLLabel);
	    buttonSpringDSL.setToolTipText(Messages.newProjectWizardTemplatePageSpringDSLDescription);
	    buttonSpringDSL.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		validate();
	    	}
	    });
	    
	    buttonJavaDSL = new Button(grpDslSelection, SWT.RADIO);
	    buttonJavaDSL.setText(Messages.newProjectWizardTemplatePageJavaDSLLabel);
	    buttonJavaDSL.setToolTipText(Messages.newProjectWizardTemplatePageJavaDSLDescription);
	    buttonJavaDSL.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		validate();
	    	}
	    });

	    buttonEmptyProject.setSelection(true);
	    buttonBlueprintDSL.setSelection(true);
	    
		setControl(container);
		
		setTemplatesActive(false);
		validate();
	}
	
	/**
	 * @param parent
	 * @return
	 */
	private FilteredTree createFilteredTree(Composite parent) {
		final int treeStyle = SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER;
		listTemplates = new FilteredTree(parent, treeStyle, new TemplateNameAndKeywordPatternFilter(), true);
		listTemplates.getFilterControl().setMessage(Messages.newProjectWizardTemplatePageFilterBoxText);
		listTemplates.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).create());
		listTemplates.getViewer().setContentProvider(new TemplateContentProvider());
		listTemplates.getViewer().setLabelProvider(new TemplateLabelProvider());
		listTemplates.getViewer().addFilter(new ExcludeEmptyCategoriesFilter());
		listTemplates.getViewer().setInput(getTemplates());
		listTemplates.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!event.getSelection().isEmpty()) {
					Object selObj = Selections.getFirstSelection(event.getSelection());
					if (selObj instanceof TemplateItem) {
						updateTemplateInfo((TemplateItem)selObj);
						updateDSLButtonGroup((TemplateItem)selObj);
						validate();
						return;
					}
				} 
				updateTemplateInfo(null);
			}
		});
		return listTemplates;
	}
	
	private void updateTemplateInfo(TemplateItem template) {
		if (template == null) {
			buttonBlueprintDSL.setEnabled(true);
			buttonSpringDSL.setEnabled(true);
			buttonJavaDSL.setEnabled(true);
			templateInfoText.setText("");
		} else {
			buttonBlueprintDSL.setEnabled(template.getTemplate().supportsDSL(CamelDSLType.BLUEPRINT));
			buttonSpringDSL.setEnabled(template.getTemplate().supportsDSL(CamelDSLType.SPRING));
			buttonJavaDSL.setEnabled(template.getTemplate().supportsDSL(CamelDSLType.JAVA));
			templateInfoText.setText(template.getDescription());
		}
		validate();
	}
	
	/**
	 * /!\ Public for test purpose
	 */
	public TemplateModel getTemplates() {
		return new TemplateModel();
	}
	
	private void setTemplatesActive(boolean active) {
		listTemplates.getViewer().getTree().setEnabled(active);
		listTemplates.getViewer().getTree().getParent().setEnabled(active);
		listTemplates.getFilterControl().setEnabled(active);
		listTemplates.setEnabled(active);
		templateInfoText.setEnabled(active);
		if (!active) {
			// user selected Empty Project -> activate all DSL buttons
			buttonBlueprintDSL.setEnabled(true);
			buttonSpringDSL.setEnabled(true);
			buttonJavaDSL.setEnabled(true);
		} else {
			// user wants to use template -> activate dsl buttons if supported
			buttonBlueprintDSL.setEnabled(getSelectedTemplate() != null && getSelectedTemplate().getTemplate().supportsDSL(CamelDSLType.BLUEPRINT));
			buttonSpringDSL.setEnabled(getSelectedTemplate() != null && getSelectedTemplate().getTemplate().supportsDSL(CamelDSLType.SPRING));
			buttonJavaDSL.setEnabled(getSelectedTemplate() != null && getSelectedTemplate().getTemplate().supportsDSL(CamelDSLType.JAVA));
			updateDSLButtonGroup(getSelectedTemplate());
		}
	}
	
	private void selectFirstSupportedDSLType(TemplateItem template) {
		for (CamelDSLType dslType : CamelDSLType.values()) {
			if (template.getTemplate().supportsDSL(dslType)) {
				selectButtonForDSL(dslType);
				return;
			}
		}
	}
	
	private void updateDSLButtonGroup(TemplateItem template) {
		if (template == null || !disabledDSLSelected()) return;
		selectFirstSupportedDSLType(template);
	}
	
	private void selectButtonForDSL(CamelDSLType dsltype) {
		if (dsltype.equals(CamelDSLType.BLUEPRINT)) {
			buttonBlueprintDSL.setSelection(true);
			buttonJavaDSL.setSelection(false);
			buttonSpringDSL.setSelection(false);
		} else if (dsltype.equals(CamelDSLType.JAVA)) {
			buttonBlueprintDSL.setSelection(false);
			buttonJavaDSL.setSelection(true);
			buttonSpringDSL.setSelection(false);
		} else {
			buttonBlueprintDSL.setSelection(false);
			buttonJavaDSL.setSelection(false);
			buttonSpringDSL.setSelection(true);
		}
	}
	
	private boolean isSelectedAndDisabled(Button button) {
		return button.getSelection() && !button.isEnabled();
	}
	
	private boolean disabledDSLSelected() {
		return 	isSelectedAndDisabled(buttonBlueprintDSL) ||
				isSelectedAndDisabled(buttonJavaDSL) ||
				isSelectedAndDisabled(buttonSpringDSL);
	}
	
	private void validate() {
		if (buttonTemplateProject.getSelection() &&
			(listTemplates.getViewer().getSelection().isEmpty() || 
			 Selections.getFirstSelection(listTemplates.getViewer().getSelection()) instanceof CategoryItem || !isSelectedDSLSupported())) {
			setPageComplete(false);
		} else {
			setPageComplete(true);
		}
	}
	
	/**
	 * checks if the selected template supports the selected DSL
	 * 
	 * @return
	 */
	private boolean isSelectedDSLSupported() {
		CamelDSLType dsl = null;
		if (buttonBlueprintDSL.getSelection()) {
			dsl = CamelDSLType.BLUEPRINT;
		} else if (buttonSpringDSL.getSelection()) {
			dsl = CamelDSLType.SPRING;
		} else if (buttonJavaDSL.getSelection()) {
			dsl = CamelDSLType.JAVA;
		}
		return 	dsl != null && 
				getSelectedTemplate() != null && 
				getSelectedTemplate().getTemplate() != null &&
				getSelectedTemplate().getTemplate().supportsDSL(dsl);
	}
	
	/**
	 * returns true if the user selected empty project
	 * 
	 * @return
	 */
	public boolean isEmptyProject() {
		if (!Widgets.isDisposed(buttonEmptyProject)) {
			return buttonEmptyProject.getSelection();
		} else {
			return getSelectedTemplate() == null;
		}
	}
		
	/**
	 * returns true if the user selected a template project
	 * 
	 * @return
	 */
	public boolean isTemplateProject() {
		if (buttonTemplateProject != null && !buttonTemplateProject.isDisposed()) {
			return buttonTemplateProject.getSelection();
		}
		return false;
	}
	
	/**
	 * returns the selected template or null if none selected
	 * 
	 * @return
	 */
	public TemplateItem getSelectedTemplate() {
		if (buttonTemplateProject.getSelection() && 
			!listTemplates.getViewer().getSelection().isEmpty()) {
			Object o = Selections.getFirstSelection(listTemplates.getViewer().getSelection());
			if (o instanceof TemplateItem) {
				return (TemplateItem)o;
			}
		}
		return null;
	}
	
	public CamelDSLType getDSL() {
		if (buttonBlueprintDSL.getSelection()) return CamelDSLType.BLUEPRINT;
		if (buttonSpringDSL.getSelection()) return CamelDSLType.SPRING;
		if (buttonJavaDSL.getSelection()) return CamelDSLType.JAVA;
		return null;
	}
	
	/**
	 * /!\ Public for test purpose
	 */
	public FilteredTree getListTemplates() {
		return this.listTemplates;
	}
	
	/**
	 * /!\ Public for test purpose
	 */
	public Button getBtnBlueprintDSL() {
		return this.buttonBlueprintDSL;
	}
	
	/**
	 * /!\ Public for test purpose
	 */
	public Button getBtnJavaDSL() {
		return this.buttonJavaDSL;
	}
	
	/**
	 * @return the btn_springDSL
	 */
	public Button getBtnSpringDSL() {
		return this.buttonSpringDSL;
	}
	
	/**
	 * /!\ Public for test purpose
	 */
	public Button getBtnTemplateProject() {
		return this.buttonTemplateProject;
	}
}
