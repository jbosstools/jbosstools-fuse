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

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.componentcore.internal.flat.IChildModuleReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.web.internal.deployables.FlatComponentDeployable;
import org.jboss.ide.eclipse.as.wtp.core.modules.JBTFlatProjectModuleFactory;

/**
 * @author lhein
 */
public class CamelModuleFactory extends JBTFlatProjectModuleFactory {
	public static final String FACTORY_ID = "org.fusesource.ide.project.fuseModuleFactory"; //$NON-NLS-1$
	public static final String MODULE_TYPE = ICamelFacetDataModelProperties.CAMEL_PROJECT_FACET;
	public static final String V1_0 = "1.0"; //$NON-NLS-1$
	
	public String getFactoryId() {
		return FACTORY_ID;
	}
	
	public CamelModuleFactory() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.modules.JBTFlatProjectModuleFactory#createModuleDelegate(org.eclipse.core.resources.IProject, org.eclipse.wst.common.componentcore.resources.IVirtualComponent)
	 */
	@Override
	protected FlatComponentDeployable createModuleDelegate(IProject project, IVirtualComponent component) {
		return new CamelModuleDelegate(project, component, this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.modules.JBTFlatProjectModuleFactory#canHandleProject(org.eclipse.core.resources.IProject)
	 */
	@Override
	protected boolean canHandleProject(IProject project) {
		return hasProjectFacet(project, MODULE_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.modules.JBTFlatProjectModuleFactory#getModuleType(org.eclipse.core.resources.IProject)
	 */
	@Override
	protected String getModuleType(IProject project) {
		return MODULE_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.modules.JBTFlatProjectModuleFactory#getModuleVersion(org.eclipse.core.resources.IProject)
	 */
	@Override
	protected String getModuleVersion(IProject project) {
		return V1_0;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.modules.JBTFlatProjectModuleFactory#getModuleType(java.io.File)
	 */
	@Override
	protected String getModuleType(File binaryFile) {
		// sar allows no child modules
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.modules.JBTFlatProjectModuleFactory#getModuleVersion(java.io.File)
	 */
	@Override
	protected String getModuleVersion(File binaryFile) {
		// sar allows no child modules
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.modules.JBTFlatProjectModuleFactory#createChildModule(org.eclipse.wst.web.internal.deployables.FlatComponentDeployable, org.eclipse.wst.common.componentcore.internal.flat.IChildModuleReference)
	 */
	@Override 
	public IModule createChildModule(FlatComponentDeployable parent, IChildModuleReference child) {
		// sar allows no child modules
		return null;
	}
}
