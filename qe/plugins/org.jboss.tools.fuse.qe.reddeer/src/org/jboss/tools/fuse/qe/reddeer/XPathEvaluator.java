/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author apodhrad
 */
public class XPathEvaluator {

	public static final boolean DEFAULT_NAMESPACE_AWARE = false;

	private static final DocumentBuilderFactory DOC_FACTORY = DocumentBuilderFactory.newInstance();
	private static final XPath XPATH = XPathFactory.newInstance().newXPath();

	private Document doc;

	public XPathEvaluator(File file) {
		this(file, DEFAULT_NAMESPACE_AWARE);
	}

	public XPathEvaluator(File file, boolean namespaceAware) {
		this(getReader(file), namespaceAware);
	}

	public XPathEvaluator(Reader reader) {
		this(reader, DEFAULT_NAMESPACE_AWARE);
	}

	public XPathEvaluator(InputStream inputStream) {
		this(inputStream, DEFAULT_NAMESPACE_AWARE);
	}

	public XPathEvaluator(Reader reader, boolean namespaceAware) {
		try {
			DOC_FACTORY.setNamespaceAware(namespaceAware);
			doc = DOC_FACTORY.newDocumentBuilder().parse(new InputSource(reader));
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public XPathEvaluator(InputStream inputStream, boolean namespaceAware) {
		try {
			DOC_FACTORY.setNamespaceAware(namespaceAware);
			doc = DOC_FACTORY.newDocumentBuilder().parse(new InputSource(inputStream));
			inputStream.close();
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	private static Reader getReader(File file) {
		try {
			return new FileReader(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean evaluateBoolean(String expr) {
		try {
			return (Boolean) XPATH.evaluate(expr, doc, XPathConstants.BOOLEAN);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			System.out.println("Error evaluating xPath '" + expr + "'");
			return false;
		}
	}

	public String evaluateString(String expr) {
		try {
			return (String) XPATH.evaluate(expr, doc, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			System.out.println("Error evaluating xPath '" + expr + "'");
			return null;
		}
	}

	public Node evaluateNode(String expr) {
		try {
			return (Node) XPATH.evaluate(expr, doc, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			System.out.println("Error evaluating xPath '" + expr + "'");
			return null;
		}
	}

	public void printDocument(Result target) throws IOException, TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		transformer.transform(new DOMSource(doc), target);
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		try {
			printDocument(new StreamResult(writer));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return writer.toString();
	}
}
