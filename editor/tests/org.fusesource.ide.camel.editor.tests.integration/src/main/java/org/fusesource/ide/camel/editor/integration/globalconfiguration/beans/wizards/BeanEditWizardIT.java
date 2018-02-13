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
 * * ID & Class reference editing (Spring) -- testBasicEditCaseSpring
 * * ID & Class reference editing (Blueprint) -- testBasicEditCaseBlueprint
 * * ID & Class plus Argument editing (Spring) -- testBasicEditCaseWithArgumentSpring
 * * ID & Class plus Argument editing (Blueprint) -- testBasicEditCaseWithArgumentBlueprint
 * * ID & Class plus Property editing (Spring) -- testBasicEditCaseWithPropertySpring
 * * ID & Class plus Property editing (Blueprint) -- testBasicEditCaseWithPropertyBlueprint
 * * ID & Class plus init-method and destroy-method editing (Spring) -- testBasicEditCaseWithPropertySpring
 * * ID & Class plus init-method and destroy-method editing (Blueprint) -- testBasicEditCaseWithPropertyBlueprint
 * 
 * @author brianf
 *
 */
public class BeanEditWizardIT {

	private BeanConfigUtil beanConfigUtil = new BeanConfigUtil();
	private static String className = String.class.getName();
	private static String newClassName = Integer.class.getName();
	private static String argType = String.class.getName();
	private static String argValue = "someInputString";
	private static String newArgType = Integer.class.getName();
	private static String newArgValue = "someOtherInputString";
	private static String propName = "testName";
	private static String propValue = "testValue";
	private static String newPropName = "editedTestName";
	private static String newPropValue = "editedTestValue";
	private static String TRIM_METHOD = "trim";
	private static String HASHCODE_METHOD = "hashCode";
	private static String STATIC_ACCOUNT_FACTORY = "org.apache.aries.simple.StaticAccountFactory";
	private static String CREATE_ACCOUNT_METHOD = "createAccount";
	
	private static String basicEditCaseBeanId = "basicCaseID";
	private static String editedBasicEditCaseBeanId = "editedBasicCaseID";
	private static String basicEditCaseBeanIdWithArgument = "basicCaseIDWithArgument";
	private static String basicEditCaseBeanIdWithProperty = "basicCaseIDWithProperty";
	private static String basicEditCaseBeanIdWithInitDestroyMethods = "basicCaseIDWithInitDestroy";
	private static String basicEditCaseBeanIdWithArgumentAndFactoryMethod = "basicCaseIDWithFactoryAndArgument";
	
	private CamelFile editTestsCamelFile = null;
	private CamelFile editTestsBlueprintCamelFile = null;
	
	@Rule
	public FuseProject fuseProject = new FuseProject(BeanEditWizardIT.class.getName());

	@Rule
	public FuseProject fuseBlueprintProject = new FuseProject(BeanEditWizardIT.class.getName() + "Blueprint");

	@Test
	public void testProjectWasCreated() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		assertThat(editTestsCamelFile).isNotNull();
	}
	
	@Test
	public void testBlueprintProjectWasCreated() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		assertThat(editTestsBlueprintCamelFile).isNotNull();
	}

	@SuppressWarnings("restriction")
	private void logInfo(String msg) {
		CamelEditorUIActivator.pluginLog().logInfo(msg);
	}
	
	@Test
	public void testBasicEditCaseSpring() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		runBasicEditCase(editTestsCamelFile);
	}
	
	@Test
	public void testBasicEditCaseBlueprint() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		runBasicEditCase(editTestsBlueprintCamelFile);
	}

	private void runBasicEditCase(final CamelFile camelFile) throws CoreException, IOException, InterruptedException, InvocationTargetException {
		// edit bean node like we do in EditGlobalBeanWizard
		// simple bean with id & class - same on blueprint and spring
		Element beanNode = beanConfigUtil.createBeanNode(camelFile, basicEditCaseBeanId, className);
		assertThat(beanNode).isNotNull();
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_ID)).isEqualTo(basicEditCaseBeanId);
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_CLASS)).isEqualTo(className);
		logInfo("Bean created: " + basicEditCaseBeanId);
		
		new CamelGlobalConfigEditor(null).addNewGlobalBeanElement(camelFile, beanNode);
		logInfo("Added bean to global configuration model");

		// Check that element just created has been correctly initialized
		check(basicEditCaseBeanId, camelFile);
		
		// update id for bean
		beanConfigUtil.setAttributeValue(beanNode, GlobalBeanEIP.PROP_ID, editedBasicEditCaseBeanId);
		logInfo("Updated bean ID in global configuration model");

		// update class for bean from String to Integer
		beanConfigUtil.setAttributeValue(beanNode, GlobalBeanEIP.PROP_CLASS, newClassName);
		logInfo("Updated bean class in global configuration model");

		// Check that Model is valid after reloading from the filesystem
		CamelIOHandler camelIOHandler = new CamelIOHandler();
		camelIOHandler.setDocument(camelFile.getDocument());
		camelIOHandler.saveCamelModel(camelFile, camelFile.getResource().getLocation().toFile(), new NullProgressMonitor());
		CamelFile reloadedCamelFile = camelIOHandler.loadCamelModel(camelFile.getResource(), new NullProgressMonitor());
		
		Element oldElement = getBeanElement(basicEditCaseBeanId, reloadedCamelFile);
		// we should not find the bean by the old element
		assertThat(oldElement).isNull();

		// but we should find it by the edited ID
		check(editedBasicEditCaseBeanId, reloadedCamelFile);
		logInfo("Found bean id (" + editedBasicEditCaseBeanId + ") in reloaded global configuration model");
		
		Element updatedElement = getBeanElement(editedBasicEditCaseBeanId, reloadedCamelFile);
		assertThat(updatedElement).isNotNull();
		assertThat(updatedElement.getAttribute(GlobalBeanEIP.PROP_ID)).isEqualTo(editedBasicEditCaseBeanId);
		assertThat(updatedElement.getAttribute(GlobalBeanEIP.PROP_CLASS)).isEqualTo(newClassName);
		logInfo("Bean id and class for (" + basicEditCaseBeanId + ") in reloaded global configuration was updated correctly");
		
	}

	@Test
	public void testBasicEditCaseWithArgumentSpring() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		runBasicEditCaseForArgument(editTestsCamelFile);
	}
	
	@Test
	public void testBasicEditCaseWithArgumentBlueprint() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		runBasicEditCaseForArgument(editTestsBlueprintCamelFile);
	}

	private void runBasicEditCaseForArgument(final CamelFile camelFile) throws CoreException, IOException, InterruptedException, InvocationTargetException {
		// create new bean node like we do in EditGlobalBeanWizard
		// simple bean plus argument
		Element beanNode = beanConfigUtil.createBeanNode(camelFile, basicEditCaseBeanIdWithArgument, className);
		assertThat(beanNode).isNotNull();
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_ID)).isEqualTo(basicEditCaseBeanIdWithArgument);
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_CLASS)).isEqualTo(className);
		logInfo("Bean created: " + basicEditCaseBeanIdWithArgument);
		
		beanConfigUtil.addBeanArgument(camelFile, beanNode, argType, argValue);
		logInfo("Bean argument created: " + argType + "/" + argValue);

		new CamelGlobalConfigEditor(null).addNewGlobalBeanElement(camelFile, beanNode);
		logInfo("Added bean to global configuration model");

		// Check that element just created has been correctly initialized
		check(basicEditCaseBeanIdWithArgument, camelFile);
		
		// check that argument exists
		Element addedArgument = (Element) findArgument(beanNode, argType, argValue);
		assertThat(addedArgument).isNotNull();

		// check that the tag name matches what we expect it to be
		assertThat(addedArgument.getTagName()).isEqualTo(beanConfigUtil.getArgumentTag(camelFile));

		beanConfigUtil.editBeanArgument(addedArgument, newArgType, newArgValue);
		
		// Check that Model is valid after reloading from the filesystem
		CamelIOHandler camelIOHandler = new CamelIOHandler();
		camelIOHandler.setDocument(camelFile.getDocument());
		camelIOHandler.saveCamelModel(camelFile, camelFile.getResource().getLocation().toFile(), new NullProgressMonitor());
		CamelFile reloadedCamelFile = camelIOHandler.loadCamelModel(camelFile.getResource(), new NullProgressMonitor());
		
		Element reloadedBeanElement = getBeanElement(basicEditCaseBeanIdWithArgument, reloadedCamelFile);
		Element oldElement = (Element) findArgument(reloadedBeanElement, argType, argValue);
		
		// we should not find the argument by the old properties
		assertThat(oldElement).isNull();
		
		check(basicEditCaseBeanIdWithArgument, reloadedCamelFile);
		logInfo("Found bean id (" + basicEditCaseBeanIdWithArgument + ") in reloaded global configuration model");
		checkValueForArgument(basicEditCaseBeanIdWithArgument, reloadedCamelFile, newArgType, newArgValue);
		logInfo("Found edited bean argument (" + newArgType + "/" + newArgValue + ") in reloaded global configuration model");
	}
	
	@Test
	public void testBasicEditCaseWithFactoryMethodAndArgumentSpring() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		runBasicEditCaseFactoryMethodArgument(editTestsCamelFile);
	}
	
	@Test
	public void testBasicEditCaseWithFactoryMethodAndArgumentBlueprint() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		runBasicEditCaseFactoryMethodArgument(editTestsBlueprintCamelFile);
	}

	private void runBasicEditCaseFactoryMethodArgument(final CamelFile camelFile) throws CoreException, IOException, InterruptedException, InvocationTargetException {
		// create new bean node like we do in EditGlobalBeanWizard
		// simple bean plus factory-method plus argument
		Element beanNode = beanConfigUtil.createBeanNode(camelFile, basicEditCaseBeanIdWithArgumentAndFactoryMethod, STATIC_ACCOUNT_FACTORY);
		assertThat(beanNode).isNotNull();
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_ID)).isEqualTo(basicEditCaseBeanIdWithArgumentAndFactoryMethod);
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_CLASS)).isEqualTo(STATIC_ACCOUNT_FACTORY);
		logInfo("Bean created: " + basicEditCaseBeanIdWithArgumentAndFactoryMethod);
		
		// update factory-method
		String factoryAttribute = beanConfigUtil.getFactoryMethodAttribute();
		beanConfigUtil.setAttributeValue(beanNode, factoryAttribute, CREATE_ACCOUNT_METHOD);
		logInfo("Updated " + factoryAttribute + " in global configuration model");

		String factoryArg = null;
		String factoryArgValue = "2";
		
		beanConfigUtil.addBeanArgument(camelFile, beanNode, factoryArg, factoryArgValue);
		logInfo("Bean argument created: null type/" + factoryArgValue);

		new CamelGlobalConfigEditor(null).addNewGlobalBeanElement(camelFile, beanNode);
		logInfo("Added bean to global configuration model");

		// Check that element just created has been correctly initialized
		check(basicEditCaseBeanIdWithArgumentAndFactoryMethod, camelFile);
		
		// check that argument exists
		Element addedArgument = (Element) findArgument(beanNode, factoryArg, factoryArgValue);
		assertThat(addedArgument).isNotNull();

		// check that the tag name matches what we expect it to be
		assertThat(addedArgument.getTagName()).isEqualTo(beanConfigUtil.getArgumentTag(camelFile));

		// Check that Model is valid after reloading from the filesystem
		CamelIOHandler camelIOHandler = new CamelIOHandler();
		camelIOHandler.setDocument(camelFile.getDocument());
		camelIOHandler.saveCamelModel(camelFile, camelFile.getResource().getLocation().toFile(), new NullProgressMonitor());
		CamelFile reloadedCamelFile = camelIOHandler.loadCamelModel(camelFile.getResource(), new NullProgressMonitor());

		Element reloadedBeanElement = getBeanElement(basicEditCaseBeanIdWithArgumentAndFactoryMethod, reloadedCamelFile);
		String retestedFactoryAttribute = beanConfigUtil.getFactoryMethodAttribute();
		assertThat(reloadedBeanElement.getAttribute(retestedFactoryAttribute)).isNotNull();
		assertThat(reloadedBeanElement.getAttribute(retestedFactoryAttribute)).isEqualTo(CREATE_ACCOUNT_METHOD);
		
		check(basicEditCaseBeanIdWithArgumentAndFactoryMethod, reloadedCamelFile);
		logInfo("Found bean id (" + basicEditCaseBeanIdWithArgumentAndFactoryMethod + ") in reloaded global configuration model");
		checkValueForArgument(basicEditCaseBeanIdWithArgumentAndFactoryMethod, reloadedCamelFile, factoryArg, factoryArgValue);
	}

	@Test
	public void testBasicEditCaseWithPropertySpring() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		runBasicEditCaseForProperty(editTestsCamelFile);
	}
	
	@Test
	public void testBasicEditCaseWithPropertyBlueprint() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		runBasicEditCaseForProperty(editTestsBlueprintCamelFile);
	}

	private void runBasicEditCaseForProperty(final CamelFile camelFile) throws CoreException, IOException, InterruptedException, InvocationTargetException {
		// create new bean node like we do in EditGlobalBeanWizard
		// simple bean plus property
		Element beanNode = beanConfigUtil.createBeanNode(camelFile, basicEditCaseBeanIdWithProperty, className);
		assertThat(beanNode).isNotNull();
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_ID)).isEqualTo(basicEditCaseBeanIdWithProperty);
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_CLASS)).isEqualTo(className);
		logInfo("Bean created: " + basicEditCaseBeanIdWithProperty);
		
		beanConfigUtil.addBeanProperty(camelFile, beanNode, propName, propValue);
		logInfo("Bean property created: " + propName + "/" + propValue);

		new CamelGlobalConfigEditor(null).addNewGlobalBeanElement(camelFile, beanNode);
		logInfo("Added bean to global configuration model");

		// Check that element just created has been correctly initialized
		check(basicEditCaseBeanIdWithProperty, camelFile);
		
		// check that the property exists
		Element addedProperty = (Element) findProperty(beanNode, propName, propValue);
		assertThat(addedProperty).isNotNull();

		beanConfigUtil.editBeanProperty(addedProperty, newPropName, newPropValue);

		// Check that Model is valid after reloading from the filesystem
		CamelIOHandler camelIOHandler = new CamelIOHandler();
		camelIOHandler.setDocument(camelFile.getDocument());
		camelIOHandler.saveCamelModel(camelFile, camelFile.getResource().getLocation().toFile(), new NullProgressMonitor());
		CamelFile reloadedCamelFile = camelIOHandler.loadCamelModel(camelFile.getResource(), new NullProgressMonitor());
		
		Element reloadedBeanElement = getBeanElement(basicEditCaseBeanIdWithProperty, reloadedCamelFile);
		Element oldElement = (Element) findProperty(reloadedBeanElement, propName, propValue);
		
		// we should not find the property by the old properties
		assertThat(oldElement).isNull();

		check(basicEditCaseBeanIdWithProperty, reloadedCamelFile);
		logInfo("Found bean id (" + basicEditCaseBeanIdWithProperty + ") in reloaded global configuration model");
		checkValueForProperty(basicEditCaseBeanIdWithProperty, reloadedCamelFile, newPropName, newPropValue);
		logInfo("Found edited bean argument (" + newPropName + "/" + newPropValue + ") in reloaded global configuration model");
	}

	@Test
	public void testBasicEditCaseWithInitAndDestroySpring() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		runBasicEditCaseWithInitAndDestroyMethod(editTestsCamelFile);
	}
	
	@Test
	public void testBasicEditCaseWithInitAndDestroyBlueprint() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		runBasicEditCaseWithInitAndDestroyMethod(editTestsBlueprintCamelFile);
	}
	
	private void runBasicEditCaseWithInitAndDestroyMethod(final CamelFile camelFile) throws CoreException, IOException, InterruptedException, InvocationTargetException {
		
		// edit bean node like we do in EditGlobalBeanWizard
		Element beanNode = beanConfigUtil.createBeanNode(camelFile, basicEditCaseBeanIdWithInitDestroyMethods, className);
		assertThat(beanNode).isNotNull();
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_ID)).isEqualTo(basicEditCaseBeanIdWithInitDestroyMethods);
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_CLASS)).isEqualTo(className);
		logInfo("Bean created: " + basicEditCaseBeanIdWithInitDestroyMethods);
		
		new CamelGlobalConfigEditor(null).addNewGlobalBeanElement(camelFile, beanNode);
		logInfo("Added bean to global configuration model");

		// Check that element just created has been correctly initialized
		check(basicEditCaseBeanIdWithInitDestroyMethods, camelFile);
		
		// update init-method and destroy-method
		beanConfigUtil.setAttributeValue(beanNode, GlobalBeanEIP.PROP_INIT_METHOD, TRIM_METHOD);
		logInfo("Updated init-method in global configuration model");
		beanConfigUtil.setAttributeValue(beanNode, GlobalBeanEIP.PROP_DESTROY_METHOD, HASHCODE_METHOD);
		logInfo("Updated destroy-method in global configuration model");

		// Check that Model is valid after reloading from the filesystem
		CamelIOHandler camelIOHandler = new CamelIOHandler();
		camelIOHandler.setDocument(camelFile.getDocument());
		camelIOHandler.saveCamelModel(camelFile, camelFile.getResource().getLocation().toFile(), new NullProgressMonitor());
		CamelFile reloadedCamelFile = camelIOHandler.loadCamelModel(camelFile.getResource(), new NullProgressMonitor());
		check(basicEditCaseBeanIdWithInitDestroyMethods, reloadedCamelFile);
		logInfo("Found bean id (" + basicEditCaseBeanIdWithInitDestroyMethods + ") in reloaded global configuration model");

		Element updatedElement = getBeanElement(basicEditCaseBeanIdWithInitDestroyMethods, reloadedCamelFile);
		assertThat(updatedElement).isNotNull();
		assertThat(updatedElement.getAttribute(GlobalBeanEIP.PROP_INIT_METHOD)).isEqualTo(TRIM_METHOD);
		assertThat(updatedElement.getAttribute(GlobalBeanEIP.PROP_DESTROY_METHOD)).isEqualTo(HASHCODE_METHOD);
		logInfo("Init- and Destroy-method found for (" + basicEditCaseBeanIdWithInitDestroyMethods + ") in reloaded global configuration was updated correctly");
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
				// null type is ok
				if ((typeVal == null || typeVal.trim().isEmpty()) && argType == null && valueVal.equals(argValue)) {
					return element;
				}
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
		logInfo("Starting setup for "+ BeanEditWizardIT.class.getSimpleName());
		waitJob();

		// define creation project
		IProject project = fuseProject.getProject();
		IFile file = project.getFile("spring.xml");
		editTestsCamelFile = fuseProject.createEmptyCamelFile(file);
		assertThat(project.exists()).describedAs("The project " + project.getName() + " doesn't exist.").isTrue();
		logInfo("Project created: " + project.getName());

		// define blueprint creation project
		IProject bpproject = fuseBlueprintProject.getProject();
		IFile bpfile = bpproject.getFile("blueprint.xml");
		editTestsBlueprintCamelFile = fuseBlueprintProject.createEmptyBlueprintCamelFile(bpfile);
		assertThat(project.exists()).describedAs("The project " + bpproject.getName() + " doesn't exist.").isTrue();
		logInfo("Project created: " + bpproject.getName());

		logInfo("End setup for "+ BeanEditWizardIT.class.getSimpleName());
	}

	protected void waitJob() {
		JobWaiterUtil jobWaiterUtil = new BuildAndRefreshJobWaiterUtil();
		jobWaiterUtil.setEndless(true);
		jobWaiterUtil.waitJob(new NullProgressMonitor());
	}

}
