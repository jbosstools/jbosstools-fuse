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
package org.fusesource.ide.camel.model.service.core.catalog.eips;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.junit.Before;
import org.junit.Test;

/**
 * @author lhein
 */
public class EipTest {

	private Eip eip;
	
	@Before
	public void onStart() throws IOException {
		File jsonFile = new File("testdata/choice.json");
		this.eip = Eip.getJSONFactoryInstance(new FileInputStream(jsonFile));
	}
	
	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#getName()}.
	 */
	@Test
	public void testGetName() {
		assertTrue("Name value is missing", eip.getName() != null && eip.getName().trim().length()>0);
		assertTrue("Name value is not matching", eip.getName().equals("choice"));
	}
	
	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#setKind(java.lang.String)}.
	 */
	@Test
	public void testSetName() {
		String oldValue = eip.getName();
		String newValue = oldValue + "Test";
		eip.setName(newValue);
		assertTrue("EIP did not persist the new value for Name", eip.getName().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#getKind()}.
	 */
	@Test
	public void testGetKind() {
		assertTrue("Kind value is missing", eip.getKind() != null && eip.getKind().trim().length()>0);
		assertTrue("Kind value is not matching", eip.getKind().equals("model"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#setKind(java.lang.String)}.
	 */
	@Test
	public void testSetKind() {
		String oldValue = eip.getKind();
		String newValue = oldValue + "Test";
		eip.setKind(newValue);
		assertTrue("EIP did not persist the new value for Kind", eip.getKind().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#getClazz()}.
	 */
	@Test
	public void testGetClazz() {
		assertTrue("JavaType value is missing", eip.getClazz() != null && eip.getClazz().trim().length()>0);
		assertTrue("JavaType value is not matching", eip.getClazz().equals("org.apache.camel.model.ChoiceDefinition"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#setClazz(java.lang.String)}.
	 */
	@Test
	public void testSetClazz() {
		String oldValue = eip.getClazz();
		String newValue = oldValue + "Test";
		eip.setClazz(newValue);
		assertTrue("EIP did not persist the new value for JavaType", eip.getClazz().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#getInput()}.
	 */
	@Test
	public void testGetInput() {
		assertTrue("Input value is missing", eip.getInput() != null && eip.getInput().trim().length()>0);
		assertTrue("Input value is not matching", eip.getInput().equalsIgnoreCase("true"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#setInput(String)}.
	 */
	@Test
	public void testSetInput() {
		String oldValue = eip.getInput();
		String newValue = oldValue + "Test";
		eip.setInput(newValue);
		assertTrue("EIP did not persist the new value for Input", eip.getInput().equals(newValue));
	}
	
	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#getOutput()}.
	 */
	@Test
	public void testGetOutput() {
		assertTrue("Output value is missing", eip.getOutput() != null && eip.getOutput().trim().length()>0);
		assertTrue("Output value is not matching", eip.getOutput().equalsIgnoreCase("false"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#setOutInput(String)}.
	 */
	@Test
	public void testSetOutput() {
		String oldValue = eip.getOutput();
		String newValue = oldValue + "Test";
		eip.setOutput(newValue);
		assertTrue("EIP did not persist the new value for Output", eip.getOutput().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#getDescription()}.
	 */
	@Test
	public void testGetDescription() {
		assertTrue("Description value is missing", eip.getDescription() != null && eip.getDescription().trim().length()>0);
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#setDescription(java.lang.String)}.
	 */
	@Test
	public void testSetDescription() {
		String oldValue = eip.getDescription();
		String newValue = oldValue + "Test";
		eip.setDescription(newValue);
		assertTrue("EIP did not persist the new value for Description", eip.getDescription().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#getTitle()}.
	 */
	@Test
	public void testGetTitle() {
		assertTrue("Title value is missing", eip.getTitle() != null && eip.getTitle().trim().length()>0);
		assertTrue("Title value is not matching", eip.getTitle().equals("Choice"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#setTitle(java.lang.String)}.
	 */
	@Test
	public void testSetTitle() {
		String oldValue = eip.getTitle();
		String newValue = oldValue + "Test";
		eip.setTitle(newValue);
		assertTrue("EIP did not persist the new value for Title", eip.getTitle().equals(newValue));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#getTags()}.
	 */
	@Test
	public void testGetTags() {
		assertTrue("Label value is missing", eip.getTags() != null && !eip.getTags().isEmpty());
		assertTrue("Label value is not matching", eip.getTags().contains("eip") && eip.getTags().contains("routing"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#setTags(java.util.List)}.
	 */
	@Test
	public void testSetTags() {
		List<String> tags = eip.getTags();
		tags.add("test");
		eip.setTags(tags);
		assertTrue("EIP did not persist the new values for Label", eip.getTags().contains("eip") &&
																		 eip.getTags().contains("routing") &&
																		 eip.getTags().contains("test"));
	}
}
