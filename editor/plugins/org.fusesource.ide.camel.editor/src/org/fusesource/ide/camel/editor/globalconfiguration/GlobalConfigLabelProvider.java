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
import org.fusesource.ide.camel.model.service.core.model.GlobalDefinitionCamelModelElement;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.w3c.dom.Node;

class GlobalConfigLabelProvider implements IStyledLabelProvider {

	private final CamelGlobalConfigEditor camelGlobalConfigEditor;

	GlobalConfigLabelProvider(CamelGlobalConfigEditor camelGlobalConfigEditor) {
		this.camelGlobalConfigEditor = camelGlobalConfigEditor;
	}

	private StyledString getStyledTextForCamelModelElement(AbstractCamelModelElement cme) {
		StyledString text = new StyledString();
		String type = getTypeFromExtensionPoint(cme);
		final String id = cme.getId();
		text.append(id != null ? id : "<unknownId>");
		if (!Strings.isEmpty(type)) {
			text.append(" (" + type + ")", StyledString.COUNTER_STYLER);
		}
		return text;
	}

	/**
	 * @param node
	 * @return
	 */
	private String getTypeFromExtensionPoint(AbstractCamelModelElement cme) {
		for (GlobalConfigElementItem item : camelGlobalConfigEditor.getElementContributions()) {
			if (item.getContributor().canHandle(cme)) {
				return item.getName();
			}
		}
		return Strings.capitalize(CamelUtils.getTranslatedNodeName(cme.getXmlNode()));
	}

	private StyledString getStyledTextForCategory(String element) {
		GlobalConfigCategoryItem cat = camelGlobalConfigEditor.getCategoryForId(element);
		return new StyledString(cat.getName());
	}

	@Override
	public StyledString getStyledText(Object element) {
		if (element instanceof String) {
			return getStyledTextForCategory((String) element);
		} else if (element instanceof AbstractCamelModelElement) {
			return getStyledTextForCamelModelElement((AbstractCamelModelElement) element);
		}
		return new StyledString();
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof GlobalDefinitionCamelModelElement) {
			return CamelEditorUIActivator.getDefault().getImage("bean16.png");
		} else if (element instanceof AbstractCamelModelElement) {
			return getImageForCamelModelElement((AbstractCamelModelElement) element);
		} else if (element instanceof String) {
			GlobalConfigCategoryItem cat = camelGlobalConfigEditor.getCategoryForId((String) element);
			return cat.getIcon();
		}
		return null;
	}

	/**
	 * @param cme
	 * @return
	 */
	private Image getImageForCamelModelElement(AbstractCamelModelElement cme) {
		final Node xmlNode = cme.getXmlNode();
		Image res = getIconFromExtensionPoint(cme);
		if (res == null) {
			if ("endpoint".equalsIgnoreCase(cme.getTranslatedNodeName())) {
				res = CamelEditorUIActivator.getDefault().getImage("endpointdef.png");
			} else if (xmlNode != null) {
				final Node parentNode = xmlNode.getParentNode();
				if ("dataFormats".equalsIgnoreCase(CamelUtils.getTranslatedNodeName(parentNode))) {
					res = CamelEditorUIActivator.getDefault().getImage("dataformat.png");
				}
			}
		}
		return res;
	}

	/**
	 * @param cme
	 */
	private Image getIconFromExtensionPoint(AbstractCamelModelElement cme) {
		for (GlobalConfigElementItem item : camelGlobalConfigEditor.getElementContributions()) {
			if (item.getContributor().canHandle(cme)) {
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