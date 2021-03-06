/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.ui.bot.tests;

import static org.jboss.tools.fuse.reddeer.ProjectTemplate.EMPTY_SPRING;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;

import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanerrorlog.CleanErrorLogRequirement.CleanErrorLog;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.condition.EditorWithTitleIsActive;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.jboss.tools.fuse.ui.bot.tests.utils.EditorManipulator;
import org.jboss.tools.fuse.ui.bot.tests.utils.FuseToolingProblems;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test cases related to <i>Problems</i> view
 * 
 * @author tsedmik
 */
@CleanWorkspace
@CleanErrorLog
@RunWith(RedDeerSuite.class)
@OpenPerspective(FuseIntegrationPerspective.class)
public class ProblemsViewTest {

	public static final String FUSE_TOOLING_PROBLEM_TYPE = "Red Hat Fuse Tooling Validation Problem";
	public static final String ROUTE_WITHOUT_ERRORS = "resources/routeWithValidationFixedOnGlobalElements.xml";
	public static final String ROUTE_WITH_ERRORS = "resources/routeWithValidationErrorOnGlobalElements.xml";
	public static final String SOURCE_TAB = "Source";
	public static final String DESIGN_TAB = "Design";
	public static final String CAMEL_CONTEXT_XML = "camel-context.xml";
	public static final String PROJECT_NAME = "TestProblemsView";

	/**
	 * <p>
	 * Checks whether Fuse Tooling Validation of Camel Context XML files works.
	 * </p>
	 * <b>Steps:</b>
	 * <ol>
	 * <li>create a new Fuse Integration Project - Empty template, Spring DSL</li>
	 * <li>introduce some errors into the Camel Context XML file - Errors on Global elements</li>
	 * <li>check Problems view whether contains these errors</li>
	 * <li>fix the Camel Context XML file</li>
	 * <li>check whether Problems view does not contain any Fuse Tooling Validation errors/warnings</li>
	 * </ol>
	 */
	@Test
	public void testProblemsOnGlobalElements() {
		ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(KARAF).template(EMPTY_SPRING).create();
		new WaitUntil(new EditorWithTitleIsActive(CAMEL_CONTEXT_XML));
		CamelEditor editor = new CamelEditor(CAMEL_CONTEXT_XML);
		CamelEditor.switchTab(SOURCE_TAB);
		EditorManipulator.copyFileContentToCamelXMLEditor(ROUTE_WITH_ERRORS);
		CamelEditor.switchTab(DESIGN_TAB);
		new WaitUntil(new FuseToolingProblems(2));
		editor.activate();
		CamelEditor.switchTab(SOURCE_TAB);
		EditorManipulator.copyFileContentToCamelXMLEditor(ROUTE_WITHOUT_ERRORS);
		CamelEditor.switchTab(DESIGN_TAB);
		new WaitUntil(new FuseToolingProblems(0));
	}
}
