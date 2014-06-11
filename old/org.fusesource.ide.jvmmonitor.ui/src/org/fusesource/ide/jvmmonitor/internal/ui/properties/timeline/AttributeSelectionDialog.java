/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.internal.ui.IHelpContextIds;


/**
 * The attribute selection dialog.
 */
public class AttributeSelectionDialog extends Dialog {

    /** The selected attributes. */
    private ArrayList<MBeanAttribute> selectedAttributes;

    /** The viewer. */
    TreeViewer viewer;

    /** The JVM. */
    private IActiveJvm jvm;

    /** The color selector. */
    private ColorSelector colorSelector;

    /**
     * The constructor.
     * 
     * @param shell
     *            The parent shell
     * @param jvm
     *            The JVM
     */
    protected AttributeSelectionDialog(Shell shell, IActiveJvm jvm) {
        super(shell);
        this.jvm = jvm;
        selectedAttributes = new ArrayList<MBeanAttribute>();
    }

    /*
     * @see Dialog#create()
     */
    @Override
    public void create() {
        super.create();
        getShell().setText(Messages.attributeSelectionDialogTitle);
        getShell().setSize(500, 600);
    }

    /*
     * @see Window#configureShell(Shell)
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        PlatformUI.getWorkbench().getHelpSystem()
                .setHelp(newShell, IHelpContextIds.ATTRIBUTE_SELECTION_DIALOG);
    }

    /*
     * @see Dialog#createDialogArea(Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 10;
        layout.marginHeight = 10;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.attributesToAddOnChartLabel);

        createMBeanViewer(composite);

        applyDialogFont(composite);

        return composite;
    }

    /*
     * @see Dialog#isResizable()
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /*
     * @see Dialog#okPressed()
     */
    @Override
    protected void okPressed() {
        ISelection selection = viewer.getSelection();
        if (!(selection instanceof IStructuredSelection)) {
            return;
        }

        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        for (Object item : structuredSelection.toArray()) {
            if (!(item instanceof AttributeNode)) {
                continue;
            }
            AttributeNode node = (AttributeNode) item;

            MBeanAttribute mBeanAttribute = new MBeanAttribute(
                    node.getObjectName(), node.getQualifiedName(),
                    node.getRgb());
            selectedAttributes.add(mBeanAttribute);
        }
        super.okPressed();
    }

    /**
     * Gets the attributes.
     * 
     * @return The attributes
     */
    public List<MBeanAttribute> getSelectedAttributes() {
        return selectedAttributes;
    }

    /**
     * Creates the MBean viewer.
     * 
     * @param parent
     *            The parent composite
     */
    private void createMBeanViewer(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        viewer = new MyFilteredTree(composite).getViewer();
        AttributeContentProvider mBeanContentProvider = new AttributeContentProvider();
        viewer.setContentProvider(mBeanContentProvider);
        viewer.setLabelProvider(new MyDecoratingStyledCellLabelProvider());
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                viewerSlectionChanged((IStructuredSelection) event
                        .getSelection());
            }
        });
        viewer.setInput(jvm);
        mBeanContentProvider.refresh(jvm);
        viewer.refresh();

        viewer.expandToLevel(2);

        new Label(composite, SWT.NONE).setText(Messages.colorLabel);
        colorSelector = createColorSelector(composite);
    }

    /**
     * Creates the color selector.
     * 
     * @param parent
     *            The parent composite
     * @return The color selector
     */
    private ColorSelector createColorSelector(Composite parent) {
        final ColorSelector selector = new ColorSelector(parent);
        selector.addListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                IStructuredSelection selection = (IStructuredSelection) viewer
                        .getSelection();
                AttributeNode attribute = (AttributeNode) selection
                        .getFirstElement();
                if (attribute != null) {
                    attribute.setRgb(selector.getColorValue());
                }
            }
        });
        selector.setEnabled(false);
        return selector;
    }

    /**
     * Notifies that the selection is changed on attributes viewer.
     * 
     * @param selection
     *            The selection
     */
    void viewerSlectionChanged(IStructuredSelection selection) {
        Object element = selection.getFirstElement();

        if (element instanceof AttributeNode) {
            AttributeNode attribute = (AttributeNode) element;
            if (attribute.isValidLeaf()) {
                colorSelector.setEnabled(true);
                colorSelector.setColorValue(attribute.getRgb());
                return;
            }
        }

        colorSelector.setEnabled(false);
    }

    /**
     * The filtered tree.
     */
    public static class MyFilteredTree extends FilteredTree {

        /**
         * The constructor.
         * 
         * @param parent
         *            The parent composite
         */
        protected MyFilteredTree(Composite parent) {
            super(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER,
                    new PatternFilter(), true);
            setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        }

        /*
         * @see FilteredTree#createControl(Composite, int)
         */
        @Override
        protected void createControl(Composite composite, int treeStyle) {
            super.createControl(composite, treeStyle);

            // adjust the indentation of filter composite
            GridData data = (GridData) filterComposite.getLayoutData();
            data.horizontalIndent = 2;
            data.verticalIndent = 2;
            filterComposite.setLayoutData(data);
        }
    }

    /**
     * The decorating styled cell label provider. To support filtering,
     * <tt>ILabelProvider</tt> has to be implemented.
     */
    private static class MyDecoratingStyledCellLabelProvider extends
            DecoratingStyledCellLabelProvider implements ILabelProvider {

        /**
         * The constructor.
         */
        public MyDecoratingStyledCellLabelProvider() {
            super(new AttributeLabelProvider(), PlatformUI.getWorkbench()
                    .getDecoratorManager().getLabelDecorator(), null);
        }

        /*
         * @see ILabelProvider#getText(Object)
         */
        @Override
        public String getText(Object element) {
            return getStyledStringProvider().getStyledText(element).toString();
        }
    }
}
