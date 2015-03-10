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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * 
 *         Specifies one of the fields in the field mapping definition. Global configuration, mapping, class, and field
 *         element values are inherited
 * 
 *         Required Attributes:
 * 
 *         Optional Attributes:
 * 
 *         date-format The string format of Date fields. This is used for field mapping between Strings and Dates
 *         
 *         set-method Indicates which set method to invoke when setting the destination value. Typically this will not be specified.
 *         By default, the beans attribute setter is used.
 *         
 *         get-method Indicates which get method to invoke on the src object to get the field value Typically this will not be specified.
 *         By default, the beans attribute getter is used.
 *         
 *         is-accessible Indicates whether Dozer bypasses getter/setter methods and accesses the field directly. This will typically be set to "false". The default value is "false". If set to "true", the
 *         getter/setter methods will NOT be invoked. You would want to set this to "true" if the field is lacking a getter or setter method.
 *       
 * 
 * <p>Java class for fieldDefinition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fieldDefinition">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="date-format" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{http://dozer.sourceforge.net}field-type" />
 *       &lt;attribute name="set-method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="get-method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="key" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="map-set-method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="map-get-method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="is-accessible" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="create-method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fieldDefinition", propOrder = {
    "content"
})
public class FieldDefinition {

    @XmlValue
    protected String content;
    @XmlAttribute(name = "date-format")
    protected String dateFormat;
    @XmlAttribute(name = "type")
    protected FieldType type;
    @XmlAttribute(name = "set-method")
    protected String setMethod;
    @XmlAttribute(name = "get-method")
    protected String getMethod;
    @XmlAttribute(name = "key")
    protected String key;
    @XmlAttribute(name = "map-set-method")
    protected String mapSetMethod;
    @XmlAttribute(name = "map-get-method")
    protected String mapGetMethod;
    @XmlAttribute(name = "is-accessible")
    protected Boolean isAccessible;
    @XmlAttribute(name = "create-method")
    protected String createMethod;

    /**
     * 
     *         Specifies one of the fields in the field mapping definition. Global configuration, mapping, class, and field
     *         element values are inherited
     * 
     *         Required Attributes:
     * 
     *         Optional Attributes:
     * 
     *         date-format The string format of Date fields. This is used for field mapping between Strings and Dates
     *         
     *         set-method Indicates which set method to invoke when setting the destination value. Typically this will not be specified.
     *         By default, the beans attribute setter is used.
     *         
     *         get-method Indicates which get method to invoke on the src object to get the field value Typically this will not be specified.
     *         By default, the beans attribute getter is used.
     *         
     *         is-accessible Indicates whether Dozer bypasses getter/setter methods and accesses the field directly. This will typically be set to "false". The default value is "false". If set to "true", the
     *         getter/setter methods will NOT be invoked. You would want to set this to "true" if the field is lacking a getter or setter method.
     *       
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContent(String value) {
        this.content = value;
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link FieldType }
     *     
     */
    public FieldType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link FieldType }
     *     
     */
    public void setType(FieldType value) {
        this.type = value;
    }

    /**
     * Gets the value of the setMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSetMethod() {
        return setMethod;
    }

    /**
     * Sets the value of the setMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSetMethod(String value) {
        this.setMethod = value;
    }

    /**
     * Gets the value of the getMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetMethod() {
        return getMethod;
    }

    /**
     * Sets the value of the getMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetMethod(String value) {
        this.getMethod = value;
    }

    /**
     * Gets the value of the key property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKey(String value) {
        this.key = value;
    }

    /**
     * Gets the value of the mapSetMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMapSetMethod() {
        return mapSetMethod;
    }

    /**
     * Sets the value of the mapSetMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMapSetMethod(String value) {
        this.mapSetMethod = value;
    }

    /**
     * Gets the value of the mapGetMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMapGetMethod() {
        return mapGetMethod;
    }

    /**
     * Sets the value of the mapGetMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMapGetMethod(String value) {
        this.mapGetMethod = value;
    }

    /**
     * Gets the value of the isAccessible property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsAccessible() {
        return isAccessible;
    }

    /**
     * Sets the value of the isAccessible property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsAccessible(Boolean value) {
        this.isAccessible = value;
    }

    /**
     * Gets the value of the createMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreateMethod() {
        return createMethod;
    }

    /**
     * Sets the value of the createMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreateMethod(String value) {
        this.createMethod = value;
    }

}
