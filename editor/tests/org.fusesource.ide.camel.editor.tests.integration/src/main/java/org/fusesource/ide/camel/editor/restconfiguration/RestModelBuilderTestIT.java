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
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.RestConfigurationElement;
import org.fusesource.ide.camel.model.service.core.model.RestElement;
import org.fusesource.ide.camel.model.service.core.model.RestVerbElement;
import org.junit.Test;

/**
 * @author brianf
 *
 */
public class RestModelBuilderTestIT {

	@Test
	public void testBuild() throws IOException {
		CamelFile testFile = loadCamelFile(new NullProgressMonitor());
		CamelContextElement ctx = (CamelContextElement)testFile.getRouteContainer();
		assertThat(ctx.getRestConfigurations()).isNotEmpty();
		assertThat(ctx.getRestElements()).isNotEmpty();

		RestConfigurationElement rcElement = 
				(RestConfigurationElement) ctx.getRestConfigurations().values().iterator().next();
		assertThat(rcElement).isNotNull();
		String componentAttrValue = rcElement.getParameter("component").toString();
		assertThat(componentAttrValue).contains("netty-http");
		
		RestElement rElement = 
				(RestElement) ctx.getRestElements().values().iterator().next();
		assertThat(rElement).isNotNull();
		String pathAttrValue = rElement.getParameter("path").toString();
		assertThat(pathAttrValue).contains("/say");
		assertThat(rElement.getChildElements()).isNotEmpty();

		RestVerbElement rvElement = 
				(RestVerbElement) rElement.getChildElements().iterator().next();
		assertThat(rvElement).isNotNull();
		String uriAttrValue = rvElement.getParameter("uri").toString();
		assertThat(uriAttrValue).contains("hello/{name}");
	}

	/*
	 * @param monitor
	 * @param resource
	 * @return
	 */
	private CamelFile loadCamelFile(IProgressMonitor monitor) throws IOException {
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
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(xmlFile))) {
			bw.write(text);
		}
		return new CamelIOHandler().loadCamelModel(xmlFile, monitor);
	}
}
