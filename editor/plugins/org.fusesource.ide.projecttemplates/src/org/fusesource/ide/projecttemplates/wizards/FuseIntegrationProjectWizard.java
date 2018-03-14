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
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.foundation.ui.wizard.ProjectWizardLocationPage;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.util.NewFuseIntegrationProjectMetaData;
import org.fusesource.ide.projecttemplates.wizards.pages.FuseIntegrationProjectWizardRuntimeAndCamelPage;
import org.fusesource.ide.projecttemplates.wizards.pages.FuseIntegrationProjectWizardTemplatePage;
import org.fusesource.ide.projecttemplates.wizards.pages.model.EnvironmentData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseDeploymentPlatform;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseRuntimeKind;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateItem;

/**
 * @author lhein
 */
public class FuseIntegrationProjectWizard extends Wizard implements INewWizard {

	protected IStructuredSelection selection;

	protected ProjectWizardLocationPage locationPage;
	protected FuseIntegrationProjectWizardRuntimeAndCamelPage runtimeAndCamelVersionPage;
	protected FuseIntegrationProjectWizardTemplatePage templateSelectionPage;

	public FuseIntegrationProjectWizard() {
		super();
		setWindowTitle(Messages.newProjectWizardTitle);
		setDefaultPageImageDescriptor(ProjectTemplatesActivator.imageDescriptorFromPlugin(ProjectTemplatesActivator.PLUGIN_ID, ProjectTemplatesActivator.IMAGE_CAMEL_PROJECT_ICON));
		setNeedsProgressMonitor(true);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		final NewFuseIntegrationProjectMetaData metadata = getProjectMetaData();
		try {
			getContainer().run(false, true, new FuseIntegrationProjectCreatorRunnable(metadata));
		} catch (InterruptedException iex) {
			ProjectTemplatesActivator.pluginLog().logError("User canceled the wizard!", iex); //$NON-NLS-1$
			Thread.currentThread().interrupt();
			return false;
		} catch (InvocationTargetException ite) {
			ProjectTemplatesActivator.pluginLog().logError("Error occured executing the wizard!", ite); //$NON-NLS-1$
			return false;
		}
		return true;
	}

	@Override
	public void addPages() {
		super.addPages();

		locationPage = new ProjectWizardLocationPage(ProjectTemplatesActivator.imageDescriptorFromPlugin(ProjectTemplatesActivator.PLUGIN_ID, ProjectTemplatesActivator.IMAGE_CAMEL_PROJECT_ICON));
		addPage(locationPage);
		EnvironmentData environmentData = new EnvironmentData(CamelCatalogUtils.getLatestCamelVersion(), FuseDeploymentPlatform.OPENSHIFT, FuseRuntimeKind.SPRINGBOOT);
		runtimeAndCamelVersionPage = new FuseIntegrationProjectWizardRuntimeAndCamelPage(environmentData);
		addPage(runtimeAndCamelVersionPage);

		templateSelectionPage = new FuseIntegrationProjectWizardTemplatePage(environmentData);
		addPage(templateSelectionPage);
	}

	private NewFuseIntegrationProjectMetaData getProjectMetaData() {
		NewFuseIntegrationProjectMetaData metadata = new NewFuseIntegrationProjectMetaData();
		metadata.setProjectName(locationPage.getProjectName());
		if (!locationPage.isInWorkspace()) {
			metadata.setLocationPath(locationPage.getLocationPath());
		}
		metadata.setCamelVersion(runtimeAndCamelVersionPage.getSelectedCamelVersion());
		metadata.setTargetRuntime(runtimeAndCamelVersionPage.getSelectedRuntime());
		TemplateItem selectedTemplateItem = templateSelectionPage.getSelectedTemplate();
		metadata.setDslType(selectedTemplateItem.getDslType());
		metadata.setTemplate(selectedTemplateItem.getTemplate());
		return metadata;
	}
}
