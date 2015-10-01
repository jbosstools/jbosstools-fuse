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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.fuse.transformation.MappingOperation;
import org.jboss.tools.fuse.transformation.editor.internal.util.BaseDialog;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.model.Model;

class FieldDialog extends BaseDialog {

    final Model rootModel;
    final TransformationConfig config;
    final MappingOperation<?, ?> mapping;
    Model field;

    FieldDialog(Shell shell,
                Model rootModel,
                TransformationConfig config,
                MappingOperation<?, ?> mapping) {
        super(shell);
        this.rootModel = rootModel;
        this.config = config;
        this.mapping = mapping;
        if (mapping.getSource() instanceof Model) {
            this.field = rootModel.equals(config.getSourceModel())
                         ? (Model) mapping.getSource()
                         : (Model) mapping.getTarget();
        }
    }

    @Override
    protected void constructContents(final Composite parent) {
        parent.setLayout(GridLayoutFactory.swtDefaults().create());
        final ModelViewer modelViewer = new ModelViewer(config, parent, rootModel, null, null);
        modelViewer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        if (field != null) {
            modelViewer.select(field);
        }
        modelViewer.treeViewer.getTree().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                final IStructuredSelection selection =
                    (IStructuredSelection)modelViewer.treeViewer.getSelection();
                field = (Model)selection.getFirstElement();
                validate();
            }
        });
    }

    @Override
    protected String message() {
        return "Select a property.";
    }

    @Override
    protected String title() {
        return "Property";
    }

    void validate() {
        boolean enabled = field != null && !Util.type(field);
        if (enabled) {
            if (rootModel.equals(config.getSourceModel())) {
                enabled = Util.validSourceAndTarget(field, mapping.getTarget(), config);
            } else {
                enabled = Util.validSourceAndTarget(mapping.getSource(), field, config);
            }
        }
        setErrorMessage(enabled ? null : "Invalid property");
        getButton(IDialogConstants.OK_ID).setEnabled(enabled);
    }
}
