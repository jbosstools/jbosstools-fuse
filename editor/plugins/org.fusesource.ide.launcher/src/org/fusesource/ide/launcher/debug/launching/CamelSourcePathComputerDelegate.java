/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.debug.launching;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.DirectorySourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.ProjectSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.WorkspaceSourceContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;

/**
 * Computes the default source lookup path for a Camel launch configuration.
 * The default source lookup path is the folder or project containing 
 * the Camel context being launched. If the camel context is not specified, the workspace
 * is searched by default.
 * 
 * @author lhein
 */
public class CamelSourcePathComputerDelegate implements ISourcePathComputerDelegate {

	@Override
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
		SubMonitor subMon = SubMonitor.convert(monitor, 2);
		String filePathUri = CamelDebugUtils.getRawCamelContextFilePathFromLaunchConfig(configuration);
		try {
			filePathUri = URLEncoder.encode(filePathUri, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			Activator.getLogger().error(e);
		}
		String filePath = URI.create(filePathUri).getPath();
		
		ISourceContainer sourceContainer = null;
		if (filePath != null) {
			File contextFile = new File(filePath);
			sourceContainer = new DirectorySourceContainer(contextFile.getParentFile(), true);
		}
		
		if (sourceContainer == null) {
			sourceContainer = new WorkspaceSourceContainer();
		}
		
		// Compute the source path for any java-based debug targets.
		ISourceContainer[] javaSourceContainers = computeJavaSourceContainers(configuration, subMon.split(1));
		ISourceContainer[] wsSourceContainers = computeWorkspaceSourceContainers(subMon.split(1));
		ISourceContainer[] sourceContainers = new ISourceContainer[javaSourceContainers.length + wsSourceContainers.length + 1];
		
		System.arraycopy(javaSourceContainers, 0, sourceContainers, 0, javaSourceContainers.length);
		System.arraycopy(wsSourceContainers, 0, sourceContainers, javaSourceContainers.length, wsSourceContainers.length);
		
		sourceContainers[sourceContainers.length-1] = sourceContainer;	
		return sourceContainers;
	}
	
	private ISourceContainer[] computeWorkspaceSourceContainers(IProgressMonitor monitor) {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		ISourceContainer[] containers = new ISourceContainer[projects.length];
		
		SubMonitor subMon = SubMonitor.convert(monitor, projects.length);
		for (int i = 0; i < projects.length; i++) {
			ISourceContainer container = new ProjectSourceContainer(projects[i], false);
			containers[i] = container;
			subMon.worked(1);
		}
		return containers;		
	}
	
	private ISourceContainer[] computeJavaSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
		
		IRuntimeClasspathEntry[] unresolvedEntries = JavaRuntime.computeUnresolvedSourceLookupPath(configuration);		
		IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = wsRoot.getProjects();			
		List<IJavaProject> javaProjectList = new ArrayList<>();
		
		processProjects(projects, javaProjectList, monitor);

		IRuntimeClasspathEntry[] projectEntries = new IRuntimeClasspathEntry[javaProjectList.size()];
		for (int i = 0; i < javaProjectList.size(); i++) {
			projectEntries[i] = JavaRuntime.newProjectRuntimeClasspathEntry(javaProjectList.get(i));
		}

		IRuntimeClasspathEntry[] entries =  new IRuntimeClasspathEntry[projectEntries.length+unresolvedEntries.length]; 
		System.arraycopy(unresolvedEntries,0,entries,0,unresolvedEntries.length);
		System.arraycopy(projectEntries,0,entries,unresolvedEntries.length,projectEntries.length);
		
		IRuntimeClasspathEntry[] resolved = JavaRuntime.resolveSourceLookupPath(entries, configuration);
		return JavaRuntime.getSourceContainers(resolved);
	}

	private void processProjects(IProject[] projects, List<IJavaProject> javaProjectList, IProgressMonitor monitor) {
		SubMonitor subMon = SubMonitor.convert(monitor, projects.length);
		for (int i = 0; i < projects.length; i++) {
			IProject project = projects[i];
			if (project != null && project.isAccessible()) {
				try {
					if (project.hasNature(JavaCore.NATURE_ID)) {
						IJavaProject javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
						if (!javaProjectList.contains(javaProject)) {
							javaProjectList.add(javaProject);
						}
					}
				} catch (CoreException e) {
					Activator.getLogger().error(e);
				}
			}
			subMon.worked(1);
		}
	}	

}
