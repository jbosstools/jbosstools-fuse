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
package org.fusesource.ide.projecttemplates.tests.integration.wizards;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.util.NewFuseIntegrationProjectMetaData;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FuseIntegrationProjectCreatorRunnableForSimpleIT extends FuseIntegrationProjectCreatorRunnableIT{
	
	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();
	
	@Parameters(name = "{0}")
	public static List<String> parameters(){
		return CamelCatalogUtils.getCamelVersionsToTestWith(); 
	}
	
	public FuseIntegrationProjectCreatorRunnableForSimpleIT(String version) {
		super();
		camelVersion = version;
	}

	
	@Test
	public void testEmptyBlueprintProjectCreation() throws Exception {
		testProjectCreation("-SimpleBlueprintProject-"+camelVersion, CamelDSLType.BLUEPRINT, "src/main/resources/OSGI-INF/blueprint/blueprint.xml", null);
	}

	@Test
	public void testEmptySpringProjectCreation() throws Exception {
		testProjectCreation("-SimpleSpringProject-"+camelVersion, CamelDSLType.SPRING, "src/main/resources/META-INF/spring/camel-context.xml", null);
	}

	@Test
	public void testEmptySpringProjectCreationOnLocationOutsideWorkspace() throws Exception {
		NewFuseIntegrationProjectMetaData metadata = createDefaultNewProjectMetadata(CamelDSLType.SPRING,
				getClass().getSimpleName() + "-SimpleSpringProject_outsideProject");
		File folderForprojectOutsiddeWorkspaceLocation = tmpFolder.newFolder("folderForProjectOutsideWorkspaceLocation");
		final Path locationPath = new Path(folderForprojectOutsiddeWorkspaceLocation.getAbsolutePath());
		metadata.setLocationPath(locationPath);
		testProjectCreation("-SimpleSpringProject_outsideProject", CamelDSLType.SPRING, "src/main/resources/META-INF/spring/camel-context.xml", metadata);

		assertThat(Files.isSameFile(project.getLocation().toFile().toPath(), locationPath.toFile().toPath())).isTrue();
	}

	@Test
	public void testEmptyJavaProjectCreation() throws Exception {          
		testProjectCreation("-SimpleJavaProject-"+camelVersion, CamelDSLType.JAVA, "src/main/java/com/mycompany/CamelRoute.java", null);
	}
	
	@Override
	protected void launchDebug(IProject project) throws InterruptedException {
		// TODO: currently we generate simple project which are not valid so cannot be launched
	}
	
	@Override
	protected void additionalChecks(IProject project) {
		String textToFind;
		if(isOlderThan220()){
			textToFind = "<groupId>org.jboss.fuse.bom</groupId>";
		} else if(isNewerThan221()) {
			textToFind = "<groupId>org.jboss.redhat-fuse</groupId>";
		} else {
			textToFind = "<groupId>org.jboss.fuse</groupId>";
		}
		try {
			assertThat(Files.readAllLines(project.findMember("pom.xml").getLocation().toFile().toPath())
					.stream()
					.filter(line -> line.contains(textToFind))
					.findAny().orElse(null))
			.isNotNull();
		} catch (IOException e) {
			fail("pom seems not available", e);
		}
	}
}
