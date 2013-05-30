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

package org.fusesource.ide.camel.editor.outline;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.graphiti.ui.internal.util.gef.ScalableRootEditPartAnimated;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.fusesource.ide.camel.editor.AbstractNodes;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.editor.INodeViewer;
import org.fusesource.ide.camel.editor.editor.ModelChangeListener;
import org.fusesource.ide.camel.editor.editor.NodeSelectionSupport;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.editor.RiderEditorContextMenuProvider;
import org.fusesource.ide.camel.editor.outline.tree.AbstractNodeTreeEditPart;
import org.fusesource.ide.camel.editor.outline.tree.RouteTreeEditPartFactory;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.commons.ui.Trees;


/**
 * @author lhein
 */
public class RiderOutlinePage extends ContentOutlinePage implements INodeViewer, ModelChangeListener {

	private RiderDesignEditor editor;
	private ScrollableThumbnail thumbnail;
	private DisposeListener disposeListener;
	private SashForm sash;
	private RouteTreeEditPartFactory editPartFactory;
	private NodeSelectionSupport nodeListener = new NodeSelectionSupport();
	private RouteSupport previousSelection;


	/**
	 * Create a new outline page for the shapes editor.
	 * 
	 * @param viewer
	 *            a viewer (TreeViewer instance) used for this outline page
	 * @throws IllegalArgumentException
	 *             if editor is null
	 */
	public RiderOutlinePage(RiderDesignEditor editor) {
		super(new TreeViewer());
		this.editor = editor;
		this.editPartFactory = new RouteTreeEditPartFactory(this);
		this.editor.addModelChangeListener(this);
	}

	@Override
	public AbstractNode getSelectedNode() {
		return nodeListener.getSelectedNode();
	}

	@Override
	public void setSelectedNode(AbstractNode newSelection) {
		if (newSelection != null) {
			AbstractNodeTreeEditPart selectEditPart = AbstractNodeTreeEditPart.findEditPart(newSelection, getViewer().getRootEditPart());
			if (selectEditPart != null) {
				getViewer().setSelection(new StructuredSelection(selectEditPart));
			} else {
				Activator.getLogger().error("Could not find editPart for selection: " + newSelection, new Exception());
			}
		}

	}

	@Override
	public void createControl(Composite parent) {
		sash = new SashForm(parent, SWT.VERTICAL);
		getViewer().createControl(sash);
		getViewer().setEditDomain(editor.getEditDomain());
		getViewer().setEditPartFactory(editPartFactory);
		onModelChange();
		editor.getSelectionSyncer().addViewer(getViewer());

		// Creation de la miniature.
		Canvas canvas = new Canvas(sash, SWT.BORDER);
		LightweightSystem lws = new LightweightSystem(canvas);
		GraphicalViewer graphicalViewer = editor.getGraphicalViewer();
		if (graphicalViewer == null)
			return;
		
		RootEditPart rootEditPart = graphicalViewer.getRootEditPart();
		
		// TODO Graphiti - not a ScalableRootEditPart but some kind of DiagramEditPart
		if (rootEditPart instanceof ScalableRootEditPartAnimated) {
			ScalableRootEditPartAnimated diagramEditPart = (ScalableRootEditPartAnimated) rootEditPart;
			IFigure figure = diagramEditPart.getFigure();
			thumbnail = new ScrollableThumbnail( (Viewport) figure);
			IFigure source = diagramEditPart.getLayer(LayerConstants.PRINTABLE_LAYERS);
			thumbnail.setSource(source);
			lws.setContents(thumbnail);
		} else if (rootEditPart instanceof ScalableRootEditPart) {
			thumbnail = new ScrollableThumbnail( (Viewport) ((ScalableRootEditPart) rootEditPart).getFigure());
			thumbnail.setSource(((ScalableRootEditPart) rootEditPart).getLayer(LayerConstants.PRINTABLE_LAYERS));
			lws.setContents(thumbnail);
		}
		disposeListener = new DisposeListener() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
			 */
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (thumbnail != null) {
					thumbnail.deactivate();
					thumbnail = null;
				}
			}
		};
		graphicalViewer.getControl().addDisposeListener(disposeListener);

		final TreeViewer viewer = getTreeViewer();
		// TODO how to find the part?
		final IWorkbenchPart part = null;
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				final ISelection selection = event.getSelection();
				nodeListener.selectionChanged(part, selection);
				AbstractNode node = AbstractNodes.getSelectedNode(selection);
				RouteSupport route = AbstractNodes.getRoute(node);
				if (route != null && route != previousSelection) {
					// lets potentially update the selected route
					previousSelection = route;
					// only set the route if it differs from the currently selected one in the editor
					if (editor.getSelectedRoute() != route) {
						editor.setSelectedRoute(route);
					}

					// lets make sure we get the focus afterwards
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							//viewer.setSelection(selection);
							setFocus();
						}
					});
				}

				// TODO should we update the diagram's selection too?
			}
		});
		final Tree tree = (Tree)viewer.getControl();
		ToolTip tooltip = new DefaultToolTip(tree) {
			@Override
			protected String getText(Event event) {
				TreeItem item = tree.getItem(new Point(event.x, event.y));
				if (item != null) {
					Object data = item.getData();
					AbstractNode node = AbstractNodes.toAbstractNode(item.getData());
					if (node != null) {
						return node.getDisplayToolTip();
					}
					if (data instanceof AbstractNodeTreeEditPart) {
						AbstractNodeTreeEditPart nodeTree = (AbstractNodeTreeEditPart) data;
						return nodeTree.getToolTip();
					}
				}
				return super.getText(event);
			}
		};
	}


	@Override
	public void onModelChange() {
		RouteContainer model = editor.getModel();
		if (model != null) {
			Tree tree = getTree();
			if (tree == null) {
				System.out.println("Warning - attempt to set the model when no tree!");
			} else {
				getViewer().setContents(model);
				Trees.expandAll(tree);
			}
		}
	}

	public Tree getTree() {
		return (Tree) getTreeViewer().getControl();
	}

	protected TreeViewer getTreeViewer() {
		return (TreeViewer)getViewer();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#init(org.eclipse.ui.part.IPageSite)
	 */
	@Override
	public void init(IPageSite pageSite) {
		super.init(pageSite);

		IActionBars bars = getSite().getActionBars();
		bars.setGlobalActionHandler(ActionFactory.UNDO.getId(), editor.getActionRegistry().getAction(ActionFactory.UNDO.getId()));
		bars.setGlobalActionHandler(ActionFactory.REDO.getId(), editor.getActionRegistry().getAction(ActionFactory.REDO.getId()));
		bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), editor.getActionRegistry().getAction(ActionFactory.DELETE.getId()));
		bars.updateActionBars();

		getViewer().setKeyHandler(editor.getKeyHandler());
		ContextMenuProvider provider = new RiderEditorContextMenuProvider(editor, this, getViewer(), editor.getActionRegistry());
		getViewer().setContextMenu(provider);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.ContentOutlinePage#getControl()
	 */
	@Override
	public Control getControl() {
		return sash;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	@Override
	public void dispose() {
		this.editor.removeModelChangeListener(this);
		editor.getSelectionSyncer().removeViewer(getViewer());
		if (editor.getGraphicalViewer() != null && editor.getGraphicalViewer().getControl() != null && 
			!editor.getGraphicalViewer().getControl().isDisposed()) {
			editor.getGraphicalViewer().getControl().removeDisposeListener(disposeListener);
		}
		super.dispose();
	}
}
