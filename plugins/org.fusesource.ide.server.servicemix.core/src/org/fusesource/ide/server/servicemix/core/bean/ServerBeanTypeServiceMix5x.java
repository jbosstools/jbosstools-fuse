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

import static org.fusesource.ide.server.servicemix.core.util.IServiceMixToolingConstants.SERVER_SMX_50;

import java.io.File;

import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;

/**
 * @author lhein
 *
 */
public class ServerBeanTypeServiceMix5x  extends ServerBeanType {
	
	protected static final String SMX5x_RELEASE_VERSION = "Bundle-Version"; //$NON-NLS-1$

	public static final String V5_0 = "5.0";
	public static final String V5_x = "5.";
	
	protected ServerBeanTypeServiceMix5x() {
		super(	"SMX5x", //$NON-NLS-1$
				"Apache ServiceMix 5.x", //$NON-NLS-1$
				"lib" + File.separator + "servicemix-version.jar", //$NON-NLS-1$
				new ServiceMix5xServerTypeCondition());
	}

	public static class ServiceMix5xServerTypeCondition extends
			org.jboss.ide.eclipse.as.core.server.bean.AbstractCondition {

		/*
		 * (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.ICondition#isServerRoot(java.io.File)
		 */
		public boolean isServerRoot(File location) {
			return checkServiceMixVersion(location, SMX5x_RELEASE_VERSION, V5_x);
		}

		/**
		 * 
		 * @param location
		 * @param property
		 * @param propPrefix
		 * @return
		 */
		protected static boolean checkServiceMixVersion(File location, String property, String propPrefix) {
			String mainFolder = new ServerBeanTypeServiceMix5x().getSystemJarPath();
			String value = getJarProperty(new File(location + File.separator + mainFolder), property);
			return value != null && value.startsWith(propPrefix);
		}

		/*
		 * (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.ICondition#getServerTypeId(java.lang.String)
		 */
		public String getServerTypeId(String version) {
			if( version.equals(V5_0)) return SERVER_SMX_50;
			// In case a 5.1 comes out, it should work on 5.0 until fixed
			if( version.startsWith(V5_x)) return SERVER_SMX_50;
			return null;
		}
	}
}
