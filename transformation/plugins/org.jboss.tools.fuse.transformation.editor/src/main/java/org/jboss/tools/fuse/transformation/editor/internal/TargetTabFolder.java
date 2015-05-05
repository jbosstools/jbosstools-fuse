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

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.fuse.transformation.Variable;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.model.Model;

public final class TargetTabFolder extends ModelTabFolder {

    final TransformationConfig config;

    public TargetTabFolder(final TransformationConfig config,
                           final Composite parent,
                           final List<PotentialDropTarget> potentialDropTargets) {
        super(config, parent, "Target", config.getTargetModel(), potentialDropTargets);
        this.config = config;
    }

    @Override
    protected ModelViewer constructModelViewer(final TransformationConfig config,
                                               final List<PotentialDropTarget> potentialDropTargets) {
        final ModelViewer modelViewer =
            new ModelViewer(config, this, config.getTargetModel(), potentialDropTargets);
        modelViewer.treeViewer.addDropSupport(DND.DROP_MOVE,
                                              new Transfer[] {LocalSelectionTransfer.getTransfer()},
                                              new ViewerDropAdapter(modelViewer.treeViewer) {

            @Override
            public boolean performDrop(final Object data) {
                try {
                    final Object source =
                        ((IStructuredSelection) LocalSelectionTransfer.getTransfer()
                                                                      .getSelection())
                                                                      .getFirstElement();
                    if (source instanceof Model) {
                        config.mapField((Model) source, (Model) getCurrentTarget());
                    } else {
                        config.mapVariable((Variable) source, (Model) getCurrentTarget());
                    }
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
                return getCurrentLocation() == ViewerDropAdapter.LOCATION_ON
                                               && Util.draggingFromValidSource(config)
                                               && Util.validSourceAndTarget(Util.draggedObject(),
                                                                            target,
                                                                            config);
            }
        });
        potentialDropTargets.add(new PotentialDropTarget(modelViewer.treeViewer.getTree()) {

            @Override
            public boolean valid() {
                return Util.draggingFromValidSource(config);
            }
        });
        return modelViewer;
    }
}
