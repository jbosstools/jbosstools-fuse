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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView.ProblemType;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIgniteExtensionProjectFirstPage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIgniteExtensionProjectSecondPage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIgniteExtensionProjectWizard;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests "New Fuse Ignite Extension Project Wizard"<br/>
 * <i>Long</i> - tests that created projects are ok
 * 
 * @author tsedmik
 */
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class NewFuseIgniteProjectTest extends DefaultTest {

	@Before
	public void setupDeleteProjects() {
		ProjectFactory.deleteAllProjects();
	}

	/**
	 * <p>
	 * Tests New Fuse Ignite Extension project - Custom Step - Camel Route
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open <i>New Fuse Ignite Extension Project</i> wizard</li>
	 * <li>Create a new project - Custom step - Camel Route</li>
	 * <li>Check that problems view does not contain any problem</li>
	 * <li>Check error log for fuse errors</li>
	 * <li>Run Maven build clean verify</li>
	 * <li>Check that build ended with SUCCESS</li>
	 * </ol>
	 */
	@Test
	public void testCustomStepCamelRoute() {
		NewFuseIgniteExtensionProjectWizard wizard = new NewFuseIgniteExtensionProjectWizard();
		wizard.open();
		NewFuseIgniteExtensionProjectFirstPage firstPage = new NewFuseIgniteExtensionProjectFirstPage(wizard);
		firstPage.setTextProjectName("CustomStepCamelRoute");
		AbstractWait.sleep(TimePeriod.SHORT);
		wizard.finish(TimePeriod.VERY_LONG);
		checkProject("CustomStepCamelRoute");
	}

	/**
	 * <p>
	 * Tests New Fuse Ignite Extension project - Custom Step - Java Route
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open <i>New Fuse Ignite Extension Project</i> wizard</li>
	 * <li>Create a new project - Custom step - Camel Route</li>
	 * <li>Check that problems view does not contain any problem</li>
	 * <li>Check error log for fuse errors</li>
	 * <li>Run Maven build clean verify</li>
	 * <li>Check that build ended with SUCCESS</li>
	 * </ol>
	 */
	@Test
	public void testCustomStepJavaRoute() {
		NewFuseIgniteExtensionProjectWizard wizard = new NewFuseIgniteExtensionProjectWizard();
		wizard.open();
		NewFuseIgniteExtensionProjectFirstPage firstPage = new NewFuseIgniteExtensionProjectFirstPage(wizard);
		firstPage.setTextProjectName("CustomStepJavaRoute");
		wizard.next();
		NewFuseIgniteExtensionProjectSecondPage secondPage = new NewFuseIgniteExtensionProjectSecondPage(wizard);
		secondPage.toggleJavaBeanRDB(true);
		AbstractWait.sleep(TimePeriod.SHORT);
		wizard.finish(TimePeriod.VERY_LONG);
		checkProject("CustomStepJavaRoute");
	}

	/**
	 * <p>
	 * Tests New Fuse Ignite Extension project - Custom Connector
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Open <i>New Fuse Ignite Extension Project</i> wizard</li>
	 * <li>Create a new project - Custom step - Camel Route</li>
	 * <li>Check that problems view does not contain any problem</li>
	 * <li>Check error log for fuse errors</li>
	 * <li>Run Maven build clean verify</li>
	 * <li>Check that build ended with SUCCESS</li>
	 * </ol>
	 */
	@Test
	public void testCustomConnector() {
		NewFuseIgniteExtensionProjectWizard wizard = new NewFuseIgniteExtensionProjectWizard();
		wizard.open();
		NewFuseIgniteExtensionProjectFirstPage firstPage = new NewFuseIgniteExtensionProjectFirstPage(wizard);
		firstPage.setTextProjectName("CustomConnector");
		wizard.next();
		NewFuseIgniteExtensionProjectSecondPage secondPage = new NewFuseIgniteExtensionProjectSecondPage(wizard);
		secondPage.toggleCustomConnectorRDB(true);
		AbstractWait.sleep(TimePeriod.SHORT);
		wizard.finish(TimePeriod.VERY_LONG);
		checkProject("CustomConnector");
	}

	private void checkProject(String name) {
		assertTrue("Project 'CustomStepCamelRoute' is not present in Project Explorer", new ProjectExplorer().containsProject(name));
		if (hasErrors()) {
			new CamelProject(name).update();
		}
		assertFalse("Project '" + name + "' was created with errors", hasErrors());
		new ProjectExplorer().getProject(name).getResource().runAs("Maven clean verify");
		new WaitUntil(new ConsoleHasText("BUILD SUCCESS"), TimePeriod.VERY_LONG);
	}

	private boolean hasErrors() {

		ProblemsView view = new ProblemsView();
		view.open();
		view.close();
		AbstractWait.sleep(TimePeriod.getCustom(5));
		view.open();
		return !view.getProblems(ProblemType.ERROR).isEmpty();
	}
}
