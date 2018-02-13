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
package org.fusesource.ide.camel.editor.integration.globalconfiguration.beans;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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

/**
 * Cases tested:
 *
 * case 1 - spring with root namespace prefix "beans"
 * case 2 - blueprint with root namespace prefix "bp"
 * case 3 - spring with deeper namespace prefix "camel" that should not affect new global bean
 * case 4 - blueprint with deeper namespace prefix "camel" that should not affect new global bean
 * case 5 - spring with root namespace "beans" and deeper namespace prefix "camel" (bean should pick up "beans")
 * case 6 - blueprint with root namespace "bp" and deeper namespace prefix "camel" (bean should pick up "bp")
 * 
 * @author brianf
 *
 */
public class BeanConfigUtilIT {

	private BeanConfigUtil beanConfigUtil = new BeanConfigUtil();
	private CamelIOHandler marshaller = new CamelIOHandler();
	private static String className = String.class.getName();
	private static String basicEditCaseBeanId = "basicCaseID";
	private CamelFile springCamelFileRootPrefix = null;
	private CamelFile blueprintCamelFileRootPrefix = null;
	private CamelFile springCamelFileDeeperPrefix = null;
	private CamelFile blueprintCamelFileDeeperPrefix = null;
	private CamelFile springCamelFileRootAndDeeperPrefix = null;
	private CamelFile blueprintCamelFileRootAndDeeperPrefix = null;
	
	@Rule
	public FuseProject fuseProject = new FuseProject(BeanConfigUtilIT.class.getName());

	@Rule
	public FuseProject fuseBlueprintProject = new FuseProject(BeanConfigUtilIT.class.getName() + "Blueprint");

	@Test
	public void testProjectWasCreated() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		assertThat(springCamelFileRootPrefix).isNotNull();
	}
	
	@Test
	public void testBlueprintProjectWasCreated() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		assertThat(blueprintCamelFileRootPrefix).isNotNull();
	}

	@SuppressWarnings("restriction")
	private void logInfo(String msg) {
		CamelEditorUIActivator.pluginLog().logInfo(msg);
	}
	
	@Test
	public void testRootPrefixCaseSpring() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		runBasicEditCase(springCamelFileRootPrefix, "beans");
	}
	
	@Test
	public void testDeeperPrefixCaseSpring() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		runBasicEditCase(springCamelFileDeeperPrefix, null);
	}

	@Test
	public void testRootAndDeeperPrefixCaseSpring() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		runBasicEditCase(springCamelFileRootAndDeeperPrefix, "beans");
	}

	@Test
	public void testRootPrefixCaseBlueprint() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		runBasicEditCase(blueprintCamelFileRootPrefix, "bp");
	}

	@Test
	public void testDeeperPrefixCaseBlueprint() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		runBasicEditCase(blueprintCamelFileDeeperPrefix, null);
	}

	@Test
	public void testRootAndDeeperPrefixCaseBlueprint() throws CoreException, IOException, InterruptedException, InvocationTargetException {
		runBasicEditCase(blueprintCamelFileRootAndDeeperPrefix, "bp");
	}

	private void runBasicEditCase(final CamelFile camelFile, String prefix) throws CoreException, IOException, InterruptedException, InvocationTargetException {
		// edit bean node like we do in EditGlobalBeanWizard
		// simple bean with id & class - same on blueprint and spring
		Element beanNode = beanConfigUtil.createBeanNode(camelFile, basicEditCaseBeanId, className);
		assertThat(beanNode).isNotNull();
		String searchFor = "bean";
		if (prefix != null) {
			searchFor = prefix + ":bean";
		}
		assertThat(beanNode.getTagName()).isEqualTo(searchFor);
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_ID)).isEqualTo(basicEditCaseBeanId);
		assertThat(beanNode.getAttribute(GlobalBeanEIP.PROP_CLASS)).isEqualTo(className);
		logInfo("Bean created: " + basicEditCaseBeanId);
		
		new CamelGlobalConfigEditor(null).addNewGlobalBeanElement(camelFile, beanNode);
		logInfo("Added bean to global configuration model with prefix");

		// Check that element just created has been correctly initialized
		check(basicEditCaseBeanId, camelFile);
	}

	private void check(final String id, CamelFile camelFile) {
		Assertions.assertThat(camelFile.getRouteContainer() instanceof CamelContextElement).isTrue();
		Assertions.assertThat(onlyOneGlobalChildElementWithID(id, camelFile)).isTrue();
	}
	
	private boolean onlyOneGlobalChildElementWithID(final String id, CamelFile camelFile) {
		Collection<GlobalDefinitionCamelModelElement> childElements = camelFile.getGlobalDefinitions().values();
		int idCount = 0;
		for (AbstractCamelModelElement abstractCamelModelElement : childElements) {
			if (abstractCamelModelElement.getId().equals(id) && abstractCamelModelElement.getXmlNode() != null) {
				Element testElement = (Element) abstractCamelModelElement.getXmlNode();
				if (testElement.getTagName().equals(CamelBean.BEAN_NODE) || testElement.getTagName().contains(':' + CamelBean.BEAN_NODE)) {
					idCount++;
				}
			}
		}
		return idCount == 1;
	}
	
	protected CamelFile createProjectWithCamelFile(FuseProject fuseProject, String filename, String templatepath) throws IOException, CoreException {
		IProject project = fuseProject.getProject();
		IFile file = project.getFile(filename);
		CamelFile camelFile = loadLocalCamelFile(templatepath, file);
		assertThat(project.exists()).describedAs("The project " + project.getName() + " doesn't exist.").isTrue();
		logInfo("Project created: " + project.getName());
		return camelFile;
	}
	
	@Before
	public void setup() throws Exception {
		logInfo("Starting setup for "+ BeanConfigUtilIT.class.getSimpleName());
		waitJob();

		springCamelFileRootPrefix = createProjectWithCamelFile(fuseProject, "spring.xml", "src/main/resources/empty-CamelFileWithRootPrefix.xml");
		blueprintCamelFileRootPrefix = createProjectWithCamelFile(fuseBlueprintProject, "blueprint.xml", "src/main/resources/empty-BlueprintCamelFileWithRootPrefix.xml");
		springCamelFileDeeperPrefix = createProjectWithCamelFile(fuseProject, "spring2.xml", "src/main/resources/empty-CamelFileWithDeeperPrefix.xml");
		blueprintCamelFileDeeperPrefix = createProjectWithCamelFile(fuseBlueprintProject, "blueprint2.xml", "src/main/resources/empty-BlueprintCamelFileWithDeeperPrefix.xml");
		springCamelFileRootAndDeeperPrefix = createProjectWithCamelFile(fuseProject, "spring3.xml", "src/main/resources/empty-CamelFileWithBothRootAndDeeper.xml");
		blueprintCamelFileRootAndDeeperPrefix = createProjectWithCamelFile(fuseBlueprintProject, "blueprint3.xml", "src/main/resources/empty-BlueprintCamelFileWithBothRootAndDeeper.xml");

		logInfo("End setup for "+ BeanConfigUtilIT.class.getSimpleName());
	}
	
	protected CamelFile loadLocalCamelFile(String name, IFile file) throws IOException, CoreException {
		InputStream inputStream = BeanConfigUtilIT.class.getClassLoader().getResourceAsStream("/" + name);

		File baseFile = File.createTempFile("baseFile" + name, "xml");
		Files.copy(inputStream, baseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

		inputStream = BeanConfigUtilIT.class.getClassLoader().getResourceAsStream("/" + name);
		IFile fileInProject = file;
		fileInProject.create(inputStream, true, new NullProgressMonitor());

		CamelFile model1 = marshaller.loadCamelModel(fileInProject, new NullProgressMonitor());
		assertThat(model1.getRouteContainer()).isNotNull();
		
		return model1;
	}

	protected void waitJob() {
		JobWaiterUtil jobWaiterUtil = new BuildAndRefreshJobWaiterUtil();
		jobWaiterUtil.setEndless(true);
		jobWaiterUtil.waitJob(new NullProgressMonitor());
	}

}
