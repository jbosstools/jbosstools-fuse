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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.tests.util.CommonTestUtils;
import org.fusesource.ide.camel.tests.util.MavenProjectHelper;
import org.fusesource.ide.launcher.debug.model.CamelDebugFacade;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.remote.debug.RemoteCamelLaunchConfigurationDelegate;
import org.fusesource.ide.launcher.run.util.CamelContextLaunchConfigConstants;
import org.fusesource.ide.launcher.ui.launch.ExecutePomAction;
import org.fusesource.ide.launcher.ui.launch.ExecutePomActionPostProcessor;
import org.fusesource.ide.projecttemplates.util.JobWaiterUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RemoteCamelLaunchConfigurationDelegateIT {
	
	private static final String POM_XML = "pom.xml";
	
	private boolean deploymentFinished;
	private boolean isDeploymentOk;
	private boolean connected = false;

	private IFile camelFile;
	private IProject project;
	private ILaunch launch;
	
	@Before
	public void setup() throws Exception{
		
		File projectFolder = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile(), "maven-project-to-test-JMX");
		projectFolder.mkdirs();
		Files.copy(RemoteCamelLaunchConfigurationDelegateIT.class.getResourceAsStream("/jmx-pom.xml"), new File(projectFolder, POM_XML).toPath(), StandardCopyOption.REPLACE_EXISTING);
		File camelContextFileFolder = new File(projectFolder, "src/main/resources/META-INF/spring");
		camelContextFileFolder.mkdirs();
		Files.copy(RemoteCamelLaunchConfigurationDelegateIT.class.getResourceAsStream("/camel-context.xml"), new File(camelContextFileFolder, "camel-context.xml").toPath(), StandardCopyOption.REPLACE_EXISTING);
		project = new MavenProjectHelper().importProjects(projectFolder, new String[]{POM_XML})[0];
		camelFile = project.getFile("src/main/resources/META-INF/spring/camel-context.xml");
		launchCamelRoute(project);
		
	}
	
	@After
	public void tearDown() throws DebugException{
		if(launch != null && !launch.isTerminated()){
			launch.terminate();
		}
	}

	@Test
	public void testRemoteCamelLaunch() throws Exception {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType launchConfigurationType = manager.getLaunchConfigurationType(RemoteCamelLaunchConfigurationDelegate.LAUNCH_CONFIGURATION_TYPE);
 		ILaunchConfigurationWorkingCopy configuration = launchConfigurationType.newInstance(null, "Remote Camel Debug - "+ camelFile.getName());
 		
 		configuration.setAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, camelFile.getLocation().toOSString());
 		configuration.setAttribute(ICamelDebugConstants.ATTR_JMX_URI_ID, "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi/camel");
 		
		launch = configuration.doSave().launch(ILaunchManager.DEBUG_MODE, new NullProgressMonitor());
		
		CamelDebugTarget debugTarget = (CamelDebugTarget) launch.getDebugTarget();
		
		waitRemoteCamelDebuggerConnection();
		
		assertThat(debugTarget.canTerminate()).isFalse();
		assertThat(debugTarget.canSuspend()).isTrue();
		assertThat(debugTarget.canResume()).isFalse();
		assertThat(debugTarget.canDisconnect()).isTrue();
		
		checkSuspend(debugTarget);
		checkResume(debugTarget);
		
		CamelFile camelModel = new CamelIOHandler().loadCamelModel(camelFile, new NullProgressMonitor());
		AbstractCamelModelElement firstInFlow = camelModel.getChildElements().get(0).getChildElements().get(0).getChildElements().get(0);
		IBreakpoint breakPointOnFirstElement = checkBreakpointAddition(debugTarget, firstInFlow);
		
		checkBreakpointDeletion(debugTarget, breakPointOnFirstElement);
		
		checkDisconnect(debugTarget);
	}

	private void waitRemoteCamelDebuggerConnection() throws InterruptedException {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				new JobWaiterUtil(Arrays.asList(CamelDebugTarget.JMX_CONNECT_JOB_FAMILY)).waitJob(new NullProgressMonitor());
				connected = true;
			}
		});
		int wait = 0;
		while(!connected && wait < 500){
			CommonTestUtils.readAndDispatch(0);
			Thread.sleep(100);
			wait++;
		}
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

	private void launchCamelRoute(IProject project) throws MalformedObjectNameException, InterruptedException, DebugException {
		final File parent = new File("target/MavenLaunchOutputs");
		parent.mkdirs();
		final String mavenOutputFilePath = new File(parent, "MavenLaunchOutput-"+project.getName()+".txt").getAbsolutePath();
		final ExecutePomAction executePomAction = new ExecutePomAction(){
			
			@Override
			protected void appendAttributes(IContainer basedir, ILaunchConfigurationWorkingCopy workingCopy, String goal) {
				super.appendAttributes(basedir, workingCopy, goal);
				System.out.println("Maven output file path: "+mavenOutputFilePath);
				workingCopy.setAttribute("org.eclipse.debug.ui.ATTR_CAPTURE_IN_FILE", mavenOutputFilePath);
			}
			
		};
		
		executePomAction.setPostProcessor(new ExecutePomActionPostProcessor() {

			@Override
			public void executeOnSuccess() {
				//Won't happen
			}

			@Override
			public void executeOnFailure() {
				//if there is a Maven build failure
				deploymentFinished = true;
				isDeploymentOk = false;
			}
		});
		
		executePomAction.launch(new StructuredSelection(project), ILaunchManager.DEBUG_MODE);
		int currentAwaitedTime = 0;
		while (currentAwaitedTime < CamelDebugTarget.ThreadGarbageCollector.THREAD_LIFE_DURATION && !deploymentFinished) {
			try(JMXConnector jmxc = JMXConnectorFactory.connect(new JMXServiceURL(ICamelDebugConstants.DEFAULT_JMX_URI))) {
				MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
				deploymentFinished = !mbsc.queryMBeans(new ObjectName(CamelDebugFacade.CAMEL_DEBUGGER_MBEAN_DEFAULT), null).isEmpty();
				isDeploymentOk = deploymentFinished;
				System.out.println("JMX connection succeeded");
				System.out.println("isDeployment Finished? " + isDeploymentOk);
			} catch(IOException ioe){
				System.out.println("JMX connection attempt failed");
			}
			Thread.sleep(500);
			currentAwaitedTime += 500;
		}
		assertThat(isDeploymentOk).isTrue();
		//disconnect the debug target used with Local debug
		for(IDebugTarget debugTarget : executePomAction.getLaunch().getDebugTargets()){
			if(debugTarget instanceof CamelDebugTarget){
				debugTarget.disconnect();
			}
		}
	}
	
}
