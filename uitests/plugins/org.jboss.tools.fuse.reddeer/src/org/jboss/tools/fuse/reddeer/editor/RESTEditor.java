/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.reddeer.core.handler.LabelHandler;
import org.eclipse.reddeer.core.lookup.WidgetLookup;
import org.eclipse.reddeer.swt.api.Combo;
import org.eclipse.reddeer.swt.api.Text;
import org.eclipse.reddeer.swt.impl.combo.LabeledCombo;
import org.eclipse.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.eclipse.reddeer.swt.impl.list.DefaultList;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.uiforms.api.Section;
import org.eclipse.reddeer.uiforms.impl.section.DefaultSection;
import org.eclipse.reddeer.workbench.impl.editor.DefaultEditor;
import org.eclipse.swt.widgets.Label;

/**
 * This class is intended for manipulations with REST editor.
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 *
 */
public class RESTEditor extends DefaultEditor {

	public static final String REST_EDITOR_TAB = "REST";
	public static final String REST_CONFIGURATION_SECTION = "REST Configuration";
	public static final String REST_CONFIGURATION_COMPONENT = "Component:";
	public static final String REST_CONFIGURATION_PORT = "Port:";
	public static final String REST_CONFIGURATION_BINDING_MODE = "Binding Mode:";
	public static final String REST_ELEMENTS_SECTION = "REST Elements";
	public static final String REST_OPERATIONS_SECTION = "REST Operations";

	public RESTEditor(String title) {
		super(title);
		new DefaultCTabItem(REST_EDITOR_TAB).activate();
	}

	public String getRestConfigurationComponent() {
		return getRestConfigurationCMB(REST_CONFIGURATION_COMPONENT).getText();
	}

	public String getRestConfigurationPort() {
		return getRestConfigurationTXT(REST_CONFIGURATION_PORT).getText();
	}

	public String getRestConfigurationBindingMode() {
		return getRestConfigurationCMB(REST_CONFIGURATION_BINDING_MODE).getText();
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
