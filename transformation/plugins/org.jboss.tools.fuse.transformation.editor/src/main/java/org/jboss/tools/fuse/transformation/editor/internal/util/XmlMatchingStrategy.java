/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others. 
 * All rights reserved. This program and the accompanying materials are 
 * made available under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.fusesource.ide.commons.contenttype.FindNamespaceHandlerSupport;
import org.fusesource.ide.commons.contenttype.XmlMatchingStrategySupport;
import org.fusesource.ide.commons.util.IFiles;
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
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.contenttype.XmlMatchingStrategySupport#matches(org.eclipse.core.resources.IFile)
	 */
	public boolean matches(IFile ifile) {
		try {
			File file = IFiles.toFile(ifile);
			if (file != null) {
				// lets parse the XML and look for the namespaces 
				FindNamespaceHandlerSupport handler = createNamespaceFinder();
				boolean canParse = handler.parseContents(new InputSource(new FileInputStream(file)));
				return canParse;
			}
		} catch (Exception e) {
			// ignore anything that's not XML
		}
		return false;
	}

}
