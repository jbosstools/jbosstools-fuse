/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
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
import org.fusesource.ide.foundation.ui.io.CamelXMLEditorInput;
import org.fusesource.ide.launcher.debug.util.CamelDebugRegistry;
import org.fusesource.ide.launcher.debug.util.CamelDebugRegistryEntry;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;

/**
 * Camel source lookup director. For Camel source lookup there are two sources
 * lookup participant: a Camel specific and the JavaSource one.
 * 
 * @author lhein
 */
public class CamelSourceLookupDirector extends AbstractSourceLookupDirector implements ISourcePresentation {

	@Override
	public void initializeParticipants() {
		addParticipants(new ISourceLookupParticipant[]{new CamelSourceLookupParticipant(), new JavaSourceLookupParticipant()});
	}

	@Override
	public IEditorInput getEditorInput(Object element) {
		if (element instanceof LocalFileStorage) {
			return getEditorInput((LocalFileStorage)element);
		} else if (element instanceof File) {
			return getEditorInput((File)element);
		} else if (element instanceof IFile) {
			return getEditorInput((IFile)element);
		}
		return null;
	}

	private IEditorInput getEditorInput(LocalFileStorage lfs) {
		for (CamelDebugRegistryEntry entry : CamelDebugRegistry.getInstance().getEntries().values()) {
			IFile f = entry.getEditorInput().getAdapter(IFile.class);
			if (f.getLocation().toFile().getPath().equals(lfs.getFile().getPath())) {
				return entry.getEditorInput();
			}
		}
		return null;
	}

	private IEditorInput getEditorInput(IFile sourceFile) {
		if (isFileNameWithJavaExtension(sourceFile.getName())) {
			return new FileEditorInput(sourceFile);
		}
		for (CamelDebugRegistryEntry entry : CamelDebugRegistry.getInstance().getEntries().values()) {
			IFile f = ((CamelXMLEditorInput)entry.getEditorInput()).getCamelContextFile(); 
			if (f != null && f.getFullPath().equals(sourceFile.getFullPath())) {
				return entry.getEditorInput();
			}
		}	
		return null;
	}

	private IEditorInput getEditorInput(File sourceFile) {
		if (isFileNameWithJavaExtension(sourceFile.getName())) {
			IProject prj = CamelDebugUtils.getProjectForFilePath(sourceFile.getPath());
			IPath fp = prj.findMember(sourceFile.getPath()).getFullPath();
			IFile sourceFileResolved = prj.getFile(fp);
			return new FileEditorInput(sourceFileResolved); 
		}
		for (CamelDebugRegistryEntry entry : CamelDebugRegistry.getInstance().getEntries().values()) {
			IFile f = entry.getEditorInput().getAdapter(IFile.class);
			if (f.getLocation().toFile().getName().equals(sourceFile.getName())) {
				return entry.getEditorInput();
			}
		}	
		return null;
	}

	private boolean isFileNameWithJavaExtension(String filename) {
		return filename.toLowerCase().endsWith(".java");
	}

	@Override
	public String getEditorId(IEditorInput input, Object element) {
		if (input instanceof FileEditorInput) {
			return JavaUI.ID_CU_EDITOR;
		} else {
			return ICamelDebugConstants.CAMEL_EDITOR_ID;
		}
	}
}
