/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.runtime.impl;

/**
 * EAP Server
 * 
 * @author apodhrad
 * 
 */
public class ServerEAP extends ServerAS {

	public static final String CATEGORY = "Red Hat JBoss Middleware";

	private final String label = "Red Hat JBoss Enterprise Application Platform";

	public ServerEAP() {
		setType("EAP");
	}
	
	@Override
	public String getCategory() {
		return CATEGORY;
	}

	@Override
	public String getServerType() {
		return label + " " + getVersion();
	}

	@Override
	public String getRuntimeType() {
		return label + " " + getVersion() + " Runtime";
	}

}
