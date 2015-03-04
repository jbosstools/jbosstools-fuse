/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others. 
 * All rights reserved. This program and the accompanying materials are 
 * made available under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.mapper.eclipse.wizards;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.jboss.mapper.camel.CamelConfigBuilder;
import org.jboss.mapper.eclipse.Activator;
import org.jboss.mapper.eclipse.internal.util.JavaUtil;
import org.jboss.mapper.test.TestGenerator;

/**
 *
 */
public class NewTransformationTestWizard extends Wizard implements INewWizard {

    static final String DEFAULT_DOZER_CONFIG_FILE_NAME = "dozerBeanMapping.xml";

    IProject project;
    IJavaProject javaProject;
    IFile dozerConfigFile;
    CamelConfigBuilder builder;
    String transformID = null;
    String packageName = null;
    String className = null;

    /**
     *
     */
    public NewTransformationTestWizard() {
        addPage(constructMainPage());
    }

    private IWizardPage constructMainPage() {
        return new WizardPage("New Transformation Test", "New Transformation Test",
                Activator.imageDescriptor("transform.png")) {

            protected Text testClassNameText;
            protected Text testClassPackageNameText;
            protected ComboViewer transformationIDViewer;

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
             */
            @Override
            public void createControl(final Composite parent) {
                setDescription("Specify the transformation endpoint to test, " 
                        + "then provide the class name and java package for the generated test class.");
                final Composite page = new Composite(parent, SWT.NONE);
                setControl(page);
                page.setLayout(GridLayoutFactory.swtDefaults().spacing(0, 5).numColumns(3).create());
                Label label = new Label(page, SWT.NONE);
                label.setText("Transformation ID:");
                label.setToolTipText("Transformation endpoint to test");
                transformationIDViewer = new ComboViewer(new Combo(page, SWT.READ_ONLY));
                transformationIDViewer.getCombo().setLayoutData(GridDataFactory.swtDefaults()
                        .grab(true, false)
                        .span(2, 1)
                        .align(SWT.FILL, SWT.CENTER)
                        .create());

                new Label(page, SWT.NONE).setText("Test Class Name:");
                label.setToolTipText("Name of the generated test class");
                testClassNameText = new Text(page, SWT.BORDER);
                testClassNameText.setLayoutData(GridDataFactory.swtDefaults()
                        .grab(true, false)
                        .span(2, 1)
                        .align(SWT.FILL, SWT.CENTER)
                        .create());
                testClassNameText.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent event) {
                        className = testClassNameText.getText().trim();
                        validatePage();
                    }
                });

                new Label(page, SWT.NONE).setText("Test Class Package:");
                label.setToolTipText("Package where the test class is to be generated");
                testClassPackageNameText = new Text(page, SWT.BORDER);
                testClassPackageNameText.setLayoutData(GridDataFactory.swtDefaults()
                        .grab(true, false)
                        .align(SWT.FILL, SWT.CENTER)
                        .create());
                Button browsePackage = new Button(page, SWT.PUSH);
                browsePackage.setText("...");
                browsePackage.setLayoutData(new GridData());
                browsePackage.setToolTipText("Select existing package.");
                browsePackage.addSelectionListener(new SelectionListener() {

                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        if (javaProject != null) {
                            try {
                                SelectionDialog dialog =
                                        JavaUI.createPackageDialog(getShell(), javaProject, 0);
                                if (dialog.open() == SelectionDialog.OK) {
                                    IPackageFragment result =
                                            (IPackageFragment) dialog.getResult()[0];
                                    testClassPackageNameText.setText(result.getElementName());
                                    packageName = result.getElementName();
                                }
                            } catch (JavaModelException e) {
                                Activator.error(e);
                            }
                        }
                    }

                    @Override
                    public void widgetDefaultSelected(SelectionEvent event) {
                        // empty
                    }
                });
                testClassPackageNameText.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent event) {
                        packageName = testClassPackageNameText.getText().trim();
                        validatePage();
                    }
                });
                if (packageName != null) {
                    testClassPackageNameText.setText(packageName);
                }

                transformationIDViewer.setLabelProvider(new LabelProvider() {
                    @Override
                    public String getText(final Object element) {
                        return ((String) element);
                    }
                });
                if (builder != null) {
                    transformationIDViewer.add(builder.getTransformEndpointIds().toArray());
                }

                transformationIDViewer.getCombo().addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                        transformID =
                                (String) ((IStructuredSelection) transformationIDViewer
                                        .getSelection()).getFirstElement();
                        validatePage();
                    }
                });
                validatePage();
            }

            void validatePage() {
                if (!transformationIDViewer.getSelection().isEmpty()
                        && !testClassNameText.getText().trim().isEmpty()
                        && !testClassPackageNameText.getText().trim().isEmpty()) {
                    if (javaProject != null) {
                        IStatus packageOK = JavaUtil.validatePackageName(packageName, javaProject);
                        if (!packageOK.isOK()) {
                            setErrorMessage(packageOK.getMessage());
                        } else {
                            IStatus classNameOK =
                                    JavaUtil.validateClassFileName(className, javaProject);
                            if (!classNameOK.isOK()) {
                                setErrorMessage(classNameOK.getMessage());
                            } else {
                                try {
                                    IType foundType =
                                            javaProject.findType(packageName + "." + className);
                                    if (foundType != null) {
                                        setErrorMessage("A generated test class with that "
                                                + "name and package already exists.");
                                    } else {
                                        setErrorMessage(null);
                                    }
                                } catch (JavaModelException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    setPageComplete(true);
                    return;
                }
                setPageComplete(false);
            }
        };
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     *      org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // what are we passing in? assume we're right-clicking on the dozer file
        if (selection.size() != 1) {
            return;
        }
        Object selectedObject = selection.getFirstElement();
        if (selectedObject != null && selectedObject instanceof IFile) {
            dozerConfigFile = (IFile) selectedObject;
            project = dozerConfigFile.getProject();
        } else if (selectedObject != null && selectedObject instanceof IProject) {
            project = (IProject) selectedObject;
            IResource findCamelContext =
                    project.findMember("src/main/resources/META-INF/spring/camel-context.xml");
            if (findCamelContext != null) {
                dozerConfigFile = (IFile) findCamelContext;
            } else {
                Activator.error(new Throwable("Can't find camel context file."));
            }
        }
        if (project != null) {
            javaProject = JavaCore.create(project);
        }
        if (dozerConfigFile != null) {
            File file = new File(dozerConfigFile.getLocationURI());
            try {
                builder = CamelConfigBuilder.loadConfig(file);
            } catch (Exception e) {
                Activator.error(e);
            }
        }
        if (javaProject != null) {
            IJavaElement element = JavaUtil.getInitialPackageForProject(javaProject);
            if (element != null) {
                packageName = element.getElementName();
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        // here we're creating the transformation test
        IJavaProject javaProject = null;
        if (project != null) {
            javaProject = JavaCore.create(project);
            IFolder folder = javaProject.getProject().getFolder("src/test/java");
            if (!JavaUtil.findFolderOnProjectClasspath(javaProject, folder)) {
                JavaUtil.addFolderToProjectClasspath(javaProject, folder);
            }
            File targetPath = new File(folder.getRawLocation().toPortableString());
            try {
                TestGenerator.createTransformTest(transformID,
                        packageName,
                        className,
                        targetPath);
                project.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
                return true;
            } catch (Exception e) {
                Activator.error(e);
            }
        }

        return false;
    }

}
