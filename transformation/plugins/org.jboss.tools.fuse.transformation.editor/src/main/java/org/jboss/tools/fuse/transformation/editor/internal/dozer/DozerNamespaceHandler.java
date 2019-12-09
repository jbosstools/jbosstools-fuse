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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.fusesource.ide.foundation.core.xml.namespace.FindNamespaceHandlerSupport;

/**
 * @author bfitzpat
 */
public class DozerNamespaceHandler extends FindNamespaceHandlerSupport {

	private static String[] dozerNamespaces = new String[]{ "http://dozermapper.github.io/schema/bean-mapping" }; //$NON-NLS-1$
	private static final Set<String> dzrNamespaces =
	    Collections.unmodifiableSet(new HashSet<>(Arrays.asList(dozerNamespaces)));

	public DozerNamespaceHandler() {
		super(dzrNamespaces);
	}
}
