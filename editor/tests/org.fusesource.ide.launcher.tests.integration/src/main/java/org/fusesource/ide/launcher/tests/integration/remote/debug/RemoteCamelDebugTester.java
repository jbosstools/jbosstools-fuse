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
package org.fusesource.ide.launcher.tests.integration.remote.debug;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.model.JMXCamelConnectJob;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;
import org.fusesource.ide.projecttemplates.util.JobWaiterUtil;

public class RemoteCamelDebugTester {

	private ILaunch remoteDebuglaunch;
	private IProject project;
	private IFile camelFile;

	public RemoteCamelDebugTester(ILaunch remoteDebuglaunch, IProject project, IFile camelFile) {
		this.remoteDebuglaunch = remoteDebuglaunch;
		this.project = project;
		this.camelFile = camelFile;
	}

	public void test() throws CoreException {
		CamelDebugTarget debugTarget = (CamelDebugTarget) remoteDebuglaunch.getDebugTarget();
		
		new JobWaiterUtil(Arrays.asList(JMXCamelConnectJob.JMX_CONNECT_JOB_FAMILY)).waitJob(new NullProgressMonitor());
		
		checkInitialActionState(debugTarget);
		checkSuspend(debugTarget);
		checkResume(debugTarget);
		
		CamelFile camelModel = new CamelIOHandler().loadCamelModel(camelFile, new NullProgressMonitor());
		AbstractCamelModelElement firstInFlow = camelModel.getChildElements().get(0).getChildElements().get(0).getChildElements().get(0);
		IBreakpoint breakPointOnFirstElement = checkBreakpointAddition(debugTarget, firstInFlow);
		
		checkBreakpointDeletion(debugTarget, breakPointOnFirstElement);
		
		checkDisconnect(debugTarget);
	}

	private void checkInitialActionState(CamelDebugTarget debugTarget) {
		assertThat(debugTarget.canTerminate()).isFalse();
		assertThat(debugTarget.canSuspend()).isTrue();
		assertThat(debugTarget.canResume()).isFalse();
		assertThat(debugTarget.canDisconnect()).isTrue();
	}

	private IBreakpoint checkBreakpointAddition(CamelDebugTarget debugTarget, AbstractCamelModelElement firstInFlow) throws CoreException {
		IBreakpoint breakPointOnFirstElement = CamelDebugUtils.createAndRegisterEndpointBreakpoint(camelFile, firstInFlow, project.getName(), camelFile.getName());
		assertThat(debugTarget.getDebugger().getBreakpoints()).containsExactly(firstInFlow.getId());
		return breakPointOnFirstElement;
	}

	private void checkBreakpointDeletion(CamelDebugTarget debugTarget, IBreakpoint breakPointOnFirstElement) throws CoreException {
		breakPointOnFirstElement.delete();
		assertThat(debugTarget.getDebugger().getBreakpoints()).isEmpty();
	}

	private void checkDisconnect(CamelDebugTarget debugTarget) throws DebugException {
		debugTarget.disconnect();
		assertThat(debugTarget.canSuspend()).isFalse();
		assertThat(debugTarget.canResume()).isFalse();
		assertThat(debugTarget.isDisconnected()).isTrue();
	}

	private void checkResume(CamelDebugTarget debugTarget) throws DebugException {
		debugTarget.resume();
		assertThat(debugTarget.isSuspended()).isFalse();
		assertThat(debugTarget.canSuspend()).isTrue();
		assertThat(debugTarget.canResume()).isFalse();
		assertThat(debugTarget.canDisconnect()).isTrue();
	}

	private void checkSuspend(CamelDebugTarget debugTarget) throws DebugException {
		debugTarget.suspend();
		assertThat(debugTarget.isSuspended()).isTrue();
		assertThat(debugTarget.canSuspend()).isFalse();
		assertThat(debugTarget.canResume()).isTrue();
	}

}
