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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.jboss.mapper.eclipse.Activator;

/**
 *
 */
public class Util {

    /**
     *
     */
    public static final String MAIN_PATH = "src/main/";

    /**
     *
     */
    public static final String RESOURCES_PATH = MAIN_PATH + "resources/";

    /**
     *
     */
    public static final Image ADD_IMAGE = PlatformUI.getWorkbench().getSharedImages()
            .getImage(ISharedImages.IMG_OBJ_ADD);

    /**
     *
     */
    public static final Image ADD_OPERATION_IMAGE = Activator.imageDescriptor("addOperation16.gif")
            .createImage();

    /**
     *
     */
    public static final ImageDescriptor ADD_OVERLAY_IMAGE_DESCRIPTOR = Activator
            .imageDescriptor("addOverlay.gif");

    /**
     *
     */
    public static final Image CHANGE_IMAGE = Activator.imageDescriptor("change16.gif")
            .createImage();

    /**
     *
     */
    public static final Image DELETE_IMAGE = PlatformUI.getWorkbench().getSharedImages()
            .getImage(ISharedImages.IMG_ETOOL_DELETE);

    /**
     *
     */
    public static final Image LITERAL_IMAGE = Activator.imageDescriptor("literal16.gif")
            .createImage();

    /**
     *
     */
    public static final Image RIGHT_ARROW_IMAGE = Activator.imageDescriptor("rightArrow16.gif")
            .createImage();

    private static void populateClasses(final Shell shell,
            final IParent parent,
            final List<IType> types,
            final Filter filter) {
        try {
            for (final IJavaElement element : parent.getChildren()) {
                if (element instanceof IType) {
                    final IType type = (IType) element;
                    if (type.isClass() && type.isStructureKnown() && !type.isAnonymous()
                            && !type.isLocal()
                            && !Flags.isAbstract(type.getFlags())
                            && Flags.isPublic(type.getFlags())
                            && (filter == null || filter.accept(type))) {
                        types.add(type);
                    }
                } else if (element instanceof IParent
                        && !element.getPath().toString().contains("/test/")
                        && (!(element instanceof IPackageFragmentRoot)
                        || !((IPackageFragmentRoot) element).isExternal())) {
                    populateClasses(shell, (IParent) element, types, filter);
                }
            }
        } catch (final JavaModelException e) {
            Activator.error(e);
        }
    }

    private static void populateResources(final Shell shell,
            final IContainer container,
            final List<IResource> resources) {
        try {
            for (final IResource resource : container.members()) {
                if (resource instanceof IContainer) {
                    populateResources(shell, (IContainer) resource, resources);
                } else {
                    resources.add(resource);
                }
            }
        } catch (final Exception e) {
            Activator.error(e);
        }
    }

    /**
     * @param shell
     * @param project
     * @return the selected file
     */
    public static IType selectClass(final Shell shell,
            final IProject project) {
        return selectClass(shell, project, null);
    }

    /**
     * @param shell
     * @param project
     * @param filter
     * @return the selected file
     */
    public static IType selectClass(final Shell shell,
            final IProject project,
            final Filter filter) {
        final int flags = JavaElementLabelProvider.SHOW_DEFAULT
                | JavaElementLabelProvider.SHOW_POST_QUALIFIED
                | JavaElementLabelProvider.SHOW_ROOT;
        final ElementListSelectionDialog dlg =
                new ElementListSelectionDialog(shell, new JavaElementLabelProvider(flags));
        dlg.setTitle("Select Custom Operation(s) Class");
        dlg.setMessage("Select a custom operation(s) class");
        dlg.setMatchEmptyString(true);
        dlg.setHelpAvailable(false);
        final List<IType> types = new ArrayList<>();
        populateClasses(shell, JavaCore.create(project), types, filter);
        dlg.setElements(types.toArray());
        return (dlg.open() == Window.OK) ? (IType) dlg.getFirstResult() : null;
    }

    /**
     * @param shell
     * @param project
     * @param schemaType
     * @return the selected file
     */
    public static String selectFile(final Shell shell,
            final IProject project,
            final String schemaType) {
        final int flags = JavaElementLabelProvider.SHOW_DEFAULT
                | JavaElementLabelProvider.SHOW_POST_QUALIFIED
                | JavaElementLabelProvider.SHOW_ROOT;
        final ElementListSelectionDialog dlg =
                new ElementListSelectionDialog(shell, new JavaElementLabelProvider(flags) {

                    @Override
                    public String getText(final Object element) {
                        return super.getText(element) + " - "
                                + ((IResource) element).getParent().getFullPath().makeRelative();
                    }
                });
        dlg.setTitle("Select " + schemaType);
        dlg.setMessage("Select the " + schemaType + " file for the transformation");
        dlg.setMatchEmptyString(true);
        dlg.setHelpAvailable(false);
        final List<IResource> resources = new ArrayList<>();
        populateResources(shell, project, resources);
        dlg.setElements(resources.toArray());
        if (dlg.open() == Window.OK) {
            return ((IFile) dlg.getFirstResult()).getProjectRelativePath().toString();
        }
        return null;
    }

    /**
     * @param shell
     * @param extension
     * @param project
     * @return The selected resource
     */
    public static IResource selectResourceFromWorkspace(final Shell shell,
            final String extension,
            final IProject project) {
        IJavaProject javaProject = null;
        if (project != null) {
            javaProject = JavaCore.create(project);
        }
        ClasspathResourceSelectionDialog dialog = null;
        if (javaProject == null) {
            dialog =
                    new ClasspathResourceSelectionDialog(shell, ResourcesPlugin.getWorkspace()
                            .getRoot(), "xml"); //$NON-NLS-1$
        } else {
            dialog = new ClasspathResourceSelectionDialog(shell, javaProject.getProject(), "xml"); //$NON-NLS-1$
        }
        dialog.setTitle("Select Camel XML File from Project");
        dialog.setInitialPattern("*.xml"); //$NON-NLS-1$
        dialog.open();
        final Object[] result = dialog.getResult();
        if (result == null || result.length == 0 || !(result[0] instanceof IFile)) {
            return null;
        }
        final IFile resource = (IFile) result[0];
        return resource;
    }

    private Util() {}

    /**
     * Provides users with the ability to further filter which classes appear in
     * the dialog shown by {@link Util#selectClass(Shell, IProject, Filter)}
     */
    public static interface Filter {

        /**
         * @param type
         * @return <code>true</code> if the supplied type should appear in the
         *         dialog shown by
         *         {@link Util#selectClass(Shell, IProject, Filter)}
         */
        boolean accept(IType type);
    }
}
