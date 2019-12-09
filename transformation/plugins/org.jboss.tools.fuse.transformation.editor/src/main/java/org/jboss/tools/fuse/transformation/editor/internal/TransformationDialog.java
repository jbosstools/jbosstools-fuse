/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.fuse.transformation.core.MappingOperation;
import org.jboss.tools.fuse.transformation.core.MappingType;
import org.jboss.tools.fuse.transformation.core.TransformationMapping;
import org.jboss.tools.fuse.transformation.core.model.Model;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.internal.util.BaseDialog;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.editor.transformations.Function;
import org.jboss.tools.fuse.transformation.editor.transformations.Function.Arg;

// TODO handle variable length args
class TransformationDialog extends BaseDialog {

    final MappingOperation<?, ?> mapping;
    final IProject project;
    Method origTransformation, transformation;
    String[] argumentValues;

    ListViewer listViewer;
    Browser description;
    Composite argsPane;
    TableViewer tableViewer;

    TransformationDialog(Shell shell,
                   MappingOperation<?, ?> mapping,
                   IProject project) {
        super(shell);
        this.mapping = mapping;
        this.project = project;
        if (mapping.getType() == MappingType.TRANSFORMATION) {
            TransformationMapping xformMapping = (TransformationMapping)mapping;
            try {
                Class<?> xformClass = Class.forName(xformMapping.getTransformationClass());
                for (Method method : xformClass.getMethods()) {
                    if (method.getAnnotation(Function.class) != null
                        && method.getName().equals(xformMapping.getTransformationName()))
                        origTransformation = method;
                }
            } catch (ClassNotFoundException e) {
                Activator.error(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.editor.internal.util.BaseDialog#constructContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void constructContents(final Composite parent) {
        parent.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        Group group = new Group(parent, SWT.SHADOW_ETCHED_OUT);
        group.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());
        group.setLayout(GridLayoutFactory.swtDefaults().create());
        group.setText(Messages.TransformationDialog_groupTitleTransformations);
        listViewer = new ListViewer(group, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        listViewer.getList().setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());
        listViewer.setLabelProvider(new LabelProvider() {

            @Override
            public String getText(Object element) {
                return ((Method)element).getName();
            }
        });
        listViewer.setComparator(new ViewerComparator() {

            @Override
            public int compare(Viewer viewer,
                               Object element1,
                               Object element2) {
                return ((Method)element1).getName().compareTo(((Method)element2).getName());
            }
        });
        try {
            // Add all contributed transformations
            String sourceType = ((Model)mapping.getSource()).getType();
            for (IConfigurationElement element
                 : Platform.getExtensionRegistry().getConfigurationElementsFor(Activator.TRANSFORMATION_EXTENSION_POINT)) {
                Object instance = element.createExecutableExtension("class"); //$NON-NLS-1$
                for (Method method : instance.getClass().getDeclaredMethods()) {
                    Class<?>[] types = method.getParameterTypes();
                    if (Modifier.isPublic(method.getModifiers())
                        && types.length > 0
                        && types[0].getName().equals(Util.nonPrimitiveClassName(sourceType)))
                        listViewer.add(method);
                }
            }
        } catch (Exception e) {
            Activator.error(e);
        }
        final SashForm splitter = new SashForm(parent, SWT.VERTICAL);
        splitter.setBackground(Colors.SASH);
        splitter.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        Composite pane = new Composite(splitter, SWT.NONE);
        pane.setLayout(GridLayoutFactory.fillDefaults().create());
        final Group descGroup = new Group(pane, SWT.NONE);
        descGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        descGroup.setLayout(GridLayoutFactory.fillDefaults().create());
        descGroup.setText(Messages.TransformationDialog_grouptitleDescription);
        maximizeDescription(pane, true);
        pane = new Composite(splitter, SWT.NONE);
        pane.setLayout(GridLayoutFactory.fillDefaults().create());
        final Group argsGroup = new Group(pane, SWT.NONE);
        argsGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        argsGroup.setLayout(GridLayoutFactory.fillDefaults().create());
        argsGroup.setText(Messages.TransformationDialog_groupTitleArguments);
        listViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (description != null) {
                    description.dispose();
                    description = null;
                }
                if (argsPane != null) {
                    argsPane.dispose();
                    argsPane = null;
                }

                if (event.getSelection().isEmpty()) {
                    transformation = null;
                    argumentValues = null;
                    getButton(IDialogConstants.OK_ID).setEnabled(false);
                    return;
                }

                transformation = (Method)((IStructuredSelection)event.getSelection()).getFirstElement();
                transformationSelected(descGroup, argsGroup, parent);
            }
        });
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.editor.internal.util.BaseDialog#create()
     */
    @Override
    public void create() {
        super.create();
        // Select applicable method if editing a transformation mapping
        if (origTransformation != null) listViewer.setSelection(new StructuredSelection(origTransformation));
    }

    private void transformationSelected(Group descGroup,
                                        Group argsGroup,
                                        final Composite parent) {
        final Class<?>[] types = transformation.getParameterTypes();
        argumentValues = new String[types.length - 1];
        final Function annotation = transformation.getAnnotation(Function.class);

        description = new Browser(descGroup, SWT.BORDER);
        description.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        description.setText(annotation == null ? "" : annotation.description()); //$NON-NLS-1$

        if (types.length > 1) {
            maximizeDescription(description, false);
            argsPane = new Composite(argsGroup, SWT.NONE);
            argsPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
            argsPane.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
            argsPane.setBackground(argsPane.getDisplay().getSystemColor(SWT.COLOR_WHITE));
            Composite headerPane = new Composite(argsPane, SWT.BORDER);
            headerPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
            headerPane.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).numColumns(4).create());
            GridData nameData = GridDataFactory.fillDefaults().create();
            newTableColumnHeader(headerPane, Messages.TransformationDialog_NameColumn).setLayoutData(nameData);
            GridData valData = GridDataFactory.fillDefaults().create();
            newTableColumnHeader(headerPane, Messages.TransformationDialog_ValueColumn).setLayoutData(valData);
            GridData typeData = GridDataFactory.fillDefaults().create();
            newTableColumnHeader(headerPane, Messages.TransformationDialog_TypeColumn).setLayoutData(typeData);
            newTableColumnHeader(headerPane, Messages.TransformationDialog_DescriptionColumn).setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
            final int headerPaneHeight = headerPane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
            final ScrolledComposite scroller = new ScrolledComposite(argsPane, SWT.V_SCROLL);
            scroller.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
            scroller.setExpandHorizontal(true);
            scroller.setExpandVertical(true);
            scroller.setShowFocusedControl(true);
            Composite scrollerPane = new Composite(scroller, SWT.NONE);
            scroller.setContent(scrollerPane);
            scrollerPane.setLayout(GridLayoutFactory.fillDefaults().numColumns(4).spacing(0, 0).create());
            scrollerPane.setBackground(scroller.getBackground());
            // Create new components for selected transformation's arguments
            String[] mappingArgs =
                transformation.equals(origTransformation) ? ((TransformationMapping)mapping).getTransformationArguments() : null;
            for (int typeNdx = 1; typeNdx < types.length; typeNdx++) {
                final Class<?> type = types[typeNdx];
                final int argNdx = typeNdx - 1;
                final Arg argAnno =
                    annotation == null ? null : argNdx < annotation.args().length ? annotation.args()[argNdx] : null;
                if (argAnno != null) argumentValues[argNdx] = argAnno.defaultValue();
                Composite cell = newTableCell(scrollerPane, false, true, false);
                Label label = new Label(cell, SWT.NONE);
                label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.TOP).create());
                if (argAnno == null) label.setText(Messages.TransformationDialog_labelArgument + (argNdx + 1));
                else label.setText(argAnno.name() + (argAnno.defaultValue().isEmpty() ? "" : Messages.TransformationDialog_optional)); //$NON-NLS-1$
                nameData.widthHint = Math.max(nameData.widthHint, cell.computeSize(SWT.DEFAULT, SWT.DEFAULT).x);
                cell = newTableCell(scrollerPane, false, false, false);
                if (type == Boolean.class) {
                    final Button checkBox = new Button(cell, SWT.CHECK);
                    checkBox.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.TOP).create());
                    if (mappingArgs != null) {
                        String val = mappingArgs[argNdx].split("=")[1]; //$NON-NLS-1$
                        argumentValues[argNdx] = val;
					} else {
						argumentValues[argNdx] = Boolean.FALSE.toString();
                    }
					checkBox.setSelection(Boolean.valueOf(argumentValues[argNdx]));
                    checkBox.addSelectionListener(new SelectionAdapter() {

                        @Override
                        public void widgetSelected(SelectionEvent event) {
                            argumentValues[argNdx] = String.valueOf(checkBox.getSelection());
                            validate(annotation, types);
                        }
                    });
                } else {
                    final Text text = new Text(cell, SWT.BORDER);
                    text.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.TOP).create());
                    if (mappingArgs != null) {
                        String val = mappingArgs[argNdx].split("=")[1]; //$NON-NLS-1$
                        argumentValues[argNdx] = val;
                        if (argAnno == null || !argAnno.hideDefault() || !val.equals(argAnno.defaultValue())) text.setText(val);
                    }
                    text.addModifyListener(new ModifyListener() {

                        @Override
                        public void modifyText(ModifyEvent event) {
                            String val = text.getText();
                            argumentValues[argNdx] = val.isEmpty() && argAnno != null ? argAnno.defaultValue() : val;
                            validate(annotation, types);
                        }
                    });
                }
                valData.widthHint = Math.max(valData.widthHint, cell.computeSize(SWT.DEFAULT, SWT.DEFAULT).x);
                cell = newTableCell(scrollerPane, false, false, false);
                label = new Label(cell, SWT.NONE);
                label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.TOP).create());
                label.setText(type.getSimpleName());
                label.setToolTipText(Util.displayName(type));
                typeData.widthHint = Math.max(typeData.widthHint, cell.computeSize(SWT.DEFAULT, SWT.DEFAULT).x);
                cell = newTableCell(scrollerPane, true, false, true);
                label = new Label(cell, SWT.WRAP);
                label.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
                if (argAnno != null) label.setText(argAnno.description());
            }
            scroller.addControlListener(new ControlAdapter() {

                @Override
                public void controlResized(ControlEvent event) {
                    Point size = argsPane.computeSize(scroller.getClientArea().width, SWT.DEFAULT);
                    size.y -= headerPaneHeight;
                    scroller.setMinSize(size);
                    parent.layout();
                }
            });
            validate(annotation, types);
        } else {
            maximizeDescription(description, true);
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        }
        descGroup.layout();
        argsGroup.layout();
    }

    private void maximizeDescription(Control control,
                                     boolean maximize) {
        // Find splitter, then maximize its description pane accordingly
        for (Composite parent = control.getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof SashForm) {
                ((SashForm)parent).setMaximizedControl(maximize ? parent.getChildren()[0] : null);
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.editor.internal.util.BaseDialog#message()
     */
    @Override
    protected String message() {
		return Messages.bind(Messages.TransformationDialog_message, ((Model) mapping.getSource()).getName());
    }

    private Composite newTableCell(Composite parent,
                                   boolean grabHorizontally,
                                   boolean leftBorder,
                                   boolean rightBorder) {
        Composite cell = new Composite(parent, SWT.NONE);
        cell.setLayoutData(GridDataFactory.fillDefaults().grab(grabHorizontally, false).create());
        cell.setLayout(GridLayoutFactory.swtDefaults().create());
        cell.setBackground(argsPane.getBackground());
        cell.addPaintListener(Util.tableCellBorderPainter(leftBorder, rightBorder));
        return cell;
    }

    private Label newTableColumnHeader(Composite parent,
                                 String text) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(text);
        label.addPaintListener(Util.tableColumnHeaderBorderPainter());
        return label;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.fuse.transformation.editor.internal.util.BaseDialog#title()
     */
    @Override
    protected String title() {
        return (origTransformation == null ? Messages.TransformationDialog_AddTitle : Messages.TransformationDialog_EditTitle) + Messages.TransformationDialog_titleSuffix;
    }

    private void validate(Function annotation,
                          Class<?>[] types) {
        boolean valid = true;
        for (int ndx = 0; ndx < argumentValues.length; ndx++) {
            final Arg arg = annotation == null ? null : ndx < annotation.args().length ? annotation.args()[ndx] : null;
            if (!Util.valid(argumentValues[ndx], arg, types[ndx + 1])) {
                valid = false;
                break;
            }
        }
        getButton(IDialogConstants.OK_ID).setEnabled(valid);
    }
}
