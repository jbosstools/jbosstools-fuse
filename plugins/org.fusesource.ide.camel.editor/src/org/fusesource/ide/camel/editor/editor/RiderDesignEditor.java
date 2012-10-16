package org.fusesource.ide.camel.editor.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.internal.services.GraphitiInternal;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorFactory;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
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
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.fusesource.camel.tooling.util.ValidationHandler;
import org.fusesource.ide.camel.editor.AbstractNodes;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.EditorMessages;
import org.fusesource.ide.camel.editor.commands.DiagramOperations;
import org.fusesource.ide.camel.editor.commands.ImportCamelContextElementsCommand;
import org.fusesource.ide.camel.editor.editor.io.CamelContextIOUtils;
import org.fusesource.ide.camel.editor.outline.RiderOutlinePage;
import org.fusesource.ide.camel.editor.utils.CamelContextSelectionSynchronizer;
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

	boolean recreateModel = true;	// WARNING: THIS IS THE BIG EVIL SWITCH! DON'T TOUCH IF POSSIBLE!
	// TODO have found when disabled round tripping is not yet working
	// e.g. create a new XML file, add a route, switch to XML, edit XML say add a new route, switch back
	// to diagram & its not updated

	private final RiderDesignEditorData data;

	private RiderEditor editor;
	private KeyHandler keyHandler;

	private AbstractEditPart selectedEditPart;
	private AbstractEditPart lastSelectedEditPart;

	private boolean asyncSwitchToSource;

	private boolean disableCommandEvents;

	private IEditorSite editorSite;
	private IEditorInput editorInput;

	private IFeatureProvider featureProvider;
	private Diagram diagram;
	private DiagramEditorInput diagramInput;

	private IProject container;
	private IFile camelContextFile;
	private RouteContainer model;
	private RouteSupport activeRoute;
	private CamelContextSelectionSynchronizer synchronizer;

	private static final boolean selectFirstRouteOnLayout = true;
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

	private TransactionalEditingDomain editingDomain;

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

	@Override
	public void init(IEditorSite editorSite, IEditorInput input) throws PartInitException {
		this.editorSite = editorSite;
		this.editorInput = input;
		diagramInput = null;
		if (input instanceof DiagramEditorInput) {
			diagramInput = (DiagramEditorInput) input;
		} else {
			diagramInput = createDiagramInput(editorInput);
		}
		if (recreateModel) {
			updateDiagramInput();
		} else {
			this.activeConfig.input = diagramInput;
			super.init(editorSite, diagramInput);
			autoLayoutRoute();
			fireModelChanged();
		}
		//		clearChangedFlags();
	}
	
	@Override
	protected void setInput(IEditorInput input) {
		setPartName(input.getName());

		if (recreateModel) {
			super.setInput(input);

			if (data.loadModelOnSetInput) {
				loadModelFromInput(input);
			}
		} else {
			if (input instanceof DiagramEditorInput) {
				activeConfig.input = (DiagramEditorInput) input;
			} else {
				activeConfig.input = createDiagramInput(input);
			}
			super.setInput(activeConfig.input);
			autoLayoutRoute();
			fireModelChanged();
		}

		// setup grid visibility
		setupGridVisibility();

		//		clearChangedFlags();
	}


	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		super.createPartControl(parent);
		//	contributeToActionBars();
	}

	protected void contributeToActionBars() {
		IActionBars bars = null;
		if (editorSite != null) {
			bars = editorSite.getActionBars();
		} else {
			Activator.getLogger().warning("No IEditorSite registered for " + this);
			return;
		}
		//		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	//	protected void fillLocalPullDown(IMenuManager manager) {
	//		IContributionItem item = manager.find("rider.camel.menu");
	//
	//		manager.add(new EditorContributionItem());
	//	}

	protected void fillLocalToolBar(IToolBarManager manager) {
	}


	public Composite getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.internal.editor.DiagramEditorInternal#createPaletteRoot()
	 */
	@Override
	protected PaletteRoot createPaletteRoot() {
		return new CamelPaletteRoot(getConfigurationProvider());
	}
	
	/**
	 * Creates the diagram from the current model
	 */
	protected DiagramEditorInput createDiagramInput(IEditorInput input) {
		IFileEditorInput fileEditorInput = asFileEditorInput(input);
		if (fileEditorInput != null) {
			this.camelContextFile = fileEditorInput.getFile();
			this.container = this.camelContextFile.getProject();

			// load the model
			if (getModel() == null) {
				this.model = CamelContextIOUtils.loadModelFromFile(camelContextFile);
				ValidationHandler status = CamelContextIOUtils.validateModel(this.model);
				if (status.hasErrors()) {
					Activator.getLogger().error("Unable to validate the model. Invalid input!");
					return null;
				}
			}
			return createDiagram();
		} else {
			IRemoteCamelEditorInput remoteEditorInput = asRemoteCamelEditorInput(input);
			if (remoteEditorInput != null) {
				this.camelContextFile = null;
				this.container = null;

				// load the model
				if (getModel() == null) {
					try {
						String text = remoteEditorInput.getXml();
						this.model = CamelContextIOUtils.loadModelFromText(text);
						ValidationHandler status = CamelContextIOUtils.validateModel(this.model);
						if (status.hasErrors()) {
							Activator.getLogger().error("Unable to validate the model. Invalid input!");
							return null;
						}
					} catch (IOException e) {
						Activator.getLogger().error("Unable to load the model: " + e, e);
						return null;
					}
				}
				return createDiagram();

			} else if (input instanceof DiagramEditorInput) {
				if (recreateModel) {
					// Create the diagram and its file
					if (diagram == null) {
						diagram = Graphiti.getPeCreateService().createDiagram("CamelContext", "CamelContext", true); //$NON-NLS-1$ //$NON-NLS-2$
						DiagramOperations.execute(this.editingDomain, new ImportCamelContextElementsCommand(this, this.editingDomain, diagram), true);
						diagramInput = DiagramEditorInput.createEditorInput(diagram, this.editingDomain, GraphitiInternal.getEmfService().getDTPForDiagram(diagram).getProviderId(), false);
					}
					return diagramInput;
				} else {
					// Create the diagram and its file
					if (activeConfig.diagram == null) {
						activeConfig.diagram = Graphiti.getPeCreateService().createDiagram("CamelContext", "CamelContext", true); //$NON-NLS-1$ //$NON-NLS-2$
						DiagramOperations.execute(this.editingDomain, new ImportCamelContextElementsCommand(this, this.editingDomain, activeConfig.diagram), true);
						activeConfig.input = DiagramEditorInput.createEditorInput(activeConfig.diagram, this.editingDomain, GraphitiInternal.getEmfService().getDTPForDiagram(activeConfig.diagram).getProviderId(), false);
					}
					return activeConfig.input;
				}
			}
		}
		return null;
	}

	protected DiagramEditorInput createDiagram() {
		// Create the diagram and its file
		if (recreateModel) {
			diagram = Graphiti.getPeCreateService().createDiagram("CamelContext", "CamelContext", true); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			activeConfig.diagram = Graphiti.getPeCreateService().createDiagram("CamelContext", "CamelContext", true); //$NON-NLS-1$ //$NON-NLS-2$
		}

		ResourceSet resourceSet = new ResourceSetImpl();

		this.editingDomain = TransactionUtil.getEditingDomain(resourceSet);
		if (this.editingDomain == null) {
			this.editingDomain = DiagramEditorFactory.createResourceSetAndEditingDomain();

			// TODO we need to associate the editngDomain / resourceSet now with the diagram
			resourceSet = this.editingDomain.getResourceSet();
		}

		// Create the data within a command and save (must not happen
		// inside
		// the command since finishing the command will trigger setting
		// the
		// modification flag on the resource which will be used by the
		// save
		// operation to determine which resources need to be saved)
		if (recreateModel) {
			this.editingDomain.getCommandStack().execute(new ImportCamelContextElementsCommand(this, this.editingDomain, diagram));
			return DiagramEditorInput.createEditorInput(diagram, this.editingDomain, GraphitiInternal.getEmfService().getDTPForDiagram(diagram).getProviderId(), false);
		} else {
			this.editingDomain.getCommandStack().execute(new ImportCamelContextElementsCommand(this, this.editingDomain, activeConfig.diagram));
			return DiagramEditorInput.createEditorInput(activeConfig.diagram, this.editingDomain, GraphitiInternal.getEmfService().getDTPForDiagram(activeConfig.diagram).getProviderId(), false);
		}
	}

	/**
	 * @param editingDomain the editingDomain to set
	 */
	public void setEditingDomain(TransactionalEditingDomain editingDomain) {
		this.editingDomain = editingDomain;
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

	private IFile getCamelContextFile() {
		if (recreateModel) {
			IFileEditorInput fileEditorInput = getFileEditorInput();
			if (fileEditorInput != null) {
				return fileEditorInput.getFile();
			}
			return null;
		} else {
			return this.camelContextFile;
		}
	}

	protected IFileEditorInput getFileEditorInput() {
		return asFileEditorInput(editorInput);
	}

	protected IRemoteCamelEditorInput getRemoteCamelEditorInput() {
		return asRemoteCamelEditorInput(editorInput);
	}

	protected IFileEditorInput asFileEditorInput(IEditorInput input) {
		if (input instanceof IFileEditorInput) {
			return (IFileEditorInput) input;
		} else if (input instanceof ICamelEditorInput) {
			ICamelEditorInput camelEditorInput = (ICamelEditorInput) input;
			IEditorInput fileEditorInput = camelEditorInput.getFileEditorInput();
			if (fileEditorInput instanceof IFileEditorInput) {
				return (IFileEditorInput) fileEditorInput;
			}
		}
		return null;
	}

	protected IRemoteCamelEditorInput asRemoteCamelEditorInput(IEditorInput input) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#
	 * createPaletteViewerProvider()
	 */
	@Override
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.gef.ui.palette.PaletteViewerProvider#
			 * configurePaletteViewer(org.eclipse.gef.ui.palette.PaletteViewer)
			 */
			@Override
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				// create a drag source listener for this palette viewer
				// together with an appropriate transfer drop target listener,
				// this will enable
				// model element creation by dragging a
				// CombinatedTemplateCreationEntries
				// from the palette into the editor
				// @see ShapesEditor#createTransferDropTargetListener()
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(
						viewer));
			}
		};
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
		if (recreateModel) {
			if (data.model == null && editorInput != null) {
				data.lazyLoading = true;
				try {
					loadModelFromInput(editorInput);
				} finally {
					data.lazyLoading = false;
				}
			}
			return this.data.model;
		} else {
			return this.model;
		}
	}

	public void setModel(RouteContainer model) throws PartInitException {
		if (recreateModel) {
			if (model != this.data.model){
				this.data.model = model;
				this.data.selectedRoute = null;

				updateDiagramIfNotLazyLoading();
			}
		} else {
			// save the old input
			DiagramEditorInput inputOld = this.activeConfig.input;
			// clear all caches
			clearCache();
			// restore the input
			this.activeConfig.input = inputOld;
			// the users did changes in XML so we can't rely on the old model any longer
			// so we clear the model cache
			this.cache.clear();
			// set the new model
			this.model = model;
			// recreate the diagram input as the model changed and we need a new diagram
			// and then select the first route in the new model
			switchRoute((RouteSupport)this.model.getChildren().get(0));
			//			throw new PartInitException("Do not set a model this way when using non-RECREATEMODEL-mode...");
		}
	}

	public RouteSupport getSelectedRoute() {
		if (recreateModel) {
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
		} else {
			if (this.activeRoute == null) {
				setInitialRoute();
			}
			return this.activeRoute;
		}
	}

	public void clearSelectedRouteCache() {
		this.data.selectedRoute = null;
		this.data.selectedRouteIndex = -1;
	}

	public void setSelectedRoute(RouteSupport selectedRoute) {
		if (recreateModel) {
			if (this.data.selectedRoute != selectedRoute) {
				int index = data.indexOfRoute(selectedRoute);
				setSelectedRouteIndex(index);
			}
		} else {
			switchRoute(selectedRoute);
		}
	}

	public void switchRoute(final RouteSupport newRoute) {
		if (newRoute == activeRoute) return;
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
		setInput(createDiagramInput(activeConfig.input));
		refresh();
	}

	private void setInitialRoute() {
		if (getModel() != null) {
			if (getModel().getChildren().isEmpty()) {
				// lets add an empty route for now...
				Route route = new Route();
				getModel().addChild(route);
			}
			this.activeRoute = (RouteSupport)getModel().getChildren().get(0);
			if (recreateModel) {
				this.data.selectedRoute = activeRoute;
			}
		}
	}

	public void setSelectedRouteIndex(int index) {
		if (this.data.selectedRouteIndex != index) {
			if (!recreateModel) {
				// lets store the old values
				RouteSupport oldRoute = getSelectedRoute();
				if (diagram != null && oldRoute != null) {
					DesignerCache value = new DesignerCache();
					value.diagram = diagram;
					value.input = diagramInput;
					cache.put(oldRoute, value);
				}
			}

			this.data.selectedRouteIndex = index;
			this.data.selectedRoute = null;

			if (recreateModel) {
				// now lets recreate the model so that we don't get any Graphiti woe since the model is intertwined with the diagram
				data.recreateModel();

				// setup grid visibility
				setupGridVisibility();

				try {
					updateDiagramIfNotLazyLoading();
				} catch (PartInitException e) {
					Activator.getLogger().warning("Failed to update diagram on route selection change: " + e, e);
				}
			} else {
				RouteSupport route = getSelectedRoute();

				DesignerCache value = cache.get(route);
				if (value == null || value.input == null) {
					// lets force creation of a new diagram and input
					diagram = null;
					diagramInput = createDiagramInput(diagramInput);
				} else {
					diagram = value.diagram;
					diagramInput = value.input;
				}
				setInput(diagramInput);
				refresh();

			}
		}
	}

	public void setupGridVisibility() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				// TODO is this causing transaction issues???
				DiagramOperations.updateGridColor(RiderDesignEditor.this);

				// retrieve the grid visibility setting
				boolean gridVisible = PreferenceManager.getInstance().loadPreferenceAsBoolean(PreferencesConstants.EDITOR_GRID_VISIBILITY);

				// reset the grid visibility flag
				DiagramUtils.setGridVisible(gridVisible);
			}
		});
	}

	/**
	 * @return the container
	 */
	public IProject getContainer() {
		return this.container;
	}

	private void updateDiagramIfNotLazyLoading() throws PartInitException {
		// lets only notify if we are not the first time we load the model
		if (!data.lazyLoading) {
			// lets clear all previous diagrams so we don't reuse them
			clearCache();

			// TODO if we ever get recreateModel = false working, we could just
			// set the input here...
			editor.recreateDesignPage();
			fireModelChanged();
		}
	}

	private void updateDiagramInput() throws PartInitException {
		super.init(editorSite, diagramInput);

		// we currently don't need this to be async
		boolean syncLayout = true;
		if (syncLayout) {
			autoLayoutRoute();
		} else {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					autoLayoutRoute();
				}
			});
		}
		//		clearChangedFlags();
	}



	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.internal.editor.DiagramEditorInternal#getSelectionSynchronizerInternal()
	 */
	@Override
	public SelectionSynchronizer getSelectionSynchronizerInternal() {
		if (recreateModel) {
			return super.getSelectionSynchronizerInternal();
		} else {
			return getSelectionSynchronizer();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#getSelectionSynchronizer()
	 */
	@Override
	protected SelectionSynchronizer getSelectionSynchronizer() {
		if (recreateModel) {
			return super.getSelectionSynchronizer();
		} else {
			synchronized (this) {
				if (this.synchronizer == null) {
					this.synchronizer = new CamelContextSelectionSynchronizer(this);
				}
			}
			return this.synchronizer;
		}
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
		this.diagram = null;

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
		DiagramOperations.layoutDiagram(this);
	}

	public Diagram getDiagram() {
		return diagram;
	}

	public void setTransactionalEditingDomain(TransactionalEditingDomain editingDomain) {
		// TODO how do we do this???

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
		return featureProvider;
	}

	public void setFeatureProvider(IFeatureProvider featureProvider) {
		this.featureProvider = featureProvider;
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
		if (recreateModel) {
			// nothing here for now
		} else {
			try {
				if (this.activeConfig.diagram != null) this.activeConfig.diagram.eResource().delete(null);
				if (this.getModel() != null && this.getModel().eResource() != null) this.getModel().eResource().delete(null);
			} catch (Exception ex) {
				Activator.getLogger().error("Unable to clear the diagram editor cache.", ex);
			}
			this.cache.clear();
			this.model = null;
			this.activeRoute = null;
			this.activeConfig.input.dispose();
			this.activeConfig = new DesignerCache();
			refresh();
		}
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
}
