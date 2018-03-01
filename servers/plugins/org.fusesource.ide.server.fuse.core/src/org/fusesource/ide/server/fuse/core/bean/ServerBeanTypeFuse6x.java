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
package org.fusesource.ide.server.fuse.core.bean;

import static org.fusesource.ide.server.fuse.core.util.FuseToolingConstants.SERVER_FUSE_60;
import static org.fusesource.ide.server.fuse.core.util.FuseToolingConstants.SERVER_FUSE_61;
import static org.fusesource.ide.server.fuse.core.util.FuseToolingConstants.SERVER_FUSE_62;
import static org.fusesource.ide.server.fuse.core.util.FuseToolingConstants.SERVER_FUSE_63;

import java.io.File;

import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;

/**
 * @author lhein
 */
public class ServerBeanTypeFuse6x extends ServerBeanType {
	
	protected static final String FUSE6X_RELEASE_VERSION = "Bundle-Version"; //$NON-NLS-1$

	public static final String V60 = "6.0";
	public static final String V61 = "6.1";
	public static final String V62 = "6.2";
	public static final String V63 = "6.3";
	public static final String V6X = "6.";
	
	protected ServerBeanTypeFuse6x() {
		super(	"FUSE6x", //$NON-NLS-1$
				"Red Hat Fuse 6.x", //$NON-NLS-1$
				"lib" + File.separator + "esb-version.jar", //$NON-NLS-1$
				new Fuse6xServerTypeCondition());
	}

	public static class Fuse6xServerTypeCondition extends
			org.jboss.ide.eclipse.as.core.server.bean.AbstractCondition {

		/*
		 * (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.ICondition#isServerRoot(java.io.File)
		 */
		@Override
		public boolean isServerRoot(File location) {
			return checkFuseVersion(location, FUSE6X_RELEASE_VERSION, V6X);
		}

		/**
		 * 
		 * @param location
		 * @param property
		 * @param propPrefix
		 * @return
		 */
		protected static boolean checkFuseVersion(File location, String property, String propPrefix) {
			String mainFolder = new ServerBeanTypeFuse6x().getSystemJarPath();
			String value = getJarProperty(new File(location + File.separator + mainFolder), property);
			return value != null && value.startsWith(propPrefix);
		}

		/*
		 * (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.ICondition#getServerTypeId(java.lang.String)
		 */
		@Override
		public String getServerTypeId(String version) {
			if( version.equals(V60)) return SERVER_FUSE_60;
			if( version.equals(V61)) return SERVER_FUSE_61;
			if( version.equals(V62)) return SERVER_FUSE_62;
			if( version.equals(V63)) return SERVER_FUSE_63;
			// In case a 6.4 comes out, it should work on 6.3 until fixed
			if( version.startsWith(V6X)) return SERVER_FUSE_63;
			return null;
		}
	}
}
