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

import static org.eclipse.reddeer.requirements.server.ServerRequirementState.PRESENT;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.junit.annotation.RequirementRestriction;
import org.eclipse.reddeer.junit.requirement.inject.InjectRequirement;
import org.eclipse.reddeer.junit.requirement.matcher.RequirementMatcher;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.impl.label.DefaultLabel;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement;
import org.jboss.tools.fuse.reddeer.requirement.FuseRequirement.Fuse;
import org.jboss.tools.fuse.reddeer.runtime.ServerTypeMatcher;
import org.jboss.tools.fuse.reddeer.runtime.impl.ServerFuse;
import org.jboss.tools.fuse.reddeer.runtime.impl.ServerKaraf;
import org.jboss.tools.fuse.reddeer.utils.FuseServerManipulator;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests with a different JRE
 * 
 * @author tsedmik
 */
@Fuse(state = PRESENT)
@OpenPerspective(FuseIntegrationPerspective.class)
@CleanWorkspace
@RunWith(RedDeerSuite.class)
public class ServerJRETest extends DefaultTest {

	@InjectRequirement
	private FuseRequirement serverRequirement;
	
	@RequirementRestriction
	public static RequirementMatcher getRestrictionMatcher() {
		return new RequirementMatcher(Fuse.class, "server", new ServerTypeMatcher(ServerFuse.class, ServerKaraf.class));
	}

	/**
	 * <p>
	 * Tries to run Red Hat Fuse server with different JRE than runs JBDS
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>add new JRE different from that runs JBDS</li>
	 * <li>add new Red Hat Fuse Server Runtime with different JRE</li>
	 * <li>start the server</li>
	 * <li>check Console View whether the server starting with the selected JRE</li>
	 * </ol>
	 */
	@Test
	public void testJRE() {

		ServerKaraf fuse = (ServerKaraf) serverRequirement.getConfiguration().getServer();
		assertNotNull("Different JRE is not specified!", fuse.getJre());
		FuseServerManipulator.startServer(fuse.getName());
		new ConsoleView().open();
		assertTrue(new DefaultLabel().getText().contains(fuse.getJre()));
	}
}
