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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lhein
 */
public class SpringNamespaceHandler extends FindNamespaceHandlerSupport {
	
	public static final String NAMESPACEURI_CAMEL_SPRING = "http://camel.apache.org/schema/spring";
	private static final String[] springNamespaces = new String[]{ NAMESPACEURI_CAMEL_SPRING };
	private static final Set<String> sprNamespaces = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(springNamespaces)));
	
	public SpringNamespaceHandler() {
		super(sprNamespaces);
	}
}
