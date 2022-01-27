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

import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.jboss.tools.fuse.reddeer.ProjectTemplate.CBR_SPRING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeFalse;

import java.io.IOException;

import org.eclipse.reddeer.core.util.FileUtil;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanerrorlog.CleanErrorLogRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.JiraIssue;
import org.jboss.tools.fuse.reddeer.component.CXF;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Contains additional tests for CamelEditorTest.java which are not necessarily to be part of Smoke tests suite
 * 
 * @author djelinek
 * 
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class CamelEditorAdditionTest {

	public static final String PROJECT_NAME = "cbr";
	public static final String CAMEL_CONTEXT = "camel-context.xml";
	public static final String CXF_ARTIFACT_ID = "camel-cxf";

	private CamelProject project;
	private CamelEditor editor;

	@BeforeClass
	public static void setupResetCamelContext() {
		new WorkbenchShell().maximize();
		ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(KARAF).template(CBR_SPRING).create();
	}

	@Before
	public void cleanErrorLog() {
		new CleanErrorLogRequirement().fulfill();
	}

	@AfterClass
	public static void cleanWorkspace() {
		new CleanWorkspaceRequirement().fulfill();
	}

	/**
	 * <p>
	 * Test verifies adding/removing required dependencies into project <i>pom.xml</i> file
	 * </p>
	 * <ol>
	 * <li>create a new project from template 'CBR->Spring DSL'</li>
	 * <li>open camel-context.xml file</li>
	 * <li>add Camel component 'CXF'</li>
	 * <li>save changes and verifies present of new 'camel-cxf' dependency in 'pom.xml' file</li>
	 * <li>delete Camel component 'CXF'</li>
	 * <li>save changes and verifies 'camel-cxf' dependency is not present in 'pom.xml' file</li>
	 * </ol>
	 */
	@Test
	public void testPOMFileDependecies() {
		editor = new CamelEditor(CAMEL_CONTEXT);
		project = new CamelProject(PROJECT_NAME);
		CXF cxf = new CXF(); // CXF component requires dependency in pom.xml

		editor.activate();
		editor.addCamelComponent(cxf.getPaletteEntry(), "Route cbr-route");
		editor.save();
		assertPOMDependency(true, CXF_ARTIFACT_ID, "Dependency should has been present in POM.xml file");

		editor.activate();
		editor.deleteCamelComponent(cxf.getLabel());
		editor.save();
		assertPOMDependency(false, CXF_ARTIFACT_ID, "Dependency should has NOT been present in POM.xml file");
	}

	private void assertPOMDependency(boolean expected, String artifactId, String msg) {
		String pom = null;
		try {
			pom = FileUtil.readFile(project.getFile().getAbsolutePath() + "/pom.xml");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String pattern = "<artifactId>" + artifactId + "</artifactId>";
		if(!expected) {
			// see https://issues.jboss.org/browse/FUSETOOLS-2974
			assumeFalse(new JiraIssue("FUSETOOLS-2974").getMessage(), pom.contains(pattern));
		}
		assertEquals(msg, expected, pom.contains(pattern));
	}
}
