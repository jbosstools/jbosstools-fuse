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
package org.fusesource.ide.camel.model.service.core.model;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.camel.model.service.core.model.eips.RestVerbElementEIP;
import org.w3c.dom.Node;

/**
 * @author bfitzpat
 */
public class RestVerbElement extends AbstractRestCamelModelElement {

	public static final String GET_VERB = "get"; //$NON-NLS-1$
	public static final String POST_VERB = "post"; //$NON-NLS-1$
	public static final String PUT_VERB = "put"; //$NON-NLS-1$
	public static final String PATCH_VERB = "patch"; //$NON-NLS-1$
	public static final String DELETE_VERB = "delete"; //$NON-NLS-1$
	public static final String HEAD_VERB = "head"; //$NON-NLS-1$
	public static final String TRACE_VERB = "trace"; //$NON-NLS-1$
	public static final String CONNECT_VERB = "connect"; //$NON-NLS-1$
	public static final String OPTIONS_VERB = "options"; //$NON-NLS-1$
	
	/**
	 * @param parent
	 * @param underlyingNode
	 */
	public RestVerbElement(AbstractCamelModelElement parent, Node underlyingNode) {
		super(parent, underlyingNode);
		setUnderlyingMetaModelObject(new RestVerbElementEIP(getNodeTypeId()));
	}
	
	public RestVerbElement(String name) {
		super(null, null);
		setUnderlyingMetaModelObject(new RestVerbElementEIP());
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
			CamelModelServiceCoreActivator.pluginLog().logWarning("ParseAttributes: Missing EIP for REST Verb. Ignored.");
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
		// we do want to parse REST Verb contents
		return true;
	}
}