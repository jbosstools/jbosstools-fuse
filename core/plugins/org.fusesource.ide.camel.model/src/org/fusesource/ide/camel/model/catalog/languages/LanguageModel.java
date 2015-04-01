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
package org.fusesource.ide.camel.model.catalog.languages;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fusesource.ide.camel.model.Activator;
import org.fusesource.ide.camel.model.catalog.CamelModel;
import org.fusesource.ide.camel.model.catalog.Dependency;
import org.fusesource.ide.camel.model.catalog.Parameter;
import org.xml.sax.InputSource;

/**
 * @author lhein
 */
@XmlRootElement(name="languages")
public class LanguageModel {

	private CamelModel model;
	private ArrayList<Language> supportedLanguages;
	
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
	 * @return the supportedLanguages
	 */
	@XmlElement(name = "language")
	public ArrayList<Language> getSupportedLanguages() {
		return this.supportedLanguages;
	}
	
	/**
	 * @param supportedLanguages the supportedLanguages to set
	 */
	public void setSupportedLanguages(ArrayList<Language> supportedLanguages) {
		this.supportedLanguages = supportedLanguages;
	}
		
	/**
	 * creates the model from the given input stream and sets the parent model before it returns it
	 * 
	 * @param stream	the stream to parse
	 * @param parent	the parent model
	 * @return			the created model instance of null on errors
	 */
	public static LanguageModel getFactoryInstance(InputStream stream, CamelModel parent) {
		try {
			// create JAXB context and instantiate marshaller
		    JAXBContext context = JAXBContext.newInstance(LanguageModel.class, Language.class, Dependency.class, Parameter.class);
		    Unmarshaller um = context.createUnmarshaller();
		    LanguageModel model = (LanguageModel) um.unmarshal(new InputSource(stream));
		    model.setModel(parent);
		    return model;
		} catch (JAXBException ex) {
			Activator.getLogger().error(ex);
		}
		return null;
	}
}
