/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.provider.ext;

import java.util.List;

import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;

/**
 * @author lhein
 */
public interface ICustomGlobalConfigElementContribution {

	/**
	 * this method is invoked when a user tries to create an element of this
	 * type in the global config tab. Implementors should come up with a wizard
	 * for creating the element and if that wizard is finished it should create
	 * the xml node ready to be injected into the xml document. This node will
	 * be gathered via the GlobalConfigurationTypeWizard.getResultNode() method.
	 * Make sure you set your result element correctly. DO NOT MANIPULATE THE
	 * DOCUMENT OR INSERT THE NODE YOURSELF! THIS IS DONE BY THE EDITOR
	 * AUTOMATICALLY!
	 * 
	 * @param camelFile
	 *            a reference to the camel file providing the context on which
	 *            global elements will be created
	 * @return a wizard for adding this kind of element
	 */
	GlobalConfigurationTypeWizard createGlobalElement(CamelFile camelFile);
	
	/**
	 * returns a list of dependencies to be injected into the maven pom file
	 * in order to make the new element working
	 * 
	 * @return	a list of dependencies or an empty list if no dep required
	 */
	List<Dependency> getElementDependencies();
	
	/**
	 * this method is invokes when a user tries to modify an existing global
	 * element of this type in the global config tab. Implementors should come
	 * up with a wizard or dialog for modifying the element node. It's up to the
	 * implementors code to change the XML element given as parameter. If no
	 * wizard provided, the edit action will put focus on Properties view.
	 * 
	 * @param camelFile
	 *            a reference to the camel file providing the context on which
	 *            global elements will be created
	 * @return true if changed by user or false if canceled modifying
	 */
	GlobalConfigurationTypeWizard modifyGlobalElement(CamelFile camelFile);
	
	/**
	 * this method is invoked if the user deleted a global element from the
	 * camel xml file via the global config tab. Deletions in the xml source are
	 * NOT tracked. This is an informational hook so implementors can react upon
	 * deletion of their elements and properly do clean up actions etc.
	 * 
	 * @param camelModelElement
	 */
	void onGlobalElementDeleted(AbstractCamelModelElement camelModelElement);

	/**
	 * checks whether the extension can handle the given node or not
	 * 
	 * @param nodeToHandle	the xml node/element we need to handle
	 * @return	true if handled by this class, otherwise false (keep in mind 
	 * that it is not enough to check for the node name as for instance "bean"
	 * elements are used for several cases - maybe you are not the only one. So 
	 * be sure you check if its exactly the type of node you are responsible for
	 */
	boolean canHandle(final AbstractCamelModelElement camelModelElementToHandle);
	
	/**
	 * returns the type of global element this class handles
	 * 
	 * @return	one of the enums values
	 */
	GlobalConfigElementType getGlobalConfigElementType();
}
