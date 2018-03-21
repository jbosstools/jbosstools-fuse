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
package org.fusesource.ide.projecttemplates.wizards.pages.model;

/**
 * @author lhein
 */
public interface TemplateOrCategoryItem {
	
	/**
	 * returns the name of the item
	 * 
	 * @return
	 */
	String getName();
	
	/**
	 * returns the weight of the item
	 * 
	 * @return
	 */
	int getWeight();
}
