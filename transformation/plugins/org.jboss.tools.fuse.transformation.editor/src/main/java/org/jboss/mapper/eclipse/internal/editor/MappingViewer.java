package org.jboss.mapper.eclipse.internal.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jboss.mapper.CustomMapping;
import org.jboss.mapper.FieldMapping;
import org.jboss.mapper.Literal;
import org.jboss.mapper.LiteralMapping;
import org.jboss.mapper.MappingOperation;
import org.jboss.mapper.MappingType;
import org.jboss.mapper.eclipse.Activator;
import org.jboss.mapper.eclipse.TransformationEditor;
import org.jboss.mapper.eclipse.internal.util.Util;
import org.jboss.mapper.model.Model;

/**
 *
 */
public final class MappingViewer {

    private static final String ADD_CUSTOM_OPERATION_TOOL_TIP =
            "Add a custom operation to this field";
    private static final String DELETE_CUSTOM_OPERATION_TOOL_TIP = "Delete this custom operation";

    final TransformationEditor editor;

    final ScrolledComposite scroller;
    Composite contentPane;
    MappingOperation<?, ?> mapping;

    private final Point imageButtonLabelSize;

    /**
     * @param editor
     * @param parent
     * @param background
     */
    public MappingViewer(final TransformationEditor editor,
            final Composite parent,
            final Color background) {
        this.editor = editor;

        scroller = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);
        scroller.setBackground(background);

        final Label label = new Label(parent.getShell(), SWT.NONE);
        label.setImage(Util.Images.ADD_FUNCTION);
        imageButtonLabelSize = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        label.dispose();
    }

    void addCustomOperation(final FieldMapping mapping,
            final Shell shell) throws Exception {
        final AddCustomFunctionDialog dlg =
                new AddCustomFunctionDialog(shell,
                        editor.project(),
                        mapping.getSource().getType());
        if (dlg.open() == Window.OK) {
            final CustomMapping customMapping = editor.map(mapping,
                    dlg.type.getFullyQualifiedName(),
                    dlg.method.getElementName());
            update(Util.root(customMapping.getSource()),
                    Util.root(customMapping.getTarget()),
                    customMapping);
            editor.updateMapping(customMapping);
        }
    }

    private Text createCustomMappingPane(final Composite parent,
            final CustomMapping customMapping) {
        final Composite opPane = new Composite(parent, SWT.NONE);
        opPane.setLayoutData(GridDataFactory.fillDefaults().create());
        opPane.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());
        opPane.setBackground(opPane.getDisplay().getSystemColor(SWT.COLOR_GRAY));
        opPane.addPaintListener(Util.roundedRectanglePainter(10,
                opPane.getDisplay()
                        .getSystemColor(SWT.COLOR_WHITE)));
        final Label deleteOpLabel = new Label(opPane, SWT.NONE);
        deleteOpLabel.setLayoutData(GridDataFactory.swtDefaults()
                .hint(imageButtonLabelSize)
                .create());
        deleteOpLabel.setToolTipText(DELETE_CUSTOM_OPERATION_TOOL_TIP);
        final Label opLabel = new Label(opPane, SWT.CENTER);
        opLabel.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        opLabel.setText(customMapping.getMappingOperation() + "()");
        opLabel.setToolTipText(customMapping.getMappingClass()
                + '.'
                + customMapping.getMappingOperation());
        final Label spacer = new Label(opPane, SWT.NONE);
        spacer.setLayoutData(GridDataFactory.swtDefaults()
                .hint(imageButtonLabelSize)
                .create());
        final Text sourceText = createText(opPane);
        sourceText.setLayoutData(GridDataFactory.fillDefaults()
                .span(3, 1)
                .grab(true, false)
                .create());
        sourceText.setText(customMapping.getSource().getName());
        sourceText.setToolTipText(Util.fullyQualifiedName(customMapping.getSource()));
        final MouseTrackListener mouseOverListener = new MouseTrackAdapter() {

            @Override
            public void mouseEnter(final MouseEvent event) {
                deleteOpLabel.setImage(Util.Images.DELETE);
            }

            @Override
            public void mouseExit(final MouseEvent event) {
                deleteOpLabel.setImage(null);
            }
        };
        opPane.addMouseTrackListener(mouseOverListener);
        deleteOpLabel.addMouseTrackListener(mouseOverListener);
        opLabel.addMouseTrackListener(mouseOverListener);
        deleteOpLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(final MouseEvent event) {
                try {
                    removeCustomOperation(customMapping);
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });
        return sourceText;
    }

    private Composite createDetailPane(final Composite parent,
            final Model model) {
        final Composite pane = new Composite(parent, SWT.NONE);
        pane.setLayout(GridLayoutFactory.swtDefaults().create());
        pane.setBackground(parent.getBackground());
        pane.addPaintListener(Util.roundedRectanglePainter(10,
                pane.getDisplay()
                        .getSystemColor(SWT.COLOR_GRAY)));
        final Label label = new Label(pane, SWT.NONE);
        label.setLayoutData(GridDataFactory.fillDefaults()
                .align(SWT.CENTER, SWT.CENTER)
                .create());
        label.setText(model.getName());
        return pane;
    }

    private Text createMappingPane(final Composite parent,
            final MappingOperation<?, ?> mapping) {
        final Composite pane = new Composite(parent, SWT.NONE);
        pane.setLayoutData(GridDataFactory.fillDefaults().create());
        pane.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
        pane.setBackground(pane.getDisplay().getSystemColor(SWT.COLOR_GRAY));
        final Label addOpLabel = new Label(pane, SWT.NONE);
        addOpLabel.setLayoutData(GridDataFactory.swtDefaults()
                .hint(imageButtonLabelSize)
                .create());
        final boolean fieldMapping = mapping instanceof FieldMapping;
        addOpLabel.setToolTipText(fieldMapping ? ADD_CUSTOM_OPERATION_TOOL_TIP : null);
        final Text sourceText = createText(pane);
        if (mapping != null) {
            if (MappingType.LITERAL == mapping.getType()) {
                sourceText.setText("\""
                        + ((LiteralMapping) mapping).getSource().getValue()
                        + "\"");
            } else {
                final Model model = ((FieldMapping) mapping).getSource();
                sourceText.setText(model.getName());
                sourceText.setToolTipText(Util.fullyQualifiedName(model));
            }
        }
        final MouseTrackListener mouseOverListener = new MouseTrackAdapter() {

            @Override
            public void mouseEnter(final MouseEvent event) {
                addOpLabel.setImage(fieldMapping ? Util.Images.ADD_FUNCTION : null);
            }

            @Override
            public void mouseExit(final MouseEvent event) {
                addOpLabel.setImage(null);
            }
        };
        pane.addMouseTrackListener(mouseOverListener);
        addOpLabel.addMouseTrackListener(mouseOverListener);
        sourceText.addMouseTrackListener(mouseOverListener);
        addOpLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(final MouseEvent event) {
                if (mapping instanceof Literal)
                    return;
                try {
                    addCustomOperation((FieldMapping) mapping, addOpLabel.getShell());
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        });
        return sourceText;
    }

    private Text createText(final Composite parent) {
        final Text text = new Text(parent, SWT.BORDER);
        text.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        text.setEditable(false);
        text.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(final FocusEvent event) {
                text.selectAll();
            }

        });
        return text;
    }

    MappingOperation<?, ?> dropOnSource(final Object dragSource,
            final Text sourceText) throws Exception {
        MappingOperation<?, ?> newMapping;
        if (dragSource instanceof Literal) {
            final Literal literal = (Literal) dragSource;
            newMapping = editor.map(literal, (Model) mapping.getTarget());
            sourceText.setText("\"" + literal.getValue() + "\"");
            sourceText.setToolTipText(null);
        } else {
            final Model model = (Model) dragSource;
            newMapping = editor.map(model, (Model) mapping.getTarget());
            if (mapping instanceof CustomMapping) {
                final CustomMapping customMapping = (CustomMapping) mapping;
                newMapping = editor.map((FieldMapping) newMapping,
                        customMapping.getMappingClass(),
                        customMapping.getMappingOperation());
            }
            sourceText.setText(model.getName());
            sourceText.setToolTipText(Util.fullyQualifiedName(model));
            editor.refreshSourceModelViewer();
        }
        return newMapping;
    }

    MappingOperation<?, ?> dropOnTarget(final Model dragModel,
            final Text targetText) throws Exception {
        final MappingOperation<?, ?> newMapping =
                editor.map(mapping.getSource(), dragModel);
        targetText.setText(dragModel.getName());
        targetText.setToolTipText(Util.fullyQualifiedName(dragModel));
        editor.refreshTargetModelViewer();
        return newMapping;
    }

    void removeCustomOperation(final CustomMapping customMapping) throws Exception {
        editor.unmap(customMapping);
        final FieldMapping mapping =
                (FieldMapping) editor.map(customMapping.getSource(), customMapping.getTarget());
        update(Util.root(mapping.getSource()), Util.root(mapping.getTarget()), mapping);
        editor.updateMapping(mapping);
    }

    /**
     * @param sourceModel
     * @param targetModel
     * @param mapping
     */
    public void update(final Model sourceModel,
            final Model targetModel,
            final MappingOperation<?, ?> mapping) {
        // Create mapping detail pane
        if (contentPane != null)
            contentPane.dispose();
        this.mapping = mapping;
        contentPane = new Composite(scroller, SWT.NONE);
        scroller.setContent(contentPane);
        contentPane.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
        contentPane.setBackground(scroller.getBackground());
        final Composite sourcePane = createDetailPane(contentPane, sourceModel);
        sourcePane.setLayoutData(GridDataFactory.swtDefaults()
                .align(SWT.RIGHT, SWT.CENTER)
                .grab(true, true)
                .create());
        final Text sourceText;
        if (mapping instanceof CustomMapping)
            sourceText = createCustomMappingPane(sourcePane, (CustomMapping) mapping);
        else {
            sourceText = createMappingPane(sourcePane, mapping);
        }
        final DropTarget sourceDropTarget = new DropTarget(sourceText, DND.DROP_MOVE);
        sourceDropTarget.setTransfer(new Transfer[] {LocalSelectionTransfer.getTransfer()});
        sourceDropTarget.addDropListener(new DropListener(sourceText, editor.sourceModel()) {

            @Override
            MappingOperation<?, ?> drop(final Object dragSource) throws Exception {
                return dropOnSource(dragSource, sourceText);
            }

            @Override
            boolean valid(final Object dragSource) {
                return super.valid(dragSource) || validSourceDropTarget(dragSource);
            }
        });
        final Label mapsToLabel = new Label(contentPane, SWT.NONE);
        mapsToLabel.setImage(Util.Images.MAPPED);
        final Composite targetPane = createDetailPane(contentPane, targetModel);
        targetPane.setLayoutData(GridDataFactory.swtDefaults()
                .align(SWT.LEFT, SWT.CENTER)
                .grab(true, true)
                .create());
        final Text targetText = createText(targetPane);
        if (mapping != null) {
            final Model model = (Model) mapping.getTarget();
            targetText.setText(model.getName());
            targetText.setToolTipText(Util.fullyQualifiedName(model));
        }
        final DropTarget targetDropTarget = new DropTarget(targetText, DND.DROP_MOVE);
        targetDropTarget.setTransfer(new Transfer[] {LocalSelectionTransfer.getTransfer()});
        targetDropTarget.addDropListener(new DropListener(targetText, editor.targetModel()) {

            @Override
            MappingOperation<?, ?> drop(final Object dragSource) throws Exception {
                return dropOnTarget((Model) dragSource, targetText);
            }
        });
        scroller.setMinSize(contentPane.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        contentPane.layout();
    }

    boolean validSourceDropTarget(final Object dragSource) {
        return dragSource instanceof Literal && !(mapping instanceof CustomMapping);
    }

    private abstract class DropListener extends DropTargetAdapter {

        private final Text dropText;
        private final Model dragRootModel;

        DropListener(final Text dropText,
                final Model dragRootModel) {
            this.dropText = dropText;
            this.dragRootModel = dragRootModel;
        }

        @Override
        public final void dragEnter(final DropTargetEvent event) {
            if (valid(dragSource()))
                dropText.setBackground(dropText.getDisplay().getSystemColor(SWT.COLOR_BLUE));
        }

        @Override
        public final void dragLeave(final DropTargetEvent event) {
            dropText.setBackground(dropText.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        }

        private Object dragSource() {
            return ((IStructuredSelection) LocalSelectionTransfer.getTransfer()
                    .getSelection())
                    .getFirstElement();
        }

        @Override
        public final void drop(final DropTargetEvent event) {
            editor.unmap(mapping);
            try {
                mapping = drop(dragSource());
                editor.updateMapping(mapping);
            } catch (final Exception e) {
                Activator.error(e);
            }
            contentPane.layout();
        }

        abstract MappingOperation<?, ?> drop(final Object dragSource) throws Exception;

        private Model root(final Model model) {
            return model.getParent() == null ? model : root(model.getParent());
        }

        boolean valid(final Object dragSource) {
            return dragSource instanceof Model
                    && root((Model) dragSource).equals(dragRootModel);
        }
    }
}
