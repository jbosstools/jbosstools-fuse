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

package org.fusesource.ide.camel.editor.behaviours;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.fusesource.ide.camel.editor.CamelDesignEditor;

/**
 * @author lhein
 */
public class CamelUpdateBehaviour  extends DefaultUpdateBehavior {

	@SuppressWarnings("unused")
	private TransactionalEditingDomain editingDomain;
	
	/**
	 * @param riderDesignEditor
	 */
	public CamelUpdateBehaviour(CamelDesignEditor camelDesignEditor) {
		super(camelDesignEditor.getDiagramBehavior());
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior#createEditingDomain()
//	 */
//	@Override
//	protected void createEditingDomain() {
//		this.editingDomain = GraphitiUi.getEmfService().createResourceSetAndEditingDomain(); 
//		initializeEditingDomain(this.editingDomain);
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior#getEditingDomain()
//	 */
//	@Override
//	public synchronized TransactionalEditingDomain getEditingDomain() {
//		if (this.editingDomain == null) {
//			createEditingDomain();
//		}
//		return this.editingDomain;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior#disposeEditingDomain()
//	 */
//	@Override
//	protected void disposeEditingDomain() {
//		this.editingDomain.dispose();
//	}
}
