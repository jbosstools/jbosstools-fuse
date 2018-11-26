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
package org.fusesource.ide.camel.model.service.core.tests.integration.core.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CamelIOHandlerIT {

	private CamelIOHandler marshaller = new CamelIOHandler();

	@Rule
	public FuseProject fuseProject = new FuseProject("External Files");

	private String fileNameToTest;

	public CamelIOHandlerIT(final String fileNameToTest) {
		this.fileNameToTest = fileNameToTest;
	}

	@Parameters(name = "{0}")
	public static Collection<String> params() {
		//@formatter:off
		return Arrays.asList(
				"usingCamelPrefixNamespace-blueprint.xml",
				"usingCamelPrefixNamespace-spring.xml",
				"externalTagNameCollisionWithCamelTags.xml",
				"sample.xml",
				"filterSample.xml",
				"filterSampleBlueprint.xml",
				"cbrSample.xml",
				"onDeliverySample.xml",
				"tryCatchSample.xml",
				"propertyPlaceHolderSample.xml",
				"unmarshalSample.xml",
				"withGlobalDefinitionSample.xml",
				"testWithCXFGlobalEndpoint.xml",
				"emptyOtherwiseSample.xml",
				"JMXBeanAnswer--camelContext--11--11.xml",
				"jaas-blueprint.xml",
				"withQuestionMark.xml",
				"globalElementsSample.xml",
				"routes.xml",
				"withMarshalCsvHeaders.xml",
				"routeContext.xml",
				"withCDATA.xml",
				"withComments.xml");
		//@formatter:on
	}

	@Test
	public void testLoadAndSaveOfSimpleModel() throws IOException, CoreException {
		assertModelRoundTrip(fileNameToTest);
	}

	protected CamelFile assertModelRoundTrip(String name) throws IOException, CoreException {
		InputStream inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);

		File baseFile = File.createTempFile("baseFile" + name, "xml");
		Files.copy(inputStream, baseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);
		IFile fileInProject = fuseProject.getProject().getFile(name);
		fileInProject.create(inputStream, true, new NullProgressMonitor());

		CamelFile model1 = marshaller.loadCamelModel(fileInProject, new NullProgressMonitor());

		File outFile = fuseProject.getProject().getFile("reloaded-"+name).getLocation().toFile();
		marshaller.saveCamelModel(model1, outFile, new NullProgressMonitor());

		CamelFile model2 = marshaller.loadCamelModel(outFile, new NullProgressMonitor());

		String model1String = model1.getDocumentAsXML();
		String model2String = model2.getDocumentAsXML();
		
		if("JMXBeanAnswer--camelContext--11--11.xml".equals(name)){
			//special case, the id are not provided initially on purpose because JMX MBean are putting them in title of the file
		} else {
			assertThat(model1String).isXmlEqualToContentOf(baseFile);
		}
		assertEquals("Should have the same content", model1String, model2String);
		
		assertThat(model1.getRouteContainer()).isNotNull();
		assertThat(model1.getRouteContainer().getUnderlyingMetaModelObject()).isNotNull();

		return model2;
	}

}
