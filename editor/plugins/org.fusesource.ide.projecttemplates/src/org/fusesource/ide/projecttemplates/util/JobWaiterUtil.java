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
package org.fusesource.ide.projecttemplates.util;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;

public class JobWaiterUtil {
	
	boolean isEndless = false;
	
	private Throwable exception = null;

	public void waitBuildAndRefreshJob(IProgressMonitor monitor) {
		waitBuildAndRefreshJob(20, monitor);
	}
	
	private void waitBuildAndRefreshJob(int decreasingCounter, IProgressMonitor monitor) {
		int currentCounter;
		if(isEndless){
			currentCounter = decreasingCounter;
			ProjectTemplatesActivator.pluginLog().logInfo("log trace to ensure it is not looping");
		} else {
			if (decreasingCounter <= 0) {
				if(exception != null){
					ProjectTemplatesActivator.pluginLog().logError(exception);
				} else {
					ProjectTemplatesActivator.pluginLog().logWarning("Waiting for build to finish unsuccessfully.");
				}
				return;
			}
			currentCounter = decreasingCounter-1;
		}
		joinJobs(monitor, currentCounter);
	}

	private void joinJobs(IProgressMonitor monitor, int currentCounter) {
		try {
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, monitor);
			Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_REFRESH, monitor);
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_REFRESH, monitor);
			Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_BUILD, monitor);
		} catch (InterruptedException | OperationCanceledException e) {
			// Workaround to bug
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=335251
			exception = e;
			waitBuildAndRefreshJob(currentCounter, monitor);
		}
	}

	public boolean isEndless() {
		return isEndless;
	}

	public void setEndless(boolean isEndless) {
		this.isEndless = isEndless;
	}
	
}
