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

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;
import org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage;
import org.eclipse.debug.ui.ISourcePresentation;
import org.eclipse.ui.IEditorInput;
import org.fusesource.ide.launcher.debug.util.CamelDebugRegistry;
import org.fusesource.ide.launcher.debug.util.CamelDebugRegistryEntry;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;

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
		addParticipants(new ISourceLookupParticipant[]{new CamelSourceLookupParticipant()});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorInput(java.lang.Object)
	 */
	@Override
	public IEditorInput getEditorInput(Object element) {
		if (element instanceof LocalFileStorage) {
			LocalFileStorage lfs = (LocalFileStorage)element;
			IEditorInput input = null;
			for (CamelDebugRegistryEntry entry : CamelDebugRegistry.getInstance().getEntries().values()) {
				IFile f = (IFile)entry.getEditorInput().getAdapter(IFile.class);
				if (f.getLocation().toFile().getPath().equals(lfs.getFile().getPath())) {
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
		return ICamelDebugConstants.CAMEL_EDITOR_ID;
	}
}
