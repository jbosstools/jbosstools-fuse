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
package org.fusesource.ide.camel.editor.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
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

	@Override
	public boolean matches(IEditorReference editorRef, IEditorInput input) {
		IEditorInput editorInput = null;
		
		try {
			editorInput = editorRef.getEditorInput();
		} catch (PartInitException ex) {
			CamelEditorUIActivator.pluginLog().logError(ex);
		}
		
		if (editorInput == null || input == null)
			return false;

		CamelXMLEditorInput currentOpenInput;
		CamelXMLEditorInput toBeOpenedInput;
		if (editorInput instanceof CamelXMLEditorInput) {
			currentOpenInput = (CamelXMLEditorInput)editorInput;
		} else {
			return false;
		}

		if (input instanceof CamelXMLEditorInput) {
			toBeOpenedInput = (CamelXMLEditorInput)input;
		} else if (input instanceof FileEditorInput) {
			FileEditorInput fei = (FileEditorInput)input;
			toBeOpenedInput = new CamelXMLEditorInput(fei.getFile(), null);
		} else {
			return false;
		}

		// we consider 2 inputs the same if their camel context file path is the same
		IFile currentInputCamelContextFile = currentOpenInput.getCamelContextFile();
		IFile toBeOpenedInputCamelContextFile = toBeOpenedInput.getCamelContextFile();
		if(currentInputCamelContextFile != null && toBeOpenedInputCamelContextFile != null) {
			IPath currentEditorInputCamelContextFileLocation = currentInputCamelContextFile.getLocation();
			IPath tobOpenedEditorInputCamelContextFileLocation = toBeOpenedInputCamelContextFile.getLocation();
			if (currentEditorInputCamelContextFileLocation != null && tobOpenedEditorInputCamelContextFileLocation != null) {
				return currentEditorInputCamelContextFileLocation.toOSString().equals(tobOpenedEditorInputCamelContextFileLocation.toOSString());
			} 
		}
		return false;
	}
}
