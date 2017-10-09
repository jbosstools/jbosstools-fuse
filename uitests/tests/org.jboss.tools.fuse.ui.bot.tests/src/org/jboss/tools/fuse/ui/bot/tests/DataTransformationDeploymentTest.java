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

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.eclipse.reddeer.requirements.server.ServerRequirementState.PRESENT;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.reddeer.common.exception.WaitTimeoutExpiredException;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.junit.annotation.RequirementRestriction;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.requirement.matcher.RequirementMatcher;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.fuse.reddeer.ResourceHelper;
import org.jboss.tools.fuse.reddeer.condition.FuseLogContainsText;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.reddeer.runtime.ServerTypeMatcher;
import org.jboss.tools.fuse.reddeer.runtime.impl.ServerFuse;
import org.jboss.tools.fuse.reddeer.utils.FuseServerManipulator;
import org.jboss.tools.fuse.reddeer.utils.FuseShellSSH;
import org.jboss.tools.fuse.ui.bot.tests.utils.ProjectFactory;
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
@Fuse(state = PRESENT)
public class DataTransformationDeploymentTest extends DefaultTest {

	@InjectRequirement
	private static FuseRequirement serverRequirement;
	
	@RequirementRestriction
	public static RequirementMatcher getRestrictionMatcher() {
		return new RequirementMatcher(Fuse.class, "server", new ServerTypeMatcher(ServerFuse.class));
	}

	/**
	 * Prepares test environment
	 */
	@BeforeClass
	public static void setupStartServer() {
		FuseServerManipulator.startServer(serverRequirement.getConfiguration().getServer().getName());
	}

	/**
	 * Cleans up test environment
	 */
	@AfterClass
	public static void setupStopServer() {
		FuseServerManipulator.removeAllModules(serverRequirement.getConfiguration().getServer().getName());
		FuseServerManipulator.stopServer(serverRequirement.getConfiguration().getServer().getName());
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
	public void testDeployment() {

		ProjectFactory.importExistingProject(
				ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, "resources/projects/trans217"),
				"trans217", false);
		CamelProject project = new CamelProject("trans217");
		project.update();
		FuseServerManipulator.addModule(serverRequirement.getConfiguration().getServer().getName(), "trans217");

		// invoke the route with copying a file
		String from = ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID,
				"resources/projects/trans217/src/main/data/abc-order.xml");
		String to = serverRequirement.getConfiguration().getServer().getHome() + "/src/main/data/abc-order.xml";
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
