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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;
import org.jboss.mapper.Literal;
import org.jboss.mapper.eclipse.Activator;
import org.jboss.mapper.eclipse.TransformationEditor;
import org.jboss.mapper.eclipse.internal.util.Util;
import org.jboss.mapper.model.Model;

/**
 *
 */
public class ModelTabFolder extends CTabFolder {

    Model model;
    ModelViewer modelViewer;

    /**
     * @param editor
     * @param parent
     * @param title
     * @param model
     */
    public ModelTabFolder(final TransformationEditor editor,
            final Composite parent,
            final String title,
            final Model model) {
        super(parent, SWT.BORDER);

        this.model = model;

        setBackground(parent.getDisplay().getSystemColor(
                SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

        final ToolBar toolBar = new ToolBar(this, SWT.RIGHT);
        setTopRight(toolBar);
        final ToolItem changeButton = new ToolItem(toolBar, SWT.NONE);
        changeButton.setImage(Util.Images.CHANGE);
        changeButton.setToolTipText("Change transformation " + title.toLowerCase());

        final CTabItem tab = new CTabItem(this, SWT.NONE);
        tab.setText(title + (model == null ? "" : ": " + model.getName()));
        modelViewer = new ModelViewer(editor, this, model);
        tab.setControl(modelViewer);
        modelViewer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        modelViewer.layout();
        setSelection(tab);

        changeButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                changeModel(editor, title, tab);
            }
        });
    }

    void changeModel(final TransformationEditor editor,
            final String title,
            final CTabItem tab) {
        final IFolder classesFolder = editor.project().getFolder("target/classes");
        final List<IResource> classes = new ArrayList<>();
        try {
            findClasses(classesFolder, classes);
            final ResourceListSelectionDialog dlg =
                    new ResourceListSelectionDialog(getShell(),
                            classes.toArray(new IResource[classes.size()])) {

                        @Override
                        protected Control createDialogArea(final Composite parent) {
                            final Composite dlgArea = (Composite) super.createDialogArea(parent);
                            for (final Control child : dlgArea.getChildren()) {
                                if (child instanceof Text) {
                                    ((Text) child).setText(model == null ? "*" : model.getName());
                                    break;
                                }
                            }
                            return dlgArea;
                        }
                    };
            dlg.setTitle("Select " + title);
            if (dlg.open() != Window.OK) {
                return;
            }

            final IFile file = (IFile) dlg.getResult()[0];
            String name =
                    file.getFullPath().makeRelativeTo(classesFolder.getFullPath()).toString()
                            .replace('/', '.');
            name = name.substring(0, name.length() - ".class".length());

            model = editor.changeModel(model, name);
            tab.setText(title + ": " + model.getName());
            modelViewer.rootModel = model;
            refresh();
            layout();
        } catch (final Exception e) {
            Activator.error(e);
        }
    }

    /**
     * @param listener
     */
    public void configureDropSupport(final DropListener listener) {
        modelViewer.treeViewer.addDropSupport(DND.DROP_MOVE,
                new Transfer[] {LocalSelectionTransfer.getTransfer()},
                new ViewerDropAdapter(modelViewer.treeViewer) {

                    @Override
                    public boolean performDrop(final Object data) {
                        try {
                            listener.drop(((IStructuredSelection) LocalSelectionTransfer
                                    .getTransfer()
                                    .getSelection()).getFirstElement(),
                                    (Model) getCurrentTarget());
                            return true;
                        } catch (final Exception e) {
                            Activator.error(e);
                            return false;
                        }
                    }

                    @Override
                    public boolean validateDrop(final Object target,
                            final int operation,
                            final TransferData transferType) {
                        return getCurrentLocation() == ViewerDropAdapter.LOCATION_ON;
                    }
                });
    }

    private void expand(final Model model) {
        if (model == null) {
            return;
        }
        expand(model.getParent());
        modelViewer.treeViewer.expandToLevel(model, 0);
    }

    private void findClasses(final IFolder folder,
            final List<IResource> classes) throws CoreException {
        for (final IResource resource : folder.members()) {
            if (resource instanceof IFolder) {
                findClasses((IFolder) resource, classes);
            } else if (resource.getName().endsWith(".class")) {
                classes.add(resource);
            }
        }
    }

    /**
     *
     */
    public void refresh() {
        modelViewer.treeViewer.refresh();
    }

    /**
     * @param object
     */
    public void select(final Object object) {
        if (object instanceof Literal)
            return;
        expand(((Model) object).getParent());
        modelViewer.treeViewer.setSelection(new StructuredSelection(object), true);
    }

    /**
     *
     */
    public static interface DropListener {

        /**
         * @param object
         * @param targetModel
         * @throws Exception
         */
        void drop(Object object,
                Model targetModel) throws Exception;
    }
}
