/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.actions.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;

public class SwitchCamelVersionWizard extends Wizard {
	
	private String initialCamelVersion;
	private SwitchCamelVersionWizardPage page;
	private boolean hasValidCamelVersion = false;

	public SwitchCamelVersionWizard(String currentVersion) {
		this.initialCamelVersion = currentVersion;
		setWindowTitle(Messages.switchCamelVersionDialogName);
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() {
		super.addPages();
		page = new SwitchCamelVersionWizardPage(initialCamelVersion);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		String selectedCamelVersion = page.getSelectedCamelVersion();
		try {
			getContainer().run(true, false, new SwitchCamelVersionRunnableWithProgress(this, selectedCamelVersion));
		} catch (InvocationTargetException e) {
			ProjectTemplatesActivator.pluginLog().logError(e);
		} catch (InterruptedException iex) {
			ProjectTemplatesActivator.pluginLog().logError(iex);
			Thread.currentThread().interrupt();
		}
		if(!hasValidCamelVersion) {
			page.setErrorMessage(NLS.bind(Messages.invalidCamelVersionMessage, selectedCamelVersion));
		}
		return hasValidCamelVersion;
	}

	public String getSelectedCamelVersion() {
		return page.getSelectedCamelVersion();
	}

	public void setHasValidCamelVersion(boolean hasValidCamelVersion) {
		this.hasValidCamelVersion = hasValidCamelVersion;
	}

}
