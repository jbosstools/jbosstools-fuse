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

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

@RunWith(MockitoJUnitRunner.class)
public class GlobalBeanContributorIT {
	
	@Mock
	private GlobalConfigUtils globalConfigUtils = new GlobalConfigUtils();

	@Test
	public void testHandleBeanWithClass() throws Exception {
		CamelBean camelBean = createBeanWithClass("with.classname");
		assertThat(new GlobalBeanContributor().canHandle(camelBean)).isTrue();
	}

	@Test
	public void testHandleBeanWithRef() throws Exception {
		CamelBean camelBean = createBeanWithRef();
		assertThat(new GlobalBeanContributor().canHandle(camelBean)).isTrue();
	}
	
	@Test
	public void testNotHandleSAPBeanWithClassWhenSAPInstalled() throws Exception {
		doReturn(true).when(globalConfigUtils).isSAPExtInstalled();
		CamelBean camelBean = createBeanWithClass("org.fusesource.camel.component.sap.SapConnectionConfiguration");
		GlobalBeanContributor globalBeanContributor = new GlobalBeanContributor();
		globalBeanContributor.setGlobalConfigUtils(globalConfigUtils);
		assertThat(globalBeanContributor.canHandle(camelBean)).isFalse();
	}
		
	@Test
	public void testHandleSAPBeanWithClassWhenSAPNotInstalled() throws Exception {
		CamelBean camelBean = createBeanWithClass("org.fusesource.camel.component.sap.SapConnectionConfiguration");
		assertThat(new GlobalBeanContributor().canHandle(camelBean)).isTrue();
	}
	
	protected CamelBean createBeanWithClass(String className) {
		CamelBean camelBean = new CamelBean("myBeanWithClass");
		camelBean.setClassName(className);
		Document xmlDoc = new DocumentImpl();
		Element beanNode = xmlDoc.createElementNS(BlueprintNamespaceHandler.NAMESPACEURI_OSGI_BLUEPRINT_HTTP, "bean");
		camelBean.setXmlNode(beanNode);
		return camelBean;
	}
	
	protected CamelBean createBeanWithRef() {
		CamelBean camelBean = new CamelBean("myBeanWithRef");
		camelBean.setParameter(GlobalBeanEIP.PROP_FACTORY_REF, "aRefId");
		Document xmlDoc = new DocumentImpl();
		Element beanNode = xmlDoc.createElementNS(BlueprintNamespaceHandler.NAMESPACEURI_OSGI_BLUEPRINT_HTTP, "bean");
		camelBean.setXmlNode(beanNode);
		return camelBean;
	}

}
