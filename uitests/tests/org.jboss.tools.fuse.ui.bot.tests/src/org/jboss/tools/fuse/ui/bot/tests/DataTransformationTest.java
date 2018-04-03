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

import org.eclipse.reddeer.common.exception.WaitTimeoutExpiredException;
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
import org.jboss.tools.fuse.reddeer.ResourceHelper;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.editor.DataTransformationEditor;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseTransformationTestWizard;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseTransformationWizard;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseTransformationWizard.TransformationType;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseTransformationWizard.TypeDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test covers Data Transformation Tooling
 * 
 * @author tsedmik
 */
@RunWith(RedDeerSuite.class)
@OpenPerspective(FuseIntegrationPerspective.class)
public class DataTransformationTest extends DefaultTest {

	/**
	 * <p>
	 * Basic Test
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>import 'starter' project from 'resources/projects/starter'</li>
	 * <li>create a new Data Transformation</li>
	 * <li>modify the Camel Route (connect Data Transformation node)</li>
	 * <li>create a new Data Transformation test</li>
	 * <li>run test and verify output</li>
	 * </ol>
	 */
	@Test
	public void testBasics() {

		ProjectFactory.importExistingProject(
				ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, "resources/projects/starter"), "starter",
				true);
		new CamelProject("starter").openCamelContext("camel-context.xml");
		CamelEditor editor = new CamelEditor("camel-context.xml");
		editor.activate();
		editor.deleteCamelComponent("file:target/messages?fileName=xyz-order.json");
		editor.save();
		editor.addCamelComponent("Data Transformation", "file:src/data?fileName=abc-order.xml&noop=true");
		NewFuseTransformationWizard wizard = new NewFuseTransformationWizard();
		wizard.setTransformationID("xml2json");
		wizard.setSourceType(TransformationType.XML);
		wizard.setTargetType(TransformationType.JSON);
		wizard.next();
		wizard.setXMLTypeDefinition(TypeDefinition.Schema);
		wizard.setXMLSourceFile("abc-order.xsd");
		wizard.next();
		wizard.setJSONTypeDefinition(TypeDefinition.Schema);
		wizard.setJSONTargetFile("xyz-order.json");
		wizard.finish();
		editor.activate();
		editor.addCamelComponent("File", "ref:xml2json");
		editor.setProperty("file:directoryName", "Uri", "file:target/messages?fileName=xyz-order.json");
		editor.close(true);

		DataTransformationEditor transEditor = new DataTransformationEditor("transformation.xml");
		transEditor.createNewVariable("ORIGIN");
		transEditor.createVariableTransformation("ABCOrder", "ORIGIN", "XyzOrder",
				new String[] { "XyzOrder", "origin" });
		transEditor.createExpressionTransformation("ABCOrder", "Header", "approvalID", "XyzOrder",
				new String[] { "XyzOrder", "approvalCode" });
		transEditor.createTransformation("ABCOrder", new String[] { "ABCOrder", "header", "customerNum" }, "XyzOrder",
				new String[] { "XyzOrder", "custId" });
		transEditor.createTransformation("ABCOrder", new String[] { "ABCOrder", "header", "orderNum" }, "XyzOrder",
				new String[] { "XyzOrder", "orderId" });
		transEditor.createTransformation("ABCOrder", new String[] { "ABCOrder", "header", "status" }, "XyzOrder",
				new String[] { "XyzOrder", "priority" });
		transEditor.createTransformation("ABCOrder", new String[] { "ABCOrder", "orderItems", "item[ ]", "id" },
				"XyzOrder", new String[] { "XyzOrder", "lineItems[ ]", "itemId" });
		transEditor.createTransformation("ABCOrder", new String[] { "ABCOrder", "orderItems", "item[ ]", "price" },
				"XyzOrder", new String[] { "XyzOrder", "lineItems[ ]", "cost" });
		transEditor.createTransformation("ABCOrder", new String[] { "ABCOrder", "orderItems", "item[ ]", "quantity" },
				"XyzOrder", new String[] { "XyzOrder", "lineItems[ ]", "amount" });

		new CamelProject("starter").selectProjectItem("src/main/resources", "META-INF", "spring", "camel-context.xml");
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
		javaEditor.insertLine(25,
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
