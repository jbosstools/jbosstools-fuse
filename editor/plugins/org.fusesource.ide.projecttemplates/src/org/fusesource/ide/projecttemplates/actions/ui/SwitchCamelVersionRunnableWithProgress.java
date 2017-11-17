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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.camel.model.service.core.CamelServiceManagerUtil;
import org.fusesource.ide.projecttemplates.internal.Messages;

final class SwitchCamelVersionRunnableWithProgress implements IRunnableWithProgress {

	private final SwitchCamelVersionWizard switchCamelVersionWizard;
	private final String newCamelVersion;

	SwitchCamelVersionRunnableWithProgress(SwitchCamelVersionWizard switchCamelVersionWizard, String newCamelVersion) {
		this.switchCamelVersionWizard = switchCamelVersionWizard;
		this.newCamelVersion = newCamelVersion;
	}

	@Override
	public void run(IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, NLS.bind(Messages.validatingCamelVersionMessage, newCamelVersion), 1);
		UnknownTimeMonitorUpdater unknownTimeMonitorUpdater = new UnknownTimeMonitorUpdater(subMonitor);
		try {
			new Thread(unknownTimeMonitorUpdater).start();
			switchCamelVersionWizard.setHasValidCamelVersion(CamelServiceManagerUtil.getManagerService().isCamelVersionExisting(newCamelVersion));
			subMonitor.setWorkRemaining(0);
		} finally {
			unknownTimeMonitorUpdater.finish();
		}
	}
}