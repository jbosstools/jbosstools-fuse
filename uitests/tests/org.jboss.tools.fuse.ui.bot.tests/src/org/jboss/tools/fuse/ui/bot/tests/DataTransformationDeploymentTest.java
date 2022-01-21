/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.ui.bot.tests;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.eclipse.reddeer.requirements.server.ServerRequirementState.RUNNING;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.reddeer.common.exception.WaitTimeoutExpiredException;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.jre.JRERequirement.JRE;
import org.jboss.tools.fuse.reddeer.ResourceHelper;
import org.jboss.tools.fuse.reddeer.condition.FuseLogContainsText;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.reddeer.utils.FuseServerManipulator;
import org.jboss.tools.fuse.reddeer.utils.FuseShellSSH;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test covers Data Transformation Tooling and deployment to Fuse Runtime
 * <p>
 * Use the following arguments to specify Fuse Integration Project:
 * <ul>
 * <li>-DfuseDeploymentType=... --- Standalone</li>
 * <li>-DfuseRuntimeType=... --- Karaf / EAP</li>
 * <li>-DfuseCamelVersion=... --- e.g. 2.18.1.redhat-000012</li>
 * <li>-DfuseDSL=... --- Blueprint / Spring</li>
 * </ul>
 * </p>
 * 
 * @author tsedmik
 */
@JRE(setDefault = true)
@RunWith(RedDeerSuite.class)
@Fuse(state = RUNNING)
public class DataTransformationDeploymentTest extends DataTransformationDefaultTest {

	public static final String EXAMPLE_KARAF_RUNTIME_PATH = "/data/abc-order.xml";
	public static final String EXAMPLE_EAP_RUNTIME_PATH = "/bin/src/data/abc-order.xml";
	public static final String EXAMPLE_XML_PATH = "resources/datatransformation/data/abc-order.xml";
	public static final String EAP_CONSOLE_NAME = "Fuse on EAP";

	@InjectRequirement
	private static FuseRequirement serverRequirement;

	/**
	 * Cleans up test environment
	 */
	@AfterClass
	public static void setupStopServer() {
		FuseServerManipulator.removeAllModules(serverRequirement.getConfiguration().getServer().getName());
	}

	@Before
	public void setupEnvironment() throws IOException, CoreException {
		createProject(PROJECT_NAME);
		copyResources(PROJECT_NAME);
		configureRoute();
		addDataTransformation();
		createMapping();
		new CamelProject(PROJECT_NAME).update();
	}

	/**
	 * <p>
	 * Test tries to deploy a Fuse project with defined Data Transformation to Red Hat Fuse runtime (Karaf or EAP)
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>start Red Hat Fuse</li>
	 * <li>create a new Fuse Integration Project</li>
	 * <li>add a data transformation into the project</li>
	 * <li>deploy the project</li>
	 * <li>invoke the route with copying a file</li>
	 * <li>check log of Red Hat Fuse (deployed project should log transformed XML file in JSON format)</li>
	 * </ol>
	 */
	@Test
	public void testDeployment() {
		FuseServerManipulator.addModule(serverRequirement.getConfiguration().getServer().getName(), PROJECT_NAME);
		copyExample();
		checkTransformation();
	}

	private void checkTransformation() {
		String transformation_log_msg = "{\"custId\":\"ACME-123\",\"priority\":\"GOLD\",\"orderId\":\"ORDER1\",\"origin\":\"ORIGIN\",\"approvalCode\":\"AUTO_OK\",\"lineItems\":[{\"itemId\":\"PICKLE\",\"amount\":1000,\"cost\":2.25},{\"itemId\":\"BANANA\",\"amount\":400,\"cost\":1.25}]}";
		if (serverRequirement.getConfiguration().getServer().getName().contains("Fuse 7")) {
			transformation_log_msg = "{\"custId\":\"ACME-123\",\"priority\":\"GOLD\",\"orderId\":\"ORDER1\",\"origin\":\"${ORIGIN}\",\"approvalCode\":\"AUTO_OK\",\"lineItems\":[{\"itemId\":\"PICKLE\",\"amount\":1000,\"cost\":2.25},{\"itemId\":\"BANANA\",\"amount\":400,\"cost\":1.25}]}";
		}
		if (serverRequirement.getConfiguration().getServer().getClass().getName().contains("EAP")) {
			try {
				new ConsoleView().activate();
				new WaitUntil(new ConsoleHasText(transformation_log_msg));
			} catch (WaitTimeoutExpiredException e) {
				fail("Transformation is broken! (EAP) \n\n" + new ConsoleView().getConsoleText());
			}
		} else {
			try {
				new WaitUntil(new FuseLogContainsText(transformation_log_msg));
			} catch (WaitTimeoutExpiredException e) {
				assumeFalse("https://issues.redhat.com/browse/FUSETOOLS-3337", issue_3337());
				fail("Transformation is broken! (Karaf) \n\n" + new FuseShellSSH().execute("log:display"));
			}
		}
	}

	private void copyExample() {
		String from = ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, EXAMPLE_XML_PATH);
		String to = serverRequirement.getConfiguration().getServer().getHome() + EXAMPLE_KARAF_RUNTIME_PATH;
		if (serverRequirement.getConfiguration().getServer().getClass().getName().contains("EAP")) {
			to = serverRequirement.getConfiguration().getServer().getHome() + EXAMPLE_EAP_RUNTIME_PATH;
		}
		try {
			Files.copy(new File(from).toPath(), new File(to).toPath(), REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			assumeFalse("https://issues.redhat.com/browse/FUSETOOLS-3337", issue_3337());
			fail("Tests cannot copy XML file to home folder of Red Hat Fuse Runtime! \n\n"
					+ new FuseShellSSH().execute("log:display"));
		}
	}

	/**
	 * For more details please see https://issues.redhat.com/browse/FUSETOOLS-3337
	 */
	private boolean issue_3337() {
		String fuseConsoleLog = new FuseShellSSH().execute("log:display");
		String to = serverRequirement.getConfiguration().getServer().getHome() + EXAMPLE_KARAF_RUNTIME_PATH;
		if (fuseConsoleLog.contains("Unresolved constraint") || fuseConsoleLog.contains("Unresolved requirements")
				|| !(new File(to).exists()) || fuseConsoleLog.contains("java.lang.ClassNotFoundException")) {
			return true;
		}
		return false;
	}

}