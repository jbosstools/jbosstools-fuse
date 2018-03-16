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
package org.fusesource.ide.camel.tests.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.fusesource.ide.camel.editor.utils.BuildAndRefreshJobWaiterUtil;
import org.fusesource.ide.camel.editor.utils.JobWaiterUtil;
import org.fusesource.ide.preferences.initializer.StagingRepositoriesPreferenceInitializer;
import org.fusesource.ide.project.RiderProjectNature;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.TestWatcher;

public abstract class AbstractProjectCreatorRunnableIT {

	public static final IProjectFacet javaFacet = ProjectFacetsManager.getProjectFacet("java");
	public static final IProjectFacet m2eFacet = ProjectFacetsManager.getProjectFacet("jboss.m2");
	public static final IProjectFacet utilFacet = ProjectFacetsManager.getProjectFacet("jst.utility");
	public static final IProjectFacet webFacet = ProjectFacetsManager.getProjectFacet("jst.web");
	public static final String SCREENSHOT_FOLDER = "./target/MavenLaunchOutputs";
	
	@Rule
	public TestWatcher printStackTraceOnFailure = new PrintThreadStackOnFailureRule();
	
	protected IProject project = null;
	protected ILaunch launch = null;
	
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
	
	public void waitForProjectDeletion() throws CoreException {
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
	
	public void terminateRunningProcesses() throws DebugException {
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

	protected void waitJob() {
		JobWaiterUtil jobWaiterUtil = new BuildAndRefreshJobWaiterUtil();
		jobWaiterUtil.setEndless(true);
		jobWaiterUtil.waitJob(new NullProgressMonitor());
	}

	protected void readAndDispatch(int currentNumberOfTry) {
		CommonTestUtils.readAndDispatch(currentNumberOfTry);
	}

	protected void checkNoValidationIssueOfType(Predicate<IMarker> filter) throws CoreException {
		final IMarker[] markers = project.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		final List<Object> readableMarkers = Arrays.asList(markers).stream()
				.filter(filter)
				.map(marker -> {
						try {
							return extractMarkerInformation(marker);
						} catch (Exception e) {
							Activator.pluginLog().logError(e);
							try {
								return "type: "+marker.getType()+"\n"+
										"attributes:\n"+
										marker.getAttributes().entrySet().stream()
							            .map(entry -> entry.getKey() + " - " + entry.getValue())
							            .collect(Collectors.joining(", "));
							} catch (CoreException e1) {
								Activator.pluginLog().logError(e1);
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

	protected void checkNoConflictingFacets(IFacetedProject fproj) {
		for (IProjectFacetVersion existingFacetVersion : fproj.getProjectFacets()) {
			for (IProjectFacetVersion existingFacetVersion2 : fproj.getProjectFacets()) {
				assertThat(existingFacetVersion.conflictsWith(existingFacetVersion2))
				.as("2 facets are conflicting: "+existingFacetVersion+ " and "+ existingFacetVersion2)
				.isFalse();
			}
		}
	}

	protected void checkCorrectNatureEnabled(IProject project) throws CoreException {
		assertThat(project.getNature(RiderProjectNature.NATURE_ID)).isNotNull();
	}

	protected void additionalChecks(IProject project) {
	}

	protected void checkNoValidationError() throws CoreException {
		checkNoValidationIssueOfType(filterError());
	}

	private Predicate<IMarker> filterError() {
		return marker -> {
			try {
				Object severity = marker.getAttribute(IMarker.SEVERITY);
				return severity == null || severity.equals(IMarker.SEVERITY_ERROR);
			} catch (CoreException e1) {
				return true;
			}
		};
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
}
