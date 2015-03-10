/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others. 
 * All rights reserved. This program and the accompanying materials are 
 * made available under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.mapper.eclipse.internal.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 *
 */
public final class JavaUtil {

    /**
     * Creates a ClassLoader using the project's build path.
     *
     * @param javaProject the Java project.
     * @param parentClassLoader the parent class loader, may be null.
     *
     * @return a new ClassLoader based on the project's build path.
     *
     * @throws Exception if something goes wrong.
     */
    public static ClassLoader getProjectClassLoader(IJavaProject javaProject,
            ClassLoader parentClassLoader)
            throws Exception {
        IProject project = javaProject.getProject();
        IWorkspaceRoot root = project.getWorkspace().getRoot();
        List<URL> urls = new ArrayList<>();
        urls.add(new File(project.getLocation()
                + "/" + javaProject.getOutputLocation().removeFirstSegments(1)
                + "/") //$NON-NLS-1$ //$NON-NLS-2$
                .toURI().toURL());
        for (IClasspathEntry classpathEntry : javaProject.getResolvedClasspath(true)) {
            if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
                IPath projectPath = classpathEntry.getPath();
                IProject otherProject = root.getProject(projectPath.segment(0));
                IJavaProject otherJavaProject = JavaCore.create(otherProject);
                urls.add(new File(otherProject.getLocation() + "/" //$NON-NLS-1$
                        + otherJavaProject.getOutputLocation().removeFirstSegments(1)
                        + "/").toURI().toURL()); //$NON-NLS-1$
            } else if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
                urls.add(new File(classpathEntry.getPath().toOSString()).toURI().toURL());
            }
        }
        if (parentClassLoader == null) {
            return new URLClassLoader(urls.toArray(new URL[urls.size()]));
        }
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), parentClassLoader);
    }

    /**
     * Create a new JavaUtil.
     */
    private JavaUtil() {}

    /**
     * Returns the first non-empty package in the project's first source folder.
     *
     * @param project the Java project
     *
     * @return the first non-empty package; may be null.
     */
    public static IJavaElement getInitialPackageForProject(IJavaProject project) {
        if (project == null) {
            return null;
        }
        try {
            IPackageFragmentRoot sourceRoot = getFirstJavaSourceRoot(project);
            if (sourceRoot == null) {
                return project;
            }
            IJavaElement[] packages = sourceRoot.getChildren();
            IJavaElement element = sourceRoot;
            for (int i = 0; i < packages.length; i++) {
                IPackageFragment frag = (IPackageFragment) packages[i];
                element = frag;
                if (!frag.isDefaultPackage()
                        && (!frag.hasSubpackages() || frag.containsJavaResources())) {
                    element = frag;
                    break;
                }
            }
            return element;
        } catch (JavaModelException e) {
            return project;
        }
    }

    /**
     * Returns the first resource folder in the project. If the project is a
     * maven project, the first resource folder configured will be used.
     *
     * @param project the Java project
     *
     * @return the resource root; may be null.
     */
    public static IResource getFirstResourceRoot(IJavaProject project) {
        if (project == null) {
            return null;
        }
        try {
            IResource sourceRoot = null;
            for (IPackageFragmentRoot frag : project.getPackageFragmentRoots()) {
                if (frag.getKind() == IPackageFragmentRoot.K_SOURCE) {
                    sourceRoot = frag.getUnderlyingResource();
                    break;
                }
            }
            return sourceRoot;
        } catch (JavaModelException e) {
            return null;
        }
    }

    /**
     * @param project
     * @return the root package for the supplied project
     */
    public static IPackageFragmentRoot getFirstJavaSourceRoot(IJavaProject project) {
        if (project == null) {
            return null;
        }
        try {
            IPackageFragmentRoot sourceRoot = null;
            for (IPackageFragmentRoot frag : project.getPackageFragmentRoots()) {
                if (frag.getKind() == IPackageFragmentRoot.K_SOURCE) {
                    sourceRoot = frag;
                    break;
                }
            }
            return sourceRoot;
        } catch (JavaModelException e) {
            return null;
        }
    }

    /**
     * @param javaProject
     * @param folder
     * @return <code>true</code> if the supplied folder was successfully added
     *         to the supplied Java project
     */
    public static boolean addFolderToProjectClasspath(IJavaProject javaProject, IResource folder) {
        IClasspathEntry[] entries;
        try {
            entries = javaProject.getRawClasspath();
            IClasspathEntry[] newEntries = new IClasspathEntry[entries.length + 1];
            System.arraycopy(entries, 0, newEntries, 0, entries.length);

            IPath srcPath = javaProject.getPath().append(folder.getProjectRelativePath());
            IClasspathEntry srcEntry = JavaCore.newSourceEntry(srcPath, null);

            newEntries[entries.length] = JavaCore.newSourceEntry(srcEntry.getPath());
            javaProject.setRawClasspath(newEntries, null);
            return true;
        } catch (JavaModelException e) {
            return false;
        }
    }

    /**
     * @param javaProject
     * @param folder
     * @return <code>true</code> if the supplied folder is part of the supplied
     *         Java project's classpath
     */
    public static boolean findFolderOnProjectClasspath(IJavaProject javaProject, IResource folder) {
        IClasspathEntry[] entries;
        try {
            IPath srcPath = javaProject.getPath().append(folder.getProjectRelativePath());
            entries = javaProject.getRawClasspath();
            for (IClasspathEntry entry : entries) {
                if (entry.getPath().equals(srcPath)) {
                    return true;
                }
            }
            return false;
        } catch (JavaModelException e) {
            return false;
        }
    }

    /**
     * @param context
     * @return the Java source and runtime compliance levels for the project
     *         containing the supplied Java element
     */
    public static String[] getSourceComplianceLevels(IJavaElement context) {
        if (context != null) {
            IJavaProject javaProject = context.getJavaProject();
            if (javaProject != null) {
                return new String[] {
                        javaProject.getOption(JavaCore.COMPILER_SOURCE, true),
                        javaProject.getOption(JavaCore.COMPILER_COMPLIANCE, true)
                };
            }
        }
        return new String[] {
                JavaCore.getOption(JavaCore.COMPILER_SOURCE),
                JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE)
        };
    }

    /**
     * @param name
     * @param context
     * @return a status indicating whether the supplied package name is valid
     *         for the supplied Java element
     */
    public static IStatus validatePackageName(String name, IJavaElement context) {
        String[] sourceComplianceLevels = getSourceComplianceLevels(context);
        return JavaConventions.validatePackageName(name, sourceComplianceLevels[0],
                sourceComplianceLevels[1]);
    }

    /**
     * @param name
     * @param context
     * @return a status indicating whether the supplied class name is valid for
     *         the supplied Java element
     */
    public static IStatus validateClassFileName(String name, IJavaElement context) {
        String[] sourceComplianceLevels = getSourceComplianceLevels(context);
        return JavaConventions.validateJavaTypeName(name, sourceComplianceLevels[0],
                sourceComplianceLevels[1]);
    }

    /**
     * Returns the resource path relative to its containing
     * IPackageFragmentRoot. If the resource is not located within a Java source
     * directory, the project name is stripped from the path.
     * 
     * @param resource the resource.
     * 
     * @return the relative path.
     */
    public static IPath getJavaPathForResource(final IResource resource) {
        if (resource == null || resource.getType() == IResource.PROJECT
                || resource.getType() == IResource.ROOT) {
            return null;
        }
        IJavaProject project = JavaCore.create(resource.getProject());
        if (project == null) {
            // just remove the project segment.
            return resource.getFullPath().removeFirstSegments(1);
        }
        IResource container = resource;
        if (container.getType() == IResource.FILE) {
            container = container.getParent();
        }
        IJavaElement element = null;
        for (; element == null && container != null; container = container.getParent()) {
            element = JavaCore.create(container, project);
        }
        if (element == null) {
            return resource.getFullPath().removeFirstSegments(1);
        } else if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
            return resource.getFullPath().makeRelativeTo(element.getParent().getPath());
        }
        return resource.getFullPath().makeRelativeTo(element.getPath());
    }
}
