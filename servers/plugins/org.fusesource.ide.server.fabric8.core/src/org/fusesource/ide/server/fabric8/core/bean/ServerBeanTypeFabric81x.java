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
package org.fusesource.ide.server.fabric8.core.bean;

import static org.fusesource.ide.server.fabric8.core.util.IFabric8ToolingConstants.SERVER_FABRIC8_11;
import static org.fusesource.ide.server.fabric8.core.util.IFabric8ToolingConstants.SERVER_FABRIC8_12;

import java.io.File;

import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;

/**
 * @author lhein
 */
public class ServerBeanTypeFabric81x extends ServerBeanType {
	
	protected static final String FABRIC81x_RELEASE_VERSION = "Bundle-Version"; //$NON-NLS-1$

    public static final String V1_1 = "1.1";
    public static final String V1_2 = "1.2";
	public static final String V1_x = "1.";
	
	protected ServerBeanTypeFabric81x() {
		super(	"FABRIC81x", //$NON-NLS-1$
				"Fabric8 1.x", //$NON-NLS-1$
				"lib" + File.separator + "fabric-version.jar", //$NON-NLS-1$
				new Fabric81xServerTypeCondition());
	}

	public static class Fabric81xServerTypeCondition extends
			org.jboss.ide.eclipse.as.core.server.bean.AbstractCondition {

		/*
		 * (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.ICondition#isServerRoot(java.io.File)
		 */
		public boolean isServerRoot(File location) {
			return checkFabric8Version(location, FABRIC81x_RELEASE_VERSION, V1_x);
		}

		/**
		 * 
		 * @param location
		 * @param property
		 * @param propPrefix
		 * @return
		 */
		protected static boolean checkFabric8Version(File location, String property, String propPrefix) {
			String mainFolder = new ServerBeanTypeFabric81x().getSystemJarPath();
			String value = getJarProperty(new File(location + File.separator + mainFolder), property);
			return value != null && value.startsWith(propPrefix);
		}

		/*
		 * (non-Javadoc)
		 * @see org.jboss.ide.eclipse.as.core.server.bean.ICondition#getServerTypeId(java.lang.String)
		 */
		public String getServerTypeId(String version) {
		    if( version.equals(V1_1)) return SERVER_FABRIC8_11;
		    if( version.equals(V1_2)) return SERVER_FABRIC8_12;
			// In case a 1.3 comes out, it should work on 1.2 until fixed
			if( version.startsWith(V1_x)) return SERVER_FABRIC8_12;
			return null;
		}
	}
}
