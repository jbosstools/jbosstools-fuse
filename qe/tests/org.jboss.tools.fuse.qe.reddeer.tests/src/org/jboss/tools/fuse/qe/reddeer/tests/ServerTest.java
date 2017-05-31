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

import static org.jboss.reddeer.requirements.server.ServerReqState.PRESENT;
import static org.jboss.tools.fuse.qe.reddeer.requirement.ServerReqType.Fuse;
import static org.jboss.tools.fuse.qe.reddeer.requirement.ServerReqType.Karaf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.reddeer.eclipse.ui.perspectives.JavaEEPerspective;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.qe.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.qe.reddeer.requirement.ServerRequirement.Server;
import org.jboss.tools.fuse.qe.reddeer.runtime.impl.ServerKaraf;
import org.jboss.tools.fuse.qe.reddeer.utils.FuseServerManipulator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests managing a Fuse server
 * 
 * @author tsedmik
 */
@Fuse(server = @Server(type = { Fuse, Karaf }, state = PRESENT))
@CleanWorkspace
@OpenPerspective(JavaEEPerspective.class)
@RunWith(RedDeerSuite.class)
public class ServerTest extends DefaultTest {

	@InjectRequirement
	private FuseRequirement serverRequirement;

	/**
	 * Prepares test environment
	 */
	@Before
	public void setupCleanUp() {

		FuseServerManipulator.deleteAllServers();
		FuseServerManipulator.deleteAllServerRuntimes();
	}

	/**
	 * <p>
	 * Tests adding/modifying/removing a server and a server runtime
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>add a new server runtime</li>
	 * <li>edit the server runtime (change name)</li>
	 * <li>add a new server</li>
	 * <li>start the server</li>
	 * <li>stop the server</li>
	 * <li>remove the server</li>
	 * <li>remove the server runtime</li>
	 * </ol>
	 */
	@Test
	public void testComplexServer() {

		ServerKaraf fuse = (ServerKaraf) serverRequirement.getConfig().getServerBase();

		FuseServerManipulator.addServerRuntime(fuse.getRuntimeType(), fuse.getHome());
		assertEquals("New server runtime is not listed in Server Runtimes", 1,
				FuseServerManipulator.getServerRuntimes().size());
		FuseServerManipulator.editServerRuntime(fuse.getRuntimeType(), fuse.getHome());
		FuseServerManipulator.addServer(fuse.getServerType(), fuse.getHost(), fuse.getName(), fuse.getPort(),
				fuse.getUsername(), fuse.getPassword());
		assertEquals("No server's record is in Servers View", 1, FuseServerManipulator.getServers().size());
		assertTrue("New server is not listed in Servers View", FuseServerManipulator.isServerPresent(fuse.getName()));
		FuseServerManipulator.startServer(fuse.getName());
		assertTrue("Server is not started", FuseServerManipulator.isServerStarted(fuse.getName()));
		assertTrue("There are some errors in error log", getErrorMessages() == 0);
		deleteErrorLog();
		FuseServerManipulator.stopServer(fuse.getName());
		assertFalse("Server is not stopped", FuseServerManipulator.isServerStarted(fuse.getName()));
		assertTrue("There are some errors in error log", getErrorMessages() == 0);
		FuseServerManipulator.removeServer(fuse.getName());
		assertEquals("Server is listed in Servers View after deletion", 0, FuseServerManipulator.getServers().size());
		FuseServerManipulator.removeServerRuntime(fuse.getRuntimeType());
		assertEquals("Server runtime is listed after deletion", 0, FuseServerManipulator.getServerRuntimes().size());
	}
}
