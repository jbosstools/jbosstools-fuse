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

import static org.jboss.tools.fuse.reddeer.CamelCatalogUtils.CatalogType.DATAFORMAT;
import static org.jboss.tools.fuse.reddeer.ProjectTemplate.EMPTY_SPRING;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.ui.perspectives.JavaEEPerspective;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.autobuilding.AutoBuildingRequirement.AutoBuilding;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.api.CCombo;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.workbench.handler.EditorHandler;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.CamelCatalogUtils;
import org.jboss.tools.fuse.reddeer.MavenDependency;
import org.jboss.tools.fuse.reddeer.ProjectType;
import org.jboss.tools.fuse.reddeer.component.Marshal;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.requirement.CamelCatalogRequirement;
import org.jboss.tools.fuse.reddeer.requirement.CamelCatalogRequirement.CamelCatalog;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
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
 * This test verifies if it is possible to set all supported data formats and if all required dependencies were added to
 * the pom.xml file.
 * 
 * For this purpose the test uses the 'marshal' camel component.
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 */
@CamelCatalog
@CleanWorkspace
@AutoBuilding(false)
@RunWith(RedDeerSuite.class)
@OpenPerspective(JavaEEPerspective.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class CamelEditorDataFormatTest {

	public static final String PROJECT_NAME = "dataformats";
	public static final ProjectType PROJECT_TYPE = ProjectType.SPRING;
	public static final String DEPENDENCY_PATTERN = "$groupId/$artifactId";
	public static final String TESTED_COMPONENT_LABEL = "Marshal";

	private static String initialPomContent;

	protected Logger log = Logger.getLogger(CamelEditorDataFormatTest.class);

	private CamelProject camelProject;
	private CamelEditor camelEditor;
	private String dataFormat;

	@InjectRequirement
	public static CamelCatalogRequirement camelCatalogRequirement;
	private static CamelCatalogUtils camelCatalogUtils;

	public CamelEditorDataFormatTest(String dataFormat) {
		this.dataFormat = dataFormat;
	}

	@Parameters(name = "{0}")
	public static Collection<String> getCamelLanguages() {
		List<String> camelDataFormats = new ArrayList<>();
		// there is a bug in RedDeer, see https://github.com/eclipse/reddeer/issues/1914
		// that's why we use hard-coded data formats - the data formats were taken from
		// cat org/apache/camel/catalog/models/marshal.json | jq -r '.properties.dataFormatType.oneOf | .[]'
		camelDataFormats.add("avro");
		camelDataFormats.add("base64");
		camelDataFormats.add("beanio");
		camelDataFormats.add("bindy");
		camelDataFormats.add("boon");
		camelDataFormats.add("castor");
		camelDataFormats.add("crypto");
		camelDataFormats.add("csv");
		camelDataFormats.add("custom");
		camelDataFormats.add("flatpack");
		camelDataFormats.add("gzip");
		camelDataFormats.add("hl7");
		camelDataFormats.add("ical");
		camelDataFormats.add("jacksonxml");
		camelDataFormats.add("jaxb");
		camelDataFormats.add("jibx");
		camelDataFormats.add("json");
		camelDataFormats.add("pgp");
		camelDataFormats.add("protobuf");
		camelDataFormats.add("rss");
		camelDataFormats.add("secureXML");
		camelDataFormats.add("serialization");
		camelDataFormats.add("soapjaxb");
		camelDataFormats.add("string");
		camelDataFormats.add("syslog");
		camelDataFormats.add("tarfile");
		camelDataFormats.add("tidyMarkup");
		camelDataFormats.add("univocity-csv");
		camelDataFormats.add("univocity-fixed");
		camelDataFormats.add("univocity-tsv");
		camelDataFormats.add("xmlBeans");
		camelDataFormats.add("xmljson");
		camelDataFormats.add("xmlrpc");
		camelDataFormats.add("xstream");
		camelDataFormats.add("zip");
		camelDataFormats.add("zipFile");
		return camelDataFormats;
	}

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void createTestProject() throws Exception {
		new WorkbenchShell().maximize();
		ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(KARAF)
				.version(camelCatalogRequirement.getConfiguration().getVersion()).template(EMPTY_SPRING).create();

		CamelProject camelProject = new CamelProject(PROJECT_NAME);
		camelProject.openCamelContext(PROJECT_TYPE.getCamelContext());
		initialPomContent = camelProject.getPomContent();
	}

	@BeforeClass
	public static void initializeCamelCatalogUtils() {
		camelCatalogUtils = new CamelCatalogUtils(camelCatalogRequirement.getConfiguration().getHome());
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

	@Before
	public void initProjectAndOpenEditor() throws Exception {
		camelProject = new CamelProject(PROJECT_NAME);
		camelProject.setPomContent(initialPomContent);
		camelProject.openCamelContext(PROJECT_TYPE.getCamelContext());

		camelEditor = new CamelEditor(PROJECT_TYPE.getCamelContext());
		camelEditor.addCamelComponent(new Marshal(), "Route _route1");
	}

	@After
	public void closeEditor() {
		camelEditor.deleteCamelComponent(TESTED_COMPONENT_LABEL);
		camelEditor.close(true);
	}

	@Test
	public void testDataFormat() throws FileNotFoundException, XPathExpressionException, ParserConfigurationException,
			SAXException, IOException {
		camelEditor.selectEditPart(TESTED_COMPONENT_LABEL);
		camelEditor.setProperty(CCombo.class, "Data Format Type", dataFormat);
		new WaitUntil(new ShellIsAvailable("Progress Information"), false);
		new WaitWhile(new ShellIsAvailable("Progress Information"), TimePeriod.VERY_LONG);
		camelEditor.save();

		List<MavenDependency> actualDeps = camelProject.getMavenDependencies();
		MavenDependency expectedDep = camelCatalogUtils.getMavenDependency(DATAFORMAT, dataFormat);
		assertTrue("Cannot find " + expectedDep + ".\nAvailable dependencies are " + actualDeps, expectedDep == null
				|| actualDeps.stream().filter(dep -> dep.equalsIgnoreVersion(expectedDep)).findAny().isPresent());
	}

}
