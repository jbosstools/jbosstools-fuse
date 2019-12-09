/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.commons.messages.contenttype;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.fusesource.ide.foundation.core.xml.namespace.FindNamespaceHandlerSupport;


public class FindMessagesNamespaceHandler extends FindNamespaceHandlerSupport {

	private static String[] camelNamesapceArray = new String[]{ "http://fabric.fusesource.org/schema/messages" };
	private static final Set<String> camelNamespaces = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(camelNamesapceArray)));
	
	public FindMessagesNamespaceHandler() {
		super(camelNamespaces);
	}
}