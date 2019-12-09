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
package org.fusesource.ide.camel.model.service.core.tests.integration.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.GlobalDefinitionCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.CamelIOHandlerIT;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.fusesource.ide.foundation.core.util.Strings;
import org.junit.Rule;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Aurelien Pupier
 *
 */
public class GlobalDefinitionsIT {

	private static final Object[] NO_CHILDREN = {};
	
	@Rule
	public FuseProject fuseProject = new FuseProject(GlobalDefinitionsIT.class.getSimpleName());

	@Test
	public void testGlobalDefinitionRead() throws IOException, CoreException {
		String name = "withGlobalDefinitionSample.xml";

		InputStream inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);

		File baseFile = File.createTempFile("baseFile" + name, "xml");
		Files.copy(inputStream, baseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);
		IFile fileInProject = fuseProject.getProject().getFile(name);
		fileInProject.create(inputStream, true, new NullProgressMonitor());

		CamelFile model1 = new CamelIOHandler().loadCamelModel(fileInProject, new NullProgressMonitor());

		GlobalDefinitionCamelModelElement globalDefinition = model1.getGlobalDefinitions().values().iterator().next();
		assertThat(globalDefinition.getId()).isEqualTo("sap-configuration");
	}

	@Test
	public void testCamelBeanProperties() throws IOException, CoreException {
		String name = "withGlobalDefinitionSample.xml";
		String dependsOnValue = "Something Awesome";

		InputStream inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);

		File baseFile = File.createTempFile("beanFile" + name, "xml");
		Files.copy(inputStream, baseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);
		IFile fileInProject = fuseProject.getProject().getFile(name);
		fileInProject.create(inputStream, true, new NullProgressMonitor());

		CamelFile model1 = new CamelIOHandler().loadCamelModel(fileInProject, new NullProgressMonitor());

		GlobalDefinitionCamelModelElement globalDefinition = model1.getGlobalDefinitions().values().iterator().next();

		// now make sure that it was parsed correctly into a bean
		assertThat(globalDefinition).isInstanceOf(CamelBean.class);

		CamelBean bean = (CamelBean) globalDefinition;
		assertThat(getAttributeValue(GlobalBeanEIP.PROP_CLASS, bean.getXmlNode())).isEqualTo("org.fusesource.camel.component.sap.SapConnectionConfiguration");
		assertThat(bean.getClassName()).isEqualTo("org.fusesource.camel.component.sap.SapConnectionConfiguration");

		assertThat(bean.getDependsOn()).isNull();
		bean.setDependsOn(dependsOnValue);
		assertThat(bean.getDependsOn()).isEqualTo(dependsOnValue);

		// Check that Model is valid after reloading from the filesystem
		CamelIOHandler camelIOHandler = new CamelIOHandler();
		camelIOHandler.setDocument(model1.getDocument());
		camelIOHandler.saveCamelModel(model1, model1.getResource().getLocation().toFile(), new NullProgressMonitor());
		CamelFile reloadedCamelFile = camelIOHandler.loadCamelModel(model1.getResource(), new NullProgressMonitor());

		GlobalDefinitionCamelModelElement newGlobalDefinition = reloadedCamelFile.getGlobalDefinitions().values().iterator().next();

		// now make sure that it was parsed correctly into a bean
		assertThat(newGlobalDefinition).isInstanceOf(CamelBean.class);
		CamelBean newbean = (CamelBean) newGlobalDefinition;
		assertThat(newbean.getDependsOn()).isEqualTo(dependsOnValue);
	}

	@Test
	public void testCamelBeanPropertiesSpring() throws IOException, CoreException {
		String name = "withGlobalDefinitionBeansSpring.xml";
		InputStream inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);
		File baseFile = File.createTempFile("beanSpringFile" + name, "xml");
		Files.copy(inputStream, baseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);
		IFile fileInProject = fuseProject.getProject().getFile(name);
		fileInProject.create(inputStream, true, new NullProgressMonitor());

		CamelFile model1 = new CamelIOHandler().loadCamelModel(fileInProject, new NullProgressMonitor());

		GlobalDefinitionCamelModelElement globalDefinition = model1.getGlobalDefinitions().get("accountOne");
		
		// now make sure that it was parsed correctly into a bean
		assertThat(globalDefinition).isInstanceOf(CamelBean.class);

		CamelBean bean = (CamelBean) globalDefinition;
		assertThat(bean.getId()).isEqualTo("accountOne");
		assertThat(getAttributeValue(GlobalBeanEIP.PROP_CLASS, bean.getXmlNode())).isEqualTo("org.apache.aries.simple.Account");
		assertThat(bean.getClassName()).isEqualTo("org.apache.aries.simple.Account");
		assertThat(getBeanProperties(bean.getXmlNode())).isEmpty();
		assertThat(getBeanArguments(bean.getXmlNode())).isEmpty();

		GlobalDefinitionCamelModelElement globalDefinition2 = model1.getGlobalDefinitions().get("accountTwo");
		CamelBean bean2 = (CamelBean) globalDefinition2;
		assertThat(bean2.getId()).isEqualTo("accountTwo");
		assertThat(bean2.getClassName()).isEqualTo("org.apache.aries.simple.Account2");
		assertThat(getBeanArguments(bean2.getXmlNode())).isNotEmpty();
		assertThat(getBeanProperties(bean2.getXmlNode())).isEmpty();
		
		Object[] bean2args = getBeanArguments(bean2.getXmlNode());
		assertThat(bean2args[0]).isInstanceOf(Element.class);
		Element bean2arg1 = (Element) bean2args[0];
		assertThat(bean2arg1.getTagName()).isEqualTo(GlobalBeanEIP.TAG_CONSTRUCTOR_ARG);
		assertThat(bean2arg1.getAttribute(GlobalBeanEIP.ARG_VALUE)).isEqualTo("1");
		assertThat(bean2arg1.getAttribute(GlobalBeanEIP.ARG_TYPE)).isNullOrEmpty();
		
		CamelBean bean3 = (CamelBean)model1.getGlobalDefinitions().get("accountThree");
		assertThat(bean3.getId()).isEqualTo("accountThree");
		assertThat(bean3.getClassName()).isEqualTo("org.apache.aries.simple.Account3");
		assertThat(getBeanArguments(bean3.getXmlNode())).isNotEmpty();
		assertThat(getBeanProperties(bean3.getXmlNode())).isNotEmpty();
		Object[] bean3props = getBeanProperties(bean3.getXmlNode());
		assertThat(bean3props[0]).isInstanceOf(Element.class);
		Element bean3prop1 = (Element) bean3props[0];
		assertThat(bean3prop1.getAttribute(GlobalBeanEIP.PROP_NAME)).isEqualTo("description");
		assertThat(bean3prop1.getAttribute(GlobalBeanEIP.PROP_VALUE)).isEqualTo("#1 account");
		
		CamelBean beanWithAllAttributes = (CamelBean)model1.getGlobalDefinitions().get("whateverWithAllFields");
		checkAllCommonAttributesLoaded(beanWithAllAttributes);
		assertThat(beanWithAllAttributes.getParameter(GlobalBeanEIP.PROP_FACTORY_BEAN)).isEqualTo("factoryMethod");
	}

	private void checkAllCommonAttributesLoaded(CamelBean beanWithAllAttributes) {
		assertThat(beanWithAllAttributes.getParameter(GlobalBeanEIP.PROP_DESTROY_METHOD)).isEqualTo("destroyMethod");
		assertThat(beanWithAllAttributes.getParameter(GlobalBeanEIP.PROP_CLASS)).isEqualTo("org.class");
		assertThat(beanWithAllAttributes.getParameter(GlobalBeanEIP.PROP_DEPENDS_ON)).isEqualTo("dependsOnMethod");
		assertThat(beanWithAllAttributes.getParameter(GlobalBeanEIP.PROP_INIT_METHOD)).isEqualTo("initMethod");
		assertThat(beanWithAllAttributes.getParameter(GlobalBeanEIP.PROP_DESTROY_METHOD)).isEqualTo("destroyMethod");
		assertThat(beanWithAllAttributes.getParameter(GlobalBeanEIP.PROP_SCOPE)).isEqualTo("singleton");
	}

	@Test
	public void testCamelBeanPropertiesBlueprint() throws IOException, CoreException {
		String name = "withGlobalDefinitionBeansBlueprint.xml";
		InputStream inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);
		File baseFile = File.createTempFile("beanBlueprintFile" + name, "xml");
		Files.copy(inputStream, baseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);
		IFile fileInProject = fuseProject.getProject().getFile(name);
		fileInProject.create(inputStream, true, new NullProgressMonitor());

		CamelFile model1 = new CamelIOHandler().loadCamelModel(fileInProject, new NullProgressMonitor());

		GlobalDefinitionCamelModelElement globalDefinition = model1.getGlobalDefinitions().get("accountOne");
		
		// now make sure that it was parsed correctly into a bean
		assertThat(globalDefinition).isInstanceOf(CamelBean.class);

		CamelBean bean = (CamelBean) globalDefinition;
		assertThat(bean.getId()).isEqualTo("accountOne");
		assertThat(getAttributeValue(GlobalBeanEIP.PROP_CLASS, bean.getXmlNode())).isEqualTo("org.apache.aries.simple.Account");
		assertThat(bean.getClassName()).isEqualTo("org.apache.aries.simple.Account");
		assertThat(getBeanProperties(bean.getXmlNode())).isEmpty();
		assertThat(getBeanArguments(bean.getXmlNode())).isEmpty();

		GlobalDefinitionCamelModelElement globalDefinition2 = model1.getGlobalDefinitions().get("accountTwo");
		CamelBean bean2 = (CamelBean) globalDefinition2;
		assertThat(bean2.getId()).isEqualTo("accountTwo");
		assertThat(bean2.getClassName()).isEqualTo("org.apache.aries.simple.Account2");
		assertThat(getBeanArguments(bean2.getXmlNode())).isNotEmpty();
		assertThat(getBeanProperties(bean2.getXmlNode())).isEmpty();
		
		Object[] bean2args = getBeanArguments(bean2.getXmlNode());
		assertThat(bean2args[0]).isInstanceOf(Element.class);
		Element bean2arg1 = (Element) bean2args[0];
		assertThat(bean2arg1.getTagName()).isEqualTo(GlobalBeanEIP.TAG_ARGUMENT);
		assertThat(bean2arg1.getAttribute(GlobalBeanEIP.ARG_VALUE)).isEqualTo("1");
		assertThat(bean2arg1.getAttribute(GlobalBeanEIP.ARG_TYPE)).isNullOrEmpty();
		
		GlobalDefinitionCamelModelElement globalDefinition3 = model1.getGlobalDefinitions().get("accountThree");
		CamelBean bean3 = (CamelBean) globalDefinition3;
		assertThat(bean3.getId()).isEqualTo("accountThree");
		assertThat(bean3.getClassName()).isEqualTo("org.apache.aries.simple.Account3");
		assertThat(getBeanArguments(bean3.getXmlNode())).isNotEmpty();
		assertThat(getBeanProperties(bean3.getXmlNode())).isNotEmpty();
		Object[] bean3props = getBeanProperties(bean3.getXmlNode());
		assertThat(bean3props[0]).isInstanceOf(Element.class);
		Element bean3prop1 = (Element) bean3props[0];
		assertThat(bean3prop1.getAttribute(GlobalBeanEIP.PROP_NAME)).isEqualTo("description");
		assertThat(bean3prop1.getAttribute(GlobalBeanEIP.PROP_VALUE)).isEqualTo("#1 account");
		
		CamelBean beanWithAllAttributes = (CamelBean)model1.getGlobalDefinitions().get("whateverWithAllFields");
		checkAllCommonAttributesLoaded(beanWithAllAttributes);
		assertThat(beanWithAllAttributes.getParameter(GlobalBeanEIP.PROP_FACTORY_METHOD)).isEqualTo("factoryMethod");
	}
	
	@Test
	public void testNonCamelGlobalElementWithTagNameCollision() throws IOException, CoreException {
		String name = "externalTagNameCollisionWithCamelTags.xml";
		InputStream inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);
		File baseFile = File.createTempFile("externalTagNameCollisionWithCamelTagsFile" + name, "xml");
		Files.copy(inputStream, baseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		inputStream = CamelIOHandlerIT.class.getClassLoader().getResourceAsStream("/" + name);
		IFile fileInProject = fuseProject.getProject().getFile(name);
		fileInProject.create(inputStream, true, new NullProgressMonitor());

		CamelFile model1 = new CamelIOHandler().loadCamelModel(fileInProject, new NullProgressMonitor());

		GlobalDefinitionCamelModelElement globalDefinition = model1.getGlobalDefinitions().get("_jndiProperties");
		
		assertThat(globalDefinition).isInstanceOf(GlobalDefinitionCamelModelElement.class);
	}
	
	private Object getAttributeValue(String attrName, Node camelNode) {
		if (camelNode != null && camelNode.hasAttributes()) {
			Node attrNode = camelNode.getAttributes().getNamedItem(attrName);
			if (attrNode != null) {
				return attrNode.getNodeValue();
			}
		}
		return null;
	}
	
	private Object[] getBeanProperties(Node node) {
		return getXMLChildrenByTag(GlobalBeanEIP.TAG_PROPERTY, node);
	}
	
	private Object[] getXMLChildrenByTag(String tag, Node camelNode) {
		if (camelNode instanceof Element) {
			Element parent = (Element) camelNode;
			return convertToArray(parent.getElementsByTagName(tag));
		}
		return NO_CHILDREN;
	}

	private Object[] convertToArray(NodeList list) {
		int length = list.getLength();
		Node[] copy = new Node[length];
		for (int n = 0; n < length; ++n) {
			copy[n] = list.item(n);
		}
		return copy;
	}
	
	private Object[] getBeanArguments(Node node) {
		String tagName = getArgumentTag(node);
		return getXMLChildrenByTag(tagName, node);
	}

	protected boolean isBlueprintConfig(Node node) {
		if (node != null) {
			String nsURI = getNamespace(node);
			if(!Strings.isEmpty(nsURI) && nsURI != null) {
				return nsURI.contains("blueprint"); //$NON-NLS-1$
			}
		}
		return false;
	}
	
	protected String getNamespace(Node node) {
		if (node != null) {
			String nsURI = node.getNamespaceURI();
			if (nsURI == null && node.getParentNode() != null) {
				return getNamespace(node.getParentNode());
			}
			if (nsURI != null) {
				return nsURI;
			}
		}
		return null;
	}
	
	protected String getArgumentTag(Node node) {
		if (node != null) {
			boolean isBlueprint = isBlueprintConfig(node);
			String tagName;
			if (isBlueprint) {
				tagName = GlobalBeanEIP.TAG_ARGUMENT;
			} else {
				tagName = GlobalBeanEIP.TAG_CONSTRUCTOR_ARG;
			}
			return tagName;
		}
		return null;
	}
}
