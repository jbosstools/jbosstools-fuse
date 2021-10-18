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
package org.jboss.tools.fuse.reddeer.utils;

import static org.jboss.tools.fuse.reddeer.ProjectTemplate.SPRINGBOOT;
import static org.jboss.tools.fuse.reddeer.ProjectType.BLUEPRINT;
import static org.jboss.tools.fuse.reddeer.ProjectType.JAVA;
import static org.jboss.tools.fuse.reddeer.ProjectType.SPRING;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.OPENSHIFT;
import static org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType.STANDALONE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.direct.preferences.PreferencesUtil;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.wizards.datatransfer.ExternalProjectImportWizardDialog;
import org.eclipse.reddeer.eclipse.ui.wizards.datatransfer.WizardProjectsImportPage;
import org.eclipse.reddeer.eclipse.utils.DeleteUtils;
import org.eclipse.reddeer.swt.api.Shell;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.button.FinishButton;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.ProjectType;
import org.jboss.tools.fuse.reddeer.SupportedCamelVersions;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizard;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardAdvancedPage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardDeploymentType;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardFirstPage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimePage;
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizardRuntimeType;
import org.osgi.framework.Version;

/**
 * Can create new Fuse projects or import existing
 * 
 * @author tsedmik
 */
public class ProjectFactory {

	private String name;
	private String version;
	private NewFuseIntegrationProjectWizardRuntimeType runtimeType;
	private NewFuseIntegrationProjectWizardDeploymentType deploymentType;
	private String[] template;

	private ProjectFactory(String name) {
		this.name = name;
	}

	public ProjectFactory version(String version) {
		this.version = version;
		return this;
	}

	public ProjectFactory template(String[] template) {
		this.template = template;
		return this;
	}

	public ProjectFactory runtimeType(NewFuseIntegrationProjectWizardRuntimeType runtimeType) {
		this.runtimeType = runtimeType;
		return this;
	}

	public ProjectFactory deploymentType(NewFuseIntegrationProjectWizardDeploymentType deploymentType) {
		this.deploymentType = deploymentType;
		return this;
	}

	public void create() {
		PreferencesUtil.setOpenAssociatedPerspective("never");
		boolean hasJava8 = JDK8Check.isJava8Available();
		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		NewFuseIntegrationProjectWizardFirstPage firstPage = new NewFuseIntegrationProjectWizardFirstPage(wiz);
		firstPage.setProjectName(name);
		wiz.next();
		NewFuseIntegrationProjectWizardRuntimePage secondPage = new NewFuseIntegrationProjectWizardRuntimePage(wiz);
		String camelVersion = SupportedCamelVersions.getCamelVersionsWithLabels().get(version);
		if (camelVersion != null) {
			secondPage.selectCamelVersion(camelVersion);
		} else {
			secondPage.typeCamelVersion(version);
		}
		if (deploymentType != null) {
			secondPage.setDeploymentType(deploymentType);
		}
		if (runtimeType != null && deploymentType == STANDALONE) {
			secondPage.setRuntimeType(runtimeType);
		}
		wiz.next();
		NewFuseIntegrationProjectWizardAdvancedPage lastPage = new NewFuseIntegrationProjectWizardAdvancedPage(wiz);
		if (template != null) {
			lastPage.selectTemplate(template);
		} else {
			lastPage.selectTemplate(SPRINGBOOT);
		}
		new FinishButton(wiz).click();

		if (!hasJava8 && (new Version(getFuseVersionFromString(camelVersion)).compareTo(new Version("7.9.0")) < 0)) {
			JDK8Check.handleMissingJava8();
		}

		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		new WaitWhile(new ShellIsAvailable("New Fuse Integration Project"), TimePeriod.getCustom(900));
	}

	public static ProjectFactory newProject(String name) {
		return new ProjectFactory(name);
	}

	/**
	 * Removes all projects from file system.
	 */
	public static void deleteAllProjects() {

		ProjectExplorer explorer = new ProjectExplorer();
		explorer.activate();
		if (explorer.getProjects().size() > 0) {
			explorer.selectAllProjects();
			new ContextMenuItem("Refresh").select();
			new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
			AbstractWait.sleep(TimePeriod.SHORT);
			new WorkbenchShell();
			explorer.activate();
			explorer.selectAllProjects();
			new ContextMenuItem("Delete").select();
			Shell s = new DefaultShell("Delete Resources");
			new CheckBox().toggle(true);
			new PushButton("OK").click();
			DeleteUtils.handleDeletion(s, TimePeriod.LONG);
		}
	}

	/**
	 * Import existing project into workspace
	 * 
	 * @param path
	 *                  Path to the project
	 * @param name
	 *                  Name of the project
	 * @param maven
	 *                  true - if the imported project is Maven project
	 * @param fuse
	 *                  true - if the imported project is Fuse project
	 */
	public static void importExistingProject(String path, String name, boolean maven) {

		ExternalProjectImportWizardDialog dialog = new ExternalProjectImportWizardDialog();
		dialog.open();
		WizardProjectsImportPage page = new WizardProjectsImportPage(dialog);
		page.copyProjectsIntoWorkspace(true);
		page.setRootDirectory(path);
		page.selectProjects(name);
		dialog.finish(TimePeriod.VERY_LONG);

		if (maven) {
			new ProjectExplorer().selectProjects(name);
			new ContextMenuItem("Configure", "Convert to Maven Project").select();
			new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
		}
	}

	/**
	 * Returns all available Camel versions in New Fuse Integration Project Wizard
	 * 
	 * @return all available Camel versions
	 */
	public static List<String> getAllAvailableCamelVersions() {
		List<String> versions;
		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		NewFuseIntegrationProjectWizardFirstPage firstPage = new NewFuseIntegrationProjectWizardFirstPage(wiz);
		firstPage.setProjectName("rfhaSS234");
		wiz.next();
		NewFuseIntegrationProjectWizardRuntimePage secondPage = new NewFuseIntegrationProjectWizardRuntimePage(wiz);
		versions = secondPage.getAllAvailableCamelVersions();
		wiz.cancel();
		return versions;
	}

	public static Collection<FuseProjectDefinition> getAllAvailableTemplates() {
		List<FuseProjectDefinition> templates = new ArrayList<>();
		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		NewFuseIntegrationProjectWizardFirstPage firstPage = new NewFuseIntegrationProjectWizardFirstPage(wiz);
		firstPage.setProjectName("rfhaSS234ss");
		wiz.next();
		NewFuseIntegrationProjectWizardRuntimePage secondPage = new NewFuseIntegrationProjectWizardRuntimePage(wiz);

		// for all deployment types
		for (NewFuseIntegrationProjectWizardDeploymentType deploymentType : NewFuseIntegrationProjectWizardDeploymentType
				.values()) {
			secondPage.setDeploymentType(deploymentType);

			// for all different runtime types
			for (NewFuseIntegrationProjectWizardRuntimeType runtimeType : NewFuseIntegrationProjectWizardRuntimeType
					.values()) {

				// skip Openshift/Karaf and OpenShift/EAP combinations
				if (deploymentType == OPENSHIFT
						&& !(runtimeType == NewFuseIntegrationProjectWizardRuntimeType.SPRINGBOOT)) {
					continue;
				}
				secondPage.setRuntimeType(runtimeType);

				// for all available Camel versions
				for (String camelVersion : secondPage.getAllAvailableCamelVersions()) {
					secondPage.typeCamelVersion(camelVersion);
					wiz.next();
					NewFuseIntegrationProjectWizardAdvancedPage lastPage = new NewFuseIntegrationProjectWizardAdvancedPage(
							wiz);

					// add all available templates
					for (String[] path : lastPage.getAllAvailableTemplates()) {

						// determine project type
						ProjectType projectType;
						if (path[path.length - 1].toLowerCase().contains("blueprint")) {
							projectType = BLUEPRINT;
						} else if (path[path.length - 1].toLowerCase().contains("spring")) {
							projectType = SPRING;
						} else {
							projectType = JAVA;
						}

						FuseProjectDefinition project = new FuseProjectDefinition(runtimeType, deploymentType, path,
								camelVersion, projectType);
						templates.add(project);
					}

					wiz.back();
				}
			}
		}
		wiz.cancel();
		return templates;
	}

	private static String getFuseVersionFromString(String camelVersion) {

		String fuseVersion = null;
		Pattern p = Pattern.compile("(?<=fuse-)\\d+");
		Matcher m = p.matcher(camelVersion);
		m.find();

		if (m.group(0).length() == 6) {
			fuseVersion = m.group(0).substring(0, 1) + "." + m.group(0).substring(1, 2) + "."
					+ m.group(0).substring(2, 3);
		} else if (m.group(0).length() == 7) { // handle f.e. 7.10.0
			fuseVersion = m.group(0).substring(0, 1) + "." + m.group(0).substring(1, 3) + "."
					+ m.group(0).substring(3, 4);
		}

		return fuseVersion;
	}
}
