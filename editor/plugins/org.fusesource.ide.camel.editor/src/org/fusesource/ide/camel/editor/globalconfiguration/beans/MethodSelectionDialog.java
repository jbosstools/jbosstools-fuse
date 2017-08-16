/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.globalconfiguration.beans;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.fusesource.ide.camel.editor.internal.UIMessages;

/**
 * @author brianf
 *
 *    Basic method selection dialog. 
 *    Pass in array of elements (i.e. list of applicable methods)
 *    Return a selected method      
 */
public class MethodSelectionDialog extends TitleAreaDialog {

	private ILabelProvider labelProvider;
	private Object[] elementArray;
	private TreeViewer treeViewer;
	private String message = ""; //$NON-NLS-1$
	private String title = ""; //$NON-NLS-1$

	public MethodSelectionDialog(Shell parent, ILabelProvider renderer) {
		super(parent);
		labelProvider = renderer;
	}

	public MethodSelectionDialog(Shell parent) {
		this(parent, new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		super.setTitle(title);
		super.setMessage(message);

		Composite area = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout(1, false);
		area.setLayout(gridLayout);
		area.setLayoutData(new GridData(GridData.FILL_BOTH));

		// list of elements
		final FilteredTree elementTable = new FilteredTree(area, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER,
				new LocalPatternFilter(), true);
		elementTable.getFilterControl().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // ignore
            }

            @Override
            public void focusLost(FocusEvent e) {
                getButton(IDialogConstants.OK_ID).setEnabled(validate());
            }
        });
		elementTable.getFilterControl().addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                // ignore
            }

            @Override
            public void keyReleased(KeyEvent e) {
                getButton(IDialogConstants.OK_ID).setEnabled(validate());
            }
        });
		
		treeViewer = elementTable.getViewer();
		treeViewer.addPostSelectionChangedListener(input -> 
			getButton(IDialogConstants.OK_ID).setEnabled(validate())
		);
		treeViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.setContentProvider(new MethodTreeContentProvider());
		treeViewer.setLabelProvider(labelProvider);
		treeViewer.setInput(elementArray);

		if (elementArray == null || elementArray.length == 0) {
			super.setMessage(UIMessages.beanConfigUtilNoMethodsAvailable,  IMessageProvider.ERROR);
			treeViewer.getTree().setEnabled(false);
			elementTable.getFilterControl().setEnabled(false);
		}

		return area;
	}

	/**
	 * Set the list of available elements to choose from.
	 * @param elements
	 */
	public void setElements(Object[] elements) {
		elementArray = elements;
	}

	/**
	 * Returns an array of the currently selected elements. To be called within or
	 * after open().
	 * 
	 * @return returns an array of the currently selected elements.
	 */
	protected Object[] getSelectedElements() {
		Assert.isNotNull(treeViewer);
		IStructuredSelection ssel = treeViewer.getStructuredSelection();
		return ssel.toArray();
	}

	/**
	 * Returns the first element from the list of results. Returns <code>null</code>
	 * if no element has been selected.
	 *
	 * @return object or null
	 */
	public Object getFirstResult() {
		Object[] result = getSelectedElements();
		if (result == null || result.length == 0) {
			return null;
		}
		return result[0];
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control rtnControl = super.createButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(validate());
		setErrorMessage(null);
		return rtnControl;
	}

	/**
	 * Validate 
	 * @return boolean
	 */
	protected boolean validate() {
		setErrorMessage(null);
		if (elementArray == null || elementArray.length == 0) {
			setErrorMessage(UIMessages.beanConfigUtilNoMethodsAvailable);
		} else if (getFirstResult() == null) {
			setErrorMessage(UIMessages.methodSelectionDialogNoMethodSelectedError);
		}
		return (getErrorMessage() == null);
	}

	/*
	 * Used by the FilteredTree for matching on element labels.
	 * 
	 * @author brianf
	 *
	 */
	class LocalPatternFilter extends PatternFilter {
		@Override
		protected boolean isLeafMatch(Viewer viewer, Object element) {
			String labelText = ((ILabelProvider) ((StructuredViewer) viewer).getLabelProvider()).getText(element);
			if (labelText == null) {
				return false;
			}
			return wordMatches(labelText);
		}
	}

	/*
	 * Very simple tree content viewer for the FilteredTree
	 * 
	 * @author brianf
	 *
	 */
	class MethodTreeContentProvider implements ITreeContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			return elementArray;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return new Object[0];
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return false;
		}

	}
}
