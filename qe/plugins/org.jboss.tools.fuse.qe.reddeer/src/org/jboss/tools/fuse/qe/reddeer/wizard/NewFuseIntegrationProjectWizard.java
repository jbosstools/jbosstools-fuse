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
package org.jboss.tools.fuse.qe.reddeer.wizard;

import java.util.ArrayList;
import java.util.List;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.jface.wizard.NewWizardDialog;
import org.jboss.reddeer.swt.api.TreeItem;
import org.jboss.reddeer.swt.impl.button.CheckBox;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.button.RadioButton;
import org.jboss.reddeer.swt.impl.combo.DefaultCombo;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.swt.impl.tree.DefaultTree;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.tools.fuse.qe.reddeer.ProjectType;

/**
 * @author tsedmik
 */
public class NewFuseIntegrationProjectWizard extends NewWizardDialog {

	private static Logger log = Logger.getLogger(NewFuseIntegrationProjectWizard.class);

	public NewFuseIntegrationProjectWizard() {
		super("JBoss Fuse", "Fuse Integration Project");
	}

	public void setProjectName(String name) {
		log.debug("Setting Project name to: " + name);
		new LabeledText("Project Name").setText(name);
	}

	public void setLocation(String path) {
		log.debug("Setting project Path to: " + path);
		new LabeledText("Path").setText(path);
	}

	public void useDefaultLocation(boolean choice) {
		log.debug("Setting 'Use default Workspace location' to: " + choice);
		new CheckBox("Use default Workspace location").toggle(choice);
	}

	public boolean isPathEditable() {
		return new LabeledText("Path").isEnabled();
	}

	public void selectTargetRuntime(String runtime) {
		log.debug("Setting 'Target Runtime' to: " + runtime);
		new DefaultCombo(0).setSelection(runtime);
	}

	public ServerRuntimeWizard newTargetRuntime() {
		log.debug("Invoking new server runtime wizard");
		new PushButton("New").click();
		new WaitUntil(new ShellWithTextIsAvailable("New Server Runtime Environment"));
		new DefaultShell("New Server Runtime Environment");
		return new ServerRuntimeWizard();
	}

	public List<String> getTargetRuntimes() {
		log.debug("Getting all available target runtimes");
		return new DefaultCombo(0).getItems();
	}

	public String getTargetRuntime() {
		log.debug("Getting selected Target Runtime");
		return new DefaultCombo(0).getSelection();
	}

	public List<String> getCamelVersions() {
		log.debug("Getting all available camel versions");
		return new DefaultCombo(1).getItems();
	}

	public String getCamelVersion() {
		log.debug("Getting selected Camel version");
		return new DefaultCombo(1).getText();
	}

	public void selectCamelVersion(String version) {
		log.debug("Selecting 'Camel Version' to: " + version);
		new DefaultCombo(1).setText(version);
	}

	public boolean isCamelVersionEditable() {
		return new DefaultCombo(1).isEnabled();
	}

	public void setCamelVersion(String version) {
		log.debug("Setting 'Camel Version' to: " + version);
		new DefaultCombo(1).setText(version);
	}

	public void setProjectType(ProjectType type) {
		switch (type) {
		case JAVA:
			log.debug("Setting project type to: Java DSL");
			new RadioButton("Java DSL").toggle(true);
			break;
		case SPRING:
			log.debug("Setting project type to: Spring DSL");
			new RadioButton("Spring DSL").toggle(true);
			break;
		case BLUEPRINT:
			log.debug("Setting project type to: Blueprint DSL");
			new RadioButton("Blueprint DSL").toggle(true);
			break;
		}
	}

	public boolean isProjectTypeAvailable(ProjectType type) {
		switch (type) {
		case JAVA:
			log.debug("Trying to determine whether 'Java DSL' project type is available");
			return new RadioButton(type.getDescription()).isEnabled();
		case SPRING:
			log.debug("Trying to determine whether 'Spring DSL' project type is available");
			return new RadioButton(type.getDescription()).isEnabled();
		case BLUEPRINT:
			log.debug("Trying to determine whether 'Blueprint DSL' project type is available");
			return new RadioButton(type.getDescription()).isEnabled();
		default:
			return true;
		}
	}

	public boolean isProjectTypeSelected(ProjectType type) {
		switch (type) {
		case JAVA:
			log.debug("Trying to determine whether 'Java DSL' project type is selected");
			return new RadioButton(type.getDescription()).isSelected();
		case SPRING:
			log.debug("Trying to determine whether 'Spring DSL' project type is selected");
			return new RadioButton(type.getDescription()).isSelected();
		case BLUEPRINT:
			log.debug("Trying to determine whether 'Blueprint DSL' project type is selected");
			return new RadioButton(type.getDescription()).isSelected();
		default:
			return true;
		}
	}

	public void startWithEmptyProject() {
		new RadioButton("Start with an empty project").toggle(true);
	}

	public void selectTemplate(String name) {
		log.debug("Selecting a predefined template: " + name);
		new RadioButton("Use a predefined template").toggle(true);
		for (TreeItem item : new DefaultTree().getAllItems()) {
			if (item.getText().equals(name)) {
				item.select();
				return;
			}
		}
		throw new NewFuseIntegrationProjectWizardException("Given template is not available: " + name);
	}

	public void selectTemplate(String... path) {
		log.debug("Selecting a predefined template: " + path);
		new RadioButton("Use a predefined template").toggle(true);
		try {
			new DefaultTreeItem(path).select();
		} catch (Exception e) {
			throw new NewFuseIntegrationProjectWizardException("Given template is not available: " + path);
		}
		
	}

	public List<String> getAllAvailableTemplates() {
		log.debug("Retrieving available templates");
		List<String> templates = new ArrayList<String>();
		new RadioButton("Use a predefined template").toggle(true);
		for (TreeItem item : new DefaultTree().getAllItems()) {
			if (item.getItems().isEmpty()) {
				templates.add(item.getText());
			}
		}
		return templates;
	}
}
