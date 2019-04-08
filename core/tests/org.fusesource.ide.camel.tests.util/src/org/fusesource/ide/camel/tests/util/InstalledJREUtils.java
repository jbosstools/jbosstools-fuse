/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.tests.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.launching.StandardVMType;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;

public class InstalledJREUtils {

	public static final String ID_MAC_OSX_TYPE = "org.eclipse.jdt.internal.launching.macosx.MacOSXType";
	
	public static boolean hasJava8VMAvailable() {
		try {
			return hasJava8VMAvailable(getStandardVMInstallType());
		} catch (CoreException e) {
			Activator.pluginLog().logError(e);
		}
		return false;
	}
	
	static boolean hasJava8VMAvailable(IVMInstallType vmInstallType) throws CoreException {
		IExecutionEnvironment java8ExecutionEnvironment = JavaRuntime.getExecutionEnvironmentsManager().getEnvironment("JavaSE-1.8");
		IVMInstall[] standardVmInstalls = vmInstallType.getVMInstalls();
		for (IVMInstall standardVmInstall : standardVmInstalls) {
			if(java8ExecutionEnvironment.isStrictlyCompatible(standardVmInstall)){
				return true;
			}
		}
		return false;
	}

	static IVMInstallType getStandardVMInstallType() {
		IVMInstallType[] vmInstallTypes = JavaRuntime.getVMInstallTypes();
		for (IVMInstallType vmInstallType : vmInstallTypes) {
			if (StandardVMType.ID_STANDARD_VM_TYPE.equals(vmInstallType.getId())) {
				if(vmInstallType.getVMInstalls().length > 0) {
					return vmInstallType;
				}
			}
			if(vmInstallType.getId().equals(ID_MAC_OSX_TYPE)) {
				if(vmInstallType.getVMInstalls().length > 0) {
					return vmInstallType;
				}
			}
		}
		return null;
	}

}
