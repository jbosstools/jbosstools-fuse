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

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.RuntimeLocatorDelegate;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.bean.KarafBeanProvider;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBean;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanLoader;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;

/**
 * This locator delegates most functionality to the the server bean loader. 
 * There we open the jar and lookup the bundle version
 * from the manifest file. Max recursion depth is defined as constant.
 * 
 * @author lhein
 */
public class KarafRuntimeLocator extends RuntimeLocatorDelegate {

	private static final int MAX_RECURSION_DEPTH = 5;
	
	@Override
	public void searchForRuntimes(IPath path, IRuntimeSearchListener listener,
			IProgressMonitor monitor) {
		if (path == null) {
			monitor.done();
			return;
		}
		
		File f = new File(path.toOSString());
		if (f.isDirectory()) {
			monitor.beginTask("Searching for Apache Karaf in " + f.getPath() + "...", IProgressMonitor.UNKNOWN);
			search(f, listener, monitor);
			monitor.worked(1);
		}
		
		monitor.done();
	}
	
	
	/**
	 * 
	 * @param folder
	 * @param listener
	 * @param monitor
	 */
	public void search(File folder, IRuntimeSearchListener listener, IProgressMonitor monitor) {
		search(folder, listener, 0, monitor);
	}
	
	public void search(File folder, IRuntimeSearchListener listener, int recursionLevel, IProgressMonitor monitor) {
		if (monitor.isCanceled() || recursionLevel == MAX_RECURSION_DEPTH) {
			return;
		}
		if (!checkRuntime(folder, listener, monitor)) {
			return;
		}
		
		// Get list of folders to check
		File[] files = folder.listFiles(File::isDirectory);
		
		for (File f: files) {
			monitor.beginTask("Searching for Apache Karaf in " + f.getPath() + "...", IProgressMonitor.UNKNOWN);
			// If there's a runtime found, it will be added in checkRuntime
			// If there's not, we recurse in. 
			// increase recursion level
			search(f, listener, recursionLevel+1, monitor);
			monitor.worked(1);
		}
	}
	
	/**
	 * 
	 * @param karafJar
	 * @param listener
	 * @param monitor
	 * @return
	 */
	private boolean checkRuntime(File karafHome, IRuntimeSearchListener listener, 
			IProgressMonitor monitor) {
		monitor.beginTask("Examine possible Apache Karaf installation at " + karafHome.getPath() + "...", IProgressMonitor.UNKNOWN);
		IRuntimeWorkingCopy runtime = getRuntimeFromDir(karafHome, monitor);
		monitor.worked(1);
		if (runtime != null) {
			listener.runtimeFound(runtime);
			return true;
		}
		return false;
	}
	
	/**
	 * retrieves the runtime working copy from the given folder
	 * 
	 * @param dir		the possible base folder
	 * @param monitor	the monitor
	 * @return			the runtime working copy or null if invalid
	 */
	public IRuntimeWorkingCopy getRuntimeFromDir(File dir, IProgressMonitor monitor) {
		String absolutePath = dir.getAbsolutePath();
		ServerBeanLoader l = new ServerBeanLoader(dir);
		ServerBean sb = l.getServerBean();
		if( isValidServerBeanType(sb) ) {
			String serverType = l.getServerAdapterId();
			if( serverType != null ) {
				IServerType t = ServerCore.findServerType(serverType);
				if( t != null ) {
					IRuntimeType rtt = t.getRuntimeType();
					try {
						IRuntimeWorkingCopy runtime = rtt.createRuntime(rtt.getId(), monitor);
						runtime.setLocation(new Path(absolutePath));
						IStatus status = runtime.validate(monitor);
						if (status == null || status.getSeverity() != IStatus.ERROR) {
							return runtime;
						}
					} catch (Exception e) {
						Activator.getLogger().error(e);
					}
				}
			}
		}
		return null;
	}
	
	protected boolean isValidServerBeanType(ServerBean sb) {
		if (sb != null) {
			ServerBeanType type = sb.getBeanType();
			return KarafBeanProvider.KARAF_2x.equals(type) ||
					KarafBeanProvider.KARAF_3x.equals(type) ||
					KarafBeanProvider.KARAF_4x.equals(type);
		}
		return false;		
	}
}
