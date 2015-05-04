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
import java.util.ArrayList;
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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.model.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.catalog.languages.Language;
import org.jboss.tools.fuse.transformation.CustomMapping;
import org.jboss.tools.fuse.transformation.Expression;
import org.jboss.tools.fuse.transformation.FieldMapping;
import org.jboss.tools.fuse.transformation.MappingOperation;
import org.jboss.tools.fuse.transformation.MappingType;
import org.jboss.tools.fuse.transformation.Variable;
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
                switch (event.getPropertyName()) {
                    case TransformationConfig.MAPPING: {
                        if (event.getOldValue() == mapping) scroller.setContent(null);
                        else if (event.getNewValue() != null)
                            update((MappingOperation<?, ?>) event.getNewValue());
                        break;
                    }
                    case TransformationConfig.MAPPING_CUSTOMIZE:
                    case TransformationConfig.MAPPING_SOURCE:
                    case TransformationConfig.MAPPING_TARGET: {
                        if (mapping != event.getOldValue()) return;
                        update((MappingOperation<?, ?>) event.getNewValue());
                    }
                }
            }
        });
    }

    void addCustomFunction() throws Exception {
        final AddCustomFunctionDialog dlg =
            new AddCustomFunctionDialog(scroller.getShell(),
                                        config.project(),
                                        ((Model)mapping.getSource()).getType());
        if (dlg.open() != Window.OK) return;
        mapping = config.customizeMapping((FieldMapping) mapping,
                                          dlg.type.getFullyQualifiedName(),
                                          dlg.method.getElementName());
        config.save();
    }

    @SuppressWarnings("unused")
    private void createCustomSourcePane(final Composite parent) {
        final Composite pane = createRoundedPane(parent, Colors.FUNCTION);
        pane.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
        new ControlWithMenuPane(pane) {

            @Override
            Control constructControl() {
                final Label functionLabel = new Label(this, SWT.NONE);
                final CustomMapping customMapping = (CustomMapping) mapping;
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
                return functionLabel;
            }
        };
        Label label = new Label(pane, SWT.NONE);
        label.setText("(");
        createSourcePane(pane);
        label = new Label(pane, SWT.NONE);
        label.setText(")");
    }

    private Composite createDetailPane(final Composite parent,
                                       final Model model) {
        final Composite pane = createRoundedPane(parent, Colors.MODEL);
        final Label label = new Label(pane, SWT.NONE);
        label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).create());
        label.setText(model.getName());
        return pane;
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

    @SuppressWarnings("unused")
    private void createSourcePane(final Composite parent) {
        new ControlWithMenuPane(parent) {

            @Override
            Control constructControl() {
                createSourceText(this, SWT.NONE);
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
                return sourceText;
            }
        };
    }

    @SuppressWarnings("unused")
    private void createTargetPane(final Composite parent) {
        new ControlWithMenuPane(parent) {

            @Override
            Control constructControl() {
                createTargetText(this);
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
                return targetText;
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

    void removeCustomFunction() throws Exception {
        config.uncustomizeMapping((CustomMapping) mapping);
        config.save();
    }

    void setExpression() throws Exception {
        final ExpressionDialog dlg = new ExpressionDialog();
        if (dlg.open() != Window.OK) return;
        Util.updateMavenDependencies(dlg.language.getDependencies(), config.project());
        mapping = config.setSourceExpression(mapping, dlg.language.getName(), dlg.expression);
        config.save();
    }

    void setField(final boolean source) throws Exception {
        final FieldDialog dlg =
            new FieldDialog(source ? config.getSourceModel() : config.getTargetModel());
        if (dlg.open() != Window.OK) return;
        mapping = source
                  ? config.setSource(mapping, dlg.field)
                  : config.setTarget(mapping, dlg.field);
        config.save();
    }

    void setVariable() throws Exception {
        final VariableDialog dlg = new VariableDialog();
        if (dlg.open() != Window.OK) return;
        mapping = config.setSource(mapping, dlg.variable);
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
        final Composite sourceDetailPane = createDetailPane(contentPane, config.getSourceModel());
        sourceDetailPane.setLayoutData(GridDataFactory.swtDefaults()
                                                      .align(SWT.RIGHT, SWT.CENTER)
                                                      .grab(true, true)
                                                      .create());
        if (mapping instanceof CustomMapping) createCustomSourcePane(sourceDetailPane);
        else createSourcePane(sourceDetailPane);
        new Label(contentPane, SWT.NONE).setImage(Images.MAPPED);
        final Composite targetDetailPane = createDetailPane(contentPane, config.getTargetModel());
        targetDetailPane.setLayoutData(GridDataFactory.swtDefaults()
                                                      .align(SWT.LEFT, SWT.CENTER)
                                                      .grab(true, true)
                                                      .create());
        createTargetPane(targetDetailPane);
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
                                                .grab(true, false)
                                                .create());
            final Control control = constructControl();
            final Label menuLabel = new Label(this, SWT.NONE);
            menuLabel.setLayoutData(GridDataFactory.swtDefaults()
                                                   .hint(imageButtonLabelSize)
                                                   .align(SWT.BEGINNING, SWT.BOTTOM)
                                                   .grab(true, false)
                                                   .create());
            final MouseTrackListener mouseOverListener = new MouseTrackAdapter() {

                @Override
                public void mouseEnter(final MouseEvent event) {
                    menuLabel.setImage(Images.MENU);
                }

                @Override
                public void mouseExit(final MouseEvent event) {
                    menuLabel.setImage(null);
                }
            };
            addMouseTrackListener(mouseOverListener);
            spacer.addMouseTrackListener(mouseOverListener);
            control.addMouseTrackListener(mouseOverListener);
            menuLabel.addMouseTrackListener(mouseOverListener);
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

        abstract Control constructControl();

        void popupMenu(final Label menuLabel,
                       final int x,
                       final int y) {
            final Point size = menuLabel.getSize();
            if (x < 0 || x > size.x || y < 0 || y > size.y) return;
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

    final class ExpressionDialog extends BaseDialog {

        final List<Language> languages = new ArrayList<>();
        Language language;
        String expression;

        ExpressionDialog() {
            super(sourceText.getShell());
            String languageName = null;
            if (mapping.getSource() instanceof Expression) {
                final Expression expression = (Expression) mapping.getSource();
                this.expression = expression.getExpression();
                languageName = expression.getLanguage();
            }
            final String version = CamelModelFactory.getSupportedCamelVersions().get(0);
            for (final Language language : CamelModelFactory.getModelForVersion(version)
                                                            .getLanguageModel()
                                                            .getSupportedLanguages()) {
                final String name = language.getName();
                if (!name.equals("bean") && !name.equals("file") && !name.equals("sql")
                    && !name.equals("xtokenize") && !name.equals("tokenize")
                    && !name.equals("spel")) {
                    if (languageName != null && name.equals(languageName))
                        this.language = language;
                    languages.add(language);
                }
            }
        }

        @Override
        protected void constructContents(final Composite parent) {
            parent.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
            Label label = new Label(parent, SWT.NONE);
            label.setText("Language:");
            final ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
            comboViewer.setContentProvider(ArrayContentProvider.getInstance());
            comboViewer.setComparator(new ViewerComparator() {

                @Override
                public int compare(final Viewer viewer,
                                   final Object object1,
                                   final Object object2) {
                    return ((Language)object1).getTitle().compareTo(((Language)object2).getTitle());
                }
            });
            comboViewer.setLabelProvider(new LabelProvider() {

                @Override
                public String getText(final Object element) {
                    return ((Language)element).getTitle();
                }
            });
            label = new Label(parent, SWT.NONE);
            label.setText("Expression:");
            final Text text = new Text(parent, SWT.BORDER);
            text.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
            if (expression != null) text.setText(expression.replace("\\${", "${"));

            comboViewer.getCombo().addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent event) {
                    final IStructuredSelection selection =
                        (IStructuredSelection)comboViewer.getSelection();
                    language = (Language)selection.getFirstElement();
                    text.setFocus();
                    validate();
                }
            });
            text.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(final ModifyEvent event) {
                    String expr = text.getText().trim();
                    for (int ndx = expr.indexOf("${"); ndx >= 0; ndx = expr.indexOf("${", ndx)) {
                        if (ndx == 0 || expr.charAt(ndx - 1) != '\\') {
                            expr = expr.substring(0, ndx) + '\\' + expr.substring(ndx);
                            ndx += 3;
                        }
                    }
                    expression = expr;
                    validate();
                }
            });

            comboViewer.setInput(languages);
            if (language != null) comboViewer.setSelection(new StructuredSelection(language));
        }

        @Override
        public void create() {
            super.create();
            validate();
        }

        @Override
        protected String message() {
            return "Select the expression language, then enter the expression using that language.";
        }

        @Override
        protected String title() {
            return "Expression";
        }

        void validate() {
            getButton(IDialogConstants.OK_ID).setEnabled(language != null
                                                         && expression != null
                                                         && !expression.isEmpty());
        }
    }

    final class FieldDialog extends BaseDialog {

        private final Model rootModel;
        Model field;

        FieldDialog(final Model rootModel) {
            super(sourceText.getShell());
            this.rootModel = rootModel;
            if (mapping.getSource() instanceof Model)
                this.field = rootModel.equals(config.getSourceModel())
                             ? (Model) mapping.getSource()
                             : (Model) mapping.getTarget();
        }

        @Override
        protected void constructContents(final Composite parent) {
            parent.setLayout(GridLayoutFactory.swtDefaults().create());
            final ModelViewer modelViewer = new ModelViewer(config, parent, rootModel, null);
            modelViewer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
            if (field != null) modelViewer.select(field);
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
                if (rootModel.equals(config.getSourceModel()))
                    enabled = Util.validSourceAndTarget(field, mapping.getTarget(), config);
                else enabled = Util.validSourceAndTarget(mapping.getSource(), field, config);
            }
            setErrorMessage(enabled ? null : "Invalid field");
            getButton(IDialogConstants.OK_ID).setEnabled(enabled);
        }
    }

    final class VariableDialog extends BaseDialog {

        Variable variable;

        VariableDialog() {
            super(sourceText.getShell());
            if (mapping.getSource() instanceof Variable)
                this.variable = (Variable) mapping.getSource();
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
            if (variable != null) comboViewer.setSelection(new StructuredSelection(variable));
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
