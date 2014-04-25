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
import static org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants.RUNTIME_KARAF_20;
import static org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants.RUNTIME_KARAF_21;
import static org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants.RUNTIME_KARAF_22;
import static org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants.RUNTIME_KARAF_23;


/**
 * @author lhein
 */
public interface IKarafRuntime {

	static final String[] KARAF_RUNTIME_TYPES_SUPPORTED = new String[] {
		RUNTIME_KARAF_20, RUNTIME_KARAF_21, RUNTIME_KARAF_22, RUNTIME_KARAF_23
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
