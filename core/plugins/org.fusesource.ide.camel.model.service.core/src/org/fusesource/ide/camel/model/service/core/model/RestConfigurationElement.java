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
package org.fusesource.ide.camel.model.service.core.model;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.camel.model.service.core.model.eips.RestConfigurationElementEIP;
import org.w3c.dom.Node;

/**
 * @author bfitzpat
 */
public class RestConfigurationElement extends AbstractRestCamelModelElement {

	public static final String REST_CONFIGURATION_TAG = "restConfiguration"; //$NON-NLS-1$

	/**
	 * @param parent
	 * @param underlyingNode
	 */
	public RestConfigurationElement(AbstractCamelModelElement parent, Node underlyingNode) {
		super(parent, underlyingNode, false);
		setUnderlyingMetaModelObject(new RestConfigurationElementEIP());
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement#getKind(java.lang.String)
	 */
	@Override
	public String getKind(String name) {
		// due to the missing EIP as underlying meta model we have to tell AbstractCamelModelElement what
		// kind the attribute is ... so if we got other than ATTRIBUTE please adapt this methods logic!
		return NODE_KIND_ATTRIBUTE;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement#parseAttributes()
	 */
	@Override
	protected void parseAttributes() {
		Eip eip = getUnderlyingMetaModelObject();
		if (eip != null) {
			for (Parameter param : getUnderlyingMetaModelObject().getParameters()) {
				initAttribute(param.getName());
			}
		} else {
			CamelModelServiceCoreActivator.pluginLog().logWarning("ParseAttributes: Missing EIP for REST Configuration. Ignored.");
		}
	}
	
	private void initAttribute(String paramName) {
		String value = parseAttribute(paramName);
		if (value != null) {
			setParameter(paramName, value);
		}
	}	
	
	private String parseAttribute(String name) {
		Node tmp = getXmlNode().getAttributes().getNamedItem(name);
		if (tmp != null) {
			return tmp.getNodeValue();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement#shouldParseNode()
	 */
	@Override
	protected boolean shouldParseNode() {
		// we do want to parse REST contents
		return true;
	}
	
	/**
	 * retrieves the host for the configuration
	 * 
	 * @return
	 */
	public String getHost() {
		return (String)getParameter(RestConfigurationElementEIP.PROP_HOST);
	}
	
	/**
	 * sets the host for the configuration
	 * 
	 * @param uri
	 */
	public void setHost(String host) {
		setParameter(RestConfigurationElementEIP.PROP_HOST, host);
	}
	
	public String getPort() {
		return (String)getParameter(RestConfigurationElementEIP.PROP_PORT);
	}
	public void setPort(String port) {
		setParameter(RestConfigurationElementEIP.PROP_PORT, port);
	}

	public String getComponent() {
		return (String)getParameter(RestConfigurationElementEIP.PROP_COMPONENT);
	}
	public void setComponent(String component) {
		setParameter(RestConfigurationElementEIP.PROP_COMPONENT, component);
	}

	public String getContextPath() {
		return (String)getParameter(RestConfigurationElementEIP.PROP_CONTEXTPATH);
	}
	public void setContextPath(String contextPath) {
		setParameter(RestConfigurationElementEIP.PROP_CONTEXTPATH, contextPath);
	}

	public String getBindingMode() {
		return (String)getParameter(RestConfigurationElementEIP.PROP_BINDINGMODE);
	}
	public void setBindingMode(String bindingMode) {
		setParameter(RestConfigurationElementEIP.PROP_BINDINGMODE, bindingMode);
	}
}