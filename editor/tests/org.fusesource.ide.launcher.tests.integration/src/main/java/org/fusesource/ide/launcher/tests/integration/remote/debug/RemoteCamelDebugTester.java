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
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelContextMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelJMXFacade;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.foundation.core.util.JobWaiterUtil;
import org.fusesource.ide.jmx.camel.internal.JmxTemplateCamelFacade;
import org.fusesource.ide.jmx.camel.internal.RemoteJMXCamelFacade;
import org.fusesource.ide.jmx.commons.JmxPluginJmxTemplate;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.model.JMXCamelConnectJob;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;

public class RemoteCamelDebugTester {

	private ILaunch remoteDebuglaunch;
	private IProject project;
	private IFile camelFile;

	public RemoteCamelDebugTester(ILaunch remoteDebuglaunch, IProject project, IFile camelFile) {
		this.remoteDebuglaunch = remoteDebuglaunch;
		this.project = project;
		this.camelFile = camelFile;
	}

	public void test() throws Exception {
		CamelDebugTarget debugTarget = (CamelDebugTarget) remoteDebuglaunch.getDebugTarget();
		
		new JobWaiterUtil(Arrays.asList(JMXCamelConnectJob.JMX_CONNECT_JOB_FAMILY)).waitJob(new NullProgressMonitor());
		
		checkInitialActionState(debugTarget);
		checkSuspend(debugTarget);
		checkResume(debugTarget);
		
		CamelFile camelModel = new CamelIOHandler().loadCamelModel(camelFile, new NullProgressMonitor());
		AbstractCamelModelElement firstInFlow = camelModel.getChildElements().get(0).getChildElements().get(0).getChildElements().get(0);
		IBreakpoint breakPointOnFirstElement = checkBreakpointAddition(debugTarget, firstInFlow);
		
		checkBreakpointDeletion(debugTarget, breakPointOnFirstElement);
		
		checkRouteUpdate(debugTarget);
		
		checkDisconnect(debugTarget);
	}

	private void checkRouteUpdate(CamelDebugTarget debugTarget) throws Exception {
		CamelJMXFacade facade = createCamelFacade(debugTarget);
		CamelContextMBean camelContextMBean = facade.getCamelContexts().get(0);
		String initialRoute = camelContextMBean.dumpRoutesAsXml();
		String updatedRouteXml = initialRoute.replaceFirst(getFirstProcessorId(facade, camelContextMBean), "anotherComponentId");
		camelContextMBean.addOrUpdateRoutesFromXml(updatedRouteXml);
		assertThat(getFirstProcessorId(facade, camelContextMBean)).isEqualTo("anotherComponentId");
	}

	protected CamelJMXFacade createCamelFacade(CamelDebugTarget debugTarget) throws Exception {
		if(debugTarget.getJmxConnectionWrapper() != null) {
			return new JmxTemplateCamelFacade(new JmxPluginJmxTemplate(debugTarget.getJmxConnectionWrapper()));
		} else {
			return new RemoteJMXCamelFacade(debugTarget.getMBeanConnection());
		}
	}

	private String getFirstProcessorId(CamelJMXFacade facade, CamelContextMBean camelContextMBean) throws Exception {
		return facade.getProcessors(camelContextMBean.getManagementName()).get(0).getProcessorId();
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
		Job registeredDispatcher = debugTarget.getDispatcher();
		checkJobRunning(registeredDispatcher);
		Job garbageCollector = debugTarget.getGarbageCollector();
		checkJobRunning(garbageCollector);
		
		debugTarget.disconnect();
		
		assertThat(debugTarget.canSuspend()).isFalse();
		assertThat(debugTarget.canResume()).isFalse();
		assertThat(debugTarget.getDispatcher()).isNull();
		assertThat(debugTarget.isDisconnected()).isTrue();
		checkJobCanceled(registeredDispatcher);
		checkJobCanceled(garbageCollector);
	}

	private void checkJobCanceled(Job job) {
		int time = 0;
		while(time < 5000 || job.getState() != Job.NONE) {
			time += 100;
		}
		assertThat(job.getState()).isEqualTo(Job.NONE);
	}

	private void checkJobRunning(Job job) {
		assertThat(job.getState()).isEqualTo(Job.RUNNING);
		assertThat(job.getResult()).isNull();
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
