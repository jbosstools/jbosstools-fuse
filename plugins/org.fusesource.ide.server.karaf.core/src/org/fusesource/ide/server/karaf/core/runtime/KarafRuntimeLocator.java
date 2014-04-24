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

import java.io.File;
import java.io.FileFilter;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.RuntimeLocatorDelegate;
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
	
	/**
	 * empty default constructor
	 */
	public KarafRuntimeLocator() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.RuntimeLocatorDelegate#searchForRuntimes(org.eclipse.core.runtime.IPath, org.eclipse.wst.server.core.model.RuntimeLocatorDelegate.IRuntimeSearchListener, org.eclipse.core.runtime.IProgressMonitor)
	 */
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
		File[] files = null;
		if (folder == null) {
			files = File.listRoots();
		} else {
			files = folder.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			});
		}
		
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
		if( sb != null ) {
			ServerBeanType type = sb.getBeanType();
			if( type != null ) {
				if( type.equals(KarafBeanProvider.KARAF_2x)) {
					String serverType = l.getServerAdapterId();
					if( serverType != null ) {
						IServerType t = ServerCore.findServerType(serverType);
						if( t != null ) {
							IRuntimeType rtt = t.getRuntimeType();
							try {
								IRuntimeWorkingCopy runtime = rtt.createRuntime(rtt.getId(), monitor);
								// commented out the naming of the runtime as it seems to break server to runtime links
								runtime.setName(dir.getName());
								runtime.setLocation(new Path(absolutePath));
								IKarafRuntimeWorkingCopy wc = (IKarafRuntimeWorkingCopy) runtime.loadAdapter(IKarafRuntimeWorkingCopy.class, null);
								wc.setKarafInstallDir(absolutePath);
								wc.setKarafVersion(sb.getFullVersion());
								wc.setKarafPropertiesFileLocation("");
								IStatus status = runtime.validate(monitor);
								if (status == null || status.getSeverity() != IStatus.ERROR) {
									return runtime;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
				}
			}
		}
		return null;
	}
}
