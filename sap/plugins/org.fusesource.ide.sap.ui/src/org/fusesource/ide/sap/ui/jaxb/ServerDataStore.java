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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="property")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerDataStore {

	@XmlAttribute(name="name")
	private static final String name = "serverDataStore";
	
	@XmlElementWrapper(name="map")
	@XmlElements(
			@XmlElement(name="entry")
	)
	private List<ServerDataStoreEntry> entries = new ArrayList<ServerDataStoreEntry>();
	
	public ServerData add(String name) {
		ServerData serverData = new ServerData();
		ServerDataStoreEntry entry = new ServerDataStoreEntry();
		
		entry.setName(name);
		entry.setValue(serverData);
		entries.add(entry);

		return serverData;
	}
	
	List<ServerDataStoreEntry> getEntries() {
		return entries;
	}
	
}
