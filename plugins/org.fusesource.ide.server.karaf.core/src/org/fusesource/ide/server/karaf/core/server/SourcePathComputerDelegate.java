/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.server.karaf.core.server;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
/**
 * SourcePathComputer for the Fuse server adapter launch configuration.
 */
public class SourcePathComputerDelegate implements ISourcePathComputerDelegate  {

	public String getId() {
		return "org.fusesource.ide.server.karaf.core.server.sourcePathComputerDelegate";
	}
	/**
	 * Computes the default source lookup path for the launch configuration. The
	 * default source lookup path must contain the project info for applications 
	 * deployed to the server under debug.
	 */
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate#computeSourceContainers(org.eclipse.debug.core.ILaunchConfiguration, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
		
		IRuntimeClasspathEntry[] unresolvedEntries = JavaRuntime.computeUnresolvedSourceLookupPath(configuration);
		
		IServer server = ServerUtil.getServer(configuration);
		if (server == null)
			return null;
		
		IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = wsRoot.getProjects();			
		List<IJavaProject> javaProjectList = new ArrayList<IJavaProject>();
		
		processProjects(projects, javaProjectList, monitor);

		IRuntimeClasspathEntry[] projectEntries = new IRuntimeClasspathEntry[javaProjectList.size()];
		for (int i = 0; i < javaProjectList.size(); i++)
			projectEntries[i] = JavaRuntime.newProjectRuntimeClasspathEntry(javaProjectList.get(i)); 

		IRuntimeClasspathEntry[] entries =  new IRuntimeClasspathEntry[projectEntries.length+unresolvedEntries.length]; 
		System.arraycopy(unresolvedEntries,0,entries,0,unresolvedEntries.length);
		System.arraycopy(projectEntries,0,entries,unresolvedEntries.length,projectEntries.length);
		
		IRuntimeClasspathEntry[] resolved = JavaRuntime.resolveSourceLookupPath(entries, configuration);
		ISourceContainer[] javaSourceContainers = JavaRuntime.getSourceContainers(resolved);
		
		return javaSourceContainers;		
	}

	private void processProjects(IProject[] projects, List<IJavaProject> javaProjectList, IProgressMonitor monitor) {
		
		for (int i = 0; i < projects.length; i++) {
			IProject project = projects[i];
			
			if (project != null && project.isAccessible()) {

				try {
					if (project.hasNature(JavaCore.NATURE_ID)) {
						IJavaProject javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
						
						if (!javaProjectList.contains(javaProject))
							javaProjectList.add(javaProject);
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
