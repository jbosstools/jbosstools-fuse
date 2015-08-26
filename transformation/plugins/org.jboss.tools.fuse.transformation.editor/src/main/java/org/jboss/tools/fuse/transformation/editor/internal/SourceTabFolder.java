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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.fuse.transformation.Variable;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;

public final class SourceTabFolder extends ModelTabFolder {

    private final CTabItem variablesTab;
    private final VariablesViewer variablesViewer;

    public SourceTabFolder(final TransformationConfig config,
                           final Composite parent,
                           final List<PotentialDropTarget> potentialDropTargets) {
        super(config, parent, "Source", config.getSourceModel(), potentialDropTargets);

        // Create variables tab
        variablesTab = new CTabItem(this, SWT.NONE);
        variablesTab.setText("Variables");
        variablesViewer = new VariablesViewer(config, this);
        variablesTab.setControl(variablesViewer);
        variablesTab.setImage(Images.VARIABLE);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.editor.internal.ModelTabFolder#constructModelViewer(org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig, java.util.List)
     */
    @Override
    ModelViewer constructModelViewer(TransformationConfig config,
            List<PotentialDropTarget> potentialDropTargets) {
        final ModelViewer viewer = super.constructModelViewer(config, potentialDropTargets);
        config.addListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                if (event.getPropertyName().equals(TransformationConfig.MAPPING_TARGET)) {
                    if (!viewer.treeViewer.getControl().isDisposed()) {
                        viewer.treeViewer.refresh();
                    }
                }
            }
        });
        return viewer;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.editor.internal.ModelTabFolder#select(java.lang.Object)
     */
    @Override
    public void select(Object object) {
        super.select(object);
        if (object instanceof Variable) {
            setSelection(variablesTab);
            variablesViewer.tableViewer.setSelection(new StructuredSelection(object), true);
        }
    }
}
