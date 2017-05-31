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
package org.jboss.tools.fuse.qe.reddeer.editor;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.workbench.impl.editor.DefaultEditor;
import org.jboss.tools.fuse.qe.reddeer.projectexplorer.CamelProject;

/**
 * Manipulates with Configurations Tab in Camel Editor
 * 
 * @author djelinek
 */
public class ConfigurationsEditor extends DefaultEditor {

	private static Logger log = Logger.getLogger(ConfigurationsEditor.class);
	private static final String TYPE = "JBoss Fuse";

	public enum Element {
		ENDPOINT, DATAFORMAT
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

	public void selectConfig(String... path) {
		activate();
		new DefaultTreeItem(path).select();
	}

	public void addConfig(String... path) {
		activate();
		new PushButton("Add").click();
		new WaitUntil(new ShellWithTextIsAvailable("Create new global element..."));
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
		new WaitUntil(new ShellWithTextIsAvailable("Create new global element..."));
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
		new WaitUntil(new ShellWithTextIsAvailable("Create new global element..."));
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
		switch (element) {
		case ENDPOINT:
			title += " (Endpoint)";
			break;
		case DATAFORMAT:
			title += " (Data Format)";
			break;
		default:
			break;
		}
		try {
			new DefaultTreeItem(new String[] { TYPE, title }).select();
			new PushButton("Delete").click();
			save();
		} catch (Exception e) {
			log.debug("Component with title - " + title + " isn't exist", e);
		}
	}

}
