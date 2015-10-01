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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.fuse.transformation.CustomMapping;
import org.jboss.tools.fuse.transformation.FieldMapping;
import org.jboss.tools.fuse.transformation.MappingOperation;
import org.jboss.tools.fuse.transformation.MappingType;
import org.jboss.tools.fuse.transformation.Variable;
import org.jboss.tools.fuse.transformation.dozer.BaseDozerMapping;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.function.Function;
import org.jboss.tools.fuse.transformation.editor.function.Function.Arg;
import org.jboss.tools.fuse.transformation.editor.internal.util.BaseDialog;
import org.jboss.tools.fuse.transformation.editor.internal.util.CanceledDialogException;
import org.jboss.tools.fuse.transformation.editor.internal.util.FormatParser;
import org.jboss.tools.fuse.transformation.editor.internal.util.FormatParser.FormatSpecifier;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;
import org.jboss.tools.fuse.transformation.model.Model;

public final class MappingDetailViewer extends MappingViewer {

    private static final String PREFERENCE_PREFIX = MappingDetailViewer.class.getName() + ".";

    private static final String STANDARD_FORMAT_PREFERENCE = PREFERENCE_PREFIX + "standardFormat";

    final ScrolledComposite scroller;
    final Point imageButtonLabelSize;
    final Map<Integer, Text> textById = new HashMap<>();
    int nextFocusedTextId;
    transient int focusedTextId = -1;
    transient int focusedTextOffset;
    boolean standardFormat;

    /**
     * @param config
     * @param parent
     * @param potentialDropTargets
     */
    public MappingDetailViewer(final TransformationConfig config,
                               final Composite parent,
                               final List<PotentialDropTarget> potentialDropTargets) {
        super(config, potentialDropTargets);

        scroller = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);
        scroller.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        final Label label = new Label(parent.getShell(), SWT.NONE);
        label.setImage(Images.MENU);
        imageButtonLabelSize = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        label.dispose();

        config.addListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                configEvent(event.getPropertyName(), event.getOldValue(), event.getNewValue());
            }
        });

        standardFormat = Activator.plugin().getPreferenceStore().getBoolean(STANDARD_FORMAT_PREFERENCE);
    }

    void addCustomFunction() throws Exception {
        final AddCustomFunctionDialog dlg =
            new AddCustomFunctionDialog(scroller.getShell(), config.project(), ((Model)mapping.getSource()).getType());
        if (dlg.open() != Window.OK) return;
        mapping = config.customizeMapping((FieldMapping)mapping, dlg.type.getFullyQualifiedName(), dlg.method.getElementName());
        config.save();
    }

    void addOrEditFunction() throws Exception {
        final FunctionDialog dlg = new FunctionDialog(scroller.getShell(), mapping, config.project());
        if (dlg.open() != Window.OK) return;
        String[] args = new String[dlg.argumentValues.length];
        Class<?>[] types = dlg.function.getParameterTypes();
        for (int ndx = 0; ndx < dlg.argumentValues.length; ++ndx) {
            args[ndx] = types[ndx + 1].getName() + "=" + dlg.argumentValues[ndx];
        }
        mapping = config.customizeMapping((FieldMapping)mapping, dlg.function.getDeclaringClass().getName(),
                                          dlg.function.getName(), args);
        config.save();
    }

    void configEvent(final String eventType,
                     final Object oldValue,
                     final Object newValue) {
        switch (eventType) {
            case TransformationConfig.MAPPING: {
                if (equals(mapping, oldValue)) scroller.setContent(null);
                else if (newValue != null) update((MappingOperation<?, ?>)newValue);
                break;
            }
            case TransformationConfig.MAPPING_CUSTOMIZE: {
                if (equals(mapping, oldValue)) update((MappingOperation<?, ?>)newValue);
                if (oldValue instanceof CustomMapping && newValue instanceof CustomMapping) {
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

    private Composite createContainerPane(Composite parent,
                                          final Model model,
                                          List<Integer> indexes) {
        if (model == null) return parent;
        parent = createContainerPane(parent, model.getParent(), parentIndexes(indexes));
        final Color color;
        if (model.getParent() == null) color = Colors.CONTAINER;
        else if (parent.getForeground().equals(Colors.CONTAINER)) color = Colors.CONTAINER_ALTERNATE;
        else color = Colors.CONTAINER;
        final Composite pane = createRoundedPane(parent, color);
        if (model.getParent() == null) {
            pane.setLayoutData(GridDataFactory.swtDefaults()
                                              .align(model == config.getSourceModel() ? SWT.RIGHT : SWT.LEFT, SWT.CENTER)
                                              .grab(true, true)
                                              .create());
        } else pane.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true).create());
        final Label label = new Label(pane, SWT.NONE);
        label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).create());
        final Integer index = indexes == null ? null : indexes.get(indexes.size() - 1);
        label.setText(model.getName() + (index == null ? "" : "[" + index + "]"));
        label.setBackground(pane.getForeground());
        return pane;
    }

    private void createFunctionArgumentControl(Composite parent,
                                               final Class<?> type,
                                               final Arg argAnno,
                                               final CustomMapping customMapping,
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
                    updateFunctionArgument(customMapping, mappingArgs, argNdx, argAnno, type,
                                           String.valueOf(checkBox.getSelection()));
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
                    updateFunctionArgument(customMapping, mappingArgs, argNdx, argAnno, type, text.getText());
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
        if (argAnno == null) control.setToolTipText(type.getName());
        else {
            StringBuilder builder = new StringBuilder(argAnno.name());
            builder.append(" <");
            builder.append(type.getName());
            builder.append(">");
            if (!argAnno.defaultValue().isEmpty()) builder.append(" (optional)");
            builder.append(": ");
            builder.append(argAnno.description());
            control.setToolTipText(builder.toString());
        }
    }

    private void createFunctionSourcePane(Composite parent) {
        Model sourceModel = ((Model)mapping.getSource());
        parent = createContainerPane(parent, sourceModel.getParent(), parentIndexes(mapping.getSourceIndex()));
        final CustomMapping customMapping = (CustomMapping)mapping;
        try {
            Method function = function(customMapping, sourceModel);
            final Function annotation = function.getAnnotation(Function.class);
            final String[] mappingArgs = customMapping.getFunctionArguments();
            final Class<?>[] types = function.getParameterTypes();
            if (standardFormat || annotation == null || annotation.format().isEmpty()) {
                new FunctionControl(parent, customMapping, annotation) {

                    @Override
                    void createFunction(Composite parent,
                                        GridLayout layout) {
                        layout.numColumns = 4 + mappingArgs.length * 2;
                        Label label = new Label(parent, SWT.NONE);
                        label.setText(customMapping.getFunctionName() + "(");
                        setToolTipToFunctionDescription(label);
                        createSourcePane(parent);
                        for (int typeNdx = 1; typeNdx < types.length; typeNdx++) {
                            new Label(parent, SWT.NONE).setText(",");
                            int argNdx = typeNdx - 1;
                            Arg argAnno =
                                annotation == null ? null : argNdx < annotation.args().length ? annotation.args()[argNdx] : null;
                            createFunctionArgumentControl(parent, types[typeNdx], argAnno, customMapping, mappingArgs, argNdx);
                        }
                        new Label(parent, SWT.NONE).setText(")");
                    }
                }.create();
            } else {
                new FunctionControl(parent, customMapping, annotation) {

                    @Override
                    void createFunction(Composite parent,
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
                                    createFunctionArgumentControl(parent, types[typeNdx], argAnno, customMapping, mappingArgs, argNdx);
                                }
                            } else {
                                Label label = new Label(parent, SWT.NONE);
                                label.setText(part.toString().trim());
                                setToolTipToFunctionDescription(label);
                            }
                            layout.numColumns++;
                        }
                    }
                }.create();
            }
        } catch (ClassNotFoundException e) {
            new FunctionControl(parent, customMapping, null) {

                @Override
                void createFunction(Composite parent,
                                    GridLayout layout) {
                    layout.numColumns = 4;
                    Label label = new Label(parent, SWT.NONE);
                    label.setText(customMapping.getFunctionName() + "(");
                    label.setToolTipText(customMapping.getFunctionClass() + '.' + customMapping.getFunctionName());
                    createSourcePane(parent);
                    new Label(parent, SWT.NONE).setText(")");
                }
            }.create();
        }
    }

    private Composite createRoundedPane(final Composite parent,
                                        final Color color) {
        final Composite pane = new Composite(parent, SWT.NONE);
        pane.setLayout(GridLayoutFactory.swtDefaults().create());
        pane.setBackground(parent.getForeground());
        pane.setForeground(color);
        pane.addPaintListener(Util.roundedRectanglePainter(10, pane.getForeground()));
        return pane;
    }

    private void createSourcePane(Composite parent) {
        ControlWithMenuPane fieldPane = new ControlWithMenuPane(parent) {

            @Override
            void createControl() {
                createSourceText(this, SWT.NONE);
                sourceText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
            }
        };
        fieldPane.create();
        fieldPane.addMenuItem("Set property", new MenuItemHandler() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    setField(true);
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });
        fieldPane.addMenuItem("Set variable", new MenuItemHandler() {

            @Override
            boolean enabled() {
                return mapping.getType() != MappingType.CUSTOM;
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
        fieldPane.addMenuItem("Set expression", new MenuItemHandler() {

            @Override
            boolean enabled() {
                return mapping.getType() != MappingType.CUSTOM;
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
        fieldPane.addMenuItem("Add function", new MenuItemHandler() {

            @Override
            boolean enabled() {
                return mapping.getType() == MappingType.FIELD;
            }

            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    addOrEditFunction();
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });
        fieldPane.addMenuItem("Add custom function", new MenuItemHandler() {

            @Override
            boolean enabled() {
                return mapping.getType() == MappingType.FIELD;
            }

            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    addCustomFunction();
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });
        if (mapping != null && mapping.getSource() != null) {
            if (Util.modelsNeedDateFormat(mapping.getSource(),
                                          mapping.getTarget(), true)) {
                fieldPane.addMenuItem("Set date format", new MenuItemHandler() {

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

    private void createTargetPane(final Composite parent) {
        final Composite pane;
        if (mapping.getTarget() == null) pane = createContainerPane(parent, config.getTargetModel(), null);
        else pane = createContainerPane(parent,
                                        ((Model)mapping.getTarget()).getParent(),
                                        parentIndexes(mapping.getTargetIndex()));
        ControlWithMenuPane fieldPane = new ControlWithMenuPane(pane) {

            @Override
            void createControl() {
                createTargetText(this);
                targetText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
            }
        };
        fieldPane.create();
        fieldPane.addMenuItem("Set property", new MenuItemHandler() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    setField(false);
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });
        if (mapping != null && mapping.getTarget() != null) {
            if (Util.modelsNeedDateFormat(mapping.getSource(),
                                          mapping.getTarget(), false)) {
                fieldPane.addMenuItem("Set date format", new MenuItemHandler() {

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

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        sourceDropTarget.dispose();
        targetDropTarget.dispose();
        super.finalize();
    }

    private Method function(CustomMapping customMapping,
                            Model sourceModel) throws ClassNotFoundException {
        for (Method method : Class.forName(customMapping.getFunctionClass()).getDeclaredMethods()) {
            Class<?>[] types = method.getParameterTypes();
            if (Modifier.isPublic(method.getModifiers())
                && method.getName().equals(customMapping.getFunctionName())
                && types.length > 0 && sourceModel.getType().equals(types[0].getName())) {
                return method;
            }
        }
        return null;
    }

    private List<Integer> parentIndexes(final List<Integer> indexes) {
        return indexes != null && indexes.size() > 1
            ? indexes.subList(0, indexes.size() - 1)
            : null;
    }

    private void removeFunction() throws Exception {
        config.uncustomizeMapping((CustomMapping)mapping);
        config.save();
    }

    private void setDateFormat(boolean isSource) throws Exception {
        String dateFormatStr = Util.getDateFormat(sourceText.getShell(), mapping, isSource);
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
        final ExpressionDialog dlg = new ExpressionDialog(sourceText.getShell(), mapping, config.project());
        if (dlg.open() != Window.OK) {
            return;
        }
        Util.updateMavenDependencies(dlg.getLanguage().getDependencies(), config.project());
        final Model targetModel = (Model)mapping.getTarget();
        try {
            final List<Integer> indexes =
                targetModel != null && Util.isOrInCollection(targetModel)
                    ? Util.indexes(sourceText.getShell(), targetModel, false)
                    : null;
            mapping =
                config.setSourceExpression(mapping, dlg.getLanguage().getName(), dlg.getExpression(), indexes);
            config.save();
        } catch (CanceledDialogException ignored) {}
    }

    void setField(final boolean source) throws Exception {
        final FieldDialog dlg =
            new FieldDialog(sourceText.getShell(), source ? config.getSourceModel() : config.getTargetModel(), config, mapping);
        if (dlg.open() != Window.OK)
            return;
        if (source) setSource(dlg.field);
        else setTarget(dlg.field);
    }

    void setVariable() throws Exception {
        final VariableDialog dlg = new VariableDialog();
        if (dlg.open() != Window.OK) {
            return;
        }
        final Model targetModel = (Model)mapping.getTarget();
        try {
            final List<Integer> indexes =
                targetModel != null && Util.isOrInCollection(targetModel)
                    ? Util.indexes(sourceText.getShell(), targetModel, false)
                    : null;
            mapping = config.setSource(mapping, dlg.variable, null, indexes);
            config.save();
        } catch (CanceledDialogException ignored) {}
    }

    /**
     * @param mapping
     */
    public void update(final MappingOperation<?, ?> mapping) {
        this.mapping = mapping;
        if (sourceDropTarget != null) dispose();
        textById.clear();
        nextFocusedTextId = 0;
        final Composite contentPane = new Composite(scroller, SWT.NONE);
        scroller.setContent(contentPane);
        contentPane.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
        contentPane.setBackground(scroller.getBackground());
        contentPane.setForeground(contentPane.getBackground());
        if (mapping.getType() == MappingType.CUSTOM) createFunctionSourcePane(contentPane);
        else {
            final Composite pane;
            if (mapping.getSource() instanceof Model) pane = createContainerPane(contentPane,
                                                                                 ((Model)mapping.getSource()).getParent(),
                                                                                 parentIndexes(mapping.getSourceIndex()));
            else pane = createContainerPane(contentPane, config.getSourceModel(), null);
            createSourcePane(pane);
        }
        final Label mapsToLabel = new Label(contentPane, SWT.NONE);
        mapsToLabel.setImage(Images.MAPPED);
        mapsToLabel.setBackground(contentPane.getBackground());
        createTargetPane(contentPane);
        scroller.setMinSize(contentPane.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        contentPane.layout();
    }

    private void updateFunctionArgument(CustomMapping mapping,
                                        String[] functionArguments,
                                        int index,
                                        Arg argAnnotation,
                                        Class<?> type,
                                        String value) {
        if (Util.valid(value, argAnnotation, type)) {
            functionArguments[index] = type.getName() + "=" + (value.isEmpty() ? argAnnotation.defaultValue() : value);
            mapping = config.customizeMapping(mapping, mapping.getFunctionClass(), mapping.getFunctionName(), functionArguments);
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
            setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
            setBackground(parent.getForeground());
        }

        void addMenuItem(final String item,
                         final MenuItemHandler handler) {
            menuItems.put(item, handler);
        }

        void create() {
            createControl();
            createMenu();
        }

        abstract void createControl();

        Label createMenu() {
            final Label menu = new Label(this, SWT.NONE);
            menu.setLayoutData(GridDataFactory.swtDefaults()
                                              .hint(imageButtonLabelSize)
                                              .align(SWT.BEGINNING, SWT.BOTTOM)
                                              .create());
            menu.setBackground(getBackground());
            menu.setImage(Images.MENU);
            menu.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseUp(final MouseEvent event) {
                    popupMenu(menu, event.x, event.y);
                }
            });
            return menu;
        }

        void popupMenu(final Label menuLabel,
                       final int xPos,
                       final int yPos) {
            final Point size = menuLabel.getSize();
            if (xPos < 0 || xPos > size.x || yPos < 0 || yPos > size.y) {
                return;
            }
            final Menu popupMenu = new Menu(menuLabel);
            menuLabel.setMenu(popupMenu);
            for (final Entry<String, MenuItemHandler> entry : menuItems.entrySet()) {
                final MenuItem item = new MenuItem(popupMenu, SWT.NONE);
                item.setText(entry.getKey());
                item.addSelectionListener(entry.getValue());
                item.setEnabled(entry.getValue().enabled());
            }
            popupMenu.setLocation(toDisplay(menuLabel.getLocation().x,
                                            menuLabel.getLocation().y + size.y + 5));
            popupMenu.setVisible(true);
        }
    }

    private abstract class FunctionControl extends ControlWithMenuPane {

        CustomMapping customMapping;
        Function annotation;

        FunctionControl(Composite parent,
                        CustomMapping customMapping,
                        Function annotation) {
            super(parent);
            this.customMapping = customMapping;
            this.annotation = annotation;
        }

        @Override
        void createControl() {
            final Composite pane = createRoundedPane(this, Colors.FUNCTION);
            pane.setBackground(pane.getParent().getBackground());
            GridLayout layout = GridLayoutFactory.swtDefaults().create();
            pane.setLayout(layout);
            setToolTipToFunctionDescription(pane);
            createFunction(pane, layout);
        }

        abstract void createFunction(Composite parent,
                                     GridLayout layout);

        @Override
        Label createMenu() {
            final Label menu = super.createMenu();
            setToolTipToFunctionDescription(menu);
            if (annotation != null) {
                addMenuItem("Edit function", new MenuItemHandler() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                        try {
                            addOrEditFunction();
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
            addMenuItem("Remove function", new MenuItemHandler() {

                @Override
                public void widgetSelected(final SelectionEvent event) {
                    try {
                        removeFunction();
                    } catch (final Exception e) {
                        Activator.error(e);
                    }
                }
            });
            return menu;
        }

        void setToolTipToFunctionDescription(Control control) {
            if (annotation == null) {
                control.setToolTipText(customMapping.getFunctionClass() + "." + customMapping.getFunctionName());
            } else control.setToolTipText(annotation.description());
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

    final class VariableDialog extends BaseDialog {

        Variable variable;

        VariableDialog() {
            super(sourceText.getShell());
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
                    final IStructuredSelection selection =
                        (IStructuredSelection)comboViewer.getSelection();
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
            return "Variable";
        }

        void validate() {
            getButton(IDialogConstants.OK_ID).setEnabled(variable != null);
        }
    }
}
