/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.foundation.ui.archetypes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "archetypes")
public class Archetypes {

    private List<Archetype> archetypes = new ArrayList<Archetype>();

	/**
	 * @return the archetypes
	 */
    @XmlElement(name = "archetype")
	public List<Archetype> getArchetypes() {
		return this.archetypes;
	}
	
	/**
	 * @param archetypes the archetypes to set
	 */
	public void setArchetypes(List<Archetype> archetypes) {
		this.archetypes = archetypes;
	}
	
	/**
	 * 
	 * @param a
	 */
    public void add(Archetype a) {
        archetypes.add(a);
    }

    public static JAXBContext newJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(Archetypes.class, Archetype.class);
    }

    public static Marshaller newMarshaller() throws JAXBException {
        Marshaller m = newJaxbContext().createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return m;
    }

    public static Unmarshaller newUnmarshaller() throws JAXBException {
        return newJaxbContext().createUnmarshaller();
    }

 

}
