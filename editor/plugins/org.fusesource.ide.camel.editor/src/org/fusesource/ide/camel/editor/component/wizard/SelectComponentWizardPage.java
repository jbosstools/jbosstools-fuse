/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.component.wizard;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author Aurelien Pupier
 *
 */
public class SelectComponentWizardPage extends WizardPage {

	private CamelModel model;
	private DataBindingContext dbc;

	private Component componentSelected;
	private String id;
	private AbstractCamelModelElement parent;

	/**
	 * @param pageName
	 */
	public SelectComponentWizardPage(DataBindingContext dbc, CamelModel model, String title, String description, AbstractCamelModelElement parent) {
		super(UIMessages.selectComponentWizardPagePageName);
		setTitle(title);
		setDescription(description);
		this.dbc = dbc;
		this.model = model;
		this.parent = parent;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).create());
		createIdLine(composite);

		createCamelComponentSelectionGroup(composite);
		setControl(composite);
		WizardPageSupport.create(this, dbc);
	}

	/**
	 * @param composite
	 */
	private void createIdLine(Composite composite) {
		Label idLabel = new Label(composite, SWT.NONE);
		idLabel.setText(UIMessages.globalEndpointWizardPageIdFieldLabel);
		Text idText = new Text(composite, SWT.BORDER);
		idText.setLayoutData(GridDataFactory.fillDefaults().indent(10, 0).create());
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new NewEndpointIdValidator(parent));

		final IObservableValue idObservable = PojoProperties.value(SelectComponentWizardPage.class, "id", String.class).observe(this); //$NON-NLS-1$
		Binding binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(idText), idObservable, strategy, null);
		ControlDecorationSupport.create(binding, SWT.LEFT | SWT.TOP);
	}

	/**
	 * @param parent
	 */
	private void createCamelComponentSelectionGroup(Composite parent) {
		Group componentSelectionGroup = new Group(parent, SWT.NONE);
		componentSelectionGroup.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
		componentSelectionGroup.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).create());
		componentSelectionGroup.setText(UIMessages.globalEndpointWizardPageComponentSelectionGroupTitle);
		final FilteredTree filteredTree = createFilteredTree(componentSelectionGroup);
		createCheckboxFilterComposite(componentSelectionGroup, filteredTree.getViewer());
	}

	/**
	 * @param componentSelectionGroup
	 * @return
	 */
	private FilteredTree createFilteredTree(Group componentSelectionGroup) {
		final int treeStyle = SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER;
		final FilteredTree filteredTree = new FilteredTree(componentSelectionGroup, treeStyle, new ComponentNameAndTagPatternFilter(), true);
		filteredTree.getFilterControl().setMessage(UIMessages.globalEndpointWizardPageFilterSearchMessage);
		final int xHint = getShell().getSize().x - 20;
		filteredTree.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).hint(xHint, 400).create());
		final TreeViewer treeViewer = filteredTree.getViewer();
		treeViewer.setContentProvider(new ComponentListTreeContentProvider());
		treeViewer.setComparator(new ViewerComparator());
		treeViewer.setLabelProvider(new ComponentLabelProvider());
		treeViewer.setInput(new ComponentManager(model));
		UpdateValueStrategy strategy = new UpdateValueStrategy() ;
		strategy.setBeforeSetValidator(new IValidator() {
			
			@Override
			public IStatus validate(Object value) {
				if (value instanceof Component) {
					return Status.OK_STATUS ;
				}
				return ValidationStatus.error(UIMessages.globalEndpointWizardPageComponentSelectionMandatoryMessage);
			}
		}) ;
		
		dbc.bindValue(ViewerProperties.singleSelection().observe(treeViewer), PojoProperties.value(SelectComponentWizardPage.class, "componentSelected", Component.class).observe(this), //$NON-NLS-1$
				strategy, null);
		return filteredTree;
	}

	/**
	 * @param componentSelectionGroup
	 * @param treeViewer
	 */
	private void createCheckboxFilterComposite(Group componentSelectionGroup, final TreeViewer treeViewer) {
		Composite buttonsComposite = new Composite(componentSelectionGroup, SWT.NONE);
		buttonsComposite.setLayout(GridLayoutFactory.fillDefaults().create());
		buttonsComposite.setLayoutData(GridDataFactory.fillDefaults().create());
		createCheckboxToGroupByCategory(buttonsComposite, treeViewer);
		createCheckboxToShowOnlyPaletteComponents(buttonsComposite, treeViewer);
	}

	private Button createCheckboxToShowOnlyPaletteComponents(Composite parent, final TreeViewer treeViewer) {
		final Button showOnlyPaletteComponents = new Button(parent, SWT.CHECK);
		showOnlyPaletteComponents.setText(UIMessages.globalEndpointWizardPageShowOnlyPaletteComonentsChecboxText);
		final WhiteListComponentFilter whiteListComponentFilter = new WhiteListComponentFilter();
		treeViewer.addFilter(whiteListComponentFilter);
		showOnlyPaletteComponents.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				if (showOnlyPaletteComponents.getSelection()) {
					treeViewer.addFilter(whiteListComponentFilter);
				} else {
					treeViewer.removeFilter(whiteListComponentFilter);
				}
			}
		});
		showOnlyPaletteComponents.setSelection(true);
		return showOnlyPaletteComponents;
	}

	private Button createCheckboxToGroupByCategory(Composite parent, final TreeViewer treeViewer) {
		final Button groupedByCategories = new Button(parent, SWT.CHECK);
		groupedByCategories.setText(UIMessages.globalEndpointWizardPageGroupByCategories);
		groupedByCategories.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (groupedByCategories.getSelection()) {
					treeViewer.setContentProvider(new ComponentGroupedByTagsTreeContenProvider());
				} else {
					treeViewer.setContentProvider(new ComponentListTreeContentProvider());
				}
			}
		});
		return groupedByCategories;
	}

	/**
	 * @return the componentSelected
	 */
	public Component getComponentSelected() {
		return componentSelected;
	}

	/**
	 * @param componentSelected
	 *            the componentSelected to set
	 */
	public void setComponentSelected(Component componentSelected) {
		this.componentSelected = componentSelected;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

}
