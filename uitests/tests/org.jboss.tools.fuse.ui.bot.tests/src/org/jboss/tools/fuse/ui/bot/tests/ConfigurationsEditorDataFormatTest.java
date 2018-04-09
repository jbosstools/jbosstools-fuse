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

import static org.jboss.tools.fuse.reddeer.ProjectTemplate.CBR_SPRING;
import static org.jboss.tools.fuse.reddeer.SupportedCamelVersions.CAMEL_2_17_0_REDHAT_630187;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

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
import org.jboss.tools.fuse.reddeer.JiraIssue;
import org.jboss.tools.fuse.reddeer.condition.TreeHasItem;
import org.jboss.tools.fuse.reddeer.editor.CamelDataFormatDialog;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
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
 * Tests manipulation with Data Formats in 'Configurations' tab in Camel editor
 * 
 * @author djelinek
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class ConfigurationsEditorDataFormatTest {

	public static final String PROJECT_NAME = "cbr";
	public static final String CONTEXT = "camel-context.xml";
	public static final String ROOT = "Red Hat Fuse";

	private ConfigurationsEditor editor;
	private String dataFormat;
	private String dataFormatName;
	private String[] path;

	private List<String> availableDataFormats = Arrays.asList("avro - Camel Avro data format",
			"barcode - Camel Barcode (e.g. QRcode, PDF417, DataMatrix) support",
			"base64 - Camel Base64 data format support", "beanio - Camel BeanIO data format support",
			"bindy-csv - Camel Bindy data format support", "bindy-fixed - Camel Bindy data format support",
			"bindy-kvp - Camel Bindy data format support", "boon - Camel Boon support",
			"castor - Camel Castor data format support", "crypto - Camel Cryptographic Support",
			"csv - Camel CSV data format support", "flatpack - Camel FlatPack support",
			"gzip - The Core Camel Java DSL based router", "hessian - Hessian serialization support",
			"hl7 - Camel HL7 support", "ical - Camel iCal component", "jacksonxml - Camel Jackson XML support",
			"jaxb - Camel JAXB support", "jibx - Camel Jibx support", "json-gson - Camel Gson support",
			"json-jackson - Camel Jackson support", "json-xstream - Camel XStream support", "lzf - Camel LZF support",
			"mime-multipart - Camel Mail support", "pgp - Camel Cryptographic Support", "protobuf - Camel Components",
			"rss - Camel RSS support", "secureXML - Camel Partial XML Encryption/Decryption and XML Signature support",
			"serialization - The Core Camel Java DSL based router", "soapjaxb - Camel SOAP support",
			"string - The Core Camel Java DSL based router", "syslog - Camel Syslog support",
			"tarfile - Camel Tar file support", "tidyMarkup - Camel TagSoup support",
			"univocity-csv - Camel UniVocity parsers data format support",
			"univocity-fixed - Camel UniVocity parsers data format support",
			"univocity-tsv - Camel UniVocity parsers data format support", "xmlBeans - Camel XMLBeans support",
			"xmljson - Camel XML JSON Data Format", "xmlrpc - Camel XML RPC support", "xstream - Camel XStream support",
			"yaml-snakeyaml - Camel SnakeYAML support", "zip - The Core Camel Java DSL based router",
			"zipfile - Camel Zip file support");

	/**
	 * Sets parameters for parameterized test
	 * 
	 * @return List of all available Global elements - Data Formats
	 */
	@Parameters
	public static Collection<String> setupData() {
		createProject();
		new CamelProject(PROJECT_NAME).openCamelContext(CONTEXT);
		CamelEditor.switchTab("Configurations");
		return ConfigurationsEditor.getDataFormats();
	}

	public ConfigurationsEditorDataFormatTest(String dataFormat) {
		this.dataFormat = dataFormat;
		this.dataFormatName = getLabel(dataFormat);
		path = new String[] { ROOT, this.dataFormatName, " (Data Format)" };
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
		CamelEditor.switchTab("Configurations");
	}

	@After
	public void clearEnviroment() {
		new CamelProject(PROJECT_NAME).openCamelContext(CONTEXT);
		CamelEditor.switchTab("Source");
		EditorManipulator.copyFileContentToCamelXMLEditor("resources/camel-context-cbr.xml");
	}

	@AfterClass
	public static void setupDeleteProjects() {
		new CleanWorkspaceRequirement().fulfill();
	}

	/**
	 * <p>
	 * Test verify that all of Data Formats are available in CamelDataFormat dialog.
	 * </p>
	 */
	@Test
	public void testDataFormatAvailability() {
		assertTrue(dataFormat, availableDataFormats.contains(dataFormat));
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
	public void testCreateDataFormat() {
		createDataFormat();
		assertDataFormatPath(true);
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
	public void testEditDataFormat() {
		createDataFormat();
		editor.editDataFormat(dataFormatName).setDetailsProperty(DetailsProperty.ID, "new_" + dataFormatName);
		path = new String[] { ROOT, "new_" + dataFormatName + " (Data Format)" };
		assertDataFormatPath(true);
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
	public void testDeleteDataFormat() {
		createDataFormat();
		assertDataFormatPath(true);
		editor.deleteDataFormat(dataFormatName);
		assertDataFormatPath(false);
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
	public void testSourceXML() {
		createDataFormat();
		editor.close(true);
		assertXPath(true, dataFormatName);
		new CamelProject(PROJECT_NAME).openCamelContext(CONTEXT);
		editor = new ConfigurationsEditor();
		editor.deleteDataFormat(dataFormatName);
		editor.close(true);
		assertXPath(false, dataFormatName);
	}

	private void assertXPath(boolean expected, String name) {
		String evalResult;
		String node = name;
		if (!node.startsWith("mime") && !node.startsWith("univocity")) {
			node = name.split("-")[0];
		}
		try {
			XPathEvaluator eval = new XPathEvaluator(editor.getAssociatedFile().getInputStream(), false);
			evalResult = eval.evaluateXPath("/beans/camelContext/dataFormats/" + node + "/@id");
		} catch (XPathExpressionException | IOException | SAXException | ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		boolean result = evalResult.equals(name);
		if (!result) {
			testIssue_1930();
		}
		assertEquals("DataFormat - " + name, expected, result);
	}

	private String getLabel(String row) {
		return row.split("\\s")[0];
	}

	private static void createProject() {
		ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(KARAF)
				.version(CAMEL_2_17_0_REDHAT_630187).template(CBR_SPRING).create();
	}

	private void createDataFormat() {
		editor = new ConfigurationsEditor();
		CamelDataFormatDialog wizard = editor.addDataFormat();
		wizard.setDataFormat(dataFormat);
		wizard.setIdText(dataFormatName);
		wizard.finish();
	}

	private void assertDataFormatPath(boolean expected) {
		WaitCondition wait = new TreeHasItem(new DefaultTree(editor), path);
		new WaitUntil(wait, TimePeriod.MEDIUM, false);
		if (wait.getResult() != null) {
			assertEquals("DataFormat - " + dataFormatName, expected, wait.getResult());
		}
	}

	/**
	 * https://issues.jboss.org/browse/FUSETOOLS-1930
	 */
	private void testIssue_1930() {
		if (dataFormat.equals("zipfile - Camel Zip file support")) {
			throw new JiraIssue("FUSETOOLS-1930");
		}
	}
}
