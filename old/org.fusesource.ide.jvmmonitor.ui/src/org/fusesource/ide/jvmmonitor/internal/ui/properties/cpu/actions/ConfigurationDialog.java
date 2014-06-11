/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuProfiler.ProfilerState;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuProfiler.ProfilerType;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.PackageLabelProvider;


/**
 * The CPU profiler configuration.
 */
public class ConfigurationDialog extends SelectionDialog {

    /** The profiler type. */
    ProfilerType profilerType;

    /** The sampling period. */
    int samplingPeriod;

    /** The packages viewer. */
    TableViewer packagesViewer;

    /** The specified Java packages. */
    Set<String> packages;

    /** The sampling period text field. */
    Text samplingPeriodText;

    /** The sampling period label. */
    Label samplingPeriodLabel;

    /** The CPU profiler state. */
    private ProfilerState profilerState;

    /** The error image. */
    private Image warningImage;

    /** The error image label. */
    private Label warningImageLabel;

    /** The error message label. */
    private Label warningMessageLabel;

    /** The BCI button. */
    private Button bciButton;

    /**
     * The constructor.
     * 
     * @param parentShell
     *            The parent shell
     * @param profilerType
     *            The profiler type
     * @param samplingPeriod
     *            The sampling period
     * @param bciProfilerState
     *            The state for BCI profiler
     * @param packages
     *            The packages
     */
    public ConfigurationDialog(Shell parentShell, ProfilerType profilerType,
            int samplingPeriod, ProfilerState bciProfilerState,
            Set<String> packages) {
        super(parentShell);
        setTitle(Messages.configureCpuProfilerTitle);
        setHelpAvailable(false);

        this.profilerType = profilerType;
        this.samplingPeriod = samplingPeriod;
        this.profilerState = bciProfilerState;
        this.packages = new LinkedHashSet<String>(packages);
    }

    /*
     * @see Dialog#createDialogArea(Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);

        Composite inner = new Composite(composite, SWT.NONE);
        inner.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        inner.setLayout(layout);

        createProfilerTypeSelection(inner);
        createPackagesViewer(inner);
        createWarningMessageControls(inner);

        applyDialogFont(composite);

        return composite;
    }

    /*
     * @see Dialog#getInitialSize()
     */
    @Override
    protected Point getInitialSize() {
        return new Point(600, 500);
    }

    /*
     * @see Dialog#create()
     */
    @Override
    public void create() {
        super.create();
        validate();
    }

    /*
     * @see Dialog#close()
     */
    @Override
    public boolean close() {
        if (warningImage != null) {
            warningImage.dispose();
        }
        return super.close();
    }

    /**
     * Gets the profiler type.
     * 
     * @return The profiler type
     */
    protected ProfilerType getProfilerType() {
        return profilerType;
    }

    /**
     * Gets the sampling period.
     * 
     * @return The sampling period
     */
    protected int getSamplingPeriod() {
        return samplingPeriod;
    }

    /**
     * Gets the packages.
     * 
     * @return The packages
     */
    protected Set<String> getPackages() {
        return packages;
    }

    /**
     * Validates the currently entered values.
     * 
     * @return True if values are valid
     */
    boolean validate() {
        boolean isValid = true;
        try {
            Integer value = Integer.valueOf(samplingPeriodText.getText());
            if (value <= 0) {
                isValid = false;
            }
        } catch (NumberFormatException e) {
            isValid = false;
        }

        String warningMessage = ""; //$NON-NLS-1$
        if (profilerState == ProfilerState.INVALID_VERSION) {
            warningMessage = Messages.invalidVersionMsg;
        } else if (profilerState == ProfilerState.AGENT_NOT_LOADED
                || profilerState == ProfilerState.UNKNOWN) {
            warningMessage = Messages.agentNotLoadedMsg;
        }
        warningImageLabel.setVisible(!warningMessage.isEmpty());
        warningMessageLabel.setText(warningMessage);
        bciButton.setEnabled(profilerState == ProfilerState.READY
                || profilerState == ProfilerState.RUNNING);

        getOkButton().setEnabled(isValid);
        return isValid;
    }

    /**
     * Creates the profiler type selection.
     * 
     * @param parent
     *            The parent composite
     */
    private void createProfilerTypeSelection(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(Messages.profilerTypeGroupLabel);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setLayout(new GridLayout(1, false));

        Button samplingButton = new Button(group, SWT.RADIO);
        samplingButton.setText(Messages.samplingButtonLabel);
        samplingButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                profilerType = ProfilerType.SAMPLING;
                samplingPeriodLabel.setEnabled(true);
                samplingPeriodText.setEnabled(true);
            }
        });

        createSamplingPeriodText(group);

        bciButton = new Button(group, SWT.RADIO);
        bciButton.setText(Messages.bciButtonLabel);
        bciButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                profilerType = ProfilerType.BCI;
                samplingPeriodLabel.setEnabled(false);
                samplingPeriodText.setEnabled(false);
            }
        });

        boolean isBCI = profilerType == ProfilerType.BCI;
        samplingButton.setSelection(!isBCI);
        bciButton.setSelection(isBCI);
        bciButton.setEnabled(profilerState != ProfilerState.AGENT_NOT_LOADED);
        samplingPeriodLabel.setEnabled(!isBCI);
        samplingPeriodText.setEnabled(!isBCI);
    }

    /**
     * Creates the sampling period text.
     * 
     * @param parent
     *            The parent composite
     */
    private void createSamplingPeriodText(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        composite.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalIndent = 20;
        composite.setLayoutData(gridData);

        samplingPeriodLabel = new Label(composite, SWT.NONE);
        samplingPeriodLabel.setText(Messages.samplingPeriodLabel);
        samplingPeriodText = new Text(composite, SWT.BORDER);
        samplingPeriodText.setText(String.valueOf(samplingPeriod));
        samplingPeriodText
                .setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        samplingPeriodText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (validate()) {
                    samplingPeriod = Integer.valueOf(samplingPeriodText
                            .getText());
                }
            }
        });
    }

    /**
     * Creates the packages viewer.
     * 
     * @param parent
     *            The parent composite
     */
    private void createPackagesViewer(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.profiledPackagesLabel);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        label.setLayoutData(gridData);

        packagesViewer = new TableViewer(composite, SWT.BORDER | SWT.MULTI);
        packagesViewer.getTable().setLayoutData(
                new GridData(GridData.FILL_BOTH));
        packagesViewer.setContentProvider(new ArrayContentProvider());
        packagesViewer.setLabelProvider(new PackageLabelProvider());
        packagesViewer.setInput(packages.toArray(new String[packages.size()]));

        createAddRemoveButtons(composite);
    }

    /**
     * Creates the button.
     * 
     * @param parent
     *            The parent composite
     */
    private void createAddRemoveButtons(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);

        Button addButton = new Button(composite, SWT.PUSH);
        addButton.setText(Messages.addLabel);
        setButtonLayoutData(addButton);
        addButton.addSelectionListener(getAddButtonListener());

        Button removeButton = new Button(composite, SWT.PUSH);
        removeButton.setText(Messages.removeLabel);
        setButtonLayoutData(removeButton);
        removeButton.addSelectionListener(getRemoveButtonListener());
    }

    /**
     * Creates the controls to show warning message.
     * 
     * @param parent
     *            The parent composite
     */
    private void createWarningMessageControls(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        warningImageLabel = new Label(composite, SWT.NONE);
        warningImageLabel.setImage(getWarningImage());
        warningImageLabel.setVisible(false);

        warningMessageLabel = new Label(composite, SWT.READ_ONLY | SWT.WRAP);
        warningMessageLabel
                .setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * Gets the listener for remove button.
     * 
     * @return The listener for remove button
     */
    private SelectionListener getRemoveButtonListener() {
        return new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Object[] input = (Object[]) packagesViewer.getInput();

                packages.clear();
                for (Object object : input) {
                    packages.add((String) object);
                }
                Object[] selections = ((IStructuredSelection) packagesViewer
                        .getSelection()).toArray();
                for (Object selection : selections) {
                    packages.remove(selection);
                }

                packagesViewer.setInput(packages.toArray(new String[packages
                        .size()]));
                packagesViewer.refresh();
            }
        };
    }

    /**
     * Gets the listener for add button.
     * 
     * @return The listener for add button
     */
    private SelectionListener getAddButtonListener() {
        return new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Object[] input = (Object[]) packagesViewer.getInput();
                AddPackageDialog dialog = new AddPackageDialog(getShell(),
                        input);
                if (dialog.open() == Window.OK) {
                    packages.clear();
                    for (Object object : input) {
                        packages.add((String) object);
                    }
                    Object[] elements = dialog.getResult();
                    for (Object object : elements) {
                        packages.add(((String) object).trim());
                    }
                    String[] items = packages.toArray(new String[packages
                            .size()]);
                    Arrays.sort(items);
                    packagesViewer.setInput(items);
                    packagesViewer.refresh();
                }
            }
        };
    }

    /**
     * Gets the warning image.
     * 
     * @return The warning image
     */
    private Image getWarningImage() {
        if (warningImage == null || warningImage.isDisposed()) {
            warningImage = PlatformUI.getWorkbench().getSharedImages()
                    .getImageDescriptor(ISharedImages.IMG_OBJS_WARN_TSK)
                    .createImage();
        }
        return warningImage;
    }
}
