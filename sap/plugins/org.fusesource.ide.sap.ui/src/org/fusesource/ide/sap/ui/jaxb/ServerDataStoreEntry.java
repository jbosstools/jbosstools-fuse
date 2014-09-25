/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.ui.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="entry")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerDataStoreEntry {

	@XmlAttribute(name="key")
	private String name;
	
	@XmlElement(name="bean")
	private ServerData value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ServerData getValue() {
		return value;
	}

	public void setValue(ServerData value) {
		this.value = value;
	}

}
