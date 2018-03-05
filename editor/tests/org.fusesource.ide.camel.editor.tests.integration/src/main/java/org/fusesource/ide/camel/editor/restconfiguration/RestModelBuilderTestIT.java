/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.restconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * @author brianf
 *
 */
public class RestModelBuilderTestIT {

	@Test
	public void testBuild() throws Exception {
		CamelFile testFile = loadCamelFile(new NullProgressMonitor());
		RestModelBuilder builder = new RestModelBuilder();
		Map<String, List<Object>> restMap = builder.build(testFile);
		assertThat(restMap.get(RestConfigConstants.REST_CONFIGURATION_TAG)).isNotNull();
		assertThat(restMap.get(RestConfigConstants.REST_CONFIGURATION_TAG)).isNotEmpty();
		
		Element restConfigObj = (Element) restMap.get(RestConfigConstants.REST_CONFIGURATION_TAG).get(0);
		assertThat(restConfigObj).isNotNull();
		String componentAttrValue = restConfigObj.getAttribute("component");
		assertThat(componentAttrValue).contains("netty-http");
		
		assertThat(restMap.get(RestConfigConstants.REST_TAG)).isNotNull();
		assertThat(restMap.get(RestConfigConstants.REST_TAG)).isNotEmpty();

		Element restObj = (Element) restMap.get(RestConfigConstants.REST_TAG).get(0);
		assertThat(restObj).isNotNull();
		String pathValue = restObj.getAttribute("path");
		assertThat(pathValue).contains("/say");
	}

	/*
	 * @param monitor
	 * @param resource
	 * @return
	 */
	private CamelFile loadCamelFile(IProgressMonitor monitor) throws Exception {
		String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<blueprint xmlns=\"http://www.osgi.org/xmlns/blueprint/v1.0.0\"\n" + 
				"    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.osgi.org/xmlns/blueprint/v1.0.0 https://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd                            http://camel.apache.org/schema/blueprint http://camel.apache.org/schema/blueprint/camel-blueprint.xsd\">\n" + 
				"    <camelContext id=\"_context1\" xmlns=\"http://camel.apache.org/schema/blueprint\">\n" + 
				"        <restConfiguration component=\"netty-http\" host=\"localhost\" port=\"10000\"/>\n" + 
				"        <rest path=\"/say\">\n" + 
				"            <get uri=\"hello/{name}\">\n" + 
				"                <to uri=\"direct:service-hello\"/>\n" + 
				"            </get>\n" + 
				"        </rest>\n" + 
				"        <route id=\"service-hello\">\n" + 
				"            <from id=\"_from1\" uri=\"direct:service-hello\"/>\n" + 
				"            <setBody id=\"_setBody1\">\n" + 
				"                <simple>Hello ${header.name}</simple>\n" + 
				"            </setBody>\n" + 
				"        </route>\n" + 
				"    </camelContext>\n" + 
				"</blueprint>\n";
		File xmlFile = Files.createTempFile("empty",".xml").toFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(xmlFile));
 	    bw.write(text);
 	    bw.close();
		return new CamelIOHandler().loadCamelModel(xmlFile, monitor);
	}
}
