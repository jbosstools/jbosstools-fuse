/*************************************************************************************
 * Copyright (c) 2014 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat - Initial implementation.
 ************************************************************************************/
package org.fusesource.ide.server.karaf.core.runtime.integration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.bean.KarafBeanProvider;
import org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBean;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanLoader;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;
import org.jboss.tools.runtime.core.model.AbstractRuntimeDetectorDelegate;
import org.jboss.tools.runtime.core.model.RuntimeDefinition;

public class KarafRuntimeDetector extends AbstractRuntimeDetectorDelegate {
	
	/**
	 * Determine whether a runtime exists on the file-system level
	 * at the given root path. 
	 */
	@Override
	public RuntimeDefinition getRuntimeDefinition(File root,
			IProgressMonitor monitor) {
		if (monitor.isCanceled() || root == null) {
			return null;
		}
		
		ServerBeanLoader loader = new ServerBeanLoader(root);
		ServerBean serverBean = loader.getServerBean();
		ServerBeanType type = serverBean.getBeanType();
		List<ServerBeanType> valid = Arrays.asList(getServerBeanTypes());
		if( valid.contains(type)) {
			return new RuntimeDefinition(serverBean.getName(), serverBean.getVersion(), type.getId(), new File(serverBean.getLocation()), findMyDetector());
		}
		return null;
	}

	protected ServerBeanType[] getServerBeanTypes() {
		return new ServerBeanType[]{
				KarafBeanProvider.KARAF_2x, KarafBeanProvider.KARAF_3x, KarafBeanProvider.KARAF_4x
		};
	}

	protected boolean isValidServerType(String type) {
		return Arrays.asList(IKarafToolingConstants.ALL_KARAF_SERVER_TYPES).contains(type);
	}
	
	/**
	 * Determine whether the given runtime definition exists in the workspace.
	 */
	@Override
	public boolean exists(RuntimeDefinition runtimeDefinition ) {
		File path = runtimeDefinition == null ? null : runtimeDefinition.getLocation();
		return runtimeExistsAtLocation(path);
	}
	
	@Override
	public boolean initializeRuntime(RuntimeDefinition runtimeDefinition) throws CoreException {
		if (runtimeDefinition.isEnabled()) {
			File asLocation = runtimeDefinition.getLocation();
			if (asLocation != null && asLocation.isDirectory()) {
				String wtpServerType = new ServerBeanLoader(asLocation).getServerAdapterId();
				if( isValidServerType(wtpServerType)) {
					String name = runtimeDefinition.getName();
					String runtimeName = name + " Runtime"; //$NON-NLS-1$
					return createServer(asLocation, wtpServerType, name, runtimeName);
				}
			}
		}
		return false;
	}
	
	
	private static boolean createServer(File loc, String serverTypeId, String name, String runtimeName) {
		if (loc == null || !loc.isDirectory() || serverTypeId == null)
			return false;
		IServerType serverType = ServerCore.findServerType(serverTypeId);
		if( serverType == null )
			return false;
		IRuntimeType rtType = serverType.getRuntimeType();
		if( rtType == null )
			return false;
		
		IRuntimeWorkingCopy rt = null;
		try {
			IPath locPath = new Path(loc.getAbsolutePath());
			rt = rtType.createRuntime(runtimeName, new NullProgressMonitor());
			rt.setLocation(locPath);
			rt.setName(runtimeName);
			// We don't need to set a vm, it can use default
			IRuntime rtret = rt.save(true, new NullProgressMonitor());
			
			IServerWorkingCopy wc = serverType.createServer(name, null, rtret, new NullProgressMonitor());
			IServer saved = wc.save(true, new NullProgressMonitor());
			return saved != null;
		} catch(CoreException ce) {
			Activator.getLogger().error(ce);
		}
		return false;
	}
	
	/**
	 * @deprecated
	 */
	@Override @Deprecated
	public void computeIncludedRuntimeDefinition(
			RuntimeDefinition runtimeDefinition) {
		// Karaf has no nested runtime types
	}


	private boolean runtimeExistsAtLocation(File path) {
		if( path != null ) {
			IRuntime[] all = ServerCore.getRuntimes();
			for( int i = 0; i < all.length; i++ ) {
				File f = all[i].getLocation().toFile();
				if( path.equals(f)) {
					return true;
				}
			}
		}
		return false;
	}
	
}
