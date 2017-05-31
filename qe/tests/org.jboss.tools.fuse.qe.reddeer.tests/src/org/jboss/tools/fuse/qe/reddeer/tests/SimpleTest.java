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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.eclipse.exception.EclipseLayerException;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.eclipse.ui.perspectives.AbstractPerspective;
import org.jboss.reddeer.eclipse.ui.perspectives.JavaEEPerspective;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.tools.fuse.qe.reddeer.ProjectTemplate;
import org.jboss.tools.fuse.qe.reddeer.ProjectType;
import org.jboss.tools.fuse.qe.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.view.FuseJMXNavigator;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Simple tests verifies only presence of JBoss Fuse Tooling plugins
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
		ProjectFactory.newProject(PROJECT_NAME).template(ProjectTemplate.CBR).type(ProjectType.SPRING).create();
		try {
			new ProjectExplorer().getProject(PROJECT_NAME);
		} catch (EclipseLayerException ex) {
			fail("Created project is not present in Project Explorer");
		}
	}

	/**
	 * <p>
	 * Simple test tries to open view related to JBoss Fuse Tooling
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
	 * Simple test tries to open perspectives related to JBoss Fuse Tooling
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
