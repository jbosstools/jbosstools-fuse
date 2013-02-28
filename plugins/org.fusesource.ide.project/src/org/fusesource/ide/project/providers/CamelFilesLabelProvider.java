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

package org.fusesource.ide.project.providers;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.project.Activator;
import org.fusesource.ide.project.providers.CamelFilesContentProvider.CamelVirtualFile;
import org.fusesource.ide.project.providers.CamelFilesContentProvider.CamelVirtualFolder;

/**
 * @author lhein
 * 
 */
public class CamelFilesLabelProvider extends LabelProvider {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		String name;
		if (element instanceof CamelVirtualFolder) {
			name = "camelFolderIcon";
		} else {
			name = "camelFileIcon";
		}
		return Activator.getDefault().getImageRegistry().get(name);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if( element instanceof CamelVirtualFolder ) {
			CamelVirtualFolder vFolder = (CamelVirtualFolder)element;
			return vFolder.getName();
		} else if (element instanceof CamelVirtualFile) {
			IFile ifile = (IFile)element;
			return ifile.getProjectRelativePath().toString();
		} else if (element instanceof IFile) {
			IFile ifile = (IFile)element;
			return ifile.getName();
		}
		return element.toString();
	}
}
