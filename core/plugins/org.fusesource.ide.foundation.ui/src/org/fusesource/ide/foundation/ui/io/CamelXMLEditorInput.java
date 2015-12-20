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

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * @author lhein
 */
public class CamelXMLEditorInput implements IEditorInput, IPersistableElement {
	
	public static final String KEY_CONTEXT_FILE = "camel.context.file.path";
	
	private IFile camelContextFile;
	
	public CamelXMLEditorInput(IFile contextFile) {
		this.camelContextFile = contextFile;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter.equals(IFile.class) || adapter.equals(IResource.class)) {
			return (T)this.camelContextFile;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	@Override
	public boolean exists() {
		return this.camelContextFile != null && this.camelContextFile.exists();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	@Override
	public String getName() {
		return camelContextFile.getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	@Override
	public IPersistableElement getPersistable() {
		if (camelContextFile != null) {
			return this;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return String.format("%s%s%s", camelContextFile.getProject().getName(), File.separator, camelContextFile.getProjectRelativePath().toString());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPersistable#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		// Do not store anything for deleted objects
		boolean exists = exists();
		if (!exists) {
			return;
		}
		// Store object name, URI and diagram type provider ID
		memento.putString(KEY_CONTEXT_FILE, this.camelContextFile.getFullPath().toOSString());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPersistableElement#getFactoryId()
	 */
	@Override
	public String getFactoryId() {
		return CamelXMLEditorInputFactory.class.getName();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getCamelContextFile() == null) ? 0 : this.getCamelContextFile().hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CamelXMLEditorInput) {
			CamelXMLEditorInput input2 = (CamelXMLEditorInput)obj;
			if (this.getCamelContextFile() != null && input2.getCamelContextFile() != null) {
				return this.getCamelContextFile().equals(input2.getCamelContextFile());
			}
		}
		return false;
	}
	
	public IFile getCamelContextFile() {
		return this.camelContextFile;
	}
	
	public void onEditorInputSave() {
	}
}
