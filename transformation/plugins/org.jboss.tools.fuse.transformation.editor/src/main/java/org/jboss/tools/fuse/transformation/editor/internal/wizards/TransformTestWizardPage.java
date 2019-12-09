/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.dialogs.StatusUtil;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.jboss.tools.fuse.transformation.core.camel.CamelConfigBuilder;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.internal.util.CamelFileTypeHelper;
import org.jboss.tools.fuse.transformation.editor.internal.util.JavaUtil;
import org.jboss.tools.fuse.transformation.editor.internal.util.TestGenerator;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;

/**
 *
 *
 */
public class TransformTestWizardPage extends NewTypeWizardPage {

    protected static final String CAMEL_FILE_PATH = "TransformTestWizardPage.CamelFilePath"; //$NON-NLS-1$
    protected static final String ENDPOINT = "TransformTestWizardPage.Endpoint"; //$NON-NLS-1$
    private static final String TEST_SOURCE_FOLDER = "src/test/java"; //$NON-NLS-1$

    private ComboViewer transformationIDViewer;
    private Text camelFilePathText;

    private IProject project;
    private IJavaProject javaProject;
    private IFile camelConfigFile;
    private CamelConfigBuilder builder = null;
    private String transformID = null;
    private String camelFilePath = null;
    private IResource generatedClassResource;

    private IStatus camelFileSelectedStatus = Status.OK_STATUS;
    private IStatus camelEndpointSelectedStatus = Status.OK_STATUS;

    public TransformTestWizardPage() {
        super(true, TransformTestWizardPage.class.getSimpleName());
        setImageDescriptor(Activator.imageDescriptor("transform.png")); //$NON-NLS-1$
        setTitle(Messages.TransformTestWizardPage_title);
		setDescription(Messages.TransformTestWizardPage_description);
    }

    private void createCamelSpecificControls(Composite composite) {
        // Create camel file path widgets
        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.TransformTestWizardPage_labelCamelFilePath);
        label.setToolTipText(Messages.TransformTestWizardPage_tootlipCamelFilePath);

        camelFilePathText = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
        camelFilePathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        camelFilePathText.setToolTipText(label.getToolTipText());
        camelFilePathText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent arg0) {
                handleFieldChanged(CAMEL_FILE_PATH);
            }
        });

        final Button camelPathButton = new Button(composite, SWT.NONE);
        camelPathButton.setText(Messages.TransformTestWizardPage_Browse);
        camelPathButton.setToolTipText(Messages.TransformTestWizardPage_BrowseTooltip);
        camelPathButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        camelPathButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                builder = null;
                final IResource res = Util.selectCamelResourceFromWorkspace(getShell(), project);
                if (res != null) {
                    String path = ""; //$NON-NLS-1$
                    try {
                        IPath respath = JavaUtil.getJavaPathForResource(res);
                        if (project == null) {
                            project = res.getProject();
                            javaProject = JavaCore.create(project);
                        }
                        if (javaProject != null) {
                            IFolder srcFolder = javaProject.getProject().getFolder(TEST_SOURCE_FOLDER);
                            IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(srcFolder);
                            initContainerPage(root);
                        }
                        IFile camelConfigFile = (IFile) project.findMember(respath);
                        if (camelConfigFile == null) {
                            IPath newrespath = new Path("src/main/resources/").append(respath); //$NON-NLS-1$
                            camelConfigFile = (IFile) project.findMember(newrespath);
                        }
                        if (camelConfigFile != null) {
                            path = respath.makeRelative().toString();
                            camelFilePath = camelConfigFile.getProjectRelativePath().toPortableString();
                            File file = new File(camelConfigFile.getLocationURI());
                            boolean isValid = CamelFileTypeHelper
                                    .isSupportedCamelFile(project, camelFilePath);
                            if (isValid) {
                                builder = new CamelConfigBuilder(file);
                                camelFileSelectedStatus = Status.OK_STATUS;
                            } else {
                                builder = null;
                                camelFileSelectedStatus = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                                        Messages.TransformTestWizardPage_errorWrongFileSelected
                                        + Messages.TransformTestWizardPage_errorMessagePleaseSelectAntherFile);
                            }
                            if (builder != null) {
                                transformationIDViewer.getCombo().removeAll();
                                transformationIDViewer.add(builder.getTransformEndpointIds().toArray());
                            }
                        }
                    } catch (Exception e) {
                        Activator.error(e);
                    }
                    if (builder == null || builder.getTransformEndpointIds().isEmpty()) {
                        transformationIDViewer.getCombo().removeAll();
                        transformationIDViewer.getCombo().setToolTipText(Messages.TransformTestWizardPage_tooltipNoTransformationEndpointsAvailable);
                        camelEndpointSelectedStatus = new Status(IStatus.WARNING, Activator.PLUGIN_ID,
                                Messages.TransformTestWizardPage_errorMessageNoTransformationEndpointsAvailable);
                    } else {
                        camelEndpointSelectedStatus = new Status(IStatus.INFO, Activator.PLUGIN_ID,
                                Messages.TransformTestWizardPage_selectFormTheListOFAvailableEndpoints);
                        transformationIDViewer.getCombo().setToolTipText(
                                Messages.TransformTestWizardPage_selectFormTheListOFAvailableEndpoints);
                    }
                    camelFilePathText.setText(path);
                    handleFieldChanged(CAMEL_FILE_PATH);
                }
            }
        });

        label = new Label(composite, SWT.NONE);
        label.setText(Messages.TransformTestWizardPage_transformationIDLabel);
        label.setToolTipText(Messages.TransformTestWizardPage_transformationIDTooltip);
        transformationIDViewer = new ComboViewer(new Combo(composite, SWT.READ_ONLY));
        transformationIDViewer.getCombo().setLayoutData(
                GridDataFactory.swtDefaults().grab(true, false).span(2, 1).align(SWT.FILL, SWT.CENTER).create());

        transformationIDViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(final Object element) {
                return (String)element;
            }
        });
        if (camelFilePath != null) {
            camelFilePathText.setText(camelFilePath);
        }
        boolean noEndpoints = true;
        transformationIDViewer.getCombo().removeAll();
        if (builder != null) {
            transformationIDViewer.add(builder.getTransformEndpointIds().toArray());
            noEndpoints = builder.getTransformEndpointIds().isEmpty();
        }
        if (!noEndpoints) {
            transformationIDViewer.getCombo().setToolTipText(
                Messages.TransformTestWizardPage_selectFormTheListOFAvailableEndpoints);
            camelEndpointSelectedStatus = new Status(IStatus.INFO, Activator.PLUGIN_ID,
                    Messages.TransformTestWizardPage_selectFormTheListOFAvailableEndpoints);
        } else {
            transformationIDViewer.getCombo().setToolTipText(Messages.TransformTestWizardPage_tooltipNoTransformationEndpointsAvailable);
            camelEndpointSelectedStatus = new Status(IStatus.WARNING, Activator.PLUGIN_ID,
                    Messages.TransformTestWizardPage_errorMessageNoTransformationEndpointsAvailable);
        }

        transformationIDViewer.getCombo().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
                transformID = (String) ((IStructuredSelection) transformationIDViewer.getSelection())
                        .getFirstElement();
                camelEndpointSelectedStatus = Status.OK_STATUS;
                handleFieldChanged(ENDPOINT);
            }
        });
        doStatusUpdate();
        setErrorMessage(null);
    }

    private List<Dependency> getRequiredBlueprintTestDependencies() {
        List<Dependency> deps = new ArrayList<>();
        Dependency dep = new Dependency();
        dep.setGroupId("org.apache.camel"); //$NON-NLS-1$
        dep.setArtifactId("camel-test-blueprint"); //$NON-NLS-1$
        dep.setVersion(CamelUtils.getCurrentProjectCamelVersion());
        deps.add(dep);
        return deps;
    }

    private List<Dependency> getRequiredSpringTestDependencies() {
        List<Dependency> deps = new ArrayList<>();
        Dependency dep = new Dependency();
        dep.setGroupId("org.apache.camel"); //$NON-NLS-1$
        dep.setArtifactId("camel-test-spring"); //$NON-NLS-1$
        dep.setVersion(CamelUtils.getCurrentProjectCamelVersion());
        deps.add(dep);
        return deps;
    }
    
    private void updateProjectDeps(List<Dependency> dependencies) { 
	    new MavenUtils().updateMavenDependencies(dependencies, project);	
    }

    private ICompilationUnit createJavaClass(String packageName,
                                             String className) {
        try {
            boolean isSpring = CamelFileTypeHelper
                    .isSpringFile(project, camelFilePath);
            boolean isBlueprint = CamelFileTypeHelper
                    .isBlueprintFile(project,camelFilePath);

            if (!isSpring && !isBlueprint) {
                // obviously we're not dealing with a camel file here
                return null;
            }

            List<Dependency> dependencies = isBlueprint ? getRequiredBlueprintTestDependencies()
            											: getRequiredSpringTestDependencies();
            
            Display.getDefault().asyncExec( () -> updateProjectDeps(dependencies));

            // refresh the project in case we added dependencies
            project.refreshLocal(IProject.DEPTH_INFINITE, null);
            // Ensure build of Java classes has completed
            try {
                Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
            } catch (InterruptedException ignored) {
            	Thread.currentThread().interrupt();
            }

            IPath srcPath;
            if (getPackageFragmentRoot() != null) {
                srcPath = getPackageFragmentRoot().getPath().makeAbsolute();
                srcPath = srcPath.removeFirstSegments(1);
                IFolder folder = javaProject.getProject().getFolder(srcPath);
                if (!JavaUtil.findFolderOnProjectClasspath(javaProject, folder)) {
                    JavaUtil.addFolderToProjectClasspath(javaProject, folder);
                }
                if (!folder.exists()) {
                    try {
                        folder.refreshLocal(IResource.DEPTH_INFINITE, null);
                    } catch (CoreException e) {
                    	Activator.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error while refreshing folder", e)); //$NON-NLS-1$
                    }
                }

            } else {
                IFolder folder = javaProject.getProject().getFolder(TEST_SOURCE_FOLDER); //$NON-NLS-1$
                if (!folder.exists()) {
                    IFolder srcFolder = javaProject.getProject().getFolder("src"); //$NON-NLS-1$
                    if (!srcFolder.exists()) {
                        srcFolder.create(true,  true,  null);
                    }
                    IFolder testFolder = srcFolder.getFolder("test"); //$NON-NLS-1$
                    if (!testFolder.exists()) {
                        testFolder.create(true,  true,  null);
                    }
                    IFolder javaFolder = testFolder.getFolder("java"); //$NON-NLS-1$
                    if (!javaFolder.exists()) {
                        javaFolder.create(true,  true,  null);
                    }
                }
                if (!JavaUtil.findFolderOnProjectClasspath(javaProject, folder)) {
                    JavaUtil.addFolderToProjectClasspath(javaProject, folder);
                }
                srcPath = folder.getProjectRelativePath();
            }

            IFolder srcFolder = project.getFolder(srcPath);
            if (srcFolder == null || !srcFolder.exists()) {
                srcPath = javaProject.getPath().append(
                        srcPath.makeRelativeTo(project
                                .getLocation()));
                srcFolder = project.getFolder(srcPath);
            }
            IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(srcFolder);
            if (packageName == null) {
                packageName = ""; //$NON-NLS-1$
            }
            if (root != null) {
                IPackageFragment pkg = root.createPackageFragment(packageName,
                        false, null);

                StringBuilder clsContent = new StringBuilder();

                String filePath = getCamelFilePath();
                IResource res = project.findMember(filePath);
                IPath respath = JavaUtil.getJavaPathForResource(res);
                filePath = respath.makeRelative().toString();

                if (isSpring || isBlueprint) {
                    String codeTemplate =
                        TestGenerator.createTransformTestText(
                                transformID, packageName, className, filePath, isSpring);

                    if (codeTemplate != null) {
                        clsContent.append(codeTemplate);
                    }
                    ICompilationUnit wrapperCls = pkg.createCompilationUnit(className
                        + ".java", clsContent.toString(), true, null); //$NON-NLS-1$
                    wrapperCls.save(null, true);
                    return wrapperCls;
                }
                return null;
            }
        } catch (Exception e) {
        	Activator.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error while creating Java class", e)); //$NON-NLS-1$
        }
        return null;
    }

    @Override
    public void createType(IProgressMonitor monitor) {
        ICompilationUnit createdClass = createJavaClass(getPackageText(), getTypeName());
        if (createdClass != null) {
            generatedClassResource = createdClass.getResource();
        }
    }

    public IResource getGeneratedResource() {
        return this.generatedClassResource;
    }

    @Override
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());

        int nColumns = 4;

        GridLayout layout = new GridLayout();
        layout.numColumns = nColumns;
        composite.setLayout(layout);

        // pick & choose the wanted UI components

        createContainerControls(composite, nColumns);
        createPackageControls(composite, nColumns);

        createSeparator(composite, nColumns);

        createTypeNameControls(composite, nColumns);

        createSeparator(composite, nColumns);

        createCamelSpecificControls(composite);

        setControl(composite);

        Dialog.applyDialogFont(composite);

        doStatusUpdate();

        updateStatus(new Status(IStatus.OK, Activator.PLUGIN_ID,
				Messages.TransformTestWizardPage_description));
        setErrorMessage(null);
    }

    @Override
    public boolean isPageComplete() {
        // having an endpoint selected implies that we also have a camel file selected
        boolean endpointSelected = getTransformID() != null && !getTransformID().trim().isEmpty();
        boolean sourceFolderSpecified = (getPackageFragmentRoot() != null)
                || (getPackageFragmentRootText() != null
                && getPackageFragmentRootText().endsWith("src/test/java")); //$NON-NLS-1$
        return super.isPageComplete() && endpointSelected && sourceFolderSpecified && getTypeName() != null;
    }

    public IProject getProject() {
        return project;
    }

    public void setProject(IProject project) {
        this.project = project;
    }

    @Override
    public IJavaProject getJavaProject() {
        return javaProject;
    }

    public void setJavaProject(IJavaProject javaProject) {
        this.javaProject = javaProject;
    }

    public IFile getDozerConfigFile() {
        return camelConfigFile;
    }

    public void setCamelConfigFile(IFile dozerConfigFile) {
        this.camelConfigFile = dozerConfigFile;
        if (dozerConfigFile != null) {
            this.camelFilePath = this.camelConfigFile.getProjectRelativePath().toPortableString();
        }
    }

    public CamelConfigBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(CamelConfigBuilder builder) {
        this.builder = builder;
    }

    public String getTransformID() {
        return transformID;
    }

    public void setTransformID(String transformID) {
        this.transformID = transformID;
    }

    public String getCamelFilePath() {
        return camelFilePath;
    }

    public void setCamelFilePath(String path) {
        this.camelFilePath = path;
    }

    private void doStatusUpdate() {
        if (fContainerStatus.getMessage() != null && fContainerStatus.getMessage().endsWith("does not exist.") //$NON-NLS-1$
            && getPackageFragmentRootText().endsWith(TEST_SOURCE_FOLDER)) {
            // override this particular case, since we'll create it
            fContainerStatus = new StatusInfo(NONE, null);
        }

        boolean defaultPkg = fPackageStatus.getCode() == StatusInfo.ERROR && (fPackageStatus.getMessage() == null
            				 || fPackageStatus.getMessage().trim().isEmpty());
        if (!defaultPkg) {
        	defaultPkg = fPackageStatus.getCode() == StatusInfo.WARNING && fPackageStatus.getMessage() != null
                		 && fPackageStatus.getMessage().contains("default package is discouraged"); //$NON-NLS-1$
        }
        if (defaultPkg) {
            // override this particular case, since the default package is ok, though not great
            fPackageStatus = new StatusInfo(NONE, null);
        }

        // all used component status
        IStatus[] status = new IStatus[] {
            fContainerStatus,
            fPackageStatus,
            fTypeNameStatus,
            camelFileSelectedStatus,
            camelEndpointSelectedStatus
        };

        // the mode severe status will be displayed and the OK button enabled/disabled.
        updateStatus(status);

        IStatus currStatus = StatusUtil.getMostSevere(status);
        setPageComplete(currStatus.isOK());
    }

    /*
     * @see NewContainerWizardPage#handleFieldChanged
     */
    @Override
    protected void handleFieldChanged(String fieldName) {
        super.handleFieldChanged(fieldName);
        doStatusUpdate();
    }

    /**
     * The wizard owning this page is responsible for calling this method with the
     * current selection. The selection is used to initialize the fields of the wizard
     * page.
     *
     * @param selection used to initialize the fields
     */
    public void init(IStructuredSelection selection) {
        IJavaElement jelem = getInitialJavaElement(selection);
        initContainerPage(jelem);
        initTypePage(jelem);
        setTypeName("TransformationTest", true); //$NON-NLS-1$
        doStatusUpdate();
    }
}
