/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.editor.globalconfiguration.endpoint.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;

import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.w3c.dom.Element;

@RunWith(MockitoJUnitRunner.class)
public class GlobalEndpointContributorTest {

	@Mock
	AbstractCamelModelElement cme;

	@Test
	public void testCanHandle_GlobalEndpoint() throws Exception {
		Element nodeToHandle = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new ByteArrayInputStream(
						"<endpoint id=\"aaa\" uri=\"dozer:aaa?sourceModel=com.mycompany.camel.spring.sss&amp;targetModel=com.mycompany.camel.spring.sss&amp;mappingFile=transformation.xml\"/>"
								.getBytes(StandardCharsets.UTF_8)))
				.getDocumentElement();
		doReturn(nodeToHandle).when(cme).getXmlNode();
		assertThat(new GlobalEndpointContributor().canHandle(cme)).isTrue();
	}

	@Test
	public void testCanHandle_GlobalEndpointWithPrefix() throws Exception {
		Element nodeToHandle = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new ByteArrayInputStream(
						"<myPrefix:endpoint id=\"aaa\" uri=\"dozer:aaa?sourceModel=com.mycompany.camel.spring.sss&amp;targetModel=com.mycompany.camel.spring.sss&amp;mappingFile=transformation.xml\"/>"
								.getBytes(StandardCharsets.UTF_8)))
				.getDocumentElement();
		doReturn(nodeToHandle).when(cme).getXmlNode();
		assertThat(new GlobalEndpointContributor().canHandle(cme)).isTrue();
	}

	@Test
	public void testCanHandle_ReturnFalseForNodeNotGlobalEndpoint() throws Exception {
		Element nodeToHandle = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new ByteArrayInputStream("<bindy type=\"KeyValue\"/>".getBytes(StandardCharsets.UTF_8)))
				.getDocumentElement();
		doReturn(nodeToHandle).when(cme).getXmlNode();
		assertThat(new GlobalEndpointContributor().canHandle(cme)).isFalse();
	}

}
