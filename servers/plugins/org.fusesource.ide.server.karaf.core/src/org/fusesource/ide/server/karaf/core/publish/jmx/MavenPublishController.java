/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.publish.jmx;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.util.KarafUtils;
import org.jboss.ide.eclipse.as.core.util.JBossServerBehaviorUtils;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.AbstractSubsystemController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IControllableServerBehavior;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPrimaryPublishController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishControllerDelegate;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.util.PublishControllerUtil;

public class MavenPublishController extends AbstractSubsystemController implements IPublishControllerDelegate {
	public static final List<String> GOALS = Arrays.asList("clean", "package");

	@Override
	public int publishModule(int kind, int deltaKind, IModule[] module,
			IProgressMonitor monitor) throws CoreException {
		int publishType = PublishControllerUtil.getPublishType(getServer(), module, kind, deltaKind);
		if( publishType == PublishControllerUtil.REMOVE_PUBLISH){
			return removeModule(module, monitor);
		}
		
		if( projectIsMaven(module)) {
			boolean built = KarafUtils.runBuild(GOALS, module[0], monitor);
			if( built ) {
				String fileUrl = KarafUtils.getBundleFilePath(module[0]);
				return transferBuiltModule(module, new Path(fileUrl), monitor);
			} else {
				// TODO error gracefully
			}
		}

		// We don't know how it published
		return IServer.PUBLISH_STATE_UNKNOWN;
	}
	
	private boolean projectIsMaven(IModule[] module) {
		return true; // TODO this is wrong, stubbed out. Maybe just check module type?
	}

	private int removeModule(IModule[] module, IProgressMonitor monitor) throws CoreException {
		IPrimaryPublishController pc = JBossServerBehaviorUtils.getController(getServer(), IPublishController.SYSTEM_ID, IPrimaryPublishController.class);
		if( pc != null) {
			return pc.removeModule(module, monitor);
		}
		return IServer.PUBLISH_STATE_UNKNOWN;
	}
	
	private int transferBuiltModule(IModule[] module, IPath srcFile, IProgressMonitor monitor) throws CoreException {
		IControllableServerBehavior beh = JBossServerBehaviorUtils.getControllableBehavior(getServer());
		if( beh != null ) {
			IPublishController pc = (IPublishController)beh.getController(IPublishController.SYSTEM_ID);
			if( pc instanceof IPrimaryPublishController) {
				return ((IPrimaryPublishController)pc).transferBuiltModule(module, srcFile, monitor);
			}
		}
		return IServer.PUBLISH_STATE_UNKNOWN;
	}
	
}
