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
package org.jboss.tools.fuse.qe.reddeer.runtime.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.tools.fuse.qe.reddeer.runtime.Namespaces;

/**
 * Fabric8 Server
 * 
 * @author tsedmik
 */
@XmlRootElement(name = "fabric8", namespace = Namespaces.SOA_REQ)
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerFabric8 extends ServerKaraf {

	private final String category = "JBoss Fuse";
	private final String label = "Fabric8";

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public String getServerType() {
		return label + " " + getVersion() + " Server";
	}

	@Override
	public String getRuntimeType() {
		return label + " " + getVersion();
	}

	@Override
	public String getRuntimeName() {
		return label + " " + getVersion() + " Runtime";
	}
}
