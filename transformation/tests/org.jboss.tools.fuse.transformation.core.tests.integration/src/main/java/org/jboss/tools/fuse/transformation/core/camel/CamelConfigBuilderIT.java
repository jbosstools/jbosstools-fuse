/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.tools.fuse.transformation.core.camel;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.assertj.core.api.Assertions;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteContainerElement;
import org.jboss.tools.fuse.transformation.core.TransformType;
import org.jboss.tools.fuse.transformation.core.camel.CamelConfigBuilder.MarshalType;
import org.jboss.tools.fuse.transformation.core.dozer.DozerMapperConfiguration;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;

public class CamelConfigBuilderIT {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	private static final String NEW_CONFIG = "new-camel-config.xml";
	private static final String MULTI_CONFIG = "multiple-camel-config.xml";
	private static final String XML_JSON = "xml-to-json.xml";
	private static final String JAVA_XML = "java-to-xml.xml";
	private static final String JAVA_JAVA = "java-to-java.xml";
	private static final String JSON_JAVA = "json-to-java.xml";
	private static final String XML_JAVA = "xml-to-java.xml";
	private static final String BLUEPRINT_CONFIG = "blueprint-config.xml";
	private static final String NEW_BLUEPRINT_CONFIG = "new-blueprint-config.xml";

	private CamelContextElement getCamelContext(CamelRouteContainerElement container) {
		if (container instanceof CamelContextElement) {
			return (CamelContextElement) container;
		}
		fail("unable to work with route containers which are not CamelContext elements...");
		return null;
	}

	@Test
	public void createXmlToJson() throws Exception {
		// Document xmlJsonDoc = loadDocument(XML_JSON);
		CamelConfigBuilder config = new CamelConfigBuilder(getFile(NEW_CONFIG));
		getCamelContext(config.getModel().getRouteContainer()).setId("test-defined-id");
		AbstractCamelModelElement sourceFormat = config.createDataFormat(TransformType.XML, "xml.ABCOrder",
				MarshalType.UNMARSHALLER);
		AbstractCamelModelElement targetFormat = config.createDataFormat(TransformType.JSON, "json.XYZOrder",
				MarshalType.MARSHALLER);
		AbstractCamelModelElement endpoint = config.createEndpoint("xml2json",
				DozerMapperConfiguration.DEFAULT_DOZER_CONFIG, "xml.ABCOrder", "json.XYZOrder", sourceFormat,
				targetFormat);
		assertDocumentComparaison(config, XML_JSON);
	}

	@Test
	public void createJavaToXml() throws Exception {
		CamelConfigBuilder config = new CamelConfigBuilder(getFile(NEW_CONFIG));
		getCamelContext(config.getModel().getRouteContainer()).setId("test-defined-id");
		config.addTransformation("java2xml", DozerMapperConfiguration.DEFAULT_DOZER_CONFIG, TransformType.JAVA,
				"source.Input", TransformType.XML, "target.Output");
		assertDocumentComparaison(config, JAVA_XML);
	}

	@Test
	public void createXmlToJava() throws Exception {
		CamelConfigBuilder config = new CamelConfigBuilder(getFile(NEW_CONFIG));
		getCamelContext(config.getModel().getRouteContainer()).setId("test-defined-id");
		config.addTransformation("xml2java", DozerMapperConfiguration.DEFAULT_DOZER_CONFIG, TransformType.XML,
				"source.Input", TransformType.JAVA, "target.Output");
		assertDocumentComparaison(config, XML_JAVA);
	}

	@Test
	public void createJavaToJava() throws Exception {
		CamelConfigBuilder config = new CamelConfigBuilder(getFile(NEW_CONFIG));
		getCamelContext(config.getModel().getRouteContainer()).setId("test-defined-id");
		config.addTransformation("java2java", DozerMapperConfiguration.DEFAULT_DOZER_CONFIG, TransformType.JAVA,
				"source.Input", TransformType.JAVA, "target.Output");
		assertDocumentComparaison(config, JAVA_JAVA);
	}

	@Test
	public void createJsonToJava() throws Exception {
		CamelConfigBuilder config = new CamelConfigBuilder(getFile(NEW_CONFIG));
		getCamelContext(config.getModel().getRouteContainer()).setId("test-defined-id");
		config.addTransformation("json2java", DozerMapperConfiguration.DEFAULT_DOZER_CONFIG, TransformType.JSON,
				"source.Input", TransformType.JAVA, "target.Output");
		assertDocumentComparaison(config, JSON_JAVA);
	}

	@Test
	public void readSpringConfig() {
		CamelConfigBuilder config = new CamelConfigBuilder(getFile(XML_JSON));
		Assert.assertEquals(1, config.getEndpoints().size());
		Assert.assertEquals(2, config.getDataFormats().size());
	}

	@Test
	public void readBlueprintConfig() {
		CamelConfigBuilder config = new CamelConfigBuilder(getFile(BLUEPRINT_CONFIG));
		Assert.assertEquals(1, config.getEndpoints().size());
		Assert.assertEquals(2, config.getDataFormats().size());
	}

	@Test
	@Ignore("test is failing due to 2 missign attributes on camelContext: autoStart and streamCache")
	public void createBlueprintConfig() throws Exception {
		CamelConfigBuilder config = new CamelConfigBuilder(getFile(NEW_BLUEPRINT_CONFIG));
		getCamelContext(config.getModel().getRouteContainer()).setId("test-defined-id");
		config.addTransformation("xml2json", DozerMapperConfiguration.DEFAULT_DOZER_CONFIG, TransformType.XML,
				"abcorder.ABCOrder", TransformType.JSON, "xyzorderschema.XyzOrderSchema");
		assertDocumentComparaison(config, BLUEPRINT_CONFIG);
	}

	@Test
	public void existingConfig() throws Exception {
		// Add another transform endpoint to a config that already has one
		CamelConfigBuilder config = new CamelConfigBuilder(getFile(XML_JSON));
		getCamelContext(config.getModel().getRouteContainer()).setId("test-defined-id");
		config.addTransformation("xml2json2", DozerMapperConfiguration.DEFAULT_DOZER_CONFIG, TransformType.XML,
				"org.foo.ABCOrder", TransformType.JSON, "json.XYZOrder");
		assertDocumentComparaison(config, MULTI_CONFIG);
	}

	private File getFile(String fileName) {
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile(fileName, ".xml", tmpFolder.getRoot());
			Files.copy(this.getClass().getResourceAsStream(fileName), tmpFile.toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tmpFile;
	}

	public String toString(Document document) throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		return writer.getBuffer().toString().replaceAll("\n|\r", "");
	}

	/**
	 * @param config
	 * @param xmlJson
	 * @throws TransformerException
	 */
	private void assertDocumentComparaison(CamelConfigBuilder config, String fileReferenceName)
			throws TransformerException {
		final String configAsString = toString(config.getModel().getDocument());
		Assertions.assertThat(configAsString).isXmlEqualToContentOf(getFile(fileReferenceName));
	}
}
