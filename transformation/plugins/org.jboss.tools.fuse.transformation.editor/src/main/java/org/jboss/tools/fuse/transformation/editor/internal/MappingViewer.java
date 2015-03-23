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

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.jboss.mapper.CustomMapping;
import org.jboss.mapper.Expression;
import org.jboss.mapper.MappingOperation;
import org.jboss.mapper.MappingType;
import org.jboss.mapper.Variable;
import org.jboss.mapper.model.Model;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;

abstract class MappingViewer {

    final TransformationConfig config;
    MappingOperation<?, ?> mapping;
    Text sourceText, targetText;
    DropTarget sourceDropTarget, targetDropTarget;

    MappingViewer(final TransformationConfig config) {
        this.config = config;
    }

    void createSourceText(final Composite parent) {
        sourceText = createText(parent);
        setSourceText();
        sourceDropTarget = new DropTarget(sourceText, DND.DROP_MOVE);
        sourceDropTarget.setTransfer(new Transfer[] {LocalSelectionTransfer.getTransfer()});
        sourceDropTarget.addDropListener(new DropListener(sourceText,
                                                          config.getSourceModel()) {

            @Override
            void drop(final Object dragSource) throws Exception {
                dropOnSource(dragSource);
            }

            @Override
            boolean valid(final Object dragSource) {
                return super.valid(dragSource) || validSourceDropTarget(dragSource);
            }
        });
    }

    void createTargetText(final Composite parent) {
        targetText = createText(parent);
        setTargetText();
        targetDropTarget = new DropTarget(targetText, DND.DROP_MOVE);
        targetDropTarget.setTransfer(new Transfer[] {LocalSelectionTransfer.getTransfer()});
        targetDropTarget.addDropListener(new DropListener(targetText,
                                                          config.getTargetModel()) {

            @Override
            void drop(final Object dragSource) throws Exception {
                dropOnTarget((Model) dragSource);
            }
        });
    }

    Text createText(final Composite parent) {
        final Text text = new Text(parent, SWT.BORDER);
        text.setEditable(false);
        return text;
    }

    void dropOnSource(final Object dragSource) throws Exception {
        mapping = config.setSource(mapping, dragSource);
        config.save();
    }

    void dropOnTarget(final Model dragModel) throws Exception {
        mapping = config.setTarget(mapping, dragModel);
        config.save();
    }

    String name(final Object object) {
        if (object instanceof Model) return ((Model)object).getName();
        if (object instanceof Variable) return "${" + ((Variable)object).getName() + "}";
        if (object instanceof Expression) return "{" + ((Expression)object).getExpression() + "}";
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
            text.setToolTipText(config.fullyQualifiedName((Model)object));
            if (mapping.getType() == MappingType.CUSTOM) text.setBackground(Colors.FUNCTION);
            else text.setBackground(Colors.BACKGROUND);
            text.setForeground(Colors.FOREGROUND);
        } else if (object instanceof Variable) {
            text.setToolTipText("\"" + ((Variable)object).getValue() + "\"");
            text.setBackground(Colors.BACKGROUND);
            text.setForeground(Colors.VARIABLE);
        } else if (object instanceof Expression) {
            text.setToolTipText(((Expression)object).getLanguage() + "-language expression");
            text.setBackground(Colors.BACKGROUND);
            text.setForeground(Colors.EXPRESSION);
        } else {
            text.setToolTipText("");
            text.setBackground(Colors.BACKGROUND);
            text.setForeground(Colors.FOREGROUND);
        }
    }

    boolean validSourceDropTarget(final Object dragSource) {
        return dragSource instanceof Variable && !(mapping instanceof CustomMapping);
    }

    abstract class DropListener extends DropTargetAdapter {

        private final Text dropText;
        private final Model dragRootModel;
        private Color background, foreground;

        DropListener(final Text dropText,
                     final Model dragRootModel) {
            this.dropText = dropText;
            this.dragRootModel = dragRootModel;
        }

        @Override
        public final void dragEnter(final DropTargetEvent event) {
            background = dropText.getBackground();
            foreground = dropText.getForeground();
            if (valid(dragSource())) {
                dropText.setBackground(Colors.DROP_TARGET_BACKGROUND);
                dropText.setForeground(Colors.DROP_TARGET_FOREGROUND);
            }
        }

        @Override
        public final void dragLeave(final DropTargetEvent event) {
            dropText.setBackground(background);
            dropText.setForeground(foreground);
        }

        private Object dragSource() {
            return ((IStructuredSelection) LocalSelectionTransfer.getTransfer()
                                                                 .getSelection())
                                                                 .getFirstElement();
        }

        @Override
        public final void drop(final DropTargetEvent event) {
            try {
                drop(dragSource());
            } catch (final Exception e) {
                Activator.error(e);
            }
        }

        abstract void drop(final Object dragSource) throws Exception;

        boolean valid(final Object dragSource) {
            return dragSource instanceof Model
                   && config.root((Model) dragSource).equals(dragRootModel);
        }
    }
}
