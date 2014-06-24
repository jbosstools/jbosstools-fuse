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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IBreakpoint;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.model.CamelEndpointBreakpoint;
import org.fusesource.ide.launcher.debug.model.exchange.BacklogTracerEventMessage;
import org.fusesource.ide.launcher.run.util.CamelContextLaunchConfigConstants;

/**
 * @author lhein
 */
public class CamelDebugUtils {
	
	/**
	 * looks up a breakpoint which fits to the selected endpoint. current matching
	 * logic will be to look for equal ContextId and EndpointId
	 * 
	 * @param endpointId	the endpoint id
	 * @param fileName		the file name
	 * @return				the breakpoint which matches
	 */
	public static IBreakpoint getBreakpointForSelection(String endpointId, String fileName) {
		final IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(ICamelDebugConstants.ID_CAMEL_DEBUG_MODEL);
        for (IBreakpoint breakpoint : breakpoints) {
            final IMarker marker = breakpoint.getMarker();
            String markerType = null;
            try {
                markerType = marker.getType();
            } catch (Exception ex) {
            	Activator.getLogger().error(ex);
            }
            
            if (marker == null || endpointId == null || fileName == null || 
        		!ICamelDebugConstants.ID_CAMEL_MARKER_TYPE.equals(markerType) ||
        		!breakpointMatchesSelection((CamelEndpointBreakpoint) breakpoint, fileName, endpointId)) {
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
	 * @return				true if matched
	 */
	private static boolean breakpointMatchesSelection(final CamelEndpointBreakpoint breakpoint, final String fileName, final String endpointId) {
        return  fileName != null &&
        		endpointId != null &&
        		breakpoint != null &&
        		breakpoint.getEndpointNodeId() != null && 
        		breakpoint.getEndpointNodeId().equalsIgnoreCase(endpointId) && 
        		breakpoint.getFileName() != null &&
        		breakpoint.getFileName().equals(fileName);
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
	public static IBreakpoint createAndRegisterEndpointBreakpoint(IResource resource, AbstractNode endpoint, String projectName, String fileName) throws CoreException {
    	CamelEndpointBreakpoint epb = new CamelEndpointBreakpoint(resource, endpoint, projectName, fileName);
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
}
