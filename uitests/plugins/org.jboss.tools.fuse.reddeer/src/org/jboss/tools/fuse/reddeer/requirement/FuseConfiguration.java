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
package org.jboss.tools.fuse.reddeer.requirement;

import org.eclipse.reddeer.junit.requirement.configuration.RequirementConfiguration;
import org.jboss.tools.fuse.reddeer.runtime.ServerBase;

/**
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 * 
 */
public class FuseConfiguration implements RequirementConfiguration {

	private String name;
	private String camelVersion;
	private ServerBase server;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCamelVersion() {
		return camelVersion;
	}

	public void setCamelVersion(String camelVersion) {
		this.camelVersion = camelVersion;
	}

	public ServerBase getServer() {
		return server;
	}

	public void setServer(ServerBase serverBase) {
		this.server = serverBase;
	}

	@Override
	public String getId() {
		if (getName() != null) {
			return getName();
		}
		return getServer().getName();
	}
}
