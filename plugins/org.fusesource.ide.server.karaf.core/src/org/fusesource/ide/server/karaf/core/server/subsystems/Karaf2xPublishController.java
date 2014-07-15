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
package org.fusesource.ide.server.karaf.core.server.subsystems;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Server;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.publish.IPublishBehaviour;
import org.fusesource.ide.server.karaf.core.publish.jmx.KarafJMXPublisher;
import org.fusesource.ide.server.karaf.core.util.KarafUtils;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.AbstractSubsystemController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController;

/**
 * @author lhein
 */
public class Karaf2xPublishController extends AbstractSubsystemController 
	implements IPublishController  {

	public static final List<String> GOALS = Arrays.asList(new String[] {"clean", "package"});
	
	protected IPublishBehaviour publisher = new KarafJMXPublisher();
	
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
		switch (publishType) {
		case KarafUtils.FULL_PUBLISH:
			if (!module[0].exists())
				break;
			// do a build
			boolean built = KarafUtils.runBuild(GOALS, module[0], monitor);
			status = this.publisher.publish(getServer(), module);
			((Server)getServer()).setModuleState(module, status);
			((Server)getServer()).setModulePublishState(module, IServer.PUBLISH_STATE_NONE);
			((Server)getServer()).setServerPublishState(IServer.PUBLISH_STATE_NONE);			
			status = IServer.PUBLISH_STATE_NONE;
			break;
		case KarafUtils.INCREMENTAL_PUBLISH:
			if (!module[0].exists())
				break;
			// do a build
			built = KarafUtils.runBuild(GOALS, module[0], monitor);
			status = this.publisher.publish(getServer(), module);
			((Server)getServer()).setModuleState(module, status);
			((Server)getServer()).setModulePublishState(module, IServer.PUBLISH_STATE_NONE);
			((Server)getServer()).setServerPublishState(IServer.PUBLISH_STATE_NONE);
			status = IServer.PUBLISH_STATE_NONE;
			break;
		case KarafUtils.NO_PUBLISH:
			// we can skip this
			break;
		case KarafUtils.REMOVE_PUBLISH:
			boolean done = this.publisher.uninstall(getServer(), module);
			if (done) {
				((Server)getServer()).setModuleState(module, IServer.STATE_UNKNOWN);
				((Server)getServer()).setModulePublishState(module, IServer.PUBLISH_STATE_UNKNOWN);
				((Server)getServer()).setServerPublishState(IServer.PUBLISH_STATE_NONE);
				status = IServer.PUBLISH_STATE_NONE;
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
}
