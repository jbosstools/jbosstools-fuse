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

import static org.jboss.tools.fuse.qe.reddeer.SupportedCamelVersions.CAMEL_2_17_0_REDHAT_630187;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.eclipse.condition.ConsoleHasText;
import org.jboss.reddeer.eclipse.debug.core.Breakpoint;
import org.jboss.reddeer.eclipse.debug.core.BreakpointsView;
import org.jboss.reddeer.eclipse.debug.core.IsSuspended;
import org.jboss.reddeer.eclipse.debug.core.ResumeButton;
import org.jboss.reddeer.eclipse.debug.core.TerminateButton;
import org.jboss.reddeer.eclipse.debug.core.VariablesView;
import org.jboss.reddeer.eclipse.ui.perspectives.JavaEEPerspective;
import org.jboss.reddeer.eclipse.ui.views.log.LogMessage;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.jboss.reddeer.swt.impl.tree.DefaultTree;
import org.jboss.tools.fuse.qe.reddeer.JiraIssue;
import org.jboss.tools.fuse.qe.reddeer.LogGrapper;
import org.jboss.tools.fuse.qe.reddeer.ProjectTemplate;
import org.jboss.tools.fuse.qe.reddeer.ProjectType;
import org.jboss.tools.fuse.qe.reddeer.debug.IsRunning;
import org.jboss.tools.fuse.qe.reddeer.debug.StepOverButton;
import org.jboss.tools.fuse.qe.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.qe.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.junit.After;
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

	private static final String PROJECT_NAME = "cbr @1";
	private static final String CAMEL_CONTEXT = "camel-context.xml";
	private static final String CHOICE = "Choice";
	private static final String CHOICE_ID = "_choice1";
	private static final String LOG = "Log _log2";
	private static final String LOG_ID = "_log2";

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void setupInitial() {

		ProjectFactory.newProject(PROJECT_NAME).version(CAMEL_2_17_0_REDHAT_630187).template(ProjectTemplate.CBR).type(ProjectType.SPRING).create();
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
		assertTrue(LogGrapper.getPluginErrors("fuse").size() == 0);
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
	 * <li>check if the Variables View contains variable Message with value <name>Antwerp Zoo</name></li>
	 * <li>resume debugging</li>
	 * <li>wait until process is suspended</li>
	 * <li>check if the Variables View contains variable Endpoint with value log4</li>
	 * <li>click on the Step over button</li>
	 * <li>wait until process is suspended</li>
	 * <li>check if the Console View contains text "Sending order order1.xml to another country"</li>
	 * <li>check if the Variables View contains variable Endpoint with value to3</li>
	 * <li>remove all breakpoints</li>
	 * <li>check if the Console View contains text Removing breakpoint choice1</li>
	 * <li>check if the Console View contains text Removing breakpoint log1</li>
	 * <li>resume debugging</li>
	 * <li>terminate process via Terminate button in the Console View</li>
	 * </ol>
	 */
	@Test
	public void testDebugger() {

		new CamelProject(PROJECT_NAME).openCamelContext(CAMEL_CONTEXT);
		CamelEditor editor = new CamelEditor(CAMEL_CONTEXT);
		editor.setBreakpoint(CHOICE);
		editor.setBreakpoint(LOG);
		new CamelProject(PROJECT_NAME).debugCamelContextWithoutTests(CAMEL_CONTEXT);

		// should stop on the 'choice1' node
		new WaitUntil(new IsSuspended(), TimePeriod.NORMAL);
		assertTrue(new ConsoleHasText("Enabling debugger").test());
		VariablesView variables = new VariablesView();
		AbstractWait.sleep(TimePeriod.getCustom(5));
		assertEquals(CHOICE_ID, variables.getValue("Endpoint"));

		// get body of message
		variables.close();
		AbstractWait.sleep(TimePeriod.SHORT);
		variables.open();
		new DefaultTree().getItems().get(4).getItems().get(0).select();
		assertTrue(new DefaultStyledText().getText().contains("<name>Bristol Zoo Gardens</name>"));

		// resume and then should stop on the 'log1' node
		ResumeButton resume = new ResumeButton();
		assertTrue(resume.isEnabled());
		resume.click();
		new WaitUntil(new IsSuspended(), TimePeriod.NORMAL);
		AbstractWait.sleep(TimePeriod.getCustom(2));
		assertEquals(LOG_ID, variables.getValue("Endpoint"));

		// step over then should stop on the 'to1' endpoint
		assertTrue(resume.isEnabled());
		new StepOverButton().select();
		new WaitUntil(new IsSuspended(), TimePeriod.NORMAL);
		assertTrue(new ConsoleHasText("Sending order order2.xml to the UK").test());
		assertTrue(resume.isEnabled());
		AbstractWait.sleep(TimePeriod.getCustom(5));
		assertEquals("_to1", variables.getValue("Endpoint"));

		// remove all breakpoints
		new BreakpointsView().removeAllBreakpoints();
		assertTrue(new ConsoleHasText("Removing breakpoint _choice1").test());
		assertTrue(new ConsoleHasText("Removing breakpoint _log2").test());
		resume.click();

		// all breakpoints should be processed
		new WaitUntil(new IsRunning(), TimePeriod.NORMAL);
		new TerminateButton().click();
		testIssue2306();
		assertTrue(LogGrapper.getPluginErrors("fuse").size() == 0);
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
	 * <li>set conditional breakpoint to the choice component - simple with
	 * "${in.header.CamelFileName} == 'order1.xml'"</li>
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
		new WaitUntil(new IsSuspended(), TimePeriod.NORMAL);

		// should stop on '_choice1' node
		VariablesView variables = new VariablesView();
		assertEquals(CHOICE_ID, variables.getValue("Endpoint"));
		assertTrue(resume.isEnabled());

		// all breakpoint should be processed
		resume.click();
		new WaitUntil(new IsRunning(), TimePeriod.NORMAL);
		assertTrue(new ConsoleHasText("Receiving order order1.xml").test());
		assertTrue(new ConsoleHasText("Receiving order order2.xml").test());
		assertTrue(new ConsoleHasText("Receiving order order3.xml").test());
		assertTrue(new ConsoleHasText("Receiving order order4.xml").test());
		new TerminateButton().click();
		testIssue2306();
		assertTrue(LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * https://issues.jboss.org/browse/FUSETOOLS-2306
	 */
	private void testIssue2306() {
		List<LogMessage> errors = LogGrapper.getPluginErrors("fuse");
		if (errors.isEmpty()) {
			return;
		}
		for (LogMessage msg : errors) {
			if (msg.getMessage().contains("Connection refused to host")) {
				throw new JiraIssue("FUSETOOLS-2306");
			}
		}
	}
}
