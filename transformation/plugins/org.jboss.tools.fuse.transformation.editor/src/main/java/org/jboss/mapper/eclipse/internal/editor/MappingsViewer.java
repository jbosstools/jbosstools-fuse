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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.jboss.mapper.MappingOperation;
import org.jboss.mapper.eclipse.TransformationEditor;

/**
 *
 */
public class MappingsViewer extends Composite {

    ScrolledComposite scroller;
    Composite pane;
    Composite sourcePane;
    Composite mapsToPane;
    Composite targetPane;
    Point iconButtonSize;
    Color textBackground;
    Text prevTargetText;
    TraversalListener prevTraversalListener;
    Mapping selectedMapping;
    final List<SelectionListener> selectionListeners = new ArrayList<>();

    /**
     * @param editor
     * @param parent
     * @param mappings
     */
    public MappingsViewer(final TransformationEditor editor,
            final Composite parent,
            final List<MappingOperation<?, ?>> mappings) {
        super(parent, SWT.NONE);

        setLayout(GridLayoutFactory.fillDefaults().create());
        setBackground(parent.getBackground());

        // Create tool bar
        // final ToolBar toolBar = new ToolBar( this, SWT.NONE );
        // final ToolItem addButton = new ToolItem( toolBar, SWT.PUSH );
        // addButton.setImage(
        // PlatformUI.getWorkbench().getSharedImages().getImage(
        // ISharedImages.IMG_OBJ_ADD ) );
        // addButton.setToolTipText( "Add new transformation" );

        createContents(editor, mappings);
    }

    /**
     * @param listener
     */
    public void addSelectionListener(final SelectionListener listener) {
        selectionListeners.add(listener);
    }

    private void createContents(final TransformationEditor editor,
            final List<MappingOperation<?, ?>> mappings) {
        scroller = new ScrolledComposite(this, SWT.V_SCROLL | SWT.BORDER);
        scroller.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);
        scroller.setBackground(getBackground());
        pane = new Composite(scroller, SWT.NONE);
        scroller.setContent(pane);
        pane.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).spacing(0, 0).create());
        pane.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLUE));
        pane.setBackground(getBackground());
        sourcePane = new Composite(pane, SWT.NONE);
        sourcePane.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        sourcePane.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
        sourcePane.setBackground(getBackground());
        mapsToPane = new Composite(pane, SWT.NONE);
        mapsToPane.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());
        mapsToPane.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
        mapsToPane.setBackground(getBackground());
        targetPane = new Composite(pane, SWT.NONE);
        targetPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        targetPane.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
        targetPane.setBackground(getBackground());

        for (final MappingOperation<?, ?> mapping : mappings) {
            createMapping(editor, mapping);
        }
    }

    /**
     * @param editor
     * @param mapping
     */
    public void createMapping(final TransformationEditor editor,
            final MappingOperation<?, ?> mapping) {
        final Mapping uiMapping = new Mapping(editor, this, mapping);
        uiMapping.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                if (selectedMapping != null) {
                    selectedMapping.deselect();
                }
                selectedMapping = uiMapping;
                selectedMapping.select();
                for (SelectionListener listener : selectionListeners) {
                    listener.widgetSelected(event);
                }
            }
        });
    }

    /**
     * @param editor
     * @param mappings
     */
    public void refresh(final TransformationEditor editor,
            final List<MappingOperation<?, ?>> mappings) {
        scroller.dispose();
        createContents(editor, mappings);
    }

    interface CustomOperationListener {

        void operationChanged(String text);
    }

    static final class TraversalListener implements TraverseListener {

        Text prevText;
        Text nextText;

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
