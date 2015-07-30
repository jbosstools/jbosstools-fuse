package org.fusesource.ide.server.karaf.core.server.subsystems.publish;

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
import org.eclipse.wst.server.core.IModule;
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
		if( moduleTypeId.equals("fuse.camel")) {
			return getFuseCamelDetails(module);
		} else if( moduleTypeId.equals("jboss.osgi")) { 
			return getJBossOSGiDetails(module, srcFile);
		} else if( moduleTypeId.equals("jst.utility")) {
			return getJBossOSGiDetailsFromJar(srcFile);
		}
		// TODO maybe log? 
		return null;
	}
	
	private BundleDetails getFuseCamelDetails(IModule[] module) {
		try {
			String version2 = KarafUtils.getBundleVersion(module[0], null);
			String symbolicName2 = KarafUtils.getBundleSymbolicName(module[0]);
			return new BundleDetails(symbolicName2, version2 );
		} catch(CoreException ce) {
			//TODO cleanup
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
		JarFile jf = null;
		try {
			jf = new JarFile(srcFile.toOSString());
			Manifest m = jf.getManifest();
			Attributes attributes = m.getMainAttributes();
			String symName = attributes.getValue("Bundle-SymbolicName");
			String version = attributes.getValue("Bundle-Version");
			return new BundleDetails(symName, version);
		} catch(IOException ioe) {
			// TODO cleanup
		}finally {
			if( jf != null ) {
				try {
					jf.close();
				} catch(IOException ioe) {
					// TODO
				}
			}
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
					if( resource instanceof IFile && resource.getName().toLowerCase().equals("manifest.mf")) {
						found[0] = (IFile)resource;
					}
					return found[0] == null;
				}
				
			});
		} catch(CoreException ce) {
			// TODO log
		}
		
		if( found[0] != null ) {
			try {
				InputStream is = found[0].getContents();
				Manifest mf = new Manifest(is);
				Attributes attributes = mf.getMainAttributes();
				String symName = attributes.getValue("Bundle-SymbolicName");
				String version = attributes.getValue("Bundle-Version");
				return new BundleDetails(symName, version);
			} catch(IOException ioe) {
				// TODO log 
			} catch(CoreException ce) {
				// TODO log 
			}
		}
		
		return null;
	}
}
