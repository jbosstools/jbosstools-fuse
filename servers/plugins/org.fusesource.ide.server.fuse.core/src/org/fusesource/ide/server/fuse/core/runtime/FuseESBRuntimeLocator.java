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

package org.fusesource.ide.server.fuse.core.runtime;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.ServerCore;
import org.fusesource.ide.server.fuse.core.Activator;
import org.fusesource.ide.server.fuse.core.bean.FuseBeanProvider;
import org.fusesource.ide.server.karaf.core.runtime.KarafRuntimeLocator;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBean;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanLoader;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanType;


/**
 * @author lhein
 */
public class FuseESBRuntimeLocator extends KarafRuntimeLocator {
	
	/**
	 * empty default constructor
	 */
	public FuseESBRuntimeLocator() {
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
				if( type.equals(FuseBeanProvider.FUSE_6x)) {
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
			}
		}
		return null;
	}
}
