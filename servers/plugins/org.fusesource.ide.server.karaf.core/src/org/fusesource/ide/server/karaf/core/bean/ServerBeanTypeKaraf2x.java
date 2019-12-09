/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.bean;

import static org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants.SERVER_KARAF_22;
import static org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants.SERVER_KARAF_23;
import static org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants.SERVER_KARAF_24;

import java.io.File;

import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;

/**
 * @author lhein
 */
public class ServerBeanTypeKaraf2x extends ServerBeanType {

	protected static final String KARAF2x_RELEASE_VERSION = "Bundle-Version"; //$NON-NLS-1$

	public static final String V2_2 = "2.2";
	public static final String V2_3 = "2.3";
	public static final String V2_4 = "2.4";
    public static final String V2_x = "2.";
	
	protected ServerBeanTypeKaraf2x() {
		super(	"KARAF2x", //$NON-NLS-1$
				"Apache Karaf 2.x", //$NON-NLS-1$
				"lib" + File.separator + "karaf.jar", //$NON-NLS-1$
				new Karaf2xServerTypeCondition());
	}

	public static class Karaf2xServerTypeCondition extends BaseKarafServerTypeCondition {

		/*
		 * (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.ICondition#isServerRoot(java.io.File)
		 */
		@Override
		public boolean isServerRoot(File location) {
			return checkKarafVersion(location, KARAF2x_RELEASE_VERSION, V2_x)
					&& !isIntegratedKaraf(location);
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
		@Override
		public String getServerTypeId(String version) {
			if( version.equals(V2_2)) return SERVER_KARAF_22;
			if( version.equals(V2_3)) return SERVER_KARAF_23;
			if( version.equals(V2_4)) return SERVER_KARAF_24;
			// In case a 2.5 comes out, it should work on 2.4 until fixed
			if( version.startsWith(V2_x)) return SERVER_KARAF_24;
			return null;
		}
	}
}
