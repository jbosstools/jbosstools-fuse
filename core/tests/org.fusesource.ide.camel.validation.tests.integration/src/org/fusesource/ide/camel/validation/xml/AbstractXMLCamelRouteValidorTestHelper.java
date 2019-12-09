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
package org.fusesource.ide.camel.validation.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.validation.ValidationEvent;
import org.eclipse.wst.validation.ValidationState;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class AbstractXMLCamelRouteValidorTestHelper {

	protected static FuseProject fuseProject;
	private CamelIOHandler marshaller = new CamelIOHandler();
	private XMLCamelRoutesValidator xmlCamelRoutesValidator = new XMLCamelRoutesValidator();
	private IFile camelFileUnderTest;

	public AbstractXMLCamelRouteValidorTestHelper() {
		super();
	}
	
	@BeforeClass
	public static void beforeClass() throws Throwable {
		fuseProject = new FuseProject(AbstractXMLCamelRouteValidorTestHelper.class.getSimpleName(), CamelCatalogUtils.CAMEL_VERSION_LATEST_COMMUNITY);
		fuseProject.before();
	}
	
	@AfterClass
	public static void afterClass() {
		fuseProject.after();
	}
	
	@After
	public void tearDown() throws CoreException {
		if (camelFileUnderTest != null && camelFileUnderTest.exists()) {
			camelFileUnderTest.delete(true, new NullProgressMonitor());
		}
	}

	protected CamelFile loadRoute(String name) throws IOException, CoreException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("/" + name);
	
		File baseFile = File.createTempFile("baseFile" + name, "xml");
		Files.copy(inputStream, baseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	
		inputStream = getClass().getClassLoader().getResourceAsStream("/" + name);
		camelFileUnderTest = fuseProject.getProject().getFile(name);
		camelFileUnderTest.create(inputStream, true, new NullProgressMonitor());
	
		return marshaller.loadCamelModel(camelFileUnderTest, new NullProgressMonitor());
	}
	
	protected void testValidateCreatesAValidationMarker(String name) throws CoreException, IOException {
		testValidate(name, 1);
	}

	protected void testValidate(String name, int numbersOFMarkersExpected) throws CoreException , IOException{
		CamelFile camelFile = loadRoute(name);
		ValidationEvent event = new ValidationEvent(camelFile.getResource(), IResourceDelta.CHANGED, null);
		ValidationState state = new ValidationState();
		
		xmlCamelRoutesValidator.validate(event, state, new NullProgressMonitor());
		// Check marker created
		assertThat(camelFile.getResource().findMarkers(null, true, IResource.DEPTH_ONE)).hasSize(numbersOFMarkersExpected);
	}

}