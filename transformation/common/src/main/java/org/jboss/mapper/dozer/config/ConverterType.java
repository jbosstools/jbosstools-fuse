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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for converter-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="converter-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://dozer.sourceforge.net}class-a"/>
 *         &lt;element ref="{http://dozer.sourceforge.net}class-b"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "converter-type", propOrder = {
    "classA",
    "classB"
})
public class ConverterType {

    @XmlElement(name = "class-a", required = true)
    protected Class classA;
    @XmlElement(name = "class-b", required = true)
    protected Class classB;
    @XmlAttribute(name = "type", required = true)
    protected String type;

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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

}
