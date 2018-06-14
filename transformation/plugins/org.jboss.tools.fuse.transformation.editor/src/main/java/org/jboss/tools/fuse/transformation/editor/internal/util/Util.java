/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IClasspathEntry;
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
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;
import org.jboss.tools.fuse.transformation.core.MappingOperation;
import org.jboss.tools.fuse.transformation.core.MappingType;
import org.jboss.tools.fuse.transformation.core.Variable;
import org.jboss.tools.fuse.transformation.core.model.Model;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.dozer.DozerResourceClasspathSelectionDialog;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.transformations.Function.Arg;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Util {

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static final String TRANSFORMATIONS_FOLDER = ".transformations"; //$NON-NLS-1$

    public static String displayName(Class<?> type) {
        String name = type.getName();
        if (name.startsWith("java.lang.") && name.lastIndexOf('.') == 9) { //$NON-NLS-1$
        	return "String"; //$NON-NLS-1$
        }
        if (type == Date.class) {
        	return "Date"; //$NON-NLS-1$
        }
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
        Object object = draggedObject();
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
        return root(model).equals(manager.rootSourceModel());
    }

    /**
     * @param manager
     * @return <code>true</code> if the object being dragged is a valid target object
     */
    public static boolean draggingFromValidTarget(TransformationManager manager) {
        Object object = draggedObject();
        if (!(object instanceof Model)) {
        	return false;
        }
        final Model model = (Model)object;
        if (type(model)) {
        	return false;
        }
        return root(model).equals(manager.rootTargetModel());
    }

    public static void ensureSourceFolderExists(IJavaProject javaProject,
                                                String folderName,
                                                IProgressMonitor monitor) throws Exception {
        boolean exists = false;
        IClasspathEntry[] entries = javaProject.getRawClasspath();
        IPath path = javaProject.getPath().append(folderName);
        for (IClasspathEntry entry : entries) {
            if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE && entry.getPath().equals(path)) {
                exists = true;
                File folder = javaProject.getResource().getLocation().append(folderName).toFile();
                if (!folder.exists()) {
                    if (!folder.mkdirs()) {
                    	throw new Exception(Messages.bind(Messages.Util_UnableToCreateSourceFolder, folder));
                    }
                    javaProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
                }
                break;
            }
        }
        if (!exists) {
            IClasspathEntry[] newEntries = Arrays.copyOf(entries, entries.length + 1);
            newEntries[entries.length] = JavaCore.newSourceEntry(path);
            javaProject.setRawClasspath(newEntries, monitor);
            javaProject.save(monitor, true);
        }
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
        return model != null && model.getType() != null;
    }

    public static boolean jsonValid(String incomingJSON) {
        try {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(incomingJSON); // throws JsonSyntaxException
            if (incomingJSON.trim().isEmpty() || element instanceof JsonNull) {
                return false;
            }
            if (element instanceof JsonObject) {
                JsonObject jObj = (JsonObject) element;
                if (jObj.entrySet().isEmpty()) {
                    return false;
                }
            }
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) {
            return false;
        }
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
            if (String.class.getName().equalsIgnoreCase(srcModel.getType())
            	&& Date.class.getName().equalsIgnoreCase(tgtModel.getType()) && isSource) {
                return true;
            }
            if (String.class.getName().equalsIgnoreCase(tgtModel.getType())
            	&& Date.class.getName().equalsIgnoreCase(srcModel.getType()) && !isSource) {
                return true;
            }
        }
        return false;
    }

    public static String nonPrimitiveClassName(String type) {
        // Return wrapper class if type is primitive
        switch (type) {
            case "int": //$NON-NLS-1$
                return Integer.class.getName();
            case "long": //$NON-NLS-1$
                return Long.class.getName();
            case "double": //$NON-NLS-1$
                return Double.class.getName();
            case "float": //$NON-NLS-1$
                return Float.class.getName();
            case "boolean": //$NON-NLS-1$
                return Boolean.class.getName();
            case "short": //$NON-NLS-1$
                return Short.class.getName();
            case "char": //$NON-NLS-1$
                return Character.class.getName();
            case "byte": //$NON-NLS-1$
                return Byte.class.getName();
            default:
            	return type;
        }
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
                    if (!path.contains("/test/") //$NON-NLS-1$
                        && !path.endsWith("/.functions") && !path.endsWith("/" + TRANSFORMATIONS_FOLDER) //$NON-NLS-1$ //$NON-NLS-2$
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
        CamelResourceClasspathSelectionDialog dialog =
            new CamelResourceClasspathSelectionDialog(shell,
                                                      workspaceContext(project),
                                                      Messages.Util_SelectCamelFileDialogTitle);
        return selectResourceFromDialog(dialog, "xml"); //$NON-NLS-1$
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
                           Messages.Util_CustomTransformationClass,
                           Messages.Util_SelectACustomTransformationClass);
    }

    /**
     * @param shell
     * @param project
     * @return The selected resource
     */
    public static IResource selectDozerResourceFromWorkspace(final Shell shell,
                                                             final IProject project) {
        DozerResourceClasspathSelectionDialog dialog =
            new DozerResourceClasspathSelectionDialog(shell,
                                                      workspaceContext(project),
                                                      Messages.Util_SelectTransformationFileFromProject);
        return selectResourceFromDialog(dialog, "xml"); //$NON-NLS-1$
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
                    return super.getText(element) + " - " //$NON-NLS-1$
                           + ((IResource)element).getParent().getFullPath().makeRelative();
                }
            });
        dlg.setTitle(Messages.Util_Select_DialogTtile + schemaType);
		dlg.setMessage(Messages.bind(Messages.Util_messageSelectFileFortransformation, schemaType));
        dlg.setMatchEmptyString(true);
        dlg.setHelpAvailable(false);
        final List<IResource> resources = new ArrayList<>();
        populateResources(shell, project, resources);
        dlg.setElements(resources.toArray());
        return dlg.open() == Window.OK
            ? ((IFile)dlg.getFirstResult()).getProjectRelativePath().toString() : null;
    }

    private static IResource selectResourceFromDialog(FilteredResourcesSelectionDialog dialog,
                                                      String extension) {
        dialog.setInitialPattern("*." + extension); //$NON-NLS-1$
        dialog.open();
        Object[] result = dialog.getResult();
        return result == null || result.length == 0 || !(result[0] instanceof IFile) ? null : (IFile)result[0];
    }

    /**
     * @param shell the parent shell in which to display the selection dialog
     * @param title the title of the selection dialog
     * @param extension the file extension to which to restrict selections
     * @param project the currently selected project
     * @return The selected resource
     */
    public static IResource selectResourceFromWorkspace(Shell shell,
                                                        String title,
                                                        String extension,
                                                        IProject project) {
        return selectResourceFromDialog(new ClasspathResourceSelectionDialog(shell, workspaceContext(project), title), extension);
    }

    public static List<Integer> sourceUpdateIndexes(MappingOperation<?, ?> mapping) {
        if (mapping == null) {
        	return Collections.emptyList();
        }
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
                if (leftBorder) {
                	event.gc.drawLine(0, 0, 0, bounds.height - 1); // Left border
                }
                if (rightBorder) {
                	event.gc.drawLine(bounds.width - 1, 0, bounds.width - 1, bounds.height - 1); // Right border
                }
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
        if (mapping == null) {
        	return Collections.emptyList();
        }
        return updateIndexes(mapping, mapping.getTarget(), mapping.getTargetIndex());
    }

    public static boolean type(final Model model) {
        return !model.isCollection() && !model.getChildren().isEmpty();
    }

    public static void updateDateFormat(Shell shell,
                                        MappingOperation<?, ?> mapping) {
        // if both sides of the equation are Models, we're good to check this out
        if (mapping.getType() != MappingType.FIELD) {
        	return;
        }
        Model srcModel = (Model)mapping.getSource();
        Model tgtModel = (Model)mapping.getTarget();
        if (String.class.getName().equalsIgnoreCase(srcModel.getType())
        	&& Date.class.getName().equalsIgnoreCase(tgtModel.getType())) {
            String dateFormatStr = Util.getDateFormat(shell, mapping, true);
            mapping.setSourceDateFormat(dateFormatStr);
        } else if (String.class.getName().equalsIgnoreCase(tgtModel.getType())
        	&& Date.class.getName().equalsIgnoreCase(srcModel.getType())) {
            String dateFormatStr = Util.getDateFormat(shell, mapping, false);
            mapping.setTargetDateFormat(dateFormatStr);
        }
    }

    private static List<Integer> updateIndexes(MappingOperation<?, ?> mapping,
                                               Object object,
                                               List<Integer> indexes) {
        if (!(object instanceof Model)) {
        	return null;
        }
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
        if (model == null) {
        	return;
        }
        updateIndexes(model.getParent(), indexes, indexesIndex - 1, updateIndexes, indexed);
        if (model.isCollection() && indexed) {
            Integer index = indexesIndex < 0 ? null : indexes.get(indexesIndex);
            updateIndexes.add(index == null ? 0 : index);
        } else updateIndexes.add(null);
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
            if (type == Boolean.class) {
            	return true;
            }
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
        if (sourceModel != null && Util.type(sourceModel)) {
        	return false;
        }
        if (targetModel != null && Util.type(targetModel)) {
        	return false;
        }
        if (sourceModel != null && targetModel != null) {
            if (manager.mapped(sourceModel, targetModel)) {
            	return false;
            }
            if (sourceModel.isCollection() || targetModel.isCollection()) {
            	return false;
            }
        }
        return true;
    }

    private static IContainer workspaceContext(IProject project) {
        return project == null || JavaCore.create(project) == null ? workspaceRoot() : project;
    }

    private static IWorkspaceRoot workspaceRoot() {
        return ResourcesPlugin.getWorkspace().getRoot();
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

        ImageDescriptor ADD = Activator.imageDescriptor("addOverlay.gif"); //$NON-NLS-1$

        ImageDescriptor LIST = Activator.imageDescriptor("listOverlay.gif"); //$NON-NLS-1$

        ImageDescriptor MAPPED = Activator.imageDescriptor("mappedOverlay.gif"); //$NON-NLS-1$
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

        Image ADD_TRANSFORMATION = Activator.imageDescriptor("addTransformation16.gif").createImage(); //$NON-NLS-1$

        Image CHANGE = Activator.imageDescriptor("change16.gif").createImage(); //$NON-NLS-1$

        Image CLEAR = Activator.imageDescriptor("clear16.gif").createImage(); //$NON-NLS-1$

        Image COLLAPSE_ALL = Activator.imageDescriptor("collapseAll16.gif").createImage(); //$NON-NLS-1$

        Image DELETE = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE);

        Image HIDE_MAPPED = Activator.imageDescriptor("hideMapped16.gif").createImage(); //$NON-NLS-1$

        Image MAPPED = Activator.imageDescriptor("mapped16.gif").createImage(); //$NON-NLS-1$

        Image MAPPED_NODE = Activator.imageDescriptor("mappedNode16.gif").createImage(); //$NON-NLS-1$

        Image MAPPED_PROPERTY = Activator.imageDescriptor("mappedProperty16.gif").createImage(); //$NON-NLS-1$

        Image MENU = Activator.imageDescriptor("menu10x5.gif").createImage(); //$NON-NLS-1$

        Image NODE = Activator.imageDescriptor("node16.gif").createImage(); //$NON-NLS-1$

        Image PROPERTY = Activator.imageDescriptor("property16.gif").createImage(); //$NON-NLS-1$

        Image SEARCH = Activator.imageDescriptor("search16.gif").createImage(); //$NON-NLS-1$

        Image SHOW_TYPES = Activator.imageDescriptor("type16.png").createImage(); //$NON-NLS-1$

        Image TRANSFORMATION = Activator.imageDescriptor("transformation16.gif").createImage(); //$NON-NLS-1$

        Image TREE = Activator.imageDescriptor("tree16.gif").createImage(); //$NON-NLS-1$

        Image VARIABLE = Activator.imageDescriptor("variable16.gif").createImage(); //$NON-NLS-1$
    }
}
