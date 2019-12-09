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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.jboss.tools.fuse.transformation.core.model.Model;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationManager;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationManager.Event;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;

/**
 *
 */
public class ModelViewer extends Composite {

    private static final String PREFERENCE_PREFIX = ModelViewer.class.getName() + "."; //$NON-NLS-1$
    private static final String HIDE_MAPPED_PROPERTIES_PREFERENCE = ".hideMappedProperties"; //$NON-NLS-1$
    private static final String SHOW_TYPES_PREFERENCE = ".showTypes"; //$NON-NLS-1$

    final TransformationManager manager;
    Model rootModel;
    boolean showTypes;
    boolean hideMappedProperties;
    final Map<String, List<Model>> searchMap = new HashMap<>();
    final Set<Model> searchResults = new HashSet<>();
    private Text searchText;
    private Label searchLabel;
    private Label clearSearchLabel;
    protected final TreeViewer treeViewer;
    private Model prevSelectedModel;

    /**
     * @param parent
     * @param rootModel
     */
    public ModelViewer(final Composite parent,
                       final Model rootModel) {
        this(null, parent, rootModel, null, null);
    }

    /**
     * @param manager
     * @param parent
     * @param rootModel
     * @param potentialDropTargets
     * @param preferenceId
     */
    ModelViewer(final TransformationManager manager,
                final Composite parent,
                final Model rootModel,
                final List<PotentialDropTarget> potentialDropTargets,
                final String preferenceId) {
        super(parent, SWT.BORDER);
        setBackground(Colors.BACKGROUND);

        this.manager = manager;
        this.rootModel = rootModel;

        updateSearchMap(rootModel);

        setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        final IPreferenceStore prefs = Activator.plugin().getPreferenceStore();

        final ToolBar toolBar = new ToolBar(this, SWT.NONE);
        toolBar.setBackground(getBackground());
        final ToolItem collapseAllButton = new ToolItem(toolBar, SWT.PUSH);
        collapseAllButton.setImage(Images.COLLAPSE_ALL);
        final ToolItem filterTypesButton = new ToolItem(toolBar, SWT.CHECK);
        filterTypesButton.setImage(Images.SHOW_TYPES);
        filterTypesButton.setToolTipText(Messages.ModelViewer_Tooltip_ShowTypes);

        final ToolItem filterMappedPropertiesButton;
        if (preferenceId == null) {
        	filterMappedPropertiesButton = null;
        } else {
            filterMappedPropertiesButton = new ToolItem(toolBar, SWT.CHECK);
            filterMappedPropertiesButton.setImage(Images.HIDE_MAPPED);
            filterMappedPropertiesButton.setToolTipText(Messages.ModelViewer_Tooltip_HideMappedproperties);
        }

        Composite searchPane = new Composite(this, SWT.NONE);
        searchPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        searchPane.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());
        searchPane.setToolTipText(Messages.ModelViewer_searchPaneTooltip);
        searchPane.setBackground(getBackground());
        searchLabel = new Label(searchPane, SWT.NONE);
        searchLabel.setImage(Images.SEARCH);
        searchLabel.setToolTipText(Messages.ModelViewer_searchLabelTooltip);
        searchLabel.setBackground(getBackground());
        searchText = new Text(searchPane, SWT.NONE);
        searchText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        searchText.setToolTipText(Messages.ModelViewer_searchLabelTooltip);
        clearSearchLabel = new Label(searchPane, SWT.NONE);
        clearSearchLabel.setImage(Images.CLEAR);
        clearSearchLabel.setToolTipText(Messages.ModelViewer_clearSearchTextTooltip);
        clearSearchLabel.setBackground(getBackground());
        searchPane.addPaintListener(Util.ovalBorderPainter());

        treeViewer = new TreeViewer(this, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        treeViewer.getTree().setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, true).create());
        treeViewer.setLabelProvider(new LabelProvider());
        ColumnViewerToolTipSupport.enableFor(treeViewer);
        treeViewer.setContentProvider(new ContentProvider());
        treeViewer.addFilter(new ViewerFilter() {

            @Override
            public boolean select(final Viewer viewer,
                                  final Object parentElement,
                                  final Object element) {
                return show(element, !searchText.getText().trim().isEmpty());
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
                        if (color == Colors.POTENTIAL_DROP_TARGET1) {
                            color = Colors.POTENTIAL_DROP_TARGET2;
                        } else if (color == Colors.POTENTIAL_DROP_TARGET2) {
                            color = Colors.POTENTIAL_DROP_TARGET3;
                        } else if (color == Colors.POTENTIAL_DROP_TARGET3) {
                            color = Colors.POTENTIAL_DROP_TARGET4;
                        } else if (color == Colors.POTENTIAL_DROP_TARGET4) {
                            color = Colors.POTENTIAL_DROP_TARGET5;
                        } else if (color == Colors.POTENTIAL_DROP_TARGET5) {
                            color = Colors.POTENTIAL_DROP_TARGET6;
                        } else if (color == Colors.POTENTIAL_DROP_TARGET6) {
                            color = Colors.POTENTIAL_DROP_TARGET7;
                        } else if (color == Colors.POTENTIAL_DROP_TARGET7) {
                            color = Colors.POTENTIAL_DROP_TARGET8;
                        } else {
                            color = Colors.POTENTIAL_DROP_TARGET1;
                        }
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
                showTypes = filterTypesButton.getSelection();
                filterTypesButton.setToolTipText((showTypes ? Messages.ModelViewer_HideTooltip : Messages.ModelViewer_ShowTooltip) + Messages.ModelViewer_types);
                treeViewer.refresh();
                if (preferenceId != null)
                    prefs.setValue(PREFERENCE_PREFIX + preferenceId + SHOW_TYPES_PREFERENCE, showTypes);
            }
        });
        if (preferenceId != null) {
            showTypes = prefs.getBoolean(PREFERENCE_PREFIX + preferenceId + SHOW_TYPES_PREFERENCE);
            filterTypesButton.setSelection(showTypes);
        }
        if (filterMappedPropertiesButton != null) {
            filterMappedPropertiesButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent event) {
                    hideMappedProperties = filterMappedPropertiesButton.getSelection();
                    filterMappedPropertiesButton.setToolTipText((hideMappedProperties ? Messages.ModelViewer_ShowTooltip : Messages.ModelViewer_HideTooltip) + Messages.ModelViewer_mappedproperties);
                    if (hideMappedProperties) {
                    	prevSelectedModel = (Model)treeViewer.getStructuredSelection().getFirstElement();
                    }
                    treeViewer.refresh();
                    if (!hideMappedProperties && prevSelectedModel != null) {
                    	select(prevSelectedModel);
                    }
                    if (preferenceId != null)
                        prefs.setValue(PREFERENCE_PREFIX + preferenceId + HIDE_MAPPED_PROPERTIES_PREFERENCE, hideMappedProperties);
                }
            });
            hideMappedProperties = prefs.getBoolean(PREFERENCE_PREFIX + preferenceId + HIDE_MAPPED_PROPERTIES_PREFERENCE);
            filterMappedPropertiesButton.setSelection(hideMappedProperties);
        }
        searchLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(final MouseEvent event) {
                searchText.setFocus();
            }
        });
        clearSearchLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(final MouseEvent event) {
                searchText.setText(""); //$NON-NLS-1$
            }
        });
        searchText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent event) {
                searchResults.clear();
                final List<Model> models = searchMap.get(searchText.getText().trim().toLowerCase());
                if (models != null) {
                    for (final Model model : models) {
                        searchResults.add(model);
                        for (Model parent = model.getParent(); parent != null; parent = parent.getParent()) {
                            searchResults.add(parent);
                        }
                    }
                }
                treeViewer.refresh();
            }
        });

        if (rootModel != null) {
            treeViewer.setInput("root"); //$NON-NLS-1$
        }

        if (manager != null) {
            manager.addListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(final PropertyChangeEvent event) {
					if (event.getPropertyName().equals(Event.MAPPING.name())) {
                        if (!treeViewer.getControl().isDisposed()) {
                            treeViewer.refresh();
                        }
                    }
                }
            });
        }
    }

    boolean eligible(Model model) {
        return true;
    }

    private void expand(final Model model) {
        if (model == null) {
            return;
        }
        expand(model.getParent());
        treeViewer.expandToLevel(model, 0);
    }

    private boolean mappedOrFullyMappedParent(final Model model) {
        final List<Model> children = model.getChildren();
        for (final Model child : children) {
            if (!mappedOrFullyMappedParent(child)) {
                return false;
            }
        }
        return (manager.mapped(model)) ? true : !children.isEmpty();
    }

    void select(final Model model) {
        if (model == null) {
            return;
        }
        final List<Model> models = searchMap.get(model.getName().toLowerCase());
        if (models == null) {
            return;
        }
        for (final Model actualModel : models) {
            if (actualModel.equals(model)) {
                expand(actualModel.getParent());
                treeViewer.setSelection(new StructuredSelection(actualModel), true);
                return;
            }
        }
    }

    public void setModel(final Model model) {
        rootModel = model;
        updateSearchMap(model);
        treeViewer.setInput(model == null ? null : "root"); //$NON-NLS-1$
    }

    private boolean show(final Object element,
                         final boolean searching) {
        if (hideMappedProperties && mappedOrFullyMappedParent((Model)element)) {
            return false;
        }
        return !searching || searchResults.contains(element);
    }

    private void updateSearchMap(final Model model) {
        if (model == null) {
            return;
        }
        final StringCharacterIterator iter =
            new StringCharacterIterator(model.getName().toLowerCase());
        final StringBuilder builder = new StringBuilder();
        for (char chr = iter.first(); chr != StringCharacterIterator.DONE; chr = iter.next()) {
            builder.append(chr);
            final String key = builder.toString();
            List<Model> models = searchMap.get(key);
            if (models == null) {
                models = new ArrayList<>();
                searchMap.put(key, models);
            }
            models.add(model);
        }
        for (final Model child : model.getChildren()) {
            updateSearchMap(child);
        }
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

        private Image getImage(final Object element) {
            final Model model = (Model)element;
            if (manager != null) {
                if (model.getChildren() != null && !model.getChildren().isEmpty())
                    return manager.mapped(model) ? Images.MAPPED_NODE : Images.NODE;
                return manager.mapped(model) ? Images.MAPPED_PROPERTY : Images.PROPERTY;
            }
            // if we are hosting the viewer on a wizard page, we don't have the manager
            // so just default to parent (node) or node (property) in the tree
            if (model.getChildren() != null && !model.getChildren().isEmpty())
                return Images.NODE;
            return Images.PROPERTY;
        }

        private String getText(final Object element,
                               final StyledString text,
                               final boolean showTypes) {
            final Model model = (Model)element;
            final String type = model.getType();
            boolean eligible = eligible(model);
            text.append(model.getName(), eligible ? null : StyledString.QUALIFIER_STYLER);
            boolean list = type.startsWith("["); //$NON-NLS-1$
            if (list) {
                text.append("[", StyledString.QUALIFIER_STYLER); //$NON-NLS-1$
                if (showTypes) {
                	text.append(type.substring(1, type.length() - 1), eligible ? StyledString.DECORATIONS_STYLER : StyledString.QUALIFIER_STYLER);
                }
                                           
                else text.append(" "); //$NON-NLS-1$
                text.append("]", StyledString.QUALIFIER_STYLER); //$NON-NLS-1$
            } else if (showTypes) {
                text.append(": ", StyledString.QUALIFIER_STYLER); //$NON-NLS-1$
                text.append(type, eligible ? StyledString.DECORATIONS_STYLER : StyledString.QUALIFIER_STYLER);
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
            cell.setText(getText(element, text, showTypes));
            cell.setStyleRanges(text.getStyleRanges());
            super.update(cell);
        }
    }
}
