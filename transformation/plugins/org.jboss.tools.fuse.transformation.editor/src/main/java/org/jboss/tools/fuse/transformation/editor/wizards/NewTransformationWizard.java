/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.editor.wizards;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLClassLoader;
import java.text.StringCharacterIterator;
import java.util.Iterator;

import org.apache.camel.model.DataFormatDefinition;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.mapper.MapperConfiguration;
import org.jboss.mapper.camel.CamelEndpoint;
import org.jboss.mapper.dozer.DozerMapperConfiguration;
import org.jboss.mapper.model.json.JsonModelGenerator;
import org.jboss.mapper.model.xml.XmlModelGenerator;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.DozerConfigContentTypeDescriber;
import org.jboss.tools.fuse.transformation.editor.internal.util.JavaUtil;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;
import org.jboss.tools.fuse.transformation.editor.internal.wizards.JSONPage;
import org.jboss.tools.fuse.transformation.editor.internal.wizards.JavaPage;
import org.jboss.tools.fuse.transformation.editor.internal.wizards.Model;
import org.jboss.tools.fuse.transformation.editor.internal.wizards.ModelType;
import org.jboss.tools.fuse.transformation.editor.internal.wizards.OtherPage;
import org.jboss.tools.fuse.transformation.editor.internal.wizards.StartPage;
import org.jboss.tools.fuse.transformation.editor.internal.wizards.XMLPage;
import org.jboss.tools.fuse.transformation.editor.internal.wizards.XformWizardPage;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;

/**
 * @author brianf
 *
 */
@SuppressWarnings("restriction")
public class NewTransformationWizard extends Wizard implements INewWizard {

    private static final String JAVA_PATH = Util.MAIN_PATH + "java/";
    public static final String CAMEL_CONFIG_PATH = Util.RESOURCES_PATH
            + "META-INF/spring/camel-context.xml";
    private static final String OBJECT_FACTORY_NAME = "ObjectFactory";

    private Model uiModel = new Model();
    private DataFormatDefinition sourceFormat;
    private DataFormatDefinition targetFormat;
    private CamelEndpoint endpoint;
    private boolean saveCamelConfig = true;
    
    public StartPage start;
    public JavaPage javaSource;
    public JavaPage javaTarget;
    public XMLPage xmlSource;
    public XMLPage xmlTarget;
    public JSONPage jsonSource;
    public JSONPage jsonTarget;
    public OtherPage otherSource;
    public OtherPage otherTarget;
    public URLClassLoader loader;

    @Override
    public boolean performFinish() {
        // Save transformation file
        final IFile file =
                uiModel.getProject().getFile(Util.RESOURCES_PATH + uiModel.getFilePath());
        if (file.exists()
                && !MessageDialog.openConfirm(getShell(),
                        "Confirm",
                        "Overwrite existing transformation file (\"" + file.getFullPath() + "\")?")) {
            return false;
        }
        final MapperConfiguration dozerConfigBuilder = DozerMapperConfiguration.newConfig();
        final File newFile = new File(file.getLocationURI());
        if (!newFile.getParentFile().exists()) {
            newFile.getParentFile().mkdirs();
        }
        try (FileOutputStream configStream = new FileOutputStream(newFile)) {
            if (uiModel.getSourceFilePath() != null) {
                // Generate models
                final String sourceClassName =
                        generateModel(uiModel.getSourceFilePath(), uiModel.getSourceType());
                final String targetClassName =
                        generateModel(uiModel.getTargetFilePath(), uiModel.getTargetType());
                // Update Camel config
                final IPath resourcesPath =
                        uiModel.getProject().getFolder(Util.RESOURCES_PATH).getFullPath();
                
                sourceFormat = uiModel.camelConfigBuilder.createDataFormat(
                        uiModel.getSourceType().transformType, sourceClassName);
                targetFormat = uiModel.camelConfigBuilder.createDataFormat(
                        uiModel.getTargetType().transformType, targetClassName);
                endpoint = uiModel.camelConfigBuilder.createEndpoint(uiModel.getId(),
                                                 file.getFullPath().makeRelativeTo(resourcesPath).toString(),
                                                 sourceClassName, targetClassName,
                                                 sourceFormat, targetFormat);
                if (saveCamelConfig) {
                    try (FileOutputStream camelConfigStream =
                            new FileOutputStream(new File(uiModel.getProject()
                                    .getFile(Util.RESOURCES_PATH + uiModel.getCamelFilePath())
                                    .getLocationURI()))) {
                    
                        uiModel.camelConfigBuilder.saveConfig(camelConfigStream);
                    } catch (final Exception e) {
                        Activator.error(e);
                        return false;
                    }
                }
                dozerConfigBuilder.addClassMapping(sourceClassName, targetClassName);
            }
            dozerConfigBuilder.saveConfig(configStream);
            uiModel.getProject().refreshLocal(IProject.DEPTH_INFINITE, null);
            // Ensure build of Java classes has completed
            try {
                Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
            } catch (final InterruptedException ignored) {
            }

            // Open mapping editor
            final IEditorDescriptor desc =
                    PlatformUI
                            .getWorkbench()
                            .getEditorRegistry()
                            .getEditors(
                                    file.getName(),
                                    Platform.getContentTypeManager().getContentType(
                                            DozerConfigContentTypeDescriber.ID))[0];
            uiModel.getProject().refreshLocal(IProject.DEPTH_INFINITE, null);
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .openEditor(new FileEditorInput(file),
                            desc.getId());
        } catch (final Exception e) {
            Activator.error(e);
            return false;
        }
        return true;
    }

    @Override
    public void addPages() {
        if (start == null) {
            start = new StartPage(uiModel);
        }
        addPage(start);
        if (javaSource == null) {
            javaSource = new JavaPage("SourceJava", uiModel, true);
        }
        addPage(javaSource);
        if (javaTarget == null) {
            javaTarget = new JavaPage("TargetJava", uiModel, false);
        }
        addPage(javaTarget);
        if (xmlSource == null) {
            xmlSource = new XMLPage("SourceXml", uiModel, true);
        }
        addPage(xmlSource);
        if (xmlTarget == null) {
            xmlTarget = new XMLPage("TargetXml", uiModel, false);
        }
        addPage(xmlTarget);
        if (jsonSource == null) {
            jsonSource = new JSONPage("Sourcejson", uiModel, true);
        }
        addPage(jsonSource);
        if (jsonTarget == null) {
            jsonTarget = new JSONPage("Targetjson", uiModel, false);
        }
        addPage(jsonTarget);
        if (otherSource == null) {
            otherSource = new OtherPage("Sourceother", uiModel, true);
        }
        addPage(otherSource);
        if (otherTarget == null) {
            otherTarget = new OtherPage("Targetother", uiModel, false);
        }
        addPage(otherTarget);
    }

    @Override
    public String getWindowTitle() {
        return "New Fuse Transformation";
    }
    
    public void setSelectedProject(IProject project) {
        uiModel.setProject(project);
        final IJavaProject javaProject = JavaCore.create(project);
        try {
            loader =
                    (URLClassLoader) JavaUtil.getProjectClassLoader(javaProject,
                            getClass().getClassLoader());
        } catch (final Exception e) {
            // eat exception
            e.printStackTrace();
        }
    }
    
    public URLClassLoader getLoader() {
        return this.loader;
    }

    public void setCamelFilePath(String path) {
        uiModel.setCamelFilePath(path);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        for (final Iterator<IProject> iter = uiModel.projects.iterator(); iter.hasNext();) {
            if (iter.next().findMember(CAMEL_CONFIG_PATH) == null) {
                iter.remove();
            }
        }
        if (uiModel.projects.size() == 1) {
            uiModel.setProject(uiModel.projects.get(0));
        } else {
            final IStructuredSelection resourceSelection =
                    (IStructuredSelection) workbench.getActiveWorkbenchWindow()
                            .getSelectionService()
                            .getSelection("org.eclipse.ui.navigator.ProjectExplorer");
            if (resourceSelection == null || resourceSelection.size() != 1) {
                return;
            }
            final IProject project =
                    ((IResource) ((IAdaptable) resourceSelection.getFirstElement())
                            .getAdapter(IResource.class)).getProject();
            if (uiModel.projects.contains(project)) {
                uiModel.setProject(project);
            }
        }
    }

    private void resetPage(IWizardPage page) {
        if (page != null && page instanceof XformWizardPage) {
            ((XformWizardPage) page).resetFinish();
        }
    }
    
    public void resetSourceAndTargetPages() {
        resetPage(javaSource);
        resetPage(javaTarget);
        resetPage(xmlSource);
        resetPage(xmlTarget);
        resetPage(jsonSource);
        resetPage(jsonTarget);
        resetPage(otherSource);
        resetPage(otherTarget);
    }
    
    @Override
    public boolean canFinish() {
        if (start != null && start.getSourcePage() != null && start.getTargetPage() != null) {
            if (start.getSourcePage().isPageComplete() &&
                    start.getTargetPage().isPageComplete()) {
                return true;
            }
        } else {
            return false;
        }
        return super.canFinish();
    }

    private String generateModel(final String filePath,
            final ModelType type) throws Exception {
        // Build class name from file name
        final StringBuilder className = new StringBuilder();
        final StringCharacterIterator iter =
                new StringCharacterIterator(filePath.substring(filePath.lastIndexOf('/') + 1,
                        filePath.lastIndexOf('.')));
        boolean wordStart = true;
        for (char chr = iter.first(); chr != StringCharacterIterator.DONE; chr = iter.next()) {
            if (className.length() == 0) {
                if (Character.isJavaIdentifierStart(chr)) {
                    className.append(wordStart ? Character.toUpperCase(chr) : chr);
                    wordStart = false;
                }
            } else if (Character.isJavaIdentifierPart(chr)) {
                className.append(wordStart ? Character.toUpperCase(chr) : chr);
                wordStart = false;
            } else {
                wordStart = true;
            }
        }
        // Build package name from class name
        int sequencer = 1;
        String pkgName = className.toString();
        while (uiModel.getProject().exists(new Path(JAVA_PATH + pkgName))) {
            pkgName = className.toString() + sequencer++;
        }
        pkgName = pkgName.toLowerCase();
        // Generate model
        final File targetClassesFolder =
                new File(uiModel.getProject().getFolder(JAVA_PATH).getLocationURI());
        switch (type) {
            case CLASS: {
                final IJavaProject javaProject = JavaCore.create(uiModel.getProject());
                IType pkg = javaProject.findType(filePath, new NullProgressMonitor());
                if (pkg != null) {
                    return pkg.getFullyQualifiedName();
                }
                return null;
            }
            case JAVA: {
                final IJavaProject javaProject = JavaCore.create(uiModel.getProject());
                IType pkg = javaProject.findType(filePath, new NullProgressMonitor());
                if (pkg != null) {
                    return pkg.getFullyQualifiedName();
                }
                return null;
            }
            case JSON: {
                final JsonModelGenerator generator = new JsonModelGenerator();
                generator.generateFromInstance(className.toString(),
                        pkgName,
                        uiModel.getProject().findMember(filePath).getLocationURI().toURL(),
                        targetClassesFolder);
                return pkgName + "." + className;
            }
            case JSON_SCHEMA: {
                final JsonModelGenerator generator = new JsonModelGenerator();
                generator.generateFromSchema(className.toString(),
                        pkgName,
                        uiModel.getProject().findMember(filePath).getLocationURI().toURL(),
                        targetClassesFolder);
                return pkgName + "." + className;
            }
            case XSD: {
                final XmlModelGenerator generator = new XmlModelGenerator();
                final JCodeModel model =
                        generator
                                .generateFromSchema(
                                        new File(uiModel.getProject().findMember(filePath)
                                                .getLocationURI()),
                                        pkgName,
                                        targetClassesFolder);
                final String modelClass = selectModelClass(model);
                if (modelClass != null) {
                    return modelClass;
                }
                return null;
            }
            case XML: {
                final XmlModelGenerator generator = new XmlModelGenerator();
                final File schemaPath =
                        new File(uiModel.getProject().getFile(filePath + ".xsd").getLocationURI());
                final JCodeModel model =
                        generator
                                .generateFromInstance(
                                        new File(uiModel.getProject().findMember(filePath)
                                                .getLocationURI()),
                                        schemaPath,
                                        pkgName,
                                        targetClassesFolder);
                final String modelClass = selectModelClass(model);
                if (modelClass != null) {
                    return modelClass;
                }
                return null;
            }
            default:
                return null;
        }
    }

    private String selectModelClass(final JCodeModel model) {
        for (final Iterator<JPackage> pkgIter = model.packages(); pkgIter.hasNext();) {
            final JPackage pkg = pkgIter.next();
            for (final Iterator<JDefinedClass> classIter = pkg.classes(); classIter.hasNext();) {
                // TODO this only works when a single top-level class exists;
                // fix after issue #33 is fixed
                final JDefinedClass definedClass = classIter.next();
                if (OBJECT_FACTORY_NAME.equals(definedClass.name())) {
                    continue;
                }
                return definedClass.fullName();
            }
        }
        return null;
    }

    public void setSaveCamelConfig(boolean saveCamelConfig) {
        this.saveCamelConfig = saveCamelConfig;
    }
    
    public DataFormatDefinition getSourceFormat() {
        return sourceFormat;
    }

    public DataFormatDefinition getTargetFormat() {
        return targetFormat;
    }
    
    public CamelEndpoint getEndpoint() {
        return endpoint;
    }

    public Model getModel() {
        return uiModel;
    }
}
