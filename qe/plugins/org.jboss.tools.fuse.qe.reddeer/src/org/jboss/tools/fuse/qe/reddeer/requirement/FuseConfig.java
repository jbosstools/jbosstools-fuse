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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.tools.fuse.qe.reddeer.runtime.Namespaces;

/**
 * @author tsedmik
 */
@XmlRootElement(name = "fuse-requirement", namespace = Namespaces.SOA_REQ)
@XmlAccessorType(XmlAccessType.FIELD)
public class FuseConfig extends ServerConfig {

	@XmlElement(name = "camelVersion", namespace = Namespaces.SOA_REQ)
	private String camelVersion;

	public String getCamelVersion() {
		return camelVersion;
	}

	public void setCamelVersion(String camelVersion) {
		this.camelVersion = camelVersion;
	}
}
