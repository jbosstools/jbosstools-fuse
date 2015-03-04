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
 *         Specifies one of the classes in the mapping definition. All Mapping definitions are bi-directional by default.
 *         Global configuration and Mapping element values are inherited
 * 
 *         Required Attributes:
 * 
 *         Optional Attributes:
 * 
 *         bean-factory The factory class to create data objects. This typically will not be specified.
 *         By default Dozer constructs new instances of data objects by invoking the no-arg constructor
 *         
 *         factory-bean-id The id passed to the specified bean factory
 *         
 *         map-set-method For Map backed objects, this indicates which setter method should be used to retrieve field
 *         values. This should only be used of Map backed objects.
 *         
 *         map-get-method For Map backed objects, this indicates which getter method should be used to retrieve field values.
 *         This should only be used of Map backed objects.
 *         
 *         create-method Which method to invoke to create a new instance of the class. This is typically not specified.
 *         By default, the no arg constructor(public or private) is used
 *         
 *         map-null Indicates whether null values are mapped. The default value is "true"
 *         
 *         map-empty-string Indicates whether empty string values are mapped. The default value is "true"
 * 
 *         is-accessible Indicates whether Dozer bypasses getter/setter methods and accesses the field directly. This will typically be set to "false". The default value is "false". If set to "true", the
 *         getter/setter methods will NOT be invoked. You would want to set this to "true" if the field is lacking a getter or setter method.
 *       
 * 
 * <p>Java class for class complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="class">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="bean-factory" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="factory-bean-id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="map-set-method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="map-get-method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="create-method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="map-null" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="map-empty-string" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="is-accessible" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "class", propOrder = {
    "content"
})
public class Class {

    @XmlValue
    protected String content;
    @XmlAttribute(name = "bean-factory")
    protected String beanFactory;
    @XmlAttribute(name = "factory-bean-id")
    protected String factoryBeanId;
    @XmlAttribute(name = "map-set-method")
    protected String mapSetMethod;
    @XmlAttribute(name = "map-get-method")
    protected String mapGetMethod;
    @XmlAttribute(name = "create-method")
    protected String createMethod;
    @XmlAttribute(name = "map-null")
    protected Boolean mapNull;
    @XmlAttribute(name = "map-empty-string")
    protected Boolean mapEmptyString;
    @XmlAttribute(name = "is-accessible")
    protected Boolean isAccessible;

    /**
     * 
     *         Specifies one of the classes in the mapping definition. All Mapping definitions are bi-directional by default.
     *         Global configuration and Mapping element values are inherited
     * 
     *         Required Attributes:
     * 
     *         Optional Attributes:
     * 
     *         bean-factory The factory class to create data objects. This typically will not be specified.
     *         By default Dozer constructs new instances of data objects by invoking the no-arg constructor
     *         
     *         factory-bean-id The id passed to the specified bean factory
     *         
     *         map-set-method For Map backed objects, this indicates which setter method should be used to retrieve field
     *         values. This should only be used of Map backed objects.
     *         
     *         map-get-method For Map backed objects, this indicates which getter method should be used to retrieve field values.
     *         This should only be used of Map backed objects.
     *         
     *         create-method Which method to invoke to create a new instance of the class. This is typically not specified.
     *         By default, the no arg constructor(public or private) is used
     *         
     *         map-null Indicates whether null values are mapped. The default value is "true"
     *         
     *         map-empty-string Indicates whether empty string values are mapped. The default value is "true"
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
     * Gets the value of the factoryBeanId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFactoryBeanId() {
        return factoryBeanId;
    }

    /**
     * Sets the value of the factoryBeanId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFactoryBeanId(String value) {
        this.factoryBeanId = value;
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

}
