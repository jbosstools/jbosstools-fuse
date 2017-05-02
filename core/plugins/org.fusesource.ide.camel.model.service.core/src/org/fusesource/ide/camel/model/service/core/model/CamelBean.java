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
package org.fusesource.ide.camel.model.service.core.model;

import org.fusesource.ide.foundation.core.util.Strings;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author bfitzpat
 */
public class CamelBean extends GlobalDefinitionCamelModelElement {

	public static final String PROP_ID = "id"; //$NON-NLS-1$
	public static final String PROP_CLASS = "class"; //$NON-NLS-1$
	public static final String PROP_SCOPE = "scope"; //$NON-NLS-1$
	public static final String PROP_DEPENDS_ON = "depends-on"; //$NON-NLS-1$
	public static final String PROP_INIT_METHOD = "init-method"; //$NON-NLS-1$
	public static final String PROP_DESTROY_METHOD = "destroy-method"; //$NON-NLS-1$
	public static final String PROP_FACTORY_METHOD = "factory-method"; //$NON-NLS-1$
	public static final String PROP_FACTORY_BEAN = "factory-bean"; //$NON-NLS-1$
	public static final String ARG_TYPE = "type"; //$NON-NLS-1$
	public static final String ARG_VALUE = "value"; //$NON-NLS-1$
	public static final String PROP_NAME = "name"; //$NON-NLS-1$
	public static final String PROP_VALUE = "value"; //$NON-NLS-1$
	public static final String TAG_PROPERTY = "property"; //$NON-NLS-1$
	public static final String TAG_ARGUMENT = "argument"; //$NON-NLS-1$
	public static final String TAG_CONSTRUCTOR_ARG = "constructor-arg"; //$NON-NLS-1$
	private static final Object[] NO_CHILDREN = {};
	
	/**
	 * @param parent
	 * @param underlyingNode
	 */
	public CamelBean() {
		super(null, null);
	}

	/**
	 * @param parent
	 * @param underlyingXmlNode
	 */
	public CamelBean(AbstractCamelModelElement parent, Node underlyingXmlNode) {
		super(parent, underlyingXmlNode);
	}
	
	public CamelBean(String name) {
		super(null, null);
		setParameter(PROP_CLASS, name);
	}
	public String getClassName() {
		return (String)getParameter(PROP_CLASS);
	}
	public void setClassName(String name) {
		setParameter(PROP_CLASS, name);
	}
	public String getScope() {
		return (String)getParameter(PROP_SCOPE);
	}
	public void setScope(String value) {
		setParameter(PROP_SCOPE, value);
	}
	public String getDependsOn() {
		return (String)getParameter(PROP_DEPENDS_ON);
	}
	public void setDependsOn(String value) {
		setParameter(PROP_DEPENDS_ON, value);
	}
	public String getInitMethod() {
		return (String)getParameter(PROP_INIT_METHOD);
	}
	public void setInitMethod(String value) {
		setParameter(PROP_INIT_METHOD, value);
	}
	public String getDestroyMethod() {
		return (String)getParameter(PROP_DESTROY_METHOD);
	}
	public void setDestroyMethod(String value) {
		setParameter(PROP_DESTROY_METHOD, value);
	}
	
	@Override
	public void setParameter(String name, Object value) {
		super.setParameter(name, value);
		if (value instanceof String) {
			String newValue = (String) value;
			setAttributeValue(name, newValue);
		}
	}
	
	@Override
	public Object getParameter(String name) {
		Object value = super.getParameter(name);
		if (value != null) {
			return super.getParameter(name);
		}
		return getAttributeValue(name);
	}
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.CamelModelElement#setParent(org.fusesource.ide.camel.model.service.core.model.CamelModelElement)
	 */
	@Override
	public void setParent(AbstractCamelModelElement parent) {
		super.setParent(parent);
		if (parent != null && parent.getXmlNode() != null && getXmlNode() != null) {
			boolean alreadyChild = false;
			for (int i = 0; i < parent.getXmlNode().getChildNodes().getLength(); i++) {
				if (parent.getXmlNode().getChildNodes().item(i).isEqualNode(getXmlNode())) {
					alreadyChild = true;
					break;
				}
			}
			if (!alreadyChild) {				
				parent.getXmlNode().appendChild(getXmlNode());	
			}
		}
	}
	
	public void setAttributeValue(String attrName, String attrValue) {
		if (this.getXmlNode() != null) {
			Element e = (Element) this.getXmlNode();
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
	
	public Object getAttributeValue(String attrName) {
		Node camelNode = this.getXmlNode();
		if (camelNode != null && camelNode.hasAttributes()) {
			Node attrNode = camelNode.getAttributes().getNamedItem(attrName);
			if (attrNode != null) {
				return attrNode.getNodeValue();
			}
		}
		return null;
	}
	

	public Object[] getBeanProperties() {
		return getXMLChildrenByTag(TAG_PROPERTY);
	}
	
	public Object[] getBeanArguments() {
		String tagName = getArgumentTag(this.getXmlNode());
		return getXMLChildrenByTag(tagName);
	}

	protected Object[] getXMLChildrenByTag(String tag) {
		Node camelNode = this.getXmlNode();
		if (camelNode instanceof Element) {
			Element parent = (Element) camelNode;
			return convertToArray(parent.getElementsByTagName(tag));
		}
		return NO_CHILDREN;
	}

	private Object[] convertToArray(NodeList list)
	{
		int length = list.getLength();
		Node[] copy = new Node[length];
		for (int n = 0; n < length; ++n) {
			copy[n] = list.item(n);
		}
		return copy;
	}

	protected boolean isBlueprintConfig(Node node) {
		if (node != null) {
			String nsURI = getNamespace(node);
			if(!Strings.isEmpty(nsURI) && nsURI != null) {
				return nsURI.contains("blueprint"); //$NON-NLS-1$
			}
		}
		return false;
	}

	protected String getNamespace(Node node) {
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

	protected String getArgumentTag(Node node) {
		if (node != null) {
			boolean isBlueprint = isBlueprintConfig(node);
			String tagName;
			if (isBlueprint) {
				tagName = TAG_ARGUMENT;
			} else {
				tagName = TAG_CONSTRUCTOR_ARG;
			}
			return tagName;
		}
		return null;
	}
}
