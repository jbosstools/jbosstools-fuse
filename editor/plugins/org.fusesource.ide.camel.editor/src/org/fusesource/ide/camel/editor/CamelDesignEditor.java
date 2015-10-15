/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.fusesource.ide.camel.editor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.fusesource.ide.camel.editor.behaviours.CamelDiagramBehaviour;
import org.fusesource.ide.camel.editor.internal.CamelDesignEditorFlyoutPaletteComposite;
import org.fusesource.ide.camel.model.io.ICamelEditorInput;
import org.fusesource.ide.camel.model.io.IRemoteCamelEditorInput;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.foundation.core.util.IOUtils;

/*
 * @author lhein
 */
public class CamelDesignEditor extends DiagramEditor {
	
	private CamelEditor parent;
	private IProject workspaceProject;
	private CamelDiagramBehaviour camelDiagramBehaviour;
	private CamelDesignEditorFlyoutPaletteComposite paletteComposite;
	private CamelFile model;
	
	/**
	 * 
	 * @param parent
	 */
	public CamelDesignEditor(CamelEditor parent) {
		this.parent = parent;
//		DebugPlugin.getDefault().addDebugEventListener(this);
//		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
//			ISelectionService sel = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
//			if (sel != null) sel.addSelectionListener(ICamelDebugConstants.DEBUG_VIEW_ID, this);			
//		}
//		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramEditor#createDiagramBehavior()
	 */
	@Override
	protected DiagramBehavior createDiagramBehavior() {
		this.camelDiagramBehaviour = new CamelDiagramBehaviour(this);
		return this.camelDiagramBehaviour;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramEditor#getDiagramBehavior()
	 */
	@Override
	public DiagramBehavior getDiagramBehavior() {
		return this.camelDiagramBehaviour;
	}
	
    /**
     * Create a FlyoutPaletteComposite the will used to show a flyout palette
     * alongside the editor.
     * 
     * @param parent
     *            The parent composite hosting the FlyoutPaletteComposite.
     * @return a newly-created {@link FlyoutPaletteComposite}
     */
    @Override
	protected FlyoutPaletteComposite createPaletteComposite(Composite parent) {
        paletteComposite = new CamelDesignEditorFlyoutPaletteComposite(parent, SWT.NONE, getSite()
                .getPage(), getPaletteViewerProvider(), getPalettePreferences());
        paletteComposite.getFilter().addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                getDiagramBehavior().refreshPalette();
            }
        });
        return paletteComposite;
    }
    
	/**
	 * @return the model
	 */
	public CamelFile getModel() {
		return this.model;
	}
	
	/**
	 * @param model the model to set
	 */
	public void setModel(CamelFile model) {
		this.model = model;
	}
	
    /**
	 * @return the workspaceProject
	 */
	public IProject getWorkspaceProject() {
		return this.workspaceProject;
	}
	
	/**
	 * @param workspaceProject the workspaceProject to set
	 */
	public void setWorkspaceProject(IProject workspaceProject) {
		this.workspaceProject = workspaceProject;
	}
	
	/**
	 * returns the filter string entered in palette search box
	 * 
	 * @return
	 */
	public String getPaletteFilter() {
        if (paletteComposite != null) {
            return paletteComposite.getFilter().getText();
        }
        return null;
    }
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public IFileEditorInput asFileEditorInput(IEditorInput input) {
		if (input instanceof IFileEditorInput) {
			return (IFileEditorInput) input;
		} else if (input instanceof ICamelEditorInput) {
			ICamelEditorInput camelEditorInput = (ICamelEditorInput) input;
			IEditorInput fileEditorInput = camelEditorInput.getFileEditorInput();
			if (fileEditorInput instanceof IFileEditorInput) {
				return (IFileEditorInput) fileEditorInput;
			} else if (fileEditorInput instanceof FileStoreEditorInput) {
				return new FileEditorInput((IFile)input.getAdapter(IFile.class));
			}
		} else if (input instanceof IDiagramEditorInput) {
		    org.eclipse.emf.common.util.URI uri = ((IDiagramEditorInput)input).getUri();
		    if (uri.isPlatformResource()) {
		        return new FileEditorInput(ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(uri.toPlatformString(true))));
		    }
		}
		return null;
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public IRemoteCamelEditorInput asRemoteCamelEditorInput(IEditorInput input) {
		if (input instanceof IRemoteCamelEditorInput) {
			return (IRemoteCamelEditorInput) input;
		} else if (input instanceof ICamelEditorInput) {
			ICamelEditorInput camelEditorInput = (ICamelEditorInput) input;
			IEditorInput fileEditorInput = camelEditorInput.getFileEditorInput();
			if (fileEditorInput instanceof IRemoteCamelEditorInput) {
				return (IRemoteCamelEditorInput) fileEditorInput;
			}
		} else if (input instanceof IURIEditorInput) {
			final IURIEditorInput uriInput = (IURIEditorInput) input;
			return new IRemoteCamelEditorInput() {

				@Override
				public String getUriText() {
					return uriInput.getName();
				}

				@Override
				public String getXml() throws IOException {
					return IOUtils.loadText(uriInput.getURI().toURL().openStream(), "UTF-8");
				}
			};
        } else if (input instanceof DiagramEditorInput) {
            final DiagramEditorInput uriInput = (DiagramEditorInput) input;
            return new IRemoteCamelEditorInput() {

                @Override
                public String getUriText() {
                    return uriInput.getName();
                }

                @Override
                public String getXml() throws IOException {
                    try {
                        return IOUtils.loadText(new URI(uriInput.getUri().toString()).toURL().openStream(), "UTF-8");
                    } catch (URISyntaxException e) {
                        throw new IOException("Unable to resolve resource.", e);
                    }
                }
            };
		}
		return null;
	}

	public IFeatureProvider getFeatureProvider() {
		if (getDiagramTypeProvider() != null) return getDiagramTypeProvider().getFeatureProvider();
		return null;
	}
	
	/**
	 * @return the parent
	 */
	public CamelEditor getParent() {
		return this.parent;
	}
}
