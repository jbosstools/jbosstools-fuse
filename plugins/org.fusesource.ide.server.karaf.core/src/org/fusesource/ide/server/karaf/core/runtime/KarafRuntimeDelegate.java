/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.launching.environments.EnvironmentsManager;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
import org.fusesource.ide.server.karaf.core.Activator;
import org.jboss.ide.eclipse.as.core.server.IJBossServerRuntime;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBean;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanLoader;
import org.jboss.ide.eclipse.as.wtp.core.util.VMInstallUtil;

/**
 * @author lhein
 */
public class KarafRuntimeDelegate extends RuntimeDelegate implements IKarafRuntimeWorkingCopy {

	/**
	 * empty default constructor
	 */
	public KarafRuntimeDelegate() {
	}
	
	public void setDefaults(IProgressMonitor monitor) {
		getRuntimeWorkingCopy().setName(getNextRuntimeName());
		setVM(null);
	}

	protected String getNextRuntimeName() {
		return getNextRuntimeName(getRuntimeNameBase());
	}
	
	protected String getRuntimeNameBase() {
		String base = getRuntime().getRuntimeType().getName() + " Runtime";  //$NON-NLS-1$//$NON-NLS-2$
		return base;
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
	public String getKarafVersion() {
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
		String version = getKarafVersion();
		if (version != null && version.trim().startsWith("2.0")) {
			if (!id.toLowerCase().endsWith("karaf.runtime.20")) return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else if (version != null && version.trim().startsWith("2.1")) {
			if (!id.toLowerCase().endsWith("karaf.runtime.21")) return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else if (version != null && version.trim().startsWith("2.2")) {
			if (!id.toLowerCase().endsWith("karaf.runtime.22")) return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else if (version != null && version.trim().startsWith("2.3")) {
			if (!id.toLowerCase().endsWith("karaf.runtime.23")) return new Status(Status.ERROR, Activator.PLUGIN_ID, "Runtime type not compatible with found version...");
		} else {
			return new Status(Status.ERROR, Activator.PLUGIN_ID, "No compatible runtime type found for version " + version + "...");
		}
		
		return Status.OK_STATUS;
	}
	
	

	private static String PROPERTY_VM_ID = "PROPERTY_VM_ID"; //$NON-NLS-1$
	private static String PROPERTY_VM_TYPE_ID = "PROPERTY_VM_TYPE_ID"; //$NON-NLS-1$

	public IExecutionEnvironment getExecutionEnvironment() {
		return EnvironmentsManager.getDefault().getEnvironment("J2SE-1.4"); //$NON-NLS-1$
	}
	
	private IVMInstall getHardVM() {
		if (getAttribute(PROPERTY_VM_TYPE_ID, (String)null) != null) {
			String id = getAttribute(PROPERTY_VM_ID, (String)null);
			String type = getAttribute(PROPERTY_VM_TYPE_ID, (String)null);
			return VMInstallUtil.findVMInstall(type, id);
		}
		return null;
	}
	
	public IVMInstall getVM() {
		IVMInstall hard = getHardVM();
		if( hard == null )
			return VMInstallUtil.findVMInstall(getExecutionEnvironment());
		return hard;
	}
	
	public void setVM(IVMInstall selectedVM) {
		if (selectedVM == null) {
			setAttribute(IJBossServerRuntime.PROPERTY_VM_ID, (String) null);
			setAttribute(IJBossServerRuntime.PROPERTY_VM_TYPE_ID, (String) null);
		} else {
			setAttribute(IJBossServerRuntime.PROPERTY_VM_ID, selectedVM.getId());
			setAttribute(IJBossServerRuntime.PROPERTY_VM_TYPE_ID, 
					selectedVM.getVMInstallType().getId());
		}
	}

	public boolean isUsingDefaultJRE() {
		return getAttribute(PROPERTY_VM_TYPE_ID, (String)null) == null;
	}
	
	public IVMInstall[] getValidJREs() {
		return getExecutionEnvironment() == null ? new IVMInstall[0] 
				: getExecutionEnvironment().getCompatibleVMs();
	}


	@Override
	public IPath getLocation() {
		return getRuntime().getLocation();
	}
}
