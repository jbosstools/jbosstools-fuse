/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.util;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author lheinema
 */
public class ProjectTemplatePatcher {
	
	private CommonNewProjectMetaData projectMetaData;
	
	/**
	 * creates a patcher which will do tweaks to the template if needed
	 * 
	 * @param project
	 * @param projectMetaData
	 */
	public ProjectTemplatePatcher(CommonNewProjectMetaData projectMetaData) {
		this.projectMetaData = projectMetaData;
	}
	
	/**
	 * executes the patcher
	 * 
	 * @param m2m	the maven model 
	 * @param monitor	the progress monitor
	 */
	public void patch(Model m2m, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 1);
		// since Fuse 6.3.0.R4 patch release Aries Proxy has been removed - we need to strip them from the templates pom.xml then
		if (areAriesProxyDependenciesToBeRemoved(this.projectMetaData)) {
			removeAriesProxyDependencies(m2m, subMonitor.split(1));
		}
		subMonitor.setWorkRemaining(0);
	}
	
	private void removeAriesProxyDependencies(Model m2m, IProgressMonitor monitor) {
		List<Dependency> dependencies = m2m.getDependencies();
		if(dependencies != null) {
			List<Dependency> toRemove = getDependenciesToRemove(dependencies);
			SubMonitor subMonitor = SubMonitor.convert(monitor, toRemove.size());
			toRemove.forEach(
					depToRemove -> {
						m2m.removeDependency(depToRemove);
						subMonitor.worked(1);
						});
		}
	}

	protected List<Dependency> getDependenciesToRemove(List<Dependency> dependencies) {
		return dependencies.stream()
		.filter(dep -> dep != null && "org.apache.aries.proxy".equalsIgnoreCase(dep.getGroupId()))
		.collect(Collectors.toList());
	}
	
	private boolean areAriesProxyDependenciesToBeRemoved(CommonNewProjectMetaData projectMetaData) {
		String camelVersion = Strings.isBlank(projectMetaData.getCamelVersion()) ? new CamelMavenUtils().getCamelVersionFromProjectName(projectMetaData.getProjectName()) : projectMetaData.getCamelVersion();
		if (new CamelMavenUtils().isRedHatBrandedVersion(camelVersion)) { 
			ComparableVersion v1 = new ComparableVersion(camelVersion);
			ComparableVersion v2 = new ComparableVersion(CamelCatalogUtils.FUSE_63_R4_CAMEL_VERSION);
			return v1.compareTo(v2) >= 0; 
		}
		return false;
	}
}
