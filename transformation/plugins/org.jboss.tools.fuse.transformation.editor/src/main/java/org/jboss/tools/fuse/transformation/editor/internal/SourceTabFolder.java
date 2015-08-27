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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
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
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;
import org.jboss.tools.fuse.transformation.model.Model;

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
    ModelViewer constructModelViewer(final TransformationConfig config,
                                     List<PotentialDropTarget> potentialDropTargets,
                                     String preferenceId) {
        final ModelViewer viewer =
            super.constructModelViewer(config, potentialDropTargets, preferenceId);
        viewer.treeViewer.addDropSupport(DND.DROP_MOVE,
                                         new Transfer[] {LocalSelectionTransfer.getTransfer()},
                                         new ViewerDropAdapter(viewer.treeViewer) {

            @Override
            public boolean performDrop(final Object data) {
                try {
                    final Object target =
                        ((IStructuredSelection) LocalSelectionTransfer.getTransfer()
                                                                      .getSelection())
                                                                      .getFirstElement();
                    FieldMapping mapping = null;
                    final Model sourceModel = (Model)getCurrentTarget();
                    final Model targetModel = (Model)target;
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

                    if (mapping != null
                        && Util.modelsNeedDateFormat(mapping.getSource(),
                                                     mapping.getTarget(),
                                                     true)) {
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
            public boolean validateDrop(final Object source,
                                        final int operation,
                                        final TransferData transferType) {
                return getCurrentLocation() == ViewerDropAdapter.LOCATION_ON
                                               && Util.draggingFromValidTarget(config)
                                               && Util.validSourceAndTarget(source,
                                                                            Util.draggedObject(),
                                                                            config);
            }
        });
        potentialDropTargets.add(new PotentialDropTarget(viewer.treeViewer.getTree()) {

            @Override
            public boolean valid() {
                return Util.draggingFromValidTarget(config);
            }
        });
        config.addListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                if (event.getPropertyName().equals(TransformationConfig.MAPPING_SOURCE)) {
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
