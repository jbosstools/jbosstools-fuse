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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.fuse.transformation.core.MappingOperation;
import org.jboss.tools.fuse.transformation.core.MappingType;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.internal.util.BaseDialog;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationManager;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.core.model.Model;

class PropertyDialog extends BaseDialog {

    final Model rootModel;
    final TransformationManager manager;
    final MappingOperation<?, ?> mapping;
    Model property;

    PropertyDialog(Shell shell,
                   Model rootModel,
                   TransformationManager manager,
                   MappingOperation<?, ?> mapping) {
        super(shell);
        this.rootModel = rootModel;
        this.manager = manager;
        this.mapping = mapping;
        property = manager.source(rootModel) ? (Model)mapping.getSource() : (Model)mapping.getTarget();
    }

    @Override
    protected void constructContents(final Composite parent) {
        parent.setLayout(GridLayoutFactory.swtDefaults().create());
        final ModelViewer modelViewer = new ModelViewer(manager, parent, rootModel, null, null) {

            @Override
            boolean eligible(Model model) {
                return valid(model);
            }
        };
        modelViewer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        if (property != null) {
            modelViewer.select(property);
        }
        modelViewer.treeViewer.getTree().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                final IStructuredSelection selection =
                    (IStructuredSelection)modelViewer.treeViewer.getSelection();
                property = (Model)selection.getFirstElement();
                validate();
            }
        });
    }

    @Override
    protected String message() {
        return Messages.PropertyDialog_message;
    }

    @Override
    protected String title() {
        return Messages.PropertyDialog_title;
    }

    private boolean valid(Model model) {
        boolean source = manager.source(rootModel);
        if (source) {
            if (!Util.validSourceAndTarget(model, mapping.getTarget(), manager)) return false;
        } else if (!Util.validSourceAndTarget(mapping.getSource(), model, manager)) return false;
        // Ensure property types are the same if old property is in transformation mapping
        if (source && mapping.getType() == MappingType.TRANSFORMATION)
            return model.getType().equals(((Model)mapping.getSource()).getType());
        return true;
    }

    private void validate() {
        boolean enabled = property != null && valid(property);
        setErrorMessage(enabled ? null : Messages.PropertyDialog_validationErrorMessage);
        getButton(IDialogConstants.OK_ID).setEnabled(enabled);
    }
}
