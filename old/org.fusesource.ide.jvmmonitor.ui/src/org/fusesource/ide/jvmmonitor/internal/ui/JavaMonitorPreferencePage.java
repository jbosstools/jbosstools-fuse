/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.IHost;
import org.fusesource.ide.jvmmonitor.core.JvmModel;
import org.fusesource.ide.jvmmonitor.ui.Activator;


/**
 * The preference page at preference dialog: Java > Monitor.
 */
public class JavaMonitorPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage {

    /** The minimum value of update period. */
    private static final int MIN_UPDATE_PERIOD = 100;

    /** The update period text field. */
    private Text updatePeriodText;

    /** The legend visibility check box. */
    private Button legendVisibilityButton;

    /** The check box to take stack traces into account when filtering threads. */
    private Button wideScopeThreadFilterButton;

    /**
     * The check box to take stack traces into account when filtering SWT
     * resources.
     */
    private Button wideScopeSWTResourcesFilterButton;

    /*
     * @see PreferencePage#createContents(Composite)
     */
    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);

        createUpdatePeriodText(composite);
        createTimelineGroup(composite);
        createThreadsGroup(composite);
        createMemoryGroup(composite);

        applyDialogFont(composite);

        PlatformUI.getWorkbench().getHelpSystem()
                .setHelp(parent, IHelpContextIds.JAVA_MONITOR_PREFERENCE_PAGE);

        return composite;
    }

    /*
     * @see IWorkbenchPreferencePage#init(IWorkbench)
     */
    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    /*
     * @see PreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        getPreferenceStore().setValue(IConstants.UPDATE_PERIOD,
                updatePeriodText.getText());
        getPreferenceStore().setValue(IConstants.LEGEND_VISIBILITY,
                legendVisibilityButton.getSelection());
        getPreferenceStore().setValue(IConstants.WIDE_SCOPE_THREAD_FILTER,
                wideScopeThreadFilterButton.getSelection());
        getPreferenceStore().setValue(
                IConstants.WIDE_SCOPE_SWT_RESOURCE_FILTER,
                wideScopeSWTResourcesFilterButton.getSelection());

        applyChanges();
        return true;
    }

    /**
     * Applies the changes.
     */
    private void applyChanges() {
        Integer period = Integer.valueOf(updatePeriodText.getText());
        for (IHost host : JvmModel.getInstance().getHosts()) {
            for (IActiveJvm jvm : host.getActiveJvms()) {
                jvm.getMBeanServer().setUpdatePeriod(period);
            }
        }
    }

    /*
     * @see PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        String updatePeriod = String.valueOf(getPreferenceStore()
                .getDefaultInt(IConstants.UPDATE_PERIOD));
        updatePeriodText.setText(updatePeriod);
        legendVisibilityButton.setSelection(getPreferenceStore()
                .getDefaultBoolean(IConstants.LEGEND_VISIBILITY));
        wideScopeThreadFilterButton.setSelection(getPreferenceStore()
                .getDefaultBoolean(IConstants.WIDE_SCOPE_THREAD_FILTER));
        wideScopeSWTResourcesFilterButton.setSelection(getPreferenceStore()
                .getDefaultBoolean(IConstants.WIDE_SCOPE_SWT_RESOURCE_FILTER));
        super.performDefaults();
    }

    /**
     * Creates the update period text field.
     * 
     * @param parent
     *            The parent composite
     */
    private void createUpdatePeriodText(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.updatePeriodLabel);

        updatePeriodText = new Text(composite, SWT.BORDER);
        updatePeriodText.setText(String.valueOf(getPreferenceStore().getInt(
                IConstants.UPDATE_PERIOD)));
        updatePeriodText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        updatePeriodText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                validateUpdatePeriod();
            }
        });
    }

    /**
     * Creates the timeline group.
     * 
     * @param parent
     *            The parent composite
     */
    private void createTimelineGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(Messages.timelineGroupLabel);
        GridLayout layout = new GridLayout(1, false);
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        legendVisibilityButton = new Button(group, SWT.CHECK);
        legendVisibilityButton.setText(Messages.showLegendLabel);
        legendVisibilityButton.setSelection(getPreferenceStore().getBoolean(
                IConstants.LEGEND_VISIBILITY));

        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        legendVisibilityButton.setLayoutData(gridData);
    }

    /**
     * Creates the threads group.
     * 
     * @param parent
     *            The parent composite
     */
    private void createThreadsGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(Messages.threadsGroupLabel);
        GridLayout layout = new GridLayout(1, false);
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        wideScopeThreadFilterButton = new Button(group, SWT.CHECK);
        wideScopeThreadFilterButton
                .setText(Messages.wideScopeThreadFilterLabel);
        wideScopeThreadFilterButton.setSelection(getPreferenceStore()
                .getBoolean(IConstants.WIDE_SCOPE_THREAD_FILTER));

        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        wideScopeThreadFilterButton.setLayoutData(gridData);
    }

    /**
     * Creates the memory group.
     * 
     * @param parent
     *            The parent composite
     */
    private void createMemoryGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(Messages.memoryGroupLabel);
        GridLayout layout = new GridLayout(1, false);
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        wideScopeSWTResourcesFilterButton = new Button(group, SWT.CHECK);
        wideScopeSWTResourcesFilterButton
                .setText(Messages.wideScopeSWTResourceFilterLabel);
        wideScopeSWTResourcesFilterButton.setSelection(getPreferenceStore()
                .getBoolean(IConstants.WIDE_SCOPE_SWT_RESOURCE_FILTER));

        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        wideScopeSWTResourcesFilterButton.setLayoutData(gridData);
    }

    /**
     * Validates the update period.
     */
    void validateUpdatePeriod() {

        // check if text is empty
        String period = updatePeriodText.getText();
        if (period.isEmpty()) {
            setMessage(Messages.updatePeriodNotEnteredMsg,
                    IMessageProvider.WARNING);
            return;
        }

        // check if text is integer
        try {
            Integer.parseInt(period);
        } catch (NumberFormatException e) {
            setMessage(Messages.illegalUpdatePeriodMsg, IMessageProvider.ERROR);
            return;
        }

        // check if the value is within valid range
        if (Integer.valueOf(period) < MIN_UPDATE_PERIOD) {
            setMessage(Messages.updatePeriodOutOfRangeMsg,
                    IMessageProvider.ERROR);
            return;
        }

        setMessage(null);
    }
}
