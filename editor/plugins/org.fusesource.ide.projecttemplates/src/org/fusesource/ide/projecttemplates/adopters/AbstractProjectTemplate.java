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
package org.fusesource.ide.projecttemplates.adopters;

import java.util.Collections;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.fusesource.ide.camel.model.service.core.util.OnlineArtifactVersionSearcher;
import org.fusesource.ide.projecttemplates.adopters.configurators.TemplateConfiguratorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.util.CommonNewProjectMetaData;
import org.fusesource.ide.projecttemplates.wizards.pages.model.EnvironmentData;

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
	public final boolean create(IProject project, CommonNewProjectMetaData projectMetaData, IProgressMonitor monitor) throws CoreException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
		// first we create the project template		
		boolean ok = getCreator(projectMetaData).create(project, projectMetaData, subMonitor.newChild(30));
		// then we configure the project
		if (ok) {
			refreshProjectSync(project, monitor);
			ok = getConfigurator().configure(project, projectMetaData, subMonitor.newChild(30));
			refreshProjectSync(project,  subMonitor.newChild(15));
			project.getFolder("bin").delete(true,  subMonitor.newChild(5)); //$NON-NLS-1$
			project.getFolder("build").delete(true,  subMonitor.newChild(5)); //$NON-NLS-1$
			refreshProjectSync(project,  subMonitor.newChild(15));
		}
		return ok;
	}

	private void refreshProjectSync(IProject project, IProgressMonitor monitor) throws CoreException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 2);
		project.refreshLocal(IProject.DEPTH_INFINITE, subMonitor.split(1));
		try {
			Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_REFRESH, subMonitor.split(1));
		} catch (OperationCanceledException e) {
			ProjectTemplatesActivator.pluginLog().logError(e);
		} catch (InterruptedException ex) { 
			ProjectTemplatesActivator.pluginLog().logError(ex);
			Thread.currentThread().interrupt();
		}
		subMonitor.setWorkRemaining(0);
	}
	
	/**
	 * @return the configurator
	 */
	public abstract TemplateConfiguratorSupport getConfigurator();
	
	/**
	 * @param projectMetaData
	 * @return the creator
	 */
	public abstract TemplateCreatorSupport getCreator(CommonNewProjectMetaData projectMetaData);

	public boolean isCompatible(EnvironmentData environment) {
		return true;
	}
	
	public String getArtifactLastVersion(String groupId, String artifactId) {
		Dependency bomToSearch = new Dependency();
		bomToSearch.setGroupId(groupId);
		bomToSearch.setArtifactId(artifactId);
		return new OnlineArtifactVersionSearcher().findLatestVersion(new NullProgressMonitor(), bomToSearch);
	}

	public boolean isDefault(EnvironmentData environment, CamelDSLType dslType) {
		return false;
	}

	public List<String> getJavaExecutionEnvironments() {
		return Collections.singletonList("JavaSE-1.8");
	}
}
