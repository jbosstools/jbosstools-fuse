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
package org.fusesource.ide.camel.model.service.core.catalog.components;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.xml.sax.InputSource;

/**
 * @author lhein
 */
@XmlRootElement(name="components")
public class ComponentModel {

	private CamelModel model;
	private ArrayList<Component> supportedComponents;
	
	/**
	 * @return the model
	 */
	public CamelModel getModel() {
		return this.model;
	}
	
	/**
	 * @param model the model to set
	 */
	public void setModel(CamelModel model) {
		this.model = model;
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
	 * creates the model from the given input stream 
	 * 
	 * @param stream	the stream to parse
	 * @return			the created model instance of null on errors
	 */
	public static ComponentModel getXMLFactoryInstance(InputStream stream) {
		try {
			// create JAXB context and instantiate marshaller
		    JAXBContext context = JAXBContext.newInstance(ComponentModel.class, Component.class, Dependency.class, ComponentProperty.class, Parameter.class);
		    Unmarshaller um = context.createUnmarshaller();
		    ComponentModel model = (ComponentModel) um.unmarshal(new InputSource(stream));
		    return model;
		} catch (JAXBException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
		}
		return null;
	}
}
