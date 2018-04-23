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
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
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

	/**
	 * Collection of settings used by the wsdl2rest utility.
	 */
	final Wsdl2RestOptions options;
	
	public Wsdl2RestWizard() {
		options = new Wsdl2RestOptions();
	}
	
	public Wsdl2RestWizard(Wsdl2RestOptions options) {
		this.options = options;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(UIMessages.wsdl2RestWizardWindowTitle);
		setNeedsProgressMonitor(true);
	}

	@Override
	public boolean performFinish() {
		try {
			generate();
		} catch (Exception e) {
			Wsdl2RestUIActivator.pluginLog().logError(e);
			ErrorDialog.openError(
					getShell(),
					UIMessages.wsdl2RestWizardErrorWindowTitle,
					UIMessages.wsdl2RestWizardErrorMessage,
					new Status(IStatus.ERROR, Wsdl2RestUIActivator.PLUGIN_ID, e.getMessage(), e));
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
		String projExpID = "org.eclipse.ui.navigator.ProjectExplorer"; //$NON-NLS-1$
		ISelection sel = ss.getSelection(projExpID);
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

	@Override
	public void addPages() {
		super.addPages();

		// page one
		IProject initialProject = getSelectedProjectFromSelectionService();
		Wsdl2RestWizardFirstPage pageOne = new Wsdl2RestWizardFirstPage("page1", //$NON-NLS-1$ 
				UIMessages.wsdl2RestWizardPageOneTitle);
		if (initialProject != null) {
			options.setProjectName(initialProject.getName());
		}
		addPage(pageOne);

		// page two
		Wsdl2RestWizardSecondPage pageTwo = new Wsdl2RestWizardSecondPage("page2", //$NON-NLS-1$ 
				UIMessages.wsdl2RestWizardPageTwoTitle); 
		addPage(pageTwo);
	}

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
	
	private void updateDependencies() throws CoreException {
		List<org.fusesource.ide.camel.model.service.core.catalog.Dependency> deps = new ArrayList<>();
		org.fusesource.ide.camel.model.service.core.catalog.Dependency one = 
				new org.fusesource.ide.camel.model.service.core.catalog.Dependency();
		one.setArtifactId("jboss-jaxrs-api_2.0_spec"); //$NON-NLS-1$
		one.setGroupId("org.jboss.spec.javax.ws.rs"); //$NON-NLS-1$
		one.setVersion("1.0.0.Final-redhat-1"); //$NON-NLS-1$
		deps.add(one);
		IProject project = options.getProject();
		new MavenUtils().updateMavenDependencies(deps, project);
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
	}

	private void prepare(IFolder folder) throws CoreException {
		if (!folder.exists() && folder.getParent() instanceof IFolder) {
			prepare((IFolder) folder.getParent());
			folder.create(false, true, null);
		}
	}
	
	/**
	 * Use the settings collected and call the wsdl2rest utility.
	 *
	 * @throws Exception
	 */
	private void generate() throws Exception {
		URL wsdlLocation = new URL(options.getWsdlURL());
		IPath javaPath = new org.eclipse.core.runtime.Path(options.getDestinationJava());
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(javaPath);
		IProject project = options.getProject();
		if (resource == null) {
			IFolder folder = project.getFolder(javaPath.removeFirstSegments(1));
			if (!folder.exists()) {
				prepare(folder);
				resource = folder;
				project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			}
		}

		// use project to determine if we are building a spring or blueprint project
		boolean isBlueprint = isProjectBlueprint(project);
		
		File javaFile = findJavaFile(resource);
		IPath camelPath = new org.eclipse.core.runtime.Path(options.getDestinationCamel());
		IResource camelResource = ResourcesPlugin.getWorkspace().getRoot().findMember(camelPath);
		File camelFile = findCamelFile(camelResource);
		if (javaFile != null) {
			ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
			try {
				// initialize bus using bundle classloader, to prevent project dependencies from leaking in
				Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

				Path outJavaPath = javaFile.toPath();
				Wsdl2Rest tool = new Wsdl2Rest(wsdlLocation, outJavaPath);
				initContextForTool(isBlueprint, camelFile, tool);
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
				updateDependencies();
			}  finally {
				Thread.currentThread().setContextClassLoader(oldLoader);
			}
		}
	}

	protected void initContextForTool(boolean isBlueprint, File camelFile, Wsdl2Rest tool) {
		if (!isBlueprint) {
			if (camelFile != null) {
				Path contextpath = new File(camelFile.getAbsolutePath() + File.separator + "rest-camel-context.xml").toPath(); //$NON-NLS-1$
				tool.setCamelContext(contextpath); 
			} else {
				IProject project = options.getProject();
				IPath projectPath = ResourcesPlugin.getWorkspace().getRoot().findMember(project.getName()).getLocation();
				Path contextpath = new File(projectPath.makeAbsolute() + File.separator + "rest-camel-context.xml").toPath(); //$NON-NLS-1$
				tool.setCamelContext(contextpath); 
			}
		} else {
			if (camelFile != null) {
				Path contextpath = new File(camelFile.getAbsolutePath() + File.separator + "rest-blueprint-context.xml").toPath(); //$NON-NLS-1$
				tool.setCamelContext(contextpath); 
			} else {
				IProject project = options.getProject();
				IPath projectPath = ResourcesPlugin.getWorkspace().getRoot().findMember(project.getName()).getLocation();
				Path contextpath = new File(projectPath.makeAbsolute() + File.separator + "rest-blueprint-context.xml").toPath(); //$NON-NLS-1$
				tool.setBlueprintContext(contextpath);
			}
		}
	}

	/**
	 * @param resource
	 * @return
	 * @throws CoreException
	 */
	protected File findJavaFile(IResource resource) throws CoreException {
		File javaFile = null;
		if (resource instanceof IFolder) {
			IFolder destFolder = (IFolder) resource;
			// gets URI for EFS.
			URI uri = destFolder.getLocationURI();

			// what if file is a link, resolve it.
			if(destFolder.isLinked()){
				uri = destFolder.getRawLocationURI();
			}

			// Gets native File using EFS
			javaFile = EFS.getStore(uri).toLocalFile(0, new NullProgressMonitor());			
		}
		return javaFile;
	}

	/**
	 * @param camelResource
	 * @return
	 * @throws CoreException
	 */
	protected File findCamelFile(IResource camelResource) throws CoreException {
		File camelFile = null;
		if (camelResource instanceof IFolder) {
			IFolder camelDestFolder = (IFolder) camelResource;
			// gets URI for EFS.
			URI cameluri = camelDestFolder.getLocationURI();

			// what if file is a link, resolve it.
			if(camelDestFolder.isLinked()){
				cameluri = camelDestFolder.getRawLocationURI();
			}

			// Gets native File using EFS
			camelFile = EFS.getStore(cameluri).toLocalFile(0, new NullProgressMonitor());			
		}
		return camelFile;
	}

	/**
	 * Returns the shared Wsdl2RestOptions object 
	 * @return Wsdl2RestOptions
	 */
	public Wsdl2RestOptions getOptions() {
		return options;
	}
	
}
