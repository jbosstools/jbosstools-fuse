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

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.camel.editor.dialogs.GlobalConfigCategoryItem;
import org.fusesource.ide.camel.editor.dialogs.GlobalConfigElementItem;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class GlobalConfigLabelProvider implements IStyledLabelProvider {

	private final CamelGlobalConfigEditor camelGlobalConfigEditor;

	GlobalConfigLabelProvider(CamelGlobalConfigEditor camelGlobalConfigEditor) {
		this.camelGlobalConfigEditor = camelGlobalConfigEditor;
	}

	private StyledString getTextCamelModelElement(AbstractCamelModelElement cme) {
		StyledString text = new StyledString();
		String type = Strings.capitalize(cme.getTranslatedNodeName());
		for (GlobalConfigElementItem item : camelGlobalConfigEditor.getElementContributions()) {
			if (item.getContributor().canHandle(cme.getXmlNode())) {
				type = item.getName();
				break;
			}
		}
		text.append(cme.getId());
		text.append(" (" + type + ")", StyledString.COUNTER_STYLER);
		return text;
	}

	private StyledString getTextForXMLElement(Element node) {
		StyledString text = new StyledString();
		String type = Strings.capitalize(CamelUtils.getTranslatedNodeName(node));
		for (GlobalConfigElementItem item : camelGlobalConfigEditor.getElementContributions()) {
			if (item.getContributor().canHandle(node)) {
				type = item.getName();
				break;
			}
		}
		text.append(!Strings.isEmpty(node.getAttribute("id")) ? node.getAttribute("id") : CamelUtils.getTranslatedNodeName(node));
		if (!Strings.isEmpty(node.getAttribute("id"))) text.append(" (" + type + ") ", StyledString.COUNTER_STYLER);
		return text;
	}

	private StyledString getTextForCategory(String element) {
		GlobalConfigCategoryItem cat = camelGlobalConfigEditor.getCategoryForId(element);
		return new StyledString(cat.getName());
	}

	@Override
	public StyledString getStyledText(Object element) {
		if (element instanceof String) {
			return getTextForCategory((String) element);
		} else if (element instanceof Element) {
			return getTextForXMLElement((Element) element);
		} else if (element instanceof AbstractCamelModelElement) {
			return getTextCamelModelElement((AbstractCamelModelElement) element);
		} else {
			// unhandled
		}
		return new StyledString();
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof Node) {
			return CamelEditorUIActivator.getDefault().getImage("beandef.gif");
		} else if (element instanceof AbstractCamelModelElement) {
			return getImageForCamelModelElement((AbstractCamelModelElement) element);
		} else if (element instanceof Element) {
			return getImageForXMLElement((Node) element);
		} else if (element instanceof String) {
			GlobalConfigCategoryItem cat = camelGlobalConfigEditor.getCategoryForId((String) element);
			return cat.getIcon();
		}
		return null;
	}

	/**
	 * @param element
	 */
	private Image getImageForXMLElement(Node element) {
		for (GlobalConfigElementItem item : camelGlobalConfigEditor.getElementContributions()) {
			if (item.getContributor().canHandle(element)) {
				return item.getIcon();
			}
		}
		return null;
	}

	/**
	 * @param cme
	 * @return
	 */
	private Image getImageForCamelModelElement(AbstractCamelModelElement cme) {
		for (GlobalConfigElementItem item : camelGlobalConfigEditor.getElementContributions()) {
			if (item.getContributor().canHandle(cme.getXmlNode())) {
				return item.getIcon();
			}
		}
		if (cme.getTranslatedNodeName().equalsIgnoreCase("endpoint")) {
			return CamelEditorUIActivator.getDefault().getImage("endpointdef.png");
		} else if (CamelUtils.getTranslatedNodeName(cme.getXmlNode().getParentNode()).equalsIgnoreCase("dataFormats")) {
			return CamelEditorUIActivator.getDefault().getImage("dataformat.gif");
		} else {
			return null;
		}
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}
}