/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.catalog.components;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.junit.Before;
import org.junit.Test;

/**
 * @author lhein
 */
public class ComponentTest {

	private Component component;
	
	@Before
	public void onStart() throws IOException {
		File jsonFile = new File("testdata/file.json");
		this.component = Component.getJSONFactoryInstance(new FileInputStream(jsonFile));
	}
	
	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#getId()}.
	 */
	@Test
	public void testGetId() {
		assertTrue("ID value is missing", component.getId() != null && component.getId().trim().length()>0);
		assertTrue("ID value is not matching", component.getId().equals("file"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#getName()}.
	 */
	@Test
	public void testGetName() {
		assertTrue("Name value is missing", component.getName() != null && component.getName().trim().length()>0);
		assertTrue("Name value is not matching", component.getName().equals("file"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#getConsumerOnly()}.
	 */
	@Test
	public void testGetConsumerOnly() {
		assertTrue("ConsumerOnly value is missing", component.getConsumerOnly() != null && component.getConsumerOnly().trim().length()>0);
		assertTrue("ConsumerOnly value is not matching", component.getConsumerOnly().equalsIgnoreCase("false"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#setConsumerOnly(java.lang.String)}.
	 */
	@Test
	public void testSetConsumerOnly() {
		String oldValue = component.getConsumerOnly();
		String newValue = oldValue + "Test";
		component.setConsumerOnly(newValue);
		assertTrue("Component did not persist the new value for ConsumerOnly", component.getConsumerOnly().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#getProducerOnly()}.
	 */
	@Test
	public void testGetProducerOnly() {
		assertTrue("ProducerOnly value is missing", component.getProducerOnly() != null && component.getProducerOnly().trim().length()>0);
		assertTrue("ProducerOnly value is not matching", component.getProducerOnly().equalsIgnoreCase("false"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#setProducerOnly(java.lang.String)}.
	 */
	@Test
	public void testSetProducerOnly() {
		String oldValue = component.getProducerOnly();
		String newValue = oldValue + "Test";
		component.setProducerOnly(newValue);
		assertTrue("Component did not persist the new value for ProducerOnly", component.getProducerOnly().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#getKind()}.
	 */
	@Test
	public void testGetKind() {
		assertTrue("Kind value is missing", component.getKind() != null && component.getKind().trim().length()>0);
		assertTrue("Kind value is not matching", component.getKind().equals("component"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#setKind(java.lang.String)}.
	 */
	@Test
	public void testSetKind() {
		String oldValue = component.getKind();
		String newValue = oldValue + "Test";
		component.setKind(newValue);
		assertTrue("Component did not persist the new value for Kind", component.getKind().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#getScheme()}.
	 */
	@Test
	public void testGetScheme() {
		assertTrue("Scheme value is missing", component.getScheme() != null && component.getScheme().trim().length()>0);
		assertTrue("Scheme value is not matching", component.getScheme().equals("file"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#setScheme(java.lang.String)}.
	 */
	@Test
	public void testSetScheme() {
		String oldValue = component.getScheme();
		String newValue = oldValue + "Test";
		component.setScheme(newValue);
		assertTrue("Component did not persist the new value for Scheme", component.getScheme().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#getClazz()}.
	 */
	@Test
	public void testGetClazz() {
		assertTrue("JavaType value is missing", component.getClazz() != null && component.getClazz().trim().length()>0);
		assertTrue("JavaType value is not matching", component.getClazz().equals("org.apache.camel.component.file.FileComponent"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#setClazz(java.lang.String)}.
	 */
	@Test
	public void testSetClazz() {
		String oldValue = component.getClazz();
		String newValue = oldValue + "Test";
		component.setClazz(newValue);
		assertTrue("Component did not persist the new value for JavaType", component.getClazz().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#getSyntax()}.
	 */
	@Test
	public void testGetSyntax() {
		assertTrue("Syntax value is missing", component.getSyntax() != null && component.getSyntax().trim().length()>0);
		assertTrue("Syntax value is not matching", component.getSyntax().equals("file:directoryName"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#setSyntax(java.lang.String)}.
	 */
	@Test
	public void testSetSyntax() {
		String oldValue = component.getSyntax();
		String newValue = oldValue + "Test";
		component.setSyntax(newValue);
		assertTrue("Component did not persist the new value for Syntax", component.getSyntax().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#getDescription()}.
	 */
	@Test
	public void testGetDescription() {
		assertTrue("Description value is missing", component.getDescription() != null && component.getDescription().trim().length()>0);
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#setDescription(java.lang.String)}.
	 */
	@Test
	public void testSetDescription() {
		String oldValue = component.getDescription();
		String newValue = oldValue + "Test";
		component.setDescription(newValue);
		assertTrue("Component did not persist the new value for Description", component.getDescription().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#getTitle()}.
	 */
	@Test
	public void testGetTitle() {
		assertTrue("Title value is missing", component.getTitle() != null && component.getTitle().trim().length()>0);
		assertTrue("Title value is not matching", component.getTitle().equals("File"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#setTitle(java.lang.String)}.
	 */
	@Test
	public void testSetTitle() {
		String oldValue = component.getTitle();
		String newValue = oldValue + "Test";
		component.setTitle(newValue);
		assertTrue("Component did not persist the new value for Title", component.getTitle().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#getTags()}.
	 */
	@Test
	public void testGetTags() {
		assertThat(component.getTags()).contains("core", "file");
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#setTags(java.util.List)}.
	 */
	@Test
	public void testSetTags() {
		List<String> tags = component.getTags();
		tags.add("test");
		component.setTags(tags);
		assertThat(component.getTags())
			.as("Component did not persist the new values for Label")
			.contains("core", "file", "test");
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#getDependencies()}.
	 */
	@Test
	public void testGetDependencies() {
		assertThat(component.getDependencies()).isNotEmpty();
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.components.Component#setDependencies(java.util.ArrayList)}.
	 */
	@Test
	public void testSetDependencies() {
		List<Dependency> deps = component.getDependencies();
		Dependency newOne = new Dependency();
		newOne.setGroupId("test1");
		newOne.setArtifactId("test2");
		newOne.setVersion("test3");
		deps.add(newOne);
		component.setDependencies(deps);
		assertThat(component.getDependencies())
			.as("Component did not persist the new value for Dependency")
			.contains(newOne);
	}
}
