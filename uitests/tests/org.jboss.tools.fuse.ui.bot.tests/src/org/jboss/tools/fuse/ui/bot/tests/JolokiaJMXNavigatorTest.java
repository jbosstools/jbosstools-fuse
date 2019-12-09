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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.StringJoiner;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.junit.annotation.RequirementRestriction;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.requirement.matcher.RequirementMatcher;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.tools.fuse.reddeer.LogGrapper;
import org.jboss.tools.fuse.reddeer.condition.JMXConnectionIsAvailable;
import org.jboss.tools.fuse.reddeer.requirement.CamelExampleRequirement;
import org.jboss.tools.fuse.reddeer.requirement.CamelExampleRequirementMatcher;
import org.jboss.tools.fuse.reddeer.requirement.JolokiaConfiguration;
import org.jboss.tools.fuse.reddeer.requirement.CamelExampleRequirement.CamelExample;
import org.jboss.tools.fuse.reddeer.view.FuseJMXNavigator;
import org.jboss.tools.fuse.reddeer.wizard.JMXNewConnectionWizard;
import org.jboss.tools.fuse.reddeer.wizard.JMXNewConnectionWizard.ConnectionType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This test requires an external Camel route specified by the requirement
 * restriction. The Camel route is started and stopped by the requirement logic.
 * 
 * @author djelinek
 *
 */
@CleanWorkspace
@CamelExample(useJolokia = true)
@RunWith(RedDeerSuite.class)
public class JolokiaJMXNavigatorTest {

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
	@Before
	public void defaultSetup() {
		log.info("Deleting Error Log.");
		LogView errorLog = new LogView();
		errorLog.open();
		errorLog.deleteLog();
	}

	/**
	 * <p>
	 * Test tries to create new <i>Jolokia connection</i> in JMX Navigator and access route nodes.
	 * </p>
	 * <ul>
	 * <li>Create a new connection in JMX Navigator</li>
	 * <li>New connection... --> <i>Jolokia Connection</i></li>
	 * <li>Provide the name you want</li>
	 * <li>Use Jolokia URL from Console View (by default <i>http://127.0.0.1:8778/jolokia</i>)</li>
	 * <li>Select <i>Do NOT verify SSL Certificates</i></li>
	 * <li>Click Finish</li>
	 * <li>Expand User-Defined Connections --> double-click on the connection to activate it</li>
	 * <li>Try to work with it (accessing nodes, perform available operations, ...)</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJolokiaAcccessingCamelRoute() throws Exception {

		JolokiaConfiguration conf = camelExample.getConfiguration().getJolokiaConfiguration();

		log.info("Try to open/activate 'JMX Navigator' view");
		FuseJMXNavigator jmxView = new FuseJMXNavigator();
		jmxView.open();
		jmxView.activate();
		jmxView.clickNewConnection();

		log.info("Try to create a new jolokia connection");
		JMXNewConnectionWizard connectionWizard = new JMXNewConnectionWizard();
		assertTrue("Wizard 'Create JMX connection' was not opened properly", connectionWizard.isOpen());
		connectionWizard.selectConnection(ConnectionType.JOLOKIA);
		connectionWizard.next();
		connectionWizard.setTextConnectionName(conf.getName());
		connectionWizard.setTextJolokiaURL(conf.getUrl());	
		if(!conf.isVerifySSL())
			connectionWizard.toggleDoNOTVerifySSLCertificatesCHB(true);
		connectionWizard.finish();

		String connectionName = conf.getName();
		log.info("Try to connect to the new jolokia connection");
		jmxView.connectTo("User-Defined Connections", connectionName);
		new WaitUntil(new JMXConnectionIsAvailable("User-Defined Connections", connectionName + "[Connected]"));

		log.info("Try to access route components via new Jolokia connection");
		assertJMXNode("User-Defined Connections", connectionName, "MBeans", "java.util.logging", "Logging", "LoggerNames");
		jmxView.collapseAll();
		assertJMXNode("User-Defined Connections", connectionName, "Camel", "SampleCamel", "Endpoints", "stream", "out");
		jmxView.collapseAll();
		assertJMXNode("User-Defined Connections", connectionName, "Camel", "SampleCamel", "Routes", "route1",
						"timer:hello?period={{timer.period}}", "Transform transform1", "stream:out");
		jmxView.collapseAll();
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
