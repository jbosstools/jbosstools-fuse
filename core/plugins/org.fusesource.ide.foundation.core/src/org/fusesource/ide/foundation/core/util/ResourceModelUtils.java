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

package org.fusesource.ide.foundation.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

public class ResourceModelUtils {
	
	public static final String CONTENT_TYPE_XML = "org.eclipse.core.runtime.xml";
	public static final String CONTENT_TYPE_TEXT = "org.eclipse.core.runtime.text";
	

	public static File toFile(IFile file) {
		File f = file.getRawLocation() != null ? file.getRawLocation().toFile() : null;
		if (f == null) f = file.getLocation() != null ? file.getLocation().toFile() : null;
		if (f == null) f = file.getFullPath() != null ? file.getFullPath().toFile() : null;
		return f;
	}
	
	public static List<IFile> filter(IContainer container, Filter<IFile> filter) throws CoreException {
		List<IFile> list = new ArrayList<IFile>();
		filter(container, filter, list);
		return list;
	}

	protected static void filter(IContainer container, Filter<IFile> filter, List<IFile> list) throws CoreException {
		IResource[] members = container.members();
		for (IResource resource : members) {
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				if (filter.matches(file)) {
					list.add(file);
				}
			} else if (resource instanceof IContainer) {
				filter((IContainer) resource, filter, list);
			}
		}
	}

	/**
	 * Returns the file name relative to the source directory or project it is contained
	 * inside
	 */
	public static String getRelativeFileUri(IFile file) {
		IContainer parent = file.getParent();
	
		while (parent != null && !(parent instanceof IProject)) {
			IJavaElement element = JavaCore.create(parent);
	
			// lets use package relative URIs for files
			if (element != null && element.exists() && element instanceof IPackageFragmentRoot) {
				IPath parentFullPath = parent.getFullPath();
				int segmentCount = parentFullPath.segmentCount();
				IPath relativePath = file.getFullPath().removeFirstSegments(segmentCount);
				return relativePath.toString();
			}
			parent = parent.getParent();
		}
		return file.getProjectRelativePath().toString();
	}

	public static boolean isContentTypeId(IFile file, String id) throws CoreException {
		return isContentType(file.getContentDescription(), id);
	}

	public static boolean isContentType(IContentDescription contentDescription, String id) {
		boolean answer = false;
		if (contentDescription != null) {
			return isContentType(contentDescription.getContentType(), id);
		} 
		return answer;
	}

	/**
	 * Recursively walks the content type hierarchy to find if the given content type matches the id
	 */
	public static boolean isContentType(IContentType contentType, String id) {
		if (contentType != null) {
			if (Objects.equal(contentType.getId(), id)) {
				return true;
			}
			IContentType baseType = contentType.getBaseType();
			if (baseType != null) {
				return isContentType(baseType, id);
			}
		}
		return false;
	}

	public static boolean isTextContentType(IFile file) throws CoreException {
		return isContentTypeId(file, CONTENT_TYPE_TEXT);
	}
}
