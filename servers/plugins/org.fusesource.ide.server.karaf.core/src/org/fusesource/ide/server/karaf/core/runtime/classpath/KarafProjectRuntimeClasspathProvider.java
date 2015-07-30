/*******************************************************************************
 * Copyright (c) 2011-2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.server.karaf.core.runtime.classpath;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jst.server.core.RuntimeClasspathProviderDelegate;
import org.eclipse.wst.server.core.IRuntime;

/**
 * This class is in use for all server types, because legacy projects
 * may have this classpath container ID still enabled. It cannot be changed. 
 * 
 * This class delegates to the "throw everything you can find" utility class,
 * for as6 and below. For as7/wf, it will read both from the 
 * client-all cache, as well as read the project's manifest.mf file
 * for jboss-modules style dependencies that can or should be added. 
 * 
 * This class expects the container path to have 1 
 * additional argument:  the name of the runtime,
 * though that is resolved by the superclass into an
 * actual IRuntime.
 * 
 * This class does not receive any information on facets
 * or facet versions enabled on the project. It is most often
 * used to acquire a classpath for projects that are 
 * NOT facet-based, typically POJP. 
 * 
 * The delegate utility handles caching and manipulating
 * the list of jars into a proper returnable set. The 
 * logic in *discovering* the set of jars is found in 
 * RuntimeJarUtility.
 */
public class KarafProjectRuntimeClasspathProvider 
		extends RuntimeClasspathProviderDelegate {
	
	// The path this container can be found under
	static final IPath CONTAINER_PATH = 
			new Path("org.eclipse.jst.server.core.container") //$NON-NLS-1$
			.append("org.fusesource.ide.server.karaf.core.runtime.classpath.runtimeTarget"); //$NON-NLS-1$
	
	
	public KarafProjectRuntimeClasspathProvider() {
		// Do Nothing
	}

	@Override
	public IClasspathEntry[] resolveClasspathContainer(IProject project, IRuntime runtime) {
		// TODO:   Find somewhere to pull this from based on runtime version
		// It is advisable to cache the result, at least for the default entries
		
		// More work to pull from the classpath defaults via pref page per runtime type... might need api extensions
		System.out.println("Reached resolveClasspathContainer");
		return new IClasspathEntry[0];
	}
}
