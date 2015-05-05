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

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.fuse.transformation.Expression;
import org.jboss.tools.fuse.transformation.MappingOperation;
import org.jboss.tools.fuse.transformation.MappingType;
import org.jboss.tools.fuse.transformation.Variable;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.model.Model;

abstract class MappingViewer {

    final TransformationConfig config;
    MappingOperation<?, ?> mapping;
    Text sourceText;
    Text targetText;
    DropTarget sourceDropTarget;
    DropTarget targetDropTarget;
    final List<PotentialDropTarget> potentialDropTargets;

    MappingViewer(final TransformationConfig config,
                  final List<PotentialDropTarget> potentialDropTargets) {
        this.config = config;
        this.potentialDropTargets = potentialDropTargets;
    }

    void createSourceText(final Composite parent,
                          final int style) {
        sourceText = createText(parent, style);
        setSourceText();
        sourceDropTarget = new DropTarget(sourceText, DND.DROP_MOVE);
        sourceDropTarget.setTransfer(new Transfer[] {LocalSelectionTransfer.getTransfer()});
        sourceDropTarget.addDropListener(new DropListener(sourceText) {

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
        potentialDropTargets.add(new PotentialDropTarget(sourceText) {

            @Override
            public boolean valid() {
                return mapping.getType() != MappingType.CUSTOM
                       && Util.draggingFromValidSource(config)
                       && Util.validSourceAndTarget(Util.draggedObject(),
                                                    mapping.getTarget(),
                                                    config);
            }
        });
    }

    void createTargetText(final Composite parent) {
        targetText = createText(parent, SWT.NONE);
        setTargetText();
        targetDropTarget = new DropTarget(targetText, DND.DROP_MOVE);
        targetDropTarget.setTransfer(new Transfer[] {LocalSelectionTransfer.getTransfer()});
        targetDropTarget.addDropListener(new DropListener(targetText) {

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
        potentialDropTargets.add(new PotentialDropTarget(targetText) {

            @Override
            public boolean valid() {
                return mapping.getType() != MappingType.CUSTOM
                       && Util.draggingFromValidTarget(config)
                       && Util.validSourceAndTarget(mapping.getSource(),
                                                    Util.draggedObject(),
                                                    config);
            }
        });
    }

    Text createText(final Composite parent,
                    final int style) {
        final Text text = new Text(parent, style | SWT.BORDER);
        text.setEditable(false);
        return text;
    }

    void dispose() {
        sourceDropTarget.dispose();
        targetDropTarget.dispose();
        for (final Iterator<PotentialDropTarget> iter = potentialDropTargets.iterator();
             iter.hasNext();) {
               final PotentialDropTarget potentialDropTarget = iter.next();
               if (potentialDropTarget.control == sourceText
                   || potentialDropTarget.control == targetText) {
                   iter.remove();
               }
        }
    }

    void dropOnSource() throws Exception {
        mapping = config.setSource(mapping, Util.draggedObject());
        config.save();
    }

    void dropOnTarget() throws Exception {
        mapping = config.setTarget(mapping, (Model)Util.draggedObject());
        config.save();
    }

    String name(final Object object) {
        if (object instanceof Model) {
            return ((Model)object).getName();
        } else if (object instanceof Variable) {
            return "${" + ((Variable)object).getName() + "}";
        } else if (object instanceof Expression) {
            return ((Expression)object).getLanguage();
        }
        return "";
    }

    void setSourceText() {
        setText(sourceText, mapping.getSource());
    }

    void setTargetText() {
        setText(targetText, mapping.getTarget());
    }

    private void setText(final Text text,
                         final Object object) {
        text.setText(name(object));
        if (object instanceof Model) {
            final Model model = (Model)object;
            text.setToolTipText(config.fullyQualifiedName(model));
            if (mapping.getType() == MappingType.CUSTOM && text == sourceText) {
                text.setBackground(Colors.FUNCTION);
            } else {
                text.setBackground(Colors.BACKGROUND);
            }
            text.setForeground(Colors.FOREGROUND);
        } else if (object instanceof Variable) {
            text.setToolTipText(variableToolTip((Variable)object));
            text.setBackground(Colors.BACKGROUND);
            text.setForeground(Colors.VARIABLE);
        } else if (object instanceof Expression) {
            text.setToolTipText(((Expression)object).getExpression().replace("\\${", "${"));
            text.setBackground(Colors.BACKGROUND);
            text.setForeground(Colors.EXPRESSION);
        } else {
            text.setToolTipText("");
            text.setBackground(Colors.BACKGROUND);
            text.setForeground(Colors.FOREGROUND);
        }
    }

    String variableToolTip(final Variable variable) {
        return "\"" + variable.getValue() + "\"";
    }

    void variableValueUpdated(final Variable variable) {
        if (variable.equals(mapping.getSource()))
            sourceText.setToolTipText(variableToolTip(variable));
    }

    abstract class DropListener extends DropTargetAdapter {

        private final Text dropText;
        private Color background;
        private Color foreground;

        DropListener(final Text dropText) {
            this.dropText = dropText;
        }

        @Override
        public final void dragEnter(final DropTargetEvent event) {
            background = dropText.getBackground();
            foreground = dropText.getForeground();
            if (mapping.getType() != MappingType.CUSTOM && draggingFromValidObject()) {
                dropText.setBackground(Colors.DROP_TARGET_BACKGROUND);
                dropText.setForeground(Colors.DROP_TARGET_FOREGROUND);
            } else {
                event.detail = DND.DROP_NONE;
            }
        }

        abstract boolean draggingFromValidObject();

        @Override
        public final void dragLeave(final DropTargetEvent event) {
            dropText.setBackground(background);
            dropText.setForeground(foreground);
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
