/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.jvmmonitor.internal.ui.IHelpContextIds;

/**
 * The dialog to specify the name to save chart set as.
 */
public class SaveChartSetAsDialog extends Dialog {

    /** The chart set viewer. */
    TableViewer chartSetViewer;

    /** The remove button. */
    Button deleteButton;

    /** The chart set text field. */
    Text chartSetText;

    /** The error image. */
    private Image errorImage;

    /** The error image label. */
    private Label errorImageLabel;

    /** The error message label. */
    private Label errorMessageLabel;

    /** The chart sets. */
    List<String> chartSets;

    /** The specified chart set. */
    String chartSet;

    /** The predefined chart set. */
    private List<String> predefinedChartSets;

    /** The state indicating if text field has to ignore the selection on tree. */
    protected boolean ignoreSelection;

    /**
     * The constructor.
     * 
     * @param shell
     *            The parent shell
     * @param chartSets
     *            The chart sets
     * @param predefinedChartSets
     *            The predefined chart sets
     */
    protected SaveChartSetAsDialog(Shell shell, List<String> chartSets,
            List<String> predefinedChartSets) {
        super(shell);
        this.chartSets = chartSets;
        this.chartSet = ""; //$NON-NLS-1$
        this.predefinedChartSets = predefinedChartSets;
        ignoreSelection = false;
    }

    /*
     * @see Dialog#create()
     */
    @Override
    public void create() {
        super.create();
        getShell().setText(Messages.saveChartSetAsDialogTitle);
        validate();
    }

    /*
     * @see Window#configureShell(Shell)
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        PlatformUI.getWorkbench().getHelpSystem()
                .setHelp(newShell, IHelpContextIds.SAVE_CHART_SET_AS_DIALOG);
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

        Label label = new Label(container, SWT.NONE);
        label.setText(Messages.enterOrSelectChartSetLabel);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalIndent = 5;
        gridData.verticalIndent = 5;
        label.setLayoutData(gridData);

        createChartSetText(container);
        createChartSetList(container);
        createErrorMessageLabel(container);

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
     * Gets the chart sets.
     * 
     * @return The chart sets
     */
    protected List<String> getChartSets() {
        return chartSets;
    }

    /**
     * Gets the specified chart set.
     * 
     * @return The specified chart set.
     */
    protected String getChartSet() {
        return chartSet;
    }

    /**
     * Creates the chart set text field.
     * 
     * @param parent
     *            The parent composite
     */
    private void createChartSetText(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(composite, SWT.NONE).setText(Messages.chartSetLabel);
        chartSetText = new Text(composite, SWT.BORDER);
        chartSetText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        chartSetText.setText(chartSet);
        chartSetText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                try {
                    ignoreSelection = true;
                    chartSet = chartSetText.getText();
                    if (chartSets.contains(chartSet)) {
                        chartSetViewer.setSelection(new StructuredSelection(
                                chartSet));
                    } else {
                        deleteButton.setEnabled(false);
                        chartSetViewer.setSelection(new StructuredSelection());
                    }
                } finally {
                    ignoreSelection = false;
                }
                validate();
            }
        });
    }

    /**
     * Creates the chart set list.
     * 
     * @param parent
     *            The parent composite
     */
    private void createChartSetList(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.existingChartSetsLabel);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        label.setLayoutData(gridData);

        chartSetViewer = createChartSetViewer(composite);
        createDeleteButton(composite);
    }

    /**
     * Creates the delete button.
     * 
     * @param parent
     *            The parent composite
     */
    private void createDeleteButton(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_VERTICAL));

        deleteButton = new Button(composite, SWT.PUSH);
        deleteButton.setText(Messages.deleteButtonLabel);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        deleteButton.setLayoutData(gridData);
        deleteButton.setEnabled(false);
        deleteButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = (IStructuredSelection) chartSetViewer
                        .getSelection();
                String element = (String) selection.getFirstElement();
                chartSets.remove(element);
                chartSetViewer.setInput(chartSets.toArray(new String[chartSets
                        .size()]));
            }
        });
    }

    /**
     * Creates the chart set viewer.
     * 
     * @param parent
     *            The parent composite
     * @return The chart set viewer
     */
    private TableViewer createChartSetViewer(Composite parent) {
        TableViewer viewer = new TableViewer(parent, SWT.BORDER
                | SWT.FULL_SELECTION);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.heightHint = 300;
        viewer.getTable().setLayoutData(gridData);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new LabelProvider());
        viewer.setInput(chartSets.toArray(new String[chartSets.size()]));
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (ignoreSelection) {
                    return;
                }
                ISelection selection = event.getSelection();
                if (selection instanceof StructuredSelection) {
                    Object element = ((StructuredSelection) selection)
                            .getFirstElement();
                    if (element != null) {
                        chartSetText.setText(element.toString());
                        deleteButton.setEnabled(true);
                    }
                }
            }
        });
        return viewer;
    }

    /**
     * Creates the error message label.
     * 
     * @param parent
     *            The parent composite
     */
    private void createErrorMessageLabel(Composite parent) {
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
     * Validates the specified chart set.
     */
    void validate() {
        String errorMessage = ""; //$NON-NLS-1$
        for (String predefinedChartSet : predefinedChartSets) {
            if (predefinedChartSet.equals(chartSetText.getText())) {
                errorMessage = Messages.illiegalChartSetMsg;
                break;
            }
        }
        errorImageLabel.setVisible(!errorMessage.isEmpty());
        errorMessageLabel.setText(errorMessage);

        Control button = getButton(IDialogConstants.OK_ID);
        if (button != null) {
            button.setEnabled(!chartSetText.getText().isEmpty());
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