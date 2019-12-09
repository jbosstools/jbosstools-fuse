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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.commands.ImportCamelContextElementsCommand;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElementIDUtil;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteContainerElement;

/**
 * @author lhein
 */
public class CamelPersistencyBehaviour  extends DefaultPersistencyBehavior {
	
	private CamelDesignEditor editor;
	private CamelFile camelFile;
	
	/**
	 * 
	 * @param editor
	 */
	public CamelPersistencyBehaviour(CamelDesignEditor editor) {
		super(editor.getDiagramBehavior());
		this.editor = editor;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior#loadDiagram(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Diagram loadDiagram(URI uri) {
        // load the model
        if (!loadModel(this.editor.getEditorInput())) {
            return null;
        }
     
        // add the diagram contents
        ImportCamelContextElementsCommand importCommand = new ImportCamelContextElementsCommand(editor, editor.getEditingDomain(), editor.getModel());
        editor.getEditingDomain().getCommandStack().execute(importCommand);
        this.camelFile = editor.getModel();
        
        // name the editor tab correctly
        this.editor.getParent().onFileLoading(camelFile.getResource().getName());
        
        final CamelRouteContainerElement camelContext = camelFile.getRouteContainer();
		if (camelContext != null) {
			new CamelModelElementIDUtil().ensureUniqueID(camelContext);
        }
        
        return importCommand.getDiagram();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior#saveDiagram(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void saveDiagram(IProgressMonitor monitor) {
		// save the model
		try {
			CamelIOHandler ioHandler = new CamelIOHandler();
			ioHandler.setDocument(camelFile.getDocument());
			ioHandler.saveCamelModel(camelFile, camelFile.getResource(), monitor);
		} catch (Exception ex) {
			CamelEditorUIActivator.pluginLog().logError("Unable to save Camel context file: " + camelFile.getResource().getRawLocation().toOSString(), ex);
		}
	}
	
	/**
	 * Creates the diagram from the current model
	 */
	public boolean loadModel(IEditorInput input) {
		IFileEditorInput fileEditorInput = editor.asFileEditorInput(input);
		if (fileEditorInput != null) {
			IFile camelContextFile = fileEditorInput.getFile();
			editor.setWorkspaceProject(camelContextFile.getProject());
			
			// load the model
			try {
				CamelIOHandler ioHandler = new CamelIOHandler();
				CamelFile camelModel = ioHandler.loadCamelModel(camelContextFile, new NullProgressMonitor());
				if(camelModel != null){
					editor.setModel(camelModel);
					camelFile = editor.getModel();
				} else {
					CamelEditorUIActivator.pluginLog().logError("Unable to load Camel context file: " + camelContextFile.getRawLocation().toOSString());
					return false;
				}
			} catch (Exception ex) {
				CamelEditorUIActivator.pluginLog().logError("Unable to load Camel context file: " + camelContextFile.getRawLocation().toOSString(), ex);
				return false;
			}
			return true;
		}
		return false;
	}
	
	
}
