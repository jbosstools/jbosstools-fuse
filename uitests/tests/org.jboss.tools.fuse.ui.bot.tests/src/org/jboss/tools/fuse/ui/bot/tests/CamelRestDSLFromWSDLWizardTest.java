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

import static org.jboss.tools.fuse.reddeer.ProjectTemplate.CBR_SPRING;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanerrorlog.CleanErrorLogRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.condition.ControlIsEnabled;
import org.eclipse.reddeer.swt.impl.button.FinishButton;
import org.eclipse.reddeer.swt.impl.button.NextButton;
import org.eclipse.reddeer.swt.impl.button.PredefinedButton;
import org.jboss.tools.fuse.reddeer.ResourceHelper;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.utils.LogChecker;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.jboss.tools.fuse.reddeer.wizard.CamelRestDSLFromWSDLAdvancedPage;
import org.jboss.tools.fuse.reddeer.wizard.CamelRestDSLFromWSDLFirstPage;
import org.jboss.tools.fuse.reddeer.wizard.CamelRestDSLFromWSDLWizard;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests <i>Camel Rest DSL from WSDL</i> wizard<br/>
 * <b>Quick tests:</b><br/>
 * First wizard page:
 * <ul>
 * <li>testExampleWSDLFile</li>
 * <li>testEmptyWSDLFile</li>
 * <li>testInvalidWSDLFile</li>
 * <li>testEnablingDisablingButtons</li>
 * </ul>
 * Second wizard page:
 * <ul>
 * <li>testDestinationCamelFolder</li>
 * <li>testDestinationCamelFolderContainerSelection</li>
 * <li>testDestinationJavaFolder</li>
 * <li>testDestinationJavaFolderContainerSelection</li>
 * </ul>
 * 
 * @author djelinek
 *
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class CamelRestDSLFromWSDLWizardTest {

	public static final String PROJECT_NAME = "wsdl2rest";
	public static final String WSDL_FILE_EXAMPLE_PATH = "resources/wsdl2rest/WSDLFileExample.wsdl";
	public static final String WSDL_FILE_EXAMPLE_ERROR_PATH = "resources/wsdl2rest/WSDLFileExample";
	public static final String WSDL_FILE_EMPTY_PATH = "resources/wsdl2rest/WSDLFileEmpty.wsdl";
	public static final String WSDL_FILE_INVALID_PATH = "resources/wsdl2rest/WSDLFileInvalid.wsdl";
	public static final String[] DESTINATION_JAVA_FOLDER_PATH = { "src", "main" };
	public static final String[] DESTINATION_CAMEL_FOLDER_PATH = { "src", "main", "resources", "META-INF", "spring" };

	private String path;
	private CamelRestDSLFromWSDLWizard wizard;
	private CamelRestDSLFromWSDLFirstPage firstPage;
	private CamelRestDSLFromWSDLAdvancedPage secondPage;

	@Before
	public void clean() {
		new CleanWorkspaceRequirement().fulfill();
		new CleanErrorLogRequirement().fulfill();
	}

	@Before
	public void initSetup() {
		new CleanWorkspaceRequirement().fulfill();
		new CleanErrorLogRequirement().fulfill();
		ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(KARAF).template(CBR_SPRING)
				.create();

		// should have ensure pre-filling 'Destination Project' wizard field
		new ProjectExplorer().selectProjects(PROJECT_NAME);
		wizard = new CamelRestDSLFromWSDLWizard();
		wizard.open();
		firstPage = new CamelRestDSLFromWSDLFirstPage(wizard);
	}

	/**
	 * <p>
	 * Test tries to 'Finish' wizard with path to 'example' of WSDL file
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Verifies that 'Destination Project' field was pre-filled</li>
	 * <li>Set 'wrong' path to 'example' WSDL file</li>
	 * <li>Verifies that wizard 'Finish' button is disabled</li>
	 * <li>Set 'valid' path to 'example' WSDL file</li>
	 * <li>Verifies that wizard 'Finish' button is enabled</li>
	 * <li>Tries to 'Finish' wizard</li>
	 * <li>Verifies 'Error log' to 'fuse' error</li>
	 * </ol>
	 */
	@Test
	public void testExampleWSDLFile() {
		assertDestinationProjectFieldCompletion();

		path = ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, WSDL_FILE_EXAMPLE_ERROR_PATH);
		firstPage.setTextWSDLFile("file:" + path);
		assertNextButtonEnabled(false);
		assertFinishButtonEnabled(false);

		path = ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, WSDL_FILE_EXAMPLE_PATH);
		firstPage.setTextWSDLFile("file:" + path);
		assertFinishButtonEnabled(false);
		assertNextButtonEnabled(true);

		wizard.cancel();
		LogChecker.assertNoFuseError();
	}

	/**
	 * <p>
	 * Test tries to 'Finish' wizard with path to 'empty' WSDL file (Please see
	 * https://issues.jboss.org/browse/FUSETOOLS-3001)
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Verifies that 'Destination Project' field was pre-filled</li>
	 * <li>Set path to 'empty' WSDL file</li>
	 * <li>Verifies that wizard 'Finish' button is disabled</li>
	 * <li>Click 'Next' button</li>
	 * <li>Verifies that wizard 'Finish' button is disabled</li>
	 * <li>Verifies 'Error log' to 'fuse' error</li>
	 * </ol>
	 */
	@Test
	public void testEmptyWSDLFile() {
		assertDestinationProjectFieldCompletion();

		path = ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, WSDL_FILE_EMPTY_PATH);
		firstPage.setTextWSDLFile("file:" + path);
		assertFinishButtonEnabled(false);
		wizard.next();
		AbstractWait.sleep(TimePeriod.MEDIUM);
		boolean finishEnabled = wizard.isFinishEnabled();
		wizard.cancel();
		assumeFalse("https://issues.jboss.org/browse/FUSETOOLS-3001", finishEnabled);
		LogChecker.assertNoFuseError();
	}

	/**
	 * <p>
	 * Test tries to 'Finish' wizard with path to 'invalid' WSDL file (Please see
	 * https://issues.jboss.org/browse/FUSETOOLS-3001)
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Verifies that 'Destination Project' field was pre-filled</li>
	 * <li>Set path to 'invalid' WSDL file</li>
	 * <li>Verifies that wizard 'Finish' button is disabled</li>
	 * <li>Click 'Next' button</li>
	 * <li>Verifies that wizard 'Finish' button is disabled</li>
	 * <li>Verifies 'Error log' to 'fuse' error</li>
	 * </ol>
	 */
	@Test
	public void testInvalidWSDLFile() {
		assertDestinationProjectFieldCompletion();

		path = ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, WSDL_FILE_INVALID_PATH);
		firstPage.setTextWSDLFile("file:" + path);
		assertFinishButtonEnabled(false);
		wizard.next();
		AbstractWait.sleep(TimePeriod.MEDIUM);
		boolean finishEnabled = wizard.isFinishEnabled();
		wizard.cancel();
		assumeFalse("https://issues.jboss.org/browse/FUSETOOLS-3001", finishEnabled);

		LogChecker.assertNoFuseError();
	}

	/**
	 * <p>
	 * Tests verifies enabling/disabling wizard buttons after field validation
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Verifies that 'Destination Project' field was pre-filled</li>
	 * <li>Set path to 'example' WSDL file</li>
	 * <li>Verifies that 'Finish' button is enabled</li>
	 * <li>Set 'Destination Project' field to null</li>
	 * <li>Verifies that 'Finish/Next' button is disabled</li>
	 * <li>Set 'Destination Project' field back to 'project name'</li>
	 * <li>Verifies that 'Finish/Next' button is enabled</li>
	 * <li>Tries to 'Finish' wizard</li>
	 * <li>Verifies 'Error log' to 'fuse' error</li>
	 * </ol>
	 */
	@Test
	public void testEnablingDisablingButtons() {
		assertDestinationProjectFieldCompletion();
		assertNextButtonEnabled(false);

		path = ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, WSDL_FILE_EXAMPLE_PATH);
		firstPage.setTextWSDLFile("file:" + path);
		assertNextButtonEnabled(true);
		assertFinishButtonEnabled(false);

		firstPage.setTextDestinationProject("");
		assertNextButtonEnabled(false);
		assertFinishButtonEnabled(false);

		firstPage.setTextDestinationProject(PROJECT_NAME);
		assertNextButtonEnabled(true);
		assertFinishButtonEnabled(false);

		wizard.cancel();
		LogChecker.assertNoFuseError();
	}

	/**
	 * <p>
	 * Test verifies validation of 'Destination Camel Folder' field of the 2nd wizard page
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Verifies that 'Destination Project' field was pre-filled</li>
	 * <li>Set path to 'example' WSDL file</li>
	 * <li>Click 'Next' to second page</li>
	 * <li>Set 'Destination Camel Folder' field to null</li>
	 * <li>Verifies that 'Finish' button is disabled</li>
	 * <li>Set 'Destination Camel Folder' field back</li>
	 * <li>Verifies that 'Finish' button is enabled</li>
	 * <li>Tries to 'Finish' wizard</li>
	 * <li>Verifies 'Error log' to 'fuse' error</li>
	 * </ol>
	 */
	@Test
	public void testDestinationCamelFolder() {
		assertDestinationProjectFieldCompletion();

		path = ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, WSDL_FILE_EXAMPLE_PATH);
		firstPage.setTextWSDLFile("file:" + path);
		wizard.next();

		secondPage = new CamelRestDSLFromWSDLAdvancedPage(wizard);
		String tmp = secondPage.getTextDestinationCamelFolder();
		secondPage.setTextDestinationCamelFolder("");
		assertButtonsEnabled(false);
		secondPage.setTextDestinationCamelFolder(tmp);
		waitBTNCondition(true, new FinishButton(wizard));
		assertTrue(wizard.isFinishEnabled());

		wizard.finish(TimePeriod.DEFAULT);
		LogChecker.assertNoFuseError();
	}
	
	/**
	 * <p>
	 * Test verifies validation of 'Destination Camel Folder' field of the 2nd wizard page (container selection via '...' button)
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Verifies that 'Destination Project' field was pre-filled</li>
	 * <li>Set path to 'example' WSDL file</li>
	 * <li>Click 'Next' to second page</li>
	 * <li>Set 'Destination Camel Folder' field to null</li>
	 * <li>Verifies that 'Finish' button is disabled</li>
	 * <li>Set 'Destination Camel Folder' field back via '...' button and 'Container Selection' dialog</li>
	 * <li>Verifies that 'Finish' button is enabled</li>
	 * <li>Tries to 'Finish' wizard</li>
	 * <li>Verifies 'Error log' to 'fuse' error</li>
	 * </ol>
	 */
	@Test
	public void testDestinationCamelFolderContainerSelection() {
		assertDestinationProjectFieldCompletion();

		path = ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, WSDL_FILE_EXAMPLE_PATH);
		firstPage.setTextWSDLFile("file:" + path);
		wizard.next();

		secondPage = new CamelRestDSLFromWSDLAdvancedPage(wizard);
		secondPage.setTextDestinationCamelFolder("");
		assertButtonsEnabled(false);
		secondPage.clickDestinationCamelFolderBTN(DESTINATION_CAMEL_FOLDER_PATH);
		waitBTNCondition(true, new FinishButton(wizard));
		assertTrue(wizard.isFinishEnabled());

		wizard.finish(TimePeriod.DEFAULT);
		LogChecker.assertNoFuseError();
	}

	/**
	 * <p>
	 * Test verifies validation of 'Destination Java Folder' field of the 2nd wizard page
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Verifies that 'Destination Project' field was pre-filled</li>
	 * <li>Set path to 'example' WSDL file</li>
	 * <li>Click 'Next' to second page</li>
	 * <li>Set 'Destination Java Folder' field to null</li>
	 * <li>Verifies that 'Finish' button is disabled</li>
	 * <li>Set 'Destination Java Folder' field back</li>
	 * <li>Verifies that 'Finish' button is enabled</li>
	 * <li>Tries to 'Finish' wizard</li>
	 * <li>Verifies 'Error log' to 'fuse' error</li>
	 * </ol>
	 */
	@Test
	public void testDestinationJavaFolder() {
		assertDestinationProjectFieldCompletion();

		path = ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, WSDL_FILE_EXAMPLE_PATH);
		firstPage.setTextWSDLFile("file:" + path);
		wizard.next();

		secondPage = new CamelRestDSLFromWSDLAdvancedPage(wizard);
		String tmp = secondPage.getTextDestinationJavaFolder();
		secondPage.setTextDestinationJavaFolder("");
		assertButtonsEnabled(false);
		secondPage.setTextDestinationJavaFolder(tmp);
		waitBTNCondition(true, new FinishButton(wizard));
		assertTrue(wizard.isFinishEnabled());

		wizard.finish(TimePeriod.DEFAULT);
		LogChecker.assertNoFuseError();
	}
	
	/**
	 * <p>
	 * Test verifies validation of 'Destination Java Folder' field of the 2nd wizard page (container selection via '...' button)
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>Verifies that 'Destination Project' field was pre-filled</li>
	 * <li>Set path to 'example' WSDL file</li>
	 * <li>Click 'Next' to second page</li>
	 * <li>Set 'Destination Java Folder' field to null</li>
	 * <li>Verifies that 'Finish' button is disabled</li>
	 * <li>Set 'Destination Java Folder' field back via '...' button and 'Container Selection' dialog</li>
	 * <li>Verifies that 'Finish' button is enabled</li>
	 * <li>Tries to 'Finish' wizard</li>
	 * <li>Verifies 'Error log' to 'fuse' error</li>
	 * </ol>
	 */
	@Test
	public void testDestinationJavaFolderContainerSelection() {
		assertDestinationProjectFieldCompletion();

		path = ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, WSDL_FILE_EXAMPLE_PATH);
		firstPage.setTextWSDLFile("file:" + path);
		wizard.next();

		secondPage = new CamelRestDSLFromWSDLAdvancedPage(wizard);
		secondPage.setTextDestinationJavaFolder("");
		assertButtonsEnabled(false);
		secondPage.clickDestinationJavaFolderBTN(DESTINATION_JAVA_FOLDER_PATH);
		secondPage.setTextDestinationJavaFolder(secondPage.getTextDestinationJavaFolder() + "/java");
		waitBTNCondition(true, new FinishButton(wizard));
		assertTrue(wizard.isFinishEnabled());

		wizard.finish(TimePeriod.DEFAULT);
		LogChecker.assertNoFuseError();
	}

	private void assertDestinationProjectFieldCompletion() {
		assertNotEquals("Field 'Destination Project' was not correctly pre-filled", "",
				firstPage.getTextDestinationProject());
	}

	private void assertButtonsEnabled(boolean enabled) {
		waitBTNCondition(enabled, new NextButton(wizard));
		waitBTNCondition(enabled, new FinishButton(wizard));
		boolean next = wizard.isNextEnabled();
		boolean finish = wizard.isFinishEnabled();
		if (enabled) {
			assertTrue(finish && next);
		} else {
			assertFalse(finish && next);
		}
	}
	
	private void assertNextButtonEnabled(boolean enabled) {
		waitBTNCondition(enabled, new NextButton(wizard));
		boolean next = wizard.isNextEnabled();
		if (enabled) {
			assertTrue(next);
		} else {
			assertFalse(next);
		}
	}

	private void assertFinishButtonEnabled(boolean enabled) {
		waitBTNCondition(enabled, new FinishButton(wizard));
		boolean finish = wizard.isFinishEnabled();
		if (enabled) {
			assertTrue(finish);
		} else {
			assertFalse(finish);
		}
	}

	private void waitBTNCondition(boolean enabled, PredefinedButton btn) {
		ControlIsEnabled controlIsEnabled = new ControlIsEnabled(btn);
		if (enabled) {
			new WaitUntil(controlIsEnabled, TimePeriod.DEFAULT);
		} else {
			new WaitWhile(controlIsEnabled, TimePeriod.DEFAULT);
		}
	}

}
