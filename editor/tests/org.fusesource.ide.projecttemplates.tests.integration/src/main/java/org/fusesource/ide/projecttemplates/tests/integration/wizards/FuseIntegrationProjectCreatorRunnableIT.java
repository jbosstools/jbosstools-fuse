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
package org.fusesource.ide.projecttemplates.tests.integration.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;
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
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.tests.util.AbstractProjectCreatorRunnableIT;
import org.fusesource.ide.camel.tests.util.CommonTestUtils;
import org.fusesource.ide.foundation.core.util.VersionUtil;
import org.fusesource.ide.foundation.ui.util.ScreenshotUtil;
import org.fusesource.ide.launcher.debug.model.CamelDebugFacade;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.model.ThreadGarbageCollector;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.ui.launch.ExecutePomAction;
import org.fusesource.ide.launcher.ui.launch.ExecutePomActionPostProcessor;
import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.impl.simple.EmptyProjectTemplateForFuse6;
import org.fusesource.ide.projecttemplates.impl.simple.EmptyProjectTemplateForFuse7;
import org.fusesource.ide.projecttemplates.impl.simple.EmptyProjectTemplateForFuse71;
import org.fusesource.ide.projecttemplates.tests.integration.ProjectTemplatesIntegrationTestsActivator;
import org.fusesource.ide.projecttemplates.util.NewFuseIntegrationProjectMetaData;
import org.fusesource.ide.projecttemplates.wizards.FuseIntegrationProjectCreatorRunnable;
import org.fusesource.ide.projecttemplates.wizards.pages.model.EnvironmentData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseDeploymentPlatform;
import org.fusesource.ide.projecttemplates.wizards.pages.model.FuseRuntimeKind;
import org.junit.Before;

/**
 * @author Aurelien Pupier
 *
 */
public abstract class FuseIntegrationProjectCreatorRunnableIT extends AbstractProjectCreatorRunnableIT {
	
	public static IProjectFacet camelFacet = ProjectFacetsManager.getProjectFacet("jst.camel");
	
	boolean deploymentFinished = false;
	boolean isDeploymentOk = false;
	protected String camelVersion;

	@Before
	public void setup() throws Exception {
		ProjectTemplatesIntegrationTestsActivator.pluginLog().logInfo("Starting setup for "+ FuseIntegrationProjectCreatorRunnableIT.class.getSimpleName());
		CommonTestUtils.prepareIntegrationTestLaunch(SCREENSHOT_FOLDER);

		String projectName = project != null ? project.getName() : String.format("%s-%s", getClass().getSimpleName(), camelVersion);
		ScreenshotUtil.saveScreenshotToFile(String.format("%s/MavenLaunchOutput-%s_BEFORE.png", SCREENSHOT_FOLDER, projectName), SWT.IMAGE_PNG);
		
		//No staging repository currently
//		if("2.18.1.redhat-000012".equals(camelVersion) || "2.17.0.redhat-630224".equals(camelVersion)){
//			new StagingRepositoriesPreferenceInitializer().setStagingRepositoriesEnablement(true);
//		}

		ProjectTemplatesIntegrationTestsActivator.pluginLog().logInfo("End setup for "+ FuseIntegrationProjectCreatorRunnableIT.class.getSimpleName());
	}

	protected void testProjectCreation(String projectNameSuffix, CamelDSLType dsl, String camelFilePath, NewFuseIntegrationProjectMetaData metadata) throws Exception {
		final String projectName = getClass().getSimpleName() + projectNameSuffix;
		ProjectTemplatesIntegrationTestsActivator.pluginLog().logInfo("Starting creation of the project: "+projectName);
		assertThat(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).exists()).isFalse();

		if (metadata == null) {
			metadata = createDefaultNewProjectMetadata(dsl, projectName);
		}
		
		AbstractProjectTemplate template = metadata.getTemplate();
		if (template != null) {
			assertThat(template.isCompatible(createEnvironmentData())).isTrue();
		}

		new ProgressMonitorDialog(Display.getDefault().getActiveShell()).run(false, true, new FuseIntegrationProjectCreatorRunnable(metadata));

		project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		assertThat(project.exists()).describedAs("The project "+ project.getName()+ " doesn't exist.").isTrue();
		ProjectTemplatesIntegrationTestsActivator.pluginLog().logInfo("Project created: "+projectName);
		final IFile camelResource = project.getFile(camelFilePath);
		assertThat(camelResource.exists()).isTrue();

		// TODO: wait for all build job to finish?
		waitJob();

		checkCorrectEditorOpened(camelResource);
		waitJob();
		checkCorrectFacetsEnabled(project);
		waitJob();
		checkCorrectNatureEnabled(project);
		waitForValidationThreads();
		checkNoValidationError();
		checkNoValidationWarning();
		additionalChecks(project);
		
		if(!CamelDSLType.JAVA.equals(dsl)){
			launchDebug(project);
		} else {
			//TODO: different Run? or implement the java local camel context?
		}
	}

	protected EnvironmentData createEnvironmentData() {
		return new EnvironmentData(camelVersion, FuseDeploymentPlatform.STANDALONE, FuseRuntimeKind.KARAF);
	}

	private void waitForValidationThreads() throws InterruptedException {
		int waitTimeLeft = 30000;
		while(isValidationThreadRunning() && waitTimeLeft > 0) {
			Thread.sleep(100);
			waitTimeLeft -= 100;
		}
		if (waitTimeLeft < 0) {
			ProjectTemplatesIntegrationTestsActivator.pluginLog().logError("The validation thread is still active!");
		}
	}

	protected boolean isValidationThreadRunning() {
		return Thread.getAllStackTraces().keySet().stream()
				.filter(thread -> "org.eclipse.wst.sse.ui.internal.reconcile.StructuredRegionProcessor".equals(thread.getName()))
				.findAny().isPresent();
	}

	/**
	 * @param dsl
	 * @param projectName
	 * @return
	 */
	protected NewFuseIntegrationProjectMetaData createDefaultNewProjectMetadata(CamelDSLType dsl, final String projectName) {
		NewFuseIntegrationProjectMetaData metadata;
		metadata = new NewFuseIntegrationProjectMetaData();
		metadata.setProjectName(projectName);
		metadata.setLocationPath(null);
		metadata.setCamelVersion(camelVersion);
		metadata.setTargetRuntime(null);
		metadata.setDslType(dsl);
		if (isOlderThan220()) {
			metadata.setTemplate(new EmptyProjectTemplateForFuse6());
		} else if (isNewerThan221()){
			metadata.setTemplate(new EmptyProjectTemplateForFuse71());
		} else {
			metadata.setTemplate(new EmptyProjectTemplateForFuse7());
		}
		return metadata;
	}

	private void checkNoValidationWarning() throws CoreException {
		checkNoValidationIssueOfType(filterWarning());
	}
	
	private Predicate<IMarker> filterWarning(){
		return marker -> {
			try {
				Object severity = marker.getAttribute(IMarker.SEVERITY);
				boolean isWarning = severity ==null  || severity.equals(IMarker.SEVERITY_WARNING);
				String message = (String)marker.getAttribute(IMarker.MESSAGE);
				return isWarning
						//TODO: managed other dependencies than camel
						&& !message.startsWith("Duplicating managed version")
						//TODO: manage community version and pure fis version
						&& (!message.startsWith("Overriding managed version") || (camelVersion.contains("redhat") && !CamelCatalogUtils.isPureFISVersion(camelVersion)));
			} catch (CoreException e1) {
				return true;
			}
		};
	}

	/**
	 * @param camelResource
	 * @throws InterruptedException
	 */
	private void checkCorrectEditorOpened(IFile camelResource) throws InterruptedException {
		readAndDispatch(0);
		int currentAwaitedTime = 0;
		while (CommonTestUtils.getCurrentActiveEditor() == null && currentAwaitedTime < 30000) {
			Thread.sleep(100);
			currentAwaitedTime += 100;
			System.out.println("awaited activation of editor " + currentAwaitedTime);
		}
		IEditorPart editor = CommonTestUtils.getCurrentActiveEditor();
		assertThat(editor).as("No editor has been opened.").isNotNull();
		IEditorInput editorInput = editor.getEditorInput();
		assertThat(editorInput.getAdapter(IFile.class)).isEqualTo(camelResource);
		assertThat(editor.isDirty()).as("A newly created project should not have dirty editor.").isFalse();
		
		// if xml context we check if the design editor loads fine
		if ("xml".equalsIgnoreCase(camelResource.getFileExtension()) && editor instanceof CamelEditor) {
			CamelEditor ed = (CamelEditor)editor;
			assertThat(ed.getDesignEditor()).as("The Camel Designer has not been created.").isNotNull();
			assertThat(ed.getDesignEditor().getDiagramTypeProvider()).as("Error retrieving the diagram type provider.").isNotNull();
			assertThat(ed.getDesignEditor().getDiagramTypeProvider().getDiagram()).as("Unable to access the camel context diagram.").isNotNull();
		}
	}
	
    protected void launchDebug(IProject project) throws InterruptedException, IOException, MalformedObjectNameException, DebugException {
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
		executePomAction.launch(getSelectionForLaunch(project), ILaunchManager.DEBUG_MODE);
		int currentAwaitedTime = 0;
		while (currentAwaitedTime < ThreadGarbageCollector.THREAD_LIFE_DURATION && !deploymentFinished) {
			readAndDispatch(0);
			try (JMXConnector jmxc = JMXConnectorFactory.connect(new JMXServiceURL(ICamelDebugConstants.DEFAULT_JMX_URI))) {
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
		assertThat(isDeploymentOk).as("build/deployment failed, you can have a look to the file "+ mavenOutputFilePath + " for more information.").isTrue();
		launch = executePomAction.getLaunch();
		assertThat(Stream.of(launch.getDebugTargets())
				.filter(debugTarget -> debugTarget instanceof CamelDebugTarget)
				.collect(Collectors.toList()))
		.isNotEmpty();
	}

	protected StructuredSelection getSelectionForLaunch(IProject project) {
		return new StructuredSelection(project);
	}

	protected boolean isOlderThan220() {
		return new VersionUtil().isStrictlyLowerThan2200(camelVersion);
	}
	
	protected boolean isNewerThan221() {
		return new VersionUtil().isStrictlyGreaterThan(camelVersion, "2.21.0.fuse-710");
	}
}
