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
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.jboss.mapper.model.Model;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Decorations;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;

/**
 *
 */
public class ModelViewer extends Composite {

    final TransformationConfig config;
    Model rootModel;
    boolean showFieldTypes;
    boolean hideMappedFields;

    /**
     *
     */
    protected final TreeViewer treeViewer;

    /**
     * @param config
     * @param parent
     * @param rootModel
     * @param potentialDropTargets
     */
    public ModelViewer(final TransformationConfig config,
                       final Composite parent,
                       final Model rootModel,
                       final List<PotentialDropTarget> potentialDropTargets) {
        super(parent, SWT.BORDER);
        setBackground(Colors.BACKGROUND);

        this.config = config;
        this.rootModel = rootModel;

        setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

        final ToolBar toolBar = new ToolBar(this, SWT.NONE);
        final ToolItem collapseAllButton = new ToolItem(toolBar, SWT.PUSH);
        collapseAllButton.setImage(Images.COLLAPSE_ALL);
        final ToolItem filterTypesButton = new ToolItem(toolBar, SWT.CHECK);
        filterTypesButton.setImage(Images.FILTER);
        filterTypesButton.setToolTipText("Show types");
        final ToolItem filterMappedFieldsButton = new ToolItem(toolBar, SWT.CHECK);
        filterMappedFieldsButton.setImage(Images.HIDE_MAPPED);
        filterMappedFieldsButton.setToolTipText("Hide mapped fields");

        final Composite searchPane = new Composite(this, SWT.NONE);
        searchPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        searchPane.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());
        searchPane.setToolTipText("Search");
        searchPane.setBackground(Colors.BACKGROUND);
        final Label searchLabel = new Label(searchPane, SWT.NONE);
        searchLabel.setImage(Images.SEARCH);
        searchLabel.setToolTipText("Search");
        final Text searchText = new Text(searchPane, SWT.NONE);
        searchText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        searchText.setToolTipText("Search");
        final Label clearSearchLabel = new Label(searchPane, SWT.NONE);
        clearSearchLabel.setImage(Images.CLEAR);
        clearSearchLabel.setToolTipText("Search");
        searchPane.addPaintListener(Util.ovalBorderPainter());

        treeViewer = new TreeViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        treeViewer.getTree().setLayoutData(GridDataFactory.fillDefaults()
                                                          .span(2, 1)
                                                          .grab(true, true)
                                                          .create());
        treeViewer.setComparator(new ViewerComparator() {

            @Override
            public int compare(final Viewer viewer,
                               final Object model1,
                               final Object model2) {
                if (model1 instanceof Model && model2 instanceof Model)
                    return ((Model) model1).getName().compareTo(((Model) model2).getName());
                return 0;
            }
        });
        treeViewer.setLabelProvider(new LabelProvider());
        ColumnViewerToolTipSupport.enableFor(treeViewer);
        treeViewer.setContentProvider(new ContentProvider());
        treeViewer.addFilter(new ViewerFilter() {

            @Override
            public boolean select(final Viewer viewer,
                                  final Object parentElement,
                                  final Object element) {
                return show(element);
            }
        });
        if (potentialDropTargets != null) {
            treeViewer.addDragSupport(DND.DROP_MOVE,
                                      new Transfer[] {LocalSelectionTransfer.getTransfer()},
                                      new DragSourceAdapter() {

                Color color;
                List<Control> controls = new ArrayList<>();
                private final MouseMoveListener mouseMoveListener = new MouseMoveListener() {

                    @Override
                    public void mouseMove(final MouseEvent event) {
                        for (final Control control : controls) {
                            control.redraw();
                        }
                        if (color == Colors.POTENTIAL_DROP_TARGET1)
                            color = Colors.POTENTIAL_DROP_TARGET2;
                        else if (color == Colors.POTENTIAL_DROP_TARGET2)
                            color = Colors.POTENTIAL_DROP_TARGET3;
                        else if (color == Colors.POTENTIAL_DROP_TARGET3)
                            color = Colors.POTENTIAL_DROP_TARGET4;
                        else if (color == Colors.POTENTIAL_DROP_TARGET4)
                            color = Colors.POTENTIAL_DROP_TARGET5;
                        else if (color == Colors.POTENTIAL_DROP_TARGET5)
                            color = Colors.POTENTIAL_DROP_TARGET6;
                        else if (color == Colors.POTENTIAL_DROP_TARGET6)
                            color = Colors.POTENTIAL_DROP_TARGET7;
                        else if (color == Colors.POTENTIAL_DROP_TARGET7)
                            color = Colors.POTENTIAL_DROP_TARGET8;
                        else color = Colors.POTENTIAL_DROP_TARGET1;
                    }
                };
                private final PaintListener paintListener = new PaintListener() {

                    @Override
                    public void paintControl(final PaintEvent event) {
                        event.gc.setForeground(color);
                        final Rectangle bounds = ((Control)event.widget).getBounds();
                        event.gc.drawRectangle(0, 0, bounds.width - 1, bounds.height - 1);
                    }
                };

                @Override
                public void dragFinished(final DragSourceEvent event) {
                    treeViewer.getTree().removeMouseMoveListener(mouseMoveListener);
                    for (final Control control : controls) {
                        control.removePaintListener(paintListener);
                        control.redraw();
                    }
                    controls.clear();
                }

                @Override
                public void dragStart(final DragSourceEvent event) {
                    final IStructuredSelection selection =
                        (IStructuredSelection)treeViewer.getSelection();
                    LocalSelectionTransfer.getTransfer().setSelection(selection);
                    color = Colors.POTENTIAL_DROP_TARGET1;
                    for (final PotentialDropTarget potentialDropTarget : potentialDropTargets) {
                        if (potentialDropTarget.valid()) {
                            controls.add(potentialDropTarget.control);
                            potentialDropTarget.control.addPaintListener(paintListener);
                        }
                    }
                    treeViewer.getTree().addMouseMoveListener(mouseMoveListener);
                }
            });
        }
        collapseAllButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                treeViewer.collapseAll();
            }
        });
        filterTypesButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                final ToolItem item = (ToolItem) event.widget;
                showFieldTypes = item.getSelection();
                item.setToolTipText((showFieldTypes ? "Hide" : "Show") + " types");
                treeViewer.refresh();
            }
        });
        filterMappedFieldsButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                final ToolItem item = (ToolItem) event.widget;
                hideMappedFields = item.getSelection();
                item.setToolTipText((hideMappedFields ? "Show" : "Hide") + " mapped fields");
                treeViewer.refresh();
            }
        });
        searchLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(final MouseEvent event) {
                searchText.setFocus();
            }
        });
        clearSearchLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(final MouseEvent event) {
                searchText.setText("");
            }
        });

        if (rootModel != null) treeViewer.setInput("root");

        if (config != null) config.addListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                if (event.getPropertyName().equals(TransformationConfig.MAPPING)) {
                    if (!treeViewer.getControl().isDisposed()) {
                        treeViewer.refresh();
                    }
                }
            }
        });
    }

    private void expand(final Model model) {
        if (model == null) return;
        expand(model.getParent());
        treeViewer.expandToLevel(model, 0);
    }

    /**
     * @param model
     * @param rootModel
     * @return <code>true</code> if the supplied model has been mapped at least once
     */
    public boolean mapped(final Model model,
                          final Model rootModel) {
        if (config != null) {
            return rootModel.equals(config.getSourceModel())
               ? !config.getMappingsForSource(model).isEmpty()
               : !config.getMappingsForTarget(model).isEmpty();
        }
        return false;
    }

    void select(final Model model) {
        if (model == null) return;
        expand(model.getParent());
        treeViewer.setSelection(new StructuredSelection(model), true);
    }

    public void setModel(final Model model) {
        rootModel = model;
        treeViewer.setInput("root");
    }

    boolean show(final Object element) {
        return !hideMappedFields || !mapped((Model) element, rootModel);
    }

    class ContentProvider implements ITreeContentProvider {

        @Override
        public void dispose() {}

        @Override
        public Object[] getChildren(final Object parentElement) {
            return ((Model) parentElement).getChildren().toArray();
        }

        @Override
        public Object[] getElements(final Object inputElement) {
            return new Object[] {rootModel};
        }

        @Override
        public Object getParent(final Object element) {
            return element instanceof Model ? ((Model) element).getParent() : null;
        }

        @Override
        public boolean hasChildren(final Object element) {
            return getChildren(element).length > 0;
        }

        @Override
        public void inputChanged(final Viewer viewer,
                                 final Object oldInput,
                                 final Object newInput) {}
    }

    class LabelProvider extends StyledCellLabelProvider {

        private static final String LIST_OF = "list of ";

        LabelProvider() {
            super(StyledCellLabelProvider.COLORS_ON_SELECTION);
        }

        private Image getImage(final Object element) {
            final Model model = (Model) element;
            Image img = model.getChildren() != null && model.getChildren().size() > 0
                        ? Images.ELEMENT
                        : Images.ATTRIBUTE;
            if (model.isCollection())
                img = new DecorationOverlayIcon(img,
                                                Decorations.COLLECTION,
                                                IDecoration.BOTTOM_RIGHT).createImage();
            if (mapped((Model) element, rootModel))
                return new DecorationOverlayIcon(img,
                                                 Decorations.MAPPED,
                                                 IDecoration.TOP_RIGHT).createImage();
            return img;
        }

        private String getText(final Object element,
                               final StyledString text,
                               final boolean showFieldTypesInLabel) {
            final Model model = (Model) element;
            text.append(model.getName());
            if (showFieldTypesInLabel) {
                final String type = model.getType();
                if (type.startsWith("[")) {
                    text.append(":", StyledString.DECORATIONS_STYLER);
                    text.append(" " + LIST_OF, StyledString.QUALIFIER_STYLER);
                    text.append(type.substring(1, type.length() - 1),
                                StyledString.DECORATIONS_STYLER);
                } else text.append(": " + type, StyledString.DECORATIONS_STYLER);
            }
            return text.getString();
        }

        @Override
        public String getToolTipText(final Object element) {
            return getText(element, new StyledString(), true);
        }

        @Override
        public void update(final ViewerCell cell) {
            final Object element = cell.getElement();
            final StyledString text = new StyledString();
            cell.setImage(getImage(element));
            cell.setText(getText(element, text, showFieldTypes));
            cell.setStyleRanges(text.getStyleRanges());
            super.update(cell);
        }
    }
}
