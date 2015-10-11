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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.dataformat.DataFormatsDefinition;
import org.apache.camel.spring.CamelContextFactoryBean;
import org.apache.camel.spring.CamelEndpointFactoryBean;
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
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.catalog.Dependency;
import org.jboss.tools.fuse.transformation.MapperConfiguration;
import org.jboss.tools.fuse.transformation.camel.CamelConfigBuilder;
import org.jboss.tools.fuse.transformation.camel.CamelConfigBuilder.MarshalType;
import org.jboss.tools.fuse.transformation.camel.CamelEndpoint;
import org.jboss.tools.fuse.transformation.dozer.DozerMapperConfiguration;
import org.jboss.tools.fuse.transformation.editor.Activator;
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
import org.jboss.tools.fuse.transformation.model.json.JsonModelGenerator;
import org.jboss.tools.fuse.transformation.model.xml.XmlModelGenerator;

/**
 * @author brianf
 *
 */
public class NewTransformationWizard extends Wizard implements INewWizard {

    public static final String CAMEL_CONFIG_PATH = Util.RESOURCES_PATH + "META-INF/spring/camel-context.xml";
    private static final String OBJECT_FACTORY_NAME = "ObjectFactory";

    private Model uiModel = new Model();
    private DataFormatDefinition sourceFormat;
    private DataFormatDefinition targetFormat;
    private CamelEndpoint endpoint;
    private boolean saveCamelConfig = true;
    private Endpoint routeEndpoint;

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
        final IFile file = uiModel.getProject().getFile(Util.RESOURCES_PATH + uiModel.getFilePath());
        if (file.exists()
                && !MessageDialog.openConfirm(getShell(), "Confirm", "Overwrite existing transformation file (\""
                        + file.getFullPath() + "\")?")) {
            return false;
        }
        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) {
                final MapperConfiguration dozerConfigBuilder = DozerMapperConfiguration.newConfig();
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

                        CamelConfigBuilder configBuilder = uiModel.camelConfig.getConfigBuilder();
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
                                File camelFile = new File(uiModel.getProject()
                                        .getFile(Util.RESOURCES_PATH + uiModel.getCamelFilePath()).getLocationURI());
                                uiModel.camelConfig.save(camelFile);
                            } catch (final Exception e) {
                                throw e;
                            }
                        }
                        dozerConfigBuilder.addClassMapping(sourceClassName, targetClassName);
                    }
                    dozerConfigBuilder.saveConfig(configStream);

                    if (!saveCamelConfig) {
                        // now update the camel config if we didn't already

                        RouteContainer routeContainer = org.fusesource.ide.camel.editor.Activator.getDiagramEditor()
                                .getModel();
                        CamelContextFactoryBean camelContext = routeContainer.getModel().getContextElement();

                        // Wizard completed successfully; create the necessary
                        // config
                        addCamelContextEndpoint(camelContext, endpoint.asSpringEndpoint());
                        if (sourceFormat != null) {
                            addDataFormat(camelContext, sourceFormat);
                        }
                        if (targetFormat != null) {
                            addDataFormat(camelContext, targetFormat);
                        }
                        // Create the route endpoint
                        routeEndpoint = new Endpoint("ref:" + endpoint.getId());
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

    public Endpoint getRouteEndpoint() {
        return routeEndpoint;
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
                    .getSelectionService().getSelection("org.eclipse.ui.navigator.ProjectExplorer");
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
        resetPage(otherSource);
        resetPage(otherTarget);
    }

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
            return pkgName + "." + className;
        }
        case JSON_SCHEMA: {
            final JsonModelGenerator generator = new JsonModelGenerator();
            generator.generateFromSchema(className.toString(), pkgName, uiModel.getProject().findMember(filePath)
                    .getLocationURI().toURL(), targetClassesFolder);
            return pkgName + "." + className;
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
            final File schemaPath = new File(uiModel.getProject().getFile(filePath + ".xsd").getLocationURI());
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

    private void addCamelContextEndpoint(CamelContextFactoryBean context, CamelEndpointFactoryBean endpoint) {
        List<CamelEndpointFactoryBean> endpoints = context.getEndpoints();
        if (endpoints == null) {
            endpoints = new LinkedList<>();
        }
        endpoints.add(endpoint);
        context.setEndpoints(endpoints);
    }

    private void addDataFormat(CamelContextFactoryBean context, DataFormatDefinition dataFormat) {
        DataFormatsDefinition dataFormats = context.getDataFormats();
        // create the parent element if it doesn't exist
        if (dataFormats == null) {
            dataFormats = new DataFormatsDefinition();
        }

        // add to the list of formats
        if (dataFormats.getDataFormats() == null) {
            dataFormats.setDataFormats(new LinkedList<DataFormatDefinition>());
        }

        // only add the data format if it's not present
        boolean exists = false;
        for (DataFormatDefinition dfd : dataFormats.getDataFormats()) {
            if (dataFormat.getId().equals(dfd.getId())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            dataFormats.getDataFormats().add(dataFormat);
            context.setDataFormats(dataFormats);
        }
    }

    private Dependency createDependency(String groupId, String artifactId, String version) {
        Dependency dep = new Dependency();
        dep.setGroupId(groupId);
        dep.setArtifactId(artifactId);
        dep.setVersion(version);
        return dep;
    }

    private void addCamelDozerDependency() {
        Dependency dep = createDependency("org.apache.camel", "camel-dozer", org.fusesource.ide.camel.editor.Activator
                .getDefault().getCamelVersion());
        List<Dependency> deps = new ArrayList<>();
        deps.add(dep);
        try {
            Util.updateMavenDependencies(deps, uiModel.getProject());
        } catch (CoreException e) {
            org.fusesource.ide.camel.editor.Activator.getLogger().error(e);
        }
    }

    private void addDataFormatDefinitionDependency(DataFormatDefinition dataFormat) {
        Dependency dep = null;

        if (dataFormat != null && dataFormat.getDataFormatName() != null) {
            if (dataFormat.getDataFormatName().equalsIgnoreCase("json-jackson")) {
                dep = createDependency("org.apache.camel", "camel-jackson", org.fusesource.ide.camel.editor.Activator
                        .getDefault().getCamelVersion());
            } else if (dataFormat.getDataFormatName().equalsIgnoreCase("jaxb")) {
                dep = createDependency("org.apache.camel", "camel-jaxb", org.fusesource.ide.camel.editor.Activator
                        .getDefault().getCamelVersion());
            }
            if (dep != null) {
                List<Dependency> deps = new ArrayList<>();
                deps.add(dep);
                try {
                    Util.updateMavenDependencies(deps, uiModel.getProject());
                } catch (CoreException e) {
                    org.fusesource.ide.camel.editor.Activator.getLogger().error(e);
                }
            }
        }
    }
}
