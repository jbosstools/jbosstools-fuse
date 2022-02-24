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

import static org.jboss.tools.fuse.reddeer.ProjectTemplate.CBR_SPRING;
import static org.jboss.tools.fuse.reddeer.editor.CamelEditor.SOURCE_TAB;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.List;

import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.jface.text.contentassist.ContentAssistant;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanerrorlog.CleanErrorLogRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.editor.SourceEditor;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.utils.LogChecker;
import org.jboss.tools.fuse.reddeer.utils.ProjectFactory;
import org.jboss.tools.fuse.ui.bot.tests.utils.EditorManipulator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;

/**
 * Tests <i>Apache Camel Tooling (LSP)</i> in Camel Editor (Source tab)</br>
 * <b>Quick tests:</b>
 * <ul>
 * <li>testComponentSchemes</li>
 * <li>testEndpointOptions</li>
 * <li>testAdditionalEndpointOptions</li>
 * <li>testDuplicateOptionsFilltering</li>
 * </ul>
 *
 * @author djelinek
 *
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class CamelLSPCompletionTest {

	public static final String PROJECT_NAME = "lsp";
	public static final String CAMEL_CONTEXT = "camel-context.xml";
	public static final String INSERT_SPACE = " ";

	public static enum Options {
		ID("id"), URI("uri"), REF("ref");

		private String option;

		Options(String option) {
			this.option = option;
		}

		@Override
		public String toString() {
			return option;
		}
	}

	private SourceEditor editor;
	private ContentAssistant assistant;
	private int cursorPosition;

	@BeforeClass
	public static void prepareEnvironment() {
		ProjectFactory.newProject(PROJECT_NAME).deploymentType(STANDALONE).runtimeType(KARAF).template(CBR_SPRING)
				.create();
		new CamelProject(PROJECT_NAME).openCamelContext(CAMEL_CONTEXT);
		CamelEditor.switchTab(SOURCE_TAB);
	}

	@Before
	public void initSetup() {
		new CleanErrorLogRequirement().fulfill();
	}

	@After
	public void cleanEditor() {
		new CamelProject(PROJECT_NAME).openCamelContext(CAMEL_CONTEXT);
		CamelEditor.switchTab(SOURCE_TAB);
		EditorManipulator.copyFileContentToCamelXMLEditor("resources/camel-context-cbr.xml");
	}

	@AfterClass
	public static void cleanWorkspace() {
		new CleanWorkspaceRequirement().fulfill();
	}

	@Rule
	public ErrorCollector collector = new ErrorCollector();

	/**
	 * Tests code completion is working for component schemes (the part before the ":" )
	 */
	@Test
	public void testComponentSchemes() {
		editor = new SourceEditor();

		cursorPosition = editor.getText().indexOf("<from");
		editor.setCursorPosition(cursorPosition + 24);
		assertComponentSchemes(editor.getCompletionProposals());

		cursorPosition = editor.getText().indexOf("<to");
		editor.setCursorPosition(cursorPosition + 20);
		assertComponentSchemes(editor.getCompletionProposals());

		LogChecker.assertNoFuseError();
	}

	/**
	 * Tests code completion is working for endpoint options (the part after the "?" )
	 */
	@Test
	public void testEndpointOptions() {
		editor = new SourceEditor();

		cursorPosition = editor.getText().indexOf("<from");
		editor.setCursorPosition(cursorPosition += 42);
		tryEndpointOptionsCompletion();

		cursorPosition = editor.getText().indexOf("<to");
		editor.setCursorPosition(cursorPosition += 39);
		tryEndpointOptionsCompletion();

		LogChecker.assertNoFuseError();
	}

	/**
	 * Tests code completion is working for additional endpoint options (the part after "&" )
	 */
	@Test
	public void testAdditionalEndpointOptions() {
		editor = new SourceEditor();

		cursorPosition = editor.getText().indexOf("<from");
		editor.setCursorPosition(cursorPosition += 42);
		tryAdditionalOptionsCompletion();

		cursorPosition = editor.getText().indexOf("<to");
		editor.setCursorPosition(cursorPosition += 39);
		tryAdditionalOptionsCompletion();

		LogChecker.assertNoFuseError();
	}

	/**
	 * Tests duplicate endpoint options are filtered out
	 */
	@Test
	public void testDuplicateOptionsFilltering() {
		editor = new SourceEditor();

		cursorPosition = editor.getText().indexOf("<from");
		editor.setCursorPosition(cursorPosition += 5);
		editor.selectText(cursorPosition, cursorPosition + 38);
		editor.insertText(INSERT_SPACE);

		assertDuplicateOptionsFilltering(Options.ID.option);
		assertDuplicateOptionsFilltering(Options.URI.option);
		assertDuplicateOptionsFilltering(Options.REF.option);

		cursorPosition = editor.getText().indexOf("<to");
		editor.setCursorPosition(cursorPosition += 3);
		editor.selectText(cursorPosition, cursorPosition + 42);
		editor.insertText(INSERT_SPACE);

		assertDuplicateOptionsFilltering(Options.ID.option);
		assertDuplicateOptionsFilltering(Options.URI.option);
		assertDuplicateOptionsFilltering(Options.REF.option);

		LogChecker.assertNoFuseError();
	}

	private void assertComponentSchemes(List<String> proposals) {
		collector.checkThat("Content assistant is empty", proposals.isEmpty(), equalTo(false));
		collector.checkThat("Content assistant is not filtered", proposals.get(0).startsWith("f"), equalTo(true));
	}

	private void assertDuplicateOptionsFilltering(String proposal) {
		AbstractWait.sleep(TimePeriod.getCustom(1));
		collector.checkThat(proposal, editor.getCompletionProposals().contains(proposal), equalTo(true));
		selectCompletionProposal(proposal);
		collector.checkThat(proposal, editor.getCompletionProposals().contains(proposal), equalTo(false));
	}

	private void tryEndpointOptionsCompletion() {
		editor.insertText("?e");
		editor.setCursorPosition(cursorPosition += 2);
		AbstractWait.sleep(TimePeriod.getCustom(1));
		assistant = editor.openContentAssistant();
		collector.checkThat(assistant.getProposals().isEmpty(), equalTo(false));
		assistant.chooseProposal("exchangePattern");
		AbstractWait.sleep(TimePeriod.getCustom(1));
		assistant = editor.openContentAssistant();
		collector.checkThat(assistant.getProposals().isEmpty(), equalTo(false));
		assistant.chooseProposal("InOnly");
		editor.save();
	}

	private void tryAdditionalOptionsCompletion() {
		editor.insertText("?fileName=testFileName&amp;");
		editor.setCursorPosition(cursorPosition += 27);
		AbstractWait.sleep(TimePeriod.getCustom(1));
		assistant = editor.openContentAssistant();
		collector.checkThat(assistant.getProposals().isEmpty(), equalTo(false));
		assistant.chooseProposal("allowNullBody");
		editor.save();
	}

	private void selectCompletionProposal(String option) {
		AbstractWait.sleep(TimePeriod.getCustom(1));	
		editor.openContentAssistant().chooseProposal(option);
		editor.setCursorPosition(cursorPosition += 6);
		editor.insertText(INSERT_SPACE);
		editor.setCursorPosition(cursorPosition += 1);
	}

}
