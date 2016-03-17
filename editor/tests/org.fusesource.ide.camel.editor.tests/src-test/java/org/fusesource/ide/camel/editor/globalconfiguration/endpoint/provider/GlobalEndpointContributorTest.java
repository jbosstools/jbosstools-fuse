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
package org.fusesource.ide.camel.editor.globalconfiguration.endpoint.provider;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;

import org.fusesource.ide.camel.editor.globalconfiguration.endpoint.provider.GlobalEndpointContributor;
import org.junit.Test;
import org.w3c.dom.Element;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalEndpointContributorTest {

	@Test
	public void testCanHandle_GlobalEndpoint() throws Exception {
		Element nodeToHandle = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new ByteArrayInputStream(
						"<endpoint id=\"aaa\" uri=\"dozer:aaa?sourceModel=com.mycompany.camel.spring.sss&amp;targetModel=com.mycompany.camel.spring.sss&amp;mappingFile=transformation.xml\"/>"
								.getBytes(StandardCharsets.UTF_8.name())))
				.getDocumentElement();
		assertThat(new GlobalEndpointContributor().canHandle(nodeToHandle)).isTrue();
	}

	@Test
	public void testCanHandle_GlobalEndpointWithPrefix() throws Exception {
		Element nodeToHandle = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new ByteArrayInputStream(
						"<myPrefix:endpoint id=\"aaa\" uri=\"dozer:aaa?sourceModel=com.mycompany.camel.spring.sss&amp;targetModel=com.mycompany.camel.spring.sss&amp;mappingFile=transformation.xml\"/>"
								.getBytes(StandardCharsets.UTF_8.name())))
				.getDocumentElement();
		assertThat(new GlobalEndpointContributor().canHandle(nodeToHandle)).isTrue();
	}

	@Test
	public void testCanHandle_ReturnFalseForNodeNotGlobalEndpoint() throws Exception {
		Element nodeToHandle = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new ByteArrayInputStream("<bindy type=\"KeyValue\"/>".getBytes(StandardCharsets.UTF_8.name())))
				.getDocumentElement();
		assertThat(new GlobalEndpointContributor().canHandle(nodeToHandle)).isFalse();
	}

}
