/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.globalconfiguration.beans.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.fusesource.ide.camel.editor.utils.GlobalConfigUtils;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;
import org.fusesource.ide.foundation.core.xml.namespace.BlueprintNamespaceHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@RunWith(MockitoJUnitRunner.class)
public class GlobalBeanContributorIT {
	
	@Mock
	private GlobalConfigUtils globalConfigUtils;

	@Test
	public void testHandleBeanWithClassWithoutSAPInstalled() throws ParserConfigurationException {
		CamelBean camelBean = createBeanWithClass("with.classname");
		GlobalBeanContributor globalBeanContributor = createGlobalBeanWithoutSAPInstalled();
		assertThat(globalBeanContributor.canHandle(camelBean)).isTrue();
	}

	@Test
	public void testHandleBeanWithRefWithoutSAPInstalled() throws ParserConfigurationException {
		CamelBean camelBean = createBeanWithRef();
		GlobalBeanContributor globalBeanContributor = createGlobalBeanWithoutSAPInstalled();
		assertThat(globalBeanContributor.canHandle(camelBean)).isTrue();
	}
	
	@Test
	public void testHandleBeanWithClassWithSAPInstalled() throws ParserConfigurationException {
		CamelBean camelBean = createBeanWithClass("with.classname");
		GlobalBeanContributor globalBeanContributor = createGlobalBeanContributorFakingSAPInstalled();
		assertThat(globalBeanContributor.canHandle(camelBean)).isTrue();
	}

	@Test
	public void testHandleBeanWithRefWithSAPInstalled() throws ParserConfigurationException {
		CamelBean camelBean = createBeanWithRef();
		GlobalBeanContributor globalBeanContributor = createGlobalBeanContributorFakingSAPInstalled();
		assertThat(globalBeanContributor.canHandle(camelBean)).isTrue();
	}
	
	@Test
	public void testNotHandleSAPBeanWithClassWhenSAPInstalled() throws ParserConfigurationException {
		CamelBean camelBean = createBeanWithClass("org.fusesource.camel.component.sap.SapConnectionConfiguration");
		GlobalBeanContributor globalBeanContributor = createGlobalBeanContributorFakingSAPInstalled();
		assertThat(globalBeanContributor.canHandle(camelBean)).isFalse();
	}
		
	@Test
	public void testHandleSAPBeanWithClassWhenSAPNotInstalled() throws ParserConfigurationException {
		CamelBean camelBean = createBeanWithClass("org.fusesource.camel.component.sap.SapConnectionConfiguration");
		GlobalBeanContributor globalBeanContributor = createGlobalBeanWithoutSAPInstalled();
		assertThat(globalBeanContributor.canHandle(camelBean)).isTrue();
	}
	
	protected CamelBean createBeanWithClass(String className) throws ParserConfigurationException {
		CamelBean camelBean = new CamelBean("myBeanWithClass");
		camelBean.setClassName(className);
		Document xmlDoc = createDocument();
		Element beanNode = xmlDoc.createElementNS(BlueprintNamespaceHandler.NAMESPACEURI_OSGI_BLUEPRINT_HTTP, "bean");
		camelBean.setXmlNode(beanNode);
		return camelBean;
	}

	
	protected CamelBean createBeanWithRef() throws ParserConfigurationException {
		CamelBean camelBean = new CamelBean("myBeanWithRef");
		camelBean.setParameter(GlobalBeanEIP.PROP_FACTORY_REF, "aRefId");
		Document xmlDoc = createDocument();
		Element beanNode = xmlDoc.createElementNS(BlueprintNamespaceHandler.NAMESPACEURI_OSGI_BLUEPRINT_HTTP, "bean");
		camelBean.setXmlNode(beanNode);
		return camelBean;
	}

	protected Document createDocument() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		return docBuilder.newDocument();
	}
	
	protected GlobalBeanContributor createGlobalBeanWithoutSAPInstalled() {
		return createGlobeanContributorWithSAP(false);
	}
	
	protected GlobalBeanContributor createGlobalBeanContributorFakingSAPInstalled() {
		return createGlobeanContributorWithSAP(true);
	}

	protected GlobalBeanContributor createGlobeanContributorWithSAP(boolean installed) {
		GlobalBeanContributor globalBeanContributor = new GlobalBeanContributor();
		doReturn(installed).when(globalConfigUtils).isSAPExtInstalled();
		globalBeanContributor.setGlobalConfigUtils(globalConfigUtils);
		return globalBeanContributor;
	}

}
