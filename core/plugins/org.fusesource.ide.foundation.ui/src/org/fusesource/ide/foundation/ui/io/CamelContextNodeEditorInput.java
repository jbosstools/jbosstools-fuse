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
package org.fusesource.ide.foundation.ui.io;

import java.lang.reflect.Method;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.fusesource.ide.foundation.core.util.IOUtils;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;
import org.jboss.tools.jmx.core.tree.Node;

/**
 * @author lhein
 */
public class CamelContextNodeEditorInput extends CamelXMLEditorInput {
	
	private final Node contextNode;
	private String contextId;

	public CamelContextNodeEditorInput(Node contextNode, IFile camelContextTempFile) {
		super(camelContextTempFile, null);
		this.contextNode = contextNode;
		if (contextNode.getClass().getName().equals("org.fusesource.ide.jmx.camel.navigator.CamelContextNode")) {
			try {
				Method m = contextNode.getClass().getMethod("getContextId", (Class[])null);
				Object result = m.invoke(contextNode, (Object[])null);
				if (result instanceof String) {
					this.contextId = (String)result;
					setSelectedContainerId(contextId);
				}
			} catch (Exception ex) {
				FoundationUIActivator.pluginLog().logError(ex.getCause() != null ? ex.getCause() : ex);
			}
		} 
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return FoundationUIActivator.getDefault().getSharedImages().descriptor(FoundationUIActivator.IMAGE_CAMEL_ICON);
	}

	@Override
	public String getName() {
		return "Remote CamelContext: " + contextId;
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
			FoundationUIActivator.pluginLog().logError("Error saving changes to remote camel context " + this.contextId, ex);
		}
	}
	
	public void pushbackToRemoteContext(String xml) {
		if (contextNode.getClass().getName().equals("org.fusesource.ide.jmx.camel.navigator.CamelContextNode")) {
			try {
				Method m = contextNode.getClass().getMethod("updateXml", String.class);
				m.invoke(contextNode, xml);
			} catch (Exception ex) {
				FoundationUIActivator.pluginLog().logError(ex.getCause() != null ? ex.getCause() : ex);
			}
		} 
	}
}
