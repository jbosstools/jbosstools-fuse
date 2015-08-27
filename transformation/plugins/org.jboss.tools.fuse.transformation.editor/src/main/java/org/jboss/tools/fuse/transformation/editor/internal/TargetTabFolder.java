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

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.fuse.transformation.FieldMapping;
import org.jboss.tools.fuse.transformation.Variable;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.util.CanceledDialogException;
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
                                               final List<PotentialDropTarget> potentialDropTargets,
                                               String preferenceId) {
        final ModelViewer viewer =
            super.constructModelViewer(config, potentialDropTargets, preferenceId);
        viewer.treeViewer.addDropSupport(DND.DROP_MOVE,
                                         new Transfer[] {LocalSelectionTransfer.getTransfer()},
                                         new ViewerDropAdapter(viewer.treeViewer) {

            @Override
            public boolean performDrop(final Object data) {
                try {
                    final Object source =
                        ((IStructuredSelection) LocalSelectionTransfer.getTransfer()
                                                                      .getSelection())
                                                                      .getFirstElement();
                    FieldMapping mapping = null;
                    if (source instanceof Model) {
                        final Model sourceModel = (Model)source;
                        final Model targetModel = (Model)getCurrentTarget();
                        final boolean sourceIsOrInCollection = Util.isOrInCollection(sourceModel);
                        final boolean targetIsOrInCollection = Util.isOrInCollection(targetModel);

                        if (sourceIsOrInCollection == targetIsOrInCollection) {
                            mapping = config.mapField(sourceModel, targetModel, null, null);
                        } else try {
                            final List<Integer> sourceIndexes = sourceIsOrInCollection
                                                                ? Util.indexes(getShell(),
                                                                               sourceModel, true)
                                                                : null;
                            final List<Integer> targetIndexes = targetIsOrInCollection
                                                                ? Util.indexes(getShell(),
                                                                               targetModel, false)
                                                                : null;
                            mapping =
                                config.mapField(sourceModel, targetModel, sourceIndexes, targetIndexes);
                        } catch (CanceledDialogException e) {
                            return false;
                        }
                    } else {
                        final Variable variable = (Variable)source;
                        final Model targetModel = (Model)getCurrentTarget();
                        final List<Integer> indexes =
                                Util.isOrInCollection(targetModel)
                                ? Util.indexes(getShell(), targetModel, false)
                                : null;
                        config.mapVariable(variable, targetModel, indexes);
                    }

                    if (mapping != null
                        && Util.modelsNeedDateFormat(mapping.getSource(),
                                                     mapping.getTarget(),
                                                     false)) {
                        Util.updateDateFormat(getShell(), mapping);
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
        potentialDropTargets.add(new PotentialDropTarget(viewer.treeViewer.getTree()) {

            @Override
            public boolean valid() {
                return Util.draggingFromValidSource(config);
            }
        });
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
}
