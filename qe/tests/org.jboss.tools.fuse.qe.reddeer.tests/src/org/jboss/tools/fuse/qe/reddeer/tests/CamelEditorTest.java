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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.core.exception.CoreLayerException;
import org.jboss.reddeer.eclipse.ui.perspectives.JavaEEPerspective;
import org.jboss.reddeer.gef.view.PaletteView;
import org.jboss.reddeer.jface.text.contentassist.ContentAssistant;
import org.jboss.reddeer.junit.execution.annotation.RunIf;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.autobuilding.AutoBuildingRequirement.AutoBuilding;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.workbench.exception.WorkbenchLayerException;
import org.jboss.reddeer.workbench.handler.EditorHandler;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.qe.reddeer.LogGrapper;
import org.jboss.tools.fuse.qe.reddeer.ProjectTemplate;
import org.jboss.tools.fuse.qe.reddeer.ProjectType;
import org.jboss.tools.fuse.qe.reddeer.component.File;
import org.jboss.tools.fuse.qe.reddeer.component.Log;
import org.jboss.tools.fuse.qe.reddeer.component.Otherwise;
import org.jboss.tools.fuse.qe.reddeer.condition.IssueIsClosed;
import org.jboss.tools.fuse.qe.reddeer.condition.IssueIsClosed.Jira;
import org.jboss.tools.fuse.qe.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.qe.reddeer.editor.SourceEditor;
import org.jboss.tools.fuse.qe.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.EditorManipulator;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.view.ErrorLogView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests:
 * <ul>
 * <li>creation of all components in Fuse Camel editor</li>
 * <li>manipulation with components in the Camel Editor</li>
 * </ul>
 * 
 * @author tsedmik
 */
@CleanWorkspace
@OpenPerspective(JavaEEPerspective.class)
@RunWith(RedDeerSuite.class)
@AutoBuilding(false)
public class CamelEditorTest extends DefaultTest {

	protected Logger log = Logger.getLogger(CamelEditorTest.class);

	/**
	 * Prepares test environment
	 */
	@Before
	public void setupResetCamelContext() {

		new WorkbenchShell();
		ProjectFactory.newProject("cbr").template(ProjectTemplate.CBR).type(ProjectType.SPRING).create();
		new ErrorLogView().deleteLog();
	}

	@Before
	public void setupClosePaletteView() {
		new WorkbenchShell();
		log.info("Trying to close Palette View (if it's open)");
		try {
			new PaletteView().close();
		} catch (WorkbenchLayerException ex) {
			log.info("Palette view is already closed. Nothing to do.");
		}
	}

	/**
	 * Cleans up test environment
	 */
	@After
	public void setupDeleteProjects() {

		new WorkbenchShell();
		EditorHandler.getInstance().closeAll(true);
		ProjectFactory.deleteAllProjects();
	}

	/**
	 * Prepares IDE for tests manipulate with components in the Camel Editor
	 */
	private static void setupPrepareIDEForManipulationTests() {

		new CamelProject("cbr").openCamelContext("camel-context.xml");
		CamelEditor.switchTab("Source");
		EditorManipulator.copyFileContentToCamelXMLEditor("resources/camel-context-otherwise.xml");
		CamelEditor.switchTab("Design");
	}

	/**
	 * <p>
	 * Test tries <i>Palette</i> inside Camel Editor
	 * </p>
	 * <ol>
	 * <li>create a new project from template 'Content Based Router'</li>
	 * <li>open Project Explorer view</li>
	 * <li>open camel-context.xml file</li>
	 * <li>get number of palette items</li>
	 * <li>search for <i>File</i> component</li>
	 * <li>check that <i>File</i> component was found</li>
	 * <li>clear search box</li>
	 * <li>check that all components are available</li>
	 * </ol>
	 */
	@Test
	public void testPalette() {

		new CamelProject("cbr").openCamelContext("camel-context.xml");
		CamelEditor editor = new CamelEditor("camel-context.xml");
		editor.activate();
		int initSize = editor.palleteGetComponents().size();
		editor.paletteSearch("File");
		assertEquals(2, editor.palleteGetComponents().size());
		editor.paletteSearch("");
		assertEquals(initSize, editor.palleteGetComponents().size());
	}

	/**
	 * <p>
	 * Test tries to manipulate with XML source of the camel-context.xml file and checks if it affects components in the
	 * Camel Editor.
	 * </p>
	 * <b>Steps</b>
	 * <ol>
	 * <li>create a new project from template 'Content Based Router'</li>
	 * <li>open Project Explorer view</li>
	 * <li>open camel-context.xml file</li>
	 * <li>switch to Source tab in the Camel Editor</li>
	 * <li>cut branch otherwise</li>
	 * <li>switch to Design tab in the Camel Editor</li>
	 * <li>check if the otherwise component is no longer available in the Camel Editor</li>
	 * <li>switch to Source tab in the Camel Editor</li>
	 * <li>paste branch otherwise</li>
	 * <li>switch to Design tab in the Camel Editor</li>
	 * <li>check if the otherwise component is available in the Camel Editor</li>
	 * </ol>
	 */
	@Test
	public void testXMLEditor() {

		setupPrepareIDEForManipulationTests();
		CamelEditor editor = new CamelEditor("camel-context.xml");
		assertFalse(editor.isComponentAvailable("Otherwise"));
		CamelEditor.switchTab("Source");
		EditorManipulator.copyFileContentToCamelXMLEditor("resources/camel-context-all.xml");
		CamelEditor.switchTab("Design");
		assertTrue(editor.isComponentAvailable("Otherwise"));
		assertTrue(editor.isComponentAvailable("file:target/messages/others"));
		assertTrue(LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * <p>
	 * Test tries to code completion assistant in the Camel Editor on Source tab.
	 * </p>
	 * <ol>
	 * <li>create a new project from template 'Content Based Router'</li>
	 * <li>open Project Explorer view</li>
	 * <li>open camel-context.xml file</li>
	 * <li>switch to Source tab in the Camel Editor</li>
	 * <li>invoke content assistant on from element</li>
	 * <li>check available entries (id, ref)</li>
	 * <li>check that already set attribute uri is not present</li>
	 * <li>add id attribute</li>
	 * <li>check that id attribute is not present in content assistant</li>
	 * <li>invoke content assistant within route element</li>
	 * <li>insert to element</li>
	 * </ol>
	 */
	@Test
	@Jira("FUSETOOLS-2281")
	@RunIf(conditionClass = IssueIsClosed.class)
	public void testCodeCompletion() {

		new CamelProject("cbr").openCamelContext("camel-context.xml");
		CamelEditor.switchTab("Source");
		EditorManipulator.copyFileContentToCamelXMLEditor("resources/camel-context-all.xml");
		SourceEditor editor = new SourceEditor();
		int i = editor.getText().indexOf("<from");
		editor.setCursorPosition(i + 6);
		ContentAssistant assistent = editor.openContentAssistant();
		List<String> proposals = assistent.getProposals();
		assertTrue("Content Assistent does not contain 'id' value", proposals.contains("id"));
		assertTrue("Content Assistent does not contain 'ref' value", proposals.contains("ref"));
		assertFalse("Content Assistent does contain 'uri' value. The attribute is already set",
				proposals.contains("uri"));
		assistent.close();
		editor.insertTest("id=\"test\" ");
		assistent = editor.openContentAssistant();
		proposals = assistent.getProposals();
		assertFalse("Content Assistent does contain 'id' value", proposals.contains("id"));
		assistent.close();
		editor.setCursorPosition(i - 3);
		assistent = editor.openContentAssistant();
		assistent.chooseProposal("to");
		assertTrue("Editor does not contain generated text", editor.getText().contains("<to></to>"));
		i = editor.getText().indexOf("<to");
		editor.setCursorPosition(i + 3);
		editor.insertTest(" ");
		editor.setCursorPosition(i + 4);
		assistent = editor.openContentAssistant();
		proposals = assistent.getProposals();
		assertTrue("Content Assistent does not contain 'uri' value", proposals.contains("uri"));
		assertTrue(LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * <p>
	 * Test tries to add components in the Camel Editor via Drag&Drop feature (Adds components from Palette and
	 * connections between components via the Camel Editor).
	 * </p>
	 * <ol>
	 * <li>create a new project from template 'Content Based Router'</li>
	 * <li>open Project Explorer view</li>
	 * <li>open camel-context.xml file</li>
	 * <li>switch to Source tab in the Camel Editor</li>
	 * <li>remove branch otherwise</li>
	 * <li>switch to Design tab in the Camel Editor</li>
	 * <li>add back removed components (from branch otherwise) via Drag&Drop feature</li>
	 * <li>switch to Source tab in the Camel Editor</li>
	 * <li>check if the content of the file is equals to content of the same file immediately after project creation
	 * </li>
	 * </ol>
	 */
	@Test
	public void testDragAndDropComponents() {

		setupPrepareIDEForManipulationTests();
		CamelEditor editor = new CamelEditor("camel-context.xml");
		editor.addCamelComponent(new Otherwise(), "Choice");
		editor.addCamelComponent(new Log(), "Otherwise");
		try {
			editor.setProperty("Log _log2", "Message *", "Other message");
		} catch (CoreLayerException e) {
			log.warn("There is no component with label 'Log _log2'! See https://issues.jboss.org/browse/FUSETOOLS-2294");
			editor.setProperty("Log", "Message *", "Other message");
		}
		editor.addCamelComponent(new File(), "Otherwise");
		editor.setProperty("file:directoryName", "Uri *", "file:target/messages/others");
		CamelEditor.switchTab("Source");
		assertTrue(EditorManipulator.isEditorContentEqualsFile("resources/camel-context-all.xml"));
		assertTrue(LogGrapper.getPluginErrors("fuse").size() == 0);
	}
}
