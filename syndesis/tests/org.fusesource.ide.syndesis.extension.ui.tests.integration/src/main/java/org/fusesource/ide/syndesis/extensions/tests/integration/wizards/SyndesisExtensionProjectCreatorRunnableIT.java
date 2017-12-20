/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.tests.integration.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.management.MalformedObjectNameException;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenExecutionContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.editor.utils.BuildAndRefreshJobWaiterUtil;
import org.fusesource.ide.camel.editor.utils.JobWaiterUtil;
import org.fusesource.ide.camel.tests.util.CommonTestUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.util.ScreenshotUtil;
import org.fusesource.ide.preferences.initializer.StagingRepositoriesPreferenceInitializer;
import org.fusesource.ide.project.RiderProjectNature;
import org.fusesource.ide.projecttemplates.tests.integration.wizards.PrintThreadStackOnFailureRule;
import org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension;
import org.fusesource.ide.syndesis.extensions.tests.integration.SyndesisExtensionIntegrationTestsActivator;
import org.fusesource.ide.syndesis.extensions.ui.wizards.SyndesisExtensionProjectCreatorRunnable;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;

/**
 * @author lheinema
 */
public abstract class SyndesisExtensionProjectCreatorRunnableIT {

	public static final IProjectFacet camelFacet  = ProjectFacetsManager.getProjectFacet("jst.camel");
	public static final IProjectFacet javaFacet 	= ProjectFacetsManager.getProjectFacet("java");
	public static final IProjectFacet m2eFacet 	= ProjectFacetsManager.getProjectFacet("jboss.m2");
	public static final IProjectFacet utilFacet 	= ProjectFacetsManager.getProjectFacet("jst.utility");

	protected static final String CAMEL_RESOURCE_PATH = "src/main/resources/camel/extension.xml";
	protected static final String SYNDESIS_RESOURCE_PATH = "src/main/resources/META-INF/syndesis/syndesis-extension-definition.json";
	
    public static final String SCREENSHOT_FOLDER = "./target/MavenLaunchOutputs";
    
	@Rule
	public TestWatcher printStackTraceOnFailure = new PrintThreadStackOnFailureRule();

	protected IProject project = null;
	boolean buildFinished = false;
	boolean buildOK = false;
	protected ILaunch launch = null;

	@Before
	public void setup() throws Exception {
		SyndesisExtensionIntegrationTestsActivator.pluginLog().logInfo("Starting setup for "+ SyndesisExtensionProjectCreatorRunnableIT.class.getSimpleName());
		CommonTestUtils.prepareIntegrationTestLaunch(SCREENSHOT_FOLDER);

		String projectName = project != null ? project.getName() : String.format("%s", getClass().getSimpleName());
		ScreenshotUtil.saveScreenshotToFile(String.format("%s/MavenLaunchOutput-%s_BEFORE.png", SCREENSHOT_FOLDER, projectName), SWT.IMAGE_PNG);

		// TODO: for now we need the staging repos, disable before GA
		new StagingRepositoriesPreferenceInitializer().setStagingRepositoriesEnablement(true);

		SyndesisExtensionIntegrationTestsActivator.pluginLog().logInfo("End setup for "+ SyndesisExtensionProjectCreatorRunnableIT.class.getSimpleName());
	}

	@After
	public void tearDown() throws CoreException {
		terminateRunningProcesses();

		waitForProjectDeletion();
		
		// kill all running jobs
		Job.getJobManager().cancel(ResourcesPlugin.FAMILY_AUTO_BUILD);
		Job.getJobManager().cancel(ResourcesPlugin.FAMILY_MANUAL_REFRESH);
		Job.getJobManager().cancel(ResourcesPlugin.FAMILY_AUTO_REFRESH);
		Job.getJobManager().cancel(ResourcesPlugin.FAMILY_MANUAL_BUILD);
		
		CommonTestUtils.closeAllEditors();
		new StagingRepositoriesPreferenceInitializer().setStagingRepositoriesEnablement(false);
	}
	
	private void waitForProjectDeletion() throws CoreException {
		if (project != null) {
			//refresh otherwise cannot delete due to target folder created
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			waitJob();
			readAndDispatch(0);
			boolean projectSuccesfullyDeleted = false;
			while(!projectSuccesfullyDeleted ){
				try{
					project.delete(true, true, new NullProgressMonitor());
				} catch(Exception e){
					//some lock/stream kept on camel-context.xml surely by the killed process, need time to let OS such as Windows to re-allow deletion
					readAndDispatch(0);
					waitJob();
					continue;
				}
				projectSuccesfullyDeleted = true;
			}
		}
	}
	
	private void terminateRunningProcesses() throws DebugException {
		if(launch != null && launch.canTerminate()) {
			launch.terminate();
		} else if (launch != null) {
			for (IProcess p : launch.getProcesses()) {
				if (p.canTerminate()) {
					while (!p.isTerminated()) {
						p.terminate();
					}
				}
			}
		}
	}
	
	private SyndesisExtension createDefaultNewSyndesisExtension() {
		SyndesisExtension extension = new SyndesisExtension();
		extension.setSpringBootVersion("1.5.8.RELEASE");
		extension.setCamelVersion("2.20.1");
		extension.setSyndesisVersion("1.2-SNAPSHOT");
		extension.setExtensionId("com.acme.custom");
		extension.setVersion("1.0.0");
		extension.setName("ACME Custom Extension");
		extension.setDescription("ACME Custom Extension Filter");
		extension.setTags(Arrays.asList("test", "acme"));
		return extension;
	}
	
	protected void testProjectCreation(String projectNameSuffix, SyndesisExtension extension, String camelPath, String syndesisPath) throws Exception {
		final String projectName = getClass().getSimpleName() + projectNameSuffix;
		SyndesisExtensionIntegrationTestsActivator.pluginLog().logInfo("Starting creation of the project: "+projectName);
		assertThat(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).exists()).isFalse();

		SyndesisExtension syndesisExtension = extension;
		if (syndesisExtension == null) {
			syndesisExtension = createDefaultNewSyndesisExtension();
		}

		new ProgressMonitorDialog(Display.getDefault().getActiveShell()).run(false, true, new SyndesisExtensionProjectCreatorRunnable(projectName, ResourcesPlugin.getWorkspace().getRoot().getLocation(), true, syndesisExtension));

		project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		assertThat(project.exists()).describedAs("The project "+ project.getName()+ " doesn't exist.").isTrue();
		SyndesisExtensionIntegrationTestsActivator.pluginLog().logInfo("Project created: "+projectName);
		
		final IFile camelResource = project.getFile(Strings.isBlank(camelPath) ? CAMEL_RESOURCE_PATH : camelPath);
		assertThat(camelResource.exists()).isTrue();

		final IFile syndesisResource = project.getFile(Strings.isBlank(syndesisPath) ? SYNDESIS_RESOURCE_PATH : syndesisPath);
		assertThat(syndesisResource.exists()).isTrue();
		
		// TODO: wait for all build job to finish?
		waitJob();

		checkCamelEditorOpened(camelResource);
		waitJob();
		checkJSONEditorOpened(syndesisResource);
		waitJob();
		checkCorrectFacetsEnabled(project);
		waitJob();
		checkCorrectNatureEnabled(project);
		waitForValidationThreads();
		checkNoValidationError();
		checkNoValidationWarning();
		additionalChecks(project);
		
		launchBuild(project, new NullProgressMonitor());
	}

	private void waitForValidationThreads() throws InterruptedException {
		int waitTimeLeft = 30000;
		while(isValidationThreadRunning() && waitTimeLeft > 0) {
			Thread.sleep(100);
			waitTimeLeft -= 100;
		}
		if (waitTimeLeft < 0) {
			SyndesisExtensionIntegrationTestsActivator.pluginLog().logError("The validation thread is still active!");
		}
	}

	protected boolean isValidationThreadRunning() {
		return Thread.getAllStackTraces().keySet().stream()
				.anyMatch(thread -> "org.eclipse.wst.sse.ui.internal.reconcile.StructuredRegionProcessor".equals(thread.getName()));
	}

	protected void additionalChecks(IProject project2) {
	}

	protected void waitJob() {
		JobWaiterUtil jobWaiterUtil = new BuildAndRefreshJobWaiterUtil();
		jobWaiterUtil.setEndless(true);
		jobWaiterUtil.waitJob(new NullProgressMonitor());
	}

	private void checkNoValidationError() throws CoreException {
		checkNoValidationIssueOfType(filterError());
	}
	
	private Predicate<IMarker> filterError(){
		return marker -> {
			try {
				Object severity = marker.getAttribute(IMarker.SEVERITY);
				return severity == null || severity.equals(IMarker.SEVERITY_ERROR);
			} catch (CoreException e1) {
				return true;
			}
		};
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
						&& !message.startsWith("Overriding managed version");
			} catch (CoreException e1) {
				return true;
			}
		};
	}

	private void checkNoValidationIssueOfType(Predicate<IMarker> filter) throws CoreException {
		final IMarker[] markers = project.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		final List<Object> readableMarkers = Arrays.asList(markers).stream()
				.filter(filter)
				.map(marker -> {
						try {
							return extractMarkerInformation(marker);
						} catch (Exception e) {
							SyndesisExtensionIntegrationTestsActivator.pluginLog().logError(e);
							try {
								return "type: "+marker.getType()+"\n"+
										"attributes:\n"+
										marker.getAttributes().entrySet().stream()
							            .map(entry -> entry.getKey() + " - " + entry.getValue())
							            .collect(Collectors.joining(", "));
							} catch (CoreException e1) {
								SyndesisExtensionIntegrationTestsActivator.pluginLog().logError(e1);
								return marker;
							}
						}
					})
				.collect(Collectors.toList());
		assertThat(readableMarkers).isEmpty();
	}

	private Object extractMarkerInformation(IMarker marker) throws CoreException, IOException {
		Map<String, Object> markerInformations = marker.getAttributes() != null ? marker.getAttributes() : new HashMap<>();
		IResource resource = marker.getResource();
		if(resource != null){
			markerInformations.put("resource affected", resource.getLocation().toOSString());
			if(resource instanceof IFile){
				InputStream contents = ((IFile) resource).getContents();
				try (BufferedReader buffer = new BufferedReader(new InputStreamReader(contents))) {
					markerInformations.put("resource affected content", buffer.lines().collect(Collectors.joining("\n")));
				}
			}
		}
		markerInformations.put("type: ", marker.getType());
		markerInformations.put("Creation time: ", marker.getCreationTime());
		return markerInformations;
	}

	/**
	 * @param camelResource
	 * @throws InterruptedException
	 */
	private void checkCamelEditorOpened(IFile camelResource) throws InterruptedException, PartInitException {
		readAndDispatch(0);
		int currentAwaitedTime = 0;
		while (CommonTestUtils.getCurrentOpenEditors().length < 2 && currentAwaitedTime < 30000) {
			Thread.sleep(100);
			currentAwaitedTime += 100;
			System.out.println("awaited activation of editor " + currentAwaitedTime);
		}
		IEditorReference editor = getEditorForFile(camelResource);
		assertThat(editor).as("No editor has been opened.").isNotNull();
		assertThat(editor.isDirty()).as("A newly created project should not have dirty editor.").isFalse();
		
		// if xml context we check if the design editor loads fine
		if ("xml".equalsIgnoreCase(camelResource.getFileExtension()) && editor instanceof CamelEditor) {
			CamelEditor ed = (CamelEditor)editor;
			assertThat(ed.getDesignEditor()).as("The Camel Designer has not been created.").isNotNull();
			assertThat(ed.getDesignEditor().getDiagramTypeProvider()).as("Error retrieving the diagram type provider.").isNotNull();
			assertThat(ed.getDesignEditor().getDiagramTypeProvider().getDiagram()).as("Unable to access the camel context diagram.").isNotNull();
		}
	}
	
	private IEditorReference getEditorForFile(IFile file) throws PartInitException {
		for (IEditorReference ref : CommonTestUtils.getCurrentOpenEditors()) {
			IEditorInput editorInput = ref.getEditorInput();
			if ((editorInput.getAdapter(IFile.class)).equals(file)) {
				return ref;
			}
		}
		return null;
	}
	
	/**
	 * @param syndesisResource
	 * @throws InterruptedException
	 */
	private void checkJSONEditorOpened(IFile syndesisResource) throws InterruptedException, PartInitException {
		readAndDispatch(0);
		int currentAwaitedTime = 0;
		while (CommonTestUtils.getCurrentOpenEditors().length < 2 && currentAwaitedTime < 30000) {
			Thread.sleep(100);
			currentAwaitedTime += 100;
			System.out.println("awaited activation of editor " + currentAwaitedTime);
		}
		IEditorReference editor = getEditorForFile(syndesisResource);
		assertThat(editor).as("No editor has been opened.").isNotNull();
		assertThat(editor.isDirty()).as("A newly created project should not have dirty editor.").isFalse();
	}
	
	private void checkCorrectNatureEnabled(IProject project) throws CoreException {
		assertThat(project.getNature(RiderProjectNature.NATURE_ID)).isNotNull();
	}
	
	protected void checkCorrectFacetsEnabled(IProject project) throws CoreException {
		IFacetedProject fproj = ProjectFacetsManager.create(project);

		boolean javaFacetFound = fproj.hasProjectFacet(javaFacet);
		boolean mavenFacetFound = fproj.hasProjectFacet(m2eFacet);
		boolean utilityFacetFound = fproj.hasProjectFacet(utilFacet);
				
		assertThat(javaFacetFound).isTrue();
		assertThat(mavenFacetFound).isTrue();
		assertThat(utilityFacetFound).isTrue();
		
        checkNoConflictingFacets(fproj);
	}
	
    protected void checkNoConflictingFacets(IFacetedProject fproj) {
    	for (IProjectFacetVersion existingFacetVersion : fproj.getProjectFacets()) {
    		for (IProjectFacetVersion existingFacetVersion2 : fproj.getProjectFacets()) {
    			assertThat(existingFacetVersion.conflictsWith(existingFacetVersion2))
    			.as("2 facets are conflicting: "+existingFacetVersion+ " and "+ existingFacetVersion2)
    			.isFalse();
    		}
    	}
    }

	protected void readAndDispatch(int currentNumberOfTry) {
		CommonTestUtils.readAndDispatch(currentNumberOfTry);
	}

	protected void launchBuild(IProject project, IProgressMonitor monitor) throws CoreException, InterruptedException, IOException, MalformedObjectNameException {
		final File parent = new File("target/MavenLaunchOutputs");
		parent.mkdirs();
		final String mavenOutputFilePath = new File(parent, "MavenLaunchOutput-"+project.getName()+".txt").getAbsolutePath();
		
		IMaven maven = MavenPlugin.getMaven();
		IMavenExecutionContext executionContext = maven.createExecutionContext();
		MavenExecutionRequest executionRequest = executionContext.getExecutionRequest();
		executionRequest.setPom(project.getFile("pom.xml").getLocation().toFile());
		executionRequest.setGoals(Arrays.asList("clean", "verify"));
		MavenExecutionResult result = maven.execute(executionRequest, monitor);
		buildFinished = true;
		buildOK = !result.hasExceptions();
		for (Throwable t : result.getExceptions()) {
			SyndesisExtensionIntegrationTestsActivator.pluginLog().logError(t);
		}
		assertThat(buildOK).as("build failed, you can have a look to the file "+ mavenOutputFilePath + " for more information.").isTrue();
	}

	protected StructuredSelection getSelectionForLaunch(IProject project) {
		return new StructuredSelection(project.getFile("pom.xml"));
	}
}
