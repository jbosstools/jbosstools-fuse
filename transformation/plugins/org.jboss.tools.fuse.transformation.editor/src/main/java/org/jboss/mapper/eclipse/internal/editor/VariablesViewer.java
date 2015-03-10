/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others. 
 * All rights reserved. This program and the accompanying materials are 
 * made available under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.mapper.eclipse.internal.editor;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.jboss.mapper.Variable;

/**
 *
 */
public final class VariablesViewer extends Composite {

    /**
     * @param parent
     * @param variables
     */
    public VariablesViewer(final Composite parent,
            final List<Variable> variables) {
        super(parent, SWT.NONE);

        setLayout(GridLayoutFactory.fillDefaults().create());
        setBackground(parent.getBackground());

        final ToolBar toolBar = new ToolBar(this, SWT.NONE);
        final ToolItem addButton = new ToolItem(toolBar, SWT.PUSH);
        addButton.setImage(PlatformUI.getWorkbench().getSharedImages()
                .getImage(ISharedImages.IMG_OBJ_ADD));
        addButton.setToolTipText("Add a new variable");
        final ToolItem deleteButton = new ToolItem(toolBar, SWT.PUSH);
        deleteButton.setImage(PlatformUI.getWorkbench().getSharedImages()
                .getImage(ISharedImages.IMG_ETOOL_DELETE));
        deleteButton.setToolTipText("Delete the selected variable(s)");
        deleteButton.setEnabled(false);

        final ListViewer listViewer = new ListViewer(this);
        listViewer.getList()
                .setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        listViewer.addDragSupport(DND.DROP_MOVE,
                new Transfer[] {LocalSelectionTransfer.getTransfer()},
                new DragSourceAdapter() {

                    @Override
                    public void dragSetData(final DragSourceEvent event) {
                        if (LocalSelectionTransfer.getTransfer().isSupportedType(event.dataType)) {
                            LocalSelectionTransfer.getTransfer().setSelection(
                                    listViewer.getSelection());
                        }
                    }
                });
        listViewer.setComparator(new ViewerComparator());
        listViewer.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(final Object element) {
                return (String) element;
            }
        });
        addButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                final InputDialog dlg = new InputDialog(getShell(),
                        "Add Variable",
                        "Enter a new variable name",
                        null,
                        new IInputValidator() {

                            @Override
                            public String isValid(final String text) {
                                return listViewer.getList().indexOf(text) < 0 ? null
                                        : "Variable already exists";
                            }
                        });
                if (dlg.open() == Window.OK) {
                    listViewer.add(dlg.getValue()); // TODO should be a variable
                }
            }
        });
        listViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                deleteButton.setEnabled(!event.getSelection().isEmpty());
            }
        });
        deleteButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                for (final Iterator<?> iter =
                        ((IStructuredSelection) listViewer.getSelection()).iterator(); iter
                        .hasNext();) {
                    listViewer.remove(iter.next());
                }
            }
        });

        // Populate
        for (final Variable variable : variables) {
            listViewer.add(variable.getName());
        }
    }
}
