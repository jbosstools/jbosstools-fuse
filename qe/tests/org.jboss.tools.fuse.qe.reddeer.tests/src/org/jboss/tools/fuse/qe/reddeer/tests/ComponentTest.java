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
package org.jboss.tools.fuse.qe.reddeer.tests;

import static org.jboss.tools.fuse.qe.reddeer.ProjectType.SPRING;
import static org.jboss.tools.fuse.qe.reddeer.SupportedCamelVersions.CAMEL_2_17_0_REDHAT_630187;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.eclipse.ui.perspectives.JavaEEPerspective;
import org.jboss.reddeer.gef.view.PaletteView;
import org.jboss.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.autobuilding.AutoBuildingRequirement.AutoBuilding;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.workbench.exception.WorkbenchLayerException;
import org.jboss.reddeer.workbench.handler.EditorHandler;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.qe.reddeer.LogGrapper;
import org.jboss.tools.fuse.qe.reddeer.component.CamelComponent;
import org.jboss.tools.fuse.qe.reddeer.component.CamelComponents;
import org.jboss.tools.fuse.qe.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.view.ErrorLogView;
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
		ProjectFactory.newProject("camel-spring").type(SPRING).version(CAMEL_2_17_0_REDHAT_630187).create();
		new ErrorLogView().deleteLog();
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
