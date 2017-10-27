/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.core.xml.namespace;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.internal.content.ContentMessages;
import org.eclipse.core.internal.runtime.RuntimeLog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.XMLContentDescriber;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public abstract class NamespaceXmlContentDescriberSupport extends XMLContentDescriber {

	static final String RESULT = "org.fusesource.ide.commons.contenttype.XmlContentDescriber.result";
	static final String FOUND = "org.fusesource.ide.commons.contenttype.XmlContentDescriber.found";

	static boolean isProcessed(Map properties) {
		Boolean result = (Boolean) properties.get(RESULT);
		// It can be set to false which means that content can't be parsed
		return result != null;
	}

	private int checkCriteria(InputSource contents, Map properties) throws IOException {
		if (!isProcessed(properties))
			fillContentProperties(contents, properties);
		return checkCriteria(properties);
	}

	private int checkCriteria(Map properties) {
		Boolean result = (Boolean) properties.get(RESULT);
		if (!result.booleanValue())
			return INDETERMINATE;
		
		Boolean found = (Boolean) properties.get(FOUND);
		if (found == null || !found.booleanValue())
			return INDETERMINATE;
	
		// We must be okay then.		
		return VALID;
	}

	@Override
	public int describe(InputStream contents, IContentDescription description) throws IOException {
		return describe(contents, description, new HashMap<Object, Object>());
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public int describe(InputStream contents, IContentDescription description, Map properties) throws IOException {
		// call the basic XML describer to do basic recognition
		if (super.describe(contents, description) == INVALID)
			return INVALID;
		// super.describe will have consumed some chars, need to rewind		
		contents.reset();
		// Check to see if we matched our criteria.		
		return checkCriteria(new InputSource(contents), properties);
	}

	@Override
	public int describe(Reader contents, IContentDescription description) throws IOException {
		return describe(contents, description, new HashMap<Object, Object>());
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public int describe(Reader contents, IContentDescription description, Map properties) throws IOException {
		// call the basic XML describer to do basic recognition
		if (super.describe(contents, description) == INVALID)
			return INVALID;
		// super.describe will have consumed some chars, need to rewind
		contents.reset();
		// Check to see if we matched our criteria.
		return checkCriteria(new InputSource(contents), properties);
	}

	public NamespaceXmlContentDescriberSupport() {
		super();
	}

	@SuppressWarnings("unchecked")
	void fillContentProperties(InputSource input, Map properties) throws IOException {
		FindNamespaceHandlerSupport xmlHandler = createNamespaceFinder();
		try {
			if (!xmlHandler.parseContents(input)) {
				properties.put(RESULT, Boolean.FALSE);
				return;
			}
		} catch (SAXException e) {
			// we may be handed any kind of contents... it is normal we fail to parse
			properties.put(RESULT, Boolean.FALSE);
			return;
		} catch (ParserConfigurationException e) {
			// some bad thing happened - force this describer to be disabled
			String message = ContentMessages.content_parserConfiguration;
			RuntimeLog.log(new Status(IStatus.ERROR, ContentMessages.OWNER_NAME, 0, message, e));
			throw new RuntimeException(message);
		}
		Boolean found = xmlHandler.isNamespaceFound() ? Boolean.TRUE : Boolean.FALSE;
		properties.put(FOUND, found);
		properties.put(RESULT, Boolean.TRUE);
	}

	protected abstract FindNamespaceHandlerSupport createNamespaceFinder();

}