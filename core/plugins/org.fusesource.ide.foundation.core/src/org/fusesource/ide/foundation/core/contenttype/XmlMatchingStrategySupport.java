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

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.fusesource.ide.foundation.core.xml.namespace.FindNamespaceHandlerSupport;


public abstract class XmlMatchingStrategySupport implements IEditorMatchingStrategy {

	@Override
	public boolean matches(IEditorReference editorRef, IEditorInput input) {
		if (input instanceof IFileEditorInput) {
			IFile ifile = ((IFileEditorInput) input).getFile();
			return matches(ifile);
		}
		return false;
	}

	public boolean matches(IFile ifile) {
		FindNamespaceHandlerSupport handler = createNamespaceFinder();
		return CamelUtils.matches(handler, ifile);
	}

	protected abstract FindNamespaceHandlerSupport createNamespaceFinder();

}