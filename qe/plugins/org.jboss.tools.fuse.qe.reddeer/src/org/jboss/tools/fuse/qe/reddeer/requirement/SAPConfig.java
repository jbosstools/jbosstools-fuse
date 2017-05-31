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

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.tools.fuse.qe.reddeer.runtime.Namespaces;
import org.jboss.tools.fuse.qe.reddeer.runtime.impl.SAPDestination;
import org.jboss.tools.fuse.qe.reddeer.runtime.impl.SAPLib;
import org.jboss.tools.fuse.qe.reddeer.runtime.impl.SAPServer;

/**
 * 
 * @author apodhrad
 * 
 */

@XmlRootElement(name = "sap-requirement", namespace = Namespaces.SOA_REQ)
public class SAPConfig {

	private String name;

	@XmlAttribute(name = "name")
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@XmlElement(name = "lib", namespace = Namespaces.SOA_REQ)
	private SAPLib lib;

	public SAPLib getLib() {
		return lib;
	}

	@XmlElementWrapper(name = "destinations", namespace = Namespaces.SOA_REQ)
	@XmlElement(name = "destination", namespace = Namespaces.SOA_REQ)
	private List<SAPDestination> destinations;

	public List<SAPDestination> getDestinations() {
		return destinations;
	}

	public SAPDestination getDestination() {
		return destinations.get(0);
	}

	@XmlElementWrapper(name = "servers", namespace = Namespaces.SOA_REQ)
	@XmlElement(name = "server", namespace = Namespaces.SOA_REQ)
	private List<SAPServer> servers;

	public List<SAPServer> getServers() {
		return servers;
	}

	public SAPServer getServer() {
		return servers.get(0);
	}

}
