/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
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
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
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

	/**
	 * Constructor
	 */
	public Wsdl2RestWizard() {
		options = new Wsdl2RestOptions();
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
			generate();
		} catch (Exception e) {
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

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();

		// page one
		IProject project = getSelectedProjectFromSelectionService();
		Wsdl2RestWizardFirstPage pageOne = new Wsdl2RestWizardFirstPage("page1", UIMessages.wsdl2RestWizardPageOneTitle, null); //$NON-NLS-1$
		if (project != null) {
			options.setProjectName(project.getName());
		}
		addPage(pageOne);

		// page two
		Wsdl2RestWizardSecondPage pageTwo = new Wsdl2RestWizardSecondPage("page2", UIMessages.wsdl2RestWizardPageTwoTitle, null); //$NON-NLS-1$
		addPage(pageTwo);
	}

	/**
	 * Use the settings collected and call the wsdl2rest utility.
	 * @throws Exception
	 */
	private void generate() throws Exception {
		URL wsdlLocation = new URL(options.getWsdlURL());
		IPath path = new org.eclipse.core.runtime.Path(options.getDestinationJava());
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
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
		if (javaFile != null) {
			Path outpath = javaFile.toPath();
			Path contextpath = new File(javaFile.getAbsolutePath() + File.separator + "rest-camel-context.xml").toPath(); //$NON-NLS-1$
			Wsdl2Rest tool = new Wsdl2Rest(wsdlLocation, outpath);
			tool.setTargetContext(contextpath);
			if (!Strings.isEmpty(options.getBeanClassName())) {
				tool.setTargetBean(options.getBeanClassName());
			}
			if (!Strings.isEmpty(options.getTargetServiceAddress())) {
				URL targetAddressURL = new URL(options.getTargetServiceAddress());
				tool.setTargetAddress(targetAddressURL);
			}
			tool.process();
		}
	}

	/**
	 * Returns the shared Wsdl2RestOptions object 
	 * @return Wsdl2RestOptions
	 */
	public Wsdl2RestOptions getOptions() {
		return options;
	}
}
