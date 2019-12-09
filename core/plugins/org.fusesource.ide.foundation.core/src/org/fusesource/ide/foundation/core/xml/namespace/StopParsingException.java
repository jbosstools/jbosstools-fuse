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

import org.xml.sax.SAXException;

/**
 * An exception indicating that the parsing should stop. This is usually
 * triggered when the top-level element has been found.
 */
public class StopParsingException extends SAXException {
	/**
	 * All serializable objects should have a stable serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an instance of <code>StopParsingException</code> with a
	 * <code>null</code> detail message.
	 */
	public StopParsingException() {
		super((String) null);
	}
}