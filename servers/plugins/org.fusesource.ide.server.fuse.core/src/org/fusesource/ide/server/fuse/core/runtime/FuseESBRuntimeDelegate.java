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
package org.fusesource.ide.server.fuse.core.runtime;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.server.fuse.core.Activator;
import org.fusesource.ide.server.fuse.core.util.FuseToolingConstants;
import org.fusesource.ide.server.karaf.core.runtime.KarafRuntimeDelegate;
import org.jboss.ide.eclipse.as.wtp.core.launching.IExecutionEnvironmentConstants;

/**
 * @author lhein
 */
public class FuseESBRuntimeDelegate extends KarafRuntimeDelegate {
	
	@Override
	public IStatus validate() {
		return validateRuntimeAndVersion(getRuntime().getRuntimeType().getId(), getVersion());
	}
	
	@Override
	protected IStatus validateRuntimeAndVersion(String runtimeId, String runtimeVersion) {
		if (Strings.isBlank(runtimeVersion)) {
			return new Status(Status.ERROR, Activator.PLUGIN_ID, "Empty runtime version found for runtime " + runtimeId + "...");
		}
		String versionStartString = getMajorMinorString(runtimeVersion);
		if (!runtimeId.toLowerCase().endsWith(String.format("fuseesb.runtime.%s", versionStartString)) && !isNewerUnsupportedVersion(runtimeVersion)) {
			return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type " + runtimeId + " is not compatible with found version " + runtimeVersion);
		}
		return Status.OK_STATUS;
	}
	
	@Override
	public IExecutionEnvironment getMinimumExecutionEnvironment() {
		if (getRuntime().getRuntimeType().getVersion().startsWith(FuseToolingConstants.FUSE_VERSION_7X)) {
			return JavaRuntime.getExecutionEnvironmentsManager().getEnvironment(IExecutionEnvironmentConstants.EXEC_ENV_JavaSE18);
		}
		return JavaRuntime.getExecutionEnvironmentsManager().getEnvironment(IExecutionEnvironmentConstants.EXEC_ENV_JavaSE17);
	}
}
