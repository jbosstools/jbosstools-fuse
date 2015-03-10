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
import org.jboss.mapper.camel.CamelConfigBuilder;

/*
 * Allows user to select a Camel resource on the project's classpath. TODO -
 * work in progress - the CamelResourceFilter implementation is VERY resource
 * intensive has to be a better way to introspect the file to see whether it's a
 * valid camel route file or not
 */
class CamelResourceClasspathSelectionDialog extends FilteredResourcesSelectionDialog {

    Set<String> fileExtensions;
    IJavaModel fJavaModel;

    /**
     * Create a new ClasspathResourceSelectionDialog.
     * 
     * @param parentShell the parent shell
     * @param container the root container
     * @param fileExtensions the types of files to display; may be null
     * @param title
     */
    CamelResourceClasspathSelectionDialog(final Shell parentShell,
            final IContainer container,
            final Set<String> fileExtensions,
            final String title) {
        super(parentShell, false, container, IResource.FILE);
        this.fJavaModel = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
        this.fileExtensions = fileExtensions == null ? Collections.<String>emptySet() : fileExtensions;
        setTitle(title);
    }

    /**
     * Create a new ClasspathResourceSelectionDialog.
     * 
     * @param parentShell the parent shell
     * @param container the root container
     * @param title
     */
    CamelResourceClasspathSelectionDialog(final Shell parentShell,
            final IContainer container,
            final String title) {
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
    CamelResourceClasspathSelectionDialog(final Shell parentShell,
            final IContainer container,
            final String fileExtension,
            final String title) {
        this(parentShell, container, fileExtension == null ? Collections.<String>emptySet()
                : Collections
                        .singleton(fileExtension), title);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog#createFilter()
     */
    @Override
    protected ItemsFilter createFilter() {
        return new CamelResourceFilter();
    }

    class CamelResourceFilter extends ResourceFilter {

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog.
         * ResourceFilter
         * #equalsFilter(org.eclipse.ui.dialogs.FilteredItemsSelectionDialog
         * .ItemsFilter)
         */
        @Override
        public boolean equalsFilter(final ItemsFilter filter) {
            return filter instanceof CamelResourceFilter && super.equalsFilter(filter);
        }

        private boolean fileIsSupportedCamel(final Object item) {
            try {
                final IResource resource = (IResource) item;
                final File testFile = new File(resource.getLocationURI());
                System.out.println("Testing " + testFile.toString());
                if (testFile.exists()) {
                    CamelConfigBuilder.loadConfig(testFile);
                    return true;
                }
            } catch (final Exception e) {
                // ignore
            }
            return false;
        }

        private boolean isParentOnClassPath(final IJavaProject javaProject,
                IResource resource) {
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

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog.
         * ResourceFilter#matchItem(java.lang.Object)
         */
        @Override
        public boolean matchItem(final Object item) {
            final IResource resource = (IResource) item;
            return super.matchItem(item)
                    && (fileExtensions == null || fileExtensions.isEmpty() || fileExtensions
                            .contains(resource
                                    .getFullPath().getFileExtension())) && select(resource);
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
        private boolean select(final IResource resource) {
            final IProject project = resource.getProject();
            final IJavaProject javaProject = JavaCore.create(project);
            try {
                final boolean isSupported = fileIsSupportedCamel(resource);
                return (javaProject != null && isParentOnClassPath(javaProject, resource) && isSupported)
                        || (project.getNature(JavaCore.NATURE_ID) != null
                                && fJavaModel.contains(resource) && isSupported);
            } catch (final CoreException e) {
                return false;
            }
        }
    }

}
