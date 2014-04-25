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

import java.io.File;
import java.io.FilenameFilter;

import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;

/**
 * @author lhein
 */
public class ServerBeanTypeKaraf2x extends ServerBeanType {

	private static final String KARAF2x_RELEASE_VERSION = "Bundle-Version"; //$NON-NLS-1$

	public static final String V2_0 = "2.0";
	public static final String V2_1 = "2.1";
	public static final String V2_2 = "2.2";
	public static final String V2_3 = "2.3";
	public static final String V2_x = "2.";

	public static final String SERVER_KARAF_20 = "org.fusesource.ide.karaf.server.20";
	public static final String SERVER_KARAF_21 = "org.fusesource.ide.karaf.server.21";
	public static final String SERVER_KARAF_22 = "org.fusesource.ide.karaf.server.22";
	public static final String SERVER_KARAF_23 = "org.fusesource.ide.karaf.server.23";
	public static final String SERVER_KARAF_2x = SERVER_KARAF_23;

	protected ServerBeanTypeKaraf2x() {
		super(	"KARAF2x", //$NON-NLS-1$
				"Apache Karaf 2.x", //$NON-NLS-1$
				"lib" + File.separator + "karaf.jar", //$NON-NLS-1$
				new Karaf2xServerTypeCondition());
	}

	public static class Karaf2xServerTypeCondition extends
			org.jboss.ide.eclipse.as.core.server.bean.AbstractCondition {

		/*
		 * (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.ICondition#isServerRoot(java.io.File)
		 */
		public boolean isServerRoot(File location) {
			return checkKarafVersion(location, KARAF2x_RELEASE_VERSION, V2_x)
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
		protected static boolean checkKarafVersion(File location,
				String property, String propPrefix) {
			String mainFolder = new ServerBeanTypeKaraf2x().jbossSystemJarPath;
			String value = getJarProperty(new File(location + File.separator
					+ mainFolder), property);
			return value != null && value.startsWith(propPrefix);
		}

		/*
		 * (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.ICondition#getServerTypeId(java.lang.String)
		 */
		public String getServerTypeId(String version) {
			if (version.equals(V2_0))
				return SERVER_KARAF_20;
			if (version.equals(V2_1))
				return SERVER_KARAF_21;
			if (version.equals(V2_2))
				return SERVER_KARAF_22;
			if (version.equals(V2_3))
				return SERVER_KARAF_23;
			if (version.equals(V2_x))
				return SERVER_KARAF_2x;
			return null;
		}
	}
}
