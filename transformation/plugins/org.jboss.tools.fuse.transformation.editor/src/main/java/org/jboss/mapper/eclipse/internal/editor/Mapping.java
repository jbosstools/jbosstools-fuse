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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.mapper.CustomMapping;
import org.jboss.mapper.FieldMapping;
import org.jboss.mapper.Literal;
import org.jboss.mapper.LiteralMapping;
import org.jboss.mapper.MappingOperation;
import org.jboss.mapper.MappingType;
import org.jboss.mapper.eclipse.Activator;
import org.jboss.mapper.eclipse.TransformationEditor;
import org.jboss.mapper.eclipse.internal.editor.MappingsViewer.TraversalListener;
import org.jboss.mapper.eclipse.internal.util.Util;
import org.jboss.mapper.model.Model;

final class Mapping {

    // static final String ADD_CUSTOM_OPERATION_TOOL_TIP =
    // "Add a custom operation that uses the source element that follows as its first parameter";
    static final String ADD_CUSTOM_OPERATION_TOOL_TIP =
            "Add a custom operation to the source element that follows";
    static final String DELETE_CUSTOM_OPERATION_TOOL_TIP =
            "Delete the custom operation that follows";

    final TransformationEditor editor;
    final MappingsViewer mappingsViewer;
    MappingOperation<?, ?> mapping;
    final Composite mappingSourcePane;
    final Label opIconLabel;
    final Label opStartLabel;
    final Text sourceText;
    final Label opEndLabel;
    final Label mapsToLabel;
    final Composite mappingTargetPane;
    Image opImage;
    SelectionListener selectionListener;

    Mapping(final TransformationEditor editor,
            final MappingsViewer mappingsViewer,
            final MappingOperation<?, ?> mapping) {
        this.editor = editor;
        this.mappingsViewer = mappingsViewer;
        this.mapping = mapping;

        mappingSourcePane = new Composite(mappingsViewer.sourcePane, SWT.NONE);

        mappingSourcePane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        mappingSourcePane.setLayout(GridLayoutFactory.swtDefaults().spacing(0, 0).numColumns(4)
                .create());
        mappingSourcePane.setBackground(mappingsViewer.getBackground());

        final boolean customMapping = mapping instanceof CustomMapping;
        final boolean fieldMapping = mapping instanceof FieldMapping;

        // Add add/delete op "button" for source component pane
        opIconLabel = new Label(mappingSourcePane, SWT.NONE);
        if (mappingsViewer.iconButtonSize == null) {
            opIconLabel.setImage(Util.ADD_OPERATION_IMAGE);
            mappingsViewer.iconButtonSize = opIconLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            opIconLabel.setImage(null);
        }
        opIconLabel.setLayoutData(GridDataFactory.swtDefaults()
                .hint(mappingsViewer.iconButtonSize)
                .grab(true, false)
                .align(SWT.RIGHT, SWT.CENTER)
                .create());
        opIconLabel.setBackground(mappingsViewer.getBackground());
        opIconLabel.setToolTipText(customMapping ? DELETE_CUSTOM_OPERATION_TOOL_TIP
                : fieldMapping ? ADD_CUSTOM_OPERATION_TOOL_TIP : null);
        // Configure op button to appear on mouse-over
        final MouseTrackListener mouseOverListener = new MouseTrackAdapter() {

            @Override
            public void mouseEnter(final MouseEvent event) {
                opIconLabel.setImage(opImage);
            }

            @Override
            public void mouseExit(final MouseEvent event) {
                opIconLabel.setImage(null);
            }
        };
        opIconLabel.addMouseTrackListener(mouseOverListener);
        opImage =
                customMapping ? Util.DELETE_IMAGE : fieldMapping ? Util.ADD_OPERATION_IMAGE : null;

        opStartLabel = new Label(mappingSourcePane, SWT.NONE);
        opStartLabel.setLayoutData(GridDataFactory.swtDefaults().exclude(!customMapping).create());
        // Configure op button to appear on mouse-over of op
        opStartLabel.addMouseTrackListener(mouseOverListener);
        if (customMapping) {
            final CustomMapping cMapping = (CustomMapping) mapping;
            opStartLabel.setText(cMapping.getMappingOperation() + "(");
            opStartLabel.setToolTipText(cMapping.getMappingClass() + '.'
                    + cMapping.getMappingOperation());
        }

        sourceText = new Text(mappingSourcePane, SWT.BORDER);
        if (MappingType.LITERAL == mapping.getType()) {
            sourceText.setText("\"" + ((LiteralMapping) mapping).getSource().getValue() + "\"");
        } else {
            final Model model = ((FieldMapping) mapping).getSource();
            sourceText.setText(model.getName());
            sourceText.setToolTipText(fullyQualifiedName(model));
        }
        sourceText.setEditable(false);
        // Configure op button to appear on mouse-over of source text
        sourceText.addMouseTrackListener(mouseOverListener);
        // Save text background to restore after leaving mouse-over during DnD
        if (mappingsViewer.textBackground == null) {
            mappingsViewer.textBackground = sourceText.getBackground();
        }

        opEndLabel = new Label(mappingSourcePane, SWT.NONE);
        opEndLabel.setLayoutData(GridDataFactory.swtDefaults().exclude(!customMapping).create());
        opEndLabel.setText(")");

        mapsToLabel = new Label(mappingsViewer.mapsToPane, SWT.NONE);
        mapsToLabel.setLayoutData(GridDataFactory.swtDefaults().create());
        mapsToLabel.setBackground(mappingsViewer.getBackground());
        mapsToLabel.setImage(Util.RIGHT_ARROW_IMAGE);
        mapsToLabel.setToolTipText("Delete this mapping");
        // Configure maps-to label to appear as delete "button" on mouse-over
        mapsToLabel.addMouseTrackListener(new MouseTrackAdapter() {

            @Override
            public void mouseEnter(final MouseEvent event) {
                ((GridData) opEndLabel.getLayoutData()).exclude = true;
                mapsToLabel.setImage(Util.DELETE_IMAGE);
            }

            @Override
            public void mouseExit(final MouseEvent event) {
                mapsToLabel.setImage(Util.RIGHT_ARROW_IMAGE);
            }
        });

        mappingTargetPane = new Composite(mappingsViewer.targetPane, SWT.NONE);
        mappingTargetPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        mappingTargetPane.setLayout(GridLayoutFactory.swtDefaults().spacing(0, 0).create());
        mappingTargetPane.setBackground(mappingsViewer.getBackground());
        final Text targetText = new Text(mappingTargetPane, SWT.BORDER);
        final Model model = (Model) mapping.getTarget();
        targetText.setText(model.getName());
        targetText.setToolTipText(fullyQualifiedName(model));
        targetText.setEditable(false);

        // Make mappingSourcePane, mapsToLabel, & mappingTargetPane the same
        // height
        int height = mappingSourcePane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
        height = Math.max(height, mapsToLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
        height = Math.max(height, mappingTargetPane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
        ((GridData) mappingSourcePane.getLayoutData()).heightHint = height;
        ((GridData) mapsToLabel.getLayoutData()).heightHint = height;
        ((GridData) mappingTargetPane.getLayoutData()).heightHint = height;

        // Configure maps-to label (as delete button) to delete the
        // transformation entry
        mapsToLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(final MouseEvent event) {
                try {
                    unmap();
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });

        // Configure traversal of source and target text to ignore immediate
        // containers
        final TraversalListener sourceTraversalListener =
                new TraversalListener(mappingsViewer.prevTargetText, targetText);
        sourceText.addTraverseListener(sourceTraversalListener);
        final TraversalListener targetTraversalListener = new TraversalListener(sourceText, null);
        targetText.addTraverseListener(targetTraversalListener);
        if (mappingsViewer.prevTraversalListener != null) {
            mappingsViewer.prevTraversalListener.nextText = sourceText;
        }

        opIconLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(final MouseEvent event) {
                try {
                    if (opImage == Util.ADD_OPERATION_IMAGE) {
                        addCustomOperation(mouseOverListener);
                    } else if (opImage == Util.DELETE_IMAGE) {
                        removeCustomOperation();
                    }
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });

        final DropTarget sourceDropTarget = new DropTarget(sourceText, DND.DROP_MOVE);
        sourceDropTarget.setTransfer(new Transfer[] {LocalSelectionTransfer.getTransfer()});
        sourceDropTarget.addDropListener(new DropListener(sourceText, editor.sourceModel(),
                mappingSourcePane) {

            @Override
            MappingOperation<?, ?> drop(final Object dragSource,
                    final MappingOperation<?, ?> mapping) throws Exception {
                MappingOperation<?, ?> newMapping;
                if (dragSource instanceof Literal) {
                    final Literal literal = (Literal) dragSource;
                    newMapping = editor.map(literal, (Model) mapping.getTarget());
                    sourceText.setText("\"" + literal.getValue() + "\"");
                    sourceText.setToolTipText(null);
                    return newMapping;
                }
                final Model model = (Model) dragSource;
                newMapping = editor.map(model, (Model) mapping.getTarget());
                if (mapping instanceof CustomMapping) {
                    final CustomMapping customMapping = (CustomMapping) mapping;
                    newMapping = editor.map((FieldMapping) newMapping,
                            customMapping.getMappingClass(),
                            customMapping.getMappingOperation());
                }
                sourceText.setText(model.getName());
                sourceText.setToolTipText(fullyQualifiedName(model));
                editor.refreshSourceModelViewer();
                return newMapping;
            }

            @Override
            boolean valid(final Object dragSource) {
                return super.valid(dragSource) || dragSource instanceof Literal;
            }
        });

        final DropTarget targetDropTarget = new DropTarget(targetText, DND.DROP_MOVE);
        targetDropTarget.setTransfer(new Transfer[] {LocalSelectionTransfer.getTransfer()});
        targetDropTarget.addDropListener(new DropListener(targetText, editor.targetModel(),
                mappingTargetPane) {

            @Override
            MappingOperation<?, ?> drop(final Object dragSource,
                    final MappingOperation<?, ?> mapping) throws Exception {
                final Model dragModel = (Model) dragSource;
                final MappingOperation<?, ?> newMapping =
                        editor.map(mapping.getSource(), dragModel);
                targetText.setText(dragModel.getName());
                targetText.setToolTipText(fullyQualifiedName(dragModel));
                editor.refreshTargetModelViewer();
                return newMapping;
            }
        });

        final MouseListener listener = new MouseAdapter() {

            @Override
            public void mouseUp(final MouseEvent event) {
                final Event swtEvent = new Event();
                swtEvent.type = SWT.Selection;
                swtEvent.widget = mappingsViewer;
                swtEvent.display = mappingsViewer.getDisplay();
                long timestamp = System.currentTimeMillis() * 1000;
                while (timestamp > 0x7FFFFFFF) {
                    timestamp -= 0x7FFFFFFF;
                }
                swtEvent.time = (int) timestamp;
                swtEvent.data = mapping;
                selectionListener.widgetSelected(new SelectionEvent(swtEvent));
            }
        };
        mappingSourcePane.addMouseListener(listener);
        mappingTargetPane.addMouseListener(listener);

        mappingsViewer.prevTargetText = targetText;
        mappingsViewer.prevTraversalListener = targetTraversalListener;

        update();
    }

    void addCustomOperation(final MouseTrackListener mouseOverListener) throws Exception {
        final FieldMapping mapping = (FieldMapping) this.mapping;
        final AddCustomOperationDialog dlg =
                new AddCustomOperationDialog(mappingsViewer.getShell(),
                        editor.project(),
                        mapping.getSource().getType());
        if (dlg.open() != Window.OK) {
            return;
        }
        this.mapping =
                editor.map(mapping, dlg.type.getFullyQualifiedName(), dlg.method.getElementName());
        opStartLabel.setText(dlg.method.getElementName() + "(");
        opStartLabel.setToolTipText(dlg.type.getFullyQualifiedName() + '.'
                + dlg.method.getElementName());
        ((GridData) opStartLabel.getLayoutData()).exclude = false;
        ((GridData) opEndLabel.getLayoutData()).exclude = false;
        opImage = Util.DELETE_IMAGE;
        opIconLabel.setToolTipText(DELETE_CUSTOM_OPERATION_TOOL_TIP);
        mappingSourcePane.layout();
    }

    void addSelectionListener(final SelectionListener listener) {
        selectionListener = listener;
    }

    void deselect() {
        setBackground(mappingsViewer.getBackground());
    }

    String fullyQualifiedName(final Model model) {
        return fullyQualifiedName(model, new StringBuilder());
    }

    private String fullyQualifiedName(final Model model,
            final StringBuilder builder) {
        if (model.getParent() != null) {
            fullyQualifiedName(model.getParent(), builder);
            builder.append('.');
        }
        builder.append(model.getName());
        return builder.toString();
    }

    void removeCustomOperation() throws Exception {
        editor.unmap(mapping);
        mapping = editor.map((Model) mapping.getSource(), (Model) mapping.getTarget());
        ((GridData) opStartLabel.getLayoutData()).exclude = true;
        ((GridData) opEndLabel.getLayoutData()).exclude = true;
        opImage = Util.ADD_OPERATION_IMAGE;
        opIconLabel.setToolTipText(ADD_CUSTOM_OPERATION_TOOL_TIP);
        opStartLabel.setToolTipText(null);
        mappingSourcePane.layout();
    }

    void select() {
        setBackground(mappingsViewer.getDisplay().getSystemColor(SWT.COLOR_BLUE));
    }

    private void setBackground(final Color color) {
        mappingSourcePane.setBackground(color);
        opIconLabel.setBackground(color);
        mapsToLabel.setBackground(color);
        mappingTargetPane.setBackground(color);
    }

    void unmap() throws Exception {
        editor.unmap(mapping);
        editor.save();
        mappingSourcePane.dispose();
        mapsToLabel.dispose();
        mappingTargetPane.dispose();
        update();
    }

    void update() {
        mappingsViewer.pane.layout();
        mappingsViewer.sourcePane.layout();
        mappingsViewer.mapsToPane.layout();
        mappingsViewer.targetPane.layout();
        mappingsViewer.scroller.setMinSize(mappingsViewer.pane
                .computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    private abstract class DropListener extends DropTargetAdapter {

        private final Text dropText;
        private final Model dragRootModel;
        private final Composite mappingPane;

        DropListener(final Text dropText,
                final Model dragRootModel,
                final Composite mappingPane) {
            this.dropText = dropText;
            this.dragRootModel = dragRootModel;
            this.mappingPane = mappingPane;
        }

        @Override
        public final void dragEnter(final DropTargetEvent event) {
            if (valid(dragSource())) {
                dropText.setBackground(mappingPane.getDisplay().getSystemColor(SWT.COLOR_BLUE));
            }
        }

        @Override
        public final void dragLeave(final DropTargetEvent event) {
            dropText.setBackground(mappingsViewer.textBackground);
        }

        private Object dragSource() {
            return ((IStructuredSelection) LocalSelectionTransfer.getTransfer().getSelection())
                    .getFirstElement();
        }

        @Override
        public final void drop(final DropTargetEvent event) {
            editor.unmap(mapping);
            try {
                mapping = drop(dragSource(), mapping);
            } catch (final Exception e) {
                Activator.error(e);
            }
            mappingPane.layout();
        }

        abstract MappingOperation<?, ?> drop(final Object dragSource,
                MappingOperation<?, ?> mapping) throws Exception;

        private Model root(final Model model) {
            return model.getParent() == null ? model : root(model.getParent());
        }

        boolean valid(final Object dragSource) {
            return dragSource instanceof Model && root((Model) dragSource).equals(dragRootModel);
        }
    }
}
