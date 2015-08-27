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

package org.fusesource.ide.foundation.core.util;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.fusesource.ide.foundation.core.internal.FoundationCoreActivator;
import org.fusesource.ide.foundation.core.xml.namespace.BlueprintNamespaceHandler;
import org.fusesource.ide.foundation.core.xml.namespace.FindCamelNamespaceHandler;
import org.fusesource.ide.foundation.core.xml.namespace.FindNamespaceHandlerSupport;
import org.fusesource.ide.foundation.core.xml.namespace.SpringNamespaceHandler;
import org.xml.sax.InputSource;

/**
 * @author lhein
 */
public class CamelUtils {
	
	private static FindNamespaceHandlerSupport blueprintXmlMatcher = new BlueprintNamespaceHandler();
	private static FindNamespaceHandlerSupport springXmlMatcher = new SpringNamespaceHandler();
	private static FindNamespaceHandlerSupport camelXmlMatcher = new FindCamelNamespaceHandler();
	
	/**
	 * checks if the given file is a blueprint file or not
	 * @param filePath
	 * @return
	 */
	public static boolean isCamelContextFile(String filePath) {
		return matchesNamespace(filePath, camelXmlMatcher);
	}
	
	
	/**
	 * checks if the given file is a blueprint file or not
	 * @param filePath
	 * @return
	 */
	public static boolean isBlueprintFile(String filePath) {
		return matchesNamespace(filePath,  blueprintXmlMatcher);
	}
	
	/**
	 * checks if the given file is a spring file or not
	 * @param filePath
	 * @return
	 */
	public static boolean isSpringFile(String filePath) {
		return matchesNamespace(filePath, springXmlMatcher);
	}

	
	private static boolean matchesNamespace(String filePath, FindNamespaceHandlerSupport support) {
		boolean matches = false;
		if (filePath != null && filePath.trim().length()>0) {
			String rawPath = null;
			if (filePath.startsWith("file:")) {
				rawPath = filePath.substring("file:".length());
			} else {
				rawPath = filePath;
			}
			IPath f = Path.fromOSString(rawPath);
			if (f.toFile().exists() && f.toFile().isFile()) {
				// file exists, now check if its blueprint or spring
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(f);
				if (file != null) 
					matches = matches(support, file);
			}
		}
		
		return matches;
	}
	
	public static boolean matches(FindNamespaceHandlerSupport handler, IFile ifile) {
		try {
			File file = ResourceModelUtils.toFile(ifile);
			if (file != null) {
				handler.parseContents(new InputSource(new FileInputStream(file)));
				return handler.isNamespaceFound();
			}
		} catch (Exception e) {
			FoundationCoreActivator.pluginLog().logError("** Load failed. Using default model. **", e);
		}
		return false;
	}

	
}
