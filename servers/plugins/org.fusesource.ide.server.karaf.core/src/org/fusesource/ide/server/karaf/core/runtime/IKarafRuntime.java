/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.server.karaf.core.runtime;

import static org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants.RUNTIME_KARAF_22;
import static org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants.RUNTIME_KARAF_23;
import static org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants.RUNTIME_KARAF_24;
import static org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants.RUNTIME_KARAF_30;
import static org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants.RUNTIME_KARAF_40;
import static org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants.RUNTIME_KARAF_41;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;


/**
 * @author lhein
 */
public interface IKarafRuntime {

	static final String[] KARAF_RUNTIME_TYPES_SUPPORTED = new String[] {
		RUNTIME_KARAF_22, RUNTIME_KARAF_23, RUNTIME_KARAF_24, RUNTIME_KARAF_30, RUNTIME_KARAF_40, RUNTIME_KARAF_41
	};
	
	/**
	 * returns the karaf version
	 * 
	 * @return
	 */
	String getVersion();
	
	
	public IPath getLocation();
	
	/**
	 * Get the current execution environment, or the minimum if none is set
	 * @return
	 */
	public IExecutionEnvironment getExecutionEnvironment();
	
	/**
	 * Get the minimum execution environment for this runtime type
	 * @return
	 */
	public IExecutionEnvironment getMinimumExecutionEnvironment();
	
	/**
	 * Get the VM to use to launch this runtime
	 * @return
	 */
	public IVMInstall getVM();
	public boolean isUsingDefaultJRE();
	
	/**
	 * Get a list of all valid java vms that are compatible with this runtime type. 
	 * @return
	 */
	public IVMInstall[] getValidJREs();
}
