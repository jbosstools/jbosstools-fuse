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

@XmlRootElement(name="bean")
@XmlAccessorType(XmlAccessType.FIELD)
public class SapConnectionConfiguration {
	
	@XmlAttribute(name="id")
	private static final String id = "sap-configuration";
	
	@XmlAttribute(name="class")
	private static final String clazz = "org.fusesource.camel.component.sap.SapConnectionConfiguration";

	@XmlElement(name="property")
	private DestinationDataStore destinationDataStore = new DestinationDataStore();

	@XmlElement(name="property")
	private ServerDataStore serverDataStore = new ServerDataStore();

	public DestinationDataStore getDestinationDataStore() {
		return destinationDataStore;
	}

	public ServerDataStore getServerDataStore() {
		return serverDataStore;
	}

}
