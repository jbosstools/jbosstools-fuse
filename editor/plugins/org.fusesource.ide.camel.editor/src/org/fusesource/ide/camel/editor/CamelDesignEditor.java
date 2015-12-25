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

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointsListener;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.fusesource.ide.camel.editor.behaviours.CamelDiagramBehaviour;
import org.fusesource.ide.camel.editor.commands.DiagramOperations;
import org.fusesource.ide.camel.editor.internal.CamelDesignEditorFlyoutPaletteComposite;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.utils.INodeViewer;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.camel.model.service.core.model.ICamelModelListener;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.foundation.ui.io.CamelXMLEditorInput;
import org.fusesource.ide.launcher.debug.model.CamelStackFrame;
import org.fusesource.ide.launcher.debug.model.CamelThread;
import org.fusesource.ide.launcher.debug.util.CamelDebugRegistry;
import org.fusesource.ide.launcher.debug.util.CamelDebugRegistryEntry;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;

/*
 * @author lhein
 */
public class CamelDesignEditor extends DiagramEditor implements ISelectionListener, 
																INodeViewer, 
																ICamelModelListener, 
																IDebugEventSetListener, 
																IBreakpointsListener {
	
	private CamelEditor parent;
	private IProject workspaceProject;
	private CamelDiagramBehaviour camelDiagramBehaviour;
	private CamelDesignEditorFlyoutPaletteComposite paletteComposite;
	private CamelFile model;
	private CamelRouteElement selectedRoute;
	private CamelModelElement highlightedNodeInDebugger;
	private AbstractEditPart selectedEditPart;
	private AbstractEditPart lastSelectedEditPart;
	
	/**
	 * 
	 * @param parent
	 */
	public CamelDesignEditor(CamelEditor parent) {
		this.parent = parent;
		DebugPlugin.getDefault().addDebugEventListener(this);
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
			ISelectionService sel = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
			if (sel != null) sel.addSelectionListener(ICamelDebugConstants.DEBUG_VIEW_ID, this);			
		}
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramEditor#dispose()
	 */
	@Override
	public void dispose() {
		getModel().removeModelListener(this);
		super.dispose();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramEditor#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);

		/**
         * the following is needed because we miss the first debug events and the first breakpoint wouldn't be highlighted otherwise
         */
        try {
	        for (CamelDebugRegistryEntry entry : CamelDebugRegistry.getInstance().getEntries().values()) {
	        	IEditorInput ips = entry.getEditorInput();
				IResource f = this.model.getResource();
	        	if (f.getFullPath().toFile().getPath().equals(asFileEditorInput(input).getFile().getFullPath().toFile().getPath())) {
	        		String endpointId = null;
	        		
	        		// first highligth the suspended node
	        		Set<String> ids = entry.getDebugTarget().getDebugger().getSuspendedBreakpointNodeIds();
	        		if (ids != null && ids.size()>0) {
	        			endpointId = ids.iterator().next();
	        		}
	        		highlightBreakpointNodeWithID(endpointId);
	        	}
	        }
        } catch (Exception ex) {
        	CamelEditorUIActivator.pluginLog().logError(ex);
        }
        /**
         * End of the highlighting code
         */
        
		getEditingDomain().getCommandStack().flush();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramEditor#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return parent.isDirty();
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
    
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramEditor#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// If not the active editor, ignore selection changed.
		if (getSite().getPage().getActiveEditor() == this.parent && this.parent.getActiveEditor() == this) {
			updateActions(getSelectionActions());

			if (selection instanceof StructuredSelection) {
				StructuredSelection structuredSelection = (StructuredSelection) selection;
				Object firstElement = structuredSelection.getFirstElement();
				if (firstElement instanceof AbstractEditPart) {
					this.selectedEditPart = (AbstractEditPart) firstElement;
					CamelModelElement node = NodeUtils.toCamelElement(firstElement);
					if (node != null) {
//						if (data.selectedNode != null) {
//							data.selectedNode
//							.removePropertyChangeListener(nodePropertyListener);
//						}
//						data.selectedNode = node;
//						data.selectedNode
//						.addPropertyChangeListener(nodePropertyListener);
					}
					if (selectedEditPart != null) {
						lastSelectedEditPart = selectedEditPart;
					}
				} else if (firstElement instanceof CamelStackFrame) {
					CamelStackFrame stackFrame = (CamelStackFrame)firstElement;
					highlightBreakpointNodeWithID(stackFrame.getEndpointId());
				} else if (firstElement instanceof CamelThread) {
					CamelThread t = (CamelThread)firstElement;
					try {
						CamelStackFrame stackFrame = (CamelStackFrame)t.getTopStackFrame();
						if (stackFrame != null) {
							highlightBreakpointNodeWithID(stackFrame.getEndpointId());	
						}
					} catch (DebugException e) {
						CamelEditorUIActivator.pluginLog().logError(e);
					}
				}
			}
		}
	}
	
	public void initializeDiagram(Diagram diagram) {
	    // set the diagram on the container
		IDiagramTypeProvider diagramTypeProvider = getDiagramTypeProvider();
		if (diagramTypeProvider == null)
			return;
		
	    if (diagramTypeProvider.getDiagram() != diagram) {
	    	diagramTypeProvider.resourceReloaded(diagram);
	    }
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
		if (model != null && model.getCamelFile() != null) {
			model.getCamelFile().addModelListener(this);
			model.getCamelFile().addModelListener(getParent().getGlobalConfigEditor());
		}
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
		} else if (input instanceof CamelXMLEditorInput) {
			return new FileEditorInput((IFile)input.getAdapter(IFile.class));
		} else if (input instanceof IDiagramEditorInput) {
			org.eclipse.emf.common.util.URI uri = ((IDiagramEditorInput)input).getUri();
		    if (uri.isPlatformResource()) {
		        return new FileEditorInput(ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(uri.toPlatformString(true))));
		    } else {
		    	FileEditorInput ei = new FileEditorInput(ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(uri.toFileString())));
		    	return ei;
		    }
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
	
	/**
	 * @return the selectedRoute
	 */
	public CamelRouteElement getSelectedRoute() {
		return this.selectedRoute;
	}
	
	/**
	 * @param selectedRoute the selectedRoute to set
	 */
	public void setSelectedRoute(CamelRouteElement selectedRoute) {
		this.selectedRoute = selectedRoute;
	}
	
	public void refreshDiagramContents(Diagram diagram) {
		getDiagramTypeProvider().init(diagram != null ? diagram : getDiagramTypeProvider().getDiagram(), getDiagramBehavior());
		getDiagramBehavior().getRefreshBehavior().initRefresh();
        setPictogramElementsForSelection(null);
        GraphicalViewer graphicalViewer = getGraphicalViewer();
	        
        if (graphicalViewer == null)
        	return;
	        
        // set Diagram as contents for the graphical viewer and refresh
        graphicalViewer.setContents(getDiagramTypeProvider().getDiagram());	        
        getDiagramBehavior().refreshContent();
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.ICamelModelListener#modelChanged()
	 */
	@Override
	public void modelChanged() {
		// we only update if the correct editor tab is selected
		if (getParent().getActivePage() != CamelEditor.DESIGN_PAGE_INDEX) return;
		Display.getDefault().asyncExec(new Runnable() {
			/*
			 * (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				DiagramOperations.updateSelectedNode(CamelDesignEditor.this);
				getDiagramTypeProvider().getDiagramBehavior().refresh();
				if (selectedEditPart != null) {
					selectedEditPart.refresh();
				} else if (lastSelectedEditPart == null) {

				} else {
					lastSelectedEditPart.refresh();
				}
				parent.setDirtyFlag(true);
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointsListener#breakpointsAdded(org.eclipse.debug.core.model.IBreakpoint[])
	 */
	@Override
	public void breakpointsAdded(IBreakpoint[] breakpoints) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				refreshDiagramContents(null);				
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointsListener#breakpointsChanged(org.eclipse.debug.core.model.IBreakpoint[], org.eclipse.core.resources.IMarkerDelta[])
	 */
	@Override
	public void breakpointsChanged(IBreakpoint[] breakpoints,
			IMarkerDelta[] deltas) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				refreshDiagramContents(null);				
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointsListener#breakpointsRemoved(org.eclipse.debug.core.model.IBreakpoint[], org.eclipse.core.resources.IMarkerDelta[])
	 */
	@Override
	public void breakpointsRemoved(IBreakpoint[] breakpoints,
			IMarkerDelta[] deltas) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				refreshDiagramContents(null);				
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent ev : events) {
			if (ev.getSource() instanceof CamelThread == false && ev.getSource() instanceof CamelStackFrame == false) continue;
			if (ev.getSource() instanceof CamelThread && (ev.getKind() == DebugEvent.TERMINATE || ev.getKind() == DebugEvent.RESUME)) {
				// we are only interested in hit camel breakpoints
				resetHighlightBreakpointNode();
			} else {
				if (ev.getSource() instanceof CamelThread) {
					CamelThread thread = (CamelThread)ev.getSource();
					if (ev.getKind() == DebugEvent.SUSPEND && ev.getDetail() == DebugEvent.BREAKPOINT) {
						// a breakpoint was hit and thread is on suspend -> stack should be selected in tree now
						try {
							CamelStackFrame stackFrame = (CamelStackFrame)thread.getTopStackFrame();
							if (stackFrame != null) highlightBreakpointNodeWithID(stackFrame.getEndpointId());
						} catch (DebugException ex) {
							CamelEditorUIActivator.pluginLog().logError(ex);
						}
					}
				}
			}
		}
	}
	
	/**
	 * @return the highlightedNodeInDebugger
	 */
	public synchronized CamelModelElement getHighlightedNodeInDebugger() {
		return this.highlightedNodeInDebugger;
	}
	
	/**
	 * highlights the node currently in breakpoint
	 * 
	 * @param endpointNodeId	the node id
	 */
	private synchronized void highlightBreakpointNodeWithID(String endpointNodeId) {
		// get the correct node for the id
		final CamelModelElement node = getModel().findNode(endpointNodeId);
		
		if (node == null) return;
		if (this.highlightedNodeInDebugger != null && node.getId() != null && node.getId().equals(highlightedNodeInDebugger.getId())) return;

		// reset old highlight
		resetHighlightBreakpointNode();
		
		// add highlight to new node
		setDebugHighLight(node, true);
				
		// remember the new highlighted node
		this.highlightedNodeInDebugger = node;
	}
	
	/**
	 * resets the highlight from the highlighted node
	 */
	private synchronized void resetHighlightBreakpointNode() {
		setDebugHighLight(this.highlightedNodeInDebugger, false);
		this.highlightedNodeInDebugger = null;
	}
	
	/**
	 * switches the debug highlight for a given node on or off
	 * 
	 * @param bo			the node
	 * @param highlight		the highlight status
	 */
	private synchronized void setDebugHighLight(CamelModelElement bo, boolean highlight) {
		if (bo == null) return;
		
		// delegate to an operation command for async transacted diagram update
		DiagramOperations.highlightNode(this, bo, highlight);
	}

	protected RootEditPart getRootEditPart() {
		return getGraphicalViewer().getRootEditPart();
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.utils.INodeViewer#getSelectedNode()
	 */
	@Override
	public CamelModelElement getSelectedNode() {
		return NodeUtils.getSelectedNode(Selections.getSelection(getEditorSite()));
	}
	
	/**
	 * Finds the given node by walking the edit part tree looking for the correct one
	 */
	@SuppressWarnings("unchecked")
	public static EditPart findEditPart(CamelModelElement node, EditPart part) {
		if (part instanceof RootEditPart) {
			RootEditPart root = (RootEditPart) part;
			EditPart contents = root.getContents();
			if (contents != null) {
				return findEditPart(node, contents);
			}
		}
		if (part instanceof EditPart) {
			EditPart nodeEditPart = part;
			CamelModelElement modelNode = NodeUtils.toCamelElement(nodeEditPart);
			if (Objects.equal(node, modelNode)) {
				return nodeEditPart;
			}
		}
		List<EditPart> children = part.getChildren();
		for (EditPart childPart : children) {
			EditPart answer = findEditPart(node, childPart);
			if (answer != null) {
				return answer;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.utils.INodeViewer#setSelectedNode(org.fusesource.ide.camel.model.service.core.model.CamelModelElement)
	 */
	@Override
	public void setSelectedNode(CamelModelElement newSelection) {
		if (newSelection != null) {
			Object selectEditPart = findEditPart(newSelection, getRootEditPart());
			if (selectEditPart != null) {
				getEditorSite().getSelectionProvider().setSelection(new StructuredSelection(selectEditPart));
			} else {
				CamelEditorUIActivator.pluginLog().logError("Could not find editPart for selection: " + newSelection, new Exception());
			}
		}
	}
}
