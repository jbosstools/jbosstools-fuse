/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.server.subsystems.publish;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.wst.server.core.IModule;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.util.KarafUtils;

public class ModuleBundleVersionUtility {

	public static class BundleDetails {
		private String symbolicName, version;

		public BundleDetails(String symbolicName, String version) {
			this.symbolicName = symbolicName;
			this.version = version;
		}
		public String getSymbolicName() {
			return symbolicName;
		}
		public String getVersion() {
			return version;
		}
	}
	
	public BundleDetails getBundleDetails(IModule[] module, IPath srcFile) {
		String moduleTypeId = module[0].getModuleType().getId(); 
		if( moduleTypeId.equals("jboss.osgi")) { 
			return getJBossOSGiDetails(module, srcFile);
		} else if( moduleTypeId.equals("jst.utility") || moduleTypeId.equals("jst.web")) {
			if( srcFile != null ) {
				return getJBossOSGiDetailsFromJar(srcFile);
			}
			return getJBossOSGiDetailsFromProject(module);
		}
		Activator.getLogger().warning("Unhandled module type for deployment: " + moduleTypeId);
		return null;
	}
	
	private BundleDetails getFuseCamelDetails(IModule[] module) {
		try {
			String version2 = KarafUtils.getBundleVersion(module[0], null);
			String symbolicName2 = KarafUtils.getBundleSymbolicName(module[0]);
			return new BundleDetails(symbolicName2, version2 );
		} catch(CoreException ce) {
			Activator.getLogger().error(ce);
		}
		return null;
	}
	
	private BundleDetails getJBossOSGiDetails(IModule[] module, IPath srcFile) {
		if( srcFile != null ) {
			return getJBossOSGiDetailsFromJar(srcFile);
		} else {
			return getJBossOSGiDetailsFromProject(module);
		}
	}
	
	private BundleDetails getJBossOSGiDetailsFromJar(IPath srcFile) {
		try (JarFile jf = new JarFile(srcFile.toOSString())){
			Manifest m = jf.getManifest();
			return createBundleDetails(m);
		} catch(IOException ioe) {
			Activator.getLogger().error(ioe);
		}
		return null;
	}
	
	private BundleDetails getJBossOSGiDetailsFromProject(IModule[] module) {
		IProject p = module[0].getProject();
		final IFile[] found = new IFile[1];
		try {
			p.accept(new IResourceVisitor(){
				@Override
				public boolean visit(IResource resource) throws CoreException {
					if( resource instanceof IFile && "manifest.mf".equalsIgnoreCase(resource.getName())) {
						found[0] = (IFile)resource;
					}
					return found[0] == null;
				}
				
			});
		} catch(CoreException ce) {
			Activator.getLogger().error(ce);
		}
		
		// if we did not find the manifest in the project directly
		if (found[0] == null) {
			// we search for the locally built artifact and get the manifest 
			// from that one
			return findBuildArtifactInProjectsOutputFolder(module);
		} else {		
			try {
				InputStream is = found[0].getContents();
				Manifest mf = new Manifest(is);
				return createBundleDetails(mf);
			} catch(IOException ioe) {
				Activator.getLogger().error(ioe);
			} catch(CoreException ce) {
				Activator.getLogger().error(ce);
			}
		}
		
		return null;
	}

	private BundleDetails findBuildArtifactInProjectsOutputFolder(IModule[] module) {
		BundleDetails bd = null;
		IProject prj = module[0].getProject();
		IMavenProjectFacade m2prj = MavenPlugin.getMavenProjectRegistry().create(prj, new NullProgressMonitor());
		String path = m2prj.getMavenProject().getBuild().getOutputDirectory();
		String file = m2prj.getMavenProject().getBuild().getFinalName();
		File out = new File(path, file);
		if (out.exists() && out.isFile()) {
			try (JarFile jf = new JarFile(out)) {
				bd = createBundleDetails(jf.getManifest());
			} catch (IOException ex) {
				Activator.getLogger().error(ex);
			}
		}
		return bd;
	}
	
	private BundleDetails createBundleDetails(Manifest mf) {
		Attributes attributes = mf.getMainAttributes();
		String symName = attributes.getValue("Bundle-SymbolicName");
		String version = attributes.getValue("Bundle-Version");
		return new BundleDetails(symName, version);
	}
}
