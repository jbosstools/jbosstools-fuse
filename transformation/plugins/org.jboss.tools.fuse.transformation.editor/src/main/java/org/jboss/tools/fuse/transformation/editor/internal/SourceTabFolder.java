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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.fuse.transformation.core.FieldMapping;
import org.jboss.tools.fuse.transformation.core.Variable;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationManager;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationManager.Event;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;
import org.jboss.tools.fuse.transformation.core.model.Model;

public class SourceTabFolder extends ModelTabFolder {

    private final CTabItem variablesTab;
    private final VariablesViewer variablesViewer;
    private PropertyChangeListener managerListener;

    public SourceTabFolder(TransformationManager manager,
                           Composite parent,
                           List<PotentialDropTarget> potentialDropTargets) {
        super(manager, parent, Messages.SourceTabFolder_Source, manager.rootSourceModel(), potentialDropTargets);

        // Create variables tab
        variablesTab = new CTabItem(this, SWT.NONE);
        variablesTab.setText(Messages.SourceTabFolder_Variables);
        variablesViewer = new VariablesViewer(manager, this);
        variablesTab.setControl(variablesViewer);
        variablesTab.setImage(Images.VARIABLE);
    }

    @Override
    ModelViewer constructModelViewer(List<PotentialDropTarget> potentialDropTargets,
                                     String preferenceId) {
        final ModelViewer viewer = super.constructModelViewer(potentialDropTargets, preferenceId);
        viewer.treeViewer.addDropSupport(DND.DROP_MOVE,
                                         new Transfer[] {LocalSelectionTransfer.getTransfer()},
                                         new ViewerDropAdapter(viewer.treeViewer) {

            @Override
            public boolean performDrop(Object data) {
                try {
                    FieldMapping mapping = manager.map((Model)getCurrentTarget(), (Model)Util.draggedObject());
                    if (mapping != null && Util.modelsNeedDateFormat(mapping.getSource(), mapping.getTarget(), true))
                        Util.updateDateFormat(getShell(), mapping);
                    manager.save();
                    return true;
                } catch (Exception e) {
                    Activator.error(e);
                    return false;
                }
            }

            @Override
            public boolean validateDrop(Object source,
                                        int operation,
                                        TransferData transferType) {
                return getCurrentLocation() == ViewerDropAdapter.LOCATION_ON
                                               && Util.draggingFromValidTarget(manager)
                                               && Util.validSourceAndTarget(source, Util.draggedObject(), manager);
            }
        });
        potentialDropTargets.add(new PotentialDropTarget(viewer.treeViewer.getTree()) {

            @Override
            public boolean valid() {
                return Util.draggingFromValidTarget(manager);
            }
        });
        managerListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                if (event.getPropertyName().equals(Event.MAPPING_SOURCE.name())) viewer.treeViewer.refresh();
                if (event.getPropertyName().equals(Event.VARIABLE.name())
                    || event.getPropertyName().equals(Event.VARIABLE_NAME.name())
                    || event.getPropertyName().equals(Event.VARIABLE_VALUE.name())) variablesViewer.tableViewer.refresh();
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

    @Override
    public void select(Object object) {
        super.select(object);
        if (object instanceof Variable) {
            setSelection(variablesTab);
            variablesViewer.tableViewer.setSelection(new StructuredSelection(object), true);
        }
    }
}
