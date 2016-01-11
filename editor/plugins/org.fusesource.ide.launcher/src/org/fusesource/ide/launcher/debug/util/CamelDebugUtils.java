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
package org.fusesource.ide.launcher.debug.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.model.CamelConditionalBreakpoint;
import org.fusesource.ide.launcher.debug.model.CamelEndpointBreakpoint;
import org.fusesource.ide.launcher.debug.model.exchange.BacklogTracerEventMessage;
import org.fusesource.ide.launcher.run.util.CamelContextLaunchConfigConstants;

/**
 * @author lhein
 */
public class CamelDebugUtils {
	
	/**
	 * returns all breakpoints fitting the given project and file name
	 * 
	 * @param fileName		the context file name
	 * @param projectName	the project name
	 * @return	a list of breakpoints which might be empty but never null
	 */
	public static IBreakpoint[] getBreakpointsForContext(String fileName, String projectName) {
		ArrayList<IBreakpoint> breakpointsFound = new ArrayList<IBreakpoint>();
		final IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(ICamelDebugConstants.ID_CAMEL_DEBUG_MODEL);
		for (IBreakpoint breakpoint : breakpoints) {
			CamelEndpointBreakpoint ceb = (CamelEndpointBreakpoint)breakpoint;
			if (fileName != null &&
        		projectName != null &&
        		ceb != null &&
				ceb.getFileName() != null &&
				ceb.getFileName().equals(fileName) &&
				ceb.getProjectName() != null &&
				ceb.getProjectName().equals(projectName)) {
				// match - add to found breakpoints
				breakpointsFound.add(breakpoint);
			}
        }
		return breakpointsFound.toArray(new IBreakpoint[breakpointsFound.size()]);
	}
	
	/**
	 * looks up a breakpoint which fits to the selected endpoint. current matching
	 * logic will be to look for equal ContextId and EndpointId
	 * 
	 * @param endpointId	the endpoint id
	 * @param fileName		the file name
	 * @param projectName	the project name
	 * @return				the breakpoint which matches
	 */
	public static IBreakpoint getBreakpointForSelection(String endpointId, String fileName, String projectName) {
		final IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(ICamelDebugConstants.ID_CAMEL_DEBUG_MODEL);
        for (IBreakpoint breakpoint : breakpoints) {
            final IMarker marker = breakpoint.getMarker();
            String markerType = null;
            try {
                markerType = marker.getType();
            } catch (Exception ex) {
            	Activator.getLogger().error(ex);
            }
            
            if (marker == null || 
            	!(ICamelDebugConstants.ID_CAMEL_BREAKPOINT_MARKER_TYPE.equals(markerType) || ICamelDebugConstants.ID_CAMEL_CONDITIONALBREAKPOINT_MARKER_TYPE.equals(markerType)) ||
        		!breakpointMatchesSelection((CamelEndpointBreakpoint) breakpoint, fileName, endpointId, projectName)) {
            	continue;
            }
            return breakpoint;
        }
        return null;
	}

	/**
	 * checks if the breakpoint matches the context id and endpoint id which is considered a unique key together
	 * 
	 * @param breakpoint	the breakpoint to check
	 * @param fileName		the file name to check for
	 * @param endpointId	the endpoint id to check for
	 * @param projectName	the project name
	 * @return				true if matched
	 */
	private static boolean breakpointMatchesSelection(final CamelEndpointBreakpoint breakpoint, final String fileName, final String endpointId, final String projectName) {
        return  fileName != null &&
        		endpointId != null &&
        		projectName != null &&
        		breakpoint != null &&
        		breakpoint.getEndpointNodeId() != null && 
        		breakpoint.getEndpointNodeId().equalsIgnoreCase(endpointId) && 
        		breakpoint.getFileName() != null &&
        		breakpoint.getFileName().equals(fileName) &&
        		breakpoint.getProjectName() != null &&
        		breakpoint.getProjectName().equals(projectName);
    }
	
	/**
	 * creates and registers a breakpoint for the given resource and endpoint
	 * 
	 * @param resource		the camel context file resource
	 * @param endpoint		the endpoint id
	 * @param projectName	the name of the project
	 * @param fileName		the name of the camel context file
	 * @return				the created breakpoint
	 * @throws CoreException
	 */
	public static IBreakpoint createAndRegisterEndpointBreakpoint(IResource resource, CamelModelElement endpoint, String projectName, String fileName) throws CoreException {
    	CamelEndpointBreakpoint epb = new CamelEndpointBreakpoint(resource, endpoint, projectName, fileName);
    	DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(epb);
    	return epb;
    }
	
	/**
	 * creates and registers a breakpoint for the given resource and endpoint
	 * 
	 * @param resource		the camel context file resource
	 * @param endpoint		the endpoint id
	 * @param projectName	the name of the project
	 * @param fileName		the name of the camel context file
	 * @param language		the language of the condition
	 * @param condition		the condition
	 * @return				the created breakpoint
	 * @throws CoreException
	 */
	public static IBreakpoint createAndRegisterConditionalBreakpoint(IResource resource, CamelModelElement endpoint, String projectName, String fileName, String language, String condition) throws CoreException {
    	CamelConditionalBreakpoint epb = new CamelConditionalBreakpoint(resource, endpoint, projectName, fileName, language, condition);
    	DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(epb);
    	return epb;
    }
	
	/**
	 * extracts the endpoints node id from a breakpoint
	 * 
	 * @param breakpoint	the breakpoint
	 * @return	the node id or null if not set or unsupported breakpoint type
	 */
	public static String getEndpointNodeId(IBreakpoint breakpoint) {
		if (breakpoint instanceof CamelEndpointBreakpoint) {
			return ((CamelEndpointBreakpoint)breakpoint).getEndpointNodeId();	
		}
		return null;
	}
	
	/**
	 * extracts the language from a conditional breakpoint
	 * 
	 * @param breakpoint	the breakpoint
	 * @return	the language or null if not set or unsupported breakpoint type
	 */
	public static String getLanguage(IBreakpoint breakpoint) {
		if (breakpoint instanceof CamelConditionalBreakpoint) {
			return ((CamelConditionalBreakpoint)breakpoint).getLanguage();	
		}
		return null;
	}
	
	/**
	 * extracts the condition from a conditional breakpoint
	 * 
	 * @param breakpoint	the breakpoint
	 * @return	the condition or null if not set or unsupported breakpoint type
	 */
	public static String getCondition(IBreakpoint breakpoint) {
		if (breakpoint instanceof CamelConditionalBreakpoint) {
			return ((CamelConditionalBreakpoint)breakpoint).getConditionPredicate();	
		}
		return null;
	}
	
	/**
	 * creates the backlog tracer event message for a given xml dump
	 * 
	 * @param xmlDump	the xml dump of the message
	 * @return	the message object or null on errors
	 */
	public static BacklogTracerEventMessage getBacklogTracerEventMessage(String xmlDump) {
		try {
			// create JAXB context and instantiate marshaller
		    JAXBContext context = JAXBContext.newInstance(BacklogTracerEventMessage.class);
		    Unmarshaller um = context.createUnmarshaller();
		    return (BacklogTracerEventMessage) um.unmarshal(new StringReader(xmlDump));
		} catch (JAXBException ex) {
			Activator.getLogger().error(ex);
		}
		return null;
	}
	
	/**
	 * retrieves the project a file is stored in
	 * 
	 * @param filePath	the file path
	 * @return		the project or null if not able to determine a project
	 */
	public static IProject getProjectForFilePath(String filePath) {
		if (Strings.isBlank(filePath)) return null;
		String checkPath = filePath;
		IFile contextFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(Path.fromOSString(checkPath));
		if (contextFile != null) {
			return contextFile.getProject();
		}
		return null;
	}
	
	/**
	 * retrieves the launched camel context file from the launch config and removes a possible prefix "file:"
	 * 
	 * @param launchConfig 	the launch config
	 * @return	null if not determinable, the file path as string otherwise
	 */
	public static String getRawCamelContextFilePathFromLaunchConfig(ILaunchConfiguration launchConfig) {
		if (launchConfig != null) {
			try {
				String fileUnderDebug = launchConfig.getAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, (String)null);
				if (fileUnderDebug.startsWith("file:")) fileUnderDebug = fileUnderDebug.substring("file:".length());
				return fileUnderDebug;
			} catch (CoreException ex) {
				Activator.getLogger().error(ex);
			}
		}
		return null;
	}
	
	/**
	 * Fetch the list of files in the project that match the camel content type. 
	 * This method looks at only the source folders if the project is a Java project.
	 * @param project
	 * @return list of camel files with content-type org.fusesource.ide.camel.editor.camelContentType
	 * @throws CoreException
	 */
	public static List<IFile> getFilesWithCamelContentType(IProject project) throws CoreException{ 
		final List<IFile> files = new ArrayList<IFile>();
		if (project.hasNature(JavaCore.NATURE_ID)) {
			//limit the search to source folders
	        IJavaProject javaProject = JavaCore.create(project);
	        if(javaProject!=null){
	        	for(IPackageFragmentRoot ifr:javaProject.getAllPackageFragmentRoots()){
	        		if(ifr.getKind()==IPackageFragmentRoot.K_SOURCE){
	        			files.addAll(getFilesWithCamelContentType(ifr.getCorrespondingResource()));
	        		}
	        	}
	        }
	    } else {
	    	files.addAll(getFilesWithCamelContentType(project));//most likely NA
	    }	
		return files;
	}
	
	private static List<IFile> getFilesWithCamelContentType(IResource root) throws CoreException{ 
		final List<IFile> files = new ArrayList<IFile>();
		if(root!=null){			
			root.accept(new IResourceVisitor() {		
				@Override
				public boolean visit(IResource resource) throws CoreException {
					if(resource instanceof IFile){
						IFile file = (IFile)resource;
						IContentDescription contentDescription  = null;
						try{
							contentDescription  = file.getContentDescription();
						} catch (CoreException e) {
							if (e.getStatus().getCode() == IResourceStatus.OUT_OF_SYNC_LOCAL) {
								//refresh and retry once
								resource.refreshLocal(IResource.DEPTH_ONE, null);
								contentDescription  = file.getContentDescription();
							} else {
								throw e;
							}
						}						
						if (contentDescription != null
								&& "org.fusesource.ide.camel.editor.camelContentType"
										.equals(contentDescription.getContentType().getId())) {
							files.add(file);
						}
					}
					return true; //depth infinite
				}
			});
		}
		return files;
	}
}
