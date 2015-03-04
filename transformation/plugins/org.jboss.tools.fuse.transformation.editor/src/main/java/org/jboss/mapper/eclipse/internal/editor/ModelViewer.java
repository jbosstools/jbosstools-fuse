/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others. 
 * All rights reserved. This program and the accompanying materials are 
 * made available under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.mapper.eclipse.internal.editor;

import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ITreeContentProvider;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.jboss.mapper.eclipse.Activator;
import org.jboss.mapper.eclipse.TransformationEditor;
import org.jboss.mapper.model.Model;

class ModelViewer extends Composite {

    final TransformationEditor editor;
    Model rootModel;
    final TreeViewer treeViewer;
    boolean showFieldTypesInLabel = false;
    boolean showMappedFields = true;

    ModelViewer(final TransformationEditor editor,
            final Composite parent,
            final Model rootModel) {
        super(parent, SWT.NONE);

        this.editor = editor;
        this.rootModel = rootModel;

        setBackground(parent.getParent().getBackground());
        setLayout(GridLayoutFactory.fillDefaults().create());

        final ToolBar toolBar = new ToolBar(this, SWT.NONE);
        toolBar.setBackground(getBackground());
        final ToolItem collapseAllButton = new ToolItem(toolBar, SWT.PUSH);
        collapseAllButton.setImage(Activator.imageDescriptor("collapseall16.gif").createImage());
        final ToolItem filterTypesButton = new ToolItem(toolBar, SWT.CHECK);
        filterTypesButton.setImage(Activator.imageDescriptor("filter16.gif").createImage());
        filterTypesButton.setToolTipText("Show/hide Types");
        final ToolItem filterMappedFieldsButton = new ToolItem(toolBar, SWT.CHECK);
        filterMappedFieldsButton.setImage(Activator.imageDescriptor("filter16.gif").createImage());
        filterMappedFieldsButton.setToolTipText("Show/hide Mapped Fields");

        treeViewer = new TreeViewer(this);
        treeViewer.getTree()
                .setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        treeViewer.setComparator(new ViewerComparator() {

            @Override
            public int compare(final Viewer viewer,
                    final Object model1,
                    final Object model2) {
                if (model1 instanceof Model && model2 instanceof Model) {
                    return ((Model) model1).getName().compareTo(((Model) model2).getName());
                }
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
                return show(editor, element);
            }
        });
        treeViewer.addDragSupport(DND.DROP_MOVE,
                new Transfer[] {LocalSelectionTransfer.getTransfer()},
                new DragSourceAdapter() {

                    @Override
                    public void dragStart(final DragSourceEvent event) {
                        LocalSelectionTransfer.getTransfer()
                                .setSelection(treeViewer.getSelection());
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
            public void widgetSelected(final org.eclipse.swt.events.SelectionEvent event) {
                final ToolItem item = (ToolItem) event.widget;
                showFieldTypesInLabel = item.getSelection();
                treeViewer.refresh(true);
            }
        });

        filterMappedFieldsButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final org.eclipse.swt.events.SelectionEvent event) {
                final ToolItem item = (ToolItem) event.widget;
                showMappedFields = !item.getSelection();
                treeViewer.refresh(true);
            }
        });

        treeViewer.setInput("root");
    }

    boolean show(final TransformationEditor editor,
            final Object element) {
        return showMappedFields || !editor.mapped((Model) element, rootModel);
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

        private Image getImage(final Object element) {
            final Model model = (Model) element;
            final ISharedImages images = JavaUI.getSharedImages();
            if (model.isCollection()) {
                return images.getImage(ISharedImages.IMG_FIELD_DEFAULT);
            }
            if ((model.getChildren() != null && model.getChildren().size() > 0)) {
                return images.getImage(ISharedImages.IMG_OBJS_CLASS);
            }
            return images.getImage(ISharedImages.IMG_FIELD_PUBLIC);
        }

        private String getText(final Object element,
                final StyledString text,
                final boolean showFieldTypesInLabel) {
            final Model modelForLabel = (Model) element;
            text.append(modelForLabel.getName());
            if (showFieldTypesInLabel) {
                final String type = modelForLabel.getType();
                if (type.startsWith("[")) {
                    text.append(":", StyledString.DECORATIONS_STYLER);
                    text.append(" " + LIST_OF, StyledString.QUALIFIER_STYLER);
                    text.append(type.substring(1, type.length() - 1),
                            StyledString.DECORATIONS_STYLER);
                } else {
                    text.append(": " + type, StyledString.DECORATIONS_STYLER);
                }
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
            if (editor.mapped((Model) element, rootModel)) {
                text.append("*", StyledString.DECORATIONS_STYLER);
            }
            cell.setImage(getImage(element));
            cell.setText(getText(element, text, showFieldTypesInLabel));
            cell.setStyleRanges(text.getStyleRanges());
            super.update(cell);
        }
    }
}
