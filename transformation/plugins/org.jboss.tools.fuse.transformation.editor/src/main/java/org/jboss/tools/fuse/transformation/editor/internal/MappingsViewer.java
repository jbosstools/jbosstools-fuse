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
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.jboss.mapper.MappingOperation;
import org.jboss.mapper.model.Model;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.TransformationEditor;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Decorations;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;

/**
 *
 */
public class MappingsViewer extends Composite {

    final TransformationEditor editor;
    final ToolItem deleteButton;
    ScrolledComposite scroller;
    Composite summaryPane, sourcePane, mapsToPane, targetPane;
    Text prevTargetText;
    TraversalListener prevTraversalListener;
    MappingSummary selectedMappingSummary;
    final List<MappingSummary> mappingSummaries = new ArrayList<>();
    private final List<PotentialDropTarget> potentialDropTargets;

    /**
     * @param config
     * @param editor
     * @param parent
     * @param potentialDropTargets
     */
    public MappingsViewer(final TransformationConfig config,
                          final TransformationEditor editor,
                          final Composite parent,
                          final List<PotentialDropTarget> potentialDropTargets) {
        super(parent, SWT.NONE);
        this.editor = editor;
        this.potentialDropTargets = potentialDropTargets;

        setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
        setBackground(parent.getParent().getBackground());

        // Create tool bar
        final ToolBar toolBar = new ToolBar(this, SWT.NONE);
        final ToolItem addButton = new ToolItem(toolBar, SWT.PUSH);
        addButton.setImage(new DecorationOverlayIcon(Images.MAPPED,
                                                     Decorations.ADD,
                                                     IDecoration.TOP_RIGHT).createImage());
        addButton.setToolTipText("Add a new mapping");
        addButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                config.newMapping();
            }
        });
        deleteButton = new ToolItem(toolBar, SWT.PUSH);
        deleteButton.setImage(Images.DELETE);
        deleteButton.setToolTipText("Delete the selected mapping");
        deleteButton.setEnabled(false);
        deleteButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    config.removeMapping(selectedMappingSummary.mapping);
                    config.save();
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });
        final Composite headerPane = new Composite(this, SWT.NONE);
        headerPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        final Label sourceHeader = createHeader(headerPane, config.getSourceModel());
        final Label targetHeader = createHeader(headerPane, config.getTargetModel());
        scroller = new ScrolledComposite(this, SWT.V_SCROLL);
        scroller.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);
        scroller.setBackground(getBackground());
        summaryPane = new Composite(scroller, SWT.NONE);
        scroller.setContent(summaryPane);
        summaryPane.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).spacing(0, 0).create());
        summaryPane.setBackground(getBackground());
        sourcePane = new Composite(summaryPane, SWT.BORDER);
        sourcePane.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        sourcePane.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
        sourcePane.setBackground(getBackground());
        mapsToPane = new Composite(summaryPane, SWT.NONE);
        final int margin = sourcePane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y / 2;
        mapsToPane.setLayout(GridLayoutFactory.fillDefaults()
                                              .margins(margin, margin)
                                              .spacing(0, 0)
                                              .create());
        final Composite tempPane = new Composite(mapsToPane, SWT.NONE);
        tempPane.setLayoutData(GridDataFactory.swtDefaults().create());
        tempPane.setLayout(GridLayoutFactory.swtDefaults().create());
        final Label tempLabel = new Label(tempPane, SWT.NONE);
        tempLabel.setImage(Images.MAPPED);
        final int mapsToPaneWidth = mapsToPane.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
        tempLabel.dispose();
        tempPane.dispose();
        mapsToPane.setLayoutData(GridDataFactory.fillDefaults()
                                                .grab(false, true)
                                                .hint(mapsToPaneWidth, SWT.DEFAULT)
                                                .create());
        mapsToPane.setBackground(getBackground());
        targetPane = new Composite(summaryPane, SWT.BORDER);
        targetPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        targetPane.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
        targetPane.setBackground(getBackground());

        for (final MappingOperation<?, ?> mapping : config.getMappings()) {
            mappingSummaries.add(new MappingSummary(config,
                                                    mapping,
                                                    this,
                                                    potentialDropTargets));
        }

        int width = Math.max(sourcePane.computeSize(SWT.DEFAULT, SWT.DEFAULT).x,
                             targetPane.computeSize(SWT.DEFAULT, SWT.DEFAULT).x);
        width = Math.max(sourceHeader.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, width);
        width = Math.max(targetHeader.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, width);
        ((GridData)sourcePane.getLayoutData()).widthHint = width;
        ((GridData)targetPane.getLayoutData()).widthHint = width;

        sourcePane.addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(final ControlEvent event) {
                sourceHeader.setBounds(sourcePane.getLocation().x, 0,
                                       sourcePane.getSize().x, sourceHeader.getSize().y);
            }
        });
        targetPane.addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(final ControlEvent event) {
                targetHeader.setBounds(targetPane.getLocation().x, 0,
                                       targetPane.getSize().x, targetHeader.getSize().y);
            }
        });
        scroller.setMinSize(summaryPane.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        config.addListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                if (!event.getPropertyName().equals(TransformationConfig.MAPPING)) return;
                final MappingOperation<?, ?> mapping = (MappingOperation<?, ?>) event.getNewValue();
                if (mapping != null) addMappingSummary(config, mapping);
            }
        });
    }

    void addMappingSummary(final TransformationConfig config,
                           final MappingOperation<?, ?> mapping) {
        final MappingSummary mappingSummary =
            new MappingSummary(config, mapping, this, potentialDropTargets);
        mappingSummaries.add(mappingSummary);
        layoutPanes();
        scroller.setMinSize(summaryPane.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        scroller.setOrigin(0, scroller.getSize().y);
        mappingSummary.sourceText.setFocus(); // This will call selected()
    }

    private Label createHeader(final Composite parent,
                               final Model model) {
        final Label label = new Label(parent, SWT.CENTER);
        label.setText(model.getName());
        label.setBackground(Colors.MODEL);
        label.setSize(label.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        label.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(final PaintEvent event) {
                event.gc.setForeground(event.display.getSystemColor(SWT.COLOR_DARK_GRAY));
                final Rectangle bounds = label.getBounds();
                event.gc.drawRectangle(0, 0, bounds.width - 1, bounds.height - 1);
            }
        });
        return label;
    }

    private void focusOnMappingSummary(final int index) {
        final MappingSummary mappingSummary = mappingSummaries.get(index);
        if (selectedMappingSummary.sourceText.isFocusControl())
            mappingSummary.sourceText.setFocus();
        else mappingSummary.targetText.setFocus();
    }

    void layoutPanes() {
        sourcePane.layout();
        mapsToPane.layout();
        targetPane.layout();
    }

    /**
     * Called by {@link MappingSummary#dispose(MappingOperation)}
     *
     * @param mappingSummary
     */
    void removeMappingSummary(final MappingSummary mappingSummary) {
        mappingSummaries.remove(mappingSummary);
        if (mappingSummary == selectedMappingSummary) {
            selectedMappingSummary = null;
            deleteButton.setEnabled(false);
        }
        layoutPanes();
        scroller.setMinSize(summaryPane.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    void selected(final MappingSummary mappingSummary) {
        if (selectedMappingSummary != null && mappingSummary != selectedMappingSummary)
            selectedMappingSummary.deselect();
        selectedMappingSummary = mappingSummary;
        deleteButton.setEnabled(true);
        editor.selected(mappingSummary.mapping);
    }

    void selectNextMappingSummary() {
        final int ndx = mappingSummaries.indexOf(this) + 1;
        if (ndx < mappingSummaries.size()) focusOnMappingSummary(ndx);
    }

    void selectPreviousMappingSummary() {
        final int ndx = mappingSummaries.indexOf(this) - 1;
        if (ndx >= 0) focusOnMappingSummary(ndx);
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
