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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.jboss.mapper.MapperConfiguration;
import org.jboss.mapper.MappingOperation;
import org.jboss.mapper.eclipse.Activator;
import org.jboss.mapper.eclipse.TransformationEditor;
import org.jboss.mapper.eclipse.internal.util.Util;
import org.jboss.mapper.model.Model;

/**
 *
 */
public class MappingsViewer extends Composite {

    final ToolItem deleteButton;
    ScrolledComposite scroller;
    Composite pane, sourcePane, mapsToPane, targetPane;
    Color textBackground;
    Text prevTargetText;
    TraversalListener prevTraversalListener;
    MappingRow selectedRow;
    final List<MappingRow> mappingRows = new ArrayList<>();

    /**
     * @param editor
     * @param parent
     * @param config
     */
    public MappingsViewer(final TransformationEditor editor,
            final Composite parent,
            final MapperConfiguration config) {
        super(parent, SWT.NONE);

        setLayout(GridLayoutFactory.fillDefaults().create());
        setBackground(parent.getParent().getBackground());

        // Create tool bar
        final ToolBar toolBar = new ToolBar(this, SWT.NONE);
        final ToolItem addButton = new ToolItem(toolBar, SWT.PUSH);
        addButton.setImage(new DecorationOverlayIcon(Util.Images.MAPPED,
                Util.Decorations.ADD,
                IDecoration.TOP_RIGHT).createImage());
        addButton.setToolTipText("Add a new mapping");
        addButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                setFocus(createMapping(editor, null));
            }
        });
        addButton.setEnabled(false); // TODO remove once fully implemented
        deleteButton = new ToolItem(toolBar, SWT.PUSH);
        deleteButton.setImage(Util.Images.DELETE);
        deleteButton.setToolTipText("Delete the selected mapping");
        deleteButton.setEnabled(false);
        deleteButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    selectedRow.unmap();
                    selectedRow = null;
                    deleteButton.setEnabled(false);
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });

        createContents(editor, config);
    }

    @SuppressWarnings("unused")
    private void createContents(final TransformationEditor editor,
            final MapperConfiguration config) {
        scroller = new ScrolledComposite(this, SWT.V_SCROLL);
        scroller.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);
        scroller.setBackground(getBackground());
        pane = new Composite(scroller, SWT.NONE);
        scroller.setContent(pane);
        pane.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).spacing(0, 0).create());
        pane.setBackground(getBackground());
        createHeader(pane, config.getSourceModel());
        new Label(pane, SWT.NONE); // spacer
        createHeader(pane, config.getTargetModel());
        sourcePane = new Composite(pane, SWT.BORDER);
        sourcePane.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        sourcePane.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
        sourcePane.setBackground(getBackground());
        mapsToPane = new Composite(pane, SWT.NONE);
        mapsToPane.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());
        final int margin = sourcePane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y / 2;
        mapsToPane.setLayout(GridLayoutFactory.fillDefaults().margins(margin, margin).spacing(0, 0)
                .create());
        mapsToPane.setBackground(getBackground());
        targetPane = new Composite(pane, SWT.BORDER);
        targetPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        targetPane.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
        targetPane.setBackground(getBackground());

        for (final MappingOperation<?, ?> mapping : config.getMappings()) {
            createMapping(editor, mapping);
        }
    }

    private void createHeader(final Composite parent,
            final Model model) {
        final Composite headerPane = new Composite(parent, SWT.BORDER);
        headerPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        headerPane.setLayout(GridLayoutFactory.fillDefaults().create());
        final Label label = new Label(headerPane, SWT.CENTER);
        label.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        label.setText(model.getName());
        label.setBackground(getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
    }

    /**
     * @param editor
     * @param mapping
     * @return the newly-created mapping row
     */
    public MappingRow createMapping(final TransformationEditor editor,
            final MappingOperation<?, ?> mapping) {
        final MappingRow mappingRow = new MappingRow(editor, this, mapping);
        mappingRows.add(mappingRow);
        return mappingRow;
    }

    /**
     * @param editor
     * @param config
     */
    public void refresh(final TransformationEditor editor,
            final MapperConfiguration config) {
        scroller.dispose();
        createContents(editor, config);
    }

    void selectMapping(final TransformationEditor editor,
            final MappingRow row) {
        if (selectedRow != null)
            selectedRow.deselect();
        selectedRow = row;
        deleteButton.setEnabled(true);
        selectedRow.select();
        editor.selectMapping(row.mapping);
    }

    /**
     * @param mappingRow
     */
    public void setFocus(final MappingRow mappingRow) {
        mappingRow.sourceText.setFocus();
    }

    void updateLayout() {
        pane.layout();
        sourcePane.layout();
        mapsToPane.layout();
        targetPane.layout();
        scroller.setMinSize(pane.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    /**
     * @param mapping
     */
    public void updateMapping(final MappingOperation<?, ?> mapping) {
        selectedRow.mapping = mapping;
    }

    interface CustomFunctionListener {

        void functionChanged(String text);
    }

    static final class TraversalListener implements TraverseListener {

        Text prevText, nextText;
        TraversalListener prevTraversalListener, nextTraversalListener;

        TraversalListener(final Text prevText,
                final Text nextText) {
            this.prevText = prevText;
            this.nextText = nextText;
        }

        @Override
        public void keyTraversed(final TraverseEvent event) {
            if (event.detail == SWT.TRAVERSE_TAB_NEXT) {
                if (nextText != null) {
                    event.detail = SWT.TRAVERSE_NONE;
                    event.doit = false;
                    nextText.setFocus();
                }
            } else if (event.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
                if (prevText != null) {
                    event.detail = SWT.TRAVERSE_NONE;
                    event.doit = false;
                    prevText.setFocus();
                }
            }
        }
    }
}
