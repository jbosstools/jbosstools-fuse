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
package org.fusesource.ide.camel.model.catalog.components;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fusesource.ide.camel.model.Activator;
import org.fusesource.ide.camel.model.catalog.CamelModel;
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
	void setModel(CamelModel model) {
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
	 * creates the model from the given input stream and sets the parent model before it returns it
	 * 
	 * @param stream	the stream to parse
	 * @param parent	the parent model
	 * @return			the created model instance of null on errors
	 */
	public static ComponentModel getFactoryInstance(InputStream stream, CamelModel parent) {
		try {
			// create JAXB context and instantiate marshaller
		    JAXBContext context = JAXBContext.newInstance(ComponentModel.class, Component.class, ComponentDependency.class, ComponentProperty.class, UriParameter.class);
		    Unmarshaller um = context.createUnmarshaller();
		    ComponentModel model = (ComponentModel) um.unmarshal(new InputSource(stream));
		    model.setModel(parent);
		    return model;
		} catch (JAXBException ex) {
			Activator.getLogger().error(ex);
		}
		return null;
	}
}
