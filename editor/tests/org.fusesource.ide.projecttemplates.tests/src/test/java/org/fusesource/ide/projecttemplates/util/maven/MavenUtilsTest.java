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
package org.fusesource.ide.projecttemplates.util.maven;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.junit.Test;

public class MavenUtilsTest {

	@Test
	public void testUpdateCamelVersionDependency() throws Exception {
		Model mavenModel = new Model();
		List<Dependency> dependencies = new ArrayList<>();
		Dependency camelCoreDependency = createCamelCoreDependency();
		dependencies.add(camelCoreDependency);
		mavenModel.setDependencies(dependencies);
		
		String camelVersionToUpdate = "2.17.0.redhat-187";
		MavenUtils.updateCamelVersionDependencies(mavenModel, dependencies, camelVersionToUpdate);
		
		assertThat(camelCoreDependency.getVersion()).isEqualTo(camelVersionToUpdate);
	}

	
	@Test
	public void testUpdateCamelVersionDependencyWhenUsingProperty() throws Exception {
		Model mavenModel = new Model();
		Properties properties = new Properties();
		properties.setProperty("camel.version", "oldVersion");
		mavenModel.setProperties(properties);
		List<Dependency> dependencies = new ArrayList<>();
		Dependency camelCoreDependency = createCamelCoreDependency();
		camelCoreDependency.setVersion("${camel.version}");
		dependencies.add(camelCoreDependency);
		mavenModel.setDependencies(dependencies);
		
		String camelVersionToUpdate = "2.17.0.redhat-187";
		MavenUtils.updateCamelVersionDependencies(mavenModel, dependencies, camelVersionToUpdate);
		
		assertThat(properties.getProperty("camel.version")).isEqualTo(camelVersionToUpdate);
	}
	
	@Test
	public void testCamelVersionNotTouchedForDependencyWhenUsingBomAndNoVersionSpecified() throws Exception {
		Model mavenModel = new Model();
		Properties properties = new Properties();
		properties.setProperty("jboss.fuse.bom.version", "oldVersion");
		mavenModel.setProperties(properties);
		DependencyManagement depMan = new DependencyManagement();
		Dependency bomDep = createBOMDep("oldVersion");
		depMan.addDependency(bomDep);
		mavenModel.setDependencyManagement(depMan);
		List<Dependency> dependencies = new ArrayList<>();
		Dependency camelCoreDependency = createCamelCoreDependency();
		dependencies.add(camelCoreDependency);
		mavenModel.setDependencies(dependencies);
		
		String camelVersionToUpdate = "2.17.0.redhat-187";

		MavenUtils.updateCamelVersionDependencies(mavenModel, dependencies, camelVersionToUpdate);
		
		assertThat(camelCoreDependency.getVersion()).isNull();
	}
	
	@Test
	public void testCamelVersionTouchedForDependencyWhenUsingBomButVersionSpecified() throws Exception {
		Model mavenModel = new Model();
		Properties properties = new Properties();
		properties.setProperty("jboss.fuse.bom.version", "oldBomVersion");
		properties.setProperty("camel.version", "oldCamelCoreVersion");
		mavenModel.setProperties(properties);
		List<Dependency> dependencies = new ArrayList<>();
		Dependency camelCoreDependency = createCamelCoreDependency();
		dependencies.add(camelCoreDependency);
		mavenModel.setDependencies(dependencies);
		
		String camelVersionToUpdate = "2.17.0.redhat-187";
		MavenUtils.updateCamelVersionDependencies(mavenModel, dependencies, camelVersionToUpdate);
		
		assertThat(properties.getProperty("camel.version")).isEqualTo(camelVersionToUpdate);
	}
	
	@Test
	public void testCamelVersionTouchedForPropertyOnlyWhenUsingBomButVersionSpecifiedUsingProperty() throws Exception {
		Model mavenModel = new Model();
		Properties properties = new Properties();
		properties.setProperty("jboss.fuse.bom.version", "oldFuseBomVersion");
		properties.setProperty("camel.version", "oldCamelCoreVersion");
		mavenModel.setProperties(properties);
		DependencyManagement depMan = new DependencyManagement();
		Dependency bomDep = createBOMDep("oldVersion");
		depMan.addDependency(bomDep);
		mavenModel.setDependencyManagement(depMan);
		List<Dependency> dependencies = new ArrayList<>();
		Dependency camelCoreDependency = createCamelCoreDependency();
		camelCoreDependency.setVersion("${camel.version}");
		dependencies.add(camelCoreDependency);
		mavenModel.setDependencies(dependencies);
		
		String camelVersionToUpdate = "2.17.0.redhat-187";
		MavenUtils.updateCamelVersionDependencies(mavenModel, dependencies, camelVersionToUpdate);
		
		assertThat(properties.getProperty("camel.version")).isEqualTo(camelVersionToUpdate);
		assertThat(camelCoreDependency.getVersion()).isEqualTo(null);
	}
	
	@Test
	public void testCamelCommunityVersionTouchedForPropertyOnlyWhenUsingBomButVersionSpecifiedUsingProperty() throws Exception {
		Model mavenModel = new Model();
		Properties properties = new Properties();
		properties.setProperty("jboss.fuse.bom.version", "oldFuseBomVersion");
		properties.setProperty("camel.version", "oldCamelCoreVersion");
		mavenModel.setProperties(properties);
		List<Dependency> dependencies = new ArrayList<>();
		Dependency camelCoreDependency = createCamelCoreDependency();
		camelCoreDependency.setVersion("${camel.version}");
		dependencies.add(camelCoreDependency);
		mavenModel.setDependencies(dependencies);
		
		String camelVersionToUpdate = "2.17.3";
		MavenUtils.updateCamelVersionDependencies(mavenModel, dependencies, camelVersionToUpdate);
		
		assertThat(properties.getProperty("camel.version")).isEqualTo(camelVersionToUpdate);
		assertThat(camelCoreDependency.getVersion()).isEqualTo("${camel.version}");
	}
	
	@Test
	public void testCamelVersionTouchedForCommunityVersion() throws Exception {
		Model mavenModel = new Model();
		Properties properties = new Properties();
		properties.setProperty("jboss.fuse.bom.version", "oldFuseBomVersion");
		properties.setProperty("camel.version", "oldCamelCoreVersion");
		mavenModel.setProperties(properties);
		List<Dependency> dependencies = new ArrayList<>();
		Dependency camelCoreDependency = createCamelCoreDependency();
		dependencies.add(camelCoreDependency);
		mavenModel.setDependencies(dependencies);
		
		String camelVersionToUpdate = "2.17.3";
		MavenUtils.updateCamelVersionDependencies(mavenModel, dependencies, camelVersionToUpdate);
		
		assertThat(properties.getProperty("camel.version")).isEqualTo(camelVersionToUpdate);
		assertThat(camelCoreDependency.getVersion()).isEqualTo(camelVersionToUpdate);
	}
	
	
	private Dependency createCamelCoreDependency() {
		Dependency camelCoreDependency = new Dependency();
		camelCoreDependency.setGroupId("org.apache.camel");
		camelCoreDependency.setArtifactId("camel-core");
		return camelCoreDependency;
	}
	
	private Dependency createBOMDep(String version) {
		Dependency bomDep = new Dependency();
		bomDep.setGroupId("org.jboss.fuse.bom");
		bomDep.setArtifactId("jboss-fuse-parent");
		bomDep.setVersion(version);
		bomDep.setType("pom");
		bomDep.setScope("import");
		return bomDep;
	}
}
