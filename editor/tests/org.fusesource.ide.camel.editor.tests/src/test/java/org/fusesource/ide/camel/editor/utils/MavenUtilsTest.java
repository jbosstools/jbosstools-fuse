/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@Ignore("Disabled test because its broken due to the model rework FUSETOOLS-2290")
@RunWith(MockitoJUnitRunner.class)
public class MavenUtilsTest {

	@Spy
	private MavenUtils mavenUtils;
	@Mock
	private IProject project;
	@Mock
	private File pomFile;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private IMavenProjectFacade mavenProjectFacade;
	@Spy
	private CamelMavenUtils camelMavenUtils;

	@Captor
	private ArgumentCaptor<List<org.fusesource.ide.camel.model.service.core.catalog.Dependency>> captor;

	@Before
	public void setup() throws CoreException {
		doReturn(pomFile).when(mavenUtils).getPomFile(project);
		doNothing().when(mavenUtils).writeNewPomFile(eq(project), eq(pomFile), any(Model.class));
		when(mavenProjectFacade.getMavenProject(Mockito.any(NullProgressMonitor.class)).getCompileDependencies()).thenReturn(Collections.emptyList());
		// TODO: repair this broken test
		// mavenUtils.setCamelMavenUtils(camelMavenUtils);
	}

	@Test
	public void testUpdateMavenDependencyWithNullVersion() throws Exception {
		doReturn(null).when(camelMavenUtils).getMavenProjectFacade(project);
		final Model mavenModel = new Model();
		final org.apache.maven.model.Dependency mavenDependency = new org.apache.maven.model.Dependency();
		mavenDependency.setArtifactId("test-artifactID");
		mavenDependency.setGroupId("test-groupID");
		mavenModel.addDependency(mavenDependency);
		doReturn(mavenModel).when(mavenUtils).readMavenModel(pomFile);

		List<Dependency> compDeps = new ArrayList<>();
		final Dependency dep = new Dependency();
		dep.setArtifactId("test-artifactID");
		dep.setGroupId("test-groupID");
		dep.setVersion("test-version");
		compDeps.add(dep);
		mavenUtils.internalUpdateMavenDependencies(compDeps, project);
		assertThat(mavenDependency.getVersion()).isEqualTo("test-version");
	}

	@Test
	public void testUpdateMavenDependencyWithManagedDependency() throws Exception {
		// so retrieved from the MavenprojectFacade
		final ArrayList<org.apache.maven.model.Dependency> m2eDependencies = new ArrayList<org.apache.maven.model.Dependency>();
		org.apache.maven.model.Dependency m2eDependency = new org.apache.maven.model.Dependency();
		m2eDependency.setArtifactId("test-artifactID");
		m2eDependency.setGroupId("test-groupID");
		m2eDependency.setVersion("differentVersion-test");
		m2eDependencies.add(m2eDependency);
		when(mavenProjectFacade.getMavenProject(Mockito.any(NullProgressMonitor.class)).getDependencies()).thenReturn(m2eDependencies);
		doReturn(mavenProjectFacade).when(camelMavenUtils).getMavenProjectFacade(project);
		final Model mavenModel = new Model();
		final org.apache.maven.model.Dependency mavenDependency = new org.apache.maven.model.Dependency();
		mavenDependency.setArtifactId("test-artifactID");
		mavenDependency.setGroupId("test-groupID");
		mavenModel.addDependency(mavenDependency);
		doReturn(mavenModel).when(mavenUtils).readMavenModel(pomFile);

		List<Dependency> compDeps = new ArrayList<>();
		final Dependency dep = new Dependency();
		dep.setArtifactId("test-artifactID");
		dep.setGroupId("test-groupID");
		dep.setVersion("test-version");
		compDeps.add(dep);
		mavenUtils.internalUpdateMavenDependencies(compDeps, project);
		assertThat(m2eDependency.getVersion()).isEqualTo("test-version");
	}

	@Test
	public void testUpdateMavenDependencyForMissingDependency() throws Exception {
		// so retrieved from the MavenprojectFacade
		final ArrayList<org.apache.maven.model.Dependency> m2eDependencies = new ArrayList<org.apache.maven.model.Dependency>();
		when(mavenProjectFacade.getMavenProject(Mockito.any(NullProgressMonitor.class)).getDependencies()).thenReturn(m2eDependencies);
		doReturn(mavenProjectFacade).when(camelMavenUtils).getMavenProjectFacade(project);
		final Model mavenModel = new Model();
		final org.apache.maven.model.Dependency mavenDependency = new org.apache.maven.model.Dependency();
		mavenDependency.setArtifactId("test-artifactID");
		mavenDependency.setGroupId("test-groupID");
		mavenModel.addDependency(mavenDependency);
		doReturn(mavenModel).when(mavenUtils).readMavenModel(pomFile);

		List<Dependency> compDeps = new ArrayList<>();
		final Dependency dep = new Dependency();
		dep.setArtifactId("test-artifactID");
		dep.setGroupId("test-groupID");
		dep.setVersion("test-version");
		compDeps.add(dep);
		mavenUtils.internalUpdateMavenDependencies(compDeps, project);
		verify(mavenUtils).addDependency(eq(mavenModel), captor.capture(), eq((String) null));
		assertThat(captor.getValue().get(0).getArtifactId()).isEqualTo("test-artifactID");
	}
}
