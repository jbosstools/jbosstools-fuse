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
package org.fusesource.ide.server.karaf.core.bean;

import static org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants.SERVER_KARAF_40;
import static org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants.SERVER_KARAF_41;

import java.io.File;

import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;

/**
 * @author lhein
 */
public class ServerBeanTypeKaraf4x extends ServerBeanType {
	private static final String KARAF4X_RELEASE_VERSION = "Bundle-Version"; //$NON-NLS-1$

	public static final String V4_0 = "4.0";
	public static final String V4_1 = "4.1";
	public static final String V4_X = "4.";
	
	protected ServerBeanTypeKaraf4x() {
		super(	"KARAF4x", //$NON-NLS-1$
				"Apache Karaf 4.x", //$NON-NLS-1$
				"lib" + File.separator + "boot" + File.separator + "org.apache.karaf.main.jar", //$NON-NLS-1$
				new Karaf4xServerTypeCondition());
	}

	public static class Karaf4xServerTypeCondition extends BaseKarafServerTypeCondition {

		/*
		 * (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.ICondition#isServerRoot(java.io.File)
		 */
		@Override
		public boolean isServerRoot(File location) {
			return checkKarafVersion(location, KARAF4X_RELEASE_VERSION, V4_X)
					&& !isIntegratedKaraf(location);
		}
		
		/* (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.AbstractCondition#getFullVersion(java.io.File, java.io.File)
		 */
		@Override
		public String getFullVersion(File location, File systemFile) {
			File jarFile = getRealJarFile(systemFile.getParentFile());
			return super.getFullVersion(location, jarFile);
		}
		
		protected static File getRealJarFile(File jarFilePath) {
			File[] files = jarFilePath.listFiles( (File dir, String name) -> name.toLowerCase().startsWith("org.apache.karaf.main-") && name.toLowerCase().endsWith(".jar") ); 
			if (files != null && files.length>0) {
				return files[0];
			}
			return jarFilePath;
		}
		
		/**
		 * 
		 * @param location
		 * @param property
		 * @param propPrefix
		 * @return
		 */
		protected static boolean checkKarafVersion(File location, String property, String propPrefix) {
			String mainFolder = new ServerBeanTypeKaraf4x().getSystemJarPath();
			File f = new File(location.getPath() + File.separator + mainFolder);
			String value = getJarProperty(getRealJarFile(f.getParentFile()), property);
			return value != null && value.startsWith(propPrefix);
		}

		/*
		 * (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.ICondition#getServerTypeId(java.lang.String)
		 */
		@Override
		public String getServerTypeId(String version) {
			if( version.equals(V4_0)) return SERVER_KARAF_40;
			if( version.equals(V4_1)) return SERVER_KARAF_41;
			// In case new 4.x comes out, it should work on 4.1 until fixed
			if( version.startsWith(V4_X)) return SERVER_KARAF_41;
			return null;
		}
		
		protected static boolean isIntegratedKaraf(File location) {
			File libFolder = new File(location + File.separator + "lib");
			File[] files = libFolder.listFiles( (File dir, String name) -> name.toLowerCase().startsWith("fuse-branding-") );
			return files != null && files.length > 0;
		}
	}
}
