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
import java.util.Collections;
import java.util.Date;
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
import org.jboss.tools.fuse.transformation.MappingOperation;
import org.jboss.tools.fuse.transformation.MappingType;
import org.jboss.tools.fuse.transformation.Variable;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.dozer.DozerResourceClasspathSelectionDialog;
import org.jboss.tools.fuse.transformation.editor.transformations.Function.Arg;
import org.jboss.tools.fuse.transformation.model.Model;

public class Util {

    public static final String MAIN_PATH = "src/main/";

    public static final String RESOURCES_PATH = MAIN_PATH + "resources/";

    public static final String JAVA_PATH = MAIN_PATH + "java/";

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static final String TRANSFORMATIONS_FOLDER = ".transformations";

    public static String displayName(Class<?> type) {
        String name = type.getName();
        if (name.startsWith("java.lang.") && name.lastIndexOf('.') == 9) return "String";
        if (type == Date.class) return "Date";
        return type.getName().replace('.', '/');
    }

    /**
     * @return the object being dragged
     */
    public static Object draggedObject() {
        return ((IStructuredSelection)LocalSelectionTransfer.getTransfer().getSelection()).getFirstElement();
    }

    /**
     * @param manager
     * @return <code>true</code> if the object being dragged is a valid source object
     */
    public static boolean draggingFromValidSource(TransformationManager manager) {
        final Object object = draggedObject();
        if (object instanceof Variable) return true;
        if (!(object instanceof Model)) return false;
        final Model model = (Model)object;
        if (type(model)) return false;
        return root(model).equals(manager.rootSourceModel());
    }

    /**
     * @param manager
     * @return <code>true</code> if the object being dragged is a valid target object
     */
    public static boolean draggingFromValidTarget(TransformationManager manager) {
        final Object object = draggedObject();
        if (!(object instanceof Model)) return false;
        final Model model = (Model)object;
        if (type(model)) return false;
        return root(model).equals(manager.rootTargetModel());
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
            builder.append('/');
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
                                       final MappingOperation<?, ?> mapping,
                                       final boolean isSource) {
        final DateFormatInputDialog dlg = new DateFormatInputDialog(shell, mapping);
        if (mapping.getSourceDateFormat() != null && isSource) {
            dlg.setFormatString(mapping.getSourceDateFormat());
        } else if (mapping.getTargetDateFormat() != null && !isSource) {
            dlg.setFormatString(mapping.getTargetDateFormat());
        }
        if (dlg.open() != Window.OK) {
            return null;
        }
        return dlg.getFormatString();
    }

    public static boolean inCollection(final Model model) {
        return model == null ? false : isOrInCollection(model.getParent());
    }

    private static boolean indexed(MappingOperation<?, ?> mapping) {
        if (mapping.getSource() instanceof Model)
            return inCollection((Model)mapping.getSource()) != inCollection((Model)mapping.getTarget());
        return inCollection((Model)mapping.getTarget());
    }

    private static boolean isOrInCollection(final Model model) {
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
            case "boolean":
                return Boolean.class.getName();
            case "short":
                return Short.class.getName();
            case "char":
                return Character.class.getName();
            case "byte":
                return Byte.class.getName();
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
                event.gc.setForeground(event.display.getSystemColor(SWT.COLOR_GRAY));
                final Rectangle bounds = ((Control)event.widget).getBounds();
                event.gc.drawRoundRectangle(0, 0, bounds.width - 1, bounds.height - 1, bounds.height, bounds.height);
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
                        types.add(type);
                    }
                } else if (element instanceof IParent) {
                    String path = element.getPath().toString();
                    if (!path.contains("/test/")
                        && !path.endsWith("/.functions") && !path.endsWith("/" + TRANSFORMATIONS_FOLDER)
                        && (!(element instanceof IPackageFragmentRoot) || !((IPackageFragmentRoot)element).isExternal()))
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
     * @param pane
     * @param arc
     * @return A paint listener that paints a border around the supplied composite.
     */
    public static final PaintListener roundedRectanglePainter(final Composite pane,
                                                              final int arc) {
        return new PaintListener() {

            @Override
            public void paintControl(final PaintEvent event) {
                event.gc.setBackground(pane.getForeground());
                Rectangle bounds = ((Composite)event.widget).getClientArea();
                event.gc.fillRoundRectangle(0, 0, bounds.width - 1, bounds.height - 1, arc, arc);
            }
        };
    }

    /**
     * @param shell
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
     * @param project
     * @return the selected file
     */
    public static IType selectCustomTransformationClass(final Shell shell,
                                                        final IProject project) {
        return selectCustomTransformationClass(shell, project, null);
    }

    /**
     * @param shell
     * @param project
     * @param filter
     * @return the selected file
     */
    public static IType selectCustomTransformationClass(final Shell shell,
                                                        final IProject project,
                                                        final Filter filter) {
        return selectClass(shell, project, filter,
                           "Custom Transformation Class",
                           "Select a custom transformation class");
    }

    /**
     * @param shell
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

    public static List<Integer> sourceUpdateIndexes(MappingOperation<?, ?> mapping) {
        if (mapping == null) return Collections.emptyList();
        return updateIndexes(mapping, mapping.getSource(), mapping.getSourceIndex());
    }

    /**
     * @return A paint listener that paints a border around a composite that's being used as a table.
     * @see #tableColumnHeaderBorderPainter()
     * @see #tableCellBorderPainter(boolean, boolean)
     */
    public static PaintListener tableBorderPainter() {
        return new PaintListener() {

            @Override
            public void paintControl(final PaintEvent event) {
                event.gc.setForeground(event.display.getSystemColor(SWT.COLOR_GRAY));
                final Rectangle bounds = ((Control)event.widget).getBounds();
                event.gc.drawLine(0, 0, 0, bounds.height - 1); // Left border
                event.gc.drawLine(bounds.width - 1, 0, bounds.width - 1, bounds.height - 1); // Right border
            }
        };
    }

    /**
     * @param leftBorder <code>true</code> if the left border should be painted
     * @param rightBorder <code>true</code> if the right border should be painted
     * @return A paint listener that paints a border around a composite that's being used as a table cell.
     * @see #tableBorderPainter()
     * @see #tableColumnHeaderBorderPainter()
     */
    public static PaintListener tableCellBorderPainter(final boolean leftBorder,
                                                       final boolean rightBorder) {
        return new PaintListener() {

            @Override
            public void paintControl(final PaintEvent event) {
                event.gc.setForeground(event.display.getSystemColor(SWT.COLOR_GRAY));
                final Rectangle bounds = ((Control)event.widget).getBounds();
                event.gc.drawLine(0, bounds.height - 1, bounds.width, bounds.height - 1); // Bottom border
                if (leftBorder) event.gc.drawLine(0, 0, 0, bounds.height - 1); // Left border
                if (rightBorder) event.gc.drawLine(bounds.width - 1, 0, bounds.width - 1, bounds.height - 1); // Right border
            }
        };
    }

    /**
     * @return A paint listener that paints a border around a control that's being used as a table's column header.
     * @see #tableBorderPainter()
     * @see #tableCellBorderPainter(boolean, boolean)
     */
    public static PaintListener tableColumnHeaderBorderPainter() {
        return new PaintListener() {

            @Override
            public void paintControl(final PaintEvent event) {
                event.gc.setForeground(event.display.getSystemColor(SWT.COLOR_GRAY));
                final Rectangle bounds = ((Control)event.widget).getBounds();
                event.gc.drawLine(bounds.width - 1, 0, bounds.width - 1, bounds.height - 1); // Right border
            }
        };
    }

    public static List<Integer> targetUpdateIndexes(MappingOperation<?, ?> mapping) {
        if (mapping == null) return Collections.emptyList();
        return updateIndexes(mapping, mapping.getTarget(), mapping.getTargetIndex());
    }

    public static boolean type(final Model model) {
        return (!model.isCollection() && !model.getChildren().isEmpty());
    }

    public static void updateDateFormat(Shell shell,
                                        MappingOperation<?, ?> mapping) {
        // if both sides of the equation are Models, we're good to check this out
        if (mapping.getType() != MappingType.FIELD) return;
        Model srcModel = (Model)mapping.getSource();
        Model tgtModel = (Model)mapping.getTarget();
        if (srcModel.getType().equalsIgnoreCase("java.lang.String") &&
            tgtModel.getType().equalsIgnoreCase("java.util.Date")) {
            String dateFormatStr = Util.getDateFormat(shell, mapping, true);
            mapping.setSourceDateFormat(dateFormatStr);
        } else if (tgtModel.getType().equalsIgnoreCase("java.lang.String") &&
                   srcModel.getType().equalsIgnoreCase("java.util.Date")) {
            String dateFormatStr = Util.getDateFormat(shell, mapping, false);
            mapping.setTargetDateFormat(dateFormatStr);
        }
    }

    private static List<Integer> updateIndexes(MappingOperation<?, ?> mapping,
                                               Object object,
                                               List<Integer> indexes) {
        if (!(object instanceof Model)) return null;
        List<Integer> updateIndexes = new ArrayList<>();
        updateIndexes(((Model)object).getParent(),
                      indexes,
                      indexes == null ? -1 : indexes.size() - 1,
                      updateIndexes,
                      indexed(mapping));
        return updateIndexes;
    }

    private static void updateIndexes(Model model,
                                      List<Integer> indexes,
                                      int indexesIndex,
                                      List<Integer> updateIndexes,
                                      boolean indexed) {
        if (model == null) return;
        updateIndexes(model.getParent(), indexes, indexesIndex - 1, updateIndexes, indexed);
        if (model.isCollection() && indexed) {
            Integer index = indexesIndex < 0 ? null : indexes.get(indexesIndex);
            updateIndexes.add(index == null ? 0 : index);
        } else updateIndexes.add(null);
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
     * @return <code>true</code> if the supplied value is valid for the supplied transformation argument's annotation and type
     * @param value
     *        An argument value to be validated
     * @param annotation
     *        the transformation argument's annotation
     * @param type
     *        the transformation argument's type
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

    public static boolean validSourceAndTarget(Object source,
                                               Object target,
                                               TransformationManager manager) {
        Model sourceModel = source instanceof Model ? (Model)source : null;
        Model targetModel = target instanceof Model ? (Model)target : null;
        if (sourceModel != null && Util.type(sourceModel)) return false;
        if (targetModel != null && Util.type(targetModel)) return false;
        if (sourceModel != null && targetModel != null) {
            if (manager.mapped(sourceModel, targetModel)) return false;
            if (sourceModel.isCollection() || targetModel.isCollection()) return false;
        }
        return true;
    }

    private Util() {}

    public static interface Colors {

        Color BACKGROUND = Activator.color(255, 255, 255);

        Color CONTAINER = Activator.color(192, 192, 192);

        Color CONTAINER_ALTERNATE = Activator.color(224, 224, 224);

        Color DROP_TARGET_BACKGROUND = Activator.color(0, 0, 255);

        Color DROP_TARGET_FOREGROUND = Activator.color(255, 255, 255);

        Color EXPRESSION = Activator.color(192, 0, 192);

        Color FOREGROUND = Activator.color(0, 0, 0);

        Color POTENTIAL_DROP_TARGET1 = Activator.color(0, 0, 128);

        Color POTENTIAL_DROP_TARGET2 = Activator.color(32, 32, 160);

        Color POTENTIAL_DROP_TARGET3 = Activator.color(64, 64, 192);

        Color POTENTIAL_DROP_TARGET4 = Activator.color(92, 92, 224);

        Color POTENTIAL_DROP_TARGET5 = Activator.color(128, 128, 255);

        Color POTENTIAL_DROP_TARGET6 = Activator.color(92, 92, 224);

        Color POTENTIAL_DROP_TARGET7 = Activator.color(64, 64, 192);

        Color POTENTIAL_DROP_TARGET8 = Activator.color(32, 32, 160);

        Color SASH = Activator.color(64, 64, 64);

        Color SELECTED = Activator.color(21, 81, 207);

        Color SELECTED_NO_FOCUS = Activator.color(212, 212, 212);
    }

    public static interface Decorations {

        ImageDescriptor ADD = Activator.imageDescriptor("addOverlay.gif");

        ImageDescriptor LIST = Activator.imageDescriptor("listOverlay.gif");

        ImageDescriptor MAPPED = Activator.imageDescriptor("mappedOverlay.gif");
    }

    /**
     * Provides users with the ability to further filter which classes appear in the dialog shown by
     * {@link Util#selectClass(Shell, IProject, Filter, String, String)}
     */
    public static interface Filter {

        /**
         * @param type
         * @return <code>true</code> if the supplied type should appear in the dialog shown by
         *         {@link Util#selectClass(Shell, IProject, Filter, String, String)}
         */
        boolean accept(IType type);
    }

    public static interface Images {

        Image ADD = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD);

        Image ADD_TRANSFORMATION = Activator.imageDescriptor("addTransformation16.gif").createImage();

        Image CHANGE = Activator.imageDescriptor("change16.gif").createImage();

        Image CLEAR = Activator.imageDescriptor("clear16.gif").createImage();

        Image COLLAPSE_ALL = Activator.imageDescriptor("collapseAll16.gif").createImage();

        Image DELETE = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE);

        Image HIDE_MAPPED = Activator.imageDescriptor("hideMapped16.gif").createImage();

        Image MAPPED = Activator.imageDescriptor("mapped16.gif").createImage();

        Image MAPPED_NODE = Activator.imageDescriptor("mappedNode16.gif").createImage();

        Image MAPPED_PROPERTY = Activator.imageDescriptor("mappedProperty16.gif").createImage();

        Image MENU = Activator.imageDescriptor("menu10x5.gif").createImage();

        Image NODE = Activator.imageDescriptor("node16.gif").createImage();

        Image PROPERTY = Activator.imageDescriptor("property16.gif").createImage();

        Image SEARCH = Activator.imageDescriptor("search16.gif").createImage();

        Image SHOW_TYPES = Activator.imageDescriptor("showTypes32x16.gif").createImage();

        Image TRANSFORMATION = Activator.imageDescriptor("transformation16.gif").createImage();

        Image TREE = Activator.imageDescriptor("tree16.gif").createImage();

        Image VARIABLE = Activator.imageDescriptor("variable16.gif").createImage();
    }
}
