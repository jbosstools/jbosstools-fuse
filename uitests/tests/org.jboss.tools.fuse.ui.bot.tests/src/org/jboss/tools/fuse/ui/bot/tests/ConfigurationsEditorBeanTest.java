/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.ui.bot.tests;

import static org.jboss.tools.fuse.reddeer.ResourceHelper.getResourceAbsolutePath;
import static org.jboss.tools.fuse.ui.bot.tests.utils.ProjectFactory.importExistingProject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.reddeer.common.util.XPathEvaluator;
import org.eclipse.reddeer.eclipse.jdt.ui.wizards.NewClassCreationWizard;
import org.eclipse.reddeer.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanerrorlog.CleanErrorLogRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.impl.editor.DefaultEditor;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.ProjectType;
import org.jboss.tools.fuse.reddeer.editor.AddBeanWizard;
import org.jboss.tools.fuse.reddeer.editor.BeanArgumentDialog;
import org.jboss.tools.fuse.reddeer.editor.BeanPropertyDialog;
import org.jboss.tools.fuse.reddeer.editor.ConfigurationsEditor;
import org.jboss.tools.fuse.reddeer.editor.EditBeanWizard;
import org.jboss.tools.fuse.reddeer.editor.FilteredSelectionDialog;
import org.jboss.tools.fuse.reddeer.editor.MethodSelectionDialog;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.view.ConfigurationsPropertiesView;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;
import org.xml.sax.SAXException;

/**
 * Tests manipulating with beans in Configuration editor. All tests are performed on prepared projects.
 * 
 * @author apodhrad
 *
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class ConfigurationsEditorBeanTest {

	public static final String PROJECT_NAME_PREFIX = "bean";

	private String projectName;
	private ProjectType projectType;
	private ConfigurationsEditor editor;

	@Parameters(name = "{0}")
	public static Collection<ProjectType> projectTypes() {
		return Arrays.asList(ProjectType.SPRING, ProjectType.BLUEPRINT);
	}

	public ConfigurationsEditorBeanTest(ProjectType type) {
		this.projectType = type;
		this.projectName = PROJECT_NAME_PREFIX + "-" + type.toString().toLowerCase();
	}

	@BeforeClass
	public static void importBeanProjects() {
		new WorkbenchShell().maximize();
		new CleanWorkspaceRequirement().fulfill();

		projectTypes().stream().forEach(type -> importBeanProject(type.toString().toLowerCase()));
	}

	private static void importBeanProject(String type) {
		importExistingProject(getResourceAbsolutePath(Activator.PLUGIN_ID, "resources/projects/bean-" + type),
				"bean-" + type, false);
	}

	@AfterClass
	public static void cleanWorkspace() {
		new CleanWorkspaceRequirement().fulfill();
	}

	@Before
	public void cleanErrorLog() {
		new CleanErrorLogRequirement().fulfill();
	}

	/**
	 * <p>
	 * Tests deleting a bean.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to Configurations</li>
	 * <li>Delete bean 'fooBean'</li>
	 * <li>Save and check the XML file</li>
	 * </ol>
	 */
	@Test
	public void testDeletingBean() {
		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		editor.deleteBean("fooBean");
		editor.close(true);

		assertXPath(false, "/bean[@id='fooBean']");
	}

	/**
	 * <p>
	 * Tests adding a new bean with an existing Java class.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to Configurations</li>
	 * <li>Add a new bean</li>
	 * <li>Set Id</li>
	 * <li>Browse Java classes and select 'HelloBean'</li>
	 * <li>Click 'Finish'</li>
	 * <li>Save and check the XML file</li>
	 * </ol>
	 */
	@Test
	public void testAddingBeanWithExistingClass() {
		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		AddBeanWizard beanWizard = editor.addBean();
		beanWizard.setId("newBeanWithExistingClass");
		FilteredSelectionDialog dialog = beanWizard.browseClass();
		dialog.setText("HelloBean");
		dialog.waitForItems();
		dialog.ok();
		beanWizard.finish();
		editor.close(true);

		assertXPath("hello.HelloBean", "/bean[@id='newBeanWithExistingClass']/@class");
		assertXPath("singleton", "/bean[@id='newBeanWithExistingClass']/@scope");
	}

	/**
	 * <p>
	 * Tests adding a new bean with creating a new Java class.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to Configurations</li>
	 * <li>Add a bean</li>
	 * <li>Set Id</li>
	 * <li>Create new Java class 'NewBean'</li>
	 * <li>Click 'Finish'</li>
	 * <li>Save and check the XML file</li>
	 * </ol>
	 */
	@Test
	public void testAddingBeanAndCreatingNewClass() {
		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		AddBeanWizard beanWizard = editor.addBean();
		beanWizard.setId("newBeanWithNewClass");
		NewClassCreationWizard classWizard = beanWizard.addClass();
		new NewClassWizardPage(classWizard).setName("NewBean");
		classWizard.finish();
		beanWizard.finish();
		editor.close(true);

		new DefaultEditor("NewBean.java").close(true);

		assertXPath("hello.NewBean", "/bean[@id='newBeanWithNewClass']/@class");
		assertXPath("singleton", "/bean[@id='newBeanWithNewClass']/@scope");
	}

	/**
	 * <p>
	 * Tests add a new bean referencing a factory bean.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to Configurations</li>
	 * <li>Add new bean</li>
	 * <li>Set Id</li>
	 * <li>Select factory bean 'factoryBean'</li>
	 * <li>Click 'Finish'</li>
	 * <li>Save and check the XML file</li>
	 * </ol>
	 */
	@Test
	public void testAddingBeanWithFactoryBean() {
		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		AddBeanWizard beanWizard = editor.addBean();
		beanWizard.setId("newBeanWithFactoryBean");
		beanWizard.selectFactoryBean("factoryBean");
		beanWizard.finish();
		editor.close(true);

		assertXPath("factoryBean", "/bean[@id='newBeanWithFactoryBean']/@" + projectType.getFactoryElement());
		assertXPath(false, "/bean[@id='newBeanWithFactoryBean']/@class");
		assertXPath("singleton", "/bean[@id='newBeanWithFactoryBean']/@scope");
	}

	/**
	 * <p>
	 * Tests whether we can create a bean with missing some mandatory attributes.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to Configurations</li>
	 * <li>Add new bean</li>
	 * <li>Check if the button 'Finish' is enabled</li>
	 * <li>Set id</li>
	 * <li>Check if the button 'Finish' is enabled</li>
	 * <li>Click 'Cancel'</li>
	 * </ol>
	 */
	@Test
	public void testAddingBeanWithoutMandatoryAttributes() {
		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		AddBeanWizard beanWizard = editor.addBean();
		assertFalse("Finish button is enabled even there is no id specified", beanWizard.isFinishEnabled());
		beanWizard.setId("BeanWithoutMandatoryAttributes");
		try {
			assertFalse("Finish button is enabled even there is no class nor factory bean specified",
					beanWizard.isFinishEnabled());
		} finally {
			beanWizard.cancel();
			editor.close(true);
		}
	}

	/**
	 * <p>
	 * Tests whether we can add a bean with a non-existing Java class.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to Configurations</li>
	 * <li>Add new bean</li>
	 * <li>Set Id</li>
	 * <li>Directly set class to 'hello.NonExistingClass'</li>
	 * <li>Check if the button 'Finish' is enabled</li>
	 * <li>Click 'Cancel'</li>
	 * </ol>
	 */
	@Test
	public void testAddingBeanWithNonExistingClass() {
		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		AddBeanWizard beanWizard = editor.addBean();
		beanWizard.setId("newBeanWithNonExistingClass");
		beanWizard.setClazz("hello.NonExistingClass");
		assertFalse("Finish button is enabled even there is non-existing class specified",
				beanWizard.isFinishEnabled());
		beanWizard.cancel();
		editor.close(true);
	}

	/**
	 * <p>
	 * Tests setting init-method in an existing bean.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open Properties view</li>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to Configurations</li>
	 * <li>Select bean 'helloBean'</li>
	 * <li>Select Java class 'HelloBean'</li>
	 * <li>Browse init-methods and select 'publicVoid()'</li>
	 * <li>Save and check the XML file</li>
	 * </ol>
	 */
	@Test
	public void testSettingInitMethod() {
		ConfigurationsPropertiesView properties = new ConfigurationsPropertiesView();
		properties.open();

		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		editor.selectBean("helloBean");
		properties.activate();
		properties.toggleBeanClass();
		FilteredSelectionDialog dialog = properties.browseClass();
		dialog.setText("HelloBean");
		dialog.waitForItems();
		dialog.selectItem("HelloBean - hello");
		dialog.ok();
		MethodSelectionDialog methodDialog = properties.browseInitMethod();
		methodDialog.setText("publicVoid");
		methodDialog.waitForItems();
		methodDialog.selectItem("publicVoid()");
		methodDialog.ok();
		editor.close(true);

		assertXPath("publicVoid", "/bean[@id='helloBean']/@init-method");
	}

	/**
	 * <p>
	 * Tests setting destroy-method in an existing bean.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open Properties view</li>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to Configurations</li>
	 * <li>Select bean 'helloBean'</li>
	 * <li>Select Java class 'HelloBean'</li>
	 * <li>Browse destroy-methods and select 'publicVoid()'</li>
	 * <li>Save and check the XML file</li>
	 * </ol>
	 */
	@Test
	public void testSettingDestroyMethod() {
		ConfigurationsPropertiesView properties = new ConfigurationsPropertiesView();
		properties.open();

		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		editor.selectBean("helloBean");
		properties.activate();
		properties.toggleBeanClass();
		FilteredSelectionDialog dialog = properties.browseClass();
		dialog.setText("HelloBean");
		dialog.waitForItems();
		dialog.selectItem("HelloBean - hello");
		dialog.ok();
		MethodSelectionDialog methodDialog = properties.browseDestroyMethod();
		methodDialog.setText("publicVoid");
		methodDialog.waitForItems();
		methodDialog.selectItem("publicVoid()");
		methodDialog.ok();
		editor.close(true);

		assertXPath("publicVoid", "/bean[@id='helloBean']/@destroy-method");
	}

	/**
	 * <p>
	 * Tests setting factory-method in an existing bean.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open Properties view</li>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to Configurations</li>
	 * <li>Select bean 'helloBean'</li>
	 * <li>Select Java class 'HelloBean'</li>
	 * <li>Browse factory-methods and select 'publicStaticFactory()'</li>
	 * <li>Save and check the XML file</li>
	 * </ol>
	 */
	@Test
	public void testSettingFactoryMethod() {
		ConfigurationsPropertiesView properties = new ConfigurationsPropertiesView();
		properties.open();

		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		editor.selectBean("helloBean");
		properties.activate();
		properties.toggleBeanClass();
		FilteredSelectionDialog dialog = properties.browseClass();
		dialog.setText("HelloBean");
		dialog.waitForItems();
		dialog.selectItem("HelloBean - hello");
		dialog.ok();
		MethodSelectionDialog methodDialog = properties.browseFactoryMethod();
		methodDialog.setText("publicStaticFactory");
		methodDialog.waitForItems();
		methodDialog.selectItem("publicStaticFactory()");
		methodDialog.ok();
		editor.close(true);

		assertXPath("publicStaticFactory", "/bean[@id='helloBean']/@factory-method");
		assertXPath(false, "/bean[@id='helloBean']/@" + projectType.getFactoryElement());
	}

	/**
	 * <p>
	 * Tests setting factory-method in an existing bean referencing a factory bean.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open Properties view</li>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to Configurations</li>
	 * <li>Select bean 'helloBean'</li>
	 * <li>Select factory bean 'factoryBean'</li>
	 * <li>Browse factory-methods and select 'publicCreateHelloBean()'</li>
	 * <li>Save and check the XML file</li>
	 * </ol>
	 */
	@Test
	public void testSettingReferenceFactoryMethod() {
		ConfigurationsPropertiesView properties = new ConfigurationsPropertiesView();
		properties.open();

		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		editor.selectBean("helloBean");
		properties.activate();
		properties.toggleBeanReference();
		properties.selectBeanReference("factoryBean");
		MethodSelectionDialog dialog = properties.browseFactoryMethod();
		dialog.setText("publicCreateHelloBean");
		dialog.waitForItems();
		dialog.selectItem("publicCreateHelloBean()");
		dialog.ok();
		editor.close(true);

		assertXPath("publicCreateHelloBean", "/bean[@id='helloBean']/@factory-method");
		assertXPath("factoryBean", "/bean[@id='helloBean']/@" + projectType.getFactoryElement());
	}

	/**
	 * <p>
	 * Tests setting bean arguments.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to Configurations</li>
	 * <li>Select bean 'helloBean' and click 'Edit'</li>
	 * <li>Add new argument (value='no type', type='')</li>
	 * <li>Add new argument (value='string type', type='java.lang.String')</li>
	 * <li>Click 'Finish'</li>
	 * <li>Save and check the XML file</li>
	 * </ol>
	 */
	@Test
	public void testSettingBeanArguments() {
		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		EditBeanWizard beanWizard = editor.editBean("helloBean");
		BeanArgumentDialog dialog = beanWizard.addArgument();
		dialog.setValue("no type");
		dialog.ok();
		dialog = beanWizard.addArgument();
		dialog.setType("java.lang.String");
		dialog.setValue("string type");
		dialog.ok();
		beanWizard.finish();

		editor.close(true);

		String argumentElement = projectType.getArgumentElement();
		assertXPath(2, "/bean[@id='helloBean']/" + argumentElement);
		assertXPath("", "/bean[@id='helloBean']/" + argumentElement + "[@value='no type']/@type");
		assertXPath("java.lang.String", "/bean[@id='helloBean']/" + argumentElement + "[@value='string type']/@type");
	}

	/**
	 * <p>
	 * Tests setting bean properties.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to Configurations</li>
	 * <li>Select bean 'helloBean' and click 'Edit'</li>
	 * <li>Add new property (name='greeting', value='Hello World')</li>
	 * <li>Click 'Finish'</li>
	 * <li>Save and check the XML file</li>
	 * </ol>
	 */
	@Test
	public void testSettingBeanProperties() {
		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		EditBeanWizard beanWizard = editor.editBean("helloBean");
		BeanPropertyDialog dialog = beanWizard.addProperty();
		dialog.setName("greeting");
		dialog.setValue("Hello World");
		dialog.ok();
		beanWizard.finish();

		editor.close(true);

		assertXPath(1, "/bean[@id='helloBean']/property");
		assertXPath("Hello World", "/bean[@id='helloBean']/property[@name='greeting']/@value");
	}

	/**
	 * <p>
	 * Tests the list of factory methods in a bean.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open Properties view</li>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to Configurations</li>
	 * <li>Select bean 'helloBean'</li>
	 * <li>Select Java class 'HelloBean'</li>
	 * <li>Browse factory methods</li>
	 * <li>Check if the list contains only public static methods from 'HelloBean'</li>
	 * </ol>
	 */
	@Test
	public void testListingFactoryMethods() {
		ConfigurationsPropertiesView properties = new ConfigurationsPropertiesView();
		properties.open();

		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		editor.selectBean("helloBean");
		properties.activate();
		properties.toggleBeanClass();
		FilteredSelectionDialog dialog = properties.browseClass();
		dialog.setText("HelloBean");
		dialog.waitForItems();
		dialog.selectItem("HelloBean - hello");
		dialog.ok();

		Set<String> actualMethods = new HashSet<>();
		MethodSelectionDialog methodDialog = properties.browseFactoryMethod();
		methodDialog.getItems().stream().forEach(item -> actualMethods.add(item.getText()));
		methodDialog.cancel();
		editor.close(true);

		assertEquals("Only public static non-void methods should be listed as possible factory methods",
				asSet("publicStaticFactory()"), actualMethods);
	}

	/**
	 * <p>
	 * Tests the list of factory methods in a bean referencing a factory bean.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open Properties view</li>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to Configurations</li>
	 * <li>Select bean 'helloBean'</li>
	 * <li>Select factory bean 'factoryBean'</li>
	 * <li>Browse factory methods</li>
	 * <li>Check if the list contains only public non-static methods from 'FactoryBean'</li>
	 * </ol>
	 */
	@Test
	public void testListingReferenceFactoryMethods() {
		ConfigurationsPropertiesView properties = new ConfigurationsPropertiesView();
		properties.open();

		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		editor.selectBean("helloBean");
		properties.activate();
		properties.toggleBeanReference();
		properties.selectBeanReference("factoryBean");

		Set<String> actualMethods = new HashSet<>();
		MethodSelectionDialog methodDialog = properties.browseFactoryMethod();
		methodDialog.getItems().stream().forEach(item -> actualMethods.add(item.getText()));
		methodDialog.cancel();
		editor.close(true);

		assertEquals("Only public non-static non-void methods should be listed as possible reference factory methods",
				asSet("publicCreateHelloBean()"), actualMethods);
	}

	/**
	 * <p>
	 * Tests the list of init methods in a bean.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open Properties view</li>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to Configurations</li>
	 * <li>Select bean 'helloBean'</li>
	 * <li>Select Java class 'HelloBean'</li>
	 * <li>Browse init methods</li>
	 * <li>Check if the list contains only public void parameter-free methods from 'HelloBean'</li>
	 * </ol>
	 */
	@Test
	public void testListingInitMethods() {
		ConfigurationsPropertiesView properties = new ConfigurationsPropertiesView();
		properties.open();

		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		editor.selectBean("helloBean");
		properties.activate();
		properties.toggleBeanClass();
		FilteredSelectionDialog dialog = properties.browseClass();
		dialog.setText("HelloBean");
		dialog.waitForItems();
		dialog.selectItem("HelloBean - hello");
		dialog.ok();

		Set<String> actualMethods = new HashSet<>();
		MethodSelectionDialog methodDialog = properties.browseInitMethod();
		methodDialog.getItems().stream().forEach(item -> actualMethods.add(item.getText()));
		methodDialog.cancel();
		editor.close(true);

		// Blueprint doesn't allow specifying methods with a return value
		if (projectType.equals(ProjectType.BLUEPRINT)) {
			assertEquals("Only public void parameter-free methods should be listed as possible init methods",
					asSet("publicVoid()", "publicStaticVoid()"), actualMethods);
		}
		// Spring allows specifying methods with a return value
		if (projectType.equals(ProjectType.SPRING)) {
			assertEquals("Only public parameter-free methods should be listed as possible init methods",
					asSet("publicVoid()", "publicStaticVoid()", "getName()", "hello()", "publicFactory()",
							"publicStaticFactory()"),
					actualMethods);
		}
	}

	/**
	 * <p>
	 * Tests the list of destroy methods in a bean.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open Properties view</li>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to Configurations</li>
	 * <li>Select bean 'helloBean'</li>
	 * <li>Select Java class 'HelloBean'</li>
	 * <li>Browse destroy methods</li>
	 * <li>Check if the list contains only public void parameter-free methods from 'HelloBean'</li>
	 * </ol>
	 */
	@Test
	public void testListingDestroyMethods() {
		ConfigurationsPropertiesView properties = new ConfigurationsPropertiesView();
		properties.open();

		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		editor.selectBean("helloBean");
		properties.activate();
		properties.toggleBeanClass();
		FilteredSelectionDialog dialog = properties.browseClass();
		dialog.setText("HelloBean");
		dialog.waitForItems();
		dialog.selectItem("HelloBean - hello");
		dialog.ok();

		Set<String> actualMethods = new HashSet<>();
		MethodSelectionDialog methodDialog = properties.browseDestroyMethod();
		methodDialog.getItems().stream().forEach(item -> actualMethods.add(item.getText()));
		methodDialog.cancel();
		editor.close(true);

		// Blueprint doesn't allow specifying methods with a return value
		if (projectType.equals(ProjectType.BLUEPRINT)) {
			assertEquals("Only public void parameter-free methods should be listed as possible destroy methods",
					asSet("publicVoid()", "publicStaticVoid()"), actualMethods);
		}
		// Spring allows specifying methods with a return value
		if (projectType.equals(ProjectType.SPRING)) {
			assertEquals("Only public parameter-free methods should be listed as possible destroy methods",
					asSet("publicVoid()", "publicStaticVoid()", "getName()", "hello()", "publicFactory()",
							"publicStaticFactory()"),
					actualMethods);
		}
	}

	private static Set<String> asSet(String... items) {
		return new HashSet<String>(Arrays.asList(items));
	}

	private void assertXPath(String expectedResult, String xPathExpression) {
		assertXPath(expectedResult, null, xPathExpression);
	}

	private void assertXPath(Boolean expectedResult, String xPathExpression) {
		assertXPath(expectedResult.toString(), "boolean", xPathExpression);
	}

	private void assertXPath(Integer expectedResult, String xPathExpression) {
		assertXPath(expectedResult.toString(), "count", xPathExpression);
	}

	private void assertXPath(String expectedResult, String function, String xPathExpression) {
		if (!xPathExpression.startsWith("/" + projectType.getRootElement())) {
			xPathExpression = "/" + projectType.getRootElement() + xPathExpression;
		}
		if (function != null) {
			xPathExpression = function + "(" + xPathExpression + ")";
		}
		String actualResult;
		try {
			XPathEvaluator xpath = new XPathEvaluator(editor.getAssociatedFile().getInputStream(), false);
			actualResult = xpath.evaluateXPath(xPathExpression);
		} catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
			throw new RuntimeException(e);
		}
		assertEquals(expectedResult, actualResult);
	}
}
