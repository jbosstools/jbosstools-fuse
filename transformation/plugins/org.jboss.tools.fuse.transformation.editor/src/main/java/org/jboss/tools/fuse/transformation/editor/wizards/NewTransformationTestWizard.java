/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others. 
 * All rights reserved. This program and the accompanying materials are 
 * made available under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.wizards;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.jboss.mapper.camel.CamelConfigBuilder;
import org.jboss.mapper.test.TestGenerator;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.util.CamelConfigurationHelper;
import org.jboss.tools.fuse.transformation.editor.internal.util.JavaUtil;
import org.jboss.tools.fuse.transformation.editor.internal.wizards.TransformTestWizardPage;

/**
 *
 */
public class NewTransformationTestWizard extends Wizard implements INewWizard {

    static final String DEFAULT_DOZER_CONFIG_FILE_NAME = "dozerBeanMapping.xml";

    IProject project;
    IJavaProject javaProject;
    IFile camelConfigFile;
    CamelConfigBuilder builder;
    String transformID = null;
    String packageName = null;
    String className = null;
    
    TransformTestWizardPage _page;

    /**
     *
     */
    public NewTransformationTestWizard() {
        _page = new TransformTestWizardPage();
        addPage(_page);
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
            camelConfigFile = (IFile) selectedObject;
            project = camelConfigFile.getProject();
        } else if (selectedObject != null && selectedObject instanceof IProject) {
            project = (IProject) selectedObject;
            IResource findCamelContext =
                    project.findMember("src/main/resources/META-INF/spring/camel-context.xml");
            if (findCamelContext != null) {
                camelConfigFile = (IFile) findCamelContext;
            } else {
                Activator.error(new Throwable("Can't find camel context file."));
            }
        }
        if (project != null) {
            _page.setProject(project);
            javaProject = JavaCore.create(project);
        }
        if (camelConfigFile != null) {
            _page.setCamelConfigFile(camelConfigFile);
            File file = new File(camelConfigFile.getLocationURI());
            try {
                builder = CamelConfigurationHelper.load(file).getConfigBuilder();
                _page.setBuilder(builder);
            } catch (Exception e) {
                Activator.error(e);
            }
        }
        if (javaProject != null) {
            _page.setJavaProject(javaProject);
            IJavaElement element = JavaUtil.getInitialPackageForProject(javaProject);
            if (element != null) {
                packageName = element.getElementName();
                _page.setPackageName(packageName);
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
        IFolder folder = javaProject.getProject().getFolder("src/test/java");
        if (!JavaUtil.findFolderOnProjectClasspath(javaProject, folder)) {
            JavaUtil.addFolderToProjectClasspath(javaProject, folder);
        }
        File targetPath = new File(folder.getRawLocation().toPortableString());
        try {
            TestGenerator.createTransformTest(_page.getTransformID(),
                    _page.getPackageName(),
                    _page.getClassName(),
                    targetPath);
            project.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
            return true;
        } catch (Exception e) {
            Activator.error(e);
        }

        return false;
    }

}
