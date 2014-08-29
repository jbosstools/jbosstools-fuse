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
package org.fusesource.ide.jmx.fabric8.utils;

/**
 * @author lhein
 */
public interface IFabric8JsonConstants {
	
	// the default value for undefined property values in json
	static final String PROPERTY_VALUE_UNDEFINED	= "?";
	
	// LogResults JSON
	static final String PROPERTY_LOGRESULTS_HOST 	= "host";
	static final String PROPERTY_LOGRESULTS_FROM 	= "fromTimestamp";
	static final String PROPERTY_LOGRESULTS_TO   	= "toTimestamp";
	static final String PROPERTY_LOGRESULTS_EVENTS	= "events";
	
	// LogEvent JSON
	static final String PROPERTY_LOGEVENT_HOST 		= "host";
	static final String PROPERTY_LOGEVENT_SEQ		= "seq";
	static final String PROPERTY_LOGEVENT_TIMESTAMP = "timestamp";
	static final String PROPERTY_LOGEVENT_LOGLEVEL	= "level";
	static final String PROPERTY_LOGEVENT_LOGGER	= "logger";
	static final String PROPERTY_LOGEVENT_THREAD	= "thread";
	static final String PROPERTY_LOGEVENT_MESSAGE	= "message";
	static final String PROPERTY_LOGEVENT_EXCEPTION = "exception";
	static final String PROPERTY_LOGEVENT_PROPERTIES= "properties";
	static final String PROPERTY_LOGEVENT_CLASS		= "className";
	static final String PROPERTY_LOGEVENT_FILE		= "fileName";
	static final String PROPERTY_LOGEVENT_METHOD	= "methodName";
	static final String PROPERTY_LOGEVENT_CONTAINER = "containerName";
	static final String PROPERTY_LOGEVENT_LINE		= "lineNumber";
	
	// LogEvent.Properties JSON
	static final String PROPERTY_LOGEVENT_PROPERTIES_BUNDLEID 		= "bundle.id";
	static final String PROPERTY_LOGEVENT_PROPERTIES_BUNDLEVERSION 	= "bundle.version";
	static final String PROPERTY_LOGEVENT_PROPERTIES_MAVEN_COORDS	= "maven.coordinates";
	static final String PROPERTY_LOGEVENT_PROPERTIES_BUNDLENAME		= "bundle.name";
	
	
}
