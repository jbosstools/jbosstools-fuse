/*******************************************************************************
 * Copyright (c) 2007 - 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.project.camel;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IRuntimeChangedEvent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.jboss.ide.eclipse.as.wtp.core.vcf.VCFClasspathCommand;

public class CamelRuntimeChangedDelegate implements IDelegate {

	@Override
	public void execute(IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
    	IRuntimeChangedEvent event = (IRuntimeChangedEvent)config;
    	IRuntime oldRT = event.getOldRuntime();
    	IRuntime newRT = event.getNewRuntime();
    	
    	if( oldRT != null )
    		VCFClasspathCommand.removeContainerClasspathEntry(project, getContainerPath(oldRT));
    	if( newRT != null )
    		VCFClasspathCommand.addContainerClasspathEntry(project, getContainerPath(newRT));
    	
	}
	
	public static IPath getContainerPath(IRuntime runtime) {
		
		// TODO THIS IS INCOMPLETE
		
//		org.eclipse.wst.server.core.IRuntime serverRuntime = ServerCore.findRuntime(runtime.getName());
//		RuntimeClasspathProviderWrapper rcpw = JavaServerPlugin.findRuntimeClasspathProvider(serverRuntime.getRuntimeType());
//		IPath serverContainerPath = new Path(RuntimeClasspathContainer.SERVER_CONTAINER)
//			.append(rcpw.getId()).append(serverRuntime.getId());
//		return serverContainerPath;
		return null;
	}
}
