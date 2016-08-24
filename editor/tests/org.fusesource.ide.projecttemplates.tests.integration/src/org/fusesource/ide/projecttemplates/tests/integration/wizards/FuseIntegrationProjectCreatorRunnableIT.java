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
import java.io.IOException;
import java.util.Arrays;
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
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.fusesource.ide.launcher.debug.model.CamelDebugFacade;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.ui.launch.ExecutePomAction;
import org.fusesource.ide.launcher.ui.launch.ExecutePomActionPostProcessor;
import org.fusesource.ide.project.RiderProjectNature;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;
import org.fusesource.ide.projecttemplates.wizards.FuseIntegrationProjectCreatorRunnable;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Aurelien Pupier
 *
 */
public class FuseIntegrationProjectCreatorRunnableIT {

	public static IProjectFacet camelFacet  = ProjectFacetsManager.getProjectFacet("jst.camel");
	public static IProjectFacet javaFacet 	= ProjectFacetsManager.getProjectFacet("java");
	public static IProjectFacet m2eFacet 	= ProjectFacetsManager.getProjectFacet("jboss.m2");
	public static IProjectFacet utilFacet 	= ProjectFacetsManager.getProjectFacet("jst.utility");
    public static IProjectFacet webFacet    = ProjectFacetsManager.getProjectFacet("jst.web"); //$NON-NLS-1$
		
	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	protected IProject project = null;
	boolean deploymentFinished = false;
	boolean isDeploymentOk = false;
	protected ILaunch launch = null;
	protected String camelVersion;

	@Before
	public void setup() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();
		store.setValue(IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE, IDEInternalPreferences.PSPM_ALWAYS);
	}

	@After
	public void tearDown() throws CoreException, InterruptedException, IOException {
		if(launch != null){
			launch.terminate();
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
	}

	protected void testProjectCreation(String projectNameSuffix, CamelDSLType dsl, String camelFilePath, NewProjectMetaData metadata) throws Exception {
		final String projectName = getClass().getSimpleName() + projectNameSuffix;
		assertThat(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).exists()).isFalse();

		if (metadata == null) {
			metadata = createDefaultNewProjectMetadata(dsl, projectName);
		}

		new ProgressMonitorDialog(Display.getDefault().getActiveShell()).run(false, true, new FuseIntegrationProjectCreatorRunnable(metadata));

		project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		assertThat(project.exists()).describedAs("The project "+ project.getName()+ " doesn't exist.").isTrue();
		final IFile camelResource = project.getFile(camelFilePath);
		assertThat(camelResource.exists()).isTrue();

		// TODO: wait for all build job to finish?
		waitJob();

		checkCorrectEditorOpened(camelResource);
		// TODO: fix project to activate no validation error check
		//checkNoValidationError();
		waitJob();
		checkCorrectFacetsEnabled(project);
		waitJob();
		checkCorrectNatureEnabled(project);
		
		if(!CamelDSLType.JAVA.equals(dsl)){
			launchDebug(project);
		} else {
			//TODO: different Run? or implement the java local camel context?
		}
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

	protected void waitJob() throws OperationCanceledException, InterruptedException {
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
		readAndDispatch(0);
		int currentAwaitedTime = 0;
		while (getCurrentActiveEditor() == null && currentAwaitedTime < 30000) {
			Thread.sleep(100);
			currentAwaitedTime += 100;
			System.out.println("awaited activation of editor " + currentAwaitedTime);
		}
		IEditorPart editor = getCurrentActiveEditor();
		IEditorInput editorInput = editor.getEditorInput();
		assertThat(editorInput.getAdapter(IFile.class)).isEqualTo(camelResource);
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
		
		assertThat(fproj.getProjectFacetVersion(camelFacet).getVersionString()).isEqualTo(camelVersion).as("The Camel Facet version is not the right one.");
	}

	protected void readAndDispatch(int currentNumberOfTry) {
		try{
			while (Display.getDefault().readAndDispatch()) {
				
			}
		} catch(SWTException swtException){
			//TODO: remove try catch when https://issues.jboss.org/browse/FUSETOOLS-1913 is done (CI with valid GUI)
			swtException.printStackTrace();
			if(currentNumberOfTry < 100){
				readAndDispatch(currentNumberOfTry ++);
			} else {
				System.out.println("Tried 100 times to wait for UI... Continue and see what happens.");
			}
		}
	}

	/**
	 * @return
	 */
	private IEditorPart getCurrentActiveEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
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
		while (currentAwaitedTime < 30000 && !deploymentFinished) {
			readAndDispatch(0);
			try{
				JMXConnector jmxc = JMXConnectorFactory.connect(new JMXServiceURL(ICamelDebugConstants.DEFAULT_JMX_URI));
				MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
				deploymentFinished = !mbsc.queryMBeans(new ObjectName(CamelDebugFacade.CAMEL_DEBUGGER_MBEAN_DEFAULT), null).isEmpty();
				isDeploymentOk = deploymentFinished;
				System.out.println("JMX connection succeeded");
				System.out.println("isDeployment Finished? " + isDeploymentOk);
				jmxc.close();
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
}
