/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.model;

import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author lhein
 */
public class CamelRoutesElement extends CamelRouteContainerElement {

	/**
	 * @param parent
	 * @param underlyingXmlNode
	 */
	public CamelRoutesElement(AbstractCamelModelElement parent, Node underlyingXmlNode) {
		super(parent, underlyingXmlNode);
	}

	/**
	 * parses the children of this node
	 */
	@Override
	protected void parseChildren() {
		NodeList children = getXmlNode().getChildNodes();
		for (int i=0; i<children.getLength(); i++) {
			Node tmp = children.item(i);
			if (tmp.getNodeType() != Node.ELEMENT_NODE) continue;
			if (CamelUtils.getTranslatedNodeName(tmp).equals(ROUTE_NODE_NAME)) {
				CamelRouteElement cme = new CamelRouteElement(this, tmp);
				cme.initialize();
				addChildElement(cme);
			} else {
				CamelModelServiceCoreActivator.pluginLog().logWarning("Unexpected child element of the routes definition: " + CamelUtils.getTranslatedNodeName(tmp));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement#getRouteContainer()
	 */
	@Override
	public CamelRouteContainerElement getRouteContainer() {
		return this;
	}
}
