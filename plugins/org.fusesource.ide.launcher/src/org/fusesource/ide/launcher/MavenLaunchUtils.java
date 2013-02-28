/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

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

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.maven.artifact.Artifact;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenLauncherConfiguration;
import org.eclipse.m2e.core.embedder.MavenRuntime;
import org.eclipse.m2e.core.embedder.MavenRuntimeManager;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MavenLaunchUtils
 * 
 * @author Igor Fedorenko
 */
public class MavenLaunchUtils {

	private static final Logger LOG = LoggerFactory.getLogger(MavenLaunchUtils.class);
	
	public static MavenRuntime getMavenRuntime(
			ILaunchConfiguration configuration) throws CoreException {
		MavenRuntimeManager runtimeManager = MavenPlugin.getMavenRuntimeManager();
		String location = configuration.getAttribute(MavenLaunchConstants.ATTR_RUNTIME, "");
		MavenRuntime runtime = runtimeManager.getRuntime(location);
		if (runtime == null) {
			throw new CoreException(new Status(IStatus.ERROR, IMavenConstants.PLUGIN_ID, -1, "Can't find Maven installation " + location, null));
		}
		return runtime;
	}

	public static String getCliResolver(MavenRuntime runtime)
			throws CoreException {
		String jarname;
		String runtimeVersion = runtime.getVersion();
		if (runtimeVersion.startsWith("3.")) {
			jarname = "org.maven.ide.eclipse.cliresolver30.jar";
		} else {
			jarname = "org.maven.ide.eclipse.cliresolver.jar";
		}
		URL url = Activator.getInstance().getBundle().getEntry(jarname);
		try {
			URL fileURL = FileLocator.toFileURL(url);
			// MNGECLIPSE-804 workaround for spaces in the original path
			URI fileURI = new URI(fileURL.getProtocol(), fileURL.getHost(),
					fileURL.getPath(), fileURL.getQuery());
			return new File(fileURI).getCanonicalPath();
		} catch (Exception ex) {
			throw new CoreException(new Status(IStatus.ERROR,
					IMavenConstants.PLUGIN_ID, -1, ex.getMessage(), ex));
		}
	}

	public static void addUserComponents(ILaunchConfiguration configuration,
			IMavenLauncherConfiguration collector) throws CoreException {
		@SuppressWarnings("unchecked")
		List<String> list = configuration.getAttribute(
				MavenLaunchConstants.ATTR_FORCED_COMPONENTS_LIST,
				new ArrayList<String>());
		if (list == null) {
			return;
		}

		IMaven maven = MavenPlugin.getMaven();
		for (String gav : list) {
			// groupId:artifactId:version
			StringTokenizer st = new StringTokenizer(gav, ":");
			String groupId = st.nextToken();
			String artifactId = st.nextToken();
			String version = st.nextToken();

			IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().getMavenProject(
					groupId, artifactId, version);

			if (facade != null) {
				collector.addProjectEntry(facade);
			} else {
				String name = groupId + ":" + artifactId + ":" + version;
				try {
					Artifact artifact = maven.resolve(groupId, artifactId,
							version, "jar", null, null, null);
					File file = artifact.getFile();
					if (file != null) {
						collector.addArchiveEntry(file.getAbsolutePath());
					}
				} catch (CoreException ex) {
					LOG.error("Artifact not found " + name, ex);
				}
			}
		}
	}
	
	  
	/**
	 * Substitute any variable
	 */
	public static String substituteVar(String s) {
		if (s == null) {
			return s;
		}
		try {
			return VariablesPlugin.getDefault().getStringVariableManager()
					.performStringSubstitution(s);
		} catch (CoreException e) {
			LOG.error("Could not substitute variable {}.", s, e);
			return null;
		}
	}
}