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
import java.net.URL;
import java.nio.file.Path;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
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

	final Wsdl2RestOptions options;

	public Wsdl2RestWizard() {
		options = new Wsdl2RestOptions();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(UIMessages.wsdl2RestWizardWindowTitle);
		setNeedsProgressMonitor(true);
	}

	@Override
	public boolean performFinish() {
		try {
			generate(new URL(options.getWsdlURL()), options.getDestinationCamel());
		} catch (Exception e) {
			Wsdl2RestUIActivator.pluginLog().logError(e);
			return false;
		}
		return true;
	}

	protected IProject sampleGetSelectedProject() {
		// This is a hack for some prototyping to make sure we know what project to stick the stuff in.
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
		IProject project = sampleGetSelectedProject();
		Wsdl2RestWizardFirstPage pageOne = new Wsdl2RestWizardFirstPage("page1", UIMessages.wsdl2RestWizardPageOneTitle, null); //$NON-NLS-1$
		if (project != null) {
			options.setProjectName(project.getName());
		}
		addPage(pageOne);
		
		// page two
		Wsdl2RestWizardSecondPage pageTwo = new Wsdl2RestWizardSecondPage("page2", UIMessages.wsdl2RestWizardPageTwoTitle, null); //$NON-NLS-1$
		addPage(pageTwo);
	}
	
    private void generate(final URL wsdlLocation, final String outputPath) throws Exception {
        Path outpath = new File(outputPath).toPath();
        Path contextpath = new File(outputPath + File.pathSeparator + "rest-camel-context.xml").toPath(); //$NON-NLS-1$
        Wsdl2Rest tool = new Wsdl2Rest(wsdlLocation, outpath);
        tool.setTargetContext(contextpath);
		tool.process();
    }

    public Wsdl2RestOptions getOptions() {
    	return options;
    }
}
