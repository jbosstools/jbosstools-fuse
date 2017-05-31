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
package org.jboss.tools.fuse.qe.reddeer.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jboss.reddeer.common.exception.WaitTimeoutExpiredException;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.eclipse.condition.ConsoleHasText;
import org.jboss.reddeer.eclipse.jdt.ui.junit.JUnitHasFinished;
import org.jboss.reddeer.eclipse.jdt.ui.junit.JUnitView;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.menu.ShellMenu;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.table.DefaultTableItem;
import org.jboss.reddeer.workbench.impl.editor.TextEditor;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.qe.reddeer.JiraIssue;
import org.jboss.tools.fuse.qe.reddeer.ResourceHelper;
import org.jboss.tools.fuse.qe.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.qe.reddeer.editor.DataTransformationEditor;
import org.jboss.tools.fuse.qe.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.qe.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.wizard.NewFuseTransformationTestWizard;
import org.jboss.tools.fuse.qe.reddeer.wizard.NewFuseTransformationWizard;
import org.jboss.tools.fuse.qe.reddeer.wizard.NewFuseTransformationWizard.TransformationType;
import org.jboss.tools.fuse.qe.reddeer.wizard.NewFuseTransformationWizard.TypeDefinition;
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
		editor.addCamelComponent("Data Transformation", "Route _route1");
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
		editor.addConnection("file:src/data?fileName=abc-order.xml&noop=true", "ref:xml2json");
		editor.addCamelComponent("File", "Route _route1");
		editor.setProperty("file:directoryName", "Uri *", "file:target/messages?fileName=xyz-order.json");
		editor.addConnection("ref:xml2json", "file:target/messages?fileName=xyz-order.json");
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
			new WaitUntil(new ShellWithTextIsAvailable("New Fuse Transformation Test"));
		} catch (WaitTimeoutExpiredException e) {
			fail("'New Fuse Transformation Test' wizard is not opened!");
		}
		test.setPackage("example");
		test.selectTransformationID("xml2json");
		try {
			test.finish();
		} catch (WaitTimeoutExpiredException e) {
			if (new ShellWithTextIsAvailable("New Fuse Transformation Test").test()) {
				throw new JiraIssue("FUSETOOLS-2398");
			}
		}
		TextEditor javaEditor = new TextEditor("TransformationTest.java");
		javaEditor.insertLine(25,
				"startEndpoint.sendBodyAndHeader(readFile(\"src/data/abc-order.xml\"), \"approvalID\", \"AUTO_OK\");");
		javaEditor.save();
		new ShellMenu("Run", "Run").select();
		new WaitUntil(new ShellWithTextIsAvailable("Run As"));
		new DefaultShell("Run As");
		new DefaultTableItem("JUnit Test").select();
		new PushButton("OK").click();
		new WaitUntil(new JUnitHasFinished(), TimePeriod.getCustom(20));
		new WorkbenchShell();
		assertEquals("Result of JUnit test is wrong", "1/1", new JUnitView().getRunStatus());
		new WaitUntil(new ConsoleHasText(
				"{\"custId\":\"ACME-123\",\"priority\":\"GOLD\",\"orderId\":\"ORDER1\",\"origin\":\"ORIGIN\",\"approvalCode\":\"AUTO_OK\",\"lineItems\":[{\"itemId\":\"PICKLE\",\"amount\":1000,\"cost\":2.25},{\"itemId\":\"BANANA\",\"amount\":400,\"cost\":1.25}]}"));
	}
}
