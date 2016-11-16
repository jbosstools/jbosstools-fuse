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
package org.fusesource.ide.projecttemplates.tests.integration.util.camel;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.fusesource.ide.camel.editor.provider.ActiveMQPaletteEntryDependenciesManager;
import org.fusesource.ide.projecttemplates.maven.CamelProjectConfigurator;
import org.fusesource.ide.projecttemplates.tests.integration.wizards.MavenProjectHelper;
import org.fusesource.ide.projecttemplates.util.camel.CamelFacetVersionChangeDelegate;
import org.junit.After;
import org.junit.Test;

public class CamelFacetVersionChangeDelegateIT {
	
	private static final IProjectFacet PROJECT_FACET = ProjectFacetsManager.getProjectFacet("jst.camel");
	private static final String POM_XML = "pom.xml";
	private IProject project;
	
	@After
	public void tearDown() throws CoreException{
		if(project != null){
			project.delete(true, new NullProgressMonitor());
		}
	}

	@Test
	public void testActiveMQUpdate() throws Exception {
		File projectFolder = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile(), "maven-project-withAMQDep");
		projectFolder.mkdirs();
		final File file = new File(projectFolder, POM_XML);
		Files.copy(CamelFacetVersionChangeDelegateIT.class.getResourceAsStream(POM_XML), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		project = new MavenProjectHelper().importProjects(projectFolder, new String[]{POM_XML})[0];
		
		testUpdate(CamelProjectConfigurator.camelFacet.getLatestVersion(), ActiveMQPaletteEntryDependenciesManager.LATEST_AMQ_VERSION);
		testUpdate(CamelProjectConfigurator.camelFacet.getVersion("2.15.1.redhat-621084"), ActiveMQPaletteEntryDependenciesManager.camelToAMQVersionMapping.get("2.15.1")+".redhat-621084");
	}

	private void testUpdate(final IProjectFacetVersion facetVersion, final String expectedDependencyVersion) throws CoreException {
		new CamelFacetVersionChangeDelegate().execute(project, facetVersion, null, new NullProgressMonitor());
		Model m2m = MavenPlugin.getMaven().readModel(project.getFile(POM_XML).getLocation().toFile());
		assertThat(m2m.getDependencies().get(0).getVersion()).isEqualTo(expectedDependencyVersion );
	}
	
	@Test
	public void testImportProjectWithRedhatUnknownQualifier() throws Exception {
		File projectFolder = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile(), "maven-project-WithRedhatUnknownQualifier");
		projectFolder.mkdirs();
		final File pomFile = new File(projectFolder, POM_XML);
		Files.copy(CamelFacetVersionChangeDelegateIT.class.getResourceAsStream("/projectWithRedhatVersionNotEmbedded/"+POM_XML), pomFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		String camelContextPathInProject = "src/main/resources/META-INF/spring/camel-context.xml";
		File camelContext = new File(projectFolder,camelContextPathInProject);
		camelContext.getParentFile().mkdirs();
		Files.copy(CamelFacetVersionChangeDelegateIT.class.getResourceAsStream("/projectWithRedhatVersionNotEmbedded/"+camelContextPathInProject), camelContext.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
		project = new MavenProjectHelper().importProjects(projectFolder, new String[]{POM_XML})[0];
		
		IFacetedProject fproj = ProjectFacetsManager.create(project);
		IProjectFacetVersion camelProjectFacetVersion = fproj.getProjectFacetVersion(PROJECT_FACET);
		
		assertThat(camelProjectFacetVersion.getVersionString()).isEqualTo("2.17.0.redhat-630187");
	}

}
