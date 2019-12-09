/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.fuse.core.bean;

import static org.fusesource.ide.server.fuse.core.util.FuseToolingConstants.SERVER_FUSE_70;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.fusesource.ide.server.fuse.core.Activator;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;

/**
 * @author lhein
 */
public class ServerBeanTypeFuse7x extends ServerBeanType {
	
	private static final String FUSE_BRANDING_JAR_PREFIX = "fuse-branding-";
	private static final String KEY_VERSION = "version";
	private static final String VERSION_FILE = "version.properties";
	
	public static final String V70 = "7.0";
	public static final String V7X = "7.";
	
	protected ServerBeanTypeFuse7x() {
		super(	"FUSE7x", //$NON-NLS-1$
				"Red Hat Fuse 7.x", //$NON-NLS-1$
				"lib", //$NON-NLS-1$
				new Fuse7xServerTypeCondition());
	}

	public static class Fuse7xServerTypeCondition extends
			org.jboss.ide.eclipse.as.core.server.bean.AbstractCondition {

		/*
		 * (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.ICondition#isServerRoot(java.io.File)
		 */
		@Override
		public boolean isServerRoot(File location) {
			return isFuseRuntime(location);
		}

		/* (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.AbstractCondition#getFullVersion(java.io.File, java.io.File)
		 */
		@Override
		public String getFullVersion(File location, File systemFile) {
			File jarFile = getBrandingJarFile(systemFile);
			return getFuseVersionFromBrandingFile(jarFile);
		}
		
		protected static String getFuseVersionFromBrandingFile(File jarFile) {
			if (jarFile == null) {
				return null;
			}
			String name = jarFile.getName();
			int startPos = name.indexOf(FUSE_BRANDING_JAR_PREFIX)+FUSE_BRANDING_JAR_PREFIX.length();
			return name.substring(startPos, name.length()-".jar".length());
		}
		
		protected static File getBrandingJarFile(File jarFilePath) {
			File[] files = jarFilePath.listFiles( (File dir, String name) -> name.toLowerCase().startsWith(FUSE_BRANDING_JAR_PREFIX) && name.toLowerCase().endsWith(".jar") ); 
			if (files != null && files.length>0) {
				return files[0];
			}
			return null;
		}
		
		/**
		 * 
		 * @param location
		 * @return
		 */
		protected static boolean isFuseRuntime(File location) {
			// we do a 2 way check
			// 1. check for a file called version.properties in the root of the runtime folder
			File versionFile = new File(location, VERSION_FILE);
			if (versionFile.exists() && versionFile.isFile()) {
				try {
					Properties p = new Properties();
					p.load(new FileInputStream(versionFile));
					String version = p.getProperty(KEY_VERSION);
					// if we have a version property and it starts with 7. it is a fuse runtime for this 7.x bean type
					if (version != null && version.startsWith(V7X)) {
						return true;
					}
				} catch (IOException ex) {
					Activator.getLogger().error(ex);
				}
			}
			
			// if the above check fails for some reason we fall back to check for lib/fuse-branding-<version>.jar
			String mainFolder = new ServerBeanTypeFuse7x().getSystemJarPath();
			File f = new File(location.getPath() + File.separator + mainFolder);
			File brandingJar = getBrandingJarFile(f);
			String version = getFuseVersionFromBrandingFile(brandingJar);
			return version != null && brandingJar.getName().startsWith(FUSE_BRANDING_JAR_PREFIX) && version.startsWith(V7X);
		}

		/*
		 * (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.ICondition#getServerTypeId(java.lang.String)
		 */
		@Override
		public String getServerTypeId(String version) {
			if( version.equals(V70)) return SERVER_FUSE_70;
			// In case a 7.1 comes out, it should work on 7.0 until fixed
			if( version.startsWith(V7X)) return SERVER_FUSE_70;
			return null;
		}
	}
}
