/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.dialogs;

import java.util.Arrays;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.fusesource.ide.camel.editor.globalconfiguration.CamelGlobalConfigEditor;
import org.fusesource.ide.foundation.ui.util.Selections;

/**
 * @author lhein
 */
public class GlobalConfigElementsSelectionDialog extends SelectionDialog {

	// the root element to populate the viewer with
    private Object inputElement;

    // the parent editor
    private CamelGlobalConfigEditor parentEditor;
    
    // providers for populating this dialog
    private StyledCellLabelProvider labelProvider;

    private IStructuredContentProvider contentProvider;

    // the visual selection widget group
    private TreeViewer listViewer;

    // sizing constants
	private static final int SIZING_SELECTION_WIDGET_HEIGHT = 250;

	private static final int SIZING_SELECTION_WIDGET_WIDTH = 300;
	
	/**
	 * @param parentShell
	 * @param parentEditor
	 * @param input
	 * @param contentProvider
	 * @param labelProvider
	 * @param message
	 */
	public GlobalConfigElementsSelectionDialog(Shell parentShell, CamelGlobalConfigEditor parentEditor, Object input,
			IStructuredContentProvider contentProvider, StyledCellLabelProvider labelProvider, String title, String message) {
		super(parentShell);
		this.parentEditor = parentEditor;
        setTitle(title);
        setMessage(message);
        inputElement = input;
        this.contentProvider = contentProvider;
        this.labelProvider = labelProvider;
	}
	
    @Override
	protected Control createDialogArea(Composite parent) {
        // page group
        Composite composite = (Composite) super.createDialogArea(parent);

        initializeDialogUnits(composite);

        createMessageArea(composite);

        listViewer = new TreeViewer(composite, SWT.BORDER | SWT.SINGLE);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
        data.widthHint = SIZING_SELECTION_WIDGET_WIDTH;
        listViewer.getTree().setLayoutData(data);
		listViewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);

        listViewer.getTree().setHeaderVisible(false);
        listViewer.getTree().setLinesVisible(false);

        listViewer.setLabelProvider(labelProvider);
        listViewer.setContentProvider(contentProvider);
        
        listViewer.addFilter(new ViewerFilter() {	
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof GlobalConfigCategoryItem) {
					return !((GlobalConfigCategoryItem)element).getChildren().isEmpty();
				}
				return true;
			}
		});
        
        initializeViewer();

		listViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				final IStructuredSelection selection = (IStructuredSelection) listViewer.getSelection();
				if (selection.isEmpty() || !(selection.getFirstElement() instanceof GlobalConfigElementItem)) {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					setMessage("Please select a global type");
				} else {
					getButton(IDialogConstants.OK_ID).setEnabled(true);
					setMessage(null);
				}
			}
		});

		listViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (getButton(IDialogConstants.OK_ID).isEnabled()) {
					okPressed();
				}
			}
		});

        Dialog.applyDialogFont(composite);

        return composite;
    }

    /**
     * Returns the viewer used to show the list.
     *
     * @return the viewer, or <code>null</code> if not yet created
     */
    protected TreeViewer getViewer() {
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
