/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import org.eclipse.core.resources.IFile;
import org.fusesource.ide.foundation.core.contenttype.XmlMatchingStrategySupport;
import org.fusesource.ide.foundation.core.util.IFiles;
import org.fusesource.ide.foundation.core.xml.namespace.FindNamespaceHandlerSupport;
import org.xml.sax.InputSource;

/**
 * Matcher to see if the file parses as XML.
 * @author brianf
 *
 */
public class XmlMatchingStrategy extends XmlMatchingStrategySupport {

	@Override
	protected FindNamespaceHandlerSupport createNamespaceFinder() {
		return new FindNamespaceHandlerSupport(new HashSet<String>());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.fusesource.ide.foundation.core.contenttype.XmlMatchingStrategySupport#matches(org.eclipse.core.resources.IFile)
	 */
	@Override
    public boolean matches(IFile ifile) {
		try {
			File file = IFiles.toFile(ifile);
			if (file != null) {
				// lets parse the XML and look for the namespaces
				FindNamespaceHandlerSupport handler = createNamespaceFinder();
				try (FileInputStream stream = new FileInputStream(file)) {
				    return handler.parseContents(new InputSource(stream));
				}
			}
		} catch (Exception e) {
			// ignore anything that's not XML
		}
		return false;
	}

}
