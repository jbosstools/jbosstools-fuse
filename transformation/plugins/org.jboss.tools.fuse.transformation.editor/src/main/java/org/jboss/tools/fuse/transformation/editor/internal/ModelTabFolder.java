/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.jboss.mapper.Variable;
import org.jboss.mapper.model.Model;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.TransformationEditor;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig;

/**
 *
 */
public class ModelTabFolder extends CTabFolder {

    Model model;
    ModelViewer modelViewer;

    /**
     * @param config
     * @param parent
     * @param title
     * @param model
     */
    public ModelTabFolder(final TransformationConfig config,
                          final Composite parent,
                          final String title,
                          final Model model) {
        super(parent, SWT.BORDER);

        this.model = model;

        setBackground(parent.getDisplay()
                            .getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

        final ToolBar toolBar = new ToolBar(this, SWT.RIGHT);
        setTopRight(toolBar);

        final CTabItem tab = new CTabItem(this, SWT.NONE);
        tab.setText(title + (model == null ? "" : ": " + model.getName()));
        modelViewer = new ModelViewer(config, this, model);
        tab.setControl(modelViewer);
        modelViewer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        modelViewer.layout();
        setSelection(tab);

        constructAdditionalTabs();

        modelViewer.treeViewer.addDropSupport(DND.DROP_MOVE,
                                              new Transfer[] {LocalSelectionTransfer.getTransfer()},
                                              new ViewerDropAdapter(modelViewer.treeViewer) {

            @Override
            public boolean performDrop(final Object data) {
                try {
                    Object source =
                        ((IStructuredSelection) LocalSelectionTransfer.getTransfer()
                                                                      .getSelection())
                                                                      .getFirstElement();
                    if (source instanceof Model)
                        config.mapField((Model) source, (Model) getCurrentTarget());
                    else config.mapVariable((Variable) source, (Model) getCurrentTarget());
                    config.save();
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

    /**
     * Does nothing. Overridden by {@link TransformationEditor}.
     */
    protected void constructAdditionalTabs() {}

    /**
     * @param object
     */
    public void select(final Object object) {
        if (object instanceof Model) modelViewer.select((Model)object);
    }
}
