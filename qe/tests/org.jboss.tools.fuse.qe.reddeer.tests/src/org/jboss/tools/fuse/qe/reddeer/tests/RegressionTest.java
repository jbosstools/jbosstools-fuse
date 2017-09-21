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

import static org.jboss.reddeer.eclipse.ui.problems.ProblemsView.ProblemType.ERROR;
import static org.jboss.tools.fuse.qe.reddeer.ProjectTemplate.CBR;
import static org.jboss.tools.fuse.qe.reddeer.ProjectType.BLUEPRINT;
import static org.jboss.tools.fuse.qe.reddeer.ProjectType.SPRING;
import static org.jboss.tools.fuse.qe.reddeer.SupportedCamelVersions.CAMEL_2_17_0_REDHAT_630254;
import static org.jboss.tools.fuse.qe.reddeer.SupportedCamelVersions.CAMEL_LATEST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.reddeer.common.exception.WaitTimeoutExpiredException;
import org.jboss.reddeer.common.matcher.RegexMatcher;
import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.core.exception.CoreLayerException;
import org.jboss.reddeer.core.matcher.WithTooltipTextMatcher;
import org.jboss.reddeer.eclipse.debug.core.IsSuspended;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.eclipse.ui.console.ConsoleView;
import org.jboss.reddeer.eclipse.ui.problems.ProblemsView;
import org.jboss.reddeer.eclipse.ui.views.properties.PropertiesView;
import org.jboss.reddeer.junit.execution.annotation.RunIf;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.swt.impl.button.CancelButton;
import org.jboss.reddeer.swt.impl.button.CheckBox;
import org.jboss.reddeer.swt.impl.button.OkButton;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.jboss.reddeer.swt.impl.menu.ContextMenu;
import org.jboss.reddeer.swt.impl.menu.ShellMenu;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.jboss.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.workbench.impl.editor.DefaultEditor;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.qe.reddeer.LogGrapper;
import org.jboss.tools.fuse.qe.reddeer.ProjectType;
import org.jboss.tools.fuse.qe.reddeer.ResourceHelper;
import org.jboss.tools.fuse.qe.reddeer.component.ConvertBodyTo;
import org.jboss.tools.fuse.qe.reddeer.component.Log;
import org.jboss.tools.fuse.qe.reddeer.component.Route;
import org.jboss.tools.fuse.qe.reddeer.component.Timer;
import org.jboss.tools.fuse.qe.reddeer.condition.IssueIsClosed;
import org.jboss.tools.fuse.qe.reddeer.condition.IssueIsClosed.Jira;
import org.jboss.tools.fuse.qe.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.qe.reddeer.perspectives.FuseIntegrationPerspective;
import org.jboss.tools.fuse.qe.reddeer.preference.FuseServerRuntimePreferencePage;
import org.jboss.tools.fuse.qe.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.EditorManipulator;
import org.jboss.tools.fuse.qe.reddeer.tests.utils.ProjectFactory;
import org.jboss.tools.fuse.qe.reddeer.view.ErrorLogView;
import org.jboss.tools.fuse.qe.reddeer.view.FuseJMXNavigator;
import org.jboss.tools.fuse.qe.reddeer.view.FusePropertiesView;
import org.jboss.tools.fuse.qe.reddeer.view.ProblemsViewExt;
import org.jboss.tools.fuse.qe.reddeer.view.FusePropertiesView.PropertyType;
import org.jboss.tools.fuse.qe.reddeer.widget.LabeledTextExt;
import org.jboss.tools.fuse.qe.reddeer.wizard.NewFuseIntegrationProjectWizard;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Class contains test cases verifying resolved issues
 * 
 * @author tsedmik
 */
@OpenPerspective(FuseIntegrationPerspective.class)
@RunWith(RedDeerSuite.class)
public class RegressionTest extends DefaultTest {

	@After
	public void setupClean() {

		ProjectFactory.deleteAllProjects();
		new ErrorLogView().deleteLog();
	}

	/**
	 * <p>
	 * GUI editor issue when using route scoped onException
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-674">https://issues.jboss.org/browse/FUSETOOLS-674</a> <br>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1728">https://issues.jboss.org/browse/FUSETOOLS-1728</a> <br>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1729">https://issues.jboss.org/browse/FUSETOOLS-1729</a> <br>
	 */
	@Test
	public void issue_674_1728_1729() throws ParserConfigurationException, SAXException, IOException {

		ProjectFactory.newProject("camel-spring").template(CBR).type(SPRING).create();
		new CamelProject("camel-spring").openCamelContext("camel-context.xml");
		new DefaultCTabItem("Source").activate();

		// copy sample of camel-context.xml
		File testFile = new File(
				ResourceHelper.getResourceAbsolutePath(Activator.PLUGIN_ID, "resources/camel-context.xml"));
		DefaultStyledText editor = new DefaultStyledText();
		try (Scanner scanner = new Scanner(testFile)) {
			scanner.useDelimiter("\\Z");
			editor.setText(scanner.next());
		}

		new DefaultCTabItem("Design").activate();
		new DefaultCTabItem("Source").activate();
		new ShellMenu("File", "Save").select();

		// check XML content
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(new DefaultStyledText().getText()));
		Document doc = builder.parse(is);
		int i = doc.getElementsByTagName("onException").item(0).getChildNodes().getLength();

		assertEquals("'camel-context.xml' file was changed!", 11, i);
	}

	/**
	 * <p>
	 * Propose a DebugAs option to start CamelContext & debug java code used by beans from camel routes
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-853">https://issues.jboss.org/browse/FUSETOOLS-853</a>
	 */
	@Test
	public void issue_853() {

		ProjectFactory.newProject("camel-blueprint").template(CBR).type(BLUEPRINT).create();
		new ProjectExplorer().getProject("camel-blueprint")
				.getProjectItem("src/main/resources", "OSGI-INF", "blueprint", "blueprint.xml").select();
		try {
			new ContextMenu("Debug As", "3 Local Camel Context (without tests)");
		} catch (Exception e) {
			fail("Context menu 'Debug As --> Local Camel Context' is missing");
		}
	}

	/**
	 * <p>
	 * New Server Runtime Wizard - Finish button error
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1076">https://issues.jboss.org/browse/FUSETOOLS-1076</a>
	 */
	@Test
	public void issue_1076() {

		WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
		FuseServerRuntimePreferencePage serverRuntime = new FuseServerRuntimePreferencePage();
		dialog.open();
		dialog.select(serverRuntime);
		new PushButton("Add...").click();
		new WaitUntil(new ShellWithTextIsAvailable("New Server Runtime Environment"));
		new DefaultShell("New Server Runtime Environment").setFocus();
		AbstractWait.sleep(TimePeriod.SHORT);
		new DefaultTreeItem("JBoss Fuse", "JBoss Fuse 6.1").select();
		if (new PushButton("Finish").isEnabled()) {
			new PushButton("Cancel").click();
			new DefaultShell("Preferences").close();
			fail("'Finish' button should not be enabled!");
		}
	}

	/**
	 * <p>
	 * JMX Navigator - prevent from close Camel Context
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1115">https://issues.jboss.org/browse/FUSETOOLS-1115</a>
	 */
	@Test
	public void issue_1115() {

		ProjectFactory.newProject("camel-spring").version(CAMEL_2_17_0_REDHAT_630254).template(CBR).type(ProjectType.SPRING).create();
		new CamelProject("camel-spring").runCamelContextWithoutTests("camel-context.xml");
		AbstractWait.sleep(TimePeriod.NORMAL);
		new FuseJMXNavigator().getNode("Local Camel Context", "Camel", "cbr-example-context").select();

		try {
			new ContextMenu("Close Camel Context");
		} catch (CoreLayerException ex) {
			return;
		} finally {
			new ConsoleView().terminateConsole();
		}

		fail("Context menu item 'Close Camel Context' is available!");
	}

	/**
	 * <p>
	 * context id is removed on save
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1123">https://issues.jboss.org/browse/FUSETOOLS-1123</a>
	 * 
	 * @throws FuseTemplateNotFoundException
	 *             Fuse archetype was not found. Tests cannot be executed!
	 */
	@Test
	public void issue_1123() {

		ProjectFactory.newProject("camel-spring").template(CBR).type(SPRING).create();
		new CamelProject("camel-spring").openCamelContext("camel-context.xml");
		CamelEditor.switchTab("Source");
		EditorManipulator.copyFileContentToCamelXMLEditor("resources/camel-context-routeContextId.xml");
		CamelEditor.switchTab("Design");
		CamelEditor.switchTab("Source");
		String editorText = new DefaultStyledText().getText();
		assertTrue(editorText.contains("id=\"test\""));
	}

	/**
	 * <p>
	 * New Fuse Project - Finish button
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1149">https://issues.jboss.org/browse/FUSETOOLS-1149</a>
	 */
	@Test
	public void issue_1149() {

		new ShellMenu("File", "New", "Fuse Integration Project").select();
		new DefaultShell("New Fuse Integration Project");
		if (new PushButton("Finish").isEnabled()) {
			new DefaultShell().close();
			fail("'Finish' button should not be enabled!");
		}
	}

	/**
	 * <p>
	 * fix ugly title when debugging
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1158">https://issues.jboss.org/browse/FUSETOOLS-1158</a>
	 */
	@Test
	public void issue_1158() {

		ProjectFactory.newProject("camel-spring").version(CAMEL_2_17_0_REDHAT_630254).template(CBR).type(SPRING).create();
		CamelProject project = new CamelProject("camel-spring");
		project.openCamelContext("camel-context.xml");
		CamelEditor editor = new CamelEditor("camel-context.xml");
		editor.activate();
		editor.setBreakpoint("Log _log1");
		editor.selectEditPart("file:work/cbr/input");
		editor.setProperty("Uri *", "file:src/main/data?noop=true");
		editor.save();
		project.debugCamelContextWithoutTests("camel-context.xml");
		new WaitUntil(new IsSuspended(), TimePeriod.LONG);
		try {
			new DefaultEditor(new RegexMatcher("camel-context.xml"));
		} catch (Exception e) {
			fail("Debuger Editor has wrong name");
		} finally {
			new ConsoleView().terminateConsole();
		}
	}

	/**
	 * <p>
	 * remove use of the customId attribute
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1172">https://issues.jboss.org/browse/FUSETOOLS-1172</a>
	 */
	@Test
	public void issue_1172() throws ParserConfigurationException, SAXException, IOException {

		ProjectFactory.newProject("camel-spring").template(CBR).type(SPRING).create();
		new CamelProject("camel-spring").openCamelContext("camel-context.xml");
		CamelEditor.switchTab("Design");
		CamelEditor editor = new CamelEditor("camel-context.xml");
		editor.click(5, 5);
		editor.setProperty("Route cbr-route", "Id", "1");
		editor.save();
		CamelEditor.switchTab("Source");

		// check XML content
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(new DefaultStyledText().getText()));
		Document doc = builder.parse(is);
		assertNull(doc.getElementsByTagName("route").item(0).getAttributes().getNamedItem("customId"));
		editor.close(true);
	}

	/**
	 * <p>
	 * Opening Camel Editor on Source tab + dirty flag
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1243">https://issues.jboss.org/browse/FUSETOOLS-1243</a>
	 */
	@Test
	public void issue_1243() {

		ProjectFactory.newProject("camel-spring").template(CBR).type(SPRING).create();
		new CamelProject("camel-spring").openCamelContext("camel-context.xml");
		assertFalse("Camel editor should not be dirty",
				new DefaultToolItem(new WorkbenchShell(), 1, new WithTooltipTextMatcher(new RegexMatcher("Save.*")))
						.isEnabled());
		CamelEditor.switchTab("Source");
		assertFalse("Camel editor should not be dirty",
				new DefaultToolItem(new WorkbenchShell(), 1, new WithTooltipTextMatcher(new RegexMatcher("Save.*")))
						.isEnabled());
		DefaultEditor editor = new DefaultEditor("camel-context.xml");
		editor.activate();
		editor.close();
	}

	/**
	 * <p>
	 * Run Configurations dialog shows launch config types for server adapters for Karaf and Fuse which
	 * partially don't work
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1214">https://issues.jboss.org/browse/FUSETOOLS-1214</a>
	 */
	@Test
	public void issue_1214() {

		ProjectFactory.newProject("camel-spring").template(CBR).type(SPRING).create();
		new ProjectExplorer().selectProjects("camel-spring");
		new ContextMenu("Run As", "Run Configurations...").select();
		new WaitUntil(new ShellWithTextIsAvailable("Run Configurations"));
		new DefaultShell("Run Configurations");
		new DefaultTreeItem("Java Application").select();
		try {
			new DefaultTreeItem("Apache Karaf Launcher").select();
			fail("Run Configurations contains forbidden item");
		} catch (CoreLayerException e) {
		}
		try {
			new DefaultTreeItem("JBoss Fuse Launcher").select();
			fail("Run Configurations contains forbidden item");
		} catch (CoreLayerException e) {
		}
	}

	/**
	 * <p>
	 * Camel Editor is still indicating that something was changed
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1403">https://issues.jboss.org/browse/FUSETOOLS-1403</a>
	 */
	@Test
	public void issue_1403() {

		ProjectFactory.newProject("camel-blueprint").template(CBR).type(BLUEPRINT).create();
		new CamelProject("camel-blueprint").selectProjectItem("src/main/resources", "OSGI-INF", "blueprint",
				"blueprint.xml");
		new ContextMenu("Open").select();
		assertFalse("Camel Editor is dirty! But no editing was performed.", new CamelEditor("blueprint.xml").isDirty());
	}

	/**
	 * <p>
	 * Graphic is disposed when using "Go into"
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1678">https://issues.jboss.org/browse/FUSETOOLS-1678</a>
	 */
	@Test
	public void issue_1678() {

		ProjectFactory.newProject("test-empty").version(CAMEL_LATEST).type(SPRING)
				.create();
		CamelEditor editor = new CamelEditor("camel-context.xml");
		editor.addCamelComponent(new Route(), 100, 100);
		editor.selectEditPart("Route _route1");
		FusePropertiesView propertiesView = new FusePropertiesView();
		propertiesView.activate();
		propertiesView.selectTab("Details");
		propertiesView.setProperty(PropertyType.TEXT, "Auto Startup", "true");
		editor.save();
		editor.doOperation("Route _route1", "Go Into");
		assertFalse("Invoke of 'Go Into' failed", editor.isComponentAvailable("Route _route2"));
		assertTrue(LogGrapper.getPluginErrors("fuse").size() == 0);
		editor.doOperation("Route _route1", "Show Camel Context");
		assertTrue("Invoke of 'Show Camel Context' failed", editor.isComponentAvailable("Route _route2"));
		assertTrue(LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * Resolved problems in Camel Context file by deleting an element are not propagated into Problems view and Project
	 * Explorer
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1694">https://issues.jboss.org/browse/FUSETOOLS-1694</a>
	 */
	@Test
	public void issue_1694() {

		ProjectFactory.newProject("cbr-spring").version(CAMEL_2_17_0_REDHAT_630254).template(CBR).type(SPRING).create();
		CamelEditor editor = new CamelEditor("camel-context.xml");
		editor.addCamelComponent(new ConvertBodyTo(), "Route cbr-route");
		ProblemsView problems = new ProblemsView();
		problems.open();
		editor.deleteCamelComponent("ConvertBodyTo _convertBodyTo1");
		new CamelProject("cbr-spring").selectCamelContext("camel-context.xml");
		new ContextMenu("Validate").select();
		assertTrue("Problems view contains errors", problems.getProblems(ERROR).isEmpty());
	}

	/**
	 * <p>
	 * Node not deleted if trying to delete two elements sequentially which are the starting point of the route
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1701">https://issues.jboss.org/browse/FUSETOOLS-1701</a>
	 */
	@Test
	@Jira("FUSETOOLS-1701")
	@RunIf(conditionClass = IssueIsClosed.class)
	public void issue_1701() {
		ProjectFactory.newProject("test").type(BLUEPRINT).create();
		CamelEditor editor = new CamelEditor("blueprint.xml");
		editor.activate();
		editor.addComponent("File", "Route _route1");
		editor.selectEditPart("file:directoryName");
		editor.setProperty("Uri *", "file:src/main/data?noop=true");
		editor.addComponent("File", "file:src/main/data?noop=true");
		editor.addComponent("Log", "file:directoryName");
		editor.setProperty("Message *", "XXX");
		editor.save();
		new ErrorLogView().deleteLog();
		editor.activate();
		editor.deleteCamelComponent("file:src/main/data?noop=true");
		editor.deleteCamelComponent("file:directoryName");
		editor.save();
		assertFalse("Deleted component is still present in Camel Editor!",
				editor.isComponentAvailable("file:directoryName"));
		assertTrue("There are some errors in Error Log!", LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * <p>
	 * Double-click on an error in problems View of a Fuse error when corresponding editor is closed don't select the
	 * correct node.
	 * </p>
	 * <b>Link: </b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1726">https://issues.jboss.org/browse/FUSETOOLS-1726</a>
	 */
	@Test
	@Jira("FUSETOOLS-1726")
	@RunIf(conditionClass = IssueIsClosed.class)
	public void issue_1726() {
		ProjectFactory.newProject("test").type(BLUEPRINT).create();
		CamelEditor editor = new CamelEditor("blueprint.xml");
		editor.activate();
		editor.addComponent("IMAP", "Route _route1");
		editor.close();
		new ErrorLogView().deleteLog();
		ProblemsViewExt problems = new ProblemsViewExt();
		problems.open();
		AbstractWait.sleep(TimePeriod.getCustom(5));
		problems.doubleClickProblem("The parameter port requires a numeric value.", ERROR);
		AbstractWait.sleep(TimePeriod.getCustom(5));
		try {
			new CamelEditor("blueprint.xml");
		} catch (Exception e) {
			fail("Camel Editor was not opened after double-click on a problem in Problems view!");
		}
		PropertiesView properties = new PropertiesView();
		properties.activate();
		try {
			properties.selectTab("Details");
			String title = new LabeledTextExt("Uri *").getText();
			assertEquals("imap:host:port", title);
		} catch (Exception e) {
			fail("Properties view does not contains properties of appropriate Camel Component!");
		}
	}

	/**
	 * <p>
	 * Error during a project deletion if switched to source after saving modifications in design editor
	 * </p>
	 * <b>Link:</b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1730">https://issues.jboss.org/browse/FUSETOOLS-1730</a>
	 */
	@Test
	public void issue_1730() {
		ProjectFactory.newProject("camel-spring").template(CBR).type(SPRING).create();
		CamelEditor editor = new CamelEditor("camel-context.xml");
		editor.activate();
		editor.setId("Route cbr-route", "1");
		editor.save();
		CamelEditor.switchTab("Source");
		new ProjectExplorer().getProject("camel-spring").select();
		new ContextMenu("Delete").select();
		new WaitUntil(new ShellWithTextIsAvailable("Delete Resources"));
		new DefaultShell("Delete Resources");
		new CheckBox().toggle(true);
		new OkButton().click();
		AbstractWait.sleep(TimePeriod.SHORT);
		try {
			// issue occurred!
			new WaitUntil(new ShellWithTextIsAvailable("Delete Resources"));
			new CancelButton().click();
			// perform workaround to project deletion
			new DefaultEditor().close();
			new ProjectExplorer().deleteAllProjects();
			fail("Error during project deletion occurred - see https://issues.jboss.org/browse/FUSETOOLS-1730");
		} catch (WaitTimeoutExpiredException e) {
			// issue is not present - great :-)
		}
	}

	/**
	 * <p>
	 * Choice should only allow a single Otherwise
	 * </p>
	 * <b>Link:</b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-1993">https://issues.jboss.org/browse/FUSETOOLS-1993</a>
	 */
	@Test
	public void issue_1993() {
		ProjectFactory.newProject("cbr-spring").template(CBR).type(SPRING).create();
		CamelEditor editor = new CamelEditor("camel-context.xml");
		CamelEditor.switchTab("Source");
		String beforeContent = new DefaultStyledText().getText();
		CamelEditor.switchTab("Design");
		editor.addComponent("Otherwise", "Choice");
		CamelEditor.switchTab("Source");
		String afterContent = new DefaultStyledText().getText();
		assertTrue("More then one 'Otherwise' component is present in Camel Editor",
				beforeContent.contentEquals(afterContent));
		assertTrue(LogGrapper.getPluginErrors("fuse").size() == 0);
	}

	/**
	 * <p>
	 * New Fuse Integration Project Wizard should prevent selection of disabled DSL type
	 * </p>
	 * <b>Link:</b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-2051">https://issues.jboss.org/browse/FUSETOOLS-2051</a>
	 */
	@Test
	public void issue_2051() {
		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		wiz.setProjectName("test-erfiojkn");
		wiz.next();
		wiz.next();
		wiz.selectTemplate("Fuse on EAP", "Medium", "Spring on EAP");
		if (wiz.isProjectTypeAvailable(BLUEPRINT) || wiz.isProjectTypeSelected(BLUEPRINT)) {
			wiz.cancel();
			fail("Disabled project type is selected - see https://issues.jboss.org/browse/FUSETOOLS-2051");
		}
	}

	/**
	 * <p>
	 * Local Launch doesn't work all times
	 * </p>
	 * <b>Link:</b>
	 * <a href="https://issues.jboss.org/browse/FUSETOOLS-2062">https://issues.jboss.org/browse/FUSETOOLS-2062</a>
	 */
	@Test
	public void issue_2062() {

		ProjectFactory.newProject("test-empty").version(CAMEL_2_17_0_REDHAT_630254).type(SPRING)
				.create();
		CamelEditor.switchTab("Source");
		CamelEditor.switchTab("Design");
		CamelEditor editor = new CamelEditor("camel-context.xml");
		editor.addCamelComponent(new Timer(), "Route _route1");
		editor.addCamelComponent(new Log(), "Route _route1");
		FusePropertiesView propertiesView = new FusePropertiesView();
		propertiesView.activate();
		propertiesView.selectTab("Details");
		propertiesView.setProperty(PropertyType.TEXT, "Message", "test-log-message");
		editor.activate();
		editor.save();
		new CamelProject("test-empty").runCamelContext();
		ConsoleView console = new ConsoleView();
		if (console.getConsoleText().contains("BUILD FAILURE")
				|| console.getConsoleText().toLowerCase().contains("[ERROR]") || console.consoleIsTerminated())
			fail("There is a problem with building 'test-empty' project");
		assertTrue("Running project 'test-empty' doesn't contains control 'test-log-message'",
				console.getConsoleText().contains("test-log-message"));
	}

}
