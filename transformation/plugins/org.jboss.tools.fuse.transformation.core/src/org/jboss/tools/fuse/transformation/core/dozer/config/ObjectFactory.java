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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the 
 * org.jboss.tools.fuse.transformation.core.model.dozer.config package. 
 * <p>An ObjectFactory allows you to programmatically 
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

    private static final String DOZER_NAMESPACE = "http://dozer.sourceforge.net"; //$NON-NLS-1$
    private static final QName _RelationshipType_QNAME = new QName(DOZER_NAMESPACE, "relationship-type"); //$NON-NLS-1$
    private static final QName _ClassA_QNAME = new QName(DOZER_NAMESPACE, "class-a"); //$NON-NLS-1$
    private static final QName _BDeepIndexHint_QNAME = new QName(DOZER_NAMESPACE, "b-deep-index-hint"); //$NON-NLS-1$
    private static final QName _ClassB_QNAME = new QName(DOZER_NAMESPACE, "class-b"); //$NON-NLS-1$
    private static final QName _MapEmptyString_QNAME = new QName(DOZER_NAMESPACE, "map-empty-string"); //$NON-NLS-1$
    private static final QName _A_QNAME = new QName(DOZER_NAMESPACE, "a"); //$NON-NLS-1$
    private static final QName _Exception_QNAME = new QName(DOZER_NAMESPACE, "exception"); //$NON-NLS-1$
    private static final QName _CopyByReference_QNAME = new QName(DOZER_NAMESPACE, "copy-by-reference"); //$NON-NLS-1$
    private static final QName _TrimStrings_QNAME = new QName(DOZER_NAMESPACE, "trim-strings"); //$NON-NLS-1$
    private static final QName _Wildcard_QNAME = new QName(DOZER_NAMESPACE, "wildcard"); //$NON-NLS-1$ 
    private static final QName _AHint_QNAME = new QName(DOZER_NAMESPACE, "a-hint"); //$NON-NLS-1$
    private static final QName _ADeepIndexHint_QNAME = new QName(DOZER_NAMESPACE, "a-deep-index-hint"); //$NON-NLS-1$
    private static final QName _B_QNAME = new QName(DOZER_NAMESPACE, "b"); //$NON-NLS-1$
    private static final QName _BeanFactory_QNAME = new QName(DOZER_NAMESPACE, "bean-factory"); //$NON-NLS-1$
    private static final QName _MapNull_QNAME = new QName(DOZER_NAMESPACE, "map-null"); //$NON-NLS-1$ 
    private static final QName _DateFormat_QNAME = new QName(DOZER_NAMESPACE, "date-format"); //$NON-NLS-1$
    private static final QName _StopOnErrors_QNAME = new QName(DOZER_NAMESPACE, "stop-on-errors"); //$NON-NLS-1$
    private static final QName _BHint_QNAME = new QName(DOZER_NAMESPACE, "b-hint"); //$NON-NLS-1$
    private static final QName _Variable_QNAME = new QName(DOZER_NAMESPACE, "variable"); //$NON-NLS-1$
    private static final QName _Converter_QNAME = new QName(DOZER_NAMESPACE, "converter"); //$NON-NLS-1$

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.jboss.tools.fuse.transformation.core.model.dozer.config
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
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "relationship-type")
    public JAXBElement<Relationship> createRelationshipType(Relationship value) {
        return new JAXBElement<>(_RelationshipType_QNAME, Relationship.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Class }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "class-a")
    public JAXBElement<Class> createClassA(Class value) {
        return new JAXBElement<>(_ClassA_QNAME, Class.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "b-deep-index-hint")
    public JAXBElement<String> createBDeepIndexHint(String value) {
        return new JAXBElement<>(_BDeepIndexHint_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Class }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "class-b")
    public JAXBElement<Class> createClassB(Class value) {
        return new JAXBElement<>(_ClassB_QNAME, Class.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "map-empty-string")
    public JAXBElement<Boolean> createMapEmptyString(Boolean value) {
        return new JAXBElement<>(_MapEmptyString_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FieldDefinition }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "a")
    public JAXBElement<FieldDefinition> createA(FieldDefinition value) {
        return new JAXBElement<>(_A_QNAME, FieldDefinition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "exception")
    public JAXBElement<String> createException(String value) {
        return new JAXBElement<>(_Exception_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "copy-by-reference")
    public JAXBElement<String> createCopyByReference(String value) {
        return new JAXBElement<>(_CopyByReference_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "trim-strings")
    public JAXBElement<Boolean> createTrimStrings(Boolean value) {
        return new JAXBElement<>(_TrimStrings_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "wildcard")
    public JAXBElement<Boolean> createWildcard(Boolean value) {
        return new JAXBElement<>(_Wildcard_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "a-hint")
    public JAXBElement<String> createAHint(String value) {
        return new JAXBElement<>(_AHint_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "a-deep-index-hint")
    public JAXBElement<String> createADeepIndexHint(String value) {
        return new JAXBElement<>(_ADeepIndexHint_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FieldDefinition }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "b")
    public JAXBElement<FieldDefinition> createB(FieldDefinition value) {
        return new JAXBElement<>(_B_QNAME, FieldDefinition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "bean-factory")
    public JAXBElement<String> createBeanFactory(String value) {
        return new JAXBElement<>(_BeanFactory_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "map-null")
    public JAXBElement<Boolean> createMapNull(Boolean value) {
        return new JAXBElement<>(_MapNull_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "date-format")
    public JAXBElement<String> createDateFormat(String value) {
        return new JAXBElement<>(_DateFormat_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "stop-on-errors")
    public JAXBElement<Boolean> createStopOnErrors(Boolean value) {
        return new JAXBElement<>(_StopOnErrors_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "b-hint")
    public JAXBElement<String> createBHint(String value) {
        return new JAXBElement<>(_BHint_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Variable }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "variable")
    public JAXBElement<Variable> createVariable(Variable value) {
        return new JAXBElement<>(_Variable_QNAME, Variable.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConverterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = DOZER_NAMESPACE, name = "converter")
    public JAXBElement<ConverterType> createConverter(ConverterType value) {
        return new JAXBElement<>(_Converter_QNAME, ConverterType.class, null, value);
    }

}
