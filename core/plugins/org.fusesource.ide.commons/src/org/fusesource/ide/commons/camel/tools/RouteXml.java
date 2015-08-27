/**
 *  Copyright 2005-2014 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package org.fusesource.ide.commons.camel.tools;

import static org.fusesource.ide.commons.camel.tools.CamelNamespaces.blueprintNS;
import static org.fusesource.ide.commons.camel.tools.CamelNamespaces.findResource;
import static org.fusesource.ide.commons.camel.tools.CamelNamespaces.getNamespaceURI;
import static org.fusesource.ide.commons.camel.tools.CamelNamespaces.nodeWithNamespacesToText;
import static org.fusesource.ide.commons.camel.tools.CamelNamespaces.nodesByNamespace;
import static org.fusesource.ide.commons.camel.tools.CamelNamespaces.replaceChild;
import static org.fusesource.ide.commons.camel.tools.CamelNamespaces.springNS;
import static org.fusesource.ide.commons.camel.tools.CamelNamespaces.springNamespace;
import static org.fusesource.ide.commons.camel.tools.CamelNamespaces.xmlToText;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.model.Constants;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.spring.CamelContextFactoryBean;
import org.apache.camel.spring.CamelEndpointFactoryBean;
import org.apache.camel.spring.CamelRouteContextFactoryBean;
import org.fusesource.ide.commons.Activator;
import org.fusesource.ide.foundation.core.xml.internal.parser.PatchedXMLParser;
import org.xml.sax.ErrorHandler;

import de.pdark.decentxml.Attribute;
import de.pdark.decentxml.Document;
import de.pdark.decentxml.Element;
import de.pdark.decentxml.Namespace;
import de.pdark.decentxml.Node;
import de.pdark.decentxml.NodeWithChildren;
import de.pdark.decentxml.XMLIOSource;
import de.pdark.decentxml.XMLSource;
import de.pdark.decentxml.XMLStringSource;
import de.pdark.decentxml.XMLWriter;

/**
 * Helper class for loading and saving XML for use at design time
 */
public class RouteXml {

    private JAXBContext _jaxbContext;
    private ClassLoader classLoader = CamelContextFactoryBean.class.getClassLoader();

    protected TransformerFactory transformerFactory = TransformerFactory.newInstance();
    protected DocumentBuilder documentBuilder = createDocumentBuilder();

    protected DocumentBuilder documentBuilder(ErrorHandler handler) {
        DocumentBuilder db = createDocumentBuilder();
        db.setErrorHandler(handler);
        return db;
    }

    public JAXBContext jaxbContext() throws JAXBException {
        if (_jaxbContext == null) {
            String packageName = Constants.JAXB_CONTEXT_PACKAGES + ":org.apache.camel.spring";
            _jaxbContext = JAXBContext.newInstance(packageName, classLoader);
        }
        return _jaxbContext;
    }

    public void setJaxbContext(JAXBContext jaxbContext) {
        this._jaxbContext = jaxbContext;
    }

    protected Document createExemplarDoc() throws IOException {
        String exemplar = "exemplar/exemplar.xml";
        URL url = findResource(exemplar, null);
        if (url != null) {
            return parse(new XMLIOSource(url));
        } else {
            Activator.getLogger().warning("Could not find file " + exemplar + " on the class path.");
            Document d = new Document();
            d.addNode(new Element("beans", springNamespace));
            return d;
        }
    }

    private Document parse(XMLSource source) {
        PatchedXMLParser parser = new PatchedXMLParser();
        return parser.parse(source);
    }

    public XmlModel unmarshal(File file) throws Exception {
        Document doc;
        if (file.exists()) {
            doc = parse(new XMLIOSource(file));
/*
      // lets find the header stuff
      val root = doc.getRootElement
      if (root != null) {
        val name = root.getNodeName()
        println("====== node name is: " + name)
        val text = IOUtil.loadTextFile(file)
        val idx = text.indexOf("<" + name)
        if (idx > 0) {
          header = text.substring(0, idx)
          println("header: " + header)
        }
      }
*/
        } else {
            doc = createExemplarDoc();
        }

        return unmarshal(doc, "XML File " + file);
    }

    public XmlModel unmarshal(String text) throws Exception {
        Document doc;
        if (text != null && text.trim().length() > 0) {
            doc = parse(new XMLStringSource(text));
        } else {
            doc = createExemplarDoc();
        }
        return unmarshal(doc, "Text");
    }

    public XmlModel unmarshal(Document doc) throws Exception {
        return unmarshal(doc, "XML document " + doc);
    }

    public XmlModel unmarshal(Document doc, String message) throws Exception {
        Unmarshaller unmarshaller = jaxbContext().createUnmarshaller();

        // ("bean", springNamespace)
        Map<String, BeanDef> beans = new HashMap<String, BeanDef>();

        // lets pull out the spring beans...
        // TODO: shouldn't we use http://www.springframework.org/schema/beans namespace instead??
        List<Node> beanElems = nodesByNamespace(doc, springNS, "bean");

        for (Node n: beanElems) {
            if (n instanceof Element) {
                String id = ((Element) n).getAttributeValue("id");
                String cn = ((Element) n).getAttributeValue("class");
                if (id != null && cn != null) {
                	BeanDef bd = new BeanDef("bean", id, cn);
                	for (Element c : ((Element) n).getChildren()) {
                        String pName = ((Element) c).getAttributeValue("name");
                        String pValue = ((Element) c).getAttributeValue("value");
                        String pRef = ((Element) c).getAttributeValue("ref");
                        bd.addProperty(new BeanProp(pName, pValue, pRef));
                	}
                    beans.put(id, bd);
                }
            }
        }
        
        // now lets pull out the jaxb routes...
        List<String[]> search = Arrays.asList(
            new String[] { springNS, "routeContext" },
            new String[] { springNS, "camelContext" },
            new String[] { springNS, "routes" },
            new String[] { blueprintNS, "routeContext" },
            new String[] { blueprintNS, "camelContext" },
            new String[] { blueprintNS, "routes" }
        );

        List<Node> found = new LinkedList<Node>();

        for (String[] pair : search) {
            List<Node> nodes = nodesByNamespace(doc, pair[0], pair[1]);
            int n = nodes.size();
            if (n != 0) {
                if (n > 1) {
                    Activator.getLogger().warning(message + " contains " + n + " <" + pair[1] + "> elements. Only the first one will be used");
                }
                Node node = nodes.get(0);
                found.add(node);
            }
        }

        if (found.size() > 0) {
            Node n = found.get(0);
            if (n != null) {
                String ns = getNamespaceURI(n);
                Node parseNode;
                if (!ns.equals(springNS)) {
                    parseNode = cloneAndReplaceNamespace(n, ns, springNS);
                } else {
                    parseNode = n;
                }

                boolean justRoutes = false;
                boolean routesContext = false;
                String xmlText = nodeWithNamespacesToText(parseNode, (Element) n);
                Object object = unmarshaller.unmarshal(new StringReader(xmlText));
                CamelContextFactoryBean sc;
                if (object instanceof CamelContextFactoryBean) {
                	Activator.getLogger().debug("Found a valid CamelContextFactoryBean! " + object);
                    sc = (CamelContextFactoryBean) object;
                    sc.setId(((CamelContextFactoryBean) object).getId());
                } else if (object instanceof RoutesDefinition) {
                    justRoutes = true;
                    sc = new CamelContextFactoryBean();
                    sc.setId(((RoutesDefinition) object).getId());
                    sc.setRoutes(((RoutesDefinition) object).getRoutes());
                } else if (object instanceof CamelRouteContextFactoryBean) {
                    routesContext = true;
                    sc = new CamelContextFactoryBean();
                    sc.setId(((CamelRouteContextFactoryBean) object).getId());
                    sc.setRoutes(((CamelRouteContextFactoryBean) object).getRoutes());
                } else if (object instanceof org.apache.camel.blueprint.CamelRouteContextFactoryBean) {
                    routesContext = true;
                    sc = new CamelContextFactoryBean();
                    sc.setId(((CamelRouteContextFactoryBean) object).getId());
                    sc.setRoutes(((org.apache.camel.blueprint.CamelRouteContextFactoryBean) object).getRoutes());
                } else {
                    Activator.getLogger().warning("Unmarshalled not a CamelContext: " + object);
                    sc = new CamelContextFactoryBean();
                }
                return new XmlModel(sc, doc, beans, n, ns, justRoutes, routesContext);
            } else {
            	Activator.getLogger().info(message + " does not contain a CamelContext. Maybe the XML namespace is not spring: '" + springNS + "' or blueprint: '" + blueprintNS + "'?");
                // lets create a new collection
                return new XmlModel(new CamelContextFactoryBean(), doc, beans, null, CamelNamespaces.springNS, false, false);
            }
        }
        return null; // ?
    }

    protected Node cloneAndReplaceNamespace(Node node, String oldNS, String newNS) {
        Node answer = node.copy();
        return replaceNamespace(answer, oldNS, newNS);
    }

    protected Node replaceNamespace(Node node, String oldNS, String newNS) {
        if (node instanceof Element) {
            String ns = getNamespaceURI(node);
            if (ns != null && ns.equals(oldNS)) {
                Namespace namespace = ((Element) node).getNamespace();
                if (namespace != null) {
                    if (namespace.getURI() != null && namespace.getURI().equals(oldNS)) {
                        ((Element) node).setNamespace(new Namespace(namespace.getPrefix(), newNS));
                    }
                }

                for (Attribute attr : ((Element) node).getAttributes()) {
                    if (attr.getName().startsWith("xmlns")) {
                        String value = attr.getValue();
                        if (value != null && value.equals(oldNS)) {
                            attr.setValue(newNS);
                        }
                    }
                }
            }
        }

        if (node instanceof NodeWithChildren) {
            for (Node n : ((NodeWithChildren) node).getNodes()) {
                replaceNamespace(n, oldNS, newNS);
            }
        }

        return node;
    }

    public void marshal(File file, final CamelContextFactoryBean context) throws Exception {
        marshal(file, new Model2Model() {
            @Override
            public XmlModel transform(XmlModel model) {
                model.update(context);
                return model;
            }
        });
    }

    public void marshal(File file, final CamelContext context) throws Exception {
        marshal(file, new Model2Model() {
            @Override
            public XmlModel transform(XmlModel model) {
                copyRoutesToElement(context, model.getContextElement());
                return model;
            }
        });
    }

    public void copyRoutesToElement(List<RouteDefinition> routeDefinitionList, CamelContextFactoryBean contextElement) {
        List<RouteDefinition> routes = contextElement.getRoutes();
        routes.clear();
        routes.addAll(routeDefinitionList);
    }

    public void copyRoutesToElement(CamelContext context, CamelContextFactoryBean contextElement) {
        if (context instanceof ModelCamelContext) {
            copyRoutesToElement(((ModelCamelContext)context).getRouteDefinitions(), contextElement);
        } else {
        	Activator.getLogger().error("Invalid camel context! (" + context.getClass().getName() + ")");
        }
    }

    /**
     * Loads the given file then updates the route definitions from the given list then stores the file again
     */
    public void marshal(File file, final List<RouteDefinition> routeDefinitionList) throws Exception {
        marshal(file, new Model2Model() {
            @Override
            public XmlModel transform(XmlModel model) {
                copyRoutesToElement(routeDefinitionList, model.getContextElement());
                return model;
            }
        });
    }

    public void marshal(File file, Model2Model transformer) throws Exception {
        // lets load the file first in case its been edited since we last loaded it
        XmlModel model = unmarshal(file);
        marshal(file, transformer.transform(model));
    }

    /**
     * This method is responsible for merging multiple pieces of Camel
     * configuration and returning that merged configuration as a string.  The
     * string parameter serves as the base config (usually pulled from source editor)
     * and the route list and camel context are merged into this base config.
     * @param text base configuration used for merging
     * @param routeDefinitionList routes that will replace routes in base config
     * @param camelContext any updates to camel context outside of route definitions
     * @return new merged configuration as a string
     */
    public String marshalToText(String text, 
            final List<RouteDefinition> routeDefinitionList,
            final CamelContextFactoryBean camelContext) throws Exception {
        return marshalToText(text, new Model2Model() {
            @Override
            public XmlModel transform(XmlModel model) {
                copyRoutesToElement(routeDefinitionList, model.getContextElement());
                if (camelContext != null) {
                    model.getContextElement().setDataFormats(camelContext.getDataFormats());
                    setEndpoints(model.getContextElement(), camelContext.getEndpoints());
                }
                return model;
            }
        });
    }

    public String marshalToText(String text, Model2Model transformer) throws Exception {
        XmlModel model = unmarshal(text);
        return marshalToText(transformer.transform(model));
    }

    public void marshal(File file, XmlModel model) throws JAXBException, IOException {
        marshalToDoc(model);
        writeXml(model.getDoc(), file);
    }

    public String marshalToText(XmlModel model) throws JAXBException, IOException {
        marshalToDoc(model);
        return xmlToText(model.getDoc());
    }

    protected void replaceCamelElement(Element docElem, Node camelElem, Node oldNode) {
        replaceChild(docElem, camelElem, oldNode);

        // lets replace the camel namespace, copying any namespace from the old node as well
        if (camelElem instanceof Element && oldNode instanceof Element) {
            for (Attribute attr : ((Element) oldNode).getAttributes()) {
                if (attr.getName().startsWith("xmlns")) {
                    ((Element) camelElem).setAttribute(attr.getName(), attr.getValue());
                }
            }
        }
    }

    /**
     * Marshals the model to XML and updates the model's doc property to contain the
     * new marshalled model
     *
     * @param model
     */
    public void marshalToDoc(XmlModel model) throws JAXBException {
        Marshaller marshaller = jaxbContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, java.lang.Boolean.TRUE);
        try {
            marshaller.setProperty("com.sun.xml.bind.indentString", "  ");
        } catch (PropertyException e) {
        	Activator.getLogger().debug("Property is not supported", e);
        	try {
        		marshaller.setProperty("com.sun.xml.internal.bind.indentString", "  ");
        	} catch (PropertyException e1) {
            	Activator.getLogger().debug("Property is not supported", e1);
        	}
        }

        Object value = model.marshalRootElement();
        Document doc = model.getDoc();
        Element docElem = doc.getRootElement();

        // JAXB only seems to do nice whitespace/namespace stuff when writing to stream
        // rather than DOM directly
        // marshaller.marshal(value, docElem);

        StringWriter buffer = new StringWriter();
        marshaller.marshal(value, buffer);

        // now lets parse the XML and insert the root element into the doc
        String xml = buffer.toString();
        if (!model.getNs().equals(springNS)) {
            // !!!
            xml = xml.replaceAll(springNS, model.getNs());
        }
        Document camelDoc = parse(new XMLStringSource(xml));
        Node camelElem = camelDoc.getRootElement();

        // TODO
        //val camelElem = doc.importNode(element, true)

        if (model.isRoutesContext() && camelDoc.getRootElement().getName().equals("camelContext")) {
            camelDoc.getRootElement().setName("routeContext");
        }
        if (model.isJustRoutes()) {
            replaceChild(doc, camelElem, docElem);
        } else {
            if (model.getNode() != null) {
                replaceCamelElement(docElem, camelElem, model.getNode());
            } else {
                docElem.addNode(camelElem);
            }
        }
    }

    public void writeXml(Document doc, File file) throws IOException {
        File parentDir = file.getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }

        XMLWriter writer = new XMLWriter(new FileWriter(file));
        doc.toXML(writer);
        writer.close();
    }

    protected DocumentBuilder createDocumentBuilder() {
//        String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
//        String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
//        String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

//        boolean validating = false;
//        if (validating) {
//            try {
//                dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
//                dbf.setAttribute(JAXP_SCHEMA_SOURCE, CamelNamespaces.camelSchemas());
//                dbf.setValidating(validating);
//            } catch (Exception e) {
//                // ignore
//            }
//        }

        dbf.setExpandEntityReferences(false);
        dbf.setIgnoringComments(false);
        dbf.setIgnoringElementContentWhitespace(false);
        dbf.setCoalescing(false);
        dbf.setNamespaceAware(true);
        try {
            return dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    /**
     * XmlModel => XmlModel
     */
    private static interface Model2Model {

        public XmlModel transform(XmlModel model);

    }

    /**
     * Due to https://issues.apache.org/jira/browse/CAMEL-8498, we cannot set
     * endpoints on CamelContextFactoryBean directly.  Use reflection for now
     * until this issue is resolved upstream.
     */
    private void setEndpoints(CamelContextFactoryBean context, List<CamelEndpointFactoryBean> endpoints) {
        try {
            Field endpointsField = context.getClass().getDeclaredField("endpoints");
            endpointsField.setAccessible(true);
            endpointsField.set(context, endpoints);
        } catch (Exception ex) {
            Activator.getLogger().error("Failed to update endpoints in camelContext", ex);
        }
    }
}
