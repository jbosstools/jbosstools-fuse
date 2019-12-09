/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
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
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.ICamelModelListener;
import org.fusesource.ide.foundation.ui.util.Widgets;

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
		viewer.setInput(computeInput());
		viewer.expandAll();
	}

	private AbstractCamelModelElement[] computeInput() {
		AbstractCamelModelElement selectedContainer = designEditor.getSelectedContainer();
		if (selectedContainer == null){
			selectedContainer = designEditor.getModel().getRouteContainer();
		}
		return getModelRoots(selectedContainer);
	}
	
	/**
	 * sets the selection in the outline view to the given element
	 * 
	 * @param cme
	 */
	public void setOutlineSelection(AbstractCamelModelElement cme) {
		if (cme == null || cme.getId() == null || Widgets.isDisposed(getTreeViewer()))
			return;
		if (getTreeViewer() != null){
			getTreeViewer().setSelection(new StructuredSelection(cme), true);
		}
		if (getTreeViewer() != null && getTreeViewer().getSelection().isEmpty()) {
			getTreeViewer().expandAll();
			TreeItem ti = findTreeItemForElement(cme, getTreeViewer().getTree().getItems());
			if (ti != null) {
				getTreeViewer().setSelection(new StructuredSelection(ti.getData()), true);
			}
		}
	}

	private TreeItem findTreeItemForElement(AbstractCamelModelElement cme, TreeItem[] treeItems) {
		TreeItem tRes = null;
		for (TreeItem ti : treeItems) {
			if (tRes != null) break;
			Object o = ti.getData();
			if (o instanceof AbstractCamelModelElement) {
				AbstractCamelModelElement cme2 = (AbstractCamelModelElement)o;
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
	public void changeInput(AbstractCamelModelElement container) {
		if (getTreeViewer() != null && getTreeViewer().getTree().isDisposed() == false) {
			getTreeViewer().setInput(getModelRoots(container));
			modelChanged();
		}
	}
	
	private AbstractCamelModelElement[] getModelRoots(AbstractCamelModelElement selectedContainer) {
		AbstractCamelModelElement[] container;
		if (selectedContainer instanceof CamelFile) {
			container = selectedContainer.getChildElements().get(0).getChildElements().toArray(new AbstractCamelModelElement[selectedContainer.getChildElements().size()]);
		} else if (selectedContainer instanceof CamelContextElement) {
			container = selectedContainer.getChildElements().toArray(new AbstractCamelModelElement[selectedContainer.getChildElements().size()]);
		} else if(selectedContainer == null){
			container = new AbstractCamelModelElement[]{};
		} else {
			container = new AbstractCamelModelElement[] { selectedContainer };
		}
		return container;
	}
}
