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

package org.fusesource.ide.server.servicemix.core.runtime;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.fusesource.ide.server.karaf.core.runtime.KarafRuntimeLocator;
import org.fusesource.ide.server.servicemix.core.ServiceMixUtils;


/**
 * @author lhein
 */
public class ServiceMix4xRuntimeLocator extends KarafRuntimeLocator {
	
	/**
	 * empty default constructor
	 */
	public ServiceMix4xRuntimeLocator() {
	}

	/**
	 * retrieves the runtime working copy from the given folder
	 * 
	 * @param dir		the possible base folder
	 * @param monitor	the monitor
	 * @return			the runtime working copy or null if invalid
	 */
	public IRuntimeWorkingCopy getRuntimeFromDir(File dir, IProgressMonitor monitor) {
		for (int i = 0; i < IServiceMixRuntime.SMX_RUNTIME_TYPES_SUPPORTED.length; i++) {
			try {
				IRuntimeType runtimeType = ServerCore.findRuntimeType(IServiceMixRuntime.SMX_RUNTIME_TYPES_SUPPORTED[i]);
				String absolutePath = dir.getAbsolutePath();
				
				// now check if the directory is valid
				if (ServiceMixUtils.isValidServiceMixInstallation(dir, null)) {
					IRuntimeWorkingCopy runtime = runtimeType.createRuntime(runtimeType.getId(), monitor);
// commented out the naming of the runtime as it seems to break server to runtime links
					runtime.setName(dir.getName());
					runtime.setLocation(new Path(absolutePath));
					IStatus status = runtime.validate(monitor);
					if (status == null || status.getSeverity() != IStatus.ERROR) {
						return runtime;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}