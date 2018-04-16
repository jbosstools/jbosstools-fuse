/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.wizards.pages.filter;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.wst.server.core.IRuntime;

public class WildflyRuntimeFilter extends RuntimeViewerFilter {

	public static final String EAP_RUNTIME_ID_PREFIX = "org.jboss.ide.eclipse.as.runtime.eap";
	public static final String WILDFLY_RUNTIME_ID_PREFIX = "org.jboss.ide.eclipse.as.runtime.wildfly";

	public WildflyRuntimeFilter(Map<String, IRuntime> serverRuntimes) {
		super(serverRuntimes, Arrays.asList(WILDFLY_RUNTIME_ID_PREFIX, EAP_RUNTIME_ID_PREFIX));
	}
}
