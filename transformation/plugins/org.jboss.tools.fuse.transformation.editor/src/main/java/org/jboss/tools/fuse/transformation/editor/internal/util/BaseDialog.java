/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.util;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 *
 */
public abstract class BaseDialog extends TitleAreaDialog {

    /**
     * @param shell
     */
    public BaseDialog(final Shell shell) {
        super(shell);
    }

    /**
     * @param parent
     */
    protected abstract void constructContents(final Composite parent);

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.dialogs.Dialog#create()
     */
    @Override
    public void create() {
        super.create();
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected final Control createDialogArea(final Composite parent) {
        setTitle(title());
        setMessage(message());
        setHelpAvailable(false);
        final Composite area = new Composite(parent, SWT.BORDER);
        area.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        constructContents(area);
        return area;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.window.Window#getShellStyle()
     */
    @Override
    protected final int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE;
    }

    /**
     * @return this dialog's message
     */
    protected abstract String message();

    /**
     * @return this dialog's title
     */
    protected abstract String title();
}
