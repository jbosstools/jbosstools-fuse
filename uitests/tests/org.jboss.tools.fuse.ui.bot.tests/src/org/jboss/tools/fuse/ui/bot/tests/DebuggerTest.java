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

import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.condition.LaunchIsSuspended;
import org.eclipse.reddeer.eclipse.debug.ui.views.breakpoints.Breakpoint;
import org.eclipse.reddeer.eclipse.debug.ui.views.breakpoints.BreakpointsView;
import org.eclipse.reddeer.eclipse.debug.ui.views.launch.ResumeButton;
import org.eclipse.reddeer.eclipse.debug.ui.views.launch.TerminateButton;
import org.eclipse.reddeer.eclipse.debug.ui.views.variables.VariablesView;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.ui.perspectives.JavaEEPerspective;
import org.eclipse.reddeer.eclipse.ui.views.log.LogMessage;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.jboss.tools.fuse.reddeer.LogGrapper;
import org.jboss.tools.fuse.reddeer.ProjectTemplate;
import org.jboss.tools.fuse.reddeer.debug.IsRunning;
import org.jboss.tools.fuse.reddeer.debug.StepOverButton;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.utils.LogChecker;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.junit.After;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests Camel Routes Debugger
 * 
 * @author tsedmik
 */
@CleanWorkspace
@OpenPerspective(JavaEEPerspective.class)
@RunWith(RedDeerSuite.class)
public class DebuggerTest extends DefaultTest {

	public static final String PROJECT_NAME = "cbr @1";
	public static final String CAMEL_CONTEXT = "camel-context.xml";
	public static final String CHOICE = "Choice";
	public static final String CHOICE_ID = "_choice1";
	public static final String LOG = "Log _log3";
	public static final String LOG_ID = "_log3";

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void setupInitial() {

		ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(KARAF)
				.template(ProjectTemplate.CBR_SPRING).create();
		CamelEditor editor = new CamelEditor(CAMEL_CONTEXT);
		editor.selectEditPart("file:work/cbr/input");
		editor.setProperty("Uri *", "file:src/main/data?noop=true");
		editor.save();
	}

	/**
	 * Cleans up test environment
	 */
	@After
	public void setupRemoveAllBreakpoints() {

		new BreakpointsView().removeAllBreakpoints();
	}

	/**
	 * <p>
	 * Test tries to add/remove/disable/enable breakpoints to the components in the Camel Editor.
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>create a new project from template 'Content Based Router'</li>
	 * <li>open Project Explorer view</li>
	 * <li>open camel-context.xml file</li>
	 * <li>set breakpoint to the choice component</li>
	 * <li>set breakpoint to the log component in the top branch</li>
	 * <li>open Breakpoints View and check if set breakpoints are present</li>
	 * <li>open camel-context.xml file</li>
	 * <li>disable the breakpoint on the choice component</li>
	 * <li>check if the breakpoint can be disabled again</li>
	 * <li>check if the breakpoint is disabled in the Breakpoints view</li>
	 * <li>enable the breakpoint on the choice component</li>
	 * <li>check if the breakpoint can be enabled again</li>
	 * <li>check if the breakpoint is enabled in the Breakpoints view</li>
	 * <li>delete the breakpoint in the CamelEditor</li>
	 * <li>check if the breakpoint can be deleted again</li>
	 * <li>check if the breakpoint is no longer available in the Breakpoints view</li>
	 * <li>remove the breakpoint on the log component via Breakpoints view</li>
	 * <li>check if the breakpoint is set in the Camel Editor</li>
	 * </ol>
	 */
	@Test
	public void testBreakpointManipulation() {

		CamelEditor editor = new CamelEditor(CAMEL_CONTEXT);

		// set some breakpoints
		editor.setBreakpoint(CHOICE);
		editor.setBreakpoint(LOG);

		// check Breakpoints View
		BreakpointsView view = new BreakpointsView();
		assertTrue(view.isBreakpointAvailable(CHOICE_ID));
		assertTrue(view.isBreakpointAvailable(LOG_ID));

		// do some operations (disable/enable/remove) and check
		view.activate();
		Breakpoint choice = view.getBreakpoint(CHOICE_ID);
		choice.disable();
		assertFalse(choice.isEnabled());
		assertFalse(editor.isBreakpointEnabled(CHOICE));
		editor.enableBreakpoint(CHOICE);
		assertTrue(editor.isBreakpointEnabled(CHOICE));
		view.open();
		choice = view.getBreakpoint(CHOICE_ID);
		assertTrue(choice.isEnabled());
		editor.deleteBreakpoint(CHOICE);
		assertFalse(editor.isBreakpointSet(CHOICE));
		view.open();
		assertTrue(view.getBreakpoint(CHOICE_ID) == null);
		view.open();
		view.getBreakpoint(LOG_ID).remove();
		assertFalse(editor.isBreakpointSet(LOG));
		LogChecker.assertNoFuseError();
	}

	/**
	 * <p>
	 * Test tries debugging of the Camel route - suspending, resuming, step over, variables values.
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>create a new project from template 'Content Based Router'</li>
	 * <li>open Project Explorer view</li>
	 * <li>open camel-context.xml file</li>
	 * <li>set breakpoint to the choice component</li>
	 * <li>set breakpoint to the log component in the otherwise branch</li>
	 * <li>debug the Camel Context without tests</li>
	 * <li>wait until process is suspended</li>
	 * <li>check if the Console View contains text Enabling Debugger</li>
	 * <li>check if the Variables View contains variable Endpoint with value choice1</li>
	 * <li>check if the Variables View contains variable Message with value <name>Erie Zoo</name></li>
	 * <li>resume debugging</li>
	 * <li>wait until process is suspended</li>
	 * <li>check if the Variables View contains variable Endpoint with value log3</li>
	 * <li>click on the Step over button</li>
	 * <li>wait until process is suspended</li>
	 * <li>check if the Console View contains text "Sending order order5.xml to US"</li>
	 * <li>check if the Variables View contains variable Endpoint with value to3</li>
	 * <li>remove all breakpoints</li>
	 * <li>check if the Console View contains text Removing breakpoint choice1</li>
	 * <li>check if the Console View contains text Removing breakpoint log3</li>
	 * <li>resume debugging</li>
	 * <li>terminate process via Terminate button in the Console View</li>
	 * </ol>
	 */
	@Test
	public void testDebugger() {

		new CamelProject(PROJECT_NAME).openCamelContext(CAMEL_CONTEXT);
		CamelEditor editor = new CamelEditor(CAMEL_CONTEXT);
		editor.setBreakpoint(CHOICE);
		new CamelProject(PROJECT_NAME).debugCamelContextWithoutTests(CAMEL_CONTEXT);

		// should stop on the 'choice1' node
		new WaitUntil(new LaunchIsSuspended(), TimePeriod.DEFAULT);
		new ConsoleView().open();
		assertTrue(new ConsoleHasText("Enabling debugger").test());
		VariablesView variables = new VariablesView();
		AbstractWait.sleep(TimePeriod.getCustom(5));
		assertEquals(CHOICE_ID, variables.getValue("Endpoint"));

		for (int i = 0; i < 5; i++) {
			variables.close();
			AbstractWait.sleep(TimePeriod.SHORT);
			variables.open();
			TreeItem msg = null;

			// gets "Message" from "Variables View"
			for (TreeItem item : new DefaultTree().getItems()) {
				if (!item.getText().equals("Message")) {
					continue;
				}
				msg = item;
			}

			// selects "MessageBody" from "Variables View"
			if (msg != null) {
				for (TreeItem item : msg.getItems()) {
					item.select();
					if (item.getText().equals("MessageBody")) {
						item.select();
						break;
					}
				}
			}

			String message = new DefaultStyledText().getText();
			editor.activate();
			if (message.contains("<name>Erie Zoo</name>")) {
				editor.setBreakpoint(LOG);
				break;
			}
			ResumeButton resume = new ResumeButton();
			assertTrue(resume.isEnabled());
			resume.click();
			new WaitUntil(new LaunchIsSuspended(), TimePeriod.DEFAULT);
		}

		// resume and then should stop on the 'log1' node
		ResumeButton resume = new ResumeButton();
		assertTrue(resume.isEnabled());
		resume.click();
		new WaitUntil(new LaunchIsSuspended(), TimePeriod.DEFAULT);
		AbstractWait.sleep(TimePeriod.getCustom(2));
		assertEquals(LOG_ID, variables.getValue("Endpoint"));

		// step over then should stop on the 'to1' endpoint
		assertTrue(resume.isEnabled());
		new StepOverButton().select();
		new WaitUntil(new LaunchIsSuspended(), TimePeriod.DEFAULT);
		assertTrue(new ConsoleHasText("Sending order order5.xml to the US").test());
		assertTrue(resume.isEnabled());
		AbstractWait.sleep(TimePeriod.getCustom(5));
		assertEquals("_to2", variables.getValue("Endpoint"));

		// remove all breakpoints
		new BreakpointsView().removeAllBreakpoints();
		assertTrue(new ConsoleHasText("Removing breakpoint _choice1").test());
		assertTrue(new ConsoleHasText("Removing breakpoint _log3").test());
		resume.click();

		// all breakpoints should be processed
		new WaitUntil(new IsRunning(), TimePeriod.DEFAULT);
		new TerminateButton().click();
		Assume.assumeFalse(testIssue2306());
		LogChecker.assertNoFuseError();
	}

	/**
	 * <p>
	 * Test tries conditional debugging of the Camel route - suspending only when condition is fulfilled.
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>create a new project from template 'Content Based Router'</li>
	 * <li>open Project Explorer view</li>
	 * <li>open camel-context.xml file</li>
	 * <li>set conditional breakpoint to the choice component - simple with "${in.header.CamelFileName} ==
	 * 'order1.xml'"</li>
	 * <li>set conditional breakpoint to the log component in the otherwise branch - simple with
	 * "${in.header.CamelFileName} == 'order2.xml'"</li>
	 * <li>debug the Camel Context without tests</li>
	 * <li>wait until process is suspended</li>
	 * <li>check if the Variables View contains variable Endpoint with value choice1</li>
	 * <li>resume debugging</li>
	 * <li>check if the Console View contains text "Receiving order order1.xml"</li>
	 * <li>check if the Console View contains text "Receiving order order2.xml"</li>
	 * <li>check if the Console View contains text "Receiving order order3.xml"</li>
	 * <li>check if the Console View contains text "Receiving order order4.xml"</li>
	 * <li>check if the process is not suspended</li>
	 * <li>terminate process via Terminate button in the Console View</li>
	 * <li>check if the Console View contains text Disabling debugger</li>
	 * </ol>
	 */
	@Test
	public void testConditionalBreakpoints() {

		new CamelProject(PROJECT_NAME).openCamelContext(CAMEL_CONTEXT);
		CamelEditor editor = new CamelEditor(CAMEL_CONTEXT);
		editor.setConditionalBreakpoint(CHOICE, "simple", "${in.header.CamelFileName} == 'order2.xml'");
		editor.setConditionalBreakpoint(LOG, "simple", "${in.header.CamelFileName} == 'order1.xml'");
		ResumeButton resume = new ResumeButton();
		new CamelProject(PROJECT_NAME).debugCamelContextWithoutTests(CAMEL_CONTEXT);
		new WaitUntil(new LaunchIsSuspended(), TimePeriod.DEFAULT);

		// should stop on '_choice1' node
		VariablesView variables = new VariablesView();
		assertEquals(CHOICE_ID, variables.getValue("Endpoint"));
		assertTrue(resume.isEnabled());

		// all breakpoint should be processed
		resume.click();
		new WaitUntil(new IsRunning(), TimePeriod.DEFAULT);
		assertTrue(new ConsoleHasText("Receiving order order1.xml").test());
		assertTrue(new ConsoleHasText("Receiving order order2.xml").test());
		assertTrue(new ConsoleHasText("Receiving order order3.xml").test());
		assertTrue(new ConsoleHasText("Receiving order order4.xml").test());
		new TerminateButton().click();
		Assume.assumeFalse(testIssue2306());
		LogChecker.assertNoFuseError();
	}

	/**
	 * https://issues.jboss.org/browse/FUSETOOLS-2306
	 */
	private boolean testIssue2306() {
		List<LogMessage> errors = LogGrapper.getPluginErrors("fuse");
		if (errors.isEmpty()) {
			return false;
		}
		for (LogMessage msg : errors) {
			if (msg.getMessage().contains("Connection refused to host")) {
				return true;
			}
		}
		return false;
	}
}
