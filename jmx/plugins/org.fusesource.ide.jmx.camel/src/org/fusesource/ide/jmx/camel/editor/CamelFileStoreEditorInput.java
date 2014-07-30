/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.camel.editor;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.fusesource.ide.camel.model.io.IRemoteCamelEditorInput;
import org.fusesource.ide.jmx.camel.navigator.CamelContextNode;


public class CamelFileStoreEditorInput extends FileStoreEditorInput implements IRemoteCamelEditorInput {

	private final CamelContextNode contextNode;

	public CamelFileStoreEditorInput(IFileStore fileStore, CamelContextNode contextNode) {
		super(fileStore);
		this.contextNode = contextNode;
	}

	@Override
	public String getXml() {
		return contextNode.getXmlString();
	}

	@Override
	public String getUriText() {
		return getURI().toString();
	}

}
