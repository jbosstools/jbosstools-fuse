/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.runtime;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.server.karaf.core.Activator;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBean;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanLoader;
import org.jboss.ide.eclipse.as.wtp.core.launching.IExecutionEnvironmentConstants;
import org.jboss.ide.eclipse.as.wtp.core.util.VMInstallUtil;
import org.osgi.framework.Version;

/**
 * @author lhein
 */
public class KarafRuntimeDelegate extends RuntimeDelegate implements IKarafRuntimeWorkingCopy {

	private static String PROPERTY_VM_ID = "PROPERTY_VM_ID"; //$NON-NLS-1$
	private static String PROPERTY_VM_TYPE_ID = "PROPERTY_VM_TYPE_ID"; //$NON-NLS-1$
	private static String PROPERTY_EXECUTION_ENVIRONMENT = "PROPERTY_EXEC_ENVIRONMENT"; //$NON-NLS-1$
	
	@Override
	public void setDefaults(IProgressMonitor monitor) {
		getRuntimeWorkingCopy().setName(getNextRuntimeName());
		setVM(null);
	}

	protected String getNextRuntimeName() {
		return getNextRuntimeName(getRuntimeNameBase());
	}
	
	protected String getRuntimeNameBase() {
		return getRuntime().getRuntimeType().getName() + " Runtime";  //$NON-NLS-1$
	}

	public static String getNextRuntimeName(String base) {
		IRuntime rt = ServerCore.findRuntime(base);
		if (rt == null)
			return base;

		int i = 0;
		while (rt != null) {
			rt = ServerCore.findRuntime(base + " " + ++i); //$NON-NLS-1$
		}
		return base + " " + i; //$NON-NLS-1$
	}

	@Override
	public String getVersion() {
		IPath loc = getRuntime().getLocation();
		ServerBean sb = new ServerBeanLoader(loc.toFile()).getServerBean();
		if( sb != null )
			return sb.getFullVersion();
		return null;
	}
	
	@Override
	public IStatus validate() {
		IStatus status = super.validate();
		if (!status.isOK())
			return status;

		String id = getRuntime().getRuntimeType().getId();
		String version = getVersion();
		
		return validateRuntimeAndVersion(id, version);
	}

	protected IStatus validateRuntimeAndVersion(String runtimeId, String runtimeVersion) {
		if (Strings.isBlank(runtimeVersion)) {
			return new Status(Status.ERROR, Activator.PLUGIN_ID, "Empty runtime version found for runtime " + runtimeId + "...");
		}
		String versionStartString = getMajorMinorString(runtimeVersion);
		if (!runtimeId.toLowerCase().endsWith(String.format("karaf.runtime.%s", versionStartString)) && !isNewerUnsupportedVersion(runtimeVersion)) {
			return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type " + runtimeId + " is not compatible with found version " + runtimeVersion);
		}
		return Status.OK_STATUS;
	}

	protected boolean isNewerUnsupportedVersion(String runtimeVersion) {
		Version foundVersion = Version.parseVersion(runtimeVersion);
		Version rtVersion = Version.parseVersion(getRuntime().getRuntimeType().getVersion());
		// if same major version then we accept newer minor/micro versions which might be yet unknown in tooling
		return foundVersion.getMajor() == rtVersion.getMajor() && foundVersion.compareTo(rtVersion) > 0;
	}
	
	protected String getMajorMinorString(String runtimeVersion) {
		int pos = runtimeVersion.indexOf('.');
		pos = runtimeVersion.indexOf('.', pos+1);
		return runtimeVersion.substring(0, pos).replaceAll("\\.", "");
	}
	
	@Override
	public IExecutionEnvironment getExecutionEnvironment() {
		String id = getAttribute(PROPERTY_EXECUTION_ENVIRONMENT, (String)null);
		return id == null ? getMinimumExecutionEnvironment() : JavaRuntime.getExecutionEnvironmentsManager().getEnvironment(id);
	}

	@Override
	public IExecutionEnvironment getMinimumExecutionEnvironment() {
		return JavaRuntime.getExecutionEnvironmentsManager().getEnvironment(IExecutionEnvironmentConstants.EXEC_ENV_JavaSE17);
	}

	@Override
	public void setExecutionEnvironment(IExecutionEnvironment environment) {
		setAttribute(PROPERTY_EXECUTION_ENVIRONMENT, environment == null ? null : environment.getId());
	}

	// Non-interface method for internal use
	public IVMInstall getHardVM() {
		if (getAttribute(PROPERTY_VM_TYPE_ID, (String)null) != null) {
			String id = getAttribute(PROPERTY_VM_ID, (String)null);
			String type = getAttribute(PROPERTY_VM_TYPE_ID, (String)null);
			return VMInstallUtil.findVMInstall(type, id);
		}
		return null;
	}
	
	@Override
	public IVMInstall getVM() {
		IVMInstall hard = getHardVM();
		if( hard == null )
			return VMInstallUtil.findVMInstall(getExecutionEnvironment());
		return hard;
	}
	
	@Override
	public void setVM(IVMInstall selectedVM) {
		if (selectedVM == null) {
			setAttribute(PROPERTY_VM_ID, (String) null);
			setAttribute(PROPERTY_VM_TYPE_ID, (String) null);
		} else {
			setAttribute(PROPERTY_VM_ID, selectedVM.getId());
			setAttribute(PROPERTY_VM_TYPE_ID, 
					selectedVM.getVMInstallType().getId());
		}
	}

	@Override
	public boolean isUsingDefaultJRE() {
		return getAttribute(PROPERTY_VM_TYPE_ID, (String)null) == null;
	}
	
	@Override
	public IVMInstall[] getValidJREs() {
		return getExecutionEnvironment() == null ? new IVMInstall[0] 
				: getExecutionEnvironment().getCompatibleVMs();
	}


	@Override
	public IPath getLocation() {
		return getRuntime().getLocation();
	}
}
