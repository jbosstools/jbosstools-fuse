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

import static org.jboss.tools.fuse.reddeer.SupportedCamelVersions.CAMEL_2_17_0_REDHAT_630254;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.reddeer.common.exception.WaitTimeoutExpiredException;
import org.eclipse.reddeer.common.matcher.RegexMatcher;
import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.direct.project.Project;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.api.Shell;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.workbench.impl.editor.DefaultEditor;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.JiraIssue;
import org.jboss.tools.fuse.reddeer.LogGrapper;
import org.jboss.tools.fuse.reddeer.ProjectTemplate;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.jboss.tools.fuse.reddeer.view.FuseJMXNavigator;
import org.jboss.tools.fuse.reddeer.view.MessagesView;
import org.jboss.tools.fuse.ui.bot.tests.utils.EditorManipulator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests covers 'Remote Route Editing' feature of the Red Hat Fuse Tooling
 * 
 * @author tsedmik
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class RouteManipulationTest extends DefaultTest {

	/**
	 * Prepares test environment
	 */
	@Before
	public void setupCreateAndRunCamelProject() {

		ProjectFactory.newProject("camel-spring").deploymentType(STANDALONE).runtimeType(KARAF)
				.version(CAMEL_2_17_0_REDHAT_630254).template(ProjectTemplate.CBR_SPRING).create();
		LogView log = new LogView();
		log.open();
		log.deleteLog();
	}

	/**
	 * Cleans up test environment
	 */
	@After
	public void setupDeleteProjects() {

		AbstractWait.sleep(TimePeriod.getCustom(3));
		new WorkbenchShell();
		new ProjectExplorer().getProject("camel-spring").delete(true);
	}

	/**
	 * <p>
	 * Tests Remote Route Editing of running camel context in JMX Navigator view.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>create a new project from 'Content Based Router' template</li>
	 * <li>change the entry point of the route to "file:src/main/data?noop=true"</li>
	 * <li>Run a Project as Local Camel Context without tests</li>
	 * <li>open JMX Navigator view</li>
	 * <li>select the node "Local Camel Context", "Camel", "cbr-example-context"</li>
	 * <li>select the context menu option Edit Routes</li>
	 * <li>set focus on the recently opened Camel Editor</li>
	 * <li>select component Log _log1</li>
	 * <li>change property Message to XXX</li>
	 * <li>save the editor</li>
	 * <li>check if the Console View contains the text "Route: cbr-route is stopped, was consuming from:
	 * Endpoint[file://work/cbr/input]"</li>
	 * <li>check if the Console View contains the text Route: "Route: cbr-route started and consuming from:
	 * Endpoint[file://work/cbr/input]"</li>
	 * <li>check if the Console View contains the text file://src/data] _route1 INFO XXX</li>
	 * <li>activate Camel Editor and switch to Source page</li>
	 * <li>remove otherwise branch</li>
	 * <li>change attribute message to YYY</li>
	 * <li>save the editor</li>
	 * <li>check if the Console View contains the text file://src/data] _route1 INFO YYY</li>
	 * <li>open JMX Navigator view</li>
	 * <li>try to select the node "Local Camel Context", "Camel", "cbr-example-context", "Routes", "_route1",
	 * "file:src/data?noop=true", "Choice", "When /person/city = 'London'", "Log _log1", "file:target/messages/uk"
	 * (successful)</li>
	 * <li>try to select the node "Local Camel Context", "Camel", "cbr-example-context", "Routes", "_route1",
	 * "file:src/data?noop=true", "Choice", "Otherwise"" (unsuccessful)</li>
	 * </ol>
	 */
	@Test
	public void testRemoteRouteEditing() {

		CamelEditor editor = new CamelEditor("camel-context.xml");
		editor.setProperty("file:work/cbr/input", "Uri *", "file:src/main/data?noop=true");
		editor.save();
		Shell workbenchShell = new WorkbenchShell();
		new CamelProject("camel-spring").runCamelContextWithoutTests("camel-context.xml");
		new WaitUntil(new ConsoleHasText("cbr-route started and consuming"), TimePeriod.getCustom(300));
		AbstractWait.sleep(TimePeriod.DEFAULT);
		workbenchShell.setFocus();

		FuseJMXNavigator jmx = new FuseJMXNavigator();
		jmx.getNode("Local Processes", "Local Camel Context", "Camel");
		AbstractWait.sleep(TimePeriod.DEFAULT);
		assertNotNull(jmx.getNode("Local Processes", "Local Camel Context", "Camel", "cbr-example-context", "Routes",
				"cbr-route", "file:src/main/data?noop=true", "Log _log1", "Choice", "Log _log5"));
		jmx.getNode("Local Processes", "Local Camel Context", "Camel", "cbr-example-context").select();
		new ContextMenuItem("Edit Routes").select();
		editor = new CamelEditor(new DefaultEditor(new RegexMatcher("<connected>Remote CamelContext:.*")).getTitle());
		assertTrue(editor.isComponentAvailable("Log _log1"));
		editor.selectEditPart("Route cbr-route");
		AbstractWait.sleep(TimePeriod.SHORT);
		editor.selectEditPart("Log _log1");
		editor.setProperty("Message *", "XXX");
		editor.save();

		// test for https://issues.jboss.org/browse/FUSETOOLS-2023
		try {
			new WaitUntil(new ConsoleHasText("Invalid xpath: /order:order/order:customer/order:country = 'UK'"));
			throw new JiraIssue("FUSETOOLS-2023");
		} catch (WaitTimeoutExpiredException e) {
			// ok
		}

		new WaitUntil(new ConsoleHasText(
				"Route: cbr-route is stopped, was consuming from: Endpoint[file://src/main/data?noop=true]"));
		new WaitUntil(new ConsoleHasText(
				"Route: cbr-route started and consuming from: Endpoint[file://src/main/data?noop=true]"));
		new WaitUntil(new ConsoleHasText("INFO  XXX"));
		editor.activate();
		CamelEditor.switchTab("Source");
		EditorManipulator.copyFileContentToCamelXMLEditor("resources/camel-context-route-edit.xml");
		CamelEditor.switchTab("Design");
		new WaitUntil(new ConsoleHasText("INFO  YYY"));
		assertNotNull(jmx.getNode("Local Processes", "Local Camel Context", "Camel", "cbr-example-context", "Routes",
				"cbr-route", "file:src/main/data?noop=true", "Log _log1", "Choice",
				"When /order/customer/country = 'UK'", "Log _log2", "file:work/cbr/output/uk"));
		assertNull(jmx.getNode("Local Processes", "Local Camel Context", "Camel", "cbr-example-context", "Routes",
				"cbr-route", "file:src/main/data?noop=true", "Choice", "Otherwise"));
		assertTrue(LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * <p>
	 * Tests Tracing of running camel context in JMX Navigator view.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>create a new project from 'Content Based Router' template</li>
	 * <li>Run a Project as Local Camel Context without tests</li>
	 * <li>open JMX Navigator view</li>
	 * <li>select the node "Local Camel Context", "Camel", "cbr-example-context"</li>
	 * <li>select the context menu option Start Tracing</li>
	 * <li>check if the context menu option was changed into Stop Tracing Context</li>
	 * <li>in Project Explorer open "camel-spring", "src", "main", "data"</li>
	 * <li>in JMX Navigator open "Local Camel Context", "Camel", "cbr-example-context", "Endpoints", "file"</li>
	 * <li>perform drag&drop order1.xml from Project Explorer to "work/cbr/input" in JMX Navigator</li>
	 * <li>perform drag&drop order2.xml from Project Explorer to "work/cbr/input" in JMX Navigator</li>
	 * <li>open Message View</li>
	 * <li>in JMX Navigator open "Local Camel Context", "Camel", "cbr-example-context"</li>
	 * <li>check if the messages in the Message View corresponds with sent messages</li>
	 * </ol>
	 * 
	 * @throws IOException
	 *             copying of test messages '.../camel-spring/src/main/data/order1.xml' to
	 *             '/camel-spring/work/cbr/input/order1.xml' does not work
	 */
	@Test
	public void testTracing() throws IOException {

		Shell workbenchShell = new WorkbenchShell();
		new CamelProject("camel-spring").runCamelContextWithoutTests("camel-context.xml");
		new WaitUntil(new ConsoleHasText("cbr-route started and consuming"), TimePeriod.getCustom(300));
		AbstractWait.sleep(TimePeriod.DEFAULT);
		workbenchShell.setFocus();

		FuseJMXNavigator jmx = new FuseJMXNavigator();
		jmx.getNode("Local Processes", "Local Camel Context", "Camel");
		AbstractWait.sleep(TimePeriod.DEFAULT);
		jmx.getNode("Local Processes", "Local Camel Context", "Camel", "cbr-example-context").select();
		new ContextMenuItem("Start Tracing").select();
		AbstractWait.sleep(TimePeriod.SHORT);
		jmx.getNode("Local Processes", "Local Camel Context", "Camel", "cbr-example-context").select();
		new ContextMenuItem("Stop Tracing Context");

		MessagesView msg = new MessagesView();
		msg.open();
		String location = Project.getLocation("camel-spring");
		Files.copy(new File(location + "/src/main/data/order1.xml").toPath(),
				new File(location + "/work/cbr/input/order1.xml").toPath());
		new WaitUntil(new ConsoleHasText("to another country"), TimePeriod.getCustom(60));
		Files.copy(new File(location + "/src/main/data/order2.xml").toPath(),
				new File(location + "/work/cbr/input/order2.xml").toPath());

		msg = new MessagesView();
		msg.open();
		jmx.getNode("Local Processes", "Local Camel Context", "Camel", "cbr-example-context").select();
		assertEquals(12, msg.getAllMessages().size());
		assertEquals("_log1", msg.getMessage(2).getTraceNode());
		assertEquals("_choice1", msg.getMessage(3).getTraceNode());
		assertEquals("_log4", msg.getMessage(4).getTraceNode());
		assertEquals("_to3", msg.getMessage(5).getTraceNode());
		assertTrue(LogGrapper.getPluginErrors("fuse").size() == 0);
	}
}
