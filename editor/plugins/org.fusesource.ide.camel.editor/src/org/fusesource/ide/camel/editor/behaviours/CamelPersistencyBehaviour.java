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

package org.fusesource.ide.camel.editor.behaviours;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.commands.ImportCamelContextElementsCommand;
import org.fusesource.ide.camel.editor.features.custom.LayoutDiagramFeature;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.io.IRemoteCamelEditorInput;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;

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
        
        return importCommand.getDiagram();
	}
	
	/**
	 * Creates the diagram from the current model
	 */
	public boolean loadModel(IEditorInput input) {
		IFile camelContextFile = null;
		
		IFileEditorInput fileEditorInput = editor.asFileEditorInput(input);
		if (fileEditorInput != null) {
			camelContextFile = fileEditorInput.getFile();
			editor.setWorkspaceProject(camelContextFile.getProject());
			
			// load the model
			try {
				CamelIOHandler ioHandler = new CamelIOHandler();
				editor.setModel(ioHandler.loadCamelModel(camelContextFile, new NullProgressMonitor()));
			} catch (Exception ex) {
				CamelEditorUIActivator.pluginLog().logError("Unable to load Camel context file: " + camelContextFile.getRawLocation().toOSString(), ex);
			}
			return true;
		} else {
			IRemoteCamelEditorInput remoteEditorInput = editor.asRemoteCamelEditorInput(input);
			if (remoteEditorInput != null) {
				camelContextFile = null;

				// load the model
				try {
					String text = remoteEditorInput.getXml();
					CamelIOHandler ioHandler = new CamelIOHandler();
					editor.setModel(ioHandler.loadCamelModel(text, new NullProgressMonitor()));
				} catch (Exception ex) {
					CamelEditorUIActivator.pluginLog().logError("Unable to load Camel context string", ex);
				}
				return true;
			}
		}
		return false;
	}
}
