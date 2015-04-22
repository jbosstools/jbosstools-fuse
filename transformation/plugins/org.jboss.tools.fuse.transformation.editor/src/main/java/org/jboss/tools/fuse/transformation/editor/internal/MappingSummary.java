package org.jboss.tools.fuse.transformation.editor.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.fuse.transformation.CustomMapping;
import org.jboss.tools.fuse.transformation.MappingOperation;
import org.jboss.tools.fuse.transformation.MappingType;
import org.jboss.tools.fuse.transformation.editor.internal.MappingsViewer.TraversalListener;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationConfig;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;
import org.jboss.tools.fuse.transformation.model.Model;

final class MappingSummary extends MappingViewer {

    final MappingsViewer mappingsViewer;
    final Composite mappingSourcePane, mapsToPane, mappingTargetPane;
    final TraversalListener sourceTraversalListener, targetTraversalListener;

    MappingSummary(final TransformationConfig config,
                   final MappingOperation<?, ?> mapping,
                   final MappingsViewer mappingsViewer,
                   final List<PotentialDropTarget> potentialDropTargets) {
        super(config, potentialDropTargets);
        this.mapping = mapping;
        this.mappingsViewer = mappingsViewer;

        mappingSourcePane = createMappingPane(mappingsViewer.sourcePane);
        createSourceText(mappingSourcePane, SWT.RIGHT);
        sourceText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        mapsToPane = new Composite(mappingsViewer.mapsToPane, SWT.NONE);
        mapsToPane.setLayoutData(GridDataFactory.swtDefaults().create());
        mapsToPane.setLayout(GridLayoutFactory.swtDefaults().create());
        mapsToPane.setBackground(mappingsViewer.getBackground());
        final Label mapsToLabel = new Label(mapsToPane, SWT.NONE);
        mapsToLabel.setImage(Images.MAPPED);
        final StringBuilder builder = new StringBuilder();
        if (mapping.getType() == MappingType.CUSTOM) {
            builder.append(((CustomMapping)mapping).getMappingOperation());
            builder.append('(');
        }
        builder.append(name(mapping.getSource()));
        if (mapping.getType() == MappingType.CUSTOM) builder.append(')');
        builder.append(" => ");
        builder.append(name(mapping.getTarget()));
        mapsToLabel.setToolTipText(builder.toString());

        mappingTargetPane = createMappingPane(mappingsViewer.targetPane);
        createTargetText(mappingTargetPane);
        targetText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        // Make mappingSourcePane, mapsToLabel, & mappingTargetPane the same height
        int height = mappingSourcePane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
        height = Math.max(height, mapsToPane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
        height = Math.max(height, mappingTargetPane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
        ((GridData) mappingSourcePane.getLayoutData()).heightHint = height;
        ((GridData) mapsToPane.getLayoutData()).heightHint = height;
        ((GridData) mappingTargetPane.getLayoutData()).heightHint = height;

        // Configure traversal of source and target text to ignore immediate containers
        sourceTraversalListener = new TraversalListener(mappingsViewer.prevTargetText, targetText);
        sourceTraversalListener.prevTraversalListener = mappingsViewer.prevTraversalListener;
        sourceText.addTraverseListener(sourceTraversalListener);
        targetTraversalListener = new TraversalListener(sourceText, null);
        sourceTraversalListener.nextTraversalListener = targetTraversalListener;
        targetTraversalListener.prevTraversalListener = sourceTraversalListener;
        targetText.addTraverseListener(targetTraversalListener);
        if (mappingsViewer.prevTraversalListener != null) {
            mappingsViewer.prevTraversalListener.nextText = sourceText;
            mappingsViewer.prevTraversalListener.nextTraversalListener = sourceTraversalListener;
        }

        final MouseListener mouseListener = new MouseAdapter() {

            @Override
            public void mouseUp(final MouseEvent event) {
                sourceText.setFocus();
            }
        };
        mappingSourcePane.addMouseListener(mouseListener);
        mapsToPane.addMouseListener(mouseListener);
        mapsToLabel.addMouseListener(mouseListener);
        mappingTargetPane.addMouseListener(mouseListener);

        mappingsViewer.prevTargetText = targetText;
        mappingsViewer.prevTraversalListener = targetTraversalListener;

        config.addListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                configEvent(event.getPropertyName(), event.getOldValue(), event.getNewValue());
            }
        });
    }

    void configEvent(final String eventType,
                     final Object oldValue,
                     final Object newValue) {
        if (mapping != oldValue) return;
        if (eventType.equals(TransformationConfig.MAPPING))
            dispose((MappingOperation<?, ?>) oldValue);
        else if (eventType.equals(TransformationConfig.MAPPING_SOURCE)) {
            MappingOperation<?, ?> tempMapping = (MappingOperation<?, ?>) newValue;
            if (Util.dragSourceIsValid((Model) tempMapping.getSource()) == null) {
                mapping = (MappingOperation<?, ?>)newValue;
                setSourceText();
                mappingSourcePane.layout();
                sourceText.setFocus();
            }
        } else if (eventType.equals(TransformationConfig.MAPPING_TARGET)) {
            MappingOperation<?, ?> tempMapping = (MappingOperation<?, ?>) newValue;
            if (Util.dragDropComboIsValid((Model) tempMapping.getSource(), (Model) tempMapping.getTarget()) == null) {
                mapping = (MappingOperation<?, ?>)newValue;
                setTargetText();
                mappingTargetPane.layout();
                targetText.setFocus();
            }
        } else if (eventType.equals(TransformationConfig.MAPPING_CUSTOMIZE)) {
            mapping = (MappingOperation<?, ?>)newValue;
            setSourceText();
        }
    }

    private Composite createMappingPane(final Composite parent) {
        final Composite pane = new Composite(parent, SWT.NONE);
        pane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        pane.setLayout(GridLayoutFactory.swtDefaults().spacing(0, 5).create());
        pane.setBackground(mappingsViewer.getBackground());
        return pane;
    }

    @Override
    Text createText(final Composite parent,
                    final int style) {
        final Text text = super.createText(parent, style);
        // Create focus listener to change highlight color when focus is lost & gained
        text.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(final FocusEvent event) {
                selected(text);
            }

            @Override
            public void focusLost(final FocusEvent event) {
                if (mappingsViewer.selectedMappingSummary == MappingSummary.this)
                    setBackground(Colors.SELECTED_NO_FOCUS);
            }
        } );
        // Create key listener to make up and down arrow navigate selection up and down
        text.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(final KeyEvent event) {
                if (event.keyCode == SWT.ARROW_DOWN) mappingsViewer.selectNextMappingSummary();
                else if (event.keyCode == SWT.ARROW_UP) mappingsViewer.selectPreviousMappingSummary();
            }
        } );
        return text;
    }

    void deselect() {
        setBackground(mappingsViewer.getBackground());
    }

    void dispose(final MappingOperation<?, ?> mapping) {
        mappingSourcePane.dispose();
        mapsToPane.dispose();
        mappingTargetPane.dispose();
        if (sourceTraversalListener.prevTraversalListener != null) {
            sourceTraversalListener.prevTraversalListener.nextText =
                targetTraversalListener.nextText;
            sourceTraversalListener.prevTraversalListener.nextTraversalListener =
                targetTraversalListener.nextTraversalListener;
        }
        if (targetTraversalListener.nextTraversalListener != null) {
            targetTraversalListener.nextTraversalListener.prevText =
                sourceTraversalListener.prevText;
            targetTraversalListener.nextTraversalListener.prevTraversalListener =
                sourceTraversalListener.prevTraversalListener;
        }
        mappingsViewer.removeMappingSummary(this);
        dispose();
    }

    void selected(final Text text) {
        text.selectAll();
        setBackground(Colors.SELECTED);
        mappingsViewer.selected(this);
    }

    void setBackground(final Color color) {
        mappingSourcePane.setBackground(color);
        mapsToPane.setBackground(color);
        mappingTargetPane.setBackground(color);
    }
}
