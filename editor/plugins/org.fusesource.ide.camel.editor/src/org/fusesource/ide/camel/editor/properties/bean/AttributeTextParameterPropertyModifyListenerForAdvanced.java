/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties.bean;

import org.fusesource.ide.camel.editor.properties.creators.modifylisteners.text.AbstractTextParameterPropertyModifyListener;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.fusesource.ide.foundation.core.util.Strings;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author brianf
 *
 */
public class AttributeTextParameterPropertyModifyListenerForAdvanced extends AbstractTextParameterPropertyModifyListener {

	private Parameter parameter;

	public AttributeTextParameterPropertyModifyListenerForAdvanced(AbstractCamelModelElement camelModelElement, Parameter parameter) {
		super(camelModelElement, parameter.getName());
		this.parameter = parameter;
	}

	@Override
	protected void updateModel(String newValue) {
		setAttributeValue(this.parameter.getName(), newValue);
	}
	
	private void setAttributeValue(String attrName, String attrValue) {
		camelModelElement.setParameter(attrName, attrValue);
		if (camelModelElement instanceof CamelBean) {
			CamelBean bean = (CamelBean) camelModelElement;
			bean.setParameter(attrName, attrValue);
		}
		if (camelModelElement.getXmlNode() != null) {
			Element e = (Element) camelModelElement.getXmlNode();
			Object oldValue = getAttributeValue(attrName);
			// if values are both null, no change
			if (oldValue == null && attrValue == null) {
				// no change
				return;
			}
			// if both values are same string, no change
			if (oldValue != null && attrValue != null) {
				String oldValueStr = (String) oldValue;
				if (oldValueStr.contentEquals(attrValue)) {
					// no change
					return;
				}
			}
			// otherwise we have a change, set new value or clear value
			if (!Strings.isEmpty(attrValue)) {
				e.setAttribute(attrName, attrValue);
			} else {
				e.removeAttribute(attrName);
			}
		}
	}
	
	private Object getAttributeValue(String attrName) {
		Node camelNode = camelModelElement.getXmlNode();
		if (camelNode != null && camelNode.hasAttributes()) {
			Node attrNode = camelNode.getAttributes().getNamedItem(attrName);
			if (attrNode != null) {
				return attrNode.getNodeValue();
			}
		}
		return null;
	}
}