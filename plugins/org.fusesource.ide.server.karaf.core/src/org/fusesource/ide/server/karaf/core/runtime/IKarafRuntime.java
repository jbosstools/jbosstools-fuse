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

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;


/**
 * @author lhein
 */
public interface IKarafRuntime {
	public static final String KARAF_20 = "org.fusesource.ide.karaf.runtime.20";
	public static final String KARAF_21 = "org.fusesource.ide.karaf.runtime.21";
	public static final String KARAF_22 = "org.fusesource.ide.karaf.runtime.22";
	public static final String KARAF_23 = "org.fusesource.ide.karaf.runtime.23";

	static final String[] KARAF_RUNTIME_TYPES_SUPPORTED = new String[] {
		KARAF_20, KARAF_21, KARAF_22, KARAF_23
	};
	
	/**
	 * returns the karaf version
	 * 
	 * @return
	 */
	String getKarafVersion();
	
	
	public IPath getLocation();
	
	public IExecutionEnvironment getExecutionEnvironment();
	public IVMInstall getVM();
	public boolean isUsingDefaultJRE();
	public IVMInstall[] getValidJREs();
}
