/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.ui.bot.tests;

import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.eclipse.reddeer.common.condition.WaitCondition;
import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.LaunchIsSuspended;
import org.eclipse.reddeer.eclipse.debug.ui.views.breakpoints.BreakpointsView;
import org.eclipse.reddeer.eclipse.debug.ui.views.launch.ResumeButton;
import org.eclipse.reddeer.eclipse.ui.views.log.LogMessage;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.junit.annotation.RequirementRestriction;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.requirement.matcher.RequirementMatcher;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.api.Shell;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.workbench.handler.EditorHandler;
import org.jboss.tools.fuse.reddeer.JiraIssue;
import org.jboss.tools.fuse.reddeer.LogGrapper;
import org.jboss.tools.fuse.reddeer.condition.JMXConnectionIsAvailable;
import org.jboss.tools.fuse.reddeer.debug.IsRunning;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.preference.ConsolePreferenceUtil;
import org.jboss.tools.fuse.reddeer.requirement.CamelExampleRequirement;
import org.jboss.tools.fuse.reddeer.requirement.CamelExampleRequirementMatcher;
import org.jboss.tools.fuse.reddeer.requirement.JolokiaConfiguration;
import org.jboss.tools.fuse.reddeer.requirement.CamelExampleRequirement.CamelExample;
import org.jboss.tools.fuse.reddeer.utils.LogChecker;
import org.jboss.tools.fuse.reddeer.view.FuseJMXNavigator;
import org.jboss.tools.fuse.reddeer.view.FusePropertiesView;
import org.jboss.tools.fuse.reddeer.view.FusePropertiesView.PropertyType;
import org.jboss.tools.fuse.reddeer.view.MessagesView;
import org.jboss.tools.fuse.reddeer.wizard.JMXNewConnectionWizard;
import org.jboss.tools.fuse.reddeer.wizard.JMXNewConnectionWizard.ConnectionType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tries to manipulate with Camel route via Jolokia connection - editing, tracing, debugging.
 * 
 * This test requires an external Camel route specified by the requirement restriction. The Camel route is started and
 * stopped by the requirement logic.
 * 
 * @author djelinek
 */
@CleanWorkspace
@CamelExample(useJolokia = true)
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class JolokiaManipulationTest {

	private static Logger log = Logger.getLogger(JolokiaManipulationTest.class);

	private static JolokiaConfiguration conf;

	public static final String MESSAGE = "Test edit route";

	private static LogView errorLog = new LogView();

	private MessagesView messageView = new MessagesView();

	private FuseJMXNavigator jmxView = new FuseJMXNavigator();

	private FusePropertiesView propertiesView = new FusePropertiesView();

	@InjectRequirement
	private static CamelExampleRequirement camelExample;

	@RequirementRestriction
	public static RequirementMatcher getRequirementMatcher() {
		return new CamelExampleRequirementMatcher("camel-example-spring-boot", "2.20.1");
	}

	@BeforeClass
	public static void prepareVariables() {
		errorLog.open();
		errorLog.setActivateOnNewEvents(false);
		ConsolePreferenceUtil.setConsoleOpenOnError(false);
		ConsolePreferenceUtil.setConsoleOpenOnOutput(false);
		conf = camelExample.getConfiguration().getJolokiaConfiguration();
	}

	@Before
	public void prepareEnvironment() throws Exception {
		errorLog.activate();
		errorLog.deleteLog();
		createJolokiaConnection();
		connect();
	}

	@After
	public void cleanEnvironment() {
		EditorHandler.getInstance().closeAll(true);
		deleteConnection();
		camelExample.cleanUp();
		camelExample.fulfill();
	}

	/**
	 * <p>
	 * Test tries to edit running route via Jolokia connection
	 * </p>
	 * <ul>
	 * <li>Create a new connection in JMX Navigator</li>
	 * <li>New connection... --> <i>Jolokia Connection</i></li>
	 * <li>Provide the name you want</li>
	 * <li>Use Jolokia URL from Console View (by default <i>http://127.0.0.1:8778/jolokia</i>)</li>
	 * <li>Select <i>Do NOT verify SSL Certificates</i></li>
	 * <li>Click Finish</li>
	 * <li>Connect to new <i>Jolokia</i> connection</li>
	 * <li>Verify that route is running and generates message <i>Hello world</i></li>
	 * <li>Select route, open context menu and click on <i>Edit routes</i></li>
	 * <li>Edit running route (change of generating message)</li>
	 * <li>Verify that message was properly changed</li>
	 * <li>Verify Error log does not contains any <i>Fuse</i> errors</li>
	 * </ul>
	 */
	@Test
	public void testEditingRoute() throws Exception {

		activateRouteEditing();

		// Activate editor and edit route
		CamelEditor editor = new CamelEditor("<connected>Remote CamelContext: SampleCamel");
		editor.activate();
		editor.clickOnEditPart("timer:hello?period={{timer.period}}");
		propertiesView.open();
		propertiesView.selectTab("Advanced");
		propertiesView.switchPropertiesTab("Consumer");
		propertiesView.setProperty(PropertyType.TEXT, "Period", "1000");
		editor.save();

		editor.activate();
		editor.clickOnEditPart("Transform transform1");
		propertiesView.activate();
		propertiesView.setProperty(PropertyType.COMBO, "Expression", "simple");
		propertiesView.setProperty(PropertyType.TEXT, "Expression", MESSAGE);
		editor.save();

		Pattern p = Pattern.compile("^" + MESSAGE);
		camelExample.getRunner().registerPattern(p);
		camelExample.getRunner().waitForOutputWithPattern(p);

		assertTrue("Message 'Hello world' was not changed to '" + MESSAGE + "' correctly",
				camelExample.getRunner().getOutput().toString().contains(MESSAGE));

		testIssue2853();
		LogChecker.assertNoFuseError();
	}

	/**
	 * <p>
	 * Test tries to start tracing route via Jolokia connection
	 * </p>
	 * <ul>
	 * <li>Create a new connection in JMX Navigator</li>
	 * <li>New connection... --> <i>Jolokia Connection</i></li>
	 * <li>Provide the name you want</li>
	 * <li>Use Jolokia URL from Console View (by default <i>http://127.0.0.1:8778/jolokia</i>)</li>
	 * <li>Select <i>Do NOT verify SSL Certificates</i></li>
	 * <li>Click Finish</li>
	 * <li>Connect to new <i>Jolokia</i> connection</li>
	 * <li>Select route, open context menu and click on <i>Start Tracing</i></li>
	 * <li>Wait short time period and then select route, open context menu and click on <i>Stop Tracing Context</i></li>
	 * <li>Open <p>Message view</p></li>
	 * <li>Verifies Message view contains <p>Hello World</p> messages</li>
	 * <li>Verify Error log does not contains any <i>Fuse</i> errors</li>
	 * </ul>
	 */
	@Test
	public void testTracingRoute() {

		TreeItem item = jmxView.getNode("User-Defined Connections", conf.getName(), "Camel", "SampleCamel");
		item.select();
		new ContextMenuItem(item, "Start Tracing").select();
		AbstractWait.sleep(TimePeriod.MEDIUM);
		jmxView.activate();
		item.select();
		new ContextMenuItem(item, "Stop Tracing Context").select();
		
		messageView.open();
		assertTrue("Message view does not contains 'Hello World' message",
				messageView.containsMessageBody("Hello World"));
		LogChecker.assertNoFuseError();
	}

	/**
	 * <p>
	 * Test tries to start tracing route via Jolokia connection
	 * </p>
	 * <ul>
	 * <li>Create a new connection in JMX Navigator</li>
	 * <li>New connection... --> <i>Jolokia Connection</i></li>
	 * <li>Provide the name you want</li>
	 * <li>Use Jolokia URL from Console View (by default <i>http://127.0.0.1:8778/jolokia</i>)</li>
	 * <li>Select <i>Do NOT verify SSL Certificates</i></li>
	 * <li>Click Finish</li>
	 * <li>Connect to new <i>Jolokia</i> connection</li>
	 * <li>Set breakpoint to <p>Transform transform1</p> component and wait until debugger is enabled</li>
	 * <li>Verify route was stopped</li>
	 * <li>Click resume (route should have again stops on breakpoint)</li>
	 * <li>Remove breakpoint</li>
	 * <li>Click resume (route should have continue)</li>
	 * <li>Verify Error log does not contains any <i>Fuse</i> errors</li>
	 * </ul>
	 */
	@Test
	public void testDebuggingRoute() {

		activateRouteEditing();

		// Activate editor and set breakpoint
		CamelEditor editor = new CamelEditor("<connected>Remote CamelContext: SampleCamel");
		editor.activate();
		editor.setBreakpoint("Transform transform1");

		WaitCondition rememberCondition = new ShellIsAvailable("Confirm Perspective Switch");
		new WaitUntil(rememberCondition, TimePeriod.MEDIUM, false);
		if (rememberCondition.getResult() != null) {
			Shell shell = new DefaultShell((org.eclipse.swt.widgets.Shell) rememberCondition.getResult());
			new CheckBox(shell, "Remember my decision").click();
			new PushButton(shell, "No").click();
		}

		new WaitUntil(new LaunchIsSuspended(), TimePeriod.MEDIUM);
		String consoleOutput = camelExample.getRunner().getOutput().toString();
		assertTrue(consoleOutput.contains("Enabling debugger"));
		assertTrue(consoleOutput.contains("Adding breakpoint transform1"));

		// Resume debugger
		ResumeButton resume = new ResumeButton();
		assertTrue("resume button is disabled", resume.isEnabled());
		resume.click();
		new WaitUntil(new LaunchIsSuspended(), TimePeriod.MEDIUM);

		// Remove all breakpoints
		new BreakpointsView().removeAllBreakpoints();
		resume.click();
		new WaitUntil(new IsRunning(), TimePeriod.MEDIUM);

		LogChecker.assertNoFuseError();
	}

	/**
	 * Opens JMX Navigator view and creates new Jolokia connection
	 * 
	 * @throws Exception
	 */
	private void createJolokiaConnection() throws Exception {
		jmxView.open();
		jmxView.activate();
		jmxView.clickNewConnection();
		log.info("Try to create a new jolokia connection");
		JMXNewConnectionWizard connectionWizard = new JMXNewConnectionWizard();
		connectionWizard.selectConnection(ConnectionType.JOLOKIA);
		connectionWizard.next();
		connectionWizard.setTextConnectionName(conf.getName());
		connectionWizard.setTextJolokiaURL(conf.getUrl());
		if (!conf.isVerifySSL())
			connectionWizard.toggleDoNOTVerifySSLCertificatesCHB(true);
		connectionWizard.finish();
	}

	/**
	 * Opens JMX Navigator view and delete Jolokia connection
	 */
	private void deleteConnection() {
		jmxView.open();
		TreeItem item = jmxView.getNode("User-Defined Connections", conf.getName());
		item.select();
		new ContextMenuItem(item, "Delete Connection").select();
	}

	/**
	 * Connect to new Jolokia connection
	 */
	private void connect() {
		String connectionName = conf.getName();
		jmxView.connectTo("User-Defined Connections", connectionName);
		new WaitUntil(new JMXConnectionIsAvailable("User-Defined Connections", connectionName));
		Pattern p = Pattern.compile("^Hello World");
		camelExample.getRunner().registerPattern(p);
		camelExample.getRunner().waitForOutputWithPattern(p);
	}

	/**
	 * Select running route
	 */
	private void activateRouteEditing() {
		TreeItem item = jmxView.getNode("User-Defined Connections", conf.getName(), "Camel", "SampleCamel");
		item.select();
		new ContextMenuItem(item, "Edit Routes").select();
		WaitCondition saveCondition = new ShellIsAvailable("Save Resource");
		new WaitUntil(saveCondition, TimePeriod.MEDIUM, false);
		if (saveCondition.getResult() != null) {
			Shell shell = new DefaultShell((org.eclipse.swt.widgets.Shell) saveCondition.getResult());
			new PushButton(shell, "Save").click();
		}
	}
	
	/**
	 * https://issues.jboss.org/browse/FUSETOOLS-2853
	 */
	private void testIssue2853() {
		if(LogChecker.noFuseError())
			return;
		for (LogMessage msg : LogGrapper.getPluginErrors("fuse")) {
			if (msg.getMessage().contains("<no message>") || msg.getMessage().contains("Resource '/.FuseRemoteCamelContextData/pom.xml' does not exist.")) {
				throw new JiraIssue("FUSETOOLS-2853");
			}
		}
	}

}
