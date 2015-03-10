package org.jboss.mapper.eclipse.internal.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.mapper.CustomMapping;
import org.jboss.mapper.FieldMapping;
import org.jboss.mapper.Literal;
import org.jboss.mapper.LiteralMapping;
import org.jboss.mapper.MappingOperation;
import org.jboss.mapper.MappingType;
import org.jboss.mapper.eclipse.Activator;
import org.jboss.mapper.eclipse.TransformationEditor;
import org.jboss.mapper.eclipse.internal.editor.MappingsViewer.TraversalListener;
import org.jboss.mapper.eclipse.internal.util.Util;
import org.jboss.mapper.model.Model;

final class MappingRow {

    static final String ADD_CUSTOM_OPERATION_TOOL_TIP =
            "Add a custom operation to the source field that follows";
    static final String DELETE_CUSTOM_OPERATION_TOOL_TIP =
            "Delete the custom operation that follows";

    final TransformationEditor editor;
    final MappingsViewer mappingsViewer;
    MappingOperation<?, ?> mapping;
    final Composite mappingSourcePane, mapsToPane, mappingTargetPane;
    final Text sourceText, targetText;
    final TraversalListener sourceTraversalListener, targetTraversalListener;

    // final Label opIconLabel, opStartLabel, opEndLabel;
    // Image opImage;

    MappingRow(final TransformationEditor editor,
            final MappingsViewer mappingsViewer,
            final MappingOperation<?, ?> mapping) {
        this.editor = editor;
        this.mappingsViewer = mappingsViewer;
        this.mapping = mapping;

        mappingSourcePane = createMappingPane(mappingsViewer.sourcePane);

        // final boolean customMapping = mapping instanceof CustomMapping;
        // final boolean fieldMapping = mapping instanceof FieldMapping;

        // Add add/delete op "button" for source component pane
        // opIconLabel = new Label( mappingSourcePane, SWT.NONE );
        // opIconLabel.setLayoutData( GridDataFactory.swtDefaults().hint(
        // Util.IMAGE_BUTTON_LABEL_SIZE )
        // .grab( true, false )
        // .align( SWT.RIGHT, SWT.TOP ).create() );
        // opIconLabel.setBackground( mappingsViewer.getBackground() );
        // opIconLabel.setToolTipText( customMapping
        // ? DELETE_CUSTOM_OPERATION_TOOL_TIP
        // : fieldMapping ? ADD_CUSTOM_OPERATION_TOOL_TIP : null );
        //
        // opStartLabel = new Label( mappingSourcePane, SWT.NONE );
        // opStartLabel.setLayoutData( GridDataFactory.swtDefaults().align(
        // SWT.BEGINNING, SWT.TOP )
        // .exclude( !customMapping ).create() );
        // if ( customMapping ) {
        // final CustomMapping cMapping = ( CustomMapping ) mapping;
        // opStartLabel.setText( cMapping.getMappingOperation() + "(" );
        // opStartLabel.setToolTipText( cMapping.getMappingClass() + '.'
        // + cMapping.getMappingOperation() );
        // }

        // Create focus listener to change highlight color when focus is lost &
        // gained
        final FocusListener focusListener = new FocusListener() {

            @Override
            public void focusGained(final FocusEvent event) {
                selected();
            }

            @Override
            public void focusLost(final FocusEvent event) {
                if (mappingsViewer.selectedRow == MappingRow.this)
                    setBackground(mappingsViewer.getDisplay()
                            .getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
            }
        };

        // Create key listener to make up and down arrow navigate selection up
        // and down
        final KeyListener keyListener = new KeyAdapter() {

            @Override
            public void keyReleased(final KeyEvent event) {
                if (event.keyCode == SWT.ARROW_DOWN)
                    selectNext();
                else if (event.keyCode == SWT.ARROW_UP)
                    selectPrevious();
            }
        };

        sourceText = createText(mappingSourcePane, focusListener, keyListener);
        sourceText.setLayoutData(GridDataFactory.swtDefaults()
                .grab(true, false)
                .align(SWT.RIGHT, SWT.CENTER)
                .create());
        // Save text background to restore after leaving mouse-over during DnD
        if (mappingsViewer.textBackground == null)
            mappingsViewer.textBackground = sourceText.getBackground();

        // opEndLabel = new Label( mappingSourcePane, SWT.NONE );
        // opEndLabel.setLayoutData( GridDataFactory.swtDefaults().align(
        // SWT.BEGINNING, SWT.TOP )
        // .exclude( !customMapping ).create() );
        // opEndLabel.setText( ")" );

        mapsToPane = new Composite(mappingsViewer.mapsToPane, SWT.NONE);
        mapsToPane.setLayoutData(GridDataFactory.swtDefaults().create());
        mapsToPane.setLayout(GridLayoutFactory.swtDefaults().spacing(0, 0).create());
        mapsToPane.setBackground(mappingsViewer.getBackground());
        final Label mapsToLabel = new Label(mapsToPane, SWT.NONE);
        mapsToLabel.setImage(Util.Images.MAPPED);

        mappingTargetPane = createMappingPane(mappingsViewer.targetPane);

        targetText = createText(mappingTargetPane, focusListener, keyListener);

        // Configure buttons to appear on mouse-over
        // opImage =
        // customMapping ? Util.Images.DELETE : fieldMapping ?
        // Util.Images.ADD_FUNCTION : null;
        // final MouseTrackListener mouseOverListener = new MouseTrackAdapter()
        // {
        //
        // @Override
        // public void mouseEnter( final MouseEvent event ) {
        // opIconLabel.setImage( opImage );
        // }
        //
        // @Override
        // public void mouseExit( final MouseEvent event ) {
        // opIconLabel.setImage( null );
        // }
        // };
        // mappingSourcePane.addMouseTrackListener( mouseOverListener );
        // opIconLabel.addMouseTrackListener( mouseOverListener );
        // sourceText.addMouseTrackListener( mouseOverListener );
        // opStartLabel.addMouseTrackListener( mouseOverListener );
        // opEndLabel.addMouseTrackListener( mouseOverListener );
        // mapsToPane.addMouseTrackListener( mouseOverListener );
        // mapsToLabel.addMouseTrackListener( mouseOverListener );
        // mappingTargetPane.addMouseTrackListener( mouseOverListener );
        // targetText.addMouseTrackListener( mouseOverListener );

        if (mapping != null) {
            if (MappingType.LITERAL == mapping.getType())
                sourceText.setText("\""
                        + ((LiteralMapping) mapping).getSource().getValue()
                        + "\"");
            else {
                final Model model = ((FieldMapping) mapping).getSource();
                sourceText.setText(model.getName());
                sourceText.setToolTipText(Util.fullyQualifiedName(model));
            }
            final Model model = (Model) mapping.getTarget();
            targetText.setText(model.getName());
            targetText.setToolTipText(Util.fullyQualifiedName(model));
        }

        // Make mappingSourcePane, mapsToLabel, & mappingTargetPane the same
        // height
        int height = mappingSourcePane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
        height = Math.max(height, mapsToPane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
        height = Math.max(height, mappingTargetPane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
        ((GridData) mappingSourcePane.getLayoutData()).heightHint = height;
        ((GridData) mapsToPane.getLayoutData()).heightHint = height;
        ((GridData) mappingTargetPane.getLayoutData()).heightHint = height;

        // Configure traversal of source and target text to ignore immediate
        // containers
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

        // opIconLabel.addMouseListener( new MouseAdapter() {
        //
        // @Override
        // public void mouseUp( final MouseEvent event ) {
        // try {
        // if ( opImage == Util.Images.ADD_FUNCTION )
        // addCustomOperation( mouseOverListener );
        // else if ( opImage == Util.Images.DELETE )
        // removeCustomOperation();
        // } catch ( final Exception e ) {
        // Activator.error( e );
        // }
        // }
        // } );

        final DropTarget sourceDropTarget = new DropTarget(sourceText, DND.DROP_MOVE);
        sourceDropTarget.setTransfer(new Transfer[] {LocalSelectionTransfer.getTransfer()});
        sourceDropTarget.addDropListener(new DropListener(sourceText, editor.sourceModel(),
                mappingSourcePane) {

            @Override
            MappingOperation<?, ?> drop(final Object dragSource) throws Exception {
                return dropOnSource(dragSource);
            }

            @Override
            boolean valid(final Object dragSource) {
                return super.valid(dragSource) || validSourceDropTarget(dragSource);
            }
        });

        final DropTarget targetDropTarget = new DropTarget(targetText, DND.DROP_MOVE);
        targetDropTarget.setTransfer(new Transfer[] {LocalSelectionTransfer.getTransfer()});
        targetDropTarget.addDropListener(new DropListener(targetText, editor.targetModel(),
                mappingTargetPane) {

            @Override
            MappingOperation<?, ?> drop(final Object dragSource) throws Exception {
                return dropOnTarget((Model) dragSource);
            }
        });

        final MouseListener mouseListener = new MouseAdapter() {

            @Override
            public void mouseUp(final MouseEvent event) {
                sourceText.setFocus();
            }
        };
        mappingSourcePane.addMouseListener(mouseListener);
        // opStartLabel.addMouseListener( mouseListener );
        // opEndLabel.addMouseListener( mouseListener );
        mapsToPane.addMouseListener(mouseListener);
        mapsToLabel.addMouseListener(mouseListener);
        mappingTargetPane.addMouseListener(mouseListener);

        mappingsViewer.prevTargetText = targetText;
        mappingsViewer.prevTraversalListener = targetTraversalListener;

        mappingsViewer.updateLayout();
    }

    private Composite createMappingPane(final Composite parent) {
        final Composite pane = new Composite(parent, SWT.NONE);
        pane.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        pane.setLayout(GridLayoutFactory.swtDefaults().create());
        pane.setBackground(mappingsViewer.getBackground());
        return pane;
    }

    // void addCustomOperation( final MouseTrackListener mouseOverListener )
    // throws Exception {
    // final FieldMapping mapping = ( FieldMapping ) this.mapping;
    // final AddCustomFunctionDialog dlg =
    // new AddCustomFunctionDialog( mappingsViewer.getShell(), editor.project(),
    // mapping
    // .getSource().getType() );
    // if ( dlg.open() != Window.OK )
    // return;
    // this.mapping =
    // editor.map( mapping, dlg.type.getFullyQualifiedName(),
    // dlg.method.getElementName() );
    // opStartLabel.setText( dlg.method.getElementName() + "(" );
    // opStartLabel.setToolTipText( dlg.type.getFullyQualifiedName() + '.'
    // + dlg.method.getElementName() );
    // ( ( GridData ) opStartLabel.getLayoutData() ).exclude = false;
    // ( ( GridData ) opEndLabel.getLayoutData() ).exclude = false;
    // opImage = Util.Images.DELETE;
    // opIconLabel.setToolTipText( DELETE_CUSTOM_OPERATION_TOOL_TIP );
    // mappingSourcePane.layout();
    // }

    private Text createText(final Composite parent,
            final FocusListener focusListener,
            final KeyListener keyListener) {
        final Text text = new Text(parent, SWT.BORDER);
        text.setEditable(false);
        text.addFocusListener(focusListener);
        text.addKeyListener(keyListener);
        return text;
    }

    void deselect() {
        setBackground(mappingsViewer.getBackground());
    }

    MappingOperation<?, ?> dropOnSource(final Object dragSource) throws Exception {
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
                newMapping =
                        editor.map((FieldMapping) newMapping, customMapping.getMappingClass(),
                                customMapping.getMappingOperation());
            }
            sourceText.setText(model.getName());
            sourceText.setToolTipText(Util.fullyQualifiedName(model));
            editor.refreshSourceModelViewer();
        }
        sourceText.setFocus();
        return newMapping;
    }

    MappingOperation<?, ?> dropOnTarget(final Model dragModel) throws Exception {
        final MappingOperation<?, ?> newMapping = editor.map(mapping.getSource(), dragModel);
        targetText.setText(dragModel.getName());
        targetText.setToolTipText(Util.fullyQualifiedName(dragModel));
        editor.refreshTargetModelViewer();
        return newMapping;
    }

    void select() {
        setBackground(mappingsViewer.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
    }

    // void removeCustomOperation() throws Exception {
    // editor.unmap( mapping );
    // mapping = editor.map( ( Model ) mapping.getSource(), ( Model )
    // mapping.getTarget() );
    // ( ( GridData ) opStartLabel.getLayoutData() ).exclude = true;
    // ( ( GridData ) opEndLabel.getLayoutData() ).exclude = true;
    // opImage = Util.Images.ADD_FUNCTION;
    // opIconLabel.setToolTipText( ADD_CUSTOM_OPERATION_TOOL_TIP );
    // opStartLabel.setToolTipText( null );
    // mappingSourcePane.layout();
    // }

    void selected() {
        mappingsViewer.selectMapping(editor, this);
        if (sourceText.isFocusControl())
            sourceText.selectAll();
        else
            targetText.selectAll();
    }

    void selectNext() {
        final int ndx = mappingsViewer.mappingRows.indexOf(this) + 1;
        if (ndx < mappingsViewer.mappingRows.size())
            setFocus(ndx);
    }

    void selectPrevious() {
        final int ndx = mappingsViewer.mappingRows.indexOf(this) - 1;
        if (ndx >= 0)
            setFocus(ndx);
    }

    void setBackground(final Color color) {
        mappingSourcePane.setBackground(color);
        // opIconLabel.setBackground( color );
        mapsToPane.setBackground(color);
        mappingTargetPane.setBackground(color);
    }

    private void setFocus(final int index) {
        final MappingRow mapping = mappingsViewer.mappingRows.get(index);
        if (sourceText.isFocusControl())
            mapping.sourceText.setFocus();
        else
            mapping.targetText.setFocus();
    }

    void unmap() throws Exception {
        editor.unmap(mapping);
        editor.save();
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
        mappingsViewer.mappingRows.remove(this);
        mappingsViewer.updateLayout();
    }

    boolean validSourceDropTarget(final Object dragSource) {
        return dragSource instanceof Literal && !(mapping instanceof CustomMapping);
    }

    private abstract class DropListener extends DropTargetAdapter {

        private final Text dropText;
        private final Model dragRootModel;
        private final Composite mappingPane;

        DropListener(final Text dropText,
                final Model dragRootModel,
                final Composite mappingPane) {
            this.dropText = dropText;
            this.dragRootModel = dragRootModel;
            this.mappingPane = mappingPane;
        }

        @Override
        public final void dragEnter(final DropTargetEvent event) {
            if (valid(dragSource()))
                dropText.setBackground(mappingPane.getDisplay().getSystemColor(SWT.COLOR_BLUE));
        }

        @Override
        public final void dragLeave(final DropTargetEvent event) {
            dropText.setBackground(mappingsViewer.textBackground);
        }

        private Object dragSource() {
            return ((IStructuredSelection) LocalSelectionTransfer.getTransfer().getSelection())
                    .getFirstElement();
        }

        @Override
        public final void drop(final DropTargetEvent event) {
            editor.unmap(mapping);
            try {
                mapping = drop(dragSource());
            } catch (final Exception e) {
                Activator.error(e);
            }
            mappingPane.layout();
        }

        abstract MappingOperation<?, ?> drop(final Object dragSource) throws Exception;

        boolean valid(final Object dragSource) {
            return dragSource instanceof Model
                    && Util.root((Model) dragSource).equals(dragRootModel);
        }
    }
}
