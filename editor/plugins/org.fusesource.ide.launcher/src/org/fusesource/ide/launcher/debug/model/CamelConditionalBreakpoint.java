/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.debug.model;

import java.io.File;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IBreakpoint;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.launcher.debug.util.CamelDebugRegistry;
import org.fusesource.ide.launcher.debug.util.CamelDebugRegistryEntry;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;

/**
 * @author lhein
 */
public class CamelConditionalBreakpoint extends CamelEndpointBreakpoint {
	
	protected String language;
	protected String conditionPredicate;
	
	/**
	 * Default constructor is required for the breakpoint manager
	 * to re-create persisted breakpoints. After instantiating a breakpoint,
	 * the <code>setMarker(...)</code> method is called to restore
	 * this breakpoint's attributes.
	 */
	public CamelConditionalBreakpoint() {
	}
	
	/**
	 * 
	 * @param resource
	 * @param endpoint
	 * @param projectName
	 * @param fileName
	 * @param language
	 * @param conditionPredicate
	 * @throws CoreException
	 */
	public CamelConditionalBreakpoint(final IResource resource, final AbstractCamelModelElement endpoint, final String projectName, final String fileName, String language, String conditionPredicate)
			throws CoreException {
		this.endpointNodeId = endpoint.getId();
		this.contextId = endpoint.getRouteContainer().getId();
		this.projectName = projectName;
		this.fileName = fileName;
		this.language = language;
		this.conditionPredicate = conditionPredicate;
		if (resource.getLocation().toFile().getPath().indexOf(String.format("%s%s%starget%s", File.separatorChar, projectName, File.separatorChar, File.separatorChar)) != -1) {
			// seems to be a running context - replace the resource with the correct one
			Iterator<ILaunchConfiguration> launchConfigIterator = CamelDebugRegistry.getInstance().getEntries().keySet().iterator();
			while (launchConfigIterator.hasNext()) {
				ILaunchConfiguration lc = launchConfigIterator.next();
				CamelDebugRegistryEntry entry = CamelDebugRegistry.getInstance().getEntry(lc);
				if (((IFile)entry.getEditorInput().getAdapter(IFile.class)).getFullPath().toFile().getPath().equals(resource.getFullPath().toFile().getPath())) {
					this.resource = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(Path.fromOSString(CamelDebugUtils.getRawCamelContextFilePathFromLaunchConfig(lc)));
				}
			}
		} else {
			this.resource = resource;
		}
		
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.core.resources.IWorkspaceRunnable#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = CamelConditionalBreakpoint.this.resource.createMarker(ICamelDebugConstants.ID_CAMEL_CONDITIONALBREAKPOINT_MARKER_TYPE);
				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IBreakpoint.PERSISTED, Boolean.TRUE);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_CONTEXTID, CamelConditionalBreakpoint.this.contextId);
				marker.setAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_ENDPOINTID, CamelConditionalBreakpoint.this.endpointNodeId);
				marker.setAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_PROJECTNAME, CamelConditionalBreakpoint.this.projectName);
				marker.setAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_FILENAME, CamelConditionalBreakpoint.this.fileName);
				marker.setAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_LANGUAGE, CamelConditionalBreakpoint.this.language);
				marker.setAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_CONDITION, CamelConditionalBreakpoint.this.conditionPredicate);
				marker.setAttribute(IMarker.MESSAGE, "Camel Breakpoint: " + CamelConditionalBreakpoint.this.resource.getName() + " [Endpoint: " + CamelConditionalBreakpoint.this.endpointNodeId + "]");
				setMarker(marker);
			}
		};
		run(getMarkerRule(this.resource), runnable);
		setPersisted(true);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.Breakpoint#setMarker(org.eclipse.core.resources.IMarker)
	 */
	@Override
	public void setMarker(IMarker marker) throws CoreException {
		super.setMarker(marker);
		this.projectName = marker.getAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_PROJECTNAME, this.projectName);
		this.endpointNodeId = marker.getAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_ENDPOINTID, this.endpointNodeId);
		this.fileName = marker.getAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_FILENAME, this.fileName);
		this.contextId = marker.getAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_CONTEXTID, this.contextId);
		this.language = marker.getAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_LANGUAGE, this.language);
		this.conditionPredicate = marker.getAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_CONDITION, this.conditionPredicate);
		this.resource = marker.getResource();
	}
	
	/**
	 * @return the language
	 */
	public String getLanguage() {
		return this.language;
	}
	
	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
	/**
	 * @return the conditionPredicate
	 */
	public String getConditionPredicate() {
		return this.conditionPredicate;
	}
	
	/**
	 * @param conditionPredicate the conditionPredicate to set
	 */
	public void setConditionPredicate(String conditionPredicate) {
		this.conditionPredicate = conditionPredicate;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Camel Conditional Breakpoint [endpointId=%s, project=%s, fileName=%s, contextId=%s, language=%s, condition=%s]", getEndpointNodeId(), getProjectName(), getFileName(), getContextId(), getLanguage(), getConditionPredicate());
	}
}
