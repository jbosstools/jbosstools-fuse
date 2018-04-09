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

import java.util.List;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.impl.editor.DefaultEditor;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.view.FusePropertiesView;

/**
 * Manipulates with 'Configurations' tab in Camel Editor
 * 
 * @author djelinek
 */
public class ConfigurationsEditor extends DefaultEditor {

	public static final String CONFIGURATIONS_TAB = "Configurations";
	public static final String ROOT_ELEMENT = "Red Hat Fuse";

	private static Logger log = Logger.getLogger(ConfigurationsEditor.class);

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
		CamelEditor.switchTab(CONFIGURATIONS_TAB);
	}

	public ConfigurationsEditor() {
		log.info("Switching to Configurations Tab");
		CamelEditor.switchTab(CONFIGURATIONS_TAB);
	}

	public ConfigurationsEditor(String name) {
		super(name);
		new DefaultCTabItem(this, CONFIGURATIONS_TAB).activate();
	}

	public FusePropertiesView editEndpoint(String endpointName) {
		selectEndpoint(endpointName);
		edit();
		return new FusePropertiesView();
	}

	public FusePropertiesView editDataFormat(String dataFormatName) {
		selectDataFormat(dataFormatName);
		edit();
		return new FusePropertiesView();
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

	public CamelDataFormatDialog addDataFormat() {
		add().select(Element.DATAFORMAT).ok();
		return new CamelDataFormatDialog();
	}

	public CamelEndpointDialog addEndpoint() {
		add().select(Element.ENDPOINT).ok();
		return new CamelEndpointDialog();
	}

	public void selectBean(String beanName) {
		new DefaultTree(this).getItem(ROOT_ELEMENT, beanName + " (Bean)").select();
	}

	public void selectDataFormat(String dataFormatName) {
		new DefaultTree(this).getItem(ROOT_ELEMENT, dataFormatName + " (Data Format)").select();
	}

	public void selectEndpoint(String endpointName) {
		new DefaultTree(this).getItem(ROOT_ELEMENT, endpointName + " (Endpoint)").select();
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

	public void deleteDataFormat(String dataFormatName) {
		selectDataFormat(dataFormatName);
		delete();
	}

	public void deleteEndpoint(String endpointName) {
		activate();
		selectEndpoint(endpointName);
		delete();
	}

	@Override
	public void close(boolean save) {
		super.close(save);
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
	}

	public static List<String> getDataFormats() {
		CamelDataFormatDialog dataFormatDialog = new ConfigurationsEditor().addDataFormat();
		List<String> items = dataFormatDialog.getDataFormats();
		dataFormatDialog.cancel();
		return items;
	}

	public static List<String> getEndpoints() {
		CamelEndpointDialog endpointDialog = new ConfigurationsEditor().addEndpoint();
		List<String> endpoints = endpointDialog.getEndpoints();
		endpointDialog.cancel();
		return endpoints;
	}

}
