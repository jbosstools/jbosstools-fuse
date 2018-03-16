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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.fusesource.ide.camel.tests.util.Activator;
import org.fusesource.ide.camel.tests.util.MavenProjectHelper;
import org.fusesource.ide.launcher.debug.model.CamelDebugFacade;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.model.ThreadGarbageCollector;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.ui.launch.ExecutePomAction;
import org.fusesource.ide.launcher.ui.launch.ExecutePomActionPostProcessor;

public class ProjectWithDebugAvailableDeployedHelper {
	
	private static final String POM_XML = IMavenConstants.POM_FILE_NAME;
	
	private boolean deploymentFinished;
	private boolean isDeploymentOk;
	private ILaunch launchUsedToInitialize;

	private IProject project;
	private IFile camelFile;
	private String projectNameAfterImport;
	
	public ProjectWithDebugAvailableDeployedHelper(String projectNameAfterImport) {
		this.projectNameAfterImport = projectNameAfterImport;
	}

	public void start() throws IOException, CoreException, MalformedObjectNameException, InterruptedException {
		File projectFolder = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile(), projectNameAfterImport);
		projectFolder.mkdirs();
		File pomFile = new File(projectFolder, POM_XML);
		Files.copy(ProjectWithDebugAvailableDeployedHelper.class.getResourceAsStream("/jmx-pom.xml"), pomFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		setArtifactIdCorrespondingToProjectName(pomFile);
		File camelContextFileFolder = new File(projectFolder, "src/main/resources/META-INF/spring");
		camelContextFileFolder.mkdirs();
		Files.copy(RemoteCamelLaunchConfigurationDelegateOverJMXIT.class.getResourceAsStream("/camel-context.xml"), new File(camelContextFileFolder, "camel-context.xml").toPath(), StandardCopyOption.REPLACE_EXISTING);
		project = new MavenProjectHelper().importProjects(projectFolder, new String[]{POM_XML})[0];
		camelFile = project.getFile("src/main/resources/META-INF/spring/camel-context.xml");
		launchCamelRoute(project);
	}

	private void setArtifactIdCorrespondingToProjectName(File pomFile) throws IOException {
		try (Stream<String> lines = Files.lines(pomFile.toPath())) {
			   List<String> replaced = lines
			       .map(line-> line.replaceAll("XXX_PROJECT_NAME_XXX", projectNameAfterImport))
			       .collect(Collectors.toList());
			   Files.write(pomFile.toPath(), replaced);
			}
	}

	public void clean() throws CoreException {
		if(launchUsedToInitialize != null && !launchUsedToInitialize.isTerminated()){
			launchUsedToInitialize.terminate();
			for(IProcess process : launchUsedToInitialize.getProcesses()){
				process.terminate();
			}
		}
		project.delete(true, new NullProgressMonitor());
	}
	
	private void launchCamelRoute(IProject project) throws MalformedObjectNameException, InterruptedException, DebugException {
		final File parent = new File("target/MavenLaunchOutputs");
		parent.mkdirs();
		final String mavenOutputFilePath = new File(parent, getMavenLaunchOutPutFileName(project)).getAbsolutePath();
		final ExecutePomAction executePomAction = new ExecutePomAction(){
			
			@Override
			protected void appendAttributes(IContainer basedir, ILaunchConfigurationWorkingCopy workingCopy, String goal) {
				super.appendAttributes(basedir, workingCopy, goal);
				Activator.getDefault().getLog().log(new Status(IStatus.INFO, Activator.ID, "Maven output file path: "+mavenOutputFilePath));
				workingCopy.setAttribute("org.eclipse.debug.ui.ATTR_CAPTURE_IN_FILE", mavenOutputFilePath);
				
				try {
					addExtraAttributesToLocalProjectLaunch(workingCopy);
				} catch (Exception e) {
					Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.ID,"Cannot configure the Maven out put file path", e));
				}
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
		while (currentAwaitedTime < ThreadGarbageCollector.THREAD_LIFE_DURATION && !deploymentFinished) {
			try(JMXConnector jmxc = JMXConnectorFactory.connect(new JMXServiceURL(ICamelDebugConstants.DEFAULT_JMX_URI))) {
				MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
				deploymentFinished = !mbsc.queryMBeans(new ObjectName(CamelDebugFacade.CAMEL_DEBUGGER_MBEAN_DEFAULT), null).isEmpty();
				isDeploymentOk = deploymentFinished;
				Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.ID, "JMX connection succeeded\nisDeployment Finished? " + isDeploymentOk));
			} catch(IOException ioe){
				Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.ID, "actively awaiting that Camel Route starts, JMX connection attempt failed so Camel route not yet started. ", ioe));
			}
			Thread.sleep(500);
			currentAwaitedTime += 500;
		}
		assertThat(isDeploymentOk).isTrue();
		launchUsedToInitialize = executePomAction.getLaunch();
		//disconnect the debug target used with Local debug
		for(IDebugTarget debugTarget : executePomAction.getLaunch().getDebugTargets()){
			if(debugTarget instanceof CamelDebugTarget){
				debugTarget.disconnect();
			}
		}
		launchUsedToInitialize = executePomAction.getLaunch();
	}

	protected String getMavenLaunchOutPutFileName(IProject project) {
		return "MavenLaunchOutput-"+project.getName()+".txt";
	}
	
	protected void addExtraAttributesToLocalProjectLaunch(ILaunchConfigurationWorkingCopy configuration) throws CoreException, IOException {
		/* Only subclass will provide parameters to configure Jolokia for instance */
	}

	public IProject getProject() {
		return project;
	}
	
	public IFile getCamelFile() {
		return camelFile;
	}

}
