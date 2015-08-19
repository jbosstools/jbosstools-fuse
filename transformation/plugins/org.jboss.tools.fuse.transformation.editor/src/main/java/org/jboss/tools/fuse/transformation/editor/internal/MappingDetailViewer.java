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
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
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
import org.jboss.tools.fuse.transformation.editor.internal.util.BaseDialog;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;
import org.jboss.tools.fuse.transformation.model.Model;

/**
 *
 */
public final class MappingDetailViewer extends MappingViewer {

    final ScrolledComposite scroller;
    final Point imageButtonLabelSize;

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
    }

    void addCustomFunction() throws Exception {
        final AddCustomFunctionDialog dlg =
            new AddCustomFunctionDialog(scroller.getShell(),
                                        config.project(),
                                        ((Model)mapping.getSource()).getType());
        if (dlg.open() != Window.OK) {
            return;
        }
        mapping = config.customizeMapping((FieldMapping) mapping,
                                          dlg.type.getFullyQualifiedName(),
                                          dlg.method.getElementName());
        config.save();
    }

    void configEvent(final String eventType,
                     final Object oldValue,
                     final Object newValue) {
        switch (eventType) {
            case TransformationConfig.MAPPING: {
                if (equals(mapping, oldValue)) {
                    scroller.setContent(null);
                } else if (newValue != null) {
                    update((MappingOperation<?, ?>)newValue);
                }
                break;
            }
            case TransformationConfig.MAPPING_CUSTOMIZE:
            case TransformationConfig.MAPPING_SOURCE:
            case TransformationConfig.MAPPING_TARGET: {
                if (equals(mapping, oldValue)) {
                    update((MappingOperation<?, ?>)newValue);
                }
                break;
            }
            case TransformationConfig.VARIABLE_VALUE: {
                variableValueUpdated((Variable)newValue);
                break;
            }
            default: // ignore other cases
        }
    }

    private Composite createContainerPane(Composite parent,
                                          final Model model,
                                          List<Integer> indexes) {
        if (model == null) return parent;
        parent = createContainerPane(parent, model.getParent(), parentIndexes(indexes));
        final Color color;
        if (model.getParent() == null) color = Colors.CONTAINER;
        else if (parent.getForeground().equals(Colors.CONTAINER))
            color = Colors.CONTAINER_ALTERNATE;
        else color = Colors.CONTAINER;
        final Composite pane = createRoundedPane(parent, color);
        if (model.getParent() == null) {
            pane.setLayoutData(GridDataFactory.swtDefaults()
                                              .align(model == config.getSourceModel()
                                                     ? SWT.RIGHT
                                                     : SWT.LEFT,
                                                     SWT.CENTER)
                                              .grab(true, true)
                                              .create());
        } else pane.setLayoutData(GridDataFactory.swtDefaults()
                                                 .align(SWT.CENTER, SWT.CENTER)
                                                 .grab(true, true)
                                                 .create());
        final Label label = new Label(pane, SWT.NONE);
        label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).create());
        final Integer index = indexes == null ? null : indexes.get(indexes.size() - 1);
        label.setText(model.getName() + (index == null ? "" : "[" + index + "]"));
        label.setBackground(pane.getForeground());
        return pane;
    }

    private void createCustomSourcePane(final Composite parent) {
        final Composite pane =
            createRoundedPane(createContainerPane(parent,
                                                  ((Model)mapping.getSource()).getParent(),
                                                  parentIndexes(mapping.getSourceIndex())),
                              Colors.FUNCTION);
        pane.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
        new ControlWithMenuPane(pane) {

            @Override
            void constructControl() {
                final Label functionLabel = new Label(this, SWT.NONE);
                final CustomMapping customMapping = (CustomMapping)mapping;
                functionLabel.setText(customMapping.getMappingOperation());
                functionLabel.setToolTipText(customMapping.getMappingClass() + '.'
                                             + customMapping.getMappingOperation());
                addMenuItem("Remove custom function", new MenuItemHandler() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                        try {
                            removeCustomFunction();
                        } catch (final Exception e) {
                            Activator.error(e);
                        }
                    }
                });
            }
        };
        Label label = new Label(pane, SWT.NONE);
        label.setText("(");
        createSourcePane(pane);
        label = new Label(pane, SWT.NONE);
        label.setText(")");
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

    private void createSourcePane(final Composite parent) {
        new ControlWithMenuPane(parent) {

            @Override
            void constructControl() {
                createSourceText(this, SWT.NONE);
                sourceText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
                addMenuItem("Set field", new MenuItemHandler() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                        try {
                            setField(true);
                        } catch (final Exception e) {
                            Activator.error(e);
                        }
                    }
                });
                addMenuItem("Set variable", new MenuItemHandler() {

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
                addMenuItem("Set expression", new MenuItemHandler() {

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
                addMenuItem("Add custom function", new MenuItemHandler() {

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
                        addMenuItem("Set date format", new MenuItemHandler() {
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
        };
    }

    private void createTargetPane(final Composite parent) {
        final Composite pane;
        if (mapping.getTarget() == null)
            pane = createContainerPane(parent, config.getTargetModel(), null);
        else pane = createContainerPane(parent,
                                        ((Model)mapping.getTarget()).getParent(),
                                        parentIndexes(mapping.getTargetIndex()));
        new ControlWithMenuPane(pane) {

            @Override
            void constructControl() {
                createTargetText(this);
                targetText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
                addMenuItem("Set field", new MenuItemHandler() {

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
                        addMenuItem("Set date format", new MenuItemHandler() {
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
        };
    }

    @Override
    Text createText(final Composite parent,
                    final int style) {
        final Text text = super.createText(parent, style);
        text.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(final FocusEvent event) {
                text.selectAll();
            }
        });
        return text;
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

    private List<Integer> parentIndexes(final List<Integer> indexes) {
        return indexes != null && indexes.size() > 1
               ? indexes.subList(0, indexes.size() - 1)
               : null;
    }

    void removeCustomFunction() throws Exception {
        config.uncustomizeMapping((CustomMapping) mapping);
        config.save();
    }

    void setDateFormat(boolean isSource) throws Exception {
        String dateFormatStr = Util.getDateFormat(sourceText.getShell(), mapping, isSource);
        if (dateFormatStr != null && !dateFormatStr.trim().isEmpty()) {
            BaseDozerMapping dMapping = (BaseDozerMapping) mapping;
            if (isSource) {
                dMapping.setSourceDateFormat(dateFormatStr);
            } else if (!isSource) {
                dMapping.setTargetDateFormat(dateFormatStr);
            }
            config.save();
        }
    }

    @SuppressWarnings("restriction")
    void setExpression() throws Exception {
        final ExpressionDialog dlg = new ExpressionDialog(sourceText.getShell(), mapping, config.project());
        if (dlg.open() != Window.OK) {
            return;
        }
        Util.updateMavenDependencies(dlg.getLanguage().getDependencies(), config.project());
        final Model targetModel = (Model)mapping.getTarget();
        final List<Integer> indexes =
            targetModel != null && Util.isOrInCollection(targetModel)
            ? Util.indexes(sourceText.getShell(), targetModel, false)
            : null;
        mapping =
            config.setSourceExpression(mapping, dlg.getLanguage().getName(), dlg.getExpression(), indexes);
        config.save();
    }

    void setField(final boolean source) throws Exception {
        final FieldDialog dlg =
            new FieldDialog(source ? config.getSourceModel() : config.getTargetModel());
        if (dlg.open() != Window.OK) {
            return;
        }
        if (source) {
            setSource(dlg.field);
        } else {
            setTarget(dlg.field);
        }
    }

    void setVariable() throws Exception {
        final VariableDialog dlg = new VariableDialog();
        if (dlg.open() != Window.OK) {
            return;
        }
        final Model targetModel = (Model)mapping.getTarget();
        final List<Integer> indexes =
            targetModel != null && Util.isOrInCollection(targetModel)
            ? Util.indexes(sourceText.getShell(), targetModel, false)
            : null;
        mapping = config.setSource(mapping, dlg.variable, null, indexes);
        config.save();
    }

    /**
     * @param mapping
     */
    public void update(final MappingOperation<?, ?> mapping) {
        this.mapping = mapping;
        if (sourceDropTarget != null) dispose();
        final Composite contentPane = new Composite(scroller, SWT.NONE);
        scroller.setContent(contentPane);
        contentPane.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
        contentPane.setBackground(scroller.getBackground());
        contentPane.setForeground(contentPane.getBackground());
        if (mapping.getType() == MappingType.CUSTOM) createCustomSourcePane(contentPane);
        else {
            final Composite pane;
            if (mapping.getSource() instanceof Model && mapping.getSourceIndex() != null)
                pane = createContainerPane(contentPane,
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

    private abstract class ControlWithMenuPane extends Composite {

        final Map<String, MenuItemHandler> menuItems = new LinkedHashMap<>();

        ControlWithMenuPane(final Composite parent) {
            super(parent, SWT.NONE);
            setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
            setLayout(GridLayoutFactory.fillDefaults().numColumns(3).spacing(1, 5).create());
            setBackground(parent.getForeground());
            final Label spacer = new Label(this, SWT.NONE);
            spacer.setLayoutData(GridDataFactory.swtDefaults()
                                                .hint(imageButtonLabelSize)
                                                .create());
            spacer.setBackground(getBackground());
            constructControl();
            final Label menuLabel = new Label(this, SWT.NONE);
            menuLabel.setLayoutData(GridDataFactory.swtDefaults()
                                                   .hint(imageButtonLabelSize)
                                                   .align(SWT.BEGINNING, SWT.BOTTOM)
                                                   .create());
            menuLabel.setBackground(getBackground());
            menuLabel.setImage(Images.MENU);
            menuLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseUp(final MouseEvent event) {
                    popupMenu(menuLabel, event.x, event.y);
                }
            });
        }

        void addMenuItem(final String item,
                         final MenuItemHandler handler) {
            menuItems.put(item, handler);
        }

        abstract void constructControl();

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

        abstract class MenuItemHandler extends SelectionAdapter {

            boolean enabled() {
                return true;
            }

            @Override
            public final void widgetDefaultSelected(final SelectionEvent event) {}

            @Override
            public abstract void widgetSelected(SelectionEvent event);
        }
    }

    final class FieldDialog extends BaseDialog {

        private final Model rootModel;
        Model field;

        FieldDialog(final Model rootModel) {
            super(sourceText.getShell());
            this.rootModel = rootModel;
            if (mapping.getSource() instanceof Model) {
                this.field = rootModel.equals(config.getSourceModel())
                             ? (Model) mapping.getSource()
                             : (Model) mapping.getTarget();
            }
        }

        @Override
        protected void constructContents(final Composite parent) {
            parent.setLayout(GridLayoutFactory.swtDefaults().create());
            final ModelViewer modelViewer = new ModelViewer(config, parent, rootModel, null);
            modelViewer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
            if (field != null) {
                modelViewer.select(field);
            }
            modelViewer.treeViewer.getTree().addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent event) {
                    final IStructuredSelection selection =
                        (IStructuredSelection)modelViewer.treeViewer.getSelection();
                    field = (Model)selection.getFirstElement();
                    validate();
                }
            });
        }

        @Override
        protected String message() {
            return "Select a field.";
        }

        @Override
        protected String title() {
            return "Field";
        }

        void validate() {
            boolean enabled = field != null && !Util.type(field);
            if (enabled) {
                if (rootModel.equals(config.getSourceModel())) {
                    enabled = Util.validSourceAndTarget(field, mapping.getTarget(), config);
                } else {
                    enabled = Util.validSourceAndTarget(mapping.getSource(), field, config);
                }
            }
            setErrorMessage(enabled ? null : "Invalid field");
            getButton(IDialogConstants.OK_ID).setEnabled(enabled);
        }
    }

    final class VariableDialog extends BaseDialog {

        Variable variable;

        VariableDialog() {
            super(sourceText.getShell());
            if (mapping.getSource() instanceof Variable) {
                this.variable = (Variable) mapping.getSource();
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
                    return ((Variable) element).getName();
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
