/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others. 
 * All rights reserved. This program and the accompanying materials are 
 * made available under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.wizards;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.eclipse.ui.dialogs.SelectionDialog;
import org.jboss.mapper.camel.CamelConfigBuilder;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.util.CamelConfigurationHelper;
import org.jboss.tools.fuse.transformation.editor.internal.util.JavaUtil;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;

/**
 *
 *
 */
public class TransformTestWizardPage extends WizardPage {

    private Text testClassNameText;
    private Text testClassPackageNameText;
    private ComboViewer transformationIDViewer;
    private Text _camelFilePathText;

    private IProject _project;
    private IJavaProject _javaProject;
    private IFile _camelConfigFile;
    private CamelConfigBuilder _builder = null;
    private String _transformID = null;
    private String _packageName = null;
    private String _className = null;
    private String _camelFilePath = null;

    public TransformTestWizardPage() {
        super("New Transformation Test", "New Transformation Test", Activator.imageDescriptor("transform.png"));
    }

    @Override
    public void createControl(Composite parent) {
        setDescription("Specify the transformation endpoint to test, then provide the camel configuration, class name and java package for the generated test class.");
        final Composite page = new Composite(parent, SWT.NONE);
        setControl(page);
        page.setLayout(GridLayoutFactory.swtDefaults().spacing(0, 5).numColumns(3).create());

        // Create camel file path widgets
        Label label = new Label(page, SWT.NONE);
        label.setText("Camel File Path:");
        label.setToolTipText("The path to the Camel configuration file.");

        _camelFilePathText = new Text(page, SWT.BORDER);
        _camelFilePathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        _camelFilePathText.setToolTipText(label.getToolTipText());

        final Button camelPathButton = new Button(page, SWT.NONE);
        camelPathButton.setText("...");
        camelPathButton.setToolTipText("Browse to select an available Camel file.");

        camelPathButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                _builder = null;
                final IResource res = Util.selectResourceFromWorkspace(getShell(), ".xml", _project);
                if (res != null) {
                    String path = "";
                    try {
                        IPath respath = JavaUtil.getJavaPathForResource(res);
                        IFile camelConfigFile = (IFile) _project.findMember(respath);
                        if (camelConfigFile == null) {
                            IPath newrespath = new Path("src/main/resources/").append(respath);
                            camelConfigFile = (IFile) _project.findMember(newrespath);
                        }
                        if (camelConfigFile != null) {
                            path = respath.makeRelative().toString();
                            File file = new File(camelConfigFile.getLocationURI());
                            _builder = CamelConfigurationHelper.load(file).getConfigBuilder();
                            if (_builder != null) {
                                transformationIDViewer.getCombo().removeAll();
                                transformationIDViewer.add(_builder.getTransformEndpointIds().toArray());
                            }
                        }
                    } catch (Exception e) {
                        Activator.error(e);
                    }
                    if (_builder == null || _builder != null && _builder.getTransformEndpointIds().isEmpty()) {
                        transformationIDViewer.getCombo().removeAll();
                        transformationIDViewer.getCombo().setToolTipText("No transformation endpoints available");
                    } else {
                        transformationIDViewer.getCombo().setToolTipText(
                                "Select from the list of available transformation endpoints");
                    }
                    _camelFilePathText.setText(path);
                    validatePage();
                }
            }
        });

        label = new Label(page, SWT.NONE);
        label.setText("Transformation ID:");
        label.setToolTipText("Transformation endpoint to test");
        transformationIDViewer = new ComboViewer(new Combo(page, SWT.READ_ONLY));
        transformationIDViewer.getCombo().setLayoutData(
                GridDataFactory.swtDefaults().grab(true, false).span(2, 1).align(SWT.FILL, SWT.CENTER).create());

        new Label(page, SWT.NONE).setText("Test Class Name:");
        label.setToolTipText("Name of the generated test class");
        testClassNameText = new Text(page, SWT.BORDER);
        testClassNameText.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).span(2, 1)
                .align(SWT.FILL, SWT.CENTER).create());
        testClassNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent event) {
                _className = testClassNameText.getText().trim();
                validatePage();
            }
        });

        new Label(page, SWT.NONE).setText("Test Class Package:");
        label.setToolTipText("Package where the test class is to be generated");
        testClassPackageNameText = new Text(page, SWT.BORDER);
        testClassPackageNameText.setLayoutData(GridDataFactory.swtDefaults().grab(true, false)
                .align(SWT.FILL, SWT.CENTER).create());
        Button browsePackage = new Button(page, SWT.PUSH);
        browsePackage.setText("...");
        browsePackage.setLayoutData(new GridData());
        browsePackage.setToolTipText("Select existing package.");
        browsePackage.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                if (_javaProject != null) {
                    try {
                        SelectionDialog dialog = JavaUI.createPackageDialog(getShell(), _javaProject, 0);
                        if (dialog.open() == SelectionDialog.OK) {
                            IPackageFragment result = (IPackageFragment) dialog.getResult()[0];
                            testClassPackageNameText.setText(result.getElementName());
                            _packageName = result.getElementName();
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
                _packageName = testClassPackageNameText.getText().trim();
                validatePage();
            }
        });
        if (_packageName != null) {
            testClassPackageNameText.setText(_packageName);
        }

        transformationIDViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(final Object element) {
                return ((String) element);
            }
        });
        if (_camelFilePath != null) {
            _camelFilePathText.setText(_camelFilePath);
        }
        if (_builder != null) {
            transformationIDViewer.add(_builder.getTransformEndpointIds().toArray());
            transformationIDViewer.getCombo().setToolTipText(
                    "Select from the list of available transformation endpoints");
        } else {
            transformationIDViewer.getCombo().removeAll();
            transformationIDViewer.getCombo().setToolTipText("No transformation endpoints available");
        }

        transformationIDViewer.getCombo().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                _transformID = (String) ((IStructuredSelection) transformationIDViewer.getSelection())
                        .getFirstElement();
                validatePage();
            }
        });
        validatePage();
    }

    void validatePage() {
        if (!transformationIDViewer.getSelection().isEmpty() && !testClassNameText.getText().trim().isEmpty()
                && !testClassPackageNameText.getText().trim().isEmpty()
                && !_camelFilePathText.getText().trim().isEmpty()) {
            if (_javaProject != null) {
                IStatus packageOK = JavaUtil.validatePackageName(_packageName, _javaProject);
                if (!packageOK.isOK()) {
                    setErrorMessage(packageOK.getMessage());
                } else {
                    IStatus classNameOK = JavaUtil.validateClassFileName(_className, _javaProject);
                    if (!classNameOK.isOK()) {
                        setErrorMessage(classNameOK.getMessage());
                    } else {
                        try {
                            IType foundType = _javaProject.findType(_packageName + "." + _className);
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

    public IProject getProject() {
        return _project;
    }

    public void setProject(IProject project) {
        this._project = project;
    }

    public IJavaProject getJavaProject() {
        return _javaProject;
    }

    public void setJavaProject(IJavaProject javaProject) {
        this._javaProject = javaProject;
    }

    public IFile getDozerConfigFile() {
        return _camelConfigFile;
    }

    public void setCamelConfigFile(IFile dozerConfigFile) {
        this._camelConfigFile = dozerConfigFile;
        if (dozerConfigFile != null) {
            this._camelFilePath = this._camelConfigFile.getProjectRelativePath().toPortableString();
        }
    }

    public CamelConfigBuilder getBuilder() {
        return _builder;
    }

    public void setBuilder(CamelConfigBuilder builder) {
        this._builder = builder;
    }

    public String getTransformID() {
        return _transformID;
    }

    public void setTransformID(String transformID) {
        this._transformID = transformID;
    }

    public String getPackageName() {
        return _packageName;
    }

    public void setPackageName(String packageName) {
        this._packageName = packageName;
    }

    public String getClassName() {
        return _className;
    }

    public void setClassName(String className) {
        this._className = className;
    }

    public String getCamelFilePath() {
        return _camelFilePath;
    }

    public void setCamelFilePath(String path) {
        this._camelFilePath = path;
    }

}
