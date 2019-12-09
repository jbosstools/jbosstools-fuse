/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.projecttemplates.adopters.configurators;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;

/**
 * @author lhein
 */
public interface TemplateConfiguratorSupport {
	
	/**
	 * this method is responsible for configuring the populated template project
	 * 
	 * @param project	the project to use for creating contents
	 * @param metadata	the metadata of the new project
	 * @param monitor	the progress monitor
	 * @return	true on success, otherwise false
	 */
	boolean configure(IProject project, CommonNewProjectMetaData metadata, IProgressMonitor monitor);
}
