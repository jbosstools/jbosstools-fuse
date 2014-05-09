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
package org.fusesource.ide.project.integration;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate;

/**
 * @author lhein
 */
public class CamelModuleFactory extends ProjectModuleFactoryDelegate {
	
	public static final String MODULE_TYPE = "fuse.camel"; //$NON-NLS-1$
	public static final String VERSION = "1.0"; //$NON-NLS-1$

	public class CamelModuleDelegate extends ModuleDelegate {
				
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.wst.server.core.model.ModuleDelegate#validate()
		 */
		@Override
		public IStatus validate() {
			return null;
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.wst.server.core.model.ModuleDelegate#getChildModules()
		 */
		@Override
		public IModule[] getChildModules() {
			return new IModule[]{};
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.wst.server.core.model.ModuleDelegate#members()
		 */
		@Override
		public IModuleResource[] members() throws CoreException {
			return new IModuleResource[]{};
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.ModuleFactoryDelegate#getModuleDelegate(org.eclipse.wst.server.core.IModule)
	 */
	@Override
	public ModuleDelegate getModuleDelegate(IModule module) {
		return new CamelModuleDelegate();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate#createModules(org.eclipse.core.resources.IProject)
	 */
	@Override
	protected IModule[] createModules(final IProject project) {
		IModule module = createModule(project.getName(), project.getName(), 
				MODULE_TYPE, VERSION, project);
		return new IModule[] { module };
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate#getListenerPaths()
	 */
	@Override
	protected IPath[] getListenerPaths() {
		return new IPath[] { new Path(".project"), // nature //$NON-NLS-1$
				new Path("META-INF/MANIFEST.MF"), // manifest //$NON-NLS-1$
				new Path("META-INF/spring/"), // spring contexts //$NON-NLS-1$
				new Path("pom.xml"), // pom xml //$NON-NLS-1$
				new Path("OSGI-INF/blueprint/"), // blueprint contexts //$NON-NLS-1$
				new Path(".settings/org.eclipse.pde.core.prefs") // pde prefs //$NON-NLS-1$
		};
	}
}
