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
package org.fusesource.ide.camel.editor.utils;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.foundation.ui.io.CamelXMLEditorInput;

/**
 * @author lhein
 */
public class CamelEditorMatchingStrategy implements IEditorMatchingStrategy {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorMatchingStrategy#matches(org.eclipse.ui.IEditorReference, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public boolean matches(IEditorReference editorRef, IEditorInput input) {
		IEditorInput editorInput = null;
		CamelXMLEditorInput currentOpenInput = null;
		CamelXMLEditorInput toBeOpenedInput  = null;
		
		try {
			editorInput = editorRef.getEditorInput();
		} catch (PartInitException ex) {
			CamelEditorUIActivator.pluginLog().logError(ex);
		}
		
		if (editorInput == null || input == null)
			return false;

		if (editorInput instanceof CamelXMLEditorInput) {
			currentOpenInput = (CamelXMLEditorInput)editorInput;
		} else {
			return false;
		}

		if (input instanceof CamelXMLEditorInput) {
			toBeOpenedInput = (CamelXMLEditorInput)input;
		} else if (input instanceof FileEditorInput) {
			FileEditorInput fei = (FileEditorInput)input;
			toBeOpenedInput = new CamelXMLEditorInput(fei.getFile());
		} else {
			return false;
		}

		// we consider 2 inputs the same if their camel context file path is the same
		if (currentOpenInput.getCamelContextFile().getLocation().toOSString().equals(toBeOpenedInput.getCamelContextFile().getLocation().toOSString())) {
			return true;
		} 
		return false;
	}
}
