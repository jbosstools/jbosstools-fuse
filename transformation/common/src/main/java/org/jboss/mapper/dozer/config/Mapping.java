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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
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
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://dozer.sourceforge.net}class-a"/>
 *         &lt;element ref="{http://dozer.sourceforge.net}class-b"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://dozer.sourceforge.net}field" maxOccurs="unbounded"/>
 *           &lt;element ref="{http://dozer.sourceforge.net}field-exclude" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="date-format" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="stop-on-errors" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="wildcard" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="trim-strings" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="map-null" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="map-empty-string" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="bean-factory" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{http://dozer.sourceforge.net}type" />
 *       &lt;attribute name="relationship-type" type="{http://dozer.sourceforge.net}relationship" />
 *       &lt;attribute name="map-id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "classA",
    "classB",
    "fieldOrFieldExclude"
})
@XmlRootElement(name = "mapping")
public class Mapping {

    @XmlElement(name = "class-a")
    protected Class classA;
    @XmlElement(name = "class-b")
    protected Class classB;
    @XmlElements({
        @XmlElement(name = "field", type = Field.class),
        @XmlElement(name = "field-exclude", type = FieldExclude.class)
    })
    protected List<Object> fieldOrFieldExclude;
    @XmlAttribute(name = "date-format")
    protected String dateFormat;
    @XmlAttribute(name = "stop-on-errors")
    protected Boolean stopOnErrors;
    @XmlAttribute(name = "wildcard")
    protected Boolean wildcard;
    @XmlAttribute(name = "trim-strings")
    protected Boolean trimStrings;
    @XmlAttribute(name = "map-null")
    protected Boolean mapNull;
    @XmlAttribute(name = "map-empty-string")
    protected Boolean mapEmptyString;
    @XmlAttribute(name = "bean-factory")
    protected String beanFactory;
    @XmlAttribute(name = "type")
    protected Type type;
    @XmlAttribute(name = "relationship-type")
    protected Relationship relationshipType;
    @XmlAttribute(name = "map-id")
    protected String mapId;

    /**
     * Gets the value of the classA property.
     * 
     * @return
     *     possible object is
     *     {@link Class }
     *     
     */
    public Class getClassA() {
        return classA;
    }

    /**
     * Sets the value of the classA property.
     * 
     * @param value
     *     allowed object is
     *     {@link Class }
     *     
     */
    public void setClassA(Class value) {
        this.classA = value;
    }

    /**
     * Gets the value of the classB property.
     * 
     * @return
     *     possible object is
     *     {@link Class }
     *     
     */
    public Class getClassB() {
        return classB;
    }

    /**
     * Sets the value of the classB property.
     * 
     * @param value
     *     allowed object is
     *     {@link Class }
     *     
     */
    public void setClassB(Class value) {
        this.classB = value;
    }

    /**
     * Gets the value of the fieldOrFieldExclude property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fieldOrFieldExclude property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFieldOrFieldExclude().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Field }
     * {@link FieldExclude }
     * 
     * 
     */
    public List<Object> getFieldOrFieldExclude() {
        if (fieldOrFieldExclude == null) {
            fieldOrFieldExclude = new ArrayList<Object>();
        }
        return this.fieldOrFieldExclude;
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link Type }
     *     
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link Type }
     *     
     */
    public void setType(Type value) {
        this.type = value;
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
     * Gets the value of the mapId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMapId() {
        return mapId;
    }

    /**
     * Sets the value of the mapId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMapId(String value) {
        this.mapId = value;
    }

}
