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

package org.fusesource.ide.server.karaf.ui.runtime;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntimeWorkingCopy;
import org.fusesource.ide.server.karaf.core.server.IKarafServerDelegateWorkingCopy;

public class KarafWizardDataModel implements
		IKarafRuntimeWorkingCopy, IKarafServerDelegateWorkingCopy {
	
	public static final String KARAF_MODEL = "karaf-model";
	
	private String karafInstallDir = "";
	private String hostName;
	private int portNumber;
	private String userName;
	private String password;

	public String getKarafInstallDir() {
		return karafInstallDir;
	}

	public String getHostName() {
		return hostName;
	}

	public String getPassword() {
		return password;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public String getUserName() {
		return userName;
	}

	public void setKarafInstallDir(String installDir) {
		this.karafInstallDir = installDir;
	}
	
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public IPath getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public IExecutionEnvironment getExecutionEnvironment() {
		// TODO Auto-generated method stub
		return null;
	}

	public IVMInstall getVM() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isUsingDefaultJRE() {
		// TODO Auto-generated method stub
		return false;
	}

	public IVMInstall[] getValidJREs() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setVM(IVMInstall selectedVM) {
		// TODO Auto-generated method stub
		//dfsdfads
	}

	public IExecutionEnvironment getMinimumExecutionEnvironment() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setExecutionEnvironment(IExecutionEnvironment environment) {
		// TODO Auto-generated method stub
		
	}
}
