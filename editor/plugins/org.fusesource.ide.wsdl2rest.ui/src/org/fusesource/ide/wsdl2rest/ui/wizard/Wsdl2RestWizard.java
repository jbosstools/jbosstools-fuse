/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.wsdl2rest.ui.wizard;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.wsdl2rest.ui.internal.UIMessages;
import org.fusesource.ide.wsdl2rest.ui.internal.Wsdl2RestUIActivator;
import org.fusesource.ide.wsdl2rest.ui.wizard.pages.Wsdl2RestWizardFirstPage;
import org.fusesource.ide.wsdl2rest.ui.wizard.pages.Wsdl2RestWizardSecondPage;
import org.jboss.fuse.wsdl2rest.impl.Wsdl2Rest;

/**
 * @author brianf
 *
 */
public class Wsdl2RestWizard extends Wizard implements INewWizard {

	private static final String APACHE_CAMEL_GROUP_ID = "org.apache.camel"; //$NON-NLS-1$

	/**
	 * Collection of settings used by the wsdl2rest utility.
	 */
	final Wsdl2RestOptions options;
	
	public static final String DEFAULT_CONFIG_NAME = "rest-camel-context.xml"; //$NON-NLS-1$
	public static final String DEFAULT_BLUEPRINT_CONFIG_NAME = "rest-blueprint-context.xml"; //$NON-NLS-1$
	public static final String DEFAULT_SPRINGBOOT_CONFIG_NAME = "rest-springboot-context.xml"; //$NON-NLS-1$

	private Wsdl2RestWizardSecondPage pageTwo;
	
	private boolean inTest = false;
	
	/**
	 * Constructor
	 */
	public Wsdl2RestWizard() {
		options = new Wsdl2RestOptions();
	}
	
	/**
	 * Constructor with passed-in options
	 * @param options
	 */
	public Wsdl2RestWizard(Wsdl2RestOptions options) {
		this.options = options;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(UIMessages.wsdl2RestWizardWindowTitle);
		setNeedsProgressMonitor(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						generate(monitor);
					} catch (Exception e) {
						Wsdl2RestUIActivator.pluginLog().logError(e);
						ErrorDialog.openError(
								getShell(),
								UIMessages.wsdl2RestWizardErrorWindowTitle,
								UIMessages.wsdl2RestWizardErrorMessage,
								new Status(IStatus.ERROR, Wsdl2RestUIActivator.PLUGIN_ID, e.getMessage(), e));
						throw new InvocationTargetException(e);
					}
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			Wsdl2RestUIActivator.pluginLog().logError(e);
			return false;
		}
		return true;
	}

	/**
	 * Uses the Workbench selection service to find the currently selected project and use it as the source. 
	 * @return IProject
	 */
	protected IProject getSelectedProjectFromSelectionService() {
		ISelectionService ss = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection sel = ss.getSelection(IPageLayout.ID_PROJECT_EXPLORER);
		Object selectedObject=sel;
		if(sel instanceof IStructuredSelection) {
			selectedObject=
					((IStructuredSelection)sel).getFirstElement();
		}
		if (selectedObject instanceof IAdaptable) {
			IResource res = ((IAdaptable) selectedObject).getAdapter(IResource.class);
			return res.getProject();
		}
		return null;
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();

		// page one
		IProject initialProject = getSelectedProjectFromSelectionService();
		Wsdl2RestWizardFirstPage pageOne = new Wsdl2RestWizardFirstPage(UIMessages.wsdl2RestWizardPageOneTitle);
		if (initialProject != null) {
			options.setProjectName(initialProject.getName());
		}
		addPage(pageOne);

		pageTwo = new Wsdl2RestWizardSecondPage(UIMessages.wsdl2RestWizardPageTwoTitle); 
		addPage(pageTwo);
	}

	/**
	 * Checks for blueprint dependency
	 * @param project
	 * @return
	 */
	public boolean isProjectBlueprint(IProject project) {
		return projectHasDependency(project, APACHE_CAMEL_GROUP_ID, "camel-blueprint"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Checks for blueprint dependency
	 * @param project
	 * @return
	 */
	public boolean isProjectSpring(IProject project) {
		return projectHasDependency(project, APACHE_CAMEL_GROUP_ID, "camel-spring"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private boolean projectHasDependency(IProject project, String groupId, String artifactId) {
		CamelMavenUtils cmu = new CamelMavenUtils();
		List<Dependency> projectDependencies = cmu.getDependencyList(project);
		Iterator<Dependency> depIter = projectDependencies.iterator();
		while(depIter.hasNext()) {
			Dependency dependency = depIter.next();
			if (groupId.equals(dependency.getGroupId()) &&
					artifactId.equals(dependency.getArtifactId())) { 
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks for spring boot
	 * @param project
	 * @return
	 */
	public boolean isProjectSpringBoot(IProject project) {
		CamelMavenUtils cmu = new CamelMavenUtils();
		List<Dependency> projectDependencies = cmu.getDependencyList(project);
		return CamelCatalogUtils.hasSpringBootDependency(projectDependencies);
	}

	
	private org.fusesource.ide.camel.model.service.core.catalog.Dependency makeDependency(
			String grpId, String artId, String version) {
		org.fusesource.ide.camel.model.service.core.catalog.Dependency dep = 
				new org.fusesource.ide.camel.model.service.core.catalog.Dependency();
		dep.setArtifactId(artId);
		dep.setGroupId(grpId);
		dep.setVersion(version);
		return dep;
	}
	
	/**
	 * Add required dependencies
	 */
	private void updateDependencies(IProgressMonitor monitor) {
		IProject project = options.getProject();

		// use project to determine if we are building a spring or blueprint project
		boolean isBlueprint = isProjectBlueprint(project);
		boolean isSpring = isProjectSpring(project);
		boolean isSpringBoot = isProjectSpringBoot(project);
		
		// retrieve camel version for use with camel dependencies
		CamelMavenUtils utils = new CamelMavenUtils();
		String camelVersion = utils.getCamelVersionFromMaven(project, true);
		
		// now build a list of the dependencies we need
		List<org.fusesource.ide.camel.model.service.core.catalog.Dependency> deps = new ArrayList<>();
		org.fusesource.ide.camel.model.service.core.catalog.Dependency jaxrsSpec = 
				makeDependency("org.jboss.spec.javax.ws.rs",  //$NON-NLS-1$
						"jboss-jaxrs-api_2.0_spec",  //$NON-NLS-1$
						"1.0.0.Final-redhat-1"); //$NON-NLS-1$
		deps.add(jaxrsSpec);
		
		org.fusesource.ide.camel.model.service.core.catalog.Dependency camelJackson = 
				makeDependency(APACHE_CAMEL_GROUP_ID,  //$NON-NLS-1$
						"camel-jackson",  //$NON-NLS-1$
						camelVersion);
		deps.add(camelJackson);
		
		org.fusesource.ide.camel.model.service.core.catalog.Dependency camelCxf = 
				makeDependency(APACHE_CAMEL_GROUP_ID,  //$NON-NLS-1$
						"camel-cxf",  //$NON-NLS-1$
						camelVersion);
		deps.add(camelCxf);

		if (isBlueprint) {
			org.fusesource.ide.camel.model.service.core.catalog.Dependency camelServlet = 
					makeDependency(APACHE_CAMEL_GROUP_ID,  //$NON-NLS-1$
							"camel-servlet",  //$NON-NLS-1$
							camelVersion);
			deps.add(camelServlet);
		}

		if (isSpring) {
			// change this once we get the spring default component updated in wsdl2rest
			org.fusesource.ide.camel.model.service.core.catalog.Dependency camelJetty = 
					makeDependency(APACHE_CAMEL_GROUP_ID,  //$NON-NLS-1$
							"camel-jetty",  //$NON-NLS-1$
							camelVersion);
			deps.add(camelJetty);
		}
		
		if (isSpringBoot) {
			// potentially update the SpringBoot application properties to enable/configure
			// the servlet component
			// camel.component.servlet.mapping.contextPath=/*
		}

		new MavenUtils().updateMavenDependencies(deps, project, monitor);
	}

	/*
	 * @param folder
	 * @param monitor
	 * @throws CoreException
	 */
	private void prepare(IFolder folder, IProgressMonitor monitor) throws CoreException {
		SubMonitor subMon = SubMonitor.convert(monitor, 2);
		if (!folder.exists() && folder.getParent() instanceof IFolder) {
			prepare((IFolder) folder.getParent(), subMon.split(1));
			folder.create(false, true, subMon.split(1));
		}
	}
	
	/*
	 * @param project
	 * @param path
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	private IResource getResourceForPath(IProject project, IPath path, IProgressMonitor monitor) throws CoreException {
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		if (resource == null) {
			if (!path.isEmpty() && path.segment(0).equals(options.getProjectName())) {
				path = path.removeFirstSegments(1);
			}
			IFolder folder = project.getFolder(path);
			if (!folder.exists()) {
				prepare(folder, monitor);
			}
			resource = folder;
		}
		return resource;
	}
	
	/*
	 * @param project
	 * @param path
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	private IResource getFileForPath(IProject project, IPath path, IProgressMonitor monitor) throws CoreException {
		SubMonitor subMon = SubMonitor.convert(monitor, 2);
		if (path.getFileExtension() == null) {
			return getResourceForPath(project, path, subMon.split(2));
		}
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		if (resource == null) {
			if (!path.isEmpty() && !path.segment(0).equals(options.getProjectName())) {
				path = project.getFullPath().append(path);
			}
			resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
			if (resource == null) {
				IPath absolute = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(path).makeAbsolute();
				java.io.File ioFile = absolute.removeLastSegments(1).makeAbsolute().toFile();
				ioFile.mkdirs();
				project.refreshLocal(IResource.DEPTH_INFINITE, subMon.split(1));
				IFile newFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(absolute);
				new CamelIOHandler().loadCamelModel(newFile, subMon.split(1));				
				resource = newFile;
			}
		}
		return resource;
	}
	
	/*
	 * Use the settings collected and call the wsdl2rest utility.
	 *
	 * @param monitor
	 * @throws Exception
	 */
	private void generate(IProgressMonitor monitor) throws Exception {
		SubMonitor subMon = SubMonitor.convert(monitor, 5);
		URL wsdlLocation = new URL(options.getWsdlURL());
		IPath javaPath = new org.eclipse.core.runtime.Path(options.getDestinationJava());
		IProject project = options.getProject();
		IResource resource = getResourceForPath(project, javaPath, subMon.split(1));
		File javaFile = findFileInEFS(resource, subMon.split(1));

		// use project to determine if we are building a spring or blueprint project
		boolean isBlueprint = isProjectBlueprint(project);
		boolean isSpringBoot = isProjectSpringBoot(project);
		
		IPath camelPath = new org.eclipse.core.runtime.Path(options.getDestinationCamel());
		IResource camelResource = getFileForPath(project, camelPath, subMon.split(1));
		File camelFile = findFileInEFS(camelResource, subMon.split(1));
		if (javaFile != null) {
			ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

				Path outJavaPath = javaFile.toPath();
				Wsdl2Rest tool = new Wsdl2Rest(wsdlLocation, outJavaPath);
				initContextForTool(isBlueprint, isSpringBoot, camelFile, tool);
				if (!Strings.isEmpty(options.getTargetServiceAddress())) {
					URI targetAddressURI = new URI(options.getTargetServiceAddress());
					URL targetAddressURL = targetAddressURI.toURL();
					tool.setJaxwsAddress(targetAddressURL);
				}
				if (!Strings.isEmpty(options.getTargetRestServiceAddress())) {
					URL targetRestAddressURL = new URL(options.getTargetRestServiceAddress());
					tool.setJaxrsAddress(targetRestAddressURL);
				}
				if (outJavaPath != null && outJavaPath.toFile().exists()) {
					tool.setJavaOut(outJavaPath);
				}
				ClassLoader loader = tool.getClass().getClassLoader();
				Thread.currentThread().setContextClassLoader(loader);
				tool.process();
				updateDependencies(subMon.split(1));
			}  finally {
				Thread.currentThread().setContextClassLoader(oldLoader);
				project.refreshLocal(IResource.DEPTH_INFINITE, subMon.split(1));
			}
		}
		subMon.setWorkRemaining(0);
	}

	/**
	 * @param isBlueprint
	 * @param isSpringBoot
	 * @return
	 */
	public String getCamelFileName(boolean isBlueprint, boolean isSpringBoot) {
		if (isBlueprint) {
			return DEFAULT_BLUEPRINT_CONFIG_NAME;
		}
		if (isSpringBoot) {
			return DEFAULT_SPRINGBOOT_CONFIG_NAME;
		}
		return DEFAULT_CONFIG_NAME;
	}
	
	/**
	 * @param isBlueprint
	 * @param camelFile
	 * @param tool
	 */
	protected Path initContextForTool(boolean isBlueprint, boolean isSpringBoot, File camelFile, Wsdl2Rest tool) {
		tool.setNoVelocityLog(true);
		Path contextpath = null;
		if (camelFile != null && !camelFile.isDirectory()) {
			contextpath = new File(camelFile.getAbsolutePath()).toPath();
			tool.setCamelContext(contextpath);
		} else {
			String camelConfigName = getCamelFileName(isBlueprint, isSpringBoot);
			if (camelFile != null) {
				contextpath = new File(camelFile.getAbsolutePath() + File.separator + camelConfigName).toPath();
				tool.setCamelContext(contextpath); 
			} else {
				IProject project = options.getProject();
				IPath projectPath = ResourcesPlugin.getWorkspace().getRoot().findMember(project.getName()).getLocation();
				contextpath = new File(projectPath.makeAbsolute() + File.separator + camelConfigName).toPath();
				tool.setCamelContext(contextpath); 
			}
		}
		return contextpath;
	}

	/**
	 * @param resource
	 * @return
	 * @throws CoreException
	 */
	protected File findFileInEFS(IResource resource, IProgressMonitor monitor) throws CoreException {
		// gets URI for EFS.
		URI uri = resource.getLocationURI();

		// what if file is a link, resolve it.
		if(resource.isLinked()){
			uri = resource.getRawLocationURI();
		}

		// Gets native File using EFS
		return EFS.getStore(uri).toLocalFile(0, monitor);			
	}

	/**
	 * Returns the shared Wsdl2RestOptions object 
	 * @return Wsdl2RestOptions
	 */
	public Wsdl2RestOptions getOptions() {
		return options;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		boolean flag = super.canFinish();
		if (flag && !this.inTest && 
				getContainer().getCurrentPage().equals(this.getStartingPage()) && 
				pageTwo.getMessageType() == IMessageProvider.WARNING &&
				pageTwo.getMessage().startsWith("Caution:")) { //$NON-NLS-1$
			return false;
		}
		return flag;
	}

	/**
	 * In place to avoid needing to trigger the user to go to the next page due to warnings.
	 * @param flag
	 */
	public void setInTest(boolean flag) {
		this.inTest = flag;
	}
	
	/**
	 * @return page 2 of wizard 
	 */
	public Wsdl2RestWizardSecondPage getSecondPage() {
		return this.pageTwo;
	}
}
