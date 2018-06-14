/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.dozer;

import java.io.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.fusesource.ide.foundation.core.contenttype.XmlMatchingStrategySupport;

/**
 * A couple of utilities to check to see if a given XML configuration file
 * is a Dozer transformation file.
 */
public final class DozerFileTypeHelper {

    private static XmlMatchingStrategySupport dozerXmlMatcher = new DozerXmlMatchingStrategy();

    /**
     * @param project
     * @param filePath
     * @return <code>true</code> if the file with the supplied filePath is a Dozer configuration file
     */
    public static boolean isDozerFile(IProject project, String filePath) {
        return fileMatches(project, filePath, dozerXmlMatcher);
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
            File nf = new File(fp.toOSString());
            if (nf.exists() && nf.isFile()) {
                IWorkspace workspace = ResourcesPlugin.getWorkspace();
                IPath location = Path.fromOSString(nf.getAbsolutePath());
                IFile file = workspace.getRoot().getFileForLocation(location);
                matches = matcher.matches(file);
            }
        }

        return matches;
    }
}
