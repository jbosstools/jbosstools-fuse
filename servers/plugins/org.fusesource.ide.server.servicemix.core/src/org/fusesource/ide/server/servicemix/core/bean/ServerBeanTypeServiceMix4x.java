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
package org.fusesource.ide.server.servicemix.core.bean;

import static org.fusesource.ide.server.servicemix.core.util.IServiceMixToolingConstants.SERVER_SMX_45;

import java.io.File;

import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;

/**
 * @author lhein
 */
public class ServerBeanTypeServiceMix4x extends ServerBeanType {
	
	protected static final String SMX4x_RELEASE_VERSION = "Bundle-Version"; //$NON-NLS-1$

	public static final String V4_5 = "4.5";
	public static final String V4_x = "4.";
	
	protected ServerBeanTypeServiceMix4x() {
		super(	"SMX4x", //$NON-NLS-1$
				"Apache ServiceMix 4.x", //$NON-NLS-1$
				"lib" + File.separator + "servicemix-version.jar", //$NON-NLS-1$
				new ServiceMix4xServerTypeCondition());
	}

	public static class ServiceMix4xServerTypeCondition extends
			org.jboss.ide.eclipse.as.core.server.bean.AbstractCondition {

		/*
		 * (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.ICondition#isServerRoot(java.io.File)
		 */
		public boolean isServerRoot(File location) {
			return checkServiceMixVersion(location, SMX4x_RELEASE_VERSION, V4_x);
		}

		/**
		 * 
		 * @param location
		 * @param property
		 * @param propPrefix
		 * @return
		 */
		protected static boolean checkServiceMixVersion(File location, String property, String propPrefix) {
			String mainFolder = new ServerBeanTypeServiceMix4x().getSystemJarPath();
			String value = getJarProperty(new File(location + File.separator + mainFolder), property);
			return value != null && value.startsWith(propPrefix);
		}

		/*
		 * (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.ICondition#getServerTypeId(java.lang.String)
		 */
		public String getServerTypeId(String version) {
			if( version.equals(V4_5)) return SERVER_SMX_45;
			// In case a 4.6 comes out, it should work on 4.5 until fixed
			if( version.startsWith(V4_x)) return SERVER_SMX_45;
			return null;
		}
	}
}
