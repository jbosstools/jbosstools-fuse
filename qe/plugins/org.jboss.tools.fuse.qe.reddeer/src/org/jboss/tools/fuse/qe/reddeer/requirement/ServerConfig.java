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
package org.jboss.tools.fuse.qe.reddeer.requirement;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.tools.fuse.qe.reddeer.runtime.Namespaces;
import org.jboss.tools.fuse.qe.reddeer.runtime.ServerBase;
import org.jboss.tools.fuse.qe.reddeer.runtime.impl.ServerAS;
import org.jboss.tools.fuse.qe.reddeer.runtime.impl.ServerEAP;
import org.jboss.tools.fuse.qe.reddeer.runtime.impl.ServerFuse;
import org.jboss.tools.fuse.qe.reddeer.runtime.impl.ServerKaraf;
import org.jboss.tools.fuse.qe.reddeer.runtime.impl.ServerWildFly;

/**
 * 
 * @author apodhrad
 * 
 */

@XmlRootElement(name = "server-requirement", namespace = Namespaces.SOA_REQ)
public class ServerConfig {

	private String name;

	@XmlAttribute(name = "name")
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@XmlElements({
		@XmlElement(name = "as", namespace = Namespaces.SOA_REQ, type = ServerAS.class),
		@XmlElement(name = "eap", namespace = Namespaces.SOA_REQ, type = ServerEAP.class),
		@XmlElement(name = "wildfly", namespace = Namespaces.SOA_REQ, type = ServerWildFly.class),
		@XmlElement(name = "karaf", namespace = Namespaces.SOA_REQ, type = ServerKaraf.class),
		@XmlElement(name = "fuse", namespace = Namespaces.SOA_REQ, type = ServerFuse.class) })
	private ServerBase serverBase;

	public ServerBase getServerBase() {
		if (serverBase != null) {
			serverBase.setName(name);
		}
		return serverBase;
	}

}
