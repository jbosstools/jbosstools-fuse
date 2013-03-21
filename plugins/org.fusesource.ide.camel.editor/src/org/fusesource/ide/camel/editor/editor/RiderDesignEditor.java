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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
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
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;
import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;
import org.eclipse.graphiti.ui.internal.config.ConfigurationProvider;
import org.eclipse.graphiti.ui.internal.config.IConfigurationProvider;
import org.eclipse.graphiti.ui.internal.util.gef.ScalableRootEditPartAnimated;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.fusesource.camel.tooling.util.ValidationHandler;
import org.fusesource.ide.camel.editor.AbstractNodes;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.CamelModelChangeListener;
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
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;
import org.fusesource.scalate.util.IOUtil;

/**
 * @author lhein
 */
public class RiderDesignEditor extends DiagramEditor implements INodeViewer {

	private final RiderDesignEditorData data;

	private RiderEditor editor;
	private KeyHandler keyHandler;

	private AbstractEditPart selectedEditPart;
	private AbstractEditPart lastSelectedEditPart;

	private boolean asyncSwitchToSource;

	private boolean disableCommandEvents;

	private IEditorSite editorSite;
	private IEditorInput editorInput;

	private IProject container;
	private IFile camelContextFile;
	private RouteContainer model;
	private RouteSupport activeRoute;
	private CamelModelChangeListener camelModelListener;
	private CamelUpdateBehaviour camelUpdateBehaviour;
	private CamelPaletteBehaviour camelPaletteBehaviour;
	private CamelPersistencyBehaviour camelPersistencyBehaviour;
	private boolean graphitiDoesNotMarkAsDirty = true;
	
	private DesignerCache activeConfig;
	private Map<RouteSupport, DesignerCache> cache = new HashMap<RouteSupport, DesignerCache>();

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

	public static RiderDesignEditor toRiderDesignEditor(IDiagramEditor editor) {
		if (editor instanceof RiderDesignEditor) {
			return (RiderDesignEditor) editor;
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
		return toRiderDesignEditor(diagramTypeProvider.getDiagramEditor());
	}

	public RiderDesignEditor(RiderEditor parent) {
		this.data = parent.getDesignEditorData();
		this.activeConfig = new DesignerCache();
		this.editor = parent;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramEditor#createUpdateBehavior()
	 */
	@Override
	protected synchronized DefaultUpdateBehavior createUpdateBehavior() {
		if (this.camelUpdateBehaviour == null) {
			this.camelUpdateBehaviour = new CamelUpdateBehaviour(this);
		}
		return this.camelUpdateBehaviour;
	}
	
	@Override
	protected synchronized DefaultPersistencyBehavior createPersistencyBehavior() {
		if (this.camelPersistencyBehaviour == null) {
			this.camelPersistencyBehaviour = new CamelPersistencyBehaviour(this);
		}
		return camelPersistencyBehaviour;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramEditor#registerBusinessObjectsListener()
	 */
	@Override
	protected void registerBusinessObjectsListener() {
		//super.registerBusinessObjectsListener();
		camelModelListener = new CamelModelChangeListener(this);
		
		TransactionalEditingDomain eDomain = getEditingDomain();
		eDomain.addResourceSetListener(camelModelListener);
	}

	@Override
	public void init(IEditorSite editorSite, IEditorInput input) throws PartInitException {
		this.editorSite = editorSite;
		this.editorInput = input;
		super.init(editorSite, input);
	}
	
	@Override
	protected void setInput(IEditorInput input) {
		setPartName(input.getName());

		super.setInput(input);

        if (data.loadModelOnSetInput) {
			loadModelFromInput(input);
		}

        initializeDiagram(activeConfig.diagram);
		
		getEditingDomain().getCommandStack().flush();
	}

	protected void initializeDiagramForSelectedRoute() {
	    if (activeRoute == null) {
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
	    if (getDiagramTypeProvider().getDiagram() != diagram) {
	        getDiagramTypeProvider().resourceReloaded(activeConfig.diagram);
	    }

	    // add the diagram contents
        getEditingDomain().getCommandStack().execute(new ImportCamelContextElementsCommand(RiderDesignEditor.this, getEditingDomain(), diagram));

        // layout the diagram
        DiagramOperations.layoutDiagram(RiderDesignEditor.this);

        // setup grid visibility
        setupGridVisibility();
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		super.createPartControl(parent);
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

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.editor.DiagramEditor#createPaletteBehaviour()
	 */
	@Override
	protected synchronized DefaultPaletteBehavior createPaletteBehaviour() {
		if (this.camelPaletteBehaviour == null) {
			this.camelPaletteBehaviour = new CamelPaletteBehaviour(this);
		}
		return this.camelPaletteBehaviour;
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
					return IOUtil.loadText(uriInput.getURI().toURL().openStream(), "UTF-8");
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
                        return IOUtil.loadText(new URI(uriInput.getUri().toString()).toURL().openStream(), "UTF-8");
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
	protected void configureGraphicalViewer() {
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

	protected void fireModelDirty() {
		firePropertyChange(IEditorPart.PROP_DIRTY);

		// only mark the diagram as changed if we really have just performed a command
		// (rather than just doing a save which just clears the dirty flag)
		boolean changed = getCommandStack().isDirty();
		if (!disableCommandEvents && (changed || graphitiDoesNotMarkAsDirty)) {
			data.diagramChanged = true;
		}
	}
	
	public IConfigurationProvider getConfigurationProvider() {
		IConfigurationProvider configurationProvider = new ConfigurationProvider(this, getDiagramTypeProvider());
		return configurationProvider;
	}

	@Override
	protected ContextMenuProvider createContextMenuProvider() {
		return new CamelDiagramEditorContextMenuProvider(getGraphicalViewer(), getActionRegistry(), getConfigurationProvider());
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
			@Override
			protected CreationFactory getFactory(final Object template) {
				if (template instanceof CreationFactory) {
					return (CreationFactory) template;
				} else if (template instanceof Class) {
					return new SimpleFactory((Class) template);
				} else {
					System.out.println("============= Template: " + template + " is not a CreationFactory or Class! " + Objects.typeName(template));
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
			RootEditPart root = getGraphicalViewer().getRootEditPart();
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
	protected void initializeGraphicalViewer() {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		saveModelToFile();

		// now lets tell the source editor to save
		StructuredTextEditor sourceEditor = editor.getSourceEditor();
		if (sourceEditor != null) {
			ProgressMonitorWrapper wrapper = createSaveProgressMonitorWrapper(monitor);
			sourceEditor.doSave(wrapper);
		} else {
			// update CommandStack
			getCommandStack().markSaveLocation();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		saveModelToFile();

		// now lets tell the source editor to save
		StructuredTextEditor sourceEditor = editor.getSourceEditor();
		if (sourceEditor != null) {
			sourceEditor.doSaveAs();
		}
	}

	/**
	 * Creates a save monitor wrapper
	 */
	public ProgressMonitorWrapper createSaveProgressMonitorWrapper(IProgressMonitor monitor) {
		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor) {

			@Override
			public void done() {
				super.done();
				// now to avoid any other change events occurring just after us, lets do an async mark as saved...
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						saveCamelEditorInput();
						markXmlSaved();
					}
				});
			}

		};
		return wrapper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return editor.isSaveAsAllowed();
	}

	/**
	 * refresh the editor contents
	 */
	public void update() {
		DiagramOperations.updateDiagram(RiderDesignEditor.this);
		refresh();
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
		if (activeConfig.diagram != null) {
	        getRefreshBehavior().initRefresh();
	        setPictogramElementsForSelection(null);
	        // set Diagram as contents for the graphical viewer and refresh
	        getGraphicalViewer().setContents(activeConfig.diagram);
	        
	        refreshContent();
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
		if (this.data.selectedRouteIndex != index && index >= 0) {
			this.data.selectedRouteIndex = index;
			this.data.selectedRoute = (RouteSupport) getModel().getChildren().get(index);

			// now lets recreate the model so that we don't get any Graphiti woe since the model is intertwined with the diagram
			//data.recreateModel();

			switchRoute(data.selectedRoute);
		}
	}

	public void setupGridVisibilityAsync() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
			    setupGridVisibility();
	        }
	    });
	}

	public void setupGridVisibility() {
		// TODO is this causing transaction issues???
		DiagramOperations.updateGridColor(RiderDesignEditor.this);

		// retrieve the grid visibility setting
		boolean gridVisible = PreferenceManager.getInstance().loadPreferenceAsBoolean(PreferencesConstants.EDITOR_GRID_VISIBILITY);

		// reset the grid visibility flag
		DiagramUtils.setGridVisible(gridVisible);
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
		this.editorInput = null;
		this.activeConfig = null;

		// dispose the ActionRegistry (will dispose all actions)
		ActionRegistry actionRegistry = getActionRegistry();
		if (actionRegistry != null) {
			actionRegistry.dispose();
		}
		// important: always call super implementation of dispose
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
					String xml = IOUtil.loadText(url.openStream(), "UTF-8");
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
	public void loadModelFromFile(IFile file) throws IOException, PartInitException {
		editor.onFileLoading(file);
		this.setModel(data.marshaller.loadRoutes(file));
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
	public void validateXml() {
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

	public void saveModelToFile() {
		// lets update the XML editor with the latest design if we've updated the design
		try {
			disableCommandEvents = true;
			editor.updatedDesignPage(false);

		} finally {
			disableCommandEvents = false;
		}

		saveCamelEditorInput();

		// NOTE assumes that the XML editor is saved after us!
	}

	protected void saveCamelEditorInput() {
		if (editorInput instanceof ICamelEditorInput) {
			ICamelEditorInput camelInput = (ICamelEditorInput) editorInput;
			IDocument document = editor.getDocument();
			if (document != null) {
				String xml = document.get();
				camelInput.save(xml);
			}
		}
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
	}

	public void onTextEditorPropertyChange() {
		data.textChanged = true;
	}

	public void runIfTextModified(Runnable block) {
		if (data.textChanged) {
			try {
				Display.getDefault().syncExec(block);
				validateXml();
			} catch (Exception e) {
				showValidationError(e);
			} finally {
				// note we could be clearing the flags before we perform the block
				clearChangedFlags();
			}
		} else {
			validateXml();
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
				cause.printStackTrace();
			} else {
				e.printStackTrace();
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
			System.out.println("================= No command stack available when trying to save document!");
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
}
