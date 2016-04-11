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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.fusesource.ide.foundation.core.util.IOUtils;
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
    protected static final String CAMEL_ROUTES  = "routes";
	public static final String LINE_NUMBER_ATT_NAME = "LINE_NUMBER_ATT_NAME";
    
    private Document document;

	/**
     * loads the camel xml from a resource
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

			return loadCamelModel(xmlFile, monitor);
		} catch (Exception ex) {
			ex.printStackTrace();
			CamelModelServiceCoreActivator.pluginLog().logError("Error loading Camel XML file from " + res.getFullPath().toOSString(), ex);
		}

		return cf;
	}

    /**
     * loads the camel xml from a file
     * 
     * @param xmlFile
     * @param monitor
     * @return	the camel file object representation or null on errors
     */
    public CamelFile loadCamelModel(File xmlFile, IProgressMonitor monitor) {
		if (xmlFile == null || xmlFile.isFile() == false || xmlFile.exists() == false) return null;

		CamelFile cf = null;
    	try {
			// SAXParser parser;
			// try {
				// SAXParserFactory factory = SAXParserFactory.newInstance();
				// parser = factory.newSAXParser();

			// } catch (ParserConfigurationException e) {
			// throw new RuntimeException("Can't create SAX parser / DOM
			// builder.", e);
			// }
			// final SAXHandlerWithLineNumber saxHandlerWithLineNumber = new
			// SAXHandlerWithLineNumber(document);
			// parser.setProperty("http://xml.org/sax/properties/lexical-handler",
			// saxHandlerWithLineNumber);
			// parser.parse(xmlFile, saxHandlerWithLineNumber);
			DocumentBuilder docBuilder = createDocumentBuilder();
			document = docBuilder.parse(xmlFile);

	        IFile documentLocation = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(xmlFile.getPath()));
			cf = readDocumentToModel(document, documentLocation);
		} catch (Exception ex) {
			CamelModelServiceCoreActivator.pluginLog().logError("Error loading Camel XML file from " + xmlFile.getPath(), ex);
		}

		return cf;
	}
    
    /**
     * loads the camel xml from a string
     * 
     * @param text
     * @param monitor
     * @return	the camel file object representation or null on errors
     */
    public CamelFile loadCamelModel(String text, IProgressMonitor monitor, CamelFile cf) {
    	CamelFile reloadedModel = null;
    	try {
			DocumentBuilder db = createDocumentBuilder();
			document = db.parse(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
			reloadedModel = readDocumentToModel(document, cf.getResource());
		} catch (Exception ex) {
			ex.printStackTrace();
			CamelModelServiceCoreActivator.pluginLog().logError("Error loading Camel XML from string", ex);
		}

		return reloadedModel;
	}
    
    /**
     * reads the document into internal model
     * 
     * @param document
     * @param res
     * @return
     */
    protected CamelFile readDocumentToModel(Document document, IResource res) {
        CamelFile cf = new CamelFile(null);
        cf.setResource(res);
        cf.setDocument(document);
        NodeList childNodes = document.getDocumentElement().getChildNodes();
        if (CamelUtils.getTranslatedNodeName(document.getDocumentElement()).equals(CAMEL_ROUTES)) {
        	// found a routes element
    		CamelContextElement cce = new CamelContextElement(cf, document.getDocumentElement());
    		String contextId = document.getDocumentElement().getAttributes().getNamedItem("id") != null ? document.getDocumentElement().getAttributes().getNamedItem("id").getNodeValue() : CamelUtils.getTranslatedNodeName(document.getDocumentElement()) + "-" + UUID.randomUUID().toString();
    		int startIdx 	= res.getFullPath().toOSString().indexOf("--");
    		int endIdx 		= res.getFullPath().toOSString().indexOf("--", startIdx+1);
    		if (startIdx != endIdx && startIdx != -1) {
    			contextId = res.getFullPath().toOSString().substring(startIdx+2, endIdx);
    		}
    		cce.setId(contextId);
    		cce.initialize();
    		// then add the context to the file
    		cf.addChildElement(cce);
        } else {
            for (int i = 0; i<childNodes.getLength(); i++) {
            	Node child = childNodes.item(i);
            	if (child.getNodeType() != Node.ELEMENT_NODE) continue;
            	String name = CamelUtils.getTranslatedNodeName(child);            	
            	String id = child.getAttributes().getNamedItem("id") != null ? child.getAttributes().getNamedItem("id").getNodeValue() : CamelUtils.getTranslatedNodeName(child) + "-" + UUID.randomUUID().toString();
            	if (name.equals(CAMEL_CONTEXT)) {
            		// found a camel context
            		CamelContextElement cce = new CamelContextElement(cf, child);
            		cce.setId(id);
            		cce.initialize();
            		// then add the context to the file
            		cf.addChildElement(cce);
            	} else {
            		// found a global configuration element
            		cf.addGlobalDefinition(id, child);
            	}	        	
            }
        }

        return cf;
    }
    
    /**
     * saves the camel model to file
     * 
     * @param camelFile
     * @param outputFile
     * @param monitor
     */
    public void saveCamelModel(CamelFile camelFile, File outputFile, IProgressMonitor monitor) {
    	if (this.document == null) this.document = createDocumentBuilder().newDocument();

    	try {
	    	// now the real save logic 
	        TransformerFactory tf = TransformerFactory.newInstance();
	        tf.setAttribute("indent-number", INDENTION_VALUE);
	
	        Transformer transformer = tf.newTransformer();
	
	        // Save vs. Save as
	        Result output = new StreamResult(outputFile);
	        Source input = new DOMSource(this.document);
	
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	        transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
	        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "" + INDENTION_VALUE);
	        transformer.transform(input, output);
    	} catch (Exception ex) {
    		CamelModelServiceCoreActivator.pluginLog().logError("Unable to save the camel file to " + outputFile.getPath(), ex);
    	}
    }
    
    /**
     * saves the camel model to file
     * 
     * @param camelFile
     * @param res
     * @param monitor
     */
    public void saveCamelModel(CamelFile camelFile, IResource res, IProgressMonitor monitor) {
    	File outputFile = getFileFromResource((res != null && res != camelFile.getResource()) ? res : camelFile.getResource());   	
    	saveCamelModel(camelFile, outputFile, monitor);
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
    	return new File(res.getLocationURI() != null ? res.getLocationURI().getPath() : res.getFullPath().toOSString());
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
	
	/**
	 * creates a new camel context file in the given newFile
	 * 
	 * @param newFile			the file to be used / where to save
	 * @param isSpringContext	if true a spring context is created, otherwise a blueprint context
	 */
	public void createCamelFileTemplate(File newFile, boolean isSpringContext) {
		if (newFile == null) throw new IllegalArgumentException("The given file parameter can't be null.");
		if (newFile.isDirectory()) throw new IllegalArgumentException("The given file parameter can't be a folder.");
		
		if (newFile.exists() && newFile.isFile()) newFile.delete();
		try {
			if (newFile.createNewFile()) {
				if (isSpringContext) {
					IOUtils.writeText(newFile, getSpringStubText());
				} else {
					IOUtils.writeText(newFile, getBlueprintStubText());
				}
			}
		} catch (IOException ex) {
			throw new IllegalArgumentException("An error occured creating the template file..." ,ex);
		}
	}

	public String getSpringStubText() {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<beans xmlns=\"http://www.springframework.org/schema/beans\"\n");
		sb.append("       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd        http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd\">\n");
		sb.append("   <camelContext id=\"camelContext1\" xmlns=\"http://camel.apache.org/schema/spring\">\n");
		sb.append("   </camelContext>\n");
		sb.append("</beans>\n");
		return sb.toString();
	}
	
	public String getBlueprintStubText() {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<blueprint xmlns=\"http://www.osgi.org/xmlns/blueprint/v1.0.0\"\n");
		sb.append("		xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
		sb.append("		       xsi:schemaLocation=\"\n");
		sb.append("       http://www.osgi.org/xmlns/blueprint/v1.0.0 https://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd\n");
		sb.append("       http://camel.apache.org/schema/blueprint http://camel.apache.org/schema/blueprint/camel-blueprint.xsd\">\n");
		sb.append("\n");
		sb.append("	<camelContext id=\"context1\" xmlns=\"http://camel.apache.org/schema/blueprint\">\n");
		sb.append("\n");
		sb.append("	</camelContext>\n");
		sb.append("\n");
		sb.append("</blueprint>\n");
		return sb.toString();
	}
}
