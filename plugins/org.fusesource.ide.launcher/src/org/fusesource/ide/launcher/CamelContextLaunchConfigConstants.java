/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.launcher;

/**
 * @author lhein
 */
public interface CamelContextLaunchConfigConstants {
	static final String CAMEL_CONTEXT_LAUNCH_CONFIG_TYPE_ID = "org.fusesource.ide.launcher.camelContext";
	static final String CAMEL_CONTEXT_NO_TESTS_LAUNCH_CONFIG_TYPE_ID = "org.fusesource.ide.launcher.camelContextNoTests";
	static final String ATTR_PROTOCOL_PREFIX = "file:";
	static final String ATTR_CONTEXT_FILE = "camel.fileApplicationContextUri";
	static final String ATTR_FILE = "rider.file";
	static final String DEFAULT_CONTEXT_NAME = "camelContext.xml";
	static final String DEFAULT_MAVEN_GOALS = "clean package org.apache.camel:camel-maven-plugin:run";
	static final String BLUEPRINT_CONTEXT = "camel.blueprint=true";
}
