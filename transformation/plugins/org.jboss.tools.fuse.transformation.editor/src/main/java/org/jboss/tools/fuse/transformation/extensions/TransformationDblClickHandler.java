/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.extensions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.fusesource.ide.camel.editor.provider.ext.ICustomDblClickHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
import org.fusesource.ide.foundation.ui.util.DialogUtils;
import org.jboss.tools.fuse.transformation.core.camel.EndpointHelper;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.util.Util;

/**
 * @author brianf
 *
 */
public class TransformationDblClickHandler implements ICustomDblClickHandler {

	public TransformationDblClickHandler() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomDblClickHandler#canHandle(org.fusesource.ide.camel.model.service.core.model.CamelModelElement)
	 */
	@Override
	public boolean canHandle(CamelModelElement clickedNode) {
		if (clickedNode.isEndpointElement()) {
			String uri = (String) clickedNode.getParameter("uri");
			if (uri != null && uri.trim().length()>0 && uri.trim().toLowerCase().startsWith("ref:")) {
				String id = uri.substring("ref:".length());
				if (id != null) {
					String refUri = (String) clickedNode.getCamelContext().getEndpointDefinitions().get(id).getParameter("uri");
					if (refUri != null && refUri.startsWith("dozer:")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * extracts a uri parameter from a given uri
	 * 
	 * @param uri	the uri to search parameter
	 * @param key	the key of the parameter
	 * @return	the parameter value or null
	 */
	private String getEndpointParameter(String uri, String key) {
		String value = null;
		StringBuilder uriStr = new StringBuilder(uri);
		if (uriStr.indexOf(key + "=") == -1) {
			return null;
		}
		int startIdx = uriStr.indexOf(key);
		int endIdx = uriStr.indexOf("&", startIdx);
		if (endIdx == -1) {
			value = uriStr.substring(startIdx + (key + '=').length());
		} else {
			value = uriStr.substring(startIdx + (key + '=').length(), endIdx); 
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomDblClickHandler#handleDoubleClick(org.fusesource.ide.camel.model.service.core.model.CamelModelElement)
	 */
	@Override
	public void handleDoubleClick(CamelModelElement clickedNode) {
		if (clickedNode.isEndpointElement()) {
			String id = ((String) clickedNode.getParameter("uri")).substring("ref:".length());
			if (id != null) {
				String refUri = (String) clickedNode.getCamelContext().getEndpointDefinitions().get(id).getParameter("uri");
				if (refUri != null && refUri.startsWith("dozer:")) {
					String filename = getEndpointParameter(refUri, EndpointHelper.MAPPING_FILE);

					// Open mapping editor
					final IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getEditors(filename,
							Platform.getContentTypeManager().getContentType(DozerConfigContentTypeDescriber.ID))[0];

					IResource res = clickedNode.getCamelFile().getResource();
					try {
						res.getProject().refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
						IPath tempPath = new Path(filename);
						IFile xmlFile = res.getProject().getFile(tempPath);
						if (xmlFile != null && !xmlFile.exists()) {
							tempPath = new Path(Util.RESOURCES_PATH + filename);
							xmlFile = res.getProject().getFile(tempPath);
							if (xmlFile != null && !xmlFile.exists()) {
								MessageDialog.openError(Display.getCurrent().getActiveShell(),
										"Transformation File Not Accessible", "The Transformation file (" + filename
												+ ") is not accessible in the Camel project.");
								return;
							}
						}

						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
								.openEditor(new FileEditorInput(xmlFile), desc.getId());
					} catch (Exception e) {
						DialogUtils.showUserError(Activator.plugin().getBundle().getSymbolicName(),
								"Exception Opening Transformation File",
								"The Transformation file (" + filename + ") is not accessible in the Camel project.",
								e);
						Activator.error(e);
					}
				}
			}
		}
	}
}
