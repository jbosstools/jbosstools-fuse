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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.reddeer.common.matcher.RegexMatcher;
import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.core.exception.CoreLayerException;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.junit.annotation.RequirementRestriction;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.requirement.matcher.RequirementMatcher;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.requirements.server.ServerRequirementState;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.impl.menu.ContextMenu;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.impl.editor.DefaultEditor;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.LogGrapper;
import org.jboss.tools.fuse.reddeer.ResourceHelper;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.preference.ConsolePreferenceUtil;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.reddeer.runtime.ServerTypeMatcher;
import org.jboss.tools.fuse.reddeer.runtime.impl.ServerFuse;
import org.jboss.tools.fuse.reddeer.utils.FuseServerManipulator;
import org.jboss.tools.fuse.reddeer.utils.FuseShellSSH;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.jboss.tools.fuse.reddeer.view.FuseJMXNavigator;
import org.jboss.tools.fuse.reddeer.view.MessagesView;
import org.jboss.tools.fuse.ui.bot.tests.utils.EditorManipulator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests covers 'Remote Route Editing' and 'Tracing' features for a project which is deployed on Red Hat Fuse Runtime
 * 
 * @author tsedmik
 */
@Fuse(state = ServerRequirementState.RUNNING)
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class RouteManipulationServerTest {

	@InjectRequirement
	private static FuseRequirement serverRequirement;

	@RequirementRestriction
	public static RequirementMatcher getRestrictionMatcher() {
		return new RequirementMatcher(Fuse.class, "server", new ServerTypeMatcher(ServerFuse.class));
	}

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void defaultClassSetup() {
		new WorkbenchShell().maximize();
		ConsolePreferenceUtil.setConsoleOpenOnError(false);
		ConsolePreferenceUtil.setConsoleOpenOnOutput(false);
		new LogView().open();
		new LogView().setActivateOnNewEvents(false);
		ProjectFactory.importExistingProject(ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID,
				"resources/projects/test-route-manipulation"), "test-route-manipulation", false);
		CamelProject project = new CamelProject("test-route-manipulation");
		project.update();
	}

	@Before
	public void setupImportProject() {
		FuseServerManipulator.addModule(serverRequirement.getConfiguration().getServer().getName(), "test-route-manipulation");
	}

	@After
	public void setupCleanup() {
		try {
			new DefaultEditor(new RegexMatcher("<connected>Remote CamelContext:.*")).close();
		} catch (Exception e) {
			// editor is not opened --> ok
		}
		FuseServerManipulator.publish(serverRequirement.getConfiguration().getServer().getName());
	}

	@AfterClass
	public static void defaultFinalClean() {
		FuseServerManipulator.removeAllModules(serverRequirement.getConfiguration().getServer().getName());
		ConsoleView console = new ConsoleView();
		console.open();
		try {
			console.terminateConsole();
			new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		} catch (CoreLayerException ex) {
		}
		FuseServerManipulator.deleteAllServers();
		FuseServerManipulator.deleteAllServerRuntimes();
		ProjectFactory.deleteAllProjects();
	}

	/**
	 * <p>
	 * Tests Remote Route Editing of deployed camel context on Red Hat Fuse in JMX Navigator view.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>import the project 'test-route-manipulation'</li>
	 * <li>deploy the project the Red Hat Fuse runtime</li>
	 * <li>open JMX Navigator view</li>
	 * <li>select the node "Red Hat Fuse", "Camel", "_context1"</li>
	 * <li>select the context menu option Edit Routes</li>
	 * <li>set focus on the recently opened Camel Editor</li>
	 * <li>select component Log _log1</li>
	 * <li>change property Message to AAA-BBB-CCC</li>
	 * <li>save the editor</li>
	 * <li>check if the Console View contains the text "AAA-BBB-CCC"</li>
	 * <li>activate Camel Editor and switch to Source page</li>
	 * <li>remove otherwise branch</li>
	 * <li>save the editor</li>
	 * <li>open JMX Navigator view</li>
	 * <li>try to select the node "Red Hat Fuse", "Camel", "_context1", "Routes", "_route1", "timer:EverySecondTimer",
	 * "Choice", "Otherwise", "Log _log2" (successful)</li>
	 * <li>activate Camel Editor and switch to Source page</li>
	 * <li>remove otherwise branch</li>
	 * <li>open JMX Navigator view</li>
	 * <li>try to select the node "Red Hat Fuse", "Camel", "_context1", "Routes", "_route1", "timer:EverySecondTimer",
	 * "Choice", "Otherwise" (unsuccessful)</li>
	 * </ol>
	 */
	@Test
	public void testRemoteRouteEditing() {
		FuseJMXNavigator jmx = new FuseJMXNavigator();
		jmx.refreshLocalProcesses();
		jmx.getNode("Local Processes", "karaf", "Camel");
		TreeItem jmxNode = jmx.getNode("Local Processes", "karaf", "Camel", "_context1");
		jmxNode.select();
		new ContextMenu(jmxNode).getItem("Edit Routes").select();
		CamelEditor editor = new CamelEditor(new DefaultEditor(new RegexMatcher("<connected>Remote CamelContext:.*")).getTitle());
		assertTrue(editor.isComponentAvailable("Log _log1"));
		editor.selectEditPart("Route _route1");
		AbstractWait.sleep(TimePeriod.SHORT);
		editor.selectEditPart("Log _log1");
		editor.setProperty("Message *", "AAA-BBB-CCC");
		editor.save();
		assertTrue(new FuseShellSSH().containsLog("AAA-BBB-CCC"));
		assertNotNull(jmx.getNode("Local Processes", "karaf", "Camel", "_context1", "Routes", "_route1", "timer:EverySecondTimer", "SetBody _setBody1", "Choice", "Otherwise", "Log _log2"));
		editor.activate();
		CamelEditor.switchTab("Source");
		EditorManipulator.copyFileContentToCamelXMLEditor("resources/blueprint-route-edit.xml");
		CamelEditor.switchTab("Design");
		jmx.refreshLocalProcesses();
		assertNull(jmx.getNode("Local Processes", "Red Hat Fuse", "Camel", "_context1", "Routes", "_route1", "timer:EverySecondTimer", "SetBody _setBody1", "Choice", "Otherwise"));
		assertTrue(LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * <p>
	 * Tests Tracing of deployed camel context on Red Hat Fuse in JMX Navigator view.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>import the project 'test-route-manipulation'</li>
	 * <li>deploy the project the Red Hat Fuse runtime</li>
	 * <li>open JMX Navigator view</li>
	 * <li>select the node "Red Hat Fuse", "Camel", "_context1"</li>
	 * <li>select the context menu option Start Tracing</li>
	 * <li>check if the context menu option was changed into Stop Tracing Context</li>
	 * <li>open Message View</li>
	 * <li>in JMX Navigator open "Local Camel Context", "Camel", "_context1"</li>
	 * <li>check if the messages in the Message View corresponds with sent messages</li>
	 * </ol>
	 */
	@Test
	public void testTracing() {
		FuseJMXNavigator jmx = new FuseJMXNavigator();
		jmx.refreshLocalProcesses();
		jmx.getNode("Local Processes", "karaf", "Camel");
		AbstractWait.sleep(TimePeriod.DEFAULT);
		TreeItem jmxNode = jmx.getNode("Local Processes", "karaf", "Camel", "_context1");
		jmxNode.select();
		new ContextMenu(jmxNode).getItem("Start Tracing").select();
		AbstractWait.sleep(TimePeriod.getCustom(3));
		jmxNode = jmx.getNode("Local Processes", "karaf", "Camel", "_context1");
		jmxNode.select();
		new ContextMenu(jmxNode).getItem("Stop Tracing Context").select();;
		MessagesView msg = new MessagesView();
		msg.open();
		assertTrue(msg.getAllMessages().size() > 4);
		assertTrue(LogGrapper.getPluginErrors("fuse").size() == 0);
	}
}
