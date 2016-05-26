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
package org.fusesource.ide.projecttemplates.adopters;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.projecttemplates.adopters.configurators.TemplateConfiguratorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;

/**
 * @author lhein
 */
public abstract class AbstractProjectTemplate {
	
	/**
	 * checks whether this template supports a given dsl type
	 * 
	 * @param type	the dsl type to check compatibility for
	 * @return	true if supported, otherwise false
	 */
	public boolean supportsDSL(CamelDSLType type) {
		// by default we support all DSL types
		return true;
	}
	
	/**
	 * starts the creation of the template and its configuration
	 * 
	 * @param project			the project to use for this template 
	 * @param projectMetaData	the metadata of the new project
	 * @return	true on success, otherwise false
	 * @throws CoreException	on any error
	 */
	public final boolean create(IProject project, NewProjectMetaData projectMetaData) throws CoreException {
		IProgressMonitor monitor = new NullProgressMonitor();
		// first we create the project template		
		boolean ok = getCreator(projectMetaData).create(project, projectMetaData);
		// then we configure the project
		if (ok) {
			project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
			ok = getConfigurator().configure(project, projectMetaData, monitor);
			project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
			project.getFolder("bin").delete(true, monitor);
			project.getFolder("build").delete(true, monitor);
			project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
		}
		return ok;
	}
	
	/**
	 * @return the configurator
	 */
	public abstract TemplateConfiguratorSupport getConfigurator();
	
	/**
	 * @param projectMetaData
	 * @return the creator
	 */
	public abstract TemplateCreatorSupport getCreator(NewProjectMetaData projectMetaData);
}
