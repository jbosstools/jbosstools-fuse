/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.dialogs;

import java.util.Arrays;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.fusesource.ide.foundation.ui.util.Selections;

/**
 * @author lhein
 */
public class GlobalConfigElementsSelectionDialog extends SelectionDialog {

	// the root element to populate the viewer with
    private Object inputElement;

    // providers for populating this dialog
    private ILabelProvider labelProvider;

    private IStructuredContentProvider contentProvider;

    // the visual selection widget group
    private TableViewer listViewer;

    // sizing constants
    private final static int SIZING_SELECTION_WIDGET_HEIGHT = 250;

    private final static int SIZING_SELECTION_WIDGET_WIDTH = 300;
	
	/**
	 * @param parentShell
	 * @param input
	 * @param contentProvider
	 * @param labelProvider
	 * @param message
	 */
	public GlobalConfigElementsSelectionDialog(Shell parentShell, Object input,
			IStructuredContentProvider contentProvider, ILabelProvider labelProvider, String title, String message) {
		super(parentShell);
        setTitle(title);
        setMessage(message);
        inputElement = input;
        this.contentProvider = contentProvider;
        this.labelProvider = labelProvider;
	}
	
    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
	protected Control createDialogArea(Composite parent) {
        // page group
        Composite composite = (Composite) super.createDialogArea(parent);

        initializeDialogUnits(composite);

        createMessageArea(composite);

        listViewer = new TableViewer(composite, SWT.BORDER | SWT.SINGLE);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
        data.widthHint = SIZING_SELECTION_WIDGET_WIDTH;
        listViewer.getTable().setLayoutData(data);
        
        listViewer.getTable().setHeaderVisible(false);
        listViewer.getTable().setLinesVisible(false);

        listViewer.setLabelProvider(labelProvider);
        listViewer.setContentProvider(contentProvider);
        
        initializeViewer();

        Dialog.applyDialogFont(composite);

        return composite;
    }

    /**
     * Returns the viewer used to show the list.
     *
     * @return the viewer, or <code>null</code> if not yet created
     */
    protected TableViewer getViewer() {
        return listViewer;
    }

    /**
     * Initializes this dialog's viewer after it has been laid out.
     */
    private void initializeViewer() {
        listViewer.setInput(inputElement);
    }

    /**
     * The <code>ListSelectionDialog</code> implementation of this
     * <code>Dialog</code> method builds a list of the selected elements for later
     * retrieval by the client and closes this dialog.
     */
    @Override
	protected void okPressed() {
    	setResult(Arrays.asList(Selections.getFirstSelection(listViewer.getSelection())));
        super.okPressed();
    }
}
