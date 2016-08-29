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

	private Button btn_emptyProject;
	private Button btn_templateProject;
	private Button btn_blueprintDSL;
	private Button btn_springDSL;
	private Button btn_javaDSL;
	
	private FilteredTree list_templates;
	private Text templateInfoText;
	
	/**
	 * 
	 */
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
		
		Label lbl_headline = new Label(container, SWT.None);
		lbl_headline.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		lbl_headline.setText(Messages.newProjectWizardTemplatePageHeadlineLabel);
		
		Composite grp_emptyVsTemplate = new Composite(container, SWT.None);
		GridLayout gridLayout = new GridLayout(1, false);
		grp_emptyVsTemplate.setLayout(gridLayout);
	    grp_emptyVsTemplate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
	       
	    btn_emptyProject = new Button(grp_emptyVsTemplate, SWT.RADIO);
	    btn_emptyProject.setText(Messages.newProjectWizardTemplatePageEmptyProjectLabel);
	    btn_emptyProject.setToolTipText(Messages.newProjectWizardTemplatePageEmptyProjectDescription);
	    btn_emptyProject.addSelectionListener(new SelectionAdapter() {
	    	/*
	    	 * (non-Javadoc)
	    	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	    	 */
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		setTemplatesActive(false);
	    		validate();
	    	};
	    });
	    
	    btn_templateProject = new Button(grp_emptyVsTemplate, SWT.RADIO);
	    btn_templateProject.setText(Messages.newProjectWizardTemplatePageTemplateProjectLabel);
	    btn_templateProject.setToolTipText(Messages.newProjectWizardTemplatePageTemplateProjectDescription);
	    btn_templateProject.addSelectionListener(new SelectionAdapter() {
	    	/*
	    	 * (non-Javadoc)
	    	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	    	 */
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		setTemplatesActive(true);
	    		validate();
	    	}
	    });

	    Composite templates = new Composite(grp_emptyVsTemplate, SWT.None);
	    GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		gd.horizontalIndent = 20;
	    templates.setLayoutData(gd);
	    templates.setLayout(new GridLayout(2, true));
	    
	    list_templates = createFilteredTree(templates);
	    
	    templateInfoText = new Text(templates, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
	    templateInfoText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    
	    Label spacer = new Label(container, SWT.None);
	    spacer.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1));
	    
	    Label lbl_dsl = new Label(container, SWT.None);
		lbl_dsl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		lbl_dsl.setText(Messages.newProjectWizardTemplatePageDSLLabel);
			    
		Composite grp_dslSelection = new Composite(container, SWT.None);
	    gridLayout = new GridLayout(1, false);
		grp_dslSelection.setLayout(gridLayout);
		gd = new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1);
		gd.horizontalIndent = 20;
	    grp_dslSelection.setLayoutData(gd);
	    
	    btn_blueprintDSL = new Button(grp_dslSelection, SWT.RADIO);
	    btn_blueprintDSL.setText(Messages.newProjectWizardTemplatePageBlueprintDSLLabel);
	    btn_blueprintDSL.setToolTipText(Messages.newProjectWizardTemplatePageBlueprintDSLDescription);
	    btn_blueprintDSL.addSelectionListener(new SelectionAdapter() {
	    	/*
	    	 * (non-Javadoc)
	    	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	    	 */
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		validate();
	    	};
	    });
	    
	    btn_springDSL = new Button(grp_dslSelection, SWT.RADIO);
	    btn_springDSL.setText(Messages.newProjectWizardTemplatePageSpringDSLLabel);
	    btn_springDSL.setToolTipText(Messages.newProjectWizardTemplatePageSpringDSLDescription);
	    btn_springDSL.addSelectionListener(new SelectionAdapter() {
	    	/*
	    	 * (non-Javadoc)
	    	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	    	 */
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		validate();
	    	};
	    });
	    
	    btn_javaDSL = new Button(grp_dslSelection, SWT.RADIO);
	    btn_javaDSL.setText(Messages.newProjectWizardTemplatePageJavaDSLLabel);
	    btn_javaDSL.setToolTipText(Messages.newProjectWizardTemplatePageJavaDSLDescription);
	    btn_javaDSL.addSelectionListener(new SelectionAdapter() {
	    	/*
	    	 * (non-Javadoc)
	    	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	    	 */
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		validate();
	    	};
	    });

	    btn_emptyProject.setSelection(true);
	    btn_blueprintDSL.setSelection(true);
	    
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
		list_templates = new FilteredTree(parent, treeStyle, new TemplateNameAndKeywordPatternFilter(), true);
		list_templates.getFilterControl().setMessage(Messages.newProjectWizardTemplatePageFilterBoxText);
		list_templates.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).create());
		list_templates.getViewer().setContentProvider(new TemplateContentProvider());
		list_templates.getViewer().setLabelProvider(new TemplateLabelProvider());
		list_templates.getViewer().addFilter(new ExcludeEmptyCategoriesFilter());
		list_templates.getViewer().setInput(getTemplates());
		list_templates.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!event.getSelection().isEmpty()) {
					Object selObj = Selections.getFirstSelection(event.getSelection());
					if (selObj instanceof TemplateItem) {
						updateTemplateInfo((TemplateItem)selObj);
						updateDSLButtonGroup((TemplateItem)selObj);
						return;
					}
				} 
				updateTemplateInfo(null);
			}
		});
		return list_templates;
	}
	
	private void updateTemplateInfo(TemplateItem template) {
		if (template == null) {
			btn_blueprintDSL.setEnabled(true);
			btn_springDSL.setEnabled(true);
			btn_javaDSL.setEnabled(true);
			templateInfoText.setText("");
		} else {
			btn_blueprintDSL.setEnabled(template.getTemplate().supportsDSL(CamelDSLType.BLUEPRINT));
			btn_springDSL.setEnabled(template.getTemplate().supportsDSL(CamelDSLType.SPRING));
			btn_javaDSL.setEnabled(template.getTemplate().supportsDSL(CamelDSLType.JAVA));
			templateInfoText.setText(template.getDescription());
		}
		validate();
	}
	
	private TemplateModel getTemplates() {
		return new TemplateModel();
	}
	
	private void setTemplatesActive(boolean active) {
		list_templates.getViewer().getTree().setEnabled(active);
		list_templates.getViewer().getTree().getParent().setEnabled(active);
		list_templates.getFilterControl().setEnabled(active);
		list_templates.setEnabled(active);
		templateInfoText.setEnabled(active);
		if (!active) {
			// user selected Empty Project -> activate all DSL buttons
			btn_blueprintDSL.setEnabled(true);
			btn_springDSL.setEnabled(true);
			btn_javaDSL.setEnabled(true);
		} else {
			// user wants to use template -> activate dsl buttons if supported
			btn_blueprintDSL.setEnabled(getSelectedTemplate() != null && getSelectedTemplate().getTemplate().supportsDSL(CamelDSLType.BLUEPRINT));
			btn_springDSL.setEnabled(getSelectedTemplate() != null && getSelectedTemplate().getTemplate().supportsDSL(CamelDSLType.SPRING));
			btn_javaDSL.setEnabled(getSelectedTemplate() != null && getSelectedTemplate().getTemplate().supportsDSL(CamelDSLType.JAVA));
			updateDSLButtonGroup(getSelectedTemplate());
		}
	}
	
	private void updateDSLButtonGroup(TemplateItem template) {
		if (template == null || !disabledDSLSelected()) return;
		for (CamelDSLType dslType : CamelDSLType.values()) {
			if (template.getTemplate().supportsDSL(dslType)) {
				selectButtonForDSL(dslType);
				return;
			}
		}
	}
	
	private void selectButtonForDSL(CamelDSLType dsltype) {
		if (dsltype.equals(CamelDSLType.BLUEPRINT)) {
			btn_blueprintDSL.setSelection(true);
			btn_javaDSL.setSelection(false);
			btn_springDSL.setSelection(false);
		} else if (dsltype.equals(CamelDSLType.JAVA)) {
			btn_blueprintDSL.setSelection(false);
			btn_javaDSL.setSelection(true);
			btn_springDSL.setSelection(false);
		} else {
			btn_blueprintDSL.setSelection(false);
			btn_javaDSL.setSelection(false);
			btn_springDSL.setSelection(true);
		}
	}
	
	private boolean disabledDSLSelected() {
		return 	(btn_blueprintDSL.getSelection() == true && btn_blueprintDSL.isEnabled() == false) ||
				(btn_javaDSL.getSelection() == true && btn_javaDSL.isEnabled() == false) ||
				(btn_springDSL.getSelection() == true && btn_springDSL.isEnabled() == false);
	}
	
	private void validate() {
		if (btn_templateProject.getSelection() &&
			(list_templates.getViewer().getSelection().isEmpty() || 
			 Selections.getFirstSelection(list_templates.getViewer().getSelection()) instanceof CategoryItem || !isSelectedDSLSupported())) {
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
		if (btn_blueprintDSL.getSelection()) {
			dsl = CamelDSLType.BLUEPRINT;
		} else if (btn_springDSL.getSelection()) {
			dsl = CamelDSLType.SPRING;
		} else if (btn_javaDSL.getSelection()) {
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
		if (!Widgets.isDisposed(btn_emptyProject)) {
			return btn_emptyProject.getSelection();
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
		if (btn_templateProject != null && !btn_templateProject.isDisposed()) {
			return btn_templateProject.getSelection();
		}
		return false;
	}
	
	/**
	 * returns the selected template or null if none selected
	 * 
	 * @return
	 */
	public TemplateItem getSelectedTemplate() {
		if (btn_templateProject.getSelection() && 
			!list_templates.getViewer().getSelection().isEmpty()) {
			Object o = Selections.getFirstSelection(list_templates.getViewer().getSelection());
			if (o instanceof TemplateItem) {
				return (TemplateItem)o;
			}
		}
		return null;
	}
	
	public CamelDSLType getDSL() {
		if (btn_blueprintDSL.getSelection()) return CamelDSLType.BLUEPRINT;
		if (btn_springDSL.getSelection()) return CamelDSLType.SPRING;
		if (btn_javaDSL.getSelection()) return CamelDSLType.JAVA;
		return null;
	}
}
