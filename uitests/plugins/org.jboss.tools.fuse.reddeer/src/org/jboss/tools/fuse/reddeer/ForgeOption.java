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
package org.jboss.tools.fuse.reddeer;

/**
 * Represents an option in JBoss Forge View
 * 
 * @author tsedmik
 */
public class ForgeOption {

	private int number;
	private boolean defaultOption;
	private String name;

	public ForgeOption(int number, boolean defaultOption, String name) {
		super();
		this.number = number;
		this.defaultOption = defaultOption;
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isDefaultOption() {
		return defaultOption;
	}

	public void setDefaultOption(boolean defaultOption) {
		this.defaultOption = defaultOption;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ForgeOption [number=" + number + ", defaultOption=" + defaultOption + ", name=" + name + "]";
	}
}
