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
package org.fusesource.ide.camel.model.service.core.catalog.dataformats;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.junit.Before;
import org.junit.Test;

/**
 * @author lhein
 */
public class DataFormatTest {

	private DataFormat dataFormat;
	
	@Before
	public void onStart() throws IOException {
		File jsonFile = new File("testdata/base64.json");
		this.dataFormat = DataFormat.getJSONFactoryInstance(new FileInputStream(jsonFile));
	}
	
	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat#getName()}.
	 */
	@Test
	public void testGetName() {
		assertTrue("Name value is missing", dataFormat.getName() != null && dataFormat.getName().trim().length()>0);
		assertTrue("Name value is not matching", dataFormat.getName().equals("base64"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat#getKind()}.
	 */
	@Test
	public void testGetKind() {
		assertTrue("Kind value is missing", dataFormat.getKind() != null && dataFormat.getKind().trim().length()>0);
		assertTrue("Kind value is not matching", dataFormat.getKind().equals("dataformat"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat#setKind(java.lang.String)}.
	 */
	@Test
	public void testSetKind() {
		String oldValue = dataFormat.getKind();
		String newValue = oldValue + "Test";
		dataFormat.setKind(newValue);
		assertTrue("DataFormat did not persist the new value for Kind", dataFormat.getKind().equals(newValue));
	}
	
	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat#getModelJavaType()}.
	 */
	@Test
	public void testGetModelJavaType() {
		assertTrue("ModelJavaType value is missing", dataFormat.getModelJavaType() != null && dataFormat.getModelJavaType().trim().length()>0);
		assertTrue("ModelJavaType value is not matching", dataFormat.getModelJavaType().equals("org.apache.camel.model.dataformat.Base64DataFormat"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat#setModelJavaType(String)}.
	 */
	@Test
	public void testSetModelJavaType() {
		String oldValue = dataFormat.getModelJavaType();
		String newValue = oldValue + "Test";
		dataFormat.setModelJavaType(newValue);
		assertTrue("DataFormat did not persist the new value for ModelJavaType", dataFormat.getModelJavaType().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat#getModelName()}.
	 */
	@Test
	public void testGetModelName() {
		assertTrue("ModelName value is missing", dataFormat.getModelName() != null && dataFormat.getModelName().trim().length()>0);
		assertTrue("ModelName value is not matching", dataFormat.getModelName().equals("base64"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat#setModelName(String)}.
	 */
	@Test
	public void testSetModelName() {
		String oldValue = dataFormat.getModelName();
		String newValue = oldValue + "Test";
		dataFormat.setModelName(newValue);
		assertTrue("DataFormat did not persist the new value for ModelName", dataFormat.getModelName().equals(newValue));
	}
	
	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat#getClazz()}.
	 */
	@Test
	public void testGetClazz() {
		assertTrue("JavaType value is missing", dataFormat.getClazz() != null && dataFormat.getClazz().trim().length()>0);
		assertTrue("JavaType value is not matching", dataFormat.getClazz().equals("org.apache.camel.dataformat.base64.Base64DataFormat"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat#setClazz(java.lang.String)}.
	 */
	@Test
	public void testSetClazz() {
		String oldValue = dataFormat.getClazz();
		String newValue = oldValue + "Test";
		dataFormat.setClazz(newValue);
		assertTrue("DataFormat did not persist the new value for JavaType", dataFormat.getClazz().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat#getDescription()}.
	 */
	@Test
	public void testGetDescription() {
		assertTrue("Description value is missing", dataFormat.getDescription() != null && dataFormat.getDescription().trim().length()>0);
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat#setDescription(java.lang.String)}.
	 */
	@Test
	public void testSetDescription() {
		String oldValue = dataFormat.getDescription();
		String newValue = oldValue + "Test";
		dataFormat.setDescription(newValue);
		assertTrue("DataFormat did not persist the new value for Description", dataFormat.getDescription().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat#getTitle()}.
	 */
	@Test
	public void testGetTitle() {
		assertTrue("Title value is missing", dataFormat.getTitle() != null && dataFormat.getTitle().trim().length()>0);
		assertTrue("Title value is not matching", dataFormat.getTitle().equals("Base64"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat#setTitle(java.lang.String)}.
	 */
	@Test
	public void testSetTitle() {
		String oldValue = dataFormat.getTitle();
		String newValue = oldValue + "Test";
		dataFormat.setTitle(newValue);
		assertTrue("DataFormat did not persist the new value for Title", dataFormat.getTitle().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat#getTags()}.
	 */
	@Test
	public void testGetTags() {
		assertTrue("Label value is missing", dataFormat.getTags() != null && !dataFormat.getTags().isEmpty());
		assertTrue("Label value is not matching", dataFormat.getTags().contains("dataformat") && dataFormat.getTags().contains("transformation"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat#setTags(java.util.List)}.
	 */
	@Test
	public void testSetTags() {
		List<String> tags = dataFormat.getTags();
		tags.add("test");
		dataFormat.setTags(tags);
		assertTrue("DataFormat did not persist the new values for Label", dataFormat.getTags().contains("dataformat") &&
																		 dataFormat.getTags().contains("transformation") &&
																		 dataFormat.getTags().contains("test"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat#getDependencies()}.
	 */
	@Test
	public void testGetDependencies() {
		assertTrue("Dependency values are missing", dataFormat.getDependencies() != null && !dataFormat.getDependencies().isEmpty());
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat#setDependencies(java.util.ArrayList)}.
	 */
	@Test
	public void testSetDependencies() {
		List<Dependency> deps = dataFormat.getDependencies();
		Dependency newOne = new Dependency();
		newOne.setGroupId("test1");
		newOne.setArtifactId("test2");
		newOne.setVersion("test3");
		deps.add(newOne);
		dataFormat.setDependencies(deps);
		assertTrue("DataFormat did not persist the new value for Dependency", dataFormat.getDependencies().contains(newOne));
	}
}
