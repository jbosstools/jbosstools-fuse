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
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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
import org.jboss.tools.fuse.transformation.FieldMapping;
import org.jboss.tools.fuse.transformation.MappingOperation;
import org.jboss.tools.fuse.transformation.Variable;
import org.jboss.tools.fuse.transformation.dozer.BaseDozerMapping;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.function.Function;
import org.jboss.tools.fuse.transformation.editor.function.Function.Arg;
import org.jboss.tools.fuse.transformation.editor.internal.dozer.DozerResourceClasspathSelectionDialog;
import org.jboss.tools.fuse.transformation.model.Model;

public class Util {

    public static final String MAIN_PATH = "src/main/";

    public static final String RESOURCES_PATH = MAIN_PATH + "resources/";

    public static final String JAVA_PATH = MAIN_PATH + "java/";

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * @return the object being dragged
     */
    public static Object draggedObject() {
        return ((IStructuredSelection)LocalSelectionTransfer.getTransfer()
                                                            .getSelection())
                                                                            .getFirstElement();
    }

    /**
     * @param config
     * @return <code>true</code> if the object being dragged is a valid source object
     */
    public static boolean draggingFromValidSource(final TransformationConfig config) {
        final Object object = draggedObject();
        if (object instanceof Variable) {
            return true;
        }
        if (!(object instanceof Model)) {
            return false;
        }
        final Model model = (Model)object;
        if (type(model)) {
            return false;
        }
        return root(model).equals(config.getSourceModel());
    }

    /**
     * @param config
     * @return <code>true</code> if the object being dragged is a valid target object
     */
    public static boolean draggingFromValidTarget(final TransformationConfig config) {
        final Object object = draggedObject();
        if (!(object instanceof Model)) {
            return false;
        }
        final Model model = (Model)object;
        if (type(model)) {
            return false;
        }
        return root(model).equals(config.getTargetModel());
    }

    /**
     * @param model
     * @return the fully-qualified name of the supplied model
     */
    public static String fullyQualifiedName(final Model model) {
        return fullyQualifiedName(model, new StringBuilder());
    }

    private static String fullyQualifiedName(final Model model,
                                             final StringBuilder builder) {
        if (model.getParent() != null) {
            fullyQualifiedName(model.getParent(), builder);
            builder.append('.');
        }
        builder.append(model.getName());
        return builder.toString();
    }

    private static ArrayList<IResource> getAllXMLFilesInProject(final IProject project) {
        ArrayList<IResource> allFiles = new ArrayList<>();
        IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
        IPath path = project.getLocation();
        recursivelyFindFilesWithExtension(allFiles, path, wsRoot, "xml");
        return allFiles;
    }

    public static String getCamelVersion(IProject project) {
        IPath pomPathValue = project.getProject().getRawLocation() != null ? project.getProject().getRawLocation().append("pom.xml") : ResourcesPlugin.getWorkspace().getRoot().getLocation().append(project.getFullPath().append("pom.xml"));
        String pomPath = pomPathValue.toOSString();
        final File pomFile = new File(pomPath);
        try {
            final org.apache.maven.model.Model model = MavenPlugin.getMaven().readModel(pomFile);
            List<org.apache.maven.model.Dependency> deps = model.getDependencies();
            for (Iterator<org.apache.maven.model.Dependency> iterator = deps.iterator(); iterator.hasNext();) {
                org.apache.maven.model.Dependency dependency = iterator.next();
                if (dependency.getArtifactId().equals("camel-core")) {
                    return dependency.getVersion();
                }
            }
        } catch (CoreException e) {
            // not found, go with default
        }
        return org.fusesource.ide.camel.editor.Activator.getDefault().getCamelVersion();
    }

    public static String getDateFormat(final Shell shell,
                                       final MappingOperation<?, ?> mappingOp,
                                       final boolean isSource) {
        final DateFormatInputDialog dlg = new DateFormatInputDialog(shell, mappingOp);
        BaseDozerMapping dMapping = (BaseDozerMapping)mappingOp;
        if (dMapping.getSourceDateFormat() != null && isSource) {
            dlg.setFormatString(dMapping.getSourceDateFormat());
        } else if (dMapping.getTargetDateFormat() != null && !isSource) {
            dlg.setFormatString(dMapping.getTargetDateFormat());
        }
        if (dlg.open() != Window.OK) {
            return null;
        }
        return dlg.getFormatString();
    }

    public static List<Integer> indexes(final Shell shell,
                                        final Model model,
                                        final boolean source) throws CanceledDialogException {
        if (Util.isOrInCollection(model)) {
            final IndexesDialog dlg = new IndexesDialog(shell, model, source);
            if (dlg.open() == Window.OK) return dlg.indexes;
            throw new CanceledDialogException();
        }
        return null;
    }

    public static boolean isOrInCollection(final Model model) {
        return model != null && (model.isCollection() || isOrInCollection(model.getParent()));
    }

    private static boolean isValidNonNullType(Model model) {
        if (model != null && model.getType() != null) {
            return true;
        }
        return false;
    }

    public static boolean modelsNeedDateFormat(final Object source,
                                               final Object target,
                                               final boolean isSource) {
        if (!(source instanceof Model && target instanceof Model)) {
            return false;
        }
        Model srcModel = (Model)source;
        Model tgtModel = (Model)target;
        if (isValidNonNullType(srcModel) && isValidNonNullType(tgtModel)) {
            if (srcModel.getType().equalsIgnoreCase("java.lang.String") &&
                tgtModel.getType().equalsIgnoreCase("java.util.Date") && isSource) {
                return true;
            } else if (tgtModel.getType().equalsIgnoreCase("java.lang.String") &&
                       srcModel.getType().equalsIgnoreCase("java.util.Date") && !isSource) {
                return true;
            }
        }
        return false;
    }

    public static String nonPrimitiveClassName(String type) {
        // Return wrapper class if type is primitive
        switch (type) {
            case "int":
                return Integer.class.getName();
            case "long":
                return Long.class.getName();
            case "double":
                return Double.class.getName();
            case "float":
                return Float.class.getName();
            case "short":
                return Short.class.getName();
            case "boolean":
                return Boolean.class.getName();
            case "char":
            case "byte":
                return String.class.getName();
        }
        return type;
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
                final Rectangle bounds = ((Control)event.widget).getBounds();
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
                    final IType type = (IType)element;
                    if (type.isClass() && type.isStructureKnown() && !type.isAnonymous()
                        && !type.isLocal() && !Flags.isAbstract(type.getFlags())
                        && Flags.isPublic(type.getFlags())
                        && (filter == null || filter.accept(type))) {
                        // jpav: remove
                        System.out.println(type.getFullyQualifiedName());
                        types.add(type);
                    }
                } else if (element instanceof IParent
                           && !element.getPath().toString().contains("/test/")
                           && !element.getElementName().equals(Function.class.getPackage().getName())
                           && (!(element instanceof IPackageFragmentRoot)
                           || !((IPackageFragmentRoot)element).isExternal())) {
                    // jpav: remove
                    System.out.println("path: " + element.getElementName());
                    populateClasses(shell, (IParent)element, types, filter);
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
                    populateResources(shell, (IContainer)resource, resources);
                } else {
                    resources.add(resource);
                }
            }
        } catch (final Exception e) {
            Activator.error(e);
        }
    }

    public static boolean projectHasCamelResource(final IProject project) {
        try {
            ArrayList<IResource> xmlResources = getAllXMLFilesInProject(project);
            for (Iterator<IResource> iterator = xmlResources.iterator(); iterator.hasNext();) {
                IResource item = iterator.next();
                File testFile = new File(item.getLocationURI());
                if (testFile.exists()) {
                    boolean isValidCamel = CamelFileTypeHelper
                                                              .isSupportedCamelFile(project,
                                                                                    item.getProjectRelativePath().toPortableString());
                    if (isValidCamel) {
                        return true;
                    }
                }
            }
        } catch (final Exception e) {
            // ignore
        }

        return false;
    }

    private static void recursivelyFindFilesWithExtension(ArrayList<IResource> allFiles,
                                                          IPath path,
                                                          IWorkspaceRoot wsRoot,
                                                          String extension) {
        IContainer container = wsRoot.getContainerForLocation(path);

        try {
            IResource[] resources = container.members();
            for (IResource resource : resources) {
                if (extension.equalsIgnoreCase(resource.getFileExtension())) {
                    allFiles.add(resource);
                }
                if (resource.getType() == IResource.FOLDER) {
                    IPath tempPath = resource.getLocation();
                    recursivelyFindFilesWithExtension(allFiles, tempPath, wsRoot, extension);
                }
            }
        } catch (CoreException e) {
            // eat the exception, but throw it in the console
            e.printStackTrace();
        }
    }

    /**
     * @param model
     * @return the root model of the supplied model
     */
    public static Model root(final Model model) {
        return model.getParent() == null ? model : root(model.getParent());
    }

    /**
     * @param arc
     * @param background
     * @return A paint listener that paints a border around a control. Useful for borders around labels
     */
    public static final PaintListener roundedRectanglePainter(final int arc,
                                                              final Color background) {
        return new PaintListener() {

            @Override
            public void paintControl(final PaintEvent event) {
                event.gc.setBackground(background);
                final Rectangle bounds = ((Composite)event.widget).getClientArea();
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
        if (project != null) {
            javaProject = JavaCore.create(project);
        }
        CamelResourceClasspathSelectionDialog dialog;
        if (javaProject == null) {
            dialog = new CamelResourceClasspathSelectionDialog(shell,
                                                               ResourcesPlugin.getWorkspace().getRoot(),
                                                               "xml");
        } else {
            dialog = new CamelResourceClasspathSelectionDialog(shell, javaProject.getProject(), "xml");
        }
        dialog.setTitle("Select Camel XML File from Project");
        dialog.setInitialPattern("*.xml"); //$NON-NLS-1$
        dialog.open();
        final Object[] result = dialog.getResult();
        if (result == null || result.length == 0 || !(result[0] instanceof IFile)) {
            return null;
        }
        return (IFile)result[0];
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
        return selectClass(shell, project, filter,
                           "Select Custom Function(s) Class",
                           "Select a custom function(s) class");
    }

    /**
     * @param shell
     * @param project
     * @param filter
     * @param title
     * @param message
     * @return the selected file
     */
    public static IType selectClass(final Shell shell,
                                    final IProject project,
                                    final Filter filter,
                                    final String title,
                                    final String message) {
        final int flags = JavaElementLabelProvider.SHOW_DEFAULT
                          | JavaElementLabelProvider.SHOW_POST_QUALIFIED
                          | JavaElementLabelProvider.SHOW_ROOT;
        final ElementListSelectionDialog dlg = new ElementListSelectionDialog(shell, new JavaElementLabelProvider(flags));
        dlg.setTitle(title);
        dlg.setMessage(message);
        dlg.setMatchEmptyString(true);
        dlg.setHelpAvailable(false);
        final List<IType> types = new ArrayList<>();
        populateClasses(shell, JavaCore.create(project), types, filter);
        dlg.setElements(types.toArray());
        return dlg.open() == Window.OK ? (IType)dlg.getFirstResult() : null;
    }

    /**
     * @param shell
     * @param extension
     * @param project
     * @return The selected resource
     */
    public static IResource selectDozerResourceFromWorkspace(final Shell shell,
                                                             final IProject project) {
        IJavaProject javaProject = null;
        if (project != null) {
            javaProject = JavaCore.create(project);
        }
        DozerResourceClasspathSelectionDialog dialog;
        if (javaProject == null) {
            dialog = new DozerResourceClasspathSelectionDialog(shell,
                                                               ResourcesPlugin.getWorkspace().getRoot(),
                                                               "xml");
        } else {
            dialog = new DozerResourceClasspathSelectionDialog(shell, javaProject.getProject(), "xml");
        }
        dialog.setTitle("Select Transformation File from Project");
        dialog.setInitialPattern("*.xml"); //$NON-NLS-1$
        dialog.open();
        final Object[] result = dialog.getResult();
        if (result == null || result.length == 0 || !(result[0] instanceof IFile)) {
            return null;
        }
        return (IFile)result[0];
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
                           + ((IResource)element).getParent().getFullPath().makeRelative();
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
            ? ((IFile)dlg.getFirstResult()).getProjectRelativePath().toString() : null;
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
        ClasspathResourceSelectionDialog dialog;
        if (javaProject == null) {
            dialog = new ClasspathResourceSelectionDialog(shell,
                                                          ResourcesPlugin.getWorkspace().getRoot(),
                                                          "xml");
        } else {
            dialog = new ClasspathResourceSelectionDialog(shell, javaProject.getProject(), "xml");
        }
        dialog.setTitle("Select Camel XML File from Project");
        dialog.setInitialPattern("*.xml"); //$NON-NLS-1$
        dialog.open();
        final Object[] result = dialog.getResult();
        if (result == null || result.length == 0 || !(result[0] instanceof IFile)) {
            return null;
        }
        return (IFile)result[0];
    }

    public static boolean type(final Model model) {
        return (!model.isCollection() && !model.getChildren().isEmpty());
    }

    public static void updateDateFormat(final Shell shell,
                                        final MappingOperation<?, ?> mappingOp) {
        if (mappingOp != null && mappingOp instanceof BaseDozerMapping) {

            // if both sides of the equation are Models, we're good to check this out
            if (!(mappingOp.getSource() instanceof Model && mappingOp.getTarget() instanceof Model)) {
                return;
            }
            Model srcModel = (Model)mappingOp.getSource();
            Model tgtModel = (Model)mappingOp.getTarget();
            BaseDozerMapping dMapping = (BaseDozerMapping)mappingOp;
            if (srcModel.getType().equalsIgnoreCase("java.lang.String") &&
                tgtModel.getType().equalsIgnoreCase("java.util.Date")) {
                String dateFormatStr = Util.getDateFormat(shell, mappingOp, true);
                dMapping.setSourceDateFormat(dateFormatStr);
            } else if (tgtModel.getType().equalsIgnoreCase("java.lang.String") &&
                       srcModel.getType().equalsIgnoreCase("java.util.Date")) {
                String dateFormatStr = Util.getDateFormat(shell, mappingOp, false);
                dMapping.setTargetDateFormat(dateFormatStr);
            }
        }
    }

    public static FieldMapping updateDateFormat(final Shell shell,
                                                final Model srcModel,
                                                final Model tgtModel,
                                                final TransformationConfig config) {

        if (srcModel != null && tgtModel != null && config != null) {
            FieldMapping mapping = config.mapField(srcModel, tgtModel);
            if (srcModel.getType().equalsIgnoreCase("java.lang.String") &&
                tgtModel.getType().equalsIgnoreCase("java.util.Date")) {
                String dateFormatStr = Util.getDateFormat(shell, mapping, true);
                mapping.setSourceDateFormat(dateFormatStr);
            } else if (tgtModel.getType().equalsIgnoreCase("java.lang.String") &&
                       srcModel.getType().equalsIgnoreCase("java.util.Date")) {
                String dateFormatStr = Util.getDateFormat(shell, mapping, false);
                mapping.setTargetDateFormat(dateFormatStr);
            }
            return mapping;
        }
        return null;
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
                if (pomDependency.getGroupId().equalsIgnoreCase(dependency.getGroupId())
                    && pomDependency.getArtifactId().equalsIgnoreCase(dependency.getArtifactId())) {
                    // check for correct version
                    if (!dependency.getVersion().equalsIgnoreCase(pomDependency.getVersion())) {
                        pomDependency.setVersion(dependency.getVersion());
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                missingDependencies.add(dependency);
            }
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

    /**
     * @return <code>true</code> if the supplied value is valid for the supplied function argument's annotation and type
     * @param value
     *        An argument value to be validated
     * @param arg
     *        the function argument's annotation
     * @param type
     *        the function argument's type
     */
    public static boolean valid(String value,
                                Arg annotation,
                                Class<?> type) {
        if (value == null || value.isEmpty()) {
            if (type == Boolean.class) return true;
            return annotation == null ? false : !annotation.defaultValue().isEmpty();
        }
        try {
            type.getConstructor(String.class).newInstance(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean validSourceAndTarget(final Object source,
                                               final Object target,
                                               final TransformationConfig config) {
        final Model sourceModel = source instanceof Model ? (Model)source : null;
        final Model targetModel = target instanceof Model ? (Model)target : null;
        if (sourceModel != null && Util.type(sourceModel)) {
            return false;
        }
        if (targetModel != null && Util.type(targetModel)) {
            return false;
        }
        if (sourceModel != null && targetModel != null) {
            if (config.getMapping(sourceModel, targetModel) != null) {
                return false;
            }
            if (sourceModel.isCollection() && targetModel.isCollection()) {
                return false;
            }
        }
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
        Color CONTAINER = Activator.color(192, 192, 192);

        /**
         *
         */
        Color CONTAINER_ALTERNATE = Activator.color(224, 224, 224);

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
        Color SASH = Activator.color(64, 64, 64);

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
     * Provides users with the ability to further filter which classes appear in the dialog shown by
     * {@link Util#selectClass(Shell, IProject, Filter)}
     */
    public static interface Filter {

        /**
         * @param type
         * @return <code>true</code> if the supplied type should appear in the dialog shown by
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
