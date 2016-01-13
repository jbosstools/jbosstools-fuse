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
package org.fusesource.ide.camel.editor.dialogs;

import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution;

/**
 * @author lhein
 */
public class GlobalConfigElementItem {
	private ICustomGlobalConfigElementContribution contributor;
	private Image icon;
	private String id;
	private String name;
	
	/**
	 * @return the contributor
	 */
	public ICustomGlobalConfigElementContribution getContributor() {
		return this.contributor;
	}
	
	/**
	 * @return the icon
	 */
	public Image getIcon() {
		return this.icon;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param contributor the contributor to set
	 */
	public void setContributor(ICustomGlobalConfigElementContribution contributor) {
		this.contributor = contributor;
	}
	
	/**
	 * @param icon the icon to set
	 */
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
}
