/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others. 
 * All rights reserved. This program and the accompanying materials are 
 * made available under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.dozer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.fusesource.ide.commons.contenttype.XmlMatchingStrategySupport;

/**
 * A couple of utilities to check to see if a given XML configuration file
 * is a Dozer transformation file.
 *
 */
@SuppressWarnings("restriction")
public final class DozerFileTypeHelper {

    private static XmlMatchingStrategySupport dozerXmlMatcher = new DozerXmlMatchingStrategy();

    /**
     * checks if the given file is a dozer file or not
     * @param filePath
     * @return
     */
    public static boolean isDozerFile(IProject project, String filePath) {
        return fileMatches(project, filePath, dozerXmlMatcher);
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
                IWorkspace workspace = ResourcesPlugin.getWorkspace();
                IPath location = Path. fromOSString(nf.getAbsolutePath());
                IFile file = workspace .getRoot().getFileForLocation(location);
                matches = matcher.matches(file);
            }
        }

        return matches;
    }

}
