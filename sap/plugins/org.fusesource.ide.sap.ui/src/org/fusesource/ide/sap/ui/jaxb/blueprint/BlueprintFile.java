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
package org.fusesource.ide.sap.ui.jaxb.blueprint;

import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fusesource.ide.sap.ui.jaxb.SapConnectionConfiguration;

@XmlRootElement(name = "beans", namespace = "http://www.osgi.org/xmlns/blueprint/v1.0.0")
@XmlAccessorType(XmlAccessType.FIELD)
public class BlueprintFile {

	@XmlElement(name = "bean")
	private SapConnectionConfiguration sapConnectionConfiguration;

	public SapConnectionConfiguration getSapConnectionConfiguration() {
		return sapConnectionConfiguration;
	}

	public void setSapConnectionConfiguration(SapConnectionConfiguration sapConnectionConfiguration) {
		this.sapConnectionConfiguration = sapConnectionConfiguration;
	}

	public void marshal(OutputStream os) throws Exception {
		if (sapConnectionConfiguration != null) {
			JAXBContext context = JAXBContext.newInstance(BlueprintFile.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.osgi.org/xmlns/blueprint/v1.0.0 http://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd http://camel.apache.org/schema/blueprint http://camel.apache.org/schema/blueprint/camel-blueprint.xsd");
			m.marshal(this, os);
		}
	}

}
