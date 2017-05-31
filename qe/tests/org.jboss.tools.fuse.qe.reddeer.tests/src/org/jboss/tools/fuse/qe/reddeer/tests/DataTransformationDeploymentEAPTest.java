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

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.jboss.reddeer.requirements.server.ServerReqState.RUNNING;
import static org.jboss.tools.fuse.qe.reddeer.requirement.ServerReqType.EAP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

import org.jboss.reddeer.common.exception.WaitTimeoutExpiredException;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.eclipse.condition.ConsoleHasText;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.fuse.qe.reddeer.FileUtils;
import org.jboss.tools.fuse.qe.reddeer.ResourceHelper;
import org.jboss.tools.fuse.qe.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.qe.reddeer.requirement.ServerRequirement.Server;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.utils.FuseServerManipulator;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test covers Data Transformation Tooling in JBoss Fuse on EAP Runtime perspective
 * 
 * @author tsedmik
 */
@RunWith(RedDeerSuite.class)
@Fuse(server = @Server(type = EAP, state = RUNNING))
public class DataTransformationDeploymentEAPTest extends DefaultTest {

	@InjectRequirement
	private static FuseRequirement serverRequirement;

	/**
	 * Cleans up test environment
	 */
	@AfterClass
	public static void setupStopServer() {
		FuseServerManipulator.removeAllModules(serverRequirement.getConfig().getName());
		FuseServerManipulator.stopServer(serverRequirement.getConfig().getName());
		FileUtils.deleteDir(new File(serverRequirement.getConfig().getServerBase().getHome() + "/bin/target/"));
		FileUtils.deleteDir(new File(serverRequirement.getConfig().getServerBase().getHome() + "/bin/src/"));
	}

	/**
	 * <p>
	 * Test tries to deploy a Fuse project with defined Data Transformation to JBoss Fuse on EAP runtime
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>start JBoss Fuse on EAP</li>
	 * <li>import 'wildfly-transformation' project from 'resources/projects/datatrans'</li>
	 * <li>deploy the project</li>
	 * <li>invoke the route with copying a file</li>
	 * <li>check whether transformation was successful - output directory contains a file with correctly transformed
	 * data</li>
	 * </ol>
	 */
	@Test
	public void testDeployment() throws FileNotFoundException {

		// import and deploy the project
		ProjectFactory.importExistingProject(ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID,
				"resources/projects/datatrans"), "wildfly-transformation", false);
		new CamelProject("wildfly-transformation").update();
		FuseServerManipulator.addModule(serverRequirement.getConfig().getName(), "wildfly-transformation");
		try {
			new WaitUntil(new ConsoleHasText(
					"started and consuming from: Endpoint[file://src/data?fileName=abc-order.xml&noop=true]"));
			new WaitUntil(new ConsoleHasText("Deployed \"wildfly-transformation.war\""));
		} catch (WaitTimeoutExpiredException e) {
			fail("Project was not sucessfully deployed!");
		}

		// invoke the route with copying a file
		String from = ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID,
				"resources/projects/datatrans/src/data/abc-order.xml");
		String to = serverRequirement.getConfig().getServerBase().getHome() + "/bin/src/data/abc-order.xml";
		try {
			Files.copy(new File(from).toPath(), new File(to).toPath(), REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Tests cannot copy XML file to home folder of JBoss Fuse on EAP Runtime!");
		}

		// check whether transformation was sucessful
		try {
			new WaitUntil(new ConsoleHasText("file://src/data) Created XMLInputFactory"));
		} catch (WaitTimeoutExpiredException e) {
			fail("Transformation did not happen! ");
		}
		File output = new File(
				serverRequirement.getConfig().getServerBase().getHome() + "/bin/target/messages/xyz-order.json");
		assertTrue("Transformation did not create output file - bin/target/messages/xyz-order.json", output.exists());
		Scanner scanner = new Scanner(output);
		String text = scanner.nextLine();
		scanner.close();
		assertEquals("Transformation has weird output \n\n" + text,
				"{\"custId\":\"[ACME-123]\",\"priority\":\"GOLD\",\"orderId\":\"ORDER1\",\"origin\":\"web\",\"approvalCode\":\"AUTO_OK\",\"lineItems\":[{\"itemId\":\"PICKLE\",\"amount\":1000,\"cost\":2.25},{\"itemId\":\"BANANA\",\"amount\":400,\"cost\":1.25}]}",
				text);
	}
}
