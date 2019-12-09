/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.project.providers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.project.Activator;

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
		if (element instanceof IProject) {
			return super.getImage(element);
		} else if (element instanceof CamelVirtualFolder) {
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
	@Override
	public String getText(Object element) {
		if( element instanceof CamelVirtualFolder ) {
			CamelVirtualFolder vFolder = (CamelVirtualFolder)element;
			return vFolder.getName();
		} else if (element instanceof IProject) {
			IProject prj = (IProject)element;
			return prj.getName();
		} else if (element instanceof IResource) {
			IResource ifile = (IResource)element;
			return ifile.getProjectRelativePath().toString();
		}
		return super.getText(element);
	}
}
