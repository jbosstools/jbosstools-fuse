/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 ******************************************************************************/

package org.fusesource.ide.camel.editor.editor;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;
import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.IDiagramContainerUI;
import org.eclipse.graphiti.ui.platform.IConfigurationProvider;
import org.fusesource.ide.camel.editor.CamelModelChangeListener;

/**
 * @author lhein
 *
 */
public class CamelDiagramBehaviour extends DiagramBehavior {
	
	private CamelUpdateBehaviour camelUpdateBehaviour;
	private CamelPaletteBehaviour camelPaletteBehaviour;
	private CamelPersistencyBehaviour camelPersistencyBehaviour;
	private CamelModelChangeListener camelModelListener;
	private RiderDesignEditor riderDesignEditor;
	
	/**
	 * 
	 */
	public CamelDiagramBehaviour(IDiagramContainerUI diagramContainer) {
		super(diagramContainer);
		this.riderDesignEditor = (RiderDesignEditor)diagramContainer;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramBehavior#getUpdateBehavior()
	 */
	@Override
	public DefaultUpdateBehavior getUpdateBehavior() {
		if (this.camelUpdateBehaviour == null) {
			this.camelUpdateBehaviour = new CamelUpdateBehaviour(this.riderDesignEditor);
		}
		return this.camelUpdateBehaviour;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramBehavior#getPersistencyBehavior()
	 */
	@Override
	protected DefaultPersistencyBehavior getPersistencyBehavior() {
		if (this.camelPersistencyBehaviour == null) {
			this.camelPersistencyBehaviour = new CamelPersistencyBehaviour(this.riderDesignEditor);
		}
		return camelPersistencyBehaviour;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramBehavior#getPaletteBehavior()
	 */
	@Override
	protected DefaultPaletteBehavior getPaletteBehavior() {
		if (this.camelPaletteBehaviour == null) {
			this.camelPaletteBehaviour = new CamelPaletteBehaviour(this.riderDesignEditor);
		}
		return this.camelPaletteBehaviour;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramBehavior#getEditingDomain()
	 */
	@Override
	public TransactionalEditingDomain getEditingDomain() {
		return (this.camelUpdateBehaviour != null ? this.camelUpdateBehaviour.getEditingDomain() : null);
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramBehavior#getConfigurationProvider()
	 */
	@Override
	public IConfigurationProvider getConfigurationProvider() {
		return super.getConfigurationProvider();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramBehavior#createContextMenuProvider()
	 */
	@Override
	protected ContextMenuProvider createContextMenuProvider() {
		return new CamelDiagramEditorContextMenuProvider(this.riderDesignEditor.getGraphicalViewer(), this.riderDesignEditor.getActionRegistry(), getConfigurationProvider());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramBehavior#registerBusinessObjectsListener()
	 */
	@Override
	protected void registerBusinessObjectsListener() {
		//super.registerBusinessObjectsListener();
		camelModelListener = new CamelModelChangeListener(this.riderDesignEditor);
		
		TransactionalEditingDomain eDomain = getEditingDomain();
		eDomain.addResourceSetListener(camelModelListener);
	}
}
