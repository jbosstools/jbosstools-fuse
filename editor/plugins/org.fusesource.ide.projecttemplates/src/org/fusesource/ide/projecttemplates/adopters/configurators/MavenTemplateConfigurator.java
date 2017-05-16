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

package org.fusesource.ide.projecttemplates.adopters.configurators;

import java.io.File;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;
import org.fusesource.ide.projecttemplates.util.BuildAndRefreshJobWaiterUtil;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;
import org.fusesource.ide.projecttemplates.util.maven.MavenUtils;

/**
 * this configurator provides helper methods for maven configuration
 * 
 * @author lhein
 */
public class MavenTemplateConfigurator extends DefaultTemplateConfigurator {
	
	@Override
	public boolean configure(IProject project, NewProjectMetaData metadata, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.MavenTemplateConfigurator_ConfiguringTemplatesMonitorMessage, 3);
		boolean ok = super.configure(project, metadata, subMonitor.newChild(1));

		if (ok) {
			// by default add the maven nature
			ok = configureMavenNature(project, subMonitor.newChild(1));
		}
		
		if (ok) {
			// by default configure the version of camel used in the pom.xml
			ok = configurePomCamelVersion(project, metadata, subMonitor.newChild(1));
		}
		
		return ok;
	}
	
	/**
	 * configures the maven nature for the given project
	 * 
	 * @param project	the project to enable maven nature
	 * @param monitor	the progress monitor
	 * @return	true on success
	 */
	protected boolean configureMavenNature(IProject project, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor,Messages.MavenTemplateConfigurator_ConfiguringMavenNatureMonitorMessage, 4);
		try {
			ResolverConfiguration configuration = new ResolverConfiguration();
			configuration.setResolveWorkspaceProjects(true);
			configuration.setSelectedProfiles(""); //$NON-NLS-1$
			new BuildAndRefreshJobWaiterUtil().waitJob(subMonitor.newChild(1));
			IProjectConfigurationManager configurationManager = MavenPlugin.getProjectConfigurationManager();
			configurationManager.enableMavenNature(project, configuration, subMonitor.newChild(1));
			configurationManager.updateProjectConfiguration(project, subMonitor.newChild(1));
			new BuildAndRefreshJobWaiterUtil().waitJob(subMonitor.newChild(1));
        } catch(CoreException ex) {
        	ProjectTemplatesActivator.pluginLog().logError(ex.getMessage(), ex);
        	return false;
        }
		return true;
	}
	
	/**
	 * changes all occurrences of Camel version in the pom.xml file with the
	 * version defined in the wizard
	 * 
	 * @param project			the project
	 * @param projectMetaData	the metadata containing the new version
	 * @param monitor			the progress monitor
	 * @return	true on success, otherwise false
	 */
	protected boolean configurePomCamelVersion(IProject project, NewProjectMetaData projectMetaData, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor,Messages.MavenTemplateConfigurator_AdaptingprojectToCamelVersionMonitorMessage, 7);
		try {
			File pomFile = new File(project.getFile(IMavenConstants.POM_FILE_NAME).getLocation().toOSString()); //$NON-NLS-1$
			Model m2m = new CamelMavenUtils().getMavenModel(project);
			subMonitor.worked(1);
			final String camelVersion = projectMetaData.getCamelVersion();
			if (m2m.getDependencyManagement() != null) {
				MavenUtils.updateCamelVersionDependencies(m2m, m2m.getDependencyManagement().getDependencies(), camelVersion);
			}
			subMonitor.worked(1);
			MavenUtils.updateCamelVersionDependencies(m2m, m2m.getDependencies(), camelVersion);
			if (m2m.getBuild().getPluginManagement() != null) {
				MavenUtils.updateCamelVersionPlugins(m2m, m2m.getBuild().getPluginManagement().getPlugins(), camelVersion);
			}
			subMonitor.worked(1);
			MavenUtils.updateCamelVersionPlugins(m2m, m2m.getBuild().getPlugins(), camelVersion);
			subMonitor.worked(1);
			
			if(projectMetaData.getTargetRuntime() == null){
				MavenUtils.alignFuseRuntimeVersion(m2m, camelVersion);
			} else {
				// we suppose that only one version of Fuse Runtime is possible for a Camel Version
				//TODO: find a way to retrieve the Fuse Runtime BOM version from the Target Runtime
				MavenUtils.alignFuseRuntimeVersion(m2m, camelVersion);
			}
			subMonitor.worked(1);
			
			MavenUtils.manageStagingRepositories(m2m);
			subMonitor.worked(1);
			
			new org.fusesource.ide.camel.editor.utils.MavenUtils().writeNewPomFile(project, pomFile, m2m);
		} catch (Exception ex) {
			ProjectTemplatesActivator.pluginLog().logError(ex);
			return false;
		}
		return true;
	}


}
