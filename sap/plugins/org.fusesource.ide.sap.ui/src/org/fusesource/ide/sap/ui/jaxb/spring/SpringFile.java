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
package org.fusesource.ide.sap.ui.jaxb.spring;

import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fusesource.ide.sap.ui.jaxb.SapConnectionConfiguration;

@XmlRootElement(name="beans", namespace="http://www.springframework.org/schema/beans")
@XmlAccessorType(XmlAccessType.FIELD)
public class SpringFile {
	
	@XmlElement(name="bean")
	private SapConnectionConfiguration sapConnectionConfiguration;
	
	public SapConnectionConfiguration getSapConnectionConfiguration() {
		return sapConnectionConfiguration;
	}

	public void setSapConnectionConfiguration(
			SapConnectionConfiguration sapConnectionConfiguration) {
		this.sapConnectionConfiguration = sapConnectionConfiguration;
	}

	public void marshal(OutputStream os) throws Exception {
		if (sapConnectionConfiguration != null) {
			JAXBContext context = JAXBContext.newInstance(SpringFile.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd");
			m.marshal(this, os);
		}
	}
	
}
