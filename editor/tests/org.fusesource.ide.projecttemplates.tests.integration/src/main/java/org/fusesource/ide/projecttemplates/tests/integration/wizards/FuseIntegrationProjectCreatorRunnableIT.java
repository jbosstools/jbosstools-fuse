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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.editor.utils.BuildAndRefreshJobWaiterUtil;
import org.fusesource.ide.camel.editor.utils.JobWaiterUtil;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.tests.util.CommonTestUtils;
import org.fusesource.ide.foundation.ui.util.ScreenshotUtil;
import org.fusesource.ide.launcher.debug.model.CamelDebugFacade;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.model.ThreadGarbageCollector;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.ui.launch.ExecutePomAction;
import org.fusesource.ide.launcher.ui.launch.ExecutePomActionPostProcessor;
import org.fusesource.ide.preferences.initializer.StagingRepositoriesPreferenceInitializer;
import org.fusesource.ide.project.RiderProjectNature;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.maven.CamelProjectConfigurator;
import org.fusesource.ide.projecttemplates.tests.integration.ProjectTemplatesIntegrationTestsActivator;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;
import org.fusesource.ide.projecttemplates.wizards.FuseIntegrationProjectCreatorRunnable;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;

/**
 * @author Aurelien Pupier
 *
 */
public abstract class FuseIntegrationProjectCreatorRunnableIT {

	public static IProjectFacet camelFacet  = ProjectFacetsManager.getProjectFacet("jst.camel");
	public static IProjectFacet javaFacet 	= ProjectFacetsManager.getProjectFacet("java");
	public static IProjectFacet m2eFacet 	= ProjectFacetsManager.getProjectFacet("jboss.m2");
	public static IProjectFacet utilFacet 	= ProjectFacetsManager.getProjectFacet("jst.utility");
    public static IProjectFacet webFacet    = ProjectFacetsManager.getProjectFacet("jst.web"); //$NON-NLS-1$
		
    public static final String SCREENSHOT_FOLDER = "./target/MavenLaunchOutputs";
    
	@Rule
	public TestWatcher printStackTraceOnFailure = new PrintThreadStackOnFailureRule();

	protected IProject project = null;
	boolean deploymentFinished = false;
	boolean isDeploymentOk = false;
	protected ILaunch launch = null;
	protected String camelVersion;

	@Before
	public void setup() throws Exception {
		ProjectTemplatesIntegrationTestsActivator.pluginLog().logInfo("Starting setup for "+ FuseIntegrationProjectCreatorRunnableIT.class.getSimpleName());
		CommonTestUtils.prepareIntegrationTestLaunch(SCREENSHOT_FOLDER);

//		if("2.18.1.redhat-000015".equals(camelVersion) /*|| "2.17.0.redhat-630254".equals(camelVersion)*/){
//			new StagingRepositoriesPreferenceInitializer().setStagingRepositoriesEnablement(true);
//		}
		
		String projectName = project != null ? project.getName() : String.format("%s-%s", getClass().getSimpleName(), camelVersion);
		ScreenshotUtil.saveScreenshotToFile(String.format("%s/MavenLaunchOutput-%s_BEFORE.png", SCREENSHOT_FOLDER, projectName), SWT.IMAGE_PNG);
		
		//No staging repository currently
//		if("2.18.1.redhat-000012".equals(camelVersion) || "2.17.0.redhat-630224".equals(camelVersion)){
//			new StagingRepositoriesPreferenceInitializer().setStagingRepositoriesEnablement(true);
//		}

		ProjectTemplatesIntegrationTestsActivator.pluginLog().logInfo("End setup for "+ FuseIntegrationProjectCreatorRunnableIT.class.getSimpleName());
	}

	@After
	public void tearDown() throws CoreException, InterruptedException, IOException {
		if(launch != null) {
			if (launch.canTerminate()) {
				launch.terminate();
			} else {
				for (IProcess p : launch.getProcesses()) {
					if (p.canTerminate()) {
						while (!p.isTerminated()) {
							p.terminate();
						}
					}
				}
			}
		}
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
		
		// kill all running jobs
		Job.getJobManager().cancel(ResourcesPlugin.FAMILY_AUTO_BUILD);
		Job.getJobManager().cancel(ResourcesPlugin.FAMILY_MANUAL_REFRESH);
		Job.getJobManager().cancel(ResourcesPlugin.FAMILY_AUTO_REFRESH);
		Job.getJobManager().cancel(ResourcesPlugin.FAMILY_MANUAL_BUILD);
		
		CommonTestUtils.closeAllEditors();
		new StagingRepositoriesPreferenceInitializer().setStagingRepositoriesEnablement(false);
	}
	
	protected void testProjectCreation(String projectNameSuffix, CamelDSLType dsl, String camelFilePath, NewProjectMetaData metadata) throws Exception {
		final String projectName = getClass().getSimpleName() + projectNameSuffix;
		ProjectTemplatesIntegrationTestsActivator.pluginLog().logInfo("Starting creation of the project: "+projectName);
		assertThat(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).exists()).isFalse();

		if (metadata == null) {
			metadata = createDefaultNewProjectMetadata(dsl, projectName);
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

	protected void additionalChecks(IProject project2) {
	}

	/**
	 * @param dsl
	 * @param projectName
	 * @return
	 */
	protected NewProjectMetaData createDefaultNewProjectMetadata(CamelDSLType dsl, final String projectName) {
		NewProjectMetaData metadata;
		metadata = new NewProjectMetaData();
		metadata.setProjectName(projectName);
		metadata.setLocationPath(null);
		// TODO use latest version, or a parameterized test to test all versions
		// available CamelModelFactory.getLatestCamelVersion()
		metadata.setCamelVersion(camelVersion);
		metadata.setTargetRuntime(null);
		metadata.setDslType(dsl);
		metadata.setBlankProject(true);
		metadata.setTemplate(null);
		return metadata;
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
						&& (!message.startsWith("Overriding managed version") || (camelVersion.contains("redhat") && !CamelCatalogUtils.isPureFISVersion(camelVersion)));
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
							ProjectTemplatesIntegrationTestsActivator.pluginLog().logError(e);
							try {
								return "type: "+marker.getType()+"\n"+
										"attributes:\n"+
										marker.getAttributes().entrySet().stream()
							            .map(entry -> entry.getKey() + " - " + entry.getValue())
							            .collect(Collectors.joining(", "));
							} catch (CoreException e1) {
								ProjectTemplatesIntegrationTestsActivator.pluginLog().logError(e1);
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
	
	private void checkCorrectNatureEnabled(IProject project) throws CoreException {
		assertThat(project.getNature(RiderProjectNature.NATURE_ID)).isNotNull();
	}
	
	protected void checkCorrectFacetsEnabled(IProject project) throws CoreException {
		IFacetedProject fproj = ProjectFacetsManager.create(project);

		boolean camelFacetFound = fproj.hasProjectFacet(camelFacet);
		boolean javaFacetFound = fproj.hasProjectFacet(javaFacet);
		boolean mavenFacetFound = fproj.hasProjectFacet(m2eFacet);
		boolean utilityFacetFound = fproj.hasProjectFacet(utilFacet);
				
		assertThat(camelFacetFound).isTrue();
		assertThat(javaFacetFound).isTrue();
		assertThat(mavenFacetFound).isTrue();
		assertThat(utilityFacetFound).isTrue();
		
		assertThat(fproj.getProjectFacetVersion(camelFacet).getVersionString()).isEqualTo(CamelProjectConfigurator.DEFAULT_CAMEL_FACET_VERSION).as("The Camel Facet version is not the right one.");
		
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
				additionalMavenAttributes(workingCopy);
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
			JMXConnector jmxc = null;
			try{
				jmxc = JMXConnectorFactory.connect(new JMXServiceURL(ICamelDebugConstants.DEFAULT_JMX_URI));
				MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
				deploymentFinished = !mbsc.queryMBeans(new ObjectName(CamelDebugFacade.CAMEL_DEBUGGER_MBEAN_DEFAULT), null).isEmpty();
				isDeploymentOk = deploymentFinished;
				System.out.println("JMX connection succeeded");
				System.out.println("isDeployment Finished? " + isDeploymentOk);
			} catch(IOException ioe){
				System.out.println("JMX connection attempt failed");
			} finally {
				if (jmxc != null) {
					jmxc.close();
				}
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
	
	/**
	 * call workingCopy.setAttribute(key, value) in the subclass if needed
	 * 
	 * @param workingCopy
	 */
	protected void additionalMavenAttributes(ILaunchConfigurationWorkingCopy workingCopy) {
		/* Used in subclass*/		
	}
}
