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
package org.fusesource.ide.server.fabric8.core.server.subsystems;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Server;
import org.fusesource.ide.server.fabric8.core.Activator;
import org.fusesource.ide.server.karaf.core.server.KarafServerDelegate;
import org.fusesource.ide.server.karaf.core.util.KarafUtils;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.AbstractSubsystemController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController;

/**
 * @author lhein
 */
public class Fabric81xPublishController extends AbstractSubsystemController
		implements IPublishController {

	public static final List<String> BUILD_GOALS = Arrays.asList(new String[] {"clean", "package"});
	public static final List<String> DEPLOY_GOALS = Arrays.asList(new String[] {"io.fabric8:fabric8-maven-plugin:1.1.0-SNAPSHOT:deploy"});

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController#canPublish()
	 */
	@Override
	public IStatus canPublish() {
		return Status.OK_STATUS;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController#canPublishModule(org.eclipse.wst.server.core.IModule[])
	 */
	@Override
	public boolean canPublishModule(IModule[] module) {
		for (IModule m : module) {
			if (!m.getModuleType().getId().equals("fuse.camel") && !m.getModuleType().getVersion().equals("1.0")) {
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController#publishStart(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void publishStart(IProgressMonitor monitor) throws CoreException {
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController#publishFinish(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void publishFinish(IProgressMonitor monitor) throws CoreException {
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController#publishModule(int, int, org.eclipse.wst.server.core.IModule[], org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public int publishModule(int kind, int deltaKind, IModule[] module,
			IProgressMonitor monitor) throws CoreException {
		monitor = monitor == null ? new NullProgressMonitor() : monitor; // nullsafe
		validate();
		int status = IServer.STATE_UNKNOWN;
		int publishType = KarafUtils.getPublishType(getServer(), module, kind, deltaKind);

		Properties serverProperties = getServerProperties((KarafServerDelegate)getServer().loadAdapter(KarafServerDelegate.class, monitor));
		if (serverProperties == null || serverProperties.isEmpty()) return status;
		
		switch (publishType) {
		case KarafUtils.FULL_PUBLISH:
			boolean deployed = KarafUtils.runBuild(DEPLOY_GOALS, serverProperties, module[0], monitor);
			status = deployed ? IServer.STATE_STARTED : IServer.STATE_UNKNOWN;
			((Server)getServer()).setServerPublishState(IServer.PUBLISH_STATE_NONE);
			((Server)getServer()).setModuleState(module, status);
			break;
		case KarafUtils.INCREMENTAL_PUBLISH:
			deployed = KarafUtils.runBuild(DEPLOY_GOALS, serverProperties, module[0], monitor);
			status = deployed ? IServer.STATE_STARTED : IServer.STATE_UNKNOWN;
			((Server)getServer()).setServerPublishState(IServer.PUBLISH_STATE_NONE);
			((Server)getServer()).setModuleState(module, status);
			break;
		case KarafUtils.NO_PUBLISH:
			// we can skip this
			break;
		case KarafUtils.REMOVE_PUBLISH:
			boolean done = uninstall(getServer(), module);
			if (done) {
				 ((Server)getServer()).setServerPublishState(IServer.PUBLISH_STATE_NONE);
				 ((Server)getServer()).setModuleState(module, IServer.STATE_UNKNOWN);
			}
			break;
		default:
			Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, "Unknown publish type " + publishType));
		}
		
		return status;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController#publishServer(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void publishServer(int kind, IProgressMonitor monitor)
			throws CoreException {
		validate();
	}
	
	/**
	 * retrieves user, password and server id for the maven proxy used for 
	 * deployments to fabric8
	 * 
	 * @param delegate
	 * @return
	 */
	protected Properties getServerProperties(KarafServerDelegate delegate) {
		Properties props = new Properties();
		
		if (delegate != null) {
			props.setProperty(KarafUtils.SERVER_ID, "fabric8.upload.repo");
			props.setProperty(KarafUtils.SERVER_USER, delegate.getUserName());
			props.setProperty(KarafUtils.SERVER_PASSWORD, delegate.getPassword());
		}
		
		return props;
	}
	
	/**
	 * uninstalls the bundle from any profile and also from the connected 
	 * fabric8 instance
	 * 
	 * @param server
	 * @param module
	 * @return
	 */
	protected boolean uninstall(IServer server, IModule[] module) {
		// TODO: find a clean way to uninstall from Fabric8
		Activator.getLogger().warning("Uninstalling bundles from Fabric8 profiles is currently unsupported. Please do that inside the Fabric8 shell.");
		return false;
	}
}
