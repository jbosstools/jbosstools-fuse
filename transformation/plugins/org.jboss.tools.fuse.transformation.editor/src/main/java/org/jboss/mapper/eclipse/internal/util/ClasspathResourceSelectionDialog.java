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

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;

/**
 * Allows user to select a resource on the project's classpath.
 *
 * @author bfitzpat
 * @author Rob Cernich
 */
class ClasspathResourceSelectionDialog extends FilteredResourcesSelectionDialog {

    Set<String> fileExtensions;
    IJavaModel fJavaModel;

    /**
     * Create a new ClasspathResourceSelectionDialog.
     *
     * @param parentShell the parent shell
     * @param container the root container
     * @param title
     */
    ClasspathResourceSelectionDialog(Shell parentShell, IContainer container, String title) {
        this(parentShell, container, Collections.<String>emptySet(), title);
    }

    /**
     * Create a new ClasspathResourceSelectionDialog.
     *
     * @param parentShell the parent shell
     * @param container the root container
     * @param fileExtension the type of files to display; may be null
     * @param title
     */
    ClasspathResourceSelectionDialog(Shell parentShell, IContainer container, String fileExtension,
            String title) {
        this(parentShell, container, fileExtension == null ? Collections.<String>emptySet()
                : Collections
                        .singleton(fileExtension), title);
    }

    /**
     * Create a new ClasspathResourceSelectionDialog.
     *
     * @param parentShell the parent shell
     * @param container the root container
     * @param fileExtensions the types of files to display; may be null
     * @param title
     */
    ClasspathResourceSelectionDialog(Shell parentShell, IContainer container,
            Set<String> fileExtensions, String title) {
        super(parentShell, false, container, IResource.FILE);
        fJavaModel = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
        this.fileExtensions = fileExtensions == null ? Collections.<String>emptySet() : fileExtensions;
        setTitle(title);
    }

    @Override
    protected ItemsFilter createFilter() {
        return new ClasspathResourceFilter();
    }

    class ClasspathResourceFilter extends ResourceFilter {

        @Override
        public boolean matchItem(Object item) {
            IResource resource = (IResource) item;
            return super.matchItem(item)
                    && (fileExtensions == null || fileExtensions.isEmpty() || fileExtensions
                            .contains(resource
                                    .getFullPath().getFileExtension())) && select(resource);
        }

        private boolean isParentOnClassPath(IJavaProject javaProject, IResource resource) {
            boolean flag = false;
            while (!flag && resource.getParent() != null) {
                flag = javaProject.isOnClasspath(resource);
                if (!flag) {
                    resource = resource.getParent();
                } else {
                    return flag;
                }
            }
            return flag;
        }

        /**
         * This is the orignal <code>select</code> method. Since
         * <code>GotoResourceDialog</code> needs to extend
         * <code>FilteredResourcesSelectionDialog</code> result of this method
         * must be combined with the <code>matchItem</code> method from super
         * class (<code>ResourceFilter</code>).
         *
         * @param resource A resource
         * @return <code>true</code> if item matches against given conditions
         *         <code>false</code> otherwise
         */
        private boolean select(IResource resource) {
            IProject project = resource.getProject();
            IJavaProject javaProject = JavaCore.create(project);
            try {
                return (javaProject != null && isParentOnClassPath(javaProject, resource))
                        || (project.getNature(JavaCore.NATURE_ID) != null && fJavaModel
                                .contains(resource));
            } catch (CoreException e) {
                return false;
            }
        }

        @Override
        public boolean equalsFilter(ItemsFilter filter) {
            return filter instanceof ClasspathResourceFilter && super.equalsFilter(filter);
        }
    }

}
