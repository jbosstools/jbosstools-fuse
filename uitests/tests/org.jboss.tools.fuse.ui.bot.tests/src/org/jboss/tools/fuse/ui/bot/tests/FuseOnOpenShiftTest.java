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

import static org.jboss.tools.fuse.reddeer.ProjectTemplate.SPRINGBOOT;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.OPENSHIFT;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.debug.ui.launchConfigurations.RunConfigurationsDialog;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.views.log.LogView;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.launchconfigurations.DefaultLaunchConfigurationJRETab;
import org.jboss.tools.fuse.reddeer.launchconfigurations.MavenBuildLaunchConfiguration;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.preference.ConsolePreferenceUtil;
import org.jboss.tools.fuse.reddeer.requirement.OpenShiftRequirement;
import org.jboss.tools.fuse.reddeer.requirement.OpenShiftRequirement.OpenShift;
import org.jboss.tools.fuse.reddeer.utils.LogChecker;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.jboss.tools.fuse.reddeer.view.OpenShiftExplorer;
import org.jboss.tools.fuse.reddeer.wizard.CreateOpenShiftProjectWizard;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests deployment of a Fuse Integration Project to OpenShift
 *
 * Using OCP 4.6 CodeReady Studio QE team cluster - https://console-openshift-console.apps.openshift4.cluster.adapters-crs-qe.com/
 * 
 * @author tsedmik
 */
@RunWith(RedDeerSuite.class)
@OpenShift
@OpenPerspective(FuseIntegrationPerspective.class)
public class FuseOnOpenShiftTest {

	public static final String BUILD_OK = "BUILD SUCCESS";
	public static final String DEPLOYMENT_OK2 = "timer://foo] INFO  simple-route - >>>";
	public static final String DEPLOYMENT_OK = "INFO  org.mycompany.Application - Started Application in";
	public static final String OPENSHIFT_DEPLOYMENT = "camel-ose-springboot-xml";
	public static final String OPENSHIFT_PROJECT_NAME = "fuse-tooling-test";
	public static final String PROJECT_NAME = "test-ose";
	public static final String MAVEN_BUILD_CONF = "Deploy " + PROJECT_NAME + " on OpenShift";
	public static final String IP_MATCH_REGEX = "https:\\/\\/(?:[0-9]{1,3}.){4}:[0-9]{1,4}";
	private final String USERNAME = openShift.getConfiguration().getUsername();
	private final String PASSWORD = openShift.getConfiguration().getPassword();
	private final String OPENSHIFT_URL = "https://" + openShift.getConfiguration().getHost() + ":"
			+ openShift.getConfiguration().getPort();
	private final String OPENSHIFT_CONNECTION = USERNAME + " " + OPENSHIFT_URL;

	@InjectRequirement
	private static OpenShiftRequirement openShift;

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void defaultClassSetup() {

		// Maximizing workbench shell
		new WorkbenchShell().maximize();

		// Disable showing Console view after standard output changes
		ConsolePreferenceUtil.setConsoleOpenOnError(false);
		ConsolePreferenceUtil.setConsoleOpenOnOutput(false);

		// Disable showing Error Log view after changes
		new LogView().open();
		new LogView().setActivateOnNewEvents(false);
	}

	@After
	public void deleteProjects() {

		new ProjectExplorer().deleteAllProjects();
		OpenShiftExplorer explorer = new OpenShiftExplorer();
		explorer.open();
		explorer.selectConnection(OPENSHIFT_CONNECTION);
		explorer.deleteProject(OPENSHIFT_PROJECT_NAME);
	}

	/**
	 * <p>
	 * Test tries to deploy a project on Fuse on OpenShift (FIS).
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>create a new Fuse Integration Project ("SpringBoot on OpenShift" template, the latest Camel version)</li>
	 * <li>create a new connection in OpenShift Explorer</li>
	 * <li>create a new project on OpenShift - test</li>
	 * <li>open Maven Build launch configuration "Deploy ${project.name} on OpenShift"</li>
	 * <li>change VM arguments 'kubernetes.master' and 'kubernetes.auth.basic.username'</li>
	 * <li>run the build and wait until the end of the build</li>
	 * <li>check the deployment</li>
	 * </ol>
	 */
	@Test
	public void testDeploymentToOpenShift() {

		// create a new Fuse Integration Project
		ProjectFactory.newProject(PROJECT_NAME).deploymentType(OPENSHIFT).template(SPRINGBOOT).create();

		// create a new project on OpenShift
		OpenShiftExplorer explorer = new OpenShiftExplorer();

		explorer.open();
		explorer.selectConnection(OPENSHIFT_CONNECTION);
		explorer.clickNewProject();
		CreateOpenShiftProjectWizard projectWizard = new CreateOpenShiftProjectWizard();
		projectWizard.setTextProjectName(OPENSHIFT_PROJECT_NAME);
		projectWizard.finish();

		// open Maven Build launch configuration "Deploy ${project.name} on OpenShift"
		RunConfigurationsDialog dialog = new RunConfigurationsDialog();
		dialog.open();
		dialog.select(new MavenBuildLaunchConfiguration(), MAVEN_BUILD_CONF);
		DefaultLaunchConfigurationJRETab jreTab = new DefaultLaunchConfigurationJRETab();
		jreTab.activate();

		// change VM arguments 'kubernetes.master' and 'kubernetes.auth.basic.username'
		String vmArgs = jreTab.getTextVMArgumentsTXT();
		vmArgs = vmArgs.replaceFirst(IP_MATCH_REGEX, OPENSHIFT_URL);
		vmArgs = vmArgs.replaceFirst("test", OPENSHIFT_PROJECT_NAME);
		vmArgs = vmArgs.replaceFirst("developer", USERNAME);
		vmArgs = vmArgs.replaceFirst("developer", PASSWORD);
		jreTab.setTextVMArgumentsTXT(vmArgs);
		jreTab.clickApplyBTN();

		// run the build and wait until the end of the build
		dialog.run();
		new ConsoleView().open();
		new WaitUntil(new ConsoleHasText(BUILD_OK), TimePeriod.getCustom(1200));

		// check the deployment
		explorer.activate();
		explorer.openPodLog(OPENSHIFT_CONNECTION, OPENSHIFT_PROJECT_NAME, OPENSHIFT_DEPLOYMENT);
		new WaitUntil(new ConsoleHasText(DEPLOYMENT_OK), TimePeriod.VERY_LONG, false);
		explorer.activate();
		explorer.openPodLog(OPENSHIFT_CONNECTION, OPENSHIFT_PROJECT_NAME, OPENSHIFT_DEPLOYMENT);
		new WaitUntil(new ConsoleHasText(DEPLOYMENT_OK2), TimePeriod.VERY_LONG, false);
		LogChecker.assertNoFuseError();
	}
}
