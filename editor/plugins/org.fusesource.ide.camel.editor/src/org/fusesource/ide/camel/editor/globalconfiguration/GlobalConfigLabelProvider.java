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

	private StyledString getStyledTextForCamelModelElement(AbstractCamelModelElement cme) {
		StyledString text = new StyledString();
		String type = getTypeFromExtensionPoint(cme.getXmlNode());
		text.append(cme.getId());
		if (!Strings.isEmpty(type)) {
			text.append(" (" + type + ")", StyledString.COUNTER_STYLER);
		}
		return text;
	}

	private StyledString getStyledTextForXMLElement(Element node) {
		StyledString text = new StyledString();
		String type = getTypeFromExtensionPoint(node);
		final String idAttributeValue = node.getAttribute("id");
		text.append(!Strings.isEmpty(idAttributeValue) ? idAttributeValue : CamelUtils.getTranslatedNodeName(node));
		if (!Strings.isEmpty(type)) {
			text.append(" (" + type + ") ", StyledString.COUNTER_STYLER);
		}
		return text;
	}

	/**
	 * @param node
	 * @return
	 */
	private String getTypeFromExtensionPoint(Node node) {
		for (GlobalConfigElementItem item : camelGlobalConfigEditor.getElementContributions()) {
			if (item.getContributor().canHandle(node)) {
				return item.getName();
			}
		}
		return Strings.capitalize(CamelUtils.getTranslatedNodeName(node));
	}

	private StyledString getStyledTextForCategory(String element) {
		GlobalConfigCategoryItem cat = camelGlobalConfigEditor.getCategoryForId(element);
		return new StyledString(cat.getName());
	}

	@Override
	public StyledString getStyledText(Object element) {
		if (element instanceof String) {
			return getStyledTextForCategory((String) element);
		} else if (element instanceof Element) {
			return getStyledTextForXMLElement((Element) element);
		} else if (element instanceof AbstractCamelModelElement) {
			return getStyledTextForCamelModelElement((AbstractCamelModelElement) element);
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
		return getIconFromExtensionPoint(element);
	}

	/**
	 * @param cme
	 * @return
	 */
	private Image getImageForCamelModelElement(AbstractCamelModelElement cme) {
		final Node xmlNode = cme.getXmlNode();
		Image res = getIconFromExtensionPoint(xmlNode);
		if (res == null) {
			if ("endpoint".equalsIgnoreCase(cme.getTranslatedNodeName())) {
				res = CamelEditorUIActivator.getDefault().getImage("endpointdef.png");
			} else if (xmlNode != null) {
				final Node parentNode = xmlNode.getParentNode();
				if ("dataFormats".equalsIgnoreCase(CamelUtils.getTranslatedNodeName(parentNode))) {
					res = CamelEditorUIActivator.getDefault().getImage("dataformat.gif");
				}
			}
		}
		return res;
	}

	/**
	 * @param cme
	 */
	private Image getIconFromExtensionPoint(Node node) {
		for (GlobalConfigElementItem item : camelGlobalConfigEditor.getElementContributions()) {
			if (item.getContributor().canHandle(node)) {
				return item.getIcon();
			}
		}
		return null;
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