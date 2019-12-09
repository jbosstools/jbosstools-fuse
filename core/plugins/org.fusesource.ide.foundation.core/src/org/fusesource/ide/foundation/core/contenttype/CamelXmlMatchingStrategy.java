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

package org.fusesource.ide.foundation.core.contenttype;

import org.fusesource.ide.foundation.core.xml.namespace.FindCamelNamespaceHandler;

/**
 * Detects the Camel namespace in an XML document to determine if we should open
 * with Rider
 */
public class CamelXmlMatchingStrategy extends XmlMatchingStrategySupport  {

	@Override
	protected FindCamelNamespaceHandler createNamespaceFinder() {
		return new FindCamelNamespaceHandler();
	}
}
