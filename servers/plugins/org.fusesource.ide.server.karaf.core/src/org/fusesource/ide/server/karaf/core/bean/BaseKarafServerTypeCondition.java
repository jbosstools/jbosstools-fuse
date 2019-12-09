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

import java.io.File;

import org.jboss.ide.eclipse.as.core.server.bean.AbstractCondition;

/**
 * @author lheinema
 */
public abstract class BaseKarafServerTypeCondition extends AbstractCondition {
	
	/**
	 * checks if the karaf is a standalone karaf or an integrated version
	 * used in Red Hat Fuse
	 * @param location
	 * @return
	 */
	protected static boolean isIntegratedKaraf(File location) {
		File libFolder = new File(location + File.separator + "lib");
		File[] files = libFolder.listFiles( (File dir, String name) -> name.toLowerCase().endsWith("-version.jar") );
		return files != null && files.length > 0;
	}
}
