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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.fuse.transformation.core.Expression;
import org.jboss.tools.fuse.transformation.core.MappingOperation;
import org.jboss.tools.fuse.transformation.core.MappingType;
import org.jboss.tools.fuse.transformation.core.Variable;
import org.jboss.tools.fuse.transformation.core.model.Model;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationManager;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;

abstract class MappingViewer {

    final TransformationManager manager;
    MappingOperation<?, ?> mapping;
    Composite sourcePropPane;
    Composite targetPropPane;
    DropTarget sourceDropTarget;
    private DropTarget targetDropTarget;
    private final List<PotentialDropTarget> potentialDropTargets;

    MappingViewer(TransformationManager manager,
                  List<PotentialDropTarget> potentialDropTargets) {
        this.manager = manager;
        this.potentialDropTargets = potentialDropTargets;
    }

    Composite createPropertyPane(Composite parent,
                                 int style) {
        Composite pane = new Composite(parent, SWT.BORDER);
        pane.setLayout(GridLayoutFactory.fillDefaults().create());
        CLabel label = new CLabel(pane, style);
        label.setMargins(1, 1, 1, 1);
        label.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        return pane;
    }

    void createSourcePropertyPane(Composite parent,
                                  int style) {
        sourcePropPane = createPropertyPane(parent, style);
        setSourceText();
        CLabel label = (CLabel)sourcePropPane.getChildren()[0];
        sourceDropTarget = new DropTarget(label, DND.DROP_MOVE);
        sourceDropTarget.setTransfer(LocalSelectionTransfer.getTransfer());
        sourceDropTarget.addDropListener(new DropListener(label) {

            @Override
            boolean draggingFromValidObject() {
                Object source = Util.draggedObject();
                boolean valid = Util.draggingFromValidSource(manager)
                                && Util.validSourceAndTarget(source, mapping.getTarget(), manager);
                // Ensure property types are the same if old property is in transformation mapping
                if (valid && mapping.getType() == MappingType.TRANSFORMATION)
                    return source instanceof Model && ((Model)source).getType().equals(((Model)mapping.getSource()).getType());
                return valid;
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
                       && Util.draggingFromValidSource(manager)
                       && Util.validSourceAndTarget(Util.draggedObject(), mapping.getTarget(), manager);
            }
        });
    }

    void createTargetPropertyPane(Composite parent) {
        targetPropPane = createPropertyPane(parent, SWT.NONE);
        setTargetText();
        CLabel label = (CLabel)targetPropPane.getChildren()[0];
        targetDropTarget = new DropTarget(label, DND.DROP_MOVE);
        targetDropTarget.setTransfer(LocalSelectionTransfer.getTransfer());
        targetDropTarget.addDropListener(new DropListener(label) {

            @Override
            boolean draggingFromValidObject() {
                return Util.draggingFromValidTarget(manager)
                       && Util.validSourceAndTarget(mapping.getSource(), Util.draggedObject(), manager);
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
                       && Util.draggingFromValidTarget(manager)
                       && Util.validSourceAndTarget(mapping.getSource(),
                                                    Util.draggedObject(),
                                                    manager);
            }
        });
    }

    public void dispose() {
        sourceDropTarget.dispose();
        targetDropTarget.dispose();
        for (Iterator<PotentialDropTarget> iter = potentialDropTargets.iterator();
             iter.hasNext();) {
               PotentialDropTarget potentialDropTarget = iter.next();
               if (potentialDropTarget.control == sourcePropPane.getChildren()[0]
                   || potentialDropTarget.control == targetPropPane.getChildren()[0]) {
                   iter.remove();
               }
        }
    }

    private void dropOnSource() throws Exception {
        setSource(Util.draggedObject());
    }

    private void dropOnTarget() throws Exception {
        setTarget((Model)Util.draggedObject());
    }

    boolean mappingsEqual(MappingOperation<?, ?> mapping,
                          Object object) {
        if (mapping == object) {
        	return true;
        }
        if (mapping == null || object == null) {
        	return false;
        }
        if (!(object instanceof MappingOperation<?, ?>)) {
        	return false;
        }
        MappingOperation<?, ?> mapping2 = (MappingOperation<?, ?>)object;
        if (mapping.getSource() == mapping2.getSource() && mapping.getTarget() == mapping2.getTarget()) {
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

    String name(Object object) {
        if (object instanceof Model) {
        	return ((Model)object).getName();
        }
        if (object instanceof Variable) {
        	return ((Variable)object).getName();
        }
        if (object instanceof Expression) {
        	return ((Expression)object).getLanguage();
        }
        return ""; //$NON-NLS-1$
    }

    void setSource(Object source) throws Exception {
        mapping = manager.setSource(mapping, source);
        Util.updateDateFormat(sourcePropPane.getShell(), mapping);
        manager.save();
    }

    void setSourceText() {
        setText(sourcePropPane, mapping.getSource());
    }

    void setTarget(Model target) throws Exception {
        mapping = manager.setTarget(mapping, target);
        Util.updateDateFormat(sourcePropPane.getShell(), mapping);
        manager.save();
    }

    void setTargetText() {
        setText(targetPropPane, mapping.getTarget());
    }

    private void setText(Composite propPane,
                         Object object) {
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
            label.setToolTipText(((Expression)object).getExpression().replace("\\${", "${")); //$NON-NLS-1$ //$NON-NLS-2$
            label.setBackground(Colors.BACKGROUND);
            label.setForeground(Colors.EXPRESSION);
        } else {
            label.setToolTipText(""); //$NON-NLS-1$
            label.setBackground(Colors.BACKGROUND);
            label.setForeground(Colors.FOREGROUND);
        }
    }

    private String variableToolTip(Variable variable) {
        return "\"" + variable.getValue() + "\""; //$NON-NLS-1$ //$NON-NLS-2$
    }

    void variableValueUpdated(Variable variable) {
        if (mapping != null && variable.equals(mapping.getSource())) {
        	sourcePropPane.setToolTipText(variableToolTip(variable));
        }
    }

    private abstract class DropListener extends DropTargetAdapter {

        private final CLabel dropLabel;
        private Color background;
        private Color foreground;

        private DropListener(final CLabel dropLabel) {
            this.dropLabel = dropLabel;
        }

        @Override
        public final void dragEnter(final DropTargetEvent event) {
            background = dropLabel.getBackground();
            foreground = dropLabel.getForeground();
            if (draggingFromValidObject()) {
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
