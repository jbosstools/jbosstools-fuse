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

package org.fusesource.ide.fabric.actions.jclouds;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.Widgets;
import org.fusesource.ide.commons.ui.config.ConfigurationDetails;
import org.fusesource.ide.commons.ui.views.TableViewSupport;
import org.fusesource.ide.commons.util.Function1;


public class CloudDetailsTable extends TableViewSupport {
	private Button addButton;
	private Button editButton;
	private Button deleteButton;
	private Action editAction;
	private final IChangeListener changeListener = new IChangeListener() {

		@Override
		public void handleChange(ChangeEvent event) {
			Widgets.refresh(getViewer());
		}
	};
	private CloudDetailsAddAction addAction;
	private CloudDetailsDeleteAction deleteAction;
	
	private CloudDetails selectedCloud;

	public CloudDetailsTable() {
		setShowSearchBox(false);
	}

	@Override
	public void dispose() {
		CloudDetails.getCloudDetailList().removeChangeListener(changeListener);
		super.dispose();
	}


	@Override
	protected void createColumns() {
		clearColumns();

		int bounds = 150;
		int column = 0;

		Function1 function = new Function1() {
			@Override
			public Object apply(Object element) {
				CloudDetails exchange = CloudDetails.asCloudDetails(element);
				if (exchange != null) {
					return exchange.getName();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Name");

		function = new Function1() {
			@Override
			public Object apply(Object element) {
				CloudDetails exchange = CloudDetails.asCloudDetails(element);
				if (exchange != null) {
					return exchange.getProviderName();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Provider");

		function = new Function1() {
			@Override
			public Object apply(Object element) {
				CloudDetails exchange = CloudDetails.asCloudDetails(element);
				if (exchange != null) {
					return exchange.getApiName();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Api");
		
		function = new Function1() {
			@Override
			public Object apply(Object element) {
				CloudDetails exchange = CloudDetails.asCloudDetails(element);
				if (exchange != null) {
					return exchange.getEndpoint();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Endpoint");
		
		function = new Function1() {
			@Override
			public Object apply(Object element) {
				CloudDetails exchange = CloudDetails.asCloudDetails(element);
				if (exchange != null) {
					return exchange.getIdentity();
				}
				return null;
			}
		};
		
		
		column = addColumnFunction(bounds, column, function, "Identity");
	}


	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		getViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectedCloud = CloudDetails.asCloudDetails(Selections.getFirstSelection(event.getSelection()));
				selectionUpdated();
			}
		});

		Composite buttonBar = new Composite(parent, SWT.NONE);

		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalAlignment = SWT.CENTER;
		buttonBar.setLayoutData(gridData);

		RowLayout layout = new RowLayout();
		layout.center = true;
		buttonBar.setLayout(layout);

		addAction = new CloudDetailsAddAction() {
			@Override
			protected void onCloudDetailsAdded(ConfigurationDetails details) {
				getViewer().setSelection(new StructuredSelection(details));
			}
		};
		editAction = new CloudDetailsEditAction() {

			@Override
			protected CloudDetails getSelectedCloudDetails() {
				return getSelectedCloud();
			}

			@Override
			protected void onCloudDetailsEdited(Object found) {
				if (found != null) {
					getViewer().setSelection(new StructuredSelection(found));
				}
			}

		};
		setDoubleClickAction(editAction);

		deleteAction = new CloudDetailsDeleteAction() {
			@Override
			protected CloudDetails getSelectedCloudDetails() {
				return getSelectedCloud();
			}
		};


		addButton = Widgets.createActionButton(buttonBar,  addAction);
		editButton = Widgets.createActionButton(buttonBar,  editAction);
		deleteButton = Widgets.createActionButton(buttonBar,  deleteAction);

		selectionUpdated();


		CloudDetails.getCloudDetailList().addChangeListener(changeListener);
	}

	@Override
	protected void configureViewer() {

		// load the current cloud details...
		reload();
	}

	public void reload() {
		getViewer().setInput(CloudDetails.getCloudDetailList());
	}

	@Override
	protected IStructuredContentProvider createContentProvider() {
		return ArrayContentProvider.getInstance();
	}

	@Override
	protected String getHelpID() {
		return getClass().getName();
	}


	public CloudDetails getSelectedCloud() {
		return this.selectedCloud;
	}

	protected void selectionUpdated() {
		boolean selected = getSelectedCloud() != null;
		editAction.setEnabled(selected);
		editButton.setEnabled(selected);
		deleteAction.setEnabled(selected);
		deleteButton.setEnabled(selected);
	}

}
