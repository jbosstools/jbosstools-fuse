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
package org.jboss.tools.fuse.ui.bot.tests.utils;

import static org.jboss.tools.fuse.reddeer.ProjectType.BLUEPRINT;
import static org.jboss.tools.fuse.reddeer.ProjectType.JAVA;
import static org.jboss.tools.fuse.reddeer.ProjectType.SPRING;

import java.util.ArrayList;
import java.util.List;

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
import org.jboss.tools.fuse.reddeer.wizard.NewFuseIntegrationProjectWizard;

/**
 * Can create new Fuse projects or import existing
 * 
 * @author tsedmik
 */
public class ProjectFactory {

	private String name;
	private String version;
	private String template;
	private List<String> templatePath;
	private ProjectType type;

	private ProjectFactory(String name) {
		this.name = name;
	}

	public ProjectFactory version(String version) {
		this.version = version;
		return this;
	}
	
	/**
	 * @param template
	 * @return You should use a full path to the Template for performance reason.
	 */
	@Deprecated
	public ProjectFactory template(String template) {
		this.template = template;
		return this;
	}
	
	public ProjectFactory template(List<String> templatePath) {
		this.templatePath = templatePath;
		return this;
	}

	public ProjectFactory type(ProjectType type) {
		this.type = type;
		return this;
	}

	public void create() {
		PreferencesUtil.setOpenAssociatedPerspective("never");
		NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
		wiz.open();
		wiz.setProjectName(name);
		wiz.next();
		if (version != null) {
			wiz.selectCamelVersion(version);
		}
		wiz.next();
		if(templatePath != null) {
			wiz.selectTemplate(templatePath.toArray(new String[templatePath.size()]));
		} else if (template != null) {
			wiz.selectTemplate(template);
		}
		wiz.setProjectType(type);
		new FinishButton().click();
		new WaitWhile(new JobIsRunning(), TimePeriod.getCustom(900));
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
	 *            Path to the project
	 * @param name
	 *            Name of the project
	 * @param maven
	 *            true - if the imported project is Maven project
	 * @param fuse
	 *            true - if the imported project is Fuse project
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
	 * Returns a list of all available templates in the following format:<br/>
	 * {name}:{DSL}:{version}
	 * 
	 * @return all available templates
	 */
	public static List<FuseProjectDefinition> getAllAvailableTemplates() {
		List<String> versions = getAllAvailableCamelVersions();
		List<FuseProjectDefinition> templates = new ArrayList<>();
		for (String version : versions) {
			templates.add(new FuseProjectDefinition("empty", BLUEPRINT, version));
			templates.add(new FuseProjectDefinition("empty", SPRING, version));
			templates.add(new FuseProjectDefinition("empty", JAVA, version));
			NewFuseIntegrationProjectWizard wiz = new NewFuseIntegrationProjectWizard();
			wiz.open();
			wiz.setProjectName("45frHHallkIIo");
			wiz.next();
			wiz.selectCamelVersion(version);
			wiz.next();
			List<String> temp = wiz.getAllAvailableTemplates();
			for (String template : temp) {
				wiz.selectTemplate(template);
				if (wiz.isProjectTypeAvailable(BLUEPRINT)) {
					templates.add(new FuseProjectDefinition(template, BLUEPRINT, version));
				}
				if (wiz.isProjectTypeAvailable(SPRING)) {
					templates.add(new FuseProjectDefinition(template, SPRING, version));
				}
				if (wiz.isProjectTypeAvailable(JAVA)) {
					templates.add(new FuseProjectDefinition(template, JAVA, version));
				}
			}
			wiz.cancel();
		}
		return templates;
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
		wiz.setProjectName("rfhaSS234");
		wiz.next();
		versions = wiz.getCamelVersions();
		wiz.cancel();
		return versions;
	}
}
