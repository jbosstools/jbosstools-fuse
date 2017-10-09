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
import org.fusesource.ide.wsdl2rest.ui.wizard.pages.Wsdl2RestWizardPage;
import org.jboss.fuse.wsdl2rest.impl.Wsdl2Rest;

/**
 * @author brianf
 *
 */
public class Wsdl2RestWizard extends Wizard implements INewWizard {

	private Wsdl2RestWizardPage page;

	public Wsdl2RestWizard() {
		// empty
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("Wsdl-to-Camel Rest DSL Wizard");
		setNeedsProgressMonitor(true);
	}

	@Override
	public boolean performFinish() {
		try {
			generate(new URL(page.getWsdlURL()), page.getOutputPathURL());
		} catch (Exception e) {
			// do something with this
		}
		return true;
	}

	protected IProject sampleGetSelectedProject() {
		// This is a hack for some prototyping to make sure we know what project to stick the stuff in.
		ISelectionService ss = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		String projExpID = "org.eclipse.ui.navigator.ProjectExplorer";
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
		IProject project = sampleGetSelectedProject();
		page = new Wsdl2RestWizardPage("page", "Select Incoming WSDL and Location for Generated Output", null);
		page.setWsdlURL("http://www.webservicex.com/globalweather.asmx?wsdl");
		page.setOutputPathURL(project.getLocation().toOSString());
		addPage(page);
	}
	
    private void generate(final URL wsdlLocation, final String outputPath) throws Exception {
        Path outpath = new File(outputPath).toPath();
        Wsdl2Rest tool = new Wsdl2Rest(wsdlLocation, outpath);
        tool.process();
    }


}
