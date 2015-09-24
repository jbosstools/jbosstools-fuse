/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.jboss.tools.fuse.transformation.model.xml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.inst2xsd.Inst2Xsd;
import org.apache.xmlbeans.impl.inst2xsd.Inst2XsdOptions;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.api.ErrorListener;
import com.sun.tools.xjc.api.Mapping;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;

/**
 * Model generator for XML type definitions. This generator supports model generation from XML schema and XML instance data.
 */
public class XmlModelGenerator {

    private static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XMLNS_NAMESPACE = "http://www.w3.org/2000/xmlns/";

    void addMissingSettersForLists(Iterator<JDefinedClass> iterator,
                                   JPrimitiveType voidType) {
        while (iterator.hasNext()) {
            JDefinedClass definedClass = iterator.next();
            addMissingSettersForLists(definedClass.classes(), voidType);
            Set<JMethod> listGetMethods = new HashSet<>();
            // Collect getters for all list fields
            for (JMethod method : definedClass.methods()) {
                if (method.name().startsWith("get") && method.type().name().startsWith("List<"))
                    listGetMethods.add(method);
            }
            if (!listGetMethods.isEmpty()) {
                // Remove getters w/ matching setters
                for (JMethod method : definedClass.methods()) {
                    if (method.name().startsWith("set")) {
                        for (Iterator<JMethod> iter = listGetMethods.iterator(); iter.hasNext();) {
                            JMethod getMethod = iter.next();
                            if (method.name().substring(3).equals(getMethod.name().substring(3))) {
                                iter.remove();
                                break;
                            }
                        }
                    }
                }
                // Add missing setters
                for (JMethod getMethod : listGetMethods) {
                    String name = getMethod.name().substring(3);
                    JMethod setMethod = definedClass.method(getMethod.mods().getValue(),
                                                            voidType,
                                                            "set" + name);
                    name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                    JVar prm = setMethod.param(0, getMethod.type(), name);
                    setMethod.body().assign(JExpr._this().ref(name), prm);
                }
            }
        }
    }

    private void addSchemaLocations(NodeList nodes,
                                    Map<String, File> fileByNamespace,
                                    Element schema,
                                    String schemaPrefix,
                                    String schemaNamespace) {
        for (int nodeNdx = 0; nodeNdx < nodes.getLength(); ++nodeNdx) {
            Node node = nodes.item(nodeNdx);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;
            Element element = (Element)node;
            NamedNodeMap attrs = element.getAttributes();
            if (attrs != null) {
                Attr nsAttr = null;
                for (int attrNdx = 0; attrNdx < attrs.getLength(); ++attrNdx) {
                    Node attr = attrs.item(attrNdx);
                    if (attr.getNodeName().startsWith("xmlns")) {
                        nsAttr = (Attr)attr;
                        break;
                    }
                }
                if (nsAttr != null) {
                    element.removeAttributeNode(nsAttr);
                    schema.setAttributeNodeNS(nsAttr);
                    Element importElement = schema.getOwnerDocument().createElementNS(schemaNamespace, schemaPrefix + ":import");
                    importElement.setAttribute("namespace", nsAttr.getNodeValue());
                    importElement.setAttribute("schemaLocation", fileByNamespace.get(nsAttr.getNodeValue()).toURI().toString());
                    schema.insertBefore(importElement, schema.getFirstChild());
                }
            }
            addSchemaLocations(element.getChildNodes(), fileByNamespace, schema, schemaPrefix, schemaNamespace);
        }
    }

    @SuppressWarnings("resource")
    private SchemaCompiler createSchemaCompiler(final File schemaFile) throws Exception {
        final SchemaCompiler sc = XJC.createSchemaCompiler();
        final FileInputStream schemaStream = new FileInputStream(schemaFile);
        final InputSource is = new InputSource(schemaStream);
        is.setSystemId(schemaFile.toURI().toString());
        sc.parseSchema(is);
        return sc;
    }

    /**
     * Returns a mapping of element names to generated Java classes for a given JCodeModel. Note that the element names are not
     * qualified, they are all the local name of the element.
     *
     * @param model
     *        generated model from a schema or instance document
     * @return map with element names as keys and class names as values
     */
    public Map<String, String> elementToClassMapping(JCodeModel model) {
        Map<String, String> mappings = new HashMap<String, String>();
        Iterator<JPackage> packageIt = model.packages();

        // We need to search through all generated classes of generated packages
        while (packageIt.hasNext()) {
            Iterator<JDefinedClass> classIt = packageIt.next().classes();
            while (classIt.hasNext()) {
                JDefinedClass jdClass = classIt.next();
                // Certain schema styles do not use XmlRootElement annotations and use
                // XmlElementDecl inside of ObjectFactory instead - check for that
                if (jdClass.name().equals("ObjectFactory")) {
                    for (JMethod method : jdClass.methods()) {
                        JAnnotationUse elementAnnotation = getAnnotation(
                                                                         method, XmlElementDecl.class.getName());
                        if (elementAnnotation != null) {
                            String elementName = getAnnotationValue(elementAnnotation, "name");
                            JType returnType = ((JClass)method.type()).getTypeParameters().get(0);
                            mappings.put(elementName, returnType.fullName());
                        }
                    }
                } else {
                    // Top-level elements will result in classes with the @XmlRootElement annotation
                    // which is all we care about.
                    JAnnotationUse elementAnnotation = getAnnotation(jdClass, XmlRootElement.class.getName());
                    if (elementAnnotation != null) {
                        String elementName = getAnnotationValue(elementAnnotation, "name");
                        mappings.put(elementName, jdClass.fullName());
                    }
                }
            }
        }
        return mappings;
    }

    /**
     * Generates Java classes in targetPath directory given an XML instance document. This method generates a schema at the path
     * specified by schemaFile and then calls generateFromSchema to generate Java classes.
     *
     * @param instanceFile
     *        file containing XML instance document
     * @param schemaFile
     *        a file reference where the schema should be generated
     * @param packageName
     *        package name for generated model classes
     * @param targetPath
     *        directory where class source will be generated
     * @return the generated code model
     * @throws Exception
     *         failure during model generation
     */
    public JCodeModel generateFromInstance(File instanceFile,
                                           File schemaFile,
                                           String packageName,
                                           File targetPath) throws Exception {
        // Step 1 - generate schema from instance doc
        Inst2XsdOptions options = new Inst2XsdOptions();
        options.setDesign(Inst2XsdOptions.DESIGN_RUSSIAN_DOLL);
        XmlObject[] xml = new XmlObject[] {XmlObject.Factory.parse(instanceFile)};
        SchemaDocument[] schemaDocs = Inst2Xsd.inst2xsd(xml, options);
        if (schemaDocs.length == 1) {
            schemaDocs[0].save(schemaFile, new XmlOptions().setSavePrettyPrint());
        } else {
            String namespace = xml[0].getDomNode().getFirstChild().getNamespaceURI();
            // Save schemas and map their namespaces to their locations
            Map<String, File> fileByNamespace = new HashMap<>();
            for (SchemaDocument schemaDoc : schemaDocs) {
                String targetNamespace = schemaDoc.getSchema().getTargetNamespace();
                File file;
                if (targetNamespace.equals(namespace)) {
                    file = schemaFile;
                } else {
                    String name = new URI(targetNamespace).getSchemeSpecificPart();
                    StringBuilder builder = new StringBuilder();
                    for (String part : name.split("/")) {
                        if (part.isEmpty()) continue;
                        if (builder.length() > 0) builder.append('.');
                        builder.append(part);
                    }
                    file = new File(builder + ".xsd");
                }
                schemaDoc.save(file, new XmlOptions().setSavePrettyPrint());
                fileByNamespace.put(targetNamespace, file);
            }
            // Update all namespace declarations to include location
            for (File file : fileByNamespace.values()) {
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
                NodeList nodes = doc.getChildNodes();
                for (int nodeNdx = 0; nodeNdx < nodes.getLength(); ++nodeNdx) {
                    Node node = nodes.item(nodeNdx);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element schema = (Element)node; // Schemas only have one top-level element
                        // Determine schema's namespace prefix
                        String schemaPrefix = schema.getNodeName();
                        int ndx = schemaPrefix.indexOf(':');
                        schemaPrefix = ndx > 0 ? schemaPrefix.substring(0, ndx) : "";
                        // Determine schema's namespace
                        String schemaNamespace;
                        NamedNodeMap attrs = schema.getAttributes();
                        if (attrs == null) {
                            schemaNamespace = "";
                        } else {
                            Attr nsAttr = null;
                            for (int attrNdx = 0; attrNdx < attrs.getLength(); ++attrNdx) {
                                Node attr = attrs.item(attrNdx);
                                if (attr.getNodeName().equals("xmlns" + (schemaPrefix.isEmpty() ? "" : ":" + schemaPrefix))) {
                                    nsAttr = (Attr)attr;
                                    break;
                                }
                            }
                            schemaNamespace = nsAttr == null ? "" : nsAttr.getValue();
                        }

                        addSchemaLocations(node.getChildNodes(), fileByNamespace, schema, schemaPrefix, schemaNamespace);
                    }
                }
                TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(file));
            }
        }

        // Step 2 - call generateFromSchema with generated schema
        return generateFromSchema(schemaFile, packageName, targetPath);
    }

    /**
     * Generates Java classes in targetPath directory given an XML schema.
     *
     * @param schemaFile
     *        file reference to the XML schema
     * @param packageName
     *        package name for generated model classes
     * @param targetPath
     *        directory where class source will be generated
     * @return the generated code model
     * @throws Exception
     *         failure during model generation
     */
    public JCodeModel generateFromSchema(final File schemaFile,
                                         final String packageName,
                                         final File targetPath) throws Exception {
        final SchemaCompiler sc = createSchemaCompiler(schemaFile);
        if (packageName != null) sc.forcePackageName(packageName);
        class SchemaErrorListener implements ErrorListener {

            Exception exception;

            @Override
            public void warning(SAXParseException exception) {
                this.exception = exception;
            }

            @Override
            public void info(SAXParseException exception) {
                this.exception = exception;
            }

            @Override
            public void fatalError(SAXParseException exception) {
                this.exception = exception;
            }

            @Override
            public void error(SAXParseException exception) {
                this.exception = exception;
            }
        }
        SchemaErrorListener listener = new SchemaErrorListener();
        sc.setErrorListener(listener);
        final S2JJAXBModel s2 = sc.bind();
        if (listener.exception != null) throw listener.exception;
        if (s2 == null) throw new Exception("Failed to parse schema into JAXB Model");
        final JCodeModel jcm = s2.generateCode(null, null);
        for (Iterator<JPackage> iter = jcm.packages(); iter.hasNext();) {
            addMissingSettersForLists(iter.next().classes(), jcm.VOID);
        }
        try (PrintStream status = new PrintStream(new ByteArrayOutputStream())) {
            jcm.build(targetPath, status);
        }
        return jcm;
    }

    private JAnnotationUse getAnnotation(JAnnotatable annotated,
                                         String type) {
        JAnnotationUse annotation = null;
        for (JAnnotationUse ann : annotated.annotations()) {
            if (ann.getAnnotationClass().fullName().equals(type)) {
                annotation = ann;
                break;
            }
        }
        return annotation;
    }

    private String getAnnotationValue(JAnnotationUse annotation,
                                      String elementName) {
        JAnnotationValue jaVal = annotation.getAnnotationMembers().get(elementName);
        if (jaVal == null) {
            return null;
        }
        StringWriter sw = new StringWriter();
        jaVal.generate(new JFormatter(sw));
        return sw.toString().replaceAll("\"", "");
    }

    /**
     * Returns a list of top-level element definitions for a given schema.
     *
     * @param schemaFile
     *        the schema to check for top-level elements
     * @return list of elements
     * @throws Exception
     */
    public List<QName> getElementsFromSchema(final File schemaFile) throws Exception {
        List<QName> elements = new LinkedList<QName>();
        SchemaCompiler sc = createSchemaCompiler(schemaFile);
        final S2JJAXBModel s2 = sc.bind();
        if (s2 == null) {
            throw new Exception("Failed to parse schema into JAXB Model");
        }
        for (Mapping mapping : s2.getMappings()) {
            elements.add(mapping.getElement());
        }

        return elements;
    }

    /**
     * Returns the qualified name of the root element in the specified XML instance document.
     *
     * @param instanceFile
     *        XML instance doc
     * @return root element name
     * @throws Exception
     *         failed to parse instance document
     */
    public QName getRootElementName(final File instanceFile) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(instanceFile);
        Element root = doc.getDocumentElement();
        if (root == null) {
            throw new Exception("Invalid instance document : no root element");
        }
        return new QName(root.getNamespaceURI(), root.getLocalName());
    }
}
