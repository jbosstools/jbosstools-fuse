/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal;

import java.util.List;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationManager;
import org.jboss.tools.fuse.transformation.core.model.Model;

abstract class ModelTabFolder extends CTabFolder {

    final TransformationManager manager;
    private final Model model;
    private final CTabItem modelTab;
    private final ModelViewer modelViewer;

    /**
     * @param manager
     * @param parent
     * @param title
     * @param model
     * @param potentialDropTargets
     */
    ModelTabFolder(final TransformationManager manager,
                   final Composite parent,
                   final String title,
                   final Model model,
                   final List<PotentialDropTarget> potentialDropTargets) {
        super(parent, SWT.BORDER);
        this.manager = manager;
        this.model = model;

        setBackground(parent.getDisplay()
                            .getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

        final ToolBar toolBar = new ToolBar(this, SWT.RIGHT);
        setTopRight(toolBar);

        modelTab = new CTabItem(this, SWT.NONE);
        modelTab.setText(title + (model == null ? "" : ": " + model.getName())); //$NON-NLS-1$ //$NON-NLS-2$
        modelViewer = constructModelViewer(potentialDropTargets, title);
        modelTab.setControl(modelViewer);
        modelViewer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        modelViewer.layout();
        setSelection(modelTab);
    }

    ModelViewer constructModelViewer(List<PotentialDropTarget> potentialDropTargets,
                                     String preferenceId) {
        return new ModelViewer(manager, this, model, potentialDropTargets, preferenceId);
    }

    /**
     * @param object
     */
    public void select(final Object object) {
        if (object instanceof Model) {
            setSelection(modelTab);
            modelViewer.select((Model)object);
        }
    }
}
