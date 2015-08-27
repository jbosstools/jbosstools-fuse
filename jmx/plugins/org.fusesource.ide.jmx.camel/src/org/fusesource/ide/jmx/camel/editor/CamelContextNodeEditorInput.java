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

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.part.FileEditorInput;
import org.fusesource.ide.camel.model.Activator;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.io.ICamelEditorInput;
import org.fusesource.ide.foundation.core.util.IOUtils;
import org.fusesource.ide.jmx.camel.CamelJMXPlugin;
import org.fusesource.ide.jmx.camel.Messages;
import org.fusesource.ide.jmx.camel.navigator.CamelContextNode;


public class CamelContextNodeEditorInput implements ICamelEditorInput {
	public static final String ID = CamelContextNodeEditorInput.class.getName();

	private final CamelContextNode contextNode;

	private IFileEditorInput fileEditorInput;
	private File tempFile;
	private IFile lastSaveAsFile;

	public CamelContextNodeEditorInput(CamelContextNode contextNode) {
		this.contextNode = contextNode;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		// the following code is needed otherwise you can't edit remote routes
		if (adapter.equals(IFile.class)) {
			getFileEditorInput();
			IPath p = Path.fromOSString(tempFile.getPath());
			IFile file= ResourcesPlugin.getWorkspace().getRoot().getFile(p);
			return file;
		}
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return CamelJMXPlugin.getDefault().getImageDescriptor("camel.png");
	}

	@Override
	public String getName() {
		return "CamelContext: " + contextNode.getContextId();
	}

	@Override
	public IPersistableElement getPersistable() {
		return new IPersistableElement() {

			@Override
			public void saveState(IMemento memento) {
				// TODO how to get the document input...
			}

			@Override
			public String getFactoryId() {
				return ID;
			}
		};
	}

	@Override
	public String getToolTipText() {
		return Messages.camelContextNodeEditRouteToolTip;
	}

	@Override
	public RouteContainer loadModel() {
		return contextNode.getModelContainer();
	}


	@Override
	public void save(String xml) {
		contextNode.updateXml(xml);
	}

	@Override
	public IEditorInput getFileEditorInput() {
		if (lastSaveAsFile != null) {
			this.fileEditorInput = new FileEditorInput(lastSaveAsFile);
		}
		if (this.fileEditorInput == null) {
			try {
				// lets create a temporary file...
				File tempDir = File.createTempFile("FuseIDE-camel-context-" + contextNode.getContextId() + "-", "");
				tempDir.delete();
				tempDir.mkdirs();
				if (tempFile == null) {
					tempFile = File.createTempFile("camelContext-", ".xml", tempDir);
				}
				// lets create the IFile from it...
				IFileSystem fileSystem = EFS.getLocalFileSystem();
				IFileStore fileStore = fileSystem.fromLocalFile(tempFile);

				String xml = contextNode.getXmlString();
				IOUtils.writeText(tempFile, xml);

				return new CamelFileStoreEditorInput(fileStore, contextNode);
			} catch (Exception e) {
				Activator.getLogger().warning("Failed to create temporary file: " + e, e);
			}
		}
		return fileEditorInput;
	}

	@Override
	public IFile getLastSaveAsFile() {
		return lastSaveAsFile;
	}

	@Override
	public void setLastSaveAsFile(IFile lastSaveAsFile) {
		this.lastSaveAsFile = lastSaveAsFile;
	}
}
