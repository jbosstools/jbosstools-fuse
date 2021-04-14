/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.ui.bot.tests;

import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.jboss.tools.fuse.reddeer.editor.CamelEditor.SOURCE_TAB;
import static org.jboss.tools.fuse.reddeer.editor.CamelEditor.DESIGN_TAB;
import static org.junit.Assert.assertTrue;
import java.util.Collection;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement;
import org.jboss.tools.fuse.reddeer.ProjectTemplate;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.utils.LogChecker;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.jboss.tools.fuse.reddeer.wizard.CamelXmlFileWizard;
import org.jboss.tools.fuse.ui.bot.tests.utils.EditorManipulator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;

/**
 * @author fpospisi
 */
@RunWith(RedDeerSuite.class)
public class RouteContextSupportTest {
	public static final String PROJECT_NAME = "routeContextSupportTest";
	public static final String ROUTES_NAME = "camel-routes.xml";
	public static final String CONTEXT_FILE = "camel-context.xml";


	/*
	 * Create new project.
	 */
	@BeforeClass
	public static void setWorkspace() {
		ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(KARAF)
				.template(ProjectTemplate.CBR_SPRING).create();
	}

	@AfterClass
	public static void cleanWorkspace() {
		new CleanWorkspaceRequirement().fulfill();
	}
	
	/**
	 * <p>
	 * routeContext support test
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>Create new project: STANDALONE - KARAF - CBR_SPRING.</li>
	 * <li>In source tab change XML to external <routeContext> "camel-routes.xml"
	 * reference.</li>
	 * <li>Create new XML - camel-routes.xml.</li>
	 * <li>Edit 'Source' in camel-routes.xml.</li>
	 * <li>Check camel-routes.xml 'Design' tab for containing items setted up in
	 * source.</li>
	 * <li>Run as Local Camel Context (without tests) and check for Fuse related
	 * errors.</li>
	 * </ol>
	 */
	@Test
	public void testRouteContextSupport() {
		/*
		 * Change content in 'Source'tab for main file.
		 */
		CamelEditor.switchTab(SOURCE_TAB);
		EditorManipulator.copyFileContentToCamelXMLEditor("resources/routeContextSupportCamelContext.xml");

		/*
		 * Create new XML file 'route-context.xml'.
		 */
		createNewFile(ROUTES_NAME);

		/*
		 * Change content in 'Source' tab for 'route-context.xml'.
		 */
		CamelEditor.switchTab(SOURCE_TAB);
		EditorManipulator.copyFileContentToCamelXMLEditor("resources/routeContextSupportRouteContext.xml");

		/*
		 * Check if design tab contains <routeContext>
		 */
		assertTrue("RouteContext not found", getRootElement().equals("RouteContext"));

		/*
		 * Run as Local Camel Context (without tests).
		 */
		CamelProject project = new CamelProject(PROJECT_NAME);
		project.openCamelContext(CONTEXT_FILE);
		CamelEditor.switchTab(DESIGN_TAB);
		project.runCamelContextWithoutTests(CONTEXT_FILE);
		assertTrue("Route cbr-route was not property started.",
				new ConsoleView().getConsoleText().contains("cbr-route started and consuming"));

		/*
		 * Check for Fuse related errors.
		 */
		LogChecker.assertNoFuseError();
	}

	/*
	 * Gets root element from design tab
	 */
	private String getRootElement() {
		CamelEditor.switchTab(DESIGN_TAB);
		Collection<TreeItem> tree_items = new ContentOutline().outlineElements();
		String rootOutlineElement = tree_items.stream().findFirst().get().getText();
		String rootOutlineElementSplit[] = rootOutlineElement.split(" ", 2);
		return rootOutlineElementSplit[0];
	}

	/*
	 * Creates new XML file
	 */
	private void createNewFile(String name) {
		CamelXmlFileWizard wizz = new CamelXmlFileWizard();
		wizz.openWizard();
		new WaitUntil(new ShellIsAvailable("New Camel Context XML File"), TimePeriod.DEFAULT);
		wizz.setName(name);
		wizz.finish();
	}

}
