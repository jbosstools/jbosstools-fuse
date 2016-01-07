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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.fusesource.ide.foundation.core.util.IOUtils;
import org.fusesource.ide.foundation.ui.io.CamelXMLEditorInput;
import org.fusesource.ide.jmx.camel.CamelJMXPlugin;
import org.fusesource.ide.jmx.camel.navigator.CamelContextNode;

/**
 * @author lhein
 */
public class CamelContextNodeEditorInput extends CamelXMLEditorInput {
	
	private final CamelContextNode contextNode;

	public CamelContextNodeEditorInput(CamelContextNode contextNode, IFile camelContextTempFile) {
		super(camelContextTempFile);
		this.contextNode = contextNode;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return CamelJMXPlugin.getDefault().getImageDescriptor("camel.png");
	}

	@Override
	public String getName() {
		return "Remote CamelContext: " + contextNode.getContextId();
	}

	@Override
	public String getToolTipText() {
		return getName();
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.foundation.ui.io.CamelXMLEditorInput#onEditorInputSave()
	 */
	@Override
	public void onEditorInputSave() {
		super.onEditorInputSave();
		String xml = null;
		try {
			getCamelContextFile().getProject().refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
			xml = IOUtils.loadText(getCamelContextFile().getContents(), "utf-8");
			pushbackToRemoteContext(xml);
		} catch (Exception ex) {
			CamelJMXPlugin.getLogger().error("Error saving changes to remote camel context " + contextNode.getContextId(), ex);
		}
	}
	
	public void pushbackToRemoteContext(String xml) {
		contextNode.updateXml(xml);
	}
}
