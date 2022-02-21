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
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
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
import org.jboss.tools.fuse.reddeer.JiraClient;
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

	private List<String> availableDataFormats = Arrays.asList(
			"avro - The Avro data format is used for serialization and deserialization of messages using Apache Avro binary dataformat.",
			"asn1 - The ASN.1 data format is used for file transfer with telecommunications protocols.",
			"barcode - The Barcode data format is used for creating barccode images (such as QR-Code)",
			"base64 - The Base64 data format is used for base64 encoding and decoding.",
			"beanio - The BeanIO data format is used for working with flat payloads (such as CSV, delimited, or fixed length formats).",
			"bindy-csv - The Bindy data format is used for working with flat payloads (such as CSV, delimited, fixed length formats, or FIX messages).",
			"bindy-fixed - The Bindy data format is used for working with flat payloads (such as CSV, delimited, fixed length formats, or FIX messages).",
			"bindy-kvp - The Bindy data format is used for working with flat payloads (such as CSV, delimited, fixed length formats, or FIX messages).",
			"boon - Boon data format is used for unmarshal a JSon payload to POJO or to marshal POJO back to JSon payload.",
			"castor - Castor data format is used for unmarshal a XML payload to POJO or to marshal POJO back to XML payload.",
			"crypto - Crypto data format is used for encrypting and decrypting of messages using Java Cryptographic Extension.",
			"csv - The CSV data format is used for handling CSV payloads.",
			"flatpack - The Flatpack data format is used for working with flat payloads (such as CSV, delimited, or fixed length formats).",
			"gzip - The GZip data format is a message compression and de-compression format (which works with the popular gzip/gunzip tools).",
			"hessian - Hessian data format is used for marshalling and unmarshalling messages using Cauchos Hessian format.",
			"hl7 - The HL7 data format can be used to marshal or unmarshal HL7 (Health Care) model objects.",
			"ical - The iCal dataformat is used for working with iCalendar messages.",
			"jacksonxml - JacksonXML data format is used for unmarshal a XML payload to POJO or to marshal POJO back to XML payload.",
			"jaxb - JAXB data format uses the JAXB2 XML marshalling standard to unmarshal an XML payload into Java objects or to marshal Java objects into an XML payload.",
			"jibx - JiBX data format is used for unmarshal a XML payload to POJO or to marshal POJO back to XML payload.",
			"json-fastjson - JSon data format is used for unmarshal a JSon payload to POJO or to marshal POJO back to JSon payload.",
			"json-gson - JSon data format is used for unmarshal a JSon payload to POJO or to marshal POJO back to JSon payload.",
			"json-jackson - JSon data format is used for unmarshal a JSon payload to POJO or to marshal POJO back to JSon payload.",
			"json-johnzon - JSon data format is used for unmarshal a JSon payload to POJO or to marshal POJO back to JSon payload.",
			"json-xstream - JSon data format is used for unmarshal a JSon payload to POJO or to marshal POJO back to JSon payload.",
			"lzf - The LZF data format is a message compression and de-compression format (uses the LZF deflate algorithm).",
			"mime-multipart - The MIME Multipart data format can marshal a Camel message with attachments into a Camel message having a MIME-Multipart message as message body (and no attachments), and vise-versa when unmarshalling.",
			"pgp - PGP data format is used for encrypting and decrypting of messages using Java Cryptographic Extension and PGP.",
			"protobuf - The Protobuf data format is used for serializing between Java objects and the Google Protobuf protocol.",
			"rss - RSS data format is used for working with RSS sync feed Java Objects and transforming to XML and vice-versa.",
			"secureXML - The XML Security data format facilitates encryption and decryption of XML payloads.",
			"serialization - Serialization is a data format which uses the standard Java Serialization mechanism to unmarshal a binary payload into Java objects or to marshal Java objects into a binary blob.",
			"soapjaxb - SOAP is a data format which uses JAXB2 and JAX-WS annotations to marshal and unmarshal SOAP payloads.",
			"string - String data format is a textual based format that supports character encoding.",
			"syslog - The Syslog dataformat is used for working with RFC3164 and RFC5424 messages (logging and monitoring).",
			"tarfile - The Tar File data format is a message compression and de-compression format of tar files.",
			"thrift - The Thrift data format is used for serialization and deserialization of messages using Apache Thrift binary dataformat.",
			"tidyMarkup - TidyMarkup data format is used for parsing HTML and return it as pretty well-formed HTML.",
			"univocity-csv - The uniVocity CSV data format is used for working with CSV (Comma Separated Values) flat payloads.",
			"univocity-fixed - The uniVocity Fixed Length data format is used for working with fixed length flat payloads.",
			"univocity-tsv - The uniVocity TSV data format is used for working with TSV (Tabular Separated Values) flat payloads.",
			"xmlBeans - XML Beans data format is used for unmarshal a XML payload to POJO or to marshal POJO back to XML payload.",
			"xmljson - XML JSon data format can convert from XML to JSON and vice-versa directly, without stepping through intermediate POJOs.",
			"xstream - XSTream data format is used for unmarshal a XML payload to POJO or to marshal POJO back to XML payload.",
			"yaml-snakeyaml - YAML is a data format to marshal and unmarshal Java objects to and from YAML.",
			"zip - Zip Deflate Compression data format is a message compression and de-compression format (not zip files).",
			"zipfile - The Zip File data format is a message compression and de-compression format of zip files.");

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
		assumeTrue(!checkForReportedParameters()); //Run only if parameter is not reported as issue.
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
		assertEquals("DataFormat - " + name, expected, result);
	}

	private String getLabel(String row) {
		return row.split("\\s")[0];
	}

	private static void createProject() {
		ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(KARAF).template(CBR_SPRING)
				.create();
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
	
	/*
	 * Checks if JIRA issue is opened. If yes, checks if actual parameter is reported one.
	 * https://issues.redhat.com/browse/FUSETOOLS-1930
	 * @return true if parameter is still reported with issue
	 * @return false else
	 * */
	public boolean checkForReportedParameters() {
		if (new JiraClient().isIssueClosed("FUSETOOLS-1930")) {
			return false;
		} else {
			return "zipfile - The Zip File data format is a message compression and de-compression format of zip files.".equals(dataFormat);
		}
	}
}
