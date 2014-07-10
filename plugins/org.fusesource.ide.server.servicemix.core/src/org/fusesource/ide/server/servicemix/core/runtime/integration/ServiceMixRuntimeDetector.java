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
package org.fusesource.ide.server.servicemix.core.runtime.integration;

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
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.ServerCore;
import org.fusesource.ide.server.servicemix.core.Activator;
import org.fusesource.ide.server.servicemix.core.bean.ServiceMixBeanProvider;
import org.fusesource.ide.server.servicemix.core.util.IServiceMixToolingConstants;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBean;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanLoader;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;
import org.jboss.tools.runtime.core.model.AbstractRuntimeDetectorDelegate;
import org.jboss.tools.runtime.core.model.RuntimeDefinition;

/**
 * @author lhein
 *
 */
public class ServiceMixRuntimeDetector extends AbstractRuntimeDetectorDelegate {
	
	/**
	 * Determine whether a runtime exists on the file-system level
	 * at the given root path. 
	 */
	public RuntimeDefinition getRuntimeDefinition(File root,
			IProgressMonitor monitor) {
		if (monitor.isCanceled() || root == null) {
			return null;
		}
		
		ServerBeanLoader loader = new ServerBeanLoader(root);
		ServerBean serverBean = loader.getServerBean();
		ServerBeanType type = serverBean.getBeanType();
		if( ServiceMixBeanProvider.SMX_4x.equals(type) ||
			ServiceMixBeanProvider.SMX_5x.equals(type) ) {
			RuntimeDefinition runtimeDefinition = new RuntimeDefinition(serverBean.getName(), 
					serverBean.getVersion(), type.getId(), new File(serverBean.getLocation()));
			return runtimeDefinition;
		}
		return null;
	}

	/**
	 * Determine whether the given runtime definition exists in the workspace.
	 */
	@Override
	public boolean exists(RuntimeDefinition runtimeDefinition ) {
		File path = runtimeDefinition == null ? null : runtimeDefinition.getLocation();
		return runtimeExistsAtLocation(path);
	}
	
	/**
	 * Create a runtime out of this runtime definition
	 */
	@Override
	public void initializeRuntimes(List<RuntimeDefinition> runtimeDefinitions) {
		createServiceMixServerFromDefinitions(runtimeDefinitions);
	}
	
	private void createServiceMixServerFromDefinitions(List<RuntimeDefinition> runtimeDefinitions) {
		for (RuntimeDefinition runtimeDefinition:runtimeDefinitions) {
			if (runtimeDefinition.isEnabled()) {
				File asLocation = runtimeDefinition.getLocation();
				if (asLocation != null && asLocation.isDirectory()) {
					String wtpServerType = new ServerBeanLoader(asLocation).getServerAdapterId();
					if( isServiceMixServerType(wtpServerType)) {
						String name = runtimeDefinition.getName();
						String runtimeName = name + " Runtime"; //$NON-NLS-1$
						createServiceMixServer(asLocation, wtpServerType, name, runtimeName);
					}
				}
			}
		}	
	}
	
	private boolean isServiceMixServerType(String type) {
		return Arrays.asList(IServiceMixToolingConstants.ALL_SMX_SERVER_TYPES).contains(type);
	}
	
	private static void createServiceMixServer(File loc, String serverTypeId, String name, String runtimeName) {
		if (loc == null || !loc.isDirectory() || serverTypeId == null)
			return;
		IServerType serverType = ServerCore.findServerType(serverTypeId);
		if( serverType == null )
			return;
		IRuntimeType rtType = serverType.getRuntimeType();
		if( rtType == null )
			return;
		
		try {
			IPath locPath = new Path(loc.getAbsolutePath());
			IRuntimeWorkingCopy rt = rtType.createRuntime(runtimeName, new NullProgressMonitor());
			rt.setLocation(locPath);
			rt.setName(runtimeName);
			// We don't need to set a vm, it can use default
			rt.save(true, new NullProgressMonitor());
			// TODO create the server also
		} catch(CoreException ce) {
			Activator.getLogger().error(ce);
		}
	}
	
	@Override
	public void computeIncludedRuntimeDefinition(
			RuntimeDefinition runtimeDefinition) {
		// ServiceMix has no nested runtime types
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
