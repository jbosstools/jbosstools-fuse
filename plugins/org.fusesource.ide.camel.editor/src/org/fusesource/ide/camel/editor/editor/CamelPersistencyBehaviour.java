/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.editor;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.fusesource.camel.tooling.util.ValidationHandler;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.editor.io.CamelContextIOUtils;
import org.fusesource.ide.camel.model.io.IRemoteCamelEditorInput;

/**
 * @author lhein
 */
public class CamelPersistencyBehaviour extends DefaultPersistencyBehavior {
	
	private RiderDesignEditor editor;
	
	/**
	 * 
	 * @param editor
	 */
	public CamelPersistencyBehaviour(RiderDesignEditor editor) {
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

        // create the diagram
        editor.getActiveConfig().diagram = Graphiti.getPeCreateService().createDiagram("CamelContext", "CamelContext", true); //$NON-NLS-1$ //$NON-NLS-2$
        return editor.getActiveConfig().diagram;
	}
	
	/**
	 * Creates the diagram from the current model
	 */
	protected boolean loadModel(IEditorInput input) {
		IFile camelContextFile = null;
		IProject container = null;
		
		IFileEditorInput fileEditorInput = editor.asFileEditorInput(input);
		if (fileEditorInput != null) {
			camelContextFile = fileEditorInput.getFile();
			container = camelContextFile.getProject();

			// load the model
			if (editor.getModel() == null) {
				try {
					editor.setModel(CamelContextIOUtils.loadModelFromFile(camelContextFile));
					ValidationHandler status = CamelContextIOUtils.validateModel(editor.getModel());
					if (status.hasErrors()) {
						Activator.getLogger().error("Unable to validate the model. Invalid input!");
						return false;
					}
				} catch (PartInitException pe) {
					Activator.getLogger().error("Unable to load the model. Invalid input!", pe);
					return false;
				}
			}
			return true;
		} else {
			IRemoteCamelEditorInput remoteEditorInput = editor.asRemoteCamelEditorInput(input);
			if (remoteEditorInput != null) {
				camelContextFile = null;
				container = null;

				// load the model
				if (editor.getModel() == null) {
					try {
						String text = remoteEditorInput.getXml();
						try {
							editor.setModel(CamelContextIOUtils.loadModelFromText(text));
							ValidationHandler status = CamelContextIOUtils.validateModel(editor.getModel());
							if (status.hasErrors()) {
								Activator.getLogger().error("Unable to validate the model. Invalid input!");
								return false;
							}
						} catch (PartInitException pe) {
							Activator.getLogger().error("Unable to load the model. Invalid input!", pe);
							return false;
						}
					} catch (IOException e) {
						Activator.getLogger().error("Unable to load the model: " + e, e);
						return false;
					}
				}
				
				this.editor.setCamelContextFile(camelContextFile);
				this.editor.setContainer(container);

				return true;
			}
		}
		return false;
	}
}
