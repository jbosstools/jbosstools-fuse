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
import org.jboss.tools.fuse.transformation.core.MappingOperation;
import org.jboss.tools.fuse.transformation.core.Variable;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationManager;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationManager.Event;
import org.jboss.tools.fuse.transformation.core.model.Model;

public final class TargetTabFolder extends ModelTabFolder {

    private PropertyChangeListener managerListener;

    public TargetTabFolder(final TransformationManager manager,
                           final Composite parent,
                           final List<PotentialDropTarget> potentialDropTargets) {
        super(manager, parent, Messages.TargetTabFolder_target, manager.rootTargetModel(), potentialDropTargets);
    }

    @Override
    protected ModelViewer constructModelViewer(final List<PotentialDropTarget> potentialDropTargets,
                                               String preferenceId) {
        final ModelViewer viewer = super.constructModelViewer(potentialDropTargets, preferenceId);
        viewer.treeViewer.addDropSupport(DND.DROP_MOVE,
                                         new Transfer[] {LocalSelectionTransfer.getTransfer()},
                                         new ViewerDropAdapter(viewer.treeViewer) {

            @Override
            public boolean performDrop(Object data) {
                try {
                    final Object source =
                        ((IStructuredSelection)LocalSelectionTransfer.getTransfer().getSelection()).getFirstElement();
                    MappingOperation<?, ?> mapping;
                    if (source instanceof Model) mapping = manager.map((Model)source, (Model)getCurrentTarget());
                    else mapping = manager.map((Variable)source, (Model)getCurrentTarget());
                    if (mapping != null && Util.modelsNeedDateFormat(mapping.getSource(), mapping.getTarget(), false))
                        Util.updateDateFormat(getShell(), mapping);
                    manager.save();
                    return true;
                } catch (Exception e) {
                    Activator.error(e);
                    return false;
                }
            }

            @Override
            public boolean validateDrop(Object target,
                                        int operation,
                                        TransferData transferType) {
                return getCurrentLocation() == ViewerDropAdapter.LOCATION_ON
                                               && Util.draggingFromValidSource(manager)
                                               && Util.validSourceAndTarget(Util.draggedObject(), target, manager);
            }
        });
        potentialDropTargets.add(new PotentialDropTarget(viewer.treeViewer.getTree()) {

            @Override
            public boolean valid() {
                return Util.draggingFromValidSource(manager);
            }
        });
        managerListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                if (event.getPropertyName().equals(Event.MAPPING_TARGET.name())) viewer.treeViewer.refresh();
            }
        };
        manager.addListener(managerListener);
        return viewer;
    }

    @Override
    public void dispose() {
        manager.removeListener(managerListener);
        super.dispose();
    }
}
