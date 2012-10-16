/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.AbstractElementListSelectionDialog;
import org.fusesource.ide.jvmmonitor.core.Activator;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.PackageLabelProvider;


/**
 * The dialog to add Java packages to profiler configuration.
 */
public class AddPackageDialog extends AbstractElementListSelectionDialog {

    /** The separator for packages. */
    private static final String SEPARATOR = ","; //$NON-NLS-1$

    /** The resource name for external plug-in libraries. */
    private static final String EXTERNAL_PLUGIN_LIBRARIES = "External Plug-in Libraries"; //$NON-NLS-1$

    /** The filtering packages. */
    private Object[] filteringPackages;

    /** The list elements. */
    private Object[] elements;

    /** The button to enable package text field. */
    private Button enableTextButton;

    /**
     * The constructor.
     * 
     * @param parent
     *            The parent shell
     * @param filteringPackages
     *            The packages to be filtered out
     */
    public AddPackageDialog(Shell parent, Object[] filteringPackages) {
        super(parent, new PackageLabelProvider());
        this.filteringPackages = filteringPackages;

        IJavaModel javaModel = JavaCore.create(ResourcesPlugin.getWorkspace()
                .getRoot());
        elements = getPackageFragments(javaModel);
        setMultipleSelection(true);
        setMessage(Messages.selectPackagesMessage);
    }

    /*
     * @see ElementListSelectionDialog#createDialogArea(Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        createMessageArea(container);
        createFilterText(container);
        createFilteredList(container);
        setListElements(elements);

        createPackageControls(container);

        setSelection(getInitialElementSelections().toArray());

        getShell().setText(Messages.addPackageDialogTitle);
        return container;
    }

    /*
     * @see SelectionStatusDialog#computeResult()
     */
    @Override
    protected void computeResult() {
        if (!enableTextButton.getSelection()) {
            setResult(Arrays.asList(getSelectedElements()));
        }
    }

    /*
     * @see SelectionStatusDialog#updateButtonsEnableState(IStatus)
     */
    @Override
    protected void updateButtonsEnableState(IStatus status) {
        if (!enableTextButton.getSelection()) {
            super.updateButtonsEnableState(status);
        }
    }

    /**
     * Creates the package controls.
     * 
     * @param parent
     *            The parent composite
     */
    private void createPackageControls(Composite parent) {
        Composite contanier = new Composite(parent, SWT.NONE);
        contanier.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout layout = new GridLayout(1, false);
        layout.marginTop = 10;
        contanier.setLayout(layout);

        enableTextButton = new Button(contanier, SWT.CHECK);
        enableTextButton.setText(Messages.enterPackageName);
        enableTextButton.setSelection(false);

        Composite container = new Composite(contanier, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        layout = new GridLayout(2, false);
        layout.marginWidth = 15;
        container.setLayout(layout);

        final Label packageNameLabel = new Label(container, SWT.NONE);
        packageNameLabel.setText(Messages.packageNameLabel);
        packageNameLabel.setEnabled(false);

        final Text packageText = createPackageText(container);

        enableTextButton.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean enabled = enableTextButton.getSelection();

                int color = enabled ? SWT.COLOR_WIDGET_BACKGROUND
                        : SWT.COLOR_LIST_BACKGROUND;
                fFilteredList.getChildren()[0].setBackground(Display
                        .getDefault().getSystemColor(color));
                fFilteredList.setSelection(new Object[0]);
                fFilteredList.setEnabled(!enabled);
                packageNameLabel.setEnabled(enabled);
                packageText.setEnabled(enabled);
                validate(packageText.getText());
            }
        });
    }

    /**
     * Creates the package text field.
     * 
     * @param parent
     *            The parent composite
     * @return The package text field
     */
    private Text createPackageText(Composite parent) {
        final Text packageText = new Text(parent, SWT.BORDER);
        packageText.addModifyListener(new ModifyListener() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void modifyText(ModifyEvent e) {
                String packageName = packageText.getText();
                if (validate(packageName)) {
                    setSelectionResult(packageName.split(SEPARATOR));
                }
            }
        });
        packageText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        packageText.setEnabled(false);
        return packageText;
    }

    /**
     * Validates the package name.
     * 
     * @param packageName
     *            The package name
     * @return <tt>true</tt> if given package name is valid
     */
    boolean validate(String packageName) {
        boolean isValid = !packageName.isEmpty();
        getOkButton().setEnabled(isValid);
        return isValid;
    }

    /**
     * Gets the package fragments.
     * 
     * @param javaModel
     *            The java model
     * @return The package fragments
     */
    private Object[] getPackageFragments(IJavaModel javaModel) {

        Set<String> packageElements = new HashSet<String>();
        IJavaProject[] projects;
        try {
            projects = javaModel.getJavaProjects();
        } catch (JavaModelException e) {
            Activator.log(IStatus.ERROR, Messages.getJavaModelFailedMsg, e);
            return new Object[0];
        }

        for (IJavaProject project : projects) {
            if (EXTERNAL_PLUGIN_LIBRARIES.equals(project.getResource()
                    .getName())) {
                continue;
            }

            IPackageFragmentRoot[] packageFragmentRoots;
            try {
                packageFragmentRoots = project.getPackageFragmentRoots();
            } catch (JavaModelException e) {
                continue;
            }

            for (IPackageFragmentRoot packageFragment : packageFragmentRoots) {
                try {
                    addPackage(packageElements, packageFragment);
                } catch (JavaModelException e) {
                    // do nothing
                }
            }
        }

        for (Object packageName : filteringPackages) {
            packageElements.remove(packageName);
        }

        return packageElements.toArray(new String[0]);
    }

    /**
     * Adds the package.
     * 
     * @param packageElements
     *            The package elements into which package is added
     * @param element
     *            The element which contains package element to be added
     * @throws JavaModelException
     */
    private void addPackage(Set<String> packageElements, IJavaElement element)
            throws JavaModelException {

        // java source folder
        if (element instanceof IPackageFragmentRoot) {
            int kind = ((IPackageFragmentRoot) element).getKind();
            if (kind == IPackageFragmentRoot.K_SOURCE) {
                IJavaElement[] children = ((IPackageFragmentRoot) element)
                        .getChildren();
                for (IJavaElement child : children) {
                    addPackage(packageElements, child);
                }
            }
        }

        // java package
        if (element instanceof IPackageFragment) {
            IJavaElement[] children = ((IPackageFragment) element)
                    .getChildren();
            for (IJavaElement child : children) {
                if (IJavaElement.COMPILATION_UNIT == child.getElementType()) {
                    packageElements.add(element.getElementName());
                    break;
                }
            }
        }
    }
}
