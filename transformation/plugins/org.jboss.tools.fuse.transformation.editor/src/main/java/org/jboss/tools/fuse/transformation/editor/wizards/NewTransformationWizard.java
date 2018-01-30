/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
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
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.jboss.tools.fuse.transformation.core.MapperConfiguration;
import org.jboss.tools.fuse.transformation.core.camel.CamelConfigBuilder;
import org.jboss.tools.fuse.transformation.core.camel.CamelConfigBuilder.MarshalType;
import org.jboss.tools.fuse.transformation.core.dozer.DozerMapperConfiguration;
import org.jboss.tools.fuse.transformation.core.model.json.JsonModelGenerator;
import org.jboss.tools.fuse.transformation.core.model.xml.XmlModelGenerator;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;
import org.jboss.tools.fuse.transformation.editor.internal.util.ImportExportPackageUpdater;
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
 *
 */
public class NewTransformationWizard extends Wizard implements INewWizard {

    public static final String CAMEL_CONFIG_PATH = MavenUtils.RESOURCES_PATH + "META-INF/spring/camel-context.xml"; //$NON-NLS-1$
    private static final String OBJECT_FACTORY_NAME = "ObjectFactory"; //$NON-NLS-1$
    private static final String CAMEL_GROUP_ID = "org.apache.camel"; //$NON-NLS-1$

    private Model uiModel = new Model();
    private AbstractCamelModelElement sourceFormat;
    private AbstractCamelModelElement targetFormat;
    private AbstractCamelModelElement endpoint;
    private boolean saveCamelConfig = true;
    private AbstractCamelModelElement routeEndpoint;
    private URLClassLoader loader;

    private StartPage start;
    private JavaPage javaSource;
    private JavaPage javaTarget;
    private XMLPage xmlSource;
    private XMLPage xmlTarget;
    private JSONPage jsonSource;
    private JSONPage jsonTarget;
    private OtherPage otherSource;
    private OtherPage otherTarget;

    private void addCamelContextEndpoint(CamelContextElement context, AbstractCamelModelElement endpoint) {
        Map<String, AbstractCamelModelElement> endpoints = context.getEndpointDefinitions();
        if (endpoints == null) {
            endpoints = new HashMap<>();
            context.setEndpointDefinitions(endpoints);
        }
        endpoints.put(endpoint.getId(), endpoint);
    }

    private void addCamelDozerDependency(IProject project) {
        Dependency dep = createDependency(CAMEL_GROUP_ID, "camel-dozer", CamelUtils.getCurrentProjectCamelVersion()); //$NON-NLS-1$
        List<Dependency> deps = new ArrayList<>();
        deps.add(dep);
        new MavenUtils().updateMavenDependencies(deps, project);
    }

    private void addDataFormat(CamelContextElement context, AbstractCamelModelElement dataFormat) {
        Map<String, AbstractCamelModelElement> dataFormats = context.getDataformats();
        if (dataFormats == null) {
            dataFormats = new HashMap<>();
            context.setDataformats(dataFormats);
        }
        dataFormats.put(dataFormat.getId(), dataFormat);
    }

    private void addDataFormatDefinitionDependency(AbstractCamelModelElement dataFormat) {
        Dependency dep = null;
        String camelVersion = CamelUtils.getCurrentProjectCamelVersion();
        if (dataFormat != null && dataFormat.getNodeTypeId() != null) {
            if (dataFormat.getNodeTypeId().startsWith("json")) { //$NON-NLS-1$
                dep = createDependency(CAMEL_GROUP_ID, "camel-jackson", camelVersion); //$NON-NLS-1$
            } else if ("jaxb".equalsIgnoreCase(dataFormat.getNodeTypeId())) { //$NON-NLS-1$
                dep = createDependency(CAMEL_GROUP_ID, "camel-jaxb", camelVersion); //$NON-NLS-1$
            }
            if (dep != null) {
                List<Dependency> deps = new ArrayList<>();
                deps.add(dep);
                new MavenUtils().updateMavenDependencies(deps, dataFormat.getCamelFile().getResource().getProject());
            }
        }
    }

    /**
     * {@inheritDoc}
     *
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
        if (otherSource == null) {
            otherSource = new OtherPage("Sourceother", uiModel, true); //$NON-NLS-1$
        }
        addPage(otherSource);
        if (otherTarget == null) {
            otherTarget = new OtherPage("Targetother", uiModel, false); //$NON-NLS-1$
        }
        addPage(otherTarget);
    }

    public JavaPage javaSourcePage() {
        return javaSource;
    }

    public JavaPage javaTargetPage() {
        return javaTarget;
    }

    public JSONPage jsonSourcePage() {
        return jsonSource;
    }

    public JSONPage jsonTargetPage() {
        return jsonTarget;
    }

    public URLClassLoader loader() {
        return loader;
    }

    public OtherPage otherSourcePage() {
        return otherSource;
    }

    public OtherPage otherTargetPage() {
        return otherTarget;
    }

    @Override
    public boolean performFinish() {
        // Save transformation file
        IProject project = CamelUtils.project();
        final IFile file = project.getFile(MavenUtils.RESOURCES_PATH + uiModel.getFilePath());
        if (file.exists()
            && !MessageDialog.openConfirm(getShell(), Messages.NewTransformationWizard_messageDialogTitleConfirm,
				Messages.bind(Messages.NewTransformationWizard_confirmationDialogmessage, file.getFullPath()))) {
            return false;
        }
        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) {
				final MapperConfiguration dozerConfigBuilder = DozerMapperConfiguration.newConfig(this.getClass().getClassLoader());
                final File newFile = new File(file.getLocationURI());
                if (!newFile.getParentFile().exists()) {
                    newFile.getParentFile().mkdirs();
                }
                CamelFile camelModel = CamelUtils.getDiagramEditor().getModel();
                try (FileOutputStream configStream = new FileOutputStream(newFile)) {
                    Util.ensureSourceFolderExists(JavaCore.create(project),
                                                  new MavenUtils().javaSourceFolder(),
                                                  monitor);
                    if (uiModel.getSourceFilePath() != null) {

                        // Generate models
                        final String sourceClassName
                            = generateModel(uiModel.getSourceFilePath(), uiModel.getSourceType(), true, monitor);
                        final String targetClassName
                            = generateModel(uiModel.getTargetFilePath(), uiModel.getTargetType(), false, monitor);
                        // Update Camel config
                        final IPath resourcesPath = project.getFolder(MavenUtils.RESOURCES_PATH).getFullPath();
                        CamelConfigBuilder configBuilder = new CamelConfigBuilder();
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
                        addCamelDozerDependency(project);
                        new ImportExportPackageUpdater(project, sourceClassName, targetClassName).updatePackageImports(monitor);
                        addDataFormatDefinitionDependency(sourceFormat);
                        addDataFormatDefinitionDependency(targetFormat);

                        if (saveCamelConfig){
                        	new CamelIOHandler().saveCamelModel(camelModel, camelModel.getResource(), monitor);
                        }
                        dozerConfigBuilder.addClassMapping(sourceClassName, targetClassName);
                    }
                    dozerConfigBuilder.saveConfig(configStream);

                    if (!saveCamelConfig) {
                        // now update the camel config if we didn't already
                    	if (camelModel.getRouteContainer() instanceof CamelContextElement) {
	                        CamelContextElement camelContext = (CamelContextElement)camelModel.getRouteContainer();

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
                    	} else {
                    		// dataformats and endpoints can be only added to a Camel Context
                    	}
                    }

                    project.refreshLocal(IProject.DEPTH_INFINITE, null);
                    // Ensure build of Java classes has completed
                    try {
                        Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
                    } catch (InterruptedException ignored) {
                    	Thread.currentThread().interrupt();
                    }

                    // Open mapping editor
                    final IEditorDescriptor desc =
                        PlatformUI.getWorkbench()
                                  .getEditorRegistry()
                                  .getEditors(file.getName(),
                                              Platform.getContentTypeManager().getContentType(DozerConfigContentTypeDescriber.ID))[0];
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
        } catch (Exception e) {
        	Activator.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error performing finish", e)); //$NON-NLS-1$
        }
        return false;
    }

    public AbstractCamelModelElement getRouteEndpoint() {
        return routeEndpoint;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#getWindowTitle()
     */
    @Override
    public String getWindowTitle() {
        return Messages.NewTransformationWizard_windowTtile;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        final IJavaProject javaProject = JavaCore.create(CamelUtils.project());
        try {
            loader = (URLClassLoader)JavaUtil.getProjectClassLoader(javaProject, getClass().getClassLoader());
        } catch (final Exception e) {
            Activator.error(e);
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

    private String generateModel(String filePath,
                                 ModelType type,
                                 boolean isSource,
                                 IProgressMonitor monitor) throws Exception {

        // Build class name from file name
        final StringBuilder classNameBuilder = new StringBuilder();
        final StringCharacterIterator iter = new StringCharacterIterator(filePath.substring(
                filePath.lastIndexOf('/') + 1, filePath.lastIndexOf('.')));
        boolean wordStart = true;
        for (char chr = iter.first(); chr != StringCharacterIterator.DONE; chr = iter.next()) {
            if (classNameBuilder.length() == 0) {
                if (Character.isJavaIdentifierStart(chr)) {
                    classNameBuilder.append(wordStart ? Character.toUpperCase(chr) : chr);
                    wordStart = false;
                }
            } else if (Character.isJavaIdentifierPart(chr)) {
                classNameBuilder.append(wordStart ? Character.toUpperCase(chr) : chr);
                wordStart = false;
            } else {
                wordStart = true;
            }
        }
        // Build package name from class name
        int sequencer = 1;
        String pkgName = classNameBuilder.toString();
        String javaSourceFolder = new MavenUtils().javaSourceFolder();
        while (CamelUtils.project().exists(new Path(javaSourceFolder + pkgName))) {
            pkgName = classNameBuilder.toString() + sequencer++;
        }
        pkgName = pkgName.toLowerCase();
        // Generate model
        File targetSourceFolder = new File(CamelUtils.project().getFolder(javaSourceFolder).getLocationURI());
        switch (type) {
        case OTHER:
        case CLASS:
        case JAVA: {
            return getJavaQualifiedName(filePath, monitor);
        }
        case JSON: {
            String className = classNameBuilder.toString().replace("_", ""); //$NON-NLS-1$ //$NON-NLS-2$
            final JsonModelGenerator generator = new JsonModelGenerator();
            generator.generateFromInstance(className, pkgName, CamelUtils.project().findMember(filePath)
                    .getLocationURI().toURL(), targetSourceFolder);
            return pkgName + "." + className; //$NON-NLS-1$
        }
        case JSON_SCHEMA: {
            String className = classNameBuilder.toString().replace("_", ""); //$NON-NLS-1$ //$NON-NLS-2$
            final JsonModelGenerator generator = new JsonModelGenerator();
            generator.generateFromSchema(className, pkgName, CamelUtils.project().findMember(filePath)
                    .getLocationURI().toURL(), targetSourceFolder);
            return pkgName + "." + className; //$NON-NLS-1$
        }
        case XSD: {
            final XmlModelGenerator generator = new XmlModelGenerator();
            final File schemaFile = new File(CamelUtils.project().findMember(filePath).getLocationURI());
            final JCodeModel model = generator.generateFromSchema(schemaFile, null, targetSourceFolder);
            return generateXmlModel(generator, model, isSource, monitor);
        }
        case XML: {
            final XmlModelGenerator generator = new XmlModelGenerator();
            final File schemaPath = new File(CamelUtils.project().getFile(filePath + ".xsd").getLocationURI()); //$NON-NLS-1$
            final JCodeModel model = generator.generateFromInstance(new File(CamelUtils.project().findMember(filePath)
                    .getLocationURI()), schemaPath, null, targetSourceFolder);
            return generateXmlModel(generator, model, isSource, monitor);
        }
        default:
            return null;
        }
    }

	private String getJavaQualifiedName(String filePath, IProgressMonitor monitor) throws JavaModelException {
		final IJavaProject javaProject = JavaCore.create(CamelUtils.project());
		IType pkg = javaProject.findType(filePath, monitor);
		if (pkg != null) {
		    return pkg.getFullyQualifiedName();
		}
		return null;
	}

    private String generateXmlModel(XmlModelGenerator generator,
                                    JCodeModel model,
                                    boolean isSource,
                                    IProgressMonitor monitor) throws Exception {
        String elementName;
        if (isSource) {
            elementName = uiModel.getSourceClassName();
        } else {
            elementName = uiModel.getTargetClassName();
        }
        String modelClassName = getModelClassName(generator, model, elementName);
        // Rename packages to avoid conflicts with existing classes
        CamelUtils.project().refreshLocal(IProject.DEPTH_INFINITE, monitor);
        long time = System.currentTimeMillis();
        IJavaProject project = JavaCore.create(CamelUtils.project());
        boolean modelClassFound = false;
        for (Iterator<JPackage> pkgIter = model.packages(); pkgIter.hasNext();) {
            JPackage modelPkg = pkgIter.next();
            Iterator<JDefinedClass> classIter = modelPkg.classes();
            if (classIter.hasNext()) {
                JDefinedClass modelClass = classIter.next();
                IPackageFragment newPkg = project.findType(modelClass.fullName(), monitor).getPackageFragment();
                String newPkgName = newPkg.getElementName() + '_' + time;
                RenameSupport renameSupport = RenameSupport.create(newPkg, newPkgName, RenameSupport.UPDATE_REFERENCES);
                renameSupport.perform(getShell(), getContainer());
                project.save(monitor, true);

                // Update transformation model class name if it's in this package
                if (!modelClassFound) {
                    if (modelClass.fullName().equals(modelClassName)) {
                        modelClassName = newPkgName + '.' + modelClass.name();
                        modelClassFound = true;
                    } else while (classIter.hasNext()) {
                        modelClass = classIter.next();
                        if (modelClass.fullName().equals(modelClassName)) {
                            modelClassName = newPkgName + '.' + modelClass.name();
                            modelClassFound = true;
                            break;
                        }
                    }
                }
            }
        }

        if (modelClassName != null) {
            return modelClassName;
        }
        return null;
    }

	private String getModelClassName(XmlModelGenerator generator, JCodeModel model, String elementName) {
        Map<String, String> mappings = generator.elementToClassMapping(model);
        if (mappings != null && !mappings.isEmpty()) {
            return mappings.get(elementName);
        }
        return selectModelClass(model);
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

    private Dependency createDependency(String groupId, String artifactId, String version) {
        Dependency dep = new Dependency();
        dep.setGroupId(groupId);
        dep.setArtifactId(artifactId);
        dep.setVersion(version);
        return dep;
    }

    public XMLPage xmlSourcePage() {
        return xmlSource;
    }

    public XMLPage xmlTargetPage() {
        return xmlTarget;
    }
}
