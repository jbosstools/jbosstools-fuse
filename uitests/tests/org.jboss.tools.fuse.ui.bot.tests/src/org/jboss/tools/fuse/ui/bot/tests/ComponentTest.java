/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.ui.bot.tests;

import static org.jboss.tools.fuse.reddeer.ProjectTemplate.EMPTY_SPRING;
import static org.jboss.tools.fuse.reddeer.view.PaletteViewExt.GROUP_COMPONENTS;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.eclipse.ui.perspectives.JavaEEPerspective;
import org.eclipse.reddeer.gef.view.PaletteView;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.autobuilding.AutoBuildingRequirement.AutoBuilding;
import org.eclipse.reddeer.requirements.cleanerrorlog.CleanErrorLogRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.tools.fuse.reddeer.component.CamelComponent;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.utils.LogChecker;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.jboss.tools.fuse.reddeer.view.PaletteViewExt;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Tests creation of all components in Fuse Camel editor
 * 
 * @author tsedmik
 */
@CleanWorkspace
@OpenPerspective(JavaEEPerspective.class)
@RunWith(RedDeerSuite.class)
@AutoBuilding(false)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class ComponentTest extends DefaultTest {

	protected Logger log = Logger.getLogger(ComponentTest.class);

	private CamelComponent component;
	
	public static String PROJECT_NAME = "camel-spring";
	public static String CAMEL_CONTEXT = "camel-context.xml";

	/**
	 * Sets parameters for parameterized test
	 * 
	 * @return List of all available components in Palette
	 */
	@Parameters(name = "{0}")
	public static Collection<String> setupData() {
		ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(KARAF).template(EMPTY_SPRING).create();
		new PaletteView().open();
		List<String> components = new PaletteViewExt().getGroupTools(GROUP_COMPONENTS);
		components.remove("Generic"); // skip Generic component
		return components;
	}

	public ComponentTest(String component) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> clazz = Class.forName("org.jboss.tools.fuse.reddeer.component." + component.replaceAll("\\s|-|\\(Secure\\)", ""));
		this.component = (CamelComponent) clazz.newInstance();
	}

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void setupResetCamelContext() {
		new CleanErrorLogRequirement().fulfill();
		ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(KARAF).template(EMPTY_SPRING).create();
	}

	/**
	 * Cleans up test environment
	 */
	@AfterClass
	public static void setupDeleteProjects() {
		new CleanWorkspaceRequirement().fulfill();
	}

	/**
	 * <p>
	 * Test tries to create all endpoint components available in the Palette view associated with the Camel Editor.
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>create a new Fuse Integration project (empty/spring)</li>
	 * <li>open Project Explorer view</li>
	 * <li>try to create all endpoints components in Palette View</li>
	 * <li>check if the component is present in Camel Editor</li>
	 * <li>delete the component from Camel Editor</li>
	 * </ol>
	 */
	@Test
	public void testComponents() {
		new CamelProject(PROJECT_NAME).openCamelContext(CAMEL_CONTEXT);
		CamelEditor editor = new CamelEditor(CAMEL_CONTEXT);
		editor.activate();
		try {
			editor.addCamelComponent(component, "Route _route1");
			editor.deleteCamelComponent(component);
		} catch (Exception e) {
			fail("There is a problem with this component - " + component.getLabel());
		}
		LogChecker.assertNoFuseError();
	}
}
