/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.model.service.core.io;

import java.io.File;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author lhein
 *
 */
public class CamelIOHandler {

    protected static final int INDENTION_VALUE 	= 3;
    protected static final String CAMEL_CONTEXT = "camelContext";
    
    private Document document;

    /**
     * creates a new io handler using the Camel Namespace Mapper
     */
    public CamelIOHandler() {
        
    }

	/**
     * loads the camel xml
     * 
     * @param res
     * @param monitor
     * @return	the camel file object representation or null on errors
     */
    public CamelFile loadCamelModel(IResource res, IProgressMonitor monitor) {
    	CamelFile cf = null;
    	try {
			File xmlFile = getFileFromResource(res);
			if (xmlFile == null) {
				CamelModelServiceCoreActivator.pluginLog().logError("Cannot determine the file path for resource " + res.getFullPath().toOSString());
				return null;
			}

			DocumentBuilder db = createDocumentBuilder();
	        document = db.parse(xmlFile);
			cf = new CamelFile(res);
	        cf.setDocument(document);
	        NodeList childNodes = document.getDocumentElement().getChildNodes();
	        for (int i = 0; i<childNodes.getLength(); i++) {
	        	Node child = childNodes.item(i);

	        	String name = child.getNodeName();
	        	if (name.equals("#text")) continue;
	        	String id = child.getAttributes().getNamedItem("id") != null ? child.getAttributes().getNamedItem("id").getNodeValue() : UUID.randomUUID().toString();
	        	if (name.equals(CAMEL_CONTEXT)) {
	        		// found a camel context
	        		CamelContextElement cce = new CamelContextElement(cf, child);
	        		cce.setId(id);
	        		// then add the context to the file
	        		cf.addChildElement(cce);
	        	} else {
	        		// found a global configuration element
	        		cf.addGlobalDefinition(id, child);
	        	}	        	
	        }
		} catch (Exception ex) {
			CamelModelServiceCoreActivator.pluginLog().logError("Error loading Camel XML file from " + res.getFullPath().toOSString(), ex);
		}

		return cf;
	}

    
    /**
     * saves the camel model to file
     * 
     * @param camelFile
     * @param res
     * @param monitor
     */
    public void saveCamelModel(CamelFile camelFile, IResource res, IProgressMonitor monitor) {
    	if (this.document == null) this.document = createDocumentBuilder().newDocument();

    	try {
	    	// now the real save logic 
	        TransformerFactory tf = TransformerFactory.newInstance();
	        tf.setAttribute("indent-number", INDENTION_VALUE);
	
	        Transformer transformer = tf.newTransformer();
	
	        // Save vs. Save as
	        File outputFile = getFileFromResource((res != null && res != camelFile.getResource()) ? res : camelFile.getResource());
	        Result output = new StreamResult(outputFile);
	        Source input = new DOMSource(this.document);
	
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	        transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
	        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "" + INDENTION_VALUE);
	        transformer.transform(input, output);
    	} catch (Exception ex) {
    		CamelModelServiceCoreActivator.pluginLog().logError("Unable to save the camel file to " + res.getFullPath().toOSString(), ex);
    	}
    }
	
	/**
	 * @return the document
	 */
	public Document getDocument() {
		return this.document;
	}
	
	/**
	 * 
	 * @param document
	 */
	public void setDocument(Document document) {
		this.document = document;
	}
	
    /**
     * converts the iresource into a file
     * 
     * @param res
     * @return
     */
    protected File getFileFromResource(IResource res) {
    	return res.getRawLocation().toFile();
    }
	
    /**
     * creates a document builder
     * 
     * @return	the document builder or null on errors
     */
	protected DocumentBuilder createDocumentBuilder() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setExpandEntityReferences(false);
		dbf.setIgnoringComments(false);
		dbf.setIgnoringElementContentWhitespace(false);
		dbf.setCoalescing(false);
		dbf.setNamespaceAware(true);

		try {
			return dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			CamelModelServiceCoreActivator.pluginLog().logError("Unable to create a document builder for loading the camel file.", e);
		}
		return null;
	}
}
