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
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.ModuleArtifactAdapterDelegate;

public class CamelArtifactAdapter extends ModuleArtifactAdapterDelegate {
	public IModuleArtifact getModuleArtifact(Object obj) {
		if( obj instanceof IJavaElement ) {
			obj = ((IJavaElement)obj).getJavaProject().getProject();
		}
		if( obj instanceof IResource ) {
			IProject p = ((IResource)obj).getProject();
			if( p != null ) {
				IModule[] mods = ServerUtil.getModules(p);
				if( mods.length == 1 && mods[0].getModuleType().getId().equals(CamelModuleFactory.MODULE_TYPE)) {
					return new MBeanNullArtifact(mods[0]);
				}
			}
		}
		return null;
	}
	
	public static class MBeanNullArtifact implements IModuleArtifact {
		private IModule module;
		public MBeanNullArtifact(IModule mod) {
			this.module = mod;
		}
		
		public IModule getModule() {
			return module;
		}
	}

}
