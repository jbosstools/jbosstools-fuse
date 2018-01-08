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
package org.fusesource.ide.foundation.ui.io;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * @author lhein
 */
public class CamelXMLEditorInputFactory implements IElementFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IElementFactory#createElement(org.eclipse.ui.IMemento)
	 */
	@Override
	public IAdaptable createElement(IMemento memento) {
		// get file path
		final String filePath = memento.getString(CamelXMLEditorInput.KEY_CONTEXT_FILE);
		if (filePath == null) {
			return null;
		}
		final String containerId = memento.getString(CamelXMLEditorInput.KEY_SELECTED_CONTAINER_ID);
		if (containerId == null) {
			return null;
		}
		return new CamelXMLEditorInput(ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(filePath)).getAdapter(IFile.class), containerId);
	}
}
