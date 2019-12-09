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
package org.jboss.tools.fuse.transformation.core.dozer.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}stop-on-errors" minOccurs="0"/>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}date-format" minOccurs="0"/>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}wildcard" minOccurs="0"/>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}trim-strings" minOccurs="0"/>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}map-null" minOccurs="0"/>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}map-empty-string" minOccurs="0"/>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}bean-factory" minOccurs="0"/>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}relationship-type" minOccurs="0"/>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}custom-converters" minOccurs="0"/>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}copy-by-references" minOccurs="0"/>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}allowed-exceptions" minOccurs="0"/>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}variables" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "stopOnErrors",
    "dateFormat",
    "wildcard",
    "trimStrings",
    "mapNull",
    "mapEmptyString",
    "beanFactory",
    "relationshipType",
    "customConverters",
    "copyByReferences",
    "allowedExceptions",
    "variables"
})
@XmlRootElement(name = "configuration")
public class Configuration {

    @XmlElement(name = "stop-on-errors")
    protected Boolean stopOnErrors;
    @XmlElement(name = "date-format")
    protected String dateFormat;
    protected Boolean wildcard;
    @XmlElement(name = "trim-strings")
    protected Boolean trimStrings;
    @XmlElement(name = "map-null")
    protected Boolean mapNull;
    @XmlElement(name = "map-empty-string")
    protected Boolean mapEmptyString;
    @XmlElement(name = "bean-factory")
    protected String beanFactory;
    @XmlElement(name = "relationship-type")
    protected Relationship relationshipType;
    @XmlElement(name = "custom-converters")
    protected CustomConverters customConverters;
    @XmlElement(name = "copy-by-references")
    protected CopyByReferences copyByReferences;
    @XmlElement(name = "allowed-exceptions")
    protected AllowedExceptions allowedExceptions;
    protected Variables variables;

    /**
     * Gets the value of the stopOnErrors property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isStopOnErrors() {
        return stopOnErrors;
    }

    /**
     * Sets the value of the stopOnErrors property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setStopOnErrors(Boolean value) {
        this.stopOnErrors = value;
    }

    /**
     * Gets the value of the dateFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Sets the value of the dateFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateFormat(String value) {
        this.dateFormat = value;
    }

    /**
     * Gets the value of the wildcard property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWildcard() {
        return wildcard;
    }

    /**
     * Sets the value of the wildcard property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWildcard(Boolean value) {
        this.wildcard = value;
    }

    /**
     * Gets the value of the trimStrings property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTrimStrings() {
        return trimStrings;
    }

    /**
     * Sets the value of the trimStrings property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTrimStrings(Boolean value) {
        this.trimStrings = value;
    }

    /**
     * Gets the value of the mapNull property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMapNull() {
        return mapNull;
    }

    /**
     * Sets the value of the mapNull property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMapNull(Boolean value) {
        this.mapNull = value;
    }

    /**
     * Gets the value of the mapEmptyString property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMapEmptyString() {
        return mapEmptyString;
    }

    /**
     * Sets the value of the mapEmptyString property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMapEmptyString(Boolean value) {
        this.mapEmptyString = value;
    }

    /**
     * Gets the value of the beanFactory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBeanFactory() {
        return beanFactory;
    }

    /**
     * Sets the value of the beanFactory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeanFactory(String value) {
        this.beanFactory = value;
    }

    /**
     * Gets the value of the relationshipType property.
     * 
     * @return
     *     possible object is
     *     {@link Relationship }
     *     
     */
    public Relationship getRelationshipType() {
        return relationshipType;
    }

    /**
     * Sets the value of the relationshipType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Relationship }
     *     
     */
    public void setRelationshipType(Relationship value) {
        this.relationshipType = value;
    }

    /**
     * Gets the value of the customConverters property.
     * 
     * @return
     *     possible object is
     *     {@link CustomConverters }
     *     
     */
    public CustomConverters getCustomConverters() {
        return customConverters;
    }

    /**
     * Sets the value of the customConverters property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomConverters }
     *     
     */
    public void setCustomConverters(CustomConverters value) {
        this.customConverters = value;
    }

    /**
     * Gets the value of the copyByReferences property.
     * 
     * @return
     *     possible object is
     *     {@link CopyByReferences }
     *     
     */
    public CopyByReferences getCopyByReferences() {
        return copyByReferences;
    }

    /**
     * Sets the value of the copyByReferences property.
     * 
     * @param value
     *     allowed object is
     *     {@link CopyByReferences }
     *     
     */
    public void setCopyByReferences(CopyByReferences value) {
        this.copyByReferences = value;
    }

    /**
     * Gets the value of the allowedExceptions property.
     * 
     * @return
     *     possible object is
     *     {@link AllowedExceptions }
     *     
     */
    public AllowedExceptions getAllowedExceptions() {
        return allowedExceptions;
    }

    /**
     * Sets the value of the allowedExceptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link AllowedExceptions }
     *     
     */
    public void setAllowedExceptions(AllowedExceptions value) {
        this.allowedExceptions = value;
    }

    /**
     * Gets the value of the variables property.
     * 
     * @return
     *     possible object is
     *     {@link Variables }
     *     
     */
    public Variables getVariables() {
        return variables;
    }

    /**
     * Sets the value of the variables property.
     * 
     * @param value
     *     allowed object is
     *     {@link Variables }
     *     
     */
    public void setVariables(Variables value) {
        this.variables = value;
    }

}
