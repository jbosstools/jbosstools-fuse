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
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.jboss.tools.fuse.transformation.core.MapperConfiguration;
import org.jboss.tools.fuse.transformation.core.camel.CamelConfigBuilder;
import org.jboss.tools.fuse.transformation.core.camel.CamelConfigBuilder.MarshalType;
import org.jboss.tools.fuse.transformation.core.dozer.DozerMapperConfiguration;
import org.jboss.tools.fuse.transformation.core.model.json.JsonModelGenerator;
import org.jboss.tools.fuse.transformation.core.model.xml.XmlModelGenerator;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.internal.util.CamelConfigurationHelper;
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
import org.jboss.tools.fuse.transformation.extensions.DozerConfigContentTypeDescriber;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;

/**
 * @author brianf
 *
 */
public class NewTransformationWizard extends Wizard implements INewWizard {

    public static final String CAMEL_CONFIG_PATH = Util.RESOURCES_PATH + "META-INF/spring/camel-context.xml"; //$NON-NLS-1$
    private static final String OBJECT_FACTORY_NAME = "ObjectFactory"; //$NON-NLS-1$

    private Model uiModel = new Model();
    private AbstractCamelModelElement sourceFormat;
    private AbstractCamelModelElement targetFormat;
    private AbstractCamelModelElement endpoint;
    private boolean saveCamelConfig = true;
    private AbstractCamelModelElement routeEndpoint;

    public StartPage start;
    public JavaPage javaSource;
    public JavaPage javaTarget;
    public XMLPage xmlSource;
    public XMLPage xmlTarget;
    public JSONPage jsonSource;
    public JSONPage jsonTarget;
//    public Hl7Page hl7Source;
//    public Hl7Page hl7Target;
    public OtherPage otherSource;
    public OtherPage otherTarget;
    public URLClassLoader loader;

    @Override
    public boolean performFinish() {
        // Save transformation file
        final IFile file = uiModel.getProject().getFile(Util.RESOURCES_PATH + uiModel.getFilePath());
        if (file.exists()
 && !MessageDialog.openConfirm(getShell(), Messages.NewTransformationWizard_messageDialogTitleConfirm,
				Messages.bind(Messages.NewTransformationWizard_confirmationDialogmessage, file.getFullPath()))) {
            return false;
        }
        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) {
				// TODO: check if it is not the Project classloader that we need
				final MapperConfiguration dozerConfigBuilder = DozerMapperConfiguration.newConfig(this.getClass().getClassLoader());
                final File newFile = new File(file.getLocationURI());
                if (!newFile.getParentFile().exists()) {
                    newFile.getParentFile().mkdirs();
                }
                try (FileOutputStream configStream = new FileOutputStream(newFile)) {
                    if (uiModel.getSourceFilePath() != null) {
                        // Generate models
                        final String sourceClassName = generateModel(uiModel.getSourceFilePath(),
                                uiModel.getSourceType(), true);
                        final String targetClassName = generateModel(uiModel.getTargetFilePath(),
                                uiModel.getTargetType(), false);
                        // Update Camel config
                        final IPath resourcesPath = uiModel.getProject().getFolder(Util.RESOURCES_PATH).getFullPath();
                        final IFile camelIFile = uiModel.getCamelIFile();

                        CamelConfigBuilder configBuilder = CamelConfigurationHelper.getConfigBuilder(camelIFile.getRawLocation().toFile());
                        if (ModelType.OTHER.equals(uiModel.getSourceType())) {
                            sourceFormat = configBuilder.getDataFormat(uiModel.getSourceDataFormatid());
                        } else {
                            sourceFormat = configBuilder.createDataFormat(uiModel.getSourceType().transformType,
                                    sourceClassName, MarshalType.UNMARSHALLER);
                        }
                        if (ModelType.OTHER.equals(uiModel.getTargetType())) {
                            targetFormat = configBuilder.getDataFormat(uiModel.getTargetDataFormatid());
                        } else {
                            targetFormat = configBuilder.createDataFormat(uiModel.getTargetType().transformType,
                                    targetClassName, MarshalType.MARSHALLER);
                        }
                        endpoint = configBuilder.createEndpoint(uiModel.getId(),
                                file.getFullPath().makeRelativeTo(resourcesPath).toString(), sourceClassName,
                                targetClassName, sourceFormat, targetFormat);

                        // make sure we add our maven dependencies where needed
                        addCamelDozerDependency();
                        addDataFormatDefinitionDependency(sourceFormat);
                        addDataFormatDefinitionDependency(targetFormat);

                        if (saveCamelConfig) {
                            try {
                                uiModel.camelConfig.save();
                            } catch (final Exception e) {
                                throw e;
                            }
                        }
                        dozerConfigBuilder.addClassMapping(sourceClassName, targetClassName);
                    }
                    dozerConfigBuilder.saveConfig(configStream);

                    if (!saveCamelConfig) {
                        // now update the camel config if we didn't already
                        CamelContextElement camelContext = CamelUtils.getDiagramEditor().getModel().getCamelContext();

                        // Wizard completed successfully; create the necessary
                        // config
                        addCamelContextEndpoint(camelContext, endpoint);
                        if (sourceFormat != null) {
                            addDataFormat(camelContext, sourceFormat);
                        }
                        if (targetFormat != null) {
                            addDataFormat(camelContext, targetFormat);
                        }
                        // Create the route endpoint
                        routeEndpoint = new org.fusesource.ide.camel.model.service.core.model.CamelEndpoint("ref:" + endpoint.getId()); //$NON-NLS-1$
                    }

                    uiModel.getProject().refreshLocal(IProject.DEPTH_INFINITE, null);
                    // Ensure build of Java classes has completed
                    try {
                        Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
                    } catch (final InterruptedException ignored) {
                    }

                    // Open mapping editor
                    final IEditorDescriptor desc = PlatformUI
                            .getWorkbench()
                            .getEditorRegistry()
                            .getEditors(file.getName(),
                                    Platform.getContentTypeManager().getContentType(DozerConfigContentTypeDescriber.ID))[0];
                    uiModel.getProject().refreshLocal(IProject.DEPTH_INFINITE, null);
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                            .openEditor(new FileEditorInput(file), desc.getId());
                } catch (final Exception e) {
                    Activator.error(e);
                }
            }
        };

        try {
            getContainer().run(false, false, op);
            return true;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.fillInStackTrace();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return false;
    }

    public AbstractCamelModelElement getRouteEndpoint() {
        return routeEndpoint;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        if (start == null) {
            start = new StartPage(uiModel);
        }
        addPage(start);
        if (javaSource == null) {
            javaSource = new JavaPage("SourceJava", uiModel, true); //$NON-NLS-1$
        }
        addPage(javaSource);
        if (javaTarget == null) {
            javaTarget = new JavaPage("TargetJava", uiModel, false); //$NON-NLS-1$
        }
        addPage(javaTarget);
        if (xmlSource == null) {
            xmlSource = new XMLPage("SourceXml", uiModel, true); //$NON-NLS-1$
        }
        addPage(xmlSource);
        if (xmlTarget == null) {
            xmlTarget = new XMLPage("TargetXml", uiModel, false); //$NON-NLS-1$
        }
        addPage(xmlTarget);
        if (jsonSource == null) {
            jsonSource = new JSONPage("Sourcejson", uiModel, true); //$NON-NLS-1$
        }
        addPage(jsonSource);
        if (jsonTarget == null) {
            jsonTarget = new JSONPage("Targetjson", uiModel, false); //$NON-NLS-1$
        }
        addPage(jsonTarget);
//        if (hl7Source == null) {
//            hl7Source = new Hl7Page("SourceHl7", uiModel, true);
//        }
//        addPage(hl7Source);
//        if (hl7Target == null) {
//            hl7Target = new Hl7Page("TargetHl7", uiModel, false);
//        }
//        addPage(hl7Target);
        if (otherSource == null) {
            otherSource = new OtherPage("Sourceother", uiModel, true); //$NON-NLS-1$
        }
        addPage(otherSource);
        if (otherTarget == null) {
            otherTarget = new OtherPage("Targetother", uiModel, false); //$NON-NLS-1$
        }
        addPage(otherTarget);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#getWindowTitle()
     */
    @Override
    public String getWindowTitle() {
        return Messages.NewTransformationWizard_windowTtile;
    }

    public void setSelectedProject(IProject project) {
        uiModel.setProject(project);
        final IJavaProject javaProject = JavaCore.create(project);
        try {
            loader = (URLClassLoader) JavaUtil.getProjectClassLoader(javaProject, getClass().getClassLoader());
        } catch (final Exception e) {
            // eat exception
            e.printStackTrace();
        }
    }

    public URLClassLoader getLoader() {
        if (this.loader == null && uiModel.getProject() != null) {
            setSelectedProject(uiModel.getProject());
        }
        return this.loader;
    }

    public void setCamelFilePath(String path) {
        uiModel.setCamelFilePath(path);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        for (final Iterator<IProject> iter = uiModel.projects.iterator(); iter.hasNext();) {
            if (iter.next().findMember(CAMEL_CONFIG_PATH) == null) {
                iter.remove();
            }
        }
        // we really want to respond to the initial selection when the wizard
        // was launched. if there's a project in there, we want to use it to
        // pre- populate the project selection. This only seems to be an issue
        // on Luna for FUSETOOLS-1443
        if (selection != null) {
            IStructuredSelection resourceSelection = selection;
            if (resourceSelection.getFirstElement() instanceof IProject) {
                uiModel.projects.clear();
                uiModel.projects.add((IProject) resourceSelection.getFirstElement());
            } else if (resourceSelection.getFirstElement() instanceof IJavaProject) {
                IJavaProject jProject = (IJavaProject) resourceSelection.getFirstElement();
                uiModel.projects.clear();
                uiModel.projects.add(jProject.getProject());
            }
        }

        if (uiModel.projects.size() == 1) {
            uiModel.setProject(uiModel.projects.get(0));
        } else {
            final IStructuredSelection resourceSelection = (IStructuredSelection) workbench.getActiveWorkbenchWindow()
                    .getSelectionService().getSelection("org.eclipse.ui.navigator.ProjectExplorer"); //$NON-NLS-1$
            if (resourceSelection == null || resourceSelection.size() != 1) {
                return;
            }
            final IProject project = ((IAdaptable)resourceSelection.getFirstElement()).getAdapter(IResource.class).getProject();
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
//        resetPage(hl7Source);
//        resetPage(hl7Target);
        resetPage(otherSource);
        resetPage(otherTarget);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     */
    @Override
    public boolean canFinish() {
        if (start != null && start.getSourcePage() != null && start.getTargetPage() != null) {
            ((XformWizardPage) start.getSourcePage()).notifyListeners();
            ((XformWizardPage) start.getTargetPage()).notifyListeners();
            if (start.isPageComplete() && start.getSourcePage().isPageComplete()
                    && start.getTargetPage().isPageComplete()) {
                return true;
            }
        } else {
            return false;
        }
        return super.canFinish();
    }

    private String generateModel(final String filePath, final ModelType type, boolean isSource) throws Exception {
        // Build class name from file name
        final StringBuilder className = new StringBuilder();
        final StringCharacterIterator iter = new StringCharacterIterator(filePath.substring(
                filePath.lastIndexOf('/') + 1, filePath.lastIndexOf('.')));
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
        while (uiModel.getProject().exists(new Path(Util.JAVA_PATH + pkgName))) {
            pkgName = className.toString() + sequencer++;
        }
        pkgName = pkgName.toLowerCase();
        // Generate model
        final File targetClassesFolder = new File(uiModel.getProject().getFolder(Util.JAVA_PATH).getLocationURI());
        switch (type) {
        case OTHER:
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
            generator.generateFromInstance(className.toString(), pkgName, uiModel.getProject().findMember(filePath)
                    .getLocationURI().toURL(), targetClassesFolder);
            return pkgName + "." + className; //$NON-NLS-1$
        }
        case JSON_SCHEMA: {
            final JsonModelGenerator generator = new JsonModelGenerator();
            generator.generateFromSchema(className.toString(), pkgName, uiModel.getProject().findMember(filePath)
                    .getLocationURI().toURL(), targetClassesFolder);
            return pkgName + "." + className; //$NON-NLS-1$
        }
        case XSD: {
            final XmlModelGenerator generator = new XmlModelGenerator();
            final File schemaFile = new File(uiModel.getProject().findMember(filePath).getLocationURI());
            final JCodeModel model = generator.generateFromSchema(schemaFile, null, targetClassesFolder);
            String elementName = null;
            if (isSource) {
                elementName = uiModel.getSourceClassName();
            } else {
                elementName = uiModel.getTargetClassName();
            }
            String modelClass = null;
            Map<String, String> mappings = generator.elementToClassMapping(model);
            if (mappings != null && !mappings.isEmpty()) {
                modelClass = mappings.get(elementName);
            } else {
                modelClass = selectModelClass(model);
            }
            if (modelClass != null) {
                return modelClass;
            }
            return null;
        }
        case XML: {
            final XmlModelGenerator generator = new XmlModelGenerator();
            final File schemaPath = new File(uiModel.getProject().getFile(filePath + ".xsd").getLocationURI()); //$NON-NLS-1$
            final JCodeModel model = generator.generateFromInstance(new File(uiModel.getProject().findMember(filePath)
                    .getLocationURI()), schemaPath, null, targetClassesFolder);
            String elementName = null;
            if (isSource) {
                elementName = uiModel.getSourceClassName();
            } else {
                elementName = uiModel.getTargetClassName();
            }
            String modelClass = null;
            Map<String, String> mappings = generator.elementToClassMapping(model);
            if (mappings != null && !mappings.isEmpty()) {
                modelClass = mappings.get(elementName);
            } else {
                modelClass = selectModelClass(model);
            }
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

    public AbstractCamelModelElement getSourceFormat() {
        return sourceFormat;
    }

    public AbstractCamelModelElement getTargetFormat() {
        return targetFormat;
    }

    public AbstractCamelModelElement getEndpoint() {
        return endpoint;
    }

    public Model getModel() {
        return uiModel;
    }

    private void addCamelContextEndpoint(CamelContextElement context, AbstractCamelModelElement endpoint) {
        Map<String, AbstractCamelModelElement> endpoints = context.getEndpointDefinitions();
        if (endpoints == null) {
            endpoints = new HashMap<>();
            context.setEndpointDefinitions(endpoints);
        }
        endpoints.put(endpoint.getId(), endpoint);
    }

    private void addDataFormat(CamelContextElement context, AbstractCamelModelElement dataFormat) {
    	Map<String, AbstractCamelModelElement> dataFormats = context.getDataformats();
        if (dataFormats == null) {
        	dataFormats = new HashMap<>();
            context.setDataformats(dataFormats);
        }
        dataFormats.put(dataFormat.getId(), dataFormat);
    }

    private Dependency createDependency(String groupId, String artifactId, String version) {
        Dependency dep = new Dependency();
        dep.setGroupId(groupId);
        dep.setArtifactId(artifactId);
        dep.setVersion(version);
        return dep;
    }

    private void addCamelDozerDependency() {
        Dependency dep = createDependency("org.apache.camel", "camel-dozer", CamelUtils.getCurrentProjectCamelVersion()); //$NON-NLS-1$ //$NON-NLS-2$
        List<Dependency> deps = new ArrayList<>();
        deps.add(dep);
        try {
            Util.updateMavenDependencies(deps, uiModel.getProject());
        } catch (CoreException e) {
            Activator.log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
        }
    }

    private void addDataFormatDefinitionDependency(AbstractCamelModelElement dataFormat) {
        Dependency dep = null;
        String camelVersion = CamelUtils.getCurrentProjectCamelVersion();
        if (dataFormat != null && dataFormat.getNodeTypeId() != null) {
            if (dataFormat.getNodeTypeId().startsWith("json")) { //$NON-NLS-1$
                dep = createDependency("org.apache.camel", "camel-jackson", camelVersion); //$NON-NLS-1$ //$NON-NLS-2$
            } else if (dataFormat.getNodeTypeId().equalsIgnoreCase("jaxb")) { //$NON-NLS-1$
                dep = createDependency("org.apache.camel", "camel-jaxb", camelVersion); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (dep != null) {
                List<Dependency> deps = new ArrayList<>();
                deps.add(dep);
                try {
                    Util.updateMavenDependencies(deps, uiModel.getProject());
                } catch (CoreException e) {
                    Activator.log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
                }
            }
        }
    }
}
