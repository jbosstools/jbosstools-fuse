/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.runtime.impl;

/**
 * JBoss Fuse Server
 * 
 * @author apodhrad
 */
public class ServerFuse extends ServerKaraf {

	private static final String CATEGORY = "Red Hat JBoss Middleware";
	private static final String LABEL_6 = "Red Hat JBoss Fuse";
	private static final String LABEL_7 = "Red Hat Fuse";

	public ServerFuse() {
		setType("Fuse");
	}
	
	@Override
	public String getCategory() {
		return CATEGORY;
	}

	@Override
	public String getServerType() {
		return getLabel(getVersion()) + " " + getVersion() + " Server";
	}

	@Override
	public String getRuntimeType() {
		return getLabel(getVersion()) + " " + getVersion();
	}

	@Override
	public String getRuntimeName() {
		return getLabel(getVersion()) + " " + getVersion() + " Runtime";
	}
	
	private String getLabel(String version) {
		if (version.startsWith("6.")) {
			return LABEL_6;
		} else {
			return LABEL_7;
		}
	}
	
}
