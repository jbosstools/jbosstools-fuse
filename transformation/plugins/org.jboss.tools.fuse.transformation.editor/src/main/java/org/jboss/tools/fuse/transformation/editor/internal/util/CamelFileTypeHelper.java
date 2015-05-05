/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others. 
 * All rights reserved. This program and the accompanying materials are 
 * made available under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.fusesource.ide.commons.contenttype.BlueprintXmlMatchingStrategy;
import org.fusesource.ide.commons.contenttype.SpringXmlMatchingStrategy;
import org.fusesource.ide.commons.contenttype.XmlMatchingStrategySupport;

/**
 * A couple of utilities to check to see if a given Camel configuration file
 * is a Spring one or a Blueprint one.
 *
 */
@SuppressWarnings("restriction")
public final class CamelFileTypeHelper {

    private static XmlMatchingStrategySupport blueprintXmlMatcher = new BlueprintXmlMatchingStrategy();
    private static XmlMatchingStrategySupport springXmlMatcher = new SpringXmlMatchingStrategy();

    /**
     * checks if the given file is a blueprint file or not
     * @param filePath
     * @return
     */
    public static boolean isBlueprintFile(IProject project, String filePath) {
        return fileMatches(project, filePath, blueprintXmlMatcher);
    }

    /**
     * checks if the given file is a spring file or not
     * @param filePath
     * @return
     */
    public static boolean isSpringFile(IProject project, String filePath) {
        return fileMatches(project, filePath, springXmlMatcher);
    }
    
    /**
     * checks if the given file is a spring or blueprint file or not
     * @param filePath
     * @return
     */
    public static boolean isSupportedCamelFile(IProject project, String filePath) {
        boolean isSpring = isSpringFile(project, filePath);
        boolean isBlueprint = isBlueprintFile(project, filePath);
        if (isSpring || isBlueprint) {
            return true;
        }
        return false;
    }

    private static boolean fileMatches(IProject project, String filePath, XmlMatchingStrategySupport matcher) {
        boolean matches = false;
        
        if (filePath != null && filePath.trim().length() > 0) {
            String rawPath = null;
            if (filePath.startsWith("file:")) {
                rawPath = filePath.substring(5);
            } else {
                IPath wholePath = project.getLocation().append(filePath);
                rawPath = wholePath.toPortableString();
            }
            Path fp = new Path(rawPath);
            java.io.File nf = new java.io.File(fp.toOSString());
            if (nf.exists() && nf.isFile()) {
                // file exists, now check if its blueprint or spring
                IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
                        fp.makeRelativeTo(ResourcesPlugin.getWorkspace().getRoot().getLocation()));
                matches = matcher.matches(file);
            }
        }
        
        return matches;
    }

}
