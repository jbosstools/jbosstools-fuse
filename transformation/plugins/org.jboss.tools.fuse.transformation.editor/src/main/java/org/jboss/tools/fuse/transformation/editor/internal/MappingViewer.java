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

import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.fuse.transformation.Expression;
import org.jboss.tools.fuse.transformation.MappingOperation;
import org.jboss.tools.fuse.transformation.MappingType;
import org.jboss.tools.fuse.transformation.Variable;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;
import org.jboss.tools.fuse.transformation.model.Model;

abstract class MappingViewer {

    final TransformationConfig config;
    MappingOperation<?, ?> mapping;
    Composite sourcePropPane;
    Composite targetPropPane;
    DropTarget sourceDropTarget;
    DropTarget targetDropTarget;
    final List<PotentialDropTarget> potentialDropTargets;

    MappingViewer(final TransformationConfig config,
                  final List<PotentialDropTarget> potentialDropTargets) {
        this.config = config;
        this.potentialDropTargets = potentialDropTargets;
    }

    Composite createPropertyPane(final Composite parent,
                                 final int style) {
        Composite pane = new Composite(parent, SWT.BORDER);
        pane.setLayout(GridLayoutFactory.fillDefaults().create());
        CLabel label = new CLabel(pane, style);
        label.setMargins(1, 1, 1, 1);
        label.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        return pane;
    }

    void createSourcePropertyPane(final Composite parent,
                                  final int style) {
        sourcePropPane = createPropertyPane(parent, style);
        setSourceText();
        CLabel label = (CLabel)sourcePropPane.getChildren()[0];
        sourceDropTarget = new DropTarget(label, DND.DROP_MOVE);
        sourceDropTarget.setTransfer(new Transfer[] {LocalSelectionTransfer.getTransfer()});
        sourceDropTarget.addDropListener(new DropListener(label) {

            @Override
            boolean draggingFromValidObject() {
                return Util.draggingFromValidSource(config)
                       && Util.validSourceAndTarget(Util.draggedObject(),
                                                    mapping.getTarget(),
                                                    config);
            }

            @Override
            void drop() throws Exception {
                dropOnSource();
            }
        });
        potentialDropTargets.add(new PotentialDropTarget(label) {

            @Override
            public boolean valid() {
                return mapping.getType() != MappingType.TRANSFORMATION
                       && Util.draggingFromValidSource(config)
                       && Util.validSourceAndTarget(Util.draggedObject(),
                                                    mapping.getTarget(),
                                                    config);
            }
        });
    }

    void createTargetPropertyPane(final Composite parent) {
        targetPropPane = createPropertyPane(parent, SWT.NONE);
        setTargetText();
        CLabel label = (CLabel)targetPropPane.getChildren()[0];
        targetDropTarget = new DropTarget(label, DND.DROP_MOVE);
        targetDropTarget.setTransfer(new Transfer[] {LocalSelectionTransfer.getTransfer()});
        targetDropTarget.addDropListener(new DropListener(label) {

            @Override
            boolean draggingFromValidObject() {
                return Util.draggingFromValidTarget(config)
                       && Util.validSourceAndTarget(mapping.getSource(),
                                                    Util.draggedObject(),
                                                    config);
            }

            @Override
            void drop() throws Exception {
                dropOnTarget();
            }
        });
        potentialDropTargets.add(new PotentialDropTarget(label) {

            @Override
            public boolean valid() {
                return mapping.getType() != MappingType.TRANSFORMATION
                       && Util.draggingFromValidTarget(config)
                       && Util.validSourceAndTarget(mapping.getSource(),
                                                    Util.draggedObject(),
                                                    config);
            }
        });
    }

    void dispose() {
        sourceDropTarget.dispose();
        targetDropTarget.dispose();
        for (final Iterator<PotentialDropTarget> iter = potentialDropTargets.iterator();
             iter.hasNext();) {
               final PotentialDropTarget potentialDropTarget = iter.next();
               if (potentialDropTarget.control == sourcePropPane.getChildren()[0]
                   || potentialDropTarget.control == targetPropPane.getChildren()[0]) {
                   iter.remove();
               }
        }
    }

    void dropOnSource() throws Exception {
        setSource(Util.draggedObject());
    }

    void dropOnTarget() throws Exception {
        setTarget((Model)Util.draggedObject());
    }

    boolean equals(final MappingOperation<?, ?> mapping,
                   final Object object) {
        if (mapping == object) {
            return true;
        }
        if (mapping == null || object == null) {
            return false;
        }
        if (!(object instanceof MappingOperation<?, ?>)) {
            return false;
        }
        final MappingOperation<?, ?> mapping2 = (MappingOperation<?, ?>)object;
        if (mapping.getSource() == mapping2.getSource()
            && mapping.getTarget() == mapping2.getTarget()) {
            return true;
        }
        if (mapping.getSource() != null && !mapping.getSource().equals(mapping2.getSource())) {
            return false;
        }
        if (mapping.getTarget() != null && !mapping.getTarget().equals(mapping2.getTarget())) {
            return false;
        }
        return true;
    }

    String name(final Object object) {
        if (object instanceof Model) return ((Model)object).getName();
        if (object instanceof Variable) return ((Variable)object).getName();
        if (object instanceof Expression) return ((Expression)object).getLanguage();
        return "";
    }

    void setSource(Object source) throws Exception {
        Model targetModel = (Model)mapping.getTarget();
        if (targetModel == null) mapping = config.setSource(mapping, source, null, null);
        else {
            boolean sourceIsOrInCollection = source instanceof Model && Util.isOrInCollection((Model)source);
            boolean targetIsOrInCollection = Util.isOrInCollection(targetModel);
            if (sourceIsOrInCollection == targetIsOrInCollection) {
                mapping = config.setSource(mapping, source, null, null);
            } else {
                List<Integer> sourceIndexes =
                    sourceIsOrInCollection ? Util.updateIndexes(mapping, source, mapping.getSourceIndex()) : null;
                List<Integer> targetIndexes =
                    targetIsOrInCollection ? Util.updateIndexes(mapping, targetModel, mapping.getTargetIndex()) : null;
                mapping = config.setSource(mapping, source, sourceIndexes, targetIndexes);
            }
        }
        if (mapping != null) Util.updateDateFormat(sourcePropPane.getShell(), mapping);
        config.save();
    }

    void setSourceText() {
        setText(sourcePropPane, mapping.getSource());
    }

    void setTarget(final Model targetModel) throws Exception {
        Object source = mapping.getSource();
        if (source == null) mapping = config.setTarget(mapping, targetModel, null, null);
        else {
            boolean sourceIsOrInCollection = source instanceof Model && Util.isOrInCollection((Model)source);
            boolean targetIsOrInCollection = Util.isOrInCollection(targetModel);
            if (sourceIsOrInCollection == targetIsOrInCollection) mapping = config.setTarget(mapping, targetModel, null, null);
            else {
                List<Integer> sourceIndexes =
                    sourceIsOrInCollection ? Util.updateIndexes(mapping, source, mapping.getSourceIndex()) : null;
                List<Integer> targetIndexes =
                    targetIsOrInCollection ? Util.updateIndexes(mapping, targetModel, mapping.getTargetIndex()) : null;
                mapping = config.setTarget(mapping, targetModel, sourceIndexes, targetIndexes);
            }
        }
        if (mapping != null) Util.updateDateFormat(sourcePropPane.getShell(), mapping);
        config.save();
    }

    void setTargetText() {
        setText(targetPropPane, mapping.getTarget());
    }

    private void setText(final Composite propPane,
                         final Object object) {
        CLabel label = (CLabel)propPane.getChildren()[0];
        label.setText(name(object));
        if (object instanceof Model) {
            label.setToolTipText(Util.fullyQualifiedName((Model)object));
            label.setBackground(Colors.BACKGROUND);
            label.setForeground(Colors.FOREGROUND);
            label.setImage(Images.PROPERTY);
        } else if (object instanceof Variable) {
            label.setToolTipText(variableToolTip((Variable)object));
            label.setBackground(Colors.BACKGROUND);
            label.setForeground(Colors.FOREGROUND);
            label.setImage(Images.VARIABLE);
        } else if (object instanceof Expression) {
            label.setToolTipText(((Expression)object).getExpression().replace("\\${", "${"));
            label.setBackground(Colors.BACKGROUND);
            label.setForeground(Colors.EXPRESSION);
        } else {
            label.setToolTipText("");
            label.setBackground(Colors.BACKGROUND);
            label.setForeground(Colors.FOREGROUND);
        }
    }

    String variableToolTip(final Variable variable) {
        return "\"" + variable.getValue() + "\"";
    }

    void variableValueUpdated(final Variable variable) {
        if (mapping != null && variable.equals(mapping.getSource())) {
            sourcePropPane.setToolTipText(variableToolTip(variable));
        }
    }

    abstract class DropListener extends DropTargetAdapter {

        private final CLabel dropLabel;
        private Color background;
        private Color foreground;

        DropListener(final CLabel dropLabel) {
            this.dropLabel = dropLabel;
        }

        @Override
        public final void dragEnter(final DropTargetEvent event) {
            background = dropLabel.getBackground();
            foreground = dropLabel.getForeground();
            if (mapping.getType() != MappingType.TRANSFORMATION && draggingFromValidObject()) {
                dropLabel.setBackground(Colors.DROP_TARGET_BACKGROUND);
                dropLabel.setForeground(Colors.DROP_TARGET_FOREGROUND);
            } else {
                event.detail = DND.DROP_NONE;
            }
        }

        abstract boolean draggingFromValidObject();

        @Override
        public final void dragLeave(final DropTargetEvent event) {
            dropLabel.setBackground(background);
            dropLabel.setForeground(foreground);
        }

        abstract void drop() throws Exception;

        @Override
        public final void drop(final DropTargetEvent event) {
            try {
                if (draggingFromValidObject()) {
                    drop();
                }
            } catch (final Exception e) {
                Activator.error(e);
            }
        }
    }
}
