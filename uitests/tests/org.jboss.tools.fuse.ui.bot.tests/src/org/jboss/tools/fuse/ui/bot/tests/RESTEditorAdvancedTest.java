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

import static org.jboss.tools.fuse.reddeer.ResourceHelper.getResourceAbsolutePath;
import static org.jboss.tools.fuse.reddeer.utils.ProjectFactory.importExistingProject;
import static org.jboss.tools.fuse.ui.bot.tests.Activator.PLUGIN_ID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.reddeer.common.util.XPathEvaluator;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanerrorlog.CleanErrorLogRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.handler.EditorHandler;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.ProjectType;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.editor.RESTEditor;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.utils.LogChecker;
import org.jboss.tools.fuse.reddeer.view.FusePropertiesView;
import org.jboss.tools.fuse.reddeer.view.FusePropertiesView.PropertyType;
import org.jboss.tools.fuse.reddeer.wizard.AddRESTOperationWizard;
import org.jboss.tools.fuse.ui.bot.tests.utils.EditorManipulator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;
import org.xml.sax.SAXException;

/**
 * Tests REST editor manipulation (edit/delete/add). The test imports prepared REST projects for blueprint, spring and
 * springboot.
 * 
 * @author djelinek
 *
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class RESTEditorAdvancedTest {

	public static final String PROJECT_NAME_PREFIX = "rest";
	public static final String PROJECT_PATH = "resources/projects";

	// REST Configuration testing properties
	public static final String CONFIGURATION_COMPONENT = "jetty";
	public static final String CONFIGURATION_CONTEXTPATH = "/testPath/context";
	public static final String CONFIGURATION_PORT = "80811";
	public static final String CONFIGURATION_BINDINGMODE = "json";
	public static final String CONFIGURATION_HOST = "localhost";

	// REST Element testing properties
	public static final String ELEMENT_EDIT = "/greetings";
	public static final String ELEMENT_EDITED = "/greetingss";
	public static final String ELEMENT_ID_EDIT = "rest-greetings-id";
	public static final String ELEMENT_ADD = "/testing";
	public static final String ELEMENT_ID_ADD = "rest-testElement";

	// REST Operation testing proeprties
	public static final String OPERATION = "/hello/{name}";
	public static final String OPERATION_ID = "get-hello-id";
	public static final String OPERATION_ID_ADD = "put-newRESTOperationID";
	public static final String OPERATION_URI_ADD = "/newRESTOperationURI";
	public static final String OPERATION_REFERENCED_ROUTEID_ADD = "direct:updateTask";

	private String projectName;
	private RESTEditor restEditor;
	private ProjectType projectType;

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	@Parameters(name = "{0}")
	public static Collection<ProjectType> projectTypes() {
		return Arrays.asList(ProjectType.SPRING, ProjectType.SPRINGBOOT, ProjectType.BLUEPRINT);
	}

	public RESTEditorAdvancedTest(ProjectType projectType) {
		this.projectType = projectType;
		this.projectName = projectName(projectType);
	}

	@BeforeClass
	public static void setup() {
		new WorkbenchShell().maximize();
		new CleanWorkspaceRequirement().fulfill();

		projectTypes().stream().forEach(projectType -> importRestProject(projectName(projectType)));
	}

	@Before
	public void prepareWorkspace() {
		new CleanErrorLogRequirement().fulfill();
	}

	@After
	public void resetProjectSetup() {
		StringBuilder filePath = new StringBuilder(PROJECT_PATH + "/" + projectName + "/src/main/resources");
		if (projectName.contains("blueprint")) {
			filePath.append("/OSGI-INF/blueprint/" + projectType.getCamelContext());
		} else {
			filePath.append("/META-INF/spring/" + projectType.getCamelContext());
		}
		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		CamelEditor.switchTab(CamelEditor.SOURCE_TAB);
		EditorManipulator.copyFileContentToCamelXMLEditor(filePath.toString());
		EditorHandler.getInstance().closeAll(true);
	}

	@AfterClass
	public static void cleanWorkspace() {
		new CleanWorkspaceRequirement().fulfill();
	}

	private static void importRestProject(String projectName) {
		importExistingProject(getResourceAbsolutePath(PLUGIN_ID, PROJECT_PATH + "/" + projectName), projectName, false);
		new CamelProject(projectName).update();
	}

	private static String projectName(ProjectType projectType) {
		return PROJECT_NAME_PREFIX + "-" + projectType.toString().toLowerCase();
	}

	/**
	 * Tests edit/delete/add of REST Configuration
	 */
	@Test
	public void testRestConfiguration() {
		restEditor = openCamelContextAndSelectRestTab();

		// edit & verify
		restEditor.setRestConfigurationComponent(CONFIGURATION_COMPONENT);
		restEditor.setRestConfigurationContextPath(CONFIGURATION_CONTEXTPATH);
		restEditor.setRestConfigurationPort(CONFIGURATION_PORT);
		restEditor.setRestConfigurationBindingMode(CONFIGURATION_BINDINGMODE);
		restEditor.setRestConfigurationHost(CONFIGURATION_HOST);
		restEditor.save();
		workaround_3060();
		errorCollector.checkThat(getXPathValue("restConfiguration/@component"), equalTo(CONFIGURATION_COMPONENT));
		errorCollector.checkThat(getXPathValue("restConfiguration/@contextPath"), equalTo(CONFIGURATION_CONTEXTPATH));
		errorCollector.checkThat(getXPathValue("restConfiguration/@port"), equalTo(CONFIGURATION_PORT));
		errorCollector.checkThat(getXPathValue("restConfiguration/@bindingMode"), equalTo(CONFIGURATION_BINDINGMODE));
		errorCollector.checkThat(getXPathValue("restConfiguration/@host"), equalTo(CONFIGURATION_HOST));
		collectFuseErrors();

		// delete & verify
		restEditor.deleteRestConfiguration();
		restEditor.save();
		workaround_3060();
		errorCollector.checkThat(getXPathValue("restConfiguration/@*").isEmpty(), equalTo(true));
		collectFuseErrors();

		// add & verify
		restEditor.addRestConfiguration();
		restEditor.save();
		workaround_3060();
		errorCollector.checkThat(getXPathValue("restConfiguration/@*").isEmpty(), equalTo(false));
		collectFuseErrors();
	}

	/**
	 * Tests edit/delete/add of REST Elements
	 */
	@Test
	public void testRestElements() {
		restEditor = openCamelContextAndSelectRestTab();
		FusePropertiesView properties = new FusePropertiesView();

		// edit & verify
		restEditor.selectRestElement(ELEMENT_EDIT);
		properties.activate();
		properties.setProperty(PropertyType.TEXT, "Id *", ELEMENT_ID_EDIT);
		properties.setProperty(PropertyType.CHECKBOX, "Enable CORS", "");
		properties.setProperty(PropertyType.TEXT, "Path", ELEMENT_EDITED);
		restEditor.save();
		workaround_3060();
		errorCollector.checkThat(getXPathValue("rest[1]/@id"), equalTo(ELEMENT_ID_EDIT));
		errorCollector.checkThat(getXPathValue("rest[1]/@enableCORS"), equalTo("true"));
		errorCollector.checkThat(getXPathValue("rest[1]/@path"), equalTo(ELEMENT_EDITED));
		collectFuseErrors();

		// delete & verify
		restEditor.deleteRestElement(ELEMENT_EDITED);
		restEditor.save();
		workaround_3060();
		errorCollector.checkThat(restEditor.getRestElements().size(), equalTo(1));
		errorCollector.checkThat(restEditor.getRestElements().contains(ELEMENT_EDITED), equalTo(false));
		errorCollector.checkThat(getXPathValue("rest[1]/@path"), not(ELEMENT_EDITED));
		collectFuseErrors();

		// add & verify
		restEditor.addRestElement();
		properties.activate();
		properties.setProperty(PropertyType.TEXT, "Id *", ELEMENT_ID_ADD);
		properties.setProperty(PropertyType.TEXT, "Path", ELEMENT_ADD);
		restEditor.save();
		workaround_3060();
		errorCollector.checkThat(restEditor.getRestElements().size(), equalTo(2));
		errorCollector.checkThat(restEditor.getRestElements().contains(ELEMENT_ADD), equalTo(true));
		errorCollector.checkThat(getXPathValue("rest[1]/@id"), equalTo(ELEMENT_ID_ADD));
		errorCollector.checkThat(getXPathValue("rest[1]/@path"), equalTo(ELEMENT_ADD));
		collectFuseErrors();
	}

	/**
	 * Tests edit/delete/add of REST Operations
	 */
	@Test
	public void testRestOperations() {
		restEditor = openCamelContextAndSelectRestTab();
		FusePropertiesView properties = new FusePropertiesView();

		// edit & verify
		restEditor.selectRestOperation(AddRESTOperationWizard.OPERATION_TYPE_GET + " " + OPERATION);
		properties.activate();
		properties.setProperty(PropertyType.TEXT, "Id *", OPERATION_ID);
		properties.setProperty(PropertyType.CHECKBOX, "Enable CORS", "");
		restEditor.save();
		workaround_3060();
		errorCollector.checkThat(getXPathValue("rest/get[1]/@id"), equalTo(OPERATION_ID));
		errorCollector.checkThat(getXPathValue("rest/get[1]/@enableCORS"), equalTo("true"));
		collectFuseErrors();

		// delete & verify
		restEditor.deleteRestOperation(AddRESTOperationWizard.OPERATION_TYPE_GET + " " + OPERATION);
		restEditor.save();
		workaround_3060();
		errorCollector.checkThat(restEditor.getRestOperations().isEmpty(), equalTo(true));
		errorCollector.checkThat(
				restEditor.getRestOperations().contains(AddRESTOperationWizard.OPERATION_TYPE_GET + " " + OPERATION),
				equalTo(false));
		errorCollector.checkThat(getXPathValue("rest/get[1]/@uri"), not(OPERATION));
		collectFuseErrors();

		// add & verify
		AddRESTOperationWizard addOperation = restEditor.addRestOperation();
		addOperation.setTextID(OPERATION_ID_ADD);
		addOperation.setTextURI(OPERATION_URI_ADD);
		addOperation.setSelectionReferencedRouteID(OPERATION_REFERENCED_ROUTEID_ADD);
		addOperation.setSelectionOperationType(AddRESTOperationWizard.OPERATION_TYPE_PUT);
		addOperation.finish(TimePeriod.MEDIUM);
		restEditor.save();
		workaround_3060();
		errorCollector.checkThat(restEditor.getRestOperations().size(), equalTo(1));
		errorCollector.checkThat(restEditor.getRestOperations()
				.contains(AddRESTOperationWizard.OPERATION_TYPE_PUT + " " + OPERATION_URI_ADD), equalTo(true));
		errorCollector.checkThat(getXPathValue("rest/put[1]/@id"), equalTo(OPERATION_ID_ADD));
		errorCollector.checkThat(getXPathValue("rest/put[1]/@uri"), equalTo(OPERATION_URI_ADD));
		errorCollector.checkThat(getXPathValue("rest/put[1]/to/@uri"), equalTo(OPERATION_REFERENCED_ROUTEID_ADD));
		collectFuseErrors();
	}

	private void collectFuseErrors() {
		errorCollector.checkThat("No Fuse errors", LogChecker.noFuseError(), equalTo(true));
	}

	private RESTEditor openCamelContextAndSelectRestTab() {
		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		return new RESTEditor(projectType.getCamelContext());
	}

	private String getXPathValue(String xPathExpression) {
		if (projectName.contains("blueprint")) {
			xPathExpression = "/blueprint/camelContext/" + xPathExpression;
		} else {
			xPathExpression = "/beans/camelContext/" + xPathExpression;
		}
		String actualResult;
		try {
			XPathEvaluator xpath = new XPathEvaluator(restEditor.getAssociatedFile().getInputStream(), false);
			actualResult = xpath.evaluateXPath(xPathExpression);
			return actualResult;
		} catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * see https://issues.jboss.org/browse/FUSETOOLS-3060
	 */
	private void workaround_3060() {
		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		CamelEditor.switchTab(CamelEditor.SOURCE_TAB);
		CamelEditor.switchTab(RESTEditor.REST_EDITOR_TAB);
	}

}
