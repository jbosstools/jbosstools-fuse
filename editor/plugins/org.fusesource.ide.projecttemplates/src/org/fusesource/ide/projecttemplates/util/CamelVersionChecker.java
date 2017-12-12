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
package org.fusesource.ide.projecttemplates.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.camel.model.service.core.CamelServiceManagerUtil;
import org.fusesource.ide.projecttemplates.actions.ui.UnknownTimeMonitorUpdater;
import org.fusesource.ide.projecttemplates.internal.Messages;

/**
 * @author lheinema
 *
 */
public class CamelVersionChecker implements IRunnableWithProgress {

	private String camelVersionToValidate;
	private boolean valid;
	private boolean done;
	private UnknownTimeMonitorUpdater unknownTimeMonitorUpdater;
	
	public CamelVersionChecker(String camelVersionToValidate) {
		this.camelVersionToValidate = camelVersionToValidate;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, NLS.bind(Messages.validatingCamelVersionMessage, camelVersionToValidate), 1);
		unknownTimeMonitorUpdater = new UnknownTimeMonitorUpdater(subMonitor);
		try {
			new Thread(unknownTimeMonitorUpdater).start();
			valid = isCamelVersionValid(camelVersionToValidate);
			subMonitor.setWorkRemaining(0);
		} finally {
			unknownTimeMonitorUpdater.finish();
		}
		done = true;
	}
	
	public void cancel() {
		this.unknownTimeMonitorUpdater.cancel();
	}
	
	public boolean isDone() {
		return done;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public synchronized boolean isCamelVersionValid(String camelVersion) {
		boolean versionValid = false;
		if (camelVersion != null) {
			versionValid = CamelServiceManagerUtil.getManagerService().isCamelVersionExisting(camelVersion);
		}
		return versionValid;
	}
}
