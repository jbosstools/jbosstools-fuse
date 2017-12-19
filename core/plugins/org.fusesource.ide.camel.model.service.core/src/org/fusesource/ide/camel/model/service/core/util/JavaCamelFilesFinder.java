/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.util;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.internal.core.ResolvedSourceType;
import org.eclipse.jdt.internal.corext.refactoring.CollectingSearchRequestor;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;

public class JavaCamelFilesFinder {
	
	private IFile fileToOpen = null;
	
	/**
	 * looks for the first best class extending RouteBuilder and returns it
	 * 
	 * @param project
	 * @param monitor
	 * @return	the routebuilder class or null
	 */
	public IFile findJavaDSLRouteBuilderClass(IProject project, IProgressMonitor monitor) {
		try {
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
		} catch (CoreException e) {
			CamelModelServiceCoreActivator.pluginLog().logError(e);
		}
		try {
			waitJob(20, monitor);
		} catch (OperationCanceledException opEx) {
			CamelModelServiceCoreActivator.pluginLog().logError(opEx);
		} catch (InterruptedException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
			Thread.currentThread().interrupt();
		}
		IJavaProject javaProject = JavaCore.create(project);
		try {
			IType routeBuilderType = javaProject.findType("org.apache.camel.builder.RouteBuilder"); //$NON-NLS-1$
			if (routeBuilderType != null) {
				doSearch(javaProject, routeBuilderType, monitor);
			}
		} catch (Exception ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
		}
		return fileToOpen;
	}
	
	private void doSearch(IJavaProject javaProject, IType routeBuilderType, IProgressMonitor monitor) {
		try{
			IJavaSearchScope searchScope = SearchEngine.createStrictHierarchyScope(javaProject, routeBuilderType, true, false, null);
			CollectingSearchRequestor requestor = new CollectingSearchRequestor();
			// @formatter:off
			final SearchPattern searchPattern = SearchPattern.createPattern("*", IJavaSearchConstants.CLASS, IJavaSearchConstants.IMPLEMENTORS, SearchPattern.R_PATTERN_MATCH); //$NON-NLS-1$
			new SearchEngine().search(searchPattern,
					new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant() },
					searchScope,
					requestor,
					monitor);
			// @formatter:on
			List<SearchMatch> results = requestor.getResults();
			for (SearchMatch searchMatch : results) {
				final Object element = searchMatch.getElement();
				if (element instanceof ResolvedSourceType) {
					fileToOpen = (IFile) ((ResolvedSourceType) element).getCompilationUnit().getCorrespondingResource();
				}
			}}
		catch (Exception e) {
			CamelModelServiceCoreActivator.pluginLog().logError(e);
		}
	}
	
	private static void waitJob(int decreasingCounter, IProgressMonitor monitor) throws InterruptedException {
		if(decreasingCounter > 0){
			return;
		}
		try {
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, monitor);
			Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_REFRESH, monitor);
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_REFRESH, monitor);
			Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_BUILD, monitor);
		} catch (InterruptedException e) {
			// Workaround to bug
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=335251
			waitJob(decreasingCounter-1 , monitor);
			Thread.currentThread().interrupt();
		}
	}

}
