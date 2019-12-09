/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal;

import java.util.Iterator;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.fuse.transformation.core.Variable;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationManager;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Decorations;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;

public class VariablesViewer extends Composite {

    final TableViewer tableViewer;

    public VariablesViewer(final TransformationManager manager,
                           Composite parent) {
        super(parent, SWT.NONE);

        setLayout(GridLayoutFactory.fillDefaults().create());
        setBackground(parent.getParent().getParent().getBackground());

        // Create tool bar
        ToolBar toolBar = new ToolBar(this, SWT.NONE);
        ToolItem addButton = new ToolItem(toolBar, SWT.PUSH);
        addButton.setImage(new DecorationOverlayIcon(Images.VARIABLE, Decorations.ADD, IDecoration.TOP_RIGHT).createImage());
        addButton.setToolTipText(Messages.VariablesViewer_toolbarTooltip_addNewVariable);
        final ToolItem deleteButton = new ToolItem(toolBar, SWT.PUSH);
        deleteButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
        deleteButton.setToolTipText(Messages.VariablesViewer_toolbarTooltip_DeleteVariable);
        deleteButton.setEnabled(false);

        // Create table
        tableViewer = new TableViewer(this);
        tableViewer.getTable().setHeaderVisible(true);
        tableViewer.getTable().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        tableViewer.setComparator(new ViewerComparator());
        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        // Create columns
        // TODO add support for changing variable names
        final TableViewerColumn nameColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        nameColumn.getColumn().setText(Messages.VariablesViewer_columnName_name);
        nameColumn.getColumn().setImage(Images.VARIABLE);
        nameColumn.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public Image getImage(Object element) {
                Image img = Images.VARIABLE;
                if (manager.mapped((Variable) element))
                    return new DecorationOverlayIcon(img, Decorations.MAPPED, IDecoration.BOTTOM_RIGHT).createImage();
                return img;
            }

            @Override
            public String getText(Object element) {
                return ((Variable) element).getName();
            }
        });
        final TableViewerColumn valueColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        valueColumn.getColumn().setText(Messages.VariablesViewer_columnName_value);
        valueColumn.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(final Object element) {
                return "\"" + ((Variable) element).getValue() + "\""; //$NON-NLS-1$ //$NON-NLS-2$
            }
        });
        valueColumn.setEditingSupport(new EditingSupport(tableViewer) {

            private final TextCellEditor cellEditor = new TextCellEditor(tableViewer.getTable());

            @Override
            protected boolean canEdit(Object element) {
                return true;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return cellEditor;
            }

            @Override
            protected Object getValue(Object element) {
                return ((Variable)element).getValue();
            }

            @Override
            protected void setValue(Object element,
                                    Object value) {
                Variable variable = (Variable)element;
                manager.setValue(variable, value.toString());
                try {
                    manager.save();
                    tableViewer.setInput(manager.variables());
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });

        // Wire
        tableViewer.addDragSupport(DND.DROP_MOVE, new Transfer[] {LocalSelectionTransfer.getTransfer()}, new DragSourceAdapter() {

            @Override
            public void dragStart(final DragSourceEvent event) {
                LocalSelectionTransfer.getTransfer().setSelection(tableViewer.getSelection());
            }
        });
        addButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                InputDialog dlg =
                    new InputDialog(getShell(), Messages.VariablesViewer_addVariableDialogTitle, Messages.VariablesViewer_addVariableDialogDescription, null, new IInputValidator() {

                    @Override
                    public String isValid(String text) {
                        for (final Variable variable : manager.variables()) {
                            if (variable.getName().equals(text)) return Messages.VariablesViewer_addVariableDialog_validation_variablealreadyExists;
                        }
                        return null;
                    }
                });
                if (dlg.open() != Window.OK) return;
                manager.addVariable(dlg.getValue(), dlg.getValue());
                try {
                    manager.save();
                    tableViewer.setInput(manager.variables());
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                deleteButton.setEnabled(!event.getSelection().isEmpty());
            }
        });
        deleteButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                boolean deleteAll = false;
                try {
                    for (Iterator<?> iter = tableViewer.getStructuredSelection().iterator(); iter.hasNext();) {
                        Variable variable = (Variable) iter.next();
                        if (manager.mapped(variable)) {
                            if (!deleteAll) {
                                MessageDialog dlg = new MessageDialog(getShell(),
                                                                      Messages.VariablesViewer_confirm,
                                                                      null,
										Messages.bind(Messages.VariablesViewer_deleteConfirmationMessage, variable.getName()),
                                                                      MessageDialog.WARNING,
                                                                      new String[] {
 IDialogConstants.CANCEL_LABEL, IDialogConstants.YES_LABEL, IDialogConstants.YES_TO_ALL_LABEL, IDialogConstants.NO_LABEL
                                                                      },
                                                                      3);
                                int button = dlg.open();
                                if (button == 2) deleteAll = true;
                                else if (button == 3) continue;
                                else if (button < 1) return;
                            }
                        }
                        manager.removeVariable(variable);
                        tableViewer.remove(variable);
                    }
                    manager.save();
                    tableViewer.setInput(manager.variables());
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });

        // Populate
        tableViewer.setInput(manager.variables());

        // Expand name and value columns to fill initial width of table each time table is resized
        tableViewer.getTable().addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(ControlEvent event) {
                int width = tableViewer.getTable().getSize().x / 2;
                nameColumn.getColumn().setWidth(width);
                valueColumn.getColumn().setWidth(width);
            }
        });
    }
}
