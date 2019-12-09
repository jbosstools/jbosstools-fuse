/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.reddeer.common.util.Display;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.core.handler.LabelHandler;
import org.eclipse.reddeer.core.lookup.WidgetLookup;
import org.eclipse.reddeer.swt.api.Combo;
import org.eclipse.reddeer.swt.api.Text;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.eclipse.reddeer.swt.impl.list.DefaultList;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.eclipse.reddeer.uiforms.api.Section;
import org.eclipse.reddeer.uiforms.impl.section.DefaultSection;
import org.eclipse.reddeer.workbench.impl.editor.DefaultEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.jboss.tools.fuse.reddeer.wizard.AddRESTOperationWizard;

/**
 * This class is intended for manipulations with REST editor.
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 * @author djelinek
 *
 */
public class RESTEditor extends DefaultEditor {

	public static final String REST_EDITOR_TAB = "REST";
	public static final String REST_CONFIGURATION_SECTION = "REST Configuration";
	public static final String REST_CONFIGURATION_COMPONENT = "Component:";
	public static final String REST_CONFIGURATION_CONTEXT_PATH = "Context Path:";
	public static final String REST_CONFIGURATION_PORT = "Port:";
	public static final String REST_CONFIGURATION_BINDING_MODE = "Binding Mode:";
	public static final String REST_CONFIGURATION_HOST = "Host:";
	public static final String REST_ELEMENTS_SECTION = "REST Elements";
	public static final String REST_OPERATIONS_SECTION = "REST Operations";
	public static final String REST_CONFIGURATION_ADD = "Add REST Configuration";
	public static final String REST_CONFIGURATION_DELETE = "Delete REST Configuration";
	public static final String REST_ELEMENT_ADD = "Add REST Element";
	public static final String REST_ELEMENT_DELETE = "Delete REST Element";
	public static final String REST_OPERATION_ADD = "Add REST Operation";
	public static final String REST_OPERATION_DELETE = "Delete REST Operation";

	public RESTEditor(String title) {
		super(title);
		new DefaultCTabItem(REST_EDITOR_TAB).activate();
	}

	public String getRestConfigurationComponent() {
		return getRestConfigurationCMB(REST_CONFIGURATION_COMPONENT).getText();
	}

	public String getRestConfigurationContextPath() {
		return getRestConfigurationTXT(REST_CONFIGURATION_CONTEXT_PATH).getText();
	}

	public String getRestConfigurationPort() {
		return getRestConfigurationTXT(REST_CONFIGURATION_PORT).getText();
	}

	public String getRestConfigurationBindingMode() {
		return getRestConfigurationCMB(REST_CONFIGURATION_BINDING_MODE).getText();
	}

	public String getRestConfigurationHost() {
		return getRestConfigurationTXT(REST_CONFIGURATION_HOST).getText();
	}

	public void setRestConfigurationComponent(String component) {
		getRestConfigurationCMB(REST_CONFIGURATION_COMPONENT).setSelection(component);
	}

	public void setRestConfigurationContextPath(String path) {
		getRestConfigurationTXT(REST_CONFIGURATION_CONTEXT_PATH).setText(path);
	}

	public void setRestConfigurationPort(String port) {
		getRestConfigurationTXT(REST_CONFIGURATION_PORT).setText(port);
	}

	public void setRestConfigurationBindingMode(String mode) {
		getRestConfigurationCMB(REST_CONFIGURATION_BINDING_MODE).setSelection(mode);
	}

	public void setRestConfigurationHost(String host) {
		getRestConfigurationTXT(REST_CONFIGURATION_HOST).setText(host);
	}

	protected Text getRestConfigurationTXT(String label) {
		return new LabeledText(new DefaultSection(this, REST_CONFIGURATION_SECTION), label);
	}

	protected Combo getRestConfigurationCMB(String label) {
		return new LabeledCombo(new DefaultSection(this, REST_CONFIGURATION_SECTION), label);
	}

	public List<String> getRestElements() {
		return Arrays.asList(new DefaultList(new DefaultSection(this, REST_ELEMENTS_SECTION)).getListItems());
	}

	public void selectRestElement(String element) {
		new DefaultList(new DefaultSection(this, REST_ELEMENTS_SECTION)).select(element);
	}

	public void selectRestOperation(String operation) {
		DefaultSection section = new DefaultSection(this, REST_OPERATIONS_SECTION);
		Display.syncExec(new Runnable() {

			@Override
			public void run() {
				ListIterator<Label> it = WidgetLookup.getInstance().activeWidgets(section, Label.class).listIterator();
				// ignore the first two labels
				it.next();
				it.next();
				// now iterate over operation labels
				Composite item = null;
				while (it.hasNext()) {
					String method = LabelHandler.getInstance().getText(it.next());
					String path = LabelHandler.getInstance().getText(it.next());
					if (operation.equals(method + " " + path)) {
						item = it.previous().getParent();
						break;
					}
				}
				if (item != null) {
					item.setFocus();
					item.notifyListeners(SWT.MouseDown, new Event());
				}
			}
		});
	}

	public void addRestConfiguration() {
		activate();
		new DefaultToolItem(new DefaultSection(this, REST_CONFIGURATION_SECTION), REST_CONFIGURATION_ADD).click();
	}

	public void deleteRestConfiguration() {
		activate();
		new DefaultToolItem(new DefaultSection(this, REST_CONFIGURATION_SECTION), REST_CONFIGURATION_DELETE).click();
		new WaitUntil(new ShellIsAvailable("Delete All REST Configuration Elements from Camel File"),
				TimePeriod.DEFAULT);
		new PushButton("Yes").click();
	}

	public void addRestElement() {
		activate();
		new DefaultToolItem(new DefaultSection(this, REST_ELEMENTS_SECTION), REST_ELEMENT_ADD).click();
	}

	public void deleteRestElement(String element) {
		activate();
		selectRestElement(element);
		new DefaultToolItem(new DefaultSection(this, REST_ELEMENTS_SECTION), REST_ELEMENT_DELETE).click();
	}

	public AddRESTOperationWizard addRestOperation() {
		activate();
		new DefaultToolItem(new DefaultSection(this, REST_OPERATIONS_SECTION), REST_OPERATION_ADD).click();
		return new AddRESTOperationWizard();
	}

	public void deleteRestOperation(String operation) {
		activate();
		selectRestOperation(operation);
		new DefaultToolItem(new DefaultSection(this, REST_OPERATIONS_SECTION), REST_OPERATION_DELETE).click();
	}

	public List<String> getRestOperations() {
		Section section = new DefaultSection(this, REST_OPERATIONS_SECTION);
		ListIterator<Label> it = WidgetLookup.getInstance().activeWidgets(section, Label.class).listIterator();
		// ignore the first two labels
		it.next();
		it.next();
		// now iterate over operation labels
		List<String> operations = new ArrayList<>();
		while (it.hasNext()) {
			String method = LabelHandler.getInstance().getText(it.next());
			String path = LabelHandler.getInstance().getText(it.next());
			operations.add(method + " " + path);
		}
		return operations;
	}

}
