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
import static org.jboss.reddeer.requirements.server.ServerReqState.PRESENT;
import static org.jboss.tools.fuse.qe.reddeer.requirement.ServerReqType.Fuse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.jboss.reddeer.common.exception.WaitTimeoutExpiredException;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.junit.execution.annotation.RunIf;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.fuse.qe.reddeer.ResourceHelper;
import org.jboss.tools.fuse.qe.reddeer.condition.FuseLogContainsText;
import org.jboss.tools.fuse.qe.reddeer.condition.IssueIsClosed;
import org.jboss.tools.fuse.qe.reddeer.condition.IssueIsClosed.Jira;
import org.jboss.tools.fuse.qe.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.qe.reddeer.requirement.ServerRequirement.Server;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.utils.FuseServerManipulator;
import org.jboss.tools.fuse.qe.reddeer.utils.FuseShellSSH;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test covers Data Transformation Tooling in JBoss Fuse Runtime perspective
 * 
 * @author tsedmik
 */
@RunWith(RedDeerSuite.class)
@Fuse(server = @Server(type = Fuse, state = PRESENT))
public class DataTransformationDeploymentTest extends DefaultTest {

	@InjectRequirement
	private static FuseRequirement serverRequirement;

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void setupStartServer() {
		FuseServerManipulator.startServer(serverRequirement.getConfig().getName());
	}

	/**
	 * Cleans up test environment
	 */
	@AfterClass
	public static void setupStopServer() {
		FuseServerManipulator.removeAllModules(serverRequirement.getConfig().getName());
		FuseServerManipulator.stopServer(serverRequirement.getConfig().getName());
	}

	/**
	 * <p>
	 * Test tries to deploy a Fuse project with defined Data Transformation to JBoss Fuse runtime
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>start JBoss Fuse</li>
	 * <li>import 'trans217' project from 'resources/projects/trans217'</li>
	 * <li>enable Fuse Camel Nature on the project (has to be done to ensure that project can be deployed to JBoss Fuse
	 * Runtime)</li>
	 * <li>deploy the project</li>
	 * <li>invoke the route with copying a file</li>
	 * <li>check log of JBoss Fuse (deployed project should log transformed XML file in JSON format)</li>
	 * </ol>
	 */
	@Test
	@Jira("ENTESB-4452")
	@RunIf(conditionClass = IssueIsClosed.class)
	public void testDeployment() {

		ProjectFactory.importExistingProject(
				ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, "resources/projects/trans217"),
				"trans217", false);
		CamelProject project = new CamelProject("trans217");
		project.update();
		FuseServerManipulator.addModule(serverRequirement.getConfig().getName(), "trans217");

		// invoke the route with copying a file
		String from = ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID,
				"resources/projects/trans217/src/main/data/abc-order.xml");
		String to = serverRequirement.getConfig().getServerBase().getHome() + "/src/main/data/abc-order.xml";
		try {
			Files.copy(new File(from).toPath(), new File(to).toPath(), REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Tests cannot copy XML file to home folder of JBoss Fuse Runtime!");
		}
		try {
			new WaitUntil(new FuseLogContainsText(
					"{\"custId\":\"ACME-123\",\"priority\":\"GOLD\",\"orderId\":\"[ORDER1]\",\"origin\":\"web\",\"approvalCode\":\"AUTO_OK\",\"lineItems\":[{\"itemId\":\"PICKLE\",\"amount\":1000,\"cost\":2.25},{\"itemId\":\"BANANA\",\"amount\":400,\"cost\":1.25}]}"));
		} catch (WaitTimeoutExpiredException e) {
			fail("Transformation is broken! \n\n" + new FuseShellSSH().execute("log:display"));
		}
	}
}
