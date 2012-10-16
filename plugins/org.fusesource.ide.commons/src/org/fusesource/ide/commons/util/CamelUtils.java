package org.fusesource.ide.commons.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
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
		boolean isBlueprint = false;
		
		if (filePath != null && filePath.trim().length()>0) {
			String rawPath = null;
			if (filePath.startsWith("file:")) {
				rawPath = filePath.substring(5);
			} else {
				rawPath = filePath;
			}
			Path f = new Path(rawPath);
			java.io.File nf = new java.io.File(f.toOSString());
			if (nf.exists() && nf.isFile()) {
				// file exists, now check if its blueprint or spring
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(f.makeRelativeTo(ResourcesPlugin.getWorkspace().getRoot().getLocation()));
				isBlueprint = camelXmlMatcher.matches(file);
			}
		}
		
		return isBlueprint;
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
			Path f = new Path(rawPath);
			java.io.File nf = new java.io.File(f.toOSString());
			if (nf.exists() && nf.isFile()) {
				// file exists, now check if its blueprint or spring
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(f.makeRelativeTo(ResourcesPlugin.getWorkspace().getRoot().getLocation()));
				isBlueprint = blueprintXmlMatcher.matches(file);
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
			Path f = new Path(rawPath);
			java.io.File nf = new java.io.File(f.toOSString());
			if (nf.exists() && nf.isFile()) {
				// file exists, now check if its blueprint or spring
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(f.makeRelativeTo(ResourcesPlugin.getWorkspace().getRoot().getLocation()));
				isSpring = springXmlMatcher.matches(file);
			}
		}
		
		return isSpring;
	}
}
