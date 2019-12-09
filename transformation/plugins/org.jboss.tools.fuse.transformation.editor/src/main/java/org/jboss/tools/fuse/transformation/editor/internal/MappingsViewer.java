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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.jboss.tools.fuse.transformation.core.MappingOperation;
import org.jboss.tools.fuse.transformation.core.MappingType;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.TransformationEditor;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationManager;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationManager.Event;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Decorations;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;
import org.jboss.tools.fuse.transformation.core.model.Model;

public class MappingsViewer extends Composite {

    private final TransformationEditor editor;
    private final ToolItem deleteButton;
    private ScrolledComposite scroller;
    private Composite summaryPane;
    Composite sourcePane;
    Composite mapsToPane;
    Composite targetPane;
    private MappingSummary selectedMappingSummary;
    private final List<MappingSummary> mappingSummaries = new ArrayList<>();
    private final List<PotentialDropTarget> potentialDropTargets;
    private final Listener focusListener;
    private final Listener keyListener;

    public MappingsViewer(final TransformationManager manager,
                          TransformationEditor editor,
                          Composite parent,
                          List<PotentialDropTarget> potentialDropTargets) {
        super(parent, SWT.NONE);
        this.editor = editor;
        this.potentialDropTargets = potentialDropTargets;

        setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
        setBackground(parent.getParent().getBackground());
        final Label title = new Label(this, SWT.CENTER);
        title.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        title.setText(Messages.MappingsViewer_labelMappings);

        // Create tool bar
        final ToolBar toolBar = new ToolBar(this, SWT.NONE);
        final ToolItem addButton = new ToolItem(toolBar, SWT.PUSH);
        addButton.setImage(new DecorationOverlayIcon(Images.MAPPED,
                                                     Decorations.ADD,
                                                     IDecoration.TOP_RIGHT).createImage());
        addButton.setToolTipText(Messages.MappingsViewer_tooltipMappings);
        addButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                manager.newMapping();
            }
        });
        deleteButton = new ToolItem(toolBar, SWT.PUSH);
        deleteButton.setImage(Images.DELETE);
        deleteButton.setToolTipText(Messages.MappingsViewer_tooltipDeleteMapping);
        deleteButton.setEnabled(false);
        deleteButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                deleteSelectedMappingSummary(manager);
            }
        });
        scroller = new ScrolledComposite(this, SWT.V_SCROLL);
        scroller.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);
        scroller.setBackground(getBackground());
        summaryPane = new Composite(scroller, SWT.NONE);
        scroller.setContent(summaryPane);
        summaryPane.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).spacing(0, 0).create());
        summaryPane.setBackground(getBackground());
        sourcePane = new Composite(summaryPane, SWT.NONE);
        sourcePane.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        sourcePane.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
        sourcePane.setBackground(getBackground());
        mapsToPane = new Composite(summaryPane, SWT.NONE);
        mapsToPane.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
        mapsToPane.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());
        mapsToPane.setBackground(getBackground());
        targetPane = new Composite(summaryPane, SWT.NONE);
        targetPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        targetPane.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
        targetPane.setBackground(getBackground());

        for (MappingOperation<?, ?> mapping : manager.mappings()) {
            if (mapping.getType() == MappingType.EXPRESSION
                || mapping.getType() == MappingType.VARIABLE
                || !((Model)mapping.getSource()).isCollection()
                || !((Model)mapping.getTarget()).isCollection()) {
                mappingSummaries.add(new MappingSummary(manager, mapping, this, potentialDropTargets));
            }
        }

        int width = Math.max(sourcePane.computeSize(SWT.DEFAULT, SWT.DEFAULT).x,
                             targetPane.computeSize(SWT.DEFAULT, SWT.DEFAULT).x);
        ((GridData)sourcePane.getLayoutData()).widthHint = width;
        ((GridData)targetPane.getLayoutData()).widthHint = width;

        scroller.setMinSize(summaryPane.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        manager.addListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (!event.getPropertyName().equals(Event.MAPPING.name())) return;
                MappingOperation<?, ?> mapping = (MappingOperation<?, ?>)event.getNewValue();
                if (mapping != null) mappingSummaryAdded(manager, mapping);
            }
        });

        focusListener = new Listener() {

            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                if (selectedMappingSummary == null || selectedMappingSummary.mapsToLabel.isDisposed()) return;
                selectedMappingSummary.setBackground(child((Control)event.widget) ? Colors.SELECTED : Colors.SELECTED_NO_FOCUS);
            }
        };
        getDisplay().addFilter(SWT.FocusIn, focusListener);
        getDisplay().addFilter(SWT.MouseDown, focusListener);
        keyListener = new Listener() {

            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                if (selectedMappingSummary == null || selectedMappingSummary.mapsToLabel.isDisposed()) return;
                if (selectedMappingSummary.mapsToLabel.getBackground().equals(Colors.SELECTED_NO_FOCUS)) return;
                switch (event.keyCode) {
                    case SWT.ARROW_UP: {
                        if ((event.stateMask & (SWT.COMMAND)) != 0) selectMappingSummary(0);
                        else selectPreviousMappingSummary();
                        break;
                    }
                    case SWT.ARROW_DOWN: {
                        if ((event.stateMask & (SWT.COMMAND)) != 0) selectMappingSummary(mappingSummaries.size() - 1);
                        else selectNextMappingSummary();
                        break;
                    }
                    case SWT.BS:
                    case SWT.DEL: {
                        deleteSelectedMappingSummary(manager);
                        break;
                    }
                    case '=': {
                        if ((event.stateMask & (SWT.SHIFT)) == 0) break;
                    }
                    // $FALL-THROUGH$ if SHIFT-EQUAL ('+')
                    case '+':
                    case SWT.KEYPAD_ADD: {
                        if ((event.stateMask & (SWT.SHIFT)) != 0) manager.newMapping();
                        break;
                    }
                }
                event.type = SWT.NONE;
            }
        };
        getDisplay().addFilter(SWT.KeyDown, keyListener);
    }

    private boolean child(Control control) {
        if (control.getParent() == null) return false;
        if (control.getParent() == this) return true;
        return child(control.getParent());
    }

    private void deleteSelectedMappingSummary(TransformationManager manager) {
        try {
            manager.unMap(selectedMappingSummary.mapping);
            manager.save();
        } catch (final Exception e) {
            Activator.error(e);
        }
    }

    @Override
    public void dispose() {
        getDisplay().removeFilter(SWT.FocusIn, focusListener);
        getDisplay().removeFilter(SWT.MouseDown, focusListener);
        getDisplay().removeFilter(SWT.KeyDown, keyListener);
        super.dispose();
    }

    void layoutPanes() {
        sourcePane.layout();
        mapsToPane.layout();
        targetPane.layout();
    }

    void mappingSummaryAdded(TransformationManager manager,
                             MappingOperation<?, ?> mapping) {
        MappingSummary mappingSummary = new MappingSummary(manager, mapping, this, potentialDropTargets);
        mappingSummaries.add(mappingSummary);
        layoutPanes();
        scroller.setMinSize(summaryPane.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        selected(mappingSummary);
    }

    /**
     * Called by {@link MappingSummary#dispose(MappingOperation)}
     *
     * @param mappingSummary
     */
    void mappingSummaryDeleted(MappingSummary mappingSummary) {
        mappingSummaries.remove(mappingSummary);
        if (mappingSummary == selectedMappingSummary) {
            selectedMappingSummary = null;
            deleteButton.setEnabled(false);
        }
        layoutPanes();
        scroller.setMinSize(summaryPane.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    void selected(MappingSummary mappingSummary) {
        forceFocus();
        if (selectedMappingSummary != null && mappingSummary != selectedMappingSummary) selectedMappingSummary.deselect();
        selectedMappingSummary = mappingSummary;
        selectedMappingSummary.select();
        deleteButton.setEnabled(true);
        editor.selected(mappingSummary.mapping);
        scroller.showControl(selectedMappingSummary.mapsToLabel);
    }

    private void selectMappingSummary(int index) {
        selected(mappingSummaries.get(index));
    }

    private void selectNextMappingSummary() {
        int ndx = mappingSummaries.indexOf(selectedMappingSummary) + 1;
        if (ndx < mappingSummaries.size()) selectMappingSummary(ndx);
    }

    private void selectPreviousMappingSummary() {
        int ndx = mappingSummaries.indexOf(selectedMappingSummary) - 1;
        if (ndx >= 0) selectMappingSummary(ndx);
    }
}
