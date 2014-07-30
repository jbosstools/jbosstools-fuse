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

/**
 * Constants for the Camel debugger.
 * 
 * @author lhein
 */
public interface ICamelDebugConstants {
	
	/**
	 * Unique identifier for the Camel debug model (value 
	 * <code>org.fusesource.ide.launcher.debug.camel</code>).
	 */
	public static final String ID_CAMEL_DEBUG_MODEL = "org.fusesource.ide.launcher.debug.camel";

	public static final String ID_CAMEL_BREAKPOINT_MARKER_TYPE = "org.fusesource.ide.launcher.debug.camel.breakpoint.marker";
	public static final String ID_CAMEL_CONDITIONALBREAKPOINT_MARKER_TYPE = "org.fusesource.ide.launcher.debug.camel.conditionalbreakpoint.marker";

	// launch config attribute id's for the JMX information
	public static final String ATTR_JMX_URI_ID 		= "org.fusesource.ide.launcher.debug.jmx.uri";
	public static final String ATTR_JMX_USER_ID 	= "org.fusesource.ide.launcher.debug.jmx.user";
	public static final String ATTR_JMX_PASSWORD_ID = "org.fusesource.ide.launcher.debug.jmx.passwd";
	public static final String ATTR_REMOTE_CONTEXT  = "org.fusesource.ide.launcher.debug.remote.context";	
	
	// marker attributes
	public static final String MARKER_ATTRIBUTE_FILENAME  		= "org.fusesource.ide.launcher.debug.marker.filename";
	public static final String MARKER_ATTRIBUTE_PROJECTNAME  	= "org.fusesource.ide.launcher.debug.marker.projectname";
	public static final String MARKER_ATTRIBUTE_ENDPOINTID  	= "org.fusesource.ide.launcher.debug.marker.endpointid";
	public static final String MARKER_ATTRIBUTE_CONTEXTID  		= "org.fusesource.ide.launcher.debug.marker.contextid";
	public static final String MARKER_ATTRIBUTE_LANGUAGE  		= "org.fusesource.ide.launcher.debug.marker.language";
	public static final String MARKER_ATTRIBUTE_CONDITION  		= "org.fusesource.ide.launcher.debug.marker.condition";
		
	public static final String CAMEL_CONTEXT_CONTENT_TYPE_SPRING = "spring";
	public static final String CAMEL_CONTEXT_CONTENT_TYPE_BLUEPRINT = "blueprint";
	
	public static final String CAMEL_EDITOR_ID 		= "org.fusesource.ide.camel.editor";
	public static final String DEBUG_VIEW_ID		= "org.eclipse.debug.ui.DebugView";

	// prefixes for auto generated ID fields
	public static final String PREFIX_GENERAL		= ""; // "debugger_";
	public static final String PREFIX_CONTEXT_ID	= PREFIX_GENERAL + "context-";
	public static final String PREFIX_NODE_ID		= PREFIX_GENERAL + ""; //"breakpoint_";
	
	public static final String DEFAULT_JMX_URI 		= "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi/camel";
}
