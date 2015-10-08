/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.extensions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;

/**
 *
 */
public class DozerConfigContentTypeDescriber implements ITextContentDescriber {

    /**
     *
     */
    public static final String ID = "org.jboss.tools.fuse.transformation.editor.dozer-config-content-type";

    private static final QualifiedName[] SUPPORTED_OPTIONS =
            new QualifiedName[] {IContentDescription.CHARSET};
    private static final String ROOT_ELEMENT = "<mappingsxmlns=";
    private static final String XMLNS = "http://dozer.sourceforge.net";

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.content.IContentDescriber#describe(java.io.InputStream,
     *      org.eclipse.core.runtime.content.IContentDescription)
     */
    @Override
    public int describe(final InputStream contents,
            final IContentDescription description) throws IOException {
        return describe(new InputStreamReader(contents), description);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.content.ITextContentDescriber#describe(java.io.Reader,
     *      org.eclipse.core.runtime.content.IContentDescription)
     */
    @Override
    public int describe(final Reader contents,
            final IContentDescription description) throws IOException {
        final char[] buf = new char[200];
        contents.read(buf);
        final String text = String.valueOf(buf).replaceAll("\\s", "");
        final int ndx = text.indexOf(ROOT_ELEMENT);
        return ndx > 0 && text.indexOf(XMLNS) > ndx ? ITextContentDescriber.VALID
                : ITextContentDescriber.INVALID;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.content.IContentDescriber#getSupportedOptions()
     */
    @Override
    public QualifiedName[] getSupportedOptions() {
        return SUPPORTED_OPTIONS;
    }
}
