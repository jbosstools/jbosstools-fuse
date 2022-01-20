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

import static org.eclipse.reddeer.requirements.server.ServerRequirementState.RUNNING;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.EAP;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.jboss.tools.fuse.ui.bot.tests.Activator.PLUGIN_ID;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.ui.browser.WebBrowserView;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanerrorlog.CleanErrorLogRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.jre.JRERequirement.JRE;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.tools.fuse.reddeer.ProjectType;
import org.jboss.tools.fuse.reddeer.ResourceHelper;
import org.jboss.tools.fuse.reddeer.SupportedCamelVersions;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.reddeer.utils.FuseProjectDefinition;
import org.jboss.tools.fuse.reddeer.utils.FuseServerManipulator;
import org.jboss.tools.fuse.reddeer.utils.FuseShellSSH;
import org.jboss.tools.fuse.reddeer.utils.LogChecker;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizard;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardAdvancedPage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardFirstPage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimePage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Deployment test for all available templates to Standalone - Karaf, EAP
 * 
 * Use the following arguments:
 * <ul>
 * <li>-DfuseRuntimeType=... --- Karaf / EAP</li>
 * <li>-Drd.config=... --- e.g. .../fuse-7.0.0.GA/target/config/reddeer.yaml</li>
 * </ul>
 * 
 * @author djelinek
 *
 */
@JRE(setDefault = true)
@CleanWorkspace
@RunWith(RedDeerSuite.class)
@Fuse(state = RUNNING)
@OpenPerspective(FuseIntegrationPerspective.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class DeploymentTest {

	public static final String PROJECT_NAME = "deploy";
	public static final String DEPLOYMENT_TYPE = "Standalone";
	public static final String RUNTIME_TYPE = System.getProperty("fuseRuntimeType", "Karaf");

	// Deployment messages
	// Karaf
	public static final String PROJECT_IS_DEPLOYED_OLD = "Route: cbr-route started and consuming from: Endpoint[file://work/cbr/input]";
	public static final String PROJECT_IS_UNDEPLOYED_OLD = "Route: cbr-route shutdown complete, was consuming from: Endpoint[file://work/cbr/input]";
	public static final String PROJECT_IS_DEPLOYED = "Route: cbr-route started and consuming from: file://work/cbr/input";
	public static final String PROJECT_IS_UNDEPLOYED = "Route: cbr-route shutdown complete, was consuming from: file://work/cbr/input";
	public static final String PROJECT_IS_DEPLOYED_ACTIVEMQ = "Route: jms-cbr-route started and consuming from: Endpoint[activemq://incomingOrders]";
	public static final String PROJECT_IS_UNDEPLOYED_ACTIVEMQ = "Route: jms-cbr-route shutdown complete, was consuming from: Endpoint[activemq://incomingOrders]";

	// EAP
	public static final String PROJECT_EAP_IS_DEPLOYED = "(CamelContext: spring-context) started";
	public static final String PROJECT_EAP_IS_UNDEPLOYED = "(CamelContext: spring-context) is shutdown";
	public static final String BROWSER_URL = "http://localhost:8080/camel-test-spring";
	public static final String BROWSER_CONTENT_DEPLOYED = "Hello null";
	public static final String BROWSER_CONTENT_UNDEPLOYED = "404";
	
	public static enum DeploymentStatus {
		DEPLOY,
		UNDEPLOY
	}

	private FuseProjectDefinition project;

	@InjectRequirement
	private static FuseRequirement serverRequirement;

	/**
	 * Sets parameters for parameterized test
	 * 
	 * @return List of all available Fuse project archetypes
	 */
	@Parameters(name = "{0}")
	public static Collection<FuseProjectDefinition> setupData() {
		String camelVersion = serverRequirement.getConfiguration().getCamelVersion();
		NewFuseIntegrationProjectWizardDeploymentType deploymentType = STANDALONE;
		NewFuseIntegrationProjectWizardRuntimeType runtimeType;
		switch (RUNTIME_TYPE) {
		case "Karaf":
			runtimeType = KARAF;
			break;
		case "EAP":
			runtimeType = EAP;
			break;
		default:
			runtimeType = KARAF;
		}
		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		NewFuseIntegrationProjectWizardFirstPage firstPage = new NewFuseIntegrationProjectWizardFirstPage(wiz);
		firstPage.setProjectName(PROJECT_NAME);
		wiz.next();
		NewFuseIntegrationProjectWizardRuntimePage secondPage = new NewFuseIntegrationProjectWizardRuntimePage(wiz);
		secondPage.setDeploymentType(deploymentType);
		secondPage.setRuntimeType(runtimeType);
		String detectedCamelVersion = SupportedCamelVersions.getCamelVersionsWithLabels().get(camelVersion);
		if(detectedCamelVersion != null) {
			secondPage.selectCamelVersion(detectedCamelVersion);
		} else {
			secondPage.typeCamelVersion(camelVersion);
		}
		wiz.next();
		NewFuseIntegrationProjectWizardAdvancedPage lastPage = new NewFuseIntegrationProjectWizardAdvancedPage(wiz);
		List<FuseProjectDefinition> projects = new ArrayList<>();
		for (String[] template : lastPage.getAllAvailableTemplates()) {
			ProjectType dsl;
			if (template[template.length - 1].toLowerCase().contains("blueprint")) {
				dsl = ProjectType.BLUEPRINT;
			} else if (template[template.length - 1].toLowerCase().contains("spring")) {
				dsl = ProjectType.SPRING;
			} else {
				dsl = ProjectType.JAVA;
			}
			FuseProjectDefinition tmp = new FuseProjectDefinition(runtimeType, deploymentType, template, camelVersion,
					dsl);
			projects.add(tmp);
		}
		wiz.cancel();
		return projects;
	}

	/**
	 * Utilizes passing parameters using the constructor
	 * 
	 * @param template
	 *                     a Fuse project archetype
	 */
	public DeploymentTest(FuseProjectDefinition project) {
		this.project = project;
	}

	@Before
	public void prepareWorkspace() {
		new CleanErrorLogRequirement().fulfill();
		ProjectFactory.newProject(PROJECT_NAME).deploymentType(project.getDeploymentType())
				.runtimeType(project.getRuntimeType()).version(project.getCamelVersion())
				.template(project.getTemplate()).create();
		new CamelProject(PROJECT_NAME).update();
	}

	@After
	public void cleanWorkspace() {
		FuseServerManipulator.removeAllModules(serverRequirement.getConfiguration().getServer().getName());
		new CleanWorkspaceRequirement().fulfill();
	}

	@AfterClass
	public static void cleanServers() {
		String serverName = serverRequirement.getConfiguration().getServer().getName();
		FuseServerManipulator.removeAllModules(serverName);
		FuseServerManipulator.stopServer(serverName);
		FuseServerManipulator.removeServer(serverName);
	}
	
	@Rule
	public ErrorCollector collector = new ErrorCollector();

	/**
	 * <p>
	 * Test tries to deploy a project on Fuse on Karaf server.
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>create a new project for each available template</li>
	 * <li>add a new Fuse server</li>
	 * <li>add the project to the server</li>
	 * <li>check if the server contains the project in Add and Remove ... dialog window</li>
	 * <li>start the server</li>
	 * <li>open Fuse Shell view and execute command log:display</li>
	 * <li>check if Fuse Shell view contains text Route: cbr-route started and consuming from:
	 * Endpoint[file://work/cbr/input] (project is deployed)</li>
	 * <li>remove all deployed modules</li>
	 * <li>open Fuse Shell view and execute command log:display</li>
	 * <li>check if Fuse Shell view contains text Route: cbr-route shutdown complete, was consuming from:
	 * Endpoint[file://work/cbr/input] (project is undeployed)</li>
	 * </ol>
	 * 
	 * <p>
	 * Test tries to deploy a project on Fuse on EAP server.
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>create a new project for each available template</li>
	 * <li>add a new Fuse on EAP server</li>
	 * <li>add the project to the server</li>
	 * <li>check if the server contains the project in Add and Remove ... dialog window</li>
	 * <li>start the server</li>
	 * <li>open Fuse Shell view and execute command log:display</li>
	 * <li>check if Fuse Shell view contains text "(CamelContext: spring-context) started"</li>
	 * <li>open Browser View and try to open URL "http://localhost:8080/wildfly-spring"</li>
	 * <li>remove all deployed modules</li>
	 * <li>open Fuse Shell view and execute command log:display</li>
	 * <li>check if Fuse Shell view contains text "(CamelContext: spring-context) is shutdown"</li>
	 * </ol>
	 */
	@Test
	public void testDeployment() {
		String serverName = serverRequirement.getConfiguration().getServer().getName();
		if (isActiveMQ()) {
			prepareEnvironment();
		}

		// deploy project (add module...)
		FuseServerManipulator.addModule(serverName, PROJECT_NAME);
		collector.checkThat("Deploy project module failed!", FuseServerManipulator.hasServerModule(serverName, PROJECT_NAME), equalTo(true));

		// check deployed
		switch (RUNTIME_TYPE) {
		case "Karaf":
			assertKaraf("The project was not properly deployed!", DeploymentStatus.DEPLOY);
			break;
		case "EAP":
			assertEAP(PROJECT_EAP_IS_DEPLOYED, BROWSER_CONTENT_DEPLOYED);
			break;
		}

		// check error log for Fuse errors
		collector.checkThat("Console contains some 'fuse' errors!", LogChecker.noFuseError(), equalTo(true));

		// undeploy project (remove module...)
		FuseServerManipulator.removeAllModules(serverName);
		collector.checkThat("Undeploy project module failed!", FuseServerManipulator.hasServerModule(serverName, PROJECT_NAME), equalTo(false));

		// check undeployed
		switch (RUNTIME_TYPE) {
		case "Karaf":
			assertKaraf("The project was not properly undeployed!", DeploymentStatus.UNDEPLOY);
			break;
		case "EAP":
			assertEAP(PROJECT_EAP_IS_UNDEPLOYED, BROWSER_CONTENT_UNDEPLOYED);
			break;
		}

		// check error log for Fuse errors
		collector.checkThat("Console contains some 'fuse' errors!", LogChecker.noFuseError(), equalTo(true));
	}

	private void assertEAP(String consoleMessage, String browserMessage) {
		new WaitUntil(new ConsoleHasText(consoleMessage), TimePeriod.LONG);
		WebBrowserView browser = new WebBrowserView();
		browser.open();
		browser.openPageURL(BROWSER_URL);
		collector.checkThat("EAP project errors!", browser.getText().contains(browserMessage), equalTo(true));
	}

	private void assertKaraf(String message, DeploymentStatus status) {
		if (status == DeploymentStatus.DEPLOY) {
			if (isActiveMQ()) {
				collector.checkThat(message, new FuseShellSSH().containsLog(PROJECT_IS_DEPLOYED_ACTIVEMQ), equalTo(true));
			} else {
				collector.checkThat(message, new FuseShellSSH().containsLog(
						serverRequirement.getConfiguration().getServer().getVersion().startsWith("7")
						? PROJECT_IS_DEPLOYED
						: PROJECT_IS_DEPLOYED_OLD), equalTo(true));
			}
		} else {
			if (isActiveMQ()) {
				collector.checkThat(message, new FuseShellSSH().containsLog(PROJECT_IS_UNDEPLOYED_ACTIVEMQ), equalTo(true));
			} else {
				collector.checkThat(message, new FuseShellSSH().containsLog(
						serverRequirement.getConfiguration().getServer().getVersion().startsWith("7")
						? PROJECT_IS_UNDEPLOYED
						: PROJECT_IS_UNDEPLOYED_OLD), equalTo(true));
			}
		}
	}

	/**
	 * Necessary steps for ActiveMQ project template deployment
	 */
	private void prepareEnvironment() {
		// install dependencies from Fuse shell:
		new FuseShellSSH().execute("features:install activemq-camel");

		// create a properties file in '/etc' folder of Red Hat Fuse containing ActiveMQ
		String serverPath = serverRequirement.getConfiguration().getServer().getHome();
		File source = new File(
				ResourceHelper.getResourceAbsolutePath(PLUGIN_ID, "/resources/config/deploymentTest-activemq.cfg"));
		File dest;
		if (project.getTemplate()[1].contains("Spring")) {
			dest = new File(serverPath + "/etc/camel.activemq.spring.cfg");
		} else {
			dest = new File(serverPath + "/etc/camel.activemq.blueprint.cfg");
		}

		try {
			Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private boolean isActiveMQ() {
		if (project.getTemplate().length > 1) {
			if (project.getTemplate()[1].startsWith("ActiveMQ"))
				return true;
		}
		return false;
	}

}
