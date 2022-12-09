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
package org.fusesource.ide.projecttemplates.adopters.creators;

import java.util.Collections;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.m2e.core.project.IArchetype;
import org.eclipse.m2e.core.ui.internal.M2EUIPluginActivator;
import org.eclipse.m2e.core.ui.internal.archetype.ArchetypeGenerator;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;

/**
 * this class can be used to create a template project from an existing 
 * maven archetype. due to lack of config in the wizard we have to fill in 
 * the missing config items manually.
 * 
 * @author lhein
 */
public abstract class ArchetypeTemplateCreator implements TemplateCreatorSupport {

	/* (non-Javadoc)
	 * @see org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport#create(org.eclipse.core.resources.IProject, org.fusesource.ide.projecttemplates.util.NewFuseIntegrationProjectMetaData)
	 */
	@Override
	public boolean create(IProject project, CommonNewProjectMetaData metadata, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.archetypeTemplateCreatorCreatingTemplateFromArchetypeMonitorMessage, 2);
		IArchetype archetype = getArchetype(metadata, subMonitor.newChild(1));
		
		ArchetypeGenerator generator = M2EUIPluginActivator.getDefault().getArchetypePlugin().getGenerator();
		try {
			generator.createArchetypeProjects(project.getLocation(),
					archetype,
					archetype.getGroupId(),
					archetype.getArtifactId(),
					archetype.getVersion(),
					getJavaPackage(),
					Collections.emptyMap(),
					subMonitor.newChild(1)); 
		} catch (CoreException ex) {
			ProjectTemplatesActivator.pluginLog().logError(ex);
			return false;
		}
		return true;
	}
	
	/**
	 * returns the archetype to execute
	 * 
	 * @param metadata	the project meta data
	 * @param monitor 
	 * @return	the archetype
	 */
	protected abstract IArchetype getArchetype(CommonNewProjectMetaData metadata, IProgressMonitor monitor);

	/**
	 * returns the java package
	 * 
	 * @return
	 */
	protected abstract String getJavaPackage();
}
