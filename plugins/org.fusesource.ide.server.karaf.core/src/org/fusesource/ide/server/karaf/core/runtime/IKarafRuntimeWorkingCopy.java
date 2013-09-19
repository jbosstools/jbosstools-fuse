/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.server.karaf.core.runtime;


/**
 * @author lhein
 */
public interface IKarafRuntimeWorkingCopy extends IKarafRuntime {
	
	/**
	 * sets the karaf installation folder
	 * 
	 * @param installDir
	 */
	void setKarafInstallDir(String installDir);
	
	/**
	 * sets the karaf properties file location
	 * 
	 * @param propFile
	 */
	void setKarafPropertiesFileLocation(String propFile);
	
	/**
	 * sets the version of the karaf installation
	 * 
	 * @param version	the version
	 */
	void setKarafVersion(String version);
}
