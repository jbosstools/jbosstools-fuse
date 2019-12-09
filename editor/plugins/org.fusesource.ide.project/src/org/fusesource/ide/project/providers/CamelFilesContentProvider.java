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

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author lhein
 * 
 */
public class CamelFilesContentProvider implements ITreeContentProvider {

	private HashMap<IProject, CamelVirtualFolder> cache = new HashMap<>();

	@Override
	public Object[] getChildren(Object parentElement) {
 	if (parentElement instanceof IProject) {
			IProject project = (IProject) parentElement;

			if (!project.isAccessible()) {
				return new Object[0];
			}

			CamelVirtualFolder cvf = cache.get(project);
			if (cvf == null) {
				cvf = new CamelVirtualFolder(project);
				cvf.populateChildren();
				cache.put(project, cvf);
			}

			return new CamelVirtualFolder[] { cvf };
		} else if (parentElement instanceof CamelVirtualFolder) {
			CamelVirtualFolder cvf = (CamelVirtualFolder) parentElement;
			return cvf.getCamelFiles().toArray(
					new IResource[cvf.getCamelFiles().size()]);
		}
		return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof CamelVirtualFolder) {
			return ((CamelVirtualFolder) element).getProject();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
