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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.fusesource.ide.foundation.ui.util.Selections;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.wizards.pages.filter.CompatibleEnvironmentFilter;
import org.fusesource.ide.projecttemplates.wizards.pages.filter.ExcludeEmptyCategoriesFilter;
import org.fusesource.ide.projecttemplates.wizards.pages.filter.TemplateNameAndKeywordPatternFilter;
import org.fusesource.ide.projecttemplates.wizards.pages.model.CategoryItem;
import org.fusesource.ide.projecttemplates.wizards.pages.model.EnvironmentData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateItem;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateModel;
import org.fusesource.ide.projecttemplates.wizards.pages.provider.TemplateContentProvider;
import org.fusesource.ide.projecttemplates.wizards.pages.provider.TemplateLabelProvider;

/**
 * @author lhein
 */
public class FuseIntegrationProjectWizardTemplatePage extends WizardPage {

	private FilteredTree listTemplates;
	private Text templateInfoText;
	private EnvironmentData environment;
	
	public FuseIntegrationProjectWizardTemplatePage(EnvironmentData environment) {
		super(Messages.newProjectWizardTemplatePageName);
		this.environment = environment;
		setTitle(Messages.newProjectWizardTemplatePageTitle);
		setDescription(Messages.newProjectWizardTemplatePageDescription);
		setImageDescriptor(ProjectTemplatesActivator.imageDescriptorFromPlugin(ProjectTemplatesActivator.PLUGIN_ID, ProjectTemplatesActivator.IMAGE_CAMEL_PROJECT_ICON));
		setPageComplete(false);
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));

		createTemplatesPanel(container);
		createLinkForOtherExamples(container);
		
		setControl(container);
		
		selectDefaultTemplates(listTemplates);
		validate();
	}

	private void createLinkForOtherExamples(Composite container) {
		Link link = new Link(container, SWT.NONE);
		link.setLayoutData(GridDataFactory.fillDefaults().span(3, 1).grab(true, true).align(SWT.END, SWT.CENTER).create());
		link.setText("<A>"+Messages.newProjectWizardTemplatePageWhereToFindMoreExamples+"</A>");
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new WhereToFindMoreTemplatesMessageDialog(getShell()).open();
			}
		});
	}

	protected void createTemplatesPanel(Composite container) {
		Composite templates = new Composite(container, SWT.None);
		templates.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(3, 1).hint(SWT.DEFAULT, 400).create());
		templates.setLayout(new GridLayout(2, true));

		createTemplatesTopInformation(templates);
		listTemplates = createFilteredTree(templates);

		templateInfoText = new Text(templates, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		templateInfoText.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).hint(200, SWT.DEFAULT).create());
	}

	protected void createTemplatesTopInformation(Composite templates) {
		Composite infoComposite = new Composite(templates, SWT.NONE);
		infoComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
		infoComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).create());
		
		Label filteredTemplatesInformationIcon = new Label(infoComposite, SWT.NONE);
		filteredTemplatesInformationIcon.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK));
		
		Label filteredTemplatesInformationMessage = new Label(infoComposite, SWT.NONE);
		filteredTemplatesInformationMessage.setText(Messages.newProjectWizardTemplatePageTemplateFilterMessageInformation);
		filteredTemplatesInformationMessage.setFont(JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT));
	}

	private FilteredTree createFilteredTree(Composite parent) {
		final int treeStyle = SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER;
		listTemplates = new FilteredTree(parent, treeStyle, new TemplateNameAndKeywordPatternFilter(), true);
		listTemplates.getFilterControl().setMessage(Messages.newProjectWizardTemplatePageFilterBoxText);
		listTemplates.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		listTemplates.getViewer().setContentProvider(new TemplateContentProvider());
		listTemplates.getViewer().setLabelProvider(new TemplateLabelProvider());
		CompatibleEnvironmentFilter compatibleEnvironmentFilter = new CompatibleEnvironmentFilter(environment);
		listTemplates.getViewer().setFilters(
				new ExcludeEmptyCategoriesFilter(),
				compatibleEnvironmentFilter);
		listTemplates.getViewer().setInput(getTemplates());
		listTemplates.getViewer().expandAll();
		listTemplates.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!event.getSelection().isEmpty()) {
					Object selObj = Selections.getFirstSelection(event.getSelection());
					if (selObj instanceof TemplateItem) {
						updateTemplateInfo((TemplateItem)selObj);
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
			templateInfoText.setText("");
		} else {
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
	
	private void validate() {
		listTemplates.getViewer().refresh();
		if ((listTemplates.getViewer().getSelection().isEmpty() || 
			 Selections.getFirstSelection(listTemplates.getViewer().getSelection()) instanceof CategoryItem)) {
			setPageComplete(false);
		} else {
			setPageComplete(true);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	
	/**
	 * returns the selected template or null if none selected
	 * 
	 * @return
	 */
	public TemplateItem getSelectedTemplate() {
		if (!listTemplates.getViewer().getSelection().isEmpty()) {
			Object o = Selections.getFirstSelection(listTemplates.getViewer().getSelection());
			if (o instanceof TemplateItem) {
				return (TemplateItem)o;
			}
		}
		return null;
	}
	
	/**
	 * /!\ Public for test purpose
	 */
	public FilteredTree getListTemplates() {
		return this.listTemplates;
	}
	
	public void refresh() {
		if (listTemplates != null) {
			listTemplates.getViewer().refresh();
			listTemplates.getViewer().expandAll();
			selectDefaultTemplates(listTemplates);
		}
	}

	private void selectDefaultTemplates(FilteredTree listTemplates) {
		TreeViewer viewer = listTemplates.getViewer();
		Set<TreeItem> unfilteredItems = retrieveAllTreeItems(viewer);
		if (viewer.getSelection().isEmpty()) {
			if (unfilteredItems.size() == 1) {
				viewer.setSelection(new StructuredSelection(unfilteredItems.iterator().next().getData()));
			} else {
				TemplateItem defaultToSelect = determineDefaultTemplateToSelect(unfilteredItems, environment);
				if (defaultToSelect != null) {
					viewer.setSelection(new StructuredSelection(defaultToSelect));
				}
			}
		}
	}

	protected Set<TreeItem> retrieveAllTreeItems(TreeViewer viewer) {
		Set<TreeItem> res = new HashSet<>();
		TreeItem[] rootItems = viewer.getTree().getItems();
		res.addAll(retrieveAllTreeItems(rootItems));
		return res;
	}

	private Set<TreeItem> retrieveAllTreeItems(TreeItem[] items) {
		Set<TreeItem> res = new HashSet<>();
		if(items !=null) {
			res.addAll(Arrays.asList(items));
			for (TreeItem treeItem : items) {
				res.addAll(retrieveAllTreeItems(treeItem.getItems()));
			}
		}
		return res;
	}

	private TemplateItem determineDefaultTemplateToSelect(Set<TreeItem> unfilteredItems, EnvironmentData environment) {
		for (TreeItem treeItem : unfilteredItems) {
			if (treeItem.getData() instanceof TemplateItem) {
				TemplateItem templateItem = (TemplateItem) treeItem.getData();
				if (templateItem.isDefault(environment)) {
					return templateItem;
				}
			}
		}
		return null;
	}

}
