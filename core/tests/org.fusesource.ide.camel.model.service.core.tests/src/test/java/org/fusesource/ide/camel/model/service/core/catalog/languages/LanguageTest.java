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
package org.fusesource.ide.camel.model.service.core.catalog.languages;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog.languages.Language;
import org.junit.Before;
import org.junit.Test;

/**
 * @author lhein
 */
public class LanguageTest {

	private Language language;
	
	@Before
	public void onStart() throws IOException {
		File jsonFile = new File("testdata/constant.json");
		this.language = Language.getJSONFactoryInstance(new FileInputStream(jsonFile));
	}
	
	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.languages.Language#getName()}.
	 */
	@Test
	public void testGetName() {
		assertTrue("Name value is missing", language.getName() != null && language.getName().trim().length()>0);
		assertTrue("Name value is not matching", language.getName().equals("constant"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.languages.Language#getKind()}.
	 */
	@Test
	public void testGetKind() {
		assertTrue("Kind value is missing", language.getKind() != null && language.getKind().trim().length()>0);
		assertTrue("Kind value is not matching", language.getKind().equals("language"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.languages.Language#setKind(java.lang.String)}.
	 */
	@Test
	public void testSetKind() {
		String oldValue = language.getKind();
		String newValue = oldValue + "Test";
		language.setKind(newValue);
		assertTrue("Language did not persist the new value for Kind", language.getKind().equals(newValue));
	}
	
	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.languages.Language#getModelJavaType()}.
	 */
	@Test
	public void testGetModelJavaType() {
		assertTrue("ModelJavaType value is missing", language.getModelJavaType() != null && language.getModelJavaType().trim().length()>0);
		assertTrue("ModelJavaType value is not matching", language.getModelJavaType().equals("org.apache.camel.model.language.ConstantExpression"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.languages.Language#setModelJavaType(String)}.
	 */
	@Test
	public void testSetModelJavaType() {
		String oldValue = language.getModelJavaType();
		String newValue = oldValue + "Test";
		language.setModelJavaType(newValue);
		assertTrue("Language did not persist the new value for ModelJavaType", language.getModelJavaType().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.languages.Language#getModelName()}.
	 */
	@Test
	public void testGetModelName() {
		assertTrue("ModelName value is missing", language.getModelName() != null && language.getModelName().trim().length()>0);
		assertTrue("ModelName value is not matching", language.getModelName().equals("constant"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.languages.Language#setModelName(String)}.
	 */
	@Test
	public void testSetModelName() {
		String oldValue = language.getModelName();
		String newValue = oldValue + "Test";
		language.setModelName(newValue);
		assertTrue("Language did not persist the new value for ModelName", language.getModelName().equals(newValue));
	}
	
	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.languages.Language#getClazz()}.
	 */
	@Test
	public void testGetClazz() {
		assertTrue("JavaType value is missing", language.getClazz() != null && language.getClazz().trim().length()>0);
		assertTrue("JavaType value is not matching", language.getClazz().equals("org.apache.camel.language.constant.ConstantLanguage"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.languages.Language#setClazz(java.lang.String)}.
	 */
	@Test
	public void testSetClazz() {
		String oldValue = language.getClazz();
		String newValue = oldValue + "Test";
		language.setClazz(newValue);
		assertTrue("Language did not persist the new value for JavaType", language.getClazz().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.languages.Language#getDescription()}.
	 */
	@Test
	public void testGetDescription() {
		assertTrue("Description value is missing", language.getDescription() != null && language.getDescription().trim().length()>0);
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.languages.Language#setDescription(java.lang.String)}.
	 */
	@Test
	public void testSetDescription() {
		String oldValue = language.getDescription();
		String newValue = oldValue + "Test";
		language.setDescription(newValue);
		assertTrue("Language did not persist the new value for Description", language.getDescription().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.languages.Language#getTitle()}.
	 */
	@Test
	public void testGetTitle() {
		assertTrue("Title value is missing", language.getTitle() != null && language.getTitle().trim().length()>0);
		assertTrue("Title value is not matching", language.getTitle().equals("Constant"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.languages.Language#setTitle(java.lang.String)}.
	 */
	@Test
	public void testSetTitle() {
		String oldValue = language.getTitle();
		String newValue = oldValue + "Test";
		language.setTitle(newValue);
		assertTrue("Language did not persist the new value for Title", language.getTitle().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.languages.Language#getTags()}.
	 */
	@Test
	public void testGetTags() {
		assertTrue("Label value is missing", language.getTags() != null && !language.getTags().isEmpty());
		assertTrue("Label value is not matching", language.getTags().contains("language") && language.getTags().contains("core"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.languages.Language#setTags(java.util.List)}.
	 */
	@Test
	public void testSetTags() {
		List<String> tags = language.getTags();
		tags.add("test");
		language.setTags(tags);
		assertTrue("Language did not persist the new values for Label", language.getTags().contains("language") &&
																		 language.getTags().contains("core") &&
																		 language.getTags().contains("test"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.languages.Language#getDependencies()}.
	 */
	@Test
	public void testGetDependencies() {
		assertTrue("Dependency values are missing", language.getDependencies() != null && !language.getDependencies().isEmpty());
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.languages.Language#setDependencies(java.util.ArrayList)}.
	 */
	@Test
	public void testSetDependencies() {
		List<Dependency> deps = language.getDependencies();
		Dependency newOne = new Dependency();
		newOne.setGroupId("test1");
		newOne.setArtifactId("test2");
		newOne.setVersion("test3");
		deps.add(newOne);
		language.setDependencies(deps);
		assertTrue("Language did not persist the new value for Dependency", language.getDependencies().contains(newOne));
	}
}
