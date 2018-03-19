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

import static org.jboss.tools.fuse.reddeer.ProjectTemplate.EMPTY_SPRING;
import static org.jboss.tools.fuse.reddeer.SupportedCamelVersions.CAMEL_2_17_0_REDHAT_630187;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.perspectives.JavaEEPerspective;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.gef.view.PaletteView;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.autobuilding.AutoBuildingRequirement.AutoBuilding;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.exception.WorkbenchLayerException;
import org.eclipse.reddeer.workbench.handler.EditorHandler;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.LogGrapper;
import org.jboss.tools.fuse.reddeer.component.CamelComponent;
import org.jboss.tools.fuse.reddeer.component.CamelComponents;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.ui.bot.tests.utils.ProjectFactory;
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

	private CamelComponent component;
	protected Logger log = Logger.getLogger(ComponentTest.class);

	/**
	 * Sets parameters for parameterized test
	 * 
	 * @return List of all available components in Palette
	 */
	@Parameters
	public static Collection<CamelComponent> setupData() {
		return CamelComponents.getEndpoints();
	}

	public ComponentTest(CamelComponent component) {
		this.component = component;
	}

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void setupResetCamelContext() {

		new WorkbenchShell();
		ProjectFactory.newProject("camel-spring").deploymentType(STANDALONE).runtimeType(KARAF)
				.version(CAMEL_2_17_0_REDHAT_630187).template(EMPTY_SPRING).create();
		new LogView().deleteLog();
	}

	@BeforeClass
	public static void setupClosePaletteView() {
		new WorkbenchShell();
		try {
			new PaletteView().close();
		} catch (WorkbenchLayerException ex) {
		}
	}

	/**
	 * Cleans up test environment
	 */
	@AfterClass
	public static void setupDeleteProjects() {

		new WorkbenchShell();
		EditorHandler.getInstance().closeAll(true);
		ProjectFactory.deleteAllProjects();
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

		new ProjectExplorer().open();
		CamelEditor editor = new CamelEditor("camel-context.xml");
		editor.activate();
		try {
			editor.addCamelComponent(component, "Route _route1");
			editor.deleteCamelComponent(component);
		} catch (Exception e) {
			fail("There is a problem with this component - " + component.getLabel());
		}
		assertTrue(LogGrapper.getPluginErrors("fuse").size() == 0);
	}
}
