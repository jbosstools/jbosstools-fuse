/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.bean;

import static org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants.SERVER_KARAF_30;

import java.io.File;
import java.io.FilenameFilter;

import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;

/**
 * @author lhein
 */
public class ServerBeanTypeKaraf3x extends ServerBeanType {
	private static final String KARAF3x_RELEASE_VERSION = "Bundle-Version"; //$NON-NLS-1$

	public static final String V3_0 = "3.0";
	public static final String V3_x = "3.";
	
	protected ServerBeanTypeKaraf3x() {
		super(	"KARAF3x", //$NON-NLS-1$
				"Apache Karaf 3.x", //$NON-NLS-1$
				"lib" + File.separator + "karaf.jar", //$NON-NLS-1$
				new Karaf3xServerTypeCondition());
	}

	public static class Karaf3xServerTypeCondition extends
			org.jboss.ide.eclipse.as.core.server.bean.AbstractCondition {

		/*
		 * (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.ICondition#isServerRoot(java.io.File)
		 */
		public boolean isServerRoot(File location) {
			return checkKarafVersion(location, KARAF3x_RELEASE_VERSION, V3_x)
					&& !isIntegratedKaraf(location);
		}

		/**
		 * checks if the karaf is a standalone karaf or an integrated version
		 * used in Apache ServiceMix or JBoss Fuse
		 * @param location
		 * @return
		 */
		protected static boolean isIntegratedKaraf(File location) {
			File libFolder = new File(location + File.separator + "lib");
			File[] files = libFolder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.toLowerCase().endsWith("-version.jar")) {
						return true;
					}
					return false;
				}
			});
			return files.length > 0;
		}

		/**
		 * 
		 * @param location
		 * @param property
		 * @param propPrefix
		 * @return
		 */
		protected static boolean checkKarafVersion(File location, String property, String propPrefix) {
			String mainFolder = new ServerBeanTypeKaraf2x().getSystemJarPath();
			String value = getJarProperty(new File(location + File.separator + mainFolder), property);
			return value != null && value.startsWith(propPrefix);
		}

		/*
		 * (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.ICondition#getServerTypeId(java.lang.String)
		 */
		public String getServerTypeId(String version) {
			if( version.equals(V3_0)) return SERVER_KARAF_30;
			// In case a 3.1 comes out, it should work on 3.0 until fixed
			if( version.startsWith(V3_x)) return SERVER_KARAF_30;
			return null;
		}
	}
}
