/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.tests.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectImportResult;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;

/**
 * A big part of this code has been retrieved from AsbtractMavenProjectTestCase
 *
 */
public class MavenProjectHelper {


	private static final class MarkerComparatorByLine implements Comparator<IMarker> {
		@Override
		public int compare(IMarker o1, IMarker o2) {
			int lineNumber1 = o1.getAttribute(IMarker.LINE_NUMBER, -1);
			int lineNumber2 = o2.getAttribute(IMarker.LINE_NUMBER, -1);
			if(lineNumber1 < lineNumber2) {
				return -1;
			}
			if(lineNumber1 > lineNumber2) {
				return 1;
			}
			// Markers on the same line
			String message1 = o1.getAttribute(IMarker.MESSAGE, "");
			String message2 = o2.getAttribute(IMarker.MESSAGE, "");
			return message1.compareTo(message2);
		}
	}

	public IProject[] importProjects(File src, String[] pomNames) throws CoreException, IOException {
		MavenModelManager mavenModelManager = MavenPlugin.getMavenModelManager();
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		File dst = new File(root.getLocation().toFile(), src.getName());
		final List<MavenProjectInfo> projectInfos = computeProjectInfos(pomNames, mavenModelManager, dst);
		final ProjectImportConfiguration importConfiguration = new ProjectImportConfiguration(new ResolverConfiguration());
		final List<IMavenProjectImportResult> importResults = importMavenprojects(workspace, projectInfos, importConfiguration);
		return createMavenProjects(projectInfos, importResults);
	}

	private List<IMavenProjectImportResult> importMavenprojects(final IWorkspace workspace, final List<MavenProjectInfo> projectInfos, final ProjectImportConfiguration importConfiguration)
			throws CoreException {
		final List<IMavenProjectImportResult> importResults = new ArrayList<>();

		workspace.run(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				importResults.addAll(MavenPlugin.getProjectConfigurationManager().importProjects(projectInfos, importConfiguration, monitor));
			}
		}, MavenPlugin.getProjectConfigurationManager().getRule(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
		return importResults;
	}

	private IProject[] createMavenProjects(final List<MavenProjectInfo> projectInfos, final List<IMavenProjectImportResult> importResults) throws CoreException {
		IProject[] projects = new IProject[projectInfos.size()];
		for (int i = 0; i < projectInfos.size(); i++) {
			IMavenProjectImportResult importResult = importResults.get(i);
			assertSame(projectInfos.get(i), importResult.getMavenProjectInfo());
			projects[i] = importResult.getProject();
			assertNotNull("Failed to import project " + projectInfos, projects[i]);

			Model model = projectInfos.get(0).getModel();
			IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create(projects[i], new NullProgressMonitor());
			if(facade == null) {
				fail("Project " + model.getGroupId() + "-" + model.getArtifactId() + "-" + model.getVersion()
				+ " was not imported. Errors: "
				+ toString(findErrorMarkers(projects[i])));
			}
		}
		return projects;
	}

	private List<MavenProjectInfo> computeProjectInfos(String[] pomNames, MavenModelManager mavenModelManager, File dst)
			throws CoreException, IOException {
		final List<MavenProjectInfo> projectInfos = new ArrayList<>();
		for(String pomName : pomNames) {
			File pomFile = new File(dst, pomName);
			Model model = mavenModelManager.readMavenModel(pomFile);
			MavenProjectInfo projectInfo = new MavenProjectInfo(pomName, pomFile, model, null);
			setBasedirRename(projectInfo);
			projectInfos.add(projectInfo);
		}
		return projectInfos;
	}

	private void setBasedirRename(MavenProjectInfo projectInfo) throws IOException {
		File workspaceRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
		File basedir = projectInfo.getPomFile().getParentFile().getCanonicalFile();

		projectInfo.setBasedirRename(basedir.getParentFile().equals(workspaceRoot)? MavenProjectInfo.RENAME_REQUIRED: MavenProjectInfo.RENAME_NO);
	}

	private static String toString(List<IMarker> markers) {
		String sep = "";
		StringBuilder sb = new StringBuilder();
		if(markers != null) {
			for(IMarker marker : markers) {
				sb.append(sep).append(toString(marker));
				sep = ", ";
			}
		}
		return sb.toString();
	}

	private static List<IMarker> findErrorMarkers(IProject project) throws CoreException {
		return findMarkers(project, IMarker.SEVERITY_ERROR);
	}

	private static String toString(IMarker marker) {
		try {
			return "Type=" + marker.getType() + ":Message=" + marker.getAttribute(IMarker.MESSAGE) + ":LineNumber=" + marker.getAttribute(IMarker.LINE_NUMBER);
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.ID, e.getMessage(), e));
			return "Error computing string reprsentation of "+ marker.toString() + " "+ e.getMessage();
		}
	}

	private static List<IMarker> findMarkers(IProject project, int targetSeverity)
			throws CoreException {
		SortedMap<IMarker, IMarker> errors = new TreeMap<>(new MarkerComparatorByLine());
		for(IMarker marker : project.findMarkers(null /* all markers */, true /* subtypes */, IResource.DEPTH_INFINITE)) {
			int severity = marker.getAttribute(IMarker.SEVERITY, 0);
			if(severity != targetSeverity) {
				continue;
			}
			errors.put(marker, marker);
		}
		List<IMarker> result = new ArrayList<>();
		result.addAll(errors.keySet());
		return result;
	}

}
