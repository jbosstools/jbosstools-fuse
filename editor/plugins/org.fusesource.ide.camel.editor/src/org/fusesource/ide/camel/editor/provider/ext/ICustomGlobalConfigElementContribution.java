/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.provider.ext;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author lhein
 */
public interface ICustomGlobalConfigElementContribution {

	/**
	 * this method is invoked when a user tries to create an element
	 * of this type in the global config tab. Implementors should come up with 
	 * a wizard for creating the element and if that wizard is finished it 
	 * should create the xml node ready to be injected into the xml document.
	 * DO NOT MANIPULATE DOCUMENT OR INSERT THE NODE YOURSELF! THIS IS DONE BY
	 * THE EDITOR AUTOMATICALLY! 
	 * 
	 * @param document		a reference to the camel context xml document to be
	 * 						used for creating dom nodes / elements
	 * @return	the xml element or null if the user canceled
	 */
	Node createGlobalElement(Document document);
	
	/**
	 * this method is invokes when a user tries to modify an existing global
	 * element of this type in the global config tab. Implementors should come
	 * up with a wizard or dialog for modifying the element node. It's up to
	 * the implementors code to change the XML element given as parameter.
	 * 
	 * @param document	a reference to the camel context xml document to be
	 * 					used for creating dom nodes / elements
	 * @param node		the node which is to be modified
	 * @return	true if changed by user or false if canceled modifying
	 */
	boolean modifyGlobalElement(Document document, Node node);
	
	/**
	 * this method is invoked if the user deleted a global element from the
	 * camel xml file via the global config tab. Deletions in the xml source 
	 * are NOT tracked. This is an informational hook so implementors can react
	 * upon deletion of their elements and properly do clean up actions etc.
	 * 
	 * @param node
	 */
	void onGlobalElementDeleted(Node node);

	/**
	 * checks whether the extension can handle the given node or not
	 * 
	 * @param nodeToHandle	the xml node/element we need to handle
	 * @return	true if handled by this class, otherwise false (keep in mind 
	 * that it is not enough to check for the node name as for instance "bean"
	 * elements are used for several cases - maybe you are not the only one. So 
	 * be sure you check if its exactly the type of node you are responsible for
	 */
	boolean canHandle(final Node nodeToHandle);
	
	/**
	 * returns the type of global element this class handles
	 * 
	 * @return	one of the enums values
	 */
	GlobalConfigElementType getGlobalConfigElementType();
}
