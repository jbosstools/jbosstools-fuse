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

import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;


/**
 * @author lhein
 */
public interface IKarafRuntimeWorkingCopy extends IKarafRuntime {
	
	/**
	 * Set the VM to use
	 * @param selectedVM
	 */
	public void setVM(IVMInstall selectedVM);
	
	/**
	 * Set the execution environment to use
	 * @param environment
	 */
	public void setExecutionEnvironment(IExecutionEnvironment environment);
}
