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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.jboss.mapper.model.Model;
import org.jboss.tools.fuse.transformation.editor.TransformationEditor;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Decorations;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;

public class ModelViewer extends Composite {

    TransformationConfig config;
    TransformationEditor editor;
    Model rootModel;
    final TreeViewer treeViewer;
    boolean showFieldTypes;
    boolean hideMappedFields;

    public ModelViewer(final TransformationConfig config,
            final Composite parent,
            final Model rootModel) {
        super(parent, SWT.NONE);
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
        treeViewer.addDragSupport(DND.DROP_MOVE,
                new Transfer[] {LocalSelectionTransfer.getTransfer()}, new DragSourceAdapter() {

                    @Override
                    public void dragStart(final DragSourceEvent event) {
                        LocalSelectionTransfer.getTransfer()
                                              .setSelection(treeViewer.getSelection());
                    }
                });
        // Override selection background color
        treeViewer.getTree().addListener(SWT.EraseItem, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                if ((event.detail & SWT.SELECTED) != 0) {
                    final Color oldBackground = event.gc.getBackground();
                    event.gc.setBackground(treeViewer.getTree().isFocusControl()
                                           ? Colors.SELECTED
                                           : Colors.SELECTED_NO_FOCUS);
                    event.gc.fillRectangle(event.getBounds());
                    event.gc.setBackground(oldBackground);
                    event.detail &= ~SWT.SELECTED;
                }
            }
        });
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

        config.addListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                if (event.getPropertyName().equals(TransformationConfig.MAPPING))
                    treeViewer.refresh();
            }
        });
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

    private void expand(final Model model) {
        if (model == null) return;
        expand(model.getParent());
        treeViewer.expandToLevel(model, 0);
    }

    void select(final Model model) {
        if (model == null) return;
        expand(model.getParent());
        treeViewer.setSelection(new StructuredSelection(model), true);
    }

    boolean show(final Object element) {
        return !hideMappedFields || !mapped((Model) element, rootModel);
    }

    public ModelViewer(final Composite parent,
            final Model rootModel) {
        super(parent, SWT.NONE);
        // simple view

        setBackground(parent.getParent().getParent().getBackground());

        this.rootModel = rootModel;

        setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());

        treeViewer = new TreeViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        treeViewer.getTree().setLayoutData(
                GridDataFactory.fillDefaults().span(2, 1).grab(true, true).create());
        treeViewer.setComparator(new ViewerComparator() {

            @Override
            public int compare(final Viewer viewer,
                    final Object model1,
                    final Object model2) {
                if (model1 instanceof Model && model2 instanceof Model)
                    return ((Model) model1).getName()
                            .compareTo(((Model) model2).getName());
                return 0;
            }
        });
        treeViewer.setLabelProvider(new LabelProvider());
        ColumnViewerToolTipSupport.enableFor(treeViewer);
        treeViewer.setContentProvider(new ContentProvider());
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

    public TreeViewer getViewer() {
        return treeViewer;
    }

    public void setModel(Model input) {
        this.rootModel = input;
    }
}
