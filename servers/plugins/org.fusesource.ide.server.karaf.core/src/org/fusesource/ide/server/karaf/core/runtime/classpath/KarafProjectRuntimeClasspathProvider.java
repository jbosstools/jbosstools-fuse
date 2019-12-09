/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.server.karaf.core.runtime.classpath;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.server.core.RuntimeClasspathProviderDelegate;
import org.eclipse.wst.server.core.IRuntime;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.server.karaf.core.Activator;
import org.jboss.ide.eclipse.as.classpath.core.runtime.CustomRuntimeClasspathModel;
import org.jboss.ide.eclipse.as.classpath.core.runtime.IRuntimePathProvider;
import org.jboss.ide.eclipse.as.classpath.core.runtime.cache.internal.RuntimeClasspathCache;
import org.jboss.ide.eclipse.as.classpath.core.runtime.internal.PathProviderResolutionUtil;

/**
 * This class is in use for all server types, because legacy projects may have
 * this classpath container ID still enabled. It cannot be changed.
 * 
 * This class delegates to the "throw everything you can find" utility class,
 * for as6 and below. For as7/wf, it will read both from the client-all cache,
 * as well as read the project's manifest.mf file for jboss-modules style
 * dependencies that can or should be added.
 * 
 * This class expects the container path to have 1 additional argument: the name
 * of the runtime, though that is resolved by the superclass into an actual
 * IRuntime.
 * 
 * This class does not receive any information on facets or facet versions
 * enabled on the project. It is most often used to acquire a classpath for
 * projects that are NOT facet-based, typically POJP.
 * 
 * The delegate utility handles caching and manipulating the list of jars into a
 * proper returnable set. The logic in *discovering* the set of jars is found in
 * RuntimeJarUtility.
 */
public class KarafProjectRuntimeClasspathProvider extends RuntimeClasspathProviderDelegate {

	// The path this container can be found under
	static final IPath CONTAINER_PATH = new Path("org.eclipse.jst.server.core.container") //$NON-NLS-1$
			.append("org.fusesource.ide.server.karaf.core.runtime.classpath.runtimeTarget"); //$NON-NLS-1$

	public KarafProjectRuntimeClasspathProvider() {
		// Do Nothing
	}

	@Override
	public IClasspathEntry[] resolveClasspathContainer(IProject project, IRuntime runtime) {
		IPath installPath = runtime.getLocation();

		if (installPath == null)
			return new IClasspathEntry[0];

		List<IClasspathEntry> list = new ArrayList<>();

		String runtimeId = runtime.getRuntimeType().getId();
		if (runtimeId.indexOf(".fuseesb.runtime.") != -1 || runtimeId.indexOf(".karaf.runtime.") != -1) {
			IPath libFolder = installPath.append("lib");
			addLibraryEntries(list, libFolder.toFile(), true);
			IPath dataFolder = installPath.append("data").append("cache");
			collectDeployedBundles(list, dataFolder);
		}
		return list.toArray(new IClasspathEntry[list.size()]);
	}

	private void collectDeployedBundles(List<IClasspathEntry> list, IPath folder) {
		// loop all subfolders
		// parse bundle.info to obtain name and version
		// go into the next subfolder and reference the bundle.jar
		// create a classpath entry with good naming reffing the jar
		for (File subFolder : folder.toFile().listFiles(new FileFilter() {
			@Override
			public boolean accept(File subDir) {
				return subDir.isDirectory() && subDir.list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.toLowerCase().trim().equals("bundle.info");
					}
				}).length == 1;
			}
		})) {
			// now we have all folders containing a bundle.info file
			// parse bundle manifest.mf inside the next subfolders jar
			File f = getJarFromFolder(subFolder);
			if (f == null) {
				continue;
			}
			IClasspathEntry cpe = null;
			try (JarFile jf = new JarFile(f)) {
				Manifest mf = jf.getManifest();
				String version = mf.getMainAttributes().getValue("Bundle-Version");
				String symbolicName = mf.getMainAttributes().getValue("Bundle-SymbolicName");

				if (!Strings.isBlank(symbolicName) && !Strings.isBlank(version)) {
					IPath bundleFolder = folder.append(subFolder.getName()).append(f.getParentFile().getName())
							.append(f.getName());
					cpe = JavaCore.newLibraryEntry(bundleFolder, null, null);
				}
			} catch (IOException ex) {
				Activator.getLogger().error(ex);
				continue;
			}

			if (cpe != null)
				list.add(cpe);
		}
	}

	private File getJarFromFolder(File folder) {
		for (File sf : folder.listFiles()) {
			if (sf.isDirectory() && sf.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.trim().equalsIgnoreCase("bundle.jar");
				}
			}).length == 1) {
				// found bundle.jar
				return new File(sf, "bundle.jar");
			}
		}
		return null;
	}

	/*
	 * For as6 and below, pull from the runtime-type model, which is cached once
	 * per runtime-type and is only recached if the list of default path
	 * providers is changed.
	 */
	public IClasspathEntry[] resolveClasspathContainerFromRuntime(IRuntime runtime) {
		if (runtime == null)
			return new IClasspathEntry[0];

		// if cache is available, use cache
		IClasspathEntry[] runtimeClasspath = RuntimeClasspathCache.getInstance().getEntries(runtime);
		if (runtimeClasspath != null) {
			return runtimeClasspath;
		}

		// resolve
		IRuntimePathProvider[] sets = CustomRuntimeClasspathModel.getInstance().getEntries(runtime.getRuntimeType());
		IPath[] allPaths = PathProviderResolutionUtil.getAllPaths(runtime, sets);
		runtimeClasspath = PathProviderResolutionUtil.getClasspathEntriesForResolvedPaths(allPaths);

		RuntimeClasspathCache.getInstance().cacheEntries(runtime, runtimeClasspath);
		return runtimeClasspath;
	}
}
