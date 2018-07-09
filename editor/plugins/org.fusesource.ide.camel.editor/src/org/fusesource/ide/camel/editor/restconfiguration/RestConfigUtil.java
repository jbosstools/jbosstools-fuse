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
package org.fusesource.ide.camel.editor.restconfiguration;

import java.util.UUID;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.RestConfigurationElement;
import org.fusesource.ide.camel.model.service.core.model.RestElement;
import org.fusesource.ide.camel.model.service.core.model.RestVerbElement;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.core.xml.namespace.BlueprintNamespaceHandler;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author brianf
 *
 */
public class RestConfigUtil {

	private static final String XMLNAMESPACE = "xmlns"; //$NON-NLS-1$

	public Parameter createParameter(String name, String jType) {
		Parameter outParm = new Parameter();
		outParm.setName(name);
		outParm.setJavaType(jType);
		return outParm;
	}

	public Object getAttributeValue(AbstractCamelModelElement element, String attrName) {
		if (element.getXmlNode() != null) {
			return getAttributeValue(element.getXmlNode(), attrName);
		}
		return null;
	}

	public Object getAttributeValue(Node element, String attrName) {
		if (element != null && element.hasAttributes()) {
			Node attrNode = element.getAttributes().getNamedItem(attrName);
			if (attrNode != null) {
				return attrNode.getNodeValue();
			}
		}
		return null;
	}

	public void setAttributeValue(AbstractCamelModelElement element, String attrName, String attrValue) {
		if (element.getXmlNode() != null) {
			setAttributeValue(element.getXmlNode(), attrName, attrValue);
		}
	}

	public void setAttributeValue(Node node, String attrName, String attrValue) {
		if (node != null) {
			Element e = (Element) node;
			Object oldValue = getAttributeValue(node, attrName);
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

	public String getNamespace(Node node) {
		if (node != null) {
			String nsURI = node.getNamespaceURI();
			if (nsURI == null && node.getParentNode() != null) {
				return getNamespace(node.getParentNode());
			}
			if (nsURI != null) {
				return nsURI;
			}
		}
		return null;
	}
	
	private String getFirstNSPrefixForURI(Node rootNode, String namespaceUri) {
		NamedNodeMap atts = rootNode.getAttributes();
		for (int i = 0; i < atts.getLength(); i++) {
			Node node = atts.item(i);
			String name = node.getNodeName();
			if (namespaceUri.equals(node.getNodeValue())
					&& (name != null && (XMLNAMESPACE.equals(name) || name.startsWith(XMLNAMESPACE + ":")))) { //$NON-NLS-1$
				if (name.startsWith(XMLNAMESPACE + ":")) { //$NON-NLS-1$
					return name.substring(name.indexOf(':') + 1);
				} else {
					return node.getPrefix();
				}
			}
		}
		return null;
	}
	
	private String getCamelNSPrefix(Node rootNode) {
		String blueprintPrefix = getFirstNSPrefixForURI(rootNode, BlueprintNamespaceHandler.NAMESPACEURI_OSGI_BLUEPRINT_HTTP);
		if (blueprintPrefix != null) {
			return blueprintPrefix;
		}
		String springPrefix = getFirstNSPrefixForURI(rootNode, org.fusesource.ide.foundation.core.util.CamelUtils.SPRING_BEANS_NAMESPACE);
		if (springPrefix != null) {
			return springPrefix;
		}
		return null;
	}

	public RestConfigurationElement createRestConfigurationNode(final CamelFile camelFile) {
		// get NS prefix from parent document, not route container node
		final String prefixNS = 
				getCamelNSPrefix(camelFile.getRouteContainer().getXmlNode().getOwnerDocument().getDocumentElement());
		Node newXMLNode = 
				camelFile.createElement(RestConfigurationElement.REST_CONFIGURATION_TAG, prefixNS);
		return new RestConfigurationElement(camelFile, newXMLNode);
	}

	public RestElement createRestElementNode(final CamelFile camelFile) {
		// get NS prefix from parent document, not route container node
		final String prefixNS = getCamelNSPrefix(camelFile.getRouteContainer().getXmlNode().getOwnerDocument().getDocumentElement());
		Node newXMLNode = camelFile.createElement(RestElement.REST_TAG, prefixNS);
		String id = computeId(newXMLNode);
		RestElement restElement = new RestElement(camelFile.getRouteContainer(), newXMLNode);
		restElement.setId(id);
		return restElement;
	}

	public RestVerbElement createRestVerbElementNode(final CamelFile camelFile, RestElement restElement, final String verb) {
		// get NS prefix from parent document, not route container node
		final String prefixNS = getCamelNSPrefix(camelFile.getRouteContainer().getXmlNode().getOwnerDocument().getDocumentElement());
		
		// create operation node
		Node newXMLNode = camelFile.createElement(verb, prefixNS);
		
		// create inner TO node
		Node newToNode = camelFile.createElement(AbstractCamelModelElement.ENDPOINT_TYPE_TO, prefixNS);
		newXMLNode.appendChild(newToNode);
		
		// ensure that we have an ID from the start
		String id = computeId(newXMLNode);
		
		RestVerbElement restVerbElement = new RestVerbElement(restElement, newXMLNode);
		restVerbElement.setId(id);
		return restVerbElement;
	}

	private String computeId(Node child) {
		Node idNode = child.getAttributes().getNamedItem(AbstractCamelModelElement.ID_ATTRIBUTE);
		if (idNode != null){
			return idNode.getNodeValue();
		} else {
			return CamelUtils.getTagNameWithoutPrefix(child) + "-" + UUID.randomUUID().toString(); //$NON-NLS-1$
		}
	}
	
	public String generateRestOperationId() {
		return UUID.randomUUID().toString(); //$NON-NLS-1$
	}
}
