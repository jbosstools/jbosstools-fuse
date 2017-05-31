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
package org.jboss.tools.fuse.qe.reddeer.projectexplorer;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.jboss.reddeer.common.exception.WaitTimeoutExpiredException;
import org.jboss.reddeer.common.matcher.RegexMatcher;
import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.core.exception.CoreLayerException;
import org.jboss.reddeer.core.matcher.WithTextMatcher;
import org.jboss.reddeer.eclipse.condition.ConsoleHasText;
import org.jboss.reddeer.eclipse.core.resources.Project;
import org.jboss.reddeer.eclipse.core.resources.ProjectItem;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.swt.impl.button.CheckBox;
import org.jboss.reddeer.swt.impl.button.OkButton;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.menu.ContextMenu;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.tools.fuse.qe.reddeer.editor.CamelEditor;

/**
 * Manipulates with Camel projects
 * 
 * @author apodhrad, tsedmik
 */
public class CamelProject {

	private Project project;

	public CamelProject(String name) {

		project = new ProjectExplorer().getProject(name);
	}

	public void selectProjectItem(String... path) {
		project.getProjectItem(path).select();
	}

	public void openFile(String... path) {

		ProjectItem item = project.getProjectItem(path);
		item.open();
	}

	public void openCamelContext(String name) {
		try {
			openFile("src/main/resources", "META-INF", "spring", name);
		} catch (Throwable t) {
			openFile("src/main/resources", "OSGI-INF", "blueprint", name);
		}
	}

	public void selectCamelContext(String name) {

		project.getProjectItem("src/main/resources", "META-INF", "spring", name).select();
	}

	public void runCamelContext() {

		project.getProjectItem("Camel Contexts").getChildren().get(0).select();
		try {
			new ContextMenu("Run As", "2 Local Camel Context").select();
		} catch (CoreLayerException ex) {
			new ContextMenu("Run As", "1 Local Camel Context").select();
		}

		ConsoleHasText camel = new ConsoleHasText("Starting Camel ...");
		ConsoleHasText jetty = new ConsoleHasText("Started Jetty Server");
		ConsoleHasText failure = new ConsoleHasText("BUILD FAILURE");
		boolean started = false;
		for (int i = 0; i < 300; i++) {
			if (camel.test() || jetty.test()) {
				started = true;
				break;
			}
			if (failure.test()) {
				break;
			}
			AbstractWait.sleep(TimePeriod.SHORT);
		}
		if (!started) {
			new WaitTimeoutExpiredException("Console doesn't contains 'Starting Camel ...' or 'Started Jetty Server'");
		}

		AbstractWait.sleep(TimePeriod.NORMAL);
		new WaitUntil(new ConsoleHasText("started and consuming from"), TimePeriod.VERY_LONG);
	}

	public void runCamelContextWithoutTests(String name) {

		String id = getCamelContextId("src/main/resources", "META-INF", "spring", name);
		project.getProjectItem("src/main/resources", "META-INF", "spring", name).select();
		new ContextMenu("Run As", "3 Local Camel Context (without tests)").select();
		new WaitUntil(new ConsoleHasText("(CamelContext: " + id + ") started"), TimePeriod.VERY_LONG);
	}

	public void debugCamelContextWithoutTests(String name) {

		new ProjectExplorer().open();
		project.getProjectItem("src/main/resources", "META-INF", "spring", name).select();
		new ContextMenu("Debug As", "3 Local Camel Context (without tests)").select();
		closeSecureStorage();
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		closePerspectiveSwitchWindow();
	}

	/**
	 * Tries to close 'Secure Storage' dialog window
	 */
	private static void closeSecureStorage() {
		try {
			new WaitUntil(new ShellWithTextIsAvailable(new WithTextMatcher(new RegexMatcher("Secure Storage.*"))), TimePeriod.NORMAL);
		} catch (RuntimeException ex1) {
			return;
		}
		new DefaultShell(new WithTextMatcher(new RegexMatcher("Secure Storage.*")));
		new LabeledText("Password:").setText("admin");
		new OkButton().click();
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	public void enableCamelNature() {

		project.select();
		try {
			new ContextMenu("Enable Fuse Camel Nature").select();
			new WaitWhile(new JobIsRunning());
		} catch (CoreLayerException e) {
			// Nature is probably already enabled
		}
	}

	/**
	 * Tries to close 'Confirm Perspective Switch' window. This window is appeared after debugging is started.
	 */
	private void closePerspectiveSwitchWindow() {

		for (int i = 0; i < 5; i++) {
			if (new ShellWithTextIsAvailable("Confirm Perspective Switch").test()) {
				new DefaultShell("Confirm Perspective Switch");
				new CheckBox("Remember my decision").toggle(true);
				new PushButton("No").click();
			}
			AbstractWait.sleep(TimePeriod.SHORT);
		}
	}

	public File getFile() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		return new File(new File(root.getLocationURI().getPath()), project.getName());
	}

	public File getCamelContextFile(String name) throws FileNotFoundException {
		File file = new File(getFile(), "src/main/resources/META-INF/spring/" + name);
		if (file.exists()) {
			return file;
		}
		file = new File(getFile(), "src/main/resources/OSGI-INF/blueprint/" + name);
		if (file.exists()) {
			return file;
		}
		throw new FileNotFoundException("Cannot find '" + name + "'");
	}

	public void update() {
		project.select();
		new ContextMenu("Maven", "Update Project...").select();
		new DefaultShell("Update Maven Project");
		new CheckBox("Force Update of Snapshots/Releases").toggle(true);
		new PushButton("OK").click();

		AbstractWait.sleep(TimePeriod.NORMAL);
		new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
	}

	/**
	 * Retrieves value of id attribute of given Camel Context file. Important - This method will work only on blueprint
	 * or spring projects!
	 * 
	 * @param name
	 *            Name of a Camel Context file
	 * @return value of id attribute
	 * @throws CoreException
	 */
	public String getCamelContextId(String... path) {
		openFile(path);
		CamelEditor editor = new CamelEditor(path[path.length - 1]);
		try {
			if (editor.xpath("/blueprint").equals("true")) {
				return editor.xpath("/blueprint/camelContext/@id");
			} else {
				return editor.xpath("/beans/camelContext/@id");
			}
		} catch (CoreException e) {
			return null;
		}
	}
}
