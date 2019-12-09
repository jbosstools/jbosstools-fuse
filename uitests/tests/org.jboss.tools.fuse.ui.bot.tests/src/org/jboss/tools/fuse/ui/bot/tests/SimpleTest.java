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

import static org.jboss.tools.fuse.reddeer.ProjectTemplate.CBR_BLUEPRINT;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.eclipse.exception.EclipseLayerException;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.perspectives.AbstractPerspective;
import org.eclipse.reddeer.eclipse.ui.perspectives.JavaEEPerspective;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.jboss.tools.fuse.reddeer.view.FuseJMXNavigator;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Simple tests verifies only presence of Red Hat Fuse Tooling plugins
 * 
 * @author tsedmik
 */
@CleanWorkspace
@OpenPerspective(JavaEEPerspective.class)
@RunWith(RedDeerSuite.class)
public class SimpleTest extends DefaultTest {

	private static Logger log = Logger.getLogger(SimpleTest.class);
	private static String PROJECT_NAME = "cbr @1";

	/**
	 * <p>
	 * Simple test tries to create a new Fuse project
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>create a new project from <i>Content Based Router</i></li>
	 * </ol>
	 */
	@Test
	public void testCreateFuseProject() {

		log.info("Create a new Fuse project from 'Content Based Router'");
		ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(KARAF).template(CBR_BLUEPRINT)
				.create();
		try {
			new ProjectExplorer().getProject(PROJECT_NAME);
		} catch (EclipseLayerException ex) {
			fail("Created project is not present in Project Explorer");
		}
	}

	/**
	 * <p>
	 * Simple test tries to open view related to Red Hat Fuse Tooling
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>open JMX Navigator view</li>
	 * </ol>
	 */
	@Test
	public void testOpenViews() {

		new FuseJMXNavigator().open();
	}

	/**
	 * <p>
	 * Simple test tries to open perspectives related to Red Hat Fuse Tooling
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>open Fuse Integration perspective</li>
	 * </ol>
	 */
	@Test
	public void testOpenPerspectives() {

		AbstractPerspective perspective = new FuseIntegrationPerspective();
		perspective.open();
		assertTrue(perspective.isOpened());
	}
}
