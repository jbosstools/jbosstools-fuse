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

package org.fusesource.ide.camel.model.service.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author lhein
 */
public class CamelFileTemplateCreatorTest {
	
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();
	
	private CamelFileTemplateCreator templateFactory;
	
	@Before
	public void setup() throws CoreException {
		this.templateFactory = new CamelFileTemplateCreator();
	}
	
	@After
	public void tearDown() throws CoreException {
		this.templateFactory = null;
	}
	
	@Test
	public void testCreateSpringTemplateFile() throws IOException {
		File f = testFolder.newFile("springTemplate.xml");
		this.templateFactory.createSpringTemplateFile(f);
		assertThat(this.templateFactory.getSpringStubText()).isXmlEqualToContentOf(f);
	}
	
	@Test
	public void testCreateBlueprintTemplateFile() throws IOException {
		File f = testFolder.newFile("blueprintTemplate.xml");
		this.templateFactory.createBlueprintTemplateFile(f);
		assertThat(this.templateFactory.getBlueprintStubText()).isXmlEqualToContentOf(f);
	}
	
	@Test
	public void testCreateRoutesTemplateFile() throws IOException {
		File f = testFolder.newFile("routesTemplate.xml");
		this.templateFactory.createRoutesTemplateFile(f);
		assertThat(this.templateFactory.getRoutesStubText()).isXmlEqualToContentOf(f);
	}
}
