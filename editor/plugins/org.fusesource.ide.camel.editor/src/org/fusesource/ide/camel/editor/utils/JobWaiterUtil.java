/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.utils;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;

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
			CamelEditorUIActivator.pluginLog().logInfo("log trace to ensure it is not looping");
		} else {
			if (decreasingCounter <= 0) {
				if(exception != null){
					CamelEditorUIActivator.pluginLog().logError(exception);
				} else {
					CamelEditorUIActivator.pluginLog().logWarning("Waiting for job to finish unsuccessfully.");
				}
				return;
			}
			currentCounter = decreasingCounter-1;
		}
		joinJobs(monitor, currentCounter);
	}

	private void joinJobs(IProgressMonitor monitor, int currentCounter) {
		try {
			for (Object jobFamily : jobFamilies) {
				Job.getJobManager().join(jobFamily, monitor);
			}
		} catch (InterruptedException iex) {
			// Workaround to bug
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=335251
			exception = iex;
			waitJob(currentCounter, monitor);
			Thread.currentThread().interrupt();			
		} catch (OperationCanceledException e) {
			// Workaround to bug
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=335251
			exception = e;
			waitJob(currentCounter, monitor);
		}
	}

	public boolean isEndless() {
		return isEndless;
	}

	public void setEndless(boolean isEndless) {
		this.isEndless = isEndless;
	}
	
}
