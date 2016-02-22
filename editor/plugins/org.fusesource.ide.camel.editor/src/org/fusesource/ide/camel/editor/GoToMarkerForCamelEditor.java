/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
import org.fusesource.ide.camel.validation.diagram.IFuseMarker;

/**
 * @author Aurelien Pupier
 *
 */
public class GoToMarkerForCamelEditor implements IGotoMarker {

	private CamelEditor camelEditor;

	/**
	 * @param camelEditor
	 */
	public GoToMarkerForCamelEditor(CamelEditor camelEditor) {
		this.camelEditor = camelEditor;
	}

	/**
	 * Go to the better fitted editor: Camel Route or if can't find the element
	 * in them, go to source editor
	 */
	@Override
	public void gotoMarker(IMarker marker) {
		try {
			String id = (String) marker.getAttribute(IFuseMarker.CAMEL_ID);
			if (id != null) {
				final CamelDesignEditor designEditor = camelEditor.getDesignEditor();
				CamelFile camelFile = designEditor.getModel();
				CamelModelElement camelModelElement = camelFile.findNode(id);
				if (camelModelElement != null) {
					camelEditor.setActiveEditor(designEditor);
					designEditor.setSelectedNode(camelModelElement);
					// TODO: go to the exact Property place
					return;
				}
			}
		} catch (CoreException e) {
			CamelEditorUIActivator.pluginLog().logError(e);
		}



		// Source editor
		int lineNumber = MarkerUtilities.getLineNumber(marker);
		if (lineNumber != -1) {
			final StructuredTextEditor sourceEditor = camelEditor.getSourceEditor();
			camelEditor.setActiveEditor(sourceEditor);
			final IGotoMarker sourceEditorGoToMarker = (IGotoMarker) sourceEditor.getAdapter(IGotoMarker.class);
			if (sourceEditorGoToMarker != null) {
				sourceEditorGoToMarker.gotoMarker(marker);
			}
		}
	}

}
