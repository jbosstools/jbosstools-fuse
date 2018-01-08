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
package org.fusesource.ide.camel.model.service.core.tests.integration.core.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.CamelIOHandlerIT;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Aurelien Pupier
 *
 */
@RunWith(Parameterized.class)
public class DataFormatsInCamelModelElementIT {

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Rule
	public FuseProject fuseProject;
 	
	public DataFormatsInCamelModelElementIT(String camelVersion) {
		this.fuseProject = new FuseProject(DataFormatsInCamelModelElementIT.class.getSimpleName(), camelVersion);
	}
	
	@Parameters(name = "{0}")
	public static Collection<String> params() {
		return CamelCatalogUtils.getCamelVersionsToTestWith();
	}
	
	@Test
	public void testDataFormatsInUnmarshal_notRemovedWhenUpdatingRefParameter() throws IOException, CoreException {
		String name = "unmarshalSample.xml";

		InputStream inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);

		File baseFile = File.createTempFile("baseFile" + name, "xml");
		Files.copy(inputStream, baseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);
		IFile fileInProject = fuseProject.getProject().getFile(name);
		fileInProject.create(inputStream, true, new NullProgressMonitor());

		CamelFile model1 = new CamelIOHandler().loadCamelModel(fileInProject, new NullProgressMonitor());

		final AbstractCamelModelElement unmarshallElement = model1.getRouteContainer().getChildElements().get(0).getChildElements().get(0);
		assertThat(unmarshallElement.getNodeTypeId()).isEqualTo("unmarshal");
		unmarshallElement.setParameter("ref", "");
		assertThat(((AbstractCamelModelElement) unmarshallElement.getParameter("dataFormatType")).getNodeTypeId()).isEqualTo("json");
	}

	@Test
	public void testCanFindTypesFromCamelModel() throws CoreException, IOException {
		String[] typesToFind = {"json"};
		CamelModel model = fuseProject.createEmptyCamelFile().getCamelModel();
		for (String searchFor : typesToFind) {
			assertThat(model.getDataFormatsByModelName(searchFor)).isNotEmpty();
		}
	}

	@Test
	public void testCanFindTypesByTagFromCamelModel() throws CoreException, IOException {
		String[] typesToFind = {"json"};
		CamelModel model = fuseProject.createEmptyCamelFile().getCamelModel();
		for (String searchFor : typesToFind) {
			assertThat(model.getDataFormatsByTag(searchFor)).isNotEmpty();
		}
	}
}
