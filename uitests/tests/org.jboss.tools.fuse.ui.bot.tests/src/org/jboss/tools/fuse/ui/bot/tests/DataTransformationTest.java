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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.reddeer.common.exception.WaitTimeoutExpiredException;
import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.condition.JUnitHasFinished;
import org.eclipse.reddeer.eclipse.jdt.junit.ui.TestRunnerViewPart;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.menu.ShellMenuItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.table.DefaultTableItem;
import org.eclipse.reddeer.workbench.impl.editor.TextEditor;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.JiraIssue;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseTransformationTestWizard;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test covers Data Transformation Tooling
 * <p>
 * Use the following arguments to specify Fuse Integration Project:
 * <ul>
 * <li>-DfuseDeploymentType=... --- OpenShift / Standalone</li>
 * <li>-DfuseRuntimeType=... --- SpringBoot / Karaf / EAP</li>
 * <li>-DfuseCamelVersion=... --- e.g. 2.18.1.redhat-000012</li>
 * <li>-DfuseDSL=... --- Blueprint / Spring</li> --> applicable only for Standalone / Karaf combination
 * </ul>
 * </p>
 * 
 * @author tsedmik
 */
@RunWith(RedDeerSuite.class)
@OpenPerspective(FuseIntegrationPerspective.class)
public class DataTransformationTest extends DataTransformationDefaultTest {

	@Before
	public void setupEnvironment() throws IOException, CoreException {
		createProject(PROJECT_NAME);
		copyResources(PROJECT_NAME);
		configureRoute();
	}

	/**
	 * <p>
	 * Basic Test
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>create a new Fuse Integration Project</li>
	 * <li>create a new Data Transformation</li>
	 * <li>modify the Camel Route (connect Data Transformation node)</li>
	 * <li>create a new Data Transformation test</li>
	 * <li>run test and verify output</li>
	 * </ol>
	 */
	@Test
	public void testBasics() {
		addDataTransformation();
		createMapping();
		createAndRunTest();
	}

	private void createAndRunTest() throws JiraIssue {
		new CamelProject(PROJECT_NAME).selectFirstCamelContext();
		AbstractWait.sleep(TimePeriod.SHORT);
		NewFuseTransformationTestWizard test = new NewFuseTransformationTestWizard();
		test.open();
		try {
			new WaitUntil(new ShellIsAvailable("New Fuse Transformation Test"));
		} catch (WaitTimeoutExpiredException e) {
			fail("'New Fuse Transformation Test' wizard is not opened!");
		}
		test.setPackage("example");
		test.selectTransformationID("xml2json");
		try {
			test.finish();
		} catch (WaitTimeoutExpiredException e) {
			if (new ShellIsAvailable("New Fuse Transformation Test").test()) {
				throw new JiraIssue("FUSETOOLS-2398");
			}
		}
		TextEditor javaEditor = new TextEditor("TransformationTest.java");
		javaEditor.insertLine(javaEditor.getLineOfText("transform()") + 1,
				"startEndpoint.sendBodyAndHeader(readFile(\"src/data/abc-order.xml\"), \"approvalID\", \"AUTO_OK\");");
		javaEditor.save();
		new ShellMenuItem(new WorkbenchShell(), "Run", "Run").select();
		new WaitUntil(new ShellIsAvailable("Run As"));
		new DefaultShell("Run As");
		new DefaultTableItem("JUnit Test").select();
		new PushButton("OK").click();
		new WaitUntil(new JUnitHasFinished(), TimePeriod.getCustom(20));
		new WorkbenchShell();
		assertEquals("Result of JUnit test is wrong", "1/1", new TestRunnerViewPart().getRunStatus());
		new WaitUntil(new ConsoleHasText(
				"{\"custId\":\"ACME-123\",\"priority\":\"GOLD\",\"orderId\":\"ORDER1\",\"origin\":\"ORIGIN\",\"approvalCode\":\"AUTO_OK\",\"lineItems\":[{\"itemId\":\"PICKLE\",\"amount\":1000,\"cost\":2.25},{\"itemId\":\"BANANA\",\"amount\":400,\"cost\":1.25}]}"));
	}
}
