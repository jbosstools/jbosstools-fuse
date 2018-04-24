/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.foundation.core.util;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.foundation.core.internal.FoundationCoreActivator;

/**
 * @author lheinema
 */
public class JobWaiterUtil {
	boolean isEndless = false;
	private Throwable exception = null;
	private List<Object> jobFamilies;
	
	public JobWaiterUtil(List<Object> jobFamilies) {
		this.jobFamilies = jobFamilies;
	}

	public void waitJob(IProgressMonitor monitor) {
		waitJob(20, monitor);
	}
	
	private void waitJob(int decreasingCounter, IProgressMonitor monitor) {
		int currentCounter;
		if(isEndless){
			currentCounter = decreasingCounter;
			FoundationCoreActivator.pluginLog().logInfo("log trace to ensure it is not looping");
		} else {
			if (decreasingCounter <= 0) {
				if(exception != null){
					FoundationCoreActivator.pluginLog().logError(exception);
				} else {
					FoundationCoreActivator.pluginLog().logWarning("Waiting for job to finish unsuccessfully.");
				}
				return;
			}
			currentCounter = decreasingCounter-1;
		}
		joinJobs(monitor, currentCounter);
	}

	private void joinJobs(IProgressMonitor monitor, int currentCounter) {
		SubMonitor subMon = SubMonitor.convert(monitor, jobFamilies.size());
		try {
			for (Object jobFamily : jobFamilies) {
				updateUI();
				Job.getJobManager().join(jobFamily, subMon.split(1));
			}
		} catch (InterruptedException iex) {
			// Workaround to bug
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=335251
			exception = iex;
			waitJob(currentCounter, subMon.split(1));
			Thread.currentThread().interrupt();			
		} catch (OperationCanceledException e) {
			// Workaround to bug
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=335251
			exception = e;
			waitJob(currentCounter, subMon.split(1));
		}
		subMon.setWorkRemaining(0);
	}

	public boolean isEndless() {
		return isEndless;
	}

	public void setEndless(boolean isEndless) {
		this.isEndless = isEndless;
	}
	
	public static void updateUI() {
		while (Display.getDefault().readAndDispatch()) {
			// wait until all unprocessed UI events are handled
		}	
	}
}
