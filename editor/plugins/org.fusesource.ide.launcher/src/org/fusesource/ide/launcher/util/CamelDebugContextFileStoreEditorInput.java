/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.launcher.util;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.fusesource.ide.camel.model.io.IRemoteCamelEditorInput;
import org.fusesource.ide.launcher.debug.model.CamelDebugFacade;

/**
 * @author lhein
 */
public class CamelDebugContextFileStoreEditorInput extends FileStoreEditorInput implements IRemoteCamelEditorInput {

	private final CamelDebugFacade debugger;

	public CamelDebugContextFileStoreEditorInput(IFileStore fileStore, CamelDebugFacade debugger) {
		super(fileStore);
		this.debugger = debugger;
	}

	@Override
	public String getXml() {
		return debugger.getContextXmlDump();
	}

	@Override
	public String getUriText() {
		return getURI().toString();
	}
}
