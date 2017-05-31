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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.ui.IViewReference;
import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.core.lookup.WorkbenchPartLookup;
import org.jboss.reddeer.core.util.Display;
import org.jboss.reddeer.eclipse.ui.perspectives.JavaEEPerspective;
import org.jboss.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.workbench.handler.EditorHandler;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.qe.reddeer.ProjectTemplate;
import org.jboss.tools.fuse.qe.reddeer.ProjectType;
import org.jboss.tools.fuse.qe.reddeer.SupportedCamelVersions;
import org.jboss.tools.fuse.qe.reddeer.XPathEvaluator;
import org.jboss.tools.fuse.qe.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.qe.reddeer.editor.CamelEndpointDialog;
import org.jboss.tools.fuse.qe.reddeer.editor.ConfigurationsEditor;
import org.jboss.tools.fuse.qe.reddeer.editor.ConfigurationsEditor.Element;
import org.jboss.tools.fuse.qe.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.EditorManipulator;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.view.FusePropertiesView;
import org.jboss.tools.fuse.qe.reddeer.view.FusePropertiesView.DetailsProperty;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Tests for global elements - Endpoints in Configurations Tab in Camel editor
 *  
 * @author djelinek
 */
@CleanWorkspace
@OpenPerspective(JavaEEPerspective.class)
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class ConfigurationsEditorEndpointsTest extends DefaultTest {

	protected Logger log = Logger.getLogger(ConfigurationsEditorEndpointsTest.class);
	
	private static final String PROJECT_NAME = "cbr";
	private static final String CONTEXT = "camel-context.xml";
	private static final String TYPE = "JBoss Fuse";
	
	private String element; 
	private List<String> availableEndpoints = Arrays.asList("Atom - The atom component is used for consuming Atom RSS feeds.", 
														    "Control Bus - The controlbus component provides easy management of Camel applications based on the Control Bus EIP pattern.",
														    "CXF - The cxf component is used for SOAP WebServices using Apache CXF.",
														    "CXF-RS - The cxfrs component is used for JAX-RS REST services using Apache CXF.",
														    "Direct - The direct component provides direct synchronous call to another endpoint from the same CamelContext.",
														    "Direct VM - The direct-vm component provides direct synchronous call to another endpoint from any CamelContext in the same JVM.",
														    "EJB - The ejb component is for invoking EJB Java beans from Camel.",
														    "File - The file component is used for reading or writing files.",
														    "FTP - The ftp component is used for uploading or downloading files from FTP servers.",
														    "FTPS - The ftps (FTP secure SSL/TLS) component is used for uploading or downloading files from FTP servers.",
														    "IMAP - To send or receive emails using imap/pop3 or stmp protocols.",
														    "IMAPS - To send or receive emails using imap/pop3 or stmp protocols.",
														    "JDBC - The jdbc component enables you to access databases through JDBC where SQL queries are sent in the message body.",
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
														    "POP3 - To send or receive emails using imap/pop3 or stmp protocols.",
														    "POP3S - To send or receive emails using imap/pop3 or stmp protocols.",
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
														    "SMTP - To send or receive emails using imap/pop3 or stmp protocols.",
														    "SMTPS - To send or receive emails using imap/pop3 or stmp protocols.",
														    "SNMP - The snmp component gives you the ability to poll SNMP capable devices or receiving traps.",
														    "SQL - The sql component can be used to perform SQL query to a database.",
														    "Timer - The timer component is used for generating message exchanges when a timer fires.",
														    "VM - The vm component provides asynchronous call to another endpoint from the same CamelContext.",
														    "XQuery - Transforms the message using a XQuery template using Saxon.",	
														    "XSLT - Transforms the message using a XSLT template."	);
	
	/**
	 * Sets parameters for parameterized test
	 * 
	 * @return List of all available Global elements - Endpoints
	 */
	@Parameters
	public static Collection<String> setupData() {
		
		new WorkbenchShell();	
		ProjectFactory.newProject(PROJECT_NAME).version(SupportedCamelVersions.CAMEL_2_17_0_REDHAT_630187).template(ProjectTemplate.CBR).type(ProjectType.SPRING).create();		
		for (IViewReference viewReference : WorkbenchPartLookup.getInstance().findAllViewReferences()) {
			if (viewReference.getPartName().equals("Welcome")) {
				final IViewReference iViewReference = viewReference;
				Display.syncExec(new Runnable() {
					@Override
					public void run() {
						iViewReference.getPage().hideView(iViewReference);
					}
				});
				break;
			}
		}
		
		new CamelProject(PROJECT_NAME).openCamelContext(CONTEXT);	
		List<String> endpoints = CamelEndpointDialog.getEndpoints();
		return endpoints;
	}

	/**
	 * Utilizes passing parameters using the constructor
	 * 
	 * @param template
	 *            a Global element - Endpoint
	 */
	public ConfigurationsEditorEndpointsTest(String element) {
		this.element = element;
	}
	
	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void setupResetCamelContext() {
		
		ProjectFactory.newProject(PROJECT_NAME).version(SupportedCamelVersions.CAMEL_2_17_0_REDHAT_630187).template(ProjectTemplate.CBR).type(ProjectType.SPRING).create();
	}
	
	@Before
	public void initialSetup() {
		
		new CamelProject(PROJECT_NAME).openCamelContext(CONTEXT);
		//new CamelEditor("camel-context.xml").activate();
		CamelEditor.switchTab("Configurations");
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
	
	@After
	public void clearEnviroment() {
		
		//new CamelEditor("camel-context.xml").activate();
		CamelEditor.switchTab("Source");
		EditorManipulator.copyFileContentToCamelXMLEditor("resources/camel-context-cbr.xml");
	}
	
	/**
	 * <p>
	 * Test verify that all of Endpoints are available in CamelEndpoint dialog.
	 * </p>
	 */
	@Test
	public void testEndpointAvailability() {
		
		assertTrue(element, availableEndpoints.contains(element));
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
		
		try {
			ConfigurationsEditor confEditor = new ConfigurationsEditor(PROJECT_NAME, CONTEXT);	
			confEditor.activate();
			String[] title = element.split(" ");
			confEditor.createNewGlobalEndpoint(title[0], element);	
			new DefaultTreeItem(new String[] { TYPE, title[0] + " (Endpoint)" }).select();
		} catch (Exception e) {
			fail(element);
		}				
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
	 * <li>edit all details properties  of <i>Endpoint</i> component</li>
	 * <li>check that <i>Endpoint</i> component was edited</li>
	 * </ol>
	 */
	@Test
	public void testEditEndpoint()	{
		
		try {
			ConfigurationsEditor confEditor = new ConfigurationsEditor(PROJECT_NAME, CONTEXT);
			confEditor.activate();
			String[] title = element.split(" ");
			confEditor.createNewGlobalEndpoint(title[0], element);	
			confEditor.editGlobalEndpoint(title[0]);
			FusePropertiesView view = new FusePropertiesView();
			view.activate();
			view.setDetailsProperty(DetailsProperty.URI, "changedUri");
			view.setDetailsProperty(DetailsProperty.DESC, "changedDesc");
			view.setDetailsProperty(DetailsProperty.ID, "changed" + title[0]);
			view.setDetailsProperty(DetailsProperty.PATTERN, null);
			view.setDetailsProperty(DetailsProperty.REF, null);
			confEditor.activate();
			new DefaultTreeItem(new String[] { TYPE, "changed" + title[0] + " (Endpoint)" }).select();
		} catch (Exception e) {
			fail(element);
		}
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
	public void testDeleteEndpoint()	{
		
		try {
			ConfigurationsEditor confEditor = new ConfigurationsEditor(PROJECT_NAME, CONTEXT);
			confEditor.activate();
			String[] title = element.split(" ");
			confEditor.createNewGlobalEndpoint(title[0], element);	
			confEditor.deleteGlobalElement(Element.ENDPOINT, title[0]);
			new DefaultTreeItem(new String[] { TYPE, title[0] + " (Endpoint)" }).select();
			fail(element);
		} catch (Exception e) {
			log.info("Endpoint: " + element + ", was deleted");
		}
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
	public void testSourceXML()	{
		
		try {		
			ConfigurationsEditor confEditor = new ConfigurationsEditor(PROJECT_NAME, CONTEXT);
			confEditor.activate();
			String[] title = element.split(" ");
			confEditor.createNewGlobalEndpoint(title[0], element);	
			CamelEditor.switchTab("Source");	
			String content = new DefaultStyledText().getText();	
			log.info(content);
			XPathEvaluator eval = new XPathEvaluator(new ByteArrayInputStream(content.getBytes()));
			if(!eval.evaluateBoolean("/beans/camelContext/endpoint[@id='" + title[0] + "']"))
				fail(element);
			CamelEditor.switchTab("Configurations");
			confEditor.deleteGlobalElement(Element.ENDPOINT, title[0]);
			CamelEditor.switchTab("Source");
			content = new DefaultStyledText().getText();
			log.info(content);
			eval = new XPathEvaluator(new ByteArrayInputStream(content.getBytes()));
			if(eval.evaluateBoolean("/beans/camelContext/endpoint[@id='" + title[0] + "']"))
				fail(element);
			CamelEditor.switchTab("Configurations");
		} catch (Exception e) {
			fail(element);
		}	
	}
	
}
