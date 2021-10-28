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
package org.fusesource.ide.projecttemplates.tests.integration.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import javax.management.MalformedObjectNameException;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.wst.server.core.IRuntime;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.impl.simple.EmptyProjectTemplateForFuse6;
import org.fusesource.ide.projecttemplates.util.NewFuseIntegrationProjectMetaData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FuseIntegrationProjectCreatorRunnableCheckForBomVersionIT extends FuseIntegrationProjectCreatorRunnableIT {
	
	//TODO: runtime behavior will need to be updated in case we support direct retrieval of Fuse Runtime bom version from a Fuse Runtime server
	@Mock
	private IRuntime runtime;
	
	@Override
	@Before
	public void setup() throws Exception {
		camelVersion = CamelCatalogUtils.CAMEL_VERSION_LATEST_PRODUCTIZED_63;
		super.setup();
	}
	

	@Test
	public void testFuseBomAlignedToCamelVersionWhenNoTargetRuntimeSelected() throws Exception {
		String projectNameSuffix = "-withoutRuntime-"+camelVersion;
		final String projectName = getClass().getSimpleName() + projectNameSuffix;
		
		NewFuseIntegrationProjectMetaData metadata;
		metadata = new NewFuseIntegrationProjectMetaData();
		metadata.setProjectName(projectName);
		metadata.setLocationPath(null);
		metadata.setCamelVersion(camelVersion);
		metadata.setTargetRuntime(null);
		metadata.setDslType(CamelDSLType.SPRING);
		metadata.setTemplate(new EmptyProjectTemplateForFuse6());
		
		testProjectCreation(projectNameSuffix, CamelDSLType.SPRING, "src/main/resources/META-INF/spring/camel-context.xml", metadata);
		
		checkBomVersion();
	}
	
	@Test
	public void testFuseBomAlignedToCamelVersionWhenTargetRuntimeSelected() throws Exception {
		String projectNameSuffix = "-withRuntime-"+camelVersion;
		final String projectName = getClass().getSimpleName() + projectNameSuffix;
		
		NewFuseIntegrationProjectMetaData metadata;
		metadata = new NewFuseIntegrationProjectMetaData();
		metadata.setProjectName(projectName);
		metadata.setLocationPath(null);
		metadata.setCamelVersion(camelVersion);
		metadata.setTargetRuntime(runtime);
		metadata.setDslType(CamelDSLType.SPRING);
		metadata.setTemplate(new EmptyProjectTemplateForFuse6());
		
		testProjectCreation(projectNameSuffix, CamelDSLType.SPRING, "src/main/resources/META-INF/spring/camel-context.xml", metadata);
		
		checkBomVersion();
	}
	
	private void checkBomVersion() throws CoreException {
		IMavenProjectFacade mavenProjectFacade = MavenPlugin.getMavenProjectRegistry().getProject(project);
		MavenProject mavenProject = mavenProjectFacade.getMavenProject(new NullProgressMonitor());
		assertThat(mavenProject.getProperties().getProperty("jboss.fuse.bom.version")).isEqualTo(CamelCatalogUtils.getBomVersionForCamelVersion(camelVersion, mavenProject.getOriginalModel(), new NullProgressMonitor()));
		
	}

	@Override
	protected void launchDebug(IProject project) throws InterruptedException, IOException, MalformedObjectNameException, DebugException {
		// not the purpose of this test
	}
}
