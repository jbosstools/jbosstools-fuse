/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.fuse.transformation.FieldMapping;
import org.jboss.tools.fuse.transformation.MappingOperation;
import org.jboss.tools.fuse.transformation.MappingType;
import org.jboss.tools.fuse.transformation.TransformationMapping;
import org.jboss.tools.fuse.transformation.Variable;
import org.jboss.tools.fuse.transformation.dozer.BaseDozerMapping;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.util.BaseDialog;
import org.jboss.tools.fuse.transformation.editor.internal.util.FormatParser;
import org.jboss.tools.fuse.transformation.editor.internal.util.FormatParser.FormatSpecifier;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;
import org.jboss.tools.fuse.transformation.editor.transformations.Function;
import org.jboss.tools.fuse.transformation.editor.transformations.Function.Arg;
import org.jboss.tools.fuse.transformation.model.Model;

public class MappingDetailViewer extends MappingViewer {

    private static final String PREFERENCE_PREFIX = MappingDetailViewer.class.getName() + ".";

    private static final String STANDARD_FORMAT_PREFERENCE = PREFERENCE_PREFIX + "standardFormat";

    private final ScrolledComposite scroller;
    private final Map<Integer, Text> textById = new HashMap<>();
    private int nextFocusedTextId;
    private transient int focusedTextId = -1;
    private transient int focusedTextOffset;
    private boolean standardFormat;

    /**
     * @param config
     * @param parent
     * @param potentialDropTargets
     */
    public MappingDetailViewer(TransformationConfig config,
                               Composite parent,
                               List<PotentialDropTarget> potentialDropTargets) {
        super(config, potentialDropTargets);

        scroller = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);
        scroller.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        config.addListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                configEvent(event.getPropertyName(), event.getOldValue(), event.getNewValue());
            }
        });

        standardFormat = Activator.plugin().getPreferenceStore().getBoolean(STANDARD_FORMAT_PREFERENCE);
    }

    private void addCustomTransformation() throws Exception {
        final AddCustomTransformationDialog dlg = new AddCustomTransformationDialog(scroller.getShell(), config.project(), ((Model)mapping.getSource()).getType());
        if (dlg.open() != Window.OK) return;
        mapping = config.setTransformation((FieldMapping)mapping, dlg.type.getFullyQualifiedName(), dlg.method.getElementName());
        config.save();
    }

    private void addOrEditTransformation() throws Exception {
        final TransformationDialog dlg = new TransformationDialog(scroller.getShell(), mapping, config.project());
        if (dlg.open() != Window.OK) return;
        String[] args = new String[dlg.argumentValues.length];
        Class<?>[] types = dlg.transformation.getParameterTypes();
        for (int ndx = 0; ndx < dlg.argumentValues.length; ++ndx) {
            args[ndx] = types[ndx + 1].getName() + "=" + dlg.argumentValues[ndx];
        }
        mapping = config.setTransformation((FieldMapping)mapping, dlg.transformation.getDeclaringClass().getName(), dlg.transformation.getName(), args);
        config.save();
    }

    private void configEvent(String eventType,
                             Object oldValue,
                             Object newValue) {
        switch (eventType) {
            case TransformationConfig.MAPPING: {
                if (equals(mapping, oldValue)) scroller.setContent(null);
                else if (newValue != null) update((MappingOperation<?, ?>)newValue);
                break;
            }
            case TransformationConfig.MAPPING_TRANSFORMATION: {
                if (equals(mapping, oldValue)) update((MappingOperation<?, ?>)newValue);
                if (oldValue instanceof TransformationMapping && newValue instanceof TransformationMapping) {
                    Text text = textById.get(focusedTextId);
                    if (text != null) {
                        text.setFocus();
                        text.setSelection(focusedTextOffset);
                    }
                }
                break;
            }
            case TransformationConfig.MAPPING_SOURCE:
            case TransformationConfig.MAPPING_TARGET: {
                if (equals(mapping, oldValue)) update((MappingOperation<?, ?>)newValue);
                break;
            }
            case TransformationConfig.VARIABLE_VALUE: {
                variableValueUpdated((Variable)newValue);
                break;
            }
        }
    }

    private Composite createContainerPane(Composite parentPane,
                                          final boolean source,
                                          final Model model,
                                          Model parentModel,
                                          final List<Integer> indexes,
                                          final int indexesIndex) {
        if (parentModel == null) return parentPane;
        parentPane = createContainerPane(parentPane, source, model, parentModel.getParent(), indexes, indexesIndex - 1);
        Color color;
        if (parentModel.getParent() == null) color = Colors.CONTAINER;
        else if (parentPane.getForeground().equals(Colors.CONTAINER)) color = Colors.CONTAINER_ALTERNATE;
        else color = Colors.CONTAINER;
        parentPane = createRoundedPane(parentPane, color);
        if (parentModel.getParent() == null) parentPane.setLayoutData(GridDataFactory.swtDefaults().align(parentModel == config.getSourceModel() ? SWT.RIGHT : SWT.LEFT, SWT.CENTER).grab(true, true).create());
        else parentPane.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true).create());
        Composite pane = new Composite(parentPane, SWT.NONE);
        pane.setLayoutData(GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).create());
        GridLayout layout = GridLayoutFactory.fillDefaults().spacing(0, 0).create();
        pane.setLayout(layout);
        StyledText text = new StyledText(pane, SWT.READ_ONLY) {

            @Override
            public boolean isFocusControl() {
                return false;
            }
        };
        StyledString string = new StyledString(parentModel.getName());
        if (parentModel.isCollection()) {
            string.append('[', StyledString.QUALIFIER_STYLER);
            Integer index = indexes.get(indexesIndex);
            if (index == null) {
                string.append(" ]", StyledString.QUALIFIER_STYLER);
                text.setText(string.toString());
                text.setStyleRanges(string.getStyleRanges());
                text.setBackground(parentPane.getForeground());
            } else {
                layout.numColumns = 3;
                text.setText(string.toString());
                text.setStyleRanges(string.getStyleRanges());
                text.setBackground(parentPane.getForeground());
                final Spinner spinner = new Spinner(pane, SWT.BORDER);
                spinner.setSelection(index);
                spinner.addModifyListener(new ModifyListener() {

                    @Override
                    public void modifyText(ModifyEvent event) {
                        updateIndex(source, model, indexes, indexesIndex, spinner.getSelection());
                    }
                });
                text = new StyledText(pane, SWT.READ_ONLY) {

                    @Override
                    public boolean isFocusControl() {
                        return false;
                    }
                };
                string = new StyledString("]", StyledString.QUALIFIER_STYLER);
                text.setText(string.toString());
                text.setStyleRanges(string.getStyleRanges());
                text.setBackground(parentPane.getForeground());
            }
        } else {
            text.setText(string.toString());
            text.setBackground(parentPane.getForeground());
        }
        return parentPane;
    }

    private Composite createRoundedPane(Composite parent,
                                        Color color) {
        final Composite pane = new Composite(parent, SWT.NONE);
        pane.setLayout(GridLayoutFactory.swtDefaults().create());
        pane.setBackground(parent.getForeground());
        pane.setForeground(color);
        pane.addPaintListener(Util.roundedRectanglePainter(10, pane.getForeground()));
        return pane;
    }

    private void createSourcePane(Composite parent) {
        ControlWithMenuPane propPane = new ControlWithMenuPane(parent) {

            @Override
            void createControl() {
                createSourcePropertyPane(this, SWT.NONE);
                sourcePropPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
            }
        };
        propPane.create();
        propPane.addMenuItem("Set property", new MenuItemHandler() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    setProperty(true);
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });
        propPane.addMenuItem("Set variable", new MenuItemHandler() {

            @Override
            boolean enabled() {
                return mapping.getType() != MappingType.TRANSFORMATION;
            }

            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    setVariable();
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });
        propPane.addMenuItem("Set expression", new MenuItemHandler() {

            @Override
            boolean enabled() {
                return mapping.getType() != MappingType.TRANSFORMATION;
            }

            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    setExpression();
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });
        propPane.addMenuItem("Add transformation", new MenuItemHandler() {

            @Override
            boolean enabled() {
                return mapping.getType() == MappingType.FIELD;
            }

            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    addOrEditTransformation();
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });
        propPane.addMenuItem("Add custom transformation", new MenuItemHandler() {

            @Override
            boolean enabled() {
                return mapping.getType() == MappingType.FIELD;
            }

            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    addCustomTransformation();
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });
        if (mapping != null && mapping.getSource() != null) {
            if (Util.modelsNeedDateFormat(mapping.getSource(), mapping.getTarget(), true)) {
                propPane.addMenuItem("Set date format", new MenuItemHandler() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                        try {
                            // open dialog to select date format
                            setDateFormat(true);
                        } catch (final Exception e) {
                            Activator.error(e);
                        }
                    }
                });
            }
        }
    }

    private void createTargetPane(Composite parent) {
        final Composite pane;
        if (mapping.getTarget() == null) pane = createContainerPane(parent, false, null, config.getTargetModel(), null, -1);
        else {
            Model model = (Model)mapping.getTarget();
            List<Integer> updateIndexes = Util.updateIndexes(mapping, model, mapping.getTargetIndex());
            pane = createContainerPane(parent, false, model, model.getParent(), updateIndexes, updateIndexes.size() - 1);
        }
        ControlWithMenuPane propPane = new ControlWithMenuPane(pane) {

            @Override
            void createControl() {
                createTargetPropertyPane(this);
                targetPropPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
            }
        };
        propPane.create();
        propPane.addMenuItem("Set property", new MenuItemHandler() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    setProperty(false);
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });
        if (mapping != null && mapping.getTarget() != null) {
            if (Util.modelsNeedDateFormat(mapping.getSource(), mapping.getTarget(), false)) {
                propPane.addMenuItem("Set date format", new MenuItemHandler() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                        try {
                            // open dialog to select date format
                            setDateFormat(false);
                        } catch (final Exception e) {
                            Activator.error(e);
                        }
                    }
                });
            }
        }
    }

    private void createTransformationParameterControl(Composite parent,
                                                      final Class<?> type,
                                                      final Arg argAnno,
                                                      final TransformationMapping transformationMapping,
                                                      final String[] mappingArgs,
                                                      final int argNdx) {
        String val = mappingArgs[argNdx].split("=")[1];
        Control control;
        if (type == Boolean.class) {
            final Button checkBox = new Button(parent, SWT.CHECK);
            checkBox.setSelection(Boolean.valueOf(val));
            checkBox.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent event) {
                    updateTransformationArgument(transformationMapping, mappingArgs, argNdx, argAnno, type, String.valueOf(checkBox.getSelection()));
                }
            });
            control = checkBox;
        } else {
            final Text text = new Text(parent, SWT.BORDER);
            if (argAnno == null || !argAnno.hideDefault() || !val.equals(argAnno.defaultValue())) text.setText(val);
            text.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent event) {
                    focusedTextOffset = text.getCaretPosition();
                    updateTransformationArgument(transformationMapping, mappingArgs, argNdx, argAnno, type, text.getText());
                }
            });
            final int id = nextFocusedTextId++;
            textById.put(id, text);
            text.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(FocusEvent event) {
                    focusedTextId = id;
                }

                @Override
                public void focusLost(FocusEvent event) {
                    focusedTextId = -1;
                }
            });
            control = text;
        }
        if (argAnno == null) control.setToolTipText(Util.name(type));
        else {
            StringBuilder builder = new StringBuilder(argAnno.name());
            builder.append(" <");
            builder.append(Util.name(type));
            builder.append(">");
            if (!argAnno.defaultValue().isEmpty()) builder.append(" (optional)");
            builder.append(": ");
            builder.append(argAnno.description());
            control.setToolTipText(builder.toString());
        }
    }

    private void createTransformationSourcePane(Composite parent) {
        Model sourceModel = ((Model)mapping.getSource());
        List<Integer> updateIndexes = Util.updateIndexes(mapping, sourceModel, mapping.getSourceIndex());
        parent = createContainerPane(parent, true, sourceModel, sourceModel.getParent(), updateIndexes, updateIndexes.size() - 1);
        final TransformationMapping xformMapping = (TransformationMapping)mapping;
        try {
            Method xform = transformation(xformMapping, sourceModel);
            final Function annotation = xform.getAnnotation(Function.class);
            final String[] mappingArgs = xformMapping.getTransformationArguments();
            final Class<?>[] types = xform.getParameterTypes();
            if (standardFormat || annotation == null || annotation.format().isEmpty()) {
                new TransformationControl(parent, xformMapping, annotation) {

                    @Override
                    void createTransformation(Composite parent,
                                              GridLayout layout) {
                        layout.numColumns = 4 + mappingArgs.length * 2;
                        Label label = new Label(parent, SWT.NONE);
                        label.setText(transformationMapping.getTransformationName() + "(");
                        setToolTipToTransformationDescription(label);
                        createSourcePane(parent);
                        for (int typeNdx = 1; typeNdx < types.length; typeNdx++) {
                            new Label(parent, SWT.NONE).setText(",");
                            int argNdx = typeNdx - 1;
                            Arg argAnno = annotation == null ? null : argNdx < annotation.args().length ? annotation.args()[argNdx] : null;
                            createTransformationParameterControl(parent, types[typeNdx], argAnno, transformationMapping, mappingArgs, argNdx);
                        }
                        new Label(parent, SWT.NONE).setText(")");
                    }
                }.create();
            } else {
                new TransformationControl(parent, xformMapping, annotation) {

                    @Override
                    void createTransformation(Composite parent,
                                              GridLayout layout) {
                        Object[] parts = FormatParser.parse(annotation.format());
                        for (int ndx = 0; ndx < parts.length; ndx++) {
                            Object part = parts[ndx];
                            if (part instanceof FormatSpecifier) {
                                int typeNdx = ((FormatSpecifier)part).index() - 1;
                                if (typeNdx == 0) createSourcePane(parent);
                                else {
                                    int argNdx = typeNdx - 1;
                                    Arg argAnno = argNdx < annotation.args().length ? annotation.args()[argNdx] : null;
                                    createTransformationParameterControl(parent, types[typeNdx], argAnno, transformationMapping, mappingArgs, argNdx);
                                }
                            } else {
                                Label label = new Label(parent, SWT.NONE);
                                label.setText(part.toString().trim());
                                setToolTipToTransformationDescription(label);
                            }
                            layout.numColumns++;
                        }
                    }
                }.create();
            }
        } catch (ClassNotFoundException e) {
            new TransformationControl(parent, xformMapping, null) {

                @Override
                void createTransformation(Composite parent,
                                          GridLayout layout) {
                    layout.numColumns = 4;
                    Label label = new Label(parent, SWT.NONE);
                    label.setText(transformationMapping.getTransformationName() + "(");
                    label.setToolTipText(transformationMapping.getTransformationClass() + '.' + transformationMapping.getTransformationName());
                    createSourcePane(parent);
                    new Label(parent, SWT.NONE).setText(")");
                }
            }.create();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    private void removeTransformation() throws Exception {
        config.removeTransformation((TransformationMapping)mapping);
        config.save();
    }

    private void setDateFormat(boolean isSource) throws Exception {
        String dateFormatStr = Util.getDateFormat(sourcePropPane.getShell(), mapping, isSource);
        if (dateFormatStr != null && !dateFormatStr.trim().isEmpty()) {
            BaseDozerMapping dMapping = (BaseDozerMapping)mapping;
            if (isSource) {
                dMapping.setSourceDateFormat(dateFormatStr);
            } else if (!isSource) {
                dMapping.setTargetDateFormat(dateFormatStr);
            }
            config.save();
        }
    }

    private void setExpression() throws Exception {
        final ExpressionDialog dlg = new ExpressionDialog(sourcePropPane.getShell(), mapping, config.project());
        if (dlg.open() != Window.OK) return;
        Util.updateMavenDependencies(dlg.getLanguage().getDependencies(), config.project());
        Model targetModel = (Model)mapping.getTarget();
        List<Integer> indexes =
            targetModel != null && Util.isOrInCollection(targetModel)
                ? Util.updateIndexes(mapping, targetModel, mapping.getTargetIndex()) : null;
        mapping = config.setSourceExpression(mapping, dlg.getLanguage().getName(), dlg.getExpression(), indexes);
        config.save();
    }

    private void setProperty(boolean source) throws Exception {
        final PropertyDialog dlg = new PropertyDialog(sourcePropPane.getShell(), source ? config.getSourceModel() : config.getTargetModel(), config, mapping);
        if (dlg.open() != Window.OK) return;
        if (source) setSource(dlg.property);
        else setTarget(dlg.property);
    }

    private void setVariable() throws Exception {
        final VariableDialog dlg = new VariableDialog();
        if (dlg.open() != Window.OK) return;
        Model targetModel = (Model)mapping.getTarget();
        List<Integer> indexes =
            targetModel != null && Util.isOrInCollection(targetModel)
                ? Util.updateIndexes(mapping, targetModel, mapping.getTargetIndex()) : null;
        mapping = config.setSource(mapping, dlg.variable, null, indexes);
        config.save();
    }

    private Method transformation(TransformationMapping transformationMapping,
                                  Model sourceModel) throws ClassNotFoundException {
        for (Method method : Class.forName(transformationMapping.getTransformationClass()).getDeclaredMethods()) {
            Class<?>[] types = method.getParameterTypes();
            if (Modifier.isPublic(method.getModifiers()) && method.getName().equals(transformationMapping.getTransformationName()) && types.length > 0 && sourceModel.getType().equals(types[0].getName())) {
                return method;
            }
        }
        return null;
    }

    /**
     * @param mapping
     */
    public void update(MappingOperation<?, ?> mapping) {
        this.mapping = mapping;
        if (sourceDropTarget != null) dispose();
        textById.clear();
        nextFocusedTextId = 0;
        final Composite contentPane = new Composite(scroller, SWT.NONE);
        scroller.setContent(contentPane);
        contentPane.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
        contentPane.setBackground(scroller.getBackground());
        contentPane.setForeground(contentPane.getBackground());
        if (mapping.getType() == MappingType.TRANSFORMATION) createTransformationSourcePane(contentPane);
        else {
            final Composite pane;
            if (mapping.getSource() instanceof Model) {
                Model sourceModel = ((Model)mapping.getSource());
                List<Integer> updateIndexes = Util.updateIndexes(mapping, sourceModel, mapping.getSourceIndex());
                pane = createContainerPane(contentPane, true, sourceModel, sourceModel.getParent(), updateIndexes, updateIndexes.size() - 1);
            } else pane = createContainerPane(contentPane, true, null, config.getSourceModel(), null, -1);
            createSourcePane(pane);
        }
        final Label mapsToLabel = new Label(contentPane, SWT.NONE);
        mapsToLabel.setImage(Images.MAPPED);
        mapsToLabel.setBackground(contentPane.getBackground());
        createTargetPane(contentPane);
        scroller.setMinSize(contentPane.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        contentPane.layout();
    }

    private void updateIndex(boolean source,
                             Model model,
                             List<Integer> indexes,
                             int indexesIndex,
                             int index) {
        int origIndex = indexes.get(indexesIndex);
        if (index == origIndex) return;
        indexes.set(indexesIndex, index);
        if (source)
            config.setSource(mapping, model, indexes, Util.updateIndexes(mapping, mapping.getTarget(), mapping.getTargetIndex()));
        else config.setTarget(mapping, model, Util.updateIndexes(mapping, mapping.getSource(), mapping.getSourceIndex()), indexes);
        try {
            config.save();
        } catch (Exception e) {
            Activator.error(e);
        }
    }

    private void updateTransformationArgument(TransformationMapping mapping,
                                              String[] transformationArguments,
                                              int index,
                                              Arg argAnnotation,
                                              Class<?> type,
                                              String value) {
        if (Util.valid(value, argAnnotation, type)) {
            transformationArguments[index] = type.getName() + "=" + (value.isEmpty() ? argAnnotation.defaultValue() : value);
            mapping = config.setTransformation(mapping, mapping.getTransformationClass(), mapping.getTransformationName(), transformationArguments);
            try {
                config.save();
            } catch (Exception e) {
                Activator.error(e);
            }
        }
    }

    private abstract class ControlWithMenuPane extends Composite {

        final Map<String, MenuItemHandler> menuItems = new LinkedHashMap<>();

        ControlWithMenuPane(final Composite parent) {
            super(parent, SWT.NONE);
            setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
            setLayout(GridLayoutFactory.fillDefaults().spacing(1, 0).numColumns(2).create());
            setBackground(parent.getForeground());
        }

        void addMenuItem(final String item,
                         final MenuItemHandler handler) {
            menuItems.put(item, handler);
        }

        void create() {
            createControl();
            createMenuArrow();
        }

        abstract void createControl();

        Label createMenuArrow() {
            final Label menuArrow = new Label(this, SWT.NONE);
            menuArrow.setImage(Images.MENU);
            menuArrow.setLayoutData(GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.BOTTOM).create());
            menuArrow.setBackground(getBackground());
            menuArrow.setImage(Images.MENU);
            MouseAdapter listener = new MouseAdapter() {

                @Override
                public void mouseUp(final MouseEvent event) {
                    popupMenu(menuArrow, (Control)event.getSource(), event.x, event.y);
                }
            };
            menuArrow.addMouseListener(listener);
            addMouseListener(listener);
            return menuArrow;
        }

        void popupMenu(Label menuArrow,
                       Control source,
                       int x,
                       int y) {
            Point size = source.getSize();
            if (x < 0 || x > size.x || y < 0 || y > size.y) return;
            Menu popupMenu = new Menu(menuArrow);
            menuArrow.setMenu(popupMenu);
            for (final Entry<String, MenuItemHandler> entry : menuItems.entrySet()) {
                final MenuItem item = new MenuItem(popupMenu, SWT.NONE);
                item.setText(entry.getKey());
                item.addSelectionListener(entry.getValue());
                item.setEnabled(entry.getValue().enabled());
            }
            popupMenu.setLocation(toDisplay(menuArrow.getBounds().x, getBounds().height + 5));
            popupMenu.setVisible(true);
        }
    }

    abstract class MenuItemHandler extends SelectionAdapter {

        boolean enabled() {
            return true;
        }

        @Override
        public final void widgetDefaultSelected(final SelectionEvent event) {}

        @Override
        public abstract void widgetSelected(SelectionEvent event);
    }

    private abstract class TransformationControl extends ControlWithMenuPane {

        TransformationMapping transformationMapping;
        Function annotation;

        TransformationControl(Composite parent,
                              TransformationMapping transformationMapping,
                              Function annotation) {
            super(parent);
            this.transformationMapping = transformationMapping;
            this.annotation = annotation;
        }

        @Override
        void createControl() {
            final Composite pane = createRoundedPane(this, Colors.TRANSFORMATION);
            pane.setBackground(pane.getParent().getBackground());
            GridLayout layout = GridLayoutFactory.swtDefaults().create();
            pane.setLayout(layout);
            setToolTipToTransformationDescription(pane);
            createTransformation(pane, layout);
        }

        @Override
        Label createMenuArrow() {
            final Label menu = super.createMenuArrow();
            setToolTipToTransformationDescription(menu);
            if (annotation != null) {
                addMenuItem("Edit transformation", new MenuItemHandler() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                        try {
                            addOrEditTransformation();
                        } catch (final Exception e) {
                            Activator.error(e);
                        }
                    }
                });
                addMenuItem("Show " + (standardFormat ? "user-friendly" : "standard") + " formatting", new MenuItemHandler() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                        try {
                            standardFormat = !standardFormat;
                            Activator.plugin().getPreferenceStore().setValue(STANDARD_FORMAT_PREFERENCE, standardFormat);
                            update(mapping);
                        } catch (final Exception e) {
                            Activator.error(e);
                        }
                    }
                });
            }
            addMenuItem("Remove transformation", new MenuItemHandler() {

                @Override
                public void widgetSelected(final SelectionEvent event) {
                    try {
                        removeTransformation();
                    } catch (final Exception e) {
                        Activator.error(e);
                    }
                }
            });
            return menu;
        }

        abstract void createTransformation(Composite parent,
                                           GridLayout layout);

        void setToolTipToTransformationDescription(Control control) {
            if (annotation == null) {
                control.setToolTipText(transformationMapping.getTransformationClass() + "." + transformationMapping.getTransformationName());
            } else control.setToolTipText(annotation.description());
        }
    }

    final class VariableDialog extends BaseDialog {

        Variable variable;

        VariableDialog() {
            super(sourcePropPane.getShell());
            if (mapping.getSource() instanceof Variable) {
                this.variable = (Variable)mapping.getSource();
            }
        }

        @Override
        protected void constructContents(final Composite parent) {
            parent.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
            final Label label = new Label(parent, SWT.NONE);
            label.setText("Variable:");
            final ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
            comboViewer.setContentProvider(new ArrayContentProvider());
            comboViewer.setLabelProvider(new LabelProvider() {

                @Override
                public Image getImage(final Object element) {
                    return Images.VARIABLE;
                }

                @Override
                public String getText(final Object element) {
                    return ((Variable)element).getName();
                }
            });

            comboViewer.getCombo().addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent event) {
                    final IStructuredSelection selection = (IStructuredSelection)comboViewer.getSelection();
                    variable = (Variable)selection.getFirstElement();
                    validate();
                }
            });

            comboViewer.setInput(config.getVariables());
            if (variable != null) {
                comboViewer.setSelection(new StructuredSelection(variable));
            }
        }

        @Override
        protected String message() {
            return "Select a variable.";
        }

        @Override
        protected String title() {
            return "Set Variable";
        }

        void validate() {
            getButton(IDialogConstants.OK_ID).setEnabled(variable != null);
        }
    }
}
