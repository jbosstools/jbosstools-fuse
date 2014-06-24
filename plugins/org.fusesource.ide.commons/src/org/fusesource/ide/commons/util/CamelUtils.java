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

package org.fusesource.ide.commons.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.fusesource.ide.commons.contenttype.BlueprintXmlMatchingStrategy;
import org.fusesource.ide.commons.contenttype.CamelXmlMatchingStrategy;
import org.fusesource.ide.commons.contenttype.SpringXmlMatchingStrategy;
import org.fusesource.ide.commons.contenttype.XmlMatchingStrategySupport;

/**
 * @author lhein
 */
public class CamelUtils {
	
	private static XmlMatchingStrategySupport blueprintXmlMatcher = new BlueprintXmlMatchingStrategy();
	private static XmlMatchingStrategySupport springXmlMatcher = new SpringXmlMatchingStrategy();
	private static XmlMatchingStrategySupport camelXmlMatcher = new CamelXmlMatchingStrategy();
	
	/**
	 * checks if the given file is a blueprint file or not
	 * @param filePath
	 * @return
	 */
	public static boolean isCamelContextFile(String filePath) {
		boolean isCamelContext = false;
		
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
				if (file != null) isCamelContext = camelXmlMatcher.matches(file);
			}
		}
		
		return isCamelContext;
	}
	
	/**
	 * checks if the given file is a blueprint file or not
	 * @param filePath
	 * @return
	 */
	public static boolean isBlueprintFile(String filePath) {
		boolean isBlueprint = false;
		
		if (filePath != null && filePath.trim().length()>0) {
			String rawPath = null;
			if (filePath.startsWith("file:")) {
				rawPath = filePath.substring(5);
			} else {
				rawPath = filePath;
			}
			IPath f = Path.fromOSString(rawPath);
			if (f.toFile().exists() && f.toFile().isFile()) {
				// file exists, now check if its blueprint or spring
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(f);
				if (file != null) isBlueprint = blueprintXmlMatcher.matches(file);
			}
		}
		
		return isBlueprint;
	}
	
	/**
	 * checks if the given file is a spring file or not
	 * @param filePath
	 * @return
	 */
	public static boolean isSpringFile(String filePath) {
		boolean isSpring = false;
		
		if (filePath != null && filePath.trim().length()>0) {
			String rawPath = null;
			if (filePath.startsWith("file:")) {
				rawPath = filePath.substring(5);
			} else {
				rawPath = filePath;
			}
			IPath f = Path.fromOSString(rawPath);
			if (f.toFile().exists() && f.toFile().isFile()) {
				// file exists, now check if its blueprint or spring
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(f);
				if (file != null) isSpring = springXmlMatcher.matches(file);
			}
		}
		
		return isSpring;
	}
}
