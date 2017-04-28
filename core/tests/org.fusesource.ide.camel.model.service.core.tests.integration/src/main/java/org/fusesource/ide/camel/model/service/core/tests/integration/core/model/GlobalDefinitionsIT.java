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
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.GlobalDefinitionCamelModelElement;
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
public class GlobalDefinitionsIT {

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Rule
	public FuseProject fuseProject = new FuseProject(GlobalDefinitionsIT.class.getSimpleName());

	@Test
	public void testGlobalDefinitionRead() throws IOException, CoreException {
		String name = "withGlobalDefinitionSample.xml";

		InputStream inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);

		File baseFile = File.createTempFile("baseFile" + name, "xml");
		Files.copy(inputStream, baseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);
		IFile fileInProject = fuseProject.getProject().getFile(name);
		fileInProject.create(inputStream, true, new NullProgressMonitor());

		CamelFile model1 = new CamelIOHandler().loadCamelModel(fileInProject, new NullProgressMonitor());

		GlobalDefinitionCamelModelElement globalDefinition = model1.getGlobalDefinitions().values().iterator().next();
		assertThat(globalDefinition.getId()).isEqualTo("sap-configuration");
	}

	@Test
	public void testCamelBeanProperties() throws IOException, CoreException {
		String name = "withGlobalDefinitionSample.xml";

		InputStream inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);

		File baseFile = File.createTempFile("beanFile" + name, "xml");
		Files.copy(inputStream, baseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);
		IFile fileInProject = fuseProject.getProject().getFile(name);
		fileInProject.create(inputStream, true, new NullProgressMonitor());

		CamelFile model1 = new CamelIOHandler().loadCamelModel(fileInProject, new NullProgressMonitor());

		GlobalDefinitionCamelModelElement globalDefinition = model1.getGlobalDefinitions().values().iterator().next();

		// now make sure that it was parsed correctly into a bean
		assertThat(globalDefinition).isInstanceOf(CamelBean.class);
		
		CamelBean bean = (CamelBean) globalDefinition;
		assertThat(bean.getAttributeValue(CamelBean.PROP_CLASS)).isEqualTo("org.fusesource.camel.component.sap.SapConnectionConfiguration");
		assertThat(bean.getClassName()).isEqualTo("org.fusesource.camel.component.sap.SapConnectionConfiguration");

		assertThat(bean.getDependsOn()).isNull();
		bean.setDependsOn("Something Awesome");
		assertThat(bean.getDependsOn()).isEqualTo("Something Awesome");

		// Check that Model is valid after reloading from the filesystem
		CamelIOHandler camelIOHandler = new CamelIOHandler();
		camelIOHandler.setDocument(model1.getDocument());
		camelIOHandler.saveCamelModel(model1, model1.getResource().getLocation().toFile(), new NullProgressMonitor());
		CamelFile reloadedCamelFile = camelIOHandler.loadCamelModel(model1.getResource(), new NullProgressMonitor());

		GlobalDefinitionCamelModelElement newGlobalDefinition = reloadedCamelFile.getGlobalDefinitions().values().iterator().next();

		// now make sure that it was parsed correctly into a bean
		assertThat(newGlobalDefinition).isInstanceOf(CamelBean.class);
		CamelBean newbean = (CamelBean) newGlobalDefinition;
		assertThat(newbean.getDependsOn()).isEqualTo("Something Awesome");
	}
}
