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
package org.fusesource.ide.jmx.camel.editor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.foundation.core.util.IOUtils;
import org.fusesource.ide.foundation.ui.io.CamelXMLEditorInput;
import org.fusesource.ide.jmx.camel.CamelJMXPlugin;
import org.fusesource.ide.jmx.camel.navigator.CamelContextNode;

/**
 * @author lhein
 */
public class CamelContextNodeEditorInput extends CamelXMLEditorInput {
	
	private final CamelContextNode contextNode;
	private String contextId;

	public CamelContextNodeEditorInput(CamelContextNode contextNode, IFile camelContextTempFile) {
		super(camelContextTempFile, null);
		this.contextNode = contextNode;
		this.contextId = contextNode.getContextId();
		setSelectedContainerId(contextId);
	}

	@Override
	public String getName() {
		String name = "Remote CamelContext: " + contextId;
		if(contextNode.isConnectionAvailable()){
			return "<connected>" + name;
		} else {
			return "<disconnected>" + name;
		}
	}

	@Override
	public String getToolTipText() {
		return getName();
	}

	@Override
	public void onEditorInputSave() {
		super.onEditorInputSave();
		if(contextNode.isConnectionAvailable()){
			try {
				getCamelContextFile().getProject().refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
				String xml = IOUtils.loadText(getCamelContextFile().getContents(), StandardCharsets.UTF_8.name());
				contextNode.updateXml(xml);
			} catch (IOException | CoreException ex) {
				CamelJMXPlugin.getLogger().error("Error saving changes to remote camel context " + this.contextId, ex);
			}
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		contextNode.dispose();
	}
	
}
