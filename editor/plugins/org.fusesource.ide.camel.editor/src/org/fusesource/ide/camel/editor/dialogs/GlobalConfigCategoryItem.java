/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation <
 ******************************************************************************/ 

package org.fusesource.ide.camel.editor.dialogs;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;

/**
 * @author lhein
 *
 */
public class GlobalConfigCategoryItem implements GlobalConfigSupport {
	private Image icon;
	private String id;
	private String name;
	private ArrayList<GlobalConfigElementItem> children = new ArrayList<GlobalConfigElementItem>();
	
	/**
	 * @return the icon
	 */
	@Override
	public Image getIcon() {
		return this.icon;
	}
	
	/**
	 * @return the id
	 */
	@Override
	public String getId() {
		return this.id;
	}
	
	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param icon the icon to set
	 */
	@Override
	public void setIcon(Image icon) {
		this.icon = icon;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the children
	 */
	public ArrayList<GlobalConfigElementItem> getChildren() {
		return this.children;
	}
}
