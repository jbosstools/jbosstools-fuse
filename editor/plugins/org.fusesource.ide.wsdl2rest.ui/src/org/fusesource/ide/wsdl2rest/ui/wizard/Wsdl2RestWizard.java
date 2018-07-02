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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.projecttemplates.util.BasicProjectCreatorRunnableUtils;
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

	/**
	 * Collection of settings used by the wsdl2rest utility.
	 */
	final Wsdl2RestOptions options;
	
	public static final String DEFAULT_CONFIG_NAME = "rest-camel-context.xml"; //$NON-NLS-1$
	public static final String DEFAULT_BLUEPRINT_CONFIG_NAME = "rest-blueprint-context.xml"; //$NON-NLS-1$
	public static final String DEFAULT_SPRINGBOOT_CONFIG_NAME = "rest-springboot-context.xml"; //$NON-NLS-1$

	private Wsdl2RestWizardSecondPage pageTwo;
	
	private boolean done = false; 
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
			getContainer().run(false, false, (IProgressMonitor monitor) -> {
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
				} finally {
					done = true;
				}
			});
		} catch (InvocationTargetException e) {
			Wsdl2RestUIActivator.pluginLog().logError(e);
			return false;
		} catch (InterruptedException e) {
			Wsdl2RestUIActivator.pluginLog().logError(e);
			Thread.currentThread().interrupt();
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
		CamelMavenUtils cmu = new CamelMavenUtils();
		List<Dependency> projectDependencies = cmu.getDependencyList(project);
		Iterator<Dependency> depIter = projectDependencies.iterator();
		while(depIter.hasNext()) {
			Dependency dependency = depIter.next();
			if ("org.apache.camel".equals(dependency.getGroupId()) && //$NON-NLS-1$
					"camel-blueprint".equals(dependency.getArtifactId())) { //$NON-NLS-1$
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

	/**
	 * Add JAX-RS dependency
	 */
	private void updateDependencies(IProgressMonitor monitor) {
		List<org.fusesource.ide.camel.model.service.core.catalog.Dependency> deps = new ArrayList<>();
		org.fusesource.ide.camel.model.service.core.catalog.Dependency one = 
				new org.fusesource.ide.camel.model.service.core.catalog.Dependency();
		one.setArtifactId("jboss-jaxrs-api_2.0_spec"); //$NON-NLS-1$
		one.setGroupId("org.jboss.spec.javax.ws.rs"); //$NON-NLS-1$
		one.setVersion("1.0.0.Final-redhat-1"); //$NON-NLS-1$
		deps.add(one);
		IProject project = options.getProject();
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
	private void generate(IProgressMonitor monitor) throws IOException, CoreException, URISyntaxException {
		SubMonitor subMon = SubMonitor.convert(monitor, 6);
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
			doWsdl2RestMigration(project, javaFile, wsdlLocation, isBlueprint, isSpringBoot, camelFile, subMon.split(2));
		}
		subMon.setWorkRemaining(0);
	}

	private void doWsdl2RestMigration(IProject project, File javaFile, URL wsdlLocation, boolean isBlueprint, boolean isSpringBoot, File camelFile, IProgressMonitor monitor) throws IOException, CoreException, URISyntaxException  {
		SubMonitor subMon = SubMonitor.convert(monitor, 2);
		Path contextPath;
		ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

			Path outJavaPath = javaFile.toPath();
			Wsdl2Rest tool = new Wsdl2Rest(wsdlLocation, outJavaPath);
			contextPath = initContextForTool(isBlueprint, isSpringBoot, camelFile, tool);
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
			try {
				tool.process();	
			} catch (Exception ex) {
				throw new IOException(ex);
			}				
			updateDependencies(subMon.split(1));
		}  finally {
			Thread.currentThread().setContextClassLoader(oldLoader);
			project.refreshLocal(IResource.DEPTH_INFINITE, subMon.split(1));
		}
		if (contextPath != null) {
			Job job = new Job("Update Camel Context File...") {
				
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					SubMonitor subMon = SubMonitor.convert(monitor, 1);
					while (!done) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
					Display.getDefault().syncExec( () -> updateIDValueInCamelFile(contextPath, subMon.split(1)));
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
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
	 * 
	 * @param path
	 * @param monitor
	 */
	protected void updateIDValueInCamelFile(Path path, IProgressMonitor monitor) {
		SubMonitor subMon = SubMonitor.convert(monitor, 6);
		IProject project = options.getProject();
		File f = path.toFile();

		// close editor if opened		
		try {
			closeEditorForCamelContextFile(project, f, subMon.split(1));
		} catch (PartInitException e) {
			Wsdl2RestUIActivator.pluginLog().logError(e);
		}

		try {
			// we need to refresh the resources to prevent popup asking for reload
			project.refreshLocal(IProject.DEPTH_INFINITE, subMon.split(1));
			// then we open the file in our editor to make sure all IDs are set and formatting is applied on save
			IFile camelFile = openCamelContextFile(project, f, subMon.split(1));
			// save it as there were formattings and id values applied to it
			// that would mark the editor dirty on opening
			saveCamelContextFile(camelFile, subMon.split(1));
		} catch (CoreException ex) {
			Wsdl2RestUIActivator.pluginLog().logError(ex);
		}
		subMon.setWorkRemaining(0);
	}
	
	private void saveCamelContextFile(IFile camelFile, IProgressMonitor monitor) throws PartInitException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 2);
		CamelEditor editor = CamelUtils.getCamelEditorForFile(camelFile, subMonitor.split(1));
		if (editor != null) {
			editor.doSave(subMonitor.split(1));
		}
		subMonitor.setWorkRemaining(0);
	}
	
	private void closeEditorForCamelContextFile(IProject project, File f, IProgressMonitor monitor) throws PartInitException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 1);
		IFile camelFile = getResourceFromProject(project, f);
		if (camelFile != null) {
			CamelEditor editor = CamelUtils.getCamelEditorForFile(camelFile, subMonitor.split(1));
			if (editor != null) {
				IWorkbenchWindow w = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (w != null && w.getActivePage() != null) {
					w.getActivePage().closeEditor(editor, false);
				}
			}
		}
		subMonitor.setWorkRemaining(0);
	}
	
	/**
	 * 
	 * @param project
	 * @param f
	 * @param monitor
	 * @throws CoreException
	 */
	private IFile openCamelContextFile(IProject project, File f, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 1);
		IFile camelFile = getResourceFromProject(project, f);
		if (camelFile != null) {
			BasicProjectCreatorRunnableUtils.openCamelFile(camelFile, subMonitor.split(1), false, false);
			return camelFile;
		}
		subMonitor.setWorkRemaining(0);
		return null;
	}
	
	private IFile getResourceFromProject(IProject project, File f) {
		if (project != null && f != null && f.exists()) {
			IFile[] files = project.getWorkspace().getRoot().findFilesForLocationURI(f.toURI());
			if (files.length>0) {
				return files[0];
			}
		}
		return null;
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
