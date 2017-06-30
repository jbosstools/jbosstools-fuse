/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.projecttemplates.wizards;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;
import org.fusesource.ide.projecttemplates.wizards.pages.FuseIntegrationProjectWizardLocationPage;
import org.fusesource.ide.projecttemplates.wizards.pages.FuseIntegrationProjectWizardRuntimeAndCamelPage;
import org.fusesource.ide.projecttemplates.wizards.pages.FuseIntegrationProjectWizardTemplatePage;

/**
 * @author lhein
 */
public class FuseIntegrationProjectWizard extends Wizard implements INewWizard {

	protected IStructuredSelection selection;

	protected FuseIntegrationProjectWizardLocationPage locationPage;
	protected FuseIntegrationProjectWizardRuntimeAndCamelPage runtimeAndCamelVersionPage;
	protected FuseIntegrationProjectWizardTemplatePage templateSelectionPage;

	public FuseIntegrationProjectWizard() {
		super();
		setWindowTitle(Messages.newProjectWizardTitle);
		setDefaultPageImageDescriptor(ProjectTemplatesActivator.imageDescriptorFromPlugin(ProjectTemplatesActivator.PLUGIN_ID, ProjectTemplatesActivator.IMAGE_CAMEL_PROJECT_ICON));
		setNeedsProgressMonitor(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#needsProgressMonitor()
	 */
	@Override
	public boolean needsProgressMonitor() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final NewProjectMetaData metadata = getProjectMetaData();
		try {
			// TODO: try to make fork true
			getContainer().run(false, true, new FuseIntegrationProjectCreatorRunnable(metadata));
		} catch (InterruptedException iex) {
			ProjectTemplatesActivator.pluginLog().logError("User canceled the wizard!", iex); //$NON-NLS-1$
			return false;
		} catch (InvocationTargetException ite) {
			ProjectTemplatesActivator.pluginLog().logError("Error occured executing the wizard!", ite); //$NON-NLS-1$
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();

		locationPage = new FuseIntegrationProjectWizardLocationPage();
		addPage(locationPage);

		runtimeAndCamelVersionPage = new FuseIntegrationProjectWizardRuntimeAndCamelPage();
		addPage(runtimeAndCamelVersionPage);

		templateSelectionPage = new FuseIntegrationProjectWizardTemplatePage();
		addPage(templateSelectionPage);
	}

	private NewProjectMetaData getProjectMetaData() {
		NewProjectMetaData metadata = new NewProjectMetaData();
		metadata.setProjectName(locationPage.getProjectName());
		if (!locationPage.isInWorkspace()) {
			metadata.setLocationPath(locationPage.getLocationPath());
		}
		metadata.setCamelVersion(runtimeAndCamelVersionPage.getSelectedCamelVersion());
		metadata.setTargetRuntime(runtimeAndCamelVersionPage.getSelectedRuntime());
		metadata.setDslType(templateSelectionPage.getDSL());
		metadata.setBlankProject(templateSelectionPage.isEmptyProject());
		metadata.setTemplate(templateSelectionPage.getSelectedTemplate() != null ? templateSelectionPage.getSelectedTemplate().getTemplate() : null);
		return metadata;
	}
}
