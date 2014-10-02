/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.commons.camel.tools.BeanDef;
import org.fusesource.ide.commons.ui.Selections;

/**
 * @author lhein
 *
 */
public class GlobalConfigEditor extends EditorPart {

	private RiderEditor parentEditor;
	private IEditorSite editorSite;
	private IEditorInput editorInput;

	private IProject container;
	@SuppressWarnings("unused")
	private IFile camelContextFile;
	@SuppressWarnings("unused")
	private RouteContainer model;
	private RouteSupport activeRoute;

	private Composite parent;
	private TreeViewer treeViewer;
	private Button btnAdd;
	private Button btnModify;
	private Button btnDelete;

	/**
	 * 
	 * @param parentEditor
	 */
	public GlobalConfigEditor(RiderEditor parentEditor) {
		this.parentEditor = parentEditor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor arg0) {
		this.parentEditor.doSave(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		this.parentEditor.doSaveAs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
	 * org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		setSite(editorSite);
		setInput(input);
		this.editorSite = editorSite;
		this.editorInput = input;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite p) {
		this.parent = new Composite(p, SWT.FLAT);

		GridLayout gl = new GridLayout(2, false);
		gl.horizontalSpacing = 10;

		this.parent.setLayout(gl);

		// now create the controls

		this.treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.SINGLE
				| SWT.H_SCROLL | SWT.V_SCROLL);
		this.treeViewer.setContentProvider(new GlobalConfigContentProvider());
		this.treeViewer.setLabelProvider(new GlobalConfigLabelProvider());
		this.treeViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 10));
		this.treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object selObj = Selections.getFirstSelection(event.getSelection());
				if (selObj != null && selObj instanceof BeanDef) {
					btnModify.setEnabled(true);
					btnDelete.setEnabled(true);
				} else {
					btnModify.setEnabled(false);
					btnDelete.setEnabled(false);
				}
			}
		});
		
		this.btnAdd = new Button(parent, SWT.BORDER | SWT.PUSH);
		this.btnAdd.setText("Add");
		GridData gd = new GridData(GridData.FILL, GridData.BEGINNING, false,
				false, 1, 1);
		gd.minimumWidth = 120;
		gd.widthHint = 120;
		this.btnAdd.setLayoutData(gd);
		this.btnAdd.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				createNewEntry();
			}
		});

		this.btnModify = new Button(parent, SWT.BORDER | SWT.PUSH);
		this.btnModify.setText("Edit");
		this.btnModify.setLayoutData(new GridData(GridData.FILL,
				GridData.BEGINNING, false, false, 1, 1));
		this.btnModify.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				modifyEntry();
			}
		});

		this.btnDelete = new Button(parent, SWT.BORDER | SWT.PUSH);
		this.btnDelete.setText("Remove");
		this.btnDelete.setLayoutData(new GridData(GridData.FILL,
				GridData.BEGINNING, false, false, 1, 1));
		this.btnDelete.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteEntries();
			}
		});

		this.treeViewer.setInput(this.parentEditor.getModel());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		this.treeViewer.getControl().setFocus();
	}
	
	public void reload() {
		this.treeViewer.setInput(this.parentEditor.getModel());
		this.treeViewer.refresh();
	}

	/**
	 * creates a new entry in the treeviewer
	 */
	private void createNewEntry() {
		// TODO: code me
		System.err.println("TODO!");
	}

	/**
	 * modifies the selected entry
	 */
	private void modifyEntry() {
		// TODO: code me
		System.err.println("TODO!");
	}

	/**
	 * deletes the selected entries
	 */
	private void deleteEntries() {
		if (this.treeViewer.getSelection().isEmpty() == false) {
			BeanDef def = (BeanDef)Selections.getFirstSelection(this.treeViewer.getSelection());
			this.parentEditor.getModel().getBeans().remove(def.getId());
			parentEditor.markDirty();
			reload();
		}
	}

	class GlobalConfigContentProvider implements ITreeContentProvider {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang
		 * .Object)
		 */
		@Override
		public Object[] getChildren(Object arg0) {
			return getElements(arg0);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang
		 * .Object)
		 */
		@Override
		public Object[] getElements(Object arg0) {
			if (arg0 instanceof RouteContainer) {
				return ((RouteContainer)arg0).getBeans().values().toArray();
			}
			return new Object[0];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang
		 * .Object)
		 */
		@Override
		public Object getParent(Object arg0) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang
		 * .Object)
		 */
		@Override
		public boolean hasChildren(Object arg0) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
		 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
			// TODO Auto-generated method stub

		}
	}

	class GlobalConfigLabelProvider extends StyledCellLabelProvider {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse
		 * .jface.viewers.ViewerCell)
		 */
		@Override
		public void update(ViewerCell cell) {
			Object element = cell.getElement();
			StyledString text = new StyledString();
			BeanDef bean = (BeanDef) element;			
			text.append(bean.getId());
			cell.setImage(getIconForBean(bean));
			text.append(" (" + bean.getBeanType() + ") ", StyledString.COUNTER_STYLER);
			cell.setText(text.toString());
			cell.setStyleRanges(text.getStyleRanges());
			super.update(cell);
		}
		
		private Image getIconForBean(BeanDef bean) {
			return null;
		}
	}
}
