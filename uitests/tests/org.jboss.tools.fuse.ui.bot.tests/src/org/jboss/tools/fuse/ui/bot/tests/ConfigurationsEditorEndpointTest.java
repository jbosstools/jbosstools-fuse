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

import static org.jboss.tools.fuse.reddeer.ProjectTemplate.CBR_SPRING;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.reddeer.common.condition.WaitCondition;
import org.eclipse.reddeer.common.util.XPathEvaluator;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanerrorlog.CleanErrorLogRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.condition.TreeHasItem;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.editor.CamelEndpointDialog;
import org.jboss.tools.fuse.reddeer.editor.ConfigurationsEditor;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.jboss.tools.fuse.reddeer.view.FusePropertiesView.DetailsProperty;
import org.jboss.tools.fuse.ui.bot.tests.utils.EditorManipulator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;
import org.xml.sax.SAXException;

/**
 * Tests for manipulation with 'Endpoints' in 'Configurations' tab in Camel editor
 * 
 * @author djelinek
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class ConfigurationsEditorEndpointTest {

	public static final String PROJECT_NAME = "cbr";
	public static final String CONTEXT = "camel-context.xml";
	public static final String ROOT = "Red Hat Fuse";

	private ConfigurationsEditor editor;
	private String endpoint;
	private String endpointName;
	private String[] path;

	private List<String> availableEndpoints = Arrays.asList(
			"Atom - The atom component is used for consuming Atom RSS feeds.",
			"Control Bus - The controlbus component provides easy management of Camel applications based on the Control Bus EIP pattern.",
			"CXF - The cxf component is used for SOAP WebServices using Apache CXF.",
			"CXF-RS - The cxfrs component is used for JAX-RS REST services using Apache CXF.",
			"Direct - The direct component provides direct, synchronous call to another endpoint from the same CamelContext.",
			"Direct VM - The direct-vm component provides direct, synchronous call to another endpoint from any CamelContext in the same JVM.",
			"EJB - The ejb component is for invoking EJB Java beans from Camel.",
			"File - The file component is used for reading or writing files.",
			"FTP - The ftp component is used for uploading or downloading files from FTP servers.",
			"FTPS - The ftps (FTP secure SSL/TLS) component is used for uploading or downloading files from FTP servers.",
			"IMAP - To send or receive emails using imap/pop3 or smtp protocols.",
			"IMAPS (Secure) - To send or receive emails using imap/pop3 or smtp protocols.",
			"JDBC - The jdbc component enables you to access databases through JDBC, where SQL queries are sent in the message body.",
			"JGroups - The jgroups component provides exchange of messages between Camel and JGroups clusters.",
			"JMS - The jms component allows messages to be sent to (or consumed from) a JMS Queue or Topic.",
			"Language - The language component allows you to send a message to an endpoint which executes a script by any of the supported Languages in Camel.",
			"Linkedin - The linkedin component is uses for retrieving LinkedIn user profiles connections companies groups posts etc.",
			"Mina2 - Socket level networking using TCP or UDP with the Apache Mina 2.x library.",
			"MQTT - Component for communicating with MQTT M2M message brokers using FuseSource MQTT Client.",
			"MVEL - Transforms the message using a MVEL template.",
			"Netty - Socket level networking using TCP or UDP with the Netty 3.x library.",
			"Netty HTTP - Netty HTTP server and client using the Netty 3.x library.",
			"Netty4 - Socket level networking using TCP or UDP with the Netty 4.x library.",
			"Netty4 HTTP - Netty HTTP server and client using the Netty 4.x library.",
			"POP3 - To send or receive emails using imap/pop3 or smtp protocols.",
			"POP3S - To send or receive emails using imap/pop3 or smtp protocols.",
			"Quartz - Provides a scheduled delivery of messages using the Quartz 1.x scheduler.",
			"Quartz2 - Provides a scheduled delivery of messages using the Quartz 2.x scheduler.",
			"Restlet - Component for consuming and producing Restful resources using Restlet.",
			"RSS - The rss component is used for consuming RSS feeds.",
			"Salesforce - The salesforce component is used for integrating Camel with the massive Salesforce API.",
			"SAP NetWeaver - The sap-netweaver component integrates with the SAP NetWeaver Gateway using HTTP transports.",
			"Scheduler - The scheduler component is used for generating message exchanges when a scheduler fires.",
			"SEDA - The seda component provides asynchronous call to another endpoint from any CamelContext in the same JVM.",
			"Servlet - To use a HTTP Servlet as entry for Camel routes when running in a servlet container.",
			"SFTP - The sftp (FTP over SSH) component is used for uploading or downloading files from SFTP servers.",
			"SMTP - To send or receive emails using imap/pop3 or smtp protocols.",
			"SMTPS - To send or receive emails using imap/pop3 or smtp protocols.",
			"SNMP - The snmp component gives you the ability to poll SNMP capable devices or receiving traps.",
			"SQL - The sql component allows you to work with databases using JDBC SQL queries.",
			"Timer - The timer component is used for generating message exchanges when a timer fires.",
			"VM - The vm component provides asynchronous call to another endpoint from the same CamelContext.",
			"XQuery - Transforms the message using a XQuery template using Saxon.",
			"XSLT - Transforms the message using a XSLT template.");

	/**
	 * Sets parameters for parameterized test
	 * 
	 * @return List of all available Global elements - Endpoints
	 */
	@Parameters
	public static Collection<String> setupData() {
		createProject();
		new CamelProject(PROJECT_NAME).openCamelContext(CONTEXT);
		CamelEditor.switchTab(CamelEditor.CONFIGURATIONS_TAB);
		return ConfigurationsEditor.getEndpoints();
	}

	public ConfigurationsEditorEndpointTest(String endpoint) {
		this.endpoint = endpoint;
		this.endpointName = getLabel(endpoint);
		path = new String[] { ROOT, this.endpointName, " (Endpoint)" };
	}

	@BeforeClass
	public static void setupResetCamelContext() {
		new WorkbenchShell().maximize();

		createProject();
	}

	@Before
	public void initialSetup() {
		new CleanErrorLogRequirement().fulfill();
		new CamelProject(PROJECT_NAME).openCamelContext(CONTEXT);
		CamelEditor.switchTab(CamelEditor.CONFIGURATIONS_TAB);
	}

	@After
	public void clearEnviroment() {
		new CamelProject(PROJECT_NAME).openCamelContext(CONTEXT);
		CamelEditor.switchTab(CamelEditor.SOURCE_TAB);
		EditorManipulator.copyFileContentToCamelXMLEditor("resources/camel-context-cbr.xml");
	}

	@AfterClass
	public static void setupDeleteProjects() {
		new CleanWorkspaceRequirement().fulfill();
	}

	/**
	 * <p>
	 * Test verify that all of Endpoints are available in CamelEndpoint dialog.
	 * </p>
	 */
	@Test
	public void testEndpointAvailability() {
		assertTrue(endpoint, availableEndpoints.contains(endpoint));
	}

	/**
	 * <p>
	 * Test tries create <i>Endpoint</i> global element inside Camel Editor in configurations tab
	 * </p>
	 * <ol>
	 * <li>create a new project with Spring-DSL project type and Content Based Router template</li>
	 * <li>open Project Explorer view</li>
	 * <li>open camel-context.xml file</li>
	 * <li>create <i>Endpoint</i> component</li>
	 * <li>check that <i>Endpoint</i> component was created</li>
	 * </ol>
	 */
	@Test
	public void testCreateEndpoint() {
		createEndpoint();
		assertEndpointPath(true);
	}

	/**
	 * <p>
	 * Test tries edit <i>Endpoint</i> global element inside Camel Editor in configurations tab
	 * </p>
	 * <ol>
	 * <li>create a new project with Spring-DSL project type and Content Based Router template</li>
	 * <li>open Project Explorer view</li>
	 * <li>open camel-context.xml file</li>
	 * <li>create <i>Endpoint</i> component</li>
	 * <li>edit all details properties of <i>Endpoint</i> component</li>
	 * <li>check that <i>Endpoint</i> component was edited</li>
	 * </ol>
	 */
	@Test
	public void testEditEndpoint() {
		createEndpoint();
		editor.editEndpoint(endpointName).setDetailsProperty(DetailsProperty.ID, "new_" + endpointName);
		path = new String[] { ROOT, "new_" + endpointName + " (Endpoint)" };
		assertEndpointPath(true);
	}

	/**
	 * <p>
	 * Test tries delete <i>Endpoint</i> global element inside Camel Editor in configurations tab
	 * </p>
	 * <ol>
	 * <li>create a new project with Spring-DSL project type and Content Based Router template</li>
	 * <li>open Project Explorer view</li>
	 * <li>open camel-context.xml file</li>
	 * <li>creates <i>Endpoint</i> component</li>
	 * <li>delete <i>Endpoint</i> component</li>
	 * <li>check that <i>Endpoint</i> component was deleted</li>
	 * </ol>
	 */
	@Test
	public void testDeleteEndpoint() {
		createEndpoint();
		assertEndpointPath(true);
		editor.deleteEndpoint(endpointName);
		assertEndpointPath(false);
	}

	/**
	 * <p>
	 * Test tries create and delete <i>Endpoint</i> global element and checks Source XML
	 * </p>
	 * <ol>
	 * <li>create a new project with Spring-DSL project type and Content Based Router template</li>
	 * <li>open Project Explorer view</li>
	 * <li>open camel-context.xml file</li>
	 * <li>create of <i>Endpoint</i> component</li>
	 * <li>checks source XML that <i>Endpoint</i> component was created</li>
	 * <li>delete <i>Endpoint</i> component</li>
	 * <li>checks source XML that <i>Endpoint</i> component was deleted</li>
	 * </ol>
	 */
	@Test
	public void testSourceXML() {
		createEndpoint();
		editor.close(true);
		assertXPath(true, endpointName);
		new CamelProject(PROJECT_NAME).openCamelContext(CONTEXT);
		editor = new ConfigurationsEditor();
		editor.deleteEndpoint(endpointName);
		editor.close(true);
		assertXPath(false, endpointName);
	}

	private void assertXPath(boolean expected, String name) {
		String result;
		try {
			XPathEvaluator eval = new XPathEvaluator(editor.getAssociatedFile().getInputStream(), false);
			result = eval.evaluateXPath("/beans/camelContext/endpoint/@id");
		} catch (XPathExpressionException | IOException | SAXException | ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		assertEquals("Endpoint - " + name, expected, result.equals(name));
	}

	private String getLabel(String row) {
		return row.split("\\s")[0].toLowerCase();
	}

	private static void createProject() {
		ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(KARAF).template(CBR_SPRING).create();
	}

	private void createEndpoint() {
		editor = new ConfigurationsEditor();
		CamelEndpointDialog wizard = editor.addEndpoint();
		wizard.setCamelComponent(endpoint);
		wizard.setId(endpointName);
		wizard.finish();
	}

	private void assertEndpointPath(boolean expected) {
		WaitCondition wait = new TreeHasItem(new DefaultTree(editor), path);
		new WaitUntil(wait, TimePeriod.MEDIUM, false);
		if (wait.getResult() != null) {
			assertEquals("Endpoint - " + endpointName, expected, wait.getResult());
		}
	}

}
