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

import org.eclipse.jface.wizard.IWizard;
import org.w3c.dom.Element;

/**
 * @author lhein
 */
public interface GlobalConfigurationTypeWizard extends IWizard {
	
	/**
	 * returns the node created by this wizard
	 * 
	 * @return	the node or null on error/abort
	 */
	Element getGlobalConfigurationElementNode();
	
	/**
	 * sets the node for the wizard
	 * 
	 * @param node
	 */
	void setGlobalConfigurationElementNode(Element node);
}
