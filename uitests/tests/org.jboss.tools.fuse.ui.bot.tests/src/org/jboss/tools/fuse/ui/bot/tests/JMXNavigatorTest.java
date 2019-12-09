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

import static org.jboss.tools.fuse.reddeer.requirement.CamelExampleRunner.IS_RESUMED_PATTERN;
import static org.jboss.tools.fuse.reddeer.requirement.CamelExampleRunner.IS_SUSPENDED_PATTERN;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.StringJoiner;

import org.eclipse.reddeer.common.exception.WaitTimeoutExpiredException;
import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.junit.annotation.RequirementRestriction;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.requirement.matcher.RequirementMatcher;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.fuse.reddeer.LogGrapper;
import org.jboss.tools.fuse.reddeer.requirement.CamelExampleRequirement;
import org.jboss.tools.fuse.reddeer.requirement.CamelExampleRequirement.CamelExample;
import org.jboss.tools.fuse.reddeer.requirement.CamelExampleRequirementMatcher;
import org.jboss.tools.fuse.reddeer.requirement.CamelExampleRunner;
import org.jboss.tools.fuse.reddeer.view.FuseJMXNavigator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests <i>JMX Navigator</i> view that:
 * <ul>
 * <li>shows running processes (local context) correctly</li>
 * <li><i>Suspend</i>, <i>Resume</i> options work</li>
 * </ul>
 * 
 * This test requires an external Camel route specified by the requirement
 * restriction. The Camel route is started and stopped by the requirement logic.
 * 
 * @author tsedmik
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 * 
 */
@CamelExample
@RunWith(RedDeerSuite.class)
public class JMXNavigatorTest {

	private static Logger log = Logger.getLogger(JMXNavigatorTest.class);

	@InjectRequirement
	private CamelExampleRequirement camelExample;

	@RequirementRestriction
	public static RequirementMatcher getRequirementMatcher() {
		return new CamelExampleRequirementMatcher("camel-example-spring-boot", "2.20.1");
	}

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void prepareTestEnvironment() {
		log.info("Disable showing Error Log view after changes");
		LogView errorLog = new LogView();
		errorLog.open();
		errorLog.setActivateOnNewEvents(false);
	}

	/**
	 * Deletes logs in Error Log view
	 */
	@Before
	public void deleteErrorLog() {
		log.info("Deleting Error Log.");
		new LogView().deleteLog();
	}

	/**
	 * <p>
	 * Tests access to nodes relevant for Local Camel Context in JMX Navigator view.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>open JMX Navigator View</li>
	 * <li>try to access node "Local Camel Context", "Camel", "SampleCamel",
	 * "Endpoints", "stream", "out"</li>
	 * <li>try to access node "Local Camel Context", "Camel", "SampleCamel",
	 * "Endpoints", "timer", "hello?period=2000"</li>
	 * <li>try to access node "Local Camel Context", "Camel", "SampleCamel",
	 * "Routes", "route1", "timer:hello?period={{timer.period}}"</li>
	 * <li>check errors in Error Log</li>
	 * </ol>
	 */
	@Test
	public void testProcessesView() {
		String root = camelExample.getConfiguration().getJarFile().getName();
		assertJMXNode("Local Processes", root, "Camel", "SampleCamel", "Endpoints", "stream", "out");
		assertJMXNode("Local Processes", root, "Camel", "SampleCamel", "Endpoints", "timer", "hello?period=2000");
		assertJMXNode("Local Processes", root, "Camel", "SampleCamel", "Routes", "route1", "timer:hello?period={{timer.period}}",
				"Transform transform1", "stream:out");
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * <p>
	 * Tests context menu options related to Camel Context runs as Local Camel
	 * Context - Suspend/Resume context.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>open JMX Navigator View</li>
	 * <li>select node "Local Camel Context", "Camel", "SampleCamel"</li>
	 * <li>select the context menu option Suspend Camel Context</li>
	 * <li>check if the camel output contains the text "is suspended in"</li>
	 * <li>select node "Local Camel Context", "Camel", "SampleCamel"</li>
	 * <li>select the context menu option Resume Camel Context</li>
	 * <li>check if the camel output contains the text "resumed in"</li>
	 * </ol>
	 */
	@Test
	public void testContextOperations() {
		String root = camelExample.getConfiguration().getJarFile().getName();
		CamelExampleRunner runner = camelExample.getRunner();
		FuseJMXNavigator jmx = new FuseJMXNavigator();
		jmx.open();
		assertTrue("Suspension was not performed", jmx.suspendCamelContext("Local Processes", root, "Camel", "SampleCamel"));
		try {
			runner.waitForOutputWithPattern(IS_SUSPENDED_PATTERN);
		} catch (WaitTimeoutExpiredException e) {
			fail("Camel context was not suspended!\n" + runner.getOutput());
		}
		assertTrue("Resume of Camel Context was not performed", jmx.resumeCamelContext("Local Processes", root, "Camel", "SampleCamel"));
		try {
			runner.waitForOutputWithPattern(IS_RESUMED_PATTERN);
		} catch (WaitTimeoutExpiredException e) {
			fail("Camel context was not resumed!");
		}
		assertTrue("There are some errors in Error Log", LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	private void assertJMXNode(String... path) {
		StringJoiner msg = new StringJoiner("/");
		for (String part : path) {
			msg.add(part);
		}

		FuseJMXNavigator jmx = new FuseJMXNavigator();
		jmx.open();
		assertNotNull("The following path is inaccesible: " + msg.toString(), jmx.getNode(path));
	}
}
