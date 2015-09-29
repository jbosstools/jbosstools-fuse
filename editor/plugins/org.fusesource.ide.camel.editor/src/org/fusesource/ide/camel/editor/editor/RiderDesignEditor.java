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

package org.fusesource.ide.camel.editor.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointsListener;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramBehavior;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;
import org.eclipse.graphiti.ui.internal.util.gef.ScalableRootEditPartAnimated;
import org.eclipse.graphiti.ui.platform.IConfigurationProvider;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.fusesource.ide.camel.editor.AbstractNodes;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.EditorMessages;
import org.fusesource.ide.camel.editor.commands.DiagramOperations;
import org.fusesource.ide.camel.editor.commands.ImportCamelContextElementsCommand;
import org.fusesource.ide.camel.editor.outline.RiderOutlinePage;
import org.fusesource.ide.camel.editor.utils.DiagramUtils;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.generated.Route;
import org.fusesource.ide.camel.model.io.ICamelEditorInput;
import org.fusesource.ide.camel.model.io.IRemoteCamelEditorInput;
import org.fusesource.ide.commons.camel.tools.ValidationHandler;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.util.IOUtils;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.launcher.debug.model.CamelStackFrame;
import org.fusesource.ide.launcher.debug.model.CamelThread;
import org.fusesource.ide.launcher.debug.util.CamelDebugRegistry;
import org.fusesource.ide.launcher.debug.util.CamelDebugRegistryEntry;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;

/**
 * @author lhein
 */
public class RiderDesignEditor extends DiagramEditor implements INodeViewer, IDebugEventSetListener, ISelectionListener, IBreakpointsListener {

	private final RiderDesignEditorData data;

	private RiderEditor editor;
	private KeyHandler keyHandler;

	private AbstractEditPart selectedEditPart;
	private AbstractEditPart lastSelectedEditPart;

	private boolean asyncSwitchToSource;
	private boolean dirty = false;
	private boolean disableCommandEvents;

	private IEditorSite editorSite;
	private IEditorInput editorInput;

	private IProject container;
	@SuppressWarnings("unused")
	private IFile camelContextFile;
	@SuppressWarnings("unused")
	private RouteContainer model;
	private RouteSupport activeRoute;
	
	private CamelDiagramBehaviour camelDiagramBehaviour;
	private boolean graphitiDoesNotMarkAsDirty = true;
	
	private DesignerCache activeConfig;
	private Map<RouteSupport, DesignerCache> cache = new HashMap<RouteSupport, DesignerCache>();

	private AbstractNode highlightedNodeInDebugger;
	
	private RiderDesignEditorFlyoutPaletteComposite paletteComposite;
	
	public class DesignerCache {
		public Diagram diagram;
		public DiagramEditorInput input;
	}

	private PropertyChangeListener nodePropertyListener = new PropertyChangeListener() {
		/*
		 * (non-Javadoc)
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			CommandStack commandStack = getCommandStack();
			if (commandStack != null) {
				// TODO need to somehow mark the command stack as dirty so it notifies the parent editor..
				// for now we've the hack using field 'graphitiDoesNotMarkAsDirty'
				PropertyChangeCommand command = new PropertyChangeCommand(evt);
				commandStack.execute(command);
			}

			// TODO Graphiti update the graphical diagram for the selected node
			// the following line causes exceptions when adding node having expressions in it
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					DiagramOperations.updateSelectedNode(RiderDesignEditor.this);
					getDiagramTypeProvider().getDiagramBehavior().refresh();
				}

			});

			if (selectedEditPart != null) {
				selectedEditPart.refresh();
			} else if (lastSelectedEditPart == null) {

			} else {
				lastSelectedEditPart.refresh();
			}
		}
	};

	private Composite parent;

	public static RiderDesignEditor toRiderDesignEditor(IDiagramBehavior behavior) {
		if (behavior instanceof RiderDesignEditor) {
			return (RiderDesignEditor) behavior;
		}
		return null;
	}

	public static RiderDesignEditor toRiderDesignEditor(IFeatureProvider fp) {
		if (fp == null) {
			return null;
		}
		return toRiderDesignEditor(fp.getDiagramTypeProvider());
	}

	public static RiderDesignEditor toRiderDesignEditor(IDiagramTypeProvider diagramTypeProvider) {
		if (diagramTypeProvider == null) {
			return null;
		}
		return toRiderDesignEditor(diagramTypeProvider.getDiagramBehavior());
	}

	public RiderDesignEditor(RiderEditor parent) {
		this.data = parent.getDesignEditorData();
		this.activeConfig = new DesignerCache();
		this.editor = parent;
		DebugPlugin.getDefault().addDebugEventListener(this);
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
			ISelectionService sel = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
			if (sel != null) sel.addSelectionListener(ICamelDebugConstants.DEBUG_VIEW_ID, this);			
		}
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
	}
	
	@Override
	public void init(IEditorSite editorSite, IEditorInput input) throws PartInitException {
		this.editorSite = editorSite;
		this.editorInput = input;
		super.init(editorSite, input);
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
	
	public IConfigurationProvider getConfigurationProvider() {
		return this.camelDiagramBehaviour.getConfigurationProvider();
	}
	
	public RiderEditor getMultiPageEditor() {
		return this.editor;
	}
	
    public String getPaletteFilter() {
        if (paletteComposite != null) {
            return paletteComposite.getFilter().getText();
        }
        return null;
    }
	
	@Override
	protected void setInput(IEditorInput input) {
		
		setPartName(input.getName());

		super.setInput(input);
		
        if (data.loadModelOnSetInput) {
			loadModelFromInput(input);
		}

        initializeDiagram(activeConfig.diagram);
		
        /**
         * the following is needed because we miss the first debug events and the first breakpoint wouldn't be highlighted otherwise
         */
        try {
	        for (CamelDebugRegistryEntry entry : CamelDebugRegistry.getInstance().getEntries().values()) {
	        	IEditorInput ips = entry.getEditorInput();
				IFile f = (IFile)ips.getAdapter(IFile.class);
	        	if (f.getFullPath().toFile().getPath().equals(asFileEditorInput(input).getFile().getFullPath().toFile().getPath())) {
	        		String endpointId = null;
	        		
	        		// first highligth the suspended node
	        		Set<String> ids = entry.getDebugTarget().getDebugger().getSuspendedBreakpointNodeIds();
	        		if (ids != null && ids.size()>0) {
	        			endpointId = ids.iterator().next();
	        		}
	        		highlightBreakpointNodeWithID(endpointId);
	        		
	        		// and then mark all breakpoints
	        		
	        	}
	        }
        } catch (Exception ex) {
        	Activator.getLogger().error(ex);
        }
        /**
         * End of the highlighting code
         */
        
		getEditingDomain().getCommandStack().flush();
	}

	protected void initializeDiagramForSelectedRoute() {
	    if (activeRoute == null || activeConfig == null) {
	        return;
	    }

	    if (activeConfig.diagram == null) {
	        // Create the diagram and its file
	        activeConfig.diagram = Graphiti.getPeCreateService().createDiagram("CamelContext", "CamelContext", true); //$NON-NLS-1$ //$NON-NLS-2$
	        initializeDiagram(activeConfig.diagram);
	    }
	}
	
	private void initializeDiagram(Diagram diagram) {
	    // set the diagram on the container
		IDiagramTypeProvider diagramTypeProvider = getDiagramTypeProvider();
		if (diagramTypeProvider == null)
			return;
		
	    if (diagramTypeProvider.getDiagram() != diagram) {
	    	diagramTypeProvider.resourceReloaded(activeConfig.diagram);
	    }

	    // add the diagram contents
        getEditingDomain().getCommandStack().execute(new ImportCamelContextElementsCommand(RiderDesignEditor.this, getEditingDomain(), diagram));

        // layout the diagram
        DiagramOperations.layoutDiagram(RiderDesignEditor.this);

        // setup grid visibility
        setupGridVisibilityAsync();
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		super.createPartControl(parent);
	}

    /**
      * Create a FlyoutPaletteComposite the will used to show a flyout palette
      * alongside the editor.
      * 
      * @param parent
      *            The parent composite hosting the FlyoutPaletteComposite.
      * @return a newly-created {@link FlyoutPaletteComposite}
      */
     protected FlyoutPaletteComposite createPaletteComposite(Composite parent) {
         paletteComposite = new RiderDesignEditorFlyoutPaletteComposite(parent, SWT.NONE, getSite()
                 .getPage(), getPaletteViewerProvider(), getPalettePreferences());
         paletteComposite.getFilter().addModifyListener(new ModifyListener() {
             @Override
             public void modifyText(ModifyEvent e) {
                 getDiagramBehavior().refreshPalette();
             }
         });
         return paletteComposite;
     }
 
protected void contributeToActionBars() {
		IActionBars bars = null;
		if (editorSite != null) {
			bars = editorSite.getActionBars();
		} else {
			Activator.getLogger().warning("No IEditorSite registered for " + this);
			return;
		}
		fillLocalToolBar(bars.getToolBarManager());
	}

	protected void fillLocalToolBar(IToolBarManager manager) {
	}


	public Composite getParent() {
		return parent;
	}

	/**
	 * @param editingDomain the editingDomain to set
	 */
	public void setEditingDomain(TransactionalEditingDomain editingDomain) {
//		this.editingDomain = editingDomain;
	}

	public String getCamelContextURI() {
		RouteSupport selectedRoute = getSelectedRoute();
		String id = selectedRoute.getId();
		if (id == null || id.length() == 0) {
			id = "#" + selectedRoute.hashCode();
		}
		IFile file = getCamelContextFile();
		if (file != null) {
			return file.getFullPath().toString() + "_" + id;
		} else {
			IRemoteCamelEditorInput input = getRemoteCamelEditorInput();
			if (input != null) {
				return input.getUriText();
			}
		}
		return id;
	}

	public IFile getCamelContextFile() {
		IFileEditorInput fileEditorInput = getFileEditorInput();
		if (fileEditorInput != null) {
			return fileEditorInput.getFile();
		}
		return null;
	}
	
	public void setCamelContextFile(IFile file) {
		this.camelContextFile = file;
	}
	
	protected IFileEditorInput getFileEditorInput() {
		return asFileEditorInput(editorInput);
	}

	protected IRemoteCamelEditorInput getRemoteCamelEditorInput() {
		return asRemoteCamelEditorInput(editorInput);
	}

	public IFileEditorInput asFileEditorInput(IEditorInput input) {
		if (input instanceof IFileEditorInput) {
			return (IFileEditorInput) input;
		} else if (input instanceof ICamelEditorInput) {
			ICamelEditorInput camelEditorInput = (ICamelEditorInput) input;
			IEditorInput fileEditorInput = camelEditorInput.getFileEditorInput();
			if (fileEditorInput instanceof IFileEditorInput) {
				return (IFileEditorInput) fileEditorInput;
			} else if (fileEditorInput instanceof FileStoreEditorInput) {
				FileStoreEditorInput fsei = (FileStoreEditorInput)fileEditorInput;
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


	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.editor.INodeViewer#getSelectedNode()
	 */
	@Override
	public AbstractNode getSelectedNode() {
		// return the currently active selection
		return AbstractNodes.getSelectedNode(Selections.getSelection(getEditorSite()));
		//return this.data.selectedNode;
	}

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.editor.INodeViewer#setSelectedNode(org.fusesource.ide.camel.model.AbstractNode)
	 */
	@Override
	public void setSelectedNode(AbstractNode newSelection) {
		if (newSelection != null) {
			Object selectEditPart = findEditPart(newSelection, getRootEditPart());
			if (selectEditPart != null) {
				getEditorSite().getSelectionProvider().setSelection(new StructuredSelection(selectEditPart));
			} else {
				Activator.getLogger().error("Could not find editPart for selection: " + newSelection, new Exception());
			}
		}
		this.data.selectedNode = newSelection;
	}


	/**
	 * Finds the given node by walking the edit part tree looking for the correct one
	 */
	@SuppressWarnings("unchecked")
	public static EditPart findEditPart(AbstractNode node, EditPart part) {
		if (part instanceof RootEditPart) {
			RootEditPart root = (RootEditPart) part;
			EditPart contents = root.getContents();
			if (contents != null) {
				return findEditPart(node, contents);
			}
		}
		if (part instanceof EditPart) {
			EditPart nodeEditPart = part;
			AbstractNode modelNode = AbstractNodes.toAbstractNode(nodeEditPart);
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

	public AbstractEditPart getLastSelectedEditPart() {
		return lastSelectedEditPart;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#getCommandStack()
	 */
	@Override
	public CommandStack getCommandStack() {
		return getEditDomain().getCommandStack();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.GraphicalEditor#selectionChanged(org.eclipse
	 * .ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// If not the active editor, ignore selection changed.
		if (getSite().getPage().getActiveEditor() == editor
				&& editor.getActiveEditor() == this) {
			updateActions(getSelectionActions());

			if (selection instanceof StructuredSelection) {
				StructuredSelection structuredSelection = (StructuredSelection) selection;
				Object firstElement = structuredSelection.getFirstElement();
				if (firstElement instanceof AbstractEditPart) {
					selectedEditPart = (AbstractEditPart) firstElement;
					AbstractNode node = AbstractNodes.toAbstractNode(firstElement);
					if (node != null) {
						if (data.selectedNode != null) {
							data.selectedNode
							.removePropertyChangeListener(nodePropertyListener);
						}
						data.selectedNode = node;
						data.selectedNode
						.addPropertyChangeListener(nodePropertyListener);
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
						Activator.getLogger().error(e);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	@Override
	public void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		GraphicalViewer viewer = getGraphicalViewer();
		/*
		// TODO Graphiti
		viewer.setEditPartFactory(new RouteEditPartFactory());

		double[] zoomLevels;
		ArrayList<String> zoomContributions;
		rootEditPart = new ScalableRootEditPart();
		viewer.setRootEditPart(rootEditPart);

		ZoomManager manager = rootEditPart.getZoomManager();
		getActionRegistry().registerAction(new ZoomInAction(manager));
		getActionRegistry().registerAction(new ZoomOutAction(manager));
		// zooms possible. 1 = 100%
		zoomLevels = new double[] { 0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 2.5, 3.0,
				4.0, 5.0, 10.0, 20.0 };
		manager.setZoomLevels(zoomLevels);

		zoomContributions = new ArrayList<String>();
		zoomContributions.add(ZoomManager.FIT_ALL);
		zoomContributions.add(ZoomManager.FIT_HEIGHT);
		zoomContributions.add(ZoomManager.FIT_WIDTH);
		manager.setZoomLevelContributions(zoomContributions);

		KeyHandler keyHandler = new KeyHandler();
		keyHandler.put(KeyStroke.getPressed(SWT.DEL, 127, 0),
				getActionRegistry().getAction(ActionFactory.DELETE.getId()));
		keyHandler.put(KeyStroke.getPressed('+', SWT.KEYPAD_ADD, 0),
				getActionRegistry().getAction(GEFActionConstants.ZOOM_IN));
		keyHandler.put(KeyStroke.getPressed('-', SWT.KEYPAD_SUBTRACT, 0),
				getActionRegistry().getAction(GEFActionConstants.ZOOM_OUT));
		viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.NONE),
				MouseWheelZoomHandler.SINGLETON);
		viewer.setKeyHandler(keyHandler);

		// IMPORTANT: the following is a dirty workaround to get the delete
		// action behave normal. Without this code no delete will be
		// possible. TODO: try to get this fixed by time.
		ActionRegistry registry = getActionRegistry();
		String id = ActionFactory.UNDO.getId();
		getEditorSite().getKeyBindingService().registerAction(
				registry.getAction(id));
		id = ActionFactory.REDO.getId();
		getEditorSite().getKeyBindingService().registerAction(
				registry.getAction(id));
		id = ActionFactory.DELETE.getId();
		getEditorSite().getKeyBindingService().registerAction(
				registry.getAction(id));

		 */
		ContextMenuProvider provider = new RiderEditorContextMenuProvider(this, this, viewer, getActionRegistry());
		viewer.setContextMenu(provider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.GraphicalEditor#commandStackChanged(java.util
	 * .EventObject)
	 */
	@Override
	public void commandStackChanged(EventObject event) {
		super.commandStackChanged(event);
		fireModelDirty();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramEditor#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return this.dirty;
	}
	
	/**
	 * @param dirty the dirty to set
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		firePropertyChange(PROP_DIRTY);
	}
	
	protected void fireModelDirty() {
		setDirty(true);

		// only mark the diagram as changed if we really have just performed a command
		// (rather than just doing a save which just clears the dirty flag)
		boolean changed = getCommandStack().isDirty();
		if (!disableCommandEvents && (changed || graphitiDoesNotMarkAsDirty)) {
			data.diagramChanged = true;
		}
	}

	/**
	 * Create a transfer drop target listener. When using a
	 * CombinedTemplateCreationEntry tool in the palette, this will enable model
	 * element creation by dragging from the palette.
	 * 
	 * @see #createPaletteViewerProvider()
	 */
	private TransferDropTargetListener createTransferDropTargetListener() {
		return new TemplateTransferDropTargetListener(getGraphicalViewer()) {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.gef.dnd.TemplateTransferDropTargetListener#getFactory(java.lang.Object)
			 */
			@SuppressWarnings("rawtypes")
			@Override
			protected CreationFactory getFactory(final Object template) {
				if (template instanceof CreationFactory) {
					return (CreationFactory) template;
				} else if (template instanceof Class) {
					return new SimpleFactory((Class) template);
				} else {
					Activator.getLogger().debug("============= Template: " + template + " is not a CreationFactory or Class! " + Objects.typeName(template));
					return new CreationFactory() {

						@Override
						public Object getNewObject() {
							return template;
						}

						@Override
						public Object getObjectType() {
							if (template == null) {
								return null;
							}
							return template.getClass();
						}

					};
				}
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getAdapter(
	 * java.lang.Class)
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class type) {
		if (type == GraphicalViewer.class
				|| type == GraphicalEditorWithFlyoutPalette.class
				|| type == EditPartViewer.class) {
			return getGraphicalViewer();
		} else if (type == CommandStack.class) {
			return getCommandStack();
		} else if (type == EditDomain.class) {
			return getEditDomain();
		} else if (type == ActionRegistry.class) {
			return getActionRegistry();
		} else if (type == ZoomManager.class) {
			GraphicalViewer graphicalViewer = getGraphicalViewer();
			if (graphicalViewer == null)
				return null;
			
			RootEditPart root = graphicalViewer.getRootEditPart();
			if (root instanceof ScalableRootEditPartAnimated) {
				ScalableRootEditPartAnimated scalableRoot = (ScalableRootEditPartAnimated) root;
				return scalableRoot.getZoomManager();
			} else if (root instanceof ScalableRootEditPart) {
				return ((ScalableRootEditPart) root).getZoomManager();
			}
			return null;
		} else if (type == IContentOutlinePage.class) {
			return new RiderOutlinePage(this);
		}
		return super.getAdapter(type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#
	 * initializeGraphicalViewer()
	 */
	@Override
	public void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();

		// listen for dropped parts
		viewer.addDropTargetListener(createTransferDropTargetListener());
	}

	/**
	 * handles exception while loading model
	 * 
	 * @param e
	 *            the exception
	 * @throws PartInitException
	 */
	private void handleLoadException(Exception e) {
		Activator.getLogger().error("** Load failed. Using default model. **", e);
		try {
			this.setModel(new RouteContainer());
		} catch (PartInitException e1) {
			Activator.getLogger().warning("Failed to reload the diagram: " + e, e);
		}
		getModel().setFailedToParseXml(true);
	}
	
	/**
	 * refresh the editor contents
	 */
	public void update() {
		DiagramOperations.updateDiagram(RiderDesignEditor.this);
		getDiagramBehavior().refresh();
		if (getDiagram() != null) selectPictogramElements(new PictogramElement[] { getDiagram().getContainer() });
	}

	/**
	 * fires a model change event to all listeners
	 */
	public void fireModelChanged() {
		for (ModelChangeListener listener : data.modelChangeListeners) {
			listener.onModelChange();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#getActionRegistry()
	 */
	@Override
	public ActionRegistry getActionRegistry() {
		return super.getActionRegistry();
	}

	/**
	 * @return the model
	 */
	public RouteContainer getModel() {
		if (data.model == null && editorInput != null) {
			data.lazyLoading = true;
			try {
				loadModelFromInput(editorInput);
			} finally {
				data.lazyLoading = false;
			}
		}
		return this.data.model;
	}

	public void setModel(RouteContainer model) throws PartInitException {
		if (model != this.data.model){
			this.data.model = model;
			this.data.selectedRoute = null;

			setInitialRoute();
			
			if (data.selectedRouteIndex != data.indexOfRoute(data.selectedRoute) && data.selectedRouteIndex < data.model.getChildren().size()) {
				setSelectedRouteIndex(data.selectedRouteIndex);
			}
		}
	}

	public RouteSupport getSelectedRoute() {
		if (data.selectedRoute == null) {
			List<AbstractNode> children = getModel().getChildren();
			if (children != null) {
				int size = children.size();
				int idx = data.selectedRouteIndex;
				if (idx < 0 || idx >= size) {
					idx = 0;
				}
				if (size > 0) {
					AbstractNode node = children.get(idx);
					if (node instanceof RouteSupport) {
						data.selectedRoute = (RouteSupport) node;
					}
				}
			}
		}
		if (data.selectedRoute == null) {
			setInitialRoute();
		}
		return data.selectedRoute;
	}

	public void clearSelectedRouteCache() {
		this.data.selectedRoute = null;
		this.data.selectedRouteIndex = -1;
	}

	public void setSelectedRoute(RouteSupport selectedRoute) {
		if (this.data.selectedRoute != selectedRoute) {
			int index = data.indexOfRoute(selectedRoute);
			setSelectedRouteIndex(index);
		}
	}

	public void switchRoute(final RouteSupport newRoute) {
		if (newRoute == activeRoute) {
			return;
		}
		if (activeRoute != null) {
			this.cache.put(activeRoute, activeConfig);
		}
		activeRoute = newRoute;
		if (this.cache.containsKey(newRoute)) {
			this.activeConfig = this.cache.get(newRoute);
		} else {
			DiagramEditorInput oldInput = this.activeConfig.input;
			this.activeConfig = new DesignerCache();
			this.activeConfig.input = oldInput;
		}
		refreshDiagramContents();
	}
	
	public void refreshDiagramContents() {
		initializeDiagramForSelectedRoute();
		if (activeConfig != null && activeConfig.diagram != null) {
			getDiagramTypeProvider().init(activeConfig.diagram, getDiagramBehavior());
			getDiagramBehavior().getRefreshBehavior().initRefresh();
	        setPictogramElementsForSelection(null);
	        GraphicalViewer graphicalViewer = getGraphicalViewer();
	        
	        if (graphicalViewer == null)
	        	return;
	        
	        // set Diagram as contents for the graphical viewer and refresh
	        graphicalViewer.setContents(activeConfig.diagram);	        
	        getDiagramBehavior().refreshContent();
		}
	}

	private void setInitialRoute() {
		if (getModel() != null) {
			if (getModel().getChildren().isEmpty()) {
				// lets add an empty route for now...
				Route route = new Route();
				getModel().addChild(route);
			}
			this.activeRoute = (RouteSupport)getModel().getChildren().get(0);
			this.data.selectedRoute = activeRoute;
		}
	}

	public void setSelectedRouteIndex(int index) {
//		if (this.data.selectedRouteIndex != index && index >= 0) {
			this.data.selectedRouteIndex = index;
			this.data.selectedRoute = (RouteSupport) getModel().getChildren().get(index);

			// now lets recreate the model so that we don't get any Graphiti woe since the model is intertwined with the diagram
			//data.recreateModel();

			switchRoute(data.selectedRoute);
//		}
	}

	public void setupGridVisibilityAsync() {
		// this can't be invoked async as its causing dirty editor all the time then
		DiagramOperations.updateGridColor(RiderDesignEditor.this);
				
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
			    setupGridVisibility();
	        }
	    });
	}

	private void setupGridVisibility() {
		// retrieve the grid visibility setting
		boolean gridVisible = PreferenceManager.getInstance().loadPreferenceAsBoolean(PreferencesConstants.EDITOR_GRID_VISIBILITY);

		// reset the grid visibility flag
		DiagramUtils.setGridVisible(gridVisible, this);
		
		getDiagramBehavior().refresh();
	}

	/**
	 * @return the container
	 */
	public IProject getContainer() {
		return this.container;
	}
	
	public void setContainer(IProject container) {
		this.container = container;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#getSelectionSynchronizer()
	 */
	@Override
	protected SelectionSynchronizer getSelectionSynchronizer() {
		return super.getSelectionSynchronizer();
	}
	
	public SelectionSynchronizer getSelectionSyncer() {
		return getSelectionSynchronizer();
	}

	/**
	 * @return the keyHandler
	 */
	public KeyHandler getKeyHandler() {
		return this.keyHandler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#dispose()
	 */
	@Override
	public void dispose() {
		DebugPlugin.getDefault().removeDebugEventListener(this);
		
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
			ISelectionService sel = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
			if (sel != null) sel.removeSelectionListener(ICamelDebugConstants.DEBUG_VIEW_ID, this);			
		}
		
		this.editorInput = null;
		this.activeConfig = null;

		// dispose the ActionRegistry (will dispose all actions)
		ActionRegistry actionRegistry = getActionRegistry();
		if (actionRegistry != null) {
			actionRegistry.dispose();
		}
		// important: always call super implementation of dispose
		if (getDiagramTypeProvider() != null)
			super.dispose();
	}


	protected void loadModelFromInput(IEditorInput editorInput) {
		data.loaded = false;
		try {
			if (editorInput instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput) editorInput).getFile();
				loadModelFromFile(file);
			} else if (editorInput instanceof ICamelEditorInput) {
				ICamelEditorInput camelInput = (ICamelEditorInput) editorInput;
				editor.onInputLoading(camelInput);
				this.setModel(camelInput.loadModel());
			} else if (editorInput instanceof IURIEditorInput) {
				editor.onInputLoading(editorInput);
				IURIEditorInput uriInput = (IURIEditorInput) editorInput;
				URI uri = uriInput.getURI();
				URL url = uri.toURL();
				if (url == null) {
					Activator.getLogger().warning("Unsupported URI type " + uri);
				} else {
					String xml = IOUtils.loadText(url.openStream(), "UTF-8");
					this.setModel(data.marshaller.loadRoutesFromText(xml));
				}
			} else {
				editor.onInputLoading(editorInput);
				Activator.getLogger().warning("Unsupported IEditorInput type " + editorInput);
			}
		} catch (Exception e) {
			handleLoadException(e);
			// lets switch to the source view as we tried to load invalid XML
			switchToSourceEditor();
		} finally {
			if (asyncSwitchToSource) {
				// lets not marked as loaded until we've switched the view...
			} else {
				data.loaded = true;
			}
		}
	}

	protected void switchToSourceEditor() {
		asyncSwitchToSource = true;
		editor.switchToSourceEditor();
	}

	/**
	 * loads the model from file
	 * 
	 * @param file
	 *            the file to load from
	 * @throws PartInitException
	 */
	public void loadModelFromFile(IFile file) throws Exception {
		editor.onFileLoading(file);
		this.setModel(data.marshaller.loadRoutes(file));
		if (file.getName().contentEquals("switchyard.xml")) {
			MessageDialog.openWarning(getSite().getShell(), EditorMessages.switchyardFoundTitle,
					EditorMessages.switchyardFoundText);
			switchToSourceEditor();
		}
		ValidationHandler status = this.getModel().validate();
		if (status.hasErrors()) {
			switchToSourceEditor();
		}
	}

	public void loadModelFromRemoteContext(ICamelEditorInput editorInput) throws IOException, PartInitException {
		editor.onInputLoading(editorInput);
		this.setModel(editorInput.loadModel());
	}
	
	public void loadEditorText(String text) {
		try {
			this.setModel(data.marshaller.loadRoutesFromText(text));
			validateXml();
		} catch (Exception e) {
			showValidationError(e);
		}
	}

	/**
	 * lets validate the XML against the XSD in case its bad & warn of possible losses
	 */
	public void validateXml() throws Exception {
		if (data.loaded) {
			// if we have no model it means lets try reparse the XML again...
			if (this.getModel() == null || this.getModel().isFailedToParseXml()) {
				IDocument document = editor.getDocument();
				if (document != null) {
					String text = document.get();
					loadEditorText(text);
				}

			} else {
				ValidationHandler status = this.getModel().validate();
				if (status.hasErrors()) {
					String error = status.userMessage();
					showXmlValidationError(error);
				}
			}
		}
	}

	public void autoLayoutRoute() {
		Display.getCurrent().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				DiagramOperations.layoutDiagram(RiderDesignEditor.this);	
			}
		});		
	}

	public Diagram getDiagram() {
		return activeConfig == null ? null : activeConfig.diagram;
	}

	public void setTransactionalEditingDomain(TransactionalEditingDomain editingDomain) {
//		this.editingDomain = editingDomain;
	}

	protected DiagramEditorInput getDiagramInput() {
		DiagramEditorInput diagramInput = null;
		IEditorInput input = getEditorInput();
		if (input instanceof DiagramEditorInput) {
			diagramInput = (DiagramEditorInput) input;
		}
		return diagramInput;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#getEditorInput()
	 */
	@Override
	public IEditorInput getEditorInput() {
		if (this.editorInput != null) {
			return this.editorInput;
		}
		return super.getEditorInput();
	}
	
	public String updateEditorText(String editorXml) {
		return data.marshaller.updateText(editorXml, getModel());
	}

	public void runIfDiagramModified(Runnable block) {
		runIfDiagramModified(block, true);
	}

	public void runIfDiagramModified(Runnable block, boolean async) {
		// called when we've switched to the text view so lets clear that we've shown the validation error
		data.shownValidationError = false;
		if (data.diagramChanged) {
			try {
				if (async) {
					Display.getDefault().syncExec(block);
				} else {
					block.run();
				}
			} finally {
				clearChangedFlags();
			}
		}
	}

	protected void clearChangedFlags() {
		data.diagramChanged = false;
		data.textChanged = false;
		getCommandStack().markSaveLocation();
		getEditingDomain().getCommandStack().flush();
		setDirty(false);
	}
	
	public void onTextEditorPropertyChange() {
		data.textChanged = true;
	}

	public void runIfTextModified(Runnable block) {
		try {
			Display.getDefault().syncExec(block);
			validateXml();
		} catch (Exception e) {
			showValidationError(e);
		} finally {
			// note we could be clearing the flags before we perform the block
			clearChangedFlags();
		}
		if (asyncSwitchToSource) {
			// we should now have switched on loading to the text view now
			// so lets re-enable error warnings.
			asyncSwitchToSource = false;
			data.loaded = true;
		}
	}

	protected void showValidationError(Exception e) {
		String text = e.getMessage();
		if (e instanceof RuntimeException) {
			Throwable cause = e.getCause();
			if (cause != null) {
				text = cause.getMessage();
				Activator.getLogger().error(e);
			} else {
				Activator.getLogger().error(e);
			}
		}
		showXmlValidationError(text);
	}

	public void showXmlValidationError(String text) {
		if (data.loaded && !data.shownValidationError) {
			String message = NLS.bind(EditorMessages.saveModifiedTextFailedText, text);
			data.shownValidationError = true;
			MessageDialog.openWarning(getSite().getShell(), EditorMessages.saveModifiedTextFailedTitle, message);
		}
	}

	public void markXmlSaved() {
		CommandStack stack = getCommandStack();
		if (stack == null) {
			Activator.getLogger().warning("No command stack available when trying to save document!");
		} else {
			stack.markSaveLocation();
		}
	}

	public IFeatureProvider getFeatureProvider() {
		return getDiagramTypeProvider().getFeatureProvider();
	}

	protected RootEditPart getRootEditPart() {
		return getGraphicalViewer().getRootEditPart();
	}

	public void addModelChangeListener(ModelChangeListener listener) {
		data.modelChangeListeners.add(listener);
	}

	public void removeModelChangeListener(ModelChangeListener listener) {
		data.modelChangeListeners.remove(listener);
	}

	/**
	 * clears the cache
	 */
	public void clearCache() {
		activeRoute = null;
		this.activeConfig.diagram = null;
		getDiagramTypeProvider().getDiagram().eResource().eAdapters().clear();
		getEditingDomain().getResourceSet().getResources().remove(getDiagramTypeProvider().getDiagram().eResource());
	}

	public void addNewRoute() {
		DiagramOperations.addNewRoute(this);
	}

	public void deleteRoute() {
		RouteSupport selectedRoute = getSelectedRoute();
		if (selectedRoute != null) {
			DiagramOperations.deleteRoute(this, selectedRoute);
		}
	}

	/**
	 * @return the editor
	 */
	public RiderEditor getEditor() {
		return this.editor;
	}
	
	public DesignerCache getActiveConfig() {
		return this.activeConfig;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointsListener#breakpointsAdded(org.eclipse.debug.core.model.IBreakpoint[])
	 */
	@Override
	public void breakpointsAdded(IBreakpoint[] breakpoints) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				refreshDiagramContents();				
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
				refreshDiagramContents();				
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
				refreshDiagramContents();				
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
							Activator.getLogger().error(ex);
						}
					}
				}
			}
		}
	}
	
	/**
	 * @return the highlightedNodeInDebugger
	 */
	public synchronized AbstractNode getHighlightedNodeInDebugger() {
		return this.highlightedNodeInDebugger;
	}
	
	/**
	 * highlights the node currently in breakpoint
	 * 
	 * @param endpointNodeId	the node id
	 */
	private synchronized void highlightBreakpointNodeWithID(String endpointNodeId) {
		// get the correct node for the id
		final AbstractNode node = getModel().getNode(endpointNodeId);
		
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
	private synchronized void setDebugHighLight(AbstractNode bo, boolean highlight) {
		if (bo == null) return;
		
		// delegate to an operation command for async transacted diagram update
		DiagramOperations.highlightNode(this, bo, highlight);
	}
}

