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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.camel.model.service.core.CamelServiceManagerUtil;
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
			getContainer().run(true, false, new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					SubMonitor subMonitor = SubMonitor.convert(monitor, NLS.bind(Messages.validatingCamelVersionMessage, selectedCamelVersion), 1);
					Thread thread = new Thread(() -> {
						while(true) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								ProjectTemplatesActivator.pluginLog().logError(e);
								Thread.currentThread().interrupt();
							}
							subMonitor.setWorkRemaining(100).split(1);
						}
					});
					thread.start();
					hasValidCamelVersion = CamelServiceManagerUtil.getManagerService().isCamelVersionExisting(selectedCamelVersion);
					subMonitor.setWorkRemaining(0);
					thread.interrupt();
				}
				
			});
		} catch (InvocationTargetException | InterruptedException e) {
			ProjectTemplatesActivator.pluginLog().logError(e);
		}
		if(!hasValidCamelVersion) {
			page.setErrorMessage(NLS.bind(Messages.invalidCamelVersionMessage, selectedCamelVersion));
		}
		return hasValidCamelVersion;
	}

	public String getSelectedCamelVersion() {
		return page.getSelectedCamelVersion();
	}

}
