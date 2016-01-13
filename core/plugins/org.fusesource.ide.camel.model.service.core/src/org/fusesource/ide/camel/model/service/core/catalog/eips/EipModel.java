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
package org.fusesource.ide.camel.model.service.core.catalog.eips;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
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
	public void setModel(CamelModel model) {
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
			if (eip.getClazz().equalsIgnoreCase(className)) {
				return eip;
			}
		}
		return null;
	}
	
	/**
	 * returns the eip with the given name
	 * 
	 * @param eipName
	 * @return the eip with the name or null if not found
	 */
	public Eip getEIPByName(String eipName) {
		for (Eip eip : getSupportedEIPs()) {
			if (eip.getName().equalsIgnoreCase(eipName)) {
				return eip;
			}
		}
		return null;
	}
		
	/**
	 * creates the model from the given input stream 
	 * 
	 * @param stream	the stream to parse
	 * @return			the created model instance of null on errors
	 */
	public static EipModel getXMLFactoryInstance(InputStream stream) {
		try {
			// create JAXB context and instantiate marshaller
		    JAXBContext context = JAXBContext.newInstance(EipModel.class, Eip.class, Parameter.class);
		    Unmarshaller um = context.createUnmarshaller();
		    EipModel model = (EipModel) um.unmarshal(new InputSource(stream));
		    return model;
		} catch (JAXBException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
		}
		return null;
	}
}
