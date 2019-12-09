/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.wizards.pages.filter;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.wst.server.core.IRuntime;

public class KarafRuntimeFilter extends RuntimeViewerFilter {

	public static final String KARAF_RUNTIME_ID_PREFIX = "org.fusesource.ide.karaf.runtime";
	public static final String FUSE_RUNTIME_ID_PREFIX = "org.fusesource.ide.fuseesb.runtime";

	public KarafRuntimeFilter(Map<String, IRuntime> serverRuntimes) {
		super(serverRuntimes, Arrays.asList(FUSE_RUNTIME_ID_PREFIX, KARAF_RUNTIME_ID_PREFIX));
	}
}
