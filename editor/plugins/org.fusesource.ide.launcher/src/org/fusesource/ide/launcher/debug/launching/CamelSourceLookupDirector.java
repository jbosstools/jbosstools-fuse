/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.debug.launching;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;
import org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage;
import org.eclipse.debug.ui.ISourcePresentation;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaSourceLookupParticipant;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.fusesource.ide.camel.model.service.core.debug.util.CamelDebugRegistry;
import org.fusesource.ide.camel.model.service.core.debug.util.CamelDebugRegistryEntry;
import org.fusesource.ide.camel.model.service.core.debug.util.CamelDebugUtils;
import org.fusesource.ide.camel.model.service.core.debug.util.ICamelDebugConstants;
import org.fusesource.ide.camel.model.service.core.io.CamelXMLEditorInput;

/**
 * Camel source lookup director. For Camel source lookup there is one source
 * lookup participant. 
 * 
 * @author lhein
 */
public class CamelSourceLookupDirector extends AbstractSourceLookupDirector implements ISourcePresentation {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.sourcelookup.ISourceLookupDirector#initializeParticipants()
	 */
	@Override
	public void initializeParticipants() {
		ISourceLookupParticipant participants[] = new ISourceLookupParticipant[2];
		participants[0] = new CamelSourceLookupParticipant();
		participants[1] = new JavaSourceLookupParticipant();
		addParticipants(participants);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorInput(java.lang.Object)
	 */
	@Override
	public IEditorInput getEditorInput(Object element) {
		IEditorInput input = null;
		
		if (element instanceof LocalFileStorage) {
			LocalFileStorage lfs = (LocalFileStorage)element;

			for (CamelDebugRegistryEntry entry : CamelDebugRegistry.getInstance().getEntries().values()) {
				IFile f = (IFile)entry.getEditorInput().getAdapter(IFile.class);
				if (f.getLocation().toFile().getPath().equals(lfs.getFile().getPath())) {
					input = entry.getEditorInput();
					break;
				}
			}
			return input;
		}
		
		// Differentiate Java and XML files.
		else if (element instanceof File) {
			File sourceFile = (File)element;
			if (sourceFile.getName().toLowerCase().endsWith(".java")) {
				IProject prj = CamelDebugUtils.getProjectForFilePath(sourceFile.getPath());
				IPath fp = prj.findMember(sourceFile.getPath()).getFullPath();
				IFile sourceFileResolved = prj.getFile(fp);
				return new FileEditorInput(sourceFileResolved); 
			}
			for (CamelDebugRegistryEntry entry : CamelDebugRegistry.getInstance().getEntries().values()) {
				IFile f = (IFile)entry.getEditorInput().getAdapter(IFile.class);
				if (f.getLocation().toFile().getName().equals(sourceFile.getName())) {
					input = entry.getEditorInput();
					break;
				}
			}	
			return input;
		} else if (element instanceof IFile) {
			IFile sourceFile = (IFile)element;
			if (sourceFile.getName().toLowerCase().endsWith(".java")) {
				return new FileEditorInput(sourceFile); 
			}
			for (CamelDebugRegistryEntry entry : CamelDebugRegistry.getInstance().getEntries().values()) {
				IFile f = ((CamelXMLEditorInput)entry.getEditorInput()).getCamelContextFile(); 
				if (f != null && f.getFullPath().equals(sourceFile.getFullPath())) {
					input = entry.getEditorInput();
					break;
				}
			}	
			return input;
		}

		return null;
	}

	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorId(org.eclipse.ui.IEditorInput, java.lang.Object)
	 */
	@Override
	public String getEditorId(IEditorInput input, Object element) {
		if (input instanceof FileEditorInput)
			return JavaUI.ID_CU_EDITOR;
		else
			return ICamelDebugConstants.CAMEL_EDITOR_ID;
	}
}
