package org.fusesource.ide.foundation.core.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.fusesource.ide.foundation.core.util.Strings;

import de.pdark.decentxml.Attribute;
import de.pdark.decentxml.Document;
import de.pdark.decentxml.Element;
import de.pdark.decentxml.Namespace;
import de.pdark.decentxml.Node;
import de.pdark.decentxml.NodeFilter;
import de.pdark.decentxml.NodeWithChildren;
import de.pdark.decentxml.Parent;
import de.pdark.decentxml.XMLWriter;

public class XmlNodeUtilities {

    public static String getNamespaceURI(Node node) {
        if (node instanceof Element) {
            Namespace ns = ((Element) node).getNamespace();
            if (ns != null) {
                String uri = ns.getURI();
                if (uri == null || uri.length() == 0) {
                    String uriAttr = ns.getPrefix().equals("") ? ((Element) node).getAttributeValue("xmlns") : null;
                    if (uriAttr != null) {
                        return uriAttr;
                    } else {
                        return getNamespaceURI(((Element) node).getParent());
                    }
                } else {
                    return uri;
                }
            }
        }
        return null;
    }
    

    public static void addParentNamespaces(Element element, Parent parent) {
        if (parent instanceof Element) {
            for (Attribute attr : ((Element) parent).getAttributes()) {
                String name = attr.getName();
                if (name.startsWith("xmlns") && element.getAttribute(name) == null) {
                    element.setAttribute(name, attr.getValue());
                }
            }
            addParentNamespaces(element, ((Element) parent).getParent());
        }
        boolean xmlnsEmpty = Strings.isEmpty(element.getAttribute("xmlns").getValue());
        if (parent == null) {
        	if( !xmlnsEmpty) {
        		if( element.getBeginName().indexOf(":") != -1) {
        			String attrKey = "xmlns:" + element.getBeginName().substring(0, element.getBeginName().indexOf(":"));
        			element.setAttribute(attrKey, element.getAttribute("xmlns").getValue());
        		}
        	}
        }
    }
    

    public static Document getOwnerDocument(Node node) {
        if (node instanceof Element) {
            return ((Element) node).getDocument();
        } else if (node instanceof Document) {
            return (Document) node;
        }
        return null;
    }
    
    public static void replaceChild(Parent parent, Node newChild, Node oldNode) {
        int idx = parent.nodeIndexOf(oldNode);
        if (idx < 0) {
            parent.addNode(newChild);
        } else {
            parent.removeNode(idx);
            parent.addNode(idx, newChild);
        }
    }

    

    public static List<Node> nodesByNamespace(Document doc, final String namespaceUri, final String localName) {
        NodeFilter<Node> filter = new NodeFilter<Node>() {
            @Override
            public boolean matches(Node n) {
                if (n instanceof Element) {
                    Namespace ns = ((Element) n).getNamespace();
                    // TODO this doesn't work with empty prefixes!
                    if (!((Element) n).getName().equals(localName)) {
                        return false;
                    } else {
                        String uri = getNamespaceURI(n);
                        return ns != null;// && namespaceUri.equals(uri); // (!)
                    }
                }
                return false;
            }
        };
        return findNodes(doc, filter);
    }

    public static String xmlToText(Node node) throws IOException {
        StringWriter buffer = new StringWriter();
        XMLWriter writer = new XMLWriter(buffer);
        node.toXML(writer);
        writer.close();

        return buffer.toString();
    }
    
    public static List<Node> findNodes(NodeWithChildren node, NodeFilter<Node> filter) {
        List<Node> answer = node.getNodes(filter);
        List<Node> children = node.getNodes();
        for (Node child: children) {
            if (child instanceof Element) {
                List<Node> childMatched = findNodes((Element) child, filter);
                answer.addAll(childMatched);
            }
        }
        return answer;
    }

    
}
