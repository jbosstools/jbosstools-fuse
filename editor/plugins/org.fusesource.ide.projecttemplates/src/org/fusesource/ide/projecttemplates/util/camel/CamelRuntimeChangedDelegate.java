/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.projecttemplates.util.camel;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.server.core.internal.JavaServerPlugin;
import org.eclipse.jst.server.core.internal.RuntimeClasspathContainer;
import org.eclipse.jst.server.core.internal.RuntimeClasspathProviderWrapper;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IRuntimeChangedEvent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
import org.jboss.ide.eclipse.as.wtp.core.vcf.VCFClasspathCommand;

/**
 * @author lhein
 */
public class CamelRuntimeChangedDelegate implements IDelegate {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.common.project.facet.core.IDelegate#execute(org.eclipse.core.resources.IProject, org.eclipse.wst.common.project.facet.core.IProjectFacetVersion, java.lang.Object, org.eclipse.core.runtime.IProgressMonitor)
	 */
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
		org.eclipse.wst.server.core.IRuntime serverRuntime = ServerCore.findRuntime(runtime.getName());
		RuntimeClasspathProviderWrapper rcpw = JavaServerPlugin.findRuntimeClasspathProvider(serverRuntime.getRuntimeType());
		IPath serverContainerPath = new Path(RuntimeClasspathContainer.SERVER_CONTAINER).append(rcpw.getId()).append(serverRuntime.getId());
		return serverContainerPath;
	}
}
