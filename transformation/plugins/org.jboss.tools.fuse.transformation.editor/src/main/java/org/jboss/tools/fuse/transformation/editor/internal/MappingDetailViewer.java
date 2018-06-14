/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
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
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.jboss.tools.fuse.transformation.core.FieldMapping;
import org.jboss.tools.fuse.transformation.core.MappingOperation;
import org.jboss.tools.fuse.transformation.core.MappingType;
import org.jboss.tools.fuse.transformation.core.TransformationMapping;
import org.jboss.tools.fuse.transformation.core.Variable;
import org.jboss.tools.fuse.transformation.core.model.Model;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.internal.util.BaseDialog;
import org.jboss.tools.fuse.transformation.editor.internal.util.FormatParser;
import org.jboss.tools.fuse.transformation.editor.internal.util.FormatParser.FormatSpecifier;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationManager;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationManager.Event;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;
import org.jboss.tools.fuse.transformation.editor.transformations.Function;
import org.jboss.tools.fuse.transformation.editor.transformations.Function.Arg;

public class MappingDetailViewer extends MappingViewer {

    private static final String PREFERENCE_PREFIX = MappingDetailViewer.class.getName() + "."; //$NON-NLS-1$

    public static final String TRANSFORMATION_BACKGROUND_PREFERENCE = PREFERENCE_PREFIX + "transformationBackground"; //$NON-NLS-1$
    public static final String TRANSFORMATION_FOREGROUND_PREFERENCE = PREFERENCE_PREFIX + "transformationForeground"; //$NON-NLS-1$
    public static final String TRANSFORMATION_USER_FRIENDLY_FORMAT_PREFERENCE =
        PREFERENCE_PREFIX + "transformationUserFriendlyFormat"; //$NON-NLS-1$

    private final ScrolledComposite scroller;
    private final Map<Integer, Text> textById = new HashMap<>();
    private int nextFocusedTextId;
    private transient int focusedTextId = -1;
    private transient int focusedTextOffset;

    /**
     * @param manager
     * @param parent
     * @param potentialDropTargets
     */
    public MappingDetailViewer(TransformationManager manager,
                               Composite parent,
                               List<PotentialDropTarget> potentialDropTargets) {
        super(manager, potentialDropTargets);

        scroller = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);
        scroller.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        manager.addListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                managerEvent(event.getPropertyName(), event.getOldValue(), event.getNewValue());
            }
        });
    }

    private void addCustomTransformation() throws Exception {
        AddCustomTransformationDialog dlg =
            new AddCustomTransformationDialog(scroller.getShell(), manager.project(), ((Model)mapping.getSource()).getType());
        if (dlg.open() != Window.OK) return;
        mapping = manager.setTransformation((FieldMapping)mapping, dlg.type.getFullyQualifiedName(), dlg.method.getElementName());
        manager.save();
    }

    private void addOrEditTransformation() throws Exception {
        TransformationDialog dlg = new TransformationDialog(scroller.getShell(), mapping, manager.project());
        if (dlg.open() != Window.OK) return;
        String[] args = new String[dlg.argumentValues.length];
        Class<?>[] types = dlg.transformation.getParameterTypes();
        for (int ndx = 0; ndx < dlg.argumentValues.length; ++ndx) {
            args[ndx] = types[ndx + 1].getName() + "=" + dlg.argumentValues[ndx]; //$NON-NLS-1$
        }
        mapping = manager.setTransformation((FieldMapping)mapping,
                                            dlg.transformation.getDeclaringClass().getName(),
                                            dlg.transformation.getName(),
                                            args);
        manager.save();
    }

    private Composite createContainerPane(Composite parentPane,
                                          final boolean source,
                                          Model parentModel,
                                          final List<Integer> indexes,
                                          final int indexesIndex) {
        if (parentModel == null) return parentPane;
        parentPane = createContainerPane(parentPane, source, parentModel.getParent(), indexes, indexesIndex - 1);
        Color color;
        if (parentModel.getParent() == null) color = Colors.CONTAINER;
        else if (parentPane.getForeground().equals(Colors.CONTAINER)) color = Colors.CONTAINER_ALTERNATE;
        else color = Colors.CONTAINER;
        parentPane = createRoundedPane(parentPane, color);
        if (parentModel.getParent() == null) {
            int hAlign = parentModel == manager.rootSourceModel() ? SWT.RIGHT : SWT.LEFT;
            parentPane.setLayoutData(GridDataFactory.swtDefaults().align(hAlign, SWT.CENTER).grab(true, true).create());
        } else parentPane.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true).create());
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
                string.append(" ]", StyledString.QUALIFIER_STYLER); //$NON-NLS-1$
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
                        updateIndex(source, indexes, indexesIndex, spinner.getSelection());
                    }
                });
                text = new StyledText(pane, SWT.READ_ONLY) {

                    @Override
                    public boolean isFocusControl() {
                        return false;
                    }
                };
                string = new StyledString("]", StyledString.QUALIFIER_STYLER); //$NON-NLS-1$
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
        Composite pane = new Composite(parent, SWT.NONE);
        pane.setLayout(GridLayoutFactory.swtDefaults().create());
        pane.setBackground(parent.getForeground());
        pane.setForeground(color);
        pane.addPaintListener(Util.roundedRectanglePainter(pane, 10));
        return pane;
    }

    private ControlWithMenuPane createSourcePane(Composite parent) {
        final ControlWithMenuPane propPane = new ControlWithMenuPane(parent) {

            @Override
            void createControl() {
                createSourcePropertyPane(this, SWT.NONE);
                sourcePropPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
            }
        };
        propPane.create();
        propPane.addMenuItem(Messages.MappingDetailViewer_menuItemSetproperty, new MenuItemHandler() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    setProperty(true);
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });
        propPane.addMenuItem(Messages.MappingDetailViewer_menuItemSetVariable, new MenuItemHandler() {

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
        propPane.addMenuItem(Messages.MappingDetailViewer_menuItemSetExpression, new MenuItemHandler() {

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
        propPane.addMenuItem(Messages.MappingDetailViewer_menuItemAddTransformation, new MenuItemHandler() {

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
        propPane.addMenuItem(Messages.MappingDetailViewer_mnuItemAddCustomTransformation, new MenuItemHandler() {

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
                propPane.addMenuItem(Messages.MappingDetailViewer_menuItemSetDateFormat, new MenuItemHandler() {

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
        return propPane;
    }

    private void createTargetPane(Composite parent) {
        Composite pane;
        if (mapping.getTarget() == null) {
        	pane = createContainerPane(parent, false, manager.rootTargetModel(), null, -1);
        }
        else {
            List<Integer> indexes = Util.targetUpdateIndexes(mapping);
            pane = createContainerPane(parent, false, ((Model)mapping.getTarget()).getParent(), indexes, indexes.size() - 1);
        }
        ControlWithMenuPane propPane = new ControlWithMenuPane(pane) {

            @Override
            void createControl() {
                createTargetPropertyPane(this);
                targetPropPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
            }
        };
        propPane.create();
        propPane.addMenuItem(Messages.MappingDetailViewer_menuItemSetproperty, new MenuItemHandler() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    setProperty(false);
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });
        if (mapping.getTarget() != null && Util.modelsNeedDateFormat(mapping.getSource(), mapping.getTarget(), false)) {
        	propPane.addMenuItem(Messages.MappingDetailViewer_menuItemSetDateFormat, new MenuItemHandler() {

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

    private void createTransformationParameterControl(Composite parent,
                                                      final Class<?> type,
                                                      final Arg argAnno,
                                                      final TransformationMapping transformationMapping,
                                                      final String[] mappingArgs,
                                                      final int argNdx) {
        String val = mappingArgs[argNdx].split("=")[1]; //$NON-NLS-1$
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
        if (argAnno == null) control.setToolTipText(Util.displayName(type));
        else {
            StringBuilder builder = new StringBuilder(argAnno.name());
            builder.append(" <"); //$NON-NLS-1$
            builder.append(Util.displayName(type));
            builder.append(">"); //$NON-NLS-1$
            if (!argAnno.defaultValue().isEmpty()) builder.append(Messages.MappingDetailViewer_optional);
            builder.append(": "); //$NON-NLS-1$
            builder.append(argAnno.description());
            control.setToolTipText(builder.toString());
        }
    }

    private void createTransformationSourcePane(Composite parent) {
        Model sourceModel = ((Model)mapping.getSource());
        List<Integer> indexes = Util.sourceUpdateIndexes(mapping);
        parent = createContainerPane(parent, true, sourceModel.getParent(), indexes, indexes.size() - 1);
        final TransformationMapping xformMapping = (TransformationMapping)mapping;
        RGB rgb = PreferenceConverter.getColor(Activator.preferences(), TRANSFORMATION_FOREGROUND_PREFERENCE);
        final Color foreground = Activator.color(rgb.red, rgb.green, rgb.blue);
        try {
            Method xform = transformation(xformMapping, sourceModel);
            final Function annotation = xform.getAnnotation(Function.class);
            final String[] mappingArgs = xformMapping.getTransformationArguments();
            final Class<?>[] types = xform.getParameterTypes();
            if (!Activator.preferences().getBoolean(TRANSFORMATION_USER_FRIENDLY_FORMAT_PREFERENCE)
                || annotation == null || annotation.format().isEmpty()) {
                new TransformationControl(parent, xformMapping, annotation) {

                    @Override
                    void createTransformation(Composite parent,
                                              GridLayout layout) {
                        layout.numColumns = 4 + mappingArgs.length * 2;
                        Label label = new Label(parent, SWT.NONE);
                        label.setText(transformationMapping.getTransformationName() + "("); //$NON-NLS-1$
                        setToolTipToTransformationDescription(label);
                        label.setForeground(foreground);
                        createSourcePane(parent).setMenuArrowColor(foreground);
                        for (int typeNdx = 1; typeNdx < types.length; typeNdx++) {
                            new Label(parent, SWT.NONE).setText(","); //$NON-NLS-1$
                            int argNdx = typeNdx - 1;
                            Arg argAnno = annotation == null ? null : argNdx < annotation.args().length ? annotation.args()[argNdx] : null;
                            createTransformationParameterControl(parent, types[typeNdx], argAnno, transformationMapping, mappingArgs, argNdx);
                        }
                        label = new Label(parent, SWT.NONE);
                        label.setText(")"); //$NON-NLS-1$
                        label.setForeground(foreground);
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
                                if (typeNdx == 0) createSourcePane(parent).setMenuArrowColor(foreground);
                                else {
                                    int argNdx = typeNdx - 1;
                                    Arg argAnno = argNdx < annotation.args().length ? annotation.args()[argNdx] : null;
                                    createTransformationParameterControl(parent, types[typeNdx], argAnno, transformationMapping, mappingArgs, argNdx);
                                }
                            } else {
                                Label label = new Label(parent, SWT.NONE);
                                label.setText(part.toString().trim());
                                label.setForeground(foreground);
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
                    label.setText(transformationMapping.getTransformationName() + "("); //$NON-NLS-1$
                    label.setToolTipText(transformationMapping.getTransformationClass() + '.' + transformationMapping.getTransformationName());
                    label.setForeground(foreground);
                    createSourcePane(parent).setMenuArrowColor(foreground);
                    label = new Label(parent, SWT.NONE);
                    label.setText(")"); //$NON-NLS-1$
                    label.setForeground(foreground);
                }
            }.create();
        }
    }

    private void managerEvent(String eventType,
                              Object oldValue,
                              Object newValue) {
        if (eventType.equals(Event.MAPPING.name())) {
            if (mappingsEqual(mapping, oldValue)) scroller.setContent(null);
            else if (newValue != null) update((MappingOperation<?, ?>)newValue);
        } else if (eventType.equals(Event.MAPPING_TRANSFORMATION.name())) {
            if (mappingsEqual(mapping, oldValue)) update((MappingOperation<?, ?>)newValue);
            if (oldValue instanceof TransformationMapping && newValue instanceof TransformationMapping) {
                // Argument updated
                Text text = textById.get(focusedTextId);
                if (text != null) {
                    text.setFocus();
                    text.setSelection(focusedTextOffset);
                }
            }
        } else if (eventType.equals(Event.MAPPING_SOURCE.name()) || eventType.equals(Event.MAPPING_TARGET.name())) {
            if (mappingsEqual(mapping, oldValue)) update((MappingOperation<?, ?>)newValue);
        } else if (eventType.equals(Event.VARIABLE_VALUE.name())) variableValueUpdated((Variable)newValue);
    }

    private void removeTransformation() throws Exception {
        manager.removeTransformation((TransformationMapping)mapping);
        manager.save();
    }

    private void setDateFormat(boolean isSource) throws Exception {
        String dateFormatStr = Util.getDateFormat(sourcePropPane.getShell(), mapping, isSource);
        if (dateFormatStr != null && !dateFormatStr.trim().isEmpty()) {
            if (isSource) {
                mapping.setSourceDateFormat(dateFormatStr);
            } else if (!isSource) {
                mapping.setTargetDateFormat(dateFormatStr);
            }
            manager.save();
        }
    }

    private void setExpression() throws Exception {
        final ExpressionDialog dlg = new ExpressionDialog(sourcePropPane.getShell(), mapping, manager.project());
        if (dlg.open() != Window.OK) return;
        new MavenUtils().updateMavenDependencies(dlg.getLanguage().getDependencies());
        mapping = manager.setExpression(mapping, dlg.getLanguage().getName(), dlg.getExpression());
        manager.save();
    }

    private void setProperty(boolean source) throws Exception {
        PropertyDialog dlg =
            new PropertyDialog(sourcePropPane.getShell(),
                               source ? manager.rootSourceModel() : manager.rootTargetModel(),
                               manager, mapping);
        if (dlg.open() != Window.OK) return;
        if (source) setSource(dlg.property);
        else setTarget(dlg.property);
    }

    private void setVariable() throws Exception {
        VariableDialog dlg = new VariableDialog();
        if (dlg.open() != Window.OK) return;
        mapping = manager.setSource(mapping, dlg.variable);
        manager.save();
    }

    private Method transformation(TransformationMapping transformationMapping,
                                  Model sourceModel) throws ClassNotFoundException {
        for (Method method : Class.forName(transformationMapping.getTransformationClass()).getDeclaredMethods()) {
            Class<?>[] types = method.getParameterTypes();
            if (Modifier.isPublic(method.getModifiers())
                && method.getName().equals(transformationMapping.getTransformationName())
                && types.length > 0
                && sourceModel.getType().equals(types[0].getName())) return method;
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
        Composite contentPane = new Composite(scroller, SWT.NONE);
        scroller.setContent(contentPane);
        contentPane.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
        contentPane.setBackground(scroller.getBackground());
        contentPane.setForeground(contentPane.getBackground());
        if (mapping.getType() == MappingType.TRANSFORMATION) createTransformationSourcePane(contentPane);
        else {
            Composite pane;
            if (mapping.getSource() instanceof Model) {
                List<Integer> indexes = Util.sourceUpdateIndexes(mapping);
                pane =
                    createContainerPane(contentPane, true, ((Model)mapping.getSource()).getParent(), indexes, indexes.size() - 1);
            } else pane = createContainerPane(contentPane, true, manager.rootSourceModel(), null, -1);
            createSourcePane(pane);
        }
        Label mapsToLabel = new Label(contentPane, SWT.NONE);
        mapsToLabel.setImage(Images.MAPPED);
        mapsToLabel.setBackground(contentPane.getBackground());
        createTargetPane(contentPane);
        scroller.setMinSize(contentPane.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        contentPane.layout();
    }

    private void updateIndex(boolean source,
                             List<Integer> indexes,
                             int indexesIndex,
                             int index) {
        int origIndex = indexes.get(indexesIndex);
        if (index == origIndex) return;
        indexes.set(indexesIndex, index);
        if (source) mapping.setSourceIndex(indexes);
        else mapping.setTargetIndex(indexes);
        try {
            manager.save();
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
            transformationArguments[index] = type.getName() + "=" + (value.isEmpty() ? argAnnotation.defaultValue() : value); //$NON-NLS-1$
            manager.setTransformation(mapping,
            		mapping.getTransformationClass(),
            		mapping.getTransformationName(),
            		transformationArguments);
            try {
                manager.save();
            } catch (Exception e) {
                Activator.error(e);
            }
        }
    }

    private abstract class ControlWithMenuPane extends Composite {

        private final Map<String, MenuItemHandler> menuItems = new LinkedHashMap<>();
        Label menuArrow;
        private Color menuArrowColor;

        private ControlWithMenuPane(Composite parent) {
            super(parent, SWT.NONE);
            setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
            setLayout(GridLayoutFactory.fillDefaults().spacing(1, 0).numColumns(2).create());
            setBackground(parent.getForeground());
        }

        void addMenuItem(String item,
                         MenuItemHandler handler) {
            menuItems.put(item, handler);
        }

        void create() {
            createControl();
            createMenuArrow();
        }

        abstract void createControl();

        void createMenuArrow() {
            menuArrow = new Label(this, SWT.NONE);
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
            menuArrow.addPaintListener(new PaintListener() {

                @Override
                public void paintControl(PaintEvent event) {
                    if (menuArrowColor == null) return;
                    event.gc.setForeground(menuArrowColor);
                    Rectangle bounds = ((Control)event.widget).getBounds();
                    for (int x = bounds.width / 2, x2 = x, y = bounds.height - 1; x >= 0; x--, x2++, y--) {
                        event.gc.drawLine(x, y, x2, y);
                    }
                }
            });
        }

        private void popupMenu(Label menuArrow,
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

        void setMenuArrowColor(Color menuArrowColor) {
            this.menuArrowColor = menuArrowColor;
        }
    }

    abstract class MenuItemHandler extends SelectionAdapter {

        boolean enabled() {
            return true;
        }

        @Override
        public final void widgetDefaultSelected(SelectionEvent event) {}

        @Override
        public abstract void widgetSelected(SelectionEvent event);
    }

    private abstract class TransformationControl extends ControlWithMenuPane {

        TransformationMapping transformationMapping;
        private Function annotation;

        private TransformationControl(Composite parent,
                                      TransformationMapping transformationMapping,
                                      Function annotation) {
            super(parent);
            this.transformationMapping = transformationMapping;
            this.annotation = annotation;
        }

        @Override
        void createControl() {
            RGB rgb = PreferenceConverter.getColor(Activator.preferences(), TRANSFORMATION_BACKGROUND_PREFERENCE);
            final Composite pane = createRoundedPane(this, Activator.color(rgb.red, rgb.green, rgb.blue));
            pane.setBackground(pane.getParent().getBackground());
            GridLayout layout = GridLayoutFactory.swtDefaults().create();
            pane.setLayout(layout);
            setToolTipToTransformationDescription(pane);
            createTransformation(pane, layout);
            final IPropertyChangeListener listener = new IPropertyChangeListener() {

                @Override
                public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
                    if (event.getProperty().equals(TRANSFORMATION_USER_FRIENDLY_FORMAT_PREFERENCE)
                        || event.getProperty().equals(TRANSFORMATION_BACKGROUND_PREFERENCE)
                        || event.getProperty().equals(TRANSFORMATION_FOREGROUND_PREFERENCE))
                        MappingDetailViewer.this.update(mapping);
                }
            };
            Activator.preferences().addPropertyChangeListener(listener);
            addDisposeListener(new DisposeListener() {

                @Override
                public void widgetDisposed(DisposeEvent event) {
                    Activator.preferences().removePropertyChangeListener(listener);
                }
            });
        }

        @Override
        void createMenuArrow() {
            super.createMenuArrow();
            setToolTipToTransformationDescription(menuArrow);
            if (annotation != null) {
                addMenuItem(Messages.MappingDetailViewer_menuItemEditTransformation, new MenuItemHandler() {

                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        try {
                            addOrEditTransformation();
                        } catch (final Exception e) {
                            Activator.error(e);
                        }
                    }
                });
            }
            addMenuItem(Messages.MappingDetailViewer_menuItemRemoveTransformation, new MenuItemHandler() {

                @Override
                public void widgetSelected(SelectionEvent event) {
                    try {
                        removeTransformation();
                    } catch (final Exception e) {
                        Activator.error(e);
                    }
                }
            });
        }

        abstract void createTransformation(Composite parent,
                                           GridLayout layout);

        void setToolTipToTransformationDescription(Control control) {
            if (annotation == null)
                control.setToolTipText(transformationMapping.getTransformationClass() + "." //$NON-NLS-1$
                                       + transformationMapping.getTransformationName());
            else control.setToolTipText(annotation.description());
        }
    }

    class VariableDialog extends BaseDialog {

        Variable variable;

        VariableDialog() {
            super(sourcePropPane.getShell());
            if (mapping.getSource() instanceof Variable) this.variable = (Variable)mapping.getSource();
        }

        @Override
        protected void constructContents(final Composite parent) {
            parent.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
            Label label = new Label(parent, SWT.NONE);
            label.setText(Messages.MappingDetailViewer_labelVariable);
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
                    variable = (Variable)comboViewer.getStructuredSelection().getFirstElement();
                    validate();
                }
            });

            comboViewer.setInput(manager.variables());
            if (variable != null) comboViewer.setSelection(new StructuredSelection(variable));
        }

        @Override
        protected String message() {
            return Messages.MappingDetailViewer_message;
        }

        @Override
        protected String title() {
            return Messages.MappingDetailViewer_title;
        }

        void validate() {
            getButton(IDialogConstants.OK_ID).setEnabled(variable != null);
        }
    }
}
