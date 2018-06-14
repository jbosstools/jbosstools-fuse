/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.fusesource.ide.foundation.core.contenttype.BlueprintXmlMatchingStrategy;
import org.fusesource.ide.foundation.core.contenttype.SpringXmlMatchingStrategy;
import org.fusesource.ide.foundation.core.contenttype.XmlMatchingStrategySupport;

/**
 * A couple of utilities to check to see if a given Camel configuration file
 * is a Spring one or a Blueprint one.
 *
 */
public final class CamelFileTypeHelper {

    private static XmlMatchingStrategySupport blueprintXmlMatcher = new BlueprintXmlMatchingStrategy();
    private static XmlMatchingStrategySupport springXmlMatcher = new SpringXmlMatchingStrategy();

    /**
     * @param project
     * @param filePath
     * @return <code>true</code> if the file with the supplied filePath is a Blueprint file
     */
    public static boolean isBlueprintFile(IProject project, String filePath) {
        return fileMatches(project, filePath, blueprintXmlMatcher);
    }

    /**
     * @param project
     * @param filePath
     * @return <code>true</code> if the file with the supplied filePath is a Spring file
     */
    public static boolean isSpringFile(IProject project, String filePath) {
        return fileMatches(project, filePath, springXmlMatcher);
    }

    /**
     * @param project
     * @param filePath
     * @return <code>true</code> if the file with the supplied filePath is a Spring or Blueprint file
     */
    public static boolean isSupportedCamelFile(IProject project, String filePath) {
        boolean isSpring = isSpringFile(project, filePath);
        boolean isBlueprint = isBlueprintFile(project, filePath);
        return isSpring || isBlueprint;
    }

    private static boolean fileMatches(IProject project, String filePath, XmlMatchingStrategySupport matcher) {
        boolean matches = false;

        if (filePath != null && filePath.trim().length() > 0) {
            String rawPath;
            if (filePath.startsWith("file:")) { //$NON-NLS-1$
                rawPath = filePath.substring(5);
            } else {
                IPath wholePath = project.getLocation().append(filePath);
                rawPath = wholePath.toPortableString();
            }
            Path fp = new Path(rawPath);
            java.io.File nf = new java.io.File(fp.toOSString());
            if (nf.exists() && nf.isFile()) {
                // file exists, now check if its blueprint or spring
                IWorkspace workspace = ResourcesPlugin.getWorkspace();
                IPath location = Path. fromOSString(nf.getAbsolutePath());
                IFile file = workspace .getRoot().getFileForLocation(location);
                matches = matcher.matches(file);
            }
        }

        return matches;
    }

    private CamelFileTypeHelper() {
    }
}
