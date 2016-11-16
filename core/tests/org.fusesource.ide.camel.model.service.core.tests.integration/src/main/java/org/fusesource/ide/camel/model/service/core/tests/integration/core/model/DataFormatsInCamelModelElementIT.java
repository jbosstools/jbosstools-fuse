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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.CamelIOHandlerIT;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Aurelien Pupier
 *
 */
public class DataFormatsInCamelModelElementIT {

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Rule
	public FuseProject fuseProject = new FuseProject(DataFormatsInCamelModelElementIT.class.getSimpleName());

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

		final AbstractCamelModelElement unmarshallElement = model1.getCamelContext().getChildElements().get(0).getChildElements().get(0);
		assertThat(unmarshallElement.getNodeTypeId()).isEqualTo("unmarshal");
		unmarshallElement.setParameter("ref", "");
		assertThat(((AbstractCamelModelElement) unmarshallElement.getParameter("dataFormatType")).getNodeTypeId()).isEqualTo("json");
	}

}
