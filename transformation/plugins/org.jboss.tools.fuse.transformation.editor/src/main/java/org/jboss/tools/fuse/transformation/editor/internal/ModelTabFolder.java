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

import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig;
import org.jboss.tools.fuse.transformation.model.Model;

abstract class ModelTabFolder extends CTabFolder {

    final Model model;

    final ModelViewer modelViewer;

    /**
     * @param config
     * @param parent
     * @param title
     * @param model
     * @param potentialDropTargets
     */
    ModelTabFolder(final TransformationConfig config,
                   final Composite parent,
                   final String title,
                   final Model model,
                   final List<PotentialDropTarget> potentialDropTargets) {
        super(parent, SWT.BORDER);

        this.model = model;

        setBackground(parent.getDisplay()
                            .getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

        final ToolBar toolBar = new ToolBar(this, SWT.RIGHT);
        setTopRight(toolBar);

        final CTabItem tab = new CTabItem(this, SWT.NONE);
        tab.setText(title + (model == null ? "" : ": " + model.getName()));
        modelViewer = constructModelViewer(config, potentialDropTargets);
        tab.setControl(modelViewer);
        modelViewer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        modelViewer.layout();
        setSelection(tab);
    }

    ModelViewer constructModelViewer(final TransformationConfig config,
                                               final List<PotentialDropTarget> potentialDropTargets) {
        return new ModelViewer(config, this, model, potentialDropTargets);
    }

    /**
     * @param object
     */
    public void select(final Object object) {
        if (object instanceof Model) modelViewer.select((Model)object);
    }
}
