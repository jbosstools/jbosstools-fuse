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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.tools.fuse.qe.reddeer.runtime.Namespaces;

@XmlRootElement(name = "lib", namespace = Namespaces.SOA_REQ)
@XmlAccessorType(XmlAccessType.FIELD)
public class SAPLib {

	@XmlElement(name = "jco3", namespace = Namespaces.SOA_REQ)
	private String jco3;

	@XmlElement(name = "jidoc", namespace = Namespaces.SOA_REQ)
	private String jidoc;

	public String getJco3() {
		return jco3;
	}

	public void setJco3(String jco3) {
		this.jco3 = jco3;
	}

	public String getJidoc() {
		return jidoc;
	}

	public void setJidoc(String jidoc) {
		this.jidoc = jidoc;
	}

}
