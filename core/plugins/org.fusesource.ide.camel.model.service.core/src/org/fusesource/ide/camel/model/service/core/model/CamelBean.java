/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
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
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;
import org.w3c.dom.Node;

/**
 * @author bfitzpat
 */
public class CamelBean extends GlobalDefinitionCamelModelElement {

	/**
	 * @param parent
	 * @param underlyingNode
	 */
	public CamelBean(AbstractCamelModelElement parent, Node underlyingNode) {
		super(parent, underlyingNode);
		setUnderlyingMetaModelObject(new GlobalBeanEIP());
	}
	
	public CamelBean(String name) {
		super(null, null);
		setUnderlyingMetaModelObject(new GlobalBeanEIP());
		setParameter(GlobalBeanEIP.PROP_CLASS, name);
	}
	
	public String getClassName() {
		return (String)getParameter(GlobalBeanEIP.PROP_CLASS);
	}
	public void setClassName(String name) {
		setParameter(GlobalBeanEIP.PROP_CLASS, name);
	}
	public String getScope() {
		return (String)getParameter(GlobalBeanEIP.PROP_SCOPE);
	}
	public void setScope(String value) {
		setParameter(GlobalBeanEIP.PROP_SCOPE, value);
	}
	public String getDependsOn() {
		return (String)getParameter(GlobalBeanEIP.PROP_DEPENDS_ON);
	}
	public void setDependsOn(String value) {
		setParameter(GlobalBeanEIP.PROP_DEPENDS_ON, value);
	}
	public String getInitMethod() {
		return (String)getParameter(GlobalBeanEIP.PROP_INIT_METHOD);
	}
	public void setInitMethod(String value) {
		setParameter(GlobalBeanEIP.PROP_INIT_METHOD, value);
	}
	public String getDestroyMethod() {
		return (String)getParameter(GlobalBeanEIP.PROP_DESTROY_METHOD);
	}
	public void setDestroyMethod(String value) {
		setParameter(GlobalBeanEIP.PROP_DESTROY_METHOD, value);
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
			CamelModelServiceCoreActivator.pluginLog().logWarning("ParseAttributes: Missing EIP for Bean. Ignored.");
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
		// we do want to parse bean contents
		return true;
	}
}