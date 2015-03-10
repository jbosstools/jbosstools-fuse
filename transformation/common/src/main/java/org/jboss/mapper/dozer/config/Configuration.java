/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.mapper.dozer.config;

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
 *         &lt;element ref="{http://dozer.sourceforge.net}stop-on-errors" minOccurs="0"/>
 *         &lt;element ref="{http://dozer.sourceforge.net}date-format" minOccurs="0"/>
 *         &lt;element ref="{http://dozer.sourceforge.net}wildcard" minOccurs="0"/>
 *         &lt;element ref="{http://dozer.sourceforge.net}trim-strings" minOccurs="0"/>
 *         &lt;element ref="{http://dozer.sourceforge.net}map-null" minOccurs="0"/>
 *         &lt;element ref="{http://dozer.sourceforge.net}map-empty-string" minOccurs="0"/>
 *         &lt;element ref="{http://dozer.sourceforge.net}bean-factory" minOccurs="0"/>
 *         &lt;element ref="{http://dozer.sourceforge.net}relationship-type" minOccurs="0"/>
 *         &lt;element ref="{http://dozer.sourceforge.net}custom-converters" minOccurs="0"/>
 *         &lt;element ref="{http://dozer.sourceforge.net}copy-by-references" minOccurs="0"/>
 *         &lt;element ref="{http://dozer.sourceforge.net}allowed-exceptions" minOccurs="0"/>
 *         &lt;element ref="{http://dozer.sourceforge.net}variables" minOccurs="0"/>
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
