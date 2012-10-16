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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanGroup;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanGroup.AxisUnit;
import org.fusesource.ide.jvmmonitor.internal.ui.IHelpContextIds;


/**
 * The configure chart dialog.
 */
public class ConfigureChartDialog extends Dialog {

    /** The chart title. */
    Text chartTitleText;

    /** The axis unit combo. */
    private Combo axisUnitCombo;

    /** The attribute viewer. */
    TreeViewer attributesViewer;

    /** The color selector. */
    private ColorSelector colorSelector;

    /** The chart title. */
    private String chartTitle;

    /** The original chart title. */
    private final String originalChartTitle;

    /** The Y axis unit. */
    private AxisUnit unit;

    /** The attributes. */
    private List<MBeanAttribute> attributes;

    /** The removed attributes. */
    private List<MBeanAttribute> removedAttributes;

    /** The JVM. */
    private IActiveJvm jvm;

    /** The remove button. */
    private Button removeButton;

    /** The state indicating if having Add/Remove buttons. */
    private boolean hasAddRemoveButtons;

    /** The error image. */
    private Image errorImage;

    /** The error image label. */
    private Label errorImageLabel;

    /** The error message label. */
    private Label errorMessageLabel;

    /**
     * The constructor.
     * 
     * @param shell
     *            The parent shell
     * @param chartTitle
     *            The chart title
     * @param unit
     *            The Y axis unit
     * @param attributes
     *            The attributes
     * @param jvm
     *            The JVM
     * @param hasAddRemoveButtons
     *            The state indicating if having Add/Remove buttons
     */
    protected ConfigureChartDialog(Shell shell, String chartTitle,
            AxisUnit unit, List<MBeanAttribute> attributes, IActiveJvm jvm,
            boolean hasAddRemoveButtons) {
        super(shell);
        this.chartTitle = chartTitle;
        this.originalChartTitle = chartTitle;
        this.unit = unit;
        this.attributes = attributes;
        this.jvm = jvm;
        this.hasAddRemoveButtons = hasAddRemoveButtons;
        removedAttributes = new ArrayList<MBeanAttribute>();
    }

    /*
     * @see Dialog#create()
     */
    @Override
    public void create() {
        super.create();
        getShell().setText(Messages.configureChartDialogTitle);
        setOkButtonVisible(!chartTitleText.getText().isEmpty());
    }

    /*
     * @see Window#configureShell(Shell)
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        PlatformUI.getWorkbench().getHelpSystem()
                .setHelp(newShell, IHelpContextIds.CONFIGURE_CHART_DIALOG);
    }

    /*
     * @see Dialog#createDialogArea(Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 5;
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        createChartConfigControls(container);
        createSeriesConfigControls(container);
        createErrorMessageControls(container);
        applyDialogFont(container);

        return container;
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
        chartTitle = chartTitleText.getText();
        AxisUnit[] values = AxisUnit.values();

        int selectionIndex = axisUnitCombo.getSelectionIndex();
        if (selectionIndex < values.length) {
            unit = values[selectionIndex];
        }

        super.okPressed();
    }

    /*
     * @see Dialog#close()
     */
    @Override
    public boolean close() {
        if (errorImage != null) {
            errorImage.dispose();
        }
        return super.close();
    }

    /**
     * Gets the entered chart title.
     * 
     * @return The chart title
     */
    public String getChartTitle() {
        return chartTitle;
    }

    /**
     * Gets the selected Y axis unit.
     * 
     * @return The Y axis unit
     */
    public AxisUnit getAxisUnit() {
        return unit;
    }

    /**
     * Gets the attributes.
     * 
     * @return The attributes
     */
    public List<MBeanAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Gets the removed attributes.
     * 
     * @return The removed attributes
     */
    protected List<MBeanAttribute> getRemovedAttributes() {
        return removedAttributes;
    }

    /**
     * Creates the controls for chart configuration.
     * 
     * @param parent
     *            The parent composite
     */
    private void createChartConfigControls(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(composite, SWT.NONE).setText(Messages.chartTitleLabel);
        chartTitleText = new Text(composite, SWT.BORDER);
        chartTitleText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        chartTitleText.setText(chartTitle);
        chartTitleText.setSelection(0, chartTitle.length());
        chartTitleText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                validate(chartTitleText.getText());
            }
        });

        new Label(composite, SWT.NONE).setText(Messages.yAxisUnitLabel);
        axisUnitCombo = new Combo(composite, SWT.READ_ONLY);
        List<String> items = new ArrayList<String>();
        int initialSelection = -1;
        AxisUnit[] values = AxisUnit.values();
        for (int i = 0; i < values.length; i++) {
            items.add(values[i].name());
            if (values[i] == unit) {
                initialSelection = i;
            }
        }
        axisUnitCombo.setItems(items.toArray(new String[items.size()]));
        if (initialSelection == -1) {
            initialSelection = items.size() - 1;
        }
        axisUnitCombo.select(initialSelection);
        axisUnitCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * Creates the controls for series configuration.
     * 
     * @param parent
     *            The parent composite
     */
    private void createSeriesConfigControls(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.monitoredAttributesLabel);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        label.setLayoutData(gridData);

        attributesViewer = createAttributesViewer(composite);
        createButtons(composite);

        if (attributes.size() > 0) {
            attributesViewer.setSelection(new StructuredSelection(attributes
                    .get(0)));
        }
    }

    /**
     * Creates the controls to show error message.
     * 
     * @param parent
     *            The parent composite
     */
    private void createErrorMessageControls(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        errorImageLabel = new Label(composite, SWT.NONE);
        errorImageLabel.setImage(getErrorImage());
        errorImageLabel.setVisible(false);

        errorMessageLabel = new Label(composite, SWT.READ_ONLY | SWT.WRAP);
        errorMessageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * Creates the attributes viewer.
     * 
     * @param parent
     *            The parent composite
     * @return The attribute viewer
     */
    private TreeViewer createAttributesViewer(Composite parent) {
        TreeViewer viewer = new TreeViewer(parent, SWT.BORDER
                | SWT.FULL_SELECTION);
        viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        viewer.setContentProvider(new MBeanAttributeContentProvider());
        viewer.setLabelProvider(new MBeanAttributeLabelProvider(viewer));
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                viewerSlectionChanged((IStructuredSelection) event
                        .getSelection());
            }

        });
        configureTree(viewer.getTree());
        viewer.setInput(attributes.toArray(new MBeanAttribute[attributes.size()]));
        return viewer;

    }

    /**
     * Configure the table.
     * 
     * @param tree
     *            The tree
     */
    private void configureTree(Tree tree) {
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);

        for (MBeanAttributeColumn column : MBeanAttributeColumn.values()) {
            TreeColumn treeColumn = new TreeColumn(tree, SWT.NONE);
            treeColumn.setText(column.label);
            treeColumn.setWidth(column.defalutWidth);
            treeColumn.setAlignment(column.alignment);
            treeColumn.setToolTipText(column.toolTip);
        }
    }

    /**
     * Creates the buttons.
     * 
     * @param parent
     *            The parent composite
     */
    private void createButtons(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_VERTICAL));

        if (hasAddRemoveButtons) {
            createAddButton(composite);
            createRemoveButton(composite);
        }

        new Label(composite, SWT.NONE).setText(Messages.colorLabel);
        colorSelector = createColorSelector(composite);
    }

    /**
     * Creates the add button.
     * 
     * @param parent
     *            The parent composite
     */
    private void createAddButton(Composite parent) {
        Button addButton = new Button(parent, SWT.PUSH);
        addButton.setText(Messages.addButtonLabel);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        addButton.setLayoutData(gridData);
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addAttributes();
            }
        });
    }

    /**
     * Adds the attributes.
     */
    void addAttributes() {
        AttributeSelectionDialog dialog = new AttributeSelectionDialog(
                getShell(), jvm);
        if (dialog.open() == OK) {
            List<MBeanAttribute> selectedAttributes = dialog
                    .getSelectedAttributes();
            if (selectedAttributes.size() == 0) {
                return;
            }

            attributes.addAll(selectedAttributes);
            attributesViewer.setInput(attributes
                    .toArray(new MBeanAttribute[attributes.size()]));
            attributesViewer.setSelection(new StructuredSelection(
                    selectedAttributes.get(0)), true);
        }
    }

    /**
     * Creates the remove button.
     * 
     * @param parent
     *            The parent composite
     */
    private void createRemoveButton(Composite parent) {
        removeButton = new Button(parent, SWT.PUSH);
        removeButton.setText(Messages.removeButtonLabel);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        removeButton.setLayoutData(gridData);
        removeButton.setEnabled(false);
        removeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selectedAttribute = (IStructuredSelection) attributesViewer
                        .getSelection();
                MBeanAttribute attribute = (MBeanAttribute) selectedAttribute
                        .getFirstElement();
                removeAttribute(attribute);
            }
        });
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
                IStructuredSelection selection = (IStructuredSelection) attributesViewer
                        .getSelection();
                MBeanAttribute attribute = (MBeanAttribute) selection
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
        if (selection.isEmpty()) {
            if (removeButton != null) {
                removeButton.setEnabled(false);
            }
            colorSelector.setEnabled(false);
            return;
        }

        MBeanAttribute attribute = (MBeanAttribute) selection.getFirstElement();
        if (removeButton != null) {
            removeButton.setEnabled(true);
        }
        colorSelector.setEnabled(true);
        colorSelector.setColorValue(attribute.getRgb());
    }

    /**
     * Removes the given attribute.
     * 
     * @param attribute
     *            The monitored attribute
     */
    void removeAttribute(MBeanAttribute attribute) {
        attributes.remove(attribute);
        attributesViewer.setInput(attributes
                .toArray(new MBeanAttribute[attributes.size()]));
        removedAttributes.add(attribute);
        if (attributes.size() > 0) {
            attributesViewer.setSelection(new StructuredSelection(attributes
                    .get(0)));
        }
    }

    /**
     * Validates the entered title name.
     * 
     * @param text
     *            The entered text
     */
    void validate(String text) {
        String errorMessage = ""; //$NON-NLS-1$
        for (IMonitoredMXBeanGroup group : jvm.getMBeanServer()
                .getMonitoredAttributeGroups()) {
            if (group.getName().equals(text)
                    && !originalChartTitle.equals(text)) {
                errorMessage = Messages.chartTitleDuplicatedMsg;
                break;
            }
        }

        if (text.isEmpty()) {
            errorMessage = Messages.chartTitleEmptyMsg;
        }

        errorImageLabel.setVisible(!errorMessage.isEmpty());
        errorMessageLabel.setText(errorMessage);
        setOkButtonVisible(errorMessage.isEmpty());
    }

    /**
     * Sets the OK button visible.
     * 
     * @param visible
     *            <tt>true</tt> to make OK button visible
     */
    private void setOkButtonVisible(boolean visible) {
        Control button = getButton(IDialogConstants.OK_ID);
        if (button != null) {
            button.setEnabled(visible);
        }
    }

    /**
     * Gets the error image.
     * 
     * @return The error image
     */
    private Image getErrorImage() {
        if (errorImage == null || errorImage.isDisposed()) {
            errorImage = PlatformUI.getWorkbench().getSharedImages()
                    .getImageDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK)
                    .createImage();
        }
        return errorImage;
    }
}
