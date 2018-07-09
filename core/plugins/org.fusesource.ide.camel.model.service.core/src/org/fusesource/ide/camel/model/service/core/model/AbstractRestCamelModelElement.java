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

import java.util.UUID;

import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.w3c.dom.Node;

public abstract class AbstractRestCamelModelElement extends AbstractCamelModelElement {

	/**
	 * @param parent The parent in the Camel Model
	 * @param underlyingNode The XML node that this Object is representing
	 * @param generateId Boolean flag indicating if an ID should be generated if it doesn't exist
	 */
	public AbstractRestCamelModelElement(AbstractCamelModelElement parent, Node underlyingNode, boolean generateId) {
		super(parent, underlyingNode);
		if (generateId) {
			String id = computeId(underlyingNode);
			this.setId(id);
		}
	}

	/**
	 * @param parent The parent in the Camel Model
	 * @param underlyingNode The XML node that this Object is representing
	 */
	public AbstractRestCamelModelElement(AbstractCamelModelElement parent, Node underlyingNode) {
		this(parent, underlyingNode, false);
	}
	
	private String computeId(Node child) {
		Node idNode = child.getAttributes().getNamedItem("id");
		if (idNode != null){
			return idNode.getNodeValue();
		} else if (ignoreNode(child)) {
			return null;
		} else {
			return CamelUtils.getTagNameWithoutPrefix(child) + "-" + UUID.randomUUID().toString();
		}
	}

	private boolean ignoreNode(Node child) {
		return !CamelUtils.isCamelNamespaceElement(child);
	}
}
