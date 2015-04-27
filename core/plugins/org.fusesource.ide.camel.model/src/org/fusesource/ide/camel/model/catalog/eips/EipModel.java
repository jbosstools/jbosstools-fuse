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
package org.fusesource.ide.camel.model.catalog.eips;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fusesource.ide.camel.model.Activator;
import org.fusesource.ide.camel.model.catalog.CamelModel;
import org.fusesource.ide.camel.model.catalog.Parameter;
import org.xml.sax.InputSource;

/**
 * @author lhein
 */
@XmlRootElement(name="eips")
public class EipModel {

	private CamelModel model;
	private ArrayList<Eip> supportedEIPs;
	
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
	 * @return the supportedEIPs
	 */
	@XmlElement(name = "eip")
	public ArrayList<Eip> getSupportedEIPs() {
		return this.supportedEIPs;
	}
	
	/**
	 * @param supportedEIPs the supportedEIPs to set
	 */
	public void setSupportedEIPs(ArrayList<Eip> supportedEIPs) {
		this.supportedEIPs = supportedEIPs;
	}
	
	/**
	 * returns the EIP for the given class or null if not found
	 * 
	 * @param className
	 * @return	the eip or null if not found
	 */
	public Eip getEIPByClass(String className) {
		for (Eip eip : getSupportedEIPs()) {
			if (eip.getName().equalsIgnoreCase(className)) {
				return eip;
			}
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
	public static EipModel getFactoryInstance(InputStream stream, CamelModel parent) {
		try {
			// create JAXB context and instantiate marshaller
		    JAXBContext context = JAXBContext.newInstance(EipModel.class, Eip.class, Parameter.class);
		    Unmarshaller um = context.createUnmarshaller();
		    EipModel model = (EipModel) um.unmarshal(new InputSource(stream));
		    model.setModel(parent);
		    return model;
		} catch (JAXBException ex) {
			Activator.getLogger().error(ex);
		}
		return null;
	}
}
