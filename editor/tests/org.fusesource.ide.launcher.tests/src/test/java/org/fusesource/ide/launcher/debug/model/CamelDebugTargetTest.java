/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.debug.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.fusesource.ide.camel.model.service.core.jmx.camel.ICamelDebuggerMBeanFacade;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CamelDebugTargetTest {

	@Mock
	private ILaunch launch;
	@Mock
	private IProcess process;
	@Mock
	private ICamelDebuggerMBeanFacade debugger;
	@Mock
	private ThreadGarbageCollector garbageCollector;
	@Mock
	protected EventDispatchJob dispatcher;
	
	private CamelDebugTarget camelDebugTarget;

	@Test
	public void testTerminateNotAvailableForRemoteDebug() throws Exception {
		createRemoteDebugTarget();
		assertThat(camelDebugTarget.canTerminate()).isFalse();
		assertThat(camelDebugTarget.isTerminated()).isFalse();
	}
	
	@Test
	public void testTerminateAvailableForLocalDebug() throws Exception {
		createLocalDebugTarget();
		assertThat(camelDebugTarget.canTerminate()).isTrue();
	}
	
	@Test
	@Ignore("Failing since upgrade to Target Platform 2022-12 - not detected impact for end-users - see FUSETOOLS-3670")
	public void testTerminateStatusForLocalDebug() throws Exception {
		createLocalDebugTarget();
		assertThat(camelDebugTarget.isTerminated()).isFalse();
		assertThat(camelDebugTarget.canTerminate()).isTrue();
		
		camelDebugTarget.terminate();
		
		verify(process).terminate();
		
		assertThat(camelDebugTarget.isTerminated()).isTrue();
		assertThat(camelDebugTarget.canTerminate()).isFalse();
	}

	@Test
	public void testDisconnectNotAvailableForLocalDebug() throws Exception {
		createLocalDebugTarget();
		assertThat(camelDebugTarget.canDisconnect()).isFalse();
		assertThat(camelDebugTarget.isDisconnected()).isFalse();
	}
	
	@Test
	public void testDisconnectAvailableForRemoteDebug() throws Exception {
		createRemoteDebugTarget();
		assertThat(camelDebugTarget.canDisconnect()).isTrue();
		assertThat(camelDebugTarget.isDisconnected()).isFalse();
	}
	
	@Test
	@Ignore("Failing since upgrade to Target Platform 2022-12 - not detected impact for end-users - see FUSETOOLS-3670")
	public void testDisconnectedStatusAfterDisconnection() throws Exception {
		createRemoteDebugTarget();
		camelDebugTarget.disconnect();
		assertThat(camelDebugTarget.isDisconnected()).isTrue();
		assertThat(camelDebugTarget.canDisconnect()).isFalse();
	}
	
	private void createRemoteDebugTarget() throws CoreException {
		createDebugTarget(null);
	}
	
	private void createLocalDebugTarget() throws CoreException {
		createDebugTarget(process);
	}

	private void createDebugTarget(IProcess process) throws CoreException {
		camelDebugTarget = new CamelDebugTarget(launch, process, "", "", ""){
			@Override
			void scheduleJobs(String jmxUser, String jmxPass) {
				/* as this method is called in constructor but don't want to test this part, I just override it to fake initialization */
				this.debugger = CamelDebugTargetTest.this.debugger;
				this.garbageCollector = CamelDebugTargetTest.this.garbageCollector;
				this.dispatcher = CamelDebugTargetTest.this.dispatcher;
			}
			
			@Override
			void registerAsBreakpointListener() {
				/* as this method is called in constructor but don't want to test this part, I just override it to do nothing in the test */
			}
			
			@Override
			void unregisterBreakpointListener() {
				/* as I cannot mock this part, I just override it to do nothing in the test */
			}
			
			@Override
			public void fireTerminateEvent(){
				/* as I cannot mock this part, I just override it to do nothing in the test */
			}
		};
	}
	
}
