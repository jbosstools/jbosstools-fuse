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

package org.fusesource.ide.jmx.commons.views.messages;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.util.Selections;
import org.fusesource.ide.foundation.ui.util.Viewers;
import org.fusesource.ide.jmx.commons.Messages;
import org.fusesource.ide.jmx.commons.messages.Exchange;
import org.fusesource.ide.jmx.commons.messages.Exchanges;


public class MessageDetailView extends ViewPart implements IPropertySheetPage {

	private Form form;
	private Exchange selectedExchange;
	private TableViewer headerViewer;
	private Text bodyText;
	private SashForm sash;

	public MessageDetailView() {
	}


	@Override
	public void createPartControl(Composite parent) {
		createControl(parent);
	}

	@Override
	public void createControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(Display.getDefault());
		this.form = toolkit.createForm(parent);

		Composite formBody = form.getBody();
		formBody.setLayout(new FillLayout());

		this.form.setText(Messages.MessageDetailFormTitle);
		toolkit.decorateFormHeading(this.form);

		this.sash = new SashForm(formBody, SWT.SMOOTH | SWT.VERTICAL);
		this.sash.setLayout(new FillLayout());

		// force columns to use all available space
		Composite compositeUpperTable = new Composite(this.sash, SWT.NONE);
		TableColumnLayout tableLayout = new TableColumnLayout();
		compositeUpperTable.setLayout(tableLayout);

		this.headerViewer = new TableViewer(compositeUpperTable, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		TableViewerColumn col = createTableViewerColumn(Messages.MessageDetailHeadersTableNameColumn, Messages.MessageDetailHeadersTableNameColumnTooltip, SWT.LEFT);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Map.Entry<?,?> entry = (Map.Entry<?,?>) element;
				return Strings.getOrElse(entry.getKey());
			}
		});
		tableLayout.setColumnData(col.getColumn(), new ColumnWeightData(4, ColumnWeightData.MINIMUM_WIDTH, true));

		col = createTableViewerColumn(Messages.MessageDetailHeadersTableValueColumn, Messages.MessageDetailHeadersTableValueColumnTooltip, SWT.LEFT);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Map.Entry<?,?> entry = (Map.Entry<?,?>) element;
				return Strings.getOrElse(entry.getValue());
			}
		});
		tableLayout.setColumnData(col.getColumn(), new ColumnWeightData(8, ColumnWeightData.MINIMUM_WIDTH, true));

		this.headerViewer.setContentProvider(new IStructuredContentProvider() {
			@Override
			public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public Object[] getElements(Object parent) {
				if (parent instanceof Map) {
					Map<?,?> map = (Map<?,?>) parent;
					return map.entrySet().toArray();
				} else {
					return new Object[0];
				}
			}
		});

		Table table = this.headerViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setToolTipText(Messages.MessageDetailHeadersTableToolTip);
		this.headerViewer.setUseHashlookup(true);

		Composite child3 = new Composite(this.sash, SWT.NONE);
		child3.setLayout(new FillLayout());

		this.bodyText = toolkit.createText(child3, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY);
		this.bodyText.setToolTipText(Messages.MessageDetailBodyTextToolTip);

		this.sash.setWeights(new int[] {60,40});
	}

	private TableViewerColumn createTableViewerColumn(String title, String tooltip, int style) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(this.headerViewer, style);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(100);
		column.setResizable(true);
		column.setMoveable(false);
		column.setToolTipText(tooltip);
		return viewerColumn;
	}

	@Override
	public void dispose() {
		disposeForm();
		super.dispose();
	}

	protected void disposeForm() {
		if (this.form != null && !this.form.isDisposed()) {
			try {
				this.form.dispose();
			} catch (Exception e) {
				// ignore any expose exceptions
			}
		}
		this.form = null;
	}

	@Override
	public Control getControl() {
		return this.form;
	}

	@Override
	public void setActionBars(IActionBars actionBars) {
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		Object firstSelection = Selections.getFirstSelection(selection);
		Exchange exchange = Exchanges.asExchange(firstSelection);
		setSelectedExchange(exchange);
	}

	public void setSelectedExchange(Exchange selectedExchange) {
		this.selectedExchange = selectedExchange;
		updateDataBinding();
	}

	protected void updateDataBinding() {
		this.bodyText.setText(MessageUIHelper.getBody(this.selectedExchange));

		// sort the headers
		Map<String, Object> headers = Exchanges.getHeaders(this.selectedExchange);
		this.headerViewer.setInput(new TreeMap<String, Object>(headers));
		Viewers.refresh(this.headerViewer);

		layoutForm();
	}


	protected void layoutForm() {
		this.form.setRedraw(true);
	}

	@Override
	public void setFocus() {
		if (this.headerViewer != null) {
			this.headerViewer.getControl().setFocus();
		}
	}

}
