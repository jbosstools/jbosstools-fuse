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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.fusesource.ide.camel.model.catalog.Dependency;
import org.jboss.tools.fuse.transformation.Variable;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.model.Model;

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
     * @return the object being dragged
     */
    public static Object draggedObject() {
        return ((IStructuredSelection) LocalSelectionTransfer.getTransfer()
                                                             .getSelection())
                                                             .getFirstElement();
    }

    /**
     * @param config
     * @return <code>true</code> if the object being dragged is a valid source object
     */
    public static boolean draggingFromValidSource(final TransformationConfig config) {
        final Object object = draggedObject();
        if (object instanceof Variable) return true;
        if (!(object instanceof Model)) return false;
        final Model model = (Model)object;
        if (type(model)) return false;
        return config.root(model).equals(config.getSourceModel());
    }

    /**
     * @param config
     * @return <code>true</code> if the object being dragged is a valid target object
     */
    public static boolean draggingFromValidTarget(final TransformationConfig config) {
        final Object object = draggedObject();
        if (!(object instanceof Model)) return false;
        final Model model = (Model)object;
        if (type(model)) return false;
        return config.root(model).equals(config.getTargetModel());
    }

    /**
     * @return a paint listener that paints a rounded border around a control
     */
    public static final PaintListener ovalBorderPainter() {
        return new PaintListener() {

            @Override
            public void paintControl(final PaintEvent event) {
                final Color oldForeground = event.gc.getForeground();
                event.gc.setForeground(event.display.getSystemColor(SWT.COLOR_GRAY));
                final Rectangle bounds = ((Control) event.widget).getBounds();
                event.gc.drawRoundRectangle(0, 0,
                                            bounds.width - 1, bounds.height - 1,
                                            bounds.height, bounds.height);
                event.gc.setForeground(oldForeground);
            }
        };
    }

    private static void populateClasses(final Shell shell,
                                        final IParent parent,
                                        final List<IType> types,
                                        final Filter filter) {
        try {
            for (final IJavaElement element : parent.getChildren()) {
                if (element instanceof IType) {
                    final IType type = (IType) element;
                    if (type.isClass() && type.isStructureKnown() && !type.isAnonymous()
                        && !type.isLocal() && !Flags.isAbstract(type.getFlags())
                        && Flags.isPublic(type.getFlags())
                        && (filter == null || filter.accept(type)))
                        types.add(type);
                } else if (element instanceof IParent
                           && !element.getPath().toString().contains("/test/")
                           && (!(element instanceof IPackageFragmentRoot)
                                 || !((IPackageFragmentRoot) element).isExternal()))
                    populateClasses(shell, (IParent) element, types, filter);
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
                if (resource instanceof IContainer)
                    populateResources(shell, (IContainer) resource, resources);
                else resources.add(resource);
            }
        } catch (final Exception e) {
            Activator.error(e);
        }
    }

    /**
     * @param arc
     * @param background
     * @return A paint listener that paints a border around a control. Useful
     *         for borders around labels
     */
    public static final PaintListener roundedRectanglePainter(final int arc,
                                                              final Color background) {
        return new PaintListener() {

            @Override
            public void paintControl(final PaintEvent event) {
                event.gc.setBackground(background);
                final Rectangle bounds = ((Composite) event.widget).getClientArea();
                event.gc.fillRoundRectangle(0, 0, bounds.width - 1, bounds.height - 1, arc, arc);
            }
        };
    }

    /**
     * @param shell
     * @param extension
     * @param project
     * @return The selected resource
     */
    public static IResource selectCamelResourceFromWorkspace(final Shell shell,
                                                        final IProject project) {
        IJavaProject javaProject = null;
        if (project != null) javaProject = JavaCore.create(project);
        CamelResourceClasspathSelectionDialog dialog;
        if (javaProject == null)
            dialog = new CamelResourceClasspathSelectionDialog(shell,
                                                          ResourcesPlugin.getWorkspace().getRoot(),
                                                          "xml");
        else dialog = new CamelResourceClasspathSelectionDialog(shell, javaProject.getProject(), "xml");
        dialog.setTitle("Select Camel XML File from Project");
        dialog.setInitialPattern("*.xml"); //$NON-NLS-1$
        dialog.open();
        final Object[] result = dialog.getResult();
        if (result == null || result.length == 0 || !(result[0] instanceof IFile)) return null;
        return (IFile) result[0];
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
        return dlg.open() == Window.OK ? (IType) dlg.getFirstResult() : null;
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
        return dlg.open() == Window.OK
               ? ((IFile) dlg.getFirstResult()).getProjectRelativePath().toString() : null;
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
        if (project != null) javaProject = JavaCore.create(project);
        ClasspathResourceSelectionDialog dialog;
        if (javaProject == null)
            dialog = new ClasspathResourceSelectionDialog(shell,
                                                          ResourcesPlugin.getWorkspace().getRoot(),
                                                          "xml");
        else dialog = new ClasspathResourceSelectionDialog(shell, javaProject.getProject(), "xml");
        dialog.setTitle("Select Camel XML File from Project");
        dialog.setInitialPattern("*.xml"); //$NON-NLS-1$
        dialog.open();
        final Object[] result = dialog.getResult();
        if (result == null || result.length == 0 || !(result[0] instanceof IFile)) return null;
        return (IFile) result[0];
    }

    public static boolean type(final Model model) {
        return (!model.isCollection() && !model.getChildren().isEmpty());
    }

    public static void updateMavenDependencies(final List<Dependency> dependencies,
                                               final IProject project) throws CoreException {
        final IFile pomIFile = project.getProject().getFile("pom.xml");
        final File pomFile = new File(pomIFile.getLocationURI());
        final org.apache.maven.model.Model pom = MavenPlugin.getMaven().readModel(pomFile);

        // Check if dependency already in the pom
        final List<Dependency> missingDependencies = new ArrayList<>();
        for (final Dependency dependency : dependencies) {
            boolean found = false;
            for (final org.apache.maven.model.Dependency pomDependency : pom.getDependencies()) {
                if (pomDependency.getGroupId().equalsIgnoreCase(dependency.getGroupId()) &&
                    pomDependency.getArtifactId().equalsIgnoreCase(dependency.getArtifactId())) {
                    // check for correct version
                    if (!pomDependency.getVersion().equalsIgnoreCase(dependency.getVersion()))
                        pomDependency.setVersion(dependency.getVersion());
                    found = true;
                    break;
                }
            }
            if (!found) missingDependencies.add(dependency);
        }

        for (final Dependency dependency : missingDependencies) {
            final org.apache.maven.model.Dependency pomDependency =
                new org.apache.maven.model.Dependency();
            pomDependency.setGroupId(dependency.getGroupId());
            pomDependency.setArtifactId(dependency.getArtifactId());
            pomDependency.setVersion(dependency.getVersion());
            pom.addDependency(pomDependency);
        }

        if (!missingDependencies.isEmpty()) {
            try (final OutputStream stream =
                     new BufferedOutputStream(new FileOutputStream(pomFile))) {
                MavenPlugin.getMaven().writeModel(pom, stream);
                pomIFile.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
            } catch (final Exception e) {
                Activator.error(e);
            }
        }
    }

    public static boolean validSourceAndTarget(final Object source,
                                               final Object target,
                                               final TransformationConfig config) {
        if (!(target instanceof Model)) return false;
        final Model targetModel = (Model)target;
        if (targetModel.getParent() == null || Util.type(targetModel)) return false;
        if (source instanceof Model) {
            final Model sourceModel = (Model)source;
            if (sourceModel.isCollection() != targetModel.isCollection()
                || sourceModel.getParent() == null
                || sourceModel.getParent().isCollection() != targetModel.getParent().isCollection())
                return false;
        } else if (targetModel.isCollection() || targetModel.getParent().isCollection())
            return false;
        return true;
    }

    private Util() {}

    /**
     *
     */
    public static interface Colors {

        /**
         *
         */
        Color BACKGROUND = Activator.color(255, 255, 255);

        /**
         *
         */
        Color DROP_TARGET_BACKGROUND = Activator.color(0, 0, 255);

        /**
         *
         */
        Color DROP_TARGET_FOREGROUND = Activator.color(255, 255, 255);

        /**
         *
         */
        Color EXPRESSION = Activator.color(192, 0, 192);

        /**
         *
         */
        Color FOREGROUND = Activator.color(0, 0, 0);

        /**
         *
         */
        Color FUNCTION = Activator.color(192, 255, 192);

        /**
         *
         */
        Color FUNCTION_ALTERNATE = Activator.color(128, 255, 128);

        /**
         *
         */
        Color MODEL = Activator.color(192, 192, 192);

        /**
         *
         */
        Color POTENTIAL_DROP_TARGET1 = Activator.color(0, 0, 128);

        /**
         *
         */
        Color POTENTIAL_DROP_TARGET2 = Activator.color(32, 32, 160);

        /**
         *
         */
        Color POTENTIAL_DROP_TARGET3 = Activator.color(64, 64, 192);

        /**
         *
         */
        Color POTENTIAL_DROP_TARGET4 = Activator.color(92, 92, 224);

        /**
         *
         */
        Color POTENTIAL_DROP_TARGET5 = Activator.color(128, 128, 255);

        /**
         *
         */
        Color POTENTIAL_DROP_TARGET6 = Activator.color(92, 92, 224);

        /**
         *
         */
        Color POTENTIAL_DROP_TARGET7 = Activator.color(64, 64, 192);

        /**
         *
         */
        Color POTENTIAL_DROP_TARGET8 = Activator.color(32, 32, 160);

        /**
         *
         */
        Color SELECTED = Activator.color(180, 213, 255);

        /**
         *
         */
        Color SELECTED_NO_FOCUS = Activator.color(212, 212, 212);

        /**
         *
         */
        Color VARIABLE = Activator.color(0, 0, 192);
    }

    /**
     *
     */
    public static interface Decorations {

        /**
         *
         */
        ImageDescriptor ADD = Activator.imageDescriptor("addOverlay.gif");

        /**
         *
         */
        ImageDescriptor COLLECTION = Activator.imageDescriptor("collectionOverlay.gif");

        /**
         *
         */
        ImageDescriptor MAPPED = Activator.imageDescriptor("mappedOverlay.gif");
    }

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

    /**
     *
     */
    public static interface Images {

        /**
         *
         */
        Image ADD_FUNCTION = Activator.imageDescriptor("addFunction16.gif").createImage();

        /**
         *
         */
        Image ADD =
            PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD);

        /**
         *
         */
        Image ATTRIBUTE = Activator.imageDescriptor("attribute16.gif").createImage();

        /**
         *
         */
        Image CHANGE = Activator.imageDescriptor("change16.gif").createImage();

        /**
         *
         */
        Image CLEAR = Activator.imageDescriptor("clear16.gif").createImage();

        /**
         *
         */
        Image COLLAPSE_ALL = Activator.imageDescriptor("collapseAll16.gif").createImage();

        /**
         *
         */
        Image DELETE =
            PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE);

        /**
         *
         */
        Image ELEMENT = Activator.imageDescriptor("element16.gif").createImage();

        /**
         *
         */
        Image FILTER = Activator.imageDescriptor("filter16.gif").createImage();

        /**
         *
         */
        Image HIDE_MAPPED = Activator.imageDescriptor("hideMapped16.gif").createImage();

        /**
         *
         */
        Image MAPPED = Activator.imageDescriptor("mapped16.gif").createImage();

        /**
         *
         */
        Image MENU = Activator.imageDescriptor("menu10x5.gif").createImage();

        /**
         *
         */
        Image SEARCH = Activator.imageDescriptor("search16.gif").createImage();

        /**
         *
         */
        Image TREE = Activator.imageDescriptor("tree16.gif").createImage();

        /**
         *
         */
        Image VARIABLE = Activator.imageDescriptor("variable16.gif").createImage();
    }
}
