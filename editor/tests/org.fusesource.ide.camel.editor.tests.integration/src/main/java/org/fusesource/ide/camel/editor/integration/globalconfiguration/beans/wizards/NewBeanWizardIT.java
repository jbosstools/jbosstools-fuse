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
package org.fusesource.ide.camel.editor.integration.globalconfiguration.beans.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.editor.globalconfiguration.CamelGlobalConfigEditor;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.BeanConfigUtil;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.utils.BuildAndRefreshJobWaiterUtil;
import org.fusesource.ide.camel.editor.utils.JobWaiterUtil;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.GlobalDefinitionCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Cases tested:
 * 
 * * ID & Class reference (Spring) -- testBasicCreateCase
 * * ID & Class reference (Blueprint) -- testBasicCreateCaseWithArgumentOnBlueprint
 * * ID & Class Reference + passing Constructor Argument (Spring) - testBasicCreateCaseWithArgument
 * * ID & Class Reference + passing Constructor Argument (Blueprint) - testBasicCreateCaseWithArgumentOnBlueprint
 * * ID & Class Reference + Property Name/Value (Spring) - testBasicCreateCaseWithProperty
 * * ID & Class Reference + Property Name/Value (Blueprint) - not needed, same code used as Spring
 * * ID & Class Reference + passing Constructor Argument and Property Name/Value (Spring) - testBasicCreateCaseWithArgumentAndProperty
 * * ID & Class Reference + passing Constructor Argument Property Name/Value(Blueprint) - testBasicCreateCaseWithArgumentAndPropertyOnBlueprint
 * 
 * @author brianf
 *
 */
public class NewBeanWizardIT {

	private BeanConfigUtil beanConfigUtil = new BeanConfigUtil();
	private static String className = String.class.getName();
	private static String argType = String.class.getName();
	private static String argValue = "someInputString";
	private static String propName = "testName";
	private static String propValue = "testValue";
	
	private static String basicCreationCaseBeanId = "basicCaseID";
	private static String basicCreationCaseBeanIdWithArgument = "basicCaseIDWithArgument";
	private static String basicCreationCaseBeanIdWithProperty = "basicCaseIDWithProperty";
	private static String basicCreationCaseBeanIdWithArgumentAndProperty = "basicCaseIDWithArgumentAndProperty";
	
	private CamelFile creationTestsCamelFile = null;
	private CamelFile creationTestsBlueprintCamelFile = null;
	
	@Rule
	public FuseProject fuseProject = new FuseProject(NewBeanWizardIT.class.getName());

	@Rule
	public FuseProject fuseBlueprintProject = new FuseProject(NewBeanWizardIT.class.getName() + "Blueprint");

	@Test
	public void testProjectWasCreated() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		assertThat(creationTestsCamelFile).isNotNull();
	}
	
	@Test
	public void testBlueprintProjectWasCreated() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		assertThat(creationTestsBlueprintCamelFile).isNotNull();
	}

	@SuppressWarnings("restriction")
	private void logInfo(String msg) {
		CamelEditorUIActivator.pluginLog().logInfo(msg);
	}
	
	@Test
	public void testBasicCreateCase() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		// create new bean node like we do in AddGlobalBeanWizard.performFinish
		// simple bean with id & class - same on blueprint and spring
		Element beanNode = beanConfigUtil.createBeanNode(creationTestsCamelFile, basicCreationCaseBeanId, className);
		assertThat(beanNode).isNotNull();
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_ID)).isEqualTo(basicCreationCaseBeanId);
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_CLASS)).isEqualTo(className);
		logInfo("Bean created: " + basicCreationCaseBeanId);
		
		new CamelGlobalConfigEditor(null).addNewGlobalBeanElement(creationTestsCamelFile, beanNode);
		logInfo("Added bean to global configuration model");

		// Check that element just created has been correctly initialized
		check(basicCreationCaseBeanId, creationTestsCamelFile);

		// Check that Model is valid after reloading from the filesystem
		CamelIOHandler camelIOHandler = new CamelIOHandler();
		camelIOHandler.setDocument(creationTestsCamelFile.getDocument());
		camelIOHandler.saveCamelModel(creationTestsCamelFile, creationTestsCamelFile.getResource().getLocation().toFile(), new NullProgressMonitor());
		CamelFile reloadedCamelFile = camelIOHandler.loadCamelModel(creationTestsCamelFile.getResource(), new NullProgressMonitor());
		
		check(basicCreationCaseBeanId, reloadedCamelFile);
		logInfo("Found bean id (" + basicCreationCaseBeanId + ") in reloaded global configuration model");
	}
	
	@Test
	public void testBasicCreateCaseWithArgument() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		// create new bean node like we do in AddGlobalBeanWizard.performFinish
		// simple bean plus argument
		Element beanNode = beanConfigUtil.createBeanNode(creationTestsCamelFile, basicCreationCaseBeanIdWithArgument, className);
		assertThat(beanNode).isNotNull();
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_ID)).isEqualTo(basicCreationCaseBeanIdWithArgument);
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_CLASS)).isEqualTo(className);
		logInfo("Bean created: " + basicCreationCaseBeanIdWithArgument);
		
		beanConfigUtil.addBeanArgument(creationTestsCamelFile, beanNode, argType, argValue);
		logInfo("Bean argument created: " + argType + "/" + argValue);

		new CamelGlobalConfigEditor(null).addNewGlobalBeanElement(creationTestsCamelFile, beanNode);
		logInfo("Added bean to global configuration model");

		// Check that element just created has been correctly initialized
		check(basicCreationCaseBeanIdWithArgument, creationTestsCamelFile);
		
		// check that argument exists
		Element addedArgument = (Element) findArgument(beanNode, argType, argValue);
		assertThat(addedArgument).isNotNull();

		// check that the tag name matches what we expect it to be
		assertThat(addedArgument.getTagName()).isEqualTo(beanConfigUtil.getArgumentTag(creationTestsCamelFile));
		assertThat(addedArgument.getTagName()).isEqualToIgnoringCase(GlobalBeanEIP.TAG_CONSTRUCTOR_ARG);

		// Check that Model is valid after reloading from the filesystem
		CamelIOHandler camelIOHandler = new CamelIOHandler();
		camelIOHandler.setDocument(creationTestsCamelFile.getDocument());
		camelIOHandler.saveCamelModel(creationTestsCamelFile, creationTestsCamelFile.getResource().getLocation().toFile(), new NullProgressMonitor());
		CamelFile reloadedCamelFile = camelIOHandler.loadCamelModel(creationTestsCamelFile.getResource(), new NullProgressMonitor());
		
		check(basicCreationCaseBeanIdWithArgument, reloadedCamelFile);
		logInfo("Found bean id (" + basicCreationCaseBeanIdWithArgument + ") in reloaded global configuration model");
		checkValueForArgument(basicCreationCaseBeanIdWithArgument, reloadedCamelFile, argType, argValue);
		logInfo("Found bean argument (" + argType + "/" + argValue + ") in reloaded global configuration model");
	}
	
	@Test
	public void testBasicCreateCaseWithArgumentOnBlueprint() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		// create new bean node like we do in AddGlobalBeanWizard.performFinish
		// simple bean plus argument
		Element beanNode = beanConfigUtil.createBeanNode(creationTestsBlueprintCamelFile, basicCreationCaseBeanIdWithArgument, className);
		assertThat(beanNode).isNotNull();
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_ID)).isEqualTo(basicCreationCaseBeanIdWithArgument);
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_CLASS)).isEqualTo(className);
		logInfo("Bean created: " + basicCreationCaseBeanIdWithArgument);
		
		beanConfigUtil.addBeanArgument(creationTestsBlueprintCamelFile, beanNode, argType, argValue);
		logInfo("Bean argument created: " + argType + "/" + argValue);

		new CamelGlobalConfigEditor(null).addNewGlobalBeanElement(creationTestsBlueprintCamelFile, beanNode);
		logInfo("Added bean to global configuration model");

		// Check that element just created has been correctly initialized
		check(basicCreationCaseBeanIdWithArgument, creationTestsBlueprintCamelFile);
		
		// check that argument exists
		Element addedArgument = (Element) findArgument(beanNode, argType, argValue);
		assertThat(addedArgument).isNotNull();
		
		// check that the tag name matches what we expect it to be
		assertThat(addedArgument.getTagName()).isEqualTo(beanConfigUtil.getArgumentTag(creationTestsBlueprintCamelFile));
		assertThat(addedArgument.getTagName()).isEqualToIgnoringCase(GlobalBeanEIP.TAG_ARGUMENT);

		// Check that Model is valid after reloading from the filesystem
		CamelIOHandler camelIOHandler = new CamelIOHandler();
		camelIOHandler.setDocument(creationTestsBlueprintCamelFile.getDocument());
		camelIOHandler.saveCamelModel(creationTestsBlueprintCamelFile, creationTestsBlueprintCamelFile.getResource().getLocation().toFile(), new NullProgressMonitor());
		CamelFile reloadedCamelFile = camelIOHandler.loadCamelModel(creationTestsBlueprintCamelFile.getResource(), new NullProgressMonitor());
		
		check(basicCreationCaseBeanIdWithArgument, reloadedCamelFile);
		logInfo("Found bean id (" + basicCreationCaseBeanIdWithArgument + ") in reloaded global configuration model");
		checkValueForArgument(basicCreationCaseBeanIdWithArgument, reloadedCamelFile, argType, argValue);
		logInfo("Found bean argument (" + argType + "/" + argValue + ") in reloaded global configuration model");
	}

	@Test
	public void testBasicCreateCaseWithProperty() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		// create new bean node like we do in AddGlobalBeanWizard.performFinish
		// simple bean plus property
		Element beanNode = beanConfigUtil.createBeanNode(creationTestsCamelFile, basicCreationCaseBeanIdWithProperty, className);
		assertThat(beanNode).isNotNull();
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_ID)).isEqualTo(basicCreationCaseBeanIdWithProperty);
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_CLASS)).isEqualTo(className);
		logInfo("Bean created: " + basicCreationCaseBeanIdWithProperty);
		
		beanConfigUtil.addBeanProperty(creationTestsCamelFile, beanNode, propName, propValue);
		logInfo("Bean property created: " + propName + "/" + propValue);

		new CamelGlobalConfigEditor(null).addNewGlobalBeanElement(creationTestsCamelFile, beanNode);
		logInfo("Added bean to global configuration model");

		// Check that element just created has been correctly initialized
		check(basicCreationCaseBeanIdWithProperty, creationTestsCamelFile);
		
		// check that the property exists
		Node addedProperty = findProperty(beanNode, propName, propValue);
		assertThat(addedProperty).isNotNull();

		// Check that Model is valid after reloading from the filesystem
		CamelIOHandler camelIOHandler = new CamelIOHandler();
		camelIOHandler.setDocument(creationTestsCamelFile.getDocument());
		camelIOHandler.saveCamelModel(creationTestsCamelFile, creationTestsCamelFile.getResource().getLocation().toFile(), new NullProgressMonitor());
		CamelFile reloadedCamelFile = camelIOHandler.loadCamelModel(creationTestsCamelFile.getResource(), new NullProgressMonitor());
		
		check(basicCreationCaseBeanIdWithProperty, reloadedCamelFile);
		logInfo("Found bean id (" + basicCreationCaseBeanIdWithProperty + ") in reloaded global configuration model");
		checkValueForProperty(basicCreationCaseBeanIdWithProperty, reloadedCamelFile, propName, propValue);
		logInfo("Found bean property (" + propName + "/" + propValue + ") in reloaded global configuration model");
	}

	@Test
	public void testBasicCreateCaseWithArgumentAndProperty() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		// create new bean node like we do in AddGlobalBeanWizard.performFinish
		// simple bean plus property and argument
		Element beanNode = beanConfigUtil.createBeanNode(creationTestsCamelFile, basicCreationCaseBeanIdWithArgumentAndProperty, className);
		assertThat(beanNode).isNotNull();
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_ID)).isEqualTo(basicCreationCaseBeanIdWithArgumentAndProperty);
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_CLASS)).isEqualTo(className);
		logInfo("Bean created: " + basicCreationCaseBeanIdWithArgumentAndProperty);
		
		beanConfigUtil.addBeanArgument(creationTestsCamelFile, beanNode, argType, argValue);
		logInfo("Bean argument created: " + argType + "/" + argValue);

		beanConfigUtil.addBeanProperty(creationTestsCamelFile, beanNode, propName, propValue);
		logInfo("Bean property created: " + propName + "/" + propValue);

		new CamelGlobalConfigEditor(null).addNewGlobalBeanElement(creationTestsCamelFile, beanNode);
		logInfo("Added bean to global configuration model");

		// Check that element just created has been correctly initialized
		check(basicCreationCaseBeanIdWithArgumentAndProperty, creationTestsCamelFile);
		
		// check that argument exists
		Node addedArgument = findArgument(beanNode, argType, argValue);
		assertThat(addedArgument).isNotNull();

		// check that the property exists
		Node addedProperty = findProperty(beanNode, propName, propValue);
		assertThat(addedProperty).isNotNull();

		// Check that Model is valid after reloading from the filesystem
		CamelIOHandler camelIOHandler = new CamelIOHandler();
		camelIOHandler.setDocument(creationTestsCamelFile.getDocument());
		camelIOHandler.saveCamelModel(creationTestsCamelFile, creationTestsCamelFile.getResource().getLocation().toFile(), new NullProgressMonitor());
		CamelFile reloadedCamelFile = camelIOHandler.loadCamelModel(creationTestsCamelFile.getResource(), new NullProgressMonitor());
		
		check(basicCreationCaseBeanIdWithArgumentAndProperty, reloadedCamelFile);
		logInfo("Found bean id (" + basicCreationCaseBeanIdWithArgumentAndProperty + ") in reloaded global configuration model");
		checkValueForArgument(basicCreationCaseBeanIdWithArgumentAndProperty, reloadedCamelFile, argType, argValue);
		logInfo("Found bean argument (" + argType + "/" + argValue + ") in reloaded global configuration model");
		checkValueForProperty(basicCreationCaseBeanIdWithArgumentAndProperty, reloadedCamelFile, propName, propValue);
		logInfo("Found bean property (" + propName + "/" + propValue + ") in reloaded global configuration model");
	}

	@Test
	public void testBasicCreateCaseWithArgumentAndPropertyOnBlueprint() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		// create new bean node like we do in AddGlobalBeanWizard.performFinish
		// simple bean plus argument and a property
		Element beanNode = beanConfigUtil.createBeanNode(creationTestsBlueprintCamelFile, basicCreationCaseBeanIdWithArgumentAndProperty, className);
		assertThat(beanNode).isNotNull();
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_ID)).isEqualTo(basicCreationCaseBeanIdWithArgumentAndProperty);
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_CLASS)).isEqualTo(className);
		logInfo("Bean created: " + basicCreationCaseBeanIdWithArgument);
		
		beanConfigUtil.addBeanArgument(creationTestsBlueprintCamelFile, beanNode, argType, argValue);
		logInfo("Bean argument created: " + argType + "/" + argValue);

		beanConfigUtil.addBeanProperty(creationTestsBlueprintCamelFile, beanNode, propName, propValue);
		logInfo("Bean property created: " + propName + "/" + propValue);

		new CamelGlobalConfigEditor(null).addNewGlobalBeanElement(creationTestsBlueprintCamelFile, beanNode);
		logInfo("Added bean to global configuration model");

		// Check that element just created has been correctly initialized
		check(basicCreationCaseBeanIdWithArgumentAndProperty, creationTestsBlueprintCamelFile);
		
		// check that argument exists
		Element addedArgument = (Element) findArgument(beanNode, argType, argValue);
		assertThat(addedArgument).isNotNull();
		
		// check that the tag name matches what we expect it to be
		assertThat(addedArgument.getTagName()).isEqualTo(beanConfigUtil.getArgumentTag(creationTestsBlueprintCamelFile));
		assertThat(addedArgument.getTagName()).isEqualToIgnoringCase(GlobalBeanEIP.TAG_ARGUMENT);

		// check that the property exists
		Node addedProperty = findProperty(beanNode, propName, propValue);
		assertThat(addedProperty).isNotNull();

		// Check that Model is valid after reloading from the filesystem
		CamelIOHandler camelIOHandler = new CamelIOHandler();
		camelIOHandler.setDocument(creationTestsBlueprintCamelFile.getDocument());
		camelIOHandler.saveCamelModel(creationTestsBlueprintCamelFile, creationTestsBlueprintCamelFile.getResource().getLocation().toFile(), new NullProgressMonitor());
		CamelFile reloadedCamelFile = camelIOHandler.loadCamelModel(creationTestsBlueprintCamelFile.getResource(), new NullProgressMonitor());
		
		check(basicCreationCaseBeanIdWithArgumentAndProperty, reloadedCamelFile);
		logInfo("Found bean id (" + basicCreationCaseBeanIdWithArgumentAndProperty + ") in reloaded global configuration model");
		checkValueForArgument(basicCreationCaseBeanIdWithArgumentAndProperty, reloadedCamelFile, argType, argValue);
		logInfo("Found bean argument (" + argType + "/" + argValue + ") in reloaded global configuration model");
		checkValueForProperty(basicCreationCaseBeanIdWithArgumentAndProperty, reloadedCamelFile, propName, propValue);
		logInfo("Found bean property (" + propName + "/" + propValue + ") in reloaded global configuration model");
	}

	private void check(final String id, CamelFile camelFile) {
		Assertions.assertThat(camelFile.getRouteContainer() instanceof CamelContextElement).isTrue();
		Assertions.assertThat(onlyOneGlobalChildElementWithID(id, camelFile)).isTrue();
	}
	
	private void checkValueForArgument(final String id, CamelFile camelFile, String argType, String argValue) {
		Element beanElement = getBeanElement(id, camelFile);
		Assertions.assertThat(beanElement).isNotNull();
		// get Spring argument tags
		Assertions.assertThat(findArgument(beanElement, argType, argValue)).isNotNull();
	}
	
	private void checkValueForProperty(final String id, CamelFile camelFile, String propName, String propValue) {
		Element beanElement = getBeanElement(id, camelFile);
		Assertions.assertThat(beanElement).isNotNull();
		// get Spring argument tags
		Assertions.assertThat(findProperty(beanElement, propName, propValue)).isNotNull();
	}

	private Node findArgument(Element beanElement, String argType, String argValue) {
		String tagName = beanConfigUtil.getArgumentTag(beanElement);
		NodeList argumentList = beanElement.getElementsByTagName(tagName);
		for (int i = 0; i < argumentList.getLength(); i++) {
			Node node = argumentList.item(i);
			if (node instanceof Element) {
				Element element = (Element) node;
				String typeVal = element.getAttribute(GlobalBeanEIP.ARG_TYPE);
				String valueVal = element.getAttribute(GlobalBeanEIP.ARG_VALUE);
				if (typeVal.equals(argType) && valueVal.equals(argValue)) {
					return element;
				}
			}
		}
		return null;
	}

	private Node findProperty(Element beanElement, String propName, String propValue) {
		NodeList propertyList = beanElement.getElementsByTagName(GlobalBeanEIP.TAG_PROPERTY);
		for (int i = 0; i < propertyList.getLength(); i++) {
			Node node = propertyList.item(i);
			if (node instanceof Element) {
				Element element = (Element) node;
				String nameVal = element.getAttribute(GlobalBeanEIP.PROP_NAME);
				String valueVal = element.getAttribute(GlobalBeanEIP.PROP_VALUE);
				if (nameVal.equals(propName) && valueVal.equals(propValue)) {
					return element;
				}
			}
		}
		return null;
	}
	
	private boolean onlyOneGlobalChildElementWithID(final String id, CamelFile camelFile) {
		Collection<GlobalDefinitionCamelModelElement> childElements = camelFile.getGlobalDefinitions().values();
		int idCount = 0;
		for (AbstractCamelModelElement abstractCamelModelElement : childElements) {
			if (abstractCamelModelElement.getId().equals(id) && abstractCamelModelElement.getXmlNode() != null) {
				Element testElement = (Element) abstractCamelModelElement.getXmlNode();
				if (testElement.getTagName().equals(CamelBean.BEAN_NODE)) {
					idCount++;
				}
			}
		}
		return idCount == 1;
	}
	
	private Element getBeanElement(final String id, CamelFile camelFile) {
		Collection<GlobalDefinitionCamelModelElement> childElements = camelFile.getGlobalDefinitions().values();
		for (AbstractCamelModelElement abstractCamelModelElement : childElements) {
			if (abstractCamelModelElement.getId().equals(id) && abstractCamelModelElement.getXmlNode() != null) {
				Element testElement = (Element) abstractCamelModelElement.getXmlNode();
				if (testElement.getTagName().equals(CamelBean.BEAN_NODE)) {
					return testElement;
				}
			}
		}
		return null;
	}

	@Before
	public void setup() throws Exception {
		logInfo("Starting setup for "+ NewBeanWizardIT.class.getSimpleName());
		waitJob();

		// define creation project
		IProject project = fuseProject.getProject();
		IFile file = project.getFile("spring.xml");
		creationTestsCamelFile = fuseProject.createEmptyCamelFile(file);
		assertThat(project.exists()).describedAs("The project " + project.getName() + " doesn't exist.").isTrue();
		logInfo("Project created: " + project.getName());

		// define blueprint creation project
		IProject bpproject = fuseBlueprintProject.getProject();
		IFile bpfile = bpproject.getFile("blueprint.xml");
		creationTestsBlueprintCamelFile = fuseBlueprintProject.createEmptyBlueprintCamelFile(bpfile);
		assertThat(project.exists()).describedAs("The project " + bpproject.getName() + " doesn't exist.").isTrue();
		logInfo("Project created: " + bpproject.getName());

		logInfo("End setup for "+ NewBeanWizardIT.class.getSimpleName());
	}

	protected void waitJob() {
		JobWaiterUtil jobWaiterUtil = new BuildAndRefreshJobWaiterUtil();
		jobWaiterUtil.setEndless(true);
		jobWaiterUtil.waitJob(new NullProgressMonitor());
	}

}
