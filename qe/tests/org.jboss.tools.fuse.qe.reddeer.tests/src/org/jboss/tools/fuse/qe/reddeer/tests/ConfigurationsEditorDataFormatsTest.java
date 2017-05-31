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
import org.jboss.reddeer.eclipse.ui.views.log.LogMessage;
import org.jboss.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.workbench.handler.EditorHandler;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.qe.reddeer.JiraIssue;
import org.jboss.tools.fuse.qe.reddeer.LogGrapper;
import org.jboss.tools.fuse.qe.reddeer.ProjectTemplate;
import org.jboss.tools.fuse.qe.reddeer.ProjectType;
import org.jboss.tools.fuse.qe.reddeer.SupportedCamelVersions;
import org.jboss.tools.fuse.qe.reddeer.XPathEvaluator;
import org.jboss.tools.fuse.qe.reddeer.editor.CamelDataFormatDialog;
import org.jboss.tools.fuse.qe.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.qe.reddeer.editor.ConfigurationsEditor;
import org.jboss.tools.fuse.qe.reddeer.editor.ConfigurationsEditor.Element;
import org.jboss.tools.fuse.qe.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.EditorManipulator;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Tests for global elements - Data Formats in Configurations Tab in Camel editor
 *  
 * @author djelinek
 */
@CleanWorkspace
@OpenPerspective(JavaEEPerspective.class)
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class ConfigurationsEditorDataFormatsTest extends DefaultTest {

	protected Logger log = Logger.getLogger(ConfigurationsEditorDataFormatsTest.class);
	
	private static final String PROJECT_NAME = "cbr";
	private static final String CONTEXT = "camel-context.xml";
	private static final String TYPE = "JBoss Fuse";
	
	private String element;
	private List<String> availableDataFormats = Arrays.asList("avro - Camel Avro data format",
															  "barcode - Camel Barcode (e.g. QRcode, PDF417, DataMatrix) support",
															  "base64 - Camel Base64 data format support",
															  "beanio - Camel BeanIO data format support",
															  "bindy-csv - Camel Bindy data format support",
															  "bindy-fixed - Camel Bindy data format support",
															  "bindy-kvp - Camel Bindy data format support",
															  "boon - Camel Boon support",
															  "castor - Camel Castor data format support",
															  "crypto - Camel Cryptographic Support",
															  "csv - Camel CSV data format support",
															  "flatpack - Camel FlatPack support",
															  "gzip - The Core Camel Java DSL based router",
															  "hessian - Hessian serialization support",
															  "hl7 - Camel HL7 support",
															  "ical - Camel iCal component",
															  "jacksonxml - Camel Jackson XML support",
															  "jaxb - Camel JAXB support",
															  "jibx - Camel Jibx support",
															  "json-gson - Camel Gson support",
															  "json-jackson - Camel Jackson support",
															  "json-xstream - Camel XStream support",
															  "lzf - Camel LZF support",
															  "mime-multipart - Camel Mail support",
															  "pgp - Camel Cryptographic Support",
															  "protobuf - Camel Components",
															  "rss - Camel RSS support",
															  "secureXML - Camel Partial XML Encryption/Decryption and XML Signature support",
															  "serialization - The Core Camel Java DSL based router",
															  "soapjaxb - Camel SOAP support",
															  "string - The Core Camel Java DSL based router",
															  "syslog - Camel Syslog support",
															  "tarfile - Camel Tar file support",
															  "tidyMarkup - Camel TagSoup support",
															  "univocity-csv - Camel UniVocity parsers data format support",
															  "univocity-fixed - Camel UniVocity parsers data format support",
															  "univocity-tsv - Camel UniVocity parsers data format support",
															  "xmlBeans - Camel XMLBeans support",
															  "xmljson - Camel XML JSON Data Format",
															  "xmlrpc - Camel XML RPC support",
															  "xstream - Camel XStream support",
															  "yaml-snakeyaml - Camel SnakeYAML support",
															  "zip - The Core Camel Java DSL based router",
															  "zipfile - Camel Zip file support"	);
	
	/**
	 * Sets parameters for parameterized test
	 * 
	 * @return List of all available Global elements - Data Formats
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
		List<String> dataFormats = CamelDataFormatDialog.getDataFormats();
		return dataFormats;
	}

	/**
	 * Utilizes passing parameters using the constructor
	 * 
	 * @param template
	 *            a Global element - Data Format
	 */
	public ConfigurationsEditorDataFormatsTest(String element) {
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
		
		CamelEditor.switchTab("Source");
		EditorManipulator.copyFileContentToCamelXMLEditor("resources/camel-context-cbr.xml");
	}
	
	/**
	 * <p>
	 * Test verify that all of Data Formats are available in CamelDataFormat dialog.
	 * </p>
	 */
	@Test
	public void testDataFormatAvailability() {
		
		assertTrue(element, availableDataFormats.contains(element));
	}
	
	/**
	 * <p>
	 * Test tries create <i>Data Format</i> global element inside Camel Editor in configurations tab
	 * </p>
	 * <ol>
	 * <li>create a new project with Spring-DSL project type and Content Based Router template</li>
	 * <li>open Project Explorer view</li>
	 * <li>open camel-context.xml file</li>
	 * <li>create <i>Data Format</i> component</li>
	 * <li>check that <i>Data Format</i> component was created</li>
	 * </ol>
	 */
	@Test
	public void testCreateDataFormat()	{
		
		try {
			ConfigurationsEditor confEditor = new ConfigurationsEditor(PROJECT_NAME, CONTEXT);
			confEditor.activate();
			String[] title = element.split(" ");
			confEditor.createNewGlobalDataFormat(title[0], element);	
			new DefaultTreeItem(new String[] { TYPE, title[0] + " (Data Format)" }).select();
		} catch (Exception e) {
			fail(element);
		}
	}
	
	/**
	 * <p>
	 * Test tries edit <i>Data Format</i> global element inside Camel Editor in configurations tab
	 * </p>
	 * <ol>
	 * <li>create a new project with Spring-DSL project type and Content Based Router template</li>
	 * <li>open Project Explorer view</li>
	 * <li>open camel-context.xml file</li>
	 * <li>create <i>Data Format</i> component</li>
	 * <li>edit name of <i>Data Format</i> component</li>
	 * <li>check that <i>Data Format</i> component was edited</li>
	 * </ol>
	 */
	@Test
	public void testEditDataFormat()	{
		
		try {
			ConfigurationsEditor confEditor = new ConfigurationsEditor(PROJECT_NAME, CONTEXT);
			confEditor.activate();
			String[] title = element.split(" ");
			confEditor.createNewGlobalDataFormat(title[0], element);	
			confEditor.editGlobalDataFormat(title[0]);				
			new LabeledText("Id").setText("changed" + title[0]);
			confEditor.activate();
			new DefaultTreeItem(new String[] { TYPE, "changed" + title[0] + " (Data Format)" }).select();
		} catch (Exception e) {
			fail(element);
		}
	}
	
	/**
	 * <p>
	 * Test tries delete <i>Data Format</i> global element inside Camel Editor in configurations tab
	 * </p>
	 * <ol>
	 * <li>create a new project with Spring-DSL project type and Content Based Router template</li>
	 * <li>open Project Explorer view</li>
	 * <li>open camel-context.xml file</li>
	 * <li>create <i>Data Format</i> component</li>
	 * <li>delete <i>Data Format</i> component</li>
	 * <li>check that <i>Data Format</i> component was deleted</li>
	 * </ol>
	 */
	@Test
	public void testDeleteDataFormat()	{
		
		try {
			ConfigurationsEditor confEditor = new ConfigurationsEditor(PROJECT_NAME, CONTEXT);	
			confEditor.activate();
			String[] title = element.split(" ");
			confEditor.createNewGlobalDataFormat(title[0], element);	
			confEditor.deleteGlobalElement(Element.DATAFORMAT, title[0]);
			new DefaultTreeItem(new String[] { TYPE, title[0] + " (Data Format)" }).select();
			fail(element);
		} catch (Exception e) {
			log.info("Data Format: " + element + ", was deleted");
		}
	}
	
	/**
	 * <p>
	 * Test tries create and delete <i>Data Format</i> global element and checks Source XML
	 * </p>
	 * <ol>
	 * <li>create a new project with Spring-DSL project type and Content Based Router template</li>
	 * <li>open Project Explorer view</li>
	 * <li>open camel-context.xml file</li>
	 * <li>create <i>Data Format</i> component</li>
	 * <li>check source XML that <i>Data Format</i> component was created</li>
	 * <li>delete <i>Data Format</i> component</li>
	 * <li>checks source XML that <i>Data format</i> component was deleted</li>
	 * </ol>
	 */
	@Test
	public void testSourceXML()	{
		
		try {
			ConfigurationsEditor confEditor = new ConfigurationsEditor(PROJECT_NAME, CONTEXT);
			confEditor.activate();
			String[] node = element.split(" ");
			String[] format = new String[] { "" };
			if(!node[0].startsWith("univocity") && !node[0].startsWith("mime"))
				format = node[0].split("-");
			else 
				format[0] = node[0];
			confEditor.createNewGlobalDataFormat(node[0], element);	
			CamelEditor.switchTab("Source");	
			String content = new DefaultStyledText().getText();	
			log.info(content);
			XPathEvaluator eval = new XPathEvaluator(new ByteArrayInputStream(content.getBytes()));
			if(!eval.evaluateBoolean("/beans/camelContext/dataFormats/" + format[0] +"[@id='" + node[0] + "']")) {
				testIssue_1930();
				fail(element);
			}
			CamelEditor.switchTab("Configurations");
			confEditor.deleteGlobalElement(Element.DATAFORMAT, node[0]);
			CamelEditor.switchTab("Source");
			content = new DefaultStyledText().getText();
			log.info(content);
			eval = new XPathEvaluator(new ByteArrayInputStream(content.getBytes()));
			if(eval.evaluateBoolean("/beans/camelContext/dataFormats/" + format[0] +"[@id='" + node[0] + "']")) {
				testIssue_1930();
				fail(element);
			}
			CamelEditor.switchTab("Configurations");		
		} catch (Exception e) {
			testIssue_1930();
			fail(element);
		}
	}
	
	/**
	 * https://issues.jboss.org/browse/FUSETOOLS-1930
	 */
	private void testIssue_1930() {
		if (element.equals("zipfile - Camel Zip file support")) {
			throw new JiraIssue("FUSETOOLS-1930");
		}
	}
}
