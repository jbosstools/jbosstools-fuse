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
package org.jboss.tools.fuse.reddeer.editor;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.impl.editor.DefaultEditor;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;

/**
 * Manipulates with Configurations Tab in Camel Editor
 * 
 * @author djelinek
 */
public class ConfigurationsEditor extends DefaultEditor {

	public static final String CONFIGURATIONS_TAB = "Configurations";
	public static final String ROOT_ELEMENT = "JBoss Fuse";

	private static Logger log = Logger.getLogger(ConfigurationsEditor.class);
	private static final String TYPE = "JBoss Fuse";

	public enum Element {

		ENDPOINT("Endpoint"), DATAFORMAT("Data Format"), BEAN("Bean");

		private String name;

		private Element(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public ConfigurationsEditor(String project, String title) {

		new CamelProject(project).openCamelContext(title);
		log.info("Switching to Configurations Tab");
		CamelEditor.switchTab("Configurations");
	}

	public ConfigurationsEditor() {

		log.info("Switching to Configurations Tab");
		CamelEditor.switchTab("Configurations");
	}

	public ConfigurationsEditor(String name) {
		super(name);
		new DefaultCTabItem(this, CONFIGURATIONS_TAB).activate();
	}

	public void selectConfig(String... path) {
		activate();
		new DefaultTreeItem(path).select();
	}

	public void addConfig(String... path) {
		activate();
		new PushButton("Add").click();
		new WaitUntil(new ShellIsAvailable("Create new global element..."));
		new DefaultShell("Create new global element...");
		new DefaultTreeItem(path).select();
		new PushButton("OK").click();
	}

	public void editConfig(String... path) {
		selectConfig(path);
		new PushButton("Edit").click();
	}

	public void deleteConfig(String... path) {
		selectConfig(path);
		new PushButton("Delete").click();
	}

	/**
	 * Adds an Endpoint global element into the Camel Editor into Configurations tab<br/>
	 * 
	 * @param title
	 *            Name of a endpoint in Endpoint dialog
	 * 
	 * @param component
	 *            Type of a component in Endpoint dialog
	 */
	public void createNewGlobalEndpoint(String title, String component) {

		log.debug("Trying to create new Global Endpoint, with title - " + title + " and component is - " + component);
		activate();
		new PushButton("Add").click();
		new WaitUntil(new ShellIsAvailable("Create new global element..."));
		new DefaultShell("Create new global element...");
		new DefaultTreeItem(new String[] { TYPE, "Endpoint" }).select();
		new PushButton("OK").click();
		CamelEndpointDialog endpointDialog = new CamelEndpointDialog();
		endpointDialog.activate();
		endpointDialog.setId(title);
		endpointDialog.chooseCamelComponent(component);
		endpointDialog.finish();
		save();
	}

	/**
	 * Adds a Data Format global element into the Camel Editor into Configurations tab<br/>
	 * 
	 * @param title
	 *            Name of a data format in Data Format dialog
	 * 
	 * @param format
	 *            Type of a component in Data Format dialog
	 */
	public void createNewGlobalDataFormat(String title, String format) {

		log.debug("Trying to create new Global Data Format with title - " + title + " and Data Format is - " + format);
		activate();
		new PushButton("Add").click();
		new WaitUntil(new ShellIsAvailable("Create new global element..."));
		new DefaultShell("Create new global element...");
		new DefaultTreeItem(new String[] { TYPE, "Data Format" }).select();
		new PushButton("OK").click();
		CamelDataFormatDialog formatDialog = new CamelDataFormatDialog();
		formatDialog.activate();
		formatDialog.setIdText(title);
		formatDialog.chooseDataFormat(format);
		formatDialog.finish();
		save();
	}

	/**
	 * Method for edit an Endpoint global element in the Camel Editor in Configurations tab<br/>
	 * 
	 * @param title
	 *            Name of a endpoint that will be choose for edit
	 */
	public void editGlobalEndpoint(String title) {

		log.debug("Trying to edit Global Element - Endpoint");
		activate();
		new DefaultTreeItem(new String[] { TYPE, title + " (Endpoint)" }).select();
		new PushButton("Edit").click();
	}

	/**
	 * Method for edit a Data Format global element in the Camel Editor in Configurations tab<br/>
	 * 
	 * @param title
	 *            Name of data format that will be choose for edit
	 */
	public void editGlobalDataFormat(String title) {

		log.debug("Trying to edit Global Element - Data Format");
		activate();
		new DefaultTreeItem(new String[] { TYPE, title + " (Data Format)" }).select();
		new PushButton("Edit").click();
	}

	/**
	 * Method for edit an Endpoint global element in the Camel Editor in Configurations tab<br/>
	 * 
	 * @param element
	 *            Type of global element, Endpoint or Data Format
	 * 
	 * @param title
	 *            Name of a element that will be choose for delete
	 */
	public void deleteGlobalElement(Element element, String title) {

		log.debug("Trying to delete Global Element - " + element + ", with title - " + title);
		activate();
		title += "(" + element.getName() + ")";
		try {
			new DefaultTreeItem(new String[] { TYPE, title }).select();
			new PushButton("Delete").click();
			save();
		} catch (Exception e) {
			log.debug("Component with title - " + title + " doesn't exist", e);
		}
	}

	public ConfigurationsDialog add() {
		activate();
		new PushButton(this, "Add").click();
		return new ConfigurationsDialog();
	}

	public void edit() {
		new PushButton(this, "Edit").click();
	}

	public void delete() {
		new PushButton(this, "Delete").click();
	}

	public AddBeanWizard addBean() {
		add().select(Element.BEAN).ok();
		return new AddBeanWizard();
	}

	public void selectBean(String beanName) {
		new DefaultTree(this).getItem(ROOT_ELEMENT, beanName + " (Bean)").select();
	}

	public EditBeanWizard editBean(String beanName) {
		selectBean(beanName);
		edit();
		return new EditBeanWizard();
	}

	public void deleteBean(String beanName) {
		selectBean(beanName);
		delete();
	}

	@Override
	public void close(boolean save) {
		super.close(save);
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
	}

}
