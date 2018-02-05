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

import org.eclipse.core.runtime.SubMonitor;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;

public final class UnknownTimeMonitorUpdater implements Runnable {
	
	private final SubMonitor subMonitor;
	private boolean finished = false;
	private boolean canceled = false;

	public UnknownTimeMonitorUpdater(SubMonitor monitor) {
		this.subMonitor = monitor;
	}

	@Override
	public void run() {
		while(!finished && !canceled) {
			subMonitor.setWorkRemaining(100).split(1);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				ProjectTemplatesActivator.pluginLog().logError(e);
				Thread.currentThread().interrupt();
			}			
		}
	}
	
	public void cancel() {
		canceled = true;
		subMonitor.setCanceled(true);
	}

	public void finish() {
		finished = true;
	}
	
	public boolean shouldTerminate() {
		return canceled || finished || subMonitor.isCanceled();
	}
}