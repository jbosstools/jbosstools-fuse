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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;

public final class SourceTabFolder extends ModelTabFolder {

    private final VariablesViewer variablesViewer;

    public SourceTabFolder(final TransformationConfig config,
                           final Composite parent,
                           final List<PotentialDropTarget> potentialDropTargets) {
        super(config, parent, "Source", config.getSourceModel(), potentialDropTargets);

        // Create variables tab
        final CTabItem variablesTab = new CTabItem(this, SWT.NONE);
        variablesTab.setText("Variables");
        variablesViewer = new VariablesViewer(config, this);
        variablesTab.setControl(variablesViewer);
        variablesTab.setImage(Images.VARIABLE);
    }
}
