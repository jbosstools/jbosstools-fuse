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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author lhein
 */
@XmlRootElement(name = "parameter")
public class CamelComponentUriParameter {
    
    private String name;
    private String type;
    private String label;
    private String description;
    private String defaultValue;
    private CamelComponentUriParameterKind kind;
    private boolean mandatory;
    
    public CamelComponentUriParameter() {
        
    }
    
    /**
     * 
     * @param name
     * @param type
     * @param defaultValue
     * @param kind
     */
    public CamelComponentUriParameter(String name, String type, String defaultValue, CamelComponentUriParameterKind kind) {
        this(name, type, defaultValue, kind, false, "", "");
    }
    
    /**
     * 
     * @param name
     * @param type
     * @param defaultValue
     * @param kind
     * @param mandatory
     * @param label
     * @param description
     */
    public CamelComponentUriParameter(String name, String type, String defaultValue, CamelComponentUriParameterKind kind, boolean mandatory, String label, String description) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.kind = kind;
        this.mandatory = mandatory;
        this.label = label;
        this.description = description;
    }
    
    /**
     * @return the name
     */
    @XmlAttribute(name = "name")
    public String getName() {
        return this.name;
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the type
     */
    @XmlAttribute(name = "type")
    public String getType() {
        return this.type;
    }
    
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * @return the label
     */
    @XmlAttribute(name = "label")
    public String getLabel() {
        return this.label;
    }
    
    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * @return the description
     */
    @XmlAttribute(name = "description")
    public String getDescription() {
        return this.description;
    }
    
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * @return the defaultValue
     */
    @XmlAttribute(name = "defaultValue")
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    /**
     * @return the kind
     */
    @XmlAttribute(name = "kind")
    public CamelComponentUriParameterKind getKind() {
        return this.kind;
    }

    /**
     * @param kind the kind to set
     */
    public void setKind(CamelComponentUriParameterKind kind) {
        this.kind = kind;
    }

    /**
     * @return the mandatory
     */
    @XmlAttribute(name = "mandatory")
    public boolean isMandatory() {
        return this.mandatory;
    }

    /**
     * @param mandatory the mandatory to set
     */
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
}
