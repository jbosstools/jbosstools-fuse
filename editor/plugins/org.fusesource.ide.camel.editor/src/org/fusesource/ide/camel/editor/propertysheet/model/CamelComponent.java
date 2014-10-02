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
package org.fusesource.ide.camel.editor.propertysheet.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author lhein
 */
@XmlRootElement(name="component")
public class CamelComponent {
    
    private String componentClass;
    private List<String> prefixes = new ArrayList<String>();
    private List<CamelComponentDependency> dependencies = new ArrayList<CamelComponentDependency>();
    private List<CamelComponentProperty> componentProperties = new ArrayList<CamelComponentProperty>();
    private List<CamelComponentUriParameter> camelComponentUriParameters = new ArrayList<CamelComponentUriParameter>();
    
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
     * @return the prefixes
     */
    @XmlElementWrapper(name = "prefixes")
    @XmlElement(name = "prefix")
    public List<String> getPrefixes() {
        return this.prefixes;
    }
    
    /**
     * @param prefixes the prefixes to set
     */
    public void setPrefixes(List<String> prefixes) {
        this.prefixes = prefixes;
    }
    
    /**
     * @return the dependencies
     */
    @XmlElementWrapper(name = "dependencies")
    @XmlElement(name = "dependency")
    public List<CamelComponentDependency> getDependencies() {
        return this.dependencies;
    }
    
    /**
     * @param dependencies the dependencies to set
     */
    public void setDependencies(List<CamelComponentDependency> dependencies) {
        this.dependencies = dependencies;
    }
    
    /**
     * @return the componentProperties
     */
    @XmlElementWrapper(name = "componentproperties")
    @XmlElement(name = "property")
    public List<CamelComponentProperty> getComponentProperties() {
        return this.componentProperties;
    }
    
    /**
     * @param componentProperties the componentProperties to set
     */
    public void setComponentProperties(List<CamelComponentProperty> componentProperties) {
        this.componentProperties = componentProperties;
    }
    
    /**
     * @return the camelComponentUriParameters
     */
    @XmlElementWrapper(name = "uriparameters")
    @XmlElement(name = "parameter")
    public List<CamelComponentUriParameter> getUriParameters() {
        return this.camelComponentUriParameters;
    }
    
    /**
     * @param camelComponentUriParameters the camelComponentUriParameters to set
     */
    public void setUriParameters(List<CamelComponentUriParameter> camelComponentUriParameters) {
        this.camelComponentUriParameters = camelComponentUriParameters;
    }
}
