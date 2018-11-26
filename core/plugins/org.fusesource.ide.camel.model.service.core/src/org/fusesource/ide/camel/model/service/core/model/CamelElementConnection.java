/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.model.service.core.model;

import org.fusesource.ide.camel.model.service.core.util.XMLUtils;
import org.w3c.dom.Node;

/**
 * @author lhein
 */
public class CamelElementConnection extends AbstractCamelModelElement {
	/**
	 * True, if the connection is attached to its endpoints.
	 */
	private boolean isConnected;

	/**
	 * Connection's source endpoint.
	 */
	private AbstractCamelModelElement source;

	/**
	 * Connection's target endpoint.
	 */
	private AbstractCamelModelElement target;

	/**
	 * Create a (solid) connection between two distinct shapes.
	 * 
	 * @param source
	 *            a source endpoint for this connection (non null)
	 * @param target
	 *            a target endpoint for this connection (non null)
	 * @throws IllegalArgumentException
	 *             if any of the parameters are null or source == target
	 * @see #setLineStyle(int)
	 */
	public CamelElementConnection(AbstractCamelModelElement source, AbstractCamelModelElement target) {
		super(null, null);
		if (source == null) {
			throw new IllegalArgumentException("No source for Flow");
		}
		if (target == null) {
			throw new IllegalArgumentException("No target for Flow");
		}
		reconnect(source, target);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CamelElementConnection other = (CamelElementConnection) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source)) {
			return false;
		} if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target)) {
			return false;
		}
		return true;
	}


	/**
	 * Disconnect this connection from the shapes it is attached to.
	 */
	public void disconnect() {
		if (isConnected) {
			source.setOutputElement(null);
			target.setInputElement(null);
			isConnected = false;
		}
	}

	/**
	 * Returns the source endpoint of this connection.
	 * 
	 * @return a non-null GenericObject instance
	 */
	public AbstractCamelModelElement getSource() {
		return source;
	}

	/**
	 * Returns the target endpoint of this connection.
	 * 
	 * @return a non-null GenericObject instance
	 */
	public AbstractCamelModelElement getTarget() {
		return target;
	}

	/**
	 * Reconnect this connection. The connection will reconnect with the shapes
	 * it was previously attached to.
	 */
	public void reconnect() {
		if (!isConnected) {
			source.setOutputElement(target);
			target.setInputElement(source);
			
			// rearrange DOM elements
			Node sourceNode = source.getXmlNode();
			Node targetNode = target.getXmlNode();
			Node currentNextNodeOfSourceNode = new XMLUtils().getNextNode(sourceNode);
			if (currentNextNodeOfSourceNode == null || !currentNextNodeOfSourceNode.equals(targetNode)) {
				source.getParent().getXmlNode().insertBefore(sourceNode, targetNode);
			}
			
			isConnected = true;
		}
	}

	/**
	 * Reconnect to a different source and/or target shape. The connection will
	 * disconnect from its current attachments and reconnect to the new source
	 * and target.
	 * 
	 * @param newSource
	 *            a new source endpoint for this connection (non null)
	 * @param newTarget
	 *            a new target endpoint for this connection (non null)
	 * @throws IllegalArgumentException
	 *             if any of the parameters are null or newSource == newTarget
	 */
	public void reconnect(AbstractCamelModelElement newSource, AbstractCamelModelElement newTarget) {
		if (newSource == null || newTarget == null || newSource == newTarget) {
			throw new IllegalArgumentException();
		}

		disconnect();

		this.source = newSource;
		this.target = newTarget;

		reconnect();
	}

	@Override
	public String toString() {
		return "Connection: " + source + " -> " + target;
	}
}
