/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.core.xml.namespace;

import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.internal.content.XMLRootHandler;
import org.fusesource.ide.foundation.core.internal.FoundationCoreActivator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class FindNamespaceHandlerSupport extends DefaultHandler {

	private static final BundleContext CONTEXT;

	static {
		Bundle bundle = FrameworkUtil.getBundle(XMLRootHandler.class);
		CONTEXT = bundle == null ? null : bundle.getBundleContext();
	}

	private final Set<String> namespaces;
	private boolean namespaceFound = false;

	public FindNamespaceHandlerSupport(Set<String> namespaces) {
		this.namespaces = namespaces;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		checkNamespace(uri);
	}

	protected void checkNamespace(String uri) throws StopParsingException {
		if (!namespaceFound && uri != null && namespaces.contains(uri)) {
			namespaceFound = true;
			throw new StopParsingException();
		}
	}

	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		super.startPrefixMapping(prefix, uri);
		checkNamespace(uri);
	}

	public boolean isNamespaceFound() {
		return namespaceFound;
	}

	public boolean parseContents(InputSource contents) throws IOException, ParserConfigurationException, SAXException {
		if (CONTEXT != null) {
			ServiceReference<SAXParserFactory> serviceReference = CONTEXT.getServiceReference(SAXParserFactory.class);
			// Parse the file into we have what we need (or an error occurs).
			try {
				SAXParserFactory factory = CONTEXT.getService(serviceReference);
				if (factory == null)
					return false;
				final SAXParser parser = createParser(factory);
				// to support external entities specified as relative URIs (see bug 63298)
				contents.setSystemId("/"); //$NON-NLS-1$
				parser.parse(contents, this);
			} catch (StopParsingException e) {
				// Abort the parsing normally. Fall through...
			} finally {
				CONTEXT.ungetService(serviceReference);
			}
			return true;
		} else {
			return false;
		}
	}

	private final SAXParser createParser(SAXParserFactory parserFactory)
			throws ParserConfigurationException, SAXException {
		parserFactory.setNamespaceAware(true);
		final SAXParser parser = parserFactory.newSAXParser();
		final XMLReader reader = parser.getXMLReader();
		// reader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
		// //$NON-NLS-1$
		// disable DTD validation (bug 63625)
		try {
			// be sure validation is "off" or the feature to ignore DTD's will not apply
			reader.setFeature("http://xml.org/sax/features/validation", false); //$NON-NLS-1$
			reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
		} catch (SAXNotRecognizedException | SAXNotSupportedException e) {
			FoundationCoreActivator.pluginLog()
					.logWarning("Exception while trying to determine if the file is an Apache Camel one.", e);
		}
		return parser;
	}

}