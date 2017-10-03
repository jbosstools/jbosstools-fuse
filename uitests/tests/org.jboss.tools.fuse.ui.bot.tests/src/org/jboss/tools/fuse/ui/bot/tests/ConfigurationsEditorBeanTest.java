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

	@Test
	public void testDeletingBean() {
		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		editor.deleteBean("fooBean");
		editor.close(true);

		assertXPath(false, "/bean[@id='fooBean']");
	}

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

	@Test
	public void testAddingBeanWithoutMandatoryAttributes() {
		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		editor = new ConfigurationsEditor();
		AddBeanWizard beanWizard = editor.addBean();
		assertFalse("Finish button is enabled even there is no id specified", beanWizard.isFinishEnabled());
		beanWizard.setId("BeanWithoutMandatoryAttributes");
		assertFalse("Finish button is enabled even there is no class nor factory bean specified",
				beanWizard.isFinishEnabled());
		beanWizard.cancel();
		editor.close(true);
	}

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

	@Test
	public void testSettingBeanArguments() {
		ConfigurationsPropertiesView properties = new ConfigurationsPropertiesView();
		properties.open();

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

	@Test
	public void testSettingBeanProperties() {
		ConfigurationsPropertiesView properties = new ConfigurationsPropertiesView();
		properties.open();

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
