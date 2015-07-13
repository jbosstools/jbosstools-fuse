/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.jboss.tools.fuse.transformation.model.Model;

class IndexesDialog extends BaseDialog {

    final Model model;
    final boolean sourceModel;
    final List<Integer> indexes = new ArrayList<>();

    IndexesDialog(final Shell shell,
                  final Model model,
                  final boolean sourceModel) {
        super(shell);
        this.model = model;
        this.sourceModel = sourceModel;
    }

    @Override
    protected void constructContents(Composite parent) {
        parent.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        constructRow(parent, model);
    }

    @SuppressWarnings("unused")
    private int constructRow(final Composite parent,
                             final Model model) {
        if (model == null) return -1;
        final int level = constructRow(parent, model.getParent());
        if (level >= 0) indexes.add(model.isCollection() ? 0 : null);
        final Label nameLabel = new Label(parent, SWT.NONE);
        final StringBuilder builder = new StringBuilder();
        for (int ndx = level + 1; --ndx >= 0;) {
            builder.append("    ");
        }
        builder.append(model.getName());
        nameLabel.setText(builder.toString());
        if (model.isCollection()) {
            final Spinner spinner = new Spinner(parent, SWT.BORDER);
            spinner.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(final ModifyEvent event) {
                    indexes.set(level, spinner.getSelection());
                }
            });
        } else {
            new Label(parent, SWT.NONE);
        }
        return level + 1;
    }

    @Override
    public void create() {
        super.create();
        getButton(IDialogConstants.OK_ID).setEnabled(true);
    }

    @Override
    protected String message() {
        return "Specify the index(es) for the collection(s) containing the "
               + (sourceModel ? "source" : "target") + " attribute.";
    }

    @Override
    protected String title() {
        return "Collection Indexes";
    }
}
