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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.jboss.mapper.model.dozer.config package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _RelationshipType_QNAME = new QName("http://dozer.sourceforge.net", "relationship-type");
    private final static QName _ClassA_QNAME = new QName("http://dozer.sourceforge.net", "class-a");
    private final static QName _BDeepIndexHint_QNAME = new QName("http://dozer.sourceforge.net", "b-deep-index-hint");
    private final static QName _ClassB_QNAME = new QName("http://dozer.sourceforge.net", "class-b");
    private final static QName _MapEmptyString_QNAME = new QName("http://dozer.sourceforge.net", "map-empty-string");
    private final static QName _A_QNAME = new QName("http://dozer.sourceforge.net", "a");
    private final static QName _Exception_QNAME = new QName("http://dozer.sourceforge.net", "exception");
    private final static QName _CopyByReference_QNAME = new QName("http://dozer.sourceforge.net", "copy-by-reference");
    private final static QName _TrimStrings_QNAME = new QName("http://dozer.sourceforge.net", "trim-strings");
    private final static QName _Wildcard_QNAME = new QName("http://dozer.sourceforge.net", "wildcard");
    private final static QName _AHint_QNAME = new QName("http://dozer.sourceforge.net", "a-hint");
    private final static QName _ADeepIndexHint_QNAME = new QName("http://dozer.sourceforge.net", "a-deep-index-hint");
    private final static QName _B_QNAME = new QName("http://dozer.sourceforge.net", "b");
    private final static QName _BeanFactory_QNAME = new QName("http://dozer.sourceforge.net", "bean-factory");
    private final static QName _MapNull_QNAME = new QName("http://dozer.sourceforge.net", "map-null");
    private final static QName _DateFormat_QNAME = new QName("http://dozer.sourceforge.net", "date-format");
    private final static QName _StopOnErrors_QNAME = new QName("http://dozer.sourceforge.net", "stop-on-errors");
    private final static QName _BHint_QNAME = new QName("http://dozer.sourceforge.net", "b-hint");
    private final static QName _Variable_QNAME = new QName("http://dozer.sourceforge.net", "variable");
    private final static QName _Converter_QNAME = new QName("http://dozer.sourceforge.net", "converter");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.jboss.mapper.model.dozer.config
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FieldExclude }
     * 
     */
    public FieldExclude createFieldExclude() {
        return new FieldExclude();
    }

    /**
     * Create an instance of {@link FieldDefinition }
     * 
     */
    public FieldDefinition createFieldDefinition() {
        return new FieldDefinition();
    }

    /**
     * Create an instance of {@link Mapping }
     * 
     */
    public Mapping createMapping() {
        return new Mapping();
    }

    /**
     * Create an instance of {@link Class }
     * 
     */
    public Class createClass() {
        return new Class();
    }

    /**
     * Create an instance of {@link Field }
     * 
     */
    public Field createField() {
        return new Field();
    }

    /**
     * Create an instance of {@link ConverterType }
     * 
     */
    public ConverterType createConverterType() {
        return new ConverterType();
    }

    /**
     * Create an instance of {@link Mappings }
     * 
     */
    public Mappings createMappings() {
        return new Mappings();
    }

    /**
     * Create an instance of {@link Configuration }
     * 
     */
    public Configuration createConfiguration() {
        return new Configuration();
    }

    /**
     * Create an instance of {@link CustomConverters }
     * 
     */
    public CustomConverters createCustomConverters() {
        return new CustomConverters();
    }

    /**
     * Create an instance of {@link CopyByReferences }
     * 
     */
    public CopyByReferences createCopyByReferences() {
        return new CopyByReferences();
    }

    /**
     * Create an instance of {@link AllowedExceptions }
     * 
     */
    public AllowedExceptions createAllowedExceptions() {
        return new AllowedExceptions();
    }

    /**
     * Create an instance of {@link Variables }
     * 
     */
    public Variables createVariables() {
        return new Variables();
    }

    /**
     * Create an instance of {@link Variable }
     * 
     */
    public Variable createVariable() {
        return new Variable();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Relationship }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "relationship-type")
    public JAXBElement<Relationship> createRelationshipType(Relationship value) {
        return new JAXBElement<Relationship>(_RelationshipType_QNAME, Relationship.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Class }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "class-a")
    public JAXBElement<Class> createClassA(Class value) {
        return new JAXBElement<Class>(_ClassA_QNAME, Class.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "b-deep-index-hint")
    public JAXBElement<String> createBDeepIndexHint(String value) {
        return new JAXBElement<String>(_BDeepIndexHint_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Class }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "class-b")
    public JAXBElement<Class> createClassB(Class value) {
        return new JAXBElement<Class>(_ClassB_QNAME, Class.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "map-empty-string")
    public JAXBElement<Boolean> createMapEmptyString(Boolean value) {
        return new JAXBElement<Boolean>(_MapEmptyString_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FieldDefinition }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "a")
    public JAXBElement<FieldDefinition> createA(FieldDefinition value) {
        return new JAXBElement<FieldDefinition>(_A_QNAME, FieldDefinition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "exception")
    public JAXBElement<String> createException(String value) {
        return new JAXBElement<String>(_Exception_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "copy-by-reference")
    public JAXBElement<String> createCopyByReference(String value) {
        return new JAXBElement<String>(_CopyByReference_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "trim-strings")
    public JAXBElement<Boolean> createTrimStrings(Boolean value) {
        return new JAXBElement<Boolean>(_TrimStrings_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "wildcard")
    public JAXBElement<Boolean> createWildcard(Boolean value) {
        return new JAXBElement<Boolean>(_Wildcard_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "a-hint")
    public JAXBElement<String> createAHint(String value) {
        return new JAXBElement<String>(_AHint_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "a-deep-index-hint")
    public JAXBElement<String> createADeepIndexHint(String value) {
        return new JAXBElement<String>(_ADeepIndexHint_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FieldDefinition }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "b")
    public JAXBElement<FieldDefinition> createB(FieldDefinition value) {
        return new JAXBElement<FieldDefinition>(_B_QNAME, FieldDefinition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "bean-factory")
    public JAXBElement<String> createBeanFactory(String value) {
        return new JAXBElement<String>(_BeanFactory_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "map-null")
    public JAXBElement<Boolean> createMapNull(Boolean value) {
        return new JAXBElement<Boolean>(_MapNull_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "date-format")
    public JAXBElement<String> createDateFormat(String value) {
        return new JAXBElement<String>(_DateFormat_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "stop-on-errors")
    public JAXBElement<Boolean> createStopOnErrors(Boolean value) {
        return new JAXBElement<Boolean>(_StopOnErrors_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "b-hint")
    public JAXBElement<String> createBHint(String value) {
        return new JAXBElement<String>(_BHint_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Variable }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "variable")
    public JAXBElement<Variable> createVariable(Variable value) {
        return new JAXBElement<Variable>(_Variable_QNAME, Variable.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConverterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dozer.sourceforge.net", name = "converter")
    public JAXBElement<ConverterType> createConverter(ConverterType value) {
        return new JAXBElement<ConverterType>(_Converter_QNAME, ConverterType.class, null, value);
    }

}
