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
package org.fusesource.ide.syndesis.extensions.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.fusesource.ide.foundation.ui.wizard.ProjectWizardLocationPage;
import org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension;
import org.fusesource.ide.syndesis.extensions.ui.internal.Messages;
import org.fusesource.ide.syndesis.extensions.ui.internal.SyndesisExtensionsUIActivator;
import org.fusesource.ide.syndesis.extensions.ui.templates.BasicSyndesisExtensionXmlProjectTemplate;
import org.fusesource.ide.syndesis.extensions.ui.util.NewSyndesisExtensionProjectMetaData;
import org.fusesource.ide.syndesis.extensions.ui.wizards.pages.SyndesisExtensionProjectWizardExtensionDetailsPage;
import org.fusesource.ide.syndesis.extensions.ui.wizards.pages.SyndesisExtensionProjectWizardVersionsPage;

/**
 * @author lhein
 */
public class SyndesisExtensionProjectWizard extends Wizard implements INewWizard {

	protected IStructuredSelection selection;

	protected ProjectWizardLocationPage locationPage;
	protected SyndesisExtensionProjectWizardVersionsPage versionPage;
	protected SyndesisExtensionProjectWizardExtensionDetailsPage extensionDetailsPage;

	public SyndesisExtensionProjectWizard() {
		super();
		setWindowTitle(Messages.newProjectWizardTitle);
		setDefaultPageImageDescriptor(SyndesisExtensionsUIActivator.imageDescriptorFromPlugin(SyndesisExtensionsUIActivator.PLUGIN_ID, SyndesisExtensionsUIActivator.SYNDESIS_EXTENSION_PROJECT_ICON));
		setNeedsProgressMonitor(true);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		final NewSyndesisExtensionProjectMetaData metadata = getProjectMetaData();
		try {
			getContainer().run(false, true, new SyndesisExtensionProjectCreatorRunnable(metadata));
		} catch (InterruptedException iex) {
			SyndesisExtensionsUIActivator.pluginLog().logError("User canceled the wizard!", iex); //$NON-NLS-1$
			Thread.currentThread().interrupt();
			return false;
		} catch (InvocationTargetException ite) {
			SyndesisExtensionsUIActivator.pluginLog().logError("Error occured executing the wizard!", ite); //$NON-NLS-1$
			return false;
		}
		return true;
	}

	@Override
	public void addPages() {
		super.addPages();

		locationPage = new ProjectWizardLocationPage(SyndesisExtensionsUIActivator.imageDescriptorFromPlugin(SyndesisExtensionsUIActivator.PLUGIN_ID, SyndesisExtensionsUIActivator.SYNDESIS_EXTENSION_PROJECT_ICON));
		addPage(locationPage);

		versionPage = new SyndesisExtensionProjectWizardVersionsPage();
		addPage(versionPage);
		
		extensionDetailsPage = new SyndesisExtensionProjectWizardExtensionDetailsPage();
		addPage(extensionDetailsPage);
	}

	private NewSyndesisExtensionProjectMetaData getProjectMetaData() {
		NewSyndesisExtensionProjectMetaData metadata = new NewSyndesisExtensionProjectMetaData();
		metadata.setProjectName(locationPage.getProjectName());
		if (!locationPage.isInWorkspace()) {
			metadata.setLocationPath(locationPage.getLocationPath());
		}
		metadata.setSyndesisExtensionConfig(getSyndesisExtension());
		metadata.setTemplate(new BasicSyndesisExtensionXmlProjectTemplate());
		return metadata;
	}
	
	private SyndesisExtension getSyndesisExtension() {
		SyndesisExtension extension = new SyndesisExtension();
		extension.setSpringBootVersion(versionPage.getSpringBootVersion());
		extension.setCamelVersion(versionPage.getCamelVersion());
		extension.setSyndesisVersion(versionPage.getSyndesisVersion());
		extension.setExtensionId(extensionDetailsPage.getExtensionId());
		extension.setVersion(extensionDetailsPage.getExtensionVersion());
		extension.setName(extensionDetailsPage.getExtensionName());
		extension.setDescription(extensionDetailsPage.getExtensionDescription());
		extension.setTags(extensionDetailsPage.getExtensionTags());
		return extension;
	}
}
