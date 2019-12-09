/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.features.custom;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.graph.CompoundDirectedGraph;
import org.eclipse.draw2d.graph.CompoundDirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.EdgeList;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.ResizeShapeContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.swt.graphics.Rectangle;
import org.fusesource.ide.camel.editor.utils.FigureUIFactory;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;


/**
 * Maps the Graphiti Diagram to a graph structure which can be consumed by the
 * GEF Layouter, layouts the graph structure and maps the new coordinates back
 * to the diagram. Refresh is triggered automatically by the changes on the
 * diagram model.
 * 
 * Disclaimer: this is just an example to show how to plug an arbitrary layouter
 * into a Graphiti diagram editor. For instance, the basic layouting here does
 * not consider bendpoints etc.
 * 
 */
public class LayoutDiagramFeature extends AbstractCustomFeature {

	/**
	 * Minimal distance between nodes.
	 */
	private static final int PADDING_H = 10;
	private static final int PADDING_V = 10;
	private static final int SPACING_H = 10;
	private static final int SPACING_V = 40;

	public LayoutDiagramFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getDescription() {
		return "Layout diagram with GEF Layouter"; //$NON-NLS-1$
	}

	@Override
	public String getName() {
		return "&Layout Diagram"; //$NON-NLS-1$
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return true;
	}

	@Override
	public boolean isAvailable(IContext context) {
		ICustomContext cc = (ICustomContext)context;
		PictogramElement pe = cc.getPictogramElements()[0] instanceof Connection ? 
				((Connection) cc.getPictogramElements()[0]).getStart().getParent() : 
				cc.getPictogramElements()[0];
        final Object bo = getBusinessObjectForPictogramElement(pe);
        return bo != null && bo instanceof AbstractCamelModelElement && ((AbstractCamelModelElement)bo).getUnderlyingMetaModelObject().canHaveChildren();
	}
	
	@Override
	public void execute(ICustomContext context) {
		doLayout(context.getPictogramElements()[0]);
	}
	
	private void doLayout(PictogramElement selectedContainer) {
		// put all connection and shape info into the directed graph
		final CompoundDirectedGraph graph = mapDiagramToGraph(selectedContainer);
		graph.setDefaultPadding(new Insets(PADDING_V, PADDING_H, PADDING_V, PADDING_H));
		
		// create a directed graph layout and recalc the layout data
		CompoundDirectedGraphLayout layout = new CompoundDirectedGraphLayout();
		int direction = PreferenceManager.getInstance().loadPreferenceAsInt(PreferencesConstants.EDITOR_LAYOUT_ORIENTATION);
		graph.setDirection(direction);
		layout.visit(graph);
		
		// map the new layout coordinates back to the diagram
		mapGraphCoordinatesToDiagram(graph);
		resizeContainer(selectedContainer);
	}

	/**
	 * determines all nodes and connections and hand them over to the directed 
	 * graph to be layed out
	 * 
	 * @param container
	 * @return
	 */
	private CompoundDirectedGraph mapDiagramToGraph(PictogramElement container) {
		Map<AnchorContainer, Node> shapeToNode = new HashMap<>();
		EdgeList edgeList = new EdgeList();
		NodeList nodeList = new NodeList();
		CompoundDirectedGraph dg = new CompoundDirectedGraph();

		if (container == null){
			return dg;
		}
		
		if (!CollapseFeature.isCollapsed(container) && container instanceof ContainerShape) {
			EList<Shape> children = ((ContainerShape)container).getChildren();
			for (Shape shape : children) {
				Node node = new Node();
				GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
				node.x = ga.getX();
				node.y = ga.getY();
				node.width = ga.getWidth();
				node.height = ga.getHeight();
				node.data = shape;
				shapeToNode.put(shape, node);
				nodeList.add(node);
			}
		}
		
		EList<Connection> connections = getDiagram().getConnections();
		for (Connection connection : connections) {
			AnchorContainer source = connection.getStart().getParent();
			AnchorContainer target = connection.getEnd().getParent();
			if (!shapeToNode.containsKey(source) || !shapeToNode.containsKey(target)){
				continue;
			}
			Edge edge = new Edge(shapeToNode.get(source), shapeToNode.get(target));
			edge.data = connection;
			edgeList.add(edge);
		}
		
		dg.nodes = nodeList;
		dg.edges = edgeList;
		
		return dg;
	}

	/**
	 * resizes the container element to fit all children 
	 * 
	 * @param containerPE
	 */
	private void resizeContainer(PictogramElement containerPE) {
		if (containerPE == null || containerPE.getGraphicsAlgorithm() == null){
			return;
		}

		Rectangle maxContentArea = new Rectangle(containerPE.getGraphicsAlgorithm().getX(), 
												 containerPE.getGraphicsAlgorithm().getY(), 
												 containerPE.getGraphicsAlgorithm().getWidth(), 
												 containerPE.getGraphicsAlgorithm().getHeight());
	
		EList<Shape> children = ((ContainerShape)containerPE).getChildren();		
		if (!CollapseFeature.isCollapsed(containerPE) && !children.isEmpty()) {
			int newWidth = 0;
			int newHeight = 0;
			for (Shape shape : children) {
				resizeContainer(shape);
				GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
				int w = ga.getX() + ga.getWidth() + PADDING_H + PADDING_H;
				int h = ga.getY() + ga.getHeight() + PADDING_V + PADDING_V;
				if (w > newWidth){
					newWidth = w;
				}
				if (h > newHeight){
					newHeight = h;
				}
			}
			maxContentArea.width = newWidth;
			maxContentArea.height = newHeight;
		} else {
			// if the container is collapsed we always assume the max collapsed height
			maxContentArea.height = FigureUIFactory.IMAGE_DEFAULT_HEIGHT;
		}
		
		// do a resize feature call 
		ResizeShapeContext cc = new ResizeShapeContext((ContainerShape)containerPE);
		cc.setX(maxContentArea.x);
		cc.setY(maxContentArea.y);
		cc.setWidth(maxContentArea.width);
		cc.setHeight(maxContentArea.height);
		getFeatureProvider().getResizeShapeFeature(cc).execute(cc);
	}

	/**
	 * maps the calculated layout data back to the diagram figures
	 * 
	 * @param graph
	 * @return
	 */
	private Diagram mapGraphCoordinatesToDiagram(CompoundDirectedGraph graph) {
		NodeList myNodes = new NodeList();
		myNodes.addAll(graph.nodes);
		myNodes.addAll(graph.subgraphs);
		
		for (Object object : myNodes) {
			Node node = (Node) object;
			Shape shape = (Shape) node.data;
			shape.getGraphicsAlgorithm().setX(node.x+SPACING_H);
			shape.getGraphicsAlgorithm().setY(node.y+SPACING_V);
			shape.getGraphicsAlgorithm().setWidth(node.width);
			shape.getGraphicsAlgorithm().setHeight(node.height);
		}
		
		return null;
	}
}
