/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.foundation.ui.archetypes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class SimpleNamespaceContext implements NamespaceContext {

    private Map<String, String> prefix2Ns = new HashMap<String, String>();
    private Map<String, String> ns2Prefix = new HashMap<String, String>();

    public SimpleNamespaceContext() {
        prefix2Ns.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        ns2Prefix.put(XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX);
        prefix2Ns.put(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        ns2Prefix.put(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE);
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return prefix2Ns.get(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        return ns2Prefix.get(namespaceURI);
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        return prefix2Ns.keySet().iterator();
    }

    /**
     * Registers prefix - namaspace URI mapping
     *
     * @param prefix
     * @param namespaceURI
     */
    public void registerMapping(String prefix, String namespaceURI) {
        prefix2Ns.put(prefix, namespaceURI);
        ns2Prefix.put(namespaceURI, prefix);
    }

}