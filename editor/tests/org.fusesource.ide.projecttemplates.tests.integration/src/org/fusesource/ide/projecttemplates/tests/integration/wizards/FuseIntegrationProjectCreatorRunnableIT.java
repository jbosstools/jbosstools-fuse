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

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.fusesource.ide.launcher.ui.launch.ExecutePomAction;
import org.fusesource.ide.launcher.ui.launch.ExecutePomActionPostProcessor;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;
import org.fusesource.ide.projecttemplates.wizards.FuseIntegrationProjectCreatorRunnable;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Aurelien Pupier
 *
 */
public class FuseIntegrationProjectCreatorRunnableIT {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	private IProject project = null;
	boolean deploymentFinished = false;
	boolean isDeploymentOk = false;

	@Before
	public void setup() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();
		store.setValue(IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE, IDEInternalPreferences.PSPM_ALWAYS);
	}

	@After
	public void tearDown() throws CoreException {
		if (project != null) {
			project.delete(true, new NullProgressMonitor());
		}
	}

	@Test
	public void testEmptyBlueprintProjectCreation() throws Exception {
		testEmptyProjectCreation("-SimpleBlueprintProject", CamelDSLType.BLUEPRINT, "src/main/resources/OSGI-INF/blueprint/blueprint.xml", null);
	}

	@Test
	public void testEmptySpringProjectCreation() throws Exception {
		testEmptyProjectCreation("-SimpleSpringProject", CamelDSLType.SPRING, "src/main/resources/META-INF/spring/camel-context.xml", null);
	}

	@Test
	public void testEmptySpringProjectCreationOnLocationOutsideWorkspace() throws Exception {
		NewProjectMetaData metadata = createDefaultNewProjectMetadata(CamelDSLType.SPRING,
				FuseIntegrationProjectCreatorRunnableIT.class.getSimpleName() + "-SimpleSpringProject_outsideProject");
		File folderForprojectOutsiddeWorkspaceLocation = tmpFolder.newFolder("folderForProjectOutsideWorkspaceLocation");
		final Path locationPath = new Path(folderForprojectOutsiddeWorkspaceLocation.getAbsolutePath());
		metadata.setLocationPath(locationPath);
		testEmptyProjectCreation("-SimpleSpringProject_outsideProject", CamelDSLType.SPRING, "src/main/resources/META-INF/spring/camel-context.xml", metadata);

		assertThat(Files.isSameFile(project.getLocation().toFile().toPath(), locationPath.toFile().toPath())).isTrue();
	}

	@Test
	@Ignore("Deactivate Java DSL test to move forward - failing randomly on CI only")
	public void testEmptyJavaProjectCreation() throws Exception {
		testEmptyProjectCreation("-SimpleJavaProject", CamelDSLType.JAVA, "src/main/java/com/mycompany/camel/CamelRoute.java", null);
	}

	private void testEmptyProjectCreation(String projectNameSuffix, CamelDSLType dsl, String camelFilePath, NewProjectMetaData metadata) throws Exception {
		final String projectName = FuseIntegrationProjectCreatorRunnableIT.class.getSimpleName() + projectNameSuffix;
		assertThat(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).exists()).isFalse();

		if (metadata == null) {
			metadata = createDefaultNewProjectMetadata(dsl, projectName);
		}

		new ProgressMonitorDialog(Display.getDefault().getActiveShell()).run(false, true, new FuseIntegrationProjectCreatorRunnable(metadata));

		project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		assertThat(project.exists()).isTrue();
		final IFile camelResource = project.getFile(camelFilePath);
		assertThat(camelResource.exists()).isTrue();

		// TODO: wait for all build job to finish?
		waitJob();

		checkCorrectEditorOpened(camelResource);
		// TODO: fix project to activate no validation error check
		// checkNoValidationError();
		// TODO: check for correct nature activated
		// TODO: currently we generate completely project which are not valid so
		// cannot be launched
		// launchDebug(project);
	}

	/**
	 * @param dsl
	 * @param projectName
	 * @return
	 */
	private NewProjectMetaData createDefaultNewProjectMetadata(CamelDSLType dsl, final String projectName) {
		NewProjectMetaData metadata;
		metadata = new NewProjectMetaData();
		metadata.setProjectName(projectName);
		metadata.setLocationPath(null);
		// TODO use latest version, or a parameterized test to test all versions
		// available CamelModelFactory.getLatestCamelVersion()
		metadata.setCamelVersion("2.15.1.redhat-621084");
		metadata.setTargetRuntime(null);
		metadata.setDslType(dsl);
		metadata.setBlankProject(true);
		metadata.setTemplate(null);
		return metadata;
	}

	private void waitJob() throws OperationCanceledException, InterruptedException {
		try {
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
			Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_REFRESH, new NullProgressMonitor());
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_REFRESH, new NullProgressMonitor());
			Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_BUILD, new NullProgressMonitor());
		} catch (InterruptedException e) {
			// Workaround to bug
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=335251
			System.out.println("Have a trace in case of infinite loop in FuseIntegrationProjectCreatorRunnableIT.waitJob()");
			waitJob();
		}
	}

	/**
	 * @throws CoreException
	 */
	private void checkNoValidationError() throws CoreException {
		final IMarker[] markers = project.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		final List<Object> readableMarkers = Arrays.asList(markers).stream().map(marker -> {
			try {
				return marker.getAttributes();
			} catch (Exception e) {
				return marker;
			}
		}).collect(Collectors.toList());
		assertThat(readableMarkers).isEmpty();
	}

	/**
	 * @param camelResource
	 * @throws InterruptedException
	 */
	private void checkCorrectEditorOpened(IFile camelResource) throws InterruptedException {
		while (Display.getDefault().readAndDispatch()) {

		}
		int currentAwaitedTime = 0;
		while (getCurrentActiveEditor() == null && currentAwaitedTime < 30000) {
			Thread.sleep(100);
			currentAwaitedTime += 100;
			System.out.println("awaited activation of editor " + currentAwaitedTime);
		}
		// @formatter:off
		IEditorPart editor = getCurrentActiveEditor();
		// @formatter:on
		IEditorInput editorInput = editor.getEditorInput();
		assertThat(editorInput.getAdapter(IFile.class)).isEqualTo(camelResource);
	}

	/**
	 * @return
	 */
	private IEditorPart getCurrentActiveEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	private void launchDebug(IProject project) throws InterruptedException {
		final ExecutePomAction executePomAction = new ExecutePomAction();

		executePomAction.setPostProcessor(new ExecutePomActionPostProcessor() {

			@Override
			public void executeOnSuccess() {
				// TODO: shutdown
				deploymentFinished = true;
				isDeploymentOk = true;
			}

			@Override
			public void executeOnFailure() {
				deploymentFinished = true;
				isDeploymentOk = false;
			}
		});
		executePomAction.launch(new StructuredSelection(project), ILaunchManager.DEBUG_MODE);
		int currentAwaitedTime = 0;
		while (currentAwaitedTime < 30000 && !deploymentFinished) {
			Thread.sleep(100);
			currentAwaitedTime += 100;
		}
		assertThat(isDeploymentOk).isTrue();
	}
}
