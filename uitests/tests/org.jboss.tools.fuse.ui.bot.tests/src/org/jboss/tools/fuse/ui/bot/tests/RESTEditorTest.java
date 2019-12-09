/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.ui.bot.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.jboss.tools.fuse.reddeer.ResourceHelper.getResourceAbsolutePath;
import static org.jboss.tools.fuse.reddeer.utils.ProjectFactory.importExistingProject;
import static org.jboss.tools.fuse.ui.bot.tests.Activator.PLUGIN_ID;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanerrorlog.CleanErrorLogRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.ProjectType;
import org.jboss.tools.fuse.reddeer.editor.RESTEditor;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Tests REST editor. The test imports prepared rest projects for blueprint and spring.
 *
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class RESTEditorTest {

	public static final String PROJECT_NAME_PREFIX = "rest";
	public static final String PROJECT_PATH = "resources/projects";

	private String projectName;
	private RESTEditor restEditor;
	private ProjectType projectType;

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	@Parameters(name = "{0}")
	public static Collection<ProjectType> projectTypes() {
		return Arrays.asList(ProjectType.SPRING, ProjectType.SPRINGBOOT, ProjectType.BLUEPRINT);
	}

	public RESTEditorTest(ProjectType projectType) {
		this.projectType = projectType;
		this.projectName = projectName(projectType);
	}

	@BeforeClass
	public static void importBeanProjects() {
		new WorkbenchShell().maximize();
		new CleanWorkspaceRequirement().fulfill();

		projectTypes().stream().forEach(projectType -> importRestProject(projectName(projectType)));
	}

	private static void importRestProject(String projectName) {
		importExistingProject(getResourceAbsolutePath(PLUGIN_ID, PROJECT_PATH + "/" + projectName), projectName, false);
		new CamelProject(projectName).update();
	}

	private static String projectName(ProjectType projectType) {
		return PROJECT_NAME_PREFIX + "-" + projectType.toString().toLowerCase();
	}

	@AfterClass
	public static void cleanWorkspace() {
		new CleanWorkspaceRequirement().fulfill();
	}

	@Before
	public void cleanErrorLog() {
		new CleanErrorLogRequirement().fulfill();
	}

	/**
	 * <p>
	 * Tests REST configuration properties.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to REST</li>
	 * <li>Check the property 'Component'</li>
	 * <li>Check the property 'Port'</li>
	 * <li>Check the property 'Binding Mode'</li>
	 * </ol>
	 */
	@Test
	public void testRestConfigurationProperties() {
		restEditor = openCamelContextAndSelectRestTab();
		errorCollector.checkThat("restlet", equalTo(restEditor.getRestConfigurationComponent()));
		errorCollector.checkThat("8080", equalTo(restEditor.getRestConfigurationPort()));
		errorCollector.checkThat("auto", equalTo(restEditor.getRestConfigurationBindingMode()));
	}

	/**
	 * <p>
	 * Tests REST elements.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to REST</li>
	 * <li>Check elements in the section 'Rest Elements'</li>
	 * </ol>
	 */
	@Test
	public void testRestElements() {
		restEditor = openCamelContextAndSelectRestTab();
		assertThat(restEditor.getRestElements()).containsOnly("/greetings", "/taskmanager");
	}

	/**
	 * <p>
	 * Tests REST element which contains only one REST operation.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to REST</li>
	 * <li>Select the REST element</li>
	 * <li>Check the REST element in the section 'Rest Elements'</li>
	 * </ol>
	 */
	@Test
	public void testOneRestOperation() {
		restEditor = openCamelContextAndSelectRestTab();
		restEditor.selectRestElement("/greetings");
		assertThat(restEditor.getRestOperations()).containsOnly("get /hello/{name}");
	}

	/**
	 * <p>
	 * Tests REST element which contains more REST operations.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open a camel context in Camel editor</li>
	 * <li>Switch to REST</li>
	 * <li>Select the REST element</li>
	 * <li>Check the REST elements in the section 'Rest Elements'</li>
	 * </ol>
	 */
	@Test
	public void testMultipleRestOperations() {
		restEditor = openCamelContextAndSelectRestTab();
		restEditor.selectRestElement("/taskmanager");
		assertThat(restEditor.getRestOperations()).containsOnly("get /tasks", "get /tasks/{id}", "verb /task/{id}",
				"head /tasks/{id}", "post /tasks", "put /tasks", "patch /tasks", "delete /tasks", "delete /tasks/{id}");
	}

	private RESTEditor openCamelContextAndSelectRestTab() {
		new CamelProject(projectName).openCamelContext(projectType.getCamelContext());
		return new RESTEditor(projectType.getCamelContext());
	}

}
