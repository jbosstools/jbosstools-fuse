/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;

/**
 * @author lhein
 *
 */
public class MavenUtils {

	/**
     * checks if we need to add a maven dependency for the chosen component
     * and inserts it into the pom.xml if needed
     */
    public static void updateMavenDependencies(List<org.fusesource.ide.camel.model.service.core.catalog.Dependency> compDeps) throws CoreException {
        RiderDesignEditor editor = Activator.getDiagramEditor();
        if (editor == null) {
            Activator.getLogger().error("Unable to add component dependencies because Editor instance can't be determined.");
            return;
        }
        
        IProject project = editor.getCamelContextFile().getProject();
        if (project == null) {
            Activator.getLogger().error("Unable to add component dependencies because selected project can't be determined.");
            return;
        }
        
        IPath pomPathValue = project.getProject().getRawLocation() != null ? project.getProject().getRawLocation().append("pom.xml") : ResourcesPlugin.getWorkspace().getRoot().getLocation().append(project.getFullPath().append("pom.xml"));
        String pomPath = pomPathValue.toOSString();
        final File pomFile = new File(pomPath);
        final Model model = MavenPlugin.getMaven().readModel(pomFile);

        // then check if component dependency is already a dep
        ArrayList<org.fusesource.ide.camel.model.service.core.catalog.Dependency> missingDeps = new ArrayList<org.fusesource.ide.camel.model.service.core.catalog.Dependency>();
        List<Dependency> deps = model.getDependencies();
        for (org.fusesource.ide.camel.model.service.core.catalog.Dependency conDep : compDeps) {
            boolean found = false;
            for (Dependency pomDep : deps) {
                if (pomDep.getGroupId().equalsIgnoreCase(conDep.getGroupId()) &&
                    pomDep.getArtifactId().equalsIgnoreCase(conDep.getArtifactId())) {
                    // check for correct version
                    if (pomDep.getVersion().equalsIgnoreCase(conDep.getVersion()) == false) {
                        // not the correct version - change it to fit
                        pomDep.setVersion(conDep.getVersion());
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                missingDeps.add(conDep);
            }
        }

        for (org.fusesource.ide.camel.model.service.core.catalog.Dependency missDep : missingDeps) {
            Dependency dep = new Dependency();
            dep.setGroupId(missDep.getGroupId());
            dep.setArtifactId(missDep.getArtifactId());
            dep.setVersion(missDep.getVersion());
            model.addDependency(dep);
        }
        
        if (missingDeps.size()>0) {
            OutputStream os = null;
            try {
                os = new BufferedOutputStream(new FileOutputStream(pomFile));
                MavenPlugin.getMaven().writeModel(model, os);
                IFile pomIFile = project.getProject().getFile("pom.xml");
                if (pomIFile != null){
                    pomIFile.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
                }
            } catch (Exception ex) {
                Activator.getLogger().error(ex);
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    Activator.getLogger().error(e);
                }
            }
        }
    }
}
