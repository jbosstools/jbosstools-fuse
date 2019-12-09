/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.fuse.transformation.editor.internal.dozer;

import org.fusesource.ide.foundation.core.contenttype.XmlMatchingStrategySupport;
import org.fusesource.ide.foundation.core.xml.namespace.FindNamespaceHandlerSupport;

/**
 * @author bfitzpat
 */
public class DozerXmlMatchingStrategy extends XmlMatchingStrategySupport {

	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.contenttype.XmlMatchingStrategySupport#createNamespaceFinder()
	 */
	@Override
	protected FindNamespaceHandlerSupport createNamespaceFinder() {
		return new DozerNamespaceHandler();
	}
}
