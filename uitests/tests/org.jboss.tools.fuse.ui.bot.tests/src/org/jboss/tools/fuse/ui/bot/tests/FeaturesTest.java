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

import static org.jboss.tools.fuse.reddeer.ProjectTemplate.CBR_BLUEPRINT;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType.KARAF;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.reddeer.common.matcher.RegexMatcher;
import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.core.exception.CoreLayerException;
import org.eclipse.reddeer.core.matcher.WithTextMatcher;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.gef.impl.editpart.LabeledEditPart;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.eclipse.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.text.DefaultText;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.reddeer.ProjectTemplate;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.reddeer.preference.FuseToolingEditorPreferencePage;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.ui.bot.tests.utils.ProjectFactory;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Class contains test cases on a variety of feature requests
 * 
 * @author tsedmik
 */
@CleanWorkspace
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class FeaturesTest extends DefaultTest {

	/**
	 * <p>
	 * Graphical Editor - Add option to configure if labels should be shown or not
	 * <p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-837">https://issues.jboss.org/browse/FUSETOOLS-837</a>
	 */
	@Test
	public void test_837() {

		ProjectFactory.newProject("camel-spring").deploymentType(STANDALONE).runtimeType(KARAF)
				.template(ProjectTemplate.CBR_SPRING).create();
		new CamelProject("camel-spring").openCamelContext("camel-context.xml");
		CamelEditor.switchTab("Design");
		CamelEditor editor = new CamelEditor("camel-context.xml");
		editor.activate();
		editor.setId("file:work/cbr/input", "start");

		// enable "If enabled the ID values will be used for labels if existing"
		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		FuseToolingEditorPreferencePage page = new FuseToolingEditorPreferencePage(dialog);
		dialog.open();
		dialog.select(page);
		page.setShowIDinEditor(true);
		dialog.ok();
		editor.activate();
		try {
			new LabeledEditPart("From start").select();
		} catch (Exception e) {
			fail("'From' endpoint should be named after id value 'start'");
		}

		// disable "If enabled the ID values will be used for labels if existing"
		dialog.open();
		dialog.select(page);
		page.setShowIDinEditor(false);
		dialog.ok();
		editor.activate();
		try {
			new LabeledEditPart("file:work/cbr/input").select();
		} catch (Exception e) {
			fail("'From' endpoint should be named after uri value 'file:src/data?noo...'");
		}
	}

	/**
	 * <p>
	 * Add Context Menu to Camel Contexts folder
	 * <p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1274">https://issues.jboss.org/browse/FUSETOOLS-1274</a>
	 */
	@Test
	public void test_1274() {

		ProjectFactory.newProject("camel-blueprint").deploymentType(STANDALONE).runtimeType(KARAF)
				.template(CBR_BLUEPRINT).create();
		new ProjectExplorer().getProject("camel-blueprint").getProjectItem("Camel Contexts").select();
		AbstractWait.sleep(TimePeriod.SHORT);

		// 1. check context menu item "New Camel XML File"
		try {
			new ContextMenuItem("New Camel XML File");
		} catch (CoreLayerException ex) {
			fail("'Camel Contexts' does not have a context menu entry 'New Camel XML File'!");
		}

		// 2. check "RouteContainer"
		new ContextMenuItem("New Camel XML File").select();
		new WaitUntil(new ShellIsAvailable("New Camel Context XML File"));
		new DefaultShell("New Camel Context XML File");
		assertTrue(new LabeledText("RouteContainer:").getText().contains("src/main/resources/OSGI-INF/blueprint"));
		new PushButton("Finish").click();
		new WaitWhile(new ShellIsAvailable("New Camel Context XML File"));

		// 3. check that wizard didn't overwrite existing file
		new ProjectExplorer().getProject("camel-blueprint").getProjectItem("Camel Contexts").select();
		AbstractWait.sleep(TimePeriod.SHORT);
		new ContextMenuItem("New Camel XML File").select();
		new WaitUntil(new ShellIsAvailable("New Camel Context XML File"));
		new DefaultShell("New Camel Context XML File");
		new DefaultText(new WithTextMatcher(new RegexMatcher(" A file with that name.*")));
		new PushButton("Cancel").click();
	}
}
