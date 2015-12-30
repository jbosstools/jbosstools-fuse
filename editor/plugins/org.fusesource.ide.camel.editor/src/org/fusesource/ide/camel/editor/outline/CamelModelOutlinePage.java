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
		viewer.setInput(this.designEditor.getModel().getCamelContext());
	}
	
	/**
	 * sets the selection in the outline view to the given element
	 * 
	 * @param cme
	 */
	public void setOutlineSelection(CamelModelElement cme) {
		if (getTreeViewer() != null) getTreeViewer().setSelection(new StructuredSelection(cme), true);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.ICamelModelListener#modelChanged()
	 */
	@Override
	public void modelChanged() {
		if (getTreeViewer() != null) getTreeViewer().refresh(true);
	}
}
