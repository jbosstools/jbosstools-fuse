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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author lhein
 */
@XmlRootElement(name = "component")
public class Connector {
	
	private String id;
	private String componentClass;
	private ArrayList<ConnectorProtocol> protocols;
	private ArrayList<ConnectorDependency> dependencies;
	
	/**
     * @return the componentClass
     */
	@XmlElement(name = "class")
    public String getComponentClass() {
        return this.componentClass;
    }
    
    /**
     * @param componentClass the componentClass to set
     */
    public void setComponentClass(String componentClass) {
        this.componentClass = componentClass;
    }
	
	/**
	 * @return the id
	 */
    @XmlElement(name = "id")
	public String getId() {
		return this.id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the protocols
	 */
	@XmlElementWrapper(name = "prefixes")
	@XmlElement(name = "prefix")
	public ArrayList<ConnectorProtocol> getProtocols() {
	    if (this.protocols != null) {
    		Collections.sort(this.protocols, new Comparator<ConnectorProtocol>() {
    		    /* (non-Javadoc)
    		     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    		     */
    		    @Override
    		    public int compare(ConnectorProtocol o1, ConnectorProtocol o2) {
    		        return o1.getPrefix().compareTo(o2.getPrefix());
    		    }
    		});
	    }
	    return this.protocols;
	}
	
	/**
	 * @param protocols the protocols to set
	 */
	public void setProtocols(ArrayList<ConnectorProtocol> protocols) {
		this.protocols = protocols;
	}
	
	/**
	 * @return the dependency
	 */
	@XmlElementWrapper(name = "dependencies")
	@XmlElement(name = "dependency")
	public ArrayList<ConnectorDependency> getDependencies() {
		return this.dependencies;
	}
	
	/**
	 * @param dependency the dependency to set
	 */
	public void setDependencies(ArrayList<ConnectorDependency> dependencies) {
		this.dependencies = dependencies;
	}
	
	/**
	 * checks if the connector can handle the given protocol
	 * 
	 * @param protocol
	 * @return
	 */
	public boolean supportsProtocol(String protocol) {
	    for (ConnectorProtocol p : protocols) {
	        if (p.getPrefix().equalsIgnoreCase(protocol)) return true;
	    }
	    return false;
	}
}
