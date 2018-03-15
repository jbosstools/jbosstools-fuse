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
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.fusesource.ide.foundation.ui.wizard.ProjectWizardLocationPage;
import org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension;
import org.fusesource.ide.syndesis.extensions.core.util.SyndesisVersionUtil;
import org.fusesource.ide.syndesis.extensions.ui.internal.Messages;
import org.fusesource.ide.syndesis.extensions.ui.internal.SyndesisExtensionsUIActivator;
import org.fusesource.ide.syndesis.extensions.ui.templates.CustomConnectorProjectTemplate;
import org.fusesource.ide.syndesis.extensions.ui.templates.CustomStepAsCamelRouteProjectTemplate;
import org.fusesource.ide.syndesis.extensions.ui.templates.CustomStepAsJavaBeanProjectTemplate;
import org.fusesource.ide.syndesis.extensions.ui.util.NewSyndesisExtensionProjectMetaData;
import org.fusesource.ide.syndesis.extensions.ui.wizards.pages.SyndesisExtensionProjectWizardExtensionDetailsPage;

/**
 * @author lhein
 */
public class SyndesisExtensionProjectWizard extends Wizard implements INewWizard {

	protected IStructuredSelection selection;

	protected ProjectWizardLocationPage locationPage;
	protected SyndesisExtensionProjectWizardExtensionDetailsPage extensionDetailsPage;

	private SyndesisExtension extensionModel = new SyndesisExtension();
	
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
		try {
			getContainer().run(false, true, new SyndesisExtensionProjectCreatorRunnable(getProjectMetaData()));
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

		extensionDetailsPage = new SyndesisExtensionProjectWizardExtensionDetailsPage();
		addPage(extensionDetailsPage);
	}

	private NewSyndesisExtensionProjectMetaData getProjectMetaData() {
		NewSyndesisExtensionProjectMetaData metadata = new NewSyndesisExtensionProjectMetaData();
		metadata.setProjectName(locationPage.getProjectName());
		if (!locationPage.isInWorkspace()) {
			metadata.setLocationPath(locationPage.getLocationPath());
		}
		metadata.setSyndesisExtensionConfig(extensionModel);
		if (extensionDetailsPage.isCustomConnector()) {
			metadata.setTemplate(new CustomConnectorProjectTemplate());
		} else if (extensionDetailsPage.isCamelRoute()) {
			metadata.setTemplate(new CustomStepAsCamelRouteProjectTemplate());
		} else {
			metadata.setTemplate(new CustomStepAsJavaBeanProjectTemplate());
		}
		Map<String, String> versions = SyndesisVersionUtil.checkSyndesisVersionExisting(metadata.getSyndesisExtensionConfig().getSyndesisVersion(), new NullProgressMonitor());
		if (versions.containsKey(SyndesisVersionUtil.PROP_CAMEL_VERSION)) {
			metadata.setCamelVersion(versions.get(SyndesisVersionUtil.PROP_CAMEL_VERSION));
		}
		return metadata;
	}
	
	public SyndesisExtension getSyndesisExtension() {
		return extensionModel;
	}
}
