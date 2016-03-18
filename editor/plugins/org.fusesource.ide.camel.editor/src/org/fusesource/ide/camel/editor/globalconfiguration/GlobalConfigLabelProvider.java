/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.globalconfiguration;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.camel.editor.dialogs.GlobalConfigCategoryItem;
import org.fusesource.ide.camel.editor.dialogs.GlobalConfigElementItem;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class GlobalConfigLabelProvider extends StyledCellLabelProvider {

	private final CamelGlobalConfigEditor camelGlobalConfigEditor;

	GlobalConfigLabelProvider(CamelGlobalConfigEditor camelGlobalConfigEditor) {
		this.camelGlobalConfigEditor = camelGlobalConfigEditor;
	}

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		StyledString text = new StyledString();
		if (element instanceof String) {
			handleString(cell, (String) element, text);
		} else if (element instanceof Element) {
			handleXMLElement(cell, (Element) element, text);
		} else if (element instanceof AbstractCamelModelElement) {
			handleCamelModelElement(cell, (AbstractCamelModelElement) element, text);
		} else {
			// unhandled
		}
		super.update(cell);
	}

	/**
	 * @param cell
	 * @param element
	 * @param text
	 */
	private void handleCamelModelElement(ViewerCell cell, AbstractCamelModelElement cme, StyledString text) {
		Image img = getIconForElement(cme);
		String type = Strings.capitalize(cme.getTranslatedNodeName());
		for (GlobalConfigElementItem item : camelGlobalConfigEditor.getElementContributions()) {
			if (item.getContributor().canHandle(cme.getXmlNode())) {
				type = item.getName();
				img = item.getIcon();
				break;
			}
		}
		text.append(cme.getId());
		cell.setImage(img);
		text.append(" (" + type + ")", StyledString.COUNTER_STYLER);
		cell.setText(text.toString());
		cell.setStyleRanges(text.getStyleRanges());
	}

	/**
	 * @param cell
	 * @param element
	 * @param text
	 */
	private void handleXMLElement(ViewerCell cell, Element node, StyledString text) {
		Image img = getIconForElement(node);
		String type = Strings.capitalize(CamelUtils.getTranslatedNodeName(node));
		for (GlobalConfigElementItem item : camelGlobalConfigEditor.getElementContributions()) {
			if (item.getContributor().canHandle(node)) {
				type = item.getName();
				img = item.getIcon();
				break;
			}
		}
		text.append(!Strings.isEmpty(node.getAttribute("id")) ? node.getAttribute("id") : CamelUtils.getTranslatedNodeName(node));
		cell.setImage(img);
		if (!Strings.isEmpty(node.getAttribute("id"))) text.append(" (" + type + ") ", StyledString.COUNTER_STYLER);
		cell.setText(text.toString());
		cell.setStyleRanges(text.getStyleRanges());
	}

	/**
	 * @param cell
	 * @param element
	 * @param text
	 */
	private void handleString(ViewerCell cell, String element, StyledString text) {
		GlobalConfigCategoryItem cat = camelGlobalConfigEditor.getCategoryForId(element);
		Image img = cat.getIcon();
		text.append(cat.getName());
		cell.setImage(img);
		cell.setText(text.toString());
		cell.setStyleRanges(text.getStyleRanges());
	}
	
	private Image getIconForElement(Object element) {
		if (element instanceof Node) {
			return CamelEditorUIActivator.getDefault().getImage("beandef.gif");
		} else if (element instanceof AbstractCamelModelElement) {
			AbstractCamelModelElement cme = (AbstractCamelModelElement)element;
			if (cme.getTranslatedNodeName().equalsIgnoreCase("endpoint")) {
				return CamelEditorUIActivator.getDefault().getImage("endpointdef.png");	
			} else if (CamelUtils.getTranslatedNodeName(cme.getXmlNode().getParentNode()).equalsIgnoreCase("dataFormats")) {
				return CamelEditorUIActivator.getDefault().getImage("dataformat.gif");	
			} else {
				// unhandled
			}
		}
		return null;
	}
}