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
package org.jboss.tools.fuse.reddeer.projectexplorer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.reddeer.common.exception.WaitTimeoutExpiredException;
import org.eclipse.reddeer.common.matcher.RegexMatcher;
import org.eclipse.reddeer.common.util.XPathEvaluator;
import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.core.exception.CoreLayerException;
import org.eclipse.reddeer.core.matcher.WithTextMatcher;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.core.resources.Project;
import org.eclipse.reddeer.eclipse.core.resources.ProjectItem;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.button.OkButton;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.jboss.tools.fuse.reddeer.MavenDependency;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.xml.sax.SAXException;

/**
 * Manipulates with Camel projects
 * 
 * @author apodhrad, tsedmik
 */
public class CamelProject {

	private String name;

	public CamelProject(String name) {
		this.name = name;
		getProject(); //ensure project is available
		new ConsoleView().open();
	}

	public void selectProjectItem(String... path) {
		getProject().getProjectItem(path).select();
	}

	public void openFile(String... path) {

		ProjectItem item = getProject().getProjectItem(path);
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
		getProject().getProjectItem("src/main/resources", "META-INF", "spring", name).select();
	}

	public void selectFirstCamelContext() {
		getProject().getProjectItem("Camel Contexts").getTreeItem().getItems().get(0).select();
	}

	public void openFirstCamelContext() {
		getProject().getProjectItem("Camel Contexts").getTreeItem().getItems().get(0).doubleClick();
	}

	public void runCamelContext() {

		getProject().getProjectItem("Camel Contexts").getChildren().get(0).select();
		try {
			new ContextMenuItem("Run As", "2 Local Camel Context").select();
		} catch (CoreLayerException ex) {
			new ContextMenuItem("Run As", "1 Local Camel Context").select();
		}
		AbstractWait.sleep(TimePeriod.DEFAULT);
		try {
			new WaitUntil(new ConsoleHasText("started and consuming from"), TimePeriod.getCustom(600));
		} catch(WaitTimeoutExpiredException ex) {
			backupHsErrPidLogs();
			throw ex;
		}
	}

	private void backupHsErrPidLogs() {
		File projectFolder = getFile();
		try {
			Files.walk(projectFolder.toPath()).filter(file -> {
				String filename = file.getFileName().toFile().getName();
				return filename.startsWith("hs_err_pid") && filename.endsWith(".log");
			}).forEach(hsErrPidLog -> {
				try {
					Path grandParent = hsErrPidLog.getParent().getParent();
					Path targetPath = grandParent.resolve(hsErrPidLog.getFileName().toFile().getName());
					Files.move(hsErrPidLog, targetPath);
					System.out.println("Moving "+ hsErrPidLog.toFile().getAbsolutePath() + " to " + targetPath.toFile().getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void runCamelContextWithoutTests(String name) {

		String id = getCamelContextId("src/main/resources", "META-INF", "spring", name);
		getProject().getProjectItem("src/main/resources", "META-INF", "spring", name).select();
		new ContextMenuItem("Run As", "3 Local Camel Context (without tests)").select();
		new WaitUntil(new ConsoleHasText("(CamelContext: " + id + ") started"), TimePeriod.VERY_LONG);
	}

	public void debugCamelContextWithoutTests(String name) {

		new ProjectExplorer().open();
		getProject().getProjectItem("src/main/resources", "META-INF", "spring", name).select();
		new ContextMenuItem("Debug As", "3 Local Camel Context (without tests)").select();
		closeSecureStorage();
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		closePerspectiveSwitchWindow();
	}

	/**
	 * Tries to close 'Secure Storage' dialog window
	 */
	private static void closeSecureStorage() {
		try {
			new WaitUntil(new ShellIsAvailable(new WithTextMatcher(new RegexMatcher("Secure Storage.*"))),
					TimePeriod.DEFAULT);
		} catch (RuntimeException ex1) {
			return;
		}
		new DefaultShell(new WithTextMatcher(new RegexMatcher("Secure Storage.*")));
		new LabeledText("Password:").setText("admin");
		new OkButton().click();
		AbstractWait.sleep(TimePeriod.SHORT);
	}

	public void enableCamelNature() {

		getProject().select();
		try {
			new ContextMenuItem("Enable Fuse Camel Nature").select();
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
			if (new ShellIsAvailable("Confirm Perspective Switch").test()) {
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

		return new File(new File(root.getLocationURI().getPath()), getProject().getName());
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
		getProject().select();
		new ContextMenuItem("Maven", "Update Project...").select();
		new DefaultShell("Update Maven Project");
		new CheckBox("Force Update of Snapshots/Releases").toggle(true);
		new PushButton("OK").click();

		AbstractWait.sleep(TimePeriod.DEFAULT);
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

	public Project getProject() {
		return new ProjectExplorer().getProject(name);
	}

	public File getPomFile() {
		return new File(getFile(), "pom.xml");
	}

	public String getPomContent() throws IOException {
		return new String(Files.readAllBytes(getPomFile().toPath()));
	}

	public void setPomContent(String content) throws IOException {
		Files.write(getPomFile().toPath(), content.getBytes());
	}

	public List<MavenDependency> getMavenDependencies() throws FileNotFoundException, ParserConfigurationException,
			SAXException, IOException, XPathExpressionException {
		List<MavenDependency> deps = new ArrayList<>();
		XPathEvaluator xpath = new XPathEvaluator(new FileInputStream(getPomFile()), false);
		int numOfDeps = Integer.valueOf(xpath.evaluateXPath("count(/project/dependencies/dependency)"));
		for (int i = 1; i <= numOfDeps; i++) {
			String groupId = xpath.evaluateXPath("/project/dependencies/dependency[" + i + "]/groupId");
			String artifactId = xpath.evaluateXPath("/project/dependencies/dependency[" + i + "]/artifactId");
			String version = xpath.evaluateXPath("/project/dependencies/dependency[" + i + "]/version");
			deps.add(new MavenDependency(groupId, artifactId, version));
		}
		return deps;
	}

}
