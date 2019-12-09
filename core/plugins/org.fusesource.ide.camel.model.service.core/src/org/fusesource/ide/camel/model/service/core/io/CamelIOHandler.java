/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.w3c.dom.Document;

/**
 * @author lhein
 *
 */
public class CamelIOHandler {

	protected static final int INDENTION_VALUE = 3;
	protected static final String CAMEL_CONTEXT = "camelContext";
	protected static final String CAMEL_ROUTES = "routes";
	public static final String LINE_NUMBER_ATT_NAME = "LINE_NUMBER_ATT_NAME";

	private Document document;

	/**
	 * loads the camel xml from a resource
	 * 
	 * @param res
	 * @param monitor
	 * @return the camel file object representation or null on errors
	 */
	public CamelFile loadCamelModel(IResource res, IProgressMonitor monitor) {
		CamelFile cf = null;
		try {
			File xmlFile = getFileFromResource(res);
			if (xmlFile == null) {
				CamelModelServiceCoreActivator.pluginLog()
						.logError("Cannot determine the file path for resource " + res.getFullPath().toOSString());
				return null;
			}

			return loadCamelModel(xmlFile, monitor);
		} catch (Exception ex) {
			CamelModelServiceCoreActivator.pluginLog()
					.logError("Error loading Camel XML file from " + res.getFullPath().toOSString(), ex);
		}

		return cf;
	}

	/**
	 * reloads a camel model from a resource and then reapplies the xmlContent
	 * to it
	 * 
	 * @param xmlContent
	 *            the xml content to reapply to the model
	 * @param monitor
	 *            the progress monitor
	 * @param resource
	 *            the original resource
	 * @return the reloaded model
	 */
	public CamelFile reloadCamelModel(String xmlContent, IProgressMonitor monitor, IResource resource) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 20);
		CamelFile cf = loadCamelModel(resource, subMonitor.split(10));
		cf = loadCamelModel(xmlContent, subMonitor.split(10), cf);
		return cf;
	}

	/**
	 * loads the camel xml from a file
	 * 
	 * @param xmlFile
	 * @param monitor
	 * @return the camel file object representation or null on errors
	 */
	public CamelFile loadCamelModel(File xmlFile, IProgressMonitor monitor) {
		if (xmlFile == null || !xmlFile.isFile() || !xmlFile.exists() || xmlFile.length() == 0) {
			return null;
		}

		CamelFile cf = null;
		try {
			DocumentBuilder docBuilder = createDocumentBuilder();
			document = docBuilder.parse(xmlFile);

			IFile documentLocation = ResourcesPlugin.getWorkspace().getRoot()
					.getFileForLocation(new Path(xmlFile.getCanonicalPath()));
			cf = readDocumentToModel(document, documentLocation);
		} catch (Exception ex) {
			CamelModelServiceCoreActivator.pluginLog()
					.logError("Error loading Camel XML file from " + xmlFile.getPath(), ex);
		}

		return cf;
	}

	/**
	 * loads the camel xml from a string
	 * 
	 * @param text
	 * @param monitor
	 * @return the camel file object representation or null on errors
	 */
	public CamelFile loadCamelModel(String text, IProgressMonitor monitor, CamelFile cf) {
		CamelFile reloadedModel = null;
		try {
			DocumentBuilder db = createDocumentBuilder();
			document = db.parse(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
			reloadedModel = readDocumentToModel(document, cf.getResource());
		} catch (Exception ex) {
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
		cf.initialize();
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
		String camelAsXml = camelFile.getDocumentAsXML();
		try {
			Files.copy(new ByteArrayInputStream(camelAsXml.getBytes(StandardCharsets.UTF_8)), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception ex) {
			CamelModelServiceCoreActivator.pluginLog()
					.logError("Unable to save the camel file to " + outputFile.getPath(), ex);
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
		File outputFile = getFileFromResource(
				(res != null && res != camelFile.getResource()) ? res : camelFile.getResource());
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
	 * @return the document builder or null on errors
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
			CamelModelServiceCoreActivator.pluginLog()
					.logError("Unable to create a document builder for loading the camel file.", e);
		}
		return null;
	}
}
