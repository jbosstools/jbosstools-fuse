/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;

/**
 * @author lhein
 */
public class ProjectClasspathChangedListener implements IElementChangedListener, IResourceChangeListener {

	private Map<IProject, String> knownProjects = new HashMap<>();
	private boolean isActivated = true;
	
	/**
	 * creates a change listener watching for events in the classpath of the project
	 */
	public ProjectClasspathChangedListener() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				for (IJavaProject jp : getCamelProjects()) {
					initializeProject(jp.getProject());
				}
			}
		});
	}
	
	@Override
	public void elementChanged(ElementChangedEvent event) {
		if (isActivated) {
			visit(event.getDelta());
		}
	}

	@Override
    public void resourceChanged(IResourceChangeEvent event) {
		IResource rsrc = event.getResource();
		if (rsrc instanceof IProject) {
			IProject prj = (IProject)rsrc;
			if (event.getType() == IResourceChangeEvent.PRE_CLOSE || 
					event.getType() == IResourceChangeEvent.PRE_DELETE) {
				// closed and deleted projects are not considered
				knownProjects.remove(prj);
			} else {
				if (isActivated && !knownProjects.containsKey(prj)) {
					initializeProject(prj);
				}
			}            	
		}
	}
	
	private void initializeProject(IProject project) {
		notifyClasspathChanged(getJavaProjectForProject(project));
	}
	
	private void visitChildren(IJavaElementDelta delta) {
		for (IJavaElementDelta c : delta.getAffectedChildren()) {
			visit(c);
		}
	}
	
	private static List<IJavaProject> getCamelProjects() {
		List<IJavaProject> projectList = new ArrayList<>();
		try {
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			IProject[] projects = workspaceRoot.getProjects();
			for (int i = 0; i < projects.length; i++) {
				IProject project = projects[i];
				if (isOpenJavaProject(project) && isOpenCamelProject(project)) {
					projectList.add(getJavaProjectForProject(project));
				}
			}
		} catch (CoreException ce) {
			CamelModelServiceCoreActivator.pluginLog().logError(ce);
		}
		return projectList;
	}

	private boolean isClasspathChanged(int flags) {
		return 0 != (flags & (IJavaElementDelta.F_CLASSPATH_CHANGED | IJavaElementDelta.F_RESOLVED_CLASSPATH_CHANGED));
	}
	
	private static IJavaProject getJavaProjectForProject(IProject project) {
		return JavaCore.create(project);
	}
	
	private static boolean isOpenJavaProject(IProject project) throws CoreException {
		return project.isAccessible() && project.hasNature(JavaCore.NATURE_ID);
	}
	
	private static boolean isOpenCamelProject(IProject project) throws CoreException {
		return project.isAccessible() && project.hasNature("org.fusesource.ide.project.RiderProjectNature");
	}

	private void visit(IJavaElementDelta delta) {
		IJavaElement el = delta.getElement();
		switch (el.getElementType()) {
			case IJavaElement.JAVA_MODEL:
				visitChildren(delta);
				break;
			case IJavaElement.JAVA_PROJECT:
				if (isClasspathChanged(delta.getFlags())) {
					notifyClasspathChanged((IJavaProject) el);
				}
				break;
			default:
				break;
		}
	}
	
	private void notifyClasspathChanged(IJavaProject project) {
		// refresh catalog if needed
		IProject prj = project.getProject();
		String camelVersion = new CamelMavenUtils().getCamelVersionFromMaven(prj, false);
		if (camelVersion != null) {
			boolean camelVersionChanged = true;
			String oldCamelVersion = knownProjects.get(prj);
			if (!knownProjects.containsKey(prj)) {
				knownProjects.put(prj, camelVersion);
			} else {
				camelVersionChanged = !camelVersion.equals(oldCamelVersion);
			}
			if (camelVersionChanged) {
				knownProjects.put(prj, camelVersion);
				CamelCatalogCacheManager.getInstance().getCamelModelForProject(prj, new NullProgressMonitor());
			}
		}
	}

	public void deactivate() {
		this.isActivated = false;
	}

	public void activate() {
		this.isActivated = true;
	}
}
