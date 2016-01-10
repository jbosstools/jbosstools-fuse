/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.outline;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.ICamelModelListener;

/**
 * @author lhein
 */
public class CamelModelOutlinePage extends ContentOutlinePage implements ICamelModelListener {
	
	private CamelDesignEditor designEditor;
	
	/**
	 * creates an outline page 
	 * 
	 * @param designEditor
	 */
	public CamelModelOutlinePage(CamelDesignEditor designEditor) {
		this.designEditor = designEditor;
		this.designEditor.getModel().addModelListener(this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	@Override
	public void dispose() {
		this.designEditor.getModel().removeModelListener(this);
		super.dispose();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		TreeViewer viewer= getTreeViewer();
		viewer.setContentProvider(new CamelModelOutlineContentProvider());
		viewer.setLabelProvider(new CamelModelOutlineLabelProvider());
		viewer.addSelectionChangedListener(this);
		CamelModelElement[] container = new CamelModelElement[] { this.designEditor.getSelectedContainer() != null ? this.designEditor.getSelectedContainer() : this.designEditor.getModel().getCamelContext() };
		viewer.setInput(container);
		viewer.expandAll();
	}
	
	/**
	 * sets the selection in the outline view to the given element
	 * 
	 * @param cme
	 */
	public void setOutlineSelection(CamelModelElement cme) {
		if (cme == null || cme.getId() == null || getTreeViewer() == null || getTreeViewer().getTree().isDisposed()) return;
		if (getTreeViewer() != null) getTreeViewer().setSelection(new StructuredSelection(cme), true);
		if (getTreeViewer() != null && getTreeViewer().getSelection().isEmpty()) {
			getTreeViewer().expandAll();
			TreeItem ti = findTreeItemForElement(cme, getTreeViewer().getTree().getItems());
			if (ti != null) {
				getTreeViewer().setSelection(new StructuredSelection(ti.getData()), true);
			}
		}
	}

	private TreeItem findTreeItemForElement(CamelModelElement cme, TreeItem[] treeItems) {
		TreeItem tRes = null;
		for (TreeItem ti : treeItems) {
			if (tRes != null) break;
			Object o = ti.getData();
			if (o instanceof CamelModelElement) {
				CamelModelElement cme2 = (CamelModelElement)o;
				if (cme.getId().equals(cme2.getId())) {
					tRes = ti;
					break;
				} else {
					tRes = findTreeItemForElement(cme, ti.getItems());
				}
			}
		}				
		return tRes;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.ICamelModelListener#modelChanged()
	 */
	@Override
	public void modelChanged() {
		if (getTreeViewer() != null && getTreeViewer().getTree().isDisposed() == false) {
			getTreeViewer().refresh(true);
			getTreeViewer().expandAll();
		}
	}
	
	/** 
	 * should be called when the diagrams base node has been switched
	 * 
	 * @param container
	 */
	public void changeInput(CamelModelElement container) {
		if (getTreeViewer() != null && getTreeViewer().getTree().isDisposed() == false) {
			getTreeViewer().setInput(new CamelModelElement[] { container });
			modelChanged();
		}
	}
}
