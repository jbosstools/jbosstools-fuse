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
package org.fusesource.ide.camel.model.service.core.util;

import org.w3c.dom.Node;

public class XMLUtils {
	
	/**
	 * returns the next element node
	 *
	 * @param node
	 * @return the next element node or null
	 */
	public Node getNextNode(Node node) {
		Node n = node.getNextSibling();
		while (n != null) {
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				return n;
			}
			n = n.getNextSibling();
		}
		return null;
	}

}
