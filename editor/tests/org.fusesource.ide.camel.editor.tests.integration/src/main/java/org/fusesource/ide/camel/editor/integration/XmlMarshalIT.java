/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class XmlMarshalIT {
	
	CamelIOHandler marshaller = new CamelIOHandler();
	
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	private String fileNameToTest;

	public XmlMarshalIT(final String fileNameToTest) {
		this.fileNameToTest = fileNameToTest;
	}

	@Parameters
	public static Collection<String> params() {
		return Arrays.asList("sample.xml", "filterSample.xml", "filterSampleBlueprint.xml", "cbrSample.xml", "onDeliverySample.xml");
	}

	private IProject project;
	
	@Before
	public void setup() throws CoreException{
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		project = ws.getRoot().getProject("External Files");
		if (!project.exists()){
		    project.create(null);
		}
		if (!project.isOpen()){
		    project.open(null);
		}
		//Create a fake pom.xml
		IFile pom = project.getFile("pom.xml");
		pom.create(new ByteArrayInputStream("".getBytes()), true, new NullProgressMonitor());
	}
	
	@After
	public void tearDown() throws CoreException{
		if (project.exists()){
			project.delete(true, new NullProgressMonitor());
		}
	}
	
	@Test
	public void testLoadAndSaveOfSimpleModel() throws Exception {
		assertModelRoundTrip(fileNameToTest, 1);
	}
	

	protected CamelFile assertModelRoundTrip(String name, int outputCount) throws IOException, CoreException {
		
		InputStream inputStream = XmlMarshalIT.class.getClassLoader().getResourceAsStream("/"+name);
		
		File baseFile = File.createTempFile("baseFile" + name, "xml");
		Files.copy(inputStream, baseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		inputStream = XmlMarshalIT.class.getClassLoader().getResourceAsStream("/" + name);
		IFile fileInProject = project.getFile(name);
		fileInProject.create(inputStream, true, new NullProgressMonitor());

		CamelFile model1 = marshaller.loadCamelModel(fileInProject, new NullProgressMonitor());
		
		File outFile = new File(testFolder.newFolder(), name);
		marshaller.saveCamelModel(model1, outFile, new NullProgressMonitor());
		
		CamelFile model2 = marshaller.loadCamelModel(outFile, new NullProgressMonitor());

		String model1String = model1.getDocumentAsXML();
		String model2String = model2.getDocumentAsXML();
		
		Assertions.assertThat(model1String).isXmlEqualToContentOf(baseFile);
		assertEquals("Should have the same content", model1String, model2String);
		
		return model2;
	}

	protected <T> void assertContains(Collection<T> collection, T... items) {
		for (T item : items) {
			assertTrue("collection should contain " + item, collection.contains(item));
		}
	}
}
