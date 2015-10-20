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
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.commons.contenttype.FindNamespaceHandlerSupport;
import org.fusesource.ide.commons.contenttype.XmlMatchingStrategySupport;
import org.fusesource.ide.commons.util.IFiles;
import org.xml.sax.InputSource;

/**
 * Present a file chooser dialog that only lists those files that are on
 * the classpath and are XML based (i.e. can be parsed by the SAX parser).
 * @author brianf
 *
 */
public class ClasspathXMLResourceSelectionDialog extends ClasspathResourceSelectionDialog {

    public ClasspathXMLResourceSelectionDialog(Shell parentShell, IContainer container, Set<String> fileExtensions,
			String title) {
		super(parentShell, container, fileExtensions, title);
	}

    @Override
    protected ItemsFilter createFilter() {
        return new XMLClasspathResourceFilter();
    }

    class XMLClasspathResourceFilter extends ClasspathResourceFilter {

        @Override
        public boolean matchItem(Object item) {
        	boolean matches = super.matchItem(item);
        	XmlMatchingStrategy strategy = new XmlMatchingStrategy();
        	boolean isXml = false;
        	if (item instanceof IFile) {
        		isXml = strategy.matches((IFile) item);
        	}
        	return matches && isXml;
        }

        @Override
        public boolean equalsFilter(ItemsFilter filter) {
            return filter instanceof XMLClasspathResourceFilter && super.equalsFilter(filter);
        }
    }
	
    /**
     * Matcher to see if the file parses as XML. 
     * @author brianf
     *
     */
    class XmlMatchingStrategy extends XmlMatchingStrategySupport {

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
}
