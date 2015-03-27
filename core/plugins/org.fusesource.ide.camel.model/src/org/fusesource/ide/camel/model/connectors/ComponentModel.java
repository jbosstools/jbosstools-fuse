/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.connectors;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fusesource.ide.camel.model.Activator;
import org.xml.sax.InputSource;

/**
 * @author lhein
 */
@XmlRootElement(name="components")
public class ComponentModel {
	
	private ArrayList<Component> supportedComponents;
	private String camelVersion;
	
	/**
	 * @return the camelVersion
	 */
	public String getCamelVersion() {
		return this.camelVersion;
	}
	
	/**
	 * @param camelVersion the camelVersion to set
	 */
	public void setCamelVersion(String camelVersion) {
		this.camelVersion = camelVersion;
	}
	
	/**
	 * @return the supportedComponents
	 */
	@XmlElement(name = "component")
	public ArrayList<Component> getSupportedComponents() {
		return this.supportedComponents;
	}
	
	/**
	 * @param supportedComponents the supportedComponents to set
	 */
	public void setSupportedComponents(ArrayList<Component> supportedComponents) {
		this.supportedComponents = supportedComponents;
	}
		
	/**
	 * looks up the connector which supports the given protocol prefix
	 * 
	 * @param scheme
	 * @return
	 */
	public Component getComponentForScheme(String scheme) {
	    for (Component c : supportedComponents) {
            if (c.supportsScheme(scheme)) return c;
        }
        return null;
	}
	
	/**
	 * creates the backlog tracer event message for a given xml dump
	 * 
	 * @param stream	the xml stream
	 * @return	the message object or null on errors
	 */
	public static ComponentModel getComponentFactoryInstance(InputStream stream, String camelVersion) {
		try {
			// create JAXB context and instantiate marshaller
		    JAXBContext context = JAXBContext.newInstance(ComponentModel.class, Component.class, ComponentDependency.class, ComponentProperty.class, UriParameter.class);
		    Unmarshaller um = context.createUnmarshaller();
		    ComponentModel model = (ComponentModel) um.unmarshal(new InputSource(stream));
		    model.setCamelVersion(camelVersion);
		    return model;
		} catch (JAXBException ex) {
			Activator.getLogger().error(ex);
		}
		return null;
	}
}
