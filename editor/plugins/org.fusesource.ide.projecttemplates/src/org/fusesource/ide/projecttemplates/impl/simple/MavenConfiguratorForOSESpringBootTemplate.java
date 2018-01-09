/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.impl.simple;

import java.nio.file.Path;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.projecttemplates.adopters.configurators.MavenTemplateConfigurator;

final class MavenConfiguratorForOSESpringBootTemplate extends MavenTemplateConfigurator {

	MavenConfiguratorForOSESpringBootTemplate(String bomVersion) {
		super(bomVersion);
	}

	@Override
	protected void configureVersions(IProject project, IProgressMonitor monitor) throws CoreException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 3);
		super.configureVersions(project, subMonitor.split(1));
		IFile pom = project.getFile("pom.xml");
		Path pomAbsolutePath = pom.getLocation().toFile().toPath();
		String fabric8MavenPluginVersion = CamelCatalogUtils.getFabric8MavenPluginVersionForBomVersion(bomVersion, subMonitor.split(1));
		replace(fabric8MavenPluginVersion, OSESpringBootXMLTemplateForFuse7.PLACEHOLDER_FABRIC8MAVENPLUGIN_VERSION, pomAbsolutePath, pomAbsolutePath);
		project.refreshLocal(IResource.DEPTH_ONE, subMonitor.split(1));
	}
}