/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.jvmmonitor.internal.ui.IHelpContextIds;

/**
 * The dialog to select the chart set to load.
 */
public class LoadChartSetDialog extends Dialog {

    /** The chart set viewer. */
    TableViewer chartSetViewer;

    /** The remove button. */
    Button deleteButton;

    /** The make default button. */
    Button makeDefaultButton;

    /** The chart sets. */
    List<String> chartSets;

    /** The selected chart set. */
    String chartSet;

    /** The default chart set. */
    String defaultChartSet;

    /** The predefined chart set. */
    List<String> predefinedChartSets;

    /**
     * The constructor.
     * 
     * @param shell
     *            The parent shell
     * @param chartSets
     *            The chart sets
     * @param defaultChartSet
     *            The default chart set
     * @param predefinedChartSets
     *            The predefined chart set
     */
    protected LoadChartSetDialog(Shell shell, List<String> chartSets,
            String defaultChartSet, List<String> predefinedChartSets) {
        super(shell);
        this.chartSets = chartSets;
        this.chartSet = defaultChartSet;
        this.defaultChartSet = defaultChartSet;
        this.predefinedChartSets = predefinedChartSets;
    }

    /*
     * @see Dialog#create()
     */
    @Override
    public void create() {
        super.create();
        getShell().setText(Messages.loadChartSetDialogTitle);
    }

    /*
     * @see Window#configureShell(Shell)
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        PlatformUI.getWorkbench().getHelpSystem()
                .setHelp(newShell, IHelpContextIds.LOAD_CHART_SET_DIALOG);
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
        label.setText(Messages.selectChartSetLabel);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalIndent = 5;
        gridData.verticalIndent = 5;
        label.setLayoutData(gridData);

        createChartSetList(container);

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

    /**
     * Gets the chart sets.
     * 
     * @return The chart sets
     */
    protected List<String> getChartSets() {
        return chartSets;
    }

    /**
     * Gets the selected chart set.
     * 
     * @return The selected chart set
     */
    protected String getChartSet() {
        return chartSet;
    }

    /**
     * Gets the default chart set.
     * 
     * @return The default chart set
     */
    protected String getDefaultChartSet() {
        return defaultChartSet;
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
        createButtons(composite);
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

        createDeleteButton(composite);
        createMakeDefaultButton(composite);
    }

    /**
     * Creates the delete button.
     * 
     * @param parent
     *            The parent composite
     */
    private void createDeleteButton(Composite parent) {
        deleteButton = new Button(parent, SWT.PUSH);
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
     * Creates the make default button.
     * 
     * @param parent
     *            The parent composite
     */
    private void createMakeDefaultButton(Composite parent) {
        makeDefaultButton = new Button(parent, SWT.PUSH);
        makeDefaultButton.setText(Messages.makeDefaultButtonLabel);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        makeDefaultButton.setLayoutData(gridData);
        makeDefaultButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = (IStructuredSelection) chartSetViewer
                        .getSelection();
                defaultChartSet = (String) selection.getFirstElement();
                chartSetViewer.refresh();
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
        final TableViewer viewer = new TableViewer(parent, SWT.BORDER
                | SWT.FULL_SELECTION);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.heightHint = 300;
        viewer.getTable().setLayoutData(gridData);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element.equals(defaultChartSet)) {
                    return NLS.bind(Messages.defaultChartSet, defaultChartSet);
                }
                return super.getText(element);
            }
        });
        viewer.setInput(chartSets.toArray(new String[chartSets.size()]));

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                ISelection selection = event.getSelection();
                Object element = ((StructuredSelection) selection)
                        .getFirstElement();

                if (deleteButton != null) {
                    deleteButton.setEnabled(!selection.isEmpty()
                            && !predefinedChartSets.contains(element));
                }
                if (element != null) {
                    chartSet = element.toString();
                } else {
                    viewer.setSelection(new StructuredSelection(chartSet));
                }
            }
        });

        viewer.setSelection(new StructuredSelection(defaultChartSet));
        return viewer;
    }
}