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
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}class-a"/>
 *         &lt;element ref="{http://dozermapper.github.io/schema/bean-mapping}class-b"/>
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
