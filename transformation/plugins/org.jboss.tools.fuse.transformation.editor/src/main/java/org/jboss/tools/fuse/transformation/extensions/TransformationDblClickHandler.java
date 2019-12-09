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
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteContainerElement;
import org.fusesource.ide.foundation.ui.util.DialogUtils;
import org.jboss.tools.fuse.transformation.core.camel.EndpointHelper;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;

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
	public boolean canHandle(AbstractCamelModelElement clickedNode) {
		if (clickedNode.isEndpointElement()) {
			String uri = (String) clickedNode.getParameter("uri"); //$NON-NLS-1$
			if (uri != null && uri.trim().length()>0 && uri.trim().toLowerCase().startsWith("ref:")) { //$NON-NLS-1$
				String id = uri.substring("ref:".length()); //$NON-NLS-1$
				if (id != null) {
					CamelRouteContainerElement container = clickedNode.getRouteContainer();
					if (container instanceof CamelContextElement) {
						String refUri = (String) ((CamelContextElement)container).getEndpointDefinitions().get(id).getParameter("uri"); //$NON-NLS-1$
						if (refUri != null && refUri.startsWith("dozer:")) { //$NON-NLS-1$
							return true;
						}
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
		StringBuilder uriStr = new StringBuilder(uri);
		if (uriStr.indexOf(key + "=") == -1) { //$NON-NLS-1$
			return null;
		}
		int startIdx = uriStr.indexOf(key);
		int endIdx = uriStr.indexOf("&", startIdx); //$NON-NLS-1$
		if (endIdx == -1) {
			return uriStr.substring(startIdx + (key + '=').length());
		} else {
			return uriStr.substring(startIdx + (key + '=').length(), endIdx);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomDblClickHandler#handleDoubleClick(org.fusesource.ide.camel.model.service.core.model.CamelModelElement)
	 */
	@Override
	public void handleDoubleClick(AbstractCamelModelElement clickedNode) {
		if (clickedNode.isEndpointElement()) {
			String id = ((String) clickedNode.getParameter("uri")).substring("ref:".length()); //$NON-NLS-1$ //$NON-NLS-2$
			if (id != null) {
				CamelRouteContainerElement container = clickedNode.getRouteContainer();
				if (container instanceof CamelContextElement) {
					String refUri = (String) ((CamelContextElement)container).getEndpointDefinitions().get(id).getParameter("uri"); //$NON-NLS-1$
					if (refUri != null && refUri.startsWith("dozer:")) { //$NON-NLS-1$
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
								tempPath = new Path(MavenUtils.RESOURCES_PATH + filename);
								xmlFile = res.getProject().getFile(tempPath);
								if (xmlFile != null && !xmlFile.exists()) {
									MessageDialog.openError(Display.getCurrent().getActiveShell(),
											Messages.TransformationDblClickHandler_ErrorDialogTitle_TransdformationFileNotAccessible,
											Messages.bind(Messages.TransformationDblClickHandler_errormessageCamelFileNotAccessible, filename));
									return;
								}
							}
	
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
									.openEditor(new FileEditorInput(xmlFile), desc.getId());
						} catch (Exception e) {
							DialogUtils.showUserError(Activator.plugin().getBundle().getSymbolicName(),
									Messages.TransformationDblClickHandler_ErroDialogTitle_ExceptionOpeningTransformationFile,
									Messages.bind(Messages.TransformationDblClickHandler_errormessageCamelFileNotAccessible, filename),
									e);
							Activator.error(e);
						}
					}
				}
			}
		}
	}
}
