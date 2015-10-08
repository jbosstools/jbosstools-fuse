package org.jboss.tools.fuse.transformation.editor.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jboss.tools.fuse.transformation.MappingOperation;
import org.jboss.tools.fuse.transformation.MappingType;
import org.jboss.tools.fuse.transformation.TransformationMapping;
import org.jboss.tools.fuse.transformation.Variable;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationManager;
import org.jboss.tools.fuse.transformation.editor.internal.util.TransformationManager.Event;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Colors;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util.Images;

final class MappingSummary extends MappingViewer {

    final MappingsViewer mappingsViewer;
    final Composite mappingSourcePane;
    final Label mapsToLabel;
    final Composite mappingTargetPane;
    final PropertyChangeListener managerListener;
    final MouseListener mouseListener = new MouseAdapter() {

        @Override
        public void mouseUp(final MouseEvent event) {
            selected();
        }
    };

    MappingSummary(final TransformationManager manager,
                   final MappingOperation<?, ?> mapping,
                   final MappingsViewer mappingsViewer,
                   final List<PotentialDropTarget> potentialDropTargets) {
        super(manager, potentialDropTargets);
        this.mapping = mapping;
        this.mappingsViewer = mappingsViewer;

        mappingSourcePane = createMappingPane(mappingsViewer.sourcePane);
        createSourcePropertyPane(mappingSourcePane, SWT.NONE);
        sourcePropPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        mapsToLabel = new Label(mappingsViewer.mapsToPane, SWT.NONE);
        mapsToLabel.setLayoutData(GridDataFactory.swtDefaults().create());
        mapsToLabel.setImage(Images.MAPPED);
        final StringBuilder builder = new StringBuilder();
        if (mapping.getType() == MappingType.TRANSFORMATION) {
            builder.append(((TransformationMapping)mapping).getTransformationName());
            builder.append('(');
        }
        builder.append(name(mapping.getSource()));
        if (mapping.getType() == MappingType.TRANSFORMATION) {
            builder.append(')');
        }
        builder.append(" => ");
        builder.append(name(mapping.getTarget()));
        mapsToLabel.setToolTipText(builder.toString());

        mappingTargetPane = createMappingPane(mappingsViewer.targetPane);
        createTargetPropertyPane(mappingTargetPane);
        targetPropPane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        // Make mappingSourcePane, mapsToLabel, & mappingTargetPane the same height
        int height = mappingSourcePane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
        height = Math.max(height, mapsToLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
        height = Math.max(height, mappingTargetPane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
        ((GridData) mappingSourcePane.getLayoutData()).heightHint = height;
        ((GridData) mapsToLabel.getLayoutData()).heightHint = height;
        ((GridData) mappingTargetPane.getLayoutData()).heightHint = height;

        mappingSourcePane.addMouseListener(mouseListener);
        mapsToLabel.addMouseListener(mouseListener);
        mappingTargetPane.addMouseListener(mouseListener);

        managerListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                managerEvent(event.getPropertyName(), event.getOldValue(), event.getNewValue());
            }
        };
        manager.addListener(managerListener);
    }

    private Composite createMappingPane(final Composite parent) {
        final Composite pane = new Composite(parent, SWT.NONE);
        pane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        pane.setLayout(GridLayoutFactory.swtDefaults().spacing(0, 5).create());
        pane.setBackground(mappingsViewer.getBackground());
        return pane;
    }

    @Override
    Composite createPropertyPane(final Composite parent,
                                 final int style) {
        final Composite pane = super.createPropertyPane(parent, style);
        CLabel label = (CLabel)pane.getChildren()[0];
        // Create listener to change summary's highlight color when selected
        label.addMouseListener(mouseListener);
        return pane;
    }

    void deselect() {
        setBackground(mappingsViewer.getBackground());
    }

    void dispose(final MappingOperation<?, ?> mapping) {
        dispose();
        mappingSourcePane.dispose();
        mapsToLabel.dispose();
        mappingTargetPane.dispose();
        manager.removeListener(managerListener);
        mappingsViewer.mappingSummaryDeleted(this);
    }

    private void managerEvent(final String eventType,
                             final Object oldValue,
                             final Object newValue) {
        if (eventType.equals(Event.VARIABLE_VALUE.name())) {
            variableValueUpdated((Variable)newValue);
            return;
        }
        if (!equals(mapping, oldValue)) return;
        if (eventType.equals(Event.MAPPING.name())) {
            dispose((MappingOperation<?, ?>)oldValue);
        } else if (eventType.equals(Event.MAPPING_SOURCE.name())) {
            mapping = (MappingOperation<?, ?>)newValue;
            setSourceText();
            mappingSourcePane.layout();
        } else if (eventType.equals(Event.MAPPING_TARGET.name())) {
            mapping = (MappingOperation<?, ?>)newValue;
            setTargetText();
            mappingTargetPane.layout();
        } else if (eventType.equals(Event.MAPPING_TRANSFORMATION.name())) {
            mapping = (MappingOperation<?, ?>)newValue;
            setSourceText();
            mappingSourcePane.layout();
        }
    }

    void select() {
        setBackground(Colors.SELECTED);
    }

    private void selected() {
        mappingsViewer.selected(this);
    }

    void setBackground(final Color color) {
        mappingSourcePane.setBackground(color);
        mapsToLabel.setBackground(color);
        mappingTargetPane.setBackground(color);
    }

    @Override
    void setSourceText() {
        super.setSourceText();
        if (mapping.getType() == MappingType.TRANSFORMATION) {
            CLabel label = (CLabel)sourcePropPane.getChildren()[0];
            label.setImage(Images.TRANSFORMATION);
        }
    }
}
