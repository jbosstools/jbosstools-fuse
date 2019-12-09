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
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IBreakpoint;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.util.CamelDebugRegistry;
import org.fusesource.ide.launcher.debug.util.CamelDebugRegistryEntry;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;

/**
 * Camel Endpoint Breakpoint
 * 
 * @author lhein
 */
public class CamelEndpointBreakpoint extends Breakpoint {
	
	protected String projectName;
	protected String fileName;
	protected String endpointNodeId;
	protected String contextId;
	protected IResource resource;
	
	/**
	 * Default constructor is required for the breakpoint manager
	 * to re-create persisted breakpoints. After instantiating a breakpoint,
	 * the <code>setMarker(...)</code> method is called to restore
	 * this breakpoint's attributes.
	 */
	public CamelEndpointBreakpoint() {
	}
	
	/**
	 * Constructs a breakpoint on the given resource at the given
	 * camel endpoint.
	 * 
	 * @param resource file on which to set the breakpoint
	 * @param endpoint the endpoint
	 * @throws CoreException if unable to create the breakpoint
	 */
	public CamelEndpointBreakpoint(final IResource resource, final AbstractCamelModelElement endpoint, final String projectName, final String fileName)
			throws CoreException {
		this.endpointNodeId = endpoint.getId();
		this.contextId = endpoint.getRouteContainer().getId();
		this.projectName = projectName;
		this.fileName = fileName;
		if (resource.getLocation().toFile().getPath().indexOf(String.format("%s%s%starget%s", File.separatorChar, projectName, File.separatorChar, File.separatorChar)) != -1) {
			// seems to be a running context - replace the resource with the correct one
			Iterator<ILaunchConfiguration> launchConfigIterator = CamelDebugRegistry.getInstance().getEntries().keySet().iterator();
			while (launchConfigIterator.hasNext()) {
				ILaunchConfiguration lc = launchConfigIterator.next();
				CamelDebugRegistryEntry entry = CamelDebugRegistry.getInstance().getEntry(lc);
				if ((entry.getEditorInput().getAdapter(IFile.class)).getFullPath().toFile().getPath().equals(resource.getFullPath().toFile().getPath())) {
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
				IMarker marker = CamelEndpointBreakpoint.this.resource.createMarker(ICamelDebugConstants.ID_CAMEL_BREAKPOINT_MARKER_TYPE);
				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IBreakpoint.PERSISTED, Boolean.TRUE);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_CONTEXTID, CamelEndpointBreakpoint.this.contextId);
				marker.setAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_ENDPOINTID, CamelEndpointBreakpoint.this.endpointNodeId);
				marker.setAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_PROJECTNAME, CamelEndpointBreakpoint.this.projectName);
				marker.setAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_FILENAME, CamelEndpointBreakpoint.this.fileName);
				marker.setAttribute(IMarker.MESSAGE, "Camel Breakpoint: " + CamelEndpointBreakpoint.this.resource.getName() + " [Endpoint: " + CamelEndpointBreakpoint.this.endpointNodeId + "]");
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
		this.resource = marker.getResource();
	}
	
	@Override
	public String getModelIdentifier() {
		return ICamelDebugConstants.ID_CAMEL_DEBUG_MODEL;
	}
	
	/**
	 * @param endpointNodeId the endpointNodeId to set
	 */
	public void setEndpointNodeId(String endpointNodeId) {
		this.endpointNodeId = endpointNodeId;
	}
	
	/**
	 * @return the endpointNodeId
	 */
	public String getEndpointNodeId() {
		return this.endpointNodeId;
	}
	
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return this.fileName;
	}
	
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return this.projectName;
	}
	
	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	/**
	 * @param resource the resource to set
	 */
	public void setResource(IResource resource) {
		this.resource = resource;
	}
	
	/**
	 * @return the resource
	 */
	public IResource getResource() {
		return this.resource;
	}
	
	/**
	 * @return the contextId
	 */
	public String getContextId() {
		return this.contextId;
	}
	
	/**
	 * @param contextId the contextId to set
	 */
	public void setContextId(String contextId) {
		this.contextId = contextId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Camel Breakpoint [endpointId=%s, project=%s, fileName=%s, contextId=%s]", getEndpointNodeId(), getProjectName(), getFileName(), getContextId());
	}

	/**
	 * @param newId
	 */
	public void updateEndpointNodeId(String newId) {
		this.endpointNodeId = newId;
		try {
			final IMarker marker = getMarker();
			if (marker != null) {
				marker.setAttribute(ICamelDebugConstants.MARKER_ATTRIBUTE_ENDPOINTID, CamelEndpointBreakpoint.this.endpointNodeId);
			}
		} catch (CoreException e) {
			Activator.getLogger().error(e);
		}

	}
}
