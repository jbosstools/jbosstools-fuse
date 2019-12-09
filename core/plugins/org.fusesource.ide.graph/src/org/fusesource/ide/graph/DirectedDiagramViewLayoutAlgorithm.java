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

package org.fusesource.ide.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.Rank;
import org.eclipse.swt.SWT;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

/**
 * @author lhein
 */
public class DirectedDiagramViewLayoutAlgorithm extends AbstractLayoutAlgorithm {

	public DirectedDiagramViewLayoutAlgorithm(int styles) {
		super(styles);
	}

	@Override
	protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider, double boundsX, double boundsY, double boundsWidth, double boundsHeight) {
		Map<InternalNode, Node> mapping = new HashMap<>(entitiesToLayout.length);
		DirectedGraph graph = new DirectedGraph();
		
		graph.setDirection(PositionConstants.SOUTH);
		graph.setMargin(new Insets(20, 60, 20, 60));
		graph.setDefaultPadding(new Insets(30, 50, 30, 50));
		
		for (int i = 0; i < entitiesToLayout.length; i++) {
			InternalNode internalNode = entitiesToLayout[i];
			Node node = new Node(internalNode);
			node.setSize(new Dimension(10, 10));
			mapping.put(internalNode, node);
			graph.nodes.add(node);
		}
		for (int i = 0; i < relationshipsToConsider.length; i++) {
			InternalRelationship relationship = relationshipsToConsider[i];
			Node source = mapping.get(relationship.getSource());
			Node dest = mapping.get(relationship.getDestination());
			Edge edge = new Edge(relationship, source, dest);
			graph.edges.add(edge);
		}
		DirectedGraphLayout directedGraphLayout = new DirectedGraphLayout();
		directedGraphLayout.visit(graph);

		for (Iterator iterator = graph.nodes.iterator(); iterator.hasNext();) {
			Node node = (Node) iterator.next();
			InternalNode internalNode = (InternalNode) node.data;
			// For horizontal layout transpose the x and y coordinates
			if ((layout_styles & SWT.HORIZONTAL) == SWT.HORIZONTAL) {
				internalNode.setInternalLocation(node.y, node.x);
			} else {
				internalNode.setInternalLocation(node.x, node.y);
			}
		}
		updateLayoutLocations(entitiesToLayout);
		adjustHorizontalSpaces(graph);
	}

	private void adjustHorizontalSpaces(DirectedGraph g) {
		for (int row = 0; row < g.ranks.size(); row++) {
			Rank rank = g.ranks.getRank(row);
			for (int n = 0; n < rank.size(); n++) {
				Node node = rank.getNode(n);
				InternalNode internalNode = (InternalNode) node.data;
				Insets padNode = g.getPadding(node);

				internalNode.setSize(internalNode.getWidthInLayout(), 25);
				
				if (node.getLeft()!=null) {
					// check space to left
					Node left = node.getLeft();
					if (left.data instanceof InternalNode) {
						InternalNode internalLeft = (InternalNode) left.data;
						Insets padLeft = g.getPadding(left);
		
						double left_end = internalLeft.getCurrentX() + internalLeft.getWidthInLayout() + padLeft.right + padNode.left;
						if (internalNode.getCurrentX() <= left_end) {
							// node overlaps with left neighbor
	//						node.x = (int)left_end;
							internalNode.setLocation(left_end, internalNode.getCurrentY());
						}
					} else {
						Activator.getLogger().warning("Unsupported object " + left.data + " can't be cast to InternalNode!");
					}
				}
				
				if (node.getRight()!=null) {
					// check space to right
					Node right = node.getRight();
					if (right.data instanceof InternalNode) { 
						InternalNode internalRight = (InternalNode) right.data;
						Insets padRight = g.getPadding(right);
						
						double node_end = internalNode.getCurrentX() + internalNode.getWidthInLayout() + padNode.right + padRight.left;
						if (node_end >= internalRight.getCurrentX()) {
							// node overlaps with right neighbor
	//						right.x = (int)node_end;
							internalRight.setLocation(node_end, internalRight.getCurrentY());
						}
					} else {
						Activator.getLogger().warning("Unsupported object " + right.data + " can't be cast to InternalNode!");
					}
				}
			}
		}
	}
	
	@Override
	protected int getCurrentLayoutStep() {
		return 0;
	}

	@Override
	protected int getTotalNumberOfLayoutSteps() {
		return 0;
	}

	@Override
	protected boolean isValidConfiguration(boolean asynchronous, boolean continuous) {
		return true;
	}

	@Override
	protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider) {
	}

	@Override
	protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider, double x, double y, double width, double height) {
	}

	@Override
	public void setLayoutArea(double x, double y, double width, double height) {
	}
}
