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
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;
import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.IDiagramContainerUI;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.internal.CamelModelChangeListener;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.provider.CamelDiagramEditorContextMenuProvider;

/**
 * @author lhein
 */
public class CamelDiagramBehaviour extends DiagramBehavior {
	
	private CamelUpdateBehaviour camelUpdateBehaviour;
	private CamelPaletteBehaviour camelPaletteBehaviour;
	private CamelPersistencyBehaviour camelPersistencyBehaviour;
	private CamelDesignEditor camelDesignEditor;
	
	public CamelDiagramBehaviour(IDiagramContainerUI diagramContainer) {
		super(diagramContainer);
		this.camelDesignEditor = (CamelDesignEditor)diagramContainer;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramBehavior#createUpdateBehavior()
	 */
	@Override
	protected DefaultUpdateBehavior createUpdateBehavior() {
		if (this.camelUpdateBehaviour == null) {
			this.camelUpdateBehaviour = new CamelUpdateBehaviour(this.camelDesignEditor);
		}
		return this.camelUpdateBehaviour;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramBehavior#createPersistencyBehavior()
	 */
	@Override
	protected DefaultPersistencyBehavior createPersistencyBehavior() {
		if (this.camelPersistencyBehaviour == null) {
			this.camelPersistencyBehaviour = new CamelPersistencyBehaviour(this.camelDesignEditor);
		}
		return camelPersistencyBehaviour;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramBehavior#createPaletteBehaviour()
	 */
	@Override
	protected DefaultPaletteBehavior createPaletteBehaviour() {
		if (this.camelPaletteBehaviour == null) {
			this.camelPaletteBehaviour = new CamelPaletteBehaviour(this.camelDesignEditor);
		}
		return this.camelPaletteBehaviour;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramBehavior#getEditingDomain()
	 */
	@Override
	public TransactionalEditingDomain getEditingDomain() {
		return this.camelUpdateBehaviour != null ? this.camelUpdateBehaviour.getEditingDomain() : null;
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramBehavior#createContextMenuProvider()
	 */
	@Override
	protected ContextMenuProvider createContextMenuProvider() {
		return new CamelDiagramEditorContextMenuProvider(this.camelDesignEditor.getGraphicalViewer(), this.camelDesignEditor.getActionRegistry(), getConfigurationProvider());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramBehavior#registerBusinessObjectsListener()
	 */
	@Override
	protected void registerBusinessObjectsListener() {
//		super.registerBusinessObjectsListener();
		CamelModelChangeListener camelModelListener = new CamelModelChangeListener(this.camelDesignEditor);
		
		TransactionalEditingDomain eDomain = getEditingDomain();
		eDomain.addResourceSetListener(camelModelListener);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramBehavior#getEditorInitializationError()
	 */
	@Override
	public String getEditorInitializationError() {
		return super.getEditorInitializationError();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.graphiti.ui.editor.DiagramBehavior#
	 * setEditorInitializationError(java.lang.String)
	 */
	@Override
	protected void setEditorInitializationError(String editorInitializationError) {
		super.setEditorInitializationError(UIMessages.camelDiagramBehaviourMessageOnErrorEditorInitialization);
	}

}
