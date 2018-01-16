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
package org.jboss.tools.fuse.transformation.core.dozer.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}a"/>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}b"/>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}a-hint" minOccurs="0"/>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}b-hint" minOccurs="0"/>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}a-deep-index-hint" minOccurs="0"/>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}b-deep-index-hint" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="relationship-type" type="{http://dozermapper.github.io/schema/bean-mapping}relationship" />
 *       &lt;attribute name="remove-orphans" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="type" type="{http://dozermapper.github.io/schema/bean-mapping}type" />
 *       &lt;attribute name="map-id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="copy-by-reference" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="custom-converter" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="custom-converter-id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="custom-converter-param" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "a",
    "b",
    "aHint",
    "bHint",
    "aDeepIndexHint",
    "bDeepIndexHint"
})
@XmlRootElement(name = "field")
public class Field {

    @XmlElement(required = true)
    protected FieldDefinition a;
    @XmlElement(required = true)
    protected FieldDefinition b;
    @XmlElement(name = "a-hint")
    protected String aHint;
    @XmlElement(name = "b-hint")
    protected String bHint;
    @XmlElement(name = "a-deep-index-hint")
    protected String aDeepIndexHint;
    @XmlElement(name = "b-deep-index-hint")
    protected String bDeepIndexHint;
    @XmlAttribute(name = "relationship-type")
    protected Relationship relationshipType;
    @XmlAttribute(name = "remove-orphans")
    protected Boolean removeOrphans;
    @XmlAttribute(name = "type")
    protected Type type;
    @XmlAttribute(name = "map-id")
    protected String mapId;
    @XmlAttribute(name = "copy-by-reference")
    protected Boolean copyByReference;
    @XmlAttribute(name = "custom-converter")
    protected String customConverter;
    @XmlAttribute(name = "custom-converter-id")
    protected String customConverterId;
    @XmlAttribute(name = "custom-converter-param")
    protected String customConverterParam;

    /**
     * Gets the value of the a property.
     * 
     * @return
     *     possible object is
     *     {@link FieldDefinition }
     *     
     */
    public FieldDefinition getA() {
        return a;
    }

    /**
     * Sets the value of the a property.
     * 
     * @param value
     *     allowed object is
     *     {@link FieldDefinition }
     *     
     */
    public void setA(FieldDefinition value) {
        this.a = value;
    }

    /**
     * Gets the value of the b property.
     * 
     * @return
     *     possible object is
     *     {@link FieldDefinition }
     *     
     */
    public FieldDefinition getB() {
        return b;
    }

    /**
     * Sets the value of the b property.
     * 
     * @param value
     *     allowed object is
     *     {@link FieldDefinition }
     *     
     */
    public void setB(FieldDefinition value) {
        this.b = value;
    }

    /**
     * Gets the value of the aHint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAHint() {
        return aHint;
    }

    /**
     * Sets the value of the aHint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAHint(String value) {
        this.aHint = value;
    }

    /**
     * Gets the value of the bHint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBHint() {
        return bHint;
    }

    /**
     * Sets the value of the bHint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBHint(String value) {
        this.bHint = value;
    }

    /**
     * Gets the value of the aDeepIndexHint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getADeepIndexHint() {
        return aDeepIndexHint;
    }

    /**
     * Sets the value of the aDeepIndexHint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setADeepIndexHint(String value) {
        this.aDeepIndexHint = value;
    }

    /**
     * Gets the value of the bDeepIndexHint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBDeepIndexHint() {
        return bDeepIndexHint;
    }

    /**
     * Sets the value of the bDeepIndexHint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBDeepIndexHint(String value) {
        this.bDeepIndexHint = value;
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
     * Gets the value of the removeOrphans property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRemoveOrphans() {
        return removeOrphans;
    }

    /**
     * Sets the value of the removeOrphans property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRemoveOrphans(Boolean value) {
        this.removeOrphans = value;
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

    /**
     * Gets the value of the copyByReference property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCopyByReference() {
        return copyByReference;
    }

    /**
     * Sets the value of the copyByReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCopyByReference(Boolean value) {
        this.copyByReference = value;
    }

    /**
     * Gets the value of the customConverter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomConverter() {
        return customConverter;
    }

    /**
     * Sets the value of the customConverter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomConverter(String value) {
        this.customConverter = value;
    }

    /**
     * Gets the value of the customConverterId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomConverterId() {
        return customConverterId;
    }

    /**
     * Sets the value of the customConverterId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomConverterId(String value) {
        this.customConverterId = value;
    }

    /**
     * Gets the value of the customConverterParam property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomConverterArgument() {
        return customConverterParam;
    }

    /**
     * Sets the value of the customConverterParam property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomConverterArgument(String value) {
        this.customConverterParam = value;
    }

}
